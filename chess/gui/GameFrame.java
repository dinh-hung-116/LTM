package chess.gui;

import chess.gui.match.MatchPanel;
import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import chess.database.Class.User;

public class GameFrame {

    private JFrame frame;
    private MatchPanel matchPanel;
    private User currentUser;


    public GameFrame(User user) {
        this.currentUser = user;
        frame = new JFrame("Chess");
        frame.setLayout(new BorderLayout());

        matchPanel = new MatchPanel(currentUser);

        frame.add(matchPanel, BorderLayout.CENTER);
        
        

        frame.setSize(new Dimension(guiUtils.FRAME_WIDTH, guiUtils.FRAME_HEIGHT));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // center screen
        frame.setVisible(true);
    }

    public static void main(String[] args) {
            
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);

        });
        
    }

    public void setVisible(boolean b) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setVisible'");
    }
}