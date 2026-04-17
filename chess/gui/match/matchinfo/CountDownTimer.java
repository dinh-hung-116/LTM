package com.chess.gui.match.matchinfo;
//package chess.gui.match.matchinfo;

// lớp dùng đồng hồ của hệ thống để chạy giờ
public class CountDownTimer {

    private long startTime;     // thời gian bắt đầu chạy
    private long elapsedTime;   // thời lượng tính từ lúc bắt đầu
    private long duration; // thời lượng đếm ngược. Vd: 10', 5', .....
    private boolean running;

    public CountDownTimer() {
        elapsedTime = 0;
        running = false;
    }

    // Start or resume
    public void start() {
        if (!running) {
            startTime = System.currentTimeMillis();
            running = true;
        }
    }

    // Pause
    // stop wor đây là tạm dừng đồng hồ. Mỗi khi gọi start thì đồng hồ sẽ 
    // chạy tiếp mà không ảnh hưởng tới thời gian đã lưu là elapsedTime
    
    public void stop() {
        if (running) {
            elapsedTime += System.currentTimeMillis() - startTime;
            running = false;
        }
    }

    // Reset everything
    public void reset() {
        elapsedTime = 0;
        running = false;
    }

    // set duration
    public void setDuration(long duration) {
        this.duration = duration;
    }
    
    // Remaining time
    // thời lượng còn lại. Vd: 1' chạy 10s thì còn lại 50s
    // tính bằng cách lấy thời lượng - thời gian đã trôi qua 
    public long getRemainingTime() {
        long timePassed = elapsedTime;

        // do elapsedTime chỉ được cập nhật khi gọi hàm stop() nên 
        // biểu thức System..... - startTime là để tính thời gian đã trôi qua
        if (running) {
            timePassed += System.currentTimeMillis() - startTime;
        }
        
        // do đồng hồ chỉ thực sự tạm dừng khi stop() được gọi nên có dòng này để 
        // nếu thời gian đã chạy hết duration thì sẽ vẫn chỉ in 00:00 chú không có số âm như 00:-1
        long remaining = duration - timePassed;
        return Math.max(0, remaining); // avoid negative
    }
    
    // Get total elapsed time (ms)
    public long getElapsedTime() {
        if (running) {
            return elapsedTime + (System.currentTimeMillis() - startTime);
        }
        return elapsedTime;
    }

    // Format thời gian theo định dạng tăng dần m:ss
    public String getFormattedTime() {
        long time = getElapsedTime();

        long minutes = (time / 60000) % 60;
        long seconds = (time / 1000) % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }
    
    // Format thời gian theo định dạng giảm dần mm:ss
    public String getFormattedRemainingTime() {
        long time = getRemainingTime(); // lấy thời gian còn lại

        long minutes = (time / 60000);
        long seconds = (time / 1000) % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    // hàm để kiểm tra xem đồng hồ có chạy không
    // nếu false thì có khả năng đang tạm dừng hoặc đã dừng hẳn
    public boolean isRunning() {
        return running;
    }
    
    // nếu hết giờ -> ngưng
    public boolean isTimeUp() {
        return this.getRemainingTime() <= 0;
    }
    
    public static void main(String[] args) throws InterruptedException {
        CountDownTimer  clock = new CountDownTimer();
        clock.setDuration(10*1000);
        clock.start();
        
        System.out.println(clock.getFormattedRemainingTime());
        while(true) {
            System.out.println(clock.getFormattedRemainingTime());
            Thread.sleep(1000);
            
            if(clock.isTimeUp()) break;
        }
    }
}