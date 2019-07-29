package com.chat.moduledynamic.ui.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chat.moduledynamic.R;
import com.chat.moduledynamic.adapter.DynamicAdapter;
import com.chat.moduledynamic.bean.test.CommentConfig;
import com.dktlh.ktl.provider.router.RouterPath;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.woniu.core.activities.BaseActivity;
import com.woniu.core.api.Config;
import com.woniu.core.bean.BaseReq;
import com.woniu.core.bean.entity.DynamicCommentData;
import com.woniu.core.bean.entity.DynamicDetailEntity;
import com.woniu.core.bean.entity.DynamicItemData;
import com.woniu.core.bean.entity.DynamicLikeData;
import com.woniu.core.utils.AppManager;
import com.woniu.core.utils.GsonUtil;
import com.woniu.core.utils.HttpUtil;
import com.woniu.core.xmpp.rxbus.RxBus;
import com.zhouyou.http.model.HttpParams;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 动态详情
 */

@Route(path = RouterPath.DynamicCenter.PATH_DYNAMIC_DETAIL)
public class DynamicDetailActivity extends BaseActivity {
    private TextView mTvTitle;
    private EditText mEtInput;
    private ClassicsHeader mLoadHeader;

    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;

    private DynamicAdapter mDynamicAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private List<DynamicItemData> mDatas = new ArrayList<>();

    private int MOMENT_ID = -1;//动态id

    private HttpUtil mHttpUtil = new HttpUtil(this);
    private Disposable mSubscribe;

    @Override
    protected int setContentViewId() {
        return R.layout.activity_dynamic_detail;
    }

    @Override
    public void initView() {
        MOMENT_ID = getIntent().getIntExtra("MOMENT_ID", -1);
        initViewID();
        loadDetailData(false);
    }

    private CommentConfig mCommentConfig;

    @Override
    public void initData() {
        mTvTitle.setText(getString(R.string.dynamic_detail));

        mLoadHeader.setAccentColor(Color.parseColor("#666666"));

        mCommentConfig = new CommentConfig();

        mEtInput.setHint("评论");

        mDynamicAdapter = new DynamicAdapter(context);
        mDynamicAdapter.dynamic_flag = DynamicAdapter.DYNAMIC_DETAIL;
        mDynamicAdapter.setNewData(mDatas);

        mLinearLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mDynamicAdapter);

