package com.cosven.socialim.ui.activity

import android.os.Handler
import android.text.TextUtils
import android.util.Log
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.cosven.my.runAlone.activity.LoginActivity
import com.cosven.socialim.R
import com.google.gson.reflect.TypeToken
import com.woniu.core.activities.BaseActivity
import com.woniu.core.api.Config
import com.woniu.core.bean.BaseReq
import com.woniu.core.bean.entity.UserInfoEntity
import com.woniu.core.manage.UserInfoManage
import com.woniu.core.utils.GsonUtil
import com.woniu.core.utils.HttpUtil
import kotlinx.android.synthetic.main.fragament_my.view.*
import org.jetbrains.anko.startActivity
import java.lang.reflect.Type

/**
 * @author Anlycal<远>
 * @date 2019/6/13
 * @description 启动页
 */
class StartActivity : BaseActivity() {

    private var mHttpUtil: HttpUtil = HttpUtil(this)

    override fun setContentViewId(): Int {
        return R.layout.activity_start
    }

    override fun initView() {
        var token: String = SPUtils.getInstance().getString(Config.Constant.DUODUO_TOKEN)
        if (token.isNullOrBlank()) {
            startActivity<LoginActivity>()
            return
        }
        loadUserinfo()
    }

    override fun initData() {
    }

    /**
     * 加载用户信息
     * @inPUll 是否是下拉刷新
     */
    private fun loadUserinfo() {

        mHttpUtil.setOnRequestCallBack { code, data ->
            if (code == -1) {
                ToastUtils.showLong(data)
                return@setOnRequestCallBack
            }

            var mType: Type = object : TypeToken<BaseReq<UserInfoEntity>>() {}.type
            var mReq: BaseReq<UserInfoEntity> = GsonUtil.getGson().fromJson(data, mType)
            if (TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {
                UserInfoManage.getInstance.saveInfo(mReq.result)
                //startActivity<MainActivity>()
                startActivity<LoadingActivity>()
                finishs()
            } else {
                ToastUtils.showLong(mReq.msg)
            }
        }.getParms(httpParams, Config.API.mine_information)
    }


}