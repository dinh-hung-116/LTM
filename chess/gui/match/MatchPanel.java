package com.chess.gui.match;

// Lớp này chứa bàn cờ, khung tên người dùng và ô tính năng

import com.chess.gui.match.chessboard.BoardPanel;
import com.chess.gui.guiUtils;
import com.chess.gui.match.chessboard.LocalBoardPanel;
import com.chess.network.NetworkClient;
import com.chess.network.NetworkConfig;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import com.chess.gui.match.matchinfo.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

// lớp này sẽ là một JPanel và dùng gridbaglayout
public class MatchPanel extends JPanel {
    // bàn cờ
    private BoardPanel boardPanel;

    // ô tính năng bên phải
    private SideBar sideBar;

    // hai ô thông tin người chơi
    private PlayerInfoPanel whiteInfo;
    private PlayerInfoPanel blackInfo;

    // hai panel để gán cho hai panel phía trên
    private PlayerInfoPanel top;
    private PlayerInfoPanel bottom;

    // dùng để điều chỉnh layout
    private GridBagConstraints gbc;

    // ── Network ───────────────────────────────────────────────
    // null khi chơi local
    private NetworkClient networkClient;

    // ─────────────────────────────────────────────────────────
    // CONSTRUCTOR LOCAL (chơi local 2 người)
    // ─────────────────────────────────────────────────────────
    public MatchPanel() {
        super.setSize(guiUtils.OUTER_FRAME_DIMENSION);
        super.setLayout(new GridBagLayout());

        // khởi tạo thành phần
        this.boardPanel = new LocalBoardPanel();

        this.sideBar = new SideBar();

        this.top = new PlayerInfoPanel("UserA");
        this.bottom = new PlayerInfoPanel("UserB");

        this.gbc = new GridBagConstraints();

        this.setBackground(guiUtils.MATCH_BG);

        // add components
        initMatchPanel();
        configPlayerInfo();
        // Đồng hồ chỉ bắt đầu sau nước đi đầu tiên (không autostart)

        listener();
    }

    // ─────────────────────────────────────────────────────────
    // KHỞI ĐỘNG VÁN ĐẤU QUA MẠNG
    // Gọi từ GameFrame sau khi server gửi ASSIGN:WHITE / ASSIGN:BLACK
    // ─────────────────────────────────────────────────────────
    /*
     * Thay bàn cờ local thành bàn cờ network và bắt đầu lắng nghe server.
     *
     * @param client     NetworkClient đang kết nối
     * @param isWhite    true = chơi bên trắng, false = bên đen
     * @param myName     tên người chơi của mình
     * @param opponentName tên đối thủ
     */
    // Callback để GameFrame biết panel đã sẵn sàng → showCard(MATCH)
    public interface ReadyListener { void onReady(); }
    private ReadyListener readyListener;
    public void setReadyListener(ReadyListener listener) { this.readyListener = listener; }

