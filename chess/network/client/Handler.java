package chess.network.client;

import chess.database.DTO.User;
import chess.gui.GameFrame;
import chess.network.GsonUtil;
import chess.network.NetworkConfig;
import chess.network.transportpacket.LoginPacket;
import chess.network.transportpacket.PacketProcess;
import com.google.gson.Gson;
import java.time.LocalDate;

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
    public void loginRequest(String username, String password) {

        LoginPacket packet = PacketProcess.craftLoginRequestPacket(username, password);

        networkClient.send(PacketProcess.toJson(packet));
        
    }

    // PARSE LOGIN RESPONSE
    public User loginResponse(String json) {

        LoginPacket packet = PacketProcess.fromJson(json, LoginPacket.class);

        if (packet.getResult() != null && packet.getResult().equals(NetworkConfig.LOGIN_OK)) {
            return packet.getUser();
        }

        return null;
    }
    
    //==========================
    // REGISTER
    // ==========================
    // REGISTER REQUEST
    public void registerRequest(User user) {

        LoginPacket packet = PacketProcess.craftRegisterRequestPacket(user);

        networkClient.send(PacketProcess.toJson(packet));
    }
    
    // REGISTER RESPONSE
    public boolean registerResponse(String json) {

        LoginPacket packet = PacketProcess.fromJson(json, LoginPacket.class);

        return NetworkConfig.REGISTER_OK.equals(packet.getResult());
    }
    
    //==========================
    // LOGOUT
    // ==========================
    // LOGOUT REQUEST
    public void logoutRequest() {

        LoginPacket packet = PacketProcess.craftLogoutRequestPacket();

        networkClient.send(PacketProcess.toJson(packet));
    }

    // =====================================================
    // MAIN ROUTER
    // =====================================================
    public void handleServerMessage(String msg) {

        try {
            String header = PacketProcess.getPacketHeader(msg);

            if (header == null) {
                System.out.println("Invalid packet header");
                return;
            }

            switch (header) {
                // GÓI TIN LOGIN
                case NetworkConfig.LOGIN_RESPONSE -> {
                    User loginUser = loginResponse(msg);

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
                        frame.onRegisterFail("Tên đăng nhập đã tồn tại!");
                    }
                    break;
                }

                default -> System.out.println("Unknown packet type: " + header);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}