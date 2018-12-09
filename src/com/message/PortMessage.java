package com.message;

public class PortMessage {
    private int portNo;

    public PortMessage(int portNo){
        this.portNo = portNo;
    }

    public int getPortNo(){
        return this.portNo;
    }

    public String toString(){
        return "PORT:"+portNo;
    }

    public static PortMessage parsePortMsg(String msg){
        if (msg.contains("PORT:")) {
            msg = msg.substring(5).trim();
            return new PortMessage(Integer.parseInt(msg));
        }
        else{
            return null;
        }
    }
}
