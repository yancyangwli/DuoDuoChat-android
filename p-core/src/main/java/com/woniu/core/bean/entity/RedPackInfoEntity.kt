package com.woniu.core.bean.entity

import java.io.Serializable

/**
 * @author Anlycal<远>
 * @date 2019/6/28
 * @description 红包信息
 */


data class RedPackInfoEntity(
    var mineRedPack: MineRedPack?,
    var redPack: RedPack?
)

data class RedPack(
    var created_at: String?,
    var integral: String?,
    var number: Int?,
    var received_integral: String?,
    var received_number: String?,
    var red_pack_id: Int,
    var type: String?,
    var updated_at: String?,
    var user: SendPackUser?,
    var user_id: Int?,
    var wishes: String?
) : Serializable

data class SendPackUser(
    var avatar: String?,
    var gender: Int?,
    var nickname: String?,
    var real_name: String?,
    var user_id: Int?
) : Serializable

data class MineRedPack(
    var created_at: String?,
    var integral:String?,
    var log_id:Int?,
    var receiver_id:Int?,
    var red_pack_id:Int?,
    var updated_at: String?
) : Serializable