package com.cosven.message.impl

import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.view.View
import cn.jiguang.imui.chatinput.model.FileItem
import com.cosven.message.bean.MyMessage
import java.io.File

/**
 * 消息的一系列事件
 */
interface MsgListenet {

    // 传感器
    fun onSensorChanged(event: SensorEvent?, mSensorManager: SensorManager)

    /**
     * Adater点击事件
     */

    //  消息点击
    fun onMessageClick(message: MyMessage)

    // 长按
    fun onMessageLongClick(view: View, message: MyMessage)

    // 头像点击
    fun onAvatarClick(message: MyMessage)

    // 点击消息状态按钮出发
    fun onStatusViewClick(message: MyMessage)

    // 加载更多消息
    fun onLoadMore(page: Int, totalCount: Int)


    /**
     * ChatView  Menu点击事件
     */

    fun onSendTextMessage(input: CharSequence)

    fun onSendFiles(list: MutableList<FileItem>?)
    fun switchToMicrophoneMode(): Boolean
    fun switchToGalleryMode(): Boolean
    fun switchToCameraMode(): Boolean
    fun switchToEmojiMode(): Boolean

    /**
     * Chatview Voice  录音
     */

    fun onStartRecord()
    fun onFinishRecord(voiceFile: File?, duration: Int)
    fun onCancelRecord()
    fun onPreviewCancel()
    fun onPreviewSend()

    /**
     *  Chatview Camera 拍照
     */

    fun onTakePictureCompleted(photoPath: String?)
    fun onStartVideoRecord()
    fun onFinishVideoRecord(videoPath: String?)
    fun onCancelVideoRecord()
}