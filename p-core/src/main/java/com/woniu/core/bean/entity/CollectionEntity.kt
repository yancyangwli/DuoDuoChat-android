package com.woniu.core.bean.entity

/**
 * @author Anlycal<远>
 * @date 2019/6/10
 * @description   收藏数据
 */


data class CollectionEntity(
    var content: String?,
    var created_at: String?,
    var favorite_id: Int,
    var target_id: Int,
    var type: String?,
    var updated_at: String?,
    var user_id: Int
)

