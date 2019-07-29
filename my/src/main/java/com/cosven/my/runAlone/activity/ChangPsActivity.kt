package com.cosven.my.runAlone.activity

import android.text.TextUtils
import android.util.Log
import android.view.View
import com.blankj.utilcode.util.RegexUtils
import com.blankj.utilcode.util.ToastUtils
import com.cosven.my.R
import com.woniu.core.activities.BaseActivity
import com.woniu.core.api.Config
import com.woniu.core.bean.BaseReq
import com.woniu.core.router.RegUtil
import com.woniu.core.utils.CountTimeUtil
import com.woniu.core.utils.GsonUtil
import com.woniu.core.utils.HttpUtil
import com.woniu.core.utils.TimerCount
import com.zhouyou.http.model.HttpParams
import com.zhuanxu.eclipse.aop.annotation.SingleClick
import kotlinx.android.synthetic.main.activity_chang_pss.*
import kotlinx.android.synthetic.main.login_item.*
import java.lang.ref.WeakReference

/**
 * 修改密码
 */
class ChangPsActivity : BaseActivity() {

    private var mHttpUtil:HttpUtil = HttpUtil(this)

    override fun setContentViewId(): Int {
        return R.layout.activity_chang_pss
    }

    override fun initView() {
    }

    override fun initData() {
        mIvBack.setOnClickListener(this)
        mTvCode.setOnClickListener(this)
        mBtnModify.setOnClickListener(this)
    }

    @SingleClick
    override fun onClick(v: View) {
        when(v.id){
            R.id.mTvCode->
                if (CountTimeUtil.getInstance().startCountTime(mEtPhone,mTvCode)){
                    sendSMS()
                }
            R.id.mIvBack-> finishs()
            R.id.mBtnModify-> modifyOpetion()
        }
    }

    private fun modifyOpetion(){
        var phone:String = mEtPhone.text.trim().toString()
        if (phone.isNullOrBlank()){
            ToastUtils.showLong(getString(R.string.phone_input_hint))
            return
        }

        if (!RegexUtils.isMobileExact(phone)){
            ToastUtils.showLong(getString(R.string.phone_err))
            return
        }

        var code:String = mEtCode.text.trim().toString()
        if (code.isNullOrBlank()){
            ToastUtils.showLong(getString(R.string.code_input_hint))
            return
        }

        var psw:String = mEtPsw.text.trim().toString()
        if (psw.isNullOrBlank()){
            ToastUtils.showLong(getString(R.string.psw_input_hint))
            return
        }

        if (psw.length < 6){
            ToastUtils.showLong(getString(R.string.psw_length_err))
            return
        }

        if (!RegUtil.isPsw(psw)){
            ToastUtils.showLong(getString(R.string.psw_err))
        }

        var confirmPsw:String = mEtConfrimPsw.text.trim().toString()
        if (confirmPsw.isNullOrBlank()){
            ToastUtils.showLong(getString(R.string.confirm_psw_input_hint))
            return
        }

        if (!TextUtils.equals(psw,confirmPsw)){
            ToastUtils.showLong(getText(R.string.confrim_psw_no_equals))
            return
        }

        var mParams:HttpParams = httpParams
        mParams.put("phone",phone)
        mParams.put("password",psw)
        mParams.put("sms_code",code)

        showProgressDialog(getString(R.string.waiting))
        mHttpUtil.setOnRequestCallBack { code, data ->
            dismissProgressDialog()
//            Log.i("FAN","修改密码---> " + data)
            if (code == -1){
                ToastUtils.showLong(data)
                return@setOnRequestCallBack
            }
            var mReq:BaseReq<*> = GsonUtil.getGson().fromJson(data,BaseReq::class.java)
            ToastUtils.showLong(mReq.msg)
            if (TextUtils.equals(mReq.status,Config.Constant.SUCCESS)){
                finishs()
            }
        }.postParms(mParams,Config.API.forget_pwd)
    }

    private fun sendSMS() {
        var mParams: HttpParams = WeakReference(HttpParams()).get()!!
        mParams.put("phone", mEtPhone.text.trim().toString())
        mParams.put("type", "forget_password")

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