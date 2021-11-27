
import Server.ServerThread;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import persistence.DAO.*;
import persistence.DTO.*;
import persistence.MyBatisConnectionFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String args[]){

        ServerSocket serverSocket = null;

        try{
            serverSocket = new ServerSocket();
        }
        catch (Exception e){
            System.out.println("serverSocketException");
            e.printStackTrace();
            return;
        }
        String address =null;
        try{
            address = InetAddress.getLocalHost().getHostAddress();
        }
        catch (Exception e){
            System.out.println("InetAddressException");
            e.printStackTrace();
            return;
        }
        try{
            serverSocket.bind(new InetSocketAddress("192.168.232.7",5000));

        }
        catch (Exception e) {
            System.out.println("bindException");
            e.printStackTrace();
        }
        System.out.println(address + ":" + 5000);
        Socket socket = null;
        while (true)
        {
            try
            {
                socket = serverSocket.accept();
            }

            catch (Exception e)
            {
                System.out.println("acceptException");
                e.printStackTrace();
                return;
            }

            System.out.println("[" + socket.getInetAddress() + ":" + socket.getPort() + "]");
            ServerThread serverThread = new ServerThread(socket);
            serverThread.run();
        }










    }
}