package com.woniu.core.router;

/**
 * @author Anlycal<远>
 * @date 2019/6/4
 * @description ...
 */


public class RegUtil {
    /**
     * 判断密码是否包含字母和数字
     *
     * @param psw 当前设置的密码
     * @return boolean
     */
    public static boolean isPsw(String psw) {
        String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$";

        return psw.matches(regex);
    }
}
