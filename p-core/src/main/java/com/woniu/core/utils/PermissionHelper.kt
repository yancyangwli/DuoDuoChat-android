package com.woniu.core.utils

import android.app.Activity
import android.widget.Toast
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ScreenUtils
import com.orhanobut.logger.Logger

class PermissionHelper {
    /**
     * 申请权限
     * @param activity
     * @param permissions
     */

    companion object {
        var Granted = false

        fun init(activity: Activity, @PermissionConstants.Permission vararg permissions: String) {
            PermissionUtils.permission(*permissions)
                    .rationale {
                        Logger.i("权限拒绝")
                        //拒绝之后会在走这个方法
                        //                        PermissionHelper.showRationaleDialog(shouldRequest);
                    }
                    .callback(object : PermissionUtils.FullCallback {
                        override fun onGranted(permissionsGranted: List<String>) {   // 同意
                            //  同意之后走什么方法
                            Granted = true
                            Logger.i("权限同意")
                        }

                        override fun onDenied(permissionsDeniedForever: List<String>,
                                              permissionsDenied: List<String>) {   // 否认
                            if (!permissionsDeniedForever.isEmpty()) {
                                Logger.i("权限失败")
                                // 否认走什么方法
                                Granted = false
                                Toast.makeText(activity, "拒绝可能导致功能无法正常使用", Toast.LENGTH_SHORT).show()
                                return
                            }
                            LogUtils.d(permissionsDeniedForever, permissionsDenied)
                        }
                    })
                    .theme {activity -> ScreenUtils.setFullScreen(activity) }
                    .request()
        }
    }
}