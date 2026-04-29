/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chess.network.transportpacket;

/**
 *
 * @author DELL
 */
public class Packet {    
    // gói tin là loại gì? LOGIN? MOVE?....
    protected String type;

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