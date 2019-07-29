package com.cosven.message.adapter.holder

import android.view.View
import android.view.ViewStub
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.cosven.message.R
import com.makeramen.roundedimageview.RoundedImageView
import com.woniu.core.xmpp.rxbus.event.ChatMessageEvent

/**
 * @author Anlycal<远>
 * @date 2019/6/19
 * @description ...
 */


class ChatLeftInviteViewHolder(mView: View) : BaseLeftChatViewHolder(mView) {
    lateinit var mIvImage: ImageView
    lateinit var mInfoTv: TextView


    override fun initSubView(viewStub: ViewStub) {
        viewStub.layoutResource = R.layout.item_chat_left_invite_info

        mInflateView = viewStub.inflate()

        mIvImage = mInflateView.findViewById(R.id.mIvImage)
        mInfoTv = mInflateView.findViewById(R.id.mInfoTv)

    }

}