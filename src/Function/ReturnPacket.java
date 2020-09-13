package Function;

public class ReturnPacket {
    private char command;
    private char mark;
    private String authentictor_tgs;

    private String resultPacket;

    private JudgeCommand judgeCommand;

    //发�?�报�?
    private String kcv;
    private char request;
    private String timeT4;
    private String replace;
    private String ticketV;

    //ticketV
    private String t_kcv;
    private String t_userId;
    private String t_timeT4;
    private String t_Lifetime4;

    public ReturnPacket(JudgeCommand judgeCommand){
        this.judgeCommand=judgeCommand;
    }

    public String GetResult(){
        ResultSet();
        return resultPacket;
    }

    private void ResultSet(){
        command=9;
        if(judgeCommand.Result()){
            mark=1;
            authentictor_tgs=SetAuthentic();
            resultPacket=command+mark+authentictor_tgs;
        }else {
            mark=0;
            authentictor_tgs=new String();
            for(int i=0;i<320;i++){
                authentictor_tgs+='0';
            }
            resultPacket=command+mark+authentictor_tgs;
        }
    }

    private String SetTicketV(){
        t_kcv=new String();//密钥
        t_userId=judgeCommand.t_userId;
        t_timeT4=new String();//时间�?
        t_Lifetime4=new String();//时间长度

        return t_kcv+t_userId+t_timeT4+t_Lifetime4;//ticketV的加�?
    }

    private String SetAuthentic(){
        kcv=t_kcv;
        request='a';//服务器v
        timeT4=t_timeT4;
        replace=new String();
        for(int i=0;i<=15;i++){
            replace+='0';
        }
        ticketV=SetTicketV();
        return kcv+request+timeT4+replace+ticketV;
    }
}
