package com.woniu.core.db;

import android.util.Log;

import com.blankj.utilcode.util.ToastUtils;
import com.woniu.core.BaseApplication;
import com.woniu.core.dao.ChatMessageEventDao;
import com.woniu.core.dao.MessageTempEventDao;
import com.woniu.core.xmpp.rxbus.event.ChatMessageEvent;
import com.woniu.core.xmpp.rxbus.event.MessageTempEvent;

import java.util.List;

/*
 * 项目名:    SmackChat
 */
public class DbHelper {
    /**
     * @return
     */
    public static List<ChatMessageEvent> getChatMessageList() {
        try {
            return getChatMessageEventDao().loadAll();
        } catch (Exception e) {
            ToastUtils.showLong("查询异常getChatMessageList---> " + e.getMessage());
            return null;
        }
    }


    /**
     * 根据用户id
     *
     * @param user_id
     * @return
     */
    public static List<ChatMessageEvent> getChatMessageFromUserId(String user_id) {
        try {
            return getChatMessageEventDao().queryRaw("where CHAT_USER_ID=?", user_id);
        } catch (Exception e) {
            Log.i("FAN", "查询异常getChatMessageFromUserId---> " + e.getMessage());
            return null;
        }
    }

    public static List<ChatMessageEvent> queryChatMessagePage(String user_id, int offset) {
        try {
            return getChatMessageEventDao().queryBuilder().orderDesc(ChatMessageEventDao.Properties.Id)
                    .offset(offset * 10).limit(10).where(ChatMessageEventDao.Properties.ChatUserID.eq(user_id)).list();
        } catch (Exception e) {
            Log.i("FAN", "查询异常getChatMessageFromUserId---> " + e.getMessage());
            return null;
        }
    }

    /**
     * 更新聊天记录
     *
     * @return
     */
    public static boolean updateChatMessage(ChatMessageEvent event) {
        try {
            getChatMessageEventDao().update(event);
            return true;
        } catch (Exception e) {
            Log.i("FAN", "更新异常updateChatMessage----> " + e.getMessage());
            return false;
        }
    }


    public static boolean deleteChatMessageAll() {
        try {
            getChatMessageEventDao().deleteAll();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 删除某个群的聊天记录
     *
     * @return
     */
    public static boolean deleteChatMessageByGroupId(int groupId) {
        try {
            getChatMessageEventDao().queryBuilder().where(ChatMessageEventDao.Properties.ToUserID.eq(groupId)).buildDelete().executeDeleteWithoutDetachingEntities();
            return true;
        } catch (Exception e) {
            ToastUtils.showLong("删除数据失败 deleteMessageTempEventAll--> " + e.getMessage());
            return false;
        }
    }

    public static long insertChatMessage(ChatMessageEvent event) {
        try {
            return getChatMessageEventDao().insert(event);
        } catch (Exception e) {
            Log.i("FAN", "插入异常insertChatMessage----> " + e.getMessage());
            return -1;
        }
    }

    public static long insertMessageTempEvent(MessageTempEvent event) {
        try {
            return getMessageTempEventDao().insert(event);
        } catch (Exception e) {
            Log.i("FAN", "插入错误insertMessageTempEvent ---> " + e.getMessage());
            return -1;
        }
    }

    /**
     * 查询临时会话列表数据
     *
     * @return
     */
    public static List<MessageTempEvent> getMessageTempList() {
        try {
            return getMessageTempEventDao().loadAll();
        } catch (Exception e) {
            ToastUtils.showLong("查询异常 getMessageTempList--> " + e.getMessage());
            return null;
        }
    }

    /**
     * 根据用户ID查询临时会话列表数据
     *
     * @param userId
     * @return
     */
    public static List<MessageTempEvent> getMessageTempListByUserId(int userId) {
        try {
            return getMessageTempEventDao().queryRaw("where USER_ID=?", String.valueOf(userId));
        } catch (Exception e) {
            ToastUtils.showLong("查询异常 getMessageTempList--> " + e.getMessage());
            return null;
        }
    }

    /**
     * 根据名称查询临时会话列表数据
     *
     * @param nickname
     * @return
     */
    public static List<MessageTempEvent> getMessageTempByNickname(String nickname) {
        try {
            return getMessageTempEventDao().queryRaw("where NICKNAME like ?", "%" + nickname + "%");
        } catch (Exception e) {
            ToastUtils.showLong("查询异常 getMessageTempList--> " + e.getMessage());
            return null;
        }
    }

    /**
     * 更新临时会话列表数据
     */
    public static boolean updateMessageTempEvent(MessageTempEvent event) {
        try {
            getMessageTempEventDao().update(event);
            return true;
        } catch (Exception e) {
            ToastUtils.showLong("更新数据失败 updateMessageTempEvent--> " + e.getMessage());
            return false;
        }
    }

    /**
     * 删除临时会话列表的单个数据
     *
     * @param event
     * @return
     */
    public static boolean deleteMessageTempEvent(MessageTempEvent event) {
        try {
            getMessageTempEventDao().delete(event);
            return true;
        } catch (Exception e) {
            ToastUtils.showLong("删除数据失败 deleteMessageTempEvent--> " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteMessageTempEventByRowId(long rowId) {
        try {
            getMessageTempEventDao().deleteByKey(rowId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 删除所有临时会话列表
     *
     * @return
     */
    public static boolean deleteMessageTempEventAll() {
        try {
            getMessageTempEventDao().deleteAll();
            return true;
        } catch (Exception e) {
            ToastUtils.showLong("删除数据失败 deleteMessageTempEventAll--> " + e.getMessage());
            return false;
        }
    }

    /**
     * 删除某个用户的临时会话列表数据
     *
     * @return
     */
    public static boolean deleteMessageTempEventAllByUserId(int userId) {
        try {
            getMessageTempEventDao().queryBuilder().where(MessageTempEventDao.Properties.UserId.eq(userId)).buildDelete().executeDeleteWithoutDetachingEntities();
            return true;
        } catch (Exception e) {
            ToastUtils.showLong("删除数据失败 deleteMessageTempEventAll--> " + e.getMessage());
            return false;
        }
    }

    private static ChatMessageEventDao getChatMessageEventDao() {
        return BaseApplication.mDaoSession.getChatMessageEventDao();
    }

    private static MessageTempEventDao getMessageTempEventDao() {
        return BaseApplication.mDaoSession.getMessageTempEventDao();
    }
}
