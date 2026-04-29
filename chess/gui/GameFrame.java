package chess.gui;

import chess.database.Class.User;
import chess.gui.match.MatchPanel;
import chess.gui.ui.lobby.LobbyPanel;
import chess.gui.ui.login.LoginPanel;
import chess.gui.ui.login.RegisterPanel;
import chess.network.client.Handler;
import chess.network.client.NetworkClient;

import javax.swing.*;
import java.awt.*;

public class GameFrame {

    private JFrame frame;

    // Main layout
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Panels
    private MatchPanel matchPanel;
    private LobbyPanel lobbyPanel;
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;

    // Network
    private NetworkClient networkClient;
    private Handler handler;

    // Current user
    private User currentUser;

    // Card names
    private static final String LOGIN = "LOGIN";
    private static final String REGISTER = "REGISTER";
    private static final String LOBBY = "LOBBY";
    private static final String MATCH = "MATCH";

    public GameFrame() {
        initComponents();
        setupCards();
        setupNetwork();
        setupListeners();
        showFrame();
    }

    // =========================
    // INIT
    // =========================
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

    // =========================
    // ADD CARDS
    // =========================
    private void setupCards() {

        mainPanel.add(loginPanel, LOGIN);
        mainPanel.add(registerPanel, REGISTER);
        mainPanel.add(lobbyPanel, LOBBY);
        mainPanel.add(matchPanel, MATCH);

        frame.add(mainPanel, BorderLayout.CENTER);
    }

    // =========================
    // NETWORK + HANDLER
    // =========================
    private void setupNetwork() {

        networkClient = new NetworkClient();

        boolean ok = networkClient.connect();

        if(!ok) {
            JOptionPane.showMessageDialog(
                null,
                "Không thể kết nối server!"
            );
            return;
        }

        handler = new Handler(this, networkClient);
        handler.setNetworkClient(networkClient);
    }

    // =========================
    // UI LISTENERS
    // =========================
    private void setupListeners() {

        // LOGIN REQUEST - handler
        loginPanel.setLoginRequestListener((username, password) -> {
            handler.LoginRequest(username, password);
        });

        // LOGIN PANEL -> REGISTER PANEL
        loginPanel.setRegisterListener(() -> {
            showCard(REGISTER);
        });

        // REGISTER PANEL -> LOGIN PANEL
        registerPanel.setBackListener(() -> {
            showCard(LOGIN);
        });
        
        // REGISTER REQUEST - handler
        registerPanel.setRegisterRequestListener(user -> {
            handler.registerRequest(user);
        });
        
    }

    // =========================
    // SHOW FRAME
    // =========================
    private void showFrame() {
        frame.setVisible(true);
        showCard(LOGIN);
    }

    private void showCard(String cardName) {
        cardLayout.show(mainPanel, cardName);
    }

    // =========================
    // LOGIN RESULT (called by Handler)
    // =========================
    /*
    - Các hàm này sẽ được handler gọi để cập nhật chỉnh sửa lên GUI
    - GameFrame đã trở thành một "điều phối viên" do sở hữu tham chiếu tới 
    các thành phần trong gui và Handle.
    */
    public void onLoginSuccess(User user) {

        this.currentUser = user;

        loginPanel.loginSuccess(user);

        showCard(LOBBY);
    }

    public void onLoginFailed() {
        loginPanel.loginFailed();
    }
    
    // =========================
    // REGISSTER RESULT (called by Handler)
    // =========================
    public void onRegisterSuccess() {
        registerPanel.showRegisterSuccess();
        showCard(LOGIN);
    }

    public void onRegisterFail(String message) {
        registerPanel.showRegisterFail(message);
    }
    //=========================

    // =========================
    // NAVIGATION HELPERS
    // =========================
    public void showLogin() {
        showCard(LOGIN);
    }

    public void showLobby() {
        showCard(LOBBY);
    }

    public void showMatch() {
        showCard(MATCH);
    }

    public void showRegister() {
        showCard(REGISTER);
    }

    // =========================
    // GETTERS
    // =========================
    public JFrame getFrame() {
        return frame;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public MatchPanel getMatchPanel() {
        return matchPanel;
    }

    public LobbyPanel getLobbyPanel() {
        return lobbyPanel;
    }

    public LoginPanel getLoginPanel() {
        return loginPanel;
    }

    public RegisterPanel getRegisterPanel() {
        return registerPanel;
    }

    public Handler getHandler() {
        return handler;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    // =========================
    // MAIN
    // =========================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameFrame::new);
    }
}