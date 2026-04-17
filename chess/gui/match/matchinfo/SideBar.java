package chess.gui.match.matchinfo;


import chess.gui.guiUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


//package chess.gui.match.matchinfo;
//
//
//import chess.gui.guiUtils;
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.GridBagConstraints;
//import java.awt.GridBagLayout;
//import java.awt.GridLayout;
//import java.awt.Image;
//import javax.swing.BorderFactory;
//import javax.swing.ImageIcon;
//import javax.swing.JButton;
//import javax.swing.JPanel;


// lớp này chứa các nước đi đã thực hiện, chat, nút đầu hàng + xin hòa
// hai cái userinfo bar tuy không trực tiếp nằm trong lớp này nhưng vẫn cho vào để dễ
// quản lý do có cung cấp thông tin và thời gian

public class SideBar extends JPanel {
    // panel nước đi
    // nút đầu hàng và xin hòa
    private JButton resignBtn;
    private JButton drawBtn;
    private JTextArea chatArea;
    private JTextField chatInput;
    private JButton sendBtn;
    // panel userinfo
    
    // gbc
    private GridBagConstraints gbc;
    
    public SideBar() {
        this.setPreferredSize(guiUtils.MATCHINFO_FRAME_DIMENSION);
        this.setLayout(new GridBagLayout());
        this.setBackground(new Color(38, 36, 33));
        this.setVisible(true);
        
        this.gbc = new GridBagConstraints();
        this.gbc.insets = new java.awt.Insets(5, 5, 5, 5); // padding giữa các phần
        
        // nút
        initChat();
        initButton();
    }

    private void initChat() {
        // --- Khu vực hiển thị tin nhắn ---
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setBackground(new Color(49, 46, 43));
        chatArea.setForeground(Color.WHITE);
        chatArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScroll.setBorder(BorderFactory.createLineBorder(new Color(60, 63, 65)));
        
        // --- Khu vực chat nhanh
        JPanel quickChatPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        quickChatPanel.setOpaque(false);
        quickChatPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        String[] quickMessages = {"👋 Chào!", "👍 Hay!", "😅 Áp lực!", "🤝 Cảm ơn!"};
        for (String msg : quickMessages) {
            JButton quickBtn = new JButton(msg);
            quickBtn.setBackground(new Color(60, 63, 65));
            quickBtn.setForeground(Color.WHITE);
            quickBtn.setFocusPainted(false);
            quickBtn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY),
                    BorderFactory.createEmptyBorder(3, 5, 3, 5)));
            quickChatPanel.add(quickBtn);
            
            quickBtn.addActionListener(e -> {
                chatInput.setText(msg);
                onSendMessage();
            });
        }
        // --- Khu vực nhập tin nhắn ---
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
        
        // --- Lắng nghe sự kiện gửi tin nhắn ---
        sendBtn.addActionListener(e -> onSendMessage());
        chatInput.addActionListener(e -> onSendMessage());
            
        
        
        // --- Thêm vào SideBar ---
        // Thêm khung hiển thị chat
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0; 
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 5, 5, 5);
        this.add(chatScroll, gbc);

        // Thêm khung chat nhanh
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add(quickChatPanel, gbc);

        
        // Thêm khung nhập liệu chat
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add(inputPanel, gbc);
    }
    
    private void initButton() {
        // khởi tạo nút
        resignBtn = new JButton();
        drawBtn = new JButton();
        // chỉnh ui
        this.styleButton(resignBtn);
        this.styleButton(drawBtn);
        
        // ===== Icons =====
        int size = 40;
        resignBtn.setIcon(loadIcon("/com/chess/gui/resources/flag.png", size, size));
        drawBtn.setIcon(loadIcon("/com/chess/gui/resources/handshake.png", size, size));
        
        // tạo một panel dùng gridlayout để đưa 2 nút vào
        JPanel btnPanel = new JPanel();
        btnPanel.setPreferredSize(guiUtils.BUTTON_FRAME_DIMENSION);
        btnPanel.setLayout(new GridLayout(1, 2));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        // padding giữa hai 2 nút
        ((GridLayout) btnPanel.getLayout()).setHgap(10);
        //btnPanel.setVisible(true);
        btnPanel.setOpaque(false);
        // thêm 2 nút vào panel
        btnPanel.add(this.resignBtn);
        btnPanel.add(this.drawBtn);
        
        // thêm panel vào khung
        this.add(btnPanel);
        
        //===== Listener =====
        resignBtn.addActionListener(e -> onResign());
        drawBtn.addActionListener(e -> onDraw());
        
    }
    
    // hành động khi nút được nhấn
    
    private void onSendMessage() {
        String message = chatInput.getText().trim();
        if (!message.isEmpty()) {
            // Thêm tin nhắn vào khung chat
            chatArea.append("Bạn: " + message + "\n");
            // Cuộn xuống cuối cùng
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
            // Xóa nội dung ô nhập
            chatInput.setText("");
            
            // TODO: Gửi tin nhắn qua Network/Socket tại đây
        }
    }

    private void onResign() {
        System.out.println("Resign button clicked");
    }

    private void onDraw() {
        System.out.println("Draw button clicked");
    }
    
    //==HELPER==
    private void styleButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);

        // chess.com-like colors
        btn.setBackground(new Color(60, 63, 65));   // dark gray
        btn.setForeground(Color.WHITE);

        // font (clean & modern)
        btn.setFont(guiUtils.MATCH_BUTTON_FONT);

        // padding
        btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        // cursor
        //btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    // ================= LOAD ICON =================
