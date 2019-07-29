package com.cosven.my.runAlone.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cosven.my.R
import com.woniu.core.bean.entity.IntegralRecordBean

/**
 * 积分
 */

class IntergralAdapter : BaseQuickAdapter<IntegralRecordBean, BaseViewHolder> {
    constructor(resId: Int) : super(resId) {
    }

    override fun convert(helper: BaseViewHolder, item: IntegralRecordBean) {
        helper.setText(R.id.mActionTv, item.desc)
            .setText(R.id.mNumberTv, item.integral)
    }
}