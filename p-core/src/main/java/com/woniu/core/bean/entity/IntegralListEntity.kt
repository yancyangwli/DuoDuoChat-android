package com.woniu.core.bean.entity

data class IntegralListEntity(
    var current_page: Int,
    var `data`: List<IntegralRecordBean>?,
    var first_page_url: String?,
    var from: Int,
    var next_page_url: String??,
    var path: String?,
    var per_page: Int,
    var prev_page_url: String?,
    var to: Int
)