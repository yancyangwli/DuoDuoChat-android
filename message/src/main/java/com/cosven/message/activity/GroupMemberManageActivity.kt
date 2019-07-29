package com.cosven.message.activity

import android.text.TextUtils
import com.blankj.utilcode.util.ToastUtils
import com.cosven.message.R
import com.woniu.core.activities.BaseActivity
import com.woniu.core.api.Config
import com.woniu.core.bean.BaseReq
import com.woniu.core.bean.GroupMember
import com.woniu.core.bean.RxBusEvent
import com.woniu.core.utils.GsonUtil
import com.woniu.core.utils.HttpUtil
import com.woniu.core.xmpp.rxbus.RxBus
import com.zhouyou.http.model.HttpParams
import kotlinx.android.synthetic.main.activity_group_member_manage.*
import org.jetbrains.anko.toast

/**
 * 群成员管理界面
 */
class GroupMemberManageActivity : BaseActivity() {

    private var groupMember: GroupMember? = null
    private var mHttpUtil: HttpUtil? = null

    override fun setContentViewId() = R.layout.activity_group_member_manage

    override fun initView() {
        setTitles(qmui_bar, "群成员管理", true)
        intent?.run {
            groupMember = getSerializableExtra("GROUP_MEMBER") as GroupMember?
        }
    }

    override fun initData() {
        groupMember?.run {
            //设置管理员
            mManagerSwitch?.run {
                isChecked = group_manager == 1//1代表是管理员
                setOnCheckedChangeListener { _, isChecked ->
                    optionManager(if (isChecked) 1 else 0)
                }
            }
            //禁言7天
            mSevenDaySwitch?.run {
                isChecked = forbidden_type.isNullOrBlank().not() && forbidden_type == "seven_days"
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        optionForbidden("seven_days")
                    } else {
                        if (!mForeverSwitch.isChecked) {
                            optionForbidden("")
                        }
                    }
                }
            }
            //永久禁言
            mForeverSwitch?.run {
                isChecked = forbidden_type.isNullOrBlank().not() && forbidden_type == "forever"
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        optionForbidden("forever")
                    } else {
                        if (!mSevenDaySwitch.isChecked) {
                            optionForbidden("")
                        }
                    }
                }
            }
        }
    }

    /**
     * 设置/取消管理员
     */
    private fun optionManager(type: Int) {
        if (mHttpUtil == null) {
            mHttpUtil = HttpUtil(this)
        }
        var mParams: HttpParams = httpParams
        groupMember?.run {
            mParams.put("group_id", group_id.toString())
            mParams.put("user_id", user_id.toString())
            mParams.put("type", type.toString())
            showProgressDialog(getString(R.string.waiting))
            mHttpUtil!!.setOnRequestCallBack { code, data ->
                //            Log.i("FAN","红包数据返回---> $data")
                dismissProgressDialog()
                if (code == -1) {
                    ToastUtils.showLong(data)
                    return@setOnRequestCallBack
                }

                var mReq: BaseReq<*> = GsonUtil.getGson().fromJson(data, BaseReq::class.java)
                toast(mReq.msg)
                RxBus.getInstance().post(RxBusEvent().apply { updateGroupMember = true })
                if (TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {
                    mReq.result?.let {
                    }
                }

            }.postParms(mParams, Config.API.group_set_manager)
        }
    }

    /**
     * 7天禁言/永久禁言
     */
    private fun optionForbidden(forbidden_type: String) {
        if (mHttpUtil == null) {
            mHttpUtil = HttpUtil(this)
        }
        var mParams: HttpParams = httpParams
        groupMember?.run {
            mParams.put("group_id", group_id.toString())
            mParams.put("user_ids", user_id.toString())
            mParams.put("option_type", "batch_forbidden")
            mParams.put("forbidden_type", forbidden_type)
            showProgressDialog(getString(R.string.waiting))
            mHttpUtil!!.setOnRequestCallBack { code, data ->
                //            Log.i("FAN","红包数据返回---> $data")
                dismissProgressDialog()
                if (code == -1) {
                    ToastUtils.showLong(data)
                    return@setOnRequestCallBack
                }

                var mReq: BaseReq<*> = GsonUtil.getGson().fromJson(data, BaseReq::class.java)
                toast(mReq.msg)
                if (TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {
                    when (forbidden_type) {
                        "seven_days" -> {
                            mSevenDaySwitch.isChecked = true
                            mForeverSwitch.isChecked = false
                        }
                        "forever" -> {
                            mSevenDaySwitch.isChecked = false
                            mForeverSwitch.isChecked = true
                        }
                        else -> {
                            mSevenDaySwitch.isChecked = false
                            mForeverSwitch.isChecked = false
                        }
                    }
                }

            }.postParms(mParams, Config.API.group_option_member)
        }
    }
}
