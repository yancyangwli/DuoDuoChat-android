package com.chat.moduledynamic.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.*;
import com.alibaba.android.arouter.launcher.ARouter;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.chat.moduledynamic.R;
import com.chat.moduledynamic.adapter.DynamicAdapter;
import com.chat.moduledynamic.bean.test.CommentConfig;
import com.chat.moduledynamic.ui.activity.DynamicDetailActivity;
import com.chat.moduledynamic.ui.activity.PublishDynamicActivity;
import com.chat.moduledynamic.ui.widget.CommentListView;
import com.dktlh.ktl.provider.router.RouterPath;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.woniu.core.activities.BaseActivity;
import com.woniu.core.api.Config;
import com.woniu.core.bean.BaseReq;
import com.woniu.core.bean.entity.*;
import com.woniu.core.fragment.BaseFragment;
import com.woniu.core.manage.UserInfoManage;
import com.woniu.core.manage.UserInfoManageForJava;
import com.woniu.core.utils.*;
import com.woniu.core.xmpp.rxbus.RxBus;
import com.zhouyou.http.model.HttpParams;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.woniu.core.api.Config.Constant.*;

/**
 * 动态列表界面
 */
public class DynamicFragment extends BaseFragment {
    public static final int SELECT_BACK = 60001;
    public static final int RELEASE_DYNAMIC = 60002;

    private RecyclerView mRecyclerView;
    private SmartRefreshLayout mRefreshLayout;
    private ImageView parallax;
    private CircleImageView mToolbarAvatar;
    private Toolbar toolbar;
    public LinearLayout mLlInput;
    private EditText mEtInput;
    private RelativeLayout mRlBodyLayout;
    private View mViewStub;
    private ImageView mIvBack;
    private TextView mTvTitle;

    private int mOffset = 0;
    private int mScrollY = 0;
    private int currentKeyboardH;
    private int selectCircleItemH;
    private int screenHeight;
    private int editTextBodyHeight;
    private int selectCommentItemOffset;

    public CommentConfig commentConfig;

    public BottomNavigationBar mNavigationBar;

    private DynamicAdapter mDynamicAdapter;
    private LinearLayoutManager mLinearLayoutManager;


    private HttpUtil mHttpUtil = new HttpUtil(getContext());

    private List<DynamicItemData> mDynamicDatas = new ArrayList<>();

    private List<LocalMedia> mBigImageList = new ArrayList<>();

    private int page = 1;
    private Disposable mBigImageSubscribe;

    private int look_type = LOOK_ALL;//查看朋友圈类型

    private int user_id = -1;//用户id

    private String request_url = Config.API.moment_friend_all;//请求地址 默认所有
    private Disposable mDynamicDispose;
    private Disposable mRefreshDispose;

    private String mUserAvatar = "";//用户头像
    private String mUserName = "";//用户名
    private String mUserMomentBg = "";//用户朋友圈背景图片

    private static final String TAG = "yancy";

    @Override
    public int setView() {
        return R.layout.fragment_dynamic;
    }

    @Override
    public void initView() {
        initContent();
        initHead();
        setViewTreeObserver();
        initEvent();
    }


