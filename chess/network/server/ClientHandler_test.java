package chess.network.server;

import chess.database.Class.User;

import java.io.*;
import java.net.Socket;

/**
 * ClientHandler_test
 *
 * Mỗi client kết nối vào server sẽ có 1 object này.
 * Nó chỉ làm 3 việc:
 *
 * 1. Đọc message từ socket
 * 2. Chuyển message lên ChessServer_test xử lý
 * 3. Gửi message ngược về client
 *
 * Không chứa business logic.
 */
public class ClientHandler_test implements Runnable {

    // =========================
    // SOCKET
    // =========================
    private final Socket socket;

    // =========================
    // I/O
    // =========================
    private final BufferedReader reader;
    private final PrintWriter writer;

    // =========================
    // SERVER REFERENCE
    // =========================
    private final ChessServer_test server;

    // =========================
    // USER SESSION
    // =========================
    private User user;
    private String username = "";

    // =========================
    // STATUS
    // =========================
    private volatile boolean running = true;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================
    public ClientHandler_test(
            Socket socket,
            ChessServer_test server
    ) throws IOException {

        this.socket = socket;
        this.server = server;

        this.reader = new BufferedReader(
                new InputStreamReader(
                        socket.getInputStream()
                )
        );

        this.writer = new PrintWriter(
                new OutputStreamWriter(
                        socket.getOutputStream()
                ),
                true // auto flush
        );
    }

    // =====================================================
    // MAIN LOOP
    // =====================================================
    @Override
    public void run() {

        log("Connected from "
                + socket.getRemoteSocketAddress());

        try {

            String line;

            while (running &&
                    (line = reader.readLine()) != null) {

                line = line.trim();

                if (!line.isEmpty()) {

                    log("← " + line);

                    // delegate to server
                    server.handleMessage(this, line);
                }
            }

        } catch (IOException e) {

            if (running) {
                log("Connection lost: "
                        + e.getMessage());
            }

        } finally {
            disconnect();
        }
    }

    // =====================================================
    // SEND TO CLIENT
    // =====================================================
    public synchronized void send(String message) {

        if (!socket.isClosed()) {

            log("→ " + message);

            writer.println(message);
        }
    }

    // =====================================================
    // DISCONNECT
    // =====================================================
    public void disconnect() {

        running = false;

        try {

            if (!socket.isClosed()) {
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        server.onClientDisconnected(this);

        log("Disconnected.");
    }

    // =====================================================
    // SESSION HELPERS
    // =====================================================
    public boolean isConnected() {
        return running && !socket.isClosed();
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    // =====================================================
    // GETTER / SETTER
    // =====================================================
    public User getUser() {
        return user;
    }

    public void setUser(User user) {

        this.user = user;

        if (user != null) {
            this.username = user.getUserName();
        } else {
            this.username = "";
        }
    }

    public String getUsername() {
        return username;
    }

    public Socket getSocket() {
        return socket;
    }

    // =====================================================
    // LOG
    // =====================================================
    private void log(String msg) {

        String tag;

        if (username == null || username.isEmpty()) {
            tag = socket.getRemoteSocketAddress().toString();
        } else {
            tag = username;
        }

        System.out.println(
                "[ClientHandler:" + tag + "] " + msg
        );
    }
}