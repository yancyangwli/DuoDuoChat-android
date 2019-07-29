package com.cosven.message.adapter

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import cn.jiguang.imui.view.RoundTextView
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cosven.message.R
import com.woniu.core.utils.DateUtil
import com.woniu.core.utils.ImageUtil
import com.woniu.core.xmpp.rxbus.event.BaseEvent
import com.woniu.core.xmpp.rxbus.event.MessageTempEvent

/**
 * @author Anlycal<远>
 * @date 2019/5/27
 * @description ...
 */


class MesageListAdapter(data: List<MessageTempEvent>) :
    BaseQuickAdapter<MessageTempEvent, BaseViewHolder>(R.layout.message_item_list, data) {
    override fun convert(helper: BaseViewHolder, item: MessageTempEvent) {
        helper.addOnClickListener(R.id.mBtnDelete)
        helper.addOnClickListener(R.id.mRlListRoot)

        if (!item.avatar.isNullOrBlank()) {
            ImageUtil.loadOriginalImage(mContext, item.avatar, helper.getView(R.id.mIvAvatar))
        } else {
            helper.setImageResource(
                R.id.mIvAvatar, when {
                    item.chatType == BaseEvent.CHAT_TYPE.PERSONAL.typeValue -> R.mipmap.icon_default_head
                    item.chatType == BaseEvent.CHAT_TYPE.GROUP.typeValue -> R.mipmap.icon_default_group
                    else -> R.mipmap.ic_system_info
                }
            )
        }

        helper.setText(R.id.mTvCreateTime, DateUtil.formatTime(item.createTime, DateUtil.yyMMdd))

        if (item.unReadNumber == 0) {
            helper.getView<RoundTextView>(R.id.mTvUnReadNum).visibility = View.GONE
        } else {
            var mTvUnReadNum = helper.getView<RoundTextView>(R.id.mTvUnReadNum)
            mTvUnReadNum.visibility = View.VISIBLE
            mTvUnReadNum.text = item.unReadNumber.toString()
        }

        helper.setText(
            R.id.mTvMessage, when {
                item.messageType == BaseEvent.TYPE.TEXT.typeValue -> item.messageContent
                item.messageType == BaseEvent.TYPE.IMAGE.typeValue -> "[图片]"
                item.messageType == BaseEvent.TYPE.RECORD_VOICE.typeValue -> "[语音]"
                item.messageType == BaseEvent.TYPE.RED_PACK.typeValue -> "[红包]"
                else -> "[其他类型]"
            }
        )

        item.nickname?.let {
            helper.setText(R.id.mTvName, it)
        }


//        var avatarUrl: String
//
//        var position: Int = helper.adapterPosition
//
//        if (position == 3){
//            helper.setText(R.id.mTvName,"系统消息")
//            helper.setText(R.id.mTvMessage,"系统消息推送了")
//            helper.setImageResource(R.id.mIvAvatar,R.mipmap.ic_system_info)
//            return
//        }
//
//        if (position == 5){
//            helper.setText(R.id.mTvName,"群消息")
//            helper.setText(R.id.mTvMessage,"您被开发群踢了")
//            helper.setImageResource(R.id.mIvAvatar,R.mipmap.ic_group_info)
//            return
//        }
//
//        if (position % 3 == 0) {
//            avatarUrl =
//                "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=2482882990,2456775461&fm=26&gp=0.jpg"
//            helper.setText(R.id.mTvName, "开发群${position}")
//        } else {
//            avatarUrl =
//                "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3340295553,242321368&fm=27&gp=0.jpg"
//        }
//        ImageUtil.loadOriginalImage(mContext, avatarUrl, helper.getView(R.id.mIvAvatar))
    }


}