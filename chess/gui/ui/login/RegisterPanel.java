package com.chess.gui.ui.login;

import com.chess.database.Class.User;
import com.chess.database.DAO.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class RegisterPanel extends JPanel {

    private JTextField userField;
    private JPasswordField passField;
    private JTextField fullNameField;
    private JComboBox<String> genderBox;
    private JTextField dobField;

    // runnable là một interface không tham số của java
    private Runnable backListener;

    public RegisterPanel() {
        initComponents();
        setupLayout();
    }

    public void setBackListener(Runnable backListener) {
        this.backListener = backListener;
    }

    private void initComponents() {
        setBackground(new Color(38, 36, 33));

        userField = createTextField();

        passField = new JPasswordField(15);
        styleField(passField);

        fullNameField = createTextField();

        genderBox = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
        genderBox.setBackground(new Color(60, 63, 65));
        genderBox.setForeground(Color.WHITE);

        dobField = createTextField();
        dobField.setText("2000-01-01");
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("ĐĂNG KÝ TÀI KHOẢN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(118,150,86));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(title, gbc);

        gbc.gridwidth = 1;

        addRow("Tên đăng nhập:", userField, 1, gbc);
        addRow("Mật khẩu:", passField, 2, gbc);
        addRow("Họ và tên:", fullNameField, 3, gbc);
        addRow("Giới tính:", genderBox, 4, gbc);
        addRow("Ngày sinh:", dobField, 5, gbc);

        JButton regBtn = new JButton("Tạo tài khoản");
        regBtn.addActionListener(e -> handleRegister());

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        add(regBtn, gbc);

        JButton backBtn = new JButton("Quay lại đăng nhập");
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setForeground(new Color(118,150,86));

        backBtn.addActionListener(e -> {
            if(backListener != null) {
                backListener.run();
            }
        });

        gbc.gridy = 7;
        add(backBtn, gbc);
    }

    private void addRow(String text, JComponent comp, int row, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel(text), gbc);

        gbc.gridx = 1;
        add(comp, gbc);
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField(15);
        styleField(tf);
        return tf;
    }

    private void styleField(JTextField field) {
        field.setBackground(new Color(60,63,65));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
    }

    private void handleRegister() {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();
        String fullName = fullNameField.getText().trim();
        String gender = (String) genderBox.getSelectedItem();
        String dobString = dobField.getText().trim();

        if(username.isEmpty() || password.isEmpty()
                || fullName.isEmpty() || dobString.isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        LocalDate dob;

        try {
            dob = LocalDate.parse(dobString);
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Ngày sinh phải dạng yyyy-mm-dd");
            return;
        }

        User user = new User(0, username, password, fullName, gender, dob);

        if(UserDAO.registerUser(user, 1200)) {
            JOptionPane.showMessageDialog(this,
                    "Đăng ký thành công!");

            if(backListener != null) {
                backListener.run();
            }

        } else {
            JOptionPane.showMessageDialog(this,
                    "Tên đăng nhập đã tồn tại!");
        }
    }
}

