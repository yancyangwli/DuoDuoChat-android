package com.chat.moduledynamic.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.SPUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chat.moduledynamic.R;
import com.chat.moduledynamic.adapter.viewholder.DynamicViewHolder;
import com.chat.moduledynamic.adapter.viewholder.ImageViewHolder;
import com.chat.moduledynamic.ui.widget.CommentListView;
import com.chat.moduledynamic.ui.widget.ExpandTextView;
import com.chat.moduledynamic.ui.widget.MultiImageView;
import com.chat.moduledynamic.ui.widget.PraiseListView;
import com.dktlh.ktl.provider.router.RouterPath;
import com.woniu.core.BaseApplication;
import com.woniu.core.api.Config;
import com.woniu.core.bean.entity.DynamicCommentData;
import com.woniu.core.bean.entity.DynamicItemData;
import com.woniu.core.bean.entity.DynamicLikeData;
import com.woniu.core.manage.UserInfoManageForJava;
import com.woniu.core.utils.ImageUtil;

import java.util.List;

public class DynamicAdapter extends BaseQuickAdapter<DynamicItemData, BaseViewHolder> {

    public final static int DYNAMIC_DETAIL = 0x1001;//动态详情
    public final static int DYNAMIC_LIST = 0x1002;//动态列表

    private OnDynamicListClickedListener onDynamicListClickedListener;

    public int dynamic_flag = DYNAMIC_LIST;//动态列表的标志


    public DynamicAdapter(Context context) {
        super(R.layout.item_dynamic_content);
        mContext = context;
    }

    public void setOnDynamicListClickedListener(OnDynamicListClickedListener onDynamicListClickedListener) {
        this.onDynamicListClickedListener = onDynamicListClickedListener;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == HEADER_VIEW) {
            return super.onCreateViewHolder(parent, viewType);
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dynamic_content, parent, false);
        if (DynamicViewHolder.TYPE_IMAGE == viewType) {
            return new ImageViewHolder(view);
        }
        return new DynamicViewHolder(view, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        if (dynamic_flag == DYNAMIC_LIST) {
            if (position == 0) {
                return super.getItemViewType(position);
            }
        }

        int itemType = -1;
        DynamicItemData item;
        if (dynamic_flag == DYNAMIC_LIST) {
            item = getData().get(position - 1);
        } else {
            item = getData().get(position);
        }

        if (item.getImages() != null && item.getImages().size() > 0) {
            itemType = DynamicViewHolder.TYPE_IMAGE;
        }
        return itemType;
    }

    private static final String TAG = "yancy";

    @Override
    protected void convert(final BaseViewHolder helper, final DynamicItemData item) {
        Log.e(TAG, "convert: =========item");

        final DynamicViewHolder holder = (DynamicViewHolder) helper;

        int user_id = SPUtils.getInstance().getInt(Config.Constant.DUODUO_USER_ID);

        if (item.getUser_id() == user_id && dynamic_flag == DYNAMIC_LIST) {
            ((DynamicViewHolder) helper).mTvDelete.setVisibility(View.VISIBLE);
        } else {
            ((DynamicViewHolder) helper).mTvDelete.setVisibility(View.GONE);
        }


        final String headUrl = item.getFriend_avatar();
        ImageUtil.INSTANCE.loadOriginalImage(mContext, headUrl, holder.mIvAvatar, R.mipmap.icon_default_head);

        String friend_remark = item.getFriend_remark();
        if (!TextUtils.isEmpty(friend_remark)) {
            holder.mTvName.setText(friend_remark);
        }

        String createTime = item.getCreated_at();
        if (!TextUtils.isEmpty(createTime)) {
            holder.mTvCreateTime.setText(createTime);
        }

        if (item.getHasFavorite()) {
            holder.mIvCollection.setImageResource(R.mipmap.ic_collection_yellow);
        } else {
            holder.mIvCollection.setImageResource(R.mipmap.ic_collection);
        }

        if (item.getHasLike()) {
            holder.mIvPraise.setImageResource(R.mipmap.ic_prise_red);

        } else {
            holder.mIvPraise.setImageResource(R.mipmap.ic_prise_gray);
        }

        String content = item.getContent();
        if (!TextUtils.isEmpty(content)) {
            holder.contentTv.setVisibility(View.VISIBLE);
            holder.contentTv.setExpand(item.isExpand());
            holder.contentTv.setExpandStatusListener(new ExpandTextView.ExpandStatusListener() {
                @Override
                public void statusChange(boolean isExpand) {
                    item.setExpand(isExpand);
                }
            });
//            holder.contentTv.setText(UrlUtils.formatUrlString(content));
            holder.contentTv.setText(content);
        } else {
            holder.contentTv.setVisibility(View.GONE);
        }

        holder.mIvComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDynamicListClickedListener != null) {
                    onDynamicListClickedListener.onCommentClicked(item, helper.getAdapterPosition());
                }
            }
        });

        //头像
        holder.mIvAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDynamicListClickedListener != null) {
                    onDynamicListClickedListener.onAvatarClicked(item, helper.getAdapterPosition());
                }
            }
        });

        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDynamicListClickedListener != null) {
                    onDynamicListClickedListener.onItemClicked(item, helper.getAdapterPosition());
                }
            }
        });


        if (holder.mTvDelete.getVisibility() == View.VISIBLE) {
            holder.mTvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onDynamicListClickedListener != null) {
                        onDynamicListClickedListener.onDeleteClicked(item, holder.getAdapterPosition());
                    }
                }
            });
        }

        holder.mIvCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getHasFavorite()) {
                    return;
                }

                if (onDynamicListClickedListener != null) {
                    onDynamicListClickedListener.onCollectClicked(item, holder.getAdapterPosition());
                }
            }
        });
        int itemViewType = helper.getItemViewType();
        if (itemViewType == DynamicViewHolder.TYPE_IMAGE) {
            imageData((ImageViewHolder) helper, item);
        }

        priseData(holder, item);

        commentData(holder, item);

