package socket;




import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import Function.JudgeCommand;
import Function.ReturnPacket;

public class Server {
    public static void main(String[] args) throws IOException {
        int port=55533;
        ServerSocket server=new ServerSocket(port);
        System.out.println("server building");
        while(true) {
            System.out.println("11111 ");
            Socket socket=server.accept();
            System.out.println("11111 building");
            new Thread(()->{
                System.out.println("2222 building");
                DataInputStream dis = null;
                try {
                    dis = new DataInputStream(socket.getInputStream());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                DataOutputStream dos = null;
                try {
                    dos = new DataOutputStream(socket.getOutputStream());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //	boolean isrunning=true;
                //while(isrunning) {
                String data = null;
                try {
                    data = dis.readUTF();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                System.out.println(data);

                JudgeCommand judgeCommand=new JudgeCommand(data);
                ReturnPacket returnPacket=new ReturnPacket(judgeCommand);
                data=returnPacket.GetResult();

                try {
                    dos.writeUTF(data);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    dos.flush();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //		if(data.equals("end")) {
                //			break;
                //		}
                //	}
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
