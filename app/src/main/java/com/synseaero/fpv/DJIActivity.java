package com.synseaero.fpv;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.FragmentActivity;

import com.synseaero.dji.MessageType;


public class DJIActivity extends FragmentActivity {

    private FPVApplication app;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (FPVApplication) getApplication();
    }

    protected boolean sendDJIMessage(Message message) {
        return app.sendDJIMessage(message);
    }

    public boolean registerDJIMessenger(int messageId, Messenger messenger) {
        Bundle bundle = new Bundle();
        bundle.putInt("registerMessageId", messageId);
        Message message = Message.obtain();
        message.what = MessageType.MSG_REGISTER_MESSENGER;
        message.replyTo = messenger;
        message.setData(bundle);
        return app.sendDJIMessage(message);
    }

    public boolean unregisterDJIMessenger(int messageId, Messenger messenger) {
        Bundle bundle = new Bundle();
        bundle.putInt("registerMessageId", messageId);
        Message message = Message.obtain();
        message.what = MessageType.MSG_UNREGISTER_MESSENGER;
        message.replyTo = messenger;
        message.setData(bundle);
        return app.sendDJIMessage(message);
    }

}
