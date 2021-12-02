
import Server.ServerThread;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class ServerMain {
    public static void main(String args[]){
        //서버 소켓 생성
        ServerSocket serverSocket = null;

        try{
            serverSocket = new ServerSocket();
        }
        catch (Exception e){
            System.out.println("serverSocketException");
            e.printStackTrace();
            return;
        }

        //ip port번호 바인딩
        try
        {
            serverSocket.bind(new InetSocketAddress("192.168.232.7",5000));
            System.out.println("server on");

        }
        catch (Exception e)
        {
            System.out.println("bindException");
            e.printStackTrace();
        }

        //클라이언트 소켓 생성
        Socket socket = null;

        while (true)
        {
            //클라이언트 접속
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
            //서버 쓰레드 생성 및 실행
            ServerThread serverThread = new ServerThread(socket);
            serverThread.start();
        }

    }
}