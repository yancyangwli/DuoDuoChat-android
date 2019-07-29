package com.chat.moduledynamic.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity;
import cn.bingoogolapple.photopicker.widget.BGASortableNinePhotoLayout;
import com.chat.moduledynamic.R;
import com.chat.moduledynamic.bean.test.Moment;
import com.woniu.core.activities.BaseActivity;
import com.woniu.core.api.Config;
import com.woniu.core.utils.HttpUtil;
import com.zhouyou.http.model.HttpParams;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.zhouyou.http.EasyHttp.getContext;

public class PublishDynamicActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks, BGASortableNinePhotoLayout.Delegate {
    private static final int PRC_PHOTO_PICKER = 1;

    private static final int RC_CHOOSE_PHOTO = 1;
    private static final int RC_PHOTO_PREVIEW = 2;

    private static final String EXTRA_MOMENT = "EXTRA_MOMENT";

    /**
     * 拖拽排序九宫格控件
     */
    private BGASortableNinePhotoLayout mPhotosSnpl;

    private EditText mContentEt;

    private TextView mToolbarTitle, mToolbarRight;

    private LinearLayout mToolbarBack;

    private HttpUtil mHttpUtil = new HttpUtil(getContext());

    @Override
    protected int setContentViewId() {
        return R.layout.activity_release_dynamic;
    }

    @SuppressLint("WrongViewCast")
    @Override
    public void initView() {

        mToolbarRight = findViewById(R.id.mToolbarRight);
        mToolbarTitle = findViewById(R.id.mToolbarTitle);
        mToolbarBack = findViewById(R.id.mToolbarBack);

        mContentEt = findViewById(R.id.mEtContent);
        mPhotosSnpl = findViewById(R.id.snpl_moment_add_photos);

        mToolbarRight.setVisibility(View.VISIBLE);
        mToolbarTitle.setText(getString(R.string.release_dynamic));
        mToolbarRight.setText(getString(R.string.release));
        mToolbarRight.setBackgroundResource(R.drawable.bg_theme_corner);

        mToolbarBack.setOnClickListener(this);
        mToolbarRight.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mPhotosSnpl.setMaxItemCount(9);
        mPhotosSnpl.setEditable(true);
        mPhotosSnpl.setPlusEnable(true);
        mPhotosSnpl.setSortable(true);
        // 设置拖拽排序控件的代理
        mPhotosSnpl.setDelegate(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int i = v.getId();
        if (i == R.id.mToolbarBack) {
            finishs();
        } else if (i == R.id.mToolbarRight) {
            releaseDynamic();
        }
    }

    private static final String TAG = "yancy";

    private void releaseDynamic() {
        String content = mContentEt.getText().toString().trim();
        if (content.length() == 0) {
            Toast.makeText(this, "请输入动态内容！", Toast.LENGTH_SHORT).show();
            return;
        }

        //上传服务器
        HttpParams params = new HttpParams();
        params.put("content", content);
        StringBuffer imagesSB = new StringBuffer();
        if (!mPhotosSnpl.getData().isEmpty()) {
            for (String datum : mPhotosSnpl.getData()) {
                imagesSB.append(datum).append(";");
            }
        }
        Log.e(TAG, "releaseDynamic: imagesSB=" + imagesSB.toString());
        params.put("images", imagesSB.toString());

        mHttpUtil.setOnRequestCallBack(new HttpUtil.OnRequestCallBack() {
            @Override
            public void onCall(int code, String data) {
                Log.e(TAG, "onCall: code=" + code);
                Log.e(TAG, "onCall: data=" + data);
            }
        }).postParms(params, Config.API.moment);
        Intent intent = new Intent();
        intent.putExtra(EXTRA_MOMENT, new Moment(content, mPhotosSnpl.getData()));
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClickAddNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, ArrayList<String> models) {
        choicePhotoWrapper();
    }

    @Override
    public void onClickDeleteNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
        mPhotosSnpl.removeItem(position);
    }

    @Override
    public void onClickNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
        Intent photoPickerPreviewIntent = new BGAPhotoPickerPreviewActivity.IntentBuilder(this)
                .previewPhotos(models) // 当前预览的图片路径集合
                .selectedPhotos(models) // 当前已选中的图片路径集合
                .maxChooseCount(mPhotosSnpl.getMaxItemCount()) // 图片选择张数的最大值
                .currentPosition(position) // 当前预览图片的索引
                .isFromTakePhoto(false) // 是否是拍完照后跳转过来
                .build();
        startActivityForResult(photoPickerPreviewIntent, RC_PHOTO_PREVIEW);
    }

    @Override
    public void onNinePhotoItemExchanged(BGASortableNinePhotoLayout sortableNinePhotoLayout, int fromPosition, int toPosition, ArrayList<String> models) {
        Toast.makeText(this, "排序发生变化", Toast.LENGTH_SHORT).show();
    }

    @AfterPermissionGranted(PRC_PHOTO_PICKER)
    private void choicePhotoWrapper() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
            File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "BGAPhotoPickerTakePhoto");

            Intent photoPickerIntent = new BGAPhotoPickerActivity.IntentBuilder(this)
                    .cameraFileDir(takePhotoDir) // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话则不开启图库里的拍照功能
                    .maxChooseCount(mPhotosSnpl.getMaxItemCount() - mPhotosSnpl.getItemCount()) // 图片选择张数的最大值
                    .selectedPhotos(null) // 当前已选中的图片路径集合
                    .pauseOnScroll(false) // 滚动列表时是否暂停加载图片
                    .build();
            startActivityForResult(photoPickerIntent, RC_CHOOSE_PHOTO);
        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问设备上的照片\n\n2.拍照", PRC_PHOTO_PICKER, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == PRC_PHOTO_PICKER) {
            Toast.makeText(this, "您拒绝了「图片选择」所需要的相关权限!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RC_CHOOSE_PHOTO) {
            mPhotosSnpl.addMoreData(BGAPhotoPickerActivity.getSelectedPhotos(data));
        } else if (requestCode == RC_PHOTO_PREVIEW) {
            mPhotosSnpl.setData(BGAPhotoPickerPreviewActivity.getSelectedPhotos(data));
        }
    }
}