    public void startNetworkMatch(NetworkClient client,
                                  boolean isWhite,
                                  String myName,
                                  String opponentName) {
        System.out.println("[MatchPanel] startNetworkMatch() EDT="
                + javax.swing.SwingUtilities.isEventDispatchThread());
        this.networkClient = client;

        // 1. Tạo LocalBoardPanel và set chế độ online
        // flip=false khi trắng (trắng dưới), flip=true khi đen (đen dưới)
        LocalBoardPanel lb = new LocalBoardPanel(!isWhite);
        lb.setNetworkClient(client,
                isWhite ? io.github.wolfraam.chessgame.board.Side.WHITE
                        : io.github.wolfraam.chessgame.board.Side.BLACK);
        this.boardPanel = lb;

        // 2. Rebuild layout ĐỒNG BỘ
        removeAll();
        initMatchPanel();
        configPlayerInfo();
        System.out.println("[MatchPanel] initMatchPanel xong. Children: " + getComponentCount());

        // 3. Cập nhật tên người chơi
        bottom.setUsername(myName);
        top.setUsername(opponentName);

        // 4. Listener
        listener();

        // 5. Đồng hồ: KHÔNG start ngay, chờ server gửi CLOCK_START

        // 6. Repaint cả panel và parent
        revalidate();
        repaint();
        if (getParent() != null) {
            getParent().revalidate();
            getParent().repaint();
        }
        System.out.println("[MatchPanel] parent: " + getParent());

        // 7. Báo GameFrame showCard(MATCH)
        System.out.println("[MatchPanel] gọi onReady");
        if (readyListener != null) readyListener.onReady();

        // 8. Gắn MessageListener nhận tin từ server
        client.setMessageListener(raw -> {
            String command = NetworkConfig.getCommand(raw);
            String data    = NetworkConfig.getData(raw);
            switch (command) {
                case NetworkConfig.MOVE -> lb.applyOpponentMove(data);

                case NetworkConfig.GAME_OVER -> {
                    String msg = formatGameOverMsg(data, isWhite);
                    SwingUtilities.invokeLater(() -> {
                        sideBar.showGameOver(msg);
                        stopClock();
                        boardPanel.setGameEnded(true);
                    });
                }
                case NetworkConfig.DRAW_OFFER -> {
                    SwingUtilities.invokeLater(() -> {
                        int res = javax.swing.JOptionPane.showConfirmDialog(
                                this, opponentName + " xin h\u00f2a. B\u1ea1n c\u00f3 \u0111\u1ed3ng \u00fd kh\u00f4ng?",
                                "Xin h\u00f2a", javax.swing.JOptionPane.YES_NO_OPTION);
                        client.send(res == javax.swing.JOptionPane.YES_OPTION
                                ? NetworkConfig.DRAW_ACCEPT : NetworkConfig.DRAW_REJECT);
                    });
                }
                case NetworkConfig.DRAW_REJECT ->
                        SwingUtilities.invokeLater(() ->
                                javax.swing.JOptionPane.showMessageDialog(this, opponentName + " t\u1eeb ch\u1ed1i h\u00f2a."));
                case NetworkConfig.MOVE_INVALID ->
                        SwingUtilities.invokeLater(() ->
                                javax.swing.JOptionPane.showMessageDialog(this,
                                        "N\u01b0\u1edbc \u0111i kh\u00f4ng h\u1ee3p l\u1ec7!", "L\u1ed7i",
                                        javax.swing.JOptionPane.ERROR_MESSAGE));

                case NetworkConfig.CLOCK_START -> {
                    // Nước đi đầu tiên: server báo bắt đầu đồng hồ
                    // Cả 2 client nhận cùng lúc → đồng bộ
                    SwingUtilities.invokeLater(() -> switchClock());
                }

                case NetworkConfig.CLOCK_SWITCH -> {
                    // data = "WHITE" hoặc "BLACK" (bên vừa đi xong)
                    // Chuyển đồng hồ: dừng bên vừa đi, chạy bên kia
                    SwingUtilities.invokeLater(() -> switchClock());
                }
            }
        });

        // 9. Nút đầu hàng / xin hòa
        sideBar.setResignListener(isWhiteTurn -> client.send(NetworkConfig.RESIGN));
        sideBar.setDrawListener(()             -> client.send(NetworkConfig.DRAW_OFFER));
    }


    /** Format chuỗi kết quả từ server thành thông báo tiếng Việt. */
    private String formatGameOverMsg(String result, boolean isWhite) {
        return switch (result) {
            case NetworkConfig.WHITE_WINS        -> isWhite ? "Bạn thắng! ♔ Chiếu hết!"
                    : "Bạn thua! ♔ Đối thủ chiếu hết.";
            case NetworkConfig.BLACK_WINS        -> isWhite ? "Bạn thua! ♚ Đối thủ chiếu hết."
                    : "Bạn thắng! ♚ Chiếu hết!";
            case NetworkConfig.DRAW_RESULT       -> "Hòa! Hai bên đồng ý.";
            case "WHITE_WINS_RESIGN"             -> isWhite ? "Bạn thắng! Đối thủ đầu hàng."
                    : "Bạn thua! Bạn đã đầu hàng.";
            case "BLACK_WINS_RESIGN"             -> isWhite ? "Bạn thua! Bạn đã đầu hàng."
                    : "Bạn thắng! Đối thủ đầu hàng.";
            case "WHITE_WINS_DISCONNECT"         -> isWhite ? "Bạn thắng! Đối thủ mất kết nối."
                    : "Bạn thua! Mất kết nối.";
            case "BLACK_WINS_DISCONNECT"         -> isWhite ? "Bạn thua! Mất kết nối."
                    : "Bạn thắng! Đối thủ mất kết nối.";
            default                              -> "Ván đấu kết thúc: " + result;
        };
    }

