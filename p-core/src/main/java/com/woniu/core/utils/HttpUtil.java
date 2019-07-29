package com.woniu.core.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.blankj.utilcode.util.SPUtils;
import com.google.gson.Gson;
import com.woniu.core.api.Config;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.body.ProgressResponseCallBack;
import com.zhouyou.http.callback.DownloadProgressCallBack;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;
import com.zhouyou.http.model.HttpHeaders;
import com.zhouyou.http.model.HttpParams;
import com.zhouyou.http.request.DeleteRequest;
import com.zhouyou.http.request.GetRequest;
import com.zhouyou.http.request.PostRequest;
import com.zhouyou.http.request.PutRequest;

import java.io.File;
import java.lang.ref.WeakReference;

import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/11/16.
 */

public class HttpUtil {
    private OnRequestCallBack onRequestCallBack;
    private Context context;

    public HttpUtil(Context context) {
        this.context = context;
    }

    public HttpUtil setOnRequestCallBack(OnRequestCallBack onRequestCallBack) {
        this.onRequestCallBack = onRequestCallBack;
        return this;
    }


    /**
     * post参数上传
     *
     * @param params 上传参数
     * @param url    上传接口
     */
    public Disposable postParms(HttpParams params, final String url) {
        PostRequest post = EasyHttp.post(url);
//        GetRequest post = EasyHttp.get(url);
        HttpHeaders header = getHeader();
        if (header != null) {
            post.headers(header);
        }
        if (params != null) {
            post.params(params);
        }
        Disposable disposable = post
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
//                        if (reLogin(e.getMessage())) {
//                            return;
//                        }
                        if (onRequestCallBack != null) {
                            onRequestCallBack.onCall(-1, e.getMessage());
                        }
                    }

