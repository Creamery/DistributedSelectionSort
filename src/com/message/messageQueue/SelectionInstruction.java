package com.message.messageQueue;

import java.io.Serializable;

public class SelectionInstruction implements Serializable {
    private int startIndex;
    private int endIndex;

    public SelectionInstruction(int startIndex, int endIndex){
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public int getStartIndex(){
        return this.startIndex;
    }

    public int getEndIndex(){
        return this.endIndex;
    }

    public String toString(){
        return "INSTR:"+startIndex+"-"+endIndex;
    }

    public static SelectionInstruction parseString(String s){
        if(s.contains("INSTR:")){
            s = s.substring(6);
            String[] tokens = s.split("-");
            return new SelectionInstruction(Integer.parseInt(tokens[0]),
                    Integer.parseInt(tokens[1]));
        }else{
            return null;
        }
    }

}