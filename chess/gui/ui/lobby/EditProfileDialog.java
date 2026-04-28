package com.chess.gui.ui.lobby;

import com.chess.gui.guiUtils;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

// Dialog chỉnh sửa thông tin cá nhân
public class EditProfileDialog extends JDialog {

    private final JTextField  tfFullName;
    private final JTextField  tfUsername;
    private final JComboBox<String> cbGender;
    private final JTextField  tfDob;
    private final JPasswordField pfOldPassword;
    private final JPasswordField pfNewPassword;
    private final JPasswordField pfConfirmPassword;

    private boolean saved = false;

    public EditProfileDialog(Frame owner) {
        super(owner, "Chỉnh Sửa Thông Tin", true); // modal
        setSize(440, 560);
        setLocationRelativeTo(owner);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(guiUtils.LOBBY_CONTENT_BG);

        // ── Header ──────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(guiUtils.LOBBY_SIDEBAR);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel title = new JLabel("Chỉnh Sửa Thông Tin");
        title.setFont(guiUtils.LOBBY_FONT_TITLE);
        title.setForeground(guiUtils.LOBBY_SIDEBAR_TEXT);
        header.add(title, BorderLayout.WEST);

        // Nút đóng X
        JButton closeBtn = new JButton("✕");
        closeBtn.setFont(guiUtils.LOBBY_FONT_BODY);
        closeBtn.setForeground(guiUtils.LOBBY_SIDEBAR_SUB);
        closeBtn.setBackground(guiUtils.LOBBY_SIDEBAR);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());
        header.add(closeBtn, BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH);

        // ── Form ────────────────────────────────────────────────
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(guiUtils.LOBBY_CONTENT_BG);
        form.setBorder(new EmptyBorder(20, 24, 12, 24));

        tfFullName       = field("Nguyễn Văn A");
        tfUsername        = field("player123");
        cbGender          = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
        tfDob            = field("2000-01-01  (yyyy-mm-dd)");
        pfOldPassword    = passField();
        pfNewPassword    = passField();
        pfConfirmPassword = passField();

        styleCombo(cbGender);

        form.add(section("THÔNG TIN CƠ BẢN"));
        form.add(Box.createVerticalStrut(8));
        form.add(row("Họ và tên",   tfFullName));
        form.add(Box.createVerticalStrut(8));
        form.add(row("Tên đăng nhập", tfUsername));
        form.add(Box.createVerticalStrut(8));
        form.add(row("Giới tính",   cbGender));
        form.add(Box.createVerticalStrut(8));
        form.add(row("Ngày sinh",   tfDob));
        form.add(Box.createVerticalStrut(18));

        form.add(section("ĐỔI MẬT KHẨU  (để trống nếu không đổi)"));
        form.add(Box.createVerticalStrut(8));
        form.add(row("Mật khẩu cũ",         pfOldPassword));
        form.add(Box.createVerticalStrut(8));
        form.add(row("Mật khẩu mới",         pfNewPassword));
        form.add(Box.createVerticalStrut(8));
        form.add(row("Xác nhận mật khẩu",    pfConfirmPassword));

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.setBackground(guiUtils.LOBBY_CONTENT_BG);
        scroll.getViewport().setBackground(guiUtils.LOBBY_CONTENT_BG);
        root.add(scroll, BorderLayout.CENTER);

        // ── Buttons ─────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        btnPanel.setBackground(guiUtils.LOBBY_CONTENT_BG);
        btnPanel.setBorder(new MatteBorder(1, 0, 0, 0, guiUtils.LOBBY_BORDER));

        JButton cancelBtn = new JButton("Hủy");
        cancelBtn.setFont(guiUtils.LOBBY_FONT_BODY);
        cancelBtn.setBackground(new Color(200, 200, 200));
        cancelBtn.setForeground(guiUtils.LOBBY_TEXT);
        cancelBtn.setBorderPainted(false);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setOpaque(true);
        cancelBtn.setPreferredSize(new Dimension(90, 36));
        cancelBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelBtn.addActionListener(e -> dispose());

        JButton saveBtn = new JButton("Lưu");
        saveBtn.setFont(guiUtils.LOBBY_FONT_BOLD);
        saveBtn.setBackground(guiUtils.LOBBY_GREEN);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setBorderPainted(false);
        saveBtn.setFocusPainted(false);
        saveBtn.setOpaque(true);
        saveBtn.setPreferredSize(new Dimension(90, 36));
        saveBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        saveBtn.addActionListener(e -> onSave());

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        root.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(root);
    }

    // ── Logic lưu ───────────────────────────────────────────────
    private void onSave() {
        // Kiểm tra mật khẩu khớp nếu người dùng có nhập
        String newPass    = new String(pfNewPassword.getPassword());
        String confirmPass = new String(pfConfirmPassword.getPassword());

        if (!newPass.isEmpty() && !newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this,
                "Mật khẩu mới và xác nhận không khớp!",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (tfFullName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Họ và tên không được để trống!",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        saved = true;
        dispose();
    }

    // ── Getters cho dữ liệu đã nhập ────────────────────────────
    public boolean isSaved()           { return saved; }
    public String  getFullName()        { return tfFullName.getText().trim(); }
    public String  getUsername()        { return tfUsername.getText().trim(); }
    public String  getGender()          { return (String) cbGender.getSelectedItem(); }
    public String  getDateOfBirth()     { return tfDob.getText().trim(); }
    public String  getOldPassword()     { return new String(pfOldPassword.getPassword()); }
    public String  getNewPassword()     { return new String(pfNewPassword.getPassword()); }

    // Điền dữ liệu hiện tại vào form
    public void populate(String fullName, String username, String gender, String dob) {
        tfFullName.setText(fullName);
        tfUsername.setText(username);
        cbGender.setSelectedItem(gender);
        tfDob.setText(dob);
    }

    // ── Helpers tạo UI ──────────────────────────────────────────
    private JTextField field(String placeholder) {
        JTextField tf = new JTextField(placeholder);
        styleField(tf);
        return tf;
    }

    private JPasswordField passField() {
        JPasswordField pf = new JPasswordField();
        pf.setText("");
        styleField(pf);
        return pf;
    }

    private void styleField(JTextField tf) {
        tf.setFont(guiUtils.LOBBY_FONT_BODY);
        tf.setForeground(guiUtils.LOBBY_TEXT);
        tf.setBackground(guiUtils.LOBBY_CARD);
        tf.setBorder(new CompoundBorder(
            new LineBorder(guiUtils.LOBBY_BORDER, 1),
            new EmptyBorder(6, 10, 6, 10)
        ));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
    }

    private void styleCombo(JComboBox<?> cb) {
        cb.setFont(guiUtils.LOBBY_FONT_BODY);
        cb.setBackground(guiUtils.LOBBY_CARD);
        cb.setForeground(guiUtils.LOBBY_TEXT);
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
    }

    // Nhãn section
    private JLabel section(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(guiUtils.LOBBY_SUBTEXT);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    // Hàng label + field
    private JPanel row(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setBackground(guiUtils.LOBBY_CONTENT_BG);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        p.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(guiUtils.LOBBY_FONT_BODY);
        lbl.setForeground(guiUtils.LOBBY_TEXT);
        lbl.setPreferredSize(new Dimension(130, 36));

        p.add(lbl, BorderLayout.WEST);
        p.add(field, BorderLayout.CENTER);
        return p;
    }
}
