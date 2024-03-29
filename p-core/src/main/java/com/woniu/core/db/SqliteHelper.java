package com.woniu.core.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.woniu.core.api.Config;


/*
 * 项目名:    SmackChat
 * 包名       com.azhong.smackchat.common.db
 * 文件名:    SqliteHelper
 * 创建者:    ZSY
 * 创建时间:  2017/3/2 on 16:02
 * 描述:     TODO 创建数据库
 */
public class SqliteHelper extends SQLiteOpenHelper {

    public SqliteHelper(Context context) {
        super(context, "smack.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL(getTableDB(Config.DBTable.MessageTable));
        db.execSQL(getTableDB(Config.DBTable.MessageRecordTable));

//        db.execSQL("create table user(user_id  INTEGER primary key autoincrement,user_name varchar,user_psd varchar,user_head_img varchar)");
//        db.execSQL("create table msg_list(msg_list_id  INTEGER primary key autoincrement,user_id INTEGER,to_name varchar,last_msg varchar,last_msg_time varchar,msg_list_type)");
//        db.execSQL("create table msg(msg_id INTEGER primary key autoincrement,msg_list_id  INTEGER,from_name varchar,msg_content varchar,msg_time varchar,msg_type varchar ,from_type INTEGER)");

//        db.execSQL("create table msg_list(msg_list_id  INTEGER primary key autoincrement,user_id INTEGER,to_name varchar,last_image varchar,last_img_time varchar,msg_list_type, foreign key (user_id) references  user(user_id) on delete cascade on update cascade )");
//        db.execSQL("create table msg(msg_id INTEGER primary key autoincrement,msg_list_id  INTEGER,from_name varchar,msg_content varchar,msg_time varchar,msg_type varchar, foreign key (msg_list_id) references  msg_list(msg_list_id) on delete cascade on update cascade )");

    }

    private String getTableDB(String table){
        String messageTable = "create table " + table +
                "(messageID INTEGER primary key autoincrement," +
                "messageContent varchar,messageType integer,imageWidth integer," +
                "imageHeight integer,voiceDuration integer,avatar text," +
                "nickname text,image,formUserID varchar,toUserID varchar)";
        return messageTable;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
