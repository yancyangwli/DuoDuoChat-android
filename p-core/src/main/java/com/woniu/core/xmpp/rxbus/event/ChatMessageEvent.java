package com.woniu.core.xmpp.rxbus.event;

import android.os.Parcel;
import android.os.Parcelable;
import com.woniu.core.manage.UserInfoManage;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

/**
 * @author Anlycal<远>
 * @date 2019/6/19
 * @description ...
 */


@Entity
public class ChatMessageEvent implements Parcelable {
    @Id(autoincrement = true)
    private Long id;
    private String fromUserID;
    private String toUserID;//接受者ID 或群ID
    private String messageContent;
    private int messageType = 1;
    private int imageHeight;
    private int imageWidth;
    private int voiceDuration;
    private String avatar;
    private String nickname;
    private String chatUserID;
    private long createTime;
    private boolean voiceIsRead = false;
    //红包标题
    private String redEnvelopesTitle;
    //群聊-自己在群里的昵称
    private String groupNickName;
    //群聊-群名称
    private String groupName;
    //群聊-群头像
    private String groupAvatar;

    private long userId = UserInfoManage.Companion.getGetInstance().getMUserInfoEntity().getUser_id();

    //群邀请
    private String invitationGroupName;//邀请的群名称
    private String invitationGroupAvatar;//邀请的群头像
    private String invitationPeople;//邀请人

    //    @Transient
    private int chatType;
    @Transient
    private String otherAvatar;
    @Transient
    private String otherName;


    @Generated(hash = 315524199)
    public ChatMessageEvent(Long id, String fromUserID, String toUserID, String messageContent,
                            int messageType, int imageHeight, int imageWidth, int voiceDuration, String avatar,
                            String nickname, String chatUserID, long createTime, boolean voiceIsRead,
                            String redEnvelopesTitle, String groupNickName, String groupName, String groupAvatar,
                            long userId, String invitationGroupName, String invitationGroupAvatar,
                            String invitationPeople, int chatType) {
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
        this.createTime = createTime;
        this.voiceIsRead = voiceIsRead;
        this.redEnvelopesTitle = redEnvelopesTitle;
        this.groupNickName = groupNickName;
        this.groupName = groupName;
        this.groupAvatar = groupAvatar;
        this.userId = userId;
        this.invitationGroupName = invitationGroupName;
        this.invitationGroupAvatar = invitationGroupAvatar;
        this.invitationPeople = invitationPeople;
        this.chatType = chatType;
    }

    @Generated(hash = 925048499)
    public ChatMessageEvent() {
    }


    protected ChatMessageEvent(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        fromUserID = in.readString();
        toUserID = in.readString();
        messageContent = in.readString();
        messageType = in.readInt();
        imageHeight = in.readInt();
        imageWidth = in.readInt();
        voiceDuration = in.readInt();
        avatar = in.readString();
        nickname = in.readString();
        chatUserID = in.readString();
        createTime = in.readLong();
        voiceIsRead = in.readByte() != 0;
        redEnvelopesTitle = in.readString();
        groupNickName = in.readString();
        groupName = in.readString();
        groupAvatar = in.readString();
        userId = in.readLong();
        invitationGroupName = in.readString();
        invitationGroupAvatar = in.readString();
        invitationPeople = in.readString();
        chatType = in.readInt();
        otherAvatar = in.readString();
        otherName = in.readString();
    }

    public static final Creator<ChatMessageEvent> CREATOR = new Creator<ChatMessageEvent>() {
        @Override
        public ChatMessageEvent createFromParcel(Parcel in) {
            return new ChatMessageEvent(in);
        }

        @Override
        public ChatMessageEvent[] newArray(int size) {
            return new ChatMessageEvent[size];
        }
    };

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

    public String getOtherAvatar() {
        return otherAvatar;
    }

    public void setOtherAvatar(String otherAvatar) {
        this.otherAvatar = otherAvatar;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    @Override
    public String toString() {
        return "ChatMessageEvent{" +
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
                ", createTime=" + createTime +
                '}';
    }

    public boolean getVoiceIsRead() {
        return this.voiceIsRead;
    }

    public void setVoiceIsRead(boolean voiceIsRead) {
        this.voiceIsRead = voiceIsRead;
    }

    public String getRedEnvelopesTitle() {
        return this.redEnvelopesTitle;
    }

    public void setRedEnvelopesTitle(String redEnvelopesTitle) {
        this.redEnvelopesTitle = redEnvelopesTitle;
    }

    public String getGroupNickName() {
        return this.groupNickName;
    }

    public void setGroupNickName(String groupNickName) {
        this.groupNickName = groupNickName;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public long getUserId() {
        return this.userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getGroupAvatar() {
        return this.groupAvatar;
    }

    public void setGroupAvatar(String groupAvatar) {
        this.groupAvatar = groupAvatar;
    }

    public String getInvitationGroupName() {
        return this.invitationGroupName;
    }

    public void setInvitationGroupName(String invitationGroupName) {
        this.invitationGroupName = invitationGroupName;
    }

    public String getInvitationGroupAvatar() {
        return this.invitationGroupAvatar;
    }

    public void setInvitationGroupAvatar(String invitationGroupAvatar) {
        this.invitationGroupAvatar = invitationGroupAvatar;
    }

    public String getInvitationPeople() {
        return this.invitationPeople;
    }

    public void setInvitationPeople(String invitationPeople) {
        this.invitationPeople = invitationPeople;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(fromUserID);
        dest.writeString(toUserID);
        dest.writeString(messageContent);
        dest.writeInt(messageType);
        dest.writeInt(imageHeight);
        dest.writeInt(imageWidth);
        dest.writeInt(voiceDuration);
        dest.writeString(avatar);
        dest.writeString(nickname);
        dest.writeString(chatUserID);
        dest.writeLong(createTime);
        dest.writeByte((byte) (voiceIsRead ? 1 : 0));
        dest.writeString(redEnvelopesTitle);
        dest.writeString(groupNickName);
        dest.writeString(groupName);
        dest.writeString(groupAvatar);
        dest.writeLong(userId);
        dest.writeString(invitationGroupName);
        dest.writeString(invitationGroupAvatar);
        dest.writeString(invitationPeople);
        dest.writeInt(chatType);
        dest.writeString(otherAvatar);
        dest.writeString(otherName);
    }
}
