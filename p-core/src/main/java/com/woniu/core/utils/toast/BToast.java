package com.woniu.core.utils.toast;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.woniu.core.R;


public class BToast extends Toast {

    /**
     * 图标状态 不显示图标
     */
    private static final int TYPE_HIDE = -1;
    /**
     * 图标状态 显示√
     */
    private static final int TYPE_TRUE = 0;
    /**
     * 图标状态 显示×
     */
    private static final int TYPE_FALSE = 1;

    /**
     * Toast所显示的图片
     */
    private ImageView toast_img;

    /**
     * 构造
     *
     * @param context
     */
    public BToast(Context context) {
        super(context);
    }

    /**
     * 显示一个纯文本吐司
     *
     * @param context 上下文
     * @param text    显示的文本
     */
    public static void showText(Context context, CharSequence text) {
        showToast(context, text, Toast.LENGTH_SHORT, TYPE_HIDE);
    }

    /**
     * 显示一个带图标的吐司
     *
     * @param context   上下文
     * @param text      显示的文本
     * @param isSucceed 显示【对号图标】还是【叉号图标】
     */
    public static void showText(Context context, CharSequence text, boolean isSucceed) {
        showToast(context, text, Toast.LENGTH_SHORT, isSucceed ? TYPE_TRUE : TYPE_FALSE);
    }

    /**
     * 显示一个纯文本吐司
     *
     * @param context 上下文
     * @param text    显示的文本
     * @param time    持续的时间
     */
    public static void showText(Context context, CharSequence text, int time) {
        showToast(context, text, time, TYPE_HIDE);
    }

    /**
     * 显示一个带图标的吐司
     *
     * @param context   上下文
     * @param text      显示的文本
     * @param time      持续的时间
     * @param isSucceed 显示【对号图标】还是【叉号图标】
     */
    public static void showText(Context context, CharSequence text, int time, boolean isSucceed) {
        showToast(context, text, time, isSucceed ? TYPE_TRUE : TYPE_FALSE);
    }

    /**
     * 显示Toast
     *
     * @param context 上下文
     * @param text    显示的文本
     * @param time    显示时长
     * @param imgType 图标状态
     */
    private static void showToast(Context context, CharSequence text, int time, int imgType) {
        // 初始化一个新的Toast对象
        BToast toast = initToast(context, text);

        // 设置显示时长
        if (time == Toast.LENGTH_LONG) {
            toast.setDuration(Toast.LENGTH_LONG);
        } else {
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        ImageView toast_img = toast.toast_img;
        // 判断图标是否该显示，显示√还是×
        if (imgType == TYPE_HIDE) {
            toast_img.setVisibility(View.GONE);
        } else {
            if (imgType == TYPE_TRUE) {
                toast_img.setBackgroundResource(R.drawable.toast_y);
            } else {
                toast_img.setBackgroundResource(R.drawable.toast_n);
            }
            toast_img.setVisibility(View.VISIBLE);
            ObjectAnimator.ofFloat(toast_img, "rotationY", 0, 180).setDuration(600).start();
        }

        // 显示Toast
        toast.show();
    }

    /**
     * 初始化Toast
     *
     * @param context 上下文
     * @param text    显示的文本
     */
    private static BToast initToast(Context context, CharSequence text) {
        BToast toast = new BToast(context);
        try {
            // 获取LayoutInflater对象
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // 由layout文件创建一个View对象
            View layout = inflater.inflate(R.layout.toast_layout, null);
            // 吐司上的图片
            toast.toast_img = (ImageView) layout.findViewById(R.id.toast_img);
            // 实例化ImageView和TextView对象
            TextView toast_text = (TextView) layout.findViewById(R.id.toast_text);
            toast_text.setText(text);
            toast.setView(layout);
            toast.setGravity(Gravity.CENTER, 0, 70);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toast;
    }


    public void cancel() {
        try {
            super.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}