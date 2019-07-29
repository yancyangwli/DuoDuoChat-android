package com.cosven.message.adapter.holder

import android.view.View
import android.view.ViewStub
import android.widget.ImageView
import android.widget.ProgressBar
import com.cosven.message.R
import com.makeramen.roundedimageview.RoundedImageView
import com.woniu.core.xmpp.rxbus.event.ChatMessageEvent

/**
 * @author Anlycal<远>
 * @date 2019/6/19
 * @description ...
 */


class ChatRightImageViewHolder(mView: View) : BaseRightChatViewHolder(mView) {
    lateinit var mIvError: ImageView
    lateinit var mProgressBar: ProgressBar
    lateinit var mIvImage: RoundedImageView


    override fun initSubView(viewStub: ViewStub) {
        viewStub.layoutResource = R.layout.item_chat_right_image_list

        mInflateView = viewStub.inflate()

        mIvError = mInflateView.findViewById(R.id.mIvError)
        mProgressBar = mInflateView.findViewById(R.id.mProgressBar)
        mIvImage = mInflateView.findViewById(R.id.mIvImage)

        mIvError.visibility = View.GONE
        mProgressBar.visibility = View.GONE

    }

}