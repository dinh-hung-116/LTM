package com.chess.gui;

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
    // màu đỏ nhạt
    public static final Color CHECK_HIGHLIGHT = new Color(235, 97, 80, 180);
    
    // thời lượng một ván cờ, 10'
    public static final long MATCH_DURATION = 10 * 60 * 1000;
    // thời lượng mỗi lượt trong ván đấu, 30s
    public static final long TURN_DURATION = 30 * 1000;
    
    // màu background giống chess.com
    public static final Color MATCH_BG = new Color(49, 46, 43);
    
    // hai nút đầu hàng và hòa
    public static final Font MATCH_BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 13);
    
    // side bar trong menu
    public static final int SIDE_BAR_HEIGHT = 720;
    public static final int SIDE_BAR_WIDTH = 250;
    public static final Dimension SIDE_BAR_DIMENSION = 
            new Dimension(SIDE_BAR_WIDTH, SIDE_BAR_HEIGHT); 
    
    // màu của side bar trong menu, giống màu bên chess.com
    //public static final Color MENU_SIDE_BAR_COLOR = new Color(30, 28, 26);
    
    // ═══════════════════════════════════════════════════════════
    // LOBBY (màn hình chờ – sidebar + content)
    // ═══════════════════════════════════════════════════════════

    // Kích thước
    public static final int SIDEBAR_WIDTH  = 250;
    public static final int CONTENT_WIDTH  = FRAME_WIDTH - SIDEBAR_WIDTH; // 710

    // Màu nền
    public static final Color LOBBY_SIDEBAR     = new Color(49,  46,  43);  // sidebar tối (= MATCH_BG)
    public static final Color LOBBY_CONTENT_BG  = new Color(217, 217, 217); // nền sáng bên phải (#D9D9D9)
    public static final Color LOBBY_CARD        = new Color(255, 255, 255); // card trắng
    public static final Color LOBBY_CARD_HOVER  = new Color(245, 245, 245); // hover nhẹ

    // Màu chữ (dùng trên nền sáng)
    public static final Color LOBBY_TEXT        = new Color(25,  23,  21);  // chữ tối
    public static final Color LOBBY_SUBTEXT     = new Color(110, 108, 105); // chữ phụ

    // Màu chữ sidebar (dùng trên nền tối)
    public static final Color LOBBY_SIDEBAR_TEXT = new Color(230, 230, 230);
    public static final Color LOBBY_SIDEBAR_SUB  = new Color(155, 155, 155);

    // Viền & hover
    public static final Color LOBBY_BORDER       = new Color(210, 208, 205);
    public static final Color LOBBY_SIDEBAR_HOVER = new Color(62, 59, 56);

    // Accent (chess.com green)
    public static final Color LOBBY_GREEN        = new Color(129, 187,  65);
    public static final Color LOBBY_GREEN_DARK   = new Color(100, 150,  48); // hover nút xanh

    // Font chữ lobby
    public static final Font LOBBY_FONT_TITLE = new Font("Segoe UI", Font.BOLD,  17);
    public static final Font LOBBY_FONT_H2    = new Font("Segoe UI", Font.BOLD,  14);
    public static final Font LOBBY_FONT_BODY  = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font LOBBY_FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font LOBBY_FONT_BOLD  = new Font("Segoe UI", Font.BOLD,  13);
    
    
}
