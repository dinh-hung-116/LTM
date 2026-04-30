package chess.gui.ui.login;

import chess.database.DTO.User;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class LoginPanel extends JPanel {

    private JTextField userField;
    private JPasswordField passField;
    private JButton loginBtn;
    private JButton registerBtn;

    // =========================
    // OUTGOING EVENTS
    // =========================
    public interface LoginRequestListener {
        void onLoginRequest(String username, String password);
    }

    private LoginRequestListener loginRequestListener;

    // Register button listener, Runnable là một interface không tham số
    private Runnable registerListener;

    public LoginPanel() {
        initComponents();
        setupLayout();
        setupActions();
    }

    // =========================
    // INIT UI
    // =========================
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

    // =========================
    // LAYOUT
    // =========================
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

    // =========================
    // ACTIONS
    // =========================
    private void setupActions() {

        loginBtn.addActionListener(e -> handleLogin());

        passField.addActionListener(e -> handleLogin());

        registerBtn.addActionListener(e -> {
            if (registerListener != null) {
                registerListener.run();
            }
        });
    }

    // =========================
    // LOGIN CLICK
    // =========================
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

        loginBtn.setEnabled(false);
        registerBtn.setEnabled(false);

        if (loginRequestListener != null) {
            loginRequestListener.onLoginRequest(username, password);
        }
    }

    // =========================
    // INCOMING RESPONSE METHODS
    // Handler / GameFrame will call these
    // =========================
    public void loginSuccess(User user) {
        // xóa hết dữ liệu cho lần đăng nhập kế tiếp
        wipeLoginData();
        
        loginBtn.setEnabled(true);

        JOptionPane.showMessageDialog(
                this,
                "Chào mừng " + user.getFullName() + "!"
        );
    }

    public void loginFailed() {

        loginBtn.setEnabled(true);
        passField.setText("");

        JOptionPane.showMessageDialog(
                this,
                "Sai tài khoản hoặc mật khẩu!",
                "Lỗi đăng nhập",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public void stopLoading() {
        loginBtn.setEnabled(true);
        registerBtn.setEnabled(true);
    }
    
    public void wipeLoginData() {
        char[] pwd = this.passField.getPassword();
        // nếu passField có dữ liệu thì xóa hết bằng cách thay bằng kí tự '\0'(kí tự kết thúc dòng -> chuỗi rỗng) 
        if(pwd != null) Arrays.fill(pwd, '\0');
        
        // xóa dữ liệu trên giao diện
        this.userField.setText("");
        this.passField.setText("");
    }
    
    // =========================
    // SETTERS
    // =========================
    public void setLoginRequestListener(LoginRequestListener listener) {
        this.loginRequestListener = listener;
    }

    public void setRegisterListener(Runnable listener) {
        this.registerListener = listener;
    }
}