package chess.gui.match.chessboard;


import chess.gui.guiUtils;
import gui.match.chessboard.Assets;
import io.github.wolfraam.chessgame.ChessGame;
import io.github.wolfraam.chessgame.board.Square;
import io.github.wolfraam.chessgame.move.Move;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    
    // biến này để lưu vị trí hiện tại dùng trong quá trình di chuyển quân cờ
    protected Integer sourceTile;
    
    // biến này dùng để ngăn người dùng spam chuột, phím khi thực hiện di chuyển
    // protected boolean isProcessingMove = false;
    
    // Assets
    protected Assets image;

    // BIẾN DÙNG ĐỂ LƯU VÁN CỜ
    ChessGame chessGame;
    
    
    //###############################
    BoardPanel() {
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
        
    }
    
    
    // vẽ ô cờ, ô cờ không thay đổi dù lật ngược
    // chỉ vẽ gui chứ chưa thiết lập các giá trị cho TilePanel
    public void drawBoard() {
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
            TilePanel tile = new TilePanel(i, tileColor, this);
            
            // gán vào mảng TilePanel
            this.boardTiles[i] = tile;
            // đưa vào panel
            add(tile);
        }
    }
    
    //##############################################
    
    
    // phương thức thực hiện di chuyển quân
    public boolean movePiece(int source, int dest) {
        // map tọa độ index sang enigne
        Square from = this.fromTilePanelToSquare(source);
        Square to = this.fromTilePanelToSquare(dest);
        
        // tạo Move
        Move move = new Move(from, to);
        
        // kiểm tra move
        if(this.chessGame.isLegalMove(move)) {
            // nếu đúng thì di chuyển
            this.chessGame.playMove(move);
            return true;
        }
        // nếu sai thì báo sai
        return false;
    }
    
    
    public void printMove(int source, int dest) {
        System.out.println("MOVE: <" + source +" : " + dest + ">");
    }
    
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
        this.boardTiles[index].setHighlighted(true);
    }
    
    // Phương thức highlight các nước đi hợp lệ
    // input là index
    public void legalMovesHighlight(int index) {
        // Lấy Set<Move> từ ô index
       HashSet<Move> move = 
               (HashSet<Move>) this.chessGame.getLegalMoves(this.fromTilePanelToSquare(index));
       
       // di chuyển qua từng Move và bật highlight tại Square
       for(Move mv : move) {
           this.boardTiles[this.fromSquareToIndex(mv.to)].setHighlighted(true);
       }
    }
    
    // phương thức kết hợp hai phương thức highlight
    public void highlightTileNMove(int index) {
        // bật ô
        tileHighlight(index);
        // bật ô các nước đi hợp lệ
        legalMovesHighlight(index);
    }
    
    //########
    // phưởng thức hủy bỏ thao tác trên bàn cờ khi nhấn chuột phải
    protected void Abort() {
        // gán lại sourceTile
        this.sourceTile = null;
        
        // tắt highlight mọi ô
        this.clearAllHighlighted();
        
        // thông báo hàm được gọi
        System.out.println("Abort!");
    }
    
    //########
    //----HELPER-----
    // GUI -> ENGINE
    // Phương thức để chuyển tọa độ mảng 1 chiều gui thành toạc độ bên engine
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
    
    //####################################
    // hai bàn cờ con sẽ tự có phương thức setPieceImage
    public abstract void setPieceImage();
    
    // hai bàn cờ con sẽ tự có handlerMovePiece
    public abstract void handlerMovePiece(int tileID);
}

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

    
    

*/