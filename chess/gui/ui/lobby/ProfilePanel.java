package com.chess.gui.ui.lobby;

import com.chess.gui.guiUtils;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

// Panel Hồ Sơ – hiển thị trong vùng nội dung bên phải
public class ProfilePanel extends JPanel {

    private JLabel nameLabel;
    private JLabel usernameLabel;
    private JLabel[] statValues;   // index 0-3: totalGames, wins, losses, draws
    private JLabel eloValueLabel;
    private JLabel winRateLabel;

    public ProfilePanel() {
        setLayout(new BorderLayout());
        setBackground(guiUtils.LOBBY_CONTENT_BG);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(),   BorderLayout.CENTER);
    }

    // ── Tiêu đề trang ─────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 18));
        p.setBackground(guiUtils.LOBBY_CONTENT_BG);

        JLabel title = new JLabel("Hồ Sơ Người Dùng");
        title.setFont(guiUtils.LOBBY_FONT_TITLE);
        title.setForeground(guiUtils.LOBBY_TEXT);
        p.add(title);
        return p;
    }

    // ── Nội dung ──────────────────────────────────────────────
    private JPanel buildBody() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(guiUtils.LOBBY_CONTENT_BG);
        p.setBorder(new EmptyBorder(0, 24, 24, 24));

        // ─── Card thông tin user ────────────────────────────
        JPanel userCard = card();
        userCard.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 16));

        // Avatar
        JLabel avatar = new JLabel();
        avatar.setPreferredSize(new Dimension(72, 72));
        try {
            ImageIcon raw = new ImageIcon(
                getClass().getResource("/chess/gui/resources/user-image.png"));
            Image scaled = raw.getImage().getScaledInstance(72, 72, Image.SCALE_SMOOTH);
            avatar.setIcon(new ImageIcon(scaled));
        } catch (Exception ex) {
            avatar.setText("♝");
            avatar.setFont(new Font("Serif", Font.PLAIN, 60));
            avatar.setForeground(new Color(160, 140, 100));
            avatar.setHorizontalAlignment(SwingConstants.CENTER);
        }

        // Thông tin
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(guiUtils.LOBBY_CARD);

        nameLabel = new JLabel("Nguyễn Văn A");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(guiUtils.LOBBY_TEXT);

        usernameLabel = new JLabel("@player123");
        usernameLabel.setFont(guiUtils.LOBBY_FONT_BODY);
        usernameLabel.setForeground(guiUtils.LOBBY_GREEN);

        JLabel memberLabel = new JLabel("Thành viên từ 2024");
        memberLabel.setFont(guiUtils.LOBBY_FONT_SMALL);
        memberLabel.setForeground(guiUtils.LOBBY_SUBTEXT);

        info.add(Box.createVerticalStrut(8));
        info.add(nameLabel);
        info.add(Box.createVerticalStrut(4));
        info.add(usernameLabel);
        info.add(Box.createVerticalStrut(4));
        info.add(memberLabel);

        userCard.add(avatar);
        userCard.add(info);
        userCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        // ─── Card thống kê ─────────────────────────────────
        JPanel statsCard = card();
        statsCard.setLayout(new BorderLayout(0, 12));
        statsCard.setBorder(new CompoundBorder(
            new LineBorder(guiUtils.LOBBY_BORDER, 1),
            new EmptyBorder(16, 20, 16, 20)
        ));

        JLabel statsTitle = new JLabel("Thống Kê");
        statsTitle.setFont(guiUtils.LOBBY_FONT_H2);
        statsTitle.setForeground(guiUtils.LOBBY_TEXT);

        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 12, 0));
        statsGrid.setBackground(guiUtils.LOBBY_CARD);

        String[] labels   = {"Tổng Trận", "Thắng", "Thua", "Hòa"};
        String[] defaults = {"42",        "25",    "12",   "5"};
        Color[]  colors   = {
            guiUtils.LOBBY_TEXT,
            new Color(74, 161, 74),   // xanh lá
            new Color(195, 80, 80),   // đỏ
            new Color(100, 120, 200)  // xanh dương
        };
        statValues = new JLabel[4]; // totalGames, wins, losses, draws

        for (int i = 0; i < 4; i++) {
            JPanel cell = new JPanel();
            cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
            cell.setBackground(guiUtils.LOBBY_CARD);
            cell.setBorder(new CompoundBorder(
                new LineBorder(guiUtils.LOBBY_BORDER, 1),
                new EmptyBorder(12, 0, 12, 0)
            ));

            statValues[i] = new JLabel(defaults[i]);
            statValues[i].setFont(new Font("Segoe UI", Font.BOLD, 26));
            statValues[i].setForeground(colors[i]);
            statValues[i].setAlignmentX(CENTER_ALIGNMENT);

            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(guiUtils.LOBBY_FONT_SMALL);
            lbl.setForeground(guiUtils.LOBBY_SUBTEXT);
            lbl.setAlignmentX(CENTER_ALIGNMENT);

            cell.add(statValues[i]);
            cell.add(Box.createVerticalStrut(4));
            cell.add(lbl);
            statsGrid.add(cell);
        }

        statsCard.add(statsTitle,  BorderLayout.NORTH);
        statsCard.add(statsGrid,   BorderLayout.CENTER);

        // ─── Card ELO + Tỉ lệ thắng ───────────────────────
        JPanel extraCard = card();
        extraCard.setLayout(new GridLayout(1, 2, 12, 0));

        eloValueLabel = new JLabel("1,250");
        winRateLabel  = new JLabel("59.5%");
        JPanel eloCell = miniStatCell("ELO",          eloValueLabel, guiUtils.LOBBY_GREEN);
        JPanel winCell = miniStatCell("Tỉ Lệ Thắng", winRateLabel,  new Color(74, 161, 74));

        extraCard.add(eloCell);
        extraCard.add(winCell);
        extraCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        // ─── Nút chỉnh sửa ────────────────────────────────
        JButton editBtn = new JButton("  Chỉnh Sửa Thông Tin  ");
        editBtn.setFont(guiUtils.LOBBY_FONT_BOLD);
        editBtn.setBackground(guiUtils.LOBBY_GREEN);
        editBtn.setForeground(Color.WHITE);
        editBtn.setBorderPainted(false);
        editBtn.setFocusPainted(false);
        editBtn.setOpaque(true);
        editBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        editBtn.setAlignmentX(LEFT_ALIGNMENT);
        editBtn.setPreferredSize(new Dimension(200, 38));

        // Mở EditProfileDialog khi bấm
        editBtn.addActionListener(e -> {
            Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
            EditProfileDialog dlg = new EditProfileDialog(owner);
            dlg.populate(
                nameLabel.getText(),
                usernameLabel.getText().replace("@", ""),
                "Nam", ""
            );
            dlg.setVisible(true);

            if (dlg.isSaved()) {
                setDisplayName(dlg.getFullName());
                setUsername(dlg.getUsername());
                // TODO: gọi UserDAO.updateUser() với dữ liệu mới
            }
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnRow.setBackground(guiUtils.LOBBY_CONTENT_BG);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btnRow.add(editBtn);

        p.add(userCard);
        p.add(Box.createVerticalStrut(16));
        p.add(statsCard);
        p.add(Box.createVerticalStrut(16));
        p.add(extraCard);
        p.add(Box.createVerticalStrut(20));
        p.add(btnRow);
        return p;
    }

    // ── Ô thống kê nhỏ (ELO, tỉ lệ) ─────────────────────────
    private JPanel miniStatCell(String label, JLabel valLbl, Color valueColor) {
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valLbl.setForeground(valueColor);
        valLbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel nameLbl = new JLabel(label);
        nameLbl.setFont(guiUtils.LOBBY_FONT_SMALL);
        nameLbl.setForeground(guiUtils.LOBBY_SUBTEXT);
        nameLbl.setAlignmentX(LEFT_ALIGNMENT);

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBackground(guiUtils.LOBBY_CARD);
        inner.add(valLbl);
        inner.add(Box.createVerticalStrut(4));
        inner.add(nameLbl);

        JPanel outer = card();
        outer.setLayout(new BorderLayout());
        outer.setBorder(new CompoundBorder(
            new LineBorder(guiUtils.LOBBY_BORDER, 1),
            new EmptyBorder(12, 16, 12, 16)
        ));
        outer.add(inner, BorderLayout.CENTER);
        return outer;
    }

    // ── Public setters ────────────────────────────────────────
    public void setDisplayName(String fullName)    { nameLabel.setText(fullName); }
    public void setUsername(String username)        { usernameLabel.setText("@" + username); }

    public void setStats(int totalGames, int wins, int losses,
                         int draws, double winRate, int elo) {
        statValues[0].setText(String.valueOf(totalGames));
        statValues[1].setText(String.valueOf(wins));
        statValues[2].setText(String.valueOf(losses));
        statValues[3].setText(String.valueOf(draws));
        eloValueLabel.setText(String.valueOf(elo));
        winRateLabel.setText(String.format("%.1f%%", winRate * 100));
    }

    // ── Helper ───────────────────────────────────────────────
    private JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(guiUtils.LOBBY_CARD);
        p.setBorder(new CompoundBorder(
            new LineBorder(guiUtils.LOBBY_BORDER, 1),
            new EmptyBorder(14, 18, 14, 18)
        ));
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        return p;
    }

    // ── Preview standalone ────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("ProfilePanel – Preview");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setContentPane(new ProfilePanel());
            f.setSize(guiUtils.CONTENT_WIDTH, guiUtils.FRAME_HEIGHT);
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
