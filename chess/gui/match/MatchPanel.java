package chess.gui.match;

// Lớp này chứa bàn cờ, khung tên người dùng và ô tính năng

import chess.gui.match.chessboard.BlackBoardPanel;
import chess.gui.match.chessboard.BoardPanel;
import chess.gui.guiUtils;
import javax.swing.JPanel;

import chess.gui.match.chessboard.LocalBoardPanel;
import chess.gui.match.matchinfo.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.logging.Level;
import java.util.logging.Logger;


//package chess.gui.match;
//
//// Lớp này chứa bàn cờ, khung tên người dùng và ô tính năng
//
//import chess.gui.match.chessboard.BlackBoardPanel;
//import chess.gui.match.chessboard.BoardPanel;
//import chess.gui.guiUtils;
//import javax.swing.JPanel;
//import chess.gui.match.matchinfo.*;
//import java.awt.GridBagConstraints;
//import java.awt.GridBagLayout;
//import java.awt.Insets;
//import java.util.logging.Level;
//import java.util.logging.Logger;
// lớp này sẽ là một JPanel và dùng gridbaglayout
public class MatchPanel extends JPanel {
    // bàn cờ
    private BoardPanel boardPanel;
    
    // ô tính năng bên phải
    private SideBar sideBar;
    
    // hai ô thông tin người chơi
    private PlayerInfoPanel whiteInfo;
    private PlayerInfoPanel blackInfo;
    
    // dùng để điều chỉnh layout
    private GridBagConstraints gbc;
    
    // 2 khung tên người dùng sẽ tạm thời được chỉnh "cứng"

    public MatchPanel() {
        super.setSize(guiUtils.OUTER_FRAME_DIMENSION);
        super.setLayout(new GridBagLayout());
        
        // khởi tạo thành phần
        this.boardPanel = new LocalBoardPanel();
        
        this.sideBar = new SideBar();
        
        this.whiteInfo = new PlayerInfoPanel("UserA");
        this.blackInfo = new PlayerInfoPanel("UserB");
        
        this.gbc = new GridBagConstraints();
        
        this.setBackground(guiUtils.MATCH_BG);
        
        // add components
        initMatchPanel();
        
        // thử cho đồng hồ bên b chạy
        this.blackInfo.startClock();
        
    }
    
    // gui sẽ có 2 cột, cột đầu tiên có 3 block và cột thứ hai có một block 
    // nó giống kiểu này:
    // [A] [B]
    // [C] [B]
    // [D] [B]
    
    // Insets(top, left, bottom, right)
    // thêm thành phần vào panel
    public void initMatchPanel() {
        //---LEFT COLUMN--
        // A(x;y) = (0;0)
        // UserA
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        gbc.weighty = 0.2;
        
        gbc.fill = GridBagConstraints.BOTH;
        
        gbc.insets = new Insets(10, 5, 5, 10);
        
        // tạm thời cho userA và B vào đây
        this.add(this.whiteInfo, gbc);
        
        // C(0;1)
        // BoardPanel
        gbc.gridx = 0;
        gbc.gridy = 1;
        
        //gbc.weighty = 0.8;
        
        gbc.fill = GridBagConstraints.BOTH;
        
        gbc.insets = new Insets(5, 5, 5, 10);
        
        this.add(this.boardPanel, gbc);
        
        // D(0;2)
        // UserB
        gbc.gridx = 0;
        gbc.gridy = 2;
        
        gbc.weighty = 0.2;
        
        gbc.fill = GridBagConstraints.BOTH;
        
        gbc.insets = new Insets(5, 5, 10, 10);
        
        this.add(this.blackInfo, gbc);
        
        //--RIGHT COLUMN--
        // B(1;0)
        gbc.gridx = 1;
        gbc.gridy = 0;
        // ghép 3 block lại thành 1
        gbc.gridheight = 3;
        
        gbc.fill = GridBagConstraints.BOTH;
        
        gbc.insets = new Insets(10, 10, 10, 10);
        
        this.add(this.sideBar, gbc);
    }
    
    // có thể sẽ có phương thức để khởi động ván đấu
    
}
