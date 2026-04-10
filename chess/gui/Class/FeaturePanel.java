package chess.gui.Class;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JPanel;



// lớp này chứa đồng hồ đếm giờ, các nước đi đã thực hiện, chat,... 
class FeaturePanel extends JPanel {

    public FeaturePanel() {
        this.setPreferredSize(guiUtils.FEATURE_FRAME_DIMENSION);
        this.setLayout(new BorderLayout());
        
        this.setBackground(Color.gray);
        
        this.setVisible(true);
    }
    
    
}
