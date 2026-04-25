package chess.gui;

import chess.gui.match.MatchPanel;
import chess.gui.ui.lobby.LobbyPanel;
import chess.gui.ui.login.LoginPanel;
import chess.gui.ui.login.RegisterPanel;

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
    private static final String LOGIN = "LOGIN";
    private static final String REGISTER = "REGISTER";
    private static final String LOBBY = "LOBBY";
    private static final String MATCH = "MATCH";

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

        // Login success -> Lobby
        loginPanel.setLoginListener(user -> {
            showCard(LOBBY);
        });

        // Register back -> Login
        registerPanel.setBackListener(() -> {
            showCard(LOGIN);
        });
        
        // nút đăng ký ở login
        loginPanel.setRegisterListener(() -> {
            showCard(REGISTER);
        });

        /*
        Example future use:

        lobbyPanel.setStartMatchListener(() -> {
            showCard(MATCH);
        });

        matchPanel.setExitMatchListener(() -> {
            showCard(LOBBY);
        });
        */
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