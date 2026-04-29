package chess.network.client;

import chess.database.Class.User;
import chess.gui.GameFrame;
import chess.network.GsonUtil;
import chess.network.NetworkConfig;
import chess.network.transportpacket.LoginPacket;
import com.google.gson.Gson;

import javax.swing.*;

public class Handler {

    private GameFrame frame;
    private NetworkClient networkClient;

    private final Gson gson = GsonUtil.createGson();

    public Handler(GameFrame frame, NetworkClient networkClient) {
        this.frame = frame;
        this.networkClient = networkClient;
    }

    // =========================
    // SETTERS
    // =========================
    public void setNetworkClient(NetworkClient nc) {
        this.networkClient = nc;

        // when NetworkClient receives message
        this.networkClient.setMessageListener(this::handleServerMessage);
    }

    public void setGameFrame(GameFrame gf) {
        this.frame = gf;
    }

    // =====================================================
    // LOGIN
    // =====================================================

    // LOGIN REQUEST
    public void LoginRequest(String username, String password) {

        LoginPacket packet = new LoginPacket();

        packet.setType(NetworkConfig.LOGIN_REQUEST);
        packet.setResult("");

        // TEMP:
        // later use packet.setUsername(username)
        // later use packet.setPassword(password)

        // if packet has user object:
        User tempUser = new User(username, password);

        packet.setUser(tempUser);

        String json = gson.toJson(packet);

        networkClient.send(json);
    }

    // PARSE LOGIN RESPONSE
    public User LoginResponse(String json) {

        LoginPacket packet = gson.fromJson(json, LoginPacket.class);

        if (packet.getResult() != null &&
                packet.getResult().equals(NetworkConfig.LOGIN_OK)) {

            return packet.getUser();
        }

        return null;
    }
    
    //==========================
    // REGISTER
    // ==========================
    // REGISTER REQUEST
    public void registerRequest(User user) {

        LoginPacket packet = new LoginPacket();

        packet.setType(NetworkConfig.REGISTER_REQUEST);
        packet.setUser(user);
        packet.setResult("");

        String json = gson.toJson(packet);

        networkClient.send(json);
    }
    
    // REGISTER RESPONSE
    public boolean registerResponse(String json) {

        LoginPacket packet = gson.fromJson(json, LoginPacket.class);

        return NetworkConfig.REGISTER_OK.equals(packet.getResult());
    }

    // =====================================================
    // MAIN ROUTER
    // =====================================================
    public void handleServerMessage(String msg) {

        try {

            PacketHeader header = gson.fromJson(msg, PacketHeader.class);

            if (header == null || header.type == null) {
                System.out.println("Invalid packet header");
                return;
            }

            switch (header.type) {
                // GÓI TIN LOGIN
                case NetworkConfig.LOGIN_RESPONSE -> {
                    User loginUser = LoginResponse(msg);

                    SwingUtilities.invokeLater(() -> {

                        if (loginUser != null) {
                            frame.onLoginSuccess(loginUser);
                        } else {
                            frame.onLoginFailed();
                        }

                    });
                    break;
                }
                // GÓI TIN REGISTER 
                case NetworkConfig.REGISTER_RESPONSE -> {
                    boolean success = registerResponse(msg);

                    if (success) {
                        frame.onRegisterSuccess();
                    } else {
                        frame.onRegisterFail(
                            "Tên đăng nhập đã tồn tại!"
                        );
                    }
                    break;
                }

                default -> System.out.println("Unknown packet type: " + header.type);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =====================================================
    // SMALL HELPER CLASS
    // only read packet type first
    // =====================================================
    private class PacketHeader {
        String type;
    }
}