package com.woniu.core.xmpp.smack;

import android.util.Log;
import com.blankj.utilcode.util.SPUtils;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.woniu.core.api.Config;
import com.woniu.core.db.DbHelper;
import com.woniu.core.utils.GsonUtil;
import com.woniu.core.xmpp.rxbus.RxBus;
import com.woniu.core.xmpp.rxbus.event.BaseEvent;
import com.woniu.core.xmpp.rxbus.event.ChatMessageEvent;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Smack普通消息监听处理
 *
 * @author: laohu on 2017/1/18
 * @site: http://ittiger.cn
 */
public class SmackChatManagerListener implements ChatManagerListener {
    public static final String PATTERN = "[a-zA-Z0-9_]+@";

    private static final String TAG = "yancy";
    private String mMineUserId = "";

    public SmackChatManagerListener() {
//        Log.i("FAN", "SmackChatManagerListener: 监听");
        mMineUserId = String.valueOf(SPUtils.getInstance().getInt(Config.Constant.DUODUO_USER_ID));
    }

    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {
        Log.e(TAG, "processMessage: Smack普通消息监听处理");
        chat.addMessageListener(new ChatMessageListener() {

            @Override
            public void processMessage(Chat chat, Message message) {
//                Log.i("FAN", "processMessage: 接收线程----> " + Thread.currentThread().getName());
                String subject = message.getSubject();
//                Log.i("FAN", "processMessage: -->type---> " + message.getType() + "   subject--->" + subject );

                //不会收到自己发送过来的消息
                String from = message.getFrom();//消息发送人，格式:laohu@171.17.100.201/Smack
                Log.i("FAN", "接收到消息： from---> " + from + "    to--> " + message.getTo() + "    body---> " + message.getBody());

                Log.e(TAG, "processMessage: Smack普通消息监听处理" + message.getTo());
//                String stanzaId = message.getStanzaId();

                String to = message.getTo();//消息接收人(当前登陆用户)，格式:laohu@171.17.100.201/Smack
                Matcher matcherFrom = Pattern.compile(PATTERN).matcher(from);
                Matcher matcherTo = Pattern.compile(PATTERN).matcher(to);

                if (matcherFrom.find() && matcherTo.find()) {
                    try {
                        String fromUser = matcherFrom.group(0);
                        fromUser = fromUser.substring(0, fromUser.length() - 1);//去掉@
                        String toUser = matcherTo.group(0);
                        toUser = toUser.substring(0, toUser.length() - 1);//去掉@
                        ChatMessageEvent messageEvent = GsonUtil.getGson().fromJson(message.getBody(), ChatMessageEvent.class);
                        messageEvent.setFromUserID(fromUser);
                        messageEvent.setToUserID(toUser);
                        if (fromUser != mMineUserId) {
                            messageEvent.setChatUserID(fromUser);
                        } else {
                            messageEvent.setChatUserID(toUser);
                        }
                        messageEvent.setCreateTime(System.currentTimeMillis());
                        messageEvent.setChatType(BaseEvent.CHAT_TYPE.PERSONAL.getTypeValue());

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
        });
    }
}
