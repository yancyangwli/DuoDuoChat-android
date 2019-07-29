package com.woniu.core.bean;

public class ResultInfo {
    private int user_id, friend_user_id;

    private String friend_avatar, friend_remark, friend_nick;

    private char friend_group;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getFriend_user_id() {
        return friend_user_id;
    }

    public void setFriend_user_id(int friend_user_id) {
        this.friend_user_id = friend_user_id;
    }

    public String getFriend_avatar() {
        return friend_avatar;
    }

    public void setFriend_avatar(String friend_avatar) {
        this.friend_avatar = friend_avatar;
    }

    public String getFriend_remark() {
        return friend_remark;
    }

    public void setFriend_remark(String friend_remark) {
        this.friend_remark = friend_remark;
    }

    public String getFriend_nick() {
        return friend_nick;
    }

    public void setFriend_nick(String friend_nick) {
        this.friend_nick = friend_nick;
    }

    public char getFriend_group() {
        return friend_group;
    }

    public void setFriend_group(char friend_group) {
        this.friend_group = friend_group;
    }

    @Override
    public String toString() {
        return "ResultInfo{" +
                "user_id=" + user_id +
                ", friend_user_id=" + friend_user_id +
                ", friend_avatar='" + friend_avatar + '\'' +
                ", friend_remark='" + friend_remark + '\'' +
                ", friend_nick='" + friend_nick + '\'' +
                ", friend_group=" + friend_group +
                '}';
    }
}

