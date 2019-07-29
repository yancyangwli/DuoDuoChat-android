package com.cosven.socialim.contact.activity

import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextSwitcher
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.cosven.message.activity.ChatActivity
import com.cosven.message.adapter.MesageListAdapter
import com.cosven.socialim.contact.R
import com.cosven.socialim.contact.adapter.ContactSortAdapter
import com.cosven.socialim.contact.adapter.SearchAdapter
import com.cosven.socialim.contact.adapter.SearchGroupAdapter
import com.dktlh.ktl.provider.router.RouterPath
import com.google.gson.reflect.TypeToken
import com.woniu.core.activities.BaseActivity
import com.woniu.core.api.Config
import com.woniu.core.bean.BaseListData
import com.woniu.core.bean.BaseReq
import com.woniu.core.bean.entity.*
import com.woniu.core.db.DbHelper
import com.woniu.core.utils.GsonUtil
import com.woniu.core.utils.HttpUtil
import com.woniu.core.xmpp.rxbus.event.MessageTempEvent
import com.zhouyou.http.model.HttpParams
import kotlinx.android.synthetic.main.contact_search_layout.*
import kotlinx.android.synthetic.main.fragament_contact.view.*
import kotlinx.android.synthetic.main.head_item.view.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import java.lang.reflect.Type

/**
 * 搜索页面历史记录
 */

@Route(path = RouterPath.ContactCenter.PATH_CONTACT_SEARCH)
class SearchActivity : BaseActivity() {
    private var mSearchAdapter: SearchAdapter? = null
    private var mSearchDatas: ArrayList<SearchUserData> = ArrayList()
    private var mSearchGroupAdapter: SearchGroupAdapter? = null
    private var mSearchGroupDatas: ArrayList<SearchGroupsData> = ArrayList()

    private var mHttpUtil: HttpUtil = HttpUtil(this)
    private var page: Int = 1
    private var type: String = ""

    private var mMessageListAdapter: MesageListAdapter? = null

    private var mMessageTempDatas: ArrayList<MessageTempEvent> = ArrayList()

    private var contactSortAdapter: ContactSortAdapter? = null
    private var mContactDatas: ArrayList<ContactEntity> = ArrayList()


    override fun setContentViewId(): Int {
        return R.layout.contact_search_layout
    }

    override fun initView() {
//        mRvSearchList.visibility = View.INVISIBLE
        type = intent?.getStringExtra("type") ?: "user"
        Log.e("yancy", "type=" + type)
        mRvSearchList.layoutManager = LinearLayoutManager(this@SearchActivity)
        if (type == "user") {
            mSearchAdapter = SearchAdapter(mSearchDatas)
            mRvSearchList.adapter = mSearchAdapter
        } else if (type == "searchContact") {
            contactSortAdapter = ContactSortAdapter().apply {
                showCheckBox = false
                setRes(context!!, mContactDatas)
            }
            mRvSearchList.adapter = contactSortAdapter
        } else if (type == "searchMessage") {
            mMessageListAdapter = MesageListAdapter(mMessageTempDatas)
            mRvSearchList.adapter = mMessageListAdapter
        } else {
            mSearchGroupAdapter = SearchGroupAdapter(mSearchGroupDatas)
            mRvSearchList.adapter = mSearchGroupAdapter
        }
    }

