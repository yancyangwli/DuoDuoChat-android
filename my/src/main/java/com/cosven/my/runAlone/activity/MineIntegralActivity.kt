package com.cosven.socialim.ui.activity.me

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.view.View
import com.blankj.utilcode.util.SPUtils
import com.cosven.my.R
import com.cosven.my.runAlone.fragment.IntegralFragment
import com.flyco.tablayout.listener.OnTabSelectListener
import com.woniu.core.activities.BaseActivity
import com.woniu.core.api.Config
import com.woniu.core.utils.TabUtil
import kotlinx.android.synthetic.main.activity_mine_integral.*
import kotlinx.android.synthetic.main.my_title_layout.*

/**
 * 我的积分
 */
class MineIntegralActivity : BaseActivity() {
    private var mTitles: Array<String> = arrayOf("全部", "收入", "支出")

    private var mFlags: Array<String> = arrayOf("ALL", "INCOME", "PAY")


    private var mCurrentFragment: Fragment? = null
    override fun setContentViewId(): Int {
        return R.layout.activity_mine_integral
    }

    override fun initView() {
        setTitles(qmui_bar, "我的积分", true)
        mIntegralTv.text = SPUtils.getInstance().getString(Config.Constant.MINE_INTEGRAL)
        mTabLayout.setTabData(TabUtil.getTabEntities(mTitles, null, null))
    }

    override fun initData() {
        switchFragment(0)
        initEvent()
    }

    override fun onClick(v: View?) {

    }

    private fun initEvent(): Unit {
        mTabLayout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                switchFragment(position)
            }

            override fun onTabReselect(position: Int) {
            }
        })
    }

    private fun switchFragment(index: Int) {
        var mTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        var mFragment: Fragment? = supportFragmentManager.findFragmentByTag(mFlags[index])
        if (mFragment != null) {
            if (mCurrentFragment != null) {
                mTransaction.show(mFragment).hide(mCurrentFragment!!).commitNow()
            } else {
                mTransaction.show(mFragment).commitNow()
            }
        } else {
            mFragment = IntegralFragment.newInstance(if (index == 0) "" else if (index == 1) "income" else "cost")
            if (mCurrentFragment != null) {
                mTransaction.add(R.id.fl_content, mFragment, mFlags[index]).hide(mCurrentFragment!!).commitNow()
            } else {
                mTransaction.add(R.id.fl_content, mFragment, mFlags[index]).commitNow()
            }
        }
        mCurrentFragment = mFragment
    }
}