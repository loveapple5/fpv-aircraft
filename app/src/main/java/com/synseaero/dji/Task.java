package com.synseaero.dji;

import android.os.Bundle;
import android.os.Messenger;


public abstract class Task implements Runnable {

    protected Messenger messenger;
    protected Bundle data;
    protected int flag = 0;

    public Task(Bundle data, Messenger messenger) {
        this.data = data;
        this.messenger = messenger;
        flag = data.getInt("flag", 0);
    }

}
