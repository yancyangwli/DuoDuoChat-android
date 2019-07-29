package com.cosven.my.runAlone.activity

import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ToastUtils
import com.cosven.my.R
import com.cosven.my.runAlone.adapter.CollectionAdapter
import com.dktlh.ktl.provider.router.RouterPath
import com.google.gson.reflect.TypeToken
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener
import com.woniu.core.activities.BaseActivity
import com.woniu.core.api.Config
import com.woniu.core.bean.BaseListData
import com.woniu.core.bean.BaseReq
import com.woniu.core.bean.entity.CollectionEntity
import com.woniu.core.utils.GsonUtil
import com.woniu.core.utils.HttpUtil
import com.zhouyou.http.model.HttpParams
import kotlinx.android.synthetic.main.activity_my_collection.*
import kotlinx.android.synthetic.main.my_title_layout.*
import java.lang.reflect.Type

/**
 * @author Anlycal<远>
 * @date 2019/6/10
 * @description 我的收藏
 */

class MyCollectionActivity : BaseActivity() {

    private var page: Int = 1;

    private var mHttpUtil: HttpUtil = HttpUtil(this)

    private var mCollectionDatas:ArrayList<CollectionEntity> = ArrayList()

    private lateinit var mCollectionAdapter:CollectionAdapter

    override fun setContentViewId(): Int {
        return R.layout.activity_my_collection
    }

    override fun initView() {
        setTitles(qmui_bar, "我的收藏", true)

        findViewById<ClassicsHeader>(R.id.header).setAccentColor(Color.parseColor("#666666"))
        mRvCollectionList.layoutManager = LinearLayoutManager(this)

        mCollectionAdapter = CollectionAdapter(mCollectionDatas)
        var mShadowView:View = LayoutInflater.from(this).inflate(R.layout.layout_shadow_header,mRvCollectionList,false)
        mCollectionAdapter.setHeaderView(mShadowView)

        mRvCollectionList.adapter = mCollectionAdapter

        loadCollectionList(false)
    }

    override fun initData() {

        mRefreshLayout.setOnRefreshLoadmoreListener(object : OnRefreshLoadmoreListener {
            override fun onLoadmore(refreshlayout: RefreshLayout) {
                page ++
                loadCollectionList(true)
            }

            override fun onRefresh(refreshlayout: RefreshLayout) {
                page = 1
                loadCollectionList(true)
            }
        })


        mCollectionAdapter.setOnItemClickListener { adapter, view, position ->
            var ce:CollectionEntity = mCollectionDatas.get(position)
            if (TextUtils.equals(ce.type,Config.Favorite.MONENT)) {
                ARouter
                    .getInstance()
                    .build(RouterPath.DynamicCenter.PATH_DYNAMIC_DETAIL)
                    .withInt("MOMENT_ID",ce.target_id)
                    .navigation()
            }
        }
    }

    private fun loadCollectionList(isRefresh: Boolean) {
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

            var type: Type = object : TypeToken<BaseReq<BaseListData<CollectionEntity>>>() {}.type
            var mReq: BaseReq<BaseListData<CollectionEntity>> = GsonUtil.getGson().fromJson(data, type)
            if (TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {
               if (mReq.result.next_page_url.isNullOrBlank()){
                   mRefreshLayout.setEnableLoadmore(false)
               }else{
                   mRefreshLayout.setEnableLoadmore(true)
               }
                mReq.result.data?.let {
                    if (page == 1){
                        mCollectionDatas.clear()
                    }
                    mCollectionDatas.addAll(it)
                    mCollectionAdapter.notifyDataSetChanged()
                }

            } else {
                if (page > 1){
                    page --
                }
                ToastUtils.showLong(mReq.msg)
            }

        }.getParms(mParams,Config.API.favorite)
    }

}