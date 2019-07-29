package com.chat.moduledynamic.bean.test;

import com.woniu.core.bean.entity.DynamicCommentData;
import com.woniu.core.bean.entity.DynamicItemData;

/**
 * Created by yiwei on 16/3/2.
 */
public class CommentConfig {
    public static enum Type{
        PUBLIC("public"), REPLY("reply");

        private String value;
        private Type(String value){
            this.value = value;
        }

    }

    public int circlePosition;
    public int commentPosition;
    public Type commentType;
    public DynamicItemData dynamicItemData;
    public DynamicCommentData replyComment;

}
