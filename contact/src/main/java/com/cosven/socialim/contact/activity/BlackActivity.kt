package com.cosven.socialim.contact.activity

import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ToastUtils
import com.cosven.socialim.contact.R
import com.cosven.socialim.contact.adapter.BlackAdapter
import com.dktlh.ktl.provider.router.RouterPath
import com.google.gson.reflect.TypeToken
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener
import com.woniu.core.activities.BaseActivity
import com.woniu.core.api.Config
import com.woniu.core.bean.BaseListData
import com.woniu.core.bean.BaseReq
import com.woniu.core.bean.entity.ContactEntity
import com.woniu.core.utils.GsonUtil
import com.woniu.core.utils.HttpUtil
import com.zhouyou.http.model.HttpParams
import kotlinx.android.synthetic.main.contact_black_layout.*
import kotlinx.android.synthetic.main.contact_titile_layout.*
import java.lang.reflect.Type

/**
 * 黑名单页面
 */
class BlackActivity : BaseActivity() {

    private lateinit var mBlackAdapter: BlackAdapter

    private var page: Int = 1

    private var mBlockListDatas: ArrayList<ContactEntity> = ArrayList()

    private var mHttpUtil: HttpUtil = HttpUtil(this)

    override fun setContentViewId(): Int {
        return R.layout.contact_black_layout
    }

    override fun initView() {
        setTitles(qmui_bar, "黑名单", true)

        findViewById<ClassicsHeader>(R.id.header).setAccentColor(Color.parseColor("#666666"))
        mRvBlackList.layoutManager = LinearLayoutManager(this@BlackActivity)
        mBlackAdapter = BlackAdapter(mBlockListDatas)
        mBlackAdapter.addHeaderView(
            LayoutInflater.from(this).inflate(
                R.layout.layout_shadow_header,
                mRvBlackList,
                false
            )
        )
        mRvBlackList.adapter = mBlackAdapter
        initEvent()

        loadData(false)
    }

    override fun initData() {
    }

    private fun initEvent() {
        mBlackAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.mLlBlackRoot) {
                ARouter
                    .getInstance()
                    .build(RouterPath.MineCenter.PATH_USER_INFO)
                    .withInt("USER_ID", mBlockListDatas[position].friend_user_id)
                    .navigation()
            } else if (view.id == R.id.mBtnDelete) {
                removeBlockFriend(mBlockListDatas[position], position)
            }
        }

        mRefreshLayout.setOnRefreshLoadmoreListener(object : OnRefreshLoadmoreListener {
            override fun onRefresh(refreshlayout: RefreshLayout?) {
                page = 1
                loadData(true)
            }

            override fun onLoadmore(refreshlayout: RefreshLayout?) {
                page++
                loadData(true)
            }
        })
    }

    private fun loadData(isRefresh: Boolean) {
        var mParams: HttpParams = httpParams
        mParams.put("page", page.toString())

        if (!isRefresh) {
            showProgressDialog(getString(R.string.waiting))
        }

        mHttpUtil.setOnRequestCallBack { code, data ->
            if (!isRefresh) {
                dismissProgressDialog()
            } else {
                if (page == 1) {
                    mRefreshLayout.finishRefresh()
                } else {
                    mRefreshLayout.finishLoadmore()
                }
            }

            if (code == -1) {
                ToastUtils.showLong(data)
                return@setOnRequestCallBack
            }

            var type: Type = object : TypeToken<BaseReq<BaseListData<ContactEntity>>>() {}.type
            var mReq: BaseReq<BaseListData<ContactEntity>> = GsonUtil.getGson().fromJson(data, type)
            if (!TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {
                ToastUtils.showLong(mReq.msg)
                if (page > 1) page--
            } else {
                mRefreshLayout.isEnableLoadmore = !mReq.result.next_page_url.isNullOrBlank()

                mReq.result.data?.let {
                    if (page == 1) {
                        mBlockListDatas.clear()
                        mBlockListDatas.addAll(it)
                        mBlackAdapter.notifyDataSetChanged()
                    }
                }
            }
        }.getParms(mParams, Config.API.friend_blacklist)
    }

    /**
     * 移出或者加入黑名单
     */
    private fun removeBlockFriend(user: ContactEntity, position: Int) {
        var mParams: HttpParams = httpParams
        mParams.put("block_user_id", user.is_block.toString())
        showProgressDialog(getString(R.string.waiting))

        mHttpUtil.setOnRequestCallBack { code, data ->
            dismissProgressDialog()
            if (code == -1) {
                ToastUtils.showLong(data)
            } else {
                var mReq: BaseReq<*> = GsonUtil.getGson().fromJson(data, BaseReq::class.java)
                ToastUtils.showLong(mReq.msg)
                mBlockListDatas.removeAt(position)
                mBlackAdapter.notifyItemRemoved(position + 1)
            }
        }.postParms(mParams, Config.API.friend_remove_blacklist)
    }

}