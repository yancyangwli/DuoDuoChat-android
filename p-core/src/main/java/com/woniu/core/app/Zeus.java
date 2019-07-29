package com.woniu.core.app;


import android.content.Context;

/**
 * 管理项目配置
 * author: leo
 */
public final class Zeus {

    public static Configurator init(Context context) {
        Configurator
                .getInstance()
                .getRobotConfig()
                .put(ConfigKeys.APPLICATION_CONTEXT, context.getApplicationContext());
        return Configurator.getInstance();
    }

    public static Configurator getConfigurator() {
        return Configurator.getInstance();
    }

    public static <T> T getConfiguration(Object key) {
        return getConfigurator().getConfiguration(key);
    }

    public static Context getApplicationContext() {
        return getConfiguration(ConfigKeys.APPLICATION_CONTEXT);
    }
}
