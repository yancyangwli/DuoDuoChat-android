package com.cosven.socialim.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import cn.jiguang.imui.view.RoundTextView
import com.cosven.socialim.contact.activity.AddActivity
import com.cosven.socialim.contact.activity.QRScanActivity
import com.cosven.socialim.contact.activity.SearchActivity
import com.cosven.message.activity.*
import com.cosven.message.adapter.MesageListAdapter
import com.cosven.socialim.R
import com.dxjia.library.ImageTextButton
import com.scwang.smartrefresh.layout.util.DensityUtil
import com.woniu.core.db.DbHelper
import com.woniu.core.fragment.BaseFragment
import com.woniu.core.manage.UserInfoManage
import com.woniu.core.xmpp.rxbus.RxBus
import com.woniu.core.xmpp.rxbus.event.BaseEvent
import com.woniu.core.xmpp.rxbus.event.BusEvent
import com.woniu.core.xmpp.rxbus.event.ChatMessageEvent
import com.woniu.core.xmpp.rxbus.event.MessageTempEvent
import com.zyyoona7.popup.EasyPopup
import com.zyyoona7.popup.XGravity
import com.zyyoona7.popup.YGravity
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragament_msg.view.*
import kotlinx.android.synthetic.main.titile_layout.view.*
import org.jetbrains.anko.support.v4.startActivity
import java.util.*
import kotlin.collections.ArrayList

/**
 * 消息
 */
class MessageFragment : BaseFragment() {

    private var mEasyPopup: EasyPopup? = null

    private lateinit var mHeadSearchView: View

    private var mMessageListAdapter: MesageListAdapter? = null

    private var mEventDisposable: Disposable? = null
    private var mQueryDisposable: Disposable? = null
    private var mDeleteQueryDisposable: Disposable? = null

    private var mMessageTempDatas: ArrayList<MessageTempEvent> = ArrayList()

    override fun setView(): Int {
        return R.layout.fragament_msg
    }

    override fun initView() {
//        DbHelper.deleteMessageTempEventAll()
//        DbHelper.deleteChatMessageAll()
        inflate.qmui_bar?.let {
            it.setBackgroundResource(R.drawable.base_title_shell)
            var mTvTitle: TextView = it.setTitle("消息")
            mTvTitle.setTextColor(Color.WHITE)
        }

        inflate.qmui_bar.addRightImageButton(R.mipmap.ic_add_white, R.id.empty_button)?.setOnClickListener { it ->
            showPop(it)
        }
    }

    override fun initData() {
        inflate.mRvMessageList.layoutManager = LinearLayoutManager(context)

        mMessageListAdapter = MesageListAdapter(mMessageTempDatas)

        mHeadSearchView =
            LayoutInflater.from(context).inflate(R.layout.layout_search_head, inflate.mRvMessageList, false);

        mHeadSearchView?.let {
            mMessageListAdapter!!.setHeaderView(mHeadSearchView)
        }

        inflate.mRvMessageList.adapter = mMessageListAdapter
        initEvent()

        queryTempsDatas()
    }

