package com.cosven.my.runAlone.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.RadioButton
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.RegexUtils
import com.blankj.utilcode.util.ToastUtils
import com.cosven.my.R
import com.dktlh.ktl.provider.router.RouterPath
import com.google.gson.reflect.TypeToken
import com.gyf.immersionbar.ImmersionBar
import com.woniu.core.activities.BaseActivity
import com.woniu.core.api.Config
import com.woniu.core.bean.BaseReq
import com.woniu.core.bean.entity.RegisterEntity
import com.woniu.core.manage.UserInfoManage
import com.woniu.core.utils.CountTimeUtil
import com.woniu.core.utils.GsonUtil
import com.woniu.core.utils.HttpUtil
import com.woniu.core.xmpp.DuoDuoConnectionService
import com.woniu.core.xmpp.rxbus.RxBus
import com.woniu.core.xmpp.rxbus.event.LoginEvent
import com.woniu.core.xmpp.smack.SmackManager
import com.zhouyou.http.model.HttpParams
import com.zhuanxu.eclipse.aop.annotation.SingleClick
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.login_item.*
import org.jetbrains.anko.startActivity


/**
 * 登陆
 */

@Route(path = RouterPath.MineCenter.PATH_LOGIN)
class LoginActivity : BaseActivity() {

//    val timerCount by lazy {
//        TimerCount(60000, 1000, mTvCode)
//    }

    private var mHttpUtil: HttpUtil = HttpUtil(this)

    private var mTitles: Array<String> = arrayOf("账号登陆", "验证码登陆")

    private var mCurrentLoginType: Int = 0//当前登陆类型 0密码登陆 1验证码登陆

    override fun setContentViewId(): Int {
        return R.layout.activity_login
    }

    @SingleClick
    override fun onClick(v: View) {
        when (v.id) {
            R.id.mTvRegister -> startActivity<RegisteredActivity>()
            R.id.mTvForgetPsw -> startActivity<ChangPsActivity>()
            R.id.mTvCode ->
                if (CountTimeUtil.getInstance().startCountTime(mEtPhone, mTvCode)) {
                    sendSMS()
                }
            R.id.mRbPswLogin -> {
                switchLogin(0)
            }
            R.id.mRbCodeLogin -> {
                switchLogin(1)
            }
            R.id.mBtnLogin -> loginOperation()
        }
    }

    @SuppressLint("CheckResult")
    override fun initView() {
        switchLogin(0)

        mTvRegister.setOnClickListener(this)
        mTvForgetPsw.setOnClickListener(this)

        mBtnLogin.setOnClickListener(this)

//        RxBus.getInstance()
//            .toObserverable(LoginEvent::class.java)
//            .subscribe{
//                if (it.loginSuccess){
//                    Log.i("FAN","登陆成功&&&&&&")
//                }else{
//                    Log.i("FAN","登陆失败&&&&&&")
//                }
//            }
    }


    override fun initData() {
        mRbPswLogin.setOnClickListener(this)
        mRbCodeLogin.setOnClickListener(this)
        mTvCode.setOnClickListener(this)
    }


    override fun initStatusBar() {
        ImmersionBar.with(this)
            .titleBar(R.id.toolbar)
            .statusBarDarkFont(true)
            .keyboardEnable(true)  //解决软键盘与底部输入框冲突问题
            .init()
    }

    private fun switchLogin(index: Int) {
        mCurrentLoginType = index
        mTvToolbarTitle.setText(mTitles[index])
        if (index == 0) {
            mLlCodeRoot.visibility = View.GONE
            mLlPswRoot.visibility = View.VISIBLE
            changeBackground(Color.WHITE, R.drawable.shape_corner, mRbPswLogin)
            changeBackground(resources.getColor(R.color.color_9A9A9A), null, mRbCodeLogin)
        } else {
            mLlCodeRoot.visibility = View.VISIBLE
            mLlPswRoot.visibility = View.GONE
            changeBackground(Color.WHITE, R.drawable.shape_corner, mRbCodeLogin)
            changeBackground(resources.getColor(R.color.color_9A9A9A), null, mRbPswLogin)
        }
    }

