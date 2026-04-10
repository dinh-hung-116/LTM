package chess.gui.Class;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JPanel;


// Lớp này chứa bàn cờ, khung tên người dùng và ô tính năng
// lớp này sẽ là một JPanel và dùng gridbaglayout
public class MatchPanel extends JPanel {
    // bàn cờ
    private BoardPanel boardPanel;
    
    // ô tính năng
    private FeaturePanel featurePanel;
    
    // dùng để điều chỉnh layout
    private GridBagConstraints gbc;
    
    // 2 khung tên người dùng sẽ được chỉnh "cứng"

    public MatchPanel() {
        super.setSize(guiUtils.OUTER_FRAME_DIMENSION);
        super.setLayout(new GridBagLayout());
        
        // khởi tạo thành phần
        this.boardPanel = new WhiteBoardPanel();
        
        this.featurePanel = new FeaturePanel();
        
        this.gbc = new GridBagConstraints();
        
        // add components
        initMatchPanel();
        
    }
    
    // gui sẽ có 2 cột, cột đầu tiên có 3 block và cột thứ hai có một block 
    // nó giống kiểu này:
    // [A] [B]
    // [C] [B]
    // [D] [B]
    
    //Insets(top, left, bottom, right)
    
    public void initMatchPanel() {
        //---LEFT COLUMN--
        // A(x;y) = (0;0)
        // UserA
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        gbc.fill = GridBagConstraints.BOTH;
        
        gbc.insets = new Insets(10, 5, 10, 10);
        
        this.add(new UserInfoPanel("UserA"), gbc);
        
        // C(0;1)
        // BoardPanel
        gbc.gridx = 0;
        gbc.gridy = 1;
        
        gbc.fill = GridBagConstraints.BOTH;
        
        gbc.insets = new Insets(10, 5, 10, 10);
        
        this.add(this.boardPanel, gbc);
        
        // D(0;2)
        // UserB
        gbc.gridx = 0;
        gbc.gridy = 2;
        
        gbc.fill = GridBagConstraints.BOTH;
        
        gbc.insets = new Insets(10, 5, 10, 10);
        
        this.add(new UserInfoPanel("UserB"), gbc);
        
        //--RIGHT COLUMN--
        // B(1;0)
        gbc.gridx = 1;
        gbc.gridy = 0;
        // ghép 3 block lại thành 1
        gbc.gridheight = 3;
        
        gbc.fill = GridBagConstraints.BOTH;
        
        gbc.insets = new Insets(10, 10, 10, 10);
        
        this.add(this.featurePanel, gbc);
    }
    
    
}
