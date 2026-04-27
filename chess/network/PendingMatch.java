package com.chess.network;

import java.util.Timer;
import java.util.TimerTask;

/**
 * PendingMatch - Trạng thái chờ xác nhận ghép trận giữa 2 client.
 *
 * Được tạo khi server tìm được 2 người trong queue.
 * Tự hủy (callback onTimeout) nếu không xác nhận trong CONFIRM_TIMEOUT ms.
 */


public class PendingMatch {

    private final ClientHandler player1;
    private final ClientHandler player2;

    private volatile boolean p1Confirmed = false;
    private volatile boolean p2Confirmed = false;
    private volatile boolean resolved    = false; // đã xử lý (thành công hoặc hủy)

    private final Timer confirmTimer;

    // Callback khi timeout
    public interface TimeoutCallback {
        void onTimeout(PendingMatch match);
    }

    // ─────────────────────────────────────────────────────────
    public PendingMatch(ClientHandler player1, ClientHandler player2,
                        TimeoutCallback onTimeout) {
        this.player1 = player1;
        this.player2 = player2;

        // Đếm ngược CONFIRM_TIMEOUT ms
        confirmTimer = new Timer(true); // daemon timer
        confirmTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!resolved) {
                    onTimeout.onTimeout(PendingMatch.this);
                }
            }
        }, NetworkConfig.CONFIRM_TIMEOUT);
    }

    // ─────────────────────────────────────────────────────────
    // XÁC NHẬN
    // ─────────────────────────────────────────────────────────
    /**
     * Đánh dấu một bên đã bấm xác nhận.
     * @return true nếu CẢ HAI đã xác nhận → có thể bắt đầu ván.
     */
    public synchronized boolean confirm(ClientHandler who) {
        if (resolved) return false;
        if (who == player1) p1Confirmed = true;
        if (who == player2) p2Confirmed = true;
        return p1Confirmed && p2Confirmed;
    }

    // ─────────────────────────────────────────────────────────
    // HỦY (từ chối / timeout)
    // ─────────────────────────────────────────────────────────
    /** Đánh dấu match này đã được xử lý, hủy timer. */
    public synchronized void resolve() {
        resolved = true;
        confirmTimer.cancel();
    }

    public synchronized boolean isResolved() { return resolved; }

    // ─────────────────────────────────────────────────────────
    // GETTERS
    // ─────────────────────────────────────────────────────────
    public ClientHandler getPlayer1() { return player1; }
    public ClientHandler getPlayer2() { return player2; }

    public boolean hasPlayer(ClientHandler c) {
        return c == player1 || c == player2;
    }

    /** Bên chưa xác nhận (để phạt khi timeout). */
    public ClientHandler getUnconfirmed() {
        if (!p1Confirmed) return player1;
        if (!p2Confirmed) return player2;
        return null;
    }
}