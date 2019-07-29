package com.cosven.socialim.ui.fragment

import android.content.Intent
import android.support.v4.widget.NestedScrollView
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.cosven.message.activity.RedEnvelopeActivity
import com.cosven.my.runAlone.activity.EditInfoActivity
import com.cosven.my.runAlone.activity.MyCollectionActivity
import com.cosven.socialim.R
import com.cosven.socialim.ui.activity.me.MineIntegralActivity
import com.cosven.socialim.ui.activity.me.QrCodeActivity
import com.cosven.socialim.ui.activity.me.SettingActivity
import com.dktlh.ktl.provider.router.RouterPath
import com.google.gson.reflect.TypeToken
import com.scwang.smartrefresh.layout.api.RefreshFooter
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener
import com.scwang.smartrefresh.layout.util.DensityUtil
import com.woniu.core.activities.BaseActivity
import com.woniu.core.api.Config
import com.woniu.core.bean.BaseReq
import com.woniu.core.bean.entity.FileLoadEntity
import com.woniu.core.bean.entity.UserInfoEntity
import com.woniu.core.fragment.BaseFragment
import com.woniu.core.manage.UserInfoManage
import com.woniu.core.utils.GsonUtil
import com.woniu.core.utils.HttpUtil
import com.woniu.core.utils.ImageUtil
import com.woniu.core.utils.PictureUtil
import com.woniu.core.xmpp.rxbus.RxBus
import com.zhouyou.http.model.HttpParams
import com.zhuanxu.eclipse.aop.annotation.SingleClick
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragament_my.view.*
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import java.io.File
import java.lang.reflect.Type
import java.util.*

/**
 * 我的
 */
class MyFragment : BaseFragment() {
    companion object {
        val SELECT_MINE_BG: Int = 7001//选择主页背景图片
    }

    private var mOffset = 0
    private var mScrollY = 0

    private var mPictureUtil: PictureUtil? = null

    private var mHttpUtil: HttpUtil = HttpUtil(context)

    private lateinit var mIvAvatar: CircleImageView
    private lateinit var mIvSex: ImageView
    private lateinit var mTvNickname: TextView
    private lateinit var mTvUserPhone: TextView
    private lateinit var mTvAge: TextView
    private lateinit var mTvArea: TextView
    private lateinit var mTvBirth: TextView
    private lateinit var mTvSignture: TextView

    private lateinit var mUserInfoDispose: Disposable

    override fun setView(): Int {

        return R.layout.fragament_my
    }

    override fun initView() {
        super.initView()

        mIvAvatar = inflate.findViewById(R.id.mIvAvatar)
        mIvSex = inflate.findViewById(R.id.mIvSex)
        mTvNickname = inflate.findViewById(R.id.mTvNickname)
        mTvUserPhone = inflate.findViewById(R.id.mTvUserPhone)
        mTvAge = inflate.findViewById(R.id.mTvAge)
        mTvArea = inflate.findViewById(R.id.mTvArea)
        mTvBirth = inflate.findViewById(R.id.mTvBirth)
        mTvSignture = inflate.findViewById(R.id.mTvSignture)

        initEvent()

        if (UserInfoManage.getInstance.mUserInfoEntity == null) {
            loadUserinfo(false)
        } else {
            showUserInfo(UserInfoManage.getInstance.mUserInfoEntity!!)
        }
    }

    private fun initEvent() {
        inflate.refreshLayout.setOnMultiPurposeListener(object : SimpleMultiPurposeListener() {
            override fun onRefresh(refreshLayout: RefreshLayout) {
//                refreshLayout.finishRefresh(3000)
                loadUserinfo(true)
            }

            override fun onFooterReleasing(
                footer: RefreshFooter?,
                percent: Float,
                offset: Int,
                footerHeight: Int,
                extendHeight: Int
            ) {
                mOffset = offset / 2
                inflate.parallax.translationY = (mOffset - mScrollY).toFloat();
            }

            override fun onHeaderPulling(
                header: RefreshHeader?,
                percent: Float,
                offset: Int,
                headerHeight: Int,
                extendHeight: Int
            ) {
                mOffset = offset / 2
                inflate.parallax.translationY = (mOffset - mScrollY).toFloat();
            }
        })

        inflate.scrollView.setOnScrollChangeListener(object : NestedScrollView.OnScrollChangeListener {
            private var lastScrollY = 0
            private val h = DensityUtil.dp2px(170f)
            override fun onScrollChange(
                v: NestedScrollView,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int
            ) {
                var scrollY = scrollY
                if (lastScrollY < h) {
                    scrollY = Math.min(h, scrollY)
                    mScrollY = if (scrollY > h) h else scrollY
                    inflate.parallax.translationY = (mOffset - mScrollY).toFloat()
                }
                lastScrollY = scrollY
            }
        })

        mUserInfoDispose = RxBus.getInstance()
            .toObserverable(UserInfoEntity::class.java)
            .subscribe {
                showUserInfo(it)
            }
    }

