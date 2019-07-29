package com.cosven.socialim.contact.activity

import android.annotation.SuppressLint
import android.os.SystemClock
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ToastUtils
import com.cosven.socialim.contact.R
import com.dktlh.ktl.provider.router.RouterPath
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.luck.picture.lib.entity.LocalMedia
import com.woniu.core.activities.BaseActivity
import com.woniu.core.api.Config
import com.woniu.core.bean.BaseReq
import com.woniu.core.bean.ResultInfo
import com.woniu.core.bean.RxBusEvent
import com.woniu.core.bean.entity.ApplyFriendEntity
import com.woniu.core.db.DbHelper
import com.woniu.core.manage.UserInfoManage
import com.woniu.core.utils.GsonUtil
import com.woniu.core.utils.HttpUtil
import com.woniu.core.utils.ImageUtil
import com.woniu.core.xmpp.rxbus.RxBus
import com.woniu.core.xmpp.rxbus.event.BaseEvent
import com.woniu.core.xmpp.rxbus.event.ChatMessageEvent
import com.woniu.core.xmpp.smack.SmackManager
import com.zhouyou.http.model.HttpParams
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.contact_friend_req_layout.*
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.chat.Chat
import org.jivesoftware.smackx.muc.MultiUserChat
import kotlin.Function as Function1001

@Route(path = RouterPath.ContactCenter.PATH_ADD_FRIEND_REQUESTS)
class FriendRequestsActivity : BaseActivity() {

    private var mApplyFriendEntity: ApplyFriendEntity? = null
    private var mChatMessage: ChatMessageEvent? = null
    private var mHttpUtil: HttpUtil? = null

    override fun setContentViewId(): Int {
        return R.layout.contact_friend_req_layout
    }

    override fun initView() {
        mApplyFriendEntity = intent.getSerializableExtra("FRIEND_INFO") as? ApplyFriendEntity?
        mChatMessage = intent.getParcelableExtra("CHAT_MESSAGE") as? ChatMessageEvent?
        when {
            mApplyFriendEntity?.apply_type == ApplyFriendEntity.FRIEND -> {
                Log.e("yancy", "好友申请：$mApplyFriendEntity")
                mToolbarTitle.text = "好友申请"
                mTvInfo.visibility = View.VISIBLE
            }
            mChatMessage != null -> {
                Log.e("yancy", "加群邀请 initView：" + mChatMessage.toString())
                mToolbarTitle.text = "加群邀请"
                showInviteInfo()
            }
            else -> {
                mToolbarTitle.text = "加群申请"
            }
        }
        showInfo()
        initEvent()
    }

    /**
     * 好友邀请加入群聊信息展示
     */
    private fun showInviteInfo() {
        mChatMessage?.run {
            mTvNickname.text = invitationGroupName
            mTvArea.text = "好友${nickname}邀请你加入"
            ImageUtil.loadOriginalImage(this@FriendRequestsActivity, invitationGroupAvatar, mIvAvatar)
            mRemarksLl.visibility = View.GONE
            mTvNumber.text = toUserID
        }
    }

    override fun initStatusBar() {
        ImmersionBar.with(this)
            .titleBar(R.id.toolbar)
            .statusBarDarkFont(true)
            .keyboardEnable(true)  //解决软键盘与底部输入框冲突问题
            .init()
    }

    override fun initData() {
    }

