package chess.gui.match.gamesidebar;


import chess.gui.guiUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JPanel;


// Lớp này chứa tên của User trong trận đấu
public class UserInfoPanel extends JPanel {

    // label để test tên
    public UserInfoPanel(String label) {
        this.setPreferredSize(guiUtils.USER_FRAME_DIMENSION);
        this.setLayout(new BorderLayout());
       
        this.setBackground(Color.GRAY);
        this.add(new JLabel(label));
        
        this.setVisible(true);
    }
    
}
