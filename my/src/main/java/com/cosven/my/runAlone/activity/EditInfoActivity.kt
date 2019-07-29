package com.cosven.my.runAlone.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import com.alibaba.android.arouter.launcher.ARouter
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.bigkoo.pickerview.view.TimePickerView
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.cosven.my.R
import com.dktlh.ktl.provider.router.RouterPath
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.tools.PictureFileUtils
import com.woniu.core.activities.BaseActivity
import com.woniu.core.api.Config
import com.woniu.core.bean.BaseReq
import com.woniu.core.bean.entity.FileLoadEntity
import com.woniu.core.bean.entity.UserInfoEntity
import com.woniu.core.manage.UserInfoManage
import com.woniu.core.utils.*
import com.woniu.core.xmpp.rxbus.RxBus
import com.woniu.core.xmpp.smack.SmackManager
import com.zhouyou.http.model.HttpParams
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_edit_info.*
import kotlinx.android.synthetic.main.personal_item.*
import java.io.File
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

import io.reactivex.Observable
import io.reactivex.functions.Function

/**
 * @author Anlycal<远>
 * @date 2019/5/31
 * @description 编辑个人信息
 */
class EditInfoActivity : BaseActivity() {
    private val SELECT_IMAGE = 6001;//选择图片

    private var mPictureUtil: PictureUtil? = null
    private var mAreaUtil: AreaUtil? = null
    private var mPVOptions: OptionsPickerView<String>? = null
    private var mTimePickerView: TimePickerView? = null

    private val mSexArrays: ArrayList<String> = arrayListOf<String>("男", "女")

    private var mHttpUtil: HttpUtil = HttpUtil(this)

    private var mHeardUrl: String? = null


    private var type: Int = 0;

    override fun setContentViewId(): Int {
        return R.layout.activity_edit_info
    }

    private var mProvince: String = ""
    private var mCity: String = ""
    private var mDistrict: String = ""

    private lateinit var mBtnSave: Button

    override fun initView() {
        setTitles(qmui_bar, "编辑个人信息", true)

        type = intent.getIntExtra("TYPE", 0)
        if (type == 0) {
            mBtnSave = qmui_bar.addRightTextButton("完成", R.id.click_right_text)
        } else if (type == 1) {
            mBtnSave = qmui_bar.addRightTextButton("保存", R.id.click_right_text)
            mBtnSave.visibility = View.GONE
        }
        showUserInfo(UserInfoManage.getInstance.mUserInfoEntity)
        initEvent()
    }

    override fun initData() {
    }

