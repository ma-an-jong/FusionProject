package Client;
import Client.SocketClient;

public class LoginClient {
    //iohandler에서 id, password 받은걸 socketclient에 배열로 전달
    IOHandler ioHandler = new IOHandler();
    String loginArr[];

    public String[] login() {
        loginArr = new String[3];

        String id = ioHandler.id;
        String password = ioHandler.password;

        loginArr[0] = Protocol.PT_REQ_LOGIN;
        loginArr[1] = id;
        loginArr[2] = password;

        return loginArr;
    }
}