package com.cosven.message.dialog;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.cosven.message.R;
import com.cosven.message.impl.OnActionPopClickedListener;
import com.cosven.message.utils.YancyDisplayUtil;
import com.cosven.message.utils.YancyScreen;

public class ActionPopWindow extends PopupWindow implements View.OnClickListener {
    private View view;
    private TextView mShare, mCopy, mCollection, mForward;

    private OnActionPopClickedListener onActionPopClickedListener;

    public void setOnActionPopClickedListener(OnActionPopClickedListener onActionPopClickedListener) {
        this.onActionPopClickedListener = onActionPopClickedListener;
    }

    private static final String TAG = "yancy";

    public ActionPopWindow(Context context, final View v) {
        super(context);
        setFocusable(true);
        view = LayoutInflater.from(context).inflate(R.layout.dialog_action_pop, new LinearLayout(context), true);
        setBackgroundDrawable(null);
        setContentView(view);

        int[] location = new int[2];
        //获取在整个屏幕内的绝对坐标
        v.getLocationOnScreen(location);
        //获取状态栏的高度
        int actionBarHeight = YancyScreen.getActionBarHeight((Activity) context);
        //popY坐标位置
        int posY = 0;
        //判断当前文本是否被ActionBar遮挡
        if (location[1] >= actionBarHeight) {
            posY = -(v.getHeight() + YancyDisplayUtil.dip2px(context, 43));
        } else {
            posY = 0;
        }

        Log.e(TAG, "ActionPopWindow: w=" + v.getWidth());

        showAsDropDown(v, 0, posY);
        init();
    }

    private void init() {
        mShare = view.findViewById(R.id.share);
        mCopy = view.findViewById(R.id.copy);
        mCollection = view.findViewById(R.id.collection);
        mForward = view.findViewById(R.id.forward);

        mShare.setOnClickListener(this);
        mCopy.setOnClickListener(this);
        mCollection.setOnClickListener(this);
        mForward.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        dismiss();
        if (onActionPopClickedListener == null) {
            return;
        }
        if (i == R.id.share) {
            //分享
            onActionPopClickedListener.onShareListener();
        } else if (i == R.id.copy) {
            //复制
            onActionPopClickedListener.onCopyListener();
        } else if (i == R.id.collection) {
            //收藏
            onActionPopClickedListener.onCollectionListener();
        } else if (i == R.id.forward) {
            //转发
            onActionPopClickedListener.onForwardListener();
        }
    }
}