    override fun addAction() {
        inflate.mViewChangeBg.setOnClickListener(this)
        inflate.rl_setting.setOnClickListener(this)
        inflate.mLlIntegralRoot.setOnClickListener(this)
        inflate.mLlDynamicRoot.setOnClickListener(this)
        inflate.mRlCollectionRoot.setOnClickListener(this)
        inflate.findViewById<View>(R.id.mLlQrCode).setOnClickListener(this)
        inflate.findViewById<View>(R.id.mLlEditInfo).setOnClickListener(this)
    }

    @SingleClick
    override fun onClick(v: View?) {
        v?.run {
            when (id) {
                R.id.rl_setting -> {
                    startActivity<SettingActivity>()
                }
                R.id.mLlIntegralRoot -> {
                    startActivity<MineIntegralActivity>()
                }
                R.id.mLlDynamicRoot -> {
                    ARouter
                        .getInstance()
                        .build(RouterPath.DynamicCenter.PATH_DYNAMIC_LIST)
                        .withInt("LOOK_TYPE", Config.Constant.LOOK_MY_MOMENT)
                        .navigation()
                }
                R.id.mRlCollectionRoot -> {
//                    startActivity<RedEnvelopeActivity>()
                    startActivity<MyCollectionActivity>()
                }
                R.id.mViewChangeBg -> {//设置背景
                    selectImageBg()
                }
                R.id.mLlQrCode -> {
                    UserInfoManage.getInstance.mUserInfoEntity?.let {
                        startActivity<QrCodeActivity>(Pair("USER_INFO", it))
                    }
                }
                R.id.mLlEditInfo -> {
                    val intent = Intent(activity, EditInfoActivity::class.java)
                    intent.putExtra("TYPE", 1)
                    startActivity(intent)
                }
                else -> {

                }
            }
        }
    }

    /**
     * 设置主页背景图片
     * @filePath 文件路径
     */
    fun setMineBgImages(filePath: String) {
        if (filePath.isNotEmpty()) {
            var personal_cover = Config.FolderName.personal_cover
            var mFile: File = File(filePath)
            ImageUtil.loadFileImage(context, mFile, inflate.parallax)

            mHttpUtil?.setOnRequestCallBack { code, data ->
                (context as BaseActivity).dismissProgressDialog()
                if (code == -1) {
                    ToastUtils.showLong(data)
                    return@setOnRequestCallBack
                }
                var mFileLoadEntity: FileLoadEntity = GsonUtil.getGson().fromJson(data, FileLoadEntity::class.java)
                if (!mFileLoadEntity.url.isNullOrEmpty()) {
                    editInfo("/$personal_cover")
                } else {
                    toast("设置失败")
                }
            }?.postFile(Config.API.image_url, personal_cover, filePath, httpParams)
        }
    }

