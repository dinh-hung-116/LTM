package com.chess.gui.match.chessboard;

import com.chess.network.NetworkClient;
import com.chess.network.NetworkConfig;
import io.github.wolfraam.chessgame.board.PieceType;
import io.github.wolfraam.chessgame.board.Side;
import io.github.wolfraam.chessgame.board.Square;
import io.github.wolfraam.chessgame.move.Move;
import javax.swing.SwingUtilities;

/**
 * LocalBoardPanel - Bàn cờ dùng chung cho cả 2 chế độ:
 *
 * ── LOCAL (networkClient == null):
 *    Hai người chơi luân phiên trên cùng máy.
 *
 * ── ONLINE (networkClient != null):
 *    Chỉ bên mySide mới được click.
 *    Sau nước đi hợp lệ → gửi "MOVE:from-to" lên server.
 *    Nước đối thủ nhận qua applyOpponentMove() từ NetworkClient thread.
 *
 * Lợi thế so với White/BlackBoardPanel:
 *    Đã xử lý đầy đủ en passant, castling, promotion.
 *    Bàn cờ không bị lật → index GUI = index engine, không cần flipIndex.
 */
public class LocalBoardPanel extends BoardPanel {

    // ── Network ────────────────────────────────────────────────
    // null  = local mode
    // !null = online mode
    private NetworkClient networkClient = null;

    // Bên của người chơi này khi online (WHITE hoặc BLACK)
    private Side mySide = null;

    // ─────────────────────────────────────────────────────────
    // CONSTRUCTOR
    // ─────────────────────────────────────────────────────────
    /** Local mode: trắng dưới, đen trên */
    public LocalBoardPanel() {
        super(false);
        this.drawBoard();
        this.setPieceImage();
    }

    /** Network mode: flip=true nếu chơi bên đen (bàn lật, đen ở dưới) */
    public LocalBoardPanel(boolean flip) {
        super(flip);
        this.drawBoard();
        this.setPieceImage();
    }

    // ─────────────────────────────────────────────────────────
    // CHUYỂN SANG CHẾ ĐỘ ONLINE
    // Gọi từ MatchPanel.startNetworkMatch() sau khi nhận ASSIGN
    // ─────────────────────────────────────────────────────────
    /**
     * @param client  NetworkClient đang kết nối đến server
     * @param side    Side.WHITE hoặc Side.BLACK: bên của mình
     */
    public void setNetworkClient(NetworkClient client, Side side) {
        this.networkClient = client;
        this.mySide        = side;
        System.out.println("[LocalBoardPanel] Chế độ online: " + side);
    }

    /** true = đang online, false = local */
    public boolean isNetworkMode() {
        return networkClient != null && networkClient.isConnected();
    }

    // ─────────────────────────────────────────────────────────
    // XỬ LÝ CLICK CỦA NGƯỜI DÙNG
    // ─────────────────────────────────────────────────────────
    @Override
    public void handlerMovePiece(TilePanel indexTile) {
        // Khoá khi ván đã kết thúc
        if (this.gameEnded) return;

        Side currentSide = this.chessGame.getSideToMove();
        int  index       = indexTile.getIndex();

        // Online: chỉ cho click khi đúng lượt của mình
        if (isNetworkMode() && currentSide != mySide) return;

        // ── BƯỚC 1: Chưa chọn quân ──────────────────────────
        if (this.sourceTile == null) {
            if (!indexTile.isEmpty() && indexTile.getSide() == currentSide) {
                this.sourceTile = indexTile;
                this.highlightTileNMove(index);
                System.out.println("[" + currentSide + "] chọn: "
                        + fromTilePanelToSquare(index));
            }
            return;
        }

        // ── BƯỚC 2: Đã chọn quân ────────────────────────────
        int srcIndex = this.sourceTile.getIndex();

        // Bấm lại cùng ô → hủy chọn
        if (srcIndex == index) {
            this.clearAllMovesHighlighted();
            this.sourceTile = null;
            return;
        }

        // Bấm quân cùng màu khác → đổi sang quân đó
        if (!indexTile.isEmpty() && indexTile.getSide() == currentSide) {
            this.clearAllMovesHighlighted();
            this.sourceTile = indexTile;
            this.highlightTileNMove(index);
            System.out.println("[" + currentSide + "] đổi sang: "
                    + fromTilePanelToSquare(index));
            return;
        }

        // ── BƯỚC 3: Thực hiện nước đi ───────────────────────
        this.clickDebug(index, currentSide);

        String  notation  = "";
        boolean validMove = false;
        String  netMove   = ""; // chuỗi gửi lên server

        // Phong cấp
        if (this.isPromotionAttempt(srcIndex, index)) {
            PieceType choice = PromotionDialog.show(
                    SwingUtilities.getWindowAncestor(this), this.image, currentSide);
            if (choice == null) choice = PieceType.QUEEN;

            notation  = this.playMoveWithPromotion(srcIndex, index, choice);
            validMove = true;
            // "e7-e8-Q"
            netMove   = buildMoveStr(srcIndex, index) + "-" + choice.name();
        }
        // En passant
        else if (this.isLegalMove(srcIndex, index) && this.isEnPassent(srcIndex, index)) {
            notation  = this.playMoveWithEnPassant(srcIndex, index, currentSide);
            validMove = true;
            netMove   = buildMoveStr(srcIndex, index);
        }
        // Nhập thành
        else if (this.isLegalMove(srcIndex, index)
                && this.isCastlingAttempt(srcIndex, index, currentSide)) {
            notation  = this.playMoveWithCastling(srcIndex, index, currentSide);
            validMove = true;
            netMove   = buildMoveStr(srcIndex, index);
        }
        // Nước đi thường
        else if (this.isLegalMove(srcIndex, index)) {
            notation  = this.playMove(srcIndex, index, currentSide);
            validMove = true;
            netMove   = buildMoveStr(srcIndex, index);
        }

        // ── Sau nước đi hợp lệ ──────────────────────────────
        if (validMove) {
            // Gửi lên server nếu đang online
            if (isNetworkMode()) {
                networkClient.send(NetworkConfig.msg(NetworkConfig.MOVE, netMove));
            }
            // Cập nhật sidebar
            if (this.moveListener != null) {
                this.moveListener.onMoveMade(notation);
            }
            // Kiểm tra kết thúc ván
            this.checkGameOver();
            // Highlight vua bị chiếu
            if (this.chessGame.isKingAttacked()) {
                this.highlightKingInCheck(this.chessGame.getSideToMove());
            }
            this.clearCheckHighlighted(currentSide);
        } else {
            System.out.println("[" + currentSide + "] Nước đi không hợp lệ!");
        }

        System.out.println(this.chessGame.getASCII());
        System.out.println("→ Lượt tiếp: " + this.chessGame.getSideToMove());
    }

