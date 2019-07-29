package com.cosven.message.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.cosven.message.R
import com.woniu.core.bean.entity.RedPackInfoEntity
import com.woniu.core.bean.entity.SendPackUser
import com.woniu.core.dialog.BaseDialog
import com.woniu.core.utils.ImageUtil
import kotlinx.android.synthetic.main.pop_red_pack.*

/**
 * @author Anlycal<远>
 * @date 2019/6/27
 * @description ...
 */

class RedPackDialog(mContext: Context, packInfo: RedPackInfoEntity) : BaseDialog(mContext), View.OnClickListener {

    var mRootView: View = LayoutInflater.from(mContext).inflate(R.layout.pop_red_pack, null)
    var onRedPackOpenClickedListener: OnRedPackOpenClickedListener? = null
    var mPackInfoEntity: RedPackInfoEntity

    init {
        setContentView(mRootView)

        this.mPackInfoEntity = packInfo

        packInfo?.let {
            showPackInfo(it)
        }

//        mIvPackBg.setOnClickListener(this)
        mIvDelete.setOnClickListener(this)
        mIvRedOpen.setOnClickListener(this)
        mLookDetailTv.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.mIvDelete -> dismiss()
            R.id.mIvRedOpen -> if (onRedPackOpenClickedListener != null) {
                onRedPackOpenClickedListener!!.onRedPackOpen(mPackInfoEntity.redPack!!.red_pack_id)
            }
            R.id.mLookDetailTv -> if (onRedPackOpenClickedListener != null) {
                onRedPackOpenClickedListener!!.onGotoDetail()
            }
        }
    }

    /**
     * 显示红包信息
     */
    private fun showPackInfo(info: RedPackInfoEntity) {
        var mSendUser: SendPackUser? = info.redPack?.user
        mSendUser?.let { user ->
            if (!user.avatar.isNullOrEmpty()) {
                ImageUtil.loadOriginalImage(mContext, user.avatar, mIvAvatar)
            }
            mIvNickname.text = user.nickname
        }

        if (!info.redPack?.wishes.isNullOrEmpty()) {
            mTvWishes.text = info.redPack?.wishes
        }
    }

    public fun showNonePackInfo() {
        mTvWishes.text = "手慢了，红包抢完"
        mLookDetailTv.visibility = View.VISIBLE
        mIvRedOpen.visibility = View.GONE
    }

    interface OnRedPackOpenClickedListener {
        fun onRedPackOpen(redPackId: Int)

        fun onGotoDetail()
    }
}