    private void initContent() {
        mDynamicAdapter = new DynamicAdapter(getContext());

        mIvBack = inflate.findViewById(R.id.iv_back);
        Bundle bundle = getArguments();
        if (bundle != null) {
            look_type = bundle.getInt("LOOK_TYPE", LOOK_ALL);//默认查看所有的动态列表
            if (look_type == LOOK_OTHER_MOMENT) {//查看好友的动态列表
                user_id = bundle.getInt("USER_ID", -1);
                mUserAvatar = bundle.getString("USER_AVATAR");
                mUserName = bundle.getString("USER_NAME");
                mUserMomentBg = bundle.getString("USER_MOMENT_BG");//好友动态的封面
                inflate.findViewById(R.id.iv_release).setVisibility(View.GONE);

                Log.e(TAG, "initContent: user_id=" + user_id);
                Log.e(TAG, "initContent: mUserAvatar=" + mUserAvatar);
                Log.e(TAG, "initContent: mUserName=" + mUserName);
                Log.e(TAG, "initContent: mUserMomentBg=" + mUserMomentBg);

            } else {//查看自己的动态
                initMineInfo();
            }

            boolean show_back = bundle.getBoolean("SHOW_BACK");//判断是否显示返回键
            if (show_back) {
                mIvBack.setVisibility(View.VISIBLE);
            }
        } else {
            initMineInfo();
        }
        Log.e(TAG, "initContent: look_type=" + look_type);
        Log.e(TAG, "initContent: LOOK_ALL=" + LOOK_ALL);
        if (look_type == LOOK_ALL) {
            request_url = Config.API.moment_friend_all;
        } else if (look_type == LOOK_MY_MOMENT) {
            request_url = Config.API.moment_mine;
        } else if (look_type == LOOK_OTHER_MOMENT) {
            request_url = Config.API.moment_friend;
        }
        Log.e(TAG, "initContent: request_url=" + request_url);
        mRecyclerView = inflate.findViewById(R.id.recyclerView);
        mRefreshLayout = inflate.findViewById(R.id.refreshLayout);
        parallax = inflate.findViewById(R.id.parallax);
        toolbar = inflate.findViewById(R.id.toolbar);
        mTvTitle = inflate.findViewById(R.id.title);
        mToolbarAvatar = inflate.findViewById(R.id.toolbar_avatar);
        mLlInput = inflate.findViewById(R.id.ll_input);
        mEtInput = inflate.findViewById(R.id.et_input);

//        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, VERTICAL));
        mDynamicAdapter.setNewData(mDynamicDatas);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mDynamicAdapter);
        ImageUtil.INSTANCE.loadOriginalImage(getContext(), mUserAvatar, mToolbarAvatar, R.mipmap.icon_default_head);
        ImageUtil.INSTANCE.loadOriginalImage(getContext(), mUserMomentBg, parallax, R.mipmap.icon_default_head);

        mTvTitle.setText(mUserName);

