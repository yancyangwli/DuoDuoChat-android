package com.woniu.core.bean.entity

/**
 * @author Anlycal<远>
 * @date 2019/6/8
 * @description 动态详情数据
 */


data class DynamicDetailEntity(
    var comments: List<DynamicCommentData>?,
    var likes: List<DynamicLikeData>?,
    var moment: DynamicDetailMoment?
)

data class DynamicDetailMoment(
    var comment_num: Int,
    var content: String?,
    var created_at: String?,
    var images: List<String>?,
    var liked_num: Int,
    var moment_id: Int,
    var stick_time: String?,
    var updated_at: String?,
    var user_id: Int,
    var friend_avatar:String?,//头像
    var friend_remark:String?,
    var hasLike:Boolean = false,
    var hasFavorite:Boolean = false,
    var isExpand:Boolean = false
)

data class DynamicDetailComments(
    var current_page: Int,
    var `data`: List<DynamicCommentData>?,
    var first_page_url: String?,
    var from: Int,
    var next_page_url: String?,
    var path: String?,
    var per_page: Int,
    var prev_page_url: String?,
    var to: Int
)

data class DynamicDetailLikes(
    var current_page: Int,
    var `data`: List<DynamicLikeData>?,
    var first_page_url: String?,
    var from: Int,
    var next_page_url: String?,
    var path: String?,
    var per_page: Int,
    var prev_page_url: String?,
    var to: Int
)