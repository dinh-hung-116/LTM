package com.chess.network;

/**
 * GameSession - Đại diện cho một ván cờ đang diễn ra giữa 2 client.
 *
 * Server tạo GameSession sau khi cả hai xác nhận ghép trận.
 * GameSession giữ tham chiếu đến 2 ClientHandler và quản lý việc
 * forward nước đi giữa hai bên.
 */
public class GameSession {

    private final String        sessionId;
    private final ClientHandler white;      // bên trắng
    private final ClientHandler black;      // bên đen
    private       boolean       finished  = false;

    // ─────────────────────────────────────────────────────────
    public GameSession(String sessionId, ClientHandler white, ClientHandler black) {
        this.sessionId = sessionId;
        this.white     = white;
        this.black     = black;
    }

    // ─────────────────────────────────────────────────────────
    // KHỞI ĐỘNG VÁN ĐẤU
    // ─────────────────────────────────────────────────────────
    /** Gửi ASSIGN cho cả hai và bắt đầu ván. */
    public void start() {
        log("Bắt đầu ván: " + white.getUsername() + " (Trắng) vs "
                + black.getUsername() + " (Đen)");
        white.send(NetworkConfig.ASSIGN_WHITE);
        black.send(NetworkConfig.ASSIGN_BLACK);
    }

    // ─────────────────────────────────────────────────────────
    // FORWARD NƯỚC ĐI
    // ─────────────────────────────────────────────────────────
    private int moveCount = 0; // đếm nước đi để biết nước đầu tiên

    public synchronized void handleMove(ClientHandler sender, String move) {
        if (finished) return;

        ClientHandler opponent = getOpponent(sender);
        if (opponent == null) return;

        // Xác nhận cho bên gửi
        sender.send(NetworkConfig.MOVE_OK);

        // Forward sang bên kia
        opponent.send(NetworkConfig.msg(NetworkConfig.MOVE, move));

        moveCount++;

        // Bên vừa đi xong
        String sideMoved = (sender == white) ? NetworkConfig.SIDE_WHITE
                : NetworkConfig.SIDE_BLACK;

        // Nước đầu tiên: broadcast CLOCK_START để cả 2 client bắt đầu đồng hồ
        if (moveCount == 1) {
            white.send(NetworkConfig.CLOCK_START);
            black.send(NetworkConfig.CLOCK_START);
            log("Đồng hồ bắt đầu.");
        }

        // Mỗi nước: broadcast CLOCK_SWITCH để 2 client chuyển đồng hồ đồng bộ
        String clockMsg = NetworkConfig.msg(NetworkConfig.CLOCK_SWITCH, sideMoved);
        white.send(clockMsg);
        black.send(clockMsg);

        log(sender.getUsername() + " đi: " + move);
    }

    // ─────────────────────────────────────────────────────────
    // KẾT THÚC VÁN
    // ─────────────────────────────────────────────────────────
    /** Broadcast kết quả cho cả 2 bên và đánh dấu ván đã kết thúc. */
    public synchronized void endGame(String result) {
        if (finished) return;
        finished = true;

        String msg = NetworkConfig.msg(NetworkConfig.GAME_OVER, result);
        white.send(msg);
        black.send(msg);
        log("Ván kết thúc: " + result);
    }

    /** Xử lý khi một bên ngắt kết nối giữa chừng. */
    public synchronized void handleDisconnect(ClientHandler disconnected) {
        if (finished) return;

        ClientHandler opponent = getOpponent(disconnected);
        if (opponent != null) {
            // Bên còn lại thắng
            String result = (opponent == white)
                    ? NetworkConfig.WHITE_WINS
                    : NetworkConfig.BLACK_WINS;
            endGame(result + "_DISCONNECT");
        }
    }

    // ─────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────
    /** Trả về đối thủ của sender (null nếu sender không thuộc session này). */
    public ClientHandler getOpponent(ClientHandler sender) {
        if (sender == white) return black;
        if (sender == black) return white;
        return null;
    }

    public boolean isFinished()            { return finished; }
    public String  getSessionId()          { return sessionId; }
    public ClientHandler getWhite()        { return white; }
    public ClientHandler getBlack()        { return black; }

    /** Kiểm tra client có đang tham gia session này không. */
    public boolean hasPlayer(ClientHandler c) {
        return c == white || c == black;
    }

    private void log(String msg) {
        System.out.println("[GameSession:" + sessionId + "] " + msg);
    }
}