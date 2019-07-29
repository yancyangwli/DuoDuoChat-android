package com.cosven.message.manger

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.telephony.SmsMessage
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Adapter
import cn.jiguang.imui.chatinput.listener.OnCameraCallbackListener
import cn.jiguang.imui.chatinput.listener.OnMenuClickListener
import cn.jiguang.imui.chatinput.listener.RecordVoiceListener
import cn.jiguang.imui.chatinput.model.FileItem
import cn.jiguang.imui.messages.MsgListAdapter
import com.cosven.message.bean.MyMessage
import com.cosven.message.impl.MsgListenet
import com.cosven.message.view.ChatView
import java.io.File

class MsgManger : View.OnTouchListener, SensorEventListener {

    var mAdapter: MsgListAdapter<MyMessage>? = null
    var mChatView: ChatView? = null
    var mContext: Context? = null
    var mImm: InputMethodManager? = null
    var mWindow: Window? = null
    var mSensor: Sensor? = null
    var mSensorManager: SensorManager? = null
    var mLinstener: MsgListenet? = null


    fun bindListener(mLinstener: MsgListenet?) {
        this.mLinstener = mLinstener
    }

    fun inits(mChatView: ChatView, mAdapter: MsgListAdapter<MyMessage>, mContext: Context, mWindow: Window?) {
        this.mChatView = mChatView
        this.mAdapter = mAdapter
        this.mContext = mContext
        this.mWindow = mWindow
        initSystemManger()
    }

    fun initSystemManger() {
        mImm = mContext!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager  // 软键盘输入管理
        mSensorManager = mContext!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager // 传感器服务
        mSensor = mSensorManager!!.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        mSensorManager!!.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL)

        mChatView?.let {
            it.initModule()   // 初始化输入框的布局和动态
        }
    }

    // 适配器点击事件
    fun registerAdapterClick() {

        mAdapter!!.setOnMsgClickListener {
            mLinstener!!.onMessageClick(it)
        }

        mAdapter!!.setMsgLongClickListener { view, message ->
            mLinstener!!.onMessageLongClick(view, message)
        }

        mAdapter!!.setOnAvatarClickListener {
            mLinstener!!.onAvatarClick(it)
        }
        mAdapter!!.setMsgStatusViewClickListener {
            mLinstener!!.onStatusViewClick(it)
        }

        mAdapter!!.setOnLoadMoreListener { page, totalCount ->
            mLinstener!!.onLoadMore(page, totalCount)
        }
    }

    // ChatView的适配事件
    fun registerChatViewClick() {
        mChatView!!.setOnTouchListener(this)
        // 底部menu点击事件
        mChatView!!.setMenuClickListener(object : OnMenuClickListener {
            override fun onSendTextMessage(input: CharSequence?): Boolean {
                mLinstener!!.onSendTextMessage(input!!.toString())
                return true
            }

            override fun onSendFiles(list: MutableList<FileItem>?) {
                mLinstener!!.onSendFiles(list)
            }

            override fun switchToMicrophoneMode(): Boolean {
                mLinstener!!.switchToMicrophoneMode()
                return true
            }

            override fun switchToGalleryMode(): Boolean {
                mLinstener!!.switchToGalleryMode()
                return true
            }

            override fun switchToCameraMode(): Boolean {
                mLinstener!!.switchToCameraMode()
                return true
            }

            override fun switchToEmojiMode(): Boolean {
                mLinstener!!.switchToEmojiMode()
                return true
            }
        })
        // 录音
        mChatView!!.setRecordVoiceListener(object : RecordVoiceListener {
            override fun onStartRecord() {
                mLinstener!!.onStartRecord()
            }

            override fun onFinishRecord(voiceFile: File?, duration: Int) {
                mLinstener!!.onFinishRecord(voiceFile, duration)
            }

            override fun onCancelRecord() {
                mLinstener!!.onCancelRecord()
            }

            override fun onPreviewCancel() {
                mLinstener!!.onPreviewCancel()
            }

            override fun onPreviewSend() {
                mLinstener!!.onPreviewSend()
            }
        })
        // 拍照
        mChatView!!.setOnCameraCallbackListener(object : OnCameraCallbackListener {
            override fun onTakePictureCompleted(photoPath: String?) {
                mLinstener!!.onTakePictureCompleted(photoPath)
            }

            override fun onStartVideoRecord() {
                mLinstener!!.onStartVideoRecord()
            }

            override fun onFinishVideoRecord(videoPath: String?) {
                mLinstener!!.onFinishVideoRecord(videoPath)
            }

            override fun onCancelVideoRecord() {
                mLinstener!!.onCancelVideoRecord()
            }
        })
    }


    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        when (motionEvent!!.action) {
            MotionEvent.ACTION_DOWN -> {
                val chatInputView = mChatView!!.getChatInputView()
                if (chatInputView.menuState == View.VISIBLE) {
                    chatInputView.dismissMenuLayout()
                }
                try {
                    val v = mWindow!!.getCurrentFocus()
                    if (mImm != null && v != null) {
                        mImm!!.hideSoftInputFromWindow(v!!.getWindowToken(), 0)
                        mWindow!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                        view!!.clearFocus()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            MotionEvent.ACTION_UP -> view!!.performClick()
        }
        return false
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        mSensorManager?.let { mLinstener!!.onSensorChanged(event, it) }
    }


}