package chess.network.transportpacket;

import chess.database.Class.User;

/*
json của object sẽ như thế này:

type: "LOGIN"
result: ""
User: ...


**CHÚ Ý**
- Packet này sẽ được dùng chung với LOGIN và REGISTER do tính chất giống nhau
trong việc gửi/nhận dữ liệu.
***********

*/
public class LoginPacket extends Packet {
    // biến để chứa kết quả trả về từ server: LOGIN_OK ? LOGIN_FAIL
    private String result;
    // dùng để chứa dữ liệu user
    private User user;

    public LoginPacket() {
    }

    public LoginPacket(String result, String type) {
        super(type);
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
}
