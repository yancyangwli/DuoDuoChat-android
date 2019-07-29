package com.cosven.message.bean

import cn.jiguang.imui.commons.models.IMessage
import cn.jiguang.imui.commons.models.IUser
import java.util.*

class MyMessage : IMessage {

    private val id: Long
    private var text: String? = null
    private var timeString: String? = null
    private var type: Int = 0
    private lateinit var user: IUser
    private var mediaFilePath: String? = null
    private var duration: Long = 0
    private var progress: String? = null
    private var mMsgStatus: IMessage.MessageStatus = IMessage.MessageStatus.CREATED

    constructor(text: String?, type: Int?) {
        this.text = text!!
        this.type = type!!
        this.id = UUID.randomUUID().leastSignificantBits
    }

    override fun getMsgId(): String {
        return id.toString()
    }

    fun getId(): Long {
        return this.id
    }

    override fun getFromUser(): IUser {
        return if (user == null) {
            DefaultUser("0", "user1", null)
        } else user!!
    }

    fun setUserInfo(user: IUser) {
        this.user = user
    }

    fun setMediaFilePath(path: String) {
        this.mediaFilePath = path
    }

    fun setDuration(duration: Long) {
        this.duration = duration
    }

    override fun getDuration(): Long {
        return duration
    }

    fun setProgress(progress: String) {
        this.progress = progress
    }

    override fun getProgress(): String? {
        return progress
    }

    override fun getExtras(): HashMap<String, String>? {
        return null
    }

    fun setTimeString(timeString: String) {
        this.timeString = timeString
    }

    override fun getTimeString(): String? {
        return timeString
    }

    fun setType(type: Int) {
        if (type >= 0 && type <= 12) {
            throw IllegalArgumentException("Message type should not take the value between 0 and 12")
        }
        this.type = type
    }

    override fun getType(): Int {
        return type
    }

    /**
     * Set Message status. After sending Message, change the status so that the progress bar will dismiss.
     * @param messageStatus [cn.jiguang.imui.commons.models.IMessage.MessageStatus]
     */
    fun setMessageStatus(messageStatus: IMessage.MessageStatus) {
        this.mMsgStatus = messageStatus
    }

    override fun getMessageStatus(): IMessage.MessageStatus {
        return this.mMsgStatus
    }

    override fun getText(): String? {
        return text
    }

    override fun getMediaFilePath(): String? {
        return mediaFilePath
    }
}