package com.chess.gui.ui.lobby;

import com.chess.gui.guiUtils;

import java.awt.*;

// Giữ lại để tương thích – tất cả hằng số giờ nằm trong guiUtils
final class LobbyColors {
    static final Color BG         = guiUtils.LOBBY_SIDEBAR;
    static final Color CARD       = guiUtils.LOBBY_CARD;
    static final Color CARD_DARK  = new Color(240, 240, 240);
    static final Color BORDER_CLR = guiUtils.LOBBY_BORDER;
    static final Color HEADER_BG  = new Color(36, 34, 31);
    static final Color GREEN      = guiUtils.LOBBY_GREEN;
    static final Color WHITE      = guiUtils.LOBBY_SIDEBAR_TEXT;
    static final Color GRAY       = guiUtils.LOBBY_SIDEBAR_SUB;
    static final Color HOVER      = guiUtils.LOBBY_SIDEBAR_HOVER;

    static Font bold(int size)   { return new Font("Segoe UI", Font.BOLD,   size); }
    static Font plain(int size)  { return new Font("Segoe UI", Font.PLAIN,  size); }
    static Font italic(int size) { return new Font("Segoe UI", Font.ITALIC, size); }

    private LobbyColors() {}
}
