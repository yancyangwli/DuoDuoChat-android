package com.cosven.my.runAlone.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cosven.my.R
import com.woniu.core.bean.entity.CollectionEntity
import kotlinx.android.synthetic.main.item_collection_list.view.*

/**
 * @author Anlycal<远>
 * @date 2019/6/10
 * @description 我的收藏
 */


class CollectionAdapter(data:List<CollectionEntity>):BaseQuickAdapter<CollectionEntity,BaseViewHolder>(R.layout.item_collection_list,data) {
    override fun convert(helper: BaseViewHolder, item: CollectionEntity) {
        helper.setText(R.id.mTvNickname,"用户id${item.user_id}")
        helper.setText(R.id.mTvDate,item.created_at)
        helper.setText(R.id.mTvContent,item.content)

    }
}