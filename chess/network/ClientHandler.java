package com.chess.network;

import com.chess.database.Class.User;

import java.io.*;
import java.net.Socket;

/*
 * ClientHandler - Mỗi client kết nối vào server sẽ được giao cho một
 * ClientHandler chạy trên thread riêng.
 *
 * Nhiệm vụ:
 *  - Đọc tin nhắn từ client liên tục (vòng lặp while)
 *  - Chuyển tin nhắn lên ChessServer xử lý
 *  - Cung cấp send() để server gửi tin nhắn ngược về client
 */
public class ClientHandler implements Runnable {

    // ── Socket & I/O ──────────────────────────────────────────
    private final Socket         socket;
    private final BufferedReader reader;
    private final PrintWriter    writer;

    // ── Thông tin người chơi ──────────────────────────────────
    private User   user;          // null cho đến khi đăng nhập thành công
    private String username = ""; // tên đăng nhập (cache để log nhanh)

    // ── Tham chiếu lên server ─────────────────────────────────
    private final ChessServer server;

    // ── Trạng thái ───────────────────────────────────────────
    private volatile boolean running = true;

    // ─────────────────────────────────────────────────────────
    public ClientHandler(Socket socket, ChessServer server) throws IOException {
        this.socket = socket;
        this.server = server;
        this.reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        this.writer = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream()), true); // autoFlush
    }

    // ─────────────────────────────────────────────────────────
    // VÒNG LẶP ĐỌC TIN NHẮN
    // ─────────────────────────────────────────────────────────
    @Override
    public void run() {
        log("Kết nối từ " + socket.getRemoteSocketAddress());
        try {
            String line;
            while (running && (line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    log("← " + line);
                    server.handleMessage(this, line);
                }
            }
        } catch (IOException e) {
            if (running) {
                log("Mất kết nối: " + e.getMessage());
            }
        } finally {
            disconnect();
        }
    }

    // ─────────────────────────────────────────────────────────
    // GỬI TIN NHẮN VỀ CLIENT
    // ─────────────────────────────────────────────────────────
    /*
     * Thread-safe: PrintWriter với autoFlush=true, dùng synchronized
     * để tránh interleaving khi nhiều thread cùng gửi cho một client.
     */
    public synchronized void send(String message) {
        if (!socket.isClosed()) {
            log("→ " + message);
            writer.println(message);
        }
    }

    // ─────────────────────────────────────────────────────────
    // NGẮT KẾT NỐI
    // ─────────────────────────────────────────────────────────
    public void disconnect() {
        running = false;
        try {
            if (!socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.onClientDisconnected(this);
        log("Đã ngắt kết nối.");
    }

    // ─────────────────────────────────────────────────────────
    // GETTER / SETTER
    // ─────────────────────────────────────────────────────────
    public User   getUser()     { return user; }
    public String getUsername() { return username; }

    // Đánh dấu đã đăng nhập: hoặc có User object, hoặc có username
    public boolean isLoggedIn() { return user != null || !username.isEmpty(); }

    public void setUser(User user) {
        this.user     = user;
        this.username = (user != null) ? user.getUserName() : "";
    }

    // Dùng khi không có User object (server nhận LOGIN:username)
    public void setUsername(String name) {
        this.username = (name != null) ? name.trim() : "";
    }

    public boolean isConnected() {
        return running && !socket.isClosed();
    }

    // ─────────────────────────────────────────────────────────
    // LOG
    // ─────────────────────────────────────────────────────────
    private void log(String msg) {
        String tag = username.isEmpty()
                ? socket.getRemoteSocketAddress().toString()
                : username;
        System.out.println("[ClientHandler:" + tag + "] " + msg);
    }
}