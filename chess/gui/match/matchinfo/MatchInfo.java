package chess.gui.match.matchinfo;


import chess.gui.guiUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JPanel;



// lớp này chứa đồng hồ đếm giờ, các nước đi đã thực hiện, chat,... 
public class MatchInfo extends JPanel {
    //private CountDownTimerPanel clock;
    
    public MatchInfo() {
        this.setPreferredSize(guiUtils.FEATURE_FRAME_DIMENSION);
        this.setLayout(new BorderLayout());
        
        this.setBackground(Color.gray);
        
        this.setVisible(true);
        
    }
    
    
}
