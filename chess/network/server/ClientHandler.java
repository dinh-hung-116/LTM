package chess.network.server;

import chess.network.session.UserSession;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    // biến để gửi / nhận dữ liệu
    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;
    
    // tham chiếu tới server để xử lý gói tin
    private final ChessServer server;

    // Cứ để đây nếu sau này cần thì lấy ra dùng(thường là không)
    private UserSession session;

    private volatile boolean running = true;

    public ClientHandler(Socket socket, ChessServer server) throws IOException {
        this.socket = socket;
        this.server = server;

        this.reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
        );

        this.writer = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream()),
                true
        );
    }

    @Override
    public void run() {
        log("Connected from " + socket.getRemoteSocketAddress());

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
                log("Connection lost: " + e.getMessage());
            }
        } finally {
            disconnect();
        }
    }

    // ================= SEND =================
    public synchronized void send(String message) {
        if (!socket.isClosed()) {
            log("→ " + message);
            writer.println(message);
        }
    }

    // ================= DISCONNECT =================
    public void disconnect() {
        running = false;

        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        server.handleLogout(this);

        log("Disconnected.");
    }

    // ================= SESSION =================
    public void setSession(UserSession session) {
        this.session = session;
    }

    public UserSession getSession() {
        return session;
    }

    public String getUsername() {
        return session != null ? session.getUsername() : "";
    }

    // ================= STATUS =================
    public boolean isConnected() {
        return running && !socket.isClosed();
    }

    public boolean isLoggedIn() {
        return session != null;
    }

    public Socket getSocket() {
        return socket;
    }

    // ================= LOG =================
    private void log(String msg) {

        String tag;

        if (session == null || session.getUsername() == null) {
            tag = socket.getRemoteSocketAddress().toString();
        } else {
            tag = session.getUsername();
        }

        System.out.println(
                "[ClientHandler:" + tag + "] " + msg
        );
    }
}