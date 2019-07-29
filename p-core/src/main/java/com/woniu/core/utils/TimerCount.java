package com.woniu.core.utils;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.widget.TextView;

public class TimerCount extends CountDownTimer {

    private TextView v;
    public TimerCount(long millisInFuture, long countDownInterval, TextView v) {
        super(millisInFuture, countDownInterval);
        this.v=v;
    }

    @Override
    public void onFinish() {
        v.setText("获取验证码");
        v.setClickable(true);
    }

    @Override
    public void onTick(long millisUntilFinished) {
        v.setClickable(false);
//        v.setTextColor(Color.parseColor("#999999"));
        v.setTextColor(Color.parseColor("#E12C08"));
        v.setText("   " +millisUntilFinished / 1000 + "s后重发" + "");
    }
}
