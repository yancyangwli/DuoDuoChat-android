package com.woniu.core.xmpp.rxbus.event;

/**
 * @author Anlycal<远>
 * @date 2019/6/25
 * @description ...
 */


public class BusEvent<T> {
    private int position;
    private T data;

    public BusEvent(){

    }

    public BusEvent(int position, T data) {
        this.position = position;
        this.data = data;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BusEvent{" +
                "position=" + position +
                ", t=" + data +
                '}';
    }
}
