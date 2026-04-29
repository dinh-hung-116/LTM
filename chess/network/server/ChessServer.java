package chess.network.server;

import chess.database.Class.User;
import chess.database.DAO.UserDAO;
import chess.network.NetworkConfig;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ChessServer - Server chính quản lý toàn bộ client kết nối.
 *
 * Kiến trúc:
 *  - 1 thread chính: accept() vòng lặp chờ client mới
 *  - Thread pool (ExecutorService): mỗi ClientHandler chạy trên 1 thread riêng
 *  - ConcurrentHashMap: lưu trữ clients, queue, pendingMatches, sessions
 *    (thread-safe, không cần synchronized trên toàn bộ map)
 *
 * Luồng xử lý tin nhắn:
 *  ClientHandler.run() → đọc dòng → gọi server.handleMessage(client, msg)
 *  → ChessServer.handleMessage() phân loại và xử lý
 */
public class ChessServer {

    // ── ServerSocket ──────────────────────────────────────────
    private ServerSocket serverSocket;

    // ── Thread pool cho ClientHandler ─────────────────────────
    // CachedThreadPool: tạo thread mới khi cần, tái sử dụng thread rảnh
    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    // ── Danh sách client đang kết nối ─────────────────────────
    // key: username, value: ClientHandler
    private final Map<String, ClientHandler> connectedClients
            = new ConcurrentHashMap<>();

    // ── Hàng đợi tìm trận ─────────────────────────────────────
    // LinkedHashMap để giữ thứ tự FIFO, cần synchronized vì không phải ConcurrentMap
    private final Map<String, ClientHandler> matchQueue
            = Collections.synchronizedMap(new LinkedHashMap<>());

    // ── Ghép trận đang chờ xác nhận ───────────────────────────
    // key: username của một trong hai player
    private final Map<String, PendingMatch> pendingMatches
            = new ConcurrentHashMap<>();

    // ── Ván đấu đang diễn ra ──────────────────────────────────
    // key: username của một trong hai player
    private final Map<String, GameSession> activeSessions
            = new ConcurrentHashMap<>();

    // ── ID session tự tăng ────────────────────────────────────
    private final AtomicInteger sessionCounter = new AtomicInteger(1);

