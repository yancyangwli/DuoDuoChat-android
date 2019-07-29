package com.woniu.core.bean.entity

import com.woniu.core.bean.BaseListData
import com.woniu.core.bean.GroupMember
import java.io.Serializable

/**
 * @author Anlycal<远>
 * @date 2019/6/13
 * @description 搜索
 */


data class SearchUserEntity(
    var users: BaseListData<SearchUserData>? = null
)
data class SearchGroupEntity(
    var groups: BaseListData<SearchGroupsData>? = null
)

data class SearchGroupsData(
    val announcement: String,
    val group_avatar: String,
    val group_id: Int,
    val group_member: GroupMember,
    val group_name: String
) :Serializable


data class SearchUserData(
    var avatar: String?,
    var f_id: String?,
    var nickname: String?,
    var signature: String?,
    var user_id: Int,
    var friend_info: FriendInfo? = null
):Serializable

data class FriendInfo(
    val created_at: String,
    val friend_avatar: String,
    val friend_description: String,
    val friend_group: String,
    val friend_remark: String,
    val friend_user_id: Int,
    val is_block: Int
)