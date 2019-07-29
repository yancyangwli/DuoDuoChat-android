package com.woniu.core.utils;

import android.app.Activity;
import android.content.Context;

import android.text.TextUtils;
import com.luck.picture.lib.PictureSelectionModel;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.woniu.core.R;

import java.util.List;

/**
 * Created by Administrator on 2017/8/15.
 */

public class PictureUtil {
    public static final String CROP_16_9 = "Crop_16_9";//裁剪比例 16：9
    public static final String CROP_9_16 = "Crop_9_16";//裁剪比例 16：9
    public static final String CROP_1_1 = "Crop_1_1";//裁剪比例 1：1


    private int selectPicMaxNumber = 1;//选择图片最大数量；默认数量为1
    private int selectPicMinNumber = 1;//选择图片最小数量；默认数量为1
    private int showLinePicNumber = 4;//每行显示图片的数量；默认数量为4
    private boolean enableCrop = true;//是否裁剪 默认true
    private boolean compress = true;//是否压缩 默认true

    private boolean isCropFrame = false;//是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
    private boolean circleDimmedLayer = true;//是否圆形裁剪 true or false

    private int selctionMode = PictureConfig.SINGLE;//选择图片的模式；默认单选
    public static final int SINGLE = PictureConfig.SINGLE;//单选
    public static final int MULTIPLE = PictureConfig.MULTIPLE;//多选

    private int compressMaxKB = 2048;//图片压缩质量 kb
    private int requestCode = -1;//设置请求码
    private PictureSelector pictureSelector;

    private int crop_width = 1;
    private int crop_height = 1;

    public void setAspectRatio(String flag) {
        if (TextUtils.equals(CROP_16_9, flag)) {
            crop_width = 16;
            crop_height = 9;
        } else if (TextUtils.equals(CROP_9_16, flag)) {
            crop_width = 9;
            crop_height = 16;
        }
    }

    public void setCircleDimmedLayer(boolean circleDimmedLayer) {
        this.circleDimmedLayer = circleDimmedLayer;
        this.isCropFrame = !circleDimmedLayer;
    }


    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public void setSelctionMode(int selctionMode) {
        this.selctionMode = selctionMode;
    }

    public void setSelectPicMaxNumber(int selectPicMaxNumber) {
        this.selectPicMaxNumber = selectPicMaxNumber;
    }

    public void setEnableCrop(boolean enableCrop) {
        this.enableCrop = enableCrop;
    }

    public void setCompress(boolean compress) {
        this.compress = compress;
    }

    public void setCompressMaxKB(int compressMaxKB) {
        this.compressMaxKB = compressMaxKB;
    }

    public void selectPicture(Activity activity) {
        if (pictureSelector == null){
            pictureSelector = PictureSelector.create(activity);
        }
        setModel(pictureSelector.openGallery(PictureMimeType.ofImage()));//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo());
    }

    public void selectPictureFromCamera(Activity activity) {
        if (pictureSelector == null){
            pictureSelector = PictureSelector.create(activity);
        }
        setModel(pictureSelector.openCamera(PictureMimeType.ofImage()));
    }

    private void setModel(PictureSelectionModel model) {
        model.maxSelectNum(selectPicMaxNumber)// 最大图片选择数量 int
                .minSelectNum(selectPicMinNumber)// 最小选择数量 int
                .imageSpanCount(showLinePicNumber)// 每行显示个数 int
                .selectionMode(selctionMode)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(true)// 是否可预览视频 true or false
                .enablePreviewAudio(true) // 是否可播放音频 true or false
//                .compressGrade(Luban.CUSTOM_GEAR)// luban压缩档次，默认3档 Luban.THIRD_GEAR、Luban.FIRST_GEAR、Luban.CUSTOM_GEAR
                .isCamera(true)// 是否显示拍照按钮 true or false
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                .enableCrop(enableCrop)// 是否裁剪 true or false
                .compress(compress)// 是否压缩 true or false
//                .compressMode(PictureConfig.SYSTEM_COMPRESS_MODE)//系统自带 or 鲁班压缩 PictureConfig.SYSTEM_COMPRESS_MODE or LUBAN_COMPRESS_MODE
                .glideOverride(120, 120)// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .withAspectRatio(crop_width, crop_height)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示 true or false
                .isGif(true)// 是否显示gif图片 true or false
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                .circleDimmedLayer(circleDimmedLayer)// 是否圆形裁剪 true or false
                .showCropFrame(isCropFrame)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .openClickSound(false)// 是否开启点击声音 true or false
                //    .selectionMedia(false)// 是否传入已选图片 List<LocalMedia> list
                .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                .minimumCompressSize(100)
                //.cropCompressQuality()// 裁剪压缩质量 默认90 int
//                .compressMaxKB(compressMaxKB)//压缩最大值kb compressGrade()为Luban.CUSTOM_GEAR有效 int
                // .compressWH() // 压缩宽高比 compressGrade()为Luban.CUSTOM_GEAR有效  int
                //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
                //.rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                //.videoQuality()// 视频录制质量 0 or 1 int
                //.videoSecond()// 显示多少秒以内的视频or音频也可适用 int
                //.recordVideoSecond()//视频秒数录制 默认60s int
                .forResult(requestCode != -1 ? requestCode : PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code

    }


    public static void lookBigImages(Activity activity, int index, List<LocalMedia> images) {
        PictureSelector
                .create(activity)
                .themeStyle(R.style.picture_style)
                .openExternalPreview(index, images);
    }
}
