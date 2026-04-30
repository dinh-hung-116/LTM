package chess.network;

/**
 * NetworkConfig - Cấu hình mạng tập trung.
 *
 * Tất cả IP/port đều nằm ở đây, chỉ cần đổi một chỗ khi triển khai.
 * Client đọc SERVER_IP và SERVER_PORT để kết nối.
 * Server lắng nghe trên SERVER_PORT.
 */
public final class NetworkConfig {

    // ── Địa chỉ server ────────────────────────────────────────
    // Đổi thành IP thật của máy chạy server khi deploy LAN
    public static final String SERVER_IP   = "127.0.0.1";
    public static final int    SERVER_PORT = 5000;

    // ── Cấu hình kết nối ──────────────────────────────────────
    // Số client tối đa trong hàng đợi accept() của ServerSocket
    public static final int    BACKLOG         = 50;
    // Timeout đọc socket (ms) – tránh thread bị treo mãi
    public static final int    SOCKET_TIMEOUT  = 0;   // 0 = không timeout
    // Thời gian chờ xác nhận ghép trận (ms)
    public static final int    CONFIRM_TIMEOUT = 30_000;

    // ── Giao thức tin nhắn (Client ↔ Server) ─────────────────
    //
    // --- Đăng nhập / LOGIN---
    // Client gửi ngay sau khi kết nối thành công
    // loại gói tin
    public static final String LOGIN_REQUEST = "LOGIN_REQUEST";
    public static final String LOGIN_RESPONSE = "LOGIN_RESPONSE";
    // Server xác nhận đăng nhập thành công
    public static final String LOGIN_OK       = "LOGIN_OK";
    // Server xác nhận đăng nhập không thành công
    public static final String LOGIN_FAIL = "LOGIN_FAIL";
    
    // --- ĐĂNG KÝ / REGISTER ---
    public static final String REGISTER_REQUEST = "REGISTER_REQUEST";
    public static final String REGISTER_RESPONSE = "REGISTER_RESPONSE";

    public static final String REGISTER_OK = "REGISTER_OK";
    public static final String REGISTER_FAIL = "REGISTER_FAIL";
    
    // --- ĐĂNG XUẤT / LOGOUT ---
    public static final String LOGOUT_REQUEST = "LOGOUT_REQUEST";
    public static final String LOGOUT_RESPONSE = "LOGOUT_RESPONSE";

    public static final String LOGOUT_OK = "LOGOUT_OK";
    //public static final String LOGOUT_FAIL = "LOGOUT_FAIL";

    // --- Tìm trận ---
    public static final String FIND_MATCH      = "FIND_MATCH";      // client → server: vào hàng đợi
    public static final String CANCEL_FIND     = "CANCEL_FIND";     // client → server: hủy tìm
    public static final String QUEUED          = "QUEUED";          // server → client: đã vào queue
    public static final String MATCH_FOUND     = "MATCH_FOUND";     // server → client: "MATCH_FOUND:tenDoiThu"
    public static final String CONFIRM_MATCH   = "CONFIRM_MATCH";   // client → server: đồng ý
    public static final String REJECT_MATCH    = "REJECT_MATCH";    // client → server: từ chối
    public static final String MATCH_CONFIRMED = "MATCH_CONFIRMED"; // server → client: bắt đầu ván
    public static final String MATCH_REJECTED  = "MATCH_REJECTED";  // server → client: đối thủ từ chối
    public static final String MATCH_TIMEOUT   = "MATCH_TIMEOUT";   // server → client: hết giờ xác nhận

    // --- Trong ván đấu ---
    // Server gửi "ASSIGN:WHITE" hoặc "ASSIGN:BLACK"
    // getCommand() trả về "ASSIGN", getData() trả về "WHITE" hoặc "BLACK"
    public static final String ASSIGN          = "ASSIGN";      // command part
    public static final String ASSIGN_WHITE    = "ASSIGN:WHITE"; // full message (để GameSession.start() dùng)
    public static final String ASSIGN_BLACK    = "ASSIGN:BLACK"; // full message
    public static final String SIDE_WHITE      = "WHITE";       // data part
    public static final String SIDE_BLACK      = "BLACK";       // data part
    public static final String MOVE            = "MOVE";            // "MOVE:e2-e4"
    public static final String MOVE_OK         = "MOVE_OK";         // server xác nhận nước đi hợp lệ
    public static final String MOVE_INVALID    = "MOVE_INVALID";    // server từ chối nước đi

    // --- Đồng hồ (server điều phối để 2 client đồng bộ) ---
    // Server gửi sau nước đi đầu tiên: bắt đầu đồng hồ
    public static final String CLOCK_START  = "CLOCK_START";
    // Server gửi sau mỗi nước đi: chuyển đồng hồ sang bên kia
    // Format: "CLOCK_SWITCH:WHITE" hoặc "CLOCK_SWITCH:BLACK" (bên vừa đi xong)
    public static final String CLOCK_SWITCH = "CLOCK_SWITCH";

    // --- Kết thúc ván ---
    public static final String RESIGN          = "RESIGN";          // client → server: đầu hàng
    public static final String DRAW_OFFER      = "DRAW_OFFER";      // client → server: xin hòa
    public static final String DRAW_ACCEPT     = "DRAW_ACCEPT";     // client → server: chấp nhận hòa
    public static final String DRAW_REJECT     = "DRAW_REJECT";     // client → server: từ chối hòa
    public static final String GAME_OVER       = "GAME_OVER";       // server → client: "GAME_OVER:WHITE_WINS"
    public static final String PENALTY         = "PENALTY";         // server → client: "PENALTY:-1"

    // --- Kết quả ván (dùng trong GAME_OVER) ---
    public static final String WHITE_WINS      = "WHITE_WINS";
    public static final String BLACK_WINS      = "BLACK_WINS";
    public static final String DRAW_RESULT     = "DRAW";

    // ── Separator dùng để tách lệnh và dữ liệu ───────────────
    // VD: "MOVE:e2-e4"  →  split(SEP) → ["MOVE", "e2-e4"]
    public static final String SEP = ":";

    // Không cho phép khởi tạo
    private NetworkConfig() {}

    // ── Helper: tạo chuỗi lệnh có dữ liệu ───────────────────
    public static String msg(String command, String data) {
        return command + SEP + data;
    }

    // ── Helper: lấy command từ chuỗi ─────────────────────────
    public static String getCommand(String raw) {
        int idx = raw.indexOf(SEP);
        return idx == -1 ? raw : raw.substring(0, idx);
    }

    // ── Helper: lấy data từ chuỗi ────────────────────────────
    public static String getData(String raw) {
        int idx = raw.indexOf(SEP);
        return idx == -1 ? "" : raw.substring(idx + 1);
    }
}