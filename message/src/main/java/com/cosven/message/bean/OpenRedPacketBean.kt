package com.cosven.message.bean

data class OpenRedPacketBean(
    val redPack: RedPack,
    val redPackLog: RedPackLog,
    val increaseIntegral: Int
)

data class RedPack(
    val created_at: String,
    val integral: String,
    val number: Int,
    val received_integral: String,
    val received_number: Int,
    val red_pack_id: Int,
    val type: String,
    val updated_at: String,
    val user: User,
    val user_id: Int,
    val wishes: String
)

data class User(
    val avatar: String,
    val gender: Int,
    val nickname: String,
    val real_name: Any,
    val user_id: Int
)

data class RedPackLog(
    val `data`: List<Data>,
    val current_page: Int,
    val first_page_url: String,
    val from: Int,
    val next_page_url: Any,
    val path: String,
    val per_page: Int,
    val prev_page_url: Any,
    val to: Int
)

data class Data(
    val avatar: String,
    val created_at: String,
    val gender: Int,
    val integral: String,
    val log_id: Int,
    val nickname: String,
    val real_name: Any,
    val receiver_id: Int,
    val red_pack_id: Int,
    val updated_at: String
)