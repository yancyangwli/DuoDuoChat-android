package com.cosven.socialim.contact.activity

import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ToastUtils
import com.cosven.socialim.contact.R
import com.dktlh.ktl.provider.router.RouterPath
import com.google.gson.reflect.TypeToken
import com.gyf.immersionbar.ImmersionBar
import com.woniu.core.activities.BaseActivity
import com.woniu.core.api.Config
import com.woniu.core.bean.BaseReq
import com.woniu.core.bean.entity.FriendInfoEntity
import com.woniu.core.utils.GsonUtil
import com.woniu.core.utils.HttpUtil
import com.woniu.core.utils.ImageUtil
import com.zhouyou.http.model.HttpParams
import kotlinx.android.synthetic.main.contact_add_friend_layout.*
import java.lang.reflect.Type

/**
 * 添加朋友
 */
@Route(path = RouterPath.ContactCenter.PATH_ADD_FRIEND_GROUP)
class AddFriendAcitivty : BaseActivity() {

    private var mUserInfo: FriendInfoEntity? = null

    private var mHttpUtil: HttpUtil = HttpUtil(this)

    private var user_id: Int = -1
    private var groupId: Int = -1
    private var groupAvatar: String = ""
    private var groupName: String = ""

    override fun setContentViewId(): Int {
        return R.layout.contact_add_friend_layout
    }

    override fun initView() {
        user_id = intent.getIntExtra("USER_ID", -1)
        groupId = intent.getIntExtra("GROUP_ID", -1)
        groupAvatar = intent.getStringExtra("GROUP_AVATAR") ?: ""
        groupName = intent.getStringExtra("GROUP_NAME") ?: ""

        if (user_id != -1) {
            mInfoLl.visibility = View.VISIBLE
            loadUserInfo(false)
        } else {
            ImageUtil.loadOriginalImage(this, groupAvatar, mIvAvatar)
            mTvNickname.text = groupName
            mBtnAddFriend.text = "申请加入"
        }
    }

    override fun initStatusBar() {
        ImmersionBar.with(this)
            .titleBar(R.id.toolbar)
            .statusBarDarkFont(true)
            .keyboardEnable(true)  //解决软键盘与底部输入框冲突问题
            .init()
    }

    override fun initData() {
        mRlUserInfoRoot.setOnClickListener(this)
        mBtnAddFriend.setOnClickListener(this)
        mToolbarBack.setOnClickListener(this)

        mEtNote.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                mEtNote.isCursorVisible = true
            }
            false
        }
    }

    private fun setInfo(sexImg: Int, bgColor: Int) {
        mTvInfo.setCompoundDrawablesWithIntrinsicBounds(
            resources.getDrawable(sexImg),
            null,
            null,
            null
        )
        mTvInfo.setBgColor(bgColor)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.mRlUserInfoRoot -> {
                mUserInfo?.let {
                    ARouter
                        .getInstance()
                        .build(RouterPath.MineCenter.PATH_USER_INFO)
                        .withInt("USER_ID", it.user_id)
                        .navigation()
                }
            }
            R.id.mBtnAddFriend -> {
                if (user_id != -1) {
                    addFriend()
                } else if (groupId != -1) {
                    addGroup()
                }
            }
            R.id.mToolbarBack -> finishs()
        }
    }

    /**
     * 申请加入群
     */
    private fun addGroup() {
        var mParams: HttpParams = httpParams
        mParams.put("group_id", groupId.toString())

        var note: String = mEtNote.text.toString().trim()
        mParams.put("remarks", if (note.isNullOrBlank().not()) note else "请求加入群")
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
                finish()
            }
        }?.postParms(mParams, Config.API.group_apply)
    }

    /**
     * 添加好友
     */
    private fun addFriend() {
        mUserInfo?.let {
            var mParams: HttpParams = httpParams
            mParams.put("to_user_id", it.user_id.toString())

            var note: String = mEtNote.text.toString().trim()
            mParams.put("remarks", if (note.isNullOrBlank().not()) note else "请求添加好友")

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
                    finishs()
                }
            }?.postParms(mParams, Config.API.friend_apply)
        }
    }

    private fun loadUserInfo(isRefresh: Boolean) {
        if (user_id == -1) {
            return
        }
        if (!isRefresh) {
            showProgressDialog(getString(R.string.waiting))
        }
        mHttpUtil.setOnRequestCallBack { code, data ->
            if (!isRefresh) {
                dismissProgressDialog()
            }

            if (code == -1) {
                ToastUtils.showLong(data)
                return@setOnRequestCallBack
            }

            var type: Type = object : TypeToken<BaseReq<FriendInfoEntity>>() {}.type
            var mReq: BaseReq<FriendInfoEntity> = GsonUtil.getGson().fromJson(data, type)
            if (TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {
                showUserInfo(mReq.result)
            } else {
                ToastUtils.showLong(mReq.msg)
            }
        }.getParms(httpParams, String.format(Config.API.user_info_from_userId, user_id))
    }


    private fun showUserInfo(info: FriendInfoEntity) {
        info?.let {
            mUserInfo = info
            if (!it.avatar.isNullOrBlank()) {
                ImageUtil.loadOriginalImage(this, it.avatar, mIvAvatar)
            }

            mTvNickname.text = if (!it.nickname.isNullOrBlank()) {
                it.nickname
            } else {
                ""
            }

            if (it.gender == 1) {
                setInfo(R.mipmap.ic_male_white, resources.getColor(R.color.color_sex_male))
            } else if (it.gender == 2) {
                setInfo(R.mipmap.ic_female_white, resources.getColor(R.color.color_sex_female))
            }

            var area: String = ""
            if (!it.province.isNullOrBlank()) {
                area += it.province
            }

            if (!it.city.isNullOrBlank()) {
                area += it.city
            }

            if (!it.district.isNullOrBlank()) {
                area += it.district
            }

            mTvArea.text = area
        }
    }
}