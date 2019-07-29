package com.cosven.socialim.ui.activity.me

import android.text.TextUtils
import android.view.View
import android.widget.ImageButton
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.ToastUtils
import com.cosven.my.R
import com.dktlh.ktl.provider.router.RouterPath
import com.woniu.core.activities.BaseActivity
import com.woniu.core.api.Config
import com.woniu.core.bean.BaseReq
import com.woniu.core.manage.UserInfoManage
import com.woniu.core.utils.AppManager
import com.woniu.core.utils.GsonUtil
import com.woniu.core.utils.HttpUtil
import com.zhouyou.http.model.HttpParams
import com.zhuanxu.eclipse.aop.annotation.SingleClick
import kotlinx.android.synthetic.main.activity_feed.*

/**
 * 意见反馈
 */
@Route(path = RouterPath.MineCenter.PATH_MINE_FEED_BACK)
class FeedbackActivity : BaseActivity() {

    private var flag = ""
    private var objectId = 0
    private var mHttpUtil: HttpUtil = HttpUtil(this)
    @SingleClick
    override fun onClick(v: View?) {
    }

    override fun setContentViewId(): Int {
        return R.layout.activity_feed
    }

    override fun initView() {
        flag = intent?.getStringExtra("FLAG") ?: ""
        objectId = intent?.getIntExtra("OBJECT_ID", 0) ?: 0
        if (flag.isNotEmpty()) {
            setTitles(qmui_bar, "举报", true)
        } else {
            setTitles(qmui_bar, "意见反馈", true)
        }
        mBtnConfirm.setOnClickListener {
            if (flag.isNotEmpty()) {
                report()
            } else {
                feedback()
            }
        }
    }

    /**
     * 举报用户或群
     */
    private fun report() {
        var mParams: HttpParams = httpParams
        mParams.put("content", mContentEt.text.toString())
        mParams.put("target_id", objectId.toString())
        mParams.put("type", flag)

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
                finish()
            }

        }.postParms(mParams, Config.API.report)
    }

    private fun feedback() {
        var mParams: HttpParams = httpParams
        mParams.put("content", mContentEt.text.toString())

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
                finish()
            }

        }.postParms(mParams, Config.API.suggest)
    }

    override fun initData() {

    }
}