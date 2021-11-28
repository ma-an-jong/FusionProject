import Server.ServerThread;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class TestClient {
    static String address =null;
    public static void main(String args[]){

        try{
            address = InetAddress.getLocalHost().getHostAddress();
            System.out.println(address);
        }
        catch (Exception e){
            System.out.println("될거같음?");
            return;
        }

        try{
            Socket socket = new Socket("192.168.0.96",5000);
            System.out.println("성공");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        }
        catch (Exception e){
            System.out.println("응 안돼");
            e.printStackTrace();
        }
    }
}