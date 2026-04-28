package com.chess.gui.match.matchinfo;

import com.chess.gui.guiUtils;
import java.awt.*;
import javax.swing.*;

public class PlayerInfoPanel extends JPanel {

    private CountDownTimerPanel clock;
    private JLabel playerInfo;
    private JLabel ratingLabel;
    private JLabel avatarLabel;
    private JPanel playerInfoPanel;

    public PlayerInfoPanel(String name) {
        // Main panel
        this.setPreferredSize(guiUtils.USER_FRAME_DIMENSION);
        this.setLayout(new BorderLayout());
        //this.setBackground(guiUtils.MATCH_BG); // chess.com style
        this.setOpaque(false);
        // dòng này để hỗ trợ thiết kế nếu cần
        //this.setBorder(BorderFactory.createLineBorder(Color.RED));

        // ===== LEFT SIDE (Avatar + Name + Rating) =====
        playerInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        playerInfoPanel.setOpaque(false); // inherit background

        // Avatar
        avatarLabel = new JLabel(loadAvatar("/com/chess/gui/resources/user-image.png")); //muon chay thi them com vao

        // Player name
        playerInfo = new JLabel(name);
        playerInfo.setForeground(Color.WHITE);
        playerInfo.setFont(new Font("Arial", Font.BOLD, 14));
        // cái này để thêm padding giữa ảnh và chữ
        playerInfo.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));

        // Rating (test)
        ratingLabel = new JLabel("(1500)");
        ratingLabel.setForeground(Color.LIGHT_GRAY);
        ratingLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        // Add to left panel
        playerInfoPanel.add(avatarLabel);
        playerInfoPanel.add(playerInfo);
        playerInfoPanel.add(ratingLabel);

        // ===== RIGHT SIDE (Clock) =====
        clock = new CountDownTimerPanel(guiUtils.MATCH_DURATION);

        // Add to main panel
        this.add(playerInfoPanel, BorderLayout.WEST);
        this.add(clock, BorderLayout.EAST);
    }

    // phương thức để lớp khác tương tác
    // chạy đồng hồ, dùng đồng hồ và làm mới đồng hồ
    public void startClock() {
        this.clock.startTimer();
    }

    public void stopClock() {
        this.clock.pauseTimer();
    }

    public void resetClock() {
        this.clock.resetTimer();
    }

    // Cập nhật tên người chơi (dùng khi bắt đầu ván online)
    public void setUsername(String name) {
        this.playerInfo.setText(name);
    }

    // phương thức lấy ảnh avatar của người chơi
    // Helper method to load & scale avatar
    private ImageIcon loadAvatar(String path) {
        ImageIcon icon = new ImageIcon(
                getClass().getResource(path)
        );
        Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}