package socket;

import java.io.IOException;

public class DesDecrypt {
	//解密
	// 初始56位密钥
	private String key = "";
	//子密钥
	public static char[][] keybox = new char[16][48];
	//明文
	public String message = "";
	//密文
	public String ciphertexts = ""; 
	
	//构造函数
	DesDecrypt(String ciphertexts,String key){
		this.key = key;
		this.ciphertexts = ciphertexts;
	}
	
	//密文分段，每段长度位length
	public static String[] ciphertextsplit(String ciphertext, int length) {
		//密文分段每length位位一组
		int len = ciphertext.length();
		int section = len/length;
		String[] ciphertextbox = new String[section];
		//以每组length位切片
		for(int i=0,j=0; i<section; i++,j+=length) {
			ciphertextbox[i] = ciphertext.substring(j, j+length);
		}
		return ciphertextbox;
	}
	
	//ASCII转char
	public static char asciitochar(String s) {
		char c = 0;
		char[] S = s.toCharArray();
		c = (char)((S[0]-'0')*128+(S[1]-'0')*64+(S[2]-'0')*32+(S[3]-'0')*16+(S[4]-'0')*8+(S[5]-'0')*4+(S[6]-'0')*2+(S[7]-'0')*1);
		return c;	
	}
	
	//调度函数
	public void decrypt() throws IOException {
		
		char[] ciphertextIP_1 = new char[64];
		char[] massages = new char[64];
		char[] ciphertextL = new char[32];
		char[] ciphertextR = new char[32];
		String[] keys = null;
		String messagestr = "";
		String ciphertextf = null;
		
		//构建加密实体,调用其方法
		DesEncrypt desencrypt = new DesEncrypt();
		
		//对对象初始化，主要是填写表格
		desencrypt.init();
		
		//对密钥长度处理,对于超过64位的情况，只保留前64位，不足时，补0
		keys = DesEncrypt.messagesplit(key);
		
		// 生成16个48位子密钥
		keybox = desencrypt.createkey(keys[0].toCharArray());
		
		//对密文分段
		String[] ciphertextbox = ciphertextsplit(ciphertexts,64);
		
		
		for(String c:ciphertextbox) {
			
			//最终置换
			DesEncrypt.replacement(c.toCharArray(), ciphertextIP_1, desencrypt.IP);
			
			//分片
			DesEncrypt.split(ciphertextL,ciphertextR,ciphertextIP_1);
			
			//解密F函数
			DesEncrypt.Feistel(ciphertextL, ciphertextR, keybox, 1);
		
			//将左右拼接后逆初始置换IP_1 64->64
			ciphertextf = String.valueOf(ciphertextR) + String.valueOf(ciphertextL);
			DesEncrypt.replacement(ciphertextf.toCharArray(),massages,desencrypt.IP_1);
			
			messagestr += String.valueOf(massages);
		}
		//转为字符,先分成8位二进制String
		String[] messagebox = ciphertextsplit(messagestr,8);
		StringBuilder m = new StringBuilder(messagebox.length);
		for(String w:messagebox) {
			if(w.equals("00000000")) break;
			m.append(asciitochar(w));
		}
		message = m.toString();
	}

}
