package com.synseaero.fpv;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;

import com.synseaero.dji.MessageType;

import cn.feng.skin.manager.base.BaseFragmentActivity;


public class DJIActivity extends BaseFragmentActivity {

    private FPVApplication app;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (FPVApplication) getApplication();
    }

    public boolean sendDJIMessage(Message message) {
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

    public boolean sendWatchDJIMessage(int messageId, int flag) {

        Bundle bundle = new Bundle();
        bundle.putInt("flag", flag);
        Message message = Message.obtain();
        message.what = messageId;
        message.setData(bundle);
        return app.sendDJIMessage(message);
    }

    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.common_style, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_style_1: {
                ((FPVApplication) getApplication()).changeSkin(1);
                break;
            }
            case R.id.menu_style_2: {
                ((FPVApplication) getApplication()).changeSkin(2);
                break;
            }
            case R.id.menu_style_3: {
                ((FPVApplication) getApplication()).changeSkin(3);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