//        else if (itemViewType == DynamicViewHolder.TYPE_URL) {
//            if (TextUtils.isEmpty(content)) {
//                String linkTitle = item.getLinkTitle();
//                holder.contentTv.setText(linkTitle);
//            }
//        }
    }

    private void imageData(ImageViewHolder holder, DynamicItemData item) {
        final List<String> photos = item.getImages();
        if (photos != null && photos.size() > 0) {
            holder.multiImageView.setVisibility(View.VISIBLE);
            holder.multiImageView.setList(photos);
            holder.multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (onDynamicListClickedListener != null) {
                        onDynamicListClickedListener.onImageItemClicked(photos, position);
                    }
                }
            });
        } else {
            holder.multiImageView.setVisibility(View.GONE);
        }
    }

    //点赞数据
    private void priseData(final DynamicViewHolder holder, final DynamicItemData item) {
        final List<DynamicLikeData> favortDatas = item.getLikes();
        Log.e(TAG, "priseData: favorDatas=" + favortDatas.toString());
        if (favortDatas != null && favortDatas.size() > 0) {//处理点赞列表
            holder.praiseListView.setOnItemClickListener(new PraiseListView.OnItemClickListener() {
                @Override
                public void onClick(int position) {
                    int user_id = SPUtils.getInstance().getInt(Config.Constant.DUODUO_USER_ID);
                    Log.e(TAG, "onClick: user_id=" + user_id);
                    if (user_id != favortDatas.get(position).getUser_id()) {
                        ARouter
                                .getInstance()
                                .build(RouterPath.MineCenter.PATH_USER_INFO)
                                .withInt("USER_ID", favortDatas.get(position).getUser_id())
                                .navigation();
                    }
                }
            });
            holder.praiseListView.setDatas(favortDatas);
            item.setMinePraisePosition(holder.praiseListView.mine_praise_positon);
            holder.praiseListView.setVisibility(View.VISIBLE);
        } else {
            item.setMinePraisePosition(-1);
            holder.praiseListView.setVisibility(View.GONE);
        }

        holder.mIvPraise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDynamicListClickedListener != null) {
                    onDynamicListClickedListener.onPraiseClicked(item, holder.getAdapterPosition());
                }
            }
        });
    }


    //评论数据
    private void commentData(final DynamicViewHolder holder, final DynamicItemData item) {
        final List<DynamicCommentData> commentsDatas = item.getComments();

        if (commentsDatas != null && commentsDatas.size() > 0) {//处理评论列表
            holder.commentList.setOnItemClickListener(new CommentListView.OnItemClickListener() {
                @Override
                public void onItemClick(int commentPosition) {
                    if (onDynamicListClickedListener != null) {
                        onDynamicListClickedListener.onCommentListItemClicked(item, holder.getAdapterPosition(), commentsDatas.get(commentPosition), commentPosition);
                    }
                }
            });
            holder.commentList.setOnItemLongClickListener(new CommentListView.OnItemLongClickListener() {
                @Override
                public void onItemLongClick(int commentPosition) {
                    if (onDynamicListClickedListener != null) {
                        onDynamicListClickedListener.onCommentListItemLongClicked(commentsDatas.get(commentPosition), commentPosition);
                    }
                }
            });
            holder.commentList.setDatas(commentsDatas);
            holder.commentList.setVisibility(View.VISIBLE);
        } else {
            holder.praiseListView.mine_praise_positon = -1;
            holder.commentList.setVisibility(View.GONE);
        }
    }

    public interface OnDynamicListClickedListener {
        void onItemClicked(DynamicItemData item, int position);//列表item点击

        void onCommentClicked(DynamicItemData item, int position);//评论图标点击

        void onCollectClicked(DynamicItemData item, int position);//收藏图标点击

        void onCommentListItemClicked(DynamicItemData item, int positioin, DynamicCommentData commentItem, int commentPosition);//评论列表item点击

        void onCommentListItemLongClicked(DynamicCommentData commentItem, int commentPosition);//评论列表长按

        void onPraiseClicked(DynamicItemData item, int position);//点赞图标的点击

        void onImageItemClicked(List<String> photos, int position);//图片的点击

        void onAvatarClicked(DynamicItemData item, int position);//头像的点击

        void onDeleteClicked(DynamicItemData itemData, int position);//删除
    }

}
