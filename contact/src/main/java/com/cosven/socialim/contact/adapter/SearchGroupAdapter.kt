package com.cosven.socialim.contact.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cosven.socialim.contact.R
import com.woniu.core.bean.entity.SearchGroupsData
import com.woniu.core.utils.ImageUtil
import de.hdodenhof.circleimageview.CircleImageView

class SearchGroupAdapter(data: List<SearchGroupsData>) :
    BaseQuickAdapter<SearchGroupsData, BaseViewHolder>(R.layout.contact_search_item, data) {

    override fun convert(helper: BaseViewHolder, item: SearchGroupsData) {
        ImageUtil.loadOriginalImage(mContext, item.group_avatar, helper.getView<CircleImageView>(R.id.mIvAvatar))

        helper.setText(
            R.id.mTvNickname, if (!item.group_name.isNullOrBlank()) {
                item.group_name
            } else {
                ""
            }
        )

    }
}