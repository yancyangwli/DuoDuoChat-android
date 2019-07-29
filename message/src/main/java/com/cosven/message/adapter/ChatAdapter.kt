package com.cosven.message.adapter

import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.blankj.utilcode.util.SPUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cosven.message.R
import com.cosven.message.adapter.holder.*
import com.cosven.message.impl.OnChatMessageListClickedListener
import com.makeramen.roundedimageview.RoundedImageView
import com.woniu.core.api.Config
import com.woniu.core.bean.entity.UserInfoEntity
import com.woniu.core.manage.UserInfoManage
import com.woniu.core.utils.DateUtil
import com.woniu.core.utils.ImageUtil
import com.woniu.core.xmpp.rxbus.event.BaseEvent
import com.woniu.core.xmpp.rxbus.event.ChatMessageEvent
import com.woniu.core.xmpp.rxbus.event.LoginEvent
import de.hdodenhof.circleimageview.CircleImageView

/**
 * @author Anlycal<远>
 * @date 2019/6/19
 * @description ...
 */


class ChatAdapter(
    mChatDatas: List<ChatMessageEvent>
) : BaseQuickAdapter<ChatMessageEvent, BaseViewHolder>(mChatDatas) {

    private var mMineUserId: Int = SPUtils.getInstance().getInt(Config.Constant.DUODUO_USER_ID)
    private var mMineInfo: UserInfoEntity? = UserInfoManage.getInstance.mUserInfoEntity

    var onChatMessageListClickedListener: OnChatMessageListClickedListener? = null

    override fun getItemViewType(position: Int): Int {
        var mChatData: ChatMessageEvent = data[position]
        if (mChatData.messageType == BaseEvent.TYPE.TEXT.typeValue) {
            return if (TextUtils.equals(mMineUserId.toString(), mChatData.fromUserID)) {
                BaseEvent.TYPE.TEXT_MY.typeValue
            } else {
                BaseEvent.TYPE.TEXT.typeValue
            }
        }

        if (mChatData.messageType == BaseEvent.TYPE.RECORD_VOICE.typeValue) {
            return if (TextUtils.equals(mMineUserId.toString(), mChatData.fromUserID)) {
                BaseEvent.TYPE.RECORD_VOICE_MY.typeValue
            } else {
                BaseEvent.TYPE.RECORD_VOICE.typeValue
            }
        }

        if (mChatData.messageType == BaseEvent.TYPE.IMAGE.typeValue) {
            return if (TextUtils.equals(mMineUserId.toString(), mChatData.fromUserID)) {
                BaseEvent.TYPE.IMAGE_MY.typeValue
            } else {
                BaseEvent.TYPE.IMAGE.typeValue
            }
        }

        if (mChatData.messageType == BaseEvent.TYPE.RED_PACK.typeValue) {
            return if (TextUtils.equals(mMineUserId.toString(), mChatData.fromUserID)) {
                BaseEvent.TYPE.RED_PACK_MY.typeValue
            } else {
                BaseEvent.TYPE.RED_PACK.typeValue
            }
        }

        if (mChatData.messageType == BaseEvent.TYPE.CARD.typeValue) {
            return if (TextUtils.equals(mMineUserId.toString(), mChatData.fromUserID)) {
                BaseEvent.TYPE.CARD_MY.typeValue
            } else {
                BaseEvent.TYPE.CARD.typeValue
            }
        }

        if (mChatData.messageType == BaseEvent.TYPE.GROUP_INVITE.typeValue) {
            return if (TextUtils.equals(mMineUserId.toString(), mChatData.invitationPeople)) {
                BaseEvent.TYPE.GROUP_INVITE_MY.typeValue
            } else {
                BaseEvent.TYPE.GROUP_INVITE.typeValue
            }
        }
        return super.getItemViewType(position)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        this.mContext = parent.context
        this.mLayoutInflater = LayoutInflater.from(mContext)
        return when (viewType) {
            BaseEvent.TYPE.TEXT.typeValue -> ChatLeftTextViewHolder(getBaseLeftView(parent))
            BaseEvent.TYPE.TEXT_MY.typeValue -> ChatRightTextViewHolder(getBaseRightView(parent))
            BaseEvent.TYPE.IMAGE.typeValue -> ChatLeftImageViewHolder(getBaseLeftView(parent))
            BaseEvent.TYPE.IMAGE_MY.typeValue -> ChatRightImageViewHolder(getBaseRightView(parent))
            BaseEvent.TYPE.RECORD_VOICE.typeValue -> ChatLeftVoiceViewHolder(getBaseLeftView(parent))
            BaseEvent.TYPE.RECORD_VOICE_MY.typeValue -> ChatRightVoiceViewHolder(getBaseRightView(parent))
            BaseEvent.TYPE.RED_PACK.typeValue -> ChatLeftRedPackViewHolder(getBaseLeftView(parent))
            BaseEvent.TYPE.RED_PACK_MY.typeValue -> ChatRightRedPackViewHolder(getBaseRightView(parent))
            BaseEvent.TYPE.GROUP_INVITE.typeValue -> ChatLeftInviteViewHolder(getBaseLeftView(parent))
            BaseEvent.TYPE.GROUP_INVITE_MY.typeValue -> ChatRightInviteViewHolder(getBaseRightView(parent))
            else -> BaseLeftChatViewHolder(getBaseLeftView(parent))
        }
    }


    private fun getBaseLeftView(parent: ViewGroup): View {
        return mLayoutInflater.inflate(R.layout.item_chat_base_left_list, parent, false)
    }

    private fun getBaseRightView(parent: ViewGroup): View {
        return mLayoutInflater.inflate(R.layout.item_chat_base_right_list, parent, false)
    }


    override fun convert(helper: BaseViewHolder, item: ChatMessageEvent) {
        val mItemType: Int = getItemViewType(helper.adapterPosition)

        Log.e("yancy", "显示的消息：" + item.toString());

        if (helper is BaseLeftChatViewHolder) {
            showAvatarAndTime(helper.mIvAvatar, helper.mTvTime, item, helper.adapterPosition, false)
        } else {
            helper as BaseRightChatViewHolder
            showAvatarAndTime(helper.mIvAvatar, helper.mTvTime, item, helper.adapterPosition, true)
        }

        when (mItemType) {
            BaseEvent.TYPE.TEXT.typeValue -> {
                (helper as ChatLeftTextViewHolder).mTvContent.text = item.messageContent
                helper.mTvContent.setOnLongClickListener {
                    if (onChatMessageListClickedListener != null) {
                        onChatMessageListClickedListener!!.onTextLongClicked(
                            helper.adapterPosition,
                            item,
                            helper.mTvContent
                        )
                    }
                    false
                }
            }
            BaseEvent.TYPE.TEXT_MY.typeValue -> {
                (helper as ChatRightTextViewHolder).mTvContent.text =
                    item.messageContent
                helper.mTvContent.setOnLongClickListener {
                    if (onChatMessageListClickedListener != null) {
                        onChatMessageListClickedListener!!.onTextLongClicked(
                            helper.adapterPosition,
                            item,
                            helper.mTvContent
                        )
                    }
                    false
                }
            }

            BaseEvent.TYPE.IMAGE.typeValue -> {
                helper as ChatLeftImageViewHolder
                showImage(helper.mIvImage, helper.mInflateView, item, helper.adapterPosition)
            }
            BaseEvent.TYPE.IMAGE_MY.typeValue -> {
                helper as ChatRightImageViewHolder
                showImage(helper.mIvImage, helper.mInflateView, item, helper.adapterPosition)
            }
            BaseEvent.TYPE.RECORD_VOICE.typeValue -> showFriendVoice(helper as ChatLeftVoiceViewHolder, item)
            BaseEvent.TYPE.RECORD_VOICE_MY.typeValue -> showMineVoice(helper as ChatRightVoiceViewHolder, item)
            BaseEvent.TYPE.RED_PACK.typeValue -> {
                helper as ChatLeftRedPackViewHolder
                showRedPack(helper.mTvRedPackTitle, helper.mInflateView, item, helper.adapterPosition)
            }
            BaseEvent.TYPE.RED_PACK_MY.typeValue -> {
                helper as ChatRightRedPackViewHolder
                showRedPack(helper.mTvRedPackTitle, helper.mInflateView, item, helper.adapterPosition)
            }
            BaseEvent.TYPE.GROUP_INVITE.typeValue -> {
                helper as ChatLeftInviteViewHolder
                showInvite(helper.mInfoTv, helper.mIvImage, helper.mInflateView, item, helper.adapterPosition)
            }
            BaseEvent.TYPE.GROUP_INVITE_MY.typeValue -> {
                helper as ChatRightInviteViewHolder
                showInvite(helper.mInfoTv, helper.mIvImage, helper.mInflateView, item, helper.adapterPosition)
            }
        }
    }

    /**
     * 显示头像和时间
     */
    private fun showAvatarAndTime(
        mIvAvatar: CircleImageView,
        mTvTime: TextView,
        item: ChatMessageEvent,
        position: Int,
        isMine: Boolean
    ) {
        mTvTime.text = DateUtil.timeFormatText(item.createTime)
        if (!isMine) {
            if (!item.avatar.isNullOrBlank()) {
                ImageUtil.loadOriginalImage(mContext, item.avatar, mIvAvatar)
            }
        } else {
            if (!mMineInfo?.avatar.isNullOrEmpty()) {
                ImageUtil.loadOriginalImage(mContext, mMineInfo?.avatar, mIvAvatar)
            }
        }

        mIvAvatar.setOnClickListener {
            if (onChatMessageListClickedListener != null) {
                onChatMessageListClickedListener!!.onAvatarClicked(position, item)
            }
        }
    }

    /**
     * 显示红包
     */
    private fun showRedPack(mTvRedPackTitle: TextView, mRootView: View, item: ChatMessageEvent, position: Int) {
        mTvRedPackTitle.text = "恭喜发财"
        mRootView.setOnClickListener {
            if (onChatMessageListClickedListener != null) {
                onChatMessageListClickedListener!!.onRedPackClicked(position, item)
            }
        }
    }

    /**
     * 显示加群邀请
     */
    private fun showInvite(
        mInfoTv: TextView,
        avatarIv: ImageView,
        mRootView: View,
        item: ChatMessageEvent,
        position: Int
    ) {
        mInfoTv.text = "${item.nickname}邀请你加入群聊${item.invitationGroupName}，进入可查看详情。"
        ImageUtil.loadOriginalImage(mContext, item.invitationGroupAvatar, avatarIv)
        mRootView.setOnClickListener {
            Log.e("yancy","邀请你加入群聊")
            Log.e("yancy",mMineUserId.toString())
            Log.e("yancy",item.invitationPeople)
            Log.e("yancy","**********************")
            if (!TextUtils.equals(mMineUserId.toString(), item.invitationPeople)) {
                onChatMessageListClickedListener?.let {
                    it.onInviteClicked(position, item)
                }
            }
        }
    }

    /**
     * 显示图片
     */
    private fun showImage(mIvImage: RoundedImageView, mRootView: View, item: ChatMessageEvent, position: Int) {
        if (!item.messageContent.isNullOrBlank()) {
            ImageUtil.loadOriginalImage(mContext, item.messageContent, mIvImage)
        }

        mRootView.setOnClickListener {
            if (onChatMessageListClickedListener != null) {
                onChatMessageListClickedListener!!.onImageClicked(position, item)
            }
        }
    }

    /**
     * 显示朋友的语音
     */
    private fun showFriendVoice(holder: ChatLeftVoiceViewHolder, item: ChatMessageEvent) {
        showVoice(holder.mTvVoiceDuration, holder.mInflateView, item, holder.adapterPosition)
        holder.mIvUnReadPoint.visibility = if (item.voiceIsRead) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }

        holder.itemView.setOnClickListener {
            if (onChatMessageListClickedListener != null) {
                onChatMessageListClickedListener!!.onVoiceClicked(holder.adapterPosition, item)
            }
        }
    }

    /**
     * 显示自己的语音
     */
    private fun showMineVoice(holder: ChatRightVoiceViewHolder, item: ChatMessageEvent) {
        showVoice(holder.mTvVoiceDuration, holder.mInflateView, item, holder.adapterPosition)
    }

    private fun showVoice(mTvVoiceDuration: TextView, mLlVoiceRecordRoot: View, item: ChatMessageEvent, position: Int) {
        mTvVoiceDuration.text = "${item.voiceDuration}\""

        mLlVoiceRecordRoot.setOnClickListener {
            if (onChatMessageListClickedListener != null) {
                onChatMessageListClickedListener!!.onVoiceClicked(position, item)
            }
        }
    }

}