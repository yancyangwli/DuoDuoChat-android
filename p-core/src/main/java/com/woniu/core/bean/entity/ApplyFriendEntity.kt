package com.woniu.core.bean.entity

import java.io.Serializable

/**
 * @author Anlycal<远>
 * @date 2019/6/14
 * @description ...
 */


data class ApplyFriendEntity(
    var apply_id: Int? = 0,
    var apply_status: Int = 0,
    var apply_type: Int = 0,
    var apply_user_id: Int = 0,
    var apply_user_nick: String = "",
    var apply_user_avatar: String = "",
    var created_at: String? = "",
    var is_read: Int = 0,
    var object_age: String? = "",
    var object_avatar: String? = "",
    var object_address:String? = "",
    var object_gender: Int = 0,
    var object_id: Int = 0,
    var object_nick: String? = "",
    var remarks: String? = "",
    var to_user_id: Int = 0,
    var updated_at: String? = ""
):Serializable{
    companion object{
        const val FRIEND:Int = 1//好友
        const val GROUP:Int = 2//群

        const val PENDING = 0
        const val AGREE = 1
        const val REFUSE = 2
    }

    override fun toString(): String {
        return "ApplyFriendEntity(apply_id=$apply_id, apply_status=$apply_status, apply_type=$apply_type, apply_user_id=$apply_user_id, apply_user_nick='$apply_user_nick', apply_user_avatar='$apply_user_avatar', created_at=$created_at, is_read=$is_read, object_age=$object_age, object_avatar=$object_avatar, object_address=$object_address, object_gender=$object_gender, object_id=$object_id, object_nick=$object_nick, remarks=$remarks, to_user_id=$to_user_id, updated_at=$updated_at)"
    }
}


