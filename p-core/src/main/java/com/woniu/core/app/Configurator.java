package com.woniu.core.app;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Interceptor;

/**
 * final class Configurator ：通用配置可添加全局统一配置
 * author: leo
 */
public final class Configurator {

    private static final HashMap<Object, Object> ROBOT_CONFIG = new HashMap<>();
    private static final ArrayList<Interceptor> INTERCEPTORS = new ArrayList<>();

    //通过静态内部类实现单例 -- 并发
    private static final class Holder {
        private static final Configurator INSTANCE = new Configurator();
    }

    final HashMap<Object, Object> getRobotConfig() {
        return ROBOT_CONFIG;
    }

    static Configurator getInstance() {
        return Holder.INSTANCE;
    }

    public final void configure() {
        ROBOT_CONFIG.put(ConfigKeys.CONFIG_READY, true);
    }

    public final Configurator withApiHost(String host) {
        ROBOT_CONFIG.put(ConfigKeys.API_HOST, host);
        return this;
    }

    public final Configurator withInterceptor(Interceptor interceptor) {
        INTERCEPTORS.add(interceptor);
        ROBOT_CONFIG.put(ConfigKeys.INTERCEPTOR, INTERCEPTORS);
        return this;
    }

    public final Configurator withInterceptors(ArrayList<Interceptor> interceptors) {
        INTERCEPTORS.addAll(interceptors);
        ROBOT_CONFIG.put(ConfigKeys.INTERCEPTOR, INTERCEPTORS);
        return this;
    }

    private void checkConfiguration() {
        final boolean isReady = (boolean) ROBOT_CONFIG.get(ConfigKeys.CONFIG_READY);
        if (!isReady) {
            throw new RuntimeException("Configuration is not ready,call configure() method");
        }
    }

    @SuppressWarnings("unchecked")
    final <T> T getConfiguration(Object key) {
        checkConfiguration();
        final Object value = ROBOT_CONFIG.get(key);
        if (value == null && key!= ConfigKeys.INTERCEPTOR) {
            throw new NullPointerException(key.toString() + " IS NULL");
        }
        return (T) ROBOT_CONFIG.get(key);
    }
}
