package com.woniu.core.utils;

import android.content.Context;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.blankj.utilcode.util.RegexUtils;


/**
 * Created by Anlycal(远) on 2018/12/4.
 * 作用： ****
 */

public class CountTimeUtil {
    private  long currentCount = 0;//获取验证码当前显示的倒计时
    private  final long TIME = 60 * 1000L;
    private  final long INTERVAL = 1000L;

    private  TextView mTvCode;

    private static CountTimeUtil mTimeUtil;

    private CountTimeUtil() {
    }

    public static CountTimeUtil getInstance(){
        if (mTimeUtil == null){
            mTimeUtil = new CountTimeUtil();
        }
        return mTimeUtil;
    }


    //倒计时
    private  CountDownTimer mTimer = null;

    /**
     * 开始计时
     * @param etPhone  手机号码编辑控件
     * @param tv  验证码时间显示控件
     */
    public boolean  startCountTime(EditText etPhone,TextView tv){
        String phone = etPhone.getText().toString().trim();
        Context context = etPhone.getContext();
        if (TextUtils.isEmpty(phone)){
            Toast.makeText(context, "请输入手机号", Toast.LENGTH_SHORT).show();
            return false;
        }else if (!RegexUtils.isMobileExact(phone)){
            Toast.makeText(context, "手机号错误", Toast.LENGTH_SHORT).show();
            return false;
        }

        this.mTvCode = tv;
        this.mTvCode.setEnabled(false);
        if (this.mTimer == null){
            this.mTimer = new CountDownTimer(TIME, INTERVAL) {
                @Override
                public void onTick(long millisUntilFinished) {
                    currentCount = (millisUntilFinished / INTERVAL);
                    mTvCode.setText(currentCount + " s");
                }

                @Override
                public void onFinish() {
                    mTvCode.setText("重新获取");
                    mTvCode.setEnabled(true);
                }
            };
        }
        this.mTimer.start();
        return true;
    }

    public void reset(){
        if (this.mTimer != null){
            this.mTimer.onFinish();
            this.mTimer.cancel();
        }
        this.mTimer = null;
    }
}
