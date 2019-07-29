package com.cosven.socialim.ui.activity.me

import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import android.widget.Button
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.cosven.my.R
import com.dktlh.ktl.provider.router.RouterPath
import com.scwang.smartrefresh.layout.util.DensityUtil
import com.woniu.core.activities.BaseActivity
import com.woniu.core.api.Config
import com.woniu.core.bean.GroupInfoEntity
import com.woniu.core.bean.entity.UserInfoEntity
import com.woniu.core.manage.UserInfoManage
import com.woniu.core.utils.FileUtil
import com.woniu.core.utils.ImageUtil
import com.woniu.core.utils.PictureUtil
import com.xuexiang.xqrcode.XQRCode
import com.zhuanxu.eclipse.aop.annotation.SingleClick
import kotlinx.android.synthetic.main.activity_qr_code.*
import kotlinx.android.synthetic.main.my_title_layout.*
import org.jetbrains.anko.textColor
import java.util.*

/**
 * 我的二维码
 */

@Route(path = RouterPath.MineCenter.PATH_QR_CODE)
class QrCodeActivity : BaseActivity() {
    private var isQRCodeCreated = false

    private lateinit var mRightButton: Button

    private var mUserInfoEntity: UserInfoEntity? = null
    private var mGroupInfoEntity: GroupInfoEntity? = null

    @SingleClick
    override fun onClick(v: View?) {

    }

    override fun setContentViewId(): Int {
        return R.layout.activity_qr_code
    }

    override fun initView() {

        mUserInfoEntity = intent.getSerializableExtra("USER_INFO") as UserInfoEntity?
        mGroupInfoEntity = intent.getSerializableExtra("GROUP_INFO") as GroupInfoEntity?

        mRightButton = qmui_bar.addRightTextButton("保存图片", R.id.click_right_text)
        mRightButton.textColor = resources.getColor(R.color.color_333)
        mRightButton.textSize = 14f

        mUserInfoEntity?.let {
            setTitles(qmui_bar, "我的二维码", true)
            showUserInfo(it)
        }
        mGroupInfoEntity?.let {
            setTitles(qmui_bar, "群二维码", true)
            showGroupInfo(it)
        }
    }

    private fun showGroupInfo(info: GroupInfoEntity) {
        mTvInfo.visibility = View.GONE
        mTvArea.visibility = View.GONE
        if (!info.group_avatar.isNullOrBlank()) {
//            var mBitmap:Bitmap = ImageUtil.load(this,info.avatar)
            Glide
                .with(this)
                .asBitmap()
                .load(Config.API.image_url + info.group_avatar?.replaceFirst("/".toRegex(), ""))
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        mIvAvatar.setImageBitmap(resource)
                        var qrImg: Bitmap = ImageUtils.toRoundCorner(
                            resource,
                            DensityUtil.dp2px(20f).toFloat(),
                            DensityUtil.dp2px(8f),
                            Color.WHITE,
                            false
                        )
                        createQRCodeWithLogo(qrImg, info.group_id, true)
                    }
                })
        } else {
            createQRCodeWithLogo(ImageUtils.getBitmap(R.mipmap.icon_default_head), info.group_id, true)
        }
        mTvNickname.text = info.group_name
    }

    private fun createQRCodeWithLogo(bitmap: Bitmap?, id: Int, isGroup: Boolean = false) {
        showQRCode(
            XQRCode.createQRCodeWithLogo(
                "${if (isGroup) "group" else "user"}/$id/duoduo",
                DensityUtil.dp2px(280f),
                DensityUtil.dp2px(280f),
                bitmap
            )
        )
    }

    private fun showQRCode(QRCode: Bitmap) {
        mIvQRCode.setImageBitmap(QRCode)
        isQRCodeCreated = true
    }

    private fun showUserInfo(info: UserInfoEntity) {
        if (!info.avatar.isNullOrBlank()) {
//            var mBitmap:Bitmap = ImageUtil.load(this,info.avatar)
            Glide
                .with(this)
                .asBitmap()
                .load(Config.API.image_url + info.avatar?.replaceFirst("/".toRegex(), ""))
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        mIvAvatar.setImageBitmap(resource)
                        var qrImg: Bitmap = ImageUtils.toRoundCorner(
                            resource,
                            DensityUtil.dp2px(20f).toFloat(),
                            DensityUtil.dp2px(8f),
                            Color.WHITE,
                            false
                        )
                        createQRCodeWithLogo(qrImg, info.user_id)
                    }
                })
        } else {
            createQRCodeWithLogo(ImageUtils.getBitmap(R.mipmap.icon_default_head), info.user_id)
        }

        if (!info.nickname.isNullOrBlank()) {
            mTvNickname.text = info.nickname
        } else {
            mTvNickname.text = info.phone
        }

        if (info.gender == 1) {
            mTvInfo.setCompoundDrawablesWithIntrinsicBounds(
                resources.getDrawable(R.mipmap.ic_male_white),
                null,
                null,
                null
            )
            mTvInfo.setBgColor(Color.parseColor("#FF5EAAFF"))
        } else if (info.gender == 2) {
            mTvInfo.setCompoundDrawablesWithIntrinsicBounds(
                resources.getDrawable(R.mipmap.ic_female_white),
                null,
                null,
                null
            )
            mTvInfo.setBgColor(Color.parseColor("#FFFF3984"))
        }

        mTvInfo.text = if (!info.birthday.isNullOrBlank()) {
            (Calendar.getInstance().get(Calendar.YEAR) - (info!!.birthday!!.split("-")[0]).toInt()).toString()
        } else {
            "0"
        }

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
        mTvArea.text = areaStr

    }

    private var INTERVAL: Int = 2000
    private var CURRENT_TIME: Long = 0L

    override fun initData() {
        mRightButton.setOnClickListener {
            if (System.currentTimeMillis() - CURRENT_TIME > INTERVAL) {
                CURRENT_TIME = System.currentTimeMillis()
                PermissionUtils.permission(PermissionConstants.STORAGE)
                    .rationale { shouldRequest -> shouldRequest.again(true) }
                    .callback(object : PermissionUtils.FullCallback {
                        override fun onGranted(permissionsGranted: List<String>) {
                            saveQRCode()
                        }

                        override fun onDenied(permissionsDeniedForever: List<String>, permissionsDenied: List<String>) {
                            ToastUtils.showShort("权限已拒绝")
                        }
                    })
                    .request()
            }
        }
    }


    // 保存图片
    private fun saveQRCode() {
        if (isQRCodeCreated) {
            FileUtil.saveImageToGallery(this@QrCodeActivity, ImageUtils.view2Bitmap(mLlQrCode))
        } else {
            ToastUtils.showShort("请先生成二维码!")
        }
    }
}