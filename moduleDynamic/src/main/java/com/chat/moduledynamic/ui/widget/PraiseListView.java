package com.chat.moduledynamic.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.blankj.utilcode.util.SPUtils;
import com.chat.moduledynamic.R;
import com.chat.moduledynamic.spannable.CircleMovementMethod;
import com.chat.moduledynamic.spannable.SpannableClickable;
import com.woniu.core.BaseApplication;
import com.woniu.core.api.Config;
import com.woniu.core.bean.entity.DynamicLikeData;

import java.util.List;

/**
 * Created by yiwei on 16/7/9.
 */
public class PraiseListView extends TextView {


    private int itemColor;
    private int itemSelectorColor;
    private List<DynamicLikeData> datas;
    private OnItemClickListener onItemClickListener;
    public int mine_praise_positon = -1;//我所点赞的位置

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public PraiseListView(Context context) {
        super(context);
    }

    public PraiseListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
    }

    public PraiseListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.PraiseListView, 0, 0);
        try {
            //textview的默认颜色
            itemColor = typedArray.getColor(R.styleable.PraiseListView_item_color, getResources().getColor(R.color.praise_item_default));
            itemSelectorColor = typedArray.getColor(R.styleable.PraiseListView_item_selector_color, getResources().getColor(R.color.praise_item_default));

        } finally {
            typedArray.recycle();
        }
    }

    public List<DynamicLikeData> getDatas() {
        return datas;
    }

    public void setDatas(List<DynamicLikeData> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }


    public void notifyDataSetChanged() {
        SpannableStringBuilder builder = new SpannableStringBuilder();

        int user_id = SPUtils.getInstance().getInt(Config.Constant.DUODUO_USER_ID);

        if (datas != null && datas.size() > 0) {
            //添加点赞图标
            builder.append(setImageSpan());
            DynamicLikeData item = null;
            for (int i = 0; i < datas.size(); i++) {
                item = datas.get(i);
                if (item != null) {
                    builder.append(setClickableSpan(item.getFrom_friend_remark(), i));
                    if (item.getUser_id() == user_id) {
                        mine_praise_positon = i;
                    }
                    if (i != datas.size() - 1) {
                        builder.append(", ");
                    }
                }
            }
        }

        setText(builder);
        setMovementMethod(new CircleMovementMethod(itemSelectorColor));
    }


    private SpannableString setImageSpan() {
        String text = "  ";
        SpannableString imgSpanText = new SpannableString(text);
        imgSpanText.setSpan(new ImageSpan(BaseApplication.Companion.getContext(), R.mipmap.ic_prise_gray_36, DynamicDrawableSpan.ALIGN_BASELINE),
                0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return imgSpanText;
    }

    @NonNull
    private SpannableString setClickableSpan(String textStr, final int position) {
        SpannableString subjectSpanText = new SpannableString(textStr + "");
        subjectSpanText.setSpan(new SpannableClickable(itemColor) {
                                    @Override
                                    public void onClick(View widget) {
                                        if (onItemClickListener != null) {
                                            onItemClickListener.onClick(position);
                                        }
                                    }
                                }, 0, subjectSpanText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return subjectSpanText;
    }


    public static interface OnItemClickListener {
        public void onClick(int position);
    }
}
