package com.satansin.android.compath.util;

import java.util.Calendar;

public class UITimeGenerator {

	public String getUITime(Calendar calendar) {
		String year = String.valueOf(calendar.get(Calendar.YEAR));
		String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
		String date = String.valueOf(calendar.get(Calendar.DATE));
		String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
		String minute = String.valueOf(calendar.get(Calendar.MINUTE));
		return year + "Äê" + month + "ÔÂ" + date + "ÈÕ " + hour + ":" + minute;
	}

}
