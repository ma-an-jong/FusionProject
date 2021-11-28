
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

        try
        {
            serverSocket.bind(new InetSocketAddress("192.168.0.96",5000));
            System.out.println("server on");

        }
        catch (Exception e)
        {
            System.out.println("bindException");
            e.printStackTrace();
        }

        Socket socket = null;

        while (true)
        {
            try
            {
                socket = serverSocket.accept();
                System.out.println("클라이언트 접속:" + socket.getInetAddress() + ":" + socket.getPort());
            }

            catch (Exception e)
            {
                System.out.println("acceptException");
                e.printStackTrace();
                return;
            }

            ServerThread serverThread = new ServerThread(socket);
            serverThread.run();
        }










    }
}