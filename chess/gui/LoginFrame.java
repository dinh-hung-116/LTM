package chess.gui;

import chess.database.Class.User;
import chess.database.DAO.UserDAO;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField userField;
    private JPasswordField passField;
    private JButton loginBtn;

    public LoginFrame() {
        setTitle("Chess Game - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null); 
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(38, 36, 33)); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tiêu đề
        JLabel titleLabel = new JLabel("ĐĂNG NHẬP", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(118, 150, 86)); 
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Username
        gbc.gridwidth = 1; gbc.gridy = 1;
        JLabel userLabel = new JLabel("Tên đăng nhập:");
        userLabel.setForeground(Color.WHITE);
        mainPanel.add(userLabel, gbc);

        userField = new JTextField(15);
        userField.setBackground(new Color(60, 63, 65));
        userField.setForeground(Color.WHITE);
        userField.setCaretColor(Color.WHITE);
        gbc.gridx = 1;
        mainPanel.add(userField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel passLabel = new JLabel("Mật khẩu:");
        passLabel.setForeground(Color.WHITE);
        mainPanel.add(passLabel, gbc);

        passField = new JPasswordField(15);
        passField.setBackground(new Color(60, 63, 65));
        passField.setForeground(Color.WHITE);
        passField.setCaretColor(Color.WHITE);
        gbc.gridx = 1;
        mainPanel.add(passField, gbc);

        // Nút Đăng nhập
        loginBtn = new JButton("Đăng nhập");
        loginBtn.setBackground(new Color(118, 150, 86));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        mainPanel.add(loginBtn, gbc);

        // Tạo nút Đăng ký 
        JButton registerBtn = new JButton("Chưa có tài khoản? Đăng ký ngay");
        registerBtn.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        registerBtn.setForeground(new Color(118, 150, 86)); 
        registerBtn.setBorderPainted(false);       
        registerBtn.setContentAreaFilled(false);   
        registerBtn.setFocusPainted(false);        
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        mainPanel.add(registerBtn, gbc);

        // Sự kiện khi bấm vào nút Đăng ký
        registerBtn.addActionListener(e -> {
            new RegisterFrame().setVisible(true); 
            this.dispose(); // Đóng form Đăng nhập 
        });

        // Sự kiện đăng nhập
        loginBtn.addActionListener(e -> handleLogin());
        // Cho phép ấn Enter ở ô password để đăng nhập
        passField.addActionListener(e -> handleLogin()); 

        add(mainPanel);
    }

    private void handleLogin() {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Gọi Database kiểm tra
        // Lưu ý: Nếu DB bạn lưu dạng Hash (theo tên biến passwordHash), 
        // bạn sẽ cần băm chuỗi 'password' này trước khi truyền vào hàm nếu hệ thống yêu cầu.
        User loggedInUser = UserDAO.checkLogin(username, password);

        if (loggedInUser != null) {
            JOptionPane.showMessageDialog(this, "Chào mừng " + loggedInUser.getFullName() + "!");
            this.dispose(); // Đóng form login
            
            // Mở GameFrame
            SwingUtilities.invokeLater(() -> {
                new GameFrame(loggedInUser).setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!", "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
        }
    }
}