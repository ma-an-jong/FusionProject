package Client;
import Client.SocketClient;

public class LoginClient {
    //iohandler에서 id, password 받음 -> socketclient에 배열로 전달
    String loginArr[];

    public String[] login(String[] idpw) {
        loginArr = new String[3];

        String id = idpw[0];
        String password = idpw[1];

        loginArr[0] = Protocol.PT_REQ_LOGIN;
        loginArr[1] = id;
        loginArr[2] = password;

        return loginArr;
    }

    public String[] logout(){
        String logout[] = new String[1];

        logout[0]= Protocol.PT_EXIT;

        return logout;
    }
}