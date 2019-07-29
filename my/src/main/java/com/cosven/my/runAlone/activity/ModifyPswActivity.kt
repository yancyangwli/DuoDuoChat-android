package com.cosven.my.runAlone.activity

import android.text.TextUtils
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.cosven.my.R
import com.gyf.immersionbar.ImmersionBar
import com.woniu.core.activities.BaseActivity
import com.woniu.core.api.Config
import com.woniu.core.bean.BaseReq
import com.woniu.core.router.RegUtil
import com.woniu.core.utils.GsonUtil
import com.woniu.core.utils.HttpUtil
import com.zhouyou.http.model.HttpParams
import kotlinx.android.synthetic.main.activity_modify_psw.*
import kotlinx.android.synthetic.main.my_title_layout.*
import kotlinx.android.synthetic.main.my_title_layout.qmui_bar

/**
 * @author Anlycal<远>
 * @date 2019/5/30
 * @description 修改密码
 */


class ModifyPswActivity:BaseActivity() {

    private var mHttpUtil:HttpUtil? = null;

    override fun setContentViewId(): Int {
        return R.layout.activity_modify_psw
    }

    override fun initView() {
        setTitles(qmui_bar,"修改密码",true)
    }

    override fun initData() {
        mBtnConfirm.setOnClickListener(this)
    }

    override fun initStatusBar() {
        ImmersionBar.with(this)
            .titleBar(R.id.qmui_bar)
            .statusBarDarkFont(true)
            .keyboardEnable(true)  //解决软键盘与底部输入框冲突问题
            .init()
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.mBtnConfirm -> modifyOperation()
        }
    }

    /**
     * 修改操作
     */
    private fun modifyOperation(){
        var originPsw:String = mEtOriginPsw.text.toString().trim()
        if (originPsw.isNullOrBlank()){
            ToastUtils.showLong("请输入原密码")
            return
        }

        var newPsw:String = mEtNewPsw.text.toString().trim()
        if (newPsw.isNullOrBlank()){
            ToastUtils.showLong("请输入新密码")
            return
        }
        if (newPsw.length < 6){
            ToastUtils.showLong(getString(R.string.psw_length_err))
            return
        }

        if (!RegUtil.isPsw(newPsw)){
            ToastUtils.showLong(getString(R.string.psw_err))
            return
        }

        var confirmPsw:String = mEtConfrimPsw.text.toString().trim()
        if (confirmPsw.isNullOrBlank()){
            ToastUtils.showLong(getString(R.string.confirm_psw_input_hint))
            return
        }

        if (!TextUtils.equals(newPsw,confirmPsw)){
            ToastUtils.showLong(getString(R.string.confrim_psw_no_equals))
            return
        }

        var mParams:HttpParams = httpParams
        mParams.put("old_password",originPsw)
        mParams.put("password",newPsw)
        mParams.put("password_confirmation",confirmPsw)

        showProgressDialog(getString(R.string.waiting))
        if (mHttpUtil == null){
            mHttpUtil = HttpUtil(this)
        }
        mHttpUtil!!.setOnRequestCallBack { code, data ->
           dismissProgressDialog()
            if (code == -1){
                ToastUtils.showLong(data)
                return@setOnRequestCallBack
            }
            var mReq:BaseReq<*> = GsonUtil.getGson().fromJson(data,BaseReq::class.java)
            ToastUtils.showLong(mReq.msg)
            if (TextUtils.equals(mReq.status,Config.Constant.SUCCESS)){
                finishs()
            }
        }.postParms(mParams,Config.API.change_pwd)
    }

}