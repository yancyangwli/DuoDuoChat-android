package com.cosven.message.bean

import com.woniu.core.bean.GroupMember

data class GroupMemberListBean(
    var current_page: Int,
    var `data`: MutableList<GroupMember>?,
    var first_page_url: String?,
    var from: Int,
    var next_page_url: String??,
    var path: String?,
    var per_page: Int,
    var prev_page_url: String?,
    var to: Int
)