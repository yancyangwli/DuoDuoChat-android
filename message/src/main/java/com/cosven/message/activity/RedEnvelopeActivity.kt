package com.cosven.message.activity

import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.ToastUtils
import com.cosven.message.R
import com.cosven.message.adapter.RedEnvelopeAdapter
import com.cosven.message.bean.RedPacketRecordBean
import com.dktlh.ktl.provider.router.RouterPath
import com.google.gson.reflect.TypeToken
import com.woniu.core.activities.BaseActivity
import com.woniu.core.api.Config
import com.woniu.core.bean.BaseReq
import com.woniu.core.bean.entity.RedPack
import com.woniu.core.utils.GsonUtil
import com.woniu.core.utils.HttpUtil
import com.woniu.core.utils.ImageUtil
import kotlinx.android.synthetic.main.message_activity_red_envelope.*
import kotlinx.android.synthetic.main.message_item_red_envelope_head.view.*
import kotlinx.android.synthetic.main.message_titile_layout.*
import org.jetbrains.anko.backgroundColor
import java.lang.reflect.Type

/**
 * @author Anlycal<远>
 * @date 2019/5/24
 * @description 红包详情
 */

@Route(path = RouterPath.MessageCenter.PATH_MESSAGE_RED_ENVELOPE)
class RedEnvelopeActivity : BaseActivity() {
    private var mHttpUtil: HttpUtil? = null
    private var redPack: RedPack? = null
    private lateinit var mRedEnvelopeAdapter: RedEnvelopeAdapter

    override fun setContentViewId(): Int {
        return R.layout.message_activity_red_envelope
    }

    override fun initView() {
        qmui_bar.backgroundColor = Color.parseColor("#FE4F6C")
        setTitlesWithBackIconWhite(qmui_bar,"积分红包",true)
        redPack = intent?.getSerializableExtra("REDPACK") as RedPack?
    }

    override fun initData() {
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRedEnvelopeAdapter = RedEnvelopeAdapter(R.layout.message_item_red_envelope_receive)
        var mHeadView: View = LayoutInflater.from(context).inflate(R.layout.message_item_red_envelope_head, mRecyclerView, false)
        mHeadView.mBtnNumber.setButtonText(intent?.getStringExtra("INTEGRAL"))
        redPack?.let {
            mHeadView.mTvEnvelopeName.text = it.wishes
            ImageUtil.loadOriginalImage(this, it.user?.avatar, mHeadView.mIvAvatar)
        }
        mRedEnvelopeAdapter.setHeaderView(mHeadView)

        mRecyclerView.adapter = mRedEnvelopeAdapter
        lookRedPackInfo()
    }


    private fun lookRedPackInfo() {
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
            val mType: Type = object : TypeToken<BaseReq<RedPacketRecordBean>>() {}.type
            val mReq: BaseReq<RedPacketRecordBean> = GsonUtil.getGson().fromJson(data, mType)

            if (!TextUtils.equals(mReq.status, Config.Constant.SUCCESS)) {
                ToastUtils.showLong(mReq.msg)
                return@setOnRequestCallBack
            } else {
                mReq.result.data?.let {
                    mRedEnvelopeAdapter.setNewData(it)
                }
            }
        }.getParms(httpParams, String.format(Config.API.red_pack_log, intent?.getStringExtra("RED_PACK_ID")))


    }
}