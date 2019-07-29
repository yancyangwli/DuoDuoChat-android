package com.cosven.socialim.contact.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ToastUtils
import com.cosven.socialim.contact.R
import com.dktlh.ktl.provider.router.RouterPath
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia
import com.woniu.core.activities.BaseActivity
import com.woniu.core.utils.PictureUtil
import com.woniu.core.utils.Tools
import com.xuexiang.xqrcode.XQRCode
import com.xuexiang.xqrcode.ui.CaptureActivity
import com.xuexiang.xqrcode.ui.CaptureFragment
import com.xuexiang.xqrcode.util.QRCodeAnalyzeUtils
import com.zhuanxu.eclipse.aop.annotation.SingleClick
import kotlinx.android.synthetic.main.contact_titile_layout.*
import org.jetbrains.anko.toast

/**
 * 扫一扫
 */
class QRScanActivity : BaseActivity() {

    val mPictureUtil by lazy {
        PictureUtil()
    }


    @SingleClick
    override fun onClick(v: View?) {

    }

    override fun setContentViewId(): Int {
        return R.layout.contact_qr_sao_layout
    }

    override fun initView() {

        setTitles(qmui_bar, "扫一扫", true)
        // 右边的点击栏目
        qmui_bar.addRightTextButton("相册", R.id.empty_button).setOnClickListener {
            mPictureUtil.setSelectPicMaxNumber(1)
            mPictureUtil.setSelctionMode(PictureUtil.SINGLE)
            mPictureUtil.setEnableCrop(false)
            mPictureUtil.selectPicture(this@QRScanActivity)
        }

        initCaptureFragment()

    }

    override fun initData() {
    }

    /**
     * 照相机初始化监听
     */
    internal var cameraInitCallBack: CaptureFragment.CameraInitCallBack = CaptureFragment.CameraInitCallBack { e ->
        if (e != null) {
            CaptureActivity.showNoPermissionTip(this@QRScanActivity)
        }
    }

    /**
     * 显示无照相机权限提示
     *
     * @param activity
     * @param listener 确定点击事件
     * @return
     */
    fun showNoPermissionTip(activity: Activity, listener: DialogInterface.OnClickListener): AlertDialog {
        return AlertDialog.Builder(activity)
                .setTitle(com.xuexiang.xqrcode.R.string.xqrcode_pay_attention)
                .setMessage(com.xuexiang.xqrcode.R.string.xqrcode_not_get_permission)
                .setPositiveButton(com.xuexiang.xqrcode.R.string.xqrcode_submit, listener)
                .show()
    }

//    /**
//     * 显示无照相机权限提示
//     *
//     * @param activity
//     * @return
//     */
//    fun showNoPermissionTip(activity: Activity): AlertDialog {
//        return showNoPermissionTip(activity, DialogInterface.OnClickListener { dialog, which -> activity.finish() })
//    }

    /**
     * 二维码解析回调函数
     */
    internal var analyzeCallback: QRCodeAnalyzeUtils.AnalyzeCallback = object : QRCodeAnalyzeUtils.AnalyzeCallback {
        override fun onAnalyzeSuccess(mBitmap: Bitmap, result: String) {
            val resultIntent = Intent()
            val bundle = Bundle()
            bundle.putInt(XQRCode.RESULT_TYPE, XQRCode.RESULT_SUCCESS)
            bundle.putString(XQRCode.RESULT_DATA, result)
            resultIntent.putExtras(bundle)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        override fun onAnalyzeFailed() {
            val resultIntent = Intent()
            val bundle = Bundle()
            bundle.putInt(XQRCode.RESULT_TYPE, XQRCode.RESULT_FAILED)
            bundle.putString(XQRCode.RESULT_DATA, "")
            resultIntent.putExtras(bundle)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
    val REQUEST_CODE_REQUEST_PERMISSIONS = 222

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_REQUEST_PERMISSIONS) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initCaptureFragment()
            } else {
                CaptureActivity.showNoPermissionTip(this@QRScanActivity)
            }
        }
    }

    private fun initCaptureFragment() {
        val captureFragment = CaptureFragment()
        captureFragment.analyzeCallback = analyzeCallback
        captureFragment.setCameraInitCallBack(cameraInitCallBack)
        supportFragmentManager.beginTransaction().replace(R.id.fl_zxing_container, captureFragment).commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && resultCode === Activity.RESULT_OK && requestCode == PictureConfig.CHOOSE_REQUEST) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片、视频、音频选择结果回调
                    val selectList: MutableList<LocalMedia> = PictureSelector.obtainMultipleResult(data)
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
//                    adapter.setList(selectList)
//                    adapter.notifyDataSetChanged()
                    selectList?.let {
                        XQRCode.analyzeQRCode((selectList.get(0).path), object : QRCodeAnalyzeUtils.AnalyzeCallback {
                            override fun onAnalyzeSuccess(mBitmap: Bitmap, result: String) {
                                //处理扫描结果（在界面上显示）
                                handleScanResult(data)
                            }

                            override fun onAnalyzeFailed() {
                                ToastUtils.showShort("解析二维码失败", android.widget.Toast.LENGTH_LONG)
                            }
                        })
                    }

                }
            }
        }
    }

    private fun handleScanResult(data: Intent?) {
        if (data != null) {
            val bundle = data.extras
            if (bundle != null) {
                if (bundle.getInt(XQRCode.RESULT_TYPE) == XQRCode.RESULT_SUCCESS) {
                    val barCode = bundle.getString(XQRCode.RESULT_DATA)
                    Log.e("handleScanResult", "handleScanResult: 解析结果:$barCode")
                    when {
                        barCode.startsWith("group") -> {
                            ARouter
                                .getInstance()
                                .build(RouterPath.MesageCenter.PATH_GROUP_INFO)
                                .withInt("GROUP_ID", barCode.split("/")[1].toInt())
                                .navigation()
                        }
                        barCode.startsWith("user") -> {
                            ARouter
                                .getInstance()
                                .build(RouterPath.MineCenter.PATH_USER_INFO)
                                .withInt("USER_ID", barCode.split("/")[1].toInt())
                                .navigation()
                        }
                        else -> toast("非法二维码")
                    }
                } else if (bundle.getInt(XQRCode.RESULT_TYPE) == XQRCode.RESULT_FAILED) {
                    Tools.showToast(this@QRScanActivity, "解析二维码失败")
                }
            }
        }
    }


}