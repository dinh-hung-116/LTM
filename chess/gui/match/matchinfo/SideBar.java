package chess.gui.match.matchinfo;


import chess.gui.guiUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;



// lớp này chứa các nước đi đã thực hiện, chat, nút đầu hàng + xin hòa
public class SideBar extends JPanel {

    // ===== Move History =====
    private static final Color MOVE_BG_ODD  = new Color(38, 36, 33);
    private static final Color MOVE_BG_EVEN = new Color(45, 43, 40);
    private static final Color MOVE_TEXT    = new Color(220, 220, 220);
    private static final Color MOVE_NUM_COLOR = new Color(130, 130, 130);
    private static final Font  MOVE_FONT    = new Font("Segoe UI", Font.PLAIN, 13);

    //===== Move list =====
    private JPanel moveListPanel;
    private JScrollPane moveScrollPane;
    private int halfMoveCount = 0;
    private JLabel pendingBlackLabel = null;
    //===================

    // ===== Buttons =====
    private JButton resignBtn;
    private JButton drawBtn;
    //==================
    
    //===== Chatbox =====
    private JTextArea chatArea;
    private JTextField chatInput;
    private JButton sendBtn;
    //==================

    // gbc
    private GridBagConstraints gbc;

    public SideBar() {
        this.setPreferredSize(guiUtils.MATCHINFO_FRAME_DIMENSION);
        this.setLayout(new GridBagLayout());
        this.setBackground(new Color(38, 36, 33));
        this.setVisible(true);

        this.gbc = new GridBagConstraints();

        initMoveHistory();
        initButton();
        initChat();
    }

    // ===========================
    
    //===== MOVE HISTORY PANEL =====
    private void initMoveHistory() {
        // Panel chứa tiêu đề cột
        JPanel headerPanel = new JPanel(new GridLayout(1, 3, 0, 0));
        headerPanel.setBackground(new Color(30, 28, 26));
        headerPanel.setPreferredSize(new Dimension(guiUtils.MATCH_INFO_FRAME_WIDTH, 28));
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel hNum   = makeLabel("#",    MOVE_NUM_COLOR, SwingConstants.CENTER, new Font("Segoe UI", Font.BOLD, 12));
        JLabel hWhite = makeLabel("Trắng", new Color(240, 240, 240), SwingConstants.CENTER, new Font("Segoe UI", Font.BOLD, 12));
        JLabel hBlack = makeLabel("Đen",  new Color(180, 180, 180), SwingConstants.CENTER, new Font("Segoe UI", Font.BOLD, 12));
        headerPanel.add(hNum);
        headerPanel.add(hWhite);
        headerPanel.add(hBlack);

        // Panel danh sách nước đi
        moveListPanel = new JPanel();
        moveListPanel.setLayout(new BoxLayout(moveListPanel, BoxLayout.Y_AXIS));
        moveListPanel.setBackground(MOVE_BG_ODD);

        moveScrollPane = new JScrollPane(moveListPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        moveScrollPane.setBorder(BorderFactory.createEmptyBorder());
        moveScrollPane.getVerticalScrollBar().setUnitIncrement(12);
        moveScrollPane.setBackground(MOVE_BG_ODD);

        // Wrapper gộp header + scroll
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(30, 28, 26));
        wrapper.setBorder(BorderFactory.createLineBorder(new Color(60, 58, 55), 1));

        GridBagConstraints wgbc = new GridBagConstraints();
        wgbc.gridx = 0; 
        wgbc.gridy = 0;
        
        wgbc.weightx = 1; 
        wgbc.weighty = 0;
        wgbc.fill = GridBagConstraints.HORIZONTAL;
        wrapper.add(headerPanel, wgbc);

        wgbc.gridy = 1;
        wgbc.weighty = 1;
        wgbc.fill = GridBagConstraints.BOTH;
        wrapper.add(moveScrollPane, wgbc);

        // Thêm wrapper vào SideBar (chiếm ~60% chiều cao)
        gbc.gridx = 0; 
        gbc.gridy = 0;
        
        gbc.weightx = 1; 
        gbc.weighty = 0.6;
        
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(8, 8, 4, 8);
        this.add(wrapper, gbc);
    }

