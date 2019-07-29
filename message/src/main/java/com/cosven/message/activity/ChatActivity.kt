package com.cosven.message.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils

import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import cn.jiguang.imui.chatinput.listener.OnMenuClickListener
import cn.jiguang.imui.chatinput.listener.RecordVoiceListener
import cn.jiguang.imui.chatinput.model.FileItem
import cn.jiguang.imui.messages.ptr.PtrHandler
import cn.jiguang.imui.messages.ptr.PullToRefreshLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import com.dktlh.ktl.provider.router.RouterPath

import com.cosven.message.R
import com.cosven.message.adapter.ChatAdapter
import com.cosven.message.bean.OpenRedPacketBean
import com.cosven.message.bean.RedPacketBean
import com.cosven.message.dialog.ActionPopWindow
import com.cosven.message.dialog.RedPackDialog
import com.cosven.message.impl.OnActionPopClickedListener
import com.cosven.message.impl.OnChatMessageListClickedListener
import com.cosven.message.view.ChatView
import com.google.gson.reflect.TypeToken
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.tools.PictureFileUtils
import com.orhanobut.logger.Logger
import com.woniu.core.api.Config
import com.woniu.core.bean.BaseReq
import com.woniu.core.bean.GroupInfoEntity
import com.woniu.core.bean.entity.*
import com.woniu.core.db.DbHelper
import com.woniu.core.listeners.OnRedPacketClickListener
import com.woniu.core.manage.UserInfoManage
import com.woniu.core.utils.FileUtil
import com.woniu.core.utils.GsonUtil
import com.woniu.core.utils.HttpUtil
import com.woniu.core.utils.PictureUtil
import com.woniu.core.xmpp.rxbus.RxBus
import com.woniu.core.xmpp.rxbus.event.BaseEvent
import com.woniu.core.xmpp.rxbus.event.ChatMessageEvent
import com.woniu.core.xmpp.smack.SmackManager
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.message_chat_layout.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smack.chat.Chat
import org.jivesoftware.smackx.muc.MultiUserChat
import java.io.File
import java.lang.reflect.Type
import kotlin.collections.ArrayList

/**
 * 聊天室..
 */
@Route(path = RouterPath.MesageCenter.PATH_CHAT_LIST)
class ChatActivity : BaseMsgActivity(), View.OnTouchListener {
    companion object {
        const val SEND_RED_PACKAGE = 10001//发送红包请求码
    }

    private var offset = 0
    private val SELECT_IMAGE_CODE: Int = 0x9001//选择图片的请求码
    private val SELECT_IMAGE_FROM_CAMERA_CODE: Int = 0x9002//调用相机摄取图片的请求码

    private lateinit var mImm: InputMethodManager
    private lateinit var mWindow: Window

    private lateinit var mLinearLayoutManager: LinearLayoutManager

    private lateinit var mChatAdapter: ChatAdapter
    private lateinit var mMediaPlayer: MediaPlayer
    private lateinit var mUserAccount: String
    private var chatType: Int = BaseEvent.CHAT_TYPE.PERSONAL.typeValue

    private var mChat: Chat? = null
    private var mGroupChat: MultiUserChat? = null
    private var mChatDatas: ArrayList<ChatMessageEvent> = ArrayList()


    private var mHandler: Handler = Handler()

    private var mQueryDisposable: Disposable? = null

    private var mPictureUtil: PictureUtil? = null
    private var mHttpUtil: HttpUtil? = null

    private var mChatDisposable: Disposable? = null
    private var mMessageDisposable: Disposable? = null
    private var mSendDisposable: Disposable? = null

    private var mUserAvatar: String = ""//用户头像
    private var mUserNickname: String = ""//用户昵称

    private var mMineInfo: UserInfoEntity? = null//我的信息（通过单例模式获取）

    private var mImageList: ArrayList<LocalMedia> = ArrayList()//图片集合（供查看大图时使用）
    private var redPackDialog: RedPackDialog? = null
    private var redPack: RedPack? = null


