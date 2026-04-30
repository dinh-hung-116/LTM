package chess.network.server;

import chess.database.DTO.User;


/**
 * Lớp này chứa các thông tin và trạng thái của một người chơi
 */
public class UserSession {
    // Lớp enum lưu trạng thái của người dùng như online, đang trong ván đấu,....
    public enum PlayerStatus {
        ONLINE("ONLINE"),
        IN_QUEUE("IN_QUEUE"),
        IN_GAME("IN_MATCH"),
        DISCONNECTED("DISCONNECTED");

        private final String display;

        PlayerStatus(String display) {
            this.display = display;
        }

        @Override
        public String toString() {
            return display;
        }
    }

    private User user;                      // thông tin user (từ DB)
    private ClientHandler clientHandler;    // kết nối socket

    private PlayerStatus status;            // trạng thái hiện tại
    // có thể sẽ có một lớp quản lý ván đấu


    // ================= CONSTRUCTOR =================
    public UserSession(User user, ClientHandler clientHandler) {
        this.user = user;
        this.clientHandler = clientHandler;
        this.status = PlayerStatus.ONLINE;
    }

    // ================= GETTER =================
    public User getUser() {
        return user;
    }

    public String getUsername() {
        return user != null ? user.getUserName() : "Anonymous_User";
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    public PlayerStatus getStatus() {
        return status;
    }


    // ================= SETTER =================
    public void setClientHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // ================= HELPER =================

    public boolean isOnline() {
        return status == PlayerStatus.ONLINE;
    }

    public boolean isInQueue() {
        return status == PlayerStatus.IN_QUEUE;
    }

    public boolean isInGame() {
        return status == PlayerStatus.IN_GAME;
    }

    public boolean isDisconnected() {
        return status == PlayerStatus.DISCONNECTED;
    }

   // ====== GỬI DỮ LIỆU ======
    public void sendPacket(String message) {
        if (clientHandler != null) {
            clientHandler.send(message);
        }
    }
    
}