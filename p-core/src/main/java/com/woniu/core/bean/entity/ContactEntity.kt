package com.woniu.core.bean.entity


/**
 * @author Anlycal<远>
 * @date 2019/6/11
 * @description 联系人数据
 */

data class ContactEntity(
    var created_at: String?,
    var friend_avatar: String?,
    var friend_description: String?,
    var friend_group: String?,
    var friend_remark: String?,
    var friend_tag: String?,
    var friend_user_id: Int,
    var id: Int,
    var is_block: Int,
    var updated_at: String?,
    var user_id: Int
)