package com.woniu.core.bean.entity

data class IntegralRecordBean(
    val created_at: String,
    val desc: String,
    val integral: String,
    val log_id: Int,
    val order_id: String,
    val type: Int,
    val updated_at: String,
    val user_id: Int
)