    /**
     * 更新个人中心封面图
     */
    private fun editInfo(personal_page_cover: String) {
        var mParams: HttpParams = httpParams
        mParams.put("personal_page_cover", personal_page_cover)
        (activity as BaseActivity).showProgressDialog(getString(R.string.waiting))
        mHttpUtil.setOnRequestCallBack { code, data ->
            (activity as BaseActivity).dismissProgressDialog()
            if (code == -1) {
                ToastUtils.showLong(data)
                return@setOnRequestCallBack
            }

            var mReq: BaseReq<*> = GsonUtil.getGson().fromJson(data, BaseReq::class.java)
            ToastUtils.showLong(mReq.msg)

            if (TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {
                var mUser: UserInfoEntity = UserInfoManage.getInstance.mUserInfoEntity!!
                loadUserinfo(false)
//                mUser.personal_page_cover?.let {
//                    ImageUtil.loadOriginalImage(
//                        context,
//                        Config.API.image_url + it.replaceFirst("/", ""), inflate.parallax
//                    )
//                }
            }

        }.postParms(mParams, Config.API.mine_information)
    }

    /**
     * 选择图片背景
     */
    private fun selectImageBg() {
        PermissionUtils
            .permission(PermissionConstants.STORAGE)
            .rationale {
                it.again(true)
            }
            .callback(object : PermissionUtils.FullCallback {
                override fun onGranted(permissionsGranted: MutableList<String>?) {
                    if (mPictureUtil == null) {
                        mPictureUtil = PictureUtil()
                        mPictureUtil!!.setCircleDimmedLayer(false)
                        mPictureUtil!!.setAspectRatio(PictureUtil.CROP_9_16)
                        mPictureUtil!!.setRequestCode(SELECT_MINE_BG)
                    }
                    mPictureUtil!!.selectPicture(activity)
                }

                override fun onDenied(
                    permissionsDeniedForever: MutableList<String>?,
                    permissionsDenied: MutableList<String>?
                ) {
                    ToastUtils.showShort("权限已拒绝")
                }
            })
            .request()
    }

    /**
     * 加载用户信息
     * @inPUll 是否是下拉刷新
     */
    private fun loadUserinfo(isPull: Boolean) {
        if (!isPull) {
            (activity as BaseActivity).showProgressDialog(getString(R.string.waiting))
        }

        mHttpUtil.setOnRequestCallBack { code, data ->
            if (!isPull) {
                (activity as BaseActivity).dismissProgressDialog()
            } else {
                inflate.refreshLayout.finishRefresh()
            }

            if (code == -1) {
                ToastUtils.showLong(data)
                return@setOnRequestCallBack
            }

            var mType: Type = object : TypeToken<BaseReq<UserInfoEntity>>() {}.type
            var mReq: BaseReq<UserInfoEntity> = GsonUtil.getGson().fromJson(data, mType)
            if (TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {
                showUserInfo(mReq.result)
            } else {
                ToastUtils.showLong(mReq.msg)
            }
        }.getParms(httpParams, Config.API.mine_information)
    }

    /**
     * 显示用户数据
     */
    private fun showUserInfo(info: UserInfoEntity) {
        UserInfoManage.getInstance.mUserInfoEntity = info

        ImageUtil.loadOriginalImage(
            context,
            inflate.parallax,
            inflate.parallax,
            R.mipmap.image_weibo_home_2
        )
        ImageUtil.loadOriginalImage(
            context,
            info.avatar,
            mIvAvatar,
            R.mipmap.icon_default_head
        )

//        if (!info.avatar.isNullOrBlank()) {
////            ImageUtil.loadOriginalImage(
////                context,
////                Config.API.image_url + info.avatar!!.replaceFirst("/", ""),
////                mIvAvatar
////            )
//            ImageUtil.loadOriginalImage(
//                context,
//                "/images/group_avatar/20190628/FCGSAPPHUU.png",
//                mIvAvatar
//            )
//        }

        when {
            info.gender == 0 -> //性别:0未知|1男|2女
                mIvSex.visibility = View.GONE
            info.gender == 1 -> {
                mIvSex.visibility = View.VISIBLE
                mIvSex.imageResource = R.mipmap.icon_male
            }
            info.gender == 2 -> {
                mIvSex.visibility = View.VISIBLE
                mIvSex.imageResource = R.mipmap.icon_female
            }
        }

        mTvNickname.text = if (!info.nickname.isNullOrBlank()) {
            info.nickname
        } else {
            info.phone
        }

        mTvUserPhone.text = "用户账号：${info.user_id}"

        var areaStr: String = ""
        if (!info.province.isNullOrBlank()) {
            areaStr += info.province
        }

        if (!info.city.isNullOrBlank()) {
            areaStr += info.city
        }

        if (!info.district.isNullOrBlank()) {
            areaStr += info.district
        }

        mTvArea.text = if (areaStr.isNullOrBlank()) {
            "城市未填写"
        } else {
            areaStr
        }

        mTvBirth.text = if (info.birthday.isNullOrBlank()) {
            mTvAge.text = "0岁"
            "生日未填写"
        } else {
            var year: Int = Calendar.getInstance().get(Calendar.YEAR)
            mTvAge.text = "${year - (info.birthday!!.split("-")[0]).toInt()}岁"
            info.birthday
        }

        mTvSignture.text = if (info.signature.isNullOrBlank()) {
            "签名未设置"
        } else {
            info.signature
        }
        SPUtils.getInstance().put(Config.Constant.MINE_INTEGRAL, info.integral)
        inflate.mTvIngegral.text = info.integral
    }

    override fun onDestroy() {
        super.onDestroy()
        mUserInfoDispose.dispose()
    }
}