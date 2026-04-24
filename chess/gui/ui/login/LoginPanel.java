package chess.gui.ui.login;

import chess.database.Class.User;
import chess.database.DAO.UserDAO;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    private JTextField userField;
    private JPasswordField passField;
    private JButton loginBtn;

    public LoginPanel() {
        initComponents();
        setupLayout();
        setupActions();
    }

    private void initComponents() {
        setBackground(new Color(38, 36, 33));

        userField = new JTextField(15);
        userField.setBackground(new Color(60, 63, 65));
        userField.setForeground(Color.WHITE);
        userField.setCaretColor(Color.WHITE);

        passField = new JPasswordField(15);
        passField.setBackground(new Color(60, 63, 65));
        passField.setForeground(Color.WHITE);
        passField.setCaretColor(Color.WHITE);

        loginBtn = new JButton("Đăng nhập");
        loginBtn.setBackground(new Color(118, 150, 86));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("ĐĂNG NHẬP", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(118, 150, 86));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // Username Label
        JLabel userLabel = new JLabel("Tên đăng nhập:");
        userLabel.setForeground(Color.WHITE);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        add(userLabel, gbc);

        gbc.gridx = 1;
        add(userField, gbc);

        // Password Label
        JLabel passLabel = new JLabel("Mật khẩu:");
        passLabel.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(passLabel, gbc);

        gbc.gridx = 1;
        add(passField, gbc);

        // Login Button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(loginBtn, gbc);
    }

    private void setupActions() {
        loginBtn.addActionListener(e -> handleLogin());
        passField.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Vui lòng nhập đầy đủ thông tin!",
                "Cảnh báo",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        User loggedInUser = UserDAO.checkLogin(username, password);

        if (loggedInUser != null) {
            JOptionPane.showMessageDialog(
                this,
                "Chào mừng " + loggedInUser.getFullName() + "!"
            );

            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }

            /*
            SwingUtilities.invokeLater(() -> {
                new GameFrame(loggedInUser).setVisible(true);
            });
*/

        } else {
            JOptionPane.showMessageDialog(
                this,
                "Sai tài khoản hoặc mật khẩu!",
                "Lỗi đăng nhập",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}