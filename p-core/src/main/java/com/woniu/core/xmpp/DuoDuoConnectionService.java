package com.woniu.core.xmpp;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.blankj.utilcode.util.SPUtils;
import com.orhanobut.logger.Logger;
import com.woniu.core.api.Config;
import com.woniu.core.bean.entity.GroupEntity;
import com.woniu.core.bean.entity.UserInfoEntity;
import com.woniu.core.manage.GroupListManage;
import com.woniu.core.manage.UserInfoManage;
import com.woniu.core.xmpp.smack.MultiChatMessageListener;
import com.woniu.core.xmpp.smack.SmackChatManagerListener;
import com.woniu.core.xmpp.smack.SmackConnectionListener;
import com.woniu.core.xmpp.smack.SmackManager;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.json.JSONObject;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DuoDuoConnectionService extends Service {

    private SSLContext mSSLContext;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        dbHelper = new DbHelper(this);
        addXMPPListener();
        return super.onStartCommand(intent, flags, startId);
    }


    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public DuoDuoConnectionService getService() {
            return DuoDuoConnectionService.this;
        }
    }

    /**
     * 添加xmpp的消息监听
     */
    private void addXMPPListener(){
        //单聊的监听
        SmackManager.getInstance().getChatManager().addChatListener(new SmackChatManagerListener());

        //循环加入群聊并监听
        List<GroupEntity> groupEntityList = GroupListManage.Companion.getGetInstance().getGroupList();
        if (groupEntityList != null && groupEntityList.size() > 0) {
//            String mUser = SmackManager.getInstance().getConnection().getUser();
            UserInfoEntity userInfoEntity = UserInfoManage.Companion.getGetInstance().getMUserInfoEntity();
            for (GroupEntity groupEntity: groupEntityList) {
                MultiUserChat multiUserChat = SmackManager.getInstance().getMultiChat(String.valueOf(groupEntity.getGroup_id()));
                try {
//                    multiUserChat.join(mUser.split("/")[0].split("@")[0]);
                    multiUserChat.join(String.valueOf(userInfoEntity.getUser_id()));
                    multiUserChat.addMessageListener(new MultiChatMessageListener() {
                        @Override
                        public void processMessage(Message message) {
                            super.processMessage(message);
                        }
                    });
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }
        }
//        ChatManager.getInstanceFor(mConnection).addChatListener(new SmackChatManagerListener());
    }

}
