package chess.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;



public class guiUtils {
    // FRAME GAME - KHUNG GAME
    public static final int FRAME_HEIGHT = 720;
    public static final int FRAME_WIDTH = 960;
    // (w, h)
    public static final Dimension OUTER_FRAME_DIMENSION = new Dimension(FRAME_WIDTH, FRAME_HEIGHT);
    
    // BOARDPANEL - BÀN CỜ
    public static final int BOARD_SIZE = 528;
    public static final Dimension BOARD_PANEL_DIMENSION = new Dimension(BOARD_SIZE, BOARD_SIZE);
    
    // KHUNG TÊN NGƯỜI CHƠI
    public static final int USER_FRAME_HEIGHT = 40;
    public static final int USER_FRAME_WIDTH = 528;
    public static final Dimension USER_FRAME_DIMENSION = 
            new Dimension(USER_FRAME_WIDTH, USER_FRAME_HEIGHT);
    
    // KHUNG CÁC TÍNH NĂNG NHƯ CHAT, THỜI GIAN, XIN HÒA, NƯỚC ĐI, ...
    public static final int MATCH_INFO_FRAME_HEIGHT = 662;
    public static final int MATCH_INFO_FRAME_WIDTH = 352;
    public static final Dimension MATCHINFO_FRAME_DIMENSION = 
            new Dimension(MATCH_INFO_FRAME_WIDTH, MATCH_INFO_FRAME_HEIGHT);
    
    // CÁC KHOẢNG TRẮNG GIỮA KHUNG TÊN NGƯỜI DÙNG VÀ KHUNG SIDEBAR
    public static final int USER_BOARD_GAP = 27;
    public static final int BOARD_SIDEBAR_GAP = 20;
    
    // KHUNG ĐỒNG HỒ ĐẾM GIỜ(TẠM THỜI)
    public static final int CLOCK_FRAME_HEIGHT = USER_FRAME_HEIGHT;
    public static final int CLOCK_FRAME_WIDTH = 177;
    public static final Dimension CLOCK_FRAME_DIMENSION = 
            new Dimension(CLOCK_FRAME_WIDTH, CLOCK_FRAME_HEIGHT);
    
    // phông chữ đồng hồ
    public static final Font CLOCK_FONT = new Font("Arial", Font.BOLD, 30);
    
    // màu của khung chứa đồng hồ
    public static final Color CLOCK_BG = new Color(240, 240, 240);
    
    // khung chứa 2 nút đầu hàng và hòa
    public static final Dimension BUTTON_FRAME_DIMENSION = new Dimension(352, 97);
    
    // TILE PANEL
    public static final int TILE_SIZE = 66; 
    public static final Dimension TILE_PANEL_DIMENSION = new Dimension(TILE_SIZE, TILE_SIZE);
    
    // Tile Colors
    public static final Color LIGHT_TILE = new Color(240, 217, 181);
    public static final Color DARK_TILE = new Color(181, 136, 99);

    // màu xanh biển nhạt
    public static final Color HIGHLIGHT = new Color(100, 180, 255, 90);
    
    // thời lượng một ván cờ, 10'
    public static final long MATCH_DURATION = 10 * 60 * 1000;
    // thời lượng mỗi lượt trong ván đấu, 30s
    public static final long TURN_DURATION = 30 * 1000;
    
    // màu background giống chess.com
    public static final Color MATCH_BG = new Color(49, 46, 43);
    
    // hai nút đầu hàng và hòa
    public static final Font MATCH_BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 13);
}
