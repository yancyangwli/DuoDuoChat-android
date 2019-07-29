package com.woniu.core.router

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter

class RouterConfig {
    companion object {

        fun init(app: Application, isDebug: Boolean) {
            if (isDebug) {
                ARouter.openLog()  // 打印日志
                ARouter.openDebug()    // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
            }
            ARouter.init(app)      // 尽可能早，推荐在Application中初始化
        }

    }
}