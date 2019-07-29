package com.cosven.socialim.contact.activity

import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ToastUtils
import com.cosven.socialim.contact.R
import com.cosven.socialim.contact.adapter.GroupAdapter
import com.dktlh.ktl.provider.router.RouterPath
import com.google.gson.reflect.TypeToken
import com.woniu.core.activities.BaseActivity
import com.woniu.core.api.Config
import com.woniu.core.bean.BaseListData
import com.woniu.core.bean.BaseReq
import com.woniu.core.bean.entity.GroupEntity
import com.woniu.core.manage.UserInfoManage
import com.woniu.core.utils.GsonUtil
import com.woniu.core.utils.HttpUtil
import com.woniu.core.xmpp.rxbus.event.BaseEvent
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.contact_group_list.*
import kotlinx.android.synthetic.main.contact_titile_layout.*
import java.lang.reflect.Type

/**
 * 群聊List展示页面
 */
class GroupActivity : BaseActivity() {
    private var show_checkbox: Boolean = false

    private lateinit var mGroupAdapter: GroupAdapter

    private var mShareHeadView: View? = null

    private var mHttpUtil: HttpUtil? = null

    private var mData: ArrayList<GroupEntity> = ArrayList()

//    private var arrays = Array<String>(20, {
//        it.toString()
//    })

    override fun setContentViewId(): Int {
        return R.layout.contact_group_list
    }

    override fun initView() {
        setTitles(qmui_bar, "群聊", true)

        intent?.let {
            if (it.hasExtra("SHOW_CHECKBOX")) {
                show_checkbox = it.getBooleanExtra("SHOW_CHECKBOX", false)
            }
        }

        mRvGroupList.layoutManager = LinearLayoutManager(this@GroupActivity)
//        mGroupAdapter = GroupAdapter(arrays.toMutableList())
        mGroupAdapter = GroupAdapter(mData)
        mGroupAdapter.showCheckBox = show_checkbox

        if (show_checkbox) {
            mBtnShare.visibility = View.VISIBLE

            mShareHeadView = LayoutInflater.from(this).inflate(R.layout.layout_share_friend_header, mRvGroupList, false)
            mGroupAdapter.setHeaderView(mShareHeadView)
        }

        mGroupAdapter.showCheckBox = show_checkbox
        mRvGroupList.adapter = mGroupAdapter

        initEvent()
    }

    override fun initData() {
        showProgressDialog(getString(R.string.waiting))
        if (mHttpUtil == null) {
            mHttpUtil = HttpUtil(this)
        }
        mHttpUtil?.setOnRequestCallBack { code, data ->
            dismissProgressDialog()
            Log.i("FAN", "获取群聊列表返回---> ${data}")
            if (code == -1) {
                ToastUtils.showLong(data)
                return@setOnRequestCallBack
            }

            val type: Type = object : TypeToken<BaseReq<MutableList<GroupEntity>>>() {}.type
            val mReq: BaseReq<MutableList<GroupEntity>> = GsonUtil.getGson().fromJson(data, type)
            if (TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {
                mReq.result?.let {
                    showData(it)
                }
            }
        }?.getParms(httpParams, Config.API.group_list)
    }

    private fun initEvent() {
        mGroupAdapter.setOnItemClickListener { adapter, view, position ->
            if (!show_checkbox) {
                var groupEntity = mData[position]
                groupEntity?.let {
                    ARouter
                        .getInstance()
                        .build(RouterPath.MesageCenter.PATH_CHAT_LIST)
                        .withString("USER_ID", it.group_id.toString())
                        .withString(
                            "USER_AVATAR", if (!it.group_avatar.isNullOrBlank()) {
                                it.group_avatar
                            } else {
                                ""
                            }
                        )
                        .withString(
                            "USER_NICKNAME", if (!it.group_name.isNullOrBlank()) {
                                it.group_name
                            } else {
                                ""
                            }
                        )
                        .withInt("CHAT_TYPE", BaseEvent.CHAT_TYPE.GROUP.typeValue)//群聊
                        .navigation()
                }
            }
        }

        mShareHeadView?.setOnClickListener {
            finishs()
        }
    }

    private fun showData(datas: List<GroupEntity>) {
        Observable.create(ObservableOnSubscribe<List<GroupEntity>> {
            mData.clear()
            mData.addAll(datas)
            it.onNext(mData)
            it.onComplete()
        })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(object : Observer<List<GroupEntity>> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: List<GroupEntity>) {
                    activity?.runOnUiThread {
                        mGroupAdapter.setNewData(mData)
                    }
                }

                override fun onError(e: Throwable) {
                }
            })
    }
}