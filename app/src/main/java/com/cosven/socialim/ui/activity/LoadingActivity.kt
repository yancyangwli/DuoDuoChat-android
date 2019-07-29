package com.cosven.socialim.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.cosven.socialim.R
import com.google.gson.reflect.TypeToken
import com.woniu.core.activities.BaseActivity
import com.woniu.core.api.Config
import com.woniu.core.bean.BaseReq
import com.woniu.core.bean.entity.GroupEntity
import com.woniu.core.manage.GroupListManage
import com.woniu.core.utils.GsonUtil
import com.woniu.core.utils.HttpUtil
import com.woniu.core.xmpp.DuoDuoConnectionService
import com.woniu.core.xmpp.smack.SmackManager
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.startActivity
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import java.lang.reflect.Type

class LoadingActivity : BaseActivity() {

    private var mHttpUtil: HttpUtil? = null

    private var USER_ACCOUNT: String? = null

    private var mConnection: XMPPTCPConnection? = null

    override fun setContentViewId(): Int = R.layout.activity_loading

    override fun initView() {
    }

    override fun initData() {
        loadGroupList()
    }

    fun loadGroupList() {
        if (mHttpUtil == null) {
            mHttpUtil = HttpUtil(this)
        }
        mHttpUtil?.setOnRequestCallBack { code, data ->
            dismissProgressDialog()
            Log.i("FAN", "获取群聊列表返回---> ${data}")
            if (code == -1) {
                ToastUtils.showLong(data)
                return@setOnRequestCallBack
            }

            val type: Type = object : TypeToken<BaseReq<MutableList<GroupEntity>>>() {}.type
            val mReq: BaseReq<MutableList<GroupEntity>> = GsonUtil.getGson().fromJson(data, type)
            if (TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {
                mReq.result?.let {
                    GroupListManage.getInstance.saveInfo(it)

                    USER_ACCOUNT = SPUtils.getInstance().getInt(Config.Constant.DUODUO_USER_ID).toString()
                    Log.i("FAN", "USER_ACCOUNT---> $USER_ACCOUNT")
                    login(USER_ACCOUNT!!, Config.XMPP.XMPP_PSW)
                }
            }
        }?.getParms(httpParams, Config.API.group_list)
    }

    /**
     * 登陆
     *
     * @param username 用户账号
     * @param password 用户密码
     * @return
     * @throws Exception
     */
    @SuppressLint("CheckResult")
    fun login(username: String, password: String) {
        Observable
            .just(arrayOf(username, password))
            .subscribeOn(Schedulers.io())
            .flatMap { strings -> isLogin(strings[0], strings[1]) }
            .observeOn(Schedulers.io())
            .subscribe { aBoolean ->
                if (aBoolean!!) {
                    Log.i("FAN", "accept: 登陆成功")
                    var mIntent = Intent(this@LoadingActivity, DuoDuoConnectionService::class.java)
                    startService(mIntent)

                    startActivity<MainActivity>()
                    finish()
                } else {
                    Log.i("FAN", "accept: 登陆失败")
                }
            }
    }

    /**
     * 判断是否登陆
     * @param username 用户名（用户id）
     * @param password 密码
     * @return
     */
    private fun isLogin(username: String, password: String): Observable<Boolean> {
        if (mConnection == null) {
            mConnection = SmackManager.getInstance().connection
        }

        return if (mConnection == null) {
            Observable.just(false)
        } else {
            if (!mConnection!!.isAuthenticated) {
                SmackManager.getInstance().login(username, password)
            }
            Observable.just(true)
        }
    }
}
