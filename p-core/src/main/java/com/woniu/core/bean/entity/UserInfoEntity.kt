package com.woniu.core.bean.entity

import java.io.Serializable

/**
 * @author Anlycal<远>
 * @date 2019/6/4
 * @description 用户信息
 */


data class UserInfoEntity(
    var avatar: String?,
    var city: String?,
    var created_at: String? = "",
    var district: String?,
    var email: String? = "",
    var gender: Int,
    var integral: String? = "",
    var invite_code: String? = "",
    var is_certification: Int = -1,
    var last_login_ip: String? = "",
    var level: Int = -1,
    var money: String? = "",
    var nickname: String?,
    var phone: String? = "",
    var province: String?,
    var real_name: String? = "",
    var signature: String? = "",
    var status: Int = -1,
    var updated_at: String? = "",
    var upper_id: Any? = null,
    var uppers: Any? = null,
    var user_id: Int,
    var wechat: String? = "",
    var birthday: String?,
    var moment_cover: String? = "",
    var personal_page_cover: String? = "",
    var is_allow_add_friend: Int = 0,//是否允许添加好友，1是，0否
    var is_show_group_notify: Int = 0//是否显示群通知，1是，0否
) : Serializable {
    override fun toString(): String {
        return "UserInfoEntity(avatar=$avatar, city=$city, created_at=$created_at, district=$district, email=$email, gender=$gender, integral=$integral, invite_code=$invite_code, is_certification=$is_certification, last_login_ip=$last_login_ip, level=$level, money=$money, nickname=$nickname, phone=$phone, province=$province, real_name=$real_name, signature=$signature, status=$status, updated_at=$updated_at, upper_id=$upper_id, uppers=$uppers, user_id=$user_id, wechat=$wechat, birthday=$birthday, moment_cover=$moment_cover, personal_page_cover=$personal_page_cover, is_allow_add_friend=$is_allow_add_friend, is_show_group_notify=$is_show_group_notify)"
    }
}
