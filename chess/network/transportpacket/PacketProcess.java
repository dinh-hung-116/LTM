package chess.network.transportpacket;

import chess.database.DTO.User;
import chess.network.GsonUtil;
import chess.network.NetworkConfig;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.time.LocalDate;

/**
 * 1. Lớp này dùng để chứa các phương thức để chỉnh sửa thông tin gói tin
 * 2. Lớp này cũng dùng để chuyển đổi giữa JSON <-> PACKET
 * 
 * client -> server
 * - gói tin như này:
 * LoginPacket {
 * type: {LOGIN_REQUEST},
 * result: {},
 * User: {username, password,....,...}
 * 
 * */

public class PacketProcess {

    public static final String NA = "N/A";
    private static final Gson gson = GsonUtil.createGson();

    // ================= LOGIN REQUEST =================
    // packet này chứa username, password. Các trường khác thì thông tin rỗng
    public static LoginPacket craftLoginRequestPacket(String username, String password) {
        User user = dumpUser(username, password);

        LoginPacket packet = new LoginPacket(NetworkConfig.LOGIN_REQUEST, NA);
        packet.setUser(user);

        return packet;
    }

    // ================= LOGIN RESPONSE =================
    // packet chứa một User(nếu có) nhưng phần password = "N/A";
    public static LoginPacket craftLoginResponsePacket(boolean success, User user) {
        LoginPacket packet;

        if (success) {
            packet = new LoginPacket(NetworkConfig.LOGIN_RESPONSE, NetworkConfig.LOGIN_OK);
        } else {
            packet = new LoginPacket(NetworkConfig.LOGIN_RESPONSE, NetworkConfig.LOGIN_FAIL);
        }

        packet.setUser(user);
        return packet;
    }
 
    // ================= REGISTER REQUEST =================
    // packet này chứa một User đầy đủ
    public static LoginPacket craftRegisterRequestPacket(User user) {
        LoginPacket packet = new LoginPacket(NetworkConfig.REGISTER_REQUEST, NA);
        packet.setUser(user);

        return packet;
    }

    // ================= REGISTER RESPONSE =================
    // packet này chứa type, result, User rỗng
    public static LoginPacket craftRegisterResponsePacket(boolean success) {
        LoginPacket packet;

        if (success) {
            packet = new LoginPacket(NetworkConfig.REGISTER_RESPONSE, NetworkConfig.REGISTER_OK);
        } else {
            packet = new LoginPacket(NetworkConfig.REGISTER_RESPONSE, NetworkConfig.REGISTER_FAIL);
        }
        
        packet.setUser(new User());
        return packet;
    }
    
    // ================= LOGOUT REQUEST =================
    // tạm thời packet này không quan trọng User có hay không
    public static LoginPacket craftLogoutRequestPacket() {
        LoginPacket packet = new LoginPacket(NetworkConfig.LOGOUT_REQUEST, NA);
        packet.setUser(new User());

        return packet;
    }
    
    // ================= LOGOUT RESPONSE =================
    // tương tự LOGOUT_REQUEST
    public static LoginPacket craftLogoutResponsePacket() {
        LoginPacket packet = new LoginPacket(
                NetworkConfig.LOGOUT_RESPONSE, NetworkConfig.LOGOUT_OK);
        packet.setUser(new User());

        return packet;
    }
    
    // ====== HELPER ======
    // lấy ra Header(type) của packet
    public static String getPacketHeader(String rawJson) {
        try {
            JsonElement element = JsonParser.parseString(rawJson);

            if (!element.isJsonObject()) {
                return null; // not a valid packet format
            }

            JsonObject obj = element.getAsJsonObject();
            JsonElement typeElement = obj.get("type");

            if (typeElement == null || typeElement.isJsonNull()) {
                return null;
            }

            return typeElement.getAsString();

        } catch (JsonSyntaxException | IllegalStateException e) {
            return null; // invalid JSON
        }
    }
    
    // Phương thức tạo User có username và password
    public static User dumpUser(String username, String password) {
        User user = new User();
        user.setUserId(0);
        user.setUserName(username);
        user.setPasswordHash(password);
        user.setGender(null);
        user.setDateOfBirth(null);
        
        return user;
    }
    
    // ====================
    // ====== JSON CONVERT ======
    // JSON -> PACKET
    // phương thức chuyển đổi json thành packet <T> tương ứng với tham số clazz
    public static <T> T fromJson(String raw, Class<T> clazz) {
        return gson.fromJson(raw, clazz);
    }
    
    // PACKET -> JSON
    // mọi class trong java đều kế thừa Object nên dùng Object để có thể truyền nhiều loại
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }
    
}