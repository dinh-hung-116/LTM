package com.chess.gui;

import com.chess.gui.match.MatchPanel;
import com.chess.gui.ui.lobby.LobbyPanel;
import com.chess.gui.ui.login.LoginPanel;
import com.chess.gui.ui.login.RegisterPanel;
import com.chess.network.NetworkClient;
import com.chess.network.NetworkConfig;
import com.chess.database.Class.User;

import javax.swing.*;
import java.awt.*;

public class GameFrame {

    private JFrame frame;

    // cardlayout để lưu và show ra đúng 1 panel cần thiết
    private CardLayout cardLayout;
    private JPanel mainPanel;

    private MatchPanel matchPanel;
    private LobbyPanel lobbyPanel;
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;

    // Card names
    private static final String LOGIN    = "LOGIN";
    private static final String REGISTER = "REGISTER";
    private static final String LOBBY    = "LOBBY";
    private static final String MATCH    = "MATCH";

    // ── Network & User ────────────────────────────────────────
    private NetworkClient networkClient;
    private User          currentUser;

    public GameFrame() {
        initComponents();
        setupCards();
        setupListeners();
        showFrame();
    }

    private void initComponents() {
        frame = new JFrame("Chess");

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        matchPanel = new MatchPanel();
        lobbyPanel = new LobbyPanel();
        loginPanel = new LoginPanel();
        registerPanel = new RegisterPanel();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(guiUtils.FRAME_WIDTH, guiUtils.FRAME_HEIGHT));
        frame.setLocationRelativeTo(null);
    }

    private void setupCards() {
        mainPanel.add(loginPanel, LOGIN);
        mainPanel.add(registerPanel, REGISTER);
        mainPanel.add(lobbyPanel, LOBBY);
        mainPanel.add(matchPanel, MATCH);

        frame.add(mainPanel, BorderLayout.CENTER);
    }

    private void setupListeners() {

        // ── ĐĂNG NHẬP ─────────────────────────────────────────
        loginPanel.setLoginListener(user -> {
            this.currentUser = user;
            lobbyPanel.getSidebar().setUsername(user.getFullName());

            // Chuyển sang Lobby trước
            showCard(LOBBY);

            // Kết nối server trên thread riêng (tránh đơ UI nếu server chậm)
            new Thread(() -> connectToServer(user.getUserName()), "ServerConnect").start();
        });

        // Register back -> Login
        registerPanel.setBackListener(() -> showCard(LOGIN));

        // Nút đăng ký ở login
        loginPanel.setRegisterListener(() -> showCard(REGISTER));

        // ── ĐĂNG XUẤT ─────────────────────────────────────────
        lobbyPanel.getSidebar().addLogoutListener(e -> {
            if (networkClient != null) {
                networkClient.disconnect();
                networkClient = null;
            }
            currentUser = null;
            showCard(LOGIN);
        });

        // ── TÌM TRẬN ──────────────────────────────────────────
        lobbyPanel.getFindMatchPanel().addFindMatchListener(e -> {
            if (currentUser == null) return;

            // Kiểm tra còn kết nối không (nếu bị drop thì reconnect)
            if (networkClient == null || !networkClient.isConnected()) {
                connectToServer(currentUser.getUserName());
            }

            // Đổi nút thành "Đang tìm..."
            lobbyPanel.getFindMatchPanel().setFinding(true);

            // Gửi FIND_MATCH (đã kết nối và LOGIN từ lúc đăng nhập)
            networkClient.send(NetworkConfig.FIND_MATCH);
        });
    }

    // ── KẾT NỐI SERVER ────────────────────────────────────────
    /**
     * Kết nối TCP đến server, gửi LOGIN, gắn toàn bộ MessageListener.
     * Gọi ngay sau khi đăng nhập thành công.
     */
    private void connectToServer(String username) {
        networkClient = new NetworkClient();
        boolean connected = networkClient.connect();

        if (!connected) {
            SwingUtilities.invokeLater(() -> {
                lobbyPanel.getSidebar().setConnectionStatus(false);
                JOptionPane.showMessageDialog(frame,
                        "Không thể kết nối server!\nKiểm tra lại IP: "
                                + NetworkConfig.SERVER_IP + ":" + NetworkConfig.SERVER_PORT,
                        "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
            });
            return;
        }

        // Gắn MessageListener TRƯỚC khi gửi LOGIN
        networkClient.setMessageListener(raw -> {
            String command = NetworkConfig.getCommand(raw);
            String data    = NetworkConfig.getData(raw);

            SwingUtilities.invokeLater(() -> {
                switch (command) {

                    case NetworkConfig.LOGIN_OK -> {
                        // Kết nối + đăng nhập server thành công
                        System.out.println("[GameFrame] Đã kết nối server: " + username);
                        lobbyPanel.getSidebar().setConnectionStatus(true);
                    }

                    case NetworkConfig.QUEUED -> {
                        // Đang trong hàng đợi tìm trận
                    }

                    case NetworkConfig.MATCH_FOUND -> {
                        // Tìm được đối thủ → hỏi xác nhận
                        int confirm = JOptionPane.showConfirmDialog(
                                frame,
                                "Tìm được đối thủ: " + data
                                        + "\nBạn có muốn bắt đầu ván đấu không?",
                                "Ghép trận",
                                JOptionPane.YES_NO_OPTION
                        );
                        if (confirm == JOptionPane.YES_OPTION) {
                            networkClient.send(NetworkConfig.CONFIRM_MATCH);
                        } else {
                            networkClient.send(NetworkConfig.REJECT_MATCH);
                            lobbyPanel.getFindMatchPanel().setFinding(false);
                        }
                    }

                    case NetworkConfig.ASSIGN -> {
                        // data = "WHITE" hoặc "BLACK"
                        boolean isWhite = data.equals(NetworkConfig.SIDE_WHITE);
                        lobbyPanel.getFindMatchPanel().setFinding(false);

                        matchPanel.setReadyListener(() -> showCard(MATCH));
                        SwingUtilities.invokeLater(() ->
                                matchPanel.startNetworkMatch(
                                        networkClient,
                                        isWhite,
                                        currentUser.getFullName(),
                                        "Đối thủ"
                                )
                        );
                    }

                    case NetworkConfig.MATCH_REJECTED,
                         NetworkConfig.MATCH_TIMEOUT -> {
                        lobbyPanel.getFindMatchPanel().setFinding(false);
                        JOptionPane.showMessageDialog(frame,
                                "Đối thủ từ chối hoặc hết giờ. Thử lại nếu muốn.");
                    }

                    case NetworkConfig.PENALTY -> {
                        JOptionPane.showMessageDialog(frame,
                                "Bạn bị trừ " + data + " điểm do từ chối ghép trận.",
                                "Phạt điểm", JOptionPane.WARNING_MESSAGE);
                    }

                    default -> { /* MatchPanel tự xử lý MOVE, GAME_OVER, CLOCK_* */ }
                }
            });
        });

        // Khi mất kết nối bất ngờ
        networkClient.setDisconnectListener(() ->
                SwingUtilities.invokeLater(() -> {
                    lobbyPanel.getSidebar().setConnectionStatus(false);
                    lobbyPanel.getFindMatchPanel().setFinding(false);
                    JOptionPane.showMessageDialog(frame,
                            "Mất kết nối với server!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                })
        );

        // Gửi LOGIN (listener đã gắn xong)
        networkClient.send(NetworkConfig.msg(NetworkConfig.LOGIN, username));
    }

    private void showFrame() {
        frame.setVisible(true);
        showCard(LOGIN); // first screen
    }

    private void showCard(String cardName) {
        cardLayout.show(mainPanel, cardName);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameFrame::new);
    }
}