    /**
     * 查询临时会话列表数据
     */
    @SuppressLint("CheckResult")
    private fun queryTempsDatas() {
        mQueryDisposable = Observable.create<List<MessageTempEvent>> {
            val mList: MutableList<MessageTempEvent> =
                DbHelper.getMessageTempListByUserId(UserInfoManage.getInstance.mUserInfoEntity?.user_id!!)
            Log.e("yancy", "查询到的数据" + mList.size)

            Log.e("yancy", "查询到的数据" + mList.toString())
            mList?.let { list ->
                list.reverse()
                it.onNext(mList)
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                //                                Log.i("FAN", "查询--->  $it")
                if (it != null && it.isNotEmpty()) {
                    mMessageTempDatas.clear()
                    mMessageTempDatas.addAll(it)
                    mMessageListAdapter?.notifyDataSetChanged()
                }
            }
    }

    @SuppressLint("CheckResult")
    private fun initEvent() {
        mEventDisposable = RxBus.getInstance()
            .toObserverable(ChatMessageEvent::class.java)
            .subscribe {
                if (it.chatUserID == null) {
                    val mList: MutableList<MessageTempEvent> =
                        DbHelper.getMessageTempListByUserId(UserInfoManage.getInstance.mUserInfoEntity?.user_id!!)
                    if (mList.isEmpty()) {
                        mMessageTempDatas.clear()
                    } else {
                        mList.reverse()
                        mMessageTempDatas.addAll(mList)
                    }
                    mMessageListAdapter?.notifyDataSetChanged()
                } else
                    addTempData(it)
            }

        //消息item点击事件
        mMessageListAdapter?.let {
            it.setOnItemChildClickListener { adapter, view, position ->
                when (view.id) {
                    R.id.mBtnDelete -> deleteTemp(mMessageTempDatas[position].id, position)//删除会话数据
                    R.id.mRlListRoot -> {//跳转得到聊天界面
                        var mTemp: MessageTempEvent = mMessageTempDatas[position]
//                        if (mTemp.chatType == BaseEvent.CHAT_TYPE.PERSONAL.typeValue
//                            || mTemp.chatType == BaseEvent.CHAT_TYPE.GROUP.typeValue
//                        ) {
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
//                        }
                        //清除消息红点，需更新数据库
                        if (mTemp.unReadNumber > 0) {
                            mTemp.unReadNumber = 0
                            DbHelper.updateMessageTempEvent(mTemp)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }

        mHeadSearchView?.let {
            it.setOnClickListener {
                startActivity<SearchActivity>("type" to "searchMessage")
            }
        }
    }

    /**
     * 删除会话数据
     */
    @SuppressLint("CheckResult")
    private fun deleteTemp(rowId: Long, position: Int) {
        mDeleteQueryDisposable = Observable.just(rowId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap(Function<Long, ObservableSource<Boolean>> {
                var isSucess: Boolean = DbHelper.deleteMessageTempEventByRowId(it)
                if (isSucess) {
                    mMessageTempDatas.removeAt(position)
                }
                return@Function Observable.just(isSucess)
            })
            .subscribe {
                if (it) {
                    mMessageListAdapter?.notifyItemRemoved(position + 1)
                }
            }
    }

    /**
     * 插入会话数据
     */
    @SuppressLint("CheckResult")
    private fun addTempData(message: ChatMessageEvent) {
        Observable.just(mMessageTempDatas)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap(Function<ArrayList<MessageTempEvent>, ObservableSource<BusEvent<MessageTempEvent>>> {
                if (it.isEmpty()) {
                    return@Function insertTempData(message)
                } else {
                    for (index: Int in it.indices) {
                        val mItemEvent: MessageTempEvent = it[index]
                        if (TextUtils.equals(mItemEvent.chatUserID, message.chatUserID)) {
                            val mTemp: MessageTempEvent = setTempValue(message, mItemEvent, true)
                            DbHelper.updateMessageTempEvent(mTemp)
                            return@Function Observable.just(BusEvent<MessageTempEvent>(index + 1, mTemp))
                        }
                    }
                    return@Function insertTempData(message)
                }
            })
            .subscribe {
                if (it.position == -1) {
                    mMessageTempDatas.add(0, it.data)
                    mMessageListAdapter?.notifyItemInserted(1)
                } else {
                    mMessageListAdapter?.notifyItemChanged(it.position)
                }
            }
    }

    /**
     * 插入临时会话数据的观察者模式
     */
    private fun insertTempData(message: ChatMessageEvent): ObservableSource<BusEvent<MessageTempEvent>> {
        var mTemp: MessageTempEvent = setTempValue(message, MessageTempEvent(), false)
        var rowId: Long = DbHelper.insertMessageTempEvent(mTemp)
        mTemp.id = rowId
        return Observable.just(BusEvent<MessageTempEvent>(-1, mTemp))
    }

    /**
     * 设置临时会话数据值
     */
    private fun setTempValue(message: ChatMessageEvent, temp: MessageTempEvent, isContain: Boolean): MessageTempEvent {
        temp.chatUserID = message.chatUserID
        temp.createTime = message.createTime
        var count: Int = temp.unReadNumber
        temp.unReadNumber = if (isContain) {
            ++count
        } else {
            1
        }
        temp.avatar = when {
            message.chatType == BaseEvent.CHAT_TYPE.PERSONAL.typeValue -> message.avatar
            message.chatType == BaseEvent.CHAT_TYPE.GROUP.typeValue -> message.groupAvatar
            else -> ""
        }
        temp.chatType = message.chatType
        temp.messageContent = message.messageContent
        temp.fromUserID = message.fromUserID
        temp.toUserID = message.toUserID
        temp.messageType = message.messageType
        temp.nickname = when {
            message.chatType == BaseEvent.CHAT_TYPE.PERSONAL.typeValue -> message.nickname
            message.chatType == BaseEvent.CHAT_TYPE.GROUP.typeValue -> message.groupName
            else -> ""
        }
        return temp
    }

    /**
     * 显示右上角弹框
     */
    private fun showPop(v: View) {
        if (mEasyPopup == null) {
            mEasyPopup = EasyPopup.create()
            mEasyPopup!!.setContentView(context, R.layout.message_toolbar_add)
                .setAnimationStyle(R.style.message_PopAnim)
                //是否允许点击PopupWindow之外的地方消失
                .setFocusAndOutsideEnable(true)
                .setBackgroundDimEnable(true)
                .setDimValue(0f)
                .apply()

            mEasyPopup?.let { ep ->
                //创建群聊
                ep.findViewById<ImageTextButton>(R.id.btn_group_chat).setOnClickListener {
                    ep.dismiss()
                    startActivity<CreateGroupChatActivity>()
                }
                //加好友/群
                ep.findViewById<ImageTextButton>(R.id.btn_add_friend).setOnClickListener {
                    ep.dismiss()
                    startActivity<AddActivity>()
                }
                //扫一扫
                ep.findViewById<ImageTextButton>(R.id.btn_scan).setOnClickListener {
                    ep.dismiss()
                    startActivity<QRScanActivity>()
                }
            }
        }
        mEasyPopup!!.showAtAnchorView(v, YGravity.BELOW, XGravity.LEFT, DensityUtil.dp2px(50F), 0);
    }

    override fun onDestroy() {
        super.onDestroy()
        mQueryDisposable?.dispose()
        mQueryDisposable = null

        mDeleteQueryDisposable?.dispose()
        mDeleteQueryDisposable = null
    }
}