package com.cosven.socialim.ui.activity.me

import android.text.TextUtils
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ToastUtils
import com.cosven.my.R
import com.dktlh.ktl.provider.router.RouterPath
import com.woniu.core.activities.BaseActivity
import com.woniu.core.api.Config
import com.woniu.core.bean.BaseReq
import com.woniu.core.bean.RxBusEvent
import com.woniu.core.utils.GsonUtil
import com.woniu.core.utils.HttpUtil
import com.woniu.core.xmpp.rxbus.RxBus
import com.zhouyou.http.model.HttpParams
import kotlinx.android.synthetic.main.activity_friend_set.*
import org.jetbrains.anko.startActivity

/**
 * 好友设置
 */
class FriendSetActivity : BaseActivity() {
    private var mHttpUtil: HttpUtil = HttpUtil(this)
    private var friend_id = 0

    override fun setContentViewId(): Int {
        return R.layout.activity_friend_set
    }

    override fun initView() {
        setTitles(qmui_bar, "好友设置", true)
        qmui_bar.apply {
            addRightTextButton("举报", R.id.click_right_text).setOnClickListener {
                startActivity<FeedbackActivity>("FLAG" to "member", "OBJECT_ID" to friend_id)
            }
        }
        friend_id = intent?.run {
            getIntExtra("FRIEND_ID", 0)
        } ?: 0

        initEvent()
    }

    override fun initData() {
    }

    private fun initEvent() {
        mLlRecommend.setOnClickListener {
            //            ARouter
//                .getInstance()
//                .build(RouterPath.ContactCenter.PATH_CONTACT_PERSONAL)
//                .navigation()
        }

        mBtnDeleteFriend.setOnClickListener {
            deleteFriend()
        }

    }

    /**
     * 删除好友
     */
    private fun deleteFriend() {
        var mParams: HttpParams = httpParams
        mParams.put("friend_id", friend_id.toString())

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
                RxBus.getInstance().post(RxBusEvent().apply { deleteFriendWithId = friend_id })
                finish()
            }

        }.postParms(mParams, Config.API.friend_remove)
    }
}
