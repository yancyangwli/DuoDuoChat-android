package com.woniu.core.xmpp.rxbus;

import android.content.Context;
import io.reactivex.Flowable;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;


public class RxBus {
    private static volatile RxBus mInstance;

    private final FlowableProcessor<Object> bus;


    public RxBus() {
//        bus = new SerializedSubject<>(PublishSubject.create());
        //调用toSerialized()方法，保证线程安全
        bus = PublishProcessor.create().toSerialized();
    }

    /**
     * 单例模式RxBus
     *
     * @return
     */
    public static RxBus getInstance() {

        RxBus rxBus2 = mInstance;
        if (mInstance == null) {
            synchronized (RxBus.class) {
                rxBus2 = mInstance;
                if (mInstance == null) {
                    rxBus2 = new RxBus();
                    mInstance = rxBus2;
                }
            }
        }

        return rxBus2;
    }


    /**
     * 发送消息
     *
     * @param object
     */
    public void post(Object object) {

        bus.onNext(object);

    }

    /**
     * 接收消息
     *
     * @param eventType
     * @param <T>
     * @return
     */
    public <T> Flowable<T> toObserverable(Class<T> eventType) {
        return bus.ofType(eventType);
    }


}
