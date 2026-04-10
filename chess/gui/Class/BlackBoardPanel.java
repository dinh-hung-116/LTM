package chess.gui.Class;

import chess.gui.Assets;

// BÊN ĐEN SẼ LẬT NGƯỢC QUÂN CỜ LẠI VÀ CÓ PHƯƠNG THỨC ĐỂ CHUYỂN ĐỔI NƯỚC ĐI VỚI SERVER
public class BlackBoardPanel extends BoardPanel {

    public BlackBoardPanel() {
        super();
        
        // vẽ ô và quân cờ
        this.drawBoard();
        setPieceImage();
    }

    @Override
    public void setPieceImage() {
         // hàng chốt, trắng trên đen dưới
       for(int i = 0; i < 8; ++i) {
           // hàng đen
           boardTiles[8 +i].setPieceImage(this.image.WP);
           // hàng trắng
           boardTiles[48 + i].setPieceImage(this.image.BP);
       }
       
       // các quân còn lại
       // hàng quân trắng
       boardTiles[0].setPieceImage(image.WR);
       boardTiles[1].setPieceImage(image.WN);
       boardTiles[2].setPieceImage(image.WB);
       boardTiles[3].setPieceImage(image.WK);
       boardTiles[4].setPieceImage(image.WQ);
       boardTiles[5].setPieceImage(image.WB);
       boardTiles[6].setPieceImage(image.WN);
       boardTiles[7].setPieceImage(image.WR);
        
       // tạo hàng quânđen
       boardTiles[56].setPieceImage(image.BR);
       boardTiles[57].setPieceImage(image.BN);
       boardTiles[58].setPieceImage(image.BB);
       boardTiles[59].setPieceImage(image.BK);
       boardTiles[60].setPieceImage(image.BQ);
       boardTiles[61].setPieceImage(image.BB);
       boardTiles[62].setPieceImage(image.BN);
       boardTiles[63].setPieceImage(image.BR);
    }

     @Override
    public void handlerMovePiece(int tileID) {
    }
}
