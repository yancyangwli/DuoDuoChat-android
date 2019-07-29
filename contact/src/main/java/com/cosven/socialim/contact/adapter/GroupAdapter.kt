package com.cosven.socialim.contact.adapter

import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.CheckBox
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cosven.socialim.contact.R
import com.woniu.core.bean.entity.GroupEntity
import com.woniu.core.utils.ImageUtil

/**
 * adapter实例
 */
class GroupAdapter(data: List<GroupEntity>?) :
    BaseQuickAdapter<GroupEntity, BaseViewHolder>(R.layout.contact_group_item, data) {
    var showCheckBox: Boolean = false

    override fun convert(helper: BaseViewHolder, item: GroupEntity?) {
        if (showCheckBox) {
            helper.getView<CheckBox>(R.id.mCbSelect).visibility = View.VISIBLE
        } else {
            helper.getView<CheckBox>(R.id.mCbSelect).visibility = View.GONE
        }

//        var avatar: String

        helper.setText(R.id.mTvName, item!!.group_name)

//        Log.e("avatar", "item!!.group_name=" + item.group_avatar)
//        avatar = if (!TextUtils.isEmpty(item.group_avatar)) {
//            item.group_avatar
//        } else if (helper.adapterPosition % 3 == 1) {
//            "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1167697489,2400238751&fm=27&gp=0.jpg"
//        } else if (helper.adapterPosition % 3 == 2) {
//            "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2040473605,61100519&fm=27&gp=0.jpg"
//        } else {
//            "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3322402162,487439063&fm=27&gp=0.jpg"
//        }
        ImageUtil.loadOriginalImage(mContext, item.group_avatar, helper.getView(R.id.mIvAvatar))
    }
}