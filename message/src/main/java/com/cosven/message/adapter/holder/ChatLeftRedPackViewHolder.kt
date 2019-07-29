package com.cosven.message.adapter.holder

import android.view.View
import android.view.ViewStub
import android.widget.TextView
import com.cosven.message.R

/**
 * @author Anlycal<远>
 * @date 2019/6/27
 * @description ...
 */


class ChatLeftRedPackViewHolder(mView:View):BaseLeftChatViewHolder(mView) {
    lateinit var mTvRedPackTitle:TextView

    override fun initSubView(viewStub: ViewStub) {
        viewStub.layoutResource = R.layout.item_chat_red_pack_list
        mInflateView = viewStub.inflate()
        mTvRedPackTitle = mInflateView.findViewById(R.id.mTvRedPackTitle)
    }
}