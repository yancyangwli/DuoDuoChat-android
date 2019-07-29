package com.woniu.core.bean.entity

/**
 * 群数据
 */
data class GroupEntity(
    val announcement: String,
    val city: String,
    val created_at: String,
    val district: String,
    val group_avatar: String,
    val group_id: Int,
    val group_limit: Int,
    val group_member_num: Any,
    val group_name: String,
    val group_silence: Int,
    val manager_ids: String,
    val owner_id: String,
    val province: String,
    val updated_at: String
)