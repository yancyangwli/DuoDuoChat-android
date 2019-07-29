package com.cosven.message.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import com.cosven.message.R
import com.cosven.message.adapter.AvatarListAdapter
import com.dktlh.ktl.provider.router.RouterPath
import com.google.gson.reflect.TypeToken
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.entity.LocalMedia
import com.scwang.smartrefresh.layout.util.DensityUtil
import com.woniu.core.activities.BaseActivity
import com.woniu.core.api.Config
import com.woniu.core.bean.*
import com.woniu.core.bean.entity.FileLoadEntity
import com.woniu.core.db.DbHelper
import com.woniu.core.manage.UserInfoManage
import com.woniu.core.utils.GsonUtil
import com.woniu.core.utils.HttpUtil
import com.woniu.core.utils.ImageUtil
import com.woniu.core.utils.PictureUtil
import com.zhouyou.http.model.HttpParams
import kotlinx.android.synthetic.main.activity_group_info.*
import kotlinx.android.synthetic.main.message_titile_layout.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import java.io.File
import java.lang.ref.WeakReference
import java.lang.reflect.Type

/**
 * @author Anlycal<远>
 * @date 2019/5/27
 * @description 群聊信息
 */


@Route(path = RouterPath.MesageCenter.PATH_GROUP_INFO)
class GroupInfoActivity : BaseActivity() {
    private var groupId: Int = 0
    private var mHttpUtil: HttpUtil? = null
    private var imageList: MutableList<Album> = ArrayList()
    private var mPictureUtil: PictureUtil? = null
    private var mGroupInfoEntity: GroupInfoEntity? = null
    private var notInGroup: Boolean = false

    private lateinit var mAvatarListAdapter: AvatarListAdapter

    companion object {
        val SELECT_GROUP_AVATAR: Int = 7001//选择群头像
        val UPDATE_ANNOUNCEMENT: Int = 7002//群公告
        val UPDATE_GROUP_NAME: Int = 7003//修改群聊名称
        val UPDATE_GROUP_NICKNAME: Int = 7004//修改我的群昵称
    }

    override fun setContentViewId(): Int {
        return R.layout.activity_group_info
    }

    override fun initView() {
        setTitles(qmui_bar, "群聊信息", true)
        groupId = intent.getIntExtra("GROUP_ID", 0)

        mRvAvatarList.layoutManager = GridLayoutManager(context, 5)
        mRvAvatarList.isNestedScrollingEnabled = false

        mAvatarListAdapter = AvatarListAdapter(R.layout.message_item_avater_list)
        mRvAvatarList.adapter = mAvatarListAdapter

        initEvent()
    }

