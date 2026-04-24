package chess.gui;

import chess.database.Class.User;
import chess.database.DAO.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class RegisterFrame extends JFrame {
    private JTextField userField;
    private JPasswordField passField;
    private JTextField fullNameField;
    private JComboBox<String> genderBox;
    private JTextField dobField;

    public RegisterFrame() {
        setTitle("Chess Game - Register");
        setSize(400, 550); 
        setLocationRelativeTo(null); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(38, 36, 33)); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Tiêu đề ---
        JLabel titleLabel = new JLabel("ĐĂNG KÝ TÀI KHOẢN", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(118, 150, 86)); 
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // --- Các trường nhập liệu ---
        gbc.gridwidth = 1;

        // 1. Username
        addLabelAndField(mainPanel, gbc, "Tên đăng nhập:", 1);
        userField = createTextField();
        mainPanel.add(userField, gbc);

        // 2. Password
        addLabelAndField(mainPanel, gbc, "Mật khẩu:", 2);
        passField = new JPasswordField(15);
        styleField(passField);
        mainPanel.add(passField, gbc);

        // 3. Full Name
        addLabelAndField(mainPanel, gbc, "Họ và Tên:", 3);
        fullNameField = createTextField();
        mainPanel.add(fullNameField, gbc);

        // 4. Gender 
        addLabelAndField(mainPanel, gbc, "Giới tính:", 4);
        String[] genders = {"Nam", "Nữ", "Khác"};
        genderBox = new JComboBox<>(genders);
        genderBox.setBackground(new Color(60, 63, 65));
        genderBox.setForeground(Color.WHITE);
        mainPanel.add(genderBox, gbc);

        // 5. Date of Birth
        addLabelAndField(mainPanel, gbc, "Ngày sinh (yyyy-mm-dd):", 5);
        dobField = createTextField();
        dobField.setText("2000-01-01"); 
        mainPanel.add(dobField, gbc);

        // --- Nút bấm ---
        gbc.gridx = 0; gbc.gridwidth = 2;
        
        // Nút Đăng ký
        gbc.gridy = 6; 
        JButton regBtn = new JButton("Tạo tài khoản");
        regBtn.setBackground(new Color(118, 150, 86));
        regBtn.setForeground(Color.WHITE);
        regBtn.setFocusPainted(false);
        regBtn.addActionListener(e -> handleRegister());
        mainPanel.add(regBtn, gbc);

        // Nút Quay lại (Nút Text)
        gbc.gridy = 7;
        JButton backBtn = new JButton("Đã có tài khoản? Quay lại đăng nhập");
        backBtn.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        backBtn.setForeground(new Color(118, 150, 86));
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });
        mainPanel.add(backBtn, gbc);

        add(mainPanel);
    }

    // Hàm tiện ích để giảm lặp code khi thiết kế UI
    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, String labelText, int yPos) {
        gbc.gridx = 0; gbc.gridy = yPos;
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        panel.add(label, gbc);
        gbc.gridx = 1;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(15);
        styleField(field);
        return field;
    }

    private void styleField(JTextField field) {
        field.setBackground(new Color(60, 63, 65));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
    }

    // --- Logic xử lý Đăng ký ---
    private void handleRegister() {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();
        String fullName = fullNameField.getText().trim();
        String gender = (String) genderBox.getSelectedItem();
        String dobString = dobField.getText().trim();

        // 1. Kiểm tra rỗng
        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || dobString.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Ép kiểu Ngày sinh (LocalDate)
        LocalDate dob;
        try {
            dob = LocalDate.parse(dobString);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Ngày sinh không hợp lệ!\nVui lòng nhập theo định dạng YYYY-MM-DD (Ví dụ: 2004-12-25)", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Tạo User và đẩy vào Database
        User newUser = new User(0, username, password, fullName, gender, dob);
        
        // Gọi hàm registerUser() - Hàm này tự động thêm vào bảng user và user_stats
        if (UserDAO.registerUser(newUser, 1200)) { 
            JOptionPane.showMessageDialog(this, "Đăng ký thành công!\nVui lòng đăng nhập để chơi game.");
            new LoginFrame().setVisible(true);
            this.dispose(); // Đóng form đăng ký
        } else {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập đã tồn tại hoặc có lỗi xảy ra!", "Lỗi đăng ký", JOptionPane.ERROR_MESSAGE);
        }
    }
}