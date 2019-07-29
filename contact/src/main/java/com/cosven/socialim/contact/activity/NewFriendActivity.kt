package com.cosven.socialim.contact.activity

import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ToastUtils
import com.cosven.socialim.contact.R
import com.cosven.socialim.contact.adapter.NewFriendAdapter
import com.dktlh.ktl.provider.router.RouterPath
import com.google.gson.reflect.TypeToken
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener
import com.woniu.core.activities.BaseActivity
import com.woniu.core.api.Config
import com.woniu.core.bean.BaseListData
import com.woniu.core.bean.BaseReq
import com.woniu.core.bean.entity.ApplyFriendEntity
import com.woniu.core.utils.GsonUtil
import com.woniu.core.utils.HttpUtil
import com.zhouyou.http.model.HttpParams
import kotlinx.android.synthetic.main.contact_activity_new_friend.*
import kotlinx.android.synthetic.main.contact_titile_layout.*
import org.jetbrains.anko.startActivity
import java.lang.reflect.Type

/**
 * 新朋友
 */
class NewFriendActivity : BaseActivity() {

    private lateinit var mNewFriendAdapter: NewFriendAdapter

    private var mHttpUtil: HttpUtil = HttpUtil(this)

    private var page: Int = 1

    private var mDatas: ArrayList<ApplyFriendEntity> = ArrayList()

    override fun setContentViewId(): Int {
        return R.layout.contact_activity_new_friend
    }

    override fun initView() {
        setTitles(qmui_bar, "新朋友", true)

        findViewById<ClassicsHeader>(R.id.header).setAccentColor(Color.parseColor("#666666"))

        // 右边的点击栏目
        qmui_bar.addRightImageButton(R.mipmap.icon_contact_gray, R.id.empty_button).setOnClickListener {
            startActivity<AddActivity>()
        }

        mRvNewFriendList.layoutManager = LinearLayoutManager(this)

        mNewFriendAdapter = NewFriendAdapter(mDatas)

        var mHeaderView: View =
            LayoutInflater.from(this).inflate(R.layout.layout_shadow_header, mRvNewFriendList, false)

        mNewFriendAdapter.setHeaderView(mHeaderView)

        mRvNewFriendList.adapter = mNewFriendAdapter

        initEvent()
        loadData(false)
    }

    override fun initData() {

        mRefreshLayout.setOnRefreshLoadmoreListener(object : OnRefreshLoadmoreListener {

            override fun onRefresh(refreshlayout: RefreshLayout) {
                page = 1
                loadData(true)
            }

            override fun onLoadmore(refreshlayout: RefreshLayout) {
                page++
                loadData(true)
            }
        })

    }

    private fun initEvent() {
        mNewFriendAdapter.setOnItemClickListener { adapter, view, position ->
            var  mEntity:ApplyFriendEntity = mDatas[position]
            if (mEntity.apply_status == ApplyFriendEntity.PENDING) {
                startActivity<FriendRequestsActivity>(Pair("FRIEND_INFO", mDatas[position]))
            }else {

            }
        }
    }

    private fun loadData(isRefresh: Boolean) {
        var mParams: HttpParams = httpParams
        mParams.put("page", page.toString())

        if (!isRefresh) {
            showProgressDialog(getString(R.string.waiting))
        }
        mHttpUtil.setOnRequestCallBack { code, data ->
//            Log.i("FAN","申请列表--->  ${data}")
            if (!isRefresh) {
                dismissProgressDialog()
            } else {
                if (page == 1) {
                    mRefreshLayout.finishRefresh()
                } else {
                    mRefreshLayout.finishLoadmore()
                }
            }

            if (code == -1){
                ToastUtils.showLong(data)
                return@setOnRequestCallBack
            }

            var type: Type = object : TypeToken<BaseReq<BaseListData<ApplyFriendEntity>>>() {}.type
            var mReq: BaseReq<BaseListData<ApplyFriendEntity>> = GsonUtil.getGson().fromJson(data, type)
            if (TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {
                mRefreshLayout.isEnableLoadmore = !mReq.result.next_page_url.isNullOrBlank()

                mReq.result.data?.let {
                    if (page == 1) {
                        mDatas.clear()
                    }
                    mDatas.addAll(it)
                    mNewFriendAdapter.notifyDataSetChanged()
                }
            } else {
                if (page > 1) page--
                ToastUtils.showLong(mReq.msg)
            }

        }.getParms(mParams, Config.API.friend_apply_list)
    }

}