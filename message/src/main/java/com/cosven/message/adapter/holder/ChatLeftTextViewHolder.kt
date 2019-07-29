package com.cosven.message.adapter.holder

import android.view.View
import android.view.ViewStub
import android.widget.TextView
import com.cosven.message.R

/**
 * @author Anlycal<远>
 * @date 2019/6/19
 * @description ...
 */


class ChatLeftTextViewHolder(mView:View):BaseLeftChatViewHolder(mView) {

    lateinit var mTvContent:TextView


    override fun initSubView(viewStub: ViewStub) {
        viewStub.layoutResource = R.layout.item_chat_left_text_list
        val mV:View = viewStub.inflate();
        mTvContent = mV.findViewById(R.id.mTvContent)

    }
}