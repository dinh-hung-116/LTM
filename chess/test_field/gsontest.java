package chess.test_field;

import com.google.gson.Gson;


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
    
    public static void main(String[] args) {
        Gson gson = new Gson();

        // Create object -> JSON
        Packet sendPacket = new Packet();
        sendPacket.dumpPacket("ok");

        String json = gson.toJson(sendPacket);
        System.out.println("JSON Output:");
        System.out.println(json);

        // JSON -> Object
        Packet receivedPacket = gson.fromJson(json, Packet.class);

        System.out.println("\nParsed Object:");
        System.out.println("Packet: " + receivedPacket.message);
    }
}

/*
Bản chất json là một kiểu chuỗi có cấu trúc cụ thể -> json vẫn là String nhưng có cấu trúc
*/
