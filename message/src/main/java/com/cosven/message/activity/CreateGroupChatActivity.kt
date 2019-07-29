package com.cosven.message.activity

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.cosven.message.R
import com.cosven.message.bean.OpenRedPacketBean
import com.dktlh.ktl.provider.router.RouterPath
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.tools.PictureFileUtils
import com.woniu.core.activities.BaseActivity
import com.woniu.core.api.Config
import com.woniu.core.bean.BaseReq
import com.woniu.core.bean.GroupInfoEntity
import com.woniu.core.bean.entity.FileLoadEntity
import com.woniu.core.bean.entity.GroupEntity
import com.woniu.core.utils.*
import com.zhouyou.http.model.HttpParams
import kotlinx.android.synthetic.main.message_activity_create_group_chat.*
import kotlinx.android.synthetic.main.message_titile_layout.*
import java.io.File
import java.lang.ref.WeakReference
import java.lang.reflect.Type

/**
 * @author Anlycal<远>
 * @date 2019/5/24
 * @description 创建群聊
 */

@Route(path = RouterPath.MesageCenter.PATH_CREATE_CHAT_GROUP)
class CreateGroupChatActivity : BaseActivity() {

    private val CREATE_GROUP_ANNOUNCEMENT_CODE: Int = 0x6001
    private val SELECT_IMAGE_CODE: Int = 0x6002

    private var mPictureUtil: PictureUtil? = null

    private var mAreaUtil: AreaUtil? = null;

    private var mProvince: String = ""
    private var mCity: String = ""
    private var mDistrict: String = ""

    private var mLocalMedia: LocalMedia? = null

    private var mHttpUtil: HttpUtil? = null

    override fun setContentViewId(): Int {
        return R.layout.message_activity_create_group_chat
    }

    override fun initView() {
        setTitles(qmui_bar, "新建群聊", true)
    }

    override fun initData() {
        initEvent()
    }

