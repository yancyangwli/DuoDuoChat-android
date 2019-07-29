package com.woniu.core.activities;

import android.app.Activity;


import java.util.Stack;

/**
 * 栈结构管理活动页面
 */
public class ActivityManager {
    private Stack<Activity> activityStack = new Stack<>();

    private static ActivityManager instance;

    private ActivityManager() {
    }

    public static ActivityManager getInstance() {
        if (null == instance) {
            instance = new ActivityManager();
        }
        return instance;
    }

    public void addActivity(Activity activity) {
        activityStack.push(activity);
    }

    public void exit() {
        //activity被finish后，activityStack中的存储的内容也会被移除,会抛出异常
        int size = activityStack.size() - 1;
        for (int i = size; i >= 0; i--) {
            activityStack.get(i).finish();
        }
        activityStack.clear();
        System.exit(0);
    }

    public void removeActivity(Activity activity) {
        activityStack.remove(activity);
    }

    /**
     * 清除当前Activity之前的所有Activity
     */
    public void clearBefore() {
        if (activityStack.isEmpty()) {
            return;
        }
        Activity current = activityStack.pop();
        if (!activityStack.isEmpty()) {
            Activity activity;
            while ((activity = activityStack.pop()) != null) {
                activity.finish();
                if (activityStack.isEmpty()) {
                    break;
                }
            }
        }
        activityStack.clear();
        activityStack.push(current);
    }
}
