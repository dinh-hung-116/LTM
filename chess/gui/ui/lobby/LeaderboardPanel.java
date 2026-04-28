package com.chess.gui.ui.lobby;

import com.chess.gui.guiUtils;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

// Panel Bảng Xếp Hạng – danh sách người chơi sắp theo ELO
public class LeaderboardPanel extends JPanel {

    private JPanel tableBody;

    private static final Color GOLD   = new Color(212, 175,  55);
    private static final Color SILVER = new Color(168, 168, 168);
    private static final Color BRONZE = new Color(176, 141,  87);

    // Dữ liệu giả: { tên, tổng trận, thắng, thua, hòa, elo }
    private static final Object[][] FAKE_DATA = {
        {"Nguyễn Văn An",  143, 130, 12,  1, 1850},
        {"Trần Minh Tú",   101,  94,  7,  0, 1720},
        {"Lê Quốc Hùng",   108,  90, 17,  1, 1680},
        {"Phạm Thị Lan",    98,  81, 17,  0, 1640},
        {"Hoàng Đức Mạnh",  88,  76, 11,  1, 1610},
        {"Vũ Thành Đạt",    65,  54,  8,  3, 1580},
        {"Đặng Thu Hà",     46,  43,  3,  0, 1550},
        {"Bùi Văn Tuấn",    72,  58, 12,  2, 1520},
    };

