package com.woniu.core.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.woniu.core.xmpp.rxbus.event.MessageTempEvent;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "MESSAGE_TEMP_EVENT".
*/
public class MessageTempEventDao extends AbstractDao<MessageTempEvent, Long> {

    public static final String TABLENAME = "MESSAGE_TEMP_EVENT";

    /**
     * Properties of entity MessageTempEvent.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property FromUserID = new Property(1, String.class, "fromUserID", false, "FROM_USER_ID");
        public final static Property ToUserID = new Property(2, String.class, "toUserID", false, "TO_USER_ID");
        public final static Property MessageContent = new Property(3, String.class, "messageContent", false, "MESSAGE_CONTENT");
        public final static Property MessageType = new Property(4, int.class, "messageType", false, "MESSAGE_TYPE");
        public final static Property ImageHeight = new Property(5, int.class, "imageHeight", false, "IMAGE_HEIGHT");
        public final static Property ImageWidth = new Property(6, int.class, "imageWidth", false, "IMAGE_WIDTH");
        public final static Property VoiceDuration = new Property(7, int.class, "voiceDuration", false, "VOICE_DURATION");
        public final static Property Avatar = new Property(8, String.class, "avatar", false, "AVATAR");
        public final static Property Nickname = new Property(9, String.class, "nickname", false, "NICKNAME");
        public final static Property ChatUserID = new Property(10, String.class, "chatUserID", false, "CHAT_USER_ID");
        public final static Property UnReadNumber = new Property(11, int.class, "unReadNumber", false, "UN_READ_NUMBER");
        public final static Property ChatType = new Property(12, int.class, "chatType", false, "CHAT_TYPE");
        public final static Property CreateTime = new Property(13, long.class, "createTime", false, "CREATE_TIME");
        public final static Property UserId = new Property(14, long.class, "userId", false, "USER_ID");
    }


    public MessageTempEventDao(DaoConfig config) {
        super(config);
    }
    
    public MessageTempEventDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"MESSAGE_TEMP_EVENT\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"FROM_USER_ID\" TEXT," + // 1: fromUserID
                "\"TO_USER_ID\" TEXT," + // 2: toUserID
                "\"MESSAGE_CONTENT\" TEXT," + // 3: messageContent
                "\"MESSAGE_TYPE\" INTEGER NOT NULL ," + // 4: messageType
                "\"IMAGE_HEIGHT\" INTEGER NOT NULL ," + // 5: imageHeight
                "\"IMAGE_WIDTH\" INTEGER NOT NULL ," + // 6: imageWidth
                "\"VOICE_DURATION\" INTEGER NOT NULL ," + // 7: voiceDuration
                "\"AVATAR\" TEXT," + // 8: avatar
                "\"NICKNAME\" TEXT," + // 9: nickname
                "\"CHAT_USER_ID\" TEXT," + // 10: chatUserID
                "\"UN_READ_NUMBER\" INTEGER NOT NULL ," + // 11: unReadNumber
                "\"CHAT_TYPE\" INTEGER NOT NULL ," + // 12: chatType
                "\"CREATE_TIME\" INTEGER NOT NULL ," + // 13: createTime
                "\"USER_ID\" INTEGER NOT NULL );"); // 14: userId
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MESSAGE_TEMP_EVENT\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, MessageTempEvent entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String fromUserID = entity.getFromUserID();
        if (fromUserID != null) {
            stmt.bindString(2, fromUserID);
        }
 
        String toUserID = entity.getToUserID();
        if (toUserID != null) {
            stmt.bindString(3, toUserID);
        }
 
        String messageContent = entity.getMessageContent();
        if (messageContent != null) {
            stmt.bindString(4, messageContent);
        }
        stmt.bindLong(5, entity.getMessageType());
        stmt.bindLong(6, entity.getImageHeight());
        stmt.bindLong(7, entity.getImageWidth());
        stmt.bindLong(8, entity.getVoiceDuration());
 
        String avatar = entity.getAvatar();
        if (avatar != null) {
            stmt.bindString(9, avatar);
        }
 
        String nickname = entity.getNickname();
        if (nickname != null) {
            stmt.bindString(10, nickname);
        }
 
        String chatUserID = entity.getChatUserID();
        if (chatUserID != null) {
            stmt.bindString(11, chatUserID);
        }
        stmt.bindLong(12, entity.getUnReadNumber());
        stmt.bindLong(13, entity.getChatType());
        stmt.bindLong(14, entity.getCreateTime());
        stmt.bindLong(15, entity.getUserId());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, MessageTempEvent entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String fromUserID = entity.getFromUserID();
        if (fromUserID != null) {
            stmt.bindString(2, fromUserID);
        }
 
        String toUserID = entity.getToUserID();
        if (toUserID != null) {
            stmt.bindString(3, toUserID);
        }
 
        String messageContent = entity.getMessageContent();
        if (messageContent != null) {
            stmt.bindString(4, messageContent);
        }
        stmt.bindLong(5, entity.getMessageType());
        stmt.bindLong(6, entity.getImageHeight());
        stmt.bindLong(7, entity.getImageWidth());
        stmt.bindLong(8, entity.getVoiceDuration());
 
        String avatar = entity.getAvatar();
        if (avatar != null) {
            stmt.bindString(9, avatar);
        }
 
        String nickname = entity.getNickname();
        if (nickname != null) {
            stmt.bindString(10, nickname);
        }
 
        String chatUserID = entity.getChatUserID();
        if (chatUserID != null) {
            stmt.bindString(11, chatUserID);
        }
        stmt.bindLong(12, entity.getUnReadNumber());
        stmt.bindLong(13, entity.getChatType());
        stmt.bindLong(14, entity.getCreateTime());
        stmt.bindLong(15, entity.getUserId());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public MessageTempEvent readEntity(Cursor cursor, int offset) {
        MessageTempEvent entity = new MessageTempEvent( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // fromUserID
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // toUserID
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // messageContent
            cursor.getInt(offset + 4), // messageType
            cursor.getInt(offset + 5), // imageHeight
            cursor.getInt(offset + 6), // imageWidth
            cursor.getInt(offset + 7), // voiceDuration
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // avatar
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // nickname
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // chatUserID
            cursor.getInt(offset + 11), // unReadNumber
            cursor.getInt(offset + 12), // chatType
            cursor.getLong(offset + 13), // createTime
            cursor.getLong(offset + 14) // userId
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, MessageTempEvent entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setFromUserID(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setToUserID(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setMessageContent(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setMessageType(cursor.getInt(offset + 4));
        entity.setImageHeight(cursor.getInt(offset + 5));
        entity.setImageWidth(cursor.getInt(offset + 6));
        entity.setVoiceDuration(cursor.getInt(offset + 7));
        entity.setAvatar(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setNickname(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setChatUserID(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setUnReadNumber(cursor.getInt(offset + 11));
        entity.setChatType(cursor.getInt(offset + 12));
        entity.setCreateTime(cursor.getLong(offset + 13));
        entity.setUserId(cursor.getLong(offset + 14));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(MessageTempEvent entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(MessageTempEvent entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(MessageTempEvent entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
