package com.woniu.core.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ScrollView

import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.woniu.core.R
import com.woniu.core.api.Config

import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.ref.WeakReference


import com.bumptech.glide.request.RequestOptions.bitmapTransform

/**
 * Created by Administrator on 2017/12/5.
 */

object ImageUtil {

    /**
     * 加载普通
     *
     * @param context  上下文
     * @param imageUrl 图片
     * @param intoView 图片显示控件
     */
    fun loadOriginalImage(
        context: Context?,
        imageUrl: Any?,
        intoView: ImageView?,
        defaultRes: Int = R.color.color_f9f9f9
    ) {
        context?.let {
            var imageUrl = imageUrl
            //此判断为了兼容本项目特殊需求   imageUrl格式为"/xxx/xxx.jpg"
            if (imageUrl is String && imageUrl != null && !imageUrl.startsWith("http") && imageUrl.startsWith("/")) {
                imageUrl = Config.API.image_url + imageUrl.replaceFirst("/".toRegex(), "")
            }
//            else if (imageUrl == null || (imageUrl is String && TextUtils.isEmpty(imageUrl))) {
//                return
//            }
            val options = RequestOptions()
            options.placeholder(defaultRes)
            options.error(defaultRes)
            //        options.dontAnimate();
            //        options.skipMemoryCache(false);
            options.diskCacheStrategy(DiskCacheStrategy.ALL)
            Glide.with(it)
                .load(imageUrl)
                .apply(options)
                .into(intoView!!)
        }
    }

    fun loadOriginalNoBgImage(context: Context, imageUrl: String, intoView: ImageView) {
        val options = RequestOptions()
        options.dontAnimate()
        options.skipMemoryCache(false)
        Glide.with(context.applicationContext)
            .load(imageUrl)
            .apply(options)
            .into(intoView)
    }

    fun loadOriginalImageWithCallBack(context: Context, imageUrl: String, callback: SimpleTarget<Bitmap>) {
        val options = RequestOptions()
        options.skipMemoryCache(false)
        options.placeholder(R.drawable.bg_gray_fill)
        options.dontAnimate()

        Glide.with(context.applicationContext)
            .asBitmap()
            .load(imageUrl)
            .apply(options)
            .into(callback)
    }


    //    public static void loadCircleImage(Context context, String imageUrl, ImageView intoView) {
    //        RequestOptions options = new WeakReference<>(new RequestOptions()).get();
    //        options.skipMemoryCache(false);
    ////        options.placeholder(R.mipmap-xxhdpi.ic_default_head);
    ////        options.placeholder(R.mipmap-xxhdpi.ic_default_head);
    //
    //        Glide.with(context.getApplicationContext())
    //
    //                .load(imageUrl)
    //                .apply(options)
    //                .apply(bitmapTransform(new CropCircleTransformation()))
    //                .into(intoView);
    //    }

    //    public static void loadLocalCircleImage(Context context, int res, ImageView intoView) {
    //        RequestOptions options = new WeakReference<>(new RequestOptions()).get();
    //
    //        Glide.with(context.getApplicationContext())
    //                .load(res)
    //                .apply(options)
    //                .apply(bitmapTransform(new CropCircleTransformation()))
    //                .into(intoView);
    //
    //    }

    fun loadFileImage(context: Context?, file: File?, intoView: ImageView) {
        Glide.with(context!!).load(file).into(intoView)
    }


    fun loadCornerImage(context: Context, imageUrl: String, corners: Int, intoView: ImageView) {
        val options = WeakReference(RequestOptions()).get()
        //        options.transform(new WeakReference<>(new CenterCrop()).get(),new WeakReference<>(new RoundedCorners(corners)).get());
        options?.skipMemoryCache(false)
        val centerCrop = WeakReference(CenterCrop()).get()
        val roundedCorners = WeakReference(RoundedCorners(corners)).get()
        options?.transforms(centerCrop, roundedCorners)

        Glide.with(context)
            //                .asBitmap()
            .load(imageUrl)
            .apply(options!!)
            //                .apply(bitmapTransform(new RoundedCornersTransformation(RxImageTool.dip2px(10),0)))
            //                .transform()
            .into(intoView)
    }

    fun loadLocalCornerImage(context: Context, res: Int, corners: Int, intoView: ImageView) {
        val options = WeakReference(RequestOptions()).get()
        options?.skipMemoryCache(false)
        val centerCrop = WeakReference(CenterCrop()).get()
        val roundedCorners = WeakReference(RoundedCorners(corners)).get()
        options?.transforms(centerCrop, roundedCorners)

        Glide.with(context)
            //                .asBitmap()
            .load(res)
            .apply(options!!)
            //                .apply(bitmapTransform(new RoundedCornersTransformation(RxImageTool.dip2px(10),0)))
            //                .transform()
            .into(intoView)
    }


    fun getScrollViewBitmap(scrollView: ScrollView): Bitmap {
        var h = 0
        val bitmap: Bitmap
        for (i in 0 until scrollView.childCount) {
            h += scrollView.getChildAt(i).height
        }
        //创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(scrollView.width, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        scrollView.draw(canvas)
        return bitmap
    }

    /* 截取指定View为图片
     *
     * @param view
     * @return
     * @throws Throwable
     */
    fun captureView(view: View): Bitmap {
        val bm = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        view.draw(Canvas(bm))
        return bm
    }


    /**
     * 需要在子线程执行
     *
     * @param context
     * @param url
     * @return
     */
    fun load(context: Context, url: String): Bitmap? {
        val options = WeakReference(RequestOptions()).get()
        options?.diskCacheStrategy(DiskCacheStrategy.ALL)

        try {
            return Glide.with(context.applicationContext)
                .asBitmap()
                .load(url)
                .apply(options!!)
                .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .get()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }


}
