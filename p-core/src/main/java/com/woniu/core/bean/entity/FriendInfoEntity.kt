package com.woniu.core.bean.entity

import java.io.Serializable

/**
 * @author Anlycal<远>
 * @date 2019/6/12
 * @description 好友信息
 */


data class FriendInfoEntity(
    var birthday: String?,
    var city: String?,
    var avatar: String?,
    var district: String?,
    var friend_info: RemarkInfo?,
    var gender: Int,
    var moment_cover: String?,
    var moments: List<MomentInfo>?,
    var nickname: String?,
    var province: String?,
    var signature: String?,
    var user_id: Int,
    var personal_page_cover:String?
):Serializable

data class RemarkInfo(
    var created_at: String?,
    var friend_avatar: String?,
    var friend_description: String?,
    var friend_group: Any,
    var friend_remark: String?,
    var friend_user_id: Int,
    var is_block: Int//0不在黑名单中  1在黑名单中
):Serializable

data class MomentInfo(
    var images: List<String>?,
    var moment_id: Int
):Serializable