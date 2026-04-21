===================================================
  HƯỚNG DẪN CẬP NHẬT - đặt file vào đúng thư mục
===================================================

Cấu trúc gốc của dự án: chess/

----------------------------------------------------
1. SideBar.java
   → chess/gui/match/matchinfo/SideBar.java
   Thay thế file cũ.

2. BoardPanel.java
   → chess/gui/match/chessboard/BoardPanel.java
   Thay thế file cũ.

3. LocalBoardPanel.java
   → chess/gui/match/chessboard/LocalBoardPanel.java
   Thay thế file cũ.

4. MatchPanel.java
   → chess/gui/match/MatchPanel.java
   Thay thế file cũ.

5. PromotionDialog.java  (FILE MỚI)
   → chess/gui/match/chessboard/PromotionDialog.java
   Thêm mới, chưa có trong dự án.

----------------------------------------------------
TÍNH NĂNG ĐÃ THÊM:

- Move history: hiển thị nước đi theo kiểu chess.com
  (bảng Trắng / Đen, có thanh cuộn, tự cuộn xuống cuối)

- Phong tốt: khi tốt đến hàng cuối, dialog hiện ra
  cho chọn Hậu / Xe / Tượng / Mã rồi tự cập nhật bàn cờ
===================================================
