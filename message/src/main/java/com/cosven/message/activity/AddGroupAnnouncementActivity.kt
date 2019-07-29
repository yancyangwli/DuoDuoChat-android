package com.cosven.message.activity

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.View
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.cosven.message.R
import com.woniu.core.activities.BaseActivity
import com.woniu.core.api.Config
import com.woniu.core.bean.BaseReq
import com.woniu.core.bean.GroupInfoEntity
import com.woniu.core.utils.GsonUtil
import com.woniu.core.utils.HttpUtil
import com.zhouyou.http.model.HttpParams
import kotlinx.android.synthetic.main.message_activity_group_announcement.*
import org.jetbrains.anko.toast
import java.lang.ref.WeakReference

/**
 * @author Anlycal<远>
 * @date 2019/5/24
 * @description 添加群聊公告
 */

class AddGroupAnnouncementActivity : BaseActivity() {
    companion object {
        val CREATE_GROUP_CHAT_ENTER = 0x8001//创建群聊
        val GROUP_CHAT_ENTER = 0x8002//群聊进入
        val UPDATE_GROUP_NAME = 0x8003//更新群名称
        val UPDATE_GROUP_NICKNAME = 0x8004//更新用户群昵称
    }

    private var mHttpUtil: HttpUtil? = null
    private var groupId: Int = 0
    private var enter_flag = GROUP_CHAT_ENTER
    private var mGroupInfo: GroupInfoEntity? = null

    private var content: String? = ""

    override fun setContentViewId(): Int {
        return R.layout.message_activity_group_announcement
    }

    override fun initView() {

        groupId = intent?.getIntExtra("GROUP_ID", 0)!!
        enter_flag = intent.getIntExtra("ENTER_FLAG", GROUP_CHAT_ENTER)
        content = intent.getStringExtra("CONTENT")
        mGroupInfo = intent.getSerializableExtra("GROUP_INFO") as GroupInfoEntity?

        setTitles(
            qmui_bar, when (enter_flag) {
                CREATE_GROUP_CHAT_ENTER -> "添加群公告"
                GROUP_CHAT_ENTER -> "修改群公告"
                UPDATE_GROUP_NAME -> "修改群聊名称"
                UPDATE_GROUP_NICKNAME -> "修改我的群昵称"
                else -> ""
            }, true
        )
        if (!content.isNullOrBlank()) {
            mEtContent.setText(content)
            mEtContent.setSelection(0, content!!.length)
        }

    }

    override fun initData() {
        mBtnConfirm.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.mBtnConfirm -> confirmOperation()
        }
    }

    private fun confirmOperation() {
        var content: String = mEtContent.text.toString().trim()
        if (content.isNullOrBlank()) {
            ToastUtils.showLong("内容不能为空")
            return
        }

        when (enter_flag) {
            CREATE_GROUP_CHAT_ENTER -> {
                var mIntent: Intent? = WeakReference(Intent()).get()
                mIntent?.let {
                    it.putExtra("ANNOUNCEMENT_CONTENT", content)
                    setResult(Activity.RESULT_OK, it)
                }
                finishs()
            }
            UPDATE_GROUP_NAME, GROUP_CHAT_ENTER -> {
                updateGroupInfo(content)
            }
            UPDATE_GROUP_NICKNAME -> {
                updateGroupSetting(content)
            }
        }

        KeyboardUtils.hideSoftInput(mEtContent)
    }

    /**
     * 更新群设置-->我的群聊昵称
     */
    private fun updateGroupSetting(content: String) {

        if (mHttpUtil == null) {
            mHttpUtil = HttpUtil(this)
        }
        var mParams: HttpParams = httpParams
        mParams.put("group_id", groupId.toString())
        mParams.put("group_nickname", content)
        mParams.put("not_disturb", mGroupInfo?.group_member?.not_disturb.toString())
        mParams.put("is_sticky", mGroupInfo?.group_member?.is_sticky.toString())
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
                val intent = Intent()
                intent.putExtra("data", content)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
            }

        }.postParms(mParams, Config.API.group_update_setting)
    }

    /**
     * 更新群信息
     */
    private fun updateGroupInfo(content: String) {
        if (mHttpUtil == null) {
            mHttpUtil = HttpUtil(this)
        }
        var mParams: HttpParams = httpParams
        mParams.put("group_id", groupId.toString())
        if (enter_flag == GROUP_CHAT_ENTER) {
            mParams.put("group_name", mGroupInfo?.group_name)
            mParams.put("announcement", content)
        }
        if (enter_flag == UPDATE_GROUP_NAME) {
            mParams.put("group_name", content)
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

            if (TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {
                val intent = Intent()
                intent.putExtra("data", content)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                toast(mReq.msg)
            }

        }.postParms(mParams, Config.API.group_update)
    }
}