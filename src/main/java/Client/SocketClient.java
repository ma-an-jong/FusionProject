package Client;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

public class SocketClient {
    Socket socket;
    BufferedReader bufferedReader = null;
    BufferedWriter bufferedWriter = null;
    InputStream inputStream = null;
    OutputStream outputStream = null;

    public SocketClient() throws IOException {
        socket = new Socket("192.168.232.7", 5000);
    }

    public String[] run(String[] args) {
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println();
        }

        Protocol protocol = new Protocol(args[0]);
        String str = "";
        String[] result;
        String[] removed;
        try {
            removed = removeElement(args, 0);
            protocol.setPacket(removed);
            writePacket(protocol.getPacket());

            int s;
            while(true) {
                str = bufferedReader.readLine();
                result = str.split(Protocol.splitter);

                if(result != null) return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String[] removeElement(String [] arr, int index){
        String[] arrDestination = new String[arr.length - 1];
        int remainingElements = arr.length - ( index + 1 );
        System.arraycopy(arr, 0, arrDestination, 0, index);
        System.arraycopy(arr, index + 1, arrDestination, index, remainingElements);
//        System.out.println("Elements -- "  + Arrays.toString(arrDestination));'=
        return arrDestination;
    }

    public void writePacket(String source)
    {
        try
        {
            bufferedWriter.write(source + "\n");
            bufferedWriter.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("write에 실패하였습니다.");
        }
    }

    public String readPacket()
    {
        String str = new String();
        try
        {   int c;
            while((c = bufferedReader.read()) != -1)
            {
                str += (char)c;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("read에 실패하였습니다.");
        }

        System.out.println(str);
        return str;
    }
}