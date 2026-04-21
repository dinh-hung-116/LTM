package chess.gui.match.chessboard;


import chess.gui.guiUtils;
import chess.gui.match.chessboard.Assets;
import io.github.wolfraam.chessgame.ChessGame;
import io.github.wolfraam.chessgame.board.PieceType;
import io.github.wolfraam.chessgame.board.Side;
import io.github.wolfraam.chessgame.board.Square;
import io.github.wolfraam.chessgame.move.Move;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import javax.swing.JPanel;


/*
- Cấu trúc mảng của engine:
          <y>[0 | 1 | 2 | 3 | 4 | 5 | 6 | 7] <X>
                 [a | b | c | d | e | f  | g | h] <FILE>
[0] [7] [8]      b8
[1] [6] [7] 
[2] [5] [6]
[3] [4] [5]                d5
[4] [3] [4]           c4
[5] [2] [3]
[6] [1] [2]
[7] [0] [1] a1
<x | Y | Rank>

- Rank và Y của mảng tra theo hướng từ dưới đi lên
- Tọa độ (x;y) là của [][] trong java, hàng trước cột sau.
- Tạo độ (X;Y) là của engine, cột trước hàng sau. Vd:
a1 -> (X;Y) = (0;0) -> (x;y) = (7;0)
b8 -> ....... = (1;7) -> (x;y) = (0;1)
d5 -> (X;Y) = (3;4) -> (x;y) = (3;3)
c4 -> ......   = (2;3) -> (x;y) =(4;2) 

**
- Từ gui về engine. VD: c4 có (x;y) = (4;2)
+ Chuyển sang FILE
4 là hàng và 2 là cột bên gui, ta chuyên 2 thành FILE bên engine do hàng bên gui và FILE bên engine dùng 
chung một mảng. Vậy ta có (FILE;RANK) = (y;?) = (2;?)

+ Chuyển sang RANK
hàng bên gui và engine dùng chung một mảng [0;7] nhưng ngược nhau nên chuyển đổi theo công thức:
"RANK = 7 - x". Vậy ta có RANK = 7 - 4 = 3

-> (FILE;RANK) = (2;3)
**
*/
public abstract class BoardPanel extends JPanel{
    // mảng các TilePanel
    // dùng mảng tĩnh do luôn có 64 ô cờ
    protected TilePanel[] boardTiles;
    
    // biến này để lưu ô hiện tại dùng trong quá trình di chuyển quân cờ
    protected TilePanel sourceTile;
    
    // Assets
    protected Assets image;

    // bàn cờ trong engine
    protected ChessGame chessGame;
    
    // biến để kiểm tra xem bàn cờ có bị lật không, white = false, black = true
    protected boolean flip;
    
    // chuỗi để lưu notation theo bên trắng, đen thì chạy ngược lại
    protected final String[] files = {"a","b","c","d","e","f","g","h"};    
    protected final String[] ranks = {"8","7","6","5","4","3","2","1"};
    
    // interface để MatchPanel gọi khi cần
    public interface MoveListener {
        void onMoveMade(String notation);
    }
    
    // biến interface
    protected MoveListener moveListener;

    
    protected BoardPanel(boolean flip) {
        // Tạo Panel có 8x8 ô
        this.setLayout(new GridLayout(8, 8));
        
        // kích thước
        this.setPreferredSize(guiUtils.BOARD_PANEL_DIMENSION);
        
        // Khởi tạo mảng
        this.boardTiles = new TilePanel[64];
        
        // biến lưu ô
        this.sourceTile = null;
        
        // Assets
        this.image = new Assets();
        
        // ván cờ
        // trong engine thì trắng mặc định đi trước
        this.chessGame = new ChessGame();
        
        // flipIndex
        this.flip = flip;
       
    }
    
    //===== MOVE LISTENER =====
    public void setMoveListener(MoveListener listener) {
        this.moveListener = listener;
    }
    //=========================
    
