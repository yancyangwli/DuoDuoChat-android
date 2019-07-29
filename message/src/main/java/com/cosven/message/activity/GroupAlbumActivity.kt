package com.cosven.message.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import com.cosven.message.R
import com.cosven.message.adapter.GroupAlbumAdapter
import com.cosven.message.bean.GroupAlbumListBean
import com.google.gson.reflect.TypeToken
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.entity.LocalMedia
import com.woniu.core.activities.BaseActivity
import com.woniu.core.api.Config
import com.woniu.core.bean.Album
import com.woniu.core.bean.BaseReq
import com.woniu.core.bean.entity.FileLoadEntity
import com.woniu.core.utils.GsonUtil
import com.woniu.core.utils.HttpUtil
import com.woniu.core.utils.PictureUtil
import com.woniu.core.utils.PictureUtil.MULTIPLE
import com.zhouyou.http.model.HttpParams
import kotlinx.android.synthetic.main.activity_group_album.*
import kotlinx.android.synthetic.main.message_titile_layout.*
import org.jetbrains.anko.toast

/**
 * @author Anlycal<远>
 * @date 2019/5/28
 * @description 群相册
 */


class GroupAlbumActivity : BaseActivity() {

    private lateinit var mGroupAlbumAdapter: GroupAlbumAdapter
    private lateinit var mRightText: Button
    private var isEditStatus: Boolean = false //是否是编辑状态

    private val mHttpUtil = HttpUtil(context)
    private var page = 1
    private val SELECT_IMAGE = 6001//选择图片

    private var mPictureUtil: PictureUtil? = null
    private var hasUploadedCount = 0
    private var imageStr = ""
    private var groupId = 0
    private var groupManager = 0

    override fun setContentViewId(): Int {
        return R.layout.activity_group_album
    }

    override fun initView() {
        setTitles(qmui_bar, "群相册", true)
        groupId = intent?.getIntExtra("GROUP_ID", 0) ?: 0
        groupManager = intent?.getIntExtra("GROUP_MANAGER", 0) ?: 0
        if (groupManager == 1 || groupManager == 2) {
            qmui_bar.addRightTextButton("编辑", R.id.click_right_text).apply {
                textSize = 14f
                setTextColor(Color.parseColor("#333333"))
                setOnClickListener {
                    isEditStatus = !isEditStatus
                    if (isEditStatus) {//处于编辑状态中
                        withEditStatusVisble(isEditStatus, View.GONE, View.VISIBLE, "取消")
                    } else {
                        withEditStatusVisble(isEditStatus, View.VISIBLE, View.GONE, "编辑")
                    }
                }
            }
            mBtnUpImage.visibility = View.VISIBLE
        }

        mRvGroupAblumList.layoutManager = GridLayoutManager(context, 3)

        mGroupAlbumAdapter = GroupAlbumAdapter(R.layout.message_item_group_album)
        mRvGroupAblumList.adapter = mGroupAlbumAdapter
        initEvent()
    }

    override fun initData() {
        mRefreshLayout.autoLoadmore()
    }


