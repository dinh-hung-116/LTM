package chess.gui.Class;

import javax.swing.*;
import java.awt.*;
import chess.gui.Class.BoardPanel;
import chess.gui.Assets;

public class Table {

    private JFrame frame;
    private MatchPanel matchPanel;

    public Table() {
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
            new Table();
        });
        
    }
}