package socket;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.io.InputStreamReader;
import java.io.InputStream;

import java.io.OutputStream;
import java.net.ServerSocket;


public class client {
		public static void main(String[] args) throws IOException {
			String host = "127.0.0.1"; 
		    int port = 55533;
		    // �����˽�������
		    Socket socket = new Socket(host, port);
		    // �������Ӻ�����Ϣ
		     BufferedReader console=new BufferedReader(new InputStreamReader(System.in));
		    DataOutputStream dos=new DataOutputStream(socket.getOutputStream());
		    DataInputStream dis=new DataInputStream(socket.getInputStream());
		    String data="111111111111111111111111111111111111111111111111111111111111111111111111111111";
		    while(true) {
		    	//������Ϣ
		    	//String msg=console.readLine();
			    dos.writeUTF(data);
			    dos.flush();
			    //��ȡ��Ϣ
				data=dis.readUTF();
				System.out.println("server"+data);
				if(data.equals("end")) {
					break;
				}
		    }
			dis.close();
		    dos.close();
		    socket.close(); 
		}
}
