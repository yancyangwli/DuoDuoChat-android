package com.cosven.my.runAlone.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.cosven.my.R
import com.cosven.my.runAlone.adapter.IntergralAdapter
import com.google.gson.reflect.TypeToken
import com.woniu.core.activities.BaseActivity
import com.woniu.core.api.Config
import com.woniu.core.bean.BaseReq
import com.woniu.core.bean.entity.IntegralListEntity
import com.woniu.core.bean.entity.IntegralRecordBean
import com.woniu.core.fragment.BaseFragment
import com.woniu.core.utils.GsonUtil
import com.woniu.core.utils.HttpUtil
import com.zhouyou.http.model.HttpParams
import kotlinx.android.synthetic.main.fragment_integral.*
import kotlinx.android.synthetic.main.fragment_integral.view.*
import java.util.*

/**
 * 积分
 */
class IntegralFragment : BaseFragment() {

    private val mHttpUtil = HttpUtil(context)
    private var page = 1

    private lateinit var mIntergralAdapter: IntergralAdapter

    private val mData = ArrayList<IntegralRecordBean>()

    override fun setView(): Int {
        return R.layout.fragment_integral
    }

    override fun onClick(v: View?) {
    }

    override fun initView() {
        inflate.mRecyclerView.layoutManager = LinearLayoutManager(context)
        mIntergralAdapter = IntergralAdapter(R.layout.item_intergral)
        mIntergralAdapter.setNewData(mData)
        inflate.mRecyclerView.adapter = mIntergralAdapter

        loadData(true)
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
        httpParams.put("integral_type", arguments?.getString(TYPE) ?: "")
        mHttpUtil.setOnRequestCallBack { code, data ->
            if (!isRefresh) {
                (activity as BaseActivity).dismissProgressDialog()
            } else run {
                if (page == 1) {
                    mRefreshLayout.finishRefresh()
                } else {
                    mRefreshLayout.finishLoadmore()
                }

                if (code == -1) {
                    ToastUtils.showLong(data)
                    return@run
                }

                val type = object : TypeToken<BaseReq<IntegralListEntity>>() {

                }.type

                val mReq = GsonUtil.getGson().fromJson<BaseReq<IntegralListEntity>>(data, type)
                if (TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {

                    mRefreshLayout.isEnableLoadmore = !TextUtils.isEmpty(mReq.result.next_page_url)

                    if (page == 1) {
                        mData.clear()
                    }
                    mData.addAll(mReq.result.data!!)
                    mIntergralAdapter.notifyDataSetChanged()

                } else {
                    if (page > 1) {
                        page--
                    }
                    ToastUtils.showLong(mReq.msg)
                }
            }
        }.getParms(httpParams, Config.API.mine_integral_log)
    }

    companion object {
        const val TYPE = "type"
        fun newInstance(param: String): IntegralFragment {
            var fragment = IntegralFragment()
            val args: Bundle? = Bundle()
            args?.putString(TYPE, param)
            fragment.arguments = args
            return fragment
        }
    }

}