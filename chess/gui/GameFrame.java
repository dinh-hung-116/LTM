package chess.gui;

import chess.gui.match.MatchPanel;
import chess.gui.ui.lobby.LobbyPanel;
import chess.gui.ui.login.LoginPanel;
import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameFrame {

    private JFrame frame;
    private MatchPanel matchPanel;
    private LobbyPanel lobbyPanel;
    private LoginPanel loginPanel;

    public GameFrame() {
        frame = new JFrame("Chess");
        frame.setLayout(new BorderLayout());

        matchPanel = new MatchPanel();
        lobbyPanel = new LobbyPanel();
        loginPanel = new LoginPanel();

        frame.add(loginPanel, BorderLayout.CENTER);
        
        

        frame.setSize(new Dimension(guiUtils.FRAME_WIDTH, guiUtils.FRAME_HEIGHT));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // center screen
        frame.setVisible(true);
    }

    public static void main(String[] args) {
            
        SwingUtilities.invokeLater(() -> {
            GameFrame gf = new GameFrame();

        });
        
    }
}