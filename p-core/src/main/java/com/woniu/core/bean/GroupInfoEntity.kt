package com.woniu.core.bean

import java.io.Serializable

data class GroupInfoEntity(
    val album: MutableList<Album>,
    var announcement: String,
    val city: String,
    val created_at: String,
    val deleted_at: Any,
    val district: String,
    val group_avatar: String,
    val group_id: Int,
    val group_limit: Int,
    val group_member: GroupMember,
    val group_member_num: Int,
    var group_name: String,
    var group_silence: Int,
    val manager_ids: Any,
    val members: MutableList<Member>,
    val owner_id: String,
    val province: Any,
    val updated_at: String
) : Serializable

data class Member(
    val created_at: String = "",
    val deleted_at: Any = "",
    val forbidden_type: Any = "",
    val group_id: Int = 0,
    val group_manager: Int = 0,
    val group_nickname: String = "",
    val id: Int = 0,
    val is_sticky: Int = 0,
    val lifting_time: Any = "",
    val not_disturb: Int = 0,
    val updated_at: String = "",
    val user: User,
    val user_id: Int = 0
) : Serializable

data class User(
    val avatar: Any,
    val real_name: String,
    val user_id: Int
) : Serializable

data class GroupMember(
    val created_at: String,
    val deleted_at: String,
    val forbidden_type: String,
    val group_id: Int,
    val group_manager: Int,//0成员 1管理员 2群主
    var group_nickname: String,
    val id: Int,
    var is_sticky: Int,//是否置顶聊天0未置顶，1置顶
    val lifting_time: Any,
    var not_disturb: Int,
    val updated_at: String,
    val user_id: Int,
    val user: User
) : Serializable

data class Album(
    val created_at: String,
    val creater_id: Int,
    val group_id: Int,
    val id: Int,
    var image: String,
    val updated_at: String = ""
) : Serializable