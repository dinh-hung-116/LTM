package chess.test_field;

import chess.database.DTO.User;
import chess.gui.match.chessboard.Assets;
import chess.network.GsonUtil;
import chess.network.transportpacket.LoginPacket;
import chess.network.transportpacket.PacketProcess;
import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;


public class gsontest {    
    // Packet.java
    public static class Packet {
        public String type;
        public String message;

        public Packet() {
            // Needed for Gson deserialization
        }

        public Packet(String type, String message) {
            this.type = type;
            this.message = message;
        }
        
        public void setMessage(String msg) {
            this.message = msg;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public void dumpPacket(String type) {
            this.type = type;
            message = "N/A";
        }
    }
    
    public static void main(String[] args) throws IOException {
        Gson gson = GsonUtil.createGson();

        // Create object -> JSON
        LoginPacket sendPacket = PacketProcess.craftLoginResponsePacket(false, new User());

        String json = gson.toJson(sendPacket);
        System.out.println("JSON Output:");
        System.out.println(json);

        // JSON -> Object
        LoginPacket receivedPacket = gson.fromJson(json, LoginPacket.class);

        System.out.println("\nParsed Object:");
        System.out.println("Packet: " + receivedPacket.getUser());
        
        System.out.println(Paths.get("BP.png").toAbsolutePath());
    }
}

/*
Bản chất json là một kiểu chuỗi có cấu trúc cụ thể -> json vẫn là String nhưng có cấu trúc
*/
