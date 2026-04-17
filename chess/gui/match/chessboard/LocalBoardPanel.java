package chess.gui.match.chessboard;

import chess.gui.match.chessboard.Assets;
import io.github.wolfraam.chessgame.board.Side;
import io.github.wolfraam.chessgame.board.Square;
import io.github.wolfraam.chessgame.move.Move;
import java.util.HashSet;

/*
 * LocalBoardPanel - Bàn cờ cho 2 người chơi trên cùng một máy.
 *
 * - Góc nhìn chuẩn: trắng ở dưới, đen ở trên (giống WhiteBoardPanel).
 * - Lượt đi đầu tiên luôn là TRẮNG (theo luật cờ vua).
 * - Sau mỗi nước đi hợp lệ, lượt tự động chuyển sang bên kia.
 * - Mỗi bên CHỈ được chọn và di chuyển quân của mình.
 */
public class LocalBoardPanel extends BoardPanel {

    public LocalBoardPanel() {
        super();
        // Vẽ bàn cờ và đặt quân
        this.drawBoard();
        setPieceImage();
        // Không cần playMove giả như BlackBoardPanel cũ
        // ChessGame mặc định WHITE đi trước → đúng luật
    }

    // =====================
    // ĐẶT QUÂN CỜ BAN ĐẦU
    // =====================
    // Góc nhìn trắng: trắng dưới (index 48-63), đen trên (index 0-15)
    @Override
    public void setPieceImage() {
        // Hàng chốt
        for (int i = 0; i < 8; i++) {
            boardTiles[8 + i].setPieceImage(image.BP, Side.BLACK);   // chốt đen hàng 2 từ trên
            boardTiles[48 + i].setPieceImage(image.WP, Side.WHITE);  // chốt trắng hàng 7
        }

        // Hàng quân đen (trên cùng)
        boardTiles[0].setPieceImage(image.BR, Side.BLACK);
        boardTiles[1].setPieceImage(image.BN, Side.BLACK);
        boardTiles[2].setPieceImage(image.BB, Side.BLACK);
        boardTiles[3].setPieceImage(image.BQ, Side.BLACK);
        boardTiles[4].setPieceImage(image.BK, Side.BLACK);
        boardTiles[5].setPieceImage(image.BB, Side.BLACK);
        boardTiles[6].setPieceImage(image.BN, Side.BLACK);
        boardTiles[7].setPieceImage(image.BR, Side.BLACK);

        // Hàng quân trắng (dưới cùng)
        boardTiles[56].setPieceImage(image.WR, Side.WHITE);
        boardTiles[57].setPieceImage(image.WN, Side.WHITE);
        boardTiles[58].setPieceImage(image.WB, Side.WHITE);
        boardTiles[59].setPieceImage(image.WQ, Side.WHITE);
        boardTiles[60].setPieceImage(image.WK, Side.WHITE);
        boardTiles[61].setPieceImage(image.WB, Side.WHITE);
        boardTiles[62].setPieceImage(image.WN, Side.WHITE);
        boardTiles[63].setPieceImage(image.WR, Side.WHITE);
    }

    // =====================
    // XỬ LÝ NƯỚC ĐI
    // =====================
    @Override
    public void handlerMovePiece(int index) {
        // Lấy lượt hiện tại từ engine (WHITE hoặc BLACK)
        Side currentSide = this.chessGame.getSideToMove();
        TilePanel indexTile = this.boardTiles[index];

        // === BƯỚC 1: Chưa chọn quân nào (sourceTile == null) ===
        if (this.sourceTile == null) {
            // Chỉ chọn ô không trống và quân thuộc bên đang có lượt
            if (!indexTile.isEmpty() && indexTile.getSide() == currentSide) {
                this.sourceTile = index;

                System.out.println("[" + currentSide + "] chọn ô: " + index
                        + " (" + fromTilePanelToSquare(index) + ")");

                // Highlight ô được chọn và các nước đi hợp lệ
                this.highlightTileNMove(index);
            }
            // Nếu ô trống hoặc quân không phải lượt mình → bỏ qua
        }

        // === BƯỚC 2: Đã chọn quân (sourceTile != null) ===
        else {
            TilePanel srcTile = this.boardTiles[this.sourceTile];

            // Lưu lại set nước đi hợp lệ trước khi engine cập nhật
            HashSet<Move> legalMoves =
                    (HashSet<Move>) this.chessGame.getLegalMoves(
                            this.fromTilePanelToSquare(this.sourceTile));

            // --- TH: Bấm lại cùng ô → hủy chọn ---
            if (this.sourceTile.equals(index)) {
                System.out.println("[" + currentSide + "] hủy chọn ô: " + index);
                this.clearTileHighlight(this.sourceTile);
                this.clearLegalMovesHighlight(legalMoves);
                this.sourceTile = null;
                return;
            }

            // --- TH: Bấm quân cùng màu khác → chuyển sang quân đó ---
            if (!indexTile.isEmpty() && indexTile.getSide() == currentSide) {
                // Tắt highlight quân cũ
                this.clearTileHighlight(this.sourceTile);
                this.clearLegalMovesHighlight(legalMoves);

                // Chọn quân mới
                this.sourceTile = index;
                this.highlightTileNMove(index);

                System.out.println("[" + currentSide + "] đổi sang ô: " + index
                        + " (" + fromTilePanelToSquare(index) + ")");
                return;
            }

            // --- TH: Thực hiện nước đi ---
            System.out.println("[" + currentSide + "] đi: "
                    + fromTilePanelToSquare(this.sourceTile)
                    + " → " + fromTilePanelToSquare(index));

            if (this.movePiece(this.sourceTile, index)) {
                // Cập nhật hình ảnh ô đích
                indexTile.setPieceImage(srcTile.getPieceImage(), srcTile.getSide());
                System.out.println(chessGame.getASCII());
                // Xóa ô nguồn
                srcTile.clear();

                // Tắt highlight
                this.clearTileHighlight(this.sourceTile);
                this.clearLegalMovesHighlight(legalMoves);
                this.clearTileHighlight(index);

                // Reset sourceTile → lượt tiếp theo tự động là bên kia (engine tự chuyển)
                this.sourceTile = null;

                System.out.println("→ Lượt tiếp theo: " + this.chessGame.getSideToMove());
            } else {
                // Nước đi không hợp lệ → hủy chọn
                System.out.println("[" + currentSide + "] nước đi không hợp lệ!");
                this.clearTileHighlight(this.sourceTile);
                this.clearLegalMovesHighlight(legalMoves);
                this.sourceTile = null;
            }
        }
    }
}