package com.woniu.core.xmpp.smack;

import android.util.Log;
import com.blankj.utilcode.util.ToastUtils;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

/**
 * 服务器连接监听
 * @author: laohu on 2017/1/18
 * @site: http://ittiger.cn
 */
public class SmackConnectionListener implements ConnectionListener {

    @Override
    public void connected(XMPPConnection connection) {
        ToastUtils.showLong("XMPP connected");
//        Log.i("FAN", "connection connected");
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        ToastUtils.showLong("XMPP authenticated");
//        Log.i("FAN", "connection authenticated");
    }

    @Override
    public void connectionClosed() {
        Log.i("FAN", "connectionClosed: ");
        ToastUtils.showLong("XMPP connectionClosed");
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        Log.i("FAN", "connectionClosedOnError: " + e.getMessage());
        ToastUtils.showLong("XMPP connectionClosedOnError " + e.getMessage());
    }

    @Override
    public void reconnectingIn(int seconds) {
        Log.i("FAN", "connection reconnectingIn " + seconds + " second");
    }

    @Override
    public void reconnectionFailed(Exception e) {
        Log.i("FAN", "reconnectionFailed");
        ToastUtils.showLong("XMPP reconnectionFailed " + e.getMessage());
    }

    @Override
    public void reconnectionSuccessful() {
        Log.i("FAN", "reconnectionSuccessful");
        ToastUtils.showLong("XMPP reconnectionSuccessful");
    }
}
