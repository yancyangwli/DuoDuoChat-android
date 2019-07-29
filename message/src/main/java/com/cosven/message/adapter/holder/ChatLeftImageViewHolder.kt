package com.cosven.message.adapter.holder

import android.view.View
import android.view.ViewStub
import com.makeramen.roundedimageview.RoundedImageView
import com.cosven.message.R

/**
 * @author Anlycal<远>
 * @date 2019/6/19
 * @description ...
 */


class ChatLeftImageViewHolder(mView:View):BaseLeftChatViewHolder(mView) {
    lateinit var mIvImage: RoundedImageView
    override fun initSubView(viewStub: ViewStub) {
        viewStub.layoutResource = R.layout.item_chat_left_image_list
        mInflateView = viewStub.inflate()
        mIvImage = mInflateView.findViewById(R.id.mIvImage)
    }

}