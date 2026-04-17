package com.chess.gui.match.matchinfo;

import com.chess.gui.match.matchinfo.CountDownTimer;
import com.chess.gui.guiUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

//
//package chess.gui.match.matchinfo;
//
//import chess.gui.match.matchinfo.CountDownTimer;
//import chess.gui.guiUtils;
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Font;
//import java.awt.GridLayout;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.SwingConstants;
//import javax.swing.Timer;
/*
* Tạm thời chỉ dùng matchTimer
*/
public class CountDownTimerPanel extends JPanel {
    // đồng hồ đếm ngược của người chơi
    private CountDownTimer matchTimer;
    // Label để hiển thị thời gian
    private JLabel matchTimerLabel;
    private Timer timer;

    // construtor chỉ khởi tạo và thiết lập đồng hồ chứ chưa chạy
    public CountDownTimerPanel(long matchTimer) {
        // khởi tạo biến
        // panel
        this.setLayout(new GridLayout());
        this.setPreferredSize(guiUtils.CLOCK_FRAME_DIMENSION);
        this.setBackground(guiUtils.CLOCK_BG);
        
        // thiết lập đồng hồ
        this.matchTimer = new CountDownTimer();
        
        // đặt thời gian
        this.matchTimer.setDuration(matchTimer);
        
        // label
        this.matchTimerLabel = new JLabel(
                this.matchTimer.getFormattedRemainingTime(), SwingConstants.CENTER);
        this.matchTimerLabel.setFont(guiUtils.CLOCK_FONT);
        
        this.add(this.matchTimerLabel);
        
        // thiết lập timer
        setTimer();
    }
    
    // thiết lập timer 
    private void setTimer() {
        this.timer = new Timer(200, e -> {
            // kiểm tra nếu đồng hồ đang chạy
            if(this.matchTimer.isRunning()) {
                // cập nhật đồng hồ
                this.matchTimerLabel.setText(this.matchTimer.getFormattedRemainingTime());
            }
            // nếu đang ngừng thì ngưng hiển thị
            else {
                // tạm thời ngưng lambda lại
                timer.stop();
            }
        });
    }
    
    // phương thức để các lớp khác tương tác với đồng hồ
    // bắt đầu chạy mới hoặc từ lức tạm dừng
    public void startTimer() {
        matchTimer.start();
        timer.start();
    }
    
    // tạm dừng
    public void pauseTimer() {
        matchTimer.stop();;
        timer.stop();
    }
    
    // làm mới đồng hồ
    public void resetTimer() {
        matchTimer.reset();
        matchTimerLabel.setText(matchTimer.getFormattedRemainingTime());
        timer.stop();
    }
    
    
    
}
