package com.zhuanxu.eclipse.aop.aspect

import android.content.Intent
import android.text.TextUtils
import android.view.View
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut


/**
 * UserInfoModel: Axl_Jacobs(Axl.Jacobs@gmail.com)
 * Date: 2018/4/10
 * Time: 17:12
 */
@Aspect
class CheckLoginAspect {

    @Pointcut("execution(@com.zhuanxu.eclipse.aop.annotation.CheckLogin * *(..))") //方法切入点
    fun methodAnnotated() {
    }

    @Around("methodAnnotated()")//在连接点进行方法替换
    @Throws(Throwable::class)
    fun aroundJoinPoint(joinPoint: ProceedingJoinPoint) {
//        var view: View? = null
//        for (arg in joinPoint.args) {
//            if (arg is View) view = arg
//        }

//        if (TextUtils.isEmpty(UserInfoModel.LOGIN_KEY)) {
//            ZXApp.instance().toast("请先登录")
//            ZXApp.instance().startActivity(Intent(ZXApp.instance(), LoginActivity::class.java))
//            return
//        }


        joinPoint.proceed()//执行原方法


    }
}