    override fun initData() {

        if (mHttpUtil == null) {
            mHttpUtil = HttpUtil(this)
        }
        showProgressDialog(getString(R.string.waiting))
        mHttpUtil!!.setOnRequestCallBack { code, data ->
            //            Log.i("FAN","红包数据返回---> $data")
            dismissProgressDialog()
            if (code == -1) {
                ToastUtils.showLong(data)
                return@setOnRequestCallBack
            }

            val type: Type = object : TypeToken<BaseReq<GroupInfoEntity>>() {}.type
            val mReq: BaseReq<GroupInfoEntity> = GsonUtil.getGson().fromJson(data, type)
            if (TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {
                mReq.result?.let {
                    mGroupInfoEntity = it
                    showGroupInfo()
                }
            }

        }.getParms(httpParams, String.format(Config.API.group_info, groupId))
    }

    private fun showGroupInfo() {
        mGroupInfoEntity?.let {
            it.members.run {
                if (isNotEmpty() && size > 9) {
                    subList(0, 9)
                } else {
                    it
                }
                var user = User(R.mipmap.ic_add_group_member, "", 0)
                add(Member(user = user))
                mAvatarListAdapter.setNewData(this)
            }
            ImageUtil.loadOriginalImage(this, it.group_avatar, mIvGroupAvatar)
            mEtGroupName.text = it.group_name
            mGroupIntroTv.text = it.announcement
            notInGroup = it.group_member == null
            if (it.group_member == null) {//如果不是群成员
                mMoreMemberLl.visibility = View.GONE
                mAlbumLl.visibility = View.GONE
                mLlGroupNickNameRoot.visibility = View.GONE
                mBottomLl.visibility = View.GONE
                mDismissGroupBtn.visibility = View.GONE
                mClearChatRecordBtn.text = "申请加入"
                mClearChatRecordBtn.setTextColor(Color.parseColor("#10D8FC"))
            } else {
                mTvLookGroupMember.text = "查看全部群聊人员(${it.group_member_num}人)"
                mEtGroupNickName.text = it.group_member.group_nickname
                mNoDisturbingSwitch.isChecked = it.group_member.not_disturb == 1
                mStickySwitch.isChecked = it.group_member.is_sticky == 1
                mTotalSilenceSwitch.isChecked = it.group_silence == 1

                mNoDisturbingSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                    updateGroupSetting(
                        not_disturb = if (isChecked) 1 else 0,
                        is_sticky = if (mStickySwitch.isChecked) 1 else 0
                    )
                }
                mStickySwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                    updateGroupSetting(
                        not_disturb = if (mNoDisturbingSwitch.isChecked) 1 else 0,
                        is_sticky = if (isChecked) 1 else 0
                    )
                }
                mEtGroupNickName.setOnClickListener { v ->
                    startActivityForResult<AddGroupAnnouncementActivity>(
                        UPDATE_GROUP_NICKNAME, "GROUP_ID" to groupId,
                        "ENTER_FLAG" to AddGroupAnnouncementActivity.UPDATE_GROUP_NICKNAME,
                        "CONTENT" to it.group_member.group_nickname,
                        "GROUP_INFO" to it
                    )
                }

                //群公告，群聊名称，我的群昵称
                if (it.group_member.group_manager == 1 || it.group_member.group_manager == 2) {
                    mTotalSilenceLl.visibility = View.VISIBLE
                    mGroupAvatarIv.visibility = View.VISIBLE
                    mEtGroupName.setCompoundDrawablesWithIntrinsicBounds(
                        null, null,
                        ContextCompat.getDrawable(this, R.mipmap.ic_arrow_right), null
                    )
                    mLlGroupNoticeRoot.setOnClickListener { v ->
                        startActivityForResult<AddGroupAnnouncementActivity>(
                            UPDATE_ANNOUNCEMENT, "GROUP_ID" to groupId,
                            "ENTER_FLAG" to AddGroupAnnouncementActivity.GROUP_CHAT_ENTER,
                            "CONTENT" to it.announcement,
                            "GROUP_INFO" to it
                        )
                    }
                    mEtGroupName.setOnClickListener { v ->
                        startActivityForResult<AddGroupAnnouncementActivity>(
                            UPDATE_GROUP_NAME, "GROUP_ID" to groupId,
                            "ENTER_FLAG" to AddGroupAnnouncementActivity.UPDATE_GROUP_NAME,
                            "CONTENT" to it.group_name,
                            "GROUP_INFO" to it
                        )
                    }
                    mTotalSilenceSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                        updateGroupInfo(group_silence = if (isChecked) 1 else 0)
                    }
                    //更换群头像
                    mIvGroupAvatar.setOnClickListener {
                        selectGroupAvatar()
                    }
                }
                if (it.group_member.group_manager != 2) {
                    qmui_bar.addRightTextButton("举报", R.id.click_right_text).setOnClickListener {
                        ARouter
                            .getInstance()
                            .build(RouterPath.MineCenter.PATH_MINE_FEED_BACK)
                            .withString("FLAG", "group")
                            .withInt("OBJECT_ID", groupId)
                            .navigation()
                    }
                    mDismissGroupBtn.text = "删除并退出"
                }

                imageList = it.album
                if (imageList.isNotEmpty()) {
                    when (imageList.size) {
                        1 -> ImageUtil.loadOriginalImage(context, imageList[0].image, mIvImage1)
                        2 -> {
                            ImageUtil.loadOriginalImage(context, imageList[0].image, mIvImage1)
                            ImageUtil.loadOriginalImage(context, imageList[1].image, mIvImage2)
                        }
                        else -> {
                            ImageUtil.loadOriginalImage(context, imageList[0].image, mIvImage1)
                            ImageUtil.loadOriginalImage(context, imageList[1].image, mIvImage2)
                            ImageUtil.loadOriginalImage(context, imageList[2].image, mIvImage3)
                        }
                    }
                }
            }
        }
    }

    /**
     * 更新群信息
     */
    private fun updateGroupInfo(group_silence: Int = -1, group_avatar: String = "") {
        if (mHttpUtil == null) {
            mHttpUtil = HttpUtil(this)
        }
        var mParams: HttpParams = httpParams
        mParams.put("group_id", groupId.toString())
        mParams.put("group_name", mEtGroupName.text.toString())
        if (group_silence != -1) {
            mParams.put("group_silence", group_silence.toString())
        }
        if (group_avatar.isNullOrBlank().not()) {
            mParams.put("group_avatar", group_avatar)
        }
        showProgressDialog(getString(R.string.waiting))
        mHttpUtil!!.setOnRequestCallBack { code, data ->
            //            Log.i("FAN","红包数据返回---> $data")
            dismissProgressDialog()
            if (code == -1) {
                ToastUtils.showLong(data)
                return@setOnRequestCallBack
            }

            val mReq: BaseReq<*> = GsonUtil.getGson().fromJson(data, BaseReq::class.java)
            toast(mReq.msg)
            if (TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {
                mGroupInfoEntity?.group_silence = group_silence
            } else {
            }

        }.postParms(mParams, Config.API.group_update)
    }

    /**
     * 更新群设置
     * 免打扰，置顶聊天
     */
    private fun updateGroupSetting(not_disturb: Int = 0, is_sticky: Int = 0) {

        if (mHttpUtil == null) {
            mHttpUtil = HttpUtil(this)
        }
        var mParams: HttpParams = httpParams
        mParams.put("group_id", groupId.toString())
        mParams.put("group_nickname", mEtGroupNickName.text.toString())
        mParams.put("not_disturb", not_disturb.toString())
        mParams.put("is_sticky", is_sticky.toString())
        showProgressDialog(getString(R.string.waiting))
        mHttpUtil!!.setOnRequestCallBack { code, data ->
            //            Log.i("FAN","红包数据返回---> $data")
            dismissProgressDialog()
            if (code == -1) {
                ToastUtils.showLong(data)
                return@setOnRequestCallBack
            }

            val mReq: BaseReq<*> = GsonUtil.getGson().fromJson(data, BaseReq::class.java)
            toast(mReq.msg)
            if (TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {
                mGroupInfoEntity?.group_member?.not_disturb = not_disturb
                mGroupInfoEntity?.group_member?.is_sticky = is_sticky
            } else {
            }

        }.postParms(mParams, Config.API.group_update_setting)
    }

    private fun initEvent() {
        mAvatarListAdapter.setOnItemClickListener { adapter, view, position ->
            adapter.data.run {
                if (position == size - 1) {
                    ARouter
                        .getInstance()
                        .build(RouterPath.ContactCenter.PATH_CONTACT_PERSONAL)
                        .withString("FLAG", "invite")
                        .withString("GROUP_NAME", mGroupInfoEntity?.group_name)
                        .withInt("GROUP_ID", groupId)
                        .withString("GROUP_AVATAR", mGroupInfoEntity?.group_avatar)
                        .navigation()
                }
            }
            //            ARouter
//                .getInstance()
//                .build(RouterPath.MineCenter.PATH_USER_INFO)
//                .withInt("USER_ID", (adapter.data[position] as Member).user_id)
//                .navigation()
        }

        mTvLookGroupMember.setOnClickListener {
            startActivity<GroupMemberActivity>(
                "GROUP_ID" to groupId,
                "GROUP_MANAGER" to mGroupInfoEntity?.group_member?.group_manager
            )
        }

        //第一张图片
        mIvImage1.setOnClickListener {
            lookBigImages(0)
        }

        //第二张图片
        mIvImage2.setOnClickListener {
            lookBigImages(1)
        }

        //全部相册
        mFlLookAlbumRoot.setOnClickListener {
            startActivity<GroupAlbumActivity>(
                "GROUP_ID" to groupId,
                "GROUP_MANAGER" to mGroupInfoEntity?.group_member?.group_manager
            )
        }

        //群二维码
        mLlGroupQRCode.setOnClickListener {
            ARouter.getInstance().build(RouterPath.MineCenter.PATH_QR_CODE)
                .withSerializable("GROUP_INFO", mGroupInfoEntity)
                .navigation()
        }

        //解散群
        mDismissGroupBtn.setOnClickListener {
            mGroupInfoEntity?.let {
                if (it.group_member?.group_manager == 2) {//群主
                    dismissGroup()
                }
            }
        }

        //清除聊天记录/申请加入
        mClearChatRecordBtn.setOnClickListener {
            if (notInGroup) {//申请加入群
                ARouter
                    .getInstance()
                    .build(RouterPath.ContactCenter.PATH_ADD_FRIEND_GROUP)
                    .withInt("GROUP_ID", groupId)
                    .withString("GROUP_AVATAR", mGroupInfoEntity?.group_avatar)
                    .withString("GROUP_NAME", mGroupInfoEntity?.group_name)
                    .navigation()
            } else {
                DbHelper.deleteChatMessageByGroupId(groupId)
            }
        }
    }

    /**
     * 解散群组
     */
    private fun dismissGroup() {
        if (mHttpUtil == null) {
            mHttpUtil = HttpUtil(this)
        }
        var mParams: HttpParams = httpParams
        mParams.put("group_id", groupId.toString())
        showProgressDialog(getString(R.string.waiting))
        mHttpUtil!!.setOnRequestCallBack { code, data ->
            //            Log.i("FAN","红包数据返回---> $data")
            dismissProgressDialog()
            if (code == -1) {
                ToastUtils.showLong(data)
                return@setOnRequestCallBack
            }

            val mReq: BaseReq<*> = GsonUtil.getGson().fromJson(data, BaseReq::class.java)
            if (TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {
                toast("解散群成功")
            } else {
                toast(mReq.msg)
            }

        }.postParms(mParams, Config.API.dismiss_group)
    }

    /**
     * 选择群头像
     */
    private fun selectGroupAvatar() {
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
                        mPictureUtil!!.setRequestCode(SELECT_GROUP_AVATAR)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null && resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                SELECT_GROUP_AVATAR -> {
                    val selectList: MutableList<LocalMedia> = PictureSelector.obtainMultipleResult(data)
                    if (selectList.isNotEmpty()) {
                        ImageUtil.loadFileImage(
                            context,
                            WeakReference(File(selectList[0].compressPath)).get()!!,
                            mIvGroupAvatar
                        )
                        upAvatarFile(selectList[0].compressPath)
                    }
                }
                UPDATE_ANNOUNCEMENT -> {//更新公告
                    mGroupInfoEntity?.announcement = data?.getStringExtra("data")
                    mGroupIntroTv.text = mGroupInfoEntity?.announcement
                }
                UPDATE_GROUP_NAME -> {//更新群聊名称
                    mGroupInfoEntity?.group_name = data?.getStringExtra("data")
                    mEtGroupName.text = mGroupInfoEntity?.group_name
                }
                UPDATE_GROUP_NICKNAME -> {//更新我的群聊昵称
                    mGroupInfoEntity?.group_member?.group_nickname = data?.getStringExtra("data")
                    mEtGroupNickName.text = mGroupInfoEntity?.group_member?.group_nickname
                }
            }
        }
    }


    //上传头像图片
    private fun upAvatarFile(filePath: String) {
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
                updateGroupInfo(group_avatar = "/$group_avatar")
            }
        }?.postFile(Config.API.image_url, group_avatar, filePath, httpParams)
    }

    //查看大图
    private fun lookBigImages(index: Int) {
        var images: ArrayList<LocalMedia> = ArrayList()

        for (image: Album in imageList) {
            var lm: LocalMedia = LocalMedia()
            image.apply {
                if (this.image.startsWith("/")) {
                    this.image = Config.API.image_url + this.image.replaceFirst("/", "")
                }
            }
            lm.path = image.image
            images.add(lm)
        }

        if (images.size > index) {
            PictureUtil.lookBigImages(this, index, images)
        }
    }
}