package com.cosven.socialim.ui.activity.me

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import android.widget.ImageButton
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ToastUtils
import com.cosven.my.R
import com.cosven.my.runAlone.activity.LoginActivity
import com.cosven.my.runAlone.activity.ModifyPswActivity
import com.dktlh.ktl.provider.router.RouterPath
import com.woniu.core.activities.BaseActivity
import com.woniu.core.api.Config
import com.woniu.core.bean.BaseReq
import com.woniu.core.bean.entity.UserInfoEntity
import com.woniu.core.db.DbHelper
import com.woniu.core.manage.UserInfoManage
import com.woniu.core.utils.AppManager
import com.woniu.core.utils.GsonUtil
import com.woniu.core.utils.HttpUtil
import com.zhouyou.http.model.HttpParams
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_set.*
import kotlinx.android.synthetic.main.my_title_layout.*
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange
import org.jetbrains.anko.startActivity

/**
 * 设置
 */
class SettingActivity : BaseActivity() {

    private var mHttpUtil: HttpUtil = HttpUtil(this)
    private var mUserInfoEntity: UserInfoEntity? = null

    override fun setContentViewId(): Int {
        return R.layout.activity_set
    }

    override fun initView() {
        setTitles(qmui_bar, "设置", true)
        mUserInfoEntity = UserInfoManage.getInstance.mUserInfoEntity
        mUserInfoEntity?.let {
            mAddFriendSwitch.isChecked = it.is_allow_add_friend == 1
            mShowGroupNoticeSwitch.isChecked = it.is_show_group_notify == 1
        }

    }

    override fun initData() {
        mTvFeedback.setOnClickListener(this)
        mTvClearCache.setOnClickListener(this)
        mTvModifyPsw.setOnClickListener(this)
        mBtnOut.setOnClickListener(this)
        mAddFriendSwitch.setOnCheckedChangeListener { _, isChecked ->
            editInfo(if (isChecked) 1 else 0, -1)
        }
        mShowGroupNoticeSwitch.setOnCheckedChangeListener { _, isChecked ->
            editInfo(-1, if (isChecked) 1 else 0)
        }
    }

    /**
     * 更新用户信息
     * is_allow_add_friend   是否允许添加好友，1是，0否
     * is_show_group_notify  是否显示群通知，1是，0否
     */
    private fun editInfo(is_allow_add_friend: Int, is_show_group_notify: Int) {
        var mParams: HttpParams = httpParams

        if (is_allow_add_friend != -1) {
            mParams.put("is_allow_add_friend", is_allow_add_friend.toString())
        }

        if (is_show_group_notify != -1) {
            mParams.put("is_show_group_notify", is_show_group_notify.toString())
        }

        showProgressDialog(getString(R.string.waiting))
        mHttpUtil.setOnRequestCallBack { code, data ->
            dismissProgressDialog()
            if (code == -1) {
                ToastUtils.showLong(data)
                return@setOnRequestCallBack
            }

            var mReq: BaseReq<*> = GsonUtil.getGson().fromJson(data, BaseReq::class.java)
            ToastUtils.showLong(mReq.msg)

            if (TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {
                mUserInfoEntity?.let {
                    if (is_allow_add_friend != -1) {
                        it.is_allow_add_friend = is_allow_add_friend
                    }
                    if (is_show_group_notify != -1) {
                        it.is_show_group_notify = is_show_group_notify
                    }
                    UserInfoManage.getInstance.mUserInfoEntity = it
                }
            }

        }.postParms(mParams, Config.API.mine_information)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.mTvFeedback -> startActivity<FeedbackActivity>()
            R.id.mTvClearCache -> startActivity<ClearCacheActivity>()
            R.id.mTvModifyPsw -> startActivity<ModifyPswActivity>()
            R.id.mBtnOut -> outOperation()
        }
    }

    /**
     * 退出操作
     */
    @SuppressLint("CheckResult")
    private fun outOperation() {
        Observable.create<Boolean> {
//            DbHelper.deleteChatMessageAll()
//            DbHelper.deleteMessageTempEventAll()
            UserInfoManage.getInstance.clearUserInfo()
//            SmackManager.getInstance().logout()
            it.onNext(true)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it) {
                    AppManager.get().finishAllActivity()
                    startActivity<LoginActivity>()
                }
            }

    }


}