    // gui sẽ có 2 cột, cột đầu tiên có 3 block và cột thứ hai có một block
    // nó giống kiểu này:
    // [A] [B]
    // [C] [B]
    // [D] [B]

    // Insets(top, left, bottom, right)
    // thêm thành phần vào panel
    public void initMatchPanel() {
        // Reset gbc về mặc định mỗi lần gọi
        // Tránh gridheight=3 từ lần trước còn sót khi removeAll() + rebuild
        this.gbc = new GridBagConstraints();

        //---LEFT COLUMN--
        // A(x;y) = (0;0)
        // UserA
        gbc.gridx = 0;
        gbc.gridy = 0;

        gbc.weighty = 0.2;

        gbc.fill = GridBagConstraints.BOTH;

        gbc.insets = new Insets(10, 5, 5, 10);

        // tạm thời cho userA và B vào đây
        this.add(this.top, gbc);

        // C(0;1)
        // BoardPanel
        gbc.gridx = 0;
        gbc.gridy = 1;

        //gbc.weighty = 0.8;

        gbc.fill = GridBagConstraints.BOTH;

        gbc.insets = new Insets(5, 5, 5, 10);

        this.add(this.boardPanel, gbc);

        // D(0;2)
        // UserB
        gbc.gridx = 0;
        gbc.gridy = 2;

        gbc.weighty = 0.2;

        gbc.fill = GridBagConstraints.BOTH;

        gbc.insets = new Insets(5, 5, 10, 10);

        this.add(this.bottom, gbc);

        //--RIGHT COLUMN--
        // B(1;0)
        gbc.gridx = 1;
        gbc.gridy = 0;
        // ghép 3 block lại thành 1
        gbc.gridheight = 3;

        gbc.fill = GridBagConstraints.BOTH;

        gbc.insets = new Insets(10, 10, 10, 10);

        this.add(this.sideBar, gbc);
    }

    public void configPlayerInfo() {
        // nếu là bên trắng -> !flip = true
        if(!this.boardPanel.isFlip()) {
            this.whiteInfo = this.bottom;
            this.blackInfo = this.top;
        }
        // bên đen thì ngược lại
        else {
            this.whiteInfo = top;
            this.blackInfo = bottom;
        }
    }
    //===== LOGIC =====
    private boolean clockStarted = false; // đồng hồ chưa chạy trước nước đi đầu

    private void listener() {
        // Cập nhật lịch sử nước đi, đổi đồng hồ, đồng bộ lượt cho sidebar
        this.boardPanel.setMoveListener(notation -> {
            this.sideBar.addMove(notation);
            // Đồng hồ chỉ bắt đầu sau nước đi đầu tiên
            if (!clockStarted) {
                clockStarted = true;
            }
            this.switchClock();
            this.sideBar.setWhiteTurn(this.boardPanel.isWhiteTurn());
        });

        // Kết thúc ván do engine phát hiện (chiếu hết, hòa, pat)
        this.boardPanel.setGameOverListener(msg -> {
            this.sideBar.showGameOver(msg);
            this.stopClock();
        });

        // Đầu hàng: bên đang có lượt tự thua
        this.sideBar.setResignListener(isWhiteTurn -> {
            String msg = isWhiteTurn
                    ? "Đen thắng! \u265a Trắng đầu hàng."
                    : "Trắng thắng! \u2654 Đen đầu hàng.";
            this.sideBar.showGameOver(msg);
            this.stopClock();
            this.boardPanel.setGameEnded(true);
        });

        // Xin hòa: cả hai đồng ý (local)
        this.sideBar.setDrawListener(() -> {
            this.sideBar.showGameOver("Hòa! Hai bên đồng ý.");
            this.stopClock();
            this.boardPanel.setGameEnded(true);
        });
    }

    // hàm để đổi lượt đồng hồ
    private void switchClock() {
        if(boardPanel.isWhiteTurn()) {
            whiteInfo.startClock();
            blackInfo.stopClock();
        } else {
            blackInfo.startClock();
            whiteInfo.stopClock();
        }
    }

    // tắt đồng hồ khi kết thúc trận
    private void stopClock() {
        blackInfo.stopClock();
        whiteInfo.stopClock();
    }
    //=================
    // có thể sẽ có phương thức để khởi động ván đấu

}