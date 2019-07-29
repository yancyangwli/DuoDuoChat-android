package com.woniu.core.manage

import com.blankj.utilcode.util.SPUtils
import com.woniu.core.api.Config
import com.woniu.core.bean.entity.RegisterEntity
import com.woniu.core.bean.entity.UserInfoEntity

/**
 * @author Anlycal<远>
 * @date 2019/6/5
 * @description ...
 */


class UserInfoManage private constructor() {

    var mUserInfoEntity: UserInfoEntity? = null

    companion object {
        val getInstance by lazy(mode = LazyThreadSafetyMode.NONE) {
            UserInfoManage()
        }
    }

    fun saveInfo(info: UserInfoEntity) {
        mUserInfoEntity = info
        UserInfoManageForJava.newInstance().setUserInfoEntity(mUserInfoEntity);
        info.let {
            SPUtils.getInstance().put(Config.Constant.DUODUO_USER_ID, it.user_id)
        }
    }


    fun saveToken(token: String) {
        SPUtils.getInstance().put(Config.Constant.DUODUO_TOKEN, token)
    }

    fun clearUserInfo() {
        mUserInfoEntity = null
        SPUtils.getInstance().clear()
    }

}