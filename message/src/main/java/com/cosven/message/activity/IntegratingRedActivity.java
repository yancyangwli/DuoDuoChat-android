package com.cosven.message.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.blankj.utilcode.util.ToastUtils;
import com.cosven.message.R;
import com.cosven.message.bean.RedPacketBean;
import com.google.gson.reflect.TypeToken;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.woniu.core.activities.BaseActivity;
import com.woniu.core.api.Config;
import com.woniu.core.bean.BaseReq;
import com.woniu.core.bean.entity.UserInfoEntity;
import com.woniu.core.manage.UserInfoManage;
import com.woniu.core.utils.GsonUtil;
import com.woniu.core.utils.HttpUtil;
import com.woniu.core.xmpp.rxbus.event.BaseEvent;
import com.woniu.core.xmpp.rxbus.event.ChatMessageEvent;
import com.zhouyou.http.model.HttpParams;

import java.lang.reflect.Type;

/**
 * 积分红包
 */

public class IntegratingRedActivity extends BaseActivity {

    private QMUITopBarLayout qmui_bar;
    private Button btnLogin;
    private EditText etIntegral, redCount, etWishes;
    private TextView tvRedType;
    private LinearLayout mRedTypeLl;
    HttpUtil httpUtil = new HttpUtil(this);

    private int chatType = BaseEvent.CHAT_TYPE.PERSONAL.getTypeValue();

    private static final String TAG = "yancy";

    @Override
    protected int setContentViewId() {
        return R.layout.message_integer_red;
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        /**发送红包*/
        if (i == R.id.btn_login) {
            HttpParams mParams = getHttpParams();
            mParams.put("integral", etIntegral.getText().toString());
            mParams.put("type", istype());
            mParams.put("number", redCount.getText().toString());
            mParams.put("wishes", etWishes.getText().toString());
            mParams.put("send_type", String.valueOf(chatType));
            httpUtil.setOnRequestCallBack(new HttpUtil.OnRequestCallBack() {
                @Override
                public void onCall(int code, String data) {
                    Type type = new TypeToken<BaseReq<RedPacketBean>>() {
                    }.getType();

                    BaseReq<RedPacketBean> mReq = GsonUtil.getGson().fromJson(data, type);
                    if (TextUtils.equals(mReq.getStatus(), Config.Constant.SUCCESS)) {
                        if (mReq.getResult() != null) {
                            Intent intent = new Intent();
                            intent.putExtra("data", mReq.getResult());
                            setResult(10001, intent);
                            finish();
                            ToastUtils.showLong("红包已发送");
                        } else {
                            ToastUtils.showLong("红包未发送");
                        }
//                        startActivity(new Intent(IntegratingRedActivity.this,ChatActivity.class));
                    } else {
                        ToastUtils.showLong(mReq.getMsg());
                    }
                }
            }).postParms(mParams, Config.API.base_url + Config.API.send_red_pack);
        } else if (i == R.id.tv_red_type) {
            /**切换红包类型*/
            changeRedType();
        }
    }

    @Override
    public void initView() {
        qmui_bar = findViewById(R.id.qmui_bar);
        btnLogin = findViewById(R.id.btn_login);
        etIntegral = findViewById(R.id.et_integral);
        tvRedType = findViewById(R.id.tv_red_type);
        redCount = findViewById(R.id.red_count);
        etWishes = findViewById(R.id.et_wishes);
        mRedTypeLl = findViewById(R.id.mRedTypeLl);
        setTitles(qmui_bar, "发积分红包", true);
        btnLogin.setOnClickListener(this);
        tvRedType.setOnClickListener(this);
        int chatType = getIntent().getIntExtra("TYPE", BaseEvent.CHAT_TYPE.PERSONAL.getTypeValue());
        if (chatType == BaseEvent.CHAT_TYPE.PERSONAL.getTypeValue()) {
            changeRedType();
            redCount.setText("1");
            redCount.setEnabled(false);
        }
        mRedTypeLl.setVisibility(
                chatType == BaseEvent.CHAT_TYPE.PERSONAL.getTypeValue() ? View.GONE : View.VISIBLE);

    }

    /**
     * 红包类型切换
     */
    private void changeRedType() {
        if (tvRedType.getText().toString().equals("普通红包")) {
            tvRedType.setText("随机红包");
        } else if (tvRedType.getText().toString().equals("随机红包")) {
            tvRedType.setText("普通红包");
        }
    }

    /**
     * 红包类型
     */
    private String istype() {
        if (tvRedType.getText().toString().equals("普通红包")) {
            return "random";
        } else if (tvRedType.getText().toString().equals("随机红包")) {
            return "normal";
        }
        /**默认普通*/
        return "normal";
    }

    @Override
    public void initData() {
        chatType = getIntent().getIntExtra("TYPE", BaseEvent.CHAT_TYPE.PERSONAL.getTypeValue());
        Log.e(TAG, "initData: chatType=" + chatType);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