    private fun initEvent() {
        mLlSelectAvatarRoot.setOnClickListener(this)
        mLlAddAreaRoot.setOnClickListener(this)
        mLlGroupNoticeRoot.setOnClickListener(this)
        mBtnCreate.setOnClickListener(this)

        mEtGroupName.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                mEtGroupName.isCursorVisible = true
            }
            false
        }
    }

    //选择地区
    private fun selectAreaData() {
        if (mAreaUtil == null) {
            mAreaUtil = AreaUtil(this)
            mAreaUtil!!.setOnSelectAreaCompleteListener { province, city, area ->
                mTvArea.text = if (TextUtils.equals(province, city)) {
                    setPCD("", city, area)
                    city + area
                } else {
                    if (TextUtils.equals(city, area)) {
                        setPCD(province, city, "")
                        province + city
                    } else {
                        setPCD(province, city, area)
                        province + city + area
                    }
                }
            }
        }
        mAreaUtil!!.startParseData()
    }

    private fun setPCD(pro: String, ci: String, di: String) {
        mProvince = pro
        mCity = ci
        mDistrict = di
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.mLlSelectAvatarRoot -> selectImage()
            R.id.mLlAddAreaRoot -> selectAreaData()
            R.id.mLlGroupNoticeRoot -> {
                var mIntent: Intent? = WeakReference(Intent(this, AddGroupAnnouncementActivity::class.java)).get()
                mIntent?.let {
                    it.putExtra("ANNOUNCEMENT_CONTENT", mTvAnnouncement.text.toString().trim() + "")
                    it.putExtra("ENTER_FLAG", AddGroupAnnouncementActivity.CREATE_GROUP_CHAT_ENTER)
                    startActivityForResult(it, CREATE_GROUP_ANNOUNCEMENT_CODE)
                }
            }
            R.id.mBtnCreate -> createOperation()
        }
    }

    //选择图片
    private fun selectImage() {
        PermissionUtils.permission(PermissionConstants.STORAGE)
            .rationale { shouldRequest -> shouldRequest.again(true) }
            .callback(object : PermissionUtils.FullCallback {
                override fun onGranted(permissionsGranted: List<String>) {
                    if (mPictureUtil == null) {
                        mPictureUtil = PictureUtil()
                        mPictureUtil!!.setCircleDimmedLayer(false)
                        mPictureUtil!!.setRequestCode(SELECT_IMAGE_CODE)
                    }
                    mPictureUtil!!.selectPicture(this@CreateGroupChatActivity)
                }

                override fun onDenied(permissionsDeniedForever: List<String>, permissionsDenied: List<String>) {
                    ToastUtils.showShort("权限已拒绝")
                }
            })
            .request()
    }

    //上传头像图片
    private fun upAvatarFile(groupName: String, filePath: String) {
        if (mHttpUtil == null) {
            mHttpUtil = HttpUtil(this)
        }
        showProgressDialog(getString(R.string.waiting))

        var group_avatar = Config.FolderName.group_avatar
        mHttpUtil?.setOnRequestCallBack { code, data ->
            dismissProgressDialog()
            if (code == -1) {
                ToastUtils.showLong(data)
                return@setOnRequestCallBack
            }
            var mFileLoadEntity: FileLoadEntity = GsonUtil.getGson().fromJson(data, FileLoadEntity::class.java)
            if (!mFileLoadEntity.url.isNullOrEmpty()) {
                Log.i("FAN", "url---->${mFileLoadEntity.url}")
                request(groupName, "/$group_avatar")
            }
        }?.postFile(Config.API.image_url, group_avatar, filePath, httpParams)
    }

    /**
     * 创建群组
     */
    private fun createOperation() {
        var groupName: String = mEtGroupName.text.toString().trim()
        if (groupName.isNullOrBlank()) {
            ToastUtils.showLong("请输入群名")
            return
        }

        var area: String = mTvArea.text.toString().trim()
        if (area.isNullOrBlank()) {
            ToastUtils.showLong("请选择地区")
            return
        }


        if (mLocalMedia != null) {//群头像已选取，先上传头像，再表单提交数据
            upAvatarFile(groupName, mLocalMedia!!.compressPath)
            return
        }

        request(groupName, null)
    }

    /**
     * 表单提交数据
     */
    private fun request(groupName: String, avatar: String?) {
        var mParams: HttpParams = httpParams
        mParams.put("group_name", groupName)
        avatar?.let {
            mParams.put("group_avatar", avatar)
        }
        if (!mProvince.isNullOrBlank()) {
            mParams.put("province", mProvince)
        }
        if (!mCity.isNullOrBlank()) {
            mParams.put("city", mCity)
        }
        if (!mDistrict.isNullOrBlank()) {
            mParams.put("district", mDistrict)
        }

        var announcement: String = mTvAnnouncement.text.toString().trim()
        if (!announcement.isNullOrBlank()) {
            mParams.put("announcement", announcement)
        }

        mParams.put("group_limit", 200.toString())
        showProgressDialog(getString(R.string.waiting))
        if (mHttpUtil == null) {
            mHttpUtil = HttpUtil(this)
        }
        mHttpUtil?.setOnRequestCallBack { code, data ->
            dismissProgressDialog()
//            Log.i("FAN","创建群聊返回---> ${data}")
            if (code == -1) {
                ToastUtils.showLong(data)
                return@setOnRequestCallBack
            }

            val bean = Gson().fromJson(data, BaseReq::class.javaObjectType)
            if (TextUtils.equals(bean.status, Config.Constant.SUCCESS)) {
                ToastUtils.showLong("创建成功！")
                finish()
            }
        }?.postParms(mParams, Config.API.group_create)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        data?.let {
            if (requestCode == SELECT_IMAGE_CODE && resultCode == Activity.RESULT_OK) {
                val selectList: MutableList<LocalMedia> = PictureSelector.obtainMultipleResult(it)
                if (selectList != null && selectList.size > 0) {
                    ImageUtil.loadFileImage(
                        context,
                        WeakReference(File(selectList[0].compressPath)).get()!!,
                        mIvGroupAvatar
                    )
                    mLocalMedia = selectList[0]
                }
            } else if (requestCode == CREATE_GROUP_ANNOUNCEMENT_CODE && resultCode == Activity.RESULT_OK) {
                var content: String = it.getStringExtra("ANNOUNCEMENT_CONTENT")
                mTvAnnouncement.text = content
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PictureFileUtils.deleteCacheDirFile(this)
    }
}