package com.cosven.message.bean

import java.io.Serializable

data class RedPacketBean(
    val created_at: String,
    val integral: String,//总积分
    val number: String,//红包个数
    val red_pack_id: Int,//红包id
    val type: String,//红包类型
    val updated_at: String,
    val user_id: Int,//用户id
    val wishes: String//祝福语
) : Serializable