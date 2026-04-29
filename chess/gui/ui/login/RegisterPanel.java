package chess.gui.ui.login;

import chess.database.Class.User;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class RegisterPanel extends JPanel {

    private JTextField userField;
    private JPasswordField passField;
    private JTextField fullNameField;
    private JComboBox<String> genderBox;
    private JTextField dobField;

    // =========================
    // LISTENERS
    // =========================
    // backListener dùng cho nút quay lại panel login
    private Runnable backListener;
    private RegisterRequestListener registerRequestListener;

    // =====================================================
    // REQUEST LISTENER
    // =====================================================
    // Listener dùng để gửi yêu cầu đăng ký cho Handler
    public interface RegisterRequestListener {
        void onRegisterRequest(User user);
    }

    // =====================================================
    // CONSTRUCTOR
    // =====================================================
    public RegisterPanel() {
        initComponents();
        setupLayout();
    }

    // =====================================================
    // INIT UI
    // =====================================================
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

    // =====================================================
    // LAYOUT
    // =====================================================
    private void setupLayout() {

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("ĐĂNG KÝ TÀI KHOẢN");

        title.setFont(new Font("Segoe UI",Font.BOLD, 22));

        title.setForeground(new Color(118, 150, 86));

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

        // =====================
        // REGISTER BUTTON
        // =====================
        JButton regBtn = new JButton("Tạo tài khoản");

        regBtn.addActionListener(e -> handleRegister());

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;

        add(regBtn, gbc);

        // =====================
        // BACK BUTTON
        // =====================
        JButton backBtn = new JButton("Quay lại đăng nhập");

        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setForeground(new Color(118, 150, 86));

        backBtn.addActionListener(e -> {

            if (backListener != null) {
                backListener.run();
            }
        });

        gbc.gridy = 7;

        add(backBtn, gbc);
    }

    // =====================================================
    // REGISTER ACTION
    // =====================================================
    private void handleRegister() {

        String username = userField.getText().trim();

        String password = new String(passField.getPassword()).trim();

        String fullName = fullNameField.getText().trim();

        String gender = (String) genderBox.getSelectedItem();

        String dobString = dobField.getText().trim();

        // =====================
        // EMPTY CHECK
        // =====================
        if (username.isEmpty()
                || password.isEmpty()
                || fullName.isEmpty()
                || dobString.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng nhập đầy đủ thông tin!"
            );
            return;
        }

        // =====================
        // DATE CHECK
        // =====================
        LocalDate dob;

        try {
            dob = LocalDate.parse(dobString);
        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ngày sinh phải dạng yyyy-mm-dd"
            );
            return;
        }

        // =====================
        // CREATE USER
        // =====================
        User user = new User(
                0,
                username,
                password,
                fullName,
                gender,
                dob
        );

        // =====================
        // FIRE REQUEST
        // =====================
        if (registerRequestListener != null) {
            registerRequestListener
                    .onRegisterRequest(user);
        }
    }

    // =====================================================
    // PUBLIC UI METHODS
    // =====================================================
    public void showRegisterSuccess() {

        JOptionPane.showMessageDialog(
                this,
                "Đăng ký thành công!"
        );

        clearForm();

        if (backListener != null) {
            backListener.run();
        }
    }

    public void showRegisterFail(String message) {
        JOptionPane.showMessageDialog(
                this,
                message
        );
    }

    public void clearForm() {

        userField.setText("");
        passField.setText("");
        fullNameField.setText("");
        genderBox.setSelectedIndex(0);
        dobField.setText("2000-01-01");
    }

    // =====================================================
    // HELPERS
    // =====================================================
    private void addRow(String text, JComponent comp, int row, GridBagConstraints gbc) {

        gbc.gridx = 0;
        gbc.gridy = row;

        JLabel label =
                new JLabel(text);

        label.setForeground(Color.WHITE);

        add(label, gbc);

        gbc.gridx = 1;
        add(comp, gbc);
    }

    private JTextField createTextField() {

        JTextField tf =
                new JTextField(15);

        styleField(tf);

        return tf;
    }

    private void styleField(
            JTextField field
    ) {

        field.setBackground(
                new Color(60, 63, 65)
        );

        field.setForeground(
                Color.WHITE
        );

        field.setCaretColor(
                Color.WHITE
        );
    }

    // =====================================================
    // SETTERS
    // =====================================================
    public void setBackListener(Runnable backListener) {
        this.backListener = backListener;
    }

    public void setRegisterRequestListener(RegisterRequestListener listener) {
        this.registerRequestListener = listener;
    }
}