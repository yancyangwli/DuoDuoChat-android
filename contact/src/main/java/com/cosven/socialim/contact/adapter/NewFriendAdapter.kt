package com.cosven.socialim.contact.adapter

import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.TextView
import cn.jiguang.imui.view.RoundTextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cosven.socialim.contact.R
import com.woniu.core.bean.entity.ApplyFriendEntity
import com.woniu.core.utils.ImageUtil
import kotlinx.android.synthetic.main.contact_add_friend_layout.*

/**
 * @author Anlycal<远>
 * @date 2019/5/30
 * @description ...
 */


class NewFriendAdapter(data: List<ApplyFriendEntity>) :
    BaseQuickAdapter<ApplyFriendEntity, BaseViewHolder>(R.layout.contact_new_friend_item, data) {

    override fun convert(helper: BaseViewHolder, item: ApplyFriendEntity) {

        if (item.apply_type == ApplyFriendEntity.FRIEND) {//申请类型(1好友|2群组)
            if (item.object_gender == 1) {
                setInfo(
                    helper.getView(R.id.mTvInfo),
                    R.mipmap.ic_male_white,
                    mContext.resources.getColor(R.color.color_sex_male)
                )
                helper.setText(R.id.mTvRemarks, if (item.remarks.isNullOrBlank()) "请求添加好友" else item.remarks)
                ImageUtil.loadOriginalImage(mContext, item.object_avatar, helper.getView(R.id.mIvAvatar))

                helper.setText(
                    R.id.mTvNickname, if (!item.object_nick.isNullOrBlank()) {
                        item.object_nick
                    } else {
                        "用户/群id${item.object_id}"
                    }
                )
            } else if (item.object_gender == 2) {
                setInfo(
                    helper.getView(R.id.mTvInfo),
                    R.mipmap.ic_female_white,
                    mContext.resources.getColor(R.color.color_sex_female),
                    "女"
                )
                helper.setText(R.id.mTvRemarks, "请求加入（${item.object_nick}）群")
                ImageUtil.loadOriginalImage(mContext, item.apply_user_avatar, helper.getView(R.id.mIvAvatar))

                helper.setText(
                    R.id.mTvNickname, if (!item.apply_user_nick.isNullOrBlank()) {
                        item.apply_user_nick
                    } else {
                        "用户/群id${item.object_id}"
                    }
                )
            }
        } else {
            helper.getView<View>(R.id.mTvInfo).visibility = View.GONE
        }
        setFriendStatus(helper, item.apply_status)
    }

    private fun setInfo(mTvInfo: RoundTextView, sexImg: Int, bgColor: Int, sexStr: String = "男") {
        mTvInfo.visibility = View.VISIBLE
        mTvInfo.setCompoundDrawablesWithIntrinsicBounds(
            mContext.resources.getDrawable(sexImg),
            null,
            null,
            null
        )
        mTvInfo.text = sexStr
        mTvInfo.setBgColor(bgColor)
    }

    private fun setFriendStatus(helper: BaseViewHolder, flag: Int) {
        var mBtnStatus: Button = helper.getView(R.id.mBtnStatus)

        if (flag != ApplyFriendEntity.PENDING) {
            mBtnStatus.setTextColor(Color.parseColor("#333333"))
            mBtnStatus.setBackgroundColor(Color.WHITE)
            if (flag == ApplyFriendEntity.AGREE) {
                mBtnStatus.text = "已同意"
            } else {
                mBtnStatus.text = "已拒绝"
            }
        } else {
            mBtnStatus.setTextColor(Color.WHITE)
            mBtnStatus.setBackgroundResource(R.drawable.shape_corner)
            mBtnStatus.text = "同意"
        }
    }
}