    // ─────────────────────────────────────────────────────────
    // KHỞI ĐỘNG SERVER
    // ─────────────────────────────────────────────────────────
    public void start() {
        try {
            serverSocket = new ServerSocket(
                    NetworkConfig.SERVER_PORT,
                    NetworkConfig.BACKLOG
            );
            log("Server khởi động tại port " + NetworkConfig.SERVER_PORT);
            log("Đang chờ client kết nối...");

            // Vòng lặp accept client mới
            while (!serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    // Tạo ClientHandler và chạy trên thread riêng
                    ClientHandler handler = new ClientHandler(clientSocket, this);
                    threadPool.execute(handler);
                    log("Client mới kết nối: " + clientSocket.getRemoteSocketAddress()
                            + " | Tổng: " + (connectedClients.size() + 1));
                } catch (IOException e) {
                    if (!serverSocket.isClosed()) {
                        log("Lỗi accept client: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            log("Không thể khởi động server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            threadPool.shutdown();
            log("Server đã dừng.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ─────────────────────────────────────────────────────────
    // XỬ LÝ TIN NHẮN TỪ CLIENT
    // ─────────────────────────────────────────────────────────
    /**
     * Hàm trung tâm: ClientHandler gọi hàm này mỗi khi nhận được 1 dòng.
     * Phân loại lệnh và chuyển đến handler phù hợp.
     */
    public void handleMessage(ClientHandler client, String raw) {
        String command = NetworkConfig.getCommand(raw);
        String data    = NetworkConfig.getData(raw);

        switch (command) {
            // ── Đăng nhập ─────────────────────────────────────
            case NetworkConfig.LOGIN         -> handleLogin(client, data);

            // ── Tìm trận ──────────────────────────────────────
            case NetworkConfig.FIND_MATCH    -> handleFindMatch(client);
            case NetworkConfig.CANCEL_FIND   -> handleCancelFind(client);

            // ── Xác nhận / từ chối ghép ───────────────────────
            case NetworkConfig.CONFIRM_MATCH -> handleConfirmMatch(client);
            case NetworkConfig.REJECT_MATCH  -> handleRejectMatch(client);

            // ── Trong ván đấu ─────────────────────────────────
            case NetworkConfig.MOVE          -> handleMove(client, data);
            case NetworkConfig.RESIGN        -> handleResign(client);
            case NetworkConfig.DRAW_OFFER    -> handleDrawOffer(client);
            case NetworkConfig.DRAW_ACCEPT   -> handleDrawAccept(client);
            case NetworkConfig.DRAW_REJECT   -> handleDrawReject(client);

            default -> log("Lệnh không rõ từ " + client.getUsername() + ": " + raw);
        }
    }

    // ─────────────────────────────────────────────────────────
    // ĐĂNG NHẬP
    // ─────────────────────────────────────────────────────────
    /**
     * Client gửi "LOGIN:username" ngay sau khi kết nối.
     * Server tạo User tạm và đánh dấu client đã đăng nhập.
     * (Không cần xác thực lại vì client đã login qua DB ở tầng GUI)
     */
    private void handleLogin(ClientHandler client, String username) {
        if (username == null || username.isBlank()) {
            client.send("ERROR:Username không hợp lệ");
            return;
        }

        // Tạo User object tạm chỉ để đánh dấu isLoggedIn = true
        User tempUser = new com.chess.database.Class.User();
        tempUser.setUserName(username);
        client.setUser(tempUser);

        // Đăng ký vào danh sách client đang kết nối
        connectedClients.put(username, client);
        client.send(NetworkConfig.LOGIN_OK);
        log(username + " đã đăng nhập.");
    }

    // ─────────────────────────────────────────────────────────
    // TÌM TRẬN (MATCHMAKING)
    // ─────────────────────────────────────────────────────────
    private synchronized void handleFindMatch(ClientHandler client) {
        if (!client.isLoggedIn()) {
            client.send("ERROR:Bạn chưa đăng nhập");
            return;
        }

        String username = client.getUsername();

        // Đã đang tìm hoặc đang chơi thì bỏ qua
        if (matchQueue.containsKey(username)
                || activeSessions.containsKey(username)
                || pendingMatches.containsKey(username)) {
            client.send("ERROR:Bạn đang trong trạng thái khác");
            return;
        }

        matchQueue.put(username, client);
        client.send(NetworkConfig.QUEUED);
        log(username + " vào hàng đợi. Queue size: " + matchQueue.size());

        // Nếu queue có ít nhất 2 người → ghép đôi
        tryMatch();
    }

    /** Lấy 2 người đầu queue và tạo PendingMatch. */
    private void tryMatch() {
        synchronized (matchQueue) {
            if (matchQueue.size() < 2) return;

            Iterator<ClientHandler> it = matchQueue.values().iterator();
            ClientHandler p1 = it.next(); it.remove();
            ClientHandler p2 = it.next(); it.remove();

            log("Ghép: " + p1.getUsername() + " vs " + p2.getUsername());

            // Tạo PendingMatch với callback timeout
            PendingMatch pending = new PendingMatch(p1, p2, this::handleMatchTimeout);

            pendingMatches.put(p1.getUsername(), pending);
            pendingMatches.put(p2.getUsername(), pending);

            // Thông báo cho cả hai
            p1.send(NetworkConfig.msg(NetworkConfig.MATCH_FOUND, p2.getUsername()));
            p2.send(NetworkConfig.msg(NetworkConfig.MATCH_FOUND, p1.getUsername()));
        }
    }

    private void handleCancelFind(ClientHandler client) {
        matchQueue.remove(client.getUsername());
        log(client.getUsername() + " hủy tìm trận.");
    }

    // ─────────────────────────────────────────────────────────
    // XÁC NHẬN / TỪ CHỐI GHÉP
    // ─────────────────────────────────────────────────────────
    private synchronized void handleConfirmMatch(ClientHandler client) {
        PendingMatch pending = pendingMatches.get(client.getUsername());
        if (pending == null || pending.isResolved()) return;

        boolean bothConfirmed = pending.confirm(client);
        log(client.getUsername() + " xác nhận. Cả hai: " + bothConfirmed);

        if (bothConfirmed) {
            // Cả hai đồng ý → tạo GameSession
            pending.resolve();
            removePending(pending);
            startGameSession(pending.getPlayer1(), pending.getPlayer2());
        }
    }

    private synchronized void handleRejectMatch(ClientHandler client) {
        PendingMatch pending = pendingMatches.get(client.getUsername());
        if (pending == null || pending.isResolved()) return;

        pending.resolve();
        removePending(pending);

        ClientHandler opponent = pending.getPlayer1() == client
                ? pending.getPlayer2() : pending.getPlayer1();

        // Phạt -1 điểm bên từ chối (TODO: cập nhật DB)
        client.send(NetworkConfig.msg(NetworkConfig.PENALTY, "-1"));
        client.send(NetworkConfig.MATCH_REJECTED);

        // Đưa đối thủ vào queue lại
        opponent.send(NetworkConfig.MATCH_REJECTED);
        matchQueue.put(opponent.getUsername(), opponent);
        opponent.send(NetworkConfig.QUEUED);
        log(opponent.getUsername() + " quay lại queue do " + client.getUsername() + " từ chối.");

        tryMatch();
    }

    /** Callback khi PendingMatch hết giờ xác nhận. */
    private synchronized void handleMatchTimeout(PendingMatch pending) {
        if (pending.isResolved()) return;
        pending.resolve();
        removePending(pending);

        ClientHandler unconfirmed = pending.getUnconfirmed();
        ClientHandler other = (unconfirmed == pending.getPlayer1())
                ? pending.getPlayer2() : pending.getPlayer1();

        log("Timeout! Bên chưa xác nhận: " + (unconfirmed != null ? unconfirmed.getUsername() : "?"));

        if (unconfirmed != null) {
            // Phạt bên không xác nhận
            unconfirmed.send(NetworkConfig.msg(NetworkConfig.PENALTY, "-1"));
            unconfirmed.send(NetworkConfig.MATCH_TIMEOUT);
        }

        // Đưa bên đã xác nhận quay lại queue
        if (other != null && other.isConnected()) {
            other.send(NetworkConfig.MATCH_TIMEOUT);
            matchQueue.put(other.getUsername(), other);
            other.send(NetworkConfig.QUEUED);
            tryMatch();
        }
    }

    // ─────────────────────────────────────────────────────────
    // BẮT ĐẦU VÁN ĐẤU
    // ─────────────────────────────────────────────────────────
    private void startGameSession(ClientHandler p1, ClientHandler p2) {
        String sessionId = "S" + sessionCounter.getAndIncrement();
        // Ngẫu nhiên ai chơi trắng
        ClientHandler white, black;
        if (Math.random() < 0.5) { white = p1; black = p2; }
        else                     { white = p2; black = p1; }

        GameSession session = new GameSession(sessionId, white, black);
        activeSessions.put(white.getUsername(), session);
        activeSessions.put(black.getUsername(), session);

        session.start();
        log("Ván " + sessionId + ": " + white.getUsername()
                + " (Trắng) vs " + black.getUsername() + " (Đen)");
    }

    // ─────────────────────────────────────────────────────────
    // TRONG VÁN ĐẤU
    // ─────────────────────────────────────────────────────────
    private void handleMove(ClientHandler client, String move) {
        GameSession session = activeSessions.get(client.getUsername());
        if (session == null || session.isFinished()) return;
        session.handleMove(client, move);
    }

    private void handleResign(ClientHandler client) {
        GameSession session = activeSessions.get(client.getUsername());
        if (session == null || session.isFinished()) return;

        // Bên đầu hàng thua
        String result = (client == session.getWhite())
                ? NetworkConfig.BLACK_WINS
                : NetworkConfig.WHITE_WINS;

        session.endGame(result + "_RESIGN");
        cleanupSession(session);
    }

    private void handleDrawOffer(ClientHandler client) {
        GameSession session = activeSessions.get(client.getUsername());
        if (session == null || session.isFinished()) return;
        // Forward yêu cầu hòa sang đối thủ
        ClientHandler opponent = session.getOpponent(client);
        if (opponent != null) {
            opponent.send(NetworkConfig.msg(NetworkConfig.DRAW_OFFER, client.getUsername()));
        }
    }

    private void handleDrawAccept(ClientHandler client) {
        GameSession session = activeSessions.get(client.getUsername());
        if (session == null || session.isFinished()) return;
        session.endGame(NetworkConfig.DRAW_RESULT);
        cleanupSession(session);
    }

    private void handleDrawReject(ClientHandler client) {
        GameSession session = activeSessions.get(client.getUsername());
        if (session == null || session.isFinished()) return;
        ClientHandler opponent = session.getOpponent(client);
        if (opponent != null) {
            opponent.send(NetworkConfig.DRAW_REJECT);
        }
    }

    // ─────────────────────────────────────────────────────────
    // NGẮT KẾT NỐI
    // ─────────────────────────────────────────────────────────
    /** Gọi bởi ClientHandler khi client ngắt kết nối (bất kể lý do). */
    public void onClientDisconnected(ClientHandler client) {
        String username = client.getUsername();

        // Xóa khỏi danh sách kết nối
        if (!username.isEmpty()) connectedClients.remove(username);

        // Xóa khỏi queue
        matchQueue.remove(username);

        // Xử lý PendingMatch
        PendingMatch pending = pendingMatches.get(username);
        if (pending != null && !pending.isResolved()) {
            handleRejectMatch(client);
        }

        // Xử lý GameSession đang chơi
        GameSession session = activeSessions.get(username);
        if (session != null && !session.isFinished()) {
            session.handleDisconnect(client);
            cleanupSession(session);
        }

        log(username.isEmpty() ? "Client ẩn danh" : username + " đã ngắt kết nối.");
    }

    // ─────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────

    /** Đăng ký client sau khi đăng nhập thành công (gọi từ ngoài nếu cần). */
    public void registerClient(ClientHandler client) {
        connectedClients.put(client.getUsername(), client);
    }

    /** Xóa PendingMatch khỏi map cho cả 2 player. */
    private void removePending(PendingMatch pending) {
        pendingMatches.remove(pending.getPlayer1().getUsername());
        pendingMatches.remove(pending.getPlayer2().getUsername());
    }

    /** Xóa GameSession khỏi map cho cả 2 player. */
    private void cleanupSession(GameSession session) {
        activeSessions.remove(session.getWhite().getUsername());
        activeSessions.remove(session.getBlack().getUsername());
        log("Session " + session.getSessionId() + " đã kết thúc, dọn dẹp xong.");
    }

    // ─────────────────────────────────────────────────────────
    // LOG + STATS
    // ─────────────────────────────────────────────────────────
    private void log(String msg) {
        System.out.println("[ChessServer] " + msg);
    }

    public int getConnectedCount() { return connectedClients.size(); }
    public int getQueueSize()      { return matchQueue.size(); }
    public int getActiveGames()    { return activeSessions.size() / 2; }

    // ─────────────────────────────────────────────────────────
    // ENTRY POINT
    // ─────────────────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("=== Chess Online Server ===");
        System.out.println("IP:   " + NetworkConfig.SERVER_IP);
        System.out.println("PORT: " + NetworkConfig.SERVER_PORT);
        System.out.println("==========================");
        new ChessServer().start();
    }
}