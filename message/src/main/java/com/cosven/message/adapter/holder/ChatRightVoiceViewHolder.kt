package com.cosven.message.adapter.holder

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


class ChatRightVoiceViewHolder(mView: View):BaseRightChatViewHolder(mView) {
    lateinit var mTvVoiceDuration: TextView

    override fun initSubView(viewStub: ViewStub) {
        viewStub.layoutResource = R.layout.item_chat_right_voice_record_list
        mInflateView = viewStub.inflate()
        mTvVoiceDuration = mInflateView.findViewById(R.id.mTvVoiceDuration)
    }
}