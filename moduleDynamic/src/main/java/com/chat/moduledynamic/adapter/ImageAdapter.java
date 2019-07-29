package com.chat.moduledynamic.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chat.moduledynamic.R;
import com.luck.picture.lib.entity.LocalMedia;
import com.orhanobut.logger.Logger;
import com.woniu.core.utils.ImageUtil;

import java.io.File;
import java.lang.ref.WeakReference;

public class ImageAdapter extends BaseQuickAdapter<LocalMedia, BaseViewHolder> {

    public ImageAdapter() {
        super(R.layout.item_image);
    }

    @Override
    public int getItemCount() {
        if (getData().size() > 9){
            return 9;
        }
        return getData().size();
    }

    @Override
    protected void convert(BaseViewHolder helper, LocalMedia item) {
        ImageView mIvImage = helper.<ImageView>getView(R.id.mIvImage);
        String compressPath = item.getCompressPath();

        Logger.i(TAG, "convert: ddddd");
        if (TextUtils.isEmpty(compressPath)){
            helper.<ImageView>getView(R.id.mIvImage).setImageResource(R.mipmap.ic_add_gray_);
            helper.<ImageView>getView(R.id.mIvDelete).setVisibility(View.GONE);

        }else {
            helper.<ImageView>getView(R.id.mIvDelete).setVisibility(View.VISIBLE);
            File file = new WeakReference<>(new File(compressPath)).get();
            ImageUtil.INSTANCE.loadFileImage(mContext,file,mIvImage);
        }

        helper.addOnClickListener(R.id.mIvDelete);
        helper.addOnClickListener(R.id.mCVImage);
    }
}
