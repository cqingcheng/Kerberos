package socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class NetworkService {
	private Socket socket = null;
    // �׽������������󣬴������ȡ�յ�����Ϣ
    private DataInputStream inputStream = null;
    // �׽�����������󣬴����﷢��������Ϣ
    private DataOutputStream outputStream = null;
    // ��ǰ����״̬�ı�Ǳ���
    private boolean isConnected = false;
	public interface Callback {
        void onConnected(String host, int port);        //���ӳɹ�
        void onConnectFailed(String host, int port);    //����ʧ��
        void onDisconnected();                          //�Ѿ��Ͽ�����
        void onMessageSent(String name, String msg);    //��Ϣ�Ѿ�����
        void onMessageReceived(String msg);//�յ���Ϣ
    }
    
    private Callback callback;
    public void setCallback(Callback callback) {
        this.callback = callback;
    }
	
    
    private void beginListening() {
        Runnable listening = new Runnable() {
            @Override
            public void run() {
            	 System.out.println("����");
                try {
                    inputStream = new DataInputStream(socket.getInputStream());
                    System.out.println("������");
                    while (true) {
                        String s = inputStream.readUTF();
                        System.out.println("������s   "+s);
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
        	System.out.println("����");
            // �����׽��ֶ������������������
            socket = new Socket(host, port);
            isConnected = true;
            // ֪ͨ���������
            if (callback != null) {
                callback.onConnected(host, port);
            }
            // ��ʼ�����Ƿ���������Ϣ����
            beginListening();
           
        } catch (IOException e) {
            // ���ӷ�����ʧ��
            isConnected = false;
            // ֪ͨ�������ʧ��
            if (callback != null) {
                callback.onConnectFailed(host, port);
            }
            e.printStackTrace();
        }
    }
    /**
     * �Ͽ�����
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
            // ֪ͨ������ӶϿ�
            if (callback != null) {
                callback.onDisconnected();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * �Ƿ��Ѿ����ӵ�������
     * @return trueΪ�����ӣ�falseΪδ����
     */
    public boolean isConnected() {
    	return isConnected;
    }
    /**
     * ����������Ϣ
     * @param name �û���
     * @param msg ��Ϣ����
     */
    public void sendMessage(String name, String msg) {
        // �������Ϸ���
        if (name == null || "".equals(name) || msg == null || "".equals(msg)) {
            return;
        }
        if (socket == null) {   //�׽��ֶ�������Ѵ���
        	System.out.println("�׽���Ϊ����  ");
        	return;
        }
        
        try {
            // ����Ϣд���׽��ֵ������
            outputStream = new DataOutputStream(socket.getOutputStream());
            System.out.println("��������  "+msg);
            outputStream.writeUTF(msg); 
            outputStream.flush();
            // ֪ͨ�����Ϣ�ѷ���
            if (callback != null) {
                callback.onMessageSent(name, msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
