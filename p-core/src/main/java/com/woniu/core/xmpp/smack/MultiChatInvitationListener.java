package com.woniu.core.xmpp.smack;

import android.util.Log;
import com.orhanobut.logger.Logger;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;

/**
 * 多人聊天邀请监听
 *
 * @author: laohu on 2017/1/24
 * @site: http://ittiger.cn
 */
public class MultiChatInvitationListener implements InvitationListener {
    private static final String TAG = "yancy";

    @Override
    public void invitationReceived(XMPPConnection conn, MultiUserChat room, String inviter,
                                   String reason, String password, Message message) {
        Log.e(TAG, "invitationReceived: 多人聊天邀请监听" + message.toString());
//
//        try {
//            room.join(LoginHelper.getUser().getNickname());
//            SmackMultiChatManager.saveMultiChat(room);
//            SmackListenerManager.addMultiChatMessageListener(room);
//
//            String friendUserName = room.getRoom();
//            int idx = friendUserName.indexOf(Constant.MULTI_CHAT_ADDRESS_SPLIT);
//            String friendNickName = friendUserName.substring(0, idx);
//        } catch (Exception e) {
//            Logger.e(e, "join multiChat failure on invitationReceived");
//        }
    }
}