    // Gọi từ bên ngoài sau mỗi nước đi hợp lệ
    public void addMove(String notation) {
        SwingUtilities.invokeLater(() -> {
            halfMoveCount++;
            if (halfMoveCount % 2 == 1) {
                // Nước của trắng -> tạo hàng mới
                int moveNum = (halfMoveCount + 1) / 2;
                Color rowBg = (moveNum % 2 == 0) ? MOVE_BG_EVEN : MOVE_BG_ODD;

                JPanel row = new JPanel(new GridLayout(1, 3, 0, 0));
                row.setBackground(rowBg);
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
                row.setPreferredSize(new Dimension(guiUtils.MATCH_INFO_FRAME_WIDTH, 28));

                JLabel numLabel   = makeLabel(moveNum + ".", MOVE_NUM_COLOR, SwingConstants.CENTER, MOVE_FONT);
                JLabel whiteLabel = makeLabel(notation,      new Color(255, 255, 255), SwingConstants.CENTER, MOVE_FONT);
                JLabel blackLabel = makeLabel("",            MOVE_TEXT, SwingConstants.CENTER, MOVE_FONT);

                numLabel.setBackground(rowBg);   numLabel.setOpaque(true);
                whiteLabel.setBackground(new Color(60, 95, 60)); whiteLabel.setOpaque(true);
                blackLabel.setBackground(rowBg); blackLabel.setOpaque(true);

                row.add(numLabel);
                row.add(whiteLabel);
                row.add(blackLabel);

                moveListPanel.add(row);
                pendingBlackLabel = blackLabel;
            } else {
                // Nước của đen -> điền vào ô đen của hàng hiện tại
                if (pendingBlackLabel != null) {
                    int moveNum = halfMoveCount / 2;
                    Color rowBg = (moveNum % 2 == 0) ? MOVE_BG_EVEN : MOVE_BG_ODD;
                    pendingBlackLabel.setText(notation);
                    pendingBlackLabel.setBackground(new Color(60, 60, 95));
                    pendingBlackLabel = null;
                }
            }
            moveListPanel.revalidate();
            moveListPanel.repaint();
            // Auto-scroll xuống cuối
            SwingUtilities.invokeLater(() -> {
                int max = moveScrollPane.getVerticalScrollBar().getMaximum();
                moveScrollPane.getVerticalScrollBar().setValue(max);
            });
        });
    }
    // ===========================
    
    //===== BUTTONS =====
    private void initButton() {
        resignBtn = new JButton();
        drawBtn   = new JButton();
        styleButton(resignBtn);
        styleButton(drawBtn);

        int size = 40;
        resignBtn.setIcon(loadIcon("/chess/gui/resources/flag.png", size, size));
        drawBtn.setIcon(loadIcon("/chess/gui/resources/handshake.png", size, size));

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setPreferredSize(guiUtils.BUTTON_FRAME_DIMENSION);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btnPanel.setOpaque(false);
        btnPanel.add(resignBtn);
        btnPanel.add(drawBtn);

        gbc.gridx = 0; 
        gbc.gridy = 1;
        
        gbc.weightx = 1; 
        gbc.weighty = 0;
        
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 0, 0);
        this.add(btnPanel, gbc);

