package chess.gui.Class;


import chess.gui.Assets;
import io.github.wolfraam.chessgame.board.Square;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// BÊN TRẮNG SẼ MẶC ĐỊNH THEO SERVER
public class WhiteBoardPanel extends BoardPanel {

    public WhiteBoardPanel() {
        super();
        
        // vẽ ô và quân cờ
        this.drawBoard();
        setPieceImage();
        
        printSquare();
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
           boardTiles[8 +i].setPieceImage(this.image.BP);
           // hàng trắng
           boardTiles[48 + i].setPieceImage(this.image.WP);
       }
    }
    @Override
    public void handlerMovePiece(int tileID) {
        
        //################  
    }
    
    
}
