package com.cosven.socialim

import com.blankj.utilcode.util.Utils
import com.woniu.core.BaseApplication

class IMApp : BaseApplication() {
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
    }



}