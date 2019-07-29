package com.cosven.message.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cosven.message.R
import com.woniu.core.bean.Member
import com.woniu.core.utils.ImageUtil
import de.hdodenhof.circleimageview.CircleImageView

/**
 * @author Anlycal<远>
 * @date 2019/5/28
 * @description ...
 */


class AvatarListAdapter(resId: Int) : BaseQuickAdapter<Member, BaseViewHolder>(resId) {

    override fun convert(helper: BaseViewHolder, item: Member) {

        ImageUtil.loadOriginalImage(mContext, item.user.avatar, helper.itemView as CircleImageView, R.mipmap.icon_default_head)
    }
}