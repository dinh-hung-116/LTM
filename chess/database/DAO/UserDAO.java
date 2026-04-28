package com.chess.database.DAO;

import com.chess.database.Class.User;
import java.util.ArrayList;
import java.sql.*;
import java.time.LocalDate;


public class UserDAO {
    // Lấy tất cả người dùng trong bảng
    public static ArrayList<User> getAllUser() {
        String sql = "SELECT * FROM user;";
        ArrayList<User> result = new ArrayList<>();
        
        try (
                // Thiết lập kết nối -> chuẩn bị câu truy vấn -> chạy và lấy kết quả
                Connection conn = SQLiteConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            
            // Lấy dữ liệu và đưa vào arraylist
            while(rs.next()) {
                result.add(mapResultSetToUser(rs));
            }
            
            return result;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    // Lấy người dùng theo ID
    public static User getUserbyID(int ID) {
        String sql = "SELECT * FROM user where user_id = ?;";
        User result = null;
        
        try (
                // Thiết lập kết nối -> chuẩn bị câu truy vấn -> chạy và lấy kết quả
                Connection conn = SQLiteConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
                
            // gán dữ liệu(dấu chấm hỏi) 
            ps.setInt(1, ID);
            
            ResultSet rs = ps.executeQuery();
            
            // Lấy dữ liệu và trả về User, do kết quả chỉ có một bản ghi nên không cần while
            if(rs.next()) {
                result = mapResultSetToUser(rs);
                
                // giải phóng tài nguyên
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    // Thêm người dùng mới 
    // Lệnh này có thay đổi cấu trúc bảng nên dùng executeUpdate() chứ không phải executeQuery()
    // vì một lí do gì đó mà việc insert thông qua method này không cần user_ID nhưng trong csdl thì vẫn cần?:]
    public static boolean insertUser(User user) {
        String sql = "INSERT INTO user(user_name, password_hash, full_name, gender, date_of_birth)" +
                "VALUES(?, ?, ?, ?, ?);";
        
        try (
                // Thiết lập kết nối -> chuẩn bị câu cập nhật -> chạy và lấy kết quả
                Connection conn = SQLiteConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
                
            // gán dữ liệu(dấu chấm hỏi) 
            ps.setString(1, user.getUserName());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getGender());
            // setString vì kiểu dữ liệu DATE trong SQLite có thể là kiểu TEXT, INTEGER, REAL nên ép theo TEXt
            // cho dễ quản lý và chuyển đổi. "yyyy-mm-dd"
            ps.setString(5, user.getDateOfBirth().toString());
            
            // Chạy lệnh và trả kết quả. lệnh trả về số nguyên là số dòng hay bản ghi bị tác động
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Cập nhật thông tin người dùng
    // cập nhật lại tất cả thông tin để cho dễ làm
    public static boolean updateUser(User user) {
        String sql = "UPDATE user SET user_name=?, password_hash=?, full_name=?, gender=?, date_of_birth=? "
                + "WHERE user_id=?";
        
        try (
                // Thiết lập kết nối -> chuẩn bị câu cập nhật -> chạy và lấy kết quả
                Connection conn = SQLiteConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
                
            // gán dữ liệu(dấu chấm hỏi) 
            ps.setString(1, user.getUserName());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getGender());
            // setString vì kiểu dữ liệu DATE trong SQLite có thể là kiểu TEXT, INTEGER, REAL nên ép theo TEXt
            // cho dễ quản lý và chuyển đổi. "yyyy-mm-dd"
            ps.setString(5, user.getDateOfBirth().toString());
            
            // WHERE
            ps.setLong(6, user.getUserID());
            
            // Chạy lệnh và trả kết quả. lệnh trả về số nguyên là số dòng hay bản ghi bị tác động
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Xóa người dùng theo ID
    public boolean deleteUser(long id) {
        String sql = "DELETE FROM user WHERE user_id=?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Kiểm tra đăng nhập
    // nếu đúng thì sẽ trả vể đối tượng User
    public static User checkLogin(String userName, String passwordHash) {
        String sql = "SELECT * FROM user WHERE user_name = ? AND password_hash = ?;";
        User result = null;
        
        try (
                Connection conn = SQLiteConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
                
            ps.setString(1, userName);
            ps.setString(2, passwordHash);
            
            ResultSet rs = ps.executeQuery();
            
            // Nếu có kết quả tức là tài khoản/mật khẩu hợp lệ
            if(rs.next()) {
                result = mapResultSetToUser(rs);
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }

    public static boolean registerUser(User user, int initialElo) {
    String insertUserSql = "INSERT INTO user(user_name, password_hash, full_name, gender, date_of_birth) VALUES(?, ?, ?, ?, ?);";
    String insertStatsSql = "INSERT INTO user_stats(user_id, total_games, wins, losses, draws, winning_streak, losing_streak, elo, win_rate) VALUES(?, 0, 0, 0, 0, 0, 0, ?, 0.0);";

    Connection conn = null;
    try {
        conn = SQLiteConnection.getConnection();
        // Tắt chế độ tự động lưu để bắt đầu Transaction
        conn.setAutoCommit(false);

        // BƯỚC 1: Thêm User và yêu cầu trả về ID tự động sinh ra
        PreparedStatement psUser = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS);
        psUser.setString(1, user.getUserName());
        psUser.setString(2, user.getPasswordHash());
        psUser.setString(3, user.getFullName());
        psUser.setString(4, user.getGender());
        psUser.setString(5, user.getDateOfBirth().toString());
        psUser.executeUpdate();

        // Lấy user_id vừa tạo
        ResultSet rs = psUser.getGeneratedKeys();
        int generatedId = -1;
        if (rs.next()) {
            generatedId = rs.getInt(1);
        }

        // BƯỚC 2: Thêm bản ghi vào user_stats sử dụng ID vừa lấy
        if (generatedId != -1) {
            PreparedStatement psStats = conn.prepareStatement(insertStatsSql);
            psStats.setInt(1, generatedId);
            psStats.setInt(2, initialElo);
            psStats.executeUpdate();
            
            // BƯỚC 3: Mọi thứ ổn thỏa, lưu vĩnh viễn vào DB
            conn.commit();
            return true;
        }

    } catch (SQLException e) {
        if (conn != null) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
        e.printStackTrace();
    } finally {
        if (conn != null) SQLiteConnection.closeConnection(conn);
    }
    return false;
}

    
    // HELPER: trả về biến User
    private static User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("user_name"),
                rs.getString("password_hash"),
                rs.getString("full_name"),
                rs.getString("gender"),
                LocalDate.parse(rs.getString("date_of_birth"))
        );
    }
}
