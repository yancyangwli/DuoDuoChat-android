package com.woniu.core.bean.entity

/**
 * @author Anlycal<远>
 * @date 2019/6/4
 * @description 注册
 */


 data class RegisterEntity(
    var token: String,
    var expires_in: Int,
    var token_type: String,
    var userInfo: UserInfoEntity
)
