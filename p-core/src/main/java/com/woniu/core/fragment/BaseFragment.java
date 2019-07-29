package com.woniu.core.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.woniu.core.R;
import com.woniu.core.activities.BaseActivity;
import com.woniu.core.utils.AppManager;
import com.woniu.core.utils.Tools;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.zhouyou.http.model.HttpParams;

import java.lang.ref.WeakReference;

public abstract class BaseFragment extends Fragment implements View.OnClickListener {

    public View inflate;
    public FrameLayout base_root;
    public Unbinder bind;
    public QMUITopBarLayout mTopBar;
    public QMUITipDialog tipDialog;
    private View fake_status_bar;

    protected Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        QMUIStatusBarHelper.translucent(getActivity());
        QMUIStatusBarHelper.setStatusBarLightMode(getActivity());
        inflate = View.inflate(getActivity(), R.layout.base_fragment, null);
        base_root = inflate.findViewById(R.id.base_root);
        fake_status_bar = inflate.findViewById(R.id.fake_status_bar);
//        mTopBar = inflate.findViewById(R.id.topbarss);
        if (setView() != 0) {
            View view =inflate.inflate(getActivity(),setView(), null);
            base_root.addView(view);
            bind = ButterKnife.bind(this, inflate);
        }
//        mTopBar.setBackgroundColor(Color.parseColor("#ffffff"));
//        mTopBar.setTitle("111");
//        mTopBar.setBackgroundResource(R.mipmap-xxhdpi.home_bg);

        setStatusView();
        initView();
        initData();
        addAction();
        return inflate;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;

    }

    private void setStatusView() {
//        fake_status_bar.setVisibility(View.VISIBLE);
        int statusBarHeight = getStatusBarHeight(getContext());
        //设置假状态栏高度
        LinearLayout.LayoutParams statusBarParams = (LinearLayout.LayoutParams) fake_status_bar.getLayoutParams();
        statusBarParams.height = statusBarHeight;
        fake_status_bar.setLayoutParams(statusBarParams);
    }

    //获得状态栏的高度
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            result = context.getResources().getDimensionPixelOffset(resId);
        }
        return result;
    }

    // 点击方法
    public void addAction() {
    }


    // fb
    public void initView() {
    }


    // 添加试图
    public abstract int setView();
    // 点击事件

//    public void setTitleTv(CharSequence text) {
//        mTopBar.setTitle(text + "");
//    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bind != null && bind != Unbinder.EMPTY) {
            bind.unbind();
            bind = null;
        }
    }

    public void Loading(Context context) {
        // 弹正在加载
        tipDialog = new QMUITipDialog.Builder(context)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在加载")
                .create();
        tipDialog.show();
    }

    public void LoadError(Context context) {
        tipDialog = new QMUITipDialog.Builder(context)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                .setTipWord("加载失败")
                .create();
        tipDialog.show();
    }

    public void hideDialog() {
        if (tipDialog != null && tipDialog.isShowing()) {
            tipDialog.dismiss();
            tipDialog = null;
        }
    }

    public void finish() {
        AppManager.get().finishActivity();
    }

    public void Toast(String msg) {
        Tools.showToast(getActivity(), msg);
    }

    public void showDidalog(String title, String message, final Dialogclick dialogclick) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogclick.onclick();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public interface Dialogclick {
        void onclick();
    }

    public void initData() {

    }

    public void setTitles(QMUITopBarLayout qmui_bar, String title, boolean isFinish) {
        if (qmui_bar != null) {
            qmui_bar.setTitle(title);
        }
        if (isFinish) {
            qmui_bar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    public void setTitlesWithColorWihte(QMUITopBarLayout qmui_bar, String title, boolean isFinish) {

    }

    public void showPrograssBar(String title) {
        ((BaseActivity) getActivity()).showProgressDialog(title);
    }

    public void dismissPrograssBar() {
        ((BaseActivity) getActivity()).dismissProgressDialog();
    }


    protected HttpParams getHttpParams() {
        return new WeakReference<>(new HttpParams()).get();
    }

    @Override
    public void onClick(View v) {

    }
}
