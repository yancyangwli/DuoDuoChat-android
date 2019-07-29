package com.cosven.message.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cosven.message.R
import com.cosven.message.bean.RedPacketRecordItemBean
import com.woniu.core.utils.ImageUtil

/**
 * @author Anlycal<远>
 * @date 2019/5/24
 * @description ...
 */


class RedEnvelopeAdapter(layoutResId: Int) : BaseQuickAdapter<RedPacketRecordItemBean, BaseViewHolder>(layoutResId) {
    override fun convert(helper: BaseViewHolder?, item: RedPacketRecordItemBean) {
        helper?.let {
            it.setText(R.id.mNameTv, if (item.real_name.isNullOrBlank().not()) item.real_name else item.nickname)
                .setText(R.id.mNumberTv, "${item.integral}积分")
                .setText(R.id.mTimeTv, item.created_at)
            ImageUtil.loadOriginalImage(mContext, item.avatar, it.getView(R.id.mIvIcon))
        }
    }
}