                    @Override
                    public void onSuccess(String s) {
                        if (onRequestCallBack != null) {
                            onRequestCallBack.onCall(1, s);
                        }
//                        Log.i("HttpUtil", "onSuccess: 请求成功----> " + s);
                    }
                });
        return disposable;
    }


    public Disposable deleteParams(HttpParams params, final String url) {
        DeleteRequest delete = EasyHttp.delete(url);
//        GetRequest post = EasyHttp.get(url);
        HttpHeaders header = getHeader();
        if (header != null) {
            delete.headers(header);
        }
        if (params != null) {
            delete.params(params);
        }
        Disposable disposable = delete
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
//                        if (reLogin(e.getMessage())) {
//                            return;
//                        }
                        if (onRequestCallBack != null) {
                            onRequestCallBack.onCall(-1, e.getMessage());
                        }
                    }

                    @Override
                    public void onSuccess(String s) {
                        if (onRequestCallBack != null) {
                            onRequestCallBack.onCall(1, s);
                        }
//                        Log.i("HttpUtil", "onSuccess: 请求成功----> " + s);
                    }
                });
        return disposable;
    }


    /**
     * 下载文件
     *
     * @param fileUrl  下载文件地址
     * @param savePath 文件保存位置
     * @param saveName 文件名
     * @return
     */
    public Disposable downloadFile(String fileUrl, String savePath, String saveName) {
        return EasyHttp.downLoad(fileUrl)
                .savePath(savePath)
                .saveName(saveName)
                .execute(new DownloadProgressCallBack<String>() {
                    @Override
                    public void update(long bytesRead, long contentLength, boolean done) {
//                        int progress = (int) (bytesRead * 100 / contentLength);
//                        HttpLog.e(progress + "% ");
                    }

                    @Override
                    public void onStart() {
                        //开始下载
                    }

                    @Override
                    public void onComplete(String path) {
                        //下载完成，path：下载文件保存的完整路径
                        if (onRequestCallBack != null) {
                            onRequestCallBack.onCall(1, path);
                        }
                    }

                    @Override
                    public void onError(ApiException e) {
                        //下载失败
                        if (onRequestCallBack != null) {
                            onRequestCallBack.onCall(-1, e.getMessage());
                        }
                    }
                });
    }


    /**
     * put参数上传
     *
     * @param params 上传参数
     * @param url    上传接口
     */
    public Disposable putParms(HttpParams params, final String url) {
        PutRequest put = EasyHttp.put(url);
        HttpHeaders header = getHeader();
        if (header != null) {
            put.headers(header);
        }
        if (params != null) {
            put.params(params);
        }
        Disposable disposable = put
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
//                        if (reLogin(e.getMessage())) {
//                            return;
//                        }
                        if (onRequestCallBack != null) {
                            onRequestCallBack.onCall(-1, e.getMessage());
                        }
                    }

                    @Override
                    public void onSuccess(String s) {
                        if (onRequestCallBack != null) {
                            onRequestCallBack.onCall(1, s);
                        }
//                        Log.i("HttpUtil", "onSuccess: 请求成功----> " + s);
                    }
                });
        return disposable;
    }

    /**
     * get参数上传
     *
     * @param params
     * @param url
     * @return
     */
    public Disposable getParms(HttpParams params, final String url) {
        GetRequest getRequest = EasyHttp.get(url);
//        getRequest.baseUrl(Config.API.BASE_URL);
        HttpHeaders header = getHeader();
        if (header != null) {
            getRequest.headers(header);
        }
        if (params != null) {
            getRequest.params(params);
        }
        Disposable disposable = getRequest
//                .syncRequest(true)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
//                        if (reLogin(e.getMessage())) {
//                            return;
//                        }
                        if (onRequestCallBack != null) {
                            onRequestCallBack.onCall(-1, e.getMessage());
                        }
                    }

                    @Override
                    public void onSuccess(String s) {
                        onRequestCallBack.onCall(1, s);
//                        if (!reLoggedIn(s) && onRequestCallBack != null) {
//                            onRequestCallBack.onCall(1, s);
//                        }
//                        Log.i("HttpUtil", "onSuccess: 请求成功----> " + s);
                    }
                });
        return disposable;
    }

    private static final String TAG = "yancy";
    private HttpHeaders getHeader() {
        String token = SPUtils.getInstance().getString(Config.Constant.DUODUO_TOKEN);
        if (!TextUtils.isEmpty(token)) {
            HttpHeaders headers = new WeakReference<>(new HttpHeaders()).get();
            headers.put("Authorization", "Bearer " + token);
            Log.e(TAG, "getHeader: Bearer " + token);
            return headers;
        }
        return null;
    }

    /**
     * post上传文本
     *
     * @param o   上传实体
     * @param url 接口地址
     * @return
     */
    public Disposable postJson(Object o, String url) {
        //网络性能统计
        PostRequest post = EasyHttp.post(url);
        HttpHeaders header = getHeader();
        if (header != null) {
            post.headers(header);
        }
        Disposable disposable = post.upJson(new WeakReference<>(new Gson()).get().toJson(o))
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        if (onRequestCallBack != null) {
                            onRequestCallBack.onCall(-1, e.getMessage());
                        }
                    }

                    @Override
                    public void onSuccess(String s) {
//                        Log.i("HttpUtil", "onSuccess: 请求成功----> " + s);

//                        if (!reLoggedIn(s) && onRequestCallBack != null) {
//                            onRequestCallBack.onCall(1, s);
//                        }
                    }
                });
        return disposable;
    }

    /**
     * 上传图片
     *
     * @param url 上传地址
     */
    public Disposable postFile(String OSSHost, String url, String filePath, HttpParams params) {
        File file = new WeakReference<>(new File(filePath)).get();
        PostRequest post = EasyHttp.post(url);
        if (params != null) {
            post.params(params);
        }
        return post.baseUrl(OSSHost)
//                .headers(getHeader())//multipartFile
                .params("file", file, new ProgressResponseCallBack() {
                    @Override
                    public void onResponseProgress(long bytesWritten, long contentLength, boolean done) {
//                        Log.i("FAN", "onResponseProgress: 上传图片3： bytesWritten： " + bytesWritten + "   contentLength: " + contentLength);
                    }
                })
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        if (onRequestCallBack != null) {
                            onRequestCallBack.onCall(-1, e.getMessage());
                        }
                    }

                    @Override
                    public void onSuccess(String s) {
                        if (onRequestCallBack != null) {
                            onRequestCallBack.onCall(1, s);
                        }
                    }
                });
    }

    /**
     * 重新登录
     */
//    private boolean reLoggedIn(String json) {
//        DownBaseEntity baseEntity = GsonUtil.getGson().fromJson(json, DownBaseEntity.class);
//        if (baseEntity.getCode() == 401) {
//            showText(context,"登陆信息失效，请重新登陆！");
//            removeSPData(context);
//            RxActivityTool.skipActivityAndFinishAll(context, LoginActivity.class);
//            return true;
//        }
//        return false;
//    }

    /**
     * 取消网络请求
     *
     * @param disposable Disposable对象
     */
    public void closeDisposable(Disposable disposable) {
        if (disposable != null) {
            EasyHttp.cancelSubscription(disposable);
            disposable = null;
        }
    }


    /**
     * 打开浏览器下载
     */
    public void openBrowserDownload(String url) {
        /**
         * 调用第三方浏览器打开
         * @param context
         * @param url 要浏览的资源地址
         */
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
//        intent.setData(content_url);
//        context.startActivity(intent);
        // 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
        // 官方解释 : Name of the component implementing an activity that can display the intent
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            final ComponentName componentName = intent.resolveActivity(context.getPackageManager());
            // 打印Log   ComponentName到底是什么
//            L.d("componentName = " + componentName.getClassName());
            context.startActivity(Intent.createChooser(intent, "请选择浏览器"));
        } else {
            Toast.makeText(context.getApplicationContext(), "请下载浏览器", Toast.LENGTH_SHORT).show();
        }
    }

    private void showText(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    public interface OnRequestCallBack {
        void onCall(int code, String data);
    }


}