    // ─────────────────────────────────────────────────────────
    // NHẬN NƯỚC ĐI CỦA ĐỐI THỦ TỪ SERVER
    // Gọi từ NetworkClient reader thread → phải invokeLater
    // ─────────────────────────────────────────────────────────
    /**
     * Format moveStr:
     *   Nước thường : "e2-e4"
     *   Promotion   : "e7-e8-Q"
     *   En passant  : "e5-d6"   (engine tự nhận biết)
     *   Nhập thành  : "e1-g1"   (engine tự nhận biết)
     */
    public void applyOpponentMove(String moveStr) {
        SwingUtilities.invokeLater(() -> {
            try {
                String[] parts = moveStr.split("-");
                if (parts.length < 2) {
                    System.err.println("[LocalBoardPanel] moveStr lỗi: " + moveStr);
                    return;
                }

                Square from = Square.valueOf(parts[0].toUpperCase());
                Square to   = Square.valueOf(parts[1].toUpperCase());
                int srcIdx  = this.fromSquareToIndex(from);
                int dstIdx  = this.fromSquareToIndex(to);

                Side opponentSide = (mySide == Side.WHITE) ? Side.BLACK : Side.WHITE;

                // Phong cấp
                if (parts.length >= 3) {
                    PieceType piece = PieceType.valueOf(parts[2].toUpperCase());
                    // playMoveWithPromotion cập nhật cả engine lẫn GUI
                    this.playMoveWithPromotion(srcIdx, dstIdx, piece);
                }
                // En passant
                else if (this.isEnPassent(srcIdx, dstIdx)) {
                    this.playMoveWithEnPassant(srcIdx, dstIdx, opponentSide);
                }
                // Nhập thành
                else if (this.isCastlingAttempt(srcIdx, dstIdx, opponentSide)) {
                    this.playMoveWithCastling(srcIdx, dstIdx, opponentSide);
                }
                // Nước đi thường
                else {
                    this.chessGame.playMove(new Move(from, to));
                    // Dùng getTilePanelWithIndex thay vì boardTiles[] trực tiếp
                    // vì getTilePanelWithIndex tự xử lý flip khi bàn bị lật (bên đen)
                    TilePanel srcTile = this.getTilePanelWithIndex(srcIdx);
                    TilePanel dstTile = this.getTilePanelWithIndex(dstIdx);
                    dstTile.setPieceImage(srcTile.getPieceImage(), srcTile.getSide());
                    srcTile.clear();
                }

                // Highlight vua bị chiếu
                if (this.chessGame.isKingAttacked()) {
                    this.highlightKingInCheck(this.chessGame.getSideToMove());
                }
                this.clearCheckHighlighted(opponentSide);

                // Lịch sử nước đi
                if (this.moveListener != null) {
                    this.moveListener.onMoveMade(parts[0] + "-" + parts[1]);
                }

                // Kiểm tra kết thúc ván
                this.checkGameOver();

                System.out.println("[LocalBoardPanel] Đối thủ đi: " + moveStr);
                System.out.println(this.chessGame.getASCII());

            } catch (Exception e) {
                System.err.println("[LocalBoardPanel] applyOpponentMove lỗi: "
                        + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    // ─────────────────────────────────────────────────────────
    // HELPER
    // ─────────────────────────────────────────────────────────
    /** Tạo chuỗi "from-to" từ GUI index, VD: "e2-e4" */
    private String buildMoveStr(int srcIndex, int dstIndex) {
        return fromTilePanelToSquare(srcIndex).name().toLowerCase()
                + "-"
                + fromTilePanelToSquare(dstIndex).name().toLowerCase();
    }
}