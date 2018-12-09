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
}
