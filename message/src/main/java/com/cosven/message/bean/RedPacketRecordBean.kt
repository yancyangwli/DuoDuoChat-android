package com.cosven.message.bean

class RedPacketRecordBean (
    var current_page: Int,
    var `data`: MutableList<RedPacketRecordItemBean>?,
    var first_page_url: String?,
    var from: Int,
    var next_page_url: String??,
    var path: String?,
    var per_page: Int,
    var prev_page_url: String?,
    var to: Int
)

data class RedPacketRecordItemBean(
    val avatar: String,
    val created_at: String,
    val gender: Int,
    val integral: String,
    val log_id: Int,
    val nickname: String,
    val real_name: String,
    val receiver_id: Int,
    val red_pack_id: Int,
    val updated_at: String
)