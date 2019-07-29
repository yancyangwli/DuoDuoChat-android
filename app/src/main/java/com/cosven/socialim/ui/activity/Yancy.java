package com.cosven.socialim.ui.activity;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;
import com.cosven.message.dialog.ActionPopWindow;
import com.cosven.message.impl.OnActionPopClickedListener;
import com.google.gson.Gson;
import com.woniu.core.bean.BaseReq;
import com.woniu.core.bean.ResultInfo;
import com.woniu.core.utils.GsonUtil;

import java.io.File;

import static com.woniu.core.app.Zeus.getApplicationContext;

public class Yancy extends Activity {

    public void test(Context context) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText("54545");
        BaseReq mReq = GsonUtil.getGson().fromJson("1213", BaseReq.class);
        GsonUtil.getGson().fromJson(mReq.getStatus(), ResultInfo.class);

        Gson gson = new Gson();


        String a = "23";

        String b = a + "11";
        EditText editText = new EditText(this);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    boolean isCLoa(int b) {
        if (1 > 2)
            return true;
        else if (2 > 3) {
            return true;
        }
        return true;
    }
}
