package gui.match.chessboard;

import gui.match.chessboard.Assets;
import io.github.wolfraam.chessgame.board.Side;
import io.github.wolfraam.chessgame.board.Square;
import io.github.wolfraam.chessgame.move.Move;
import java.util.HashSet;

// BÊN ĐEN (bàn cờ bị lật)
public class BlackBoardPanel extends BoardPanel {

    public BlackBoardPanel() {
        super();

        this.drawBoard();
        setPieceImage();
        
        // cho quân trắng đi trước để quân đen có thể di chuyển
        this.chessGame.playMove(new Move(Square.A2, Square.A4));
    }

    // =========================
    // CHUYỂN ĐỔI INDEX
    // =========================
    // chuyển index bên góc nhìn đen thành index thật bên góc nhìn trắng do bàn cờ đen bị ngược với server 
    // mọi quá trình thực hiện logic như di chuyển quân đều phải chuyển đổi index từ GUI về 
    // index bên trắng
    private int flipIndex(int index) {
        return 63 - index;
    }
    
    // override lại hàm trong board
    @Override
    public void legalMovesHighlight(int index) {
        // Lấy Set<Move> từ ô index được chuyển đổi
       HashSet<Move> move = 
               (HashSet<Move>) this.chessGame.getLegalMoves(this.fromTilePanelToSquare(flipIndex(index)));
       
       // di chuyển qua từng Move và chuyển đổi index trắng thành đen rồi bật highlight
       for(Move mv : move) {
           this.boardTiles[flipIndex(this.fromSquareToIndex(mv.to))].setHighlighted(true);
       }
    }
    
    @Override
    public void clearLegalMovesHighlight(HashSet<Move> move) {
       // di chuyển qua từng Move và tắt TilePanel tại index đã chuyển đổi
       for(Move mv : move) {
           this.boardTiles[flipIndex(this.fromSquareToIndex(mv.to))].setHighlighted(false);
           //System.out.println(mv.from + "->" + mv.to);
       }
    }
    
    @Override
    public void clearLegalMovesHighlight(int index) {
       // tắt higlight nước đi hợp lệ
       // Lấy Set<Move> từ ô index đưuọc chuyển đổi
       HashSet<Move> move = 
               (HashSet<Move>) this.chessGame.getLegalMoves(this.fromTilePanelToSquare(flipIndex(index)));
       
       if(move.isEmpty()) System.out.println("Set with index " + index + " is empty!");
       // di chuyển qua từng Move và tắt index được chuyển đổi
       for(Move mv : move) {
           this.boardTiles[flipIndex(this.fromSquareToIndex(mv.to))].setHighlighted(false);
           //System.out.println(mv.from + "->" + mv.to);
       }
    }

    // =========================
    // SET PIECES (same logic but flipped position)
    // =========================
    @Override
    public void setPieceImage() {

        // pawns
        for (int i = 0; i < 8; ++i) {
            // white pawns (now on top visually)
            boardTiles[8 + i].setPieceImage(image.WP, Side.WHITE);

            // black pawns (now on bottom visually)
            boardTiles[48 + i].setPieceImage(image.BP, Side.BLACK);
        }

        // quân trắng bên trên
        boardTiles[0].setPieceImage(image.WR, Side.WHITE);
        boardTiles[1].setPieceImage(image.WN, Side.WHITE);
        boardTiles[2].setPieceImage(image.WB, Side.WHITE);
        boardTiles[3].setPieceImage(image.WQ, Side.WHITE);
        boardTiles[4].setPieceImage(image.WK, Side.WHITE);
        boardTiles[5].setPieceImage(image.WB, Side.WHITE);
        boardTiles[6].setPieceImage(image.WN, Side.WHITE);
        boardTiles[7].setPieceImage(image.WR, Side.WHITE);

        // quân đen bên dưới
        boardTiles[56].setPieceImage(image.BR, Side.BLACK);
        boardTiles[57].setPieceImage(image.BN, Side.BLACK);
        boardTiles[58].setPieceImage(image.BB, Side.BLACK);
        boardTiles[59].setPieceImage(image.BQ, Side.BLACK);
        boardTiles[60].setPieceImage(image.BK, Side.BLACK);
        boardTiles[61].setPieceImage(image.BB, Side.BLACK);
        boardTiles[62].setPieceImage(image.BN, Side.BLACK);
        boardTiles[63].setPieceImage(image.BR, Side.BLACK);
    }

    // =========================
    // MOVE HANDLER (reuse logic, just flip when needed)
    // =========================
    @Override
    public void handlerMovePiece(int index) {

        // chỉ xử lý khi tới lượt đen
        if (this.chessGame.getSideToMove() == Side.BLACK) {

            TilePanel indexTile = this.boardTiles[index];

            // chọn quân
            if (!indexTile.isEmpty() && indexTile.getSide() == Side.BLACK && this.sourceTile == null) {

                this.sourceTile = index;

                System.out.println("BLACK pick: " + index);

                // highlight
                // ô được chọn thì highlight theo index gui
                this.tileHighlight(index);
                this.legalMovesHighlight(index);
                
            }

            // thực hiện nước đi
            else if (this.sourceTile != null) {

                TilePanel srcTile = this.boardTiles[sourceTile];

                // lấy legal move từ engine (phải flip source)
                HashSet<Move> move =
                        (HashSet) this.chessGame.getLegalMoves(
                                this.fromTilePanelToSquare(flipIndex(sourceTile)));

                System.out.println("BLACK move: " + sourceTile + " -> " + index);

                // kiểm tra nước đi (phải flip cả source và target)
                if (this.movePiece(flipIndex(sourceTile), flipIndex(index))) {

                    // update UI (KHÔNG flip ở đây vì đang dùng UI index)
                    indexTile.setPieceImage(
                            srcTile.getPieceImage(),
                            srcTile.getSide());

                    srcTile.clear();

                    // clear highlight
                    this.clearTileHighlight(sourceTile);
                    this.clearLegalMovesHighlight(move);
                    this.clearTileHighlight(index);
                    
                    //==========
                    // NETWORK
                    //==========
                    // to do: y như bên trắng
                    
                    //==========
                    this.sourceTile = null;
                    
                } else {

                    System.out.println("Illegal move (BLACK)");

                    // tắt highlight
                    this.clearTileHighlight(this.sourceTile);
                    this.clearLegalMovesHighlight(this.sourceTile);
                    this.sourceTile = null;
                }
            }
        }
        // NEYWORK
        else {
            // nhận nước đi của  bên trắng từ server
        }
    }
    
}