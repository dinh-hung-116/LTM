package chess.network.transportpacket;

/**
 * Lớp cha của mọi Packet
 * 
 */

public class Packet {    
    // gói tin là loại gì? LOGIN? MOVE?....
    protected String type; // hoặc header

    public Packet() {
        // Needed for Gson deserialization
    }

    public Packet(String type) {
        this.type = type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
        
}