    override fun setContentViewIds(): Int {
        return R.layout.message_chat_layout
    }

    override fun initView() {
        mUserAccount = intent.getStringExtra("USER_ID")
        mUserAvatar = intent.getStringExtra("USER_AVATAR")
        mUserNickname = intent.getStringExtra("USER_NICKNAME")
        chatType = intent.getIntExtra("CHAT_TYPE", BaseEvent.CHAT_TYPE.PERSONAL.typeValue)
        Log.e("yancy", "mUserAccount=" + mUserAccount)
        Log.e("yancy", "mUserAvatar=" + mUserAvatar)
        Log.e("yancy", "mUserNickname=" + mUserNickname)
        Log.e("yancy", "chatType=" + chatType)

        if (mUserAccount.isNullOrBlank()) {
            return
        }

        mMineInfo = UserInfoManage.getInstance.mUserInfoEntity

        mImm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mWindow = window

        chat_view.initModule()//聊天的相关视图的初始化

        if (!mUserNickname.isNullOrBlank()) {
            chat_view.setTitle(mUserNickname)
        }

        mMediaPlayer = MediaPlayer()
        mLinearLayoutManager = LinearLayoutManager(this)
//        mLinearLayoutManager.stackFromEnd = true //将列表元素处于最底部
        msg_list.layoutManager = mLinearLayoutManager
        mChatAdapter = ChatAdapter(mChatDatas)
        msg_list.adapter = mChatAdapter
        queryData()


        if (chatType == BaseEvent.CHAT_TYPE.PERSONAL.typeValue) {//个人
            initChatManager(mUserAccount)

        } else if (chatType == BaseEvent.CHAT_TYPE.GROUP.typeValue) {//群聊
            if (mHttpUtil == null) {
                mHttpUtil = HttpUtil(this)
            }
            showProgressDialog(getString(R.string.waiting))
            mHttpUtil!!.setOnRequestCallBack { code, data ->
                //            Log.i("FAN","红包数据返回---> $data")
                dismissProgressDialog()
                if (code == -1) {
                    ToastUtils.showLong(data)
                    return@setOnRequestCallBack
                }

                val type: Type = object : TypeToken<BaseReq<GroupInfoEntity>>() {}.type
                val mReq: BaseReq<GroupInfoEntity> = GsonUtil.getGson().fromJson(data, type)
                if (TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {
                    mReq.result?.let {
                        initChatManager(mUserAccount)
                    }
                }

            }.getParms(httpParams, String.format(Config.API.group_info, mUserAccount.toInt()))
        }
    }


    private fun queryData() {
        mQueryDisposable = Observable.create<List<ChatMessageEvent>> { ob ->
            var mList: List<ChatMessageEvent>? = DbHelper.queryChatMessagePage(mUserAccount, offset)
            mList?.let {
                ob.onNext(it.sortedBy { it.id })
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.i("FAN", "查询出的记录---> $it")
                pull_to_refresh_layout.refreshComplete()
                if (it != null && it.isNotEmpty()) {
//                    mChatDatas.clear()
                    offset++
                    mChatDatas.addAll(0, it)
                    mChatAdapter.notifyDataSetChanged()
//                    if (offset == 0)
                    scrollToBottom(it.size - 1)
                }
            }
    }

    override fun initData() {
        initEvent()
        initChatViewClick()
        receiveChatMessage()
    }

    @SuppressLint("CheckResult")
    private fun initChatManager(userAccount: String) {
        mChatDisposable = Observable
            .just(userAccount)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .flatMap(Function<String, ObservableSource<Boolean>> {
                var result = false
                if (chatType == BaseEvent.CHAT_TYPE.PERSONAL.typeValue) {

                    mChat = SmackManager.getInstance().createChat(SmackManager.getInstance().getChatJid(it))

                    if (mChat != null) result = true
                } else if (chatType == BaseEvent.CHAT_TYPE.GROUP.typeValue) {
                    mGroupChat = SmackManager.getInstance().getMultiChat(it)
                    mGroupChat?.let {
                        if (!it.isJoined) {
                            it.join(mMineInfo?.user_id.toString())
                        } else {

                        }
                    }
                    if (mGroupChat != null) result = true
                }
                return@Function Observable.just(result)
            })
            .subscribe {
                if (!it) {
                    initChatManager(mUserAccount)
                }
            }
    }

    private fun initEvent() {
        pull_to_refresh_layout.setPtrHandler {
            queryData()
        }
        chat_view.setOnTouchListener(this)
        //返回
        chat_view.setLeftBackListener {
            KeyboardUtils.hideSoftInput(this@ChatActivity)
            finishs()
        }

        //导航栏右边->更多信息
        chat_view.setRightMoreListener {
            when (chatType) {
                BaseEvent.CHAT_TYPE.PERSONAL.typeValue -> {//单聊
                    ARouter
                        .getInstance()
                        .build(RouterPath.MineCenter.PATH_USER_INFO)
                        .withInt("USER_ID", mUserAccount.toInt())
                        .navigation()
                }
                BaseEvent.CHAT_TYPE.GROUP.typeValue -> startActivity<GroupInfoActivity>("GROUP_ID" to mUserAccount.toInt())
            }
        }

        //发送红包
        chat_view.setRedPacketListener(object : OnRedPacketClickListener {
            override fun onRedPacketClick() {
                Log.e("yancy", "发送红包--》");
                startActivityForResult<IntegratingRedActivity>(SEND_RED_PACKAGE, "TYPE" to chatType)
            }
        })

        chat_view.chatInputView.inputView.setOnTouchListener { view, motionEvent ->
            scrollToBottom(mChatDatas.size - 1)
            false
        }

        mChatAdapter.onChatMessageListClickedListener = object : OnChatMessageListClickedListener {
            override fun onInviteClicked(positon: Int, message: ChatMessageEvent?) {
                Log.e("yancy", "邀请入群：" + message.toString())
                ARouter
                    .getInstance()
                    .build(RouterPath.ContactCenter.PATH_ADD_FRIEND_REQUESTS)
                    .withParcelable("CHAT_MESSAGE", message)
                    .navigation()

            }

            override fun onImageClicked(position: Int, message: ChatMessageEvent) {
                showBigImage(message)
            }

            override fun onTextLongClicked(position: Int, message: ChatMessageEvent, view: View) {
                var pop = ActionPopWindow(this@ChatActivity, view);

                pop.setOnActionPopClickedListener(object : OnActionPopClickedListener {
                    override fun onShareListener() {
                    }

                    override fun onCopyListener() {
                        val cm = this@ChatActivity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        // 将文本内容放到系统剪贴板里。
                        Log.e("yancy", "复制到粘贴板：" + message.messageContent);
                        cm.text = message.messageContent
                    }

                    override fun onCollectionListener() {
                    }

                    override fun onForwardListener() {
                        ARouter
                            .getInstance()
                            .build(RouterPath.ContactCenter.PATH_CONTACT_PERSONAL)
                            .withString("FLAG", "invite")
                            .navigation()
                    }

                });
            }

            override fun onVoiceClicked(positon: Int, message: ChatMessageEvent) {
                playVoice(positon, message)
            }

            override fun onAvatarClicked(position: Int, message: ChatMessageEvent) {
                Log.e("yancy", "点击邀请入群的消息" + message.toString())
                ARouter
                    .getInstance()
                    .build(RouterPath.MineCenter.PATH_USER_INFO)
                    .withInt("USER_ID", message.fromUserID.toInt())
                    .navigation()
            }

            override fun onRedPackClicked(positon: Int, message: ChatMessageEvent) {
                lookRedPackInfo(message)
            }
        }


        /*
         *功能按钮
         */
        chat_view.setRecordVoiceListener(object : RecordVoiceListener {
            override fun onStartRecord() {
                val path = FileUtil.SDCARD_DIR + "/voice"
                val destDir = File(path)
                if (!destDir.exists()) {
                    destDir.mkdirs()
                }
                chat_view.setRecordVoiceFile(destDir.path, System.currentTimeMillis().toString())
            }

            override fun onFinishRecord(voiceFile: File, duration: Int) {
//                Log.i("FAN", "onFinishRecord--> ${voiceFile.absolutePath}")
                uploadFile(
                    BaseEvent.TYPE.RECORD_VOICE.typeValue,
                    filePath = voiceFile.absolutePath,
                    upUrl = Config.voice(),
                    voiceDuration = duration
                )
            }

            override fun onCancelRecord() {
                Log.i("FAN", "onCancelRecord")
            }

            override fun onPreviewCancel() {
                Log.i("FAN", "onPreviewCancel")
            }

            override fun onPreviewSend() {
                Log.i("FAN", "onPreviewSend")
            }
        })
    }

    @SuppressLint("CheckResult")
    private fun showBigImage(message: ChatMessageEvent) {
        Observable.create<ArrayList<LocalMedia>> {
            mImageList.clear()
            var mLocalMedia = LocalMedia()
            message.apply {
                if (messageContent.startsWith("/")) {
                    messageContent = Config.API.image_url + messageContent.replaceFirst("/", "")
                }
            }
            mLocalMedia.path = message.messageContent
            mImageList.add(mLocalMedia)
            it.onNext(mImageList)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                PictureUtil.lookBigImages(this, 0, it)
            }

    }

    @SuppressLint("CheckResult")
    private fun playVoice(position: Int, message: ChatMessageEvent) {

        Observable.create<Boolean> {
            message.voiceIsRead = true
            it.onNext(DbHelper.updateChatMessage(message))

        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it) {
                    mChatAdapter.notifyItemChanged(position)
                    mMediaPlayer.reset()
                    mMediaPlayer.setDataSource(message.messageContent)
                    mMediaPlayer.prepareAsync()
                    mMediaPlayer.setOnPreparedListener {
                        it.start()
                    }
                    mMediaPlayer.setOnCompletionListener {
                        it.pause()
                    }
                }
            }
    }

    //接收聊天信息
    @SuppressLint("CheckResult")
    private fun receiveChatMessage() {
        mMessageDisposable = RxBus.getInstance()
            .toObserverable(ChatMessageEvent::class.java)
            .subscribe {
                Log.i("FAN", "接收数据---> ${it}")
                if (TextUtils.equals(mUserAccount, it.chatUserID)) {
                    mChatDatas.add(it)
                    mChatAdapter.notifyItemInserted(mChatDatas.size - 1)
                    chat_view.messageListView.smoothScrollToPosition(mChatDatas.size - 1)
                }
            }
    }

    private fun initChatViewClick() {
        chat_view.setMenuClickListener(object : OnMenuClickListener {
            override fun onSendTextMessage(input: CharSequence): Boolean {
//                Log.i("FAN", "input----> ${input} ")
                if (input.isNotEmpty()) {
                    sendMessage(input.toString(), BaseEvent.TYPE.TEXT.typeValue)
                }
                return true
            }

            override fun onSendFiles(list: MutableList<FileItem>?) {
            }

            override fun switchToMicrophoneMode(): Boolean {
                recordVoice()
                return true
            }

            override fun switchToGalleryMode(): Boolean {
                selectImage(false)
                return false
            }

            override fun switchToCameraMode(): Boolean {
                selectImage(true)
                return false
            }

            override fun switchToEmojiMode(): Boolean {
                return true
            }
        })
    }

    //开启红包
    private fun openRedPack(redPackId: String) {
        if (mHttpUtil == null) {
            mHttpUtil = HttpUtil(this)
        }
        Log.e("yacny", "红包参数：" + redPackId)
        showProgressDialog(getString(R.string.waiting))
        mHttpUtil!!.setOnRequestCallBack { code, data ->
            //            Log.i("FAN","红包数据返回---> $data")
            dismissProgressDialog()
            if (code == -1) {
                ToastUtils.showLong(data)
                redPackDialog?.dismiss()
                return@setOnRequestCallBack
            }
            val mType: Type = object : TypeToken<BaseReq<OpenRedPacketBean>>() {}.type
            val mReq: BaseReq<OpenRedPacketBean> = GsonUtil.getGson().fromJson(data, mType)
            Log.e("yancy", "打开红包的结果：" + mReq.toString());
            ToastUtils.showLong(mReq.msg)
            if (TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {
                startActivity<RedEnvelopeActivity>(
                    "RED_PACK_ID" to redPackId,
                    "REDPACK" to redPack,
                    "INTEGRAL" to mReq.result.increaseIntegral.toString()
                )
                redPackDialog?.dismiss()
                return@setOnRequestCallBack
            } else if (TextUtils.equals(mReq.status, Config.Constant.NONE)) {
                ToastUtils.showLong(mReq.msg)
                redPackDialog?.showNonePackInfo()
//                redPackDialog?.let {
//                    if (mReq.result.)
//                }
            }
        }.getParms(httpParams, String.format(Config.API.red_pack_info_receive, redPackId))
    }

    /**
     * 查看红包信息
     */
    private fun lookRedPackInfo(message: ChatMessageEvent) {
        if (mHttpUtil == null) {
            mHttpUtil = HttpUtil(this)
        }

        showProgressDialog(getString(R.string.waiting))
        mHttpUtil!!.setOnRequestCallBack { code, data ->
            //            Log.i("FAN","红包数据返回---> $data")
            dismissProgressDialog()
            if (code == -1) {
                ToastUtils.showLong(data)
                return@setOnRequestCallBack
            }
            val mType: Type = object : TypeToken<BaseReq<RedPackInfoEntity>>() {}.type
            val mReq: BaseReq<RedPackInfoEntity> = GsonUtil.getGson().fromJson(data, mType)

            if (!TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {
                ToastUtils.showLong(mReq.msg)
                return@setOnRequestCallBack
            } else {
                redPack = mReq.result.redPack
                if (mReq.result.mineRedPack != null) {
                    startActivity<RedEnvelopeActivity>(
                        "RED_PACK_ID" to message.messageContent,
                        "REDPACK" to redPack,
                        "INTEGRAL" to mReq.result.mineRedPack!!.integral
                    )
                } else {
                    redPackDialog = RedPackDialog(this@ChatActivity, mReq.result)
                    redPackDialog?.onRedPackOpenClickedListener = object : RedPackDialog.OnRedPackOpenClickedListener {
                        override fun onGotoDetail() {
                            redPackDialog?.dismiss()
                            startActivity<RedEnvelopeActivity>(
                                "RED_PACK_ID" to message.messageContent,
                                "REDPACK" to redPack,
                                "INTEGRAL" to "0"
                            )
                        }

                        override fun onRedPackOpen(redPackId: Int) {
                            openRedPack(message.messageContent)
                        }
                    }
                    redPackDialog?.show()
                }
            }
        }.getParms(httpParams, String.format(Config.API.red_pack_info, message.messageContent))


    }

    /**
     * 选择图片
     */
    private fun selectImage(isCamera: Boolean) {
        PermissionUtils.permission(PermissionConstants.STORAGE, PermissionConstants.CAMERA)
            .rationale { shouldRequest -> shouldRequest.again(true) }
            .callback(object : PermissionUtils.FullCallback {
                override fun onGranted(permissionsGranted: List<String>) {
                    if (mPictureUtil == null) {
                        mPictureUtil = PictureUtil()
                        mPictureUtil!!.setEnableCrop(false)

                    }
                    if (!isCamera) {
                        mPictureUtil!!.setRequestCode(SELECT_IMAGE_CODE)
                        mPictureUtil!!.selectPicture(this@ChatActivity)
                    } else {
                        mPictureUtil!!.setRequestCode(SELECT_IMAGE_FROM_CAMERA_CODE)
                        mPictureUtil!!.selectPictureFromCamera(this@ChatActivity)
                    }
                }

                override fun onDenied(permissionsDeniedForever: List<String>, permissionsDenied: List<String>) {
                    ToastUtils.showShort("权限已拒绝")
                }
            })
            .request()
    }

    /**
     * 录音
     */
    private fun recordVoice() {
        PermissionUtils.permission(PermissionConstants.STORAGE, PermissionConstants.MICROPHONE)
            .rationale { shouldRequest -> shouldRequest.again(true) }
            .callback(object : PermissionUtils.FullCallback {
                override fun onDenied(
                    permissionsDeniedForever: MutableList<String>?,
                    permissionsDenied: MutableList<String>?
                ) {
                }

                override fun onGranted(permissionsGranted: MutableList<String>?) {
                }
            })
            .request()
    }


    //发送消息
    @SuppressLint("CheckResult")
    private fun sendMessage(message: String, messageType: Int, localMedia: LocalMedia? = null, voiceDuration: Int = 0) {
        mSendDisposable = Observable.just(message)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .flatMap(Function<String, ObservableSource<Boolean>> {
                try {
                    var mChatEvent = ChatMessageEvent()
                    mChatEvent.messageType = messageType
                    mChatEvent.messageContent = it
                    mChatEvent.avatar = if (!mMineInfo?.avatar.isNullOrBlank()) {
                        mMineInfo?.avatar
                    } else {
                        ""
                    }
                    mChatEvent.nickname = if (!mMineInfo?.nickname.isNullOrBlank()) {
                        mMineInfo?.nickname
                    } else {
                        ""
                    }

                    localMedia?.let { img ->
                        mChatEvent.imageHeight = img.height
                        mChatEvent.imageWidth = img.width
                    }

                    mChatEvent.voiceDuration = voiceDuration



                    mChatEvent.chatType = chatType
                    mChatEvent.fromUserID = mMineInfo?.user_id.toString()
                    mChatEvent.toUserID = mUserAccount
                    mChatEvent.chatUserID = mUserAccount//设置当前聊天人的好友id，
                    mChatEvent.createTime = System.currentTimeMillis()//当前消息的时间

                    Log.e("yancy", "发送的数据:" + mChatEvent.toString())
                    if (chatType == BaseEvent.CHAT_TYPE.PERSONAL.typeValue) {
                        mChat?.sendMessage(GsonUtil.getGson().toJson(mChatEvent))
                        mChatEvent.avatar = mUserAvatar//改变头像的设置，供列表好友头像显示
                        mChatEvent.nickname = mUserNickname//改变昵称的设置，供列表好友昵称显示
                    } else if (chatType == BaseEvent.CHAT_TYPE.GROUP.typeValue) {
                        mChatEvent.groupName = mUserNickname
                        mChatEvent.groupAvatar = mUserAvatar
                        mGroupChat?.sendMessage(GsonUtil.getGson().toJson(mChatEvent))
                    }


                    var rowId: Long = DbHelper.insertChatMessage(mChatEvent)//插入数据库
                    mChatEvent.id = rowId//设置当前该信息在数据库的id
                    RxBus.getInstance().post(mChatEvent)//通知发送消息，接收位置有（MessageFragment）


                } catch (e: SmackException.NotConnectedException) {
                    return@Function Observable.just(false)
                }
                return@Function Observable.just(true)
            })
            .subscribe {
                if (!it) {
                    ToastUtils.showLong("消息发送失败")
                }
            }
    }

    private fun scrollToBottom(position: Int) {
        mHandler.postDelayed({
            //            chat_view.messageListView.smoothScrollToPosition(position)
            chat_view.messageListView.scrollToPosition(position)
        }, 200)
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                val chatInputView = chat_view.getChatInputView()
                if (chatInputView.menuState == View.VISIBLE) {
                    chatInputView.dismissMenuLayout()
                }
                try {
                    val v = currentFocus
                    if (mImm != null && v != null) {
                        mImm.hideSoftInputFromWindow(v.windowToken, 0)
                        mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                        view.clearFocus()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            MotionEvent.ACTION_UP -> view.performClick()
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        data?.let {
            if ((requestCode == SELECT_IMAGE_CODE || requestCode == SELECT_IMAGE_FROM_CAMERA_CODE) && resultCode == Activity.RESULT_OK) {
                val mImageList: List<LocalMedia> = PictureSelector.obtainMultipleResult(it)

                if (mImageList.isNotEmpty()) {
                    uploadFile(
                        BaseEvent.TYPE.IMAGE.typeValue,
                        localMedia = mImageList[0],
                        filePath = mImageList[0].compressPath,
                        upUrl = Config.msg_()
                    )
                }
            } else if (requestCode == SEND_RED_PACKAGE) {//发送红包回调

                Log.e("yancy", "发送红包成功")

                var redPacketBean = it.getSerializableExtra("data") as RedPacketBean
                //发送成功
                val userInfoEntity = UserInfoManage.getInstance.mUserInfoEntity
                val chatMessageEvent = ChatMessageEvent()
                chatMessageEvent.messageType = BaseEvent.TYPE.RED_PACK.typeValue
                chatMessageEvent.messageContent = redPacketBean.red_pack_id.toString()
                chatMessageEvent.redEnvelopesTitle = redPacketBean.wishes
                chatMessageEvent.avatar = userInfoEntity!!.avatar
                chatMessageEvent.nickname = userInfoEntity.nickname
                when (chatType) {
                    BaseEvent.CHAT_TYPE.PERSONAL.typeValue -> mChat?.sendMessage(
                        GsonUtil.getGson().toJson(
                            chatMessageEvent
                        )
                    )
                    BaseEvent.CHAT_TYPE.GROUP.typeValue -> mGroupChat?.sendMessage(
                        GsonUtil.getGson().toJson(
                            chatMessageEvent
                        )
                    )
                    else -> {

                    }
                }
                chatMessageEvent.fromUserID = mMineInfo?.user_id.toString()
                chatMessageEvent.toUserID = mUserAccount
                chatMessageEvent.nickname = mUserNickname//改变昵称的设置，供列表好友昵称显示
                chatMessageEvent.chatUserID = mUserAccount//设置当前聊天人的好友id，
                chatMessageEvent.createTime = System.currentTimeMillis()//当前消息的时间
                var rowId: Long = DbHelper.insertChatMessage(chatMessageEvent)//插入数据库
                chatMessageEvent.id = rowId//设置当前该信息在数据库的id
                RxBus.getInstance().post(chatMessageEvent)//通知发送消息，接收位置有（MessageFragment）
            }
        }
    }

    /**
     * 上传文件
     */
    private fun uploadFile(
        messageType: Int,
        filePath: String,
        upUrl: String,
        localMedia: LocalMedia? = null,
        voiceDuration: Int = 0
    ) {
        if (mHttpUtil == null) {
            mHttpUtil = HttpUtil(this)
        }

        mHttpUtil!!.setOnRequestCallBack { code, data ->
            //            Log.i("FAN","上传图片返回---> $data")
            if (code == -1) {
                ToastUtils.showLong(data)
                return@setOnRequestCallBack
            }
            var mFileLoadEntity: FileLoadEntity = GsonUtil.getGson().fromJson(data, FileLoadEntity::class.java)
            if (!mFileLoadEntity.url.isNullOrEmpty()) {
                sendMessage(
                    "/$upUrl",
                    messageType,
                    localMedia = localMedia,
                    voiceDuration = voiceDuration
                )
            }
        }.postFile(Config.API.image_url, upUrl, filePath, httpParams)
    }


    override fun onPause() {
        super.onPause()
        mMediaPlayer.pause()
    }


    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacks(null)
        mChatDisposable?.dispose()
        mChatDisposable = null

        mMessageDisposable?.dispose()
        mMessageDisposable = null

        mQueryDisposable?.dispose()
        mQueryDisposable = null

        mSendDisposable?.dispose()
        mSendDisposable = null

        mMediaPlayer.release()

        PictureFileUtils.deleteCacheDirFile(this)
    }

    override fun requestDragAndDropPermissions(event: DragEvent?): DragAndDropPermissions {
        return super.requestDragAndDropPermissions(event)
    }
}
