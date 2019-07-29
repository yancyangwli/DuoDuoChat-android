package com.cosven.socialim.contact.activity

import android.content.Intent
import android.util.Log
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import com.cosven.socialim.contact.R
import com.dktlh.ktl.provider.router.RouterPath
import com.woniu.core.activities.BaseActivity
import com.woniu.core.utils.PictureUtil
import com.woniu.core.utils.Tools
import com.xuexiang.xqrcode.XQRCode
import com.zhuanxu.eclipse.aop.annotation.SingleClick
import kotlinx.android.synthetic.main.contact_add_layout.*
import kotlinx.android.synthetic.main.contact_titile_layout.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

/**
 * 搜索账号/手机号/群号
 */
class AddActivity : BaseActivity() {

    val REQUEST_CODE = 111
    var type = "user"

    @SingleClick
    override fun onClick(v: View?) {
        v?.run {
            when (id) {
                R.id.qr_sao -> enterQrcodePage()
                R.id.mLlSearchRoot -> startActivity<SearchActivity>("type" to type)
                R.id.mLlCreateGroupChat ->
                    ARouter
                        .getInstance()
                        .build(RouterPath.MesageCenter.PATH_CREATE_CHAT_GROUP)
                        .navigation()
                else -> return
            }
        }
    }

    override fun setContentViewId(): Int {
        return R.layout.contact_add_layout
    }

    override fun initView() {
        setTitles(qmui_bar, "添加", true)
    }

    override fun initData() {
        qr_sao.setOnClickListener(this)
        mLlSearchRoot.setOnClickListener(this)
        mLlCreateGroupChat.setOnClickListener(this)
        mUserGb.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mGroupCb.isChecked = false
                type = "user"
            } else {
                if (!mGroupCb.isChecked) {
                    mUserGb.isChecked = true
                }
            }
        }
        mGroupCb.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mUserGb.isChecked = false
                type = "group"
            } else {
                if (!mUserGb.isChecked) {
                    mGroupCb.isChecked = true
                }
            }
        }
    }

    private fun enterQrcodePage(){
        PermissionUtils.permission(PermissionConstants.STORAGE,PermissionConstants.CAMERA)
            .rationale { shouldRequest -> shouldRequest.again(true) }
            .callback(object : PermissionUtils.FullCallback {
                override fun onGranted(permissionsGranted: List<String>) {
                    val intent = Intent(this@AddActivity, QRScanActivity::class.java)
                    startActivityForResult(intent, REQUEST_CODE)
                }
                override fun onDenied(permissionsDeniedForever: List<String>, permissionsDenied: List<String>) {
                    ToastUtils.showShort("权限已拒绝")
                }
            })
            .request()
    }


    // 返回结果
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //处理二维码扫描结果
        if (requestCode == REQUEST_CODE && resultCode == -1) {
            //处理扫描结果（在界面上显示）
            handleScanResult(data)
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
                    Tools.showToast(this@AddActivity, "解析二维码失败")
                }
            }
        }
    }


}