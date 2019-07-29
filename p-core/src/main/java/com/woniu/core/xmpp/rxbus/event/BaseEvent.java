package com.woniu.core.xmpp.rxbus.event;

/**
 * @author Anlycal<远>
 * @date 2019/6/24
 * @description ...
 */


public class BaseEvent {

    public enum TYPE {
        TEXT(1),//文本
        RECORD_VOICE(2),//录音
        IMAGE(3),//图片
        RED_PACK(4),//红包
        CARD(5),//名片
        GROUP_INVITE(6),//群邀请
        TEXT_MY(200),//我发送的文本
        RECORD_VOICE_MY(201),//我发送的语音
        IMAGE_MY(202),//我发送的图片
        RED_PACK_MY(203),//我发送的红包
        CARD_MY(204),//我推荐的名片
        GROUP_INVITE_MY(205);//我邀请别人加入的群聊信息

        private int typeValue;

        public int getTypeValue() {
            return typeValue;
        }

        TYPE(int typeValue) {
            this.typeValue = typeValue;
        }

    }

    public enum CHAT_TYPE{
        PERSONAL(1),//单聊
        GROUP(2),//群聊
        OTHER(3);//其他类型

        private int typeValue;

        public int getTypeValue(){
            return typeValue;
        }

        CHAT_TYPE(int typeValue){
            this.typeValue = typeValue;
        }
    }
}