    private fun initEvent() {
        mLlSex.setOnClickListener(this)
        mLlAvatarRoot.setOnClickListener(this)
        mEtNickname.setOnClickListener(this)
        mEtSignature.setOnClickListener(this)
        mLlArea.setOnClickListener(this)
        mLlBir.setOnClickListener(this)

        mBtnSave.setOnClickListener {
            if (mHeardUrl.isNullOrBlank()) {
                editInfo("")
            } else {
                upAvatarFile()
            }

            if (type == 0) {
                loginXMPP(
                    UserInfoManage.getInstance.mUserInfoEntity,
                    UserInfoManage.getInstance.mUserInfoEntity?.user_id.toString()
                )
            }
        }

        mEtNickname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mBtnSave.visibility = View.VISIBLE
            }
        })

        mEtSignature.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mBtnSave.visibility = View.VISIBLE
            }

        })

    }

    @SuppressLint("CheckResult")
    private fun loginXMPP(info: UserInfoEntity?, account: String) {
        Observable.just(arrayOf<String>(account, Config.XMPP.XMPP_PSW))
            .subscribeOn(Schedulers.io())
            .flatMap(Function<Array<String>, ObservableSource<Boolean>> { strings ->
                //                Log.i("FAN", "account---> ${strings[0]}")
                Observable.just(
                    SmackManager.getInstance().login(
                        strings[0],
                        strings[1]
                    )
                )
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { aBoolean ->
                if (aBoolean!!) {
                    ARouter
                        .getInstance()
                        .build(RouterPath.MainCenter.PATH_MAIN)
                        .navigation()
                    AppManager.get().finishAllActivity()
                } else {
                    ToastUtils.showLong("登陆失败,请重新登陆")
                }
            }
    }

    /**
     * 选择性别
     */
    private fun selectSex() {
        if (mPVOptions == null) {
            mPVOptions = PickerUtil.setPickerStyle(
                "性别选择",
                OptionsPickerBuilder(this) { options1: Int, options2: Int, options3: Int, v: View? ->
                    mBtnSave.visibility = View.VISIBLE
                    mTvSex.setText(mSexArrays[options1])
                }
            )
                .build<String>()
            mPVOptions!!.setPicker(mSexArrays.toList())
        }
        mPVOptions!!.show()
    }

    /**
     * 选择头像
     */
    private fun selectAvatar() {
        PermissionUtils.permission(PermissionConstants.STORAGE)
            .rationale { shouldRequest -> shouldRequest.again(true) }
            .callback(object : PermissionUtils.FullCallback {
                override fun onGranted(permissionsGranted: List<String>) {
                    if (mPictureUtil == null) {
                        mPictureUtil = PictureUtil()
                        mPictureUtil!!.setRequestCode(SELECT_IMAGE)
                    }
                    mPictureUtil!!.selectPicture(this@EditInfoActivity)
                }

                override fun onDenied(permissionsDeniedForever: List<String>, permissionsDenied: List<String>) {
                    ToastUtils.showShort("权限已拒绝")
                }
            })
            .request()
    }

    /**
     * 选择生日
     */
    private fun selectBirDate() {
        if (mTimePickerView == null) {
            val selectedDate: Calendar = Calendar.getInstance()

            val startDate: Calendar = Calendar.getInstance()
            startDate.set(1919, 0, 1)

            val endDate: Calendar = Calendar.getInstance()
            endDate.set(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH))

            mTimePickerView =
                PickerUtil.setTimePickerStyle(startDate, endDate, selectedDate, TimePickerBuilder(this) { date, v ->
                    mBtnSave.visibility = View.VISIBLE
                    var dateStr: String = TimeUtils.date2String(date, SimpleDateFormat("yyyy-MM-dd"))
                    mTvBirth.text = dateStr
                })
                    .build()

        }
        mTimePickerView!!.show()
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.mLlSex -> selectSex()
            R.id.mLlAvatarRoot -> selectAvatar()
            R.id.mEtNickname -> mEtNickname.isCursorVisible = true;
            R.id.mEtSignature -> mEtSignature.isCursorVisible = true
            R.id.mLlArea -> {
                if (mAreaUtil == null) {
                    mAreaUtil = AreaUtil(context)
                    mAreaUtil!!.setOnSelectAreaCompleteListener { province, city, area ->
                        mBtnSave.visibility = View.VISIBLE
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
            R.id.mLlBir -> selectBirDate()
        }
    }

    private fun setPCD(pro: String, ci: String, di: String) {
        mProvince = pro
        mCity = ci
        mDistrict = di
    }

    /**
     * 显示用户数据
     */
    private fun showUserInfo(info: UserInfoEntity?) {
        info?.let {
            if (!it.avatar.isNullOrBlank()) {
                ImageUtil.loadOriginalImage(this, it.avatar, mIvAvatar)
            }

            if (!it.nickname.isNullOrBlank()) {
                mEtNickname.setText(it.nickname)
            }

            mTvUserPhone.text = it.phone

            mTvSex.text = if (it.gender == 0) {
                ""
            } else if (it.gender == 1) {
                "男"
            } else {
                "女"
            }

            if (!it.birthday.isNullOrBlank()) {
                mTvBirth.text = it.birthday
            }

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

            if (!areaStr.isNullOrBlank()) {
                mTvArea.text = areaStr
            }

            if (!it.signature.isNullOrBlank()) {
                mEtSignature.setText(it.signature)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        data?.let {
            if (requestCode == SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
                val selectList: MutableList<LocalMedia> = PictureSelector.obtainMultipleResult(data)
                if (selectList != null && selectList.size > 0) {
                    mBtnSave.visibility = View.VISIBLE
                    mHeardUrl = selectList[0].compressPath
                    ImageUtil.loadFileImage(
                        context,
                        WeakReference(File(mHeardUrl)).get(),
                        mIvAvatar
                    )
                }
            }
        }
    }


    private fun upAvatarFile() {
        if (mHttpUtil == null) {
            mHttpUtil = HttpUtil(this)
        }
        showProgressDialog(getString(R.string.waiting))
        var avatar = Config.FolderName.avatar_

        mHttpUtil?.setOnRequestCallBack { code, data ->
            dismissProgressDialog()
            if (code == -1) {
                ToastUtils.showLong(data)
                return@setOnRequestCallBack
            }
            var mFileLoadEntity: FileLoadEntity = GsonUtil.getGson().fromJson(data, FileLoadEntity::class.java)
            if (!mFileLoadEntity.url.isNullOrEmpty()) {
                Log.i("FAN", "url---->${mFileLoadEntity.url}")
                editInfo("/$avatar")
            }
        }?.postFile(Config.API.image_url, avatar, mHeardUrl, httpParams)
    }

    private fun editInfo(avatar: String) {
        var mParams: HttpParams = httpParams

        if (!avatar.isNullOrBlank()) {
            mParams.put("avatar", avatar)
        }

        val nickname = mEtNickname.text.toString().trim()
        if (!nickname.isNullOrBlank()) {
            mParams.put("nickname", nickname)
        }

        var mSexInt: Int = 0
        val sex: String = mTvSex.text.toString().trim()
        if (!sex.isNullOrBlank()) {
            mSexInt = if (TextUtils.equals(sex, mSexArrays[0])) {
                1
            } else {
                2
            }
        }
        mParams.put("gender", mSexInt.toString())
        val birth: String = mTvBirth.text.toString().trim()
        if (!birth.isNullOrBlank()) {
            mParams.put("birthday", birth)
        }

        if (!mProvince.isNullOrBlank() || !mCity.isNullOrBlank() || !mDistrict.isNullOrBlank()) {
            mParams.put("province", mProvince)
            mParams.put("city", mCity)
            mParams.put("district", mDistrict)
        }

        val signature: String = mEtSignature.text.toString().trim()
        if (!signature.isNullOrBlank()) {
            mParams.put("signature", signature)
        }

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
                if (type==1) {
                    finishs()
                }

                var mUser: UserInfoEntity = UserInfoManage.getInstance.mUserInfoEntity!!
                if (!mUser.avatar.isNullOrBlank()) {
                    mUser.avatar = avatar
                }
                mUser.nickname = nickname
                mUser.gender = mSexInt
                mUser.birthday = birth
                if (!mProvince.isNullOrBlank() || !mCity.isNullOrBlank() || !mDistrict.isNullOrBlank()) {
                    mUser.province = mProvince
                    mUser.city = mCity
                    mUser.district = mDistrict
                }
                mUser.signature = signature

                RxBus.getInstance().post(mUser)
            }

        }.postParms(mParams, Config.API.mine_information)
    }

    override fun onDestroy() {
        super.onDestroy()
        PictureFileUtils.deleteCacheDirFile(this)
    }
}