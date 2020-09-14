package socket;



//测试github
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.TextField;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;

import socket.NetworkService;
import socket.NetworkService.Callback;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Iterator;
import java.util.Stack;

public class clientui extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	//private JTextField textField_1;
	private TextField textField5;
	private JTextArea textField6;
	private NetworkService networkService;
	private JPasswordField textField_1;
	private String Kcv;
	private void initNetworkService() {
        networkService = new NetworkService();
        networkService.setCallback(new Callback() {
            @Override
            public void onConnected(String host, int port) {
                // 连接成功时，弹对话框提示，并将按钮文字改为“断开”
             
            }

            @Override
            public void onConnectFailed(String host, int port) {
                // 连接失败时，弹对话框提示，并将按钮文字设为“连接”
               
            }

            @Override
            public void onDisconnected() {
                // 断开连接时，弹对话框提示，并将按钮文字设为“连接”
              
            }

            @Override
            public void onMessageSent(String name, String msg) {
               // 发出消息时，清空消息输入框，并将消息显示在消息区
            	textField5.setText("");
            	DesDecrypt DesDecrypt5=new DesDecrypt(msg,Kcv);
				try {
					DesDecrypt5.decrypt();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String v_c =DesDecrypt5.message;
            	
            	
                textField6.append("我(" + name + "):    密文：  " + v_c + "\r"+"明文："+msg+"\n");
            }

            @Override
            public void onMessageReceived(String msg) {
                // 收到消息时，将消息显示在消息区
            	System.out.println("收到聊天信息");
            	String account=msg.substring(1,10);
            	msg=msg.substring(10);
            	
            	DesDecrypt DesDecrypt6=new DesDecrypt(msg,Kcv);
				try {
					DesDecrypt6.decrypt();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}
				String v_c =DesDecrypt6.message;
            	
            	
            	textField6.append(account+":    密文 "+msg + "\r明文：  "+v_c+"\n");
            }
        });
    }
	

	
	
	int p=101;
	int q=103;
	int e=65537;
	int n=p*q;
	int jn=(p-1)*(q-1);
	int d=exgcd(e,jn);
	String kctgs="";
	String Tickettgs;
	String TGS="127.0.0.1";
	String ts3;
	String ts5;
	String result=null;
	//private String pwd;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					clientui frame = new clientui();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public static int exgcd(int a,int b) {
		//a*d-b*k=1
		Stack<int[]> list = new Stack<int[]>();
		int[] t = {a,b};
		list.push(t);
		while(a!=1&&b!=1) {
			int[] temp = new int[2];
			if(a<b) {
				temp[0] = a;
				temp[1] = Math.floorMod(b, a);
				list.push(temp);
			}else {
				temp[0] = Math.floorMod(a, b);
				temp[1] = b;
				list.push(temp);
			}
			a = temp[0];
			b = temp[1];
		}
		//a=1鐨勬檪鍊 ,y=1;b=1鏅 ,x=1銆 
		//a*x-b*y=1
		Iterator<int[]> iter = list.iterator();
		int x = 0,y = 0;
		if(b==1) {
			x = 1;
			int f = 1;
			while(iter.hasNext()) {
				int[] temp = list.pop();
				if((f&1)==1) {
					y = (temp[0]*x-1)/temp[1];
					x = (temp[1]*y+1)/temp[0];
				} else {
					x = (temp[1]*y+1)/temp[0];
					y = (temp[0]*x-1)/temp[1];
				}
				f++;
			}
		}
		if(a==1) {
			y = 0;
			int f = 1;
			while(iter.hasNext()) {
				int[] temp = list.pop();
				if((f&1)==0) {
					y = (temp[0]*x-1)/temp[1];
					x = (temp[1]*y+1)/temp[0];
				} else {
					x = (temp[1]*y+1)/temp[0];
					y = (temp[0]*x-1)/temp[1];
				}
				f++;
			}
		}
		return x;
	}
	
	public int Algorithm(int data,int e,int n) {//蒙哥马利算法
		String result = Integer.toBinaryString(e);
		result=new StringBuffer(result).reverse().toString();
		char[] r=result.toCharArray(); 
		int[] a=new int[result.length()];
		int output=1;
		for(int i=0;i<result.length();++i) {
			a[i]=data%n;
			int tag=n-a[i];
			if(tag<a[i]) {
				a[i]=0-tag;
			}
			data=a[i]*a[i];
			if(r[i]=='0') {
				continue;
			}
			output=output*a[i];
			if(output>n||output<-n) {
				output=output%n;
			}
		}
		output=output%n;
		if(output<0)
			output=n+output;
		return output;
		
	}
	public static char asciitochar(String s) {
		char c = 0;
		System.out.println(s);
		char[] S = s.toCharArray();
		c = (char)((S[0]-'0')*128+(S[1]-'0')*64+(S[2]-'0')*32+(S[3]-'0')*16+(S[4]-'0')*8+(S[5]-'0')*4+(S[6]-'0')*2+(S[7]-'0')*1);
		System.out.println("c   "+c);
		return c;	
	}
	
	/**
	 * Create the frame.
	 */
	public class TextFieldHintListener implements FocusListener{
		
		private String hintText;
		private TextField textField;
		
		public TextFieldHintListener(TextField TextField,String hintText) {
			this.textField = TextField;
			this.hintText = hintText;
			TextField.setText(hintText);  //默认直接显示

		}
	 
		@Override
		public void focusGained(FocusEvent e) {
			//获取焦点时，清空提示内容
			String temp = textField.getText();
			if(temp.equals(hintText)) {
				textField.setText("");
			}
		}
	 
		@Override
		public void focusLost(FocusEvent e) {
			//失去焦点时，没有输入内容，显示提示内容
			String temp = textField.getText();
			if(temp.equals("")) {
				textField.setText(hintText);
			}
		}
	 
	}
	
	public clientui() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
	
		JButton btnNewButton = new JButton("注册");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				JFrame frame1 = new JFrame("新窗口");
				frame1.setLocation(100,50);//设置在屏幕的位置
				frame1.setSize(400,300);//				窗体大小
				JLabel lblNewLabel = new JLabel("账号");
				lblNewLabel.setBounds(91, 58, 58, 15);
				contentPane.add(lblNewLabel);
			
				JLabel lblNewLabel_1 = new JLabel("密码");
				lblNewLabel_1.setBounds(91, 113, 58, 15);
				contentPane.add(lblNewLabel_1);

				TextField textField1 = new TextField();
				textField1 .setBounds(129, 55, 203, 18);
				textField1 .addFocusListener(new TextFieldHintListener(textField1, "请输入长度为9位的账号"));
				frame1.getContentPane().add(textField1);
				
				TextField textField2 = new TextField();
				textField2 .setBounds(132, 110, 200, 18);
				textField2 .addFocusListener(new TextFieldHintListener(textField2, "密码长度位6-16位数字或英语字母"));
				frame1.getContentPane().add(textField2);
				
				
				JButton button1 = new JButton("取消");
				button1.setBounds(70, 180, 90, 25);
				button1.setBorderPainted(false);
				button1.addActionListener(new ActionListener(){
					//单击按钮执行的方法
					public void actionPerformed(ActionEvent e) {
						//System.exit(0);
						frame1.setVisible(false);
					}
					
				});
				
				
				JButton button2 = new JButton("注册");
				button2.setBounds(270, 180, 90, 25);
				button2.setBorderPainted(false);
				button2.addActionListener(new ActionListener(){
					//单击按钮执行的方法
					public void actionPerformed(ActionEvent e) {
						String account=textField1.getText().toString();
						String password=textField2.getText().toString();
						
						if(account.length()==9&&password.length()<16&&password.length()>6) {
							String data=null;
							if(password.length()>=10) {
								data=0+account+password.length()+password;
							}
							if(password.length()<10) {
								data=0+account+0+password.length()+password;
							}
							System.out.println("reginst"+data);
							String result="";
							try {
								result=registersocket.register("192.168.43.82",55533,data,account);
								
							} catch (IOException e1) {// TODO Auto-generated catch block
								e1.printStackTrace();
							}

							if(result.equals("1")) {
								data=1+data.substring(1,10)+n+65537;
								try {
									result=registersocket.register("192.168.43.82",55533,data,account);
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								if(result.equals("1")) {
									frame1.setVisible(false);
									JFrame frame = new JFrame("新窗口");
									frame.setLocation(200,50);
									frame.setSize(100,100);
									JLabel lblNewLabe3 = new JLabel("注册成功");
									lblNewLabe3.setBounds(91, 58, 58, 15);
									contentPane.add(lblNewLabe3);	
									frame.add(lblNewLabe3);
									frame.setVisible(true);
								}
								
								else {
									textField1 .addFocusListener(new TextFieldHintListener(textField1, "请输入长度为9位的账号"));
									textField2 .addFocusListener(new TextFieldHintListener(textField2, "密码长度位6-16位数字或英语字母"));
									JFrame frame = new JFrame("新窗口");
									frame.setLocation(200,50);
									frame.setSize(100,100);
									JLabel lblNewLabe4 = new JLabel("注册失败");
									lblNewLabe4.setBounds(91, 58, 58, 15);
									contentPane.add(lblNewLabe4);
									frame.add(lblNewLabe4);
									frame.setVisible(true);
								}
							}
							else {
								textField1 .addFocusListener(new TextFieldHintListener(textField1, "请输入长度为9位的账号"));
								textField2 .addFocusListener(new TextFieldHintListener(textField2, "密码长度位6-16位数字或英语字母"));
								JFrame frame = new JFrame("新窗口");
								frame.setLocation(200,50);
								frame.setSize(100,100);
								JLabel lblNewLabe4 = new JLabel("注册失败");
								lblNewLabe4.setBounds(91, 58, 58, 15);
								contentPane.add(lblNewLabe4);
								frame.add(lblNewLabe4);
								frame.setVisible(true);
							}
						}
						else {
							textField1 .addFocusListener(new TextFieldHintListener(textField1, "请输入长度为9位的账号"));
							textField2 .addFocusListener(new TextFieldHintListener(textField2, "密码长度位6-16位数字或英语字母"));
							JFrame frame = new JFrame("新窗口");
							frame.setLocation(200,50);
							frame.setSize(100,100);
							JLabel lblNewLabe4 = new JLabel("注册失败");
							lblNewLabe4.setBounds(91, 58, 58, 15);
							contentPane.add(lblNewLabe4);
							frame.add(lblNewLabe4);
							frame.setVisible(true);
						}	
						
						System.out.println(account);
						System.out.println(password);
					}
					
				});
				
				Panel pan = new Panel();
				pan.setSize(100, 100);	
				frame1.getContentPane().add(lblNewLabel);
				frame1.getContentPane().add(lblNewLabel_1);
				frame1.add(button1);
				frame1.add(button2);
				frame1.getContentPane().add(pan);
				frame1.setVisible(true);
			    
			}
		});
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnNewButton.setBounds(79, 196, 97, 23);
		contentPane.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("登录");
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String account=textField.getText().toString();
			//	String password=textField_1.getText().toString();
				String password=new String(textField_1.getPassword());
				System.out.println(password);
				JFrame frame2 = new JFrame("群聊");
				frame2.setLocation(100,50);//设置在屏幕的位置
				frame2.setSize(600,500);//				窗体大小
				
				contentPane.setVisible(false);
				
				textField6 = new JTextArea();
				textField6 .setBounds(40, 40, 500, 250);
				textField6.setLineWrap(true);
				
				
				
				
				
		        
		        
		        
				textField5 = new TextField();
				textField5 .setBounds(40, 310, 500, 80);
				frame2.getContentPane().add(textField5);
				
				Long startTs = System.currentTimeMillis();
				String data=2+account+1+startTs+password;
				textField6.append("\n向AS服务器发送请求"+data+"\n");
				
				try {
					result=registersocket.register("192.168.43.82",55533,data,account);
					if(result.equals("0")) {
						//textField1.setText("认证失败");
						textField6.append("认证失败");

					}
					else {
						System.out.println("从as服务器获得票据"+result);
						//textField1.setText("从as服务器获得票据"+result);
						textField6.append("从as服务器获得票据"+result);
						
						String asmesage="";
						for(int i=0;i<125;++i) {
							String pre=result.substring(i*5,i*5+5);
							int edata = Integer.parseInt(pre); 
							int ereslut=Algorithm(edata,d,n);
							
							String s="";
							while(ereslut>0) {
								 int res = ereslut%2;
								 s = res+s;
								 ereslut=ereslut/2;
							 }
							if(i==124) {
								for(int j=12-s.length();j>0;--j) {
									
									s="0"+s;
									System.out.println("s  "+s);
								}
							}
							else {
								for(int j=13-s.length();j>0;--j) {
									s="0"+s;
								}
							}
							asmesage=asmesage+s;
						}
						
						for(int i=0;i<64;++i) {
							char pre=asciitochar(asmesage.substring(i*8,i*8+8));
							kctgs=kctgs+String.valueOf(pre);
						}
						Tickettgs=asmesage.substring(728);
						
						System.out.println("kctgs"+kctgs);
						System.out.println("TicketTgs  "+Tickettgs.length()+"  "+Tickettgs);
						
						Long startTs3 = System.currentTimeMillis();
						ts3=startTs3+"";
						String eauthc=account+"127127127127"+ts3;
						
						System.out.println(eauthc.length()+"   eauthc  "+eauthc);
						System.out.println(ts3.length()+"   ts3  "+ts3);
						System.out.println(kctgs.length()+"   kctgs  "+kctgs);
						
						DesEncrypt Desencrypt=new DesEncrypt(eauthc,kctgs);
						Desencrypt.encrypt();
						
						String Authc =Desencrypt.ciphertexts;
						textField6.append("\n向TGS服务器发送请求"+Authc+"\n");
						data=31+Tickettgs+Authc;
						System.out.println(data+"   data  "+data.length());
						result=registersocket.register("192.168.43.180",55533,data,account);
						System.out.println(result);
						if(result.equals("0")) {
							//textField1.setText("认证失败");
							textField6.append("TGS认证失败");		
						}
						else {
							//textField1.setText("从TGS服务器获得票据"+result);
							textField6.append("\n从TGS服务器获得票据"+result+"\n");
							
							
							System.out.println(result.length()+"   result  "+result);
							DesDecrypt DesDecrypt=new DesDecrypt(result,kctgs);
							DesDecrypt.decrypt();
							String tgs_c =DesDecrypt.message;
							Kcv=tgs_c.substring(0,64);
							String idv=tgs_c.substring(64,65);
							String TS4=tgs_c.substring(65,78);
							String Ticketv=tgs_c.substring(78);

							System.out.println(Kcv.length()+"   Kcv  "+Kcv);
							System.out.println(idv.length()+"   idv  "+idv);
							System.out.println(TS4.length()+"   TS4  "+TS4);
							System.out.println(Ticketv.length()+"   Ticketv  "+Ticketv);
							
							
							
							
							
							
							
							
							Long startTs4 = System.currentTimeMillis();
							ts5=startTs4+"";
							String eauthc1=account+"127127127127"+ts5;
							
							
							
							DesEncrypt Desencrypt1=new DesEncrypt(eauthc1,Kcv);
							Desencrypt1.encrypt();
							
							String Authc1 =Desencrypt1.ciphertexts;
							
							
							
							data=4+Ticketv+Authc1;
							System.out.println(data+"   data  "+data.length());
							result=registersocket.register("192.168.43.218",55533,data,account);
							System.out.println(result);
							if(result.equals("0")) {
								textField6.append("认证失败");
								
							}
							else {
								
								DesDecrypt DesDecrypt1=new DesDecrypt(result,Kcv);
								DesDecrypt1.decrypt();
								String v_c =DesDecrypt1.message;
								System.out.println("v_c   "+v_c);
							//	textField6.setText("\n");
								
								initNetworkService();
								
								//networkService = new NetworkService();
								
								System.out.println("连接应用服务器");
								networkService.connect("192.168.43.218", 55533);
								
								
								
								
								
								
								
								
								networkService.sendMessage(account,"5"+account);
								
								JButton button2 = new JButton("发送");
								button2.setBounds(440, 400, 80, 30);
								button2.setBorderPainted(false);
								frame2.add(button2);	
								button2.addActionListener(new ActionListener(){
									//单击按钮执行的方法
									public void actionPerformed(ActionEvent e) {
										String data=textField5.getText();


										DesEncrypt Desencrypt1=new DesEncrypt(data,Kcv);
										try {
											Desencrypt1.encrypt();
										} catch (IOException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
										
										String Authc1 =Desencrypt1.ciphertexts;
										
										
										data=5+account+Authc1;
										networkService.sendMessage(account,data);
									}
									
								});
													
							}								
						}		
					}
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				JButton button1 = new JButton("取消");
				button1.setBounds(40, 400, 80, 30);
				button1.setBorderPainted(false);
				button1.addActionListener(new ActionListener(){
					//单击按钮执行的方法
					public void actionPerformed(ActionEvent e) {
						//System.exit(0);
						networkService.disconnect();
						System.exit(0);
					}
					
				});
				frame2.add(button1);

				
				Panel pan = new Panel();
				pan.setSize(100, 100);
				
				 JScrollPane text2=new JScrollPane(textField6){
					   @Override
					   public Dimension getPreferredSize() {
					     return new Dimension(500, 250);
					   }
					 }; 
					
				 pan.add(text2);
				
				frame2.getContentPane().add(pan);
				frame2.setVisible(true);
				
				System.out.println(account);
				System.out.println(password);
				
			}
		});
		btnNewButton_1.setBounds(289, 196, 97, 23);
		contentPane.add(btnNewButton_1);

		JLabel lblNewLabel = new JLabel("账号");
		lblNewLabel.setBounds(91, 58, 58, 15);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("密码");
		lblNewLabel_1.setBounds(91, 113, 58, 15);
		contentPane.add(lblNewLabel_1);
		
		textField = new JTextField();
		textField.setBounds(129, 55, 203, 18);
		contentPane.add(textField);
		textField.setColumns(10);
		
		textField_1=new JPasswordField();
		textField_1.setBounds(132, 110, 200, 18);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		/*
		textField_1 = new JTextField();
		textField_1.setBounds(132, 110, 200, 18);
		contentPane.add(textField_1);
		textField_1.setColumns(10);*/
		
		
		
		
		
	}
}