        initEvent();
    }

    private void initViewID() {
        mTvTitle = findViewById(R.id.mToolbarTitle);
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRecyclerView = findViewById(R.id.recyclerView);
        mEtInput = findViewById(R.id.et_input);
        mLoadHeader = findViewById(R.id.header);
    }

    private void initEvent() {
        findViewById(R.id.mToolbarBack).setOnClickListener(this);
        findViewById(R.id.tv_send).setOnClickListener(this);


        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                loadDetailData(true);
            }
        });

        KeyboardUtils.registerSoftInputChangedListener(this, new KeyboardUtils.OnSoftInputChangedListener() {
            @Override
            public void onSoftInputChanged(int height) {
                if (height < 100){
                    mEtInput.setText("");
                    mEtInput.setHint("评论");
                    mCommentConfig.commentType = CommentConfig.Type.PUBLIC;
                }
            }
        });

        mDynamicAdapter.setOnDynamicListClickedListener(new DynamicAdapter.OnDynamicListClickedListener() {
            @Override
            public void onItemClicked(DynamicItemData item, int position) {

            }

            @Override
            public void onAvatarClicked(DynamicItemData item, int position) {
                ARouter
                        .getInstance()
                        .build(RouterPath.MineCenter.PATH_USER_INFO)
                        .withInt("USER_ID", item.getUser_id())
                        .navigation();
            }

            @Override
            public void onCommentClicked(DynamicItemData item, int position) {
                mEtInput.setHint("评论");
                mCommentConfig.commentType = CommentConfig.Type.PUBLIC;
                KeyboardUtils.showSoftInput(mEtInput);
            }

            @Override
            public void onCollectClicked(DynamicItemData item, int position) {
                collectionMoment(item);
            }

            @Override
            public void onCommentListItemClicked(DynamicItemData item, int positioin, DynamicCommentData commentItem, int commentPosition) {
                int user_id = SPUtils.getInstance().getInt(Config.Constant.DUODUO_USER_ID);
                if (user_id != commentItem.getFrom_user_id()) {
                    mEtInput.setHint("回复" + commentItem.getFrom_friend_remark());
                    mCommentConfig.commentType = CommentConfig.Type.REPLY;
                    mCommentConfig.replyComment = commentItem;
                }else {
                    mEtInput.setHint("评论");
                    mCommentConfig.commentType = CommentConfig.Type.PUBLIC;
                }
                KeyboardUtils.showSoftInput(mEtInput);
            }

            @Override
            public void onCommentListItemLongClicked(DynamicCommentData commentItem, int commentPosition) {

            }

            @SuppressLint("CheckResult")
            @Override
            public void onImageItemClicked(List<String> photos, int position) {
                Observable.just(photos)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(new Function<List<String>, ObservableSource<List<LocalMedia>>>() {
                            @Override
                            public ObservableSource<List<LocalMedia>> apply(List<String> strings) throws Exception {
                                List<LocalMedia> selectList = new ArrayList<>();
                                for (String p : photos) {
                                    LocalMedia media = new LocalMedia();
                                    media.setPath(p);
                                    selectList.add(media);
                                }
                                return Observable.just(selectList);
                            }
                        })
                        .subscribe(new Consumer<List<LocalMedia>>() {
                            @Override
                            public void accept(List<LocalMedia> localMedia) throws Exception {
                                PictureSelector.create(DynamicDetailActivity.this).themeStyle(R.style.picture_style).openExternalPreview(position, localMedia);
                            }
                        });
            }

            @Override
            public void onPraiseClicked(DynamicItemData item, int position) {
                praiseMoment(item);
            }

            @Override
            public void onDeleteClicked(DynamicItemData itemData, int position) {

            }
        });
    }

    /**
     * 加载详情数据
     */
    private void loadDetailData(final boolean isRefresh) {

        if (!isRefresh) {
            showProgressDialog(getString(R.string.waiting));
        }

        mHttpUtil.setOnRequestCallBack(new HttpUtil.OnRequestCallBack() {
            @Override
            public void onCall(int code, String data) {
                if (!isRefresh) {
                    dismissProgressDialog();
                } else {
                    mRefreshLayout.finishRefresh();
                }

                if (code == -1) {
                    ToastUtils.showLong(data);
                    return;
                }

                Type type = new TypeToken<BaseReq<DynamicDetailEntity>>() {
                }.getType();

                BaseReq<DynamicDetailEntity> mReq = GsonUtil.getGson().fromJson(data, type);

                if (TextUtils.equals(mReq.getStatus(), Config.Constant.SUCCESS)) {
                    DynamicDetailEntity result = mReq.getResult();
                    if (result == null) {
                        return;
                    }
                    addData(result);
                } else {
                    ToastUtils.showLong(mReq.getMsg());
                }
            }
        }).getParms(getHttpParams(), String.format(Config.API.moment_dtail, MOMENT_ID));
    }

    private void addData(DynamicDetailEntity detailEntity) {
        mSubscribe = Observable.just(detailEntity)
                .subscribeOn(Schedulers.newThread())
                .flatMap(new Function<DynamicDetailEntity, ObservableSource<DynamicItemData>>() {
                    @Override
                    public ObservableSource<DynamicItemData> apply(DynamicDetailEntity result) throws Exception {

                        return Observable.just(new DynamicItemData(
                                result.getMoment().getComment_num(),
                                result.getComments(),
                                result.getMoment().getContent(),
                                result.getMoment().getCreated_at(),
                                result.getMoment().getImages(),
                                result.getMoment().getLiked_num(),
                                result.getLikes(),
                                result.getMoment().getMoment_id(),
                                result.getMoment().getStick_time(),
                                result.getMoment().getUpdated_at(),
                                result.getMoment().getUser_id(),
                                result.getMoment().getFriend_avatar(),
                                result.getMoment().getFriend_remark(),
                                result.getMoment().getHasLike(),
                                result.getMoment().getHasFavorite(),
                                result.getMoment().isExpand(),
                                -1
                        ));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DynamicItemData>() {
                    @Override
                    public void accept(DynamicItemData dynamicItemData) throws Exception {
                        mDatas.clear();
                        mDatas.add(dynamicItemData);
                        mCommentConfig.dynamicItemData = dynamicItemData;
                        mDynamicAdapter.notifyDataSetChanged();
                    }
                });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mToolbarBack) {
            AppManager.get().finishActivity(this);
        }else if (v.getId() == R.id.tv_send){
            if (mDatas.size() > 0){
                momentComment();
            }
        }
    }

    /**
     * 收藏
     */
    private void collectionMoment(DynamicItemData itemData) {
        HttpParams mParams = getHttpParams();
        mParams.put("type", Config.Favorite.MONENT);
        mParams.put("target_id", String.valueOf(itemData.getMoment_id()));

        showProgressDialog(getString(R.string.waiting));
        mHttpUtil.setOnRequestCallBack(new HttpUtil.OnRequestCallBack() {
            @Override
            public void onCall(int code, String data) {
                dismissProgressDialog();
                if (code == -1) {
                    ToastUtils.showLong(data);
                    return;
                }
                BaseReq mReq = GsonUtil.getGson().fromJson(data, BaseReq.class);
                ToastUtils.showLong(mReq.getMsg());
                if (TextUtils.equals(mReq.getStatus(), Config.Constant.SUCCESS)) {
                    itemData.setHasFavorite(!itemData.getHasFavorite());
                    mDynamicAdapter.notifyDataSetChanged();
                }
            }
        }).postParms(mParams, Config.API.favorite);
    }

    /**
     * 点赞动态
     *
     * @param itemData 动态数据
     */
    private void praiseMoment(DynamicItemData itemData) {
        HttpParams mParams = getHttpParams();
        mParams.put("moment_id", String.valueOf(itemData.getMoment_id()));

        showProgressDialog(getString(R.string.waiting));
        //朋友圈点赞
        mHttpUtil.setOnRequestCallBack(new HttpUtil.OnRequestCallBack() {
            @Override
            public void onCall(int code, String data) {
                dismissProgressDialog();
                if (code == -1) {
                    ToastUtils.showLong(data);
                    return;
                }

                Type type = new TypeToken<BaseReq<DynamicLikeData>>() {
                }.getType();

                BaseReq<DynamicLikeData> mReq = GsonUtil.getGson().fromJson(data, type);
                if (TextUtils.equals(mReq.getStatus(), Config.Constant.SUCCESS)) {
                    if (mReq.getResult() != null) {
                        ToastUtils.showLong("点赞成功");
                        itemData.setHasLike(true);
                        itemData.getLikes().add(mReq.getResult());
                    } else {
                        ToastUtils.showLong("已取消点赞");
                        itemData.setHasLike(false);
                        if (itemData.getMinePraisePosition() != -1){
                            itemData.getLikes().remove(itemData.getMinePraisePosition());
                        }
                    }
                    mDynamicAdapter.notifyDataSetChanged();
                    postRefresh(mDatas.get(0));
                } else {
                    ToastUtils.showLong(mReq.getMsg());
                }
            }
        }).postParms(mParams, Config.API.moment_like);
    }

    /**
     * 评论
     */
    private void momentComment(){
        String content = mEtInput.getText().toString().trim();
        if (TextUtils.isEmpty(content)){
            ToastUtils.showLong("评论内容不能为空");
            return;
        }

        HttpParams mParams = getHttpParams();
        mParams.put("moment_id",String.valueOf(mCommentConfig.dynamicItemData.getMoment_id()));
        mParams.put("content",content);

        if (mCommentConfig.commentType == CommentConfig.Type.REPLY){
            mParams.put("comment_pid",String.valueOf(mCommentConfig.replyComment.getComment_id()));
            mParams.put("to_user_id",String.valueOf(mCommentConfig.replyComment.getFrom_user_id()));
        }

        showProgressDialog(getString(R.string.waiting));
        mHttpUtil.setOnRequestCallBack(new HttpUtil.OnRequestCallBack() {
            @Override
            public void onCall(int code, String data) {
                dismissProgressDialog();
//                Log.i("FAN", "onCall: 评论返回---> " + data);
                if (code == -1){
                    ToastUtils.showLong(data);
                    return;
                }

                Type type = new TypeToken<BaseReq<DynamicCommentData>>(){}.getType();

                BaseReq<DynamicCommentData> mReq = GsonUtil.getGson().fromJson(data,type);
                if (TextUtils.equals(mReq.getStatus(), Config.Constant.SUCCESS)){
                    if (mReq.getResult() != null){
//                        mReq.getResult().setFrom_friend_remark(mCommentConfig.dynamicItemData.getFriend_remark());
//                        if (mCommentConfig.commentType == CommentConfig.Type.REPLY) {
//                            mReq.getResult().setTo_friend_remark(commentConfig.replyComment.getTo_friend_remark());
//                        }
                        mDatas.get(0).getComments().add(mReq.getResult());
                        mDynamicAdapter.notifyItemChanged(0);

                        postRefresh(mDatas.get(0));
                    }
                    KeyboardUtils.hideSoftInput(mEtInput);
                }else {
                    ToastUtils.showLong(mReq.getMsg());
                }
            }
        }).postParms(mParams, Config.API.moment_comment);
    }


    protected void postRefresh(DynamicItemData itemData){
        RxBus.getInstance().post(itemData);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscribe != null && mSubscribe.isDisposed()) {
            mSubscribe.dispose();
        }
        mSubscribe = null;
        mHttpUtil = null;
    }
}