    /**
     * 加载新数据
     * isRefresh true刷新，false加载更多
     */
    private fun loadData(isRefresh: Boolean) {
//
//        if (!isRefresh) {
//            (activity as BaseActivity).showProgressDialog(getString(R.string.waiting))
//        }
        var httpParams = HttpParams()
        httpParams.put("group_id", groupId.toString())
        mHttpUtil.setOnRequestCallBack { code, data ->
            mRefreshLayout.finishLoadmore()
            if (code == -1) {
                ToastUtils.showLong(data)
                return@setOnRequestCallBack
            }

            val type = object : TypeToken<BaseReq<GroupAlbumListBean>>() {

            }.type

            val mReq = GsonUtil.getGson().fromJson<BaseReq<GroupAlbumListBean>>(data, type)
            if (TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {

                mRefreshLayout.isEnableLoadmore = !TextUtils.isEmpty(mReq.result.next_page_url)
                mReq.result.data?.let {
                    if (page == 1) {
                        mGroupAlbumAdapter.setNewData(it)
                    } else {
                        mGroupAlbumAdapter.addData(it)
                    }
                    page++
                }

            } else {
                if (page > 1) {
                    page--
                }
                ToastUtils.showLong(mReq.msg)
            }
        }.getParms(httpParams, Config.API.group_album_images)
    }

    private fun initEvent() {
        mRefreshLayout.setOnLoadmoreListener {
            loadData(false)
        }
        mBtnDelete.setOnClickListener {
            deleteImages()
        }
        mBtnUpImage.setOnClickListener {
            selectImages()
        }

        mGroupAlbumAdapter.setOnItemClickListener { adapter, view, position ->
            var mLm: ArrayList<LocalMedia> = ArrayList<LocalMedia>()

            for (album: Any in adapter.data) {
                var l: LocalMedia = LocalMedia()
                l.path = (album as? Album)?.apply {
                    if (this.image.startsWith("/")) {
                        this.image = Config.API.image_url + this.image.replaceFirst("/", "")
                    }
                }?.image
                mLm.add(l)
            }

            PictureUtil.lookBigImages(this, position, mLm)
        }
    }

    /**
     * 选取群相册
     */
    private fun selectImages() {

        PermissionUtils.permission(PermissionConstants.STORAGE)
            .rationale { shouldRequest -> shouldRequest.again(true) }
            .callback(object : PermissionUtils.FullCallback {
                override fun onGranted(permissionsGranted: List<String>) {
                    mPictureUtil = PictureUtil().apply {
                        setSelctionMode(MULTIPLE)
                        setSelectPicMaxNumber(9)
                        setRequestCode(SELECT_IMAGE)
                        setEnableCrop(false)
                        selectPicture(this@GroupAlbumActivity)
                    }
                }

                override fun onDenied(permissionsDeniedForever: List<String>, permissionsDenied: List<String>) {
                    ToastUtils.showShort("权限已拒绝")
                }
            })
            .request()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        data?.let {
            if (requestCode == SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
                val selectList: MutableList<LocalMedia> = PictureSelector.obtainMultipleResult(data)
                if (selectList != null && selectList.size > 0) {
                    for (image in selectList) {
                        var upUrl = Config.group()
                        imageStr = "$imageStr;/$upUrl"
                        uploadImage(upUrl, image.compressPath, selectList.size)
                    }
                }
            }
        }
    }

    private fun uploadImage(upUrl: String, filePath: String, count: Int) {
        mHttpUtil?.setOnRequestCallBack { code, data ->
            //            Log.i("FAN","上传图片返回---> $data")
            if (code == -1) {
                ToastUtils.showLong(data)
                return@setOnRequestCallBack
            }
            var mFileLoadEntity: FileLoadEntity = GsonUtil.getGson().fromJson(data, FileLoadEntity::class.java)
            if (!mFileLoadEntity.url.isNullOrEmpty()) {
                hasUploadedCount++
                if (hasUploadedCount == count) {//全部上传完成
                    uploadAlbum()
                }
            }
        }.postFile(Config.API.image_url, upUrl, filePath, httpParams)
    }

    /**
     * 更新相册信息到群信息中
     */
    private fun uploadAlbum() {

        var mParams: HttpParams = httpParams

        mParams.put("images", imageStr.replaceFirst(";", ""))
        mParams.put("group_id", groupId.toString())

        showProgressDialog(getString(R.string.waiting))
        mHttpUtil.setOnRequestCallBack { code, data ->
            dismissProgressDialog()
            if (code == -1) {
                ToastUtils.showLong(data)
                return@setOnRequestCallBack
            }

            var mReq: BaseReq<*> = GsonUtil.getGson().fromJson(data, BaseReq::class.java)
            ToastUtils.showLong(mReq.msg)

            if (TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {
                page = 1
                loadData(false)
            }

        }.postParms(mParams, Config.API.group_album_upload)
    }

    /**
     * 批量删除图片
     */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun deleteImages() {
        var ids = mGroupAlbumAdapter.selectList?.run {
            if (isEmpty()) {
                toast("请选择需要删除的图片")
                return
            }
            stream().map { it.id }
        }
        var mParams: HttpParams = httpParams
        mParams.put("ids", ids.toArray().joinToString())
        mParams.put("group_id", groupId.toString())

        showProgressDialog(getString(R.string.waiting))
        mHttpUtil.setOnRequestCallBack { code, data ->
            dismissProgressDialog()
            if (code == -1) {
                ToastUtils.showLong(data)
                return@setOnRequestCallBack
            }

            var mReq: BaseReq<*> = GsonUtil.getGson().fromJson(data, BaseReq::class.java)
            ToastUtils.showLong(mReq.msg)

            if (TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {
                page = 1
                loadData(false)
            }

        }.postParms(mParams, Config.API.group_album_remove)
    }

    /**
     * 根据是否是编辑状态显示视图
     */
    private fun withEditStatusVisble(isEdit: Boolean, showUpImageView: Int, showDeleteView: Int, content: String) {
        mRightText.text = content
        mBtnUpImage.visibility = showUpImageView
        mBtnDelete.visibility = showDeleteView

        mGroupAlbumAdapter.showCheckView = isEdit
        mGroupAlbumAdapter.notifyDataSetChanged()
    }
}