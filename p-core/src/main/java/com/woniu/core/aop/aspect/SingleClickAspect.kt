package com.zhuanxu.eclipse.aop.aspect

import android.view.View
import com.orhanobut.logger.Logger
import com.woniu.core.BuildConfig
import com.woniu.core.R
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import java.util.*

/**
 * User: Axl_Jacobs(Axl.Jacobs@gmail.com)
 * Date: 2018/4/12
 * Time: 17:04
 */
@Aspect
class SingleClickAspect {

    @Pointcut("execution(@com.zhuanxu.eclipse.aop.annotation.SingleClick * *(..))") //方法切入点
    fun methodAnnotated() {

    }

    @Around("methodAnnotated()")//在连接点进行方法替换
    @Throws(Throwable::class)
    fun aroundJoinPoint(joinPoint: ProceedingJoinPoint) {
        var view: View? = null
        for (arg in joinPoint.args) {
            if (arg is View) view = arg
        }
        if (view != null) {
            val tag = view.getTag(TIME_TAG)
            val lastClickTime = if (tag != null) tag as Long else 0
            if (BuildConfig.DEBUG) {
                Logger.d("lastClickTime:" + lastClickTime)
            }
            val currentTime = Calendar.getInstance().timeInMillis
            if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {//过滤掉600毫秒内的连续点击
                view.setTag(TIME_TAG, currentTime)
                if (BuildConfig.DEBUG) {
                    Logger.d("currentTime:" + currentTime)
                }
                joinPoint.proceed()//执行原方法
            }
        }
    }

    companion object {
        val MIN_CLICK_DELAY_TIME = 600
        internal var TIME_TAG = R.id.click_time
    }
}