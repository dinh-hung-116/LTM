package chess.network.server;

import chess.database.Class.User;
import chess.database.DAO.UserDAO;
import chess.network.GsonUtil;
import chess.network.NetworkConfig;
import chess.network.transportpacket.LoginPacket;
import chess.network.transportpacket.PacketProccess;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Server bản đầu tiên:
 * Chỉ xử lý LOGIN_REQUEST / LOGIN_RESPONSE
 */
public class ChessServer {

    // =========================
    // SOCKET SERVER
    // =========================
    private ServerSocket serverSocket;

    // =========================
    // THREAD POOL
    // =========================
    private final ExecutorService threadPool =
            Executors.newCachedThreadPool();

    // =========================
    // CLIENT ONLINE
    // key = username
    // =========================
    private final Map<String, ClientHandler> connectedClients =
            new ConcurrentHashMap<>();


    // =========================
    // START SERVER
    // =========================
    public void start() {

        try {

            serverSocket = new ServerSocket(
                    NetworkConfig.SERVER_PORT,
                    NetworkConfig.BACKLOG
            );

            log("Server started at ip/port " +serverSocket.getInetAddress().getHostAddress());
            log("Waiting for clients...");

            while (!serverSocket.isClosed()) {

                Socket clientSocket = serverSocket.accept();

                ClientHandler client = new ClientHandler(clientSocket, this);

                threadPool.execute(client);

                log("New client: "
                        + clientSocket.getRemoteSocketAddress());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // STOP
    // =========================
    public void stop() {

        try {

            if (serverSocket != null &&
                    !serverSocket.isClosed()) {
                serverSocket.close();
            }

            threadPool.shutdown();

            log("Server stopped.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =====================================================
    // MAIN ROUTER
    // =====================================================
    public void handleMessage(ClientHandler client, String raw) {
        try {
            String header = PacketProccess.getPacketHeader(raw);

            if (header == null) {
                log("Invalid packet.");
                return;
            }

            switch (header) {
                // GÓI TIN LOGIN_REQUEST
                case NetworkConfig.LOGIN_REQUEST -> {
                    handleLogin(client, raw);
                }
                // GÓI TIN REGISTER_REQUEST
                case NetworkConfig.REGISTER_REQUEST -> {
                    handleRegister(client, raw);
                }
                // GÓI TIN LOGOUT_REQUEST
                case NetworkConfig.LOGOUT_REQUEST -> {
                    handleLogout(client);
                }

                default -> log("Unknown packet type: " + header);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =====================================================
    // HANDLE LOGIN REQUEST
    // =====================================================
    private void handleLogin(ClientHandler client, String raw) {
        try {
            LoginPacket request = PacketProccess.fromJson(raw, LoginPacket.class);

            LoginPacket response;
            
            // nếu không có gói tin hoặc thông tin không đầy đủ => Login thất bại
            if (request == null || request.getUser() == null) {
                response = PacketProccess.craftLoginResponsePacket(false, new User());
                
                client.send(PacketProccess.toJson(response));
                return;
            }

            // nếu đầy đủ thông tin(username, password)
            String username = request.getUser().getUserName();

            String password = request.getUser().getPasswordHash();

            // Kiểm tra User trong csdl
            User loginUser = UserDAO.checkLogin(username, password);

            // nếu có tồn tại
            if (loginUser != null) {
                response = PacketProccess.craftLoginResponsePacket(true, loginUser);

                client.setUser(loginUser);

                connectedClients.put(loginUser.getUserName(), client);

                log(username + " login success");
            } 
            // nếu không tồn tại
            else {
                response = PacketProccess.craftLoginResponsePacket(false, new User());

                log(username + " login fail");
            }

            client.send(PacketProccess.toJson(response));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // =====================================================
    // HANDLE REGISTER REQUEST
    // =====================================================
    private void handleRegister(ClientHandler client,String raw) {
        LoginPacket receivedPacket = PacketProccess.fromJson(raw, LoginPacket.class);
        
        LoginPacket success = PacketProccess.craftRegisterResponsePacket(true);
        LoginPacket fail = PacketProccess.craftRegisterResponsePacket(false);
        
        try {
            User user = receivedPacket.getUser();

            boolean ok = UserDAO.registerUser(user, 1200);

            LoginPacket response;

            if (ok) {
                response = success;
            } 
            else {
                response = fail;
            }

            client.send(PacketProccess.toJson(response));

        } catch (Exception e) {

            e.printStackTrace();

            client.send(PacketProccess.toJson(fail));
        }
    }
    //=====================================================
    

    // =====================================================
    // DISCONNECT
    // =====================================================
    // không nhất thiết phải lấy Packet do ClientHandler đã có User và đăng xuất chỉ gửi duy nhất yêu cầu
    public void handleLogout(ClientHandler  client) {
        try { 
            // Lấy ra tên user yêu cầu logout
            String logoutUsername = client.getUsername();
            // Lấy ra User trong csdl theo username của user yêu cầu logout
            User logoutUser = UserDAO.getUserbyUsername(logoutUsername);

            // kiểm tra 
            // nếu không tồn tại User trong csdl -> User không xác định => tạm thời không làm gì
            if(logoutUser == null) {
                // tạm thời báo lỗi user không xác định
                System.out.println("Anonymous user: " + logoutUsername);
                return;
            }
            // nếu có tồn tại 
            else {
                // Tạo LoginPacket có: type = LOGOUT_RESPONSE, result = LOGOUT_OK để 
                // gửi cho client rồi mới đóng kết nối
                System.out.println("Closing connection with client: " + client.getUsername());
            }
        }
        catch(Exception e) {
            System.out.println("Error in closing connection!");
        }
        finally {
                // luôn gửi và đóng kết nối dù gặp lỗi hay không
                LoginPacket response = PacketProccess.craftLogoutResponsePacket();
                client.send(PacketProccess.toJson(response));
            }    
    }
        
    
    // =====================================================
    // LOG
    // =====================================================
    private void log(String msg) {
        System.out.println("[ChessServer] " + msg);
    }

    public static void main(String[] args) {

            System.out.println("=== Chess Server ===");

            new ChessServer().start();
        }
    }

    

