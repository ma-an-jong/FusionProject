package Client;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class SocketClient {
    Socket socket;
    BufferedReader bufferedReader = null;
    BufferedWriter bufferedWriter = null;
    InputStream inputStream = null;
    OutputStream outputStream = null;

    public SocketClient() {
        socket = new Socket();
    }

    public String[] run(String[] args) {
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println();
        }

        Protocol protocol = new Protocol(args[0]);
        String str;
        String[] result;

        try {
            removeElement(args, 0);
            protocol.setPacket(args);

            writePacket(args[0]);

            while(true) {
                str = bufferedReader.readLine();
                result = str.split(Protocol.splitter);

                if(result != null) return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void removeElement(String [] arr, int index){
        String[] arrDestination = new String[arr.length - 1];
        int remainingElements = arr.length - ( index + 1 );
        System.arraycopy(arr, 0, arrDestination, 0, index);
        System.arraycopy(arr, index + 1, arrDestination, index, remainingElements);
//        System.out.println("Elements -- "  + Arrays.toString(arrDestination));
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
}