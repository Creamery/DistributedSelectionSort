package com.message.messageQueue.Timeout;

import com.message.messageQueue.InProcessInfo;
import com.message.messageQueue.queueManager.QueueManager;

import java.util.TimerTask;

public class TimeoutTask extends TimerTask {

    private QueueManager mngr;
    private InProcessInfo parent;

    public TimeoutTask(QueueManager mngr, InProcessInfo parent){
        super();
        this.mngr = mngr;
        this.parent = parent;
    }

    @Override
    public void run() {
        this.mngr.timeout(parent);
    }
}
