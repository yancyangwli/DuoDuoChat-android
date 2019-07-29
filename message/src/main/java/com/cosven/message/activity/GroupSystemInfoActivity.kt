package com.cosven.message.activity

import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import com.cosven.message.R
import com.cosven.message.adapter.GroupSystemInfoAdapter
import com.woniu.core.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_group_system_info.*
import kotlinx.android.synthetic.main.message_titile_layout.*
import org.jetbrains.anko.startActivity

/**
 * @author Anlycal<远>
 * @date 2019/5/30
 * @description 群系统消息
 */


class GroupSystemInfoActivity:BaseActivity(){
    private lateinit var mGroupSystemInfoAdapter: GroupSystemInfoAdapter

    private var arrays= Array<String>(20,{
        it.toString()
    })

    override fun setContentViewId(): Int {
        return R.layout.activity_group_system_info
    }

    override fun initView() {
        setTitles(qmui_bar,"群系统消息",true)

        mRvGroupSystemInfoList.layoutManager = LinearLayoutManager(this)

        mGroupSystemInfoAdapter = GroupSystemInfoAdapter(arrays.toMutableList())
        var mHeader:View = LayoutInflater.from(this).inflate(R.layout.layout_shadow_header,mRvGroupSystemInfoList,false)
        mGroupSystemInfoAdapter.setHeaderView(mHeader)

        mRvGroupSystemInfoList.adapter = mGroupSystemInfoAdapter

        initEvent()
    }

    override fun initData() {

    }

    private fun initEvent(){
        mGroupSystemInfoAdapter.setOnItemClickListener { adapter, view, position ->
            startActivity<GroupInfoActivity>()
        }
    }
}