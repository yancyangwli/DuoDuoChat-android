package com.chat.moduledynamic.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.chat.moduledynamic.R;
import com.chat.moduledynamic.ui.fragment.DynamicFragment;
import com.dktlh.ktl.provider.router.RouterPath;
import com.woniu.core.activities.BaseActivity;
import com.woniu.core.bean.entity.FriendInfoEntity;
import com.woniu.core.xmpp.rxbus.RxBus;
import com.woniu.core.xmpp.rxbus.event.ChatMessageEvent;
import io.reactivex.functions.Consumer;

import static com.woniu.core.api.Config.Constant.LOOK_MY_MOMENT;
import static com.woniu.core.api.Config.Constant.LOOK_OTHER_MOMENT;

@Route(path = RouterPath.DynamicCenter.PATH_DYNAMIC_LIST)
public class DynamicActivity extends BaseActivity {

    private int look_type = LOOK_MY_MOMENT;

    @Override
    protected int setContentViewId() {
        return R.layout.activity_dynamic;
    }

    @Override
    public void initView() {
        look_type = getIntent().getIntExtra("LOOK_TYPE",LOOK_MY_MOMENT);

        DynamicFragment mFragment = new DynamicFragment();
//        Log.i("FAN", "initView: ---->" + getBottomStatusHeight(this));
        Bundle bundle = new Bundle();
        bundle.putBoolean("SHOW_BACK",true);
        bundle.putInt("LOOK_TYPE",look_type);
        if (look_type == LOOK_OTHER_MOMENT){
            FriendInfoEntity infoEntity = (FriendInfoEntity) getIntent().getSerializableExtra("USER_INFO");
            bundle.putInt("USER_ID",infoEntity.getUser_id());
            bundle.putString("USER_AVATAR",infoEntity.getAvatar());
            if (infoEntity.getFriend_info() != null && TextUtils.isEmpty(infoEntity.getFriend_info().getFriend_remark())) {
                if (!TextUtils.isEmpty(infoEntity.getFriend_info().getFriend_remark())) {
                    bundle.putString("USER_NAME", infoEntity.getFriend_info().getFriend_remark());
                }
            }else {
                bundle.putString("USER_NAME",infoEntity.getNickname());
            }
            bundle.putString("USER_MOMENT_BG",infoEntity.getMoment_cover());
        }

        mFragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fl_content,mFragment)
                .commitNow();

    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {

    }
}
