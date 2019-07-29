package com.cosven.socialim.ui.activity.me

import android.os.Environment
import android.util.Log
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.cosven.my.R
import com.woniu.core.activities.BaseActivity
import com.woniu.core.db.DbHelper
import com.woniu.core.utils.YancyDeleteFile
import com.woniu.core.xmpp.rxbus.RxBus
import com.woniu.core.xmpp.rxbus.event.ChatMessageEvent
import com.woniu.core.xmpp.rxbus.event.LoginEvent
import com.zhuanxu.eclipse.aop.annotation.SingleClick
import kotlinx.android.synthetic.main.activity_clear_cache.*
import kotlinx.android.synthetic.main.my_title_layout.*
import org.jetbrains.anko.toast
import java.io.File
import java.io.File.separator


/**
 * 清除缓存
 */
class ClearCacheActivity : BaseActivity() {
    @SingleClick
    override fun onClick(v: View?) {

    }

    override fun setContentViewId(): Int {
        return R.layout.activity_clear_cache
    }

    override fun initView() {
        setTitles(qmui_bar, "清除缓存", true)
        mClearImageFileTv.setOnClickListener {
            //固定的图片文件    /PictureSelector
            //固定的语音文件    /voice
            var directory = Environment.getExternalStorageDirectory()
            val pathIMG = "$directory/PictureSelector"
            val pathVocic = "$directory/voice"
            val clearImageAndVoice = clearImageAndVoice(pathIMG) && clearImageAndVoice(pathVocic);
            if (clearImageAndVoice) {
                ToastUtils.showLong("清除图片与语音记录成功")
            } else {
                ToastUtils.showLong("清除图片与语音记录失败")
            }
        }

        mClearChatRecordTv.setOnClickListener {
            if (flagRecord()) {
                RxBus.getInstance().post(ChatMessageEvent());
                ToastUtils.showLong("清除聊天记录成功")
            } else
                ToastUtils.showLong("清除聊天记录失败")
        }

        mClearAllTv.setOnClickListener {
            var directory = Environment.getExternalStorageDirectory()
            val pathIMG = "$directory/PictureSelector"
            val pathVocic = "$directory/voice"
            val clearImageAndVoice = clearImageAndVoice(pathIMG) && clearImageAndVoice(pathVocic) && flagRecord()
            if (clearImageAndVoice) {
                ToastUtils.showLong("清除成功")
                RxBus.getInstance().post(ChatMessageEvent());
            } else {
                ToastUtils.showLong("清除失败")
            }

        }
    }

    internal fun flagRecord(): Boolean {
        return DbHelper.deleteChatMessageAll() && DbHelper.deleteMessageTempEventAll()
    }

    internal fun clearImageAndVoice(path: String): Boolean {
        return YancyDeleteFile.delete(path)
    }

    override fun initData() {
    }
}