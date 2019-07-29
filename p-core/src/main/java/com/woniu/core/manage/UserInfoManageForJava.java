package com.woniu.core.manage;

import com.woniu.core.bean.entity.UserInfoEntity;

public class UserInfoManageForJava {

    private UserInfoEntity userInfoEntity;

    private volatile static UserInfoManageForJava forJava = new UserInfoManageForJava();

    private UserInfoManageForJava() {

    }

    public static UserInfoManageForJava newInstance() {
        if (forJava == null) {
            synchronized (UserInfoManageForJava.class) {
                if (forJava == null) {
                    forJava = new UserInfoManageForJava();
                }
            }
        }
        return forJava;
    }

    public UserInfoEntity getUserInfoEntity() {
        return userInfoEntity;
    }

    public void setUserInfoEntity(UserInfoEntity userInfoEntity) {
        this.userInfoEntity = userInfoEntity;
    }
}