    //===== THIẾT LẬP UI BÀN CỜ =====
    // vẽ ô cờ, ô cờ không thay đổi dù lật ngược
    // Vẽ màu và gán giá trị index cho ô cờ
    // tham số true = white, false = black
    public void drawBoard() {
        // biến để lật giá trị index trong TilePanel
        int flipIndex;
        if(!this.flip) flipIndex = 0;
        else flipIndex = 63;
        
        for (int i = 0; i < this.boardTiles.length; i++) {

            // chuyển đổi mảng 1 chiều thành 2 chiều
            int row = i / 8;
            /*
            VD: 
            0 / 8 = 0 -> hàng 0
            8 / 8 = 1 -> hàng 1
            */
            int col = i % 8;
            /*
            VD:
            0 % 8 = 0 -> cột 1
            1 % 8 = 1 -> cột 1
            */
            
            // Sử dụng tính chẵn-lẻ để tô màu
            Color tileColor = ((row + col) % 2 == 0)
                    ? guiUtils.LIGHT_TILE
                    : guiUtils.DARK_TILE;
            
            // tạo TilePanel
            TilePanel tile = new TilePanel(Math.abs(flipIndex - i), tileColor, this);
            
            // gán vào mảng TilePanel
            this.boardTiles[i] = tile;
            // đưa vào panel
            add(tile);
        }
    }
    public void setPieceImage() {
        // bên trắng
        if(!flip) {
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
        // bên đen
        else {
            // hàng chốt
            for (int i = 0; i < 8; ++i) {
                // trắng trên
                boardTiles[8 + i].setPieceImage(image.WP, Side.WHITE);

                // đen dưới
                boardTiles[48 + i].setPieceImage(image.BP, Side.BLACK);
            }
            // quân trắng bên trên
            boardTiles[0].setPieceImage(image.WR, Side.WHITE);
            boardTiles[1].setPieceImage(image.WN, Side.WHITE);
            boardTiles[2].setPieceImage(image.WB, Side.WHITE);
            boardTiles[3].setPieceImage(image.WK, Side.WHITE);
            boardTiles[4].setPieceImage(image.WQ, Side.WHITE);
            boardTiles[5].setPieceImage(image.WB, Side.WHITE);
            boardTiles[6].setPieceImage(image.WN, Side.WHITE);
            boardTiles[7].setPieceImage(image.WR, Side.WHITE);

            // quân đen bên dưới
            boardTiles[56].setPieceImage(image.BR, Side.BLACK);
            boardTiles[57].setPieceImage(image.BN, Side.BLACK);
            boardTiles[58].setPieceImage(image.BB, Side.BLACK);
            boardTiles[59].setPieceImage(image.BK, Side.BLACK);
            boardTiles[60].setPieceImage(image.BQ, Side.BLACK);
            boardTiles[61].setPieceImage(image.BB, Side.BLACK);
            boardTiles[62].setPieceImage(image.BN, Side.BLACK);
            boardTiles[63].setPieceImage(image.BR, Side.BLACK);
        }
    }
    //============================
    
    
    // ===== DI CHUYỂN =====
    // - Gồm các nước: 
    // + thông thường
    // + ăn quân
    //* CHÚ Ý: CHƯA XÉT CÁC TRƯỜNG HỢP ĐẶC BIỆT NHƯ EN PASSANT, PHONG CẤP VÀ NHẬP THÀNH
    public boolean isLegalMove(int source, int dest) {
        // map tọa độ index sang enigne
        Square from = this.fromTilePanelToSquare(source);
        Square to = this.fromTilePanelToSquare(dest);
        
        // tạo Move
        Move move = new Move(from, to);
        
        // kiểm tra move
        if(this.chessGame.isLegalMove(move)) {
            return true;
        }
        // nếu sai thì báo sai
        return false;
    }
    
    public String playMove(int source, int dest, Side currentSide) {
        // map tọa độ index sang enigne
        Square from = this.fromTilePanelToSquare(source);
        Square to = this.fromTilePanelToSquare(dest);
        
        // tạo Move
        Move move = new Move(from, to);
        
        // thực hiện di chuyển
        this.chessGame.playMove(move);
        
        // cập nhật gui
        // lấy Tile
        TilePanel srcTile = this.getTilePanelWithIndex(source);
        TilePanel destTile = this.getTilePanelWithIndex(dest);
        
        destTile.setPieceImage(srcTile.getPieceImage(), srcTile.getSide());
        // Xóa ô nguồn
                
        srcTile.clear();
        this.clearAllHighlighted();
        System.out.println("source: " + srcTile.getIndex() + "\ndest: " + destTile.getIndex());
                
        // Reset srcIndex → lượt tiếp theo tự động là bên kia (engine tự chuyển)
        this.sourceTile = null;
        
        return move.toString();
    }
    //======================
    
    //===== EN PASSANT =====
    public boolean isEnPassent(int source, int dest) {
        // đổi index GUI -> Square của engine
        Square from = this.fromTilePanelToSquare(source);
        Square to   = this.fromTilePanelToSquare(dest);
        // lấy quân tại ô nguồn
        var piece = this.chessGame.getPiece(from);
        // không có quân
        if (piece == null) return false;
        // phải là tốt
        if (piece.pieceType != io.github.wolfraam.chessgame.board.PieceType.PAWN) {
            return false;
        }
        // tốt phải đi chéo 1 ô
        int dx = Math.abs(to.x - from.x);
        int dy = Math.abs(to.y - from.y);

        if (dx != 1 || dy != 1) {
            return false;
        }
        // ô đích phải trống (nếu có quân thì là ăn thường, không phải en passant)
        if (this.chessGame.getPiece(to) != null) {
            return false;
        }
        // nước đi phải hợp lệ theo engine
        Move move = new Move(from, to);
        // kiểm tra nước đi hợp lệ
        if(this.chessGame.isLegalMove(move)) {
            return true;
        }
        return false;
    }
    
    public String playMoveWithEnPassant(int source, int dest, Side currentSide) {
        // map tọa độ index sang enigne
        Square from = this.fromTilePanelToSquare(source);
        Square to = this.fromTilePanelToSquare(dest);
        
        // tạo Move
        Move move = new Move(from, to);
        
        // thực hiện di chuyển
        this.chessGame.playMove(move);
        
        // cập nhật gui
        // lấy Tile
        TilePanel srcTile = this.getTilePanelWithIndex(source);
        TilePanel destTile = this.getTilePanelWithIndex(dest);
        
        // xóa ô tại vị trí phía dưới index do quân chốt đã nhảy lên vị trí phía sau quân chốt bị ăn
        // hai trường hợp trắng và đen. trắng thì + 8, đen thì - 8
        int capturedPawn;
        if(currentSide == Side.WHITE) capturedPawn = dest + 8;           
        else capturedPawn = dest - 8;            
                    
        destTile.setPieceImage(srcTile.getPieceImage(), srcTile.getSide());
        // Xóa ô nguồn
        srcTile.clear();
        this.clearAllHighlighted();
        
        // xóa ô có quân chốt bị ăn
        this.getTilePanelWithIndex(capturedPawn).clear();
                
        // Reset srcIndex → lượt tiếp theo tự động là bên kia (engine tự chuyển)
        this.sourceTile = null;
        
        return move.toString();
    }
    //======================
    

    public String squareToStr(Square sq) {
        char file = (char) ('a' + sq.x);
        char rank = (char) ('1' + sq.y);
        return "" + file + rank;
    }

    //===== PROMOTION =====
    public String playMoveWithPromotion(int source, int dest, PieceType promotion) {
        Square from = fromTilePanelToSquare(source);
        Square to   = fromTilePanelToSquare(dest);
        Move move   = new Move(from, to, promotion);
        
        chessGame.playMove(move);
          
        // CẬP NHẬT GUI
        TilePanel srcTile = this.getTilePanelWithIndex(source);
        TilePanel destTile = this.getTilePanelWithIndex(dest);
        
        // lấy ra ảnh quân phong cấp để cập nhật gui
        BufferedImage destImage = getPromotedImage(promotion, srcTile.getSide());
        destTile.setPieceImage(destImage, srcTile.getSide());
        srcTile.clear();
        this.clearAllHighlighted();
        this.sourceTile = null;
        
        String n = move.toString().replace(" ", "").substring(0, 6);
        if(n.contains("K")) n = n.replace("K", "N");
        
        return n;
    }

    // Kiểm tra nước đi có phải phong tốt không (tốt đến hàng cuối)
    // valid notation <from><to><piece>. Ex: a7a8Q
    public boolean isPromotionAttempt(int source, int dest) {
        TilePanel srcTile = this.getTilePanelWithIndex(source);
        // kiểm tra xem ô có quân cờ không
        if (srcTile.isEmpty()) {
            //System.out.println("isPromotionAttempt: Tile empty");
            return false;
        }
        // nếu có thì lấy ra và kiểm tra xem có phải chốt không
        BufferedImage img = srcTile.getPieceImage();
        boolean isPawn = (img == image.WP || img == image.BP);
        if (!isPawn) {
            //System.out.println("isPromotionAttempt: Tile isn't pawn");
            return false;
        }
        // kiểm tra xem quân chốt đó có đang đi tới cuối bàn cờ không
        Square from = fromTilePanelToSquare(source);
        Square to = fromTilePanelToSquare(dest);
        Side side = srcTile.getSide();
        //System.out.println("to: " + to.y);
        if(!((side == Side.WHITE && to.y == 7) || (side == Side.BLACK && to.y == 0))) {
            //System.out.println("isPromotionAttempt: Pawn isn't going to the corect rank");
            return false;
        }
        // trường hợp phong cấp thì Move sẽ là Move(Square, Square, PieceType())
        // lấy quân nào để kiểm tra cũng được, mặc định sẽ là QUEEN
        if(!this.chessGame.isLegalMove(new Move(from, to, PieceType.QUEEN))) {
            //System.out.println("isPromotionAttempt: not legal move");
            return false;
        }
        return true;
    }

    // Trả về ảnh quân được phong dựa theo màu và loại quân
    public BufferedImage getPromotedImage(PieceType promotion, Side side) {
        if (side == Side.WHITE) {
            switch (promotion) {
                case QUEEN:  return image.WQ;
                case ROOK:   return image.WR;
                case BISHOP: return image.WB;
                default:     return image.WN;
            }
        } else {
            switch (promotion) {
                case QUEEN:  return image.BQ;
                case ROOK:   return image.BR;
                case BISHOP: return image.BB;
                default:     return image.BN;
            }
        }
    }
    //======================
    
    //===== CASTLING =====
    public boolean isCastlingAttempt(int source, int dest, Side side) {
        Square from = fromTilePanelToSquare(source);
        Square to   = fromTilePanelToSquare(dest);
        
        // kiểm tra xem source có phải là vua không
        if(this.chessGame.getPiece(from).pieceType != PieceType.KING) {
            return false;
        }
        // kiểm tra xem vua có đang đi tới ô để nhập thành không
        // nếu là vua trắng và đi tới ô C hoặc G -> đang cố nhập thành
        boolean whiteCastling = (side == Side.WHITE && (to == Square.C1 || to == Square.G1));
        // vua đen
        boolean blackCastling = (side == Side.BLACK && (to == Square.C8 || to == Square.G8));
        if(!(whiteCastling || blackCastling)) {
            return false;
        }
        
        // nếu đủ điều kiện thì kiểm tra tính hợp lệ của bước nhập thành
        if(!this.chessGame.isLegalMove(new Move(from, to))) {
            return false;
        }
        return true;
    }
    
    public String playMoveWithCastling(int source, int dest, Side currentSide) {
        String notation = "";
        // map tọa độ index sang enigne
        Square from = this.fromTilePanelToSquare(source);
        Square to = this.fromTilePanelToSquare(dest);
        
        // tạo Move
        Move move = new Move(from, to);
        
        // thực hiện di chuyển
        this.chessGame.playMove(move);
        
        // cập nhật gui
        // lấy Tile
        TilePanel srcTile = this.getTilePanelWithIndex(source);
        TilePanel destTile = this.getTilePanelWithIndex(dest);
        
        // lấy ra Square tại index để kiểm tra            
        Square destSquare = this.fromTilePanelToSquare(destTile.getIndex());
        // biến để lưu vị trí mới  và cũ của quân xe tham gia nhập thành
        int rookNewCoor, rookOldCoor;
        // nếu nhập thành bên vua, cả vua đen và vua trắng đều sẽ di chuyển sang phải -> G1 hoặc G8
        if (destSquare == Square.G1 || destSquare == Square.G8) {
            // vị trí mới của quân xe sẽ đi về bên trái vị trí của vua(index) -> index - 1
            rookNewCoor = destTile.getIndex() - 1;
            // vị trí cũ
            rookOldCoor = destTile.getIndex() + 1;
            
            notation = "O-O";
        } // nếu là bên hậu
        else {
            // quân xe sẽ đi về bên phải của vua -> +1
            rookNewCoor = destTile.getIndex() + 1;
            // vị trí cũ
            rookOldCoor = destTile.getIndex() - 2;
            
            notation = "O-O-O";
        }

        // cập nhật hình ảnh vua
        destTile.setPieceImage(srcTile.getPieceImage(), currentSide);
        srcTile.clear();

        // cập nhật hình ảnh quân xe
        // lấy ra Tile cũ và mới của quân xe
        TilePanel rookNewTile = this.getTilePanelWithIndex(rookNewCoor);
        TilePanel rookOldTile = this.getTilePanelWithIndex(rookOldCoor);

        rookNewTile.setPieceImage(rookOldTile.getPieceImage(), currentSide);
        rookOldTile.clear();

        this.clearAllHighlighted();

        this.sourceTile = null;
        
        return notation;
    }
    //====================
    
    
    public void printMove(int source, int dest) {
        System.out.println("MOVE: <" + source +" : " + dest + ">");
    }
    
    //===== HIGHLIGHT =====
    // Phương thức tắt highlight mọi ô 
    public void clearAllHighlighted() {
       for(int i = 0; i < this.boardTiles.length; ++i) {
           this.boardTiles[i].setHighlighted(false);
       }
    }
    
    // Phương thức tắt highlight 1 ô
    public void clearTileHighlight(int index) {
        this.boardTiles[index].setHighlighted(false);
    }
    
    // Phương thức tắt highlight các ô là nước đi hợp lệ
    // input là index của ô có quân cờ
    public void clearLegalMovesHighlight(int index) {
       // tắt highlight tại index
       clearTileHighlight(index);
       
       // tắt higlight nước đi hợp lệ
       // Lấy Set<Move> từ ô index
       HashSet<Move> move = 
               (HashSet<Move>) this.chessGame.getLegalMoves(this.fromTilePanelToSquare(index));
       
       if(move.isEmpty()) System.out.println("Set with index " + index + " is empty!");
       // di chuyển qua từng Move và tắt Square
       for(Move mv : move) {
           this.boardTiles[this.fromSquareToIndex(mv.to)].setHighlighted(false);
           //System.out.println(mv.from + "->" + mv.to);
       }
    }
    
    // như trên nhưng phiên bản có input là (int, (HashSet) Set<Move>)
    public void clearLegalMovesHighlight(HashSet<Move> move) {
       // di chuyển qua từng Move và tắt TilePanel tương ứng với Square
       for(Move mv : move) {
           this.boardTiles[this.fromSquareToIndex(mv.to)].setHighlighted(false);
           //System.out.println(mv.from + "->" + mv.to);
       }
    }
    
    // phương thức kết hợp hai phương thức tắt highlight
    public void clearHighlightTileNMove(int index) {
        clearTileHighlight(index);
        clearLegalMovesHighlight(index);
    }
    
    
    // Phương thức highlight 1 ô
    public void tileHighlight(int index) {
        this.getTilePanelWithIndex(index).setHighlighted(true);
    }
    
    // Phương thức highlight các nước đi hợp lệ
    // input là index
    public void legalMovesHighlight(int index) {
        // Lấy Set<Move> từ ô index
       HashSet<Move> move = 
               (HashSet<Move>) this.chessGame.getLegalMoves(this.fromTilePanelToSquare(index));
       
       // di chuyển qua từng Move và bật highlight tại TilePanel
       for(Move mv : move) {
           this.getTilePanelWithIndex(this.fromSquareToIndex(mv.to)).setHighlighted(true);
       }
    }
    
    // phương thức kết hợp hai phương thức highlight
    public void highlightTileNMove(int index) {
        // bật ô
        tileHighlight(index);
        // bật ô các nước đi hợp lệ
        legalMovesHighlight(index);
    }
    //======================
    
    //===== ABORT =====
    // phưởng thức hủy bỏ thao tác trên bàn cờ khi nhấn chuột phải
    protected void Abort() {
        // gán lại srcIndex
        this.sourceTile = null;
        
        // tắt highlight mọi ô
        this.clearAllHighlighted();
        
        // thông báo hàm được gọi
        System.out.println("Abort!");
    }
    //================
    
    //===== HELPER =====
    // GUI -> ENGINE
    // Phương thức để chuyển tọa độ mảng 1 chiều gui thành toạ độ bên engine
    public int toRow(int index) {
        return index / 8;
    }
    
    // nhứ trên nhưng là cột
    public int toCol(int index) {
        return index % 8;
    }
    
    // phương thức để chuyển đổi tọa độ trên bàn cờ gui thành tọa độ trong bàn cờ engine
    // chuyển sang mảng 2 chiều java rồi chuyển sang tọa độ engine
    // gui[64] -> [row][col] -> [FILE][RANK] 
    public Square fromTilePanelToSquare(int index) {
        // chuyển tọa độ 1 chiều thành 2 chiều trong java
        int row = toRow(index);
        int col = toCol(index);
        
        // chuyển mảng 2 chiều trong java thành FILE và RANK trong engine
        int FILE = col;
        int RANK = 7 - row;
        
        // trả về Square
        return Square.fromCoordinates(FILE, RANK);
    }
    
    // ENGINE -> GUI
    // Phương thức chuyển Square thành index
    public int fromSquareToIndex(Square square) {
        // FILE = square.x
        // RANK = square.y
       
        // chuyển FILE và RANK thành mảng 2 chiều
        // row = 7 - RANK do hai cấu trúc ngược nhau
        int row = 7 - square.y;//row = 7 - RANK
        // col = FILE do cùng cấu trúc mảng 
        int col = square.x; // coll = FILE
        
        // chuyển row và col thành mảng 1 chiều
        // di chuyển tới row 0, 8, 16, 24 và cộng với col
        return row * 8 + col;
    }
    
    public void clickDebug(int dest, Side currentSide) {
        System.out.println("[" + currentSide + "] đi: "
                + fromTilePanelToSquare(this.sourceTile.getIndex())
                + " → " + fromTilePanelToSquare(dest));
        if (this.boardTiles[dest].getSide() != null) {
            // kiểm tra side của quân cờ
            System.out.println("source[" + this.boardTiles[this.sourceTile.getIndex()].getSide().toString() + "] - dest["
                    + this.boardTiles[dest].getSide().toString() + "]");
        }
    }
    
    // lấy ra kí tự file, rank để tô cho ô cờ
    public String getFileLabel(int col) {
        return flip ? files[7 - col] : files[col];
    }

    // get rank label by visual row
    public String getRankLabel(int row) {
        return flip ? ranks[7 - row] : ranks[row];
    }
    
    // in ra index của ô cờ để kiểm tra
    public void printTilePanelIndex() {
        for(int i = 0; i < 64; ++i) {
            if(i % 8 == 0) System.out.println();
            System.out.print(this.boardTiles[i].getIndex() + " ");
        }
    }
    
    // hàm này để lấy ra TilePanel với tham số index
    public TilePanel getTilePanelWithIndex(int index) {
        int flipValue;
        // nếu bàn cờ không bị lật => flip = false
        if(!this.flip) flipValue = 0;
        else flipValue = 63;
        return this.boardTiles[Math.abs(flipValue - index)];
        /*
        *Cách hoạt động:
        *Bàn cờ trắng sẽ có index theo thức tự của mảng boardTiles
        *Bàn cờ đen sẽ bị ngược lại do giá trị index được gán ngược với index của boardTiles
        *Vd:
        *Trắng: boardTiles[0].getIndex() = |0 - 0|
        *Đen: boardTiles[0].getIndex() = |63 - 0| = 63
        */
    }
    //==================
    
    //===== ABSTRACT METHOD =====
    // hai bàn cờ con sẽ tự có handlerMovePiece
    public abstract void handlerMovePiece(TilePanel tile);
}
//===============================

/*
- Các trường hợp cần xử lý trong bàn cờ

TRƯỜNG HỢP CHUỘT TRÁI
TH1: Ô được chọn trống
- Không làm gì cả

TH2: Ô không trống
TH2.1: Ô đó có quân cùng màu
- Ô đó và các nước đi hợp lệ sẽ được highlight

TH2.2: Ô đó có quân khác màu
- Không làm gì

TH3: Đã chọn ở TH2.1 nhưng chọn tiếp một ô khác
TH3.1: Ô tiếp theo trống
- Thực hiện di chuyển, xóa highlight và kết thúc lượt

TH3.2: Ô tiếp theo không trống
TH3.2.1: Ô đó có quân cùng màu
- Xóa hightlight quân hiện tại và chuyển sang quân mới được chọn

TH3.2.2: Ô đó có quân khác màu
- Thực hiện ăn quân

TH4: nếu chọn cùng 1 ô
- hủy thao tác với ô có quân đó

TRƯỜNG HỢP CHUỘT PHẢI 
TH1: Nếu không các trường hợp chuột trái 
- Không làm gì cả

TH2: Nếu có hành động chuột trái
- Hủy bỏ mọi hành động hiện tại

    
    
Căn lề thì bôi đen đoạn cần căn rồi Alt + Shift + F
*/