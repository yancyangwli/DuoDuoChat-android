package com.cosven.message.adapter.holder

import android.view.View
import android.view.ViewStub
import android.widget.TextView
import com.chad.library.adapter.base.BaseViewHolder
import com.cosven.message.R
import de.hdodenhof.circleimageview.CircleImageView

/**
 * @author Anlycal<远>
 * @date 2019/6/19
 * @description ...
 */


open class BaseLeftChatViewHolder(view: View) : BaseViewHolder(view) {
    var mTvTime: TextView = view.findViewById(R.id.mTvTime)
    var mIvAvatar: CircleImageView = view.findViewById(R.id.mIvAvatar)
    lateinit var mInflateView:View
    init {
        var mViewStub: ViewStub = view.findViewById(R.id.left_chat_viewstub)
        initSubView(mViewStub)
    }

    open fun initSubView(viewStub: ViewStub) {

    }
}