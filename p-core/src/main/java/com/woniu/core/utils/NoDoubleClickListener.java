package com.woniu.core.utils;

import android.view.View;

import java.util.Calendar;

import javax.crypto.interfaces.PBEKey;

public class NoDoubleClickListener implements View.OnClickListener {


    protected void noRepeatClick(View view) {

    }

    ;

    @Override
    public void onClick(View view) {
        noRepeatClick(view);
    }

    public abstract static class aaa extends NoDoubleClickListener {
        @Override
        protected void noRepeatClick(View view) {
            super.noRepeatClick(view);
        }
    }


}


