package chess.gui.match.gamesidebar;

import chess.gui.match.gamesidebar.CountDownTimer;
import chess.gui.guiUtils;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/*
* Tạm thời chỉ dùng matchTimer
*/
public class CountDownTimerPanel extends JPanel {
    // đồng hồ đếm ngược của ván cờ và lượt đấu
    private CountDownTimer matchTimer;
    private CountDownTimer turnTimer;
    // Label để hiển thị thời gian
    private JLabel matchTimerLabel;
    private JLabel turnTimerLabel;
    private Timer timer;

    public CountDownTimerPanel(long matchTimer, long turnTimer) {
        // khởi tạo biến
        // panel
        this.setLayout(new GridLayout());
        this.setOpaque(false);
        this.setPreferredSize(guiUtils.CLOCK_FRAME_DIMENSION);
        //this.setVisible(true);
        
        // label
        this.matchTimerLabel = new JLabel("00:00", SwingConstants.CENTER);
        //this.turnTimerLabel = new JLabel("00:00", SwingConstants.CENTER);
        this.matchTimerLabel.setFont(guiUtils.CLOCK_FONT);
        //this.turnTimerLabel.setFont(guiUtils.CLOCK_FONT);
        
        // thêm label vào panel
        this.add(this.matchTimerLabel);
        //this.add(this.turnTimerLabel);
        
        // thiết lập đồng hồ
        this.matchTimer = new CountDownTimer();
        //this.turnTimer = new CountDownTimer();
        
        // đặt thời gian
        this.matchTimer.setDuration(matchTimer);
        //this.turnTimer.setDuration(turnTimer);
        
        // chạy dồng hồ
        this.matchTimer.start();
        //this.turnTimer.start();
        
        // thiết lập timer
        setTimer();
        
    }
    
    
    private void setTimer() {
        this.timer = new Timer(200, e -> {
            this.matchTimerLabel.setText(this.matchTimer.getFormattedRemainingTime());
            //this.turnTimerLabel.setText(this.turnTimer.getFormattedRemainingTime());
        });
        
        timer.start();
    }
    
}