//    private ImageIcon loadIcon(String path, int w, int h) {
//        ImageIcon icon = new ImageIcon(getClass().getResource(path));
//        Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
//        return new ImageIcon(img);
//    }


    private ImageIcon loadIcon(String path, int w, int h) {
        URL url = getClass().getResource(path);

        if (url == null) {
            System.out.println("❌ Không tìm thấy ảnh: " + path);
            return null;
        }

        ImageIcon icon = new ImageIcon(url);
        Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}

/*
package chess.gui.Class;

import java.awt.*;
import javax.swing.*;


class FeaturePanel extends JPanel {

    private JButton resignBtn;
    private JButton drawBtn;


    public FeaturePanel() {
        this.setPreferredSize(guiUtils.FEATURE_FRAME_DIMENSION);
        this.setLayout(null);
        this.setBackground(Color.GRAY);

        JPanel actionPanel = createActionPanel(); //panel nut chuc nang

        // Tính vị trí Y
        int panelHeight = (int) guiUtils.FEATURE_FRAME_DIMENSION.getHeight();
        int x = 0;
        int y = panelHeight - 97 - 140;
        actionPanel.setBounds(x, y, 352, 97);

        this.add(actionPanel);
        this.setVisible(true);
    }

    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 28));
        actionPanel.setPreferredSize(new Dimension(352, 97)); // khớp thiết kế
        actionPanel.setBackground(new Color(80, 80, 80));

        resignBtn = createStyledButton("Đầu hàng");
        drawBtn   = createStyledButton( "Xin hòa");

        resignBtn.addActionListener(e -> onResign());
        drawBtn.addActionListener(e -> onDraw());

        actionPanel.add(resignBtn);
        actionPanel.add(drawBtn);

        return actionPanel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(100, 40));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(new Color(50, 50, 50));
        button.setBackground(new Color(217, 217, 217));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        // hover
        Color normal = new Color(217, 217, 217);
        Color hover  = new Color(255, 255, 255);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(hover);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(normal);
            }
        });

        return button;
    }

    private void onResign() {
        // XU LY LOGIC/NETWORK SAU
        System.out.println("Người chơi đã đầu hàng.");
    }

    private void onDraw() {
        // XU LY LOGIC/NETWORK SAU
        System.out.println("Người chơi đã xin hòa.");
    }
}
*/
