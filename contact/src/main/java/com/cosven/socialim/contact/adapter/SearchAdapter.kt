package com.cosven.socialim.contact.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cosven.socialim.contact.R
import com.woniu.core.bean.entity.SearchUserData
import com.woniu.core.bean.entity.UserInfoEntity
import com.woniu.core.utils.ImageUtil
import de.hdodenhof.circleimageview.CircleImageView

class SearchAdapter(data: List<SearchUserData>) :
    BaseQuickAdapter<SearchUserData, BaseViewHolder>(R.layout.contact_search_item, data) {

    override fun convert(helper: BaseViewHolder, item: SearchUserData) {
        if (!item.avatar.isNullOrBlank()) {
            ImageUtil.loadOriginalImage(mContext, item.avatar, helper.getView<CircleImageView>(R.id.mIvAvatar))
        }

        helper.setText(
            R.id.mTvNickname, if (!item.nickname.isNullOrBlank()) {
                item.nickname
            } else {
                ""
            }
        )

    }
}