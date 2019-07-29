package com.chat.moduledynamic.adapter.viewholder;

import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chat.moduledynamic.R;
import com.chat.moduledynamic.ui.widget.CommentListView;
import com.chat.moduledynamic.ui.widget.ExpandTextView;
import com.chat.moduledynamic.ui.widget.PraiseListView;

public  class DynamicViewHolder extends BaseViewHolder {
    public final static int TYPE_URL = 1;
    public final static int TYPE_IMAGE = 2;
    public final static int TYPE_VIDEO = 3;

    public int viewType;

    public ImageView mIvAvatar;
    public TextView mTvName;
    public TextView mTvCreateTime;
    public ImageView mIvComment;
    public ImageView mIvPraise;
    public ImageView mIvCollection;
    public TextView mTvDelete;

    /** 动态的内容 */
    public ExpandTextView contentTv;
    public TextView timeTv;
    /** 点赞列表*/
    public PraiseListView praiseListView;

    public LinearLayout digCommentBody;

    /** 评论列表 */
    public CommentListView commentList;

    public DynamicViewHolder(View view,int viewType) {
        super(view);
        initView(view);
    }

    private void initView(View view) {
        ViewStub viewStub = (ViewStub) itemView.findViewById(R.id.viewStub);

        initSubView(viewType, viewStub);

        mTvDelete = itemView.findViewById(R.id.tv_delete);
        mIvAvatar = itemView.findViewById(R.id.iv_avatar);
        mTvName = itemView.findViewById(R.id.tv_name);
        mTvCreateTime = itemView.findViewById(R.id.tv_time);
        mIvComment = itemView.findViewById(R.id.iv_comment);
        mIvPraise = itemView.findViewById(R.id.iv_praise);
        mIvCollection = itemView.findViewById(R.id.iv_collection);

        contentTv = (ExpandTextView) itemView.findViewById(R.id.contentTv);
        timeTv = (TextView) itemView.findViewById(R.id.tv_time);
        praiseListView = (PraiseListView) itemView.findViewById(R.id.praiseListView);

        digCommentBody = (LinearLayout) itemView.findViewById(R.id.digCommentBody);
        commentList = (CommentListView)itemView.findViewById(R.id.commentList);

    }

    public void initSubView(int viewType, ViewStub viewStub){};


}
