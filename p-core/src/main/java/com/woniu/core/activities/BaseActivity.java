package com.woniu.core.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.immersionbar.ImmersionBar;
import com.noober.background.BackgroundLibrary;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.woniu.core.R;
import com.woniu.core.utils.AppManager;
import com.woniu.core.utils.Tools;

import java.lang.ref.WeakReference;
import java.util.Calendar;

import com.zhouyou.http.model.HttpParams;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;


/**
 * ClassName:
 * Description:
 * Created by XL on 2018/5/16.
 * Modified by
 */

public abstract class BaseActivity extends AppCompatActivity implements BaseUI, View.OnClickListener {

    public Context context;
    public Activity activity;


    public int pageSize = 10;
    public int pageNum = 1;


    protected ProgressDialog progressDialog;
    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;


    private boolean isAlive = false;
    private boolean isRunning = false;
    private String resMsg;
    public QMUITipDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        BackgroundLibrary.inject(this); // 初始化背景图案控件
//        QMUIStatusBarHelper.translucent(this, Color.TRANSPARENT);
//        QMUIStatusBarHelper.setStatusBarLightMode(this);
        super.onCreate(savedInstanceState);

        initStatusBar();

        AppManager.get().add(this);
//        EventBusUtil.register(this);
        context = this;
        activity = this;
        isAlive = true;
        setContentView(setContentViewId());
        initView();
        initData();
//        setStatusBar();

    }

    protected void initStatusBar(){
        ImmersionBar.with(this)
                .statusBarDarkFont(true)
                .keyboardEnable(true)  //解决软键盘与底部输入框冲突问题
                //  .keyboardEnable(true, WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
                //                        | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)  //软键盘自动弹出
                .init();
    }

    @SuppressLint("ResourceAsColor")
    protected void setStatusBar() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isAlive = false;
        isRunning = false;
        progressDialog = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
    }


    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
//        if (dialog != null) {
//            dialog.dismissAllowingStateLoss();
//        }
    }

    protected abstract int setContentViewId();


    public boolean isAlive() {
        return isAlive && context != null;
    }

    public boolean isRunning() {
        return isRunning & isAlive();
    }

    public final void runUiThread(Runnable action) {
        if (isAlive() == false) {
            return;
        }
        runOnUiThread(action);
    }


    /**
     * 展示加载进度条,无标题
     *
     * @param stringResId
     */
    public void showProgressDialog(int stringResId) {
        try {
            showProgressDialog(null, context.getResources().getString(stringResId));
        } catch (Exception e) {
        }
    }

    /**
     * 展示加载进度条,无标题
     *
     * @param message
     */
    public void showProgressDialog(String message) {
        showProgressDialog(null, message);
    }

    /**
     * 展示加载进度条
     *
     * @param title   标题
     * @param message 信息
     */
    public void showProgressDialog(final String title, final String message) {
        runUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(context);
                }

//                progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.loading));
//                progressDialog.setContentView(R.layout.layout_progressbar);

                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (!TextUtils.isEmpty(title)) {
                    progressDialog.setTitle(title);
                }
                if (!TextUtils.isEmpty(message)) {
                    progressDialog.setMessage(message);
                }

                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
            }
        });
    }


    /**
     * 隐藏加载进度
     */
    public void dismissProgressDialog() {
        runUiThread(new Runnable() {
            @Override
            public void run() {
                //把判断写在runOnUiThread外面导致有时dismiss无效，可能不同线程判断progressDialog.isShowing()结果不一致
                if (progressDialog == null || progressDialog.isShowing() == false) {
                    return;
                }
                progressDialog.dismiss();
            }
        });
    }


    /**
     * 进度条
     */
    public void Loading(Context context) {
        // 弹正在加载
        dialog = new QMUITipDialog.Builder(context)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在加载")
                .create();
        dialog.show();
    }

    public void LoadError(Context context) {
        dialog = new QMUITipDialog.Builder(context)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                .setTipWord("加载失败")
                .create();
        dialog.show();
    }

    public void hideDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    protected HttpParams getHttpParams(){
        return new WeakReference<>(new HttpParams()).get();
    }


    @Override
    public void Toast(String msg) {
        Tools.showToast(this, msg);
    }

    //    private CommonDialog dialog;
//
//    private CommonDialog showDialog(String info) {
//        dialog = new CommonDialog.Builder()
//                .setContent(info)
//                .canCancelByOutSide(false)
//                .canCancelByKey(false)
//                .setOnClickListener(new DialogConfig.DialogClickListener() {
//                    @Override
//                    public void onConfirm() {
//                        App.getApp().loginOut();
//                    }
//
//                    @Override
//                    public void onCancel() {
//
//                    }
//                }).build();
//        //获取当前显示的activity
//        dialog.show(getSupportFragmentManager(), "LOGIN_DIALOG");
//        return dialog;
//    }


    // 返回
    @Override
    public void onBackPressed() {
        AppManager.get().finishActivity(this);
    }

    @Override
    public void finishs() {
        AppManager.get().finishActivity();
    }

    /**
     * 弹出提示框
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void showMessageNegativeDialog(String titile, String message) {
        new QMUIDialog.MessageDialogBuilder(this)
                .setTitle(titile)
                .setMessage(message)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction(0, "删除", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        Toast.makeText(activity, "删除成功", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })
                .create(mCurrentDialogStyle).show();
    }


    /**
     * 校验过程：
     * 1、从卡号最后一位数字开始，逆向将奇数位(1、3、5等等)相加。
     * 2、从卡号最后一位数字开始，逆向将偶数位数字，先乘以2（如果乘积为两位数，将个位十位数字相加，即将其减去9），再求和。
     * 3、将奇数位总和加上偶数位总和，结果应该可以被10整除。
     * 校验银行卡卡号
     */
    public static boolean checkBankCard(String bankCard) {
        if (bankCard.length() < 15 || bankCard.length() > 19) {
            return false;
        }
        char bit = getBankCardCheckCode(bankCard.substring(0, bankCard.length() - 1));
        if (bit == 'N') {
            return false;
        }
        return bankCard.charAt(bankCard.length() - 1) == bit;
    }

    /**
     * 从不含校验位的银行卡卡号采用 Luhn 校验算法获得校验位
     */
    public static char getBankCardCheckCode(String nonCheckCodeBankCard) {
        if (nonCheckCodeBankCard == null || nonCheckCodeBankCard.trim().length() == 0
                || !nonCheckCodeBankCard.matches("\\d+")) {
            //如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeBankCard.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }


    public  void setTitles(QMUITopBarLayout qmui_bar, String title, boolean isFinish){
        if (qmui_bar!=null){
            qmui_bar.setTitle(title);
        }
        toolbarEvent(qmui_bar,title,-1,isFinish);
    }

    public void setTitlesWithBackIconWhite(QMUITopBarLayout qmui_bar,String title,boolean isFinish){
        if (qmui_bar!=null){
            TextView mTvTitle = qmui_bar.setTitle(title);
            mTvTitle.setTextColor(Color.WHITE);
        }
        toolbarEvent(qmui_bar,title,R.mipmap.ic_arrow_left_white,isFinish);
    }

    private void toolbarEvent(QMUITopBarLayout qmui_bar,String title,int backIcon,boolean isFinish){
        ImageButton mBackView = qmui_bar.addLeftBackImageButton();
        if (backIcon == -1) {
            mBackView.setImageResource(R.mipmap.icon_arrow_left);
        }else {
            mBackView.setImageResource(backIcon);
        }
        if (isFinish) {
            mBackView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishs();
                }
            });
        }
    }


    @Override
    public void onClick(View v) {

    }

}
