package com.woniu.core.xmpp.smack;

import android.util.Log;
import com.blankj.utilcode.util.SPUtils;
import com.orhanobut.logger.Logger;
import com.woniu.core.api.Config;
import com.woniu.core.db.DbHelper;
import com.woniu.core.utils.GsonUtil;
import com.woniu.core.xmpp.rxbus.RxBus;
import com.woniu.core.xmpp.rxbus.event.BaseEvent;
import com.woniu.core.xmpp.rxbus.event.ChatMessageEvent;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 多人聊天消息监听
 *
 * @author: laohu on 2017/1/24
 * @site: http://ittiger.cn
 */
public class MultiChatMessageListener implements MessageListener {
    private static final String PATTERN = "[a-zA-Z0-9_]+@";

    private static final String TAG = "yancy";
    private String mMineUserId = "";

    public MultiChatMessageListener() {
//        Log.i("FAN", "SmackChatManagerListener: 监听");
        mMineUserId = String.valueOf(SPUtils.getInstance().getInt(Config.Constant.DUODUO_USER_ID));
    }

    @Override
    public void processMessage(Message message) {
        Log.e(TAG, "processMessage: 多人聊天消息监听=" + message.toString());
        //不会收到自己发送过来的消息
        Log.i("MultiChatMessage", message.toString());
        String from = message.getFrom();//消息发送人，格式:老胡创建的群@conference.121.42.13.79/老胡     --> 老胡发送的
        String to = message.getTo();//消息接收人(当前登陆用户)，格式:zhangsan@121.42.13.79/Smack
        Matcher matcherFrom = Pattern.compile(PATTERN).matcher(from);
        Matcher matcherTo = Pattern.compile(PATTERN).matcher(to);

        if (matcherTo.find() && matcherFrom.find()) {
            try {
                String[] fromUsers = from.split("/");
//                String friendUserName = fromUsers[0];//老胡创建的群@conference.121.42.13.79
                String friendNickName = fromUsers[1];//发送人的昵称，用于聊天窗口中显示
                if (friendNickName.equals(mMineUserId)) return;
//
//                Log.i("FAN", "processMessage: " + from);
//                JSONObject json = new JSONObject(message.getBody());

                String fromUser = matcherFrom.group(0);
                fromUser = fromUser.substring(0, fromUser.length() - 1);//去掉@
                String toUser = matcherTo.group(0);
                toUser = toUser.substring(0, toUser.length() - 1);//去掉@
                ChatMessageEvent messageEvent = GsonUtil.getGson().fromJson(message.getBody(), ChatMessageEvent.class);
                messageEvent.setFromUserID(fromUser);
                messageEvent.setToUserID(toUser);
                Log.i("FAN", "messageEvent.getAvatar(): " + messageEvent.getAvatar());
                if (fromUser != mMineUserId) {
                    messageEvent.setChatUserID(fromUser);
                } else {
                    messageEvent.setChatUserID(toUser);
                }
                messageEvent.setCreateTime(System.currentTimeMillis());
                messageEvent.setChatType(BaseEvent.CHAT_TYPE.GROUP.getTypeValue());

                long rowId = DbHelper.insertChatMessage(messageEvent);
                messageEvent.setId(rowId);
                RxBus.getInstance().post(messageEvent);

            } catch (Exception e) {
                Logger.e(e, "发送的消息格式不正确");
            }
        } else {
            Logger.e("发送人格式不正确");
        }
    }
}
