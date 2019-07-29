package com.woniu.core.utils;

import com.flyco.tablayout.listener.CustomTabEntity;
import com.woniu.core.R;
import com.woniu.core.bean.TabEntity;

import java.util.ArrayList;

/**
 * Created by Anlycal(远) on 2018/8/22.
 * 作用： ****
 */

public class TabUtil {

    public static ArrayList<CustomTabEntity> getTabEntities(String[] titles, int[] mIconSelectIds, int[] mIconUnselectIds) {
        ArrayList<CustomTabEntity> entities = new ArrayList<>();
        TabEntity entity;
        for (int i = 0; i < titles.length; i++) {
            if (mIconSelectIds != null && mIconUnselectIds != null) {
                entity = new TabEntity(titles[i], mIconSelectIds[i], mIconUnselectIds[i]);
            }else {
                entity = new TabEntity(titles[i], R.mipmap.ic_launcher,R.mipmap.ic_launcher);
            }
            entities.add(entity);
        }
        return entities;
    }
}
