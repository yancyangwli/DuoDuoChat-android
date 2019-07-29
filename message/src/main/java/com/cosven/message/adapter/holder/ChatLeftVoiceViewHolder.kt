package com.cosven.message.adapter.holder

import android.util.Log
import android.view.View
import android.view.ViewStub
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.cosven.message.R

/**
 * @author Anlycal<远>
 * @date 2019/6/26
 * @description ...
 */


class ChatLeftVoiceViewHolder(mView: View):BaseLeftChatViewHolder(mView) {
    lateinit var mTvVoiceDuration:TextView
    lateinit var mIvUnReadPoint:ImageView


    override fun initSubView(viewStub: ViewStub) {
        viewStub.layoutResource = R.layout.item_chat_left_voice_record_list
        mInflateView = viewStub.inflate()
        mTvVoiceDuration = mInflateView.findViewById(R.id.mTvVoiceDuration)
        mIvUnReadPoint = mInflateView.findViewById(R.id.mIvUnRead)
    }
}