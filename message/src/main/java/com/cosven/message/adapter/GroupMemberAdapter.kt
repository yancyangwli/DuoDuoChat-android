package com.cosven.message.adapter

import android.graphics.Color
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import cn.jiguang.imui.view.RoundTextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cosven.message.R
import com.woniu.core.bean.GroupMember
import com.woniu.core.utils.ImageUtil
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.message_item_group_member.view.*

/**
 * @author Anlycal<远>
 * @date 2019/5/28
 * @description 群成员
 */

class GroupMemberAdapter(resId: Int) : BaseQuickAdapter<GroupMember, BaseViewHolder>(resId) {

    var showSelectCheck: Boolean = false//判断是否批量选择
    var managerNumber: Int = 0

    override fun convert(helper: BaseViewHolder, item: GroupMember) {
        if (showSelectCheck) {
            if (helper.adapterPosition != 1) {
                helper.getView<CheckBox>(R.id.mCbSelect).visibility = View.VISIBLE
            } else {
                helper.getView<CheckBox>(R.id.mCbSelect).visibility = View.GONE
            }
        } else {
            helper.getView<CheckBox>(R.id.mCbSelect).visibility = View.GONE
        }

        var mTvType = helper.getView<TextView>(R.id.mTvType)
        when {
            helper.adapterPosition == 1 -> {
                mTvType.visibility = View.VISIBLE
                mTvType.text = "群主、管理员(${managerNumber}人)"
            }
            helper.adapterPosition == managerNumber + 1 -> {
                mTvType.visibility = View.VISIBLE
                mTvType.text = "普通成员(${data.size - managerNumber}人)"
            }
            else -> mTvType.visibility = View.GONE
        }

        when {
            helper.adapterPosition == 1 -> setFlagColor(helper, "#FFCA24", "群主")
            helper.adapterPosition < managerNumber + 1 -> setFlagColor(helper, "#14E799", "管理员")
            else -> setFlagColor(helper, "#1AD5F3", "普通成员")
        }

        ImageUtil.loadOriginalImage(mContext, item.user.avatar, helper.getView(R.id.mIvAvatar))
        helper.addOnClickListener(R.id.mIvAvatar)
            .addOnClickListener(R.id.mLlMember)
            .setText(
                R.id.mNameTv,
                if (item.group_nickname.isNullOrBlank().not()) item.group_nickname else item.user.real_name
            )
    }

    private fun setFlagColor(helper: BaseViewHolder, color: String, flag: String) {
        var mTvFlag = helper.getView<RoundTextView>(R.id.mTvFlag)
        mTvFlag.text = flag
        mTvFlag.setBgColor(Color.parseColor(color))
    }
}