    public LeaderboardPanel() {
        setLayout(new BorderLayout());
        setBackground(guiUtils.LOBBY_CONTENT_BG);
        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(),  BorderLayout.CENTER);
        setLeaderboard(FAKE_DATA);
    }

    // ── Tiêu đề trang ─────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(guiUtils.LOBBY_CONTENT_BG);
        p.setBorder(new EmptyBorder(18, 24, 10, 24));

        JLabel title = new JLabel("Bảng Xếp Hạng");
        title.setFont(guiUtils.LOBBY_FONT_TITLE);
        title.setForeground(guiUtils.LOBBY_TEXT);

        JLabel sub = new JLabel("Xếp theo điểm ELO");
        sub.setFont(guiUtils.LOBBY_FONT_SMALL);
        sub.setForeground(guiUtils.LOBBY_SUBTEXT);

        p.add(title, BorderLayout.WEST);
        p.add(sub,   BorderLayout.EAST);
        return p;
    }

    // ── Bảng ──────────────────────────────────────────────────
    private JPanel buildTable() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 0));
        wrapper.setBackground(guiUtils.LOBBY_CONTENT_BG);
        wrapper.setBorder(new EmptyBorder(0, 24, 24, 24));

        // Header cột
        wrapper.add(buildColHeader(), BorderLayout.NORTH);

        // Thân – scroll
        tableBody = new JPanel();
        tableBody.setLayout(new BoxLayout(tableBody, BoxLayout.Y_AXIS));
        tableBody.setBackground(guiUtils.LOBBY_CONTENT_BG);

        JScrollPane scroll = new JScrollPane(tableBody);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(guiUtils.LOBBY_CONTENT_BG);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        wrapper.add(scroll, BorderLayout.CENTER);

        return wrapper;
    }

    // Dòng tiêu đề cột
    private JPanel buildColHeader() {
        JPanel row = new JPanel(new GridLayout(1, 7, 0, 0));
        row.setBackground(new Color(190, 188, 185));
        row.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 2, 0, new Color(155, 153, 150)),
            new EmptyBorder(8, 0, 8, 0)
        ));

        String[] cols   = {"HẠNG", "TÊN NGƯỜI CHƠI", "TRẬN", "THẮNG", "THUA", "HÒA", "ELO"};
        boolean[] left  = {false, true, false, false, false, false, false};

        for (int i = 0; i < cols.length; i++) {
            JLabel l = new JLabel(cols[i]);
            l.setFont(new Font("Segoe UI", Font.BOLD, 11));
            l.setForeground(new Color(70, 68, 65));
            l.setHorizontalAlignment(left[i] ? SwingConstants.LEFT : SwingConstants.CENTER);
            l.setBorder(new EmptyBorder(0, left[i] ? 14 : 0, 0, 0));
            row.add(l);
        }
        return row;
    }

    // ── Public setter ─────────────────────────────────────────
    // data[i] = { name(String), battles, wins, losses, draws, elo }
    public void setLeaderboard(Object[][] data) {
        tableBody.removeAll();
        for (int i = 0; i < data.length; i++) {
            tableBody.add(buildRow(
                i + 1,
                (String) data[i][0],
                (int) data[i][1],
                (int) data[i][2],
                (int) data[i][3],
                (int) data[i][4],
                (int) data[i][5]
            ));
            tableBody.add(Box.createVerticalStrut(4));
        }
        tableBody.revalidate();
        tableBody.repaint();
    }

    // ── Một dòng ──────────────────────────────────────────────
    private JPanel buildRow(int rank, String name,
                            int battles, int wins, int losses, int draws, int elo) {
        Color bg = rowBg(rank);

        JPanel row = new JPanel(new GridLayout(1, 7, 0, 0));
        row.setBackground(bg);
        row.setBorder(new CompoundBorder(
            new LineBorder(guiUtils.LOBBY_BORDER, 1),
            new EmptyBorder(10, 0, 10, 0)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        row.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        // Cột HẠNG
        JPanel rankCell = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        rankCell.setBackground(bg);
        JLabel dot = new JLabel("●");
        dot.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        dot.setForeground(medalColor(rank));
        JLabel rankLbl = new JLabel(String.valueOf(rank));
        rankLbl.setFont(rank <= 3
            ? new Font("Segoe UI", Font.BOLD, 15)
            : guiUtils.LOBBY_FONT_BOLD);
        rankLbl.setForeground(medalColor(rank));
        rankCell.add(dot);
        rankCell.add(rankLbl);
        row.add(rankCell);

        // Cột TÊN
        JLabel nameLbl = new JLabel("  " + name);
        nameLbl.setFont(guiUtils.LOBBY_FONT_BOLD);
        nameLbl.setForeground(guiUtils.LOBBY_TEXT);
        nameLbl.setBackground(bg);
        nameLbl.setOpaque(true);
        row.add(nameLbl);

        // Các cột số
        row.add(numCell(battles, bg, guiUtils.LOBBY_TEXT));
        row.add(numCell(wins,    bg, new Color(55, 145, 55)));
        row.add(numCell(losses,  bg, new Color(185, 65, 65)));
        row.add(numCell(draws,   bg, new Color(75, 105, 190)));
        row.add(numCell(elo,     bg, guiUtils.LOBBY_GREEN));

        // Hover
        Color hoverBg = bg.darker();
        row.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                setRowBg(row, rankCell, hoverBg);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                setRowBg(row, rankCell, bg);
            }
        });

        return row;
    }

    private void setRowBg(JPanel row, JPanel rankCell, Color color) {
        row.setBackground(color);
        rankCell.setBackground(color);
        for (Component c : row.getComponents()) {
            if (c instanceof JLabel l) { l.setBackground(color); }
        }
    }

    private JLabel numCell(int value, Color bg, Color fg) {
        JLabel l = new JLabel(String.valueOf(value), SwingConstants.CENTER);
        l.setFont(guiUtils.LOBBY_FONT_BOLD);
        l.setForeground(fg);
        l.setBackground(bg);
        l.setOpaque(true);
        return l;
    }

    private Color rowBg(int rank) {
        return switch (rank) {
            case 1  -> new Color(255, 252, 225);
            case 2  -> new Color(246, 246, 250);
            case 3  -> new Color(251, 244, 235);
            default -> rank % 2 == 0 ? guiUtils.LOBBY_CARD : new Color(248, 248, 248);
        };
    }

    private Color medalColor(int rank) {
        return switch (rank) {
            case 1  -> GOLD;
            case 2  -> SILVER;
            case 3  -> BRONZE;
            default -> guiUtils.LOBBY_TEXT;
        };
    }

    // ── Preview standalone ────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("LeaderboardPanel – Preview");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setContentPane(new LeaderboardPanel());
            f.setSize(guiUtils.CONTENT_WIDTH, guiUtils.FRAME_HEIGHT);
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}