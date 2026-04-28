package com.chess.gui.match.chessboard;

import io.github.wolfraam.chessgame.board.PieceType;
import io.github.wolfraam.chessgame.board.Side;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Window;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/*
 * Dialog cho phép người chơi chọn quân phong khi tốt đến hàng cuối.
 * Hiển thị 4 lựa chọn: Hậu, Xe, Tượng, Mã.
 */
public class PromotionDialog extends JDialog {

    private static final Color BG       = new Color(38, 36, 33);
    private static final Color BTN_BG   = new Color(55, 53, 50);
    private static final Color BTN_HOVER= new Color(80, 78, 75);
    private static final Color BORDER   = new Color(100, 98, 95);
    private static final int   BTN_SIZE = 80;

    private PieceType selected = PieceType.QUEEN;

    private PromotionDialog(Window parent, Assets assets, Side side) {
        super(parent, "Chọn quân phong", ModalityType.APPLICATION_MODAL);

        setBackground(BG);
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        JPanel root = new JPanel();
        root.setBackground(BG);
        root.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        root.setLayout(new java.awt.BorderLayout(0, 10));

        JLabel title = new JLabel("Chọn quân phong", SwingConstants.CENTER);
        title.setForeground(new Color(220, 220, 220));
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        root.add(title, java.awt.BorderLayout.NORTH);

        JPanel btnRow = new JPanel(new GridLayout(1, 4, 8, 0));
        btnRow.setBackground(BG);

        PieceType[] choices = {PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT};
        String[]    labels  = {"Hậu",           "Xe",           "Tượng",           "Mã"};

        for (int i = 0; i < 4; i++) {
            PieceType piece = choices[i];
            BufferedImage img = getPieceImage(assets, side, piece);

            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(BTN_SIZE, BTN_SIZE + 20));
            btn.setLayout(new java.awt.BorderLayout());
            btn.setBackground(BTN_BG);
            btn.setFocusPainted(false);
            btn.setBorderPainted(true);
            btn.setBorder(BorderFactory.createLineBorder(BORDER, 1));
            btn.setOpaque(true);

            if (img != null) {
                Image scaled = img.getScaledInstance(56, 56, Image.SCALE_SMOOTH);
                JLabel imgLabel = new JLabel(new ImageIcon(scaled), SwingConstants.CENTER);
                btn.add(imgLabel, java.awt.BorderLayout.CENTER);
            }

            JLabel nameLabel = new JLabel(labels[i], SwingConstants.CENTER);
            nameLabel.setForeground(new Color(180, 180, 180));
            nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btn.add(nameLabel, java.awt.BorderLayout.SOUTH);

            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                    btn.setBackground(BTN_HOVER);
                }
                @Override public void mouseExited(java.awt.event.MouseEvent e) {
                    btn.setBackground(BTN_BG);
                }
            });

            btn.addActionListener(e -> {
                selected = piece;
                dispose();
            });

            btnRow.add(btn);
        }

        root.add(btnRow, java.awt.BorderLayout.CENTER);
        setContentPane(root);
        pack();
        setLocationRelativeTo(parent);
    }

    private BufferedImage getPieceImage(Assets a, Side side, PieceType piece) {
        if (side == Side.WHITE) {
            switch (piece) {
                case QUEEN:  return a.WQ;
                case ROOK:   return a.WR;
                case BISHOP: return a.WB;
                case KNIGHT: return a.WN;
                default:     return null;
            }
        } else {
            switch (piece) {
                case QUEEN:  return a.BQ;
                case ROOK:   return a.BR;
                case BISHOP: return a.BB;
                case KNIGHT: return a.BN;
                default:     return null;
            }
        }
    }

    /** Hiển thị dialog và trả về quân được chọn (mặc định QUEEN nếu đóng). */
    public static PieceType show(Window parent, Assets assets, Side side) {
        PromotionDialog dlg = new PromotionDialog(parent, assets, side);
        dlg.setVisible(true);
        return dlg.selected;
    }
}
