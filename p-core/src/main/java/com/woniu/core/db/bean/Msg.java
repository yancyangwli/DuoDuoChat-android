package com.woniu.core.db.bean;

/*
 * 项目名:    SmackChat
 * 包名       com.azhong.smackchat.common.db.bean
 * 文件名:    Msg
 * 创建者:    ZJB
 * 创建时间:  2017/3/6 on 15:58
 * 描述:     TODO 消息表对应的bean
 */
public class Msg {
    public static final int SELF_MSG = 1;//自己的消息
    public static final int FRIENDS_MSG = 2;//对方的消息
    int msg_id;
    int msg_list_id;
    String from_name;
    String msg_content;
    String msg_time;
    String msg_type;
    int from_type;

    public int getFrom_type() {
        return from_type;
    }

    public void setFrom_type(int from_type) {
        this.from_type = from_type;
    }

    public Msg(int msg_id, int msg_list_id, String from_name, String msg_content, String msg_time, String msg_type, int from_type) {
        this.msg_id = msg_id;
        this.msg_list_id = msg_list_id;
        this.from_name = from_name;
        this.msg_content = msg_content;
        this.msg_time = msg_time;
        this.msg_type = msg_type;
        this.from_type = from_type;
    }

    public Msg(String from_name, String msg_content, String msg_time, String msg_type, int from_type) {
        this.from_name = from_name;
        this.msg_content = msg_content;
        this.msg_time = msg_time;
        this.msg_type = msg_type;
        this.from_type = from_type;
    }

    public int getMsg_list_id() {
        return msg_list_id;
    }

    public void setMsg_list_id(int msg_list_id) {
        this.msg_list_id = msg_list_id;
    }

    public String getFrom_name() {
        return from_name;
    }

    public void setFrom_name(String from_name) {
        this.from_name = from_name;
    }

    public String getMsg_content() {
        return msg_content;
    }

    public void setMsg_content(String msg_content) {
        this.msg_content = msg_content;
    }

    public String getMsg_time() {
        return msg_time;
    }

    public void setMsg_time(String msg_time) {
        this.msg_time = msg_time;
    }

    public String getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(String msg_type) {
        this.msg_type = msg_type;
    }

    public int getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(int msg_id) {
        this.msg_id = msg_id;
    }
}