        loadDynimacData(false);
    }

    private void initMineInfo() {
        UserInfoEntity infoEntity = UserInfoManage.Companion.getGetInstance().getMUserInfoEntity();
        if (infoEntity != null) {
            mUserAvatar = infoEntity.getAvatar();
            mUserName = infoEntity.getNickname();
            mUserMomentBg = infoEntity.getMoment_cover();//我的动态
        }
    }

    private void initHead() {
        View header = LayoutInflater.from(getContext()).inflate(R.layout.item_dynamic_header, mRecyclerView, false);
        mViewStub = header.findViewById(R.id.bg_view_stub);

        mDynamicAdapter.addHeaderView(header);

        ImageView mIvAvatar = mDynamicAdapter.getHeaderLayout().findViewById(R.id.iv_avatar);

        TextView mTvName = mDynamicAdapter.getHeaderLayout().findViewById(R.id.mTvHeaderName);

        ImageUtil.INSTANCE.loadOriginalImage(getContext(), mUserAvatar, mIvAvatar, R.mipmap.icon_default_head);

        mTvName.setText(mUserName);
    }

    private void initEvent() {
        inflate.findViewById(R.id.iv_release).setOnClickListener(this);
        inflate.findViewById(R.id.tv_send).setOnClickListener(this);
        mViewStub.setOnClickListener(this);
        parallax.setOnClickListener(this);
        if (mIvBack.getVisibility() == View.VISIBLE) {
            mIvBack.setOnClickListener(this);
        }


        mDynamicDispose = RxBus.getInstance()
                .toObserverable(DynamicItemData.class)
                .observeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(itemData -> refreshData(itemData));

        mDynamicAdapter.setOnDynamicListClickedListener(new DynamicAdapter.OnDynamicListClickedListener() {
            //动态列表item的点击
            @Override
            public void onItemClicked(DynamicItemData item, int position) {
                Intent intent = new WeakReference<>(new Intent(mContext, DynamicDetailActivity.class)).get();
                intent.putExtra("MOMENT_ID", item.getMoment_id());
                mContext.startActivity(intent);
            }

            //头像的点击
            @Override
            public void onAvatarClicked(DynamicItemData item, int position) {
                int user_id = SPUtils.getInstance().getInt(Config.Constant.DUODUO_USER_ID);

                if (user_id != item.getUser_id()) {
                    ARouter
                            .getInstance()
                            .build(RouterPath.MineCenter.PATH_USER_INFO)
                            .withInt("USER_ID", item.getUser_id())
                            .navigation();
                }
            }

            //评论的点击
            @Override
            public void onCommentClicked(DynamicItemData item, int position) {//评论图标的点击
                CommentConfig config = new WeakReference<>(new CommentConfig()).get();
                config.dynamicItemData = item;
                config.circlePosition = position;
                config.commentType = CommentConfig.Type.PUBLIC;
                if (mLlInput != null) {
                    commentConfig = config;
                }
                updateEditTextBodyVisible(View.VISIBLE, config);
            }

            //收藏的点击
            @Override
            public void onCollectClicked(DynamicItemData item, int position) {
                ToastUtils.showLong("收藏");
                collectionMoment(item, position);
            }

            //评论列表item的点击
            @Override
            public void onCommentListItemClicked(DynamicItemData item, int positioin, DynamicCommentData commentItem, int commentPosition) {
                if (mLlInput.getVisibility() == View.VISIBLE) {
                    updateEditTextBodyVisible(View.GONE, null);
                    return;
                }

                int user_id = SPUtils.getInstance().getInt(Config.Constant.DUODUO_USER_ID);

                CommentConfig config = new WeakReference<>(new CommentConfig()).get();
                config.dynamicItemData = item;
                config.circlePosition = positioin;
                if (user_id != commentItem.getFrom_user_id()) {
                    config.commentPosition = commentPosition;
                    config.dynamicItemData = item;
                    config.commentType = CommentConfig.Type.REPLY;
                    config.replyComment = commentItem;
                } else {
                    config.commentType = CommentConfig.Type.PUBLIC;
                }

                if (mLlInput != null) {
                    commentConfig = config;
                }
                updateEditTextBodyVisible(View.VISIBLE, config);
            }

            //评论列表item的长点击
            @Override
            public void onCommentListItemLongClicked(DynamicCommentData commentItem, int commentPosition) {
//长按进行复制或者删除
//                    CommentItem commentItem = commentsDatas.get(commentPosition);
//                    CommentDialog dialog = new CommentDialog(context, presenter, commentItem, circlePosition);
//                    dialog.show();
            }

            //动态图片的点击
            @Override
            public void onImageItemClicked(List<String> photos, int position) {
                mBigImageSubscribe = Observable.just(photos)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap((Function<List<String>, ObservableSource<List<LocalMedia>>>) strings -> {
                            if (!mBigImageList.isEmpty()) {
                                mBigImageList.clear();
                            }

                            for (String p :
                                    photos) {
                                LocalMedia media = new LocalMedia();
                                if (p.startsWith("/")) {
                                    p = Config.API.image_url + p.replaceFirst("/", "");
                                }
                                Log.e(TAG, "onImageItemClicked: path=" + p);
                                media.setPath(p);
                                mBigImageList.add(media);
                            }
                            return Observable.just(mBigImageList);
                        })
                        .subscribe(localMedia -> PictureUtil.lookBigImages(getActivity(), position, mBigImageList));

            }

            //点赞的点击
            @Override
            public void onPraiseClicked(DynamicItemData item, int position) {
//                ToastUtils.showLong("点赞");
                praiseMoment(item, position);
            }

            //删除的点击
            @Override
            public void onDeleteClicked(DynamicItemData itemData, int position) {
                showDeleteDialog(itemData, position);
            }
        });


        mRefreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onHeaderPulling(RefreshHeader header, float percent, int offset, int headerHeight, int extendHeight) {
//                super.onHeaderPulling(header, percent, offset, headerHeight, extendHeight);
                mOffset = offset / 2;
                parallax.setTranslationY(offset);
                toolbar.setAlpha(0);
            }

            @Override
            public void onHeaderReleasing(RefreshHeader header, float percent, int offset, int footerHeight, int extendHeight) {
//                super.onHeaderReleasing(header, percent, offset, footerHeight, extendHeight);
                mOffset = offset / 2;
                parallax.setTranslationY(mOffset - mScrollY);
                parallax.setTranslationY(offset);
                toolbar.setAlpha(0);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                super.onRefresh(refreshlayout);
//                Log.i("FAN", "onRefresh: 下拉");
                page = 1;
                loadDynimacData(true);
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                super.onLoadmore(refreshlayout);
//                Log.i("FAN", "onLoadmore: 加载");
                page++;
                loadDynimacData(true);
            }
        });

        mRecyclerView.setOnTouchListener((v, event) -> {
            if (mLlInput.getVisibility() == View.VISIBLE) {
                updateEditTextBodyVisible(View.GONE, null);
                return true;
            }
            return false;
        });


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int max = DensityUtil.dp2px(150);

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Glide.with(getContext().getApplicationContext()).resumeRequests();
                } else {
                    Glide.with(getContext().getApplicationContext()).pauseRequests();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mLinearLayoutManager != null) {
                    int position = mLinearLayoutManager.findFirstVisibleItemPosition();
//                    Log.i("FAN", "onScrolled: position---> " + position);
                    View firstVisiableChildView = mLinearLayoutManager
                            .findViewByPosition(position);
                    //获取当前显示条目的高度
                    int itemHeight = firstVisiableChildView.getHeight();
                    //获取当前Recyclerview 偏移量
                    int flag = (position) * itemHeight - firstVisiableChildView.getTop();
                    if (flag > max || position > 0) {
                        toolbar.setAlpha(1);
                    } else {
                        toolbar.setAlpha(0);
                    }
                    parallax.setTranslationY(-flag);
                }
            }
        });

    }

    /**
     * 显示删除弹框
     *
     * @param itemData
     * @param position
     */
    private void showDeleteDialog(DynamicItemData itemData, int position) {
        showDidalog("提示", "确认删除该动态吗?", () -> {
            showPrograssBar(getString(R.string.waiting));
            //删除动态网络请求
            mHttpUtil.setOnRequestCallBack((code, data) -> {
                dismissPrograssBar();
                if (code == -1) {
                    ToastUtils.showLong(data);
                    return;
                }
                BaseReq mReq = GsonUtil.getGson().fromJson(data, BaseReq.class);
                ToastUtils.showLong(mReq.getMsg());
                if (TextUtils.equals(mReq.getStatus(), Config.Constant.SUCCESS)) {
                    mDynamicDatas.remove(position - 1);
                    mDynamicAdapter.notifyItemRemoved(position);
                }
            }).deleteParams(getHttpParams(), String.format(Config.API.moment_dtail, itemData.getMoment_id()));
        });
    }

    public void setBackgroundImage(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            String groupCover = Config.FolderName.moment_cover;
            ImageUtil.INSTANCE.loadFileImage(getContext(), new WeakReference<>(new File(filePath)).get(), parallax);

            mHttpUtil.setOnRequestCallBack((code, data) -> {
                if (code == -1) {
                    ToastUtils.showLong(data);
                    return;
                }
                FileLoadEntity mFileLoadEntity = GsonUtil.getGson().fromJson(data, FileLoadEntity.class);
                if (mFileLoadEntity.getUrl() != null && !mFileLoadEntity.getUrl().isEmpty()) {
                    editInfo("/" + groupCover);
                } else {
                    ToastUtils.showLong("设置失败");
                }
            }).postFile(Config.API.image_url, groupCover, filePath, new HttpParams());
        }
    }

    /**
     * 更新
     *
     * @param s
     */
    private void editInfo(String s) {
        HttpParams httpParams = new HttpParams();
        httpParams.put("moment_cover", s);
        ((BaseActivity) getActivity()).showProgressDialog(getString(R.string.waiting));
        mHttpUtil.setOnRequestCallBack((code, data) -> {
            ((BaseActivity) getActivity()).dismissProgressDialog();
            if (code == -1) {
                ToastUtils.showLong(data);
                return;
            }
            BaseReq baseReq = GsonUtil.getGson().fromJson(data, BaseReq.class);
            ToastUtils.showLong(baseReq.getMsg());
            if (TextUtils.equals(baseReq.getStatus(), Config.Constant.SUCCESS)) {
                ToastUtils.showLong("设置成功");
//                UserInfoEntity mUser = UserInfoManage.Companion.getGetInstance().getMUserInfoEntity();
//                if (mUser != null)
            } else {
                ToastUtils.showLong("设置失败");
            }
        }).postParms(httpParams, Config.API.mine_information);
    }


    private void setViewTreeObserver() {
        mRlBodyLayout = inflate.findViewById(R.id.rl_body_layout);
        final ViewTreeObserver swipeRefreshLayoutVTO = mRlBodyLayout.getViewTreeObserver();
        swipeRefreshLayoutVTO.addOnGlobalLayoutListener(() -> {

            Rect r = new WeakReference<>(new Rect()).get();
            mRlBodyLayout.getWindowVisibleDisplayFrame(r);
            int statusBarH = getStatusBarHeight(mContext);//状态栏高度
            int screenH = mRlBodyLayout.getRootView().getHeight();
            if (r.top != statusBarH) {
                //在这个demo中r.top代表的是状态栏高度，在沉浸式状态栏时r.top＝0，通过getStatusBarHeight获取状态栏高度
                r.top = statusBarH;
            }
            int keyboardH = screenH - (r.bottom - r.top);

            if (keyboardH == currentKeyboardH) {//有变化时才处理，否则会陷入死循环
                return;
            }

            currentKeyboardH = keyboardH;
            screenHeight = screenH;//应用屏幕的高度
            editTextBodyHeight = mLlInput.getHeight();

            if (keyboardH < 150) {//说明是隐藏键盘的情况
                commentConfig = null;
                if (mNavigationBar != null) {
                    mNavigationBar.setVisibility(View.VISIBLE);
                }
                return;
            } else {
                if (mNavigationBar != null) {
                    mNavigationBar.setVisibility(View.GONE);
                }
            }
            //偏移listview
            if (mLinearLayoutManager != null && commentConfig != null) {
                mLinearLayoutManager.scrollToPositionWithOffset(commentConfig.circlePosition, getListviewOffset(commentConfig));
            }
        });
    }

    public void updateEditTextBodyVisible(int visibility, CommentConfig config) {
        this.commentConfig = config;
        mLlInput.setVisibility(visibility);

        if (commentConfig != null) {
            measureCircleItemHighAndCommentItemOffset(commentConfig);
        }

        if (View.VISIBLE == visibility) {
            mEtInput.requestFocus();
            if (config.commentType == CommentConfig.Type.PUBLIC) {
                mEtInput.setHint("评论");
            } else {
                mEtInput.setHint("回复" + config.replyComment.getFrom_friend_remark());
            }

            KeyboardUtils.showSoftInput(mEtInput);
        } else if (View.GONE == visibility) {
            mEtInput.setText("");
            KeyboardUtils.hideSoftInput(mEtInput);
        }
    }

    private void measureCircleItemHighAndCommentItemOffset(CommentConfig config) {
        if (config == null)
            return;

        int firstPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
        //只能返回当前可见区域（列表可滚动）的子项
        View selectCircleItem = mLinearLayoutManager.getChildAt(config.circlePosition - firstPosition);

        if (selectCircleItem != null) {
            selectCircleItemH = selectCircleItem.getHeight();
        }

        if (config.commentType == CommentConfig.Type.REPLY) {
            //回复评论的情况
            CommentListView commentLv = (CommentListView) selectCircleItem.findViewById(R.id.commentList);
            if (commentLv != null) {
                //找到要回复的评论view,计算出该view距离所属动态底部的距离
                View selectCommentItem = commentLv.getChildAt(config.commentPosition);
                if (selectCommentItem != null) {
                    //选择的commentItem距选择的CircleItem底部的距离
                    selectCommentItemOffset = 0;
                    View parentView = selectCommentItem;
                    do {
                        int subItemBottom = parentView.getBottom();
                        parentView = (View) parentView.getParent();
                        if (parentView != null) {
                            selectCommentItemOffset += (parentView.getHeight() - subItemBottom);
                        }
                    } while (parentView != null && parentView != selectCircleItem);
                }
            }
        }
    }

    private void loadDynimacData(final boolean isRefresh) {
        HttpParams params = getHttpParams();
        params.put("page", String.valueOf(page));
        if (look_type == LOOK_OTHER_MOMENT) {
            params.put("user_id", String.valueOf(user_id));
        }

        if (!isRefresh) {
            ((BaseActivity) getActivity()).showProgressDialog(getString(R.string.waiting));
        }

        mHttpUtil.setOnRequestCallBack((code, data) -> {
            if (!isRefresh) {
                ((BaseActivity) getActivity()).dismissProgressDialog();
            } else {
                if (page == 1) {
                    mRefreshLayout.finishRefresh();
                } else {
                    mRefreshLayout.finishLoadmore();
                }
            }

            if (code == -1) {
                ToastUtils.showLong(data);
                return;
            }

            Type type = new TypeToken<BaseReq<DynamicListEntity>>() {
            }.getType();

            BaseReq<DynamicListEntity> mReq = GsonUtil.getGson().fromJson(data, type);
            Log.i("FAN", "loadDynimacData: 动态数据----> " + mReq);
            if (TextUtils.equals(mReq.getStatus(), Config.Constant.SUCCESS)) {

                if (TextUtils.isEmpty(mReq.getResult().getNext_page_url())) {
                    mRefreshLayout.setEnableLoadmore(false);
                } else {
                    mRefreshLayout.setEnableLoadmore(true);
                }


                if (page == 1) {
                    mDynamicDatas.clear();
                }
                mDynamicDatas.addAll(mReq.getResult().component2());
                mDynamicAdapter.notifyDataSetChanged();


            } else {
                if (page > 1) {
                    page--;
                }
                ToastUtils.showLong(mReq.getMsg());
            }
        }).getParms(params, request_url);
    }


    /**
     * 测量偏移量
     *
     * @param commentConfig
     * @return
     */
    private int getListviewOffset(CommentConfig commentConfig) {
        if (commentConfig == null)
            return 0;
        //这里如果你的listview上面还有其它占高度的控件，则需要减去该控件高度，listview的headview除外。
        //int listviewOffset = mScreenHeight - mSelectCircleItemH - mCurrentKeyboardH - mEditTextBodyHeight;
        int listviewOffset = screenHeight - selectCircleItemH - currentKeyboardH - editTextBodyHeight + getStatusBarHeight(getContext());
        if (commentConfig.commentType == CommentConfig.Type.REPLY) {
            //回复评论的情况
            listviewOffset = listviewOffset + selectCommentItemOffset;
        }
        return listviewOffset;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_release) {//发布
            selectImage(RELEASE_DYNAMIC);
        } else if (v.getId() == R.id.bg_view_stub) {
            selectImage(SELECT_BACK);
        } else if (v.getId() == R.id.iv_back) {
            AppManager.get().finishActivity(getActivity());
        } else if (v.getId() == R.id.tv_send) {
            momentComment();
        }
    }

    //选择图片
    private void selectImage(final int flag) {
        PermissionUtils.permission(PermissionConstants.STORAGE)
                .rationale(shouldRequest -> shouldRequest.again(true))
                .callback(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(List<String> permissionsGranted) {
                        if (flag == RELEASE_DYNAMIC) {
                            Intent intent = new WeakReference<>(new Intent(mContext, PublishDynamicActivity.class)).get();
                            startActivity(intent);
                        }/* else {
                            if (mPictureUtil == null) {
                                mPictureUtil = new PictureUtil();
                                mPictureUtil.setCircleDimmedLayer(false);
                                mPictureUtil.setAspectRatio(PictureUtil.CROP_9_16);
                            }
                            mPictureUtil.setRequestCode(flag);
                            mPictureUtil.selectPicture(getActivity());
                        }*/
                    }

                    @Override
                    public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {
                        ToastUtils.showShort("权限已拒绝");
                    }
                })
                .request();
    }

    /**
     * 点赞动态
     *
     * @param itemData 动态
     */
    private void praiseMoment(DynamicItemData itemData, int position) {
        HttpParams mParams = getHttpParams();
        mParams.put("moment_id", String.valueOf(itemData.getMoment_id()));
        showPrograssBar(getString(R.string.waiting));
        mHttpUtil.setOnRequestCallBack((code, data) -> {
            dismissPrograssBar();
            if (code == -1) {
                ToastUtils.showLong(data);
                return;
            }
            Type type = new TypeToken<BaseReq<DynamicLikeData>>() {
            }.getType();

            BaseReq<DynamicLikeData> mReq = GsonUtil.getGson().fromJson(data, type);
            Log.e(TAG, "praiseMoment:DynamicLikeData mReq=" + mReq.toString());
            if (TextUtils.equals(mReq.getStatus(), SUCCESS)) {
                if (mReq.getResult() != null) {
                    ToastUtils.showLong("点赞成功");
                    itemData.setHasLike(true);
                    // UserInfoManage infoManage=UserInfoManage.
                    mReq.getResult().setFrom_friend_remark(UserInfoManageForJava.newInstance().getUserInfoEntity().getNickname());
                    //Log.e(TAG, "praiseMoment: name=" + UserInfoManageForJava.newInstance().getUserInfoEntity().getNickname());
                    itemData.getLikes().add(mReq.getResult());
                } else {
                    ToastUtils.showLong("已取消点赞");
                    itemData.setHasLike(false);
                    if (itemData.getMinePraisePosition() != -1) {
                        itemData.getLikes().remove(itemData.getMinePraisePosition());
                    }
                }
                mDynamicAdapter.notifyItemChanged(position);
            } else {
                ToastUtils.showLong(mReq.getMsg());
            }
        }).postParms(mParams, Config.API.moment_like);
    }

    /**
     * 评论
     */
    private void momentComment() {
        if (commentConfig == null) {
            return;
        }

        String content = mEtInput.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            ToastUtils.showLong("评论内容不能为空");
            return;
        }

        HttpParams mParams = getHttpParams();
        mParams.put("moment_id", String.valueOf(commentConfig.dynamicItemData.getMoment_id()));
        mParams.put("content", content);

        if (commentConfig.commentType == CommentConfig.Type.REPLY) {
            mParams.put("comment_pid", String.valueOf(commentConfig.replyComment.getComment_id()));
            mParams.put("to_user_id", String.valueOf(commentConfig.replyComment.getFrom_user_id()));
        }

        showPrograssBar(getString(R.string.waiting));
        mHttpUtil.setOnRequestCallBack((code, data) -> {
            dismissPrograssBar();
//                Log.i("FAN", "onCall: 评论返回---> " + data);
            if (code == -1) {
                ToastUtils.showLong(data);
                return;
            }

            Type type = new TypeToken<BaseReq<DynamicCommentData>>() {
            }.getType();

            BaseReq<DynamicCommentData> mReq = GsonUtil.getGson().fromJson(data, type);
            if (TextUtils.equals(mReq.getStatus(), Config.Constant.SUCCESS)) {
                if (mReq.getResult() != null) {
                    mReq.getResult().setFrom_friend_remark(commentConfig.dynamicItemData.getFriend_remark());
                    if (commentConfig.commentType == CommentConfig.Type.REPLY) {
                        mReq.getResult().setTo_friend_remark(commentConfig.replyComment.getTo_friend_remark());
                    }
                    mDynamicDatas.get(commentConfig.circlePosition - 1).getComments().add(mReq.getResult());

                    mDynamicAdapter.notifyItemChanged(commentConfig.circlePosition);
                }
                updateEditTextBodyVisible(View.GONE, null);
            } else {
                ToastUtils.showLong(mReq.getMsg());
            }
        }).postParms(mParams, Config.API.moment_comment);

    }

    /**
     * 收藏
     */
    private void collectionMoment(DynamicItemData itemData, int position) {//目标ID (消息则是消息ID，动态则是动态ID)
        HttpParams mParams = getHttpParams();
        mParams.put("type", Config.Favorite.MONENT);
        mParams.put("target_id", String.valueOf(itemData.getMoment_id()));

        showPrograssBar(getString(R.string.waiting));
        mHttpUtil.setOnRequestCallBack((code, data) -> {
            dismissPrograssBar();
            if (code == -1) {
                ToastUtils.showLong(data);
                return;
            }
            BaseReq mReq = GsonUtil.getGson().fromJson(data, BaseReq.class);
            itemData.setHasFavorite(true);
            mDynamicAdapter.getData().set(position, itemData);
            ToastUtils.showLong(mReq.getMsg());
        }).postParms(mParams, Config.API.favorite);
    }

    @SuppressLint("CheckResult")
    private void refreshData(DynamicItemData data) {
        mRefreshDispose = Observable.just(data)
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .flatMap((Function<DynamicItemData, ObservableSource<Integer>>) itemData -> {
//                        Log.i("FAN", "apply: flatMap---> " + Thread.currentThread().getName());
                    for (int i = 0; i < mDynamicDatas.size(); i++) {
                        DynamicItemData di = mDynamicDatas.get(i);
                        if (di.getMoment_id() == itemData.getMoment_id()) {
                            mDynamicDatas.set(i, data);
                            return Observable.just(i);
                        }
                    }

                    return Observable.just(-1);
                })
                .subscribe(position -> {
//                        Log.i("FAN", "accetp: 刷新数据---> " + Thread.currentThread().getName());
                    getActivity().runOnUiThread(() -> mDynamicAdapter.notifyItemChanged(position + 1));
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBigImageSubscribe != null && mBigImageSubscribe.isDisposed()) {
            mBigImageSubscribe.dispose();
        }

        if (mDynamicDispose != null && mDynamicDispose.isDisposed()) {
            mDynamicDispose.dispose();
        }


        if (mRefreshDispose != null && mRefreshDispose.isDisposed()) {
            mRefreshDispose.dispose();
        }

        mRefreshDispose = null;
        mDynamicDispose = null;

        mBigImageSubscribe = null;

        mBigImageList = null;
    }
}
