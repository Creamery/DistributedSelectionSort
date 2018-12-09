package com.message;

import java.io.Serializable;

/**
 * This class contains the indices to be swapped
 */
public class NotifySwapMessage implements Serializable {
    private int indexA;
    private int indexB;

    public NotifySwapMessage(int a, int b){
        this.indexA = a;
        this.indexB = b;
    }

    public int getIndexA(){
        return this.indexA;
    }

    public int getIndexB(){
        return this.indexB;
    }

    public String toString(){
        return "SWAP:"+indexA+"-"+indexB;
    }

    public static NotifySwapMessage parseString(String s){
        if(s.contains("SWAP:")){
            s = s.substring(5);
            String[] tokens = s.split("-");
            return new NotifySwapMessage(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
        }
        else
            return null;
    }
}
