package gui.match.chessboard;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/*
- Lớp này dùng để khởi tạo và lưu trữ ảnh cho GUI
*/
public class Assets {

    // 🔹 White pieces
    public BufferedImage WK; // King
    public BufferedImage WQ; // Queen
    public BufferedImage WR; // Rook
    public BufferedImage WB; // Bishop
    public BufferedImage WN; // Knight
    public BufferedImage WP; // Pawn

    // 🔹 Black pieces
    public BufferedImage BK;
    public BufferedImage BQ;
    public BufferedImage BR;
    public BufferedImage BB;
    public BufferedImage BN;
    public BufferedImage BP;

    public Assets() {
        init();
    }
    
    public void init() {
        try {
            // White
            WK = loadImage("WK.png");
            WQ = loadImage("WQ.png");
            WR = loadImage("WR.png");
            WB = loadImage("WB.png");
            WN = loadImage("WN.png");
            WP = loadImage("WP.png");

            // Black
            BK = loadImage("BK.png");
            BQ = loadImage("BQ.png");
            BR = loadImage("BR.png");
            BB = loadImage("BB.png");
            BN = loadImage("BN.png");
            BP = loadImage("BP.png");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔥 helper method (cleaner)
//    private BufferedImage loadImage(String fileName) throws Exception {
//        return ImageIO.read(
//            Assets.class.getResource("/chess/gui/resources/" + fileName)
//        );
//    }

    private BufferedImage loadImage(String fileName) {
        try {
            return ImageIO.read(new File("chess/resources/" + fileName));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}