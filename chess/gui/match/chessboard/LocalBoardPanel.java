package chess.gui.match.chessboard;

import io.github.wolfraam.chessgame.board.PieceType;
import io.github.wolfraam.chessgame.board.Side;
import io.github.wolfraam.chessgame.move.Move;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import javax.swing.SwingUtilities;

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
        super(false);
        // Vẽ bàn cờ và đặt quân
        this.drawBoard();
        this.setPieceImage();
        // Không cần playMove giả như BlackBoardPanel cũ
        // ChessGame mặc định WHITE đi trước → đúng luật
        this.printTilePanelIndex();
    }


    // =====================
    // XỬ LÝ NƯỚC ĐI
    // =====================
    @Override
    public void handlerMovePiece(TilePanel indexTile) {
        // Lấy lượt hiện tại từ engine (WHITE hoặc BLACK)
        Side currentSide = this.chessGame.getSideToMove();
        //TilePanel indexTile = this.boardTiles[index];
        // index của indexTile
        int index = indexTile.getIndex();

        // === BƯỚC 1: Chưa chọn quân nào (srcIndex == null) ===
        if (this.sourceTile == null) {
            // Chỉ chọn ô không trống và quân thuộc bên đang có lượt
            if (!indexTile.isEmpty() && indexTile.getSide() == currentSide) {
                this.sourceTile = indexTile;

                System.out.println("[" + currentSide + "] chọn ô: " + index
                        + " (" + fromTilePanelToSquare(index) + ")");

                // Highlight ô được chọn và các nước đi hợp lệ
                this.highlightTileNMove(index);
            }
            // Nếu ô trống hoặc quân không phải lượt mình → bỏ qua
        }

        // === BƯỚC 2: Đã chọn quân (srcIndex != null) ===
        else {
            // lấy index srcIndex
            int srcIndex = this.sourceTile.getIndex();

            // --- TH: Bấm lại cùng ô → hủy chọn ---
            if (srcIndex == index) {
                System.out.println("[" + currentSide + "] hủy chọn ô: " + index);
                this.clearAllMovesHighlighted();
                this.sourceTile = null;
                return;
            }

            // --- TH: Bấm quân cùng màu khác → chuyển sang quân đó ---
            if (!indexTile.isEmpty() && indexTile.getSide() == currentSide) {
                // Tắt highlight quân cũ
                this.clearAllMovesHighlighted();

                // Chọn quân mới
                this.sourceTile = indexTile;
                this.highlightTileNMove(index);

                System.out.println("[" + currentSide + "] đổi sang ô: " + index
                        + " (" + fromTilePanelToSquare(index) + ")");
                return;
            }

            // --- TH: Thực hiện nước đi ---
            // hàm để in thông tin 
            this.clickDebug(index, currentSide);
            
            // Nếu nước đi có hợp lệ
            String notation = "";
            boolean validMove = false;
            //===== PROMOTION =====
            if(this.isPromotionAttempt(srcIndex, index)) {
                //System.out.println("promo");
                // Hiển thị dialog chọn quân phong
                PieceType choice = PromotionDialog.show(
                        SwingUtilities.getWindowAncestor(this), this.image, currentSide);
                // thực hiện phong cấp trong engine
                notation = this.playMoveWithPromotion(srcIndex, index, choice);
                
                validMove = true;
            }
            //=======================
            
            //===== TH còn lại =====
            else if (this.isLegalMove(srcIndex, index)) {
                //===== EN PASSANT =====
                if(this.isEnPassent(srcIndex, index)) {
                    // thực hiện di chuyển 
                    notation = this.playMoveWithEnPassant(srcIndex, index, currentSide);
                    validMove = true;
                }
                //======================
                
                //===== CASTLING =====
                else if(this.isCastlingAttempt(srcIndex, index, currentSide)) {
                    System.out.println(currentSide.toString() + " attempted to castle ");
                    // thực hiện di chuyển
                    notation = this.playMoveWithCastling(srcIndex, index, currentSide);
                    validMove = true;
                }
                //=======================
                
                //===== NƯỚC ĐI THƯỜNG =====
                else {
                    notation = this.playMove(srcIndex, index, currentSide);
                    validMove = true;
                }
                //===========================
            }
            
            //===== MOVE LISTENER =====
            // thêm nước đi vào lịch sử
            if (this.moveListener != null && validMove) {
                System.out.println(notation);
                this.moveListener.onMoveMade(notation);
            } 
            /*
            - Sau mỗi nước đi hợp lệ thực hiện:
            + Kiểm tra kết thức game
            + Kiểm tra highlight khi vua bị chiếu
            */
            if (validMove) {
                this.checkGameOver();
                
                if(this.chessGame.isKingAttacked()) this.highlightKingInCheck(this.chessGame.getSideToMove());
                // tắt highlight của bên bị chiếu trước đó do khi bị chiếu thì bắt buộc phải thoát chiếu
                // lấy current vì sau khi thực hiện nước đi thì chessGame đã đổi bên di chuyển
                this.clearCheckHighlighted(currentSide);
            }
            //=========================
            else {
                System.out.println("TH2: no");
            }
            
            System.out.println(this.chessGame.getASCII());
            System.out.println("→ Lượt tiếp theo: " + this.chessGame.getSideToMove());
        }
    }
}