        resignBtn.addActionListener(e -> onResign());
        drawBtn.addActionListener(e -> onDraw());
    }

    private void onResign() {
        System.out.println("Resign button clicked");
    }

    private void onDraw() {
        System.out.println("Draw button clicked");
    }

    //========================
    
    //===== Chatbox =====
    private void initChat() {
        // ==============================
        // PANEL CHÍNH CHỨA TOÀN BỘ CHAT
        // ==============================
        JPanel chatPanel = new JPanel(new GridBagLayout());
        chatPanel.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();

        // ==============================
        // KHU VỰC HIỂN THỊ TIN NHẮN
        // ==============================
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setBackground(new Color(49, 46, 43));
        chatArea.setForeground(Color.WHITE);
        chatArea.setCaretColor(Color.WHITE);
        chatArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScroll.setBorder(BorderFactory.createLineBorder(new Color(60, 63, 65)));

        // ==============================
        // KHU VỰC NHẬP TIN NHẮN
        // ==============================
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setOpaque(false);

        chatInput = new JTextField();
        chatInput.setBackground(new Color(60, 63, 65));
        chatInput.setForeground(Color.WHITE);
        chatInput.setCaretColor(Color.WHITE);
        chatInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        sendBtn = new JButton("Gửi");
        sendBtn.setBackground(new Color(118, 150, 86));
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setFocusPainted(false);
        sendBtn.setBorderPainted(false);

        inputPanel.add(chatInput, BorderLayout.CENTER);
        inputPanel.add(sendBtn, BorderLayout.EAST);

        // ==============================
        // EVENT GỬI TIN NHẮN
        // ==============================
        sendBtn.addActionListener(e -> onSendMessage());
        chatInput.addActionListener(e -> onSendMessage());

        // ==============================
        // ADD VÀO CHAT PANEL
        // ==============================
        // chat area
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 5);
        chatPanel.add(chatScroll, c);

        // input area
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        chatPanel.add(inputPanel, c);

        // ==============================
        // ADD CHAT PANEL VÀO PANEL CHA
        // ==============================
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 5, 5, 5);

        this.add(chatPanel, gbc);
    }
    
    private void onSendMessage() {
        String message = chatInput.getText().trim();

        if (message.isEmpty()) {
            return;
        }

        appendMessage("Bạn", message);
        chatInput.setText("");
    }

    private void appendMessage(String sender, String msg) {
        chatArea.append(sender + ": " + msg + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
    //==================
    
    //===== GAME OVER =====
    // Hiển thị thông báo kết thúc ván ngay trong bảng nước đi
    public void showGameOver(String message) {
        SwingUtilities.invokeLater(() -> {
            // Tạo panel thông báo
            JPanel resultPanel = new JPanel(new BorderLayout());
            resultPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
            resultPanel.setPreferredSize(new Dimension(guiUtils.MATCH_INFO_FRAME_WIDTH, 48));
            resultPanel.setBackground(new Color(80, 120, 80));
            resultPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(2, 0, 2, 0, new Color(100, 160, 100)),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)
            ));

            JLabel resultLabel = new JLabel(message, SwingConstants.CENTER);
            resultLabel.setForeground(Color.WHITE);
            resultLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
            resultPanel.add(resultLabel, BorderLayout.CENTER);

            moveListPanel.add(resultPanel);
            moveListPanel.revalidate();
            moveListPanel.repaint();

            // Scroll xuống để thấy thông báo
            SwingUtilities.invokeLater(() -> {
                int max = moveScrollPane.getVerticalScrollBar().getMaximum();
                moveScrollPane.getVerticalScrollBar().setValue(max);
            });

            // Hiện thêm dialog popup
            javax.swing.JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    message,
                    "Kết thúc ván đấu",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE
            );
        });
    }
    //=====================
    
    //===== HELPERS =====
    private JLabel makeLabel(String text, Color fg, int align, Font font) {
        JLabel lbl = new JLabel(text, align);
        lbl.setForeground(fg);
        lbl.setFont(font);
        return lbl;
    }

    private void styleButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBackground(new Color(60, 63, 65));
        btn.setForeground(Color.WHITE);
        btn.setFont(guiUtils.MATCH_BUTTON_FONT);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
    }

    private ImageIcon loadIcon(String path, int w, int h) {
        ImageIcon icon = new ImageIcon(getClass().getResource(path));
        Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
    //===================
}