    private fun initEvent() {
        mToolbarBack.setOnClickListener(this)
        mRlUserInfoRoot.setOnClickListener(this)
        mBtnAgree.setOnClickListener(this)
        mBtnRefuse.setOnClickListener(this)

        mEtNote.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                mEtNote.isCursorVisible = true
            }
            false
        }
    }

    private fun showInfo() {
        mApplyFriendEntity?.let {
            Log.e("yancy", "it=" + it.toString())
            if (mApplyFriendEntity?.apply_type == ApplyFriendEntity.FRIEND) {
                mTvNickname.text = if (!it.object_nick.isNullOrBlank()) {
                    it.object_nick
                } else {
                    ""
                }
                ImageUtil.loadOriginalImage(this, it.object_avatar, mIvAvatar)
            } else {
                mTvNickname.text = if (!it.apply_user_nick.isNullOrBlank()) {
                    it.apply_user_nick
                } else {
                    ""
                }
                ImageUtil.loadOriginalImage(this, it.apply_user_avatar, mIvAvatar)
            }

            if (it.object_gender == 1) {
                setInfo(R.mipmap.ic_male_white, resources.getColor(R.color.color_sex_male))
            } else if (it.object_gender == 2) {
                setInfo(R.mipmap.ic_female_white, resources.getColor(R.color.color_sex_female))
            }

            it.object_address?.let {
                mTvArea.text = it
            }

            it.remarks?.let {
                mEtNote.setText(it)
            }

            mTvNumber.text = it.object_id.toString()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.mToolbarBack -> finishs()
            R.id.mRlUserInfoRoot ->
                mApplyFriendEntity?.let {
                    ARouter
                        .getInstance()
                        .build(RouterPath.MineCenter.PATH_USER_INFO)
                        .withInt("USER_ID", it.object_id)
                        .navigation()
                }
            R.id.mBtnAgree -> agreeOrRefuseOperation(true)
            R.id.mBtnRefuse -> agreeOrRefuseOperation(false)
        }
    }

    private fun setInfo(sexImg: Int, bgColor: Int) {
        mTvInfo.setCompoundDrawablesWithIntrinsicBounds(
            resources.getDrawable(sexImg),
            null,
            null,
            null
        )
        mTvInfo.setBgColor(bgColor)
    }

    private fun agreeOrRefuseOperation(isAgree: Boolean) {
        var remarks = ""
        if (isAgree) {
            remarks = mEtNote.text.toString().trim()
            if (remarks.isNullOrBlank() && mApplyFriendEntity?.apply_type == ApplyFriendEntity.FRIEND) {
                ToastUtils.showLong("请给你的朋友一个备注吧")
                return
            }
        }
        if (mHttpUtil == null) {
            mHttpUtil = HttpUtil(this)
        }
        showProgressDialog(getString(R.string.waiting))
        var mParams: HttpParams = httpParams

        if (mApplyFriendEntity?.apply_type == ApplyFriendEntity.FRIEND) {
            mParams.put("apply_id", mApplyFriendEntity?.apply_id.toString())
            if (isAgree) {
                mParams.put("apply_id", mApplyFriendEntity?.apply_id.toString())
                mParams.put("type", "approve")
                mParams.put("friend_remark", remarks)
            } else {
                mParams.put("type", "reject")
            }
        } else {
            if (isAgree) {
                mParams.put("group_id", mChatMessage?.messageContent)
            } else {
                //finish() 拒绝入群邀请
            }
        }


        mHttpUtil!!.setOnRequestCallBack { code, data ->
            Log.e("yancy", "同意加好友返回---> $data")
            Log.e("yancy", "同意加好友返回码 code=" + code)
            dismissProgressDialog()
            if (code == -1) {
                ToastUtils.showLong(data)
                finishs()
                return@setOnRequestCallBack
            }
            var mReq: BaseReq<*> = GsonUtil.getGson().fromJson(data, BaseReq::class.java)
            ToastUtils.showLong(mReq.msg);

            if (mReq.status == Config.Constant.SUCCESS && isAgree) {
                //
                //发送消息
                sendMessage("")
            }

        }.postParms(
            mParams, if (mApplyFriendEntity?.apply_type == ApplyFriendEntity.FRIEND)
                Config.API.friend_auditing else Config.API.join_group
        )
    }

    //发送消息
    private fun sendMessage(message: String) {
        mSendDisposable = Observable.just(message)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .flatMap(Function<String, ObservableSource<Boolean>> {
                try {
                    var mChatEvent = ChatMessageEvent()
                    if (mApplyFriendEntity == null) {
                        Log.e("yancy", "发送群聊==》");
                        mChatEvent = this@FriendRequestsActivity.mChatMessage!!;
                        mChatEvent.messageType = 1
                        mGroupChat = SmackManager.getInstance()
                            .getMultiChat(SmackManager.getInstance().getChatJid(mChatEvent.chatUserID))
                        mGroupChat?.sendMessage(GsonUtil.getGson().toJson(mChatEvent))
                    } else {
                        RxBus.getInstance().post(RxBusEvent().apply { updateFriendList = true })
                        Log.e("yancy", "发送单聊==》");

                        val mUserInfoEntity = UserInfoManage.getInstance.mUserInfoEntity!!

                        mChatEvent.messageType =
                            if (mApplyFriendEntity?.apply_type == ApplyFriendEntity.FRIEND) 1 else 6
                        mChatEvent.messageContent = "你好，我是" + mUserInfoEntity?.nickname
                        mChatEvent.avatar = mUserInfoEntity?.avatar
                        mChatEvent.nickname = mUserInfoEntity?.nickname
                        mChatEvent.chatUserID = mApplyFriendEntity?.apply_user_id.toString()
                        mChatEvent.fromUserID = mApplyFriendEntity?.to_user_id.toString()
                        mChatEvent.toUserID = mApplyFriendEntity?.apply_user_id.toString()
                        // mChatEvent.id = java.lang.Long.valueOf(mApplyFriendEntity?.apply_user_id.toString())
                        mChatEvent.createTime = SystemClock.currentThreadTimeMillis();
                        mChat = SmackManager.getInstance()
                            .createChat(SmackManager.getInstance().getChatJid(mChatEvent.chatUserID))
                        Log.e("yancy", "发送数据==》" + mChatEvent);
                        mChat?.sendMessage(GsonUtil.getGson().toJson(mChatEvent))
                        /*  mChatEvent.avatar = mApplyFriendEntity?.apply_user_avatar
                          mChatEvent.nickname = mApplyFriendEntity?.object_nick*/
                    }

                    var rowId: Long = DbHelper.insertChatMessage(mChatEvent)//插入数据库
                 /*   mChatEvent.id = rowId//设置当前该信息在数据库的id
                    RxBus.getInstance().post(mChatEvent)//通知发送消息，接收位置有（MessageFragment）*/
                    //finish()
                } catch (e: SmackException.NotConnectedException) {
                    return@Function Observable.just(false)
                }
                Observable.just(true)
            })
            .subscribe {
                if (!it) {
                    ToastUtils.showLong("消息发送失败")
                }
            }
    }

    private var mSendDisposable: Disposable? = null

    override fun onDestroy() {
        super.onDestroy()
        mSendDisposable?.dispose();
    }

    private var mChat: Chat? = null

    private var mGroupChat: MultiUserChat? = null

}