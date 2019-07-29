package com.woniu.core.api;

import com.blankj.utilcode.util.SPUtils;
import com.woniu.core.utils.DateUtil;

public class Config {

    public static class API{
        public static final String base_url = "https://www.duoduochat.top/";
        public static final String image_url = "http://103.193.172.3:8888/";

        public static final String register = "api/register";//注册
        public static final String send_sms = "api/send-sms";//发送短信
        public static final String login = "api/login";//登陆
        public static final String forget_pwd = "api/forget-pwd";//忘记密码
        public static final String mine_information = "api/mine/information";//获取个人信息
        public static final String moment = "api/moment";//发布动态
        public static final String moment_friend_all = "api/moment-friend-all";//我的所有朋友的朋友圈
        public static final String moment_dtail = "api/moment/%d";//动态详情
        public static final String moment_like = "api/moment/like";//朋友圈点赞
        public static final String favorite = "api/favorite";//收藏
        public static final String moment_friend = "api/moment-friend";//我的朋友的朋友圈
        public static final String moment_mine = "api/moment-mine";//我的朋友圈
        public static final String user_info_from_userId = "api/user/%d/info";//获取用户信息
        public static final String friend_block = "api/friend/block";//拉黑好友
        public static final String moment_comment = "api/moment/comment";//评论朋友圈
        public static final String friend_list = "api/friend/list";//获取好友列表
        public static final String friend_remove_blacklist = "api/friend/remove-blacklist";//将好友移出黑名单
        public static final String friend_remove = "api/friend/remove";//将好友移出黑名单
        public static final String friend_blacklist = "api/friend/blacklist";//黑名单列表
        public static final String change_pwd = "api/change-pwd";//修改密码
        public static final String user_search = "api/search";//用户搜索
        public static final String friend_apply = "api/friend/apply";//申请添加好友
        public static final String friend_apply_list = "api/apply/list";//申请列表
        public static final String friend_auditing = "api/friend/auditing";//审核好友
        public static final String group_create = "api/group/create";//创建群聊
        public static final String group_update = "api/group/update";//更新群信息
        public static final String group_album_upload = "api/group/album-upload";//上传群相册
        public static final String group_album_remove = "api/group/album-remove";//删除群图片
        public static final String group_list = "api/group/list";//群列表
        public static final String group_info = "api/group/%d/info";//获取群信息
        public static final String group_update_setting = "api/group/update-setting";//获取群设置
        public static final String dismiss_group = "api/group/dismiss-group";//解散群
        public static final String group_members = "api/group/members";//获取群成员列表
        public static final String group_album_images = "api/group/album-images";//获取群相册
        public static final String group_apply = "api/group/apply";//申请加入群聊
        public static final String group_option_member = "api/group/option-member";//操作群成员
        public static final String group_set_manager = "api/group/set-manager";//设置管理员
        public static final String group_auditing = "api/group/auditing";//同意/拒绝加群
        public static final String red_pack_info  = "api/red-pack/%s/info";//红包信息
        public static final String red_pack_info_receive  = "api/red-pack/%s/receive";//领红包
        public static final String send_red_pack = "api/red-pack/send";//发红包
        public static final String red_pack_log = "api/red-pack/%s/log";//领取红包记录
        public static final String mine_integral_log = "api/mine/integral-log";//获取我的积分记录
        public static final String update_friend = "api/friend/update";//更新好友信息
        public static final String suggest = "api/suggest";//意见反馈
        public static final String report = "api/report";//举报用户/群

        public static final String join_group = "api/joinGroup";
    }

    public static class Constant{
        public static final String SUCCESS = "success";
        public static final String NONE = "none";

        public static final String DUODUO_TOKEN = "DUODUO_TOKEN";
        public static final String DUODUO_USER_ID = "DUODUO_USER_ID";

        public static final String MINE_INTEGRAL = "mine_integral";

        public static final int LOOK_MY_MOMENT = 0x6001;//查看我的朋友圈
        public static final int LOOK_OTHER_MOMENT = 0x6002;//查看他人的朋友圈
        public static final int LOOK_ALL = 0x6003;//查看所有
    }

    public static class Favorite{
        public static final String NORMAL_MESSAGE = "normal_message";
        public static final String GROUP_MESSAGE = "group_message";
        public static final String MONENT = "moment";
        public static final String FAVORITE_URL = "url";
        public static final String FAVORITE_OTHER = "other";
    }

    public static class XMPP{
        public static final String SERVER_IP = "duoduochat.top";
        public static final int PORT = 5222;
        public static final String SERVER_NAME = "duoduochat.top";
        public static final String XMPP_CLIENT = "Android";
        public static final String XMPP_PSW = "DD#PWD#FUCK";
    }

    public static class DBTable{
        public static final String DB_BASE_NAME = "DuoDuoChat.db";
        public static final String MessageTable = "MessageTable";
        public static final String ContactTable = "ContactTable";
        public static final String MessageRecordTable = "MessageRecordTable";
    }

    public static class FolderName{
        public static final String avatar_ = "images/avatar/" + getDate() + "/" + getUserId() + ".jpg";
        public static final String group_avatar = "images/group_avatar/" + getDate() + "/" +  getRandomStr() + ".jpg";
        public static final String moment_cover = "images/moment_cover/" + getDate() + "/" + getRandomStr() + ".jpg";
        public static final String personal_cover = "images/personal_cover/" + getDate() + "/" + getRandomStr() + ".jpg";

        private static String getDate(){
            return DateUtil.formatTime(System.currentTimeMillis(),DateUtil.yyMMdd);
        }

        private static String getUserId(){
            return String.valueOf(SPUtils.getInstance().getInt(Constant.DUODUO_USER_ID));
        }

        private static String getUserIdWithRandomStr(){
            return getUserId() + "_" + getRandomStr();
        }

        private static String getRandomStr(){
            return String.valueOf(System.currentTimeMillis());
        }
    }

    public static String msg_() {
        return "images/msg/" + FolderName.getDate() + "/" + FolderName.getUserIdWithRandomStr() + ".jpg";
    }

    public static String voice() {
        return "voice/msg/" + FolderName.getDate() + "/" + FolderName.getUserIdWithRandomStr() + ".arm";
    }

    public static String moment() {
        return "images/moment/" + FolderName.getDate() + "/" + FolderName.getRandomStr() + ".jpg";
    }

    public static String group() {
        return "images/group/" + FolderName.getDate() + "/" + FolderName.getRandomStr() + ".jpg";
    }


}