    override fun initData() {
        mTvCancel.setOnClickListener {
            finishs()
        }

        mSearchAdapter?.setOnItemClickListener { adapter, view, position ->
            if (mSearchDatas[position].friend_info == null) {
                startActivity<AddFriendAcitivty>(Pair("USER_ID", mSearchDatas[position].user_id))
            } else {
                ARouter
                    .getInstance()
                    .build(RouterPath.MineCenter.PATH_USER_INFO)
                    .withInt("USER_ID", mSearchDatas[position].user_id)
                    .navigation()
            }
        }
        mSearchGroupAdapter?.setOnItemClickListener { adapter, view, position ->
            ARouter
                .getInstance()
                .build(RouterPath.MesageCenter.PATH_GROUP_INFO)
                .withInt("GROUP_ID", mSearchGroupDatas[position].group_id)
                .navigation()
        }
        mEtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (type == "searchContact") {
                    searchContact(s.toString());
                } else if (type == "searchMessage") {
                    searchMessage(s.toString())
                }
            }
        })
        mEtSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                var content: String = mEtSearch.text.toString().trim()
                if (content.isNullOrBlank()) {
                    ToastUtils.showLong("请输入需要搜索的内容")
                } else {
                    mRvSearchList.visibility = View.VISIBLE
                    KeyboardUtils.hideSoftInput(mEtSearch)
                    page = 1
                    searchData(false)
                }

                true;
            }
            false
        }

        mEtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (type == "user") {
                    if (mSearchDatas.size > 0) {
                        mSearchDatas.clear()
                        mSearchAdapter?.notifyDataSetChanged()
                    }
                } else {
                    if (mSearchGroupDatas.size > 0) {
                        mSearchGroupDatas.clear()
                        mSearchGroupAdapter?.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    private fun searchContact(toString: String) {
        mHttpUtil.setOnRequestCallBack { code, data ->
            if (code == -1) {
                ToastUtils.showLong(data)
                return@setOnRequestCallBack
            }
            var type: Type = object : TypeToken<BaseReq<BaseListData<ContactEntity>>>() {}.type
            var mReq: BaseReq<BaseListData<ContactEntity>> = GsonUtil.getGson().fromJson(data, type)
            if (TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {

                if (!mContactDatas.isEmpty()) {
                    mContactDatas.clear()
                }
                mReq.result?.data?.let {
                    for (contactEntity in it) {
                        if (contactEntity.friend_remark?.startsWith(toString)!!) {
                            mContactDatas.add(contactEntity)
                            contactSortAdapter?.updateList(mContactDatas)
                            contactSortAdapter?.setOnItemClickListener(object : ContactSortAdapter.OnItemClickListener {
                                override fun onItemClick(view: View, position: Int) {
                                    ARouter
                                        .getInstance()
                                        .build(RouterPath.MineCenter.PATH_USER_INFO)
                                        .withInt("USER_ID", mContactDatas[position].friend_user_id)
                                        .navigation()
                                }
                            })

                        }
                    }
                }
            } else {
                ToastUtils.showLong(mReq.msg)
            }
        }.getParms(httpParams, Config.API.friend_list)
    }

    private fun searchMessage(nikename: String) {
        if (!mMessageTempDatas.isEmpty()) {
            mMessageTempDatas.clear()
        }
        mMessageTempDatas.addAll(DbHelper.getMessageTempByNickname(nikename))
        Log.e("yancy", "searchMessage=" + mMessageTempDatas.toString())
        mMessageListAdapter?.notifyDataSetChanged()
        mMessageListAdapter?.let {
            it.setOnItemChildClickListener { adapter, view, position ->
                when (view.id) {
                    R.id.mRlListRoot -> {//跳转得到聊天界面
                        var mTemp: MessageTempEvent = mMessageTempDatas[position]
                        startActivity<ChatActivity>(
                            Pair("USER_ID", mTemp.chatUserID),
                            Pair(
                                "USER_AVATAR", if (!mTemp.avatar.isNullOrBlank()) {
                                    mTemp.avatar
                                } else {
                                    ""
                                }
                            ),
                            Pair(
                                "USER_NICKNAME", if (!mTemp.nickname.isNullOrBlank()) {
                                    mTemp.nickname
                                } else {
                                    ""
                                }
                            ),
                            Pair("CHAT_TYPE", mTemp.chatType)
                        )
                    }
                }
            }
        }
    }

    private fun searchData(isRefresh: Boolean) {
        if (!isRefresh) {
            showProgressDialog(getString(R.string.waiting))
        }

        var mParams: HttpParams = httpParams
        mParams.put("page", page.toString())
        mParams.put("type", type)
        mParams.put("keywords", mEtSearch.text.toString().trim())

        mHttpUtil.setOnRequestCallBack { code, data ->
            if (!isRefresh) {
                dismissProgressDialog()
            } else {
                mRefreshLayout.finishLoadmore()
            }

            if (code == -1) {
                ToastUtils.showLong(data)
                return@setOnRequestCallBack
            }

            if (type == "user") {
                var type: Type = object : TypeToken<BaseReq<SearchUserEntity>>() {}.type
                var mReq: BaseReq<SearchUserEntity> = GsonUtil.getGson().fromJson(data, type)

                if (!TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {
                    ToastUtils.showLong(mReq.msg)
                    if (page > 1) page--
                } else {
//                mRefreshLayout.isEnableLoadmore = !mReq.result.next_page_url.isNullOrBlank()
                    mReq.result.users?.data?.let {
                        if (page == 1) {
                            mSearchDatas.clear()
                        }
                        mSearchDatas.addAll(it)
                        mSearchAdapter?.notifyDataSetChanged()
                    }
                }

            } else {
                var type: Type = object : TypeToken<BaseReq<SearchGroupEntity>>() {}.type
                var mReq: BaseReq<SearchGroupEntity> = GsonUtil.getGson().fromJson(data, type)

                if (!TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {
                    ToastUtils.showLong(mReq.msg)
                    if (page > 1) page--
                } else {
//                mRefreshLayout.isEnableLoadmore = !mReq.result.next_page_url.isNullOrBlank()
                    mReq.result.groups?.data?.let {
                        if (page == 1) {
                            mSearchGroupDatas.clear()
                        }
                        mSearchGroupDatas.addAll(it)
                        mSearchGroupAdapter?.notifyDataSetChanged()
                    }
                }

            }

        }.postParms(mParams, Config.API.user_search)
    }
}