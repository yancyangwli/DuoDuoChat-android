package com.woniu.core.utils

import android.graphics.Color
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import java.util.*

/**
 * @author Anlycal<远>
 * @date 2019/6/5
 * @description ...
 */


class PickerUtil {

    companion object{
        fun setPickerStyle(title:String,builder: OptionsPickerBuilder): OptionsPickerBuilder {
            return builder.setTitleText(title)
                .setSubmitColor(Color.parseColor("#11DBFF"))
                .setCancelColor(Color.parseColor("#999999"))
                .setDividerColor(Color.GRAY)
                .setLineSpacingMultiplier(2.2f)
                .setTextColorCenter(Color.parseColor("#11DBFF")) //设置选中项文字颜色
                .setContentTextSize(16)
        }

        fun setTimePickerStyle(startDate:Calendar,endDate:Calendar,selectedDate:Calendar,builder: TimePickerBuilder):TimePickerBuilder{
            return builder.setType(booleanArrayOf(true, true, true, false, false, false))
                .setLabel("", "", "", "", "", "") //设置空字符串以隐藏单位提示   hide label
                .setDividerColor(Color.DKGRAY)
                .setContentTextSize(20)
                .setDate(selectedDate)
                .setSubmitColor(Color.parseColor("#11DBFF"))
                .setCancelColor(Color.parseColor("#999999"))
                .setRangDate(startDate, selectedDate)
//                .setDecorView(mFrameLayout)//非dialog模式下,设置ViewGroup, pickerView将会添加到这个ViewGroup中
                .setOutSideColor(0x00000000)
                .setLineSpacingMultiplier(2.2f)
                .setOutSideCancelable(true)
        }
    }
}