package com.woniu.core.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.view.View;
import android.widget.Toast;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.google.gson.Gson;
import com.woniu.core.bean.JsonBean;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anlycal<远>
 * @date 2019/5/24
 * @description ...
 */


public class AreaUtil {

    private List<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();

    private OnParseAreaDataListener onParseAreaDataListener;
    private OnSelectAreaCompleteListener onSelectAreaCompleteListener;

    private Thread mThread;

    private Context context;

    private boolean isParseSuccess = false;//判断是否解析完成
    private OptionsPickerView pvOptions;

    public AreaUtil(Context context) {
        this.context = context;
    }

//    public AreaUtil setOnParseAreaDataListener(OnParseAreaDataListener onParseAreaDataListener) {
//        this.onParseAreaDataListener = onParseAreaDataListener;
//        return this;
//    }

    public AreaUtil setOnSelectAreaCompleteListener(OnSelectAreaCompleteListener onSelectAreaCompleteListener) {
        this.onSelectAreaCompleteListener = onSelectAreaCompleteListener;
        return this;
    }

    public void startParseData() {//解析数据
        if (isParseSuccess){
            showPickerView();
            return;
        }
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                /**
                 * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
                 * 关键逻辑在于循环体
                 *
                 * */
                String JsonData = getJson(context, "province.json");//获取assets目录下的json文件数据

                ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体

                /**
                 * 添加省份数据
                 *
                 * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
                 * PickerView会通过getPickerViewText方法获取字符串显示出来。
                 */
                options1Items = jsonBean;

                for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
                    ArrayList<String> cityList = new ArrayList<>();//该省的城市列表（第二级）
                    ArrayList<ArrayList<String>> province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

                    for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                        String cityName = jsonBean.get(i).getCityList().get(c).getName();
                        cityList.add(cityName);//添加城市
                        ArrayList<String> city_AreaList = new ArrayList<>();//该城市的所有地区列表

                        //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                /*if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    city_AreaList.add("");
                } else {
                    city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                }*/
                        city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                        province_AreaList.add(city_AreaList);//添加该省所有地区数据
                    }

                    /**
                     * 添加城市数据
                     */
                    options2Items.add(cityList);

                    /**
                     * 添加地区数据
                     */
                    options3Items.add(province_AreaList);
                }
                isParseSuccess = true;
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        if (onParseAreaDataListener != null){
//                            onParseAreaDataListener.onSuccess(options1Items,options2Items,options3Items);
//                        }
                        showPickerView();
                        mThread = null;
                    }
                });
            }
        });
        mThread.start();
    }

    private void showPickerView() {// 弹出选择器

        //返回的分别是三个级别的选中位置
//                String tx = opt1tx + opt2tx + opt3tx;
//设置选中项文字颜色
        if (pvOptions == null) {
            pvOptions = PickerUtil.Companion.setPickerStyle("城市选择",new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
                        @Override
                        public void onOptionsSelect(int options1, int options2, int options3, View v) {
                            //返回的分别是三个级别的选中位置
                            String opt1tx = options1Items.size() > 0 ?
                                    options1Items.get(options1).getPickerViewText() : "";

                            String opt2tx = options2Items.size() > 0
                                    && options2Items.get(options1).size() > 0 ?
                                    options2Items.get(options1).get(options2) : "";

                            String opt3tx = options2Items.size() > 0
                                    && options3Items.get(options1).size() > 0
                                    && options3Items.get(options1).get(options2).size() > 0 ?
                                    options3Items.get(options1).get(options2).get(options3) : "";

//                String tx = opt1tx + opt2tx + opt3tx;
                            if (onSelectAreaCompleteListener != null) {
                                onSelectAreaCompleteListener.onCompleted(opt1tx, opt2tx, opt3tx);
                            }
                        }
                    }))
                    .build();
        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
            pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        }
        pvOptions.show();
    }


    public ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    private String getJson(Context context, String fileName) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public interface OnParseAreaDataListener{
        void onSuccess(List<JsonBean> options1Items,List<ArrayList<String>> options2Item,List<ArrayList<ArrayList<String>>> options3Items);
    }

    public interface OnSelectAreaCompleteListener{
        void onCompleted(String province,String city,String area);
    }
}
