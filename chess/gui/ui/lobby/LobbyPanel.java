package com.chess.gui.ui.lobby;

import com.chess.gui.guiUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// Màn hình Lobby – sidebar trái + vùng nội dung phải (CardLayout)
public class LobbyPanel extends JPanel {

    private final LobbySidebar          sidebar;
    private final ProfilePanel          profilePanel;
    private final FindMatchPanel        findMatchPanel;
    private final OngoingMatchesPanel   ongoingMatchesPanel;
    private final CardLayout            cardLayout;
    private final JPanel                contentArea;
    private final LeaderboardPanel leaderboardPanel;
    public LobbyPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(guiUtils.FRAME_WIDTH, guiUtils.FRAME_HEIGHT));

        // ── Khởi tạo 4 panel nội dung ─────────────────────────
        profilePanel        = new ProfilePanel();
        findMatchPanel      = new FindMatchPanel();
        ongoingMatchesPanel = new OngoingMatchesPanel();
        leaderboardPanel = new LeaderboardPanel();

        // ── Vùng nội dung (CardLayout) ─────────────────────────
        cardLayout  = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setPreferredSize(
            new Dimension(guiUtils.CONTENT_WIDTH, guiUtils.FRAME_HEIGHT));
        contentArea.add(profilePanel,        LobbySidebar.CARD_PROFILE);
        contentArea.add(findMatchPanel,      LobbySidebar.CARD_FIND);
        contentArea.add(ongoingMatchesPanel, LobbySidebar.CARD_WATCH);
        contentArea.add(leaderboardPanel, LobbySidebar.CARD_LEADERBOARD);

        // ── Sidebar ────────────────────────────────────────────
        sidebar = new LobbySidebar();
        sidebar.addNavListener(this::onNavSwitch);

        add(sidebar,     BorderLayout.WEST);
        add(contentArea, BorderLayout.CENTER);
    }

    // Chuyển panel khi bấm nút sidebar
    private void onNavSwitch(ActionEvent e) {
        cardLayout.show(contentArea, e.getActionCommand());
    }

    // ── Getters để GameFrame gắn dữ liệu thật ─────────────────
    public ProfilePanel        getProfilePanel()         { return profilePanel; }
    public FindMatchPanel      getFindMatchPanel()        { return findMatchPanel; }
    public OngoingMatchesPanel getOngoingMatchesPanel()  { return ongoingMatchesPanel; }
    public LobbySidebar        getSidebar()              { return sidebar; }
    public LeaderboardPanel getLeaderboarPanel () {return this.leaderboardPanel; }

    // ── Preview standalone ────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Chess Online – Lobby");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setContentPane(new LobbyPanel());
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
