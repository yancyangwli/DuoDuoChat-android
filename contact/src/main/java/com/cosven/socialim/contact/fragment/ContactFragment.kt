package com.cosven.socialim.contact.fragment

import android.support.v7.widget.LinearLayoutManager
import android.text.LoginFilter
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ToastUtils
import com.cosven.socialim.contact.R
import com.cosven.socialim.contact.activity.*
import com.cosven.socialim.contact.adapter.ContactSortAdapter
import com.cosven.socialim.contact.view.PinyinComparator
import com.cosven.socialim.contact.view.PinyinUtils
import com.cosven.socialim.contact.view.SideBar
import com.cosven.socialim.contact.view.SortModel
import com.dktlh.ktl.provider.router.RouterPath
import com.google.gson.reflect.TypeToken
import com.qmuiteam.qmui.widget.QMUITopBarLayout
import com.woniu.core.activities.BaseActivity
import com.woniu.core.api.Config
import com.woniu.core.bean.BaseListData
import com.woniu.core.bean.BaseReq
import com.woniu.core.bean.RxBusEvent
import com.woniu.core.bean.entity.ContactEntity
import com.woniu.core.fragment.BaseFragment
import com.woniu.core.utils.GsonUtil
import com.woniu.core.utils.HttpUtil
import com.woniu.core.xmpp.rxbus.RxBus
import com.zhuanxu.eclipse.aop.annotation.SingleClick
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.contact_titile_layout.view.*
import kotlinx.android.synthetic.main.fragament_contact.view.*
import kotlinx.android.synthetic.main.head_item.view.*
import org.jetbrains.anko.support.v4.startActivity
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList

// 联系人
class ContactFragment : BaseFragment(), SideBar.OnChooseLetterChangedListener {
    private var manger: LinearLayoutManager? = null
    private var adapter: ContactSortAdapter? = null
    private var sourceDateList: List<SortModel>? = null

    private var mContactDatas: ArrayList<ContactEntity> = ArrayList()

    private var mHeaderSearchView: View? = null

    private var showCheckbox: Boolean = false
    /**
     * 根据拼音来排列RecyclerView里面的数据类
     */
    private var pinyinComparator: PinyinComparator? = null
    private var flag: String = ""

    private var mEventDisposable: Disposable? = null
    private var mHttpUtil: HttpUtil = HttpUtil(context)

    override fun onNoChooseLetter() {
    }

    override fun onChooseLetter(s: String?) {
        val i = adapter!!.getPositionForSection(s!!.get(0).toInt())
        if (i == -1) {
            return
        }
        manger!!.scrollToPositionWithOffset(i, 0)
    }

    override fun setView(): Int {
        return R.layout.fragament_contact
    }

    override fun initView() {
        arguments?.let {
            showCheckbox = it.getBoolean("SHOW_CHECKBOX")
            flag = it.getString("FLAG") ?: ""
        }

        pinyinComparator = PinyinComparator()
        inflate?.run {
            qmui_bar?.run {
                setBackgroundResource(R.drawable.base_title_shell)
                if (showCheckbox) {
                    setTitles(this, if (flag == "invite") "邀请入群" else "发送到", false)
                    addLeftBackImageButton().apply {
                        setImageResource(R.mipmap.ic_arrow_left_white)
                        setOnClickListener {
                            (activity as BaseActivity).finishs()
                        }
                    }
                } else {
                    setTitles(this, "联系人", false)
                }
                if (flag != "invite") {
                    addRightImageButton(R.mipmap.icon_right_constace, R.id.empty_button)?.run {
                        setOnClickListener {
                            startActivity<AddActivity>()
                        }
                    }
                }
            }
            hintSideBar.setOnChooseLetterChangedListener(this@ContactFragment)
            manger = LinearLayoutManager(context)
            rv_list.layoutManager = manger

            adapter = ContactSortAdapter().apply {
                showCheckBox = showCheckbox
                setRes(context!!, mContactDatas)
                if (flag != "invite") {
                    mHeaderSearchView = LayoutInflater.from(context).inflate(R.layout.head_item, inflate.rv_list, false)
                        .apply {
                            if (showCheckbox) {
                                mLlNewFriend.visibility = View.GONE
                                mLlBlackList.visibility = View.GONE
                            }
                            setHeaderView(this@apply)
                        }
                }

                rv_list.adapter = this
                setOnItemClickListener(object : ContactSortAdapter.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        ARouter
                            .getInstance()
                            .build(RouterPath.MineCenter.PATH_USER_INFO)
                            .withInt("USER_ID", mContactDatas[position - if (flag == "invite") 0 else 1].friend_user_id)
                            .navigation()
                    }
                })
            }
            initEvent()
            receiveEventMessage()
            loadContactData()
        }

        // 根据a-z进行排序源数据
