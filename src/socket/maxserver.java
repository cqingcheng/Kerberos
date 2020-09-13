package socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class maxserver {
	public static void main(String[] args) throws IOException {
		int port=55533;
		ServerSocket server=new ServerSocket(port);
		System.out.println("server 将一直等待连接");
		while(true) {
			Socket socket=server.accept();
			
			new Thread(()->{
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
