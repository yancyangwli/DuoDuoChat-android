package com.cosven.message.adapter

import android.view.View
import android.widget.CheckBox
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cosven.message.R
import com.luck.picture.lib.tools.ScreenUtils
import com.woniu.core.bean.Album
import com.woniu.core.utils.ImageUtil

/**
 * @author Anlycal<远>
 * @date 2019/5/28
 * @description ...
 */


class GroupAlbumAdapter(resId: Int) : BaseQuickAdapter<Album, BaseViewHolder>(resId) {

    var showCheckView: Boolean = false//是否显示checkView
    var selectList: MutableList<Album> = ArrayList()

    override fun convert(helper: BaseViewHolder, item: Album) {
        var screenWidth: Int = ScreenUtils.getScreenWidth(mContext)

        helper.itemView.layoutParams.height = screenWidth / 3

        helper.getView<CheckBox>(R.id.mCbSelect).run {
            visibility = if (showCheckView) {
                View.VISIBLE
            } else {
                View.GONE
            }
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) selectList.add(item) else selectList.remove(item)
            }
        }
        ImageUtil.loadOriginalImage(mContext, item.image, helper.getView(R.id.mIvAlbum))
    }
}