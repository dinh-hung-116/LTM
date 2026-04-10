package chess.gui.Class;


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
    
    //######################################
    public TilePanel(int index, Color tileColor, BoardPanel boardPanel) {
        super(new GridLayout());
        
        this.index = index;
        this.tileColor = tileColor;
        this.isHighlighted = false;
        this.pieceImage = null;
        this.boardPanel = boardPanel;
        
        // khởi tạo kích thước
        this.setPreferredSize(guiUtils.TILE_PANEL_DIMENSION);
        
        // Xử lý hành động click chuột
        // click chuột trái sẽ highlight , chuột phải sẽ clear mọi thứ
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // chuột trái
                if (SwingUtilities.isLeftMouseButton(e)) {
                    
                } 
                // chuột phải
                else {
                    // dừng mọi hành động

                }
            }
        });
        
    }
    
    // Lấy index của ô cờ
    public int getIndex() {
        return this.index;
    }
    
    // Đặt ảnh cho quân cờ
    public void setPieceImage(BufferedImage pieceImage) {
        this.pieceImage = pieceImage;
        repaint();
    }

    // Lấy ảnh của quân cờ trong ô hiện tại
    public BufferedImage getPieceImage() {
        return pieceImage;
    }
    
   
    // Xóa ảnh quân cờ
    public void clearPieceImage() {
        this.pieceImage = null;
    }

    // highlight ô cờ
    public void setHighlighted(boolean highlighted) {
        this.isHighlighted = highlighted;
        repaint();
    }

    // đưa ô cờ về lại trạng thái ban đầu(chỉ có màu)
    public void clear() {
        this.pieceImage = null;
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
        }
    }
}
