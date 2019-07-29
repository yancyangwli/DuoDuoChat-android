package com.cosven.socialim.contact.view;

import android.text.TextUtils;
import com.woniu.core.bean.entity.ContactEntity;

import java.util.Comparator;

public class PinyinComparator implements Comparator<ContactEntity> {

	public int compare(ContactEntity o1, ContactEntity o2) {
		if (TextUtils.isEmpty(o1.getFriend_remark()) || TextUtils.isEmpty(o2.getFriend_remark())){
			return 1;
		}else if (o1.getFriend_remark().equals("@")
				|| o2.getFriend_remark().equals("#")) {
			return -1;
		} else if (o1.getFriend_remark().equals("#")
				|| o2.getFriend_remark().equals("@")) {
			return 1;
		} else {
			return o1.getFriend_remark().compareTo(o2.getFriend_remark());
		}
	}

}
