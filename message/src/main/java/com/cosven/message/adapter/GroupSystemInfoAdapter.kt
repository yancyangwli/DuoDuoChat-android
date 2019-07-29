package com.cosven.message.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cosven.message.R
import com.woniu.core.utils.ImageUtil

/**
 * @author Anlycal<远>
 * @date 2019/5/30
 * @description ...
 */


class GroupSystemInfoAdapter(data:List<String>):BaseQuickAdapter<String,BaseViewHolder>(R.layout.message_item_group_system_info,data) {
    override fun convert(helper: BaseViewHolder, item: String?) {
        var imageUrl:String

        if (helper.adapterPosition % 5 == 1){

            imageUrl = "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=2635014154,1489762936&fm=26&gp=0.jpg"
        }else if (helper.adapterPosition % 5 == 2){
            imageUrl = "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=490621,3964328649&fm=26&gp=0.jpg"
        }else if (helper.adapterPosition % 5 == 3){
            imageUrl = "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3620554730,1606702177&fm=26&gp=0.jpg"
        }else if (helper.adapterPosition % 5 == 4){
            imageUrl = "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=607545086,2050719288&fm=26&gp=0.jpg"
        }else{
            imageUrl = "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=727550015,221149324&fm=26&gp=0.jpg"
        }
        ImageUtil.loadOriginalImage(mContext,imageUrl,helper.getView(R.id.mIvGroupAvatar))
    }
}