package chess.network.server;

import chess.database.Class.User;
import chess.database.DAO.UserDAO;
import chess.network.GsonUtil;
import chess.network.NetworkConfig;
import chess.network.transportpacket.LoginPacket;

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
public class ChessServer_test {

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
    private final Map<String, ClientHandler_test> connectedClients =
            new ConcurrentHashMap<>();

    // =========================
    // JSON
    // =========================
    private final Gson gson = GsonUtil.createGson();

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

                ClientHandler_test client = new ClientHandler_test(clientSocket, this);

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
    public void handleMessage(ClientHandler_test client, String raw) {
        try {
            PacketHeader header = gson.fromJson(raw, PacketHeader.class);

            if (header == null || header.type == null) {
                log("Invalid packet.");
                return;
            }

            switch (header.type) {
                // GÓI TIN LOGIN_REQUEST
                case NetworkConfig.LOGIN_REQUEST -> {
                    handleLogin(client, raw);
                }
                // GÓI TIN REGISTER_REQUEST
                case NetworkConfig.REGISTER_REQUEST -> {
                    handleRegister(client, raw);
                }

                default -> log("Unknown packet type: " + header.type);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =====================================================
    // HANDLE LOGIN REQUEST
    // =====================================================
    private void handleLogin(ClientHandler_test client, String raw) {

        try {
            LoginPacket request = gson.fromJson(raw, LoginPacket.class);

            LoginPacket response = new LoginPacket();
            response.setType(NetworkConfig.LOGIN_RESPONSE);

            if (request == null || request.getUser() == null) {

                response.setResult(NetworkConfig.LOGIN_FAIL);
                client.send(gson.toJson(response));
                return;
            }

            String username = request.getUser().getUserName();

            String password = request.getUser().getPasswordHash();

            UserDAO dao = new UserDAO();

            // chỉnh theo hàm login thật của bạn
            User loginUser = UserDAO.checkLogin(username, password);

            if (loginUser != null) {
                response.setResult(NetworkConfig.LOGIN_OK);
                response.setUser(loginUser);

                client.setUser(loginUser);

                connectedClients.put(
                        loginUser.getUserName(),
                        client
                );

                log(username + " login success");
            } 
            else {

                response.setResult(NetworkConfig.LOGIN_FAIL);

                log(username + " login fail");
            }

            client.send(gson.toJson(response));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // =====================================================
    // HANDLE REGISTER REQUEST
    // =====================================================
    private void handleRegister(ClientHandler_test client,String raw) {
        try {
            LoginPacket packet = gson.fromJson(raw, LoginPacket.class);

            User user = packet.getUser();

            boolean ok = UserDAO.registerUser(user, 1200);

            LoginPacket response = new LoginPacket();

            response.setType(NetworkConfig.REGISTER_RESPONSE);

            if (ok) {
                response.setResult(NetworkConfig.REGISTER_OK);
            } 
            else {
                response.setResult(NetworkConfig.REGISTER_FAIL);
            }

            client.send(gson.toJson(response));

        } catch (Exception e) {

            e.printStackTrace();

            LoginPacket fail = new LoginPacket();

            fail.setType(NetworkConfig.REGISTER_RESPONSE);

            fail.setResult(NetworkConfig.REGISTER_FAIL);

            client.send(gson.toJson(fail));
        }
    }
    //=====================================================
    

    // =====================================================
    // DISCONNECT
    // =====================================================
    public void onClientDisconnected(
            ClientHandler_test client
    ) {

        String username = client.getUsername();

        if (username != null &&
                !username.isEmpty()) {

            connectedClients.remove(username);

            log(username + " disconnected");

        } else {

            log("Anonymous client disconnected");
        }
    }

    // =====================================================
    // LOG
    // =====================================================
    private void log(String msg) {
        System.out.println("[ChessServer] " + msg);
    }

    // =====================================================
    // SMALL HEADER CLASS
    // only read packet type first
    // =====================================================
    private class PacketHeader {
        String type;
    }

    // =====================================================
    // MAIN
    // =====================================================
    public static void main(String[] args) {

        System.out.println("=== Chess Server ===");

        new ChessServer_test().start();
    }
}