package com.cosven.socialim.contact.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.cosven.socialim.contact.R
import com.woniu.core.bean.entity.ContactEntity
import com.woniu.core.utils.ImageUtil
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_contact_list.view.*


class ContactSortAdapter : RecyclerView.Adapter<ContactSortAdapter.ViewHolder>() {

    var showCheckBox: Boolean = false//是否显示checkbox

    var mSelectedList: MutableList<ContactEntity> = ArrayList()

    private var mInflater: LayoutInflater? = null
    private var mData: List<ContactEntity>? = null
    private var mContext: Context? = null
    private var mHeaderView: View? = null
    private var headerCount = 0
    val TYPE_HEADER = 0  //说明是带有Header的
    val TYPE_FOOTER = 1  //说明是带有Footer的
    val TYPE_NORMAL = 2  //说明是不带有header和footer的

    fun setRes(context: Context, data: List<ContactEntity>) {
        mInflater = LayoutInflater.from(context)
        mData = data
        mContext = context
    }

    fun setHeaderView(headerView: View?) {
        mHeaderView = headerView
        headerCount++
        notifyItemInserted(0)
    }

    override fun getItemViewType(position: Int): Int {
        if (mHeaderView == null) {
            return TYPE_NORMAL
        }
        if (position == 0) {
            //第一个item应该加载Header
            return TYPE_HEADER
        }
        if (position == itemCount - 1) {
            //最后一个,应该加载Footer
            return TYPE_FOOTER
        }
        return TYPE_NORMAL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactSortAdapter.ViewHolder {

        if (mHeaderView != null && viewType == TYPE_HEADER) {
            return ViewHolder(mHeaderView!!)
        }

        val view: View = mInflater!!.inflate(R.layout.item_contact_list, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.tvTag = view.findViewById(R.id.tag)
        viewHolder.tvName = view.findViewById(R.id.name)
        viewHolder.ivAvatar = view.findViewById(R.id.mIvAvatar)
        viewHolder.checkBox = view.findViewById(R.id.mCbSelect)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ContactSortAdapter.ViewHolder, position: Int) {

        if (getItemViewType(position) == TYPE_HEADER) {
            return
        }

        val section: Int = getSectionForPosition(position)
        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        mData?.let {
            it[position - headerCount].apply {
                //头部局PO = 0    默认是从0  so -> 判断首字母的 开始的position - 1 == getPositionForSection(section)
                holder.run {
                    tvTag?.run {
                        if (position - headerCount == getPositionForSection(section)) {
                            visibility = View.VISIBLE
                            text = friend_group
                        } else {
                            visibility = View.GONE
                        }
                    }
                    tvName?.text = friend_remark

                    mOnItemClickListener?.run {
                        itemView.mLlInfoRoot.setOnClickListener {
                            mOnItemClickListener!!.onItemClick(
                                holder.itemView,
                                position
                            )
                        }
                    }

                    ImageUtil.loadOriginalImage(mContext!!, friend_avatar, ivAvatar, R.mipmap.icon_default_head)

                }
                holder.checkBox?.run {
                    visibility = if (showCheckBox) View.VISIBLE else View.GONE
                    setOnCheckedChangeListener { buttonView, isChecked ->
                        if (isChecked) mSelectedList.add(this@apply)
                        else mSelectedList.remove(this@apply)
                    }
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return mData!!.size + headerCount
    }

    //**********************itemClick************************
    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    private var mOnItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(mOnItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener
    }
    //**************************************************************

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var tvTag: TextView? = null
        internal var tvName: TextView? = null
        internal var ivAvatar: CircleImageView? = null
        internal var checkBox: CheckBox? = null
    }

    /**
     * 提供给Activity刷新数据
     * @param list
     */
    fun updateList(list: List<ContactEntity>) {
        this.mData = list
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Any {
        return mData!![position]
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的char ascii值
     */
    fun getSectionForPosition(position: Int): Int {
        val c = mData?.let {
            it[position - headerCount].friend_group
        }
        var cha: CharArray = c!!.toCharArray()
        return cha[0].toInt()
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    fun getPositionForSection(section: Int): Int {
        for (i in 0 until itemCount) {
            if (i < mData!!.size) {
//                val sortStr = mData!![i].letters
                val sortStr = mData!![i].friend_group
                val firstChar = sortStr!!.toUpperCase()[0]
                if (firstChar.toInt() == section) {
                    return i
                }
            }
        }
        return -1
    }
}