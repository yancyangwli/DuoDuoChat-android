package com.cosven.message.impl;

import android.view.View;
import com.woniu.core.xmpp.rxbus.event.ChatMessageEvent;

/**
 * @author Anlycal<远>
 * @date 2019/6/26
 * @description ...
 */


public interface OnChatMessageListClickedListener {
    void onImageClicked(int position, ChatMessageEvent message);

    void onTextLongClicked(int position, ChatMessageEvent message, View view);

    void onVoiceClicked(int position, ChatMessageEvent message);

    void onAvatarClicked(int position, ChatMessageEvent message);

    void onRedPackClicked(int positon, ChatMessageEvent message);

    void onInviteClicked(int positon, ChatMessageEvent message);

}
