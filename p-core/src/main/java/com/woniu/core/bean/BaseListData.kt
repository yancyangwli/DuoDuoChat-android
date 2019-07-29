package com.woniu.core.bean

/**
 * @author Anlycal<远>
 * @date 2019/6/10
 * @description ...
 */


data class BaseListData<T> (
    val current_page: Int,
    val `data`: List<T>?,
    val first_page_url: String?,
    val from: Int,
    val last_page: Int,
    val last_page_url: String?,
    val next_page_url: String?,
    val path: String?,
    val per_page: Int,
    val prev_page_url: String?,
    val to: Int,
    val total: Int
)