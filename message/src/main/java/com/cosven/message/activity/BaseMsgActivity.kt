package com.cosven.message.activity

import android.view.Window
import android.view.inputmethod.InputMethodManager
import com.woniu.core.activities.BaseActivity

abstract class BaseMsgActivity : BaseActivity() {

    private var mImm: InputMethodManager? = null
    private var mWindow: Window? = null

    override fun setContentViewId(): Int {
        return setContentViewIds()
    }

    override fun initView() {

    }

    override fun initData() {

    }

    // 传入布局
    abstract fun setContentViewIds(): Int


}