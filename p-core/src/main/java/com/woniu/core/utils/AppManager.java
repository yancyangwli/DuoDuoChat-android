package com.woniu.core.utils;

import android.app.Activity;

import com.woniu.core.R;

import java.util.Stack;

/**
 * ClassName:
 * Description: Activity管理
 * Created by XL on 2018/6/11.
 * Modified by
 */
public class AppManager {

    private static AppManager appManager;
    private static Stack<Activity> stack = new Stack<>();

    private AppManager() {
    }

    public static AppManager get() {
        if (appManager == null) {
            synchronized (AppManager.class) {
                if (appManager == null) {
                    appManager = new AppManager();
                }
            }
        }
        return appManager;
    }

    public void add(Activity activity) {
        if (stack == null) {
            stack = new Stack<>();
        }
        activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        stack.add(activity);
    }

    public void remove(Activity activity) {
        if (stack != null && stack.size() > 0) {
            for (Activity ac : stack) {
                if (activity.equals(ac)) {
                    stack.remove(activity);
                }
            }
        }
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity current() {
        if (stack != null && stack.size() > 0) {
            Activity activity = stack.lastElement();
            return activity;
        } else {
            return null;
        }
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = stack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     *
     * @param activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            stack.remove(activity);
            activity.finish();
            activity.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        }
    }

    /**
     * 结束指定类名的Activity
     *
     * @param clazz
     */
    public void finishActivity(Class<?> clazz) {
        try {
            for (Activity activity : stack) {
                if (activity.getClass().equals(clazz)) {
                    finishActivity(activity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        try {
            for (Activity activity : stack) {
                if (activity != null)
                    activity.finish();
            }
            stack.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 退出应用程序
     */
//    public void exitApp() {
//        Context context = App.getAppContext();
//        //关闭所有打开的Activity
//        finishAllActivity();
//        //关闭后台进程
//        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        activityManager.killBackgroundProcesses(context.getPackageName());
//        //退出应用
//        System.exit(0);
//    }


}
