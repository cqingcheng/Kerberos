package socket;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JTextArea;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.io.InputStreamReader;

public class registersocket {
	static String truth="1";
	static String wrong="0";
       public static String register(String host, int port,String data,String account) throws UnknownHostException, IOException {
		    Socket socket = new Socket(host, port);
		    System.out.println("register");
		    // 建立连接后发送消息
		     BufferedReader console=new BufferedReader(new InputStreamReader(System.in));
		    DataOutputStream dos=new DataOutputStream(socket.getOutputStream());
		    DataInputStream dis=new DataInputStream(socket.getInputStream());
		    System.out.println("data"+data);
		   //发送消息
		    //	String msg=console.readLine();
			//  dos.writeUTF(msg);
		    //System.out.println("data"+receiver);
		    if(data.substring(0,1).equals("d")) {
		    	String receiver;
			    receiver=dis.readUTF();
			    return receiver;
		    }
	    	dos.writeUTF(data);	
		    dos.flush();
		    //获取消息
		    String receiver;
		    receiver=dis.readUTF();
		    
		    
		    
		    System.out.println("receiver  "+receiver);
			String tag=receiver.substring(0,1);
			if(tag.equals("6")) {
				String sign=receiver.substring(1,2);
				if(sign.equals("1")) {
					dis.close();
				    dos.close();
				    socket.close(); 
					return truth;
				}
			}
			if(tag.equals("7")) {
				String sign=receiver.substring(1,2);
				if(sign.equals("1")) {
					dis.close();
				    dos.close();
				    socket.close(); 
					return truth;
				}
			}
			if(tag.equals("8")) {
				String sign=receiver.substring(1,2);
				if(sign.equals("1")) {
					String result=receiver.substring(2);
					dis.close();
				    dos.close();
				    socket.close(); 
					return result;
				}
			}
			if(tag.equals("9")) {
				String sign=receiver.substring(1,2);
				if(sign.equals("1")) {
					String result=receiver.substring(2);
					dis.close();
				    dos.close();
				    socket.close(); 
					return result;
				}
			}
			if(tag.equals("a")) {
				String sign=receiver.substring(1,2);
				if(sign.equals("1")) {
					String result=receiver.substring(2);
					dis.close();
				    dos.close();
				    socket.close(); 
					return result;
				}
			}
			if(tag.equals("c")) {
				String sign=receiver.substring(1);
				//System.out.println("from client message"+sign);
				//textField1.append(sign);
				/*while(true) {
					data=null;
			    	//发送消息
					String msg=console.readLine();
					data=5+account+msg;
				    dos.writeUTF(data);
				    dos.flush();
				    //获取消息
					data=dis.readUTF();
					System.out.println("from client message"+data);
					if(data.equals("end")) {
						break;
					}
			    }*/
			}
			
			dis.close();
		   dos.close();
		   socket.close(); 
    	   return wrong;
    	   
       }
}
