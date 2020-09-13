package socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class NetworkService {
	private Socket socket = null;
    // 套接字输入流对象，从这里读取收到的消息
    private DataInputStream inputStream = null;
    // 套接字输出流对象，从这里发送聊天消息
    private DataOutputStream outputStream = null;
    // 当前连接状态的标记变量
    private boolean isConnected = false;
	public interface Callback {
        void onConnected(String host, int port);        //连接成功
        void onConnectFailed(String host, int port);    //连接失败
        void onDisconnected();                          //已经断开连接
        void onMessageSent(String name, String msg);    //消息已经发出
        void onMessageReceived(String msg);//收到消息
    }
    
    private Callback callback;
    public void setCallback(Callback callback) {
        this.callback = callback;
    }
	
    
    private void beginListening() {
        Runnable listening = new Runnable() {
            @Override
            public void run() {
            	 System.out.println("监听");
                try {
                    inputStream = new DataInputStream(socket.getInputStream());
                    System.out.println("监听到");
                    while (true) {
                        String s = inputStream.readUTF();
                        System.out.println("监听到s   "+s);
                        if (callback != null) {
                            callback.onMessageReceived(s);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        (new Thread(listening)).start();
    }
    
    
    
    public void connect(String host, int port) {
        try {
        	System.out.println("连接");
            // 创建套接字对象，与服务器建立连接
            socket = new Socket(host, port);
            isConnected = true;
            // 通知外界已连接
            if (callback != null) {
                callback.onConnected(host, port);
            }
            // 开始侦听是否有聊天消息到来
            beginListening();
           
        } catch (IOException e) {
            // 连接服务器失败
            isConnected = false;
            // 通知外界连接失败
            if (callback != null) {
                callback.onConnectFailed(host, port);
            }
            e.printStackTrace();
        }
    }
    /**
     * 断开连接
     */
    public void disconnect() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (inputStream!= null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            isConnected = false;
            // 通知外界连接断开
            if (callback != null) {
                callback.onDisconnected();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 是否已经连接到服务器
     * @return true为已连接，false为未连接
     */
    public boolean isConnected() {
    	return isConnected;
    }
    /**
     * 发送聊天消息
     * @param name 用户名
     * @param msg 消息内容
     */
    public void sendMessage(String name, String msg) {
        // 检查参数合法性
        if (name == null || "".equals(name) || msg == null || "".equals(msg)) {
            return;
        }
        if (socket == null) {   //套接字对象必须已创建
        	System.out.println("套接字为创建  ");
        	return;
        }
        
        try {
            // 将消息写入套接字的输出流
            outputStream = new DataOutputStream(socket.getOutputStream());
            System.out.println("发送数据  "+msg);
            outputStream.writeUTF(msg); 
            outputStream.flush();
            // 通知外界消息已发送
            if (callback != null) {
                callback.onMessageSent(name, msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