    private fun changeBackground(textColor: Int, bg: Int?, button: RadioButton) {
        button.setTextColor(textColor)
        if (bg != null) {
            button.setBackgroundResource(bg)
        } else {
            button.background = null
        }
    }

    /**
     * 登陆操作
     */
    private fun loginOperation() {
        var phone: String = mEtPhone.text.trim().trim().toString()
        if (phone.isNullOrBlank()) {
            ToastUtils.showLong(getString(R.string.phone_input_hint))
            return
        }

        if (!RegexUtils.isMobileExact(phone)) {
            ToastUtils.showLong(getString(R.string.phone_err))
            return
        }

        if (mCurrentLoginType == 0) {//密码登陆
            var psw: String = mEtPsw.text.trim().toString()
            if (psw.isNullOrBlank()) {
                ToastUtils.showLong(getString(R.string.psw_input_hint))
                return
            }
            startLogin(phone, psw = psw)
        } else {
            var code: String = mEtCode.text.trim().toString()
            if (code.isNullOrBlank()) {
                ToastUtils.showLong(getString(R.string.code_input_hint))
                return
            }
            startLogin(phone, code = code)
        }
    }

    private fun startLogin(phone: String, psw: String = "", code: String = "") {

        var mParams: HttpParams = httpParams
        mParams.put("username", phone)
        if (mCurrentLoginType == 0) {
            mParams.put("password", psw)
        } else {
            mParams.put("sms_code", code)
        }

        showProgressDialog(getString(R.string.waiting))
        mHttpUtil.setOnRequestCallBack { code, data ->
            dismissProgressDialog()
          Log.i("FAN","登陆返回---> " + data)
            //Log.e("yancy", "登录返回==" + code)
            if (code == -1) {
                ToastUtils.showLong(code)
                return@setOnRequestCallBack
            }
            val type = object : TypeToken<BaseReq<RegisterEntity>>() {}.type
            var mReq: BaseReq<RegisterEntity> =
                GsonUtil.getGson().fromJson<BaseReq<RegisterEntity>>(data, type)
            if (!TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {
                ToastUtils.showLong(mReq.msg)
            } else {
                loginXMPP(mReq.result, mReq.result.userInfo.user_id.toString())
            }
        }.postParms(mParams, Config.API.login)
    }

    private fun saveInfo(info: RegisterEntity) {
        UserInfoManage.getInstance.saveToken(info.token)
        info.userInfo?.let {
            UserInfoManage.getInstance.saveInfo(info.userInfo)
        }
    }

    @SuppressLint("CheckResult")
    private fun loginXMPP(info: RegisterEntity, account: String) {
        Observable.just(arrayOf<String>(account, Config.XMPP.XMPP_PSW))
            .subscribeOn(Schedulers.io())
            .flatMap(Function<Array<String>, ObservableSource<Boolean>> { strings ->
                //                Log.i("FAN", "account---> ${strings[0]}")
                Observable.just(
                    SmackManager.getInstance().login(
                        strings[0],
                        strings[1]
                    )
                )
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { aBoolean ->

                if (aBoolean!!) {
                    saveInfo(info)
                    ARouter
                        .getInstance()
                        .build(RouterPath.MainCenter.PATH_MAIN)
                        .navigation()
                    finishs()
                } else {
                    ToastUtils.showLong("登陆失败,请重新登陆")
                }
            }
    }

    private fun sendSMS() {
        var mParams: HttpParams = httpParams
        mParams.put("phone", mEtPhone.text.trim().toString())
        mParams.put("type", "login")

        showProgressDialog(getString(R.string.waiting))
        mHttpUtil.setOnRequestCallBack { code, data ->
            dismissProgressDialog()
            if (code == -1) {
                ToastUtils.showShort(data)
                return@setOnRequestCallBack
            }
            var bean = GsonUtil.getGson().fromJson(data, BaseReq::class.java)
            ToastUtils.showLong(bean.msg)
        }.postParms(mParams, Config.API.send_sms)

    }

    override fun onDestroy() {
        super.onDestroy()
        CountTimeUtil.getInstance().reset()
    }

}