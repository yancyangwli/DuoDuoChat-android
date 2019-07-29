package com.cosven.my.runAlone.activity

import android.content.Intent
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import com.blankj.utilcode.util.RegexUtils
import com.blankj.utilcode.util.SpanUtils
import com.blankj.utilcode.util.ToastUtils
import com.cosven.my.R
import com.google.gson.reflect.TypeToken
import com.gyf.immersionbar.ImmersionBar
import com.woniu.core.activities.BaseActivity
import com.woniu.core.api.Config
import com.woniu.core.bean.BaseReq
import com.woniu.core.bean.entity.RegisterEntity
import com.woniu.core.manage.UserInfoManage
import com.woniu.core.router.RegUtil
import com.woniu.core.utils.SpannableStringUtils
import com.woniu.core.utils.CountTimeUtil
import com.woniu.core.utils.GsonUtil
import com.woniu.core.utils.HttpUtil
import com.zhouyou.http.model.HttpParams
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.login_item.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import java.lang.ref.WeakReference
import java.lang.reflect.Type

/**
 * 注册
 */
class RegisteredActivity : BaseActivity() {

    private val mHttpUtil: HttpUtil = HttpUtil(this)

    override fun setContentViewId(): Int {
        return R.layout.activity_register
    }

    override fun initStatusBar() {
        ImmersionBar.with(this)
            .titleBar(R.id.toolbar)
            .statusBarDarkFont(true)
            .keyboardEnable(true)  //解决软键盘与底部输入框冲突问题
            .init()
    }

    override fun initView() {
        mIvBack.setOnClickListener(this)
        mBtnRegist.setOnClickListener(this)
        mTvCode.setOnClickListener(this)
        mTvPrivacyPolicy.setOnClickListener(this)
        mTvServiceAgreement.setOnClickListener(this)
    }

    override fun initData() {
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.mTvPrivacyPolicy -> ToastUtils.showLong("隐私政策")
            R.id.mTvServiceAgreement -> ToastUtils.showLong("服务协议")
            R.id.mBtnRegist -> registOperation()
            R.id.mIvBack -> finishs()
            R.id.mTvCode ->
                if (CountTimeUtil.getInstance().startCountTime(mEtPhone, mTvCode)) {
                    sendSMS()
                }

        }
    }

    private fun registOperation() {
        val phone: String = mEtPhone.text.trim().toString()
        if (phone.isNullOrBlank()) {
            ToastUtils.showShort("请输入手机号")
            return
        }
        if (!RegexUtils.isMobileExact(phone)) {
            ToastUtils.showShort("手机号码错误")
            return
        }

        val code: String = mEtCode.text.trim().toString()
        if (code.isNullOrBlank()) {
            ToastUtils.showShort("请输入验证码")
            return
        }

        val psw: String = mEtPsw.text.trim().toString()
        if (psw.isNullOrBlank()) {
            ToastUtils.showShort("请设置密码")
            return
        }

        if (psw.length < 6) {
            ToastUtils.showShort("密码至少为6位")
            return
        }

        if (!RegUtil.isPsw(psw)) {
            ToastUtils.showShort("密码必须是数字加字母的组合")
            return
        }

        val confirmPsw: String = mEtConfrimPsw.text.trim().toString()
        if (confirmPsw.isNullOrBlank()) {
            ToastUtils.showShort("请确认密码")
            return
        }

        if (!TextUtils.equals(psw, confirmPsw)) {
            ToastUtils.showShort("确认密码不一致")
            return
        }

        if (!mCbAgree.isChecked) {
            ToastUtils.showShort("请同意《隐私政策》和《服务协议》")
            return
        }

        var mParams: HttpParams = httpParams

        mParams.put("phone", phone)
        mParams.put("password", psw)
        mParams.put("password_confirmation", confirmPsw)
        mParams.put("sms_code", code)

        showProgressDialog(getString(R.string.waiting))
        mHttpUtil.setOnRequestCallBack { code, data ->
            Log.i("FAN", "注册返回---> " + data)

            //{"result":{"token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczpcL1wvd3d3LmR1b2R1b2NoYXQudG9wXC9hcGlcL3JlZ2lzdGVyIiwiaWF0IjoxNTY0MTA2Nzg3LCJleHAiOjE3MjQ4MTA3ODcsIm5iZiI6MTU2NDEwNjc4NywianRpIjoiSE12dk94T1lBdDRZbTZnTyIsInN1YiI6MjkyMzM5NywicHJ2IjoiMjNiZDVjODk0OWY2MDBhZGIzOWU3MDFjNDAwODcyZGI3YTU5NzZmNyJ9.n7OabtRF2y9Gyi_XxVlDDX_bv0VzMudAo5iIGW5RmcI","userInfo":{"phone":"13302020202","user_id":2923397,"nickname":"duo_ukFIewEbZWHQ","last_login_ip":"113.246.153.18","is_login":1,"updated_at":"2019-07-26 10:06:26","created_at":"2019-07-26 10:06:26"},"token_type":"bearer","expires_in":160704000},"msg":"","status":"success"}
            dismissProgressDialog()
            if (code == -1) {
                ToastUtils.showLong(data)
                return@setOnRequestCallBack
            }
//            var mRegisterEntity: BaseReq<RegisterEntity> =
//                GsonUtil.getGson().fromJson<BaseReq<RegisterEntity>>(data, BaseReq::class.java)

            var type: Type = object : TypeToken<BaseReq<RegisterEntity>>() {}.type

            var mReq: BaseReq<RegisterEntity> = GsonUtil.getGson().fromJson<BaseReq<RegisterEntity>>(data, type)

            if (!TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {
                ToastUtils.showLong(mReq.msg)
            } else {
                // startActivity<EditInfoActivity>()
                //ToastUtils.showLong("注册成功")
                //finishs()
                UserInfoManage.getInstance.saveToken(mReq.result.token)
                mReq.result.userInfo?.let {
                    UserInfoManage.getInstance.saveInfo(mReq.result.userInfo)
                }
                val intent = Intent(this, EditInfoActivity::class.java)
                intent.putExtra("TYPE", 0)
                intent.putExtra("TOKEN", mReq.result.token);
                startActivity(intent)
            }
        }.postParms(mParams, Config.API.register)
    }


    private fun sendSMS() {
        var mParams: HttpParams = WeakReference(HttpParams()).get()!!
        mParams.put("phone", mEtPhone.text.trim().toString())
        mParams.put("type", "register")

        showProgressDialog(getString(R.string.waiting))
        mHttpUtil.setOnRequestCallBack { code, data ->
            //            Log.i("FAN","注册返回---> $data")
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