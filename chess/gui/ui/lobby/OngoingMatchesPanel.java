package chess.gui.ui.lobby;

import chess.gui.guiUtils;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

// Panel Xem Ván Đấu – danh sách ván đang diễn ra
public class OngoingMatchesPanel extends JPanel {

    private final JPanel listPanel;

    private static final String[][] FAKE_DATA = {
        {"player1",   "player2",   "8:32"},
        {"alice99",   "bobby_f",   "4:15"},
        {"chessking", "queen_z",   "2:50"},
        {"ngoc_anh",  "minhtu",    "9:01"},
        {"hung_pro",  "tuan_dep",  "5:44"},
        {"bao_loc",   "van_thinh", "1:20"},
    };

    public OngoingMatchesPanel() {
        setLayout(new BorderLayout());
        setBackground(guiUtils.LOBBY_CONTENT_BG);

        add(buildHeader(), BorderLayout.NORTH);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(guiUtils.LOBBY_CONTENT_BG);

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(guiUtils.LOBBY_CONTENT_BG);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scroll, BorderLayout.CENTER);

        setMatches(FAKE_DATA);
    }

    // ── Tiêu đề trang ─────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(guiUtils.LOBBY_CONTENT_BG);
        p.setBorder(new EmptyBorder(18, 24, 10, 24));

        JLabel title = new JLabel("Ván Đấu Đang Diễn Ra");
        title.setFont(guiUtils.LOBBY_FONT_TITLE);
        title.setForeground(guiUtils.LOBBY_TEXT);

        JLabel note = new JLabel("Nhấn \"Xem\" để theo dõi ván đấu");
        note.setFont(guiUtils.LOBBY_FONT_SMALL);
        note.setForeground(guiUtils.LOBBY_SUBTEXT);

        p.add(title, BorderLayout.WEST);
        p.add(note,  BorderLayout.EAST);
        return p;
    }

    // ── Public setter ─────────────────────────────────────────
    // matches[i] = { tenTrang, tenDen, thoiGianConLai }
    public void setMatches(String[][] matches) {
        listPanel.removeAll();
        listPanel.setBorder(new EmptyBorder(0, 24, 24, 24));

        for (int i = 0; i < matches.length; i++) {
            listPanel.add(buildRow(matches[i][0], matches[i][1], matches[i][2], i + 1));
            listPanel.add(Box.createVerticalStrut(10));
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    // ── Một dòng trong danh sách ──────────────────────────────
    private JPanel buildRow(String white, String black, String time, int index) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(guiUtils.LOBBY_CARD);
        row.setBorder(new CompoundBorder(
            new LineBorder(guiUtils.LOBBY_BORDER, 1),
            new EmptyBorder(14, 18, 14, 18)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Số thứ tự
        JLabel num = new JLabel(String.valueOf(index));
        num.setFont(guiUtils.LOBBY_FONT_BOLD);
        num.setForeground(guiUtils.LOBBY_SUBTEXT);
        num.setPreferredSize(new Dimension(22, 22));
        num.setHorizontalAlignment(SwingConstants.CENTER);

        // Tên hai người chơi
        JPanel players = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        players.setBackground(guiUtils.LOBBY_CARD);

        JLabel wLbl = new JLabel("♔ " + white);
        wLbl.setFont(guiUtils.LOBBY_FONT_BOLD);
        wLbl.setForeground(guiUtils.LOBBY_TEXT);

        JLabel vsLbl = new JLabel("vs");
        vsLbl.setFont(guiUtils.LOBBY_FONT_BODY);
        vsLbl.setForeground(guiUtils.LOBBY_SUBTEXT);

        JLabel bLbl = new JLabel("♚ " + black);
        bLbl.setFont(guiUtils.LOBBY_FONT_BOLD);
        bLbl.setForeground(guiUtils.LOBBY_TEXT);

        players.add(wLbl);
        players.add(vsLbl);
        players.add(bLbl);

        // Thời gian + nút Xem
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setBackground(guiUtils.LOBBY_CARD);

        JLabel timeLbl = new JLabel("⏱  " + time);
        timeLbl.setFont(guiUtils.LOBBY_FONT_BOLD);
        timeLbl.setForeground(guiUtils.LOBBY_GREEN);

        JButton watchBtn = new JButton("Xem");
        watchBtn.setFont(guiUtils.LOBBY_FONT_BOLD);
        watchBtn.setBackground(guiUtils.LOBBY_GREEN);
        watchBtn.setForeground(Color.WHITE);
        watchBtn.setBorderPainted(false);
        watchBtn.setFocusPainted(false);
        watchBtn.setOpaque(true);
        watchBtn.setPreferredSize(new Dimension(70, 30));
        watchBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        watchBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { watchBtn.setBackground(guiUtils.LOBBY_GREEN_DARK); }
            @Override public void mouseExited(MouseEvent e)  { watchBtn.setBackground(guiUtils.LOBBY_GREEN); }
        });

        right.add(timeLbl);
        right.add(watchBtn);

        row.add(num,     BorderLayout.WEST);
        row.add(players, BorderLayout.CENTER);
        row.add(right,   BorderLayout.EAST);

        // Hover highlight toàn hàng
        MouseAdapter hoverEffect = new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                row.setBackground(guiUtils.LOBBY_CARD_HOVER);
                players.setBackground(guiUtils.LOBBY_CARD_HOVER);
                right.setBackground(guiUtils.LOBBY_CARD_HOVER);
            }
            @Override public void mouseExited(MouseEvent e) {
                row.setBackground(guiUtils.LOBBY_CARD);
                players.setBackground(guiUtils.LOBBY_CARD);
                right.setBackground(guiUtils.LOBBY_CARD);
            }
        };
        row.addMouseListener(hoverEffect);

        return row;
    }

    // ── Preview standalone ────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("OngoingMatchesPanel – Preview");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setContentPane(new OngoingMatchesPanel());
            f.setSize(guiUtils.CONTENT_WIDTH, guiUtils.FRAME_HEIGHT);
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
