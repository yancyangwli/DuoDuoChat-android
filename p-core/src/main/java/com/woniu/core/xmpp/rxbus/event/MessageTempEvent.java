package com.woniu.core.xmpp.rxbus.event;

import com.woniu.core.manage.UserInfoManage;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author Anlycal<远>
 * @date 2019/6/24
 * @description ...
 */

@Entity
public class MessageTempEvent {

    @Id(autoincrement = true)
    private Long id;

    private String fromUserID;
    private String toUserID;
    private String messageContent;
    private int messageType = 1;
    private int imageHeight;
    private int imageWidth;
    private int voiceDuration;
    private String avatar;
    private String nickname;
    private String chatUserID;
    private int unReadNumber;//未读数量
    private int chatType;//聊天类型
    private long createTime;
    private long userId = UserInfoManage.Companion.getGetInstance().getMUserInfoEntity().getUser_id();

    @Generated(hash = 543533959)
    public MessageTempEvent(Long id, String fromUserID, String toUserID, String messageContent,
            int messageType, int imageHeight, int imageWidth, int voiceDuration, String avatar,
            String nickname, String chatUserID, int unReadNumber, int chatType, long createTime,
            long userId) {
        this.id = id;
        this.fromUserID = fromUserID;
        this.toUserID = toUserID;
        this.messageContent = messageContent;
        this.messageType = messageType;
        this.imageHeight = imageHeight;
        this.imageWidth = imageWidth;
        this.voiceDuration = voiceDuration;
        this.avatar = avatar;
        this.nickname = nickname;
        this.chatUserID = chatUserID;
        this.unReadNumber = unReadNumber;
        this.chatType = chatType;
        this.createTime = createTime;
        this.userId = userId;
    }

    @Generated(hash = 133724734)
    public MessageTempEvent() {
    }






    public String getFromUserID() {
        return fromUserID;
    }

    public void setFromUserID(String fromUserID) {
        this.fromUserID = fromUserID;
    }

    public String getToUserID() {
        return toUserID;
    }

    public void setToUserID(String toUserID) {
        this.toUserID = toUserID;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getVoiceDuration() {
        return voiceDuration;
    }

    public void setVoiceDuration(int voiceDuration) {
        this.voiceDuration = voiceDuration;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getChatUserID() {
        return chatUserID;
    }

    public void setChatUserID(String chatUserID) {
        this.chatUserID = chatUserID;
    }

    public int getUnReadNumber() {
        return unReadNumber;
    }

    public void setUnReadNumber(int unReadNumber) {
        this.unReadNumber = unReadNumber;
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "MessageTempEvent{" +
                "id=" + id +
                ", fromUserID='" + fromUserID + '\'' +
                ", toUserID='" + toUserID + '\'' +
                ", messageContent='" + messageContent + '\'' +
                ", messageType=" + messageType +
                ", imageHeight=" + imageHeight +
                ", imageWidth=" + imageWidth +
                ", voiceDuration=" + voiceDuration +
                ", avatar='" + avatar + '\'' +
                ", nickname='" + nickname + '\'' +
                ", chatUserID='" + chatUserID + '\'' +
                ", unReadNumber=" + unReadNumber +
                ", chatType=" + chatType +
                ", createTime=" + createTime +
                '}';
    }

    public long getUserId() {
        return this.userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
