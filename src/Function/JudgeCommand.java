package Function;

public class JudgeCommand {
    private char staticCommand='3';
    private char staticServerId='1';

    //拆包后的第一�?
    public String ticketTgs;
    public String authemticator;
    public char command;
    public char serverId;

    //ticket的内�?
    public String t_kc_tgs;
    public String t_userId;
    public String t_ads;
    public char t_request;
    public String t_timeT2;
    public String t_Lifetime2;

    //认证的内�?
    public String a_idc;
    public String a_ads;
    public String a_timeT3;

    public String input;
    public JudgeCommand(String input){
        this.input=input;
    }

    //拆第�?�?
    private boolean split(){
        if(input.length()==1218) {
            command = input.charAt(0);
            serverId = input.charAt(1);
            ticketTgs = input.substring(2, 898);
            authemticator = input.substring(898, 1218);
            System.out.println("command:"+command+"    serverId:"+serverId);
            if(IsCommandSame()&&IsServerIdSame()) return true;
            else return false;
        }else {
            return false;
        }
    }

    //拆第二层
    private boolean Decryption(){
        String afterDecryptTicketTgs=new String();//=function(ticketTgs)解密程序
        String afterDecryptAuthemticator=new String();//=function(ticketTgs)解密程序

        if(afterDecryptTicketTgs.length()==112&&afterDecryptAuthemticator.length()==34) {
            t_kc_tgs = afterDecryptTicketTgs.substring(0, 64);
            System.out.println("tgs:"+t_kc_tgs);
            t_userId = afterDecryptTicketTgs.substring(64, 73);
            System.out.println("用户id�?"+t_userId);
            t_ads = afterDecryptTicketTgs.substring(73, 85);
            System.out.println("ads"+t_ads);
            t_request = afterDecryptTicketTgs.charAt(85);
            System.out.println("request"+t_request);
            t_timeT2 = afterDecryptTicketTgs.substring(86, 99);
            System.out.println("时间�?:"+t_timeT2);
            t_Lifetime2 = afterDecryptTicketTgs.substring(99, 111);
            System.out.println("存在时间�?"+t_Lifetime2);

            a_idc = afterDecryptAuthemticator.substring(0, 9);
            System.out.println("idc:"+a_idc);
            a_ads = afterDecryptAuthemticator.substring(9, 21);
            System.out.println("ads:"+a_ads);
            a_timeT3 = afterDecryptAuthemticator.substring(21, 34);
            System.out.println("时间戳："+a_timeT3);
            return IsJudgeSuccess();
        }else return false;
    }

    private boolean IsCommandSame(){
        return command==staticCommand;
    }
    private boolean IsServerIdSame(){
        return serverId==staticServerId;
    }
    private boolean IsJudgeSuccess(){
        return t_kc_tgs.equals(a_idc)&&t_ads.equals(a_ads)&&IsTimeSuccess();
    }
    private boolean IsTimeSuccess(){
        //时间比较
        return true;
    }

    public boolean Result(){
        if(split()&&Decryption())return true;
        else return false;
    }
}
