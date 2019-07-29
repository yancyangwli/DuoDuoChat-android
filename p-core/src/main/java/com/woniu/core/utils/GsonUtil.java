package com.woniu.core.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woniu.core.bean.BaseReq;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;

/**
 * Created by Anlycal(远) on 2018/9/1.
 * 作用： ****
 */

public class GsonUtil {


    public static Gson getGson(){
        Gson gson = new WeakReference<>(new Gson()).get();
        return gson;
    }

//    public BaseReq<T>  parseData(String jsonData){
//        Type type = new TypeToken<BaseReq<T>>() {
//        }.getType();
//
//        return getGson().<BaseReq<T>>fromJson(jsonData,type);
////        return getGson().fromJson(jsonData,type);
//    }

}
