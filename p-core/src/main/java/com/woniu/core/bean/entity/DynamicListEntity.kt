package com.woniu.core.bean.entity

/**
 * @author Anlycal<远>
 * @date 2019/6/6
 * @description 动态列表
 */


data class DynamicListEntity(
    var current_page: Int,
    var `data`: List<DynamicItemData>?,
    var first_page_url: String?,
    var from: Int,
    var next_page_url: String??,
    var path: String?,
    var per_page: Int,
    var prev_page_url: String?,
    var to: Int
)

data class DynamicItemData(
    var comment_num: Int,
    var comments: List<DynamicCommentData>?,
    var content: String?,
    var created_at: String?,
    var images: List<String>?,
    var liked_num: Int,
    var likes: List<DynamicLikeData>?,
    var moment_id: Int,
    var stick_time: String?,
    var updated_at: String?,
    var user_id: Int,
    var friend_avatar:String?,//头像
    var friend_remark:String?,
    var hasLike:Boolean,
    var hasFavorite:Boolean,
    var isExpand:Boolean = false,
    var minePraisePosition:Int = -1
)

data class DynamicLikeData(
    var created_at: String?,
    var like_id: Int,
    var moment_id: Int,
    var updated_at: String?,
    var user_id: Int,
    var from_friend_remark:String?//好友昵称
) {
    override fun toString(): String {
        return "DynamicLikeData(created_at=$created_at, like_id=$like_id, moment_id=$moment_id, updated_at=$updated_at, user_id=$user_id, from_friend_remark=$from_friend_remark)"
    }
}

data class DynamicCommentData(
    var comment_id: Int,
    var comment_pid: Int,
    var content: String?,
    var created_at: String?,
    var from_user_id: Int,
    var moment_id: Int,
    var to_user_id: Int,
    var updated_at: String?,
    var from_friend_remark:String?,//好友昵称
    var to_friend_remark:String?//好友备注
)