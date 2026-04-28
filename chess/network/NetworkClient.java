package com.chess.network;

import java.io.*;
import java.net.Socket;

/**
 * NetworkClient - Phía client kết nối đến ChessServer.
 *
 * Chạy trên một thread riêng, liên tục đọc tin nhắn từ server.
 * Khi nhận được tin nhắn, gọi MessageListener để tầng GUI xử lý.
 *
 * Cách dùng:
 *   NetworkClient client = new NetworkClient();
 *   client.setMessageListener(msg -> handleServerMessage(msg));
 *   client.connect();
 *   client.send(NetworkConfig.FIND_MATCH);
 */
public class NetworkClient {

    // ── Socket & I/O ──────────────────────────────────────────
    private Socket         socket;
    private BufferedReader reader;
    private PrintWriter    writer;

    // ── Trạng thái ───────────────────────────────────────────
    private volatile boolean running = false;
    private Thread           readerThread;

    // ── Callback khi nhận tin từ server ───────────────────────
    public interface MessageListener {
        /**
         * Gọi từ NETWORK THREAD khi nhận được 1 dòng từ server.
         * Nếu cần update GUI thì phải dùng SwingUtilities.invokeLater().
         */
        void onMessage(String message);
    }

    // Callback khi kết nối bị ngắt
    public interface DisconnectListener {
        void onDisconnected();
    }

    private MessageListener    messageListener;
    private DisconnectListener disconnectListener;

    // ─────────────────────────────────────────────────────────
    // KẾT NỐI
    // ─────────────────────────────────────────────────────────
    /**
     * Kết nối tới server với IP/port trong NetworkConfig.
     * @return true nếu kết nối thành công
     */
    public boolean connect() {
        return connect(NetworkConfig.SERVER_IP, NetworkConfig.SERVER_PORT);
    }

    public boolean connect(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream()), true);

            running = true;
            startReaderThread();
            log("Kết nối thành công tới " + ip + ":" + port);
            return true;

        } catch (IOException e) {
            log("Không thể kết nối: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────
    // VÒNG LẶP ĐỌC TIN NHẮN (chạy trên thread riêng)
    // ─────────────────────────────────────────────────────────
    private void startReaderThread() {
        readerThread = new Thread(() -> {
            try {
                String line;
                while (running && (line = reader.readLine()) != null) {
                    final String msg = line.trim();
                    if (!msg.isEmpty()) {
                        log("← " + msg);
                        if (messageListener != null) {
                            messageListener.onMessage(msg);
                        }
                    }
                }
            } catch (IOException e) {
                if (running) {
                    log("Mất kết nối với server: " + e.getMessage());
                }
            } finally {
                disconnect();
                if (disconnectListener != null) {
                    disconnectListener.onDisconnected();
                }
            }
        }, "NetworkClient-Reader");

        readerThread.setDaemon(true); // thread tự dừng khi app đóng
        readerThread.start();
    }

    // ─────────────────────────────────────────────────────────
    // GỬI TIN NHẮN
    // ─────────────────────────────────────────────────────────
    /** Thread-safe. Có thể gọi từ bất kỳ thread nào. */
    public synchronized void send(String message) {
        if (writer != null && isConnected()) {
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
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ─────────────────────────────────────────────────────────
    // SETTERS & GETTERS
    // ─────────────────────────────────────────────────────────
    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }

    public void setDisconnectListener(DisconnectListener listener) {
        this.disconnectListener = listener;
    }

    public boolean isConnected() {
        return running && socket != null && !socket.isClosed();
    }

    // ─────────────────────────────────────────────────────────
    // LOG
    // ─────────────────────────────────────────────────────────
    private void log(String msg) {
        System.out.println("[NetworkClient] " + msg);
    }
}