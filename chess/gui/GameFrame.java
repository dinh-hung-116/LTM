package chess.gui;

import chess.gui.match.MatchPanel;
import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameFrame {

    private JFrame frame;
    private MatchPanel matchPanel;

    public GameFrame() {
        frame = new JFrame("Chess");
        frame.setLayout(new BorderLayout());

        matchPanel = new MatchPanel();

        frame.add(matchPanel, BorderLayout.CENTER);
        
        

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