//        sourceDateList = filledData(resources.getStringArray(R.array.date))
//        Collections.sort(sourceDateList, pinyinComparator)
    }

    private fun backEvent() {
        val mBackView = inflate.qmui_bar.addLeftBackImageButton()
        mBackView.setImageResource(R.mipmap.ic_arrow_left_white)
        mBackView.setOnClickListener {
            (activity as BaseActivity).finishs()
        }
    }

    private fun initEvent() {
        mHeaderSearchView?.let { hsv ->
            hsv.search_root.setOnClickListener {
                startActivity<SearchActivity>("type" to "searchContact")
            }

            //新朋友
            hsv.mLlNewFriend.setOnClickListener {
                startActivity<NewFriendActivity>()
            }
            //群聊
            hsv.mLlGroupChat.setOnClickListener {
                if (showCheckbox) {
                    startActivity<GroupActivity>(Pair("SHOW_CHECKBOX", true))
                } else {
                    startActivity<GroupActivity>()
                }
            }
            //黑名单
            hsv.mLlBlackList.setOnClickListener {
                startActivity<BlackActivity>()
            }
        }

//        adapter!!.setOnItemClickListener(object : ContactSortAdapter.OnItemClickListener {
//            override fun onItemClick(view: View, position: Int) {
//                ARouter
//                    .getInstance()
//                    .build(RouterPath.MineCenter.PATH_USER_INFO)
//                    .withInt("USER_ID", mContactDatas[position - 1].friend_user_id)
//                    .navigation()
//            }
//        })
    }

    /**
     * 接收rxbus消息通知
     */
    private fun receiveEventMessage() {
        mEventDisposable = RxBus.getInstance()
            .toObserverable(RxBusEvent::class.java)
            .subscribe {
                when {
                    it.updateFriendList -> loadContactData()
                    it.deleteFriendWithId != 0 -> {
                        showData(mContactDatas.filterNot { f -> f.friend_user_id == it.deleteFriendWithId })
                    }
                }
            }
    }


    /**
     * 为RecyclerView填充数据
     *
     * @param date
     * @return
     */
    private fun filledData(date: Array<String>): List<SortModel> {
        val mSortList = ArrayList<SortModel>()

        for (i in date.indices) {
            val sortModel = SortModel()
            sortModel.name = (date[i])
            //汉字转换成拼音
            val pinyin = PinyinUtils.getPingYin(date[i])
            val sortString = pinyin.substring(0, 1).toUpperCase()

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]".toRegex())) {
                sortModel.letters = (sortString.toUpperCase())
            } else {
                sortModel.letters = ("#")
            }

            mSortList.add(sortModel)
        }
        return mSortList

    }

    /**
     * 为RecyclerView填充数据
     *
     * @param date
     * @return
     */
//    private fun filledContactData(data:List<ContactEntity>): List<ContactEntity> {
//
//        for (d in data) {
//            //汉字转换成拼音
//            val pinyin = PinyinUtils.getPingYin(d.friend_remark)
//            val sortString = pinyin.substring(0, 1).toUpperCase()
//
//            // 正则表达式，判断首字母是否是英文字母
//            if (sortString.matches("[A-Z]".toRegex())) {
//                d.letters = (sortString.toUpperCase())
//            } else {
//                d.letters = ("#")
//            }
//        }
//        return data
//
//    }


    //获得状态栏的高度
    fun getStatusBarHeight(): Int {
        var result = 0
        val resId = context!!.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resId > 0) {
            result = context!!.resources.getDimensionPixelOffset(resId)
        }
        return result
    }


    private fun loadContactData() {
        mHttpUtil.setOnRequestCallBack { code, data ->
            Log.i("FAN", "联系人数据---> $data")
            if (code == -1) {
                ToastUtils.showLong(data)
                return@setOnRequestCallBack
            }

            var type: Type = object : TypeToken<BaseReq<BaseListData<ContactEntity>>>() {}.type
            var mReq: BaseReq<BaseListData<ContactEntity>> = GsonUtil.getGson().fromJson(data, type)
            if (TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {
//                mContactDatas.clear()
                mReq.result?.data?.let {
                    //                   mContactDatas.addAll(filledContactData(it))
//                   Collections.sort(mContactDatas, pinyinComparator)
//                   adapter?.updateList(mContactDatas)
                    showData(it)
                }
            } else {
                ToastUtils.showLong(mReq.msg)
            }
        }.getParms(httpParams, Config.API.friend_list)
    }

    private fun showData(datas: List<ContactEntity>) {

        Observable.create(ObservableOnSubscribe<List<ContactEntity>> {
            mContactDatas.clear()
//            mContactDatas.addAll(filledContactData(datas))
            mContactDatas.addAll(datas)
            Collections.sort(mContactDatas, pinyinComparator)
            it.onNext(mContactDatas)
            it.onComplete()
        })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(object : Observer<List<ContactEntity>> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: List<ContactEntity>) {
                    activity?.runOnUiThread {
                        adapter!!.updateList(t)
                    }
                }

                override fun onError(e: Throwable) {
                }
            })
    }

    fun getSelectedList(): MutableList<ContactEntity>? {
        return adapter?.mSelectedList ?: null
    }

    override fun onDestroy() {
        super.onDestroy()
        mEventDisposable?.dispose()
        mEventDisposable = null
    }

}