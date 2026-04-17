package chess.gui.match.chessboard;


import chess.gui.match.chessboard.Assets;
import io.github.wolfraam.chessgame.ChessGame;
import io.github.wolfraam.chessgame.board.Side;
import io.github.wolfraam.chessgame.board.Square;
import io.github.wolfraam.chessgame.move.Move;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

//package chess.gui.match.chessboard;
//
//
//import gui.match.chessboard.Assets;
//import io.github.wolfraam.chessgame.ChessGame;
//import io.github.wolfraam.chessgame.board.Side;
//import io.github.wolfraam.chessgame.board.Square;
//import io.github.wolfraam.chessgame.move.Move;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Scanner;


// BÊN TRẮNG SẼ MẶC ĐỊNH THEO SERVER
public class WhiteBoardPanel extends BoardPanel {

    public WhiteBoardPanel() {
        super();
        
        // vẽ ô và quân cờ
        this.drawBoard();
        setPieceImage();
        
        //printSquare();

    }

    
    // in rq square 
    public void printSquare() {
        for(int i = 0; i < 64; ++i) {
            System.out.print(this.fromTilePanelToSquare(this.boardTiles[i].getIndex()));
            
            if((i + 1) % 8 == 0) {
                System.out.println();
            }
        }
    }
    
    // các square bên bàn cờ trắng sẽ được xếp giống bên engine
    @Override
    public void setPieceImage() {
        // hàng chốt, đen trên trắng dưới
        for(int i = 0; i < 8; ++i) {
           // hàng đen
           boardTiles[8 +i].setPieceImage(this.image.BP, Side.BLACK);
           // hàng trắng
           boardTiles[48 + i].setPieceImage(this.image.WP, Side.WHITE);
       }
       
       // hàng quân đen
       boardTiles[0].setPieceImage(image.BR, Side.BLACK);
       boardTiles[1].setPieceImage(image.BN, Side.BLACK);
       boardTiles[2].setPieceImage(image.BB, Side.BLACK);
       boardTiles[3].setPieceImage(image.BQ, Side.BLACK);
       boardTiles[4].setPieceImage(image.BK, Side.BLACK);
       boardTiles[5].setPieceImage(image.BB, Side.BLACK);
       boardTiles[6].setPieceImage(image.BN, Side.BLACK);
       boardTiles[7].setPieceImage(image.BR, Side.BLACK);
       
       // hàng quân trắng
       boardTiles[56].setPieceImage(image.WR, Side.WHITE);
       boardTiles[57].setPieceImage(image.WN, Side.WHITE);
       boardTiles[58].setPieceImage(image.WB, Side.WHITE);
       boardTiles[59].setPieceImage(image.WQ, Side.WHITE);
       boardTiles[60].setPieceImage(image.WK, Side.WHITE);
       boardTiles[61].setPieceImage(image.WB, Side.WHITE);
       boardTiles[62].setPieceImage(image.WN, Side.WHITE);
       boardTiles[63].setPieceImage(image.WR, Side.WHITE);
    }
        @Override
        public void handlerMovePiece(int index) {
            // nếu đây là lượt của trắng
            if(this.chessGame.getSideToMove() == Side.WHITE) {
                // Lấy ra TilePanel tại index cho dễ thao tác
                TilePanel indexTile = this.boardTiles[index];
                
                // nếu ô đó không trống và là quân của mình và sourceTile = null
                if(!indexTile.isEmpty() && indexTile.getSide() == Side.WHITE &&this.sourceTile == null) {
                    // lưu tọa độ vào sourceTile
                    this.sourceTile = index;
                    
                    // in ra index và side để kiểm tra
                    System.out.println("My index is: " + sourceTile + ":" + index +
                            ", and my side is: " + indexTile.getSide().toString());
                
                    // highlight ô và ô là nước đi hợp lệ
                    this.highlightTileNMove(index);
                    
                }
                // nếu sourceTile đã được gán -> kiểm tra và thực hiện di chuyển
                else if(this.sourceTile != null) {
                    // lấy ra TilePanel tại sourceTile
                    TilePanel srcTile = this.boardTiles[sourceTile];
                    
                    // lấy ra mảng nước đi hợp lệ để nếu nước đi đúng thì sẽ dùng để tắt highlight
                    HashSet<Move> move = 
                            (HashSet) this.chessGame.getLegalMoves(this.fromTilePanelToSquare(sourceTile));
                    
                    System.out.println("Source: " + sourceTile + ":" + index);
                    /*
                    // nếu sourceTile != null và sourceTile trùng với index -> hủy tương tác với ô
                    if(sourceTile.equals(index)) {
                        // xóa highlight ô và các ô là nước đi hợp lệ
                        this.clearHighlightTileNMove(sourceTile);
                        // gán lại source Tile
                        this.sourceTile = null;
                    }
                    */
                    // kiểm tra xem nước đi có hợp lệ và đã được thực thi thì cập nhật lại Tile
                    if(this.movePiece(this.sourceTile, index)) {
                        
                        // cập nhật ảnh và side cho ô cờ mới 
                        // lấy ảnh và Side của ô cờ cũ và cập nhật cho ô mới
                        indexTile.setPieceImage(
                                srcTile.getPieceImage(), 
                                srcTile.getSide());
                        
                        // cập nhật ảnh và side cho ô cờ cũ
                        srcTile.clear();
                        
                        // xóa highlight của ô cờ cũ và các ô nước đi hợp lệ
                        this.clearTileHighlight(sourceTile);
                        // cái này dùng phương thức có input (int, HashSet) do trong engine thì quân cờ tại sourceTle đã di 
                        // chuyển và không còn tồn tại ở đó nên nếu truyền sourceTile thì mảng trả về sẽ trống
                        this.clearLegalMovesHighlight(move);
                        
                        // tắt highlight tại ô index do phương thức highlight vẫn chạy khi bấm ô index
                        this.clearTileHighlight(index);
                        
                        //==========
                        // NETWORK
                        //==========
                        // to do: phần này gửi nước đi của bên trăng cho server
                        
                        //==========
                        
                        // gán lại sourceTile
                        this.sourceTile = null;
                    }
                    // nếu sai thì báo nước đi không hợp lệ và gán lại sourceTile kèm tắt highlight
                    // điều kiện này cũng bao gồm vieejc ô có quân cờ được bấm 2 lần liên tiếp
                    else {
                        System.out.println(
                                "<" + this.chessGame.getSideToMove().toString() + 
                                "> has made illegal move or a piece has been clicked twice!\n" + 
                                "<SOURCE:INDEX> = " + "<" + sourceTile + ":" + index + ">");
                        
                        this.clearHighlightTileNMove(sourceTile);
                        
                        this.sourceTile = null;
                    }
                }
            }
            // NETWORK
            else {
                // bên đen(network)
                // phần này nhận nước đi của bên đen từ server
            }
        }
        
        //###################
    }
