package com.satansin.android.compath.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UITimeGenerator {

	private static final long DAY_MILLIS_COUNT = 24 * 60 * 60 * 1000;
	private static final long HOUR_MILLIS_COUNT = 60 * 60 * 1000;
	private static final long MINUTE_MILLIS_COUNT = 60 * 1000;
	
	private static final String HOURS_AGO = "小时前";
	private static final String MINUTES_AGO = "分钟前";
	
	private String getSimpleFormattedTime(long timeInMillis, boolean detail) {
		Calendar time = Calendar.getInstance();
		time.setTimeInMillis(timeInMillis);

		Calendar current = Calendar.getInstance();
		if (time.get(Calendar.YEAR) != current.get(Calendar.YEAR)) {
			return (detail ? new SimpleDateFormat("yyyy-M-d HH:mm", Locale.getDefault()).format(new Date(timeInMillis)) :
					new SimpleDateFormat("yyyy-M-d", Locale.getDefault()).format(new Date(timeInMillis)));
		} else {
			if (!(time.get(Calendar.MONTH) == current.get(Calendar.MONTH) && (time
					.get(Calendar.DATE) == current.get(Calendar.DATE)))) {
				return (detail ? new SimpleDateFormat("M-d HH:mm", Locale.getDefault()).format(new Date(timeInMillis)) :
					new SimpleDateFormat("M-d", Locale.getDefault()).format(new Date(timeInMillis)));
			} else {
				return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(timeInMillis));
			}
		}
	}
	
	private String getCompactFormattedTime(long timeInMillis, boolean detail) {
		return (detail ? new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date(timeInMillis)) :
				new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date(timeInMillis)));
	}

	public String getFormattedMessageTime(long timeInMillis) {
		return getSimpleFormattedTime(timeInMillis, true);
	}
	
	public String getFormattedFeedTime(long timeInMillis) {
		long current = Calendar.getInstance().getTimeInMillis();
		
		long diffInMillis = current - timeInMillis;
		if (diffInMillis >= DAY_MILLIS_COUNT) {
			return getSimpleFormattedTime(timeInMillis, false);
		} else {
			int diffInHour = (int) (diffInMillis / HOUR_MILLIS_COUNT);
			if (diffInHour > 0) {
				return (String.valueOf(diffInHour) + HOURS_AGO);
			} else {
				int diffInMinute = (int) (diffInMillis / MINUTE_MILLIS_COUNT);
				if (diffInMinute == 0) {
					diffInMinute++;
				}
				return (String.valueOf(diffInMinute) + MINUTES_AGO);
			}
		}
	}

	public String getFormattedPhotoNameTime() {
		long current = Calendar.getInstance().getTimeInMillis();
		return getCompactFormattedTime(current, true);
	}

}
