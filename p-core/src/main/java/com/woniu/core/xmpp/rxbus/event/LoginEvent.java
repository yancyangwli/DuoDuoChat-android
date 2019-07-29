package com.woniu.core.xmpp.rxbus.event;

/**
 * @author Anlycal<远>
 * @date 2019/6/18
 * @description ...
 */


public class LoginEvent {
    public boolean loginSuccess;

    public LoginEvent(boolean loginSuccess) {
        this.loginSuccess = loginSuccess;
    }
}
