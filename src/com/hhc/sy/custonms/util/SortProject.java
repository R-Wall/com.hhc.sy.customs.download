package com.hhc.sy.custonms.util;

import java.util.Comparator;
import java.util.Date;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCException;

/**
 * 降序排序
 * @author Administrator
 *
 */
public class SortProject implements Comparator<Object> {

	public int compare(Object o1, Object o2) {
		TCComponent t1 = (TCComponent) o1;
		TCComponent t2 = (TCComponent) o2;
		Date d1 = null;
		Date d2 = null;
		try {
			d1 = t1.getDateProperty("creation_date");
			d2 = t2.getDateProperty("creation_date");
		} catch (TCException e) {
			e.printStackTrace();
		}
		if (d1 == null || d2 == null) {
			return 0;
		}	
		if (d2.after(d1)){
			return 1;
		} else if (d2.before(d1)){
			return -1;
		} else {
			return 0;
		}
		
	}
}
