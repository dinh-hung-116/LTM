package com.chess.gui.ui.login;

import com.chess.database.Class.User;
import com.chess.database.DAO.UserDAO;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    private JTextField userField;
    private JPasswordField passField;
    private JButton loginBtn;
    private JButton registerBtn;

    // Login success listener
    public interface LoginListener {
        void onLoginSuccess(User user);
    }

    private LoginListener loginListener;

    // Register button listener
    private Runnable registerListener;

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

        registerBtn = new JButton("Chưa có tài khoản? Đăng ký");
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        registerBtn.setForeground(new Color(118, 150, 86));
        registerBtn.setBorderPainted(false);
        registerBtn.setContentAreaFilled(false);
        registerBtn.setFocusPainted(false);
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerBtn.setOpaque(false);

    }

    private void setupLayout() {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("ĐĂNG NHẬP", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(118, 150, 86));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridwidth = 1;

        JLabel userLabel = new JLabel("Tên đăng nhập:");
        userLabel.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(userLabel, gbc);

        gbc.gridx = 1;
        add(userField, gbc);

        JLabel passLabel = new JLabel("Mật khẩu:");
        passLabel.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(passLabel, gbc);

        gbc.gridx = 1;
        add(passField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(loginBtn, gbc);

        gbc.gridy = 4;
        add(registerBtn, gbc);
    }

    private void setupActions() {

        loginBtn.addActionListener(e -> handleLogin());

        passField.addActionListener(e -> handleLogin());

        registerBtn.addActionListener(e -> {
            if (registerListener != null) {
                registerListener.run();
            }
        });
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

            if (loginListener != null) {
                loginListener.onLoginSuccess(loggedInUser);
            }

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Sai tài khoản hoặc mật khẩu!",
                    "Lỗi đăng nhập",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Login listener
    public void setLoginListener(LoginListener listener) {
        this.loginListener = listener;
    }

    // Register listener
    public void setRegisterListener(Runnable listener) {
        this.registerListener = listener;
    }
}