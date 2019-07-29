package com.cosven.message.activity

import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ToastUtils
import com.cosven.message.R
import com.cosven.message.adapter.GroupMemberAdapter
import com.cosven.message.bean.GroupMemberListBean
import com.dktlh.ktl.provider.router.RouterPath
import com.google.gson.reflect.TypeToken
import com.qmuiteam.qmui.alpha.QMUIAlphaImageButton
import com.scwang.smartrefresh.layout.util.DensityUtil
import com.woniu.core.activities.BaseActivity
import com.woniu.core.api.Config
import com.woniu.core.bean.BaseReq
import com.woniu.core.bean.GroupMember
import com.woniu.core.bean.RxBusEvent
import com.woniu.core.utils.GsonUtil
import com.woniu.core.utils.HttpUtil
import com.woniu.core.xmpp.rxbus.RxBus
import com.zhouyou.http.model.HttpParams
import com.zyyoona7.popup.EasyPopup
import com.zyyoona7.popup.XGravity
import com.zyyoona7.popup.YGravity
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_group_member.*
import kotlinx.android.synthetic.main.message_titile_layout.*
import org.jetbrains.anko.startActivity

/**
 * @author Anlycal<远>
 * @date 2019/5/28
 * @description 群聊成员
 */


class GroupMemberActivity : BaseActivity() {
    private var mEasyPopup: EasyPopup? = null

    private val mHttpUtil = HttpUtil(context)

    private lateinit var mGroupMemberAdapter: GroupMemberAdapter

    private lateinit var mRightBotton: QMUIAlphaImageButton
    private lateinit var mRightText: Button

    private lateinit var mSearchHeadView: View
    private var groupManager: Int = 0
    private var mEventDisposable: Disposable? = null

    override fun setContentViewId(): Int {
        return R.layout.activity_group_member
    }

    override fun initView() {
        setTitles(qmui_bar, "群聊成员", true)
        groupManager = intent?.getIntExtra("GROUP_MANAGER", 0)!!

        mRightBotton = qmui_bar.addRightImageButton(R.mipmap.ic_three_line, R.id.click_three_line)

        mRightText = qmui_bar.addRightTextButton("取消", R.id.click_right_text)
        mRightText.setTextColor(Color.parseColor("#333333"))
        mRightText.textSize = 14f

        showRightCancel(false)

        mGroupMemberAdapter = GroupMemberAdapter(R.layout.message_item_group_member)

        mRvMemberList.layoutManager = LinearLayoutManager(context)

        mSearchHeadView = LayoutInflater.from(context).inflate(R.layout.layout_search_head, mRvMemberList, false)
        mGroupMemberAdapter.setHeaderView(mSearchHeadView)

        mRvMemberList.adapter = mGroupMemberAdapter

        initEvent()
    }

    override fun initData() {
        mRefreshLayout.autoRefresh()
        receiveEventMessage()
    }

    /**
     * 接收rxbus消息通知
     */
    private fun receiveEventMessage() {
        mEventDisposable = RxBus.getInstance()
            .toObserverable(RxBusEvent::class.java)
            .subscribe {
                if (it.updateGroupMember) {
                    mRefreshLayout.autoRefresh()
                }
            }
    }

    private fun initEvent() {
        mRefreshLayout.isEnableRefresh = true
        mRefreshLayout.setOnRefreshListener {
            loadData(true)
        }



        mSearchHeadView?.let { head ->
            head.setOnClickListener {
                ARouter
                    .getInstance()
                    .build(RouterPath.ContactCenter.PATH_CONTACT_SEARCH)
                    .navigation()
            }
        }

        mRightBotton.setOnClickListener {
            showPop(it)
        }

        mRightText.setOnClickListener {
            showRightCancel(false)
            notifyAdapter(false, 0)
        }

        mGroupMemberAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.mIvAvatar) {
                ARouter
                    .getInstance()
                    .build(RouterPath.MineCenter.PATH_USER_INFO)
                    .withInt("USER_ID", (adapter.data[position] as GroupMember).user_id)
                    .navigation()
            } else if (view.id == R.id.mLlMember) {
                startActivity<GroupMemberManageActivity>("GROUP_MEMBER" to adapter.data[position])
            }
        }
    }

    private fun showPop(v: View) {
        if (mEasyPopup == null) {
            mEasyPopup = EasyPopup.create()
            mEasyPopup!!.setContentView(context, R.layout.message_layout_batch)
                .setAnimationStyle(R.style.message_PopAnim)
                //是否允许点击PopupWindow之外的地方消失
                .setFocusAndOutsideEnable(true)
                .setBackgroundDimEnable(true)
                .setDimValue(0f)
                .apply()

            mEasyPopup?.let { ep ->
                ep.findViewById<TextView>(R.id.mTvDelete).setOnClickListener {
                    ep.dismiss()
                    showRightCancel(true)
                    notifyAdapter(true, 1)
                }
                ep.findViewById<TextView>(R.id.mTvBanned).setOnClickListener {
                    ep.dismiss()
                    showRightCancel(true)
                    notifyAdapter(true, 2)
                }
            }
        }
        mEasyPopup!!.showAtAnchorView(v, YGravity.BELOW, XGravity.LEFT, DensityUtil.dp2px(50F), 0)
    }

    /**
     * 是否显示右边取消键
     */
    private fun showRightCancel(show: Boolean) {
        if (show) {
            mRightText.visibility = View.VISIBLE
            mRightBotton.visibility = View.GONE
        } else {
            mRightText.visibility = View.GONE
            mRightBotton.visibility = View.VISIBLE
        }
    }

    /**
     * 刷新布局
     * @showCheck 是否显示选择框
     * @flag  标记  1为批量删除  2为批量禁言  0为重置
     */
    private fun notifyAdapter(showCheck: Boolean, flag: Int) {
        mGroupMemberAdapter.showSelectCheck = showCheck
        mGroupMemberAdapter.notifyDataSetChanged()

        when (flag) {
            0 -> withFlagVisiableView(View.GONE, View.GONE)

            1 -> withFlagVisiableView(View.VISIBLE, View.GONE)

            2 -> withFlagVisiableView(View.GONE, View.VISIBLE)
        }
    }

    /**
     * 根据标记显示视图
     * @flag 标记  1为批量删除  2为批量禁言  0为重置
     */
    private fun withFlagVisiableView(showDeleteView: Int, showBanned: Int) {
        mBtnDelete.visibility = showDeleteView
        mLlBannedRoot.visibility = showBanned
    }


    /**
     * 加载新数据
     * isRefresh true刷新，false加载更多
     */
    private fun loadData(isRefresh: Boolean) {

        if (!isRefresh) {
            (activity as BaseActivity).showProgressDialog(getString(R.string.waiting))
        }
        var httpParams = HttpParams()
        httpParams.put("group_id", intent?.getIntExtra("GROUP_ID", 0).toString())
        mHttpUtil.setOnRequestCallBack { code, data ->
            if (!isRefresh) {
                (activity as BaseActivity).dismissProgressDialog()
            } else run {
                mRefreshLayout.finishRefresh()

                if (code == -1) {
                    ToastUtils.showLong(data)
                    return@run
                }

                val type = object : TypeToken<BaseReq<GroupMemberListBean>>() {

                }.type

                val mReq = GsonUtil.getGson().fromJson<BaseReq<GroupMemberListBean>>(data, type)
                if (TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {

                    mRefreshLayout.isEnableLoadmore = !TextUtils.isEmpty(mReq.result.next_page_url)

                    mGroupMemberAdapter.setNewData(mReq.result.data)
                    mReq.result.data?.let {
                        mGroupMemberAdapter.managerNumber =
                            mGroupMemberAdapter.data.filterNot { it.group_manager == 0 }.size
                    }
                } else {
                    ToastUtils.showLong(mReq.msg)
                }
            }
        }.getParms(httpParams, Config.API.group_members)
    }

    override fun onDestroy() {
        super.onDestroy()
        mEventDisposable?.dispose()
        mEventDisposable = null
    }
}