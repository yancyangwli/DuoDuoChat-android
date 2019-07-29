package com.cosven.my.runAlone.activity

import android.annotation.SuppressLint
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ToastUtils
import com.cosven.my.R
import com.cosven.socialim.ui.activity.me.FriendSetActivity
import com.cosven.socialim.ui.activity.me.QrCodeActivity
import com.dktlh.ktl.provider.router.RouterPath
import com.google.gson.reflect.TypeToken
import com.luck.picture.lib.tools.ScreenUtils
import com.woniu.core.activities.BaseActivity
import com.woniu.core.api.Config
import com.woniu.core.bean.BaseReq
import com.woniu.core.bean.entity.FriendInfoEntity
import com.woniu.core.bean.entity.MomentInfo
import com.woniu.core.bean.entity.UserInfoEntity
import com.woniu.core.manage.UserInfoManage
import com.woniu.core.utils.GsonUtil
import com.woniu.core.utils.HttpUtil
import com.woniu.core.utils.ImageUtil
import com.woniu.core.xmpp.rxbus.event.BaseEvent
import com.zhouyou.http.model.HttpParams
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_user_info.*
import kotlinx.android.synthetic.main.layout_person_header.*
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.startActivity
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author Anlycal<远>
 * @date 2019/5/29
 * @description 用户信息
 */

@Route(path = RouterPath.MineCenter.PATH_USER_INFO)
class UserInfoActivity : BaseActivity() {
    private var user_id: Int = -1
    private var isMine: Boolean = false

    private var mHttpUtil: HttpUtil = HttpUtil(this)

    private var mFriendInfo: FriendInfoEntity? = null

    private var mUserInfoEntity: UserInfoEntity? = null

    private var mImagesDispose: Disposable? = null

    private lateinit var mFriendImages: ArrayList<String>

    override fun setContentViewId(): Int {
        return R.layout.activity_user_info
    }

    override fun initView() {
        user_id = intent.getIntExtra("USER_ID", -1)
        mLlEditInfo.visibility = View.GONE

        viewVisiOrGone(View.GONE, View.GONE)

        initEvent()

        loadUserInfo(false)
    }

    override fun initData() {
//        var avatarUrl:String = "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1985268360,1020131399&fm=27&gp=0.jpg"
//        ImageUtil.loadOriginalImage(this,avatarUrl,mIvAvatar)
    }

    private fun initEvent() {
        mIvBack.setOnClickListener(this)
        mLlQrCode.setOnClickListener(this)
        mLlDynamicRoot.setOnClickListener(this)
        mIvSet.setOnClickListener(this)
        mBtnAddBlock.setOnClickListener(this)
        mBtnSendMessage.setOnClickListener(this)
        mBtnAddFriend.setOnClickListener(this)

        mRefreshLayout.setOnRefreshListener {
            loadUserInfo(true)
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
            } else {
                mRefreshLayout.finishRefresh()
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

    override fun onClick(v: View) {
        when (v.id) {
            R.id.mIvBack -> finishs()
            R.id.mLlQrCode -> {
                mUserInfoEntity?.let {
                    startActivity<QrCodeActivity>(Pair("USER_INFO", it))
                }
            }
            R.id.mLlDynamicRoot ->
                mFriendInfo?.let {
                    ARouter
                        .getInstance()
                        .build(RouterPath.DynamicCenter.PATH_DYNAMIC_LIST)
                        .withInt("LOOK_TYPE", Config.Constant.LOOK_OTHER_MOMENT)
                        .withSerializable("USER_INFO", it)
                        .navigation()
                }
            R.id.mIvSet -> startActivity<FriendSetActivity>("FRIEND_ID" to user_id)
            R.id.mBtnAddBlock -> addOrRemoveBlockFriend()
            R.id.mBtnSendMessage ->
                mFriendInfo?.let {
                    ARouter
                        .getInstance()
                        .build(RouterPath.MesageCenter.PATH_CHAT_LIST)
                        .withString("USER_ID", it.user_id.toString())
                        .withString(
                            "USER_AVATAR", if (!it.avatar.isNullOrBlank()) {
                                it.avatar
                            } else {
                                ""
                            }
                        )
                        .withString(
                            "USER_NICKNAME", if (!it.nickname.isNullOrBlank()) {
                                it.nickname
                            } else {
                                ""
                            }
                        )
                        .withInt("CHAT_TYPE", BaseEvent.CHAT_TYPE.PERSONAL.typeValue)
                        .navigation()
                }

            R.id.mBtnAddFriend -> finishs()
        }
    }

    /**
     * 显示用户数据
     */
    private fun showUserInfo(user: FriendInfoEntity?) {
        user?.let {
            mFriendInfo = it
            isMine = UserInfoManage.getInstance.mUserInfoEntity?.user_id == user_id
            if (!isMine) {
                mLlQrCode.visibility = View.GONE
            }
            mUserInfoEntity = UserInfoEntity(
                avatar = it.avatar,
                city = it.city,
                province = it.province,
                district = it.district,
                gender = it.gender,
                birthday = it.birthday,
                user_id = it.user_id,
                nickname = it.nickname
            )

            it.personal_page_cover?.let {
                ImageUtil.loadOriginalImage(
                    context,
                    Config.API.image_url + it.replaceFirst("/", ""), parallax
                )
            }

            if (!it.avatar.isNullOrBlank()) {
                ImageUtil.loadOriginalImage(
                    context,
                    Config.API.image_url + it.avatar!!.replaceFirst("/", ""),
                    mIvAvatar
                )
            }

            when {
                it.gender == 0 -> //性别:0未知|1男|2女
                    mIvSex.visibility = View.GONE
                it.gender == 1 -> {
                    mIvSex.visibility = View.VISIBLE
                    mIvSex.imageResource = R.mipmap.icon_male
                }
                it.gender == 2 -> {
                    mIvSex.visibility = View.VISIBLE
                    mIvSex.imageResource = R.mipmap.icon_female
                }
            }

            mTvNickname.text = user.nickname

            mTvUserPhone.text = "用户ID：${it.user_id}"

            var areaStr: String = ""
            if (!it.province.isNullOrBlank()) {
                areaStr += it.province
            }

            if (!it.city.isNullOrBlank()) {
                areaStr += it.city
            }

            if (!it.district.isNullOrBlank()) {
                areaStr += it.district
            }

            mTvArea.text = if (areaStr.isNullOrBlank()) {
                "城市未填写"
            } else {
                areaStr
            }

            mTvBirth.text = if (it.birthday.isNullOrBlank()) {
                mTvAge.text = "0岁"
                "生日未填写"
            } else {
                var year: Int = Calendar.getInstance().get(Calendar.YEAR)

                mTvAge.text = if (it.birthday!!.contains("/")) {
                    "${year - (it.birthday!!.split("/")[0]).toInt()}岁"
                } else {
                    "${year - (it.birthday!!.split("-")[0]).toInt()}岁"
                }
                it.birthday
            }

            mTvSignture.text = if (it.signature.isNullOrBlank()) {
                "签名未设置"
            } else {
                it.signature
            }


            if (it.friend_info == null) {
                viewVisiOrGone(View.VISIBLE, View.GONE)
            } else {
                viewVisiOrGone(View.GONE, View.VISIBLE)

                if (it.friend_info?.friend_remark.isNullOrBlank()) {
                    mTvNickname.text = it.friend_info?.friend_remark
                    mUserInfoEntity?.nickname = it.friend_info?.friend_remark
                }

                mBtnAddBlock.text = if (it.friend_info?.is_block == 0) {
                    "添加黑名单"
                } else {
                    "移出黑名单"
                }
            }

            if (it.moments != null && it.moments?.size!! > 0) {
                mLlImages.visibility = View.VISIBLE
                obtainFriendImage(it.moments!!)
            } else {
                mLlImages.visibility = View.GONE
            }

        }
    }

    @SuppressLint("CheckResult")
    private fun obtainFriendImage(momendList: List<MomentInfo>) {
        mImagesDispose = Observable.just(momendList)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .flatMap(object : Function<List<MomentInfo>, ObservableSource<List<String>>> {
                override fun apply(t: List<MomentInfo>): ObservableSource<List<String>> {
//                    Log.i("FAN", "flat-map线程--> ${Thread.currentThread().name}")
                    mFriendImages = ArrayList()
                    for (info in t) {
                        info.images?.let {
                            for (image in it) {
                                if (mFriendImages.size < 3) {
                                    mFriendImages.add(image)
                                } else {
                                    break
                                }
                            }
                        }
                    }
                    return Observable.just(mFriendImages)
                }
            })
            .subscribe {
                if (it.size < 3) {
                    mIvImage3.visibility = View.INVISIBLE
                }

                if (it.size < 2) {
                    mIvImage2.visibility = View.INVISIBLE
                }

                if (it.isEmpty()) {
                    mIvImage1.visibility = View.INVISIBLE
                }

                if (it.isNotEmpty()) {
                    setImageSize(mIvImage1, it[0])
                }

                if (it.size > 1) {
                    setImageSize(mIvImage2, it[1])
                }

                if (it.size > 2) {
                    setImageSize(mIvImage3, it[1])
                }
            }
    }


    /**
     * 移出或者加入黑名单
     */
    private fun addOrRemoveBlockFriend() {
        mFriendInfo?.let {
            var mParams: HttpParams = httpParams
            mParams.put("block_user_id", it.user_id.toString())
            showProgressDialog(getString(R.string.waiting))

            var request_url: String = if (mFriendInfo?.friend_info?.is_block == 0) {
                Config.API.friend_block
            } else {
                Config.API.friend_remove_blacklist
            }

            mHttpUtil.setOnRequestCallBack { code, data ->
                dismissProgressDialog()
                if (code == -1) {
                    ToastUtils.showLong(data)
                } else {
                    var mReq: BaseReq<*> = GsonUtil.getGson().fromJson(data, BaseReq::class.java)
                    ToastUtils.showLong(mReq.msg)
                    if (TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {

                        if (mFriendInfo?.friend_info?.is_block == 1) {
                            mFriendInfo?.friend_info?.is_block = 0
                        } else {
                            mFriendInfo?.friend_info?.is_block = 1
                        }
                        notifyUserData()
                    }
                }
            }.postParms(mParams, request_url)
        }
    }


    private fun setImageSize(imageView: ImageView, imageUrl: String) {
        imageView.visibility = View.VISIBLE
        var mWidth: Int = (ScreenUtils.getScreenWidth(this) - resources.getDimension(R.dimen.d_50)).toInt()
        var params: ViewGroup.LayoutParams = imageView.layoutParams
        params.height = mWidth / 3
        params.width = mWidth / 3
        ImageUtil.loadOriginalImage(this, imageUrl, imageView)
    }

    private fun notifyUserData() {
        mBtnAddBlock.text = if (mFriendInfo?.friend_info?.is_block == 0) {
            "添加黑名单"
        } else {
            "移出黑名单"
        }
    }

    private fun viewVisiOrGone(add: Int, status: Int) {
        mBtnAddFriend.visibility = add
        mLlFriendStatus.visibility = status
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mImagesDispose != null && mImagesDispose?.isDisposed!!) {
            mImagesDispose?.dispose()
        }
        mImagesDispose = null
    }
}