package socket;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DesEncrypt {
	// 初始56位密钥
	private String key = "";
	//子密钥
	public char[][] keybox = new char[16][48];
	//明文
	private String message = "";
	//密文
	public String ciphertexts = "";
	
	//初始置换64->68
	public int[] IP = new int[64];
	// 置换1使用，8*7,64->56
	public int[] PC_1 = new int[56];
	// 子密钥生成移位表
	static int[] RoundTable = new int[16];
	// 置换表2，8*6,56->48
	static int[] PC_2 = new int[48];
	//扩充表E,32->48
	static int[] E = new int[48];
	//S盒置换
	static int[][][] S = new int[8][4][16];
	//P置换 32->32
	static int[] P = new int[32];
	//逆初始置换
	public int[] IP_1 = new int[64];
	
	//无参数构造方法
	DesEncrypt() {
		
	}
	
	//有参构造方法
	DesEncrypt(String message,String keystr){
		this.key = keystr;
		this.message = message;
	}

	//表格填充函数
	@SuppressWarnings("resource")
	public static void tableprepare(String filename,int[] table) throws IOException {
		String line = "";
		int len = table.length;
		BufferedReader reader  = new BufferedReader(new FileReader(filename));
		line = reader.readLine();
		String[] words = line.split(", ");
		for(int i=0; i<len;i++) {
			table[i] = Integer.parseInt(words[i]);
		}
	}

	//十进制转二进制
	public static String decimaltoBinary(int t,int flag) {
		String numstr="";
		while(t>0) {
			 int res = t%2;
			 numstr = res+numstr;
			 t=t/2;
		 }
		//转为二进制后不满四位前补0
		 if(numstr.length()<4 && flag == 1) {
			 switch(numstr.length()) {
			 case 0: numstr ="0000"; break;
			 case 1: numstr = "000" + numstr; break;
			 case 2: numstr = "00" + numstr; break;
			 case 3: numstr = "0" +numstr; break;
			 }	 
		 }
		return numstr;
	}
	
	// 置换函数
	public static void replacement(char[] oldchar, char[] newchar, int[] table) {
		for (int i = 0; i < newchar.length; i++) {
			newchar[i] = oldchar[table[i] - 1];
		}
	}

	// 分左右两段
	public static void split(char[] left, char[] right, char[] origin) {
		int len = origin.length;
		for (int i = 0; i < len; i++) {
			if (i < len / 2) {
				left[i] = origin[i];
				// System.out.print(left[i]);
			} else {
				right[i - len / 2] = origin[i];
				// System.out.print(right[i-len/2]);
			}
		}
	}

	// 子密钥生成时的移位
	public static String desshift(char[] C, char[] D, int round) {
		int rotations = RoundTable[round];
		int len = C.length;
		String tmp = null;
		char[] tmp1 = new char[len];
		char[] tmp2 = new char[len];
		for (int i = 0; i < len; i++) {
			// System.out.printf("移位位数为%d,第%d的tmp写入C中第%d位\n",rotations,i,(i+rotations)%len);
			tmp1[i] = C[(i + rotations) % len];
			tmp2[i] = D[(i + rotations) % len];
		}
		// 移位后保存移位后的数据循环下次拼接
		for (int i = 0; i < C.length; i++) {
			C[i] = tmp1[i];
			D[i] = tmp2[i];
		}
		// 移位后拼接为tmp
		tmp = String.valueOf(tmp1) + String.valueOf(tmp2);
		return tmp;
	}

	// 子密钥生成,返回值为16*（28+28）的二维数组
	public char[][] createkey(char[] key0) {
		char[][] keytable = new char[16][48];
		char[] key1 = new char[56];
		// 置换1,用PC_1,得56位key0
		replacement(key0, key1, PC_1);
		// 分为左右两段
		char[] C = new char[28];
		char[] D = new char[28];
		split(C, D, key1);
		// 得到C0，D0后
		String temp = null;
		for (int i = 0; i < 16; i++) {
			// 移位生成
			temp = desshift(C, D, i);
			replacement(temp.toCharArray(), keytable[i], PC_2);
		}
		return keytable;
	}

	//char转换为ASCII
	public static String chartoascii(char c) {
		String binary = "";
		int decimal = (int)c;
		//十进制转换为二进制，前面补1个0或2个0
		binary = decimaltoBinary(decimal,0);
		if(decimal >= 32&&decimal <= 63) {
			binary = "00" + binary;
		}else if(decimal >= 64&&decimal <= 128){
			binary = "0" + binary;
		}
		return binary;
	}
	
	//明文切割分段函数，每段64位
	public static String[] messagesplit(String messages) {
		
		int len = messages.length();
		int section = len/8;
		if ((len % 8) != 0) {
			section = section + 1;
		}
		String[] messagebox = new String[section];
		char[] words = messages.toCharArray();
		StringBuilder m = new StringBuilder(64);
		String wordstr = "";
		for(int i=0 ;i<8*section ;i++) {
			
			//非补位部分
			if(i <= len-1) {
				//对words[i]转换,获得8位二进制代码
				wordstr = chartoascii(words[i]);
				//将8位二进制代码拼接
				m.append(wordstr);
				//每转换8个字符,存入一位数组中
				if((i+1)%8==0) {
					messagebox[i/8] = m.toString();
					//保存后要对m清空
					m.delete(0, 64);
				}
			}else{
				wordstr = "00000000";
				m.append(wordstr);
				if((i+1)%8==0) {
					messagebox[i/8] = m.toString();
					m.delete(0, 64);
					}
			}
		}
		return messagebox;
	}

	//异或运算
	public static char[] xor(char[] messageRE, char[] k, char[] messageRExor) {
		int len = messageRE.length;
		for(int i=0;i<len;i++) {
			messageRExor[i] = String.valueOf(((int)(messageRE[i])^(int)(k[i]))).toCharArray()[0];
			//System.out.printf("%c^%c=%c\n",messageRE[i],k[i],messageRExor[i]);
		}
		return messageRExor;
	}
	
	//S盒代换
	public static void replacementS(char[]oldmessage ,char[]newmessage) {
		
		//一段切割为两端
		char[] half1 = new char[24];
		char[] half2 = new char[24];
		split(half1,half2,oldmessage);
		
		//两段切割为四段
		char[] quarter1 = new char[12];
		char[] quarter2 = new char[12];
		char[] quarter3 = new char[12];
		char[] quarter4 = new char[12];
		split(quarter1,quarter2,half1);
		split(quarter3,quarter4,half2);
		
		//四段切割为八段
		char[][] messagepiece = new char[8][6];
		split(messagepiece[0],messagepiece[1],quarter1);
		split(messagepiece[2],messagepiece[3],quarter2);
		split(messagepiece[4],messagepiece[5],quarter3);
		split(messagepiece[6],messagepiece[7],quarter4);
		
		String numpiece = "";
		for(int i=0;i<8;i++) {
			 //二进制转十进制
			 int x = (messagepiece[i][0]-'0')*2 + (messagepiece[i][5]-'0');
			 int y = (messagepiece[i][1]-'0')*8 + (messagepiece[i][2]-'0')*4 + (messagepiece[i][3]-'0')*2 + (messagepiece[i][4]-'0');
			 int t = S[i][x][y];
			 //十进制转二进制
			 String numstr="";
			 numstr = decimaltoBinary(t,1);
			 //获得经过S盒转换的子串
			 numpiece += numstr;
		}
		System.arraycopy(numpiece.toCharArray(), 0, newmessage, 0, newmessage.length);
	}
	
	//F函数，16次循环
	public static void Feistel(char[] messageL, char[] messageR, char[][] keybox,int flag) {
		char[] temp = new char[32];
		char[] messageRExor = new char[48];
		char[] messageRExorS = new char[32];
		char[] messageRExorSP = new char[32];
		char[] k = new char[48];
		
		//16次循环
		for (int i = 0; i < 16; i++) {
			temp = messageR.clone(); // 值传递方式赋值
			if(flag==0) {
				k = keybox[i]; // 获得子密钥
			}else if(flag==1) {
				k = keybox[(15-i)%16];// 获得子密钥，倒序
			}
			char[] messageRE = new char[48];
			
			// 右部密文扩充32->48,使用表E,获得48位messageRE
			replacement(messageR, messageRE, E);
			
			xor(messageRE,k,messageRExor);// 与k异或运算48->48
			
			// S盒代换48->32
			replacementS(messageRExor,messageRExorS);
			
			// P置换32->32
			replacement(messageRExorS,messageRExorSP,P);
			
			// 与左部异或运算32->32,值传递赋值给右部
			xor(messageRExorSP,messageL,messageR);
			
			//将右部赋值给左部
			System.arraycopy(temp, 0, messageL, 0, 32);
		}
	}

	//S表填写
	public static void Stableprepare(String filename,int[][][] table) throws IOException {
		String line = "";
		@SuppressWarnings("resource")
		BufferedReader reader  = new BufferedReader(new FileReader(filename));
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 4; j++) {
				int len = table[i][j].length;
				line = reader.readLine();
				String[] words = line.split(", ");
				for(int n=0; n<len; n++) {
					table[i][j][n] = Integer.parseInt(words[n]);
				}
			}
		}
	}
	
	//初始化填充表格
	public void init() throws IOException {
		// 填充表格
		tableprepare("E.txt", E);
		tableprepare("IP.txt", IP);
		tableprepare("IP_1.txt", IP_1);
		tableprepare("P.txt", P);
		tableprepare("PC_1.txt", PC_1);
		tableprepare("PC_2.txt", PC_2);
		tableprepare("RoundTable.txt", RoundTable);
		Stableprepare("S.txt",S);
	}
	
	//加密过程调度函数
	public void encrypt() throws IOException {
		
		char[] ciphertext = new char[64];
		char[] messageIP = new char[64];
		char[] messageL = new char[32];
		char[] messageR = new char[32];
		String[] keys = null;
		String messagef = null;
	
		init();

		//对密钥长度处理,对于超过64位的情况，只保留前64位，不足时，补0
		keys = messagesplit(key);		
		
		// 生成16个48位子密钥
		keybox = createkey(keys[0].toCharArray());
		
		//对明文切割分段
		String[] messagebox = messagesplit(message);
		
		//对每一段明文加密
		for (String m : messagebox) {
			//初始置换 64->64
			replacement(m.toCharArray(),messageIP,IP);
			
			//分为左右两块
			split(messageL,messageR,messageIP);
			
			//F函数16次循环加密
			Feistel(messageL,messageR, keybox, 0);
			
			//将左右拼接后逆初始置换IP_1 64->64
			messagef = String.valueOf(messageR) + String.valueOf(messageL);
			replacement(messagef.toCharArray(),ciphertext,IP_1);
			//密文拼接
			ciphertexts += String.valueOf(ciphertext);
		}
	}
}