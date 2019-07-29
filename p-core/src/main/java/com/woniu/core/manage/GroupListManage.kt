package com.woniu.core.manage

import com.blankj.utilcode.util.SPUtils
import com.woniu.core.api.Config
import com.woniu.core.bean.entity.GroupEntity
import com.woniu.core.bean.entity.RegisterEntity
import com.woniu.core.bean.entity.UserInfoEntity

/**
 * @author Anlycal<远>
 * @date 2019/6/5
 * @description ...
 */


class GroupListManage private constructor() {

    var groupList: MutableList<GroupEntity>? = null

    companion object {
        val getInstance by lazy(mode = LazyThreadSafetyMode.NONE) {
            GroupListManage()
        }
    }

    fun saveInfo(list: MutableList<GroupEntity>) {
        groupList = list
    }

}