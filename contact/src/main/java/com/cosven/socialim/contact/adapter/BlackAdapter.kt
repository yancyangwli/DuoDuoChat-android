package com.cosven.socialim.contact.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cosven.socialim.contact.R
import com.woniu.core.bean.entity.ContactEntity
import com.woniu.core.utils.ImageUtil

class BlackAdapter(list: List<ContactEntity>):BaseQuickAdapter<ContactEntity,BaseViewHolder>(R.layout.contact_black_item,list) {
    override fun convert(helper: BaseViewHolder, item: ContactEntity) {

        if (!item.friend_avatar.isNullOrBlank()) {
            ImageUtil.loadOriginalImage(mContext, item.friend_avatar, helper.getView(R.id.mIvAvatar))
        }

        if (!item.friend_remark.isNullOrBlank()){
            helper.setText(R.id.mTvNickname,item.friend_remark)
        }
        helper.addOnClickListener(R.id.mLlBlackRoot)
        helper.addOnClickListener(R.id.mBtnDelete)
    }
}