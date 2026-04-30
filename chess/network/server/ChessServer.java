package chess.network.server;

import chess.database.DTO.User;
import chess.database.DAO.UserDAO;
import chess.network.NetworkConfig;
import chess.network.transportpacket.LoginPacket;
import chess.network.transportpacket.PacketProcess;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChessServer {

    private ServerSocket serverSocket;

    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    // Mảng lưu phiên làm việc của người dùng
    private final Map<String, UserSession> connectedUsers = new ConcurrentHashMap<>();

    // ==================== START SERVER ====================
    public void start() {
        try {
            serverSocket = new ServerSocket(
                    NetworkConfig.SERVER_PORT,
                    NetworkConfig.BACKLOG
            );

            log("Server started...");
            log("Waiting for clients...");

            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                
                // người dùng lúc này là ẩn danh nên không tạo phiên làm việc(UserSession)
                // mà chỉ giữ kết nối 
                ClientHandler client = new ClientHandler(socket, this);
                threadPool.execute(client);

                log("New client: " + socket.getRemoteSocketAddress());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // ==================== STOP SERVER ====================
    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            threadPool.shutdown();
            log("Server stopped.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ==================== PACKET ROUTER ====================
    public void handleMessage(ClientHandler client, String raw) {
        try {
            // Lấy ra type(hoặc gọi là header)
            String header = PacketProcess.getPacketHeader(raw);

            if (header == null) {
                log("Invalid packet.");
                return;
            }

            switch (header) {
                // ĐĂNG NHẬP - LOGIN
                case NetworkConfig.LOGIN_REQUEST -> handleLogin(client, raw);
                
                // ĐĂNG KÝ - REGISTER
                case NetworkConfig.REGISTER_REQUEST -> handleRegister(client, raw);
                
                //ĐĂNG XUẤT
                case NetworkConfig.LOGOUT_REQUEST -> handleLogout(client);

                default -> log("Unknown packet: " + header);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ========================= LOGIN =========================
    private void handleLogin(ClientHandler client, String raw) {
        try {
            LoginPacket request = PacketProcess.fromJson(raw, LoginPacket.class);

            LoginPacket response;
            
            // nếu packet null hoặc không có User -> Người dùng ẩn danh
            if (request == null || request.getUser() == null) {
                response = PacketProcess.craftLoginResponsePacket(false, new User());
                client.send(PacketProcess.toJson(response));
                return;
            }

            String username = request.getUser().getUserName();
            String password = request.getUser().getPasswordHash();

            User loginUser = UserDAO.checkLogin(username, password);

            // nếu có tồn tại người dùng
            if (loginUser != null) {

                // Tạo phiên làm việc cho người dùng vừa đăng nhập thành công
                UserSession session = new UserSession(loginUser, client);

                connectedUsers.put(username, session);

                response = PacketProcess.craftLoginResponsePacket(true, loginUser);

                log(username + " login success");
            } else {
                response = PacketProcess.craftLoginResponsePacket(false, new User());
                log(username + " login fail");
            }

            client.send(PacketProcess.toJson(response));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ========================= REGISTER =========================
    private void handleRegister(ClientHandler client, String raw) {

        try {
            LoginPacket request =
                    PacketProcess.fromJson(raw, LoginPacket.class);

            User user = request.getUser();

            boolean ok = UserDAO.registerUser(user, 1200);

            // nếu thêm được người dùng thì gửi thông báo ok
            LoginPacket response = PacketProcess.craftRegisterResponsePacket(ok);

            client.send(PacketProcess.toJson(response));

        } catch (Exception e) {
            e.printStackTrace();

            LoginPacket fail =
                    PacketProcess.craftRegisterResponsePacket(false);

            client.send(PacketProcess.toJson(fail));
        }
    }

    // ========================= LOGOUT =========================
    public void handleLogout(ClientHandler client) {
        try {
            String username = client.getUsername();
            
            // Nếu username == null -> có khả năng đã logout từ trước
            if (username == null) return;

            UserSession session = connectedUsers.remove(username);

            if (session != null) {
                session.setStatus(UserSession.PlayerStatus.DISCONNECTED);
                log(username + " logged out");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            LoginPacket response = PacketProcess.craftLogoutResponsePacket();

            client.send(PacketProcess.toJson(response));
        }
    }


    // ========================= LOG =========================
    private void log(String msg) {
        System.out.println("[ChessServer] " + msg);
    }

    public static void main(String[] args) {
        System.out.println("=== Chess Server ===");
        new ChessServer().start();
    }
}