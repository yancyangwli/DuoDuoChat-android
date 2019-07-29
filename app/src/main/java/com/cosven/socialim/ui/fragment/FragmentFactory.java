package com.cosven.socialim.ui.fragment;

import com.chat.moduledynamic.ui.fragment.DynamicFragment;
import com.cosven.socialim.contact.fragment.ContactFragment;
import com.woniu.core.fragment.BaseFragment;

/**
 * Fragment工厂
 */
public class FragmentFactory {

    private static MessageFragment messageFragment = null;
    private static ContactFragment contactFragment = null;
    //    private static DynamicFragment dynamicFragment = null;
    private static DynamicFragment dynamicFragment;
    private static MyFragment myFragment = null;

    public static BaseFragment getFragement(int position) {
        BaseFragment base = null;
        switch (position) {
            case 0://消息列表
                if (messageFragment == null) {
                    messageFragment = new MessageFragment();
                }
                base = messageFragment;
                break;

            case 1://联系人
                if (contactFragment == null) {
                    contactFragment = new ContactFragment();
                }
                base = contactFragment;
                break;
            case 2://动态列表
                if (dynamicFragment == null) {
                    dynamicFragment = new DynamicFragment();
                }
                base = dynamicFragment;
                break;

            case 3://个人中心
                if (myFragment == null) {
                    myFragment = new MyFragment();
                }
                base = myFragment;
                break;

        }
        return base;
    }


}
