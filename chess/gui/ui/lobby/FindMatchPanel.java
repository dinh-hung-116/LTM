package chess.gui.ui.lobby;

import chess.gui.guiUtils;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

// Panel Tìm Trận – hiển thị trong vùng nội dung bên phải
public class FindMatchPanel extends JPanel {

    private JButton findBtn;

    public FindMatchPanel() {
        setLayout(new BorderLayout());
        setBackground(guiUtils.LOBBY_CONTENT_BG);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(),   BorderLayout.CENTER);
    }

    // ── Tiêu đề trang ─────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 18));
        p.setBackground(guiUtils.LOBBY_CONTENT_BG);
        JLabel title = new JLabel("Ghép Trận");
        title.setFont(guiUtils.LOBBY_FONT_TITLE);
        title.setForeground(guiUtils.LOBBY_TEXT);
        p.add(title);
        return p;
    }

    // ── Nội dung ──────────────────────────────────────────────
    private JPanel buildBody() {
        JPanel wrapper = new JPanel(new GridBagLayout()); // để căn giữa
        wrapper.setBackground(guiUtils.LOBBY_CONTENT_BG);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(guiUtils.LOBBY_CARD);
        card.setBorder(new CompoundBorder(
            new LineBorder(guiUtils.LOBBY_BORDER, 1),
            new EmptyBorder(40, 48, 40, 48)
        ));

        // Biểu tượng quân cờ
        JLabel icon = new JLabel("♟");
        icon.setFont(new Font("Serif", Font.PLAIN, 90));
        icon.setForeground(new Color(60, 57, 54));
        icon.setAlignmentX(CENTER_ALIGNMENT);

        // Tiêu đề
        JLabel heading = new JLabel("Tìm Đối Thủ");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(guiUtils.LOBBY_TEXT);
        heading.setAlignmentX(CENTER_ALIGNMENT);

        // Mô tả
        JLabel desc = new JLabel(
            "<html><center>Hệ thống sẽ tìm đối thủ tương đương ELO<br>và ghép vào ván đấu tự động.</center></html>");
        desc.setFont(guiUtils.LOBBY_FONT_BODY);
        desc.setForeground(guiUtils.LOBBY_SUBTEXT);
        desc.setHorizontalAlignment(SwingConstants.CENTER);
        desc.setAlignmentX(CENTER_ALIGNMENT);

        // Khung thông tin thời gian
        JPanel timeBox = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 10));
        timeBox.setBackground(guiUtils.LOBBY_CONTENT_BG);
        timeBox.setBorder(new CompoundBorder(
            new LineBorder(guiUtils.LOBBY_BORDER, 1),
            new EmptyBorder(2, 0, 2, 0)
        ));
        timeBox.setMaximumSize(new Dimension(300, 44));
        timeBox.setAlignmentX(CENTER_ALIGNMENT);

        JLabel timeIcon = new JLabel("⏱");
        timeIcon.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        timeIcon.setForeground(guiUtils.LOBBY_SUBTEXT);

        JLabel timeText = new JLabel("10 phút / ván   ·   30 giây / nước");
        timeText.setFont(guiUtils.LOBBY_FONT_BODY);
        timeText.setForeground(guiUtils.LOBBY_SUBTEXT);

        timeBox.add(timeIcon);
        timeBox.add(timeText);

        // Nút TÌM TRẬN
        findBtn = new JButton("TÌM TRẬN");
        findBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        findBtn.setBackground(guiUtils.LOBBY_GREEN);
        findBtn.setForeground(Color.WHITE);
        findBtn.setBorderPainted(false);
        findBtn.setFocusPainted(false);
        findBtn.setOpaque(true);
        findBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        findBtn.setAlignmentX(CENTER_ALIGNMENT);
        findBtn.setPreferredSize(new Dimension(220, 52));
        findBtn.setMaximumSize(new Dimension(220, 52));

        // Hover effect
        findBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (findBtn.isEnabled()) findBtn.setBackground(guiUtils.LOBBY_GREEN_DARK);
            }
            @Override public void mouseExited(MouseEvent e) {
                if (findBtn.isEnabled()) findBtn.setBackground(guiUtils.LOBBY_GREEN);
            }
        });

        card.add(icon);
        card.add(Box.createVerticalStrut(16));
        card.add(heading);
        card.add(Box.createVerticalStrut(10));
        card.add(desc);
        card.add(Box.createVerticalStrut(24));
        card.add(timeBox);
        card.add(Box.createVerticalStrut(32));
        card.add(findBtn);

        wrapper.add(card);
        return wrapper;
    }

    // ── Public methods ────────────────────────────────────────
    public void addFindMatchListener(ActionListener listener) {
        findBtn.addActionListener(listener);
    }

    public void setFinding(boolean finding) {
        if (finding) {
            findBtn.setText("⏳  ĐANG TÌM...");
            findBtn.setBackground(new Color(100, 150, 48));
            findBtn.setEnabled(false);
        } else {
            findBtn.setText("TÌM TRẬN");
            findBtn.setBackground(guiUtils.LOBBY_GREEN);
            findBtn.setEnabled(true);
        }
    }

    // ── Preview standalone ────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("FindMatchPanel – Preview");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setContentPane(new FindMatchPanel());
            f.setSize(guiUtils.CONTENT_WIDTH, guiUtils.FRAME_HEIGHT);
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
