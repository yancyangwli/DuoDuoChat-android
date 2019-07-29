package com.woniu.core.bean

import java.io.Serializable

/**
 * @author Anlycal<远>
 * @date 2019/6/4
 * @description ...
 */


data class BaseReq<T>(
    var msg:String,
    var status:String,
    var result:T


) : Serializable {
    override fun toString(): String {
        return "BaseReq(msg='$msg', status='$status', result=$result)"
    }
}