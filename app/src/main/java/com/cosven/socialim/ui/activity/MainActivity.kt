package com.cosven.socialim.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ashokvarma.bottomnavigation.BottomNavigationItem
import com.blankj.utilcode.util.SPUtils
import com.chat.moduledynamic.ui.fragment.DynamicFragment
import com.cosven.my.runAlone.activity.LoginActivity
import com.cosven.socialim.R
import com.cosven.socialim.ui.fragment.FragmentFactory
import com.cosven.socialim.ui.fragment.MyFragment
import com.dktlh.ktl.provider.router.RouterPath
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.tools.PictureFileUtils
import com.woniu.core.activities.BaseActivity
import com.woniu.core.api.Config
import com.woniu.core.fragment.BaseFragment
import com.woniu.core.manage.UserInfoManage
import com.woniu.core.xmpp.rxbus.RxBus
import com.woniu.core.xmpp.rxbus.event.ChatMessageEvent
import org.jetbrains.anko.startActivity

/**
 * 主界面
 */
@Route(path = RouterPath.MainCenter.PATH_MAIN)
class MainActivity : BaseActivity() {

    lateinit var flRoot: FrameLayout
    lateinit var botton_navigation: BottomNavigationBar
    private var fakeStatusBar: View? = null

    private fun initFragment() {

        receiveChatMessage()

        val list = supportFragmentManager.fragments
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        if (list != null && list.size > 0) {
            // 说明之前有缓存frgamtn，处理fragment重影的问题
            for (i in list.indices) {
                fragmentTransaction.remove(list[i])
            }
            fragmentTransaction.commit()
        }

        val fragment = FragmentFactory.getFragement(0)
        val transaction = supportFragmentManager.beginTransaction()
        val add = transaction.add(R.id.fl_root, fragment, "0")
        transaction.commit()

    }

    private fun initBottomNavigationBar() {
        botton_navigation.setMode(BottomNavigationBar.MODE_FIXED)
        botton_navigation.addItem(BottomNavigationItem(R.mipmap.icon_msg, "消息"))
        botton_navigation.addItem(BottomNavigationItem(R.mipmap.icon_contace, "联系人"))
        botton_navigation.addItem(BottomNavigationItem(R.mipmap.icon_dongtai, "动态"))
        botton_navigation.addItem(BottomNavigationItem(R.mipmap.icon_mine, "我的"))

        //        botton_navigation.setActiveColor(R.color.app_color_blue);
        botton_navigation.initialise()

        botton_navigation.setTabSelectedListener(object : BottomNavigationBar.OnTabSelectedListener {
            override fun onTabSelected(position: Int) {
                //点击的时候fragment要轮番切换
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                val fragment = FragmentFactory.getFragement(position)
                if (position == 2) {
                    var mDF: DynamicFragment = (fragment as DynamicFragment);
                    if (mDF.mNavigationBar == null) {
                        mDF.mNavigationBar = botton_navigation
                    }
                }
                if (fragment.isAdded()) {
                    fragmentTransaction.show(fragment).commit()
                } else {
                    fragmentTransaction.add(R.id.fl_root, fragment, position.toString() + "").commit()
                }

            }

            override fun onTabUnselected(position: Int) {  // 也就是上一个
                // 点击下一个页面 上一个页面需要隐藏
                supportFragmentManager.beginTransaction().hide(FragmentFactory.getFragement(position)).commit()
            }

            override fun onTabReselected(position: Int) {

            }
        })
    }

    override fun setContentViewId(): Int {
        return R.layout.activity_main
    }

    override fun initView() {

        Log.e("yanyc", "USERINFO $ : "+UserInfoManage.getInstance.mUserInfoEntity!!.toString());

        var token: String = SPUtils.getInstance().getString(Config.Constant.DUODUO_TOKEN)

        if (token.isNullOrBlank()) {
            startActivity<LoginActivity>()
            finishs()
            return
        }

        Log.i("FAN", "token---->${token}")

        botton_navigation = findViewById(R.id.botton_navigation)
        flRoot = findViewById(R.id.fl_root)
        initBottomNavigationBar()
        initFragment()

        fakeStatusBar = findViewById(R.id.fake_status_bar)
//        setStatusView()
    }

    //接收聊天信息
    @SuppressLint("CheckResult")
    private fun receiveChatMessage() {
        RxBus.getInstance()
            .toObserverable(ChatMessageEvent::class.java)
            .subscribe {

            }
    }


    override fun initData() {
        // 下载并初始化Liveness
        //        initSDK();
        //  申请权限
//        PermissionHelper.init(activity, PermissionConstants.CAMERA, PermissionConstants.STORAGE, PermissionConstants.LOCATION, PermissionConstants.PHONE)
    }


    private fun setStatusView() {
        fakeStatusBar!!.visibility = View.VISIBLE
        val statusBarHeight = getStatusBarHeight(context)
        //设置假状态栏高度
        val statusBarParams = fakeStatusBar!!.layoutParams as LinearLayout.LayoutParams
        statusBarParams.height = statusBarHeight
        fakeStatusBar!!.layoutParams = statusBarParams
    }

    //获得状态栏的高度
    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resId > 0) {
            result = context.resources.getDimensionPixelOffset(resId)
        }
        return result
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null && resultCode == Activity.RESULT_OK) {
            val selectList: MutableList<LocalMedia> = PictureSelector.obtainMultipleResult(data)
            when (requestCode) {
                DynamicFragment.SELECT_BACK -> {
                    var mFragment: BaseFragment = FragmentFactory.getFragement(2)
                    mFragment?.let {
                        (it as DynamicFragment).setBackgroundImage(selectList[0].compressPath)
                    }
                }
                MyFragment.SELECT_MINE_BG -> {
                    var mFragment: BaseFragment = FragmentFactory.getFragement(3)
                    mFragment?.let {
                        (it as MyFragment).setMineBgImages(selectList[0].compressPath)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PictureFileUtils.deleteCacheDirFile(this)
    }

}
