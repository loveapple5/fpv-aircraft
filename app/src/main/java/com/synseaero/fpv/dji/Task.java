package com.synseaero.fpv.dji;

import android.os.Bundle;
import android.os.Messenger;


public abstract class Task implements Runnable {

    protected Messenger messenger;
    protected Bundle data;

    public Task(Bundle data, Messenger messenger) {
        this.data = data;
        this.messenger = messenger;
    }

}
