package com.message.MessageQueue.Timeout;

import com.message.MessageQueue.InProcessInfo;
import com.message.MessageQueue.QueueManager;

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
