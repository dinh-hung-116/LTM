package chess.gui.match.chessboard;


import chess.gui.guiUtils;
import io.github.wolfraam.chessgame.board.Side;
import io.github.wolfraam.chessgame.board.Square;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/*
- TilePanel sẽ kế thừa lớp JPanel để hiển thị ô + quân cờ và xử lý click chuột

- Trong engine quan trọng nhất là lớp Square, cần Square để biết tọa độ để truyền vào hàm.
phương thức getLegalMoves cũng cần Square để lấy quân cờ tại ô đó và trả về nước hợp lệ

*/
public class TilePanel extends JPanel {
    // tọa độ của ô cờ theo boardPanel gridlayout. VD: 0, 1, 2, ....
    private int index;
    
    // màu của tile
    private Color tileColor;
    
    // ảnh của quân cờ(nếu không có thì là null)
    private BufferedImage pieceImage;
    
    // biến để kiểm soát viejeecj hightlight quân cờ
    private boolean isHighlighted;
    
    // BoardPanel để Tile tạo yêu cầu cho bàn cờ khi xử lý chuột
    private BoardPanel boardPanel;
    
    // dùng để kiểm tra màu của quân cờ nếu ô có tồn tại quân cờ
    private Side side;
    
    //######################################
    public TilePanel(int index, Color tileColor, BoardPanel boardPanel) {
        super(new GridLayout());
        
        this.index = index;
        this.tileColor = tileColor;
        this.isHighlighted = false;
        this.pieceImage = null;
        this.boardPanel = boardPanel;
        this.side = null;
        
        // khởi tạo kích thước
        this.setPreferredSize(guiUtils.TILE_PANEL_DIMENSION);
        
        // Xử lý hành động click chuột
        // click chuột trái sẽ highlight , chuột phải sẽ clear mọi thứ
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // chuột trái
                if (SwingUtilities.isLeftMouseButton(e)) {
                   boardPanel.handlerMovePiece(index);
                } 
                // chuột phải
                else {
                    // dừng mọi hành động
                    // tạm thời xóa highligh ở ô dược chọn
                    boardPanel.clearHighlightTileNMove(index);

                }
            }
        });
        
    }
    
    // Lấy index của ô cờ
    public int getIndex() {
        return this.index;
    }
    
    // lấy Side(màu) của quân trong ô cờ
    public Side getSide() {
        return this.side;
    }
    
    // lấy isHighlighted
    public boolean getIsHighlighted() {
        return this.isHighlighted;
    }
    
    // Đặt ảnh cho quân cờ
    public void setPieceImage(BufferedImage pieceImage, Side side) {
        this.pieceImage = pieceImage;
        this.side = side;
        repaint();
    }

    // Lấy ảnh của quân cờ trong ô hiện tại
    public BufferedImage getPieceImage() {
        return pieceImage;
    }

    // highlight ô cờ
    public void setHighlighted(boolean highlighted) {
        this.isHighlighted = highlighted;
        repaint();
    }

    // đưa ô cờ về lại trạng thái ban đầu(ô trống)
    public void clear() {
        this.pieceImage = null;
        this.side = null;
        this.isHighlighted = false;
        repaint();
    }
    
    // hàm kiểm tra nếu ô trống -> pieceImage = null
    public boolean isEmpty() {
        return pieceImage == null;
    }
    
    // Hàm này sẽ được chạy khi phương thức paint hoặc repaint được gọi
    @Override
    protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // 1. Tô màu ô trước
            g.setColor(tileColor);
            g.fillRect(0, 0, getWidth(), getHeight());

            // 2. Highlight ô cờ nếu có(isHighlighted == true)
            if (isHighlighted) {
                g.setColor(guiUtils.HIGHLIGHT); // màu theo guiUitls
                g.fillRect(0, 0, getWidth(), getHeight());
            }

            // 3. Tô màu quân cờ nếu có
            if (pieceImage != null) {
                g.drawImage(pieceImage, 0, 0, getWidth(), getHeight(), this);
                /*
                int topPadding = 4;
                int sidePadding = 4;
                int bottomPadding = 10; // extra space for file letters

                g.drawImage(
                    pieceImage,
                    sidePadding,
                    topPadding,
                    getWidth() - 2 * sidePadding,
                    getHeight() - topPadding - bottomPadding,
                    this
                );
*/
            }
            
            // 4. vẽ tọa độ(file và rank)
            //drawNotation(g);
        }

        private void drawNotation(Graphics g) {
        int row = index / 8;
        int col = index % 8;

        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));

        // 🎯 Rank (1–8) → only left column
        if (col == 0) {
            String rank = String.valueOf(8 - row);
            g.setColor(getContrastColor());
            g.drawString(rank, 5, 15);
        }

        // 🎯 File (a–h) → only bottom row
        if (row == 7) {
            char fileChar = (char) ('a' + col);
            String file = String.valueOf(fileChar);
            g.setColor(getContrastColor());
            g.drawString(file, getWidth() - 15, getHeight() - 5);
        }
    }
        
    private Color getContrastColor() {
        int brightness = (tileColor.getRed() + tileColor.getGreen() + tileColor.getBlue()) / 3;

        // softer colors instead of pure black/white
        return brightness < 128 
            ? new Color(240, 240, 240)  // off-white
            : new Color(60, 60, 60);    // dark gray
    }
}
