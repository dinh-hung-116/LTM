package com.chess.gui.ui.lobby;

import com.chess.gui.guiUtils;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

// Sidebar trái – logo + thông tin user + điều hướng (kiểu chess.com)
public class LobbySidebar extends JPanel {

    public static final String CARD_PROFILE     = "profile";
    public static final String CARD_FIND        = "find";
    public static final String CARD_WATCH       = "watch";
    public static final String CARD_LEADERBOARD = "leaderboard";

    private JLabel nameLabel;
    private JLabel eloLabel;
    private JLabel statusLabel; // hiện trạng thái kết nối server
    private JButton activeBtn;

    // Giữ tham chiếu tới các nút nav để addNavListener() truy cập đúng
    private final List<JButton> navButtons = new ArrayList<>();

    public LobbySidebar() {
        setLayout(new BorderLayout());
        setBackground(guiUtils.LOBBY_SIDEBAR);
        setPreferredSize(new Dimension(guiUtils.SIDEBAR_WIDTH, guiUtils.FRAME_HEIGHT));

        add(buildLogo(),   BorderLayout.NORTH);
        add(buildNav(),    BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);
    }

    // ── Logo ───────────────────────────────────────────────────
    private JPanel buildLogo() {
        JPanel inner = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 16));
        inner.setBackground(guiUtils.LOBBY_SIDEBAR);

        JLabel logo = new JLabel("♛  Chess Online");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        logo.setForeground(guiUtils.LOBBY_SIDEBAR_TEXT);
        inner.add(logo);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(guiUtils.LOBBY_SIDEBAR);
        wrapper.add(inner, BorderLayout.CENTER);
        wrapper.add(hLine(), BorderLayout.SOUTH);
        return wrapper;
    }

    // ── Navigation ─────────────────────────────────────────────
    private JPanel buildNav() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(guiUtils.LOBBY_SIDEBAR);
        p.setBorder(new EmptyBorder(10, 0, 0, 0));

        // User info gọn
        p.add(buildUserCompact());
        p.add(Box.createVerticalStrut(14));
        p.add(hLine());
        p.add(Box.createVerticalStrut(6));

        // 4 nút điều hướng
        JButton btnProfile     = navBtn("Hồ Sơ",        CARD_PROFILE);
        JButton btnFind        = navBtn("Tìm Trận",      CARD_FIND);
        JButton btnWatch       = navBtn("Xem Ván Đấu",   CARD_WATCH);
        JButton btnLeaderboard = navBtn("Xếp Hạng",      CARD_LEADERBOARD);

        navButtons.add(btnProfile);
        navButtons.add(btnFind);
        navButtons.add(btnWatch);
        navButtons.add(btnLeaderboard);

        // Mặc định chọn Hồ Sơ
        selectBtn(btnProfile);
        activeBtn = btnProfile;

        p.add(btnProfile);
        p.add(btnFind);
        p.add(btnWatch);
        p.add(btnLeaderboard);
        p.add(Box.createVerticalGlue());
        return p;
    }

    // Thông tin user thu gọn
    private JPanel buildUserCompact() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        p.setBackground(guiUtils.LOBBY_SIDEBAR);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel avatar = new JLabel();
        try {
            ImageIcon raw = new ImageIcon(
                    getClass().getResource("/chess/gui/resources/user-image.png"));
            Image scaled = raw.getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH);
            avatar.setIcon(new ImageIcon(scaled));
        } catch (Exception ex) {
            avatar.setText("♝");
            avatar.setFont(new Font("Serif", Font.PLAIN, 26));
            avatar.setForeground(new Color(180, 160, 120));
        }

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(guiUtils.LOBBY_SIDEBAR);

        nameLabel = new JLabel("Người Chơi");
        nameLabel.setFont(guiUtils.LOBBY_FONT_BOLD);
        nameLabel.setForeground(guiUtils.LOBBY_SIDEBAR_TEXT);

        eloLabel = new JLabel("ELO: –");
        eloLabel.setFont(guiUtils.LOBBY_FONT_SMALL);
        eloLabel.setForeground(guiUtils.LOBBY_GREEN);

        statusLabel = new JLabel("● Đang kết nối...");
        statusLabel.setFont(guiUtils.LOBBY_FONT_SMALL);
        statusLabel.setForeground(new java.awt.Color(200, 150, 50)); // màu vàng = đang chờ

        info.add(nameLabel);
        info.add(eloLabel);
        info.add(statusLabel);

        p.add(avatar);
        p.add(info);
        return p;
    }

    // ── Nút điều hướng ─────────────────────────────────────────
    private JButton navBtn(String text, String cardName) {
        JButton b = new JButton(text);
        b.setFont(guiUtils.LOBBY_FONT_BODY);
        b.setForeground(guiUtils.LOBBY_SIDEBAR_TEXT);
        b.setBackground(guiUtils.LOBBY_SIDEBAR);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setHorizontalAlignment(SwingConstants.CENTER);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        b.setPreferredSize(new Dimension(guiUtils.SIDEBAR_WIDTH, 44));
        b.setAlignmentX(CENTER_ALIGNMENT);
        b.setBorder(new EmptyBorder(0, 0, 0, 0));
        // đặt property "card" cho button
        b.putClientProperty("card", cardName); //card cho button

        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (b != activeBtn) b.setBackground(guiUtils.LOBBY_SIDEBAR_HOVER);
            }
            @Override public void mouseExited(MouseEvent e) {
                if (b != activeBtn) b.setBackground(guiUtils.LOBBY_SIDEBAR);
            }
        });
        return b;
    }

    private void selectBtn(JButton btn) {
        btn.setBackground(guiUtils.LOBBY_SIDEBAR_HOVER);
        btn.setBorder(new CompoundBorder(
                new MatteBorder(0, 3, 0, 0, guiUtils.LOBBY_GREEN),
                new EmptyBorder(0, 0, 0, 0)
        ));
        btn.setForeground(guiUtils.LOBBY_GREEN);
    }

    private void deselectBtn(JButton btn) {
        btn.setBackground(guiUtils.LOBBY_SIDEBAR);
        btn.setBorder(new EmptyBorder(0, 0, 0, 0));
        btn.setForeground(guiUtils.LOBBY_SIDEBAR_TEXT);
    }

    // ── Gắn listener điều hướng – LobbyPanel gọi hàm này ──────
    public void addNavListener(ActionListener listener) {
        for (JButton btn : navButtons) {
            btn.addActionListener(e -> {
                if (activeBtn != null) deselectBtn(activeBtn);
                selectBtn(btn);
                activeBtn = btn;
                listener.actionPerformed(new ActionEvent(
                        btn, ActionEvent.ACTION_PERFORMED,
                        (String) btn.getClientProperty("card")
                ));
            });
        }
    }

    // ── Setters ───────────────────────────────────────────────
    public void setUsername(String name) { nameLabel.setText(name); }

    /** Cập nhật trạng thái kết nối server hiển thị dưới tên người chơi */
    public void setConnectionStatus(boolean connected) {
        SwingUtilities.invokeLater(() -> {
            if (connected) {
                statusLabel.setText("● Online");
                statusLabel.setForeground(new java.awt.Color(80, 180, 80)); // xanh
            } else {
                statusLabel.setText("● Mất kết nối");
                statusLabel.setForeground(new java.awt.Color(200, 60, 60));  // đỏ
            }
        });
    }
    public void setElo(int elo)          { eloLabel.setText("ELO: " + elo); }

    // ── Setters ───────────────────────────────────────────────
    // Gắn listener đăng xuất từ bên ngoài (GameFrame)
    public void addLogoutListener(ActionListener listener) {
        logoutBtn.addActionListener(listener);
    }

    private final JButton logoutBtn = buildLogoutBtn();

    // ── Bottom ────────────────────────────────────────────────
    private JPanel buildBottom() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(guiUtils.LOBBY_SIDEBAR);
        p.setBorder(new MatteBorder(1, 0, 0, 0, new Color(65, 62, 58)));

        // Nút Đăng Xuất
        logoutBtn.setAlignmentX(CENTER_ALIGNMENT);
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JPanel logoutRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 6));
        logoutRow.setBackground(guiUtils.LOBBY_SIDEBAR);
        logoutRow.add(logoutBtn);

        JPanel verRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 4));
        verRow.setBackground(guiUtils.LOBBY_SIDEBAR);
        JLabel ver = new JLabel("Chess Online v1.0");
        ver.setFont(guiUtils.LOBBY_FONT_SMALL);
        ver.setForeground(guiUtils.LOBBY_SIDEBAR_SUB);
        verRow.add(ver);

        p.add(logoutRow);
        p.add(verRow);
        return p;
    }

    private JButton buildLogoutBtn() {
        JButton b = new JButton("Đăng Xuất");
        b.setFont(guiUtils.LOBBY_FONT_BODY);
        b.setForeground(new Color(220, 100, 90));
        b.setBackground(guiUtils.LOBBY_SIDEBAR);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setHorizontalAlignment(SwingConstants.CENTER);
        b.setPreferredSize(new Dimension(guiUtils.SIDEBAR_WIDTH, 40));
        b.setBorder(new EmptyBorder(0, 0, 0, 0));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(guiUtils.LOBBY_SIDEBAR_HOVER); }
            @Override public void mouseExited(MouseEvent e)  { b.setBackground(guiUtils.LOBBY_SIDEBAR); }
        });
        return b;
    }

    private JSeparator hLine() {
        JSeparator s = new JSeparator();
        s.setForeground(new Color(65, 62, 58));
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return s;
    }
}