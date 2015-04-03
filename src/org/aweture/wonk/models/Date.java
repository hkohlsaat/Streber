package org.aweture.wonk.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.util.Log;

public class Date extends GregorianCalendar {
	private static final String LOG_TAG = Date.class.getSimpleName();
	
	private static final String DATE_FORMAT = "dd.MM.yyyy";
	private static final String DATETIME_FORMAT = "dd.MM.yyyy HH:mm";
	
	public static Date fromStringDate(String date) {
		return toDate(date, DATE_FORMAT);
	}
	
	public static Date fromStringDateTime(String dateTime) {
		return toDate(dateTime, DATETIME_FORMAT);
	}
	
	private static Date toDate(String stamp, String pattern) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			Date instance = new Date();
			instance.setTime(sdf.parse(stamp));
			return instance;
		} catch (ParseException e) {
			Log.e(LOG_TAG, Log.getStackTraceString(e));
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public String toDateString() {
		return toString(DATE_FORMAT);
	}
	
	public String toDateTimeString() {
		return toString(DATETIME_FORMAT);
	}
	
	private String toString(String pattern) {
		java.util.Date date = getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		String dateString = sdf.format(date);
		return dateString;
	}
	
	@Override
	public String toString() {
		java.util.Date date = getTime();
		return date.toString();
	}
	
	public String resolveToRelativeWord() {
		Calendar yesterday = GregorianCalendar.getInstance();
		yesterday.add(Calendar.DAY_OF_YEAR, -1);
		
		Calendar today = GregorianCalendar.getInstance();

		Calendar tomorrow = GregorianCalendar.getInstance();
		tomorrow.add(Calendar.DAY_OF_YEAR, 1);

		if (isSameDay(today, this)) {
			return "heute";
		} else if (isSameDay(yesterday, this)) {
			return "gestern";
		} else if (isSameDay(tomorrow, this)) {
			return "morgen";
		}
		return null;
	}
	
	private boolean isSameDay(Calendar calendar1, Calendar calendar2) {
		boolean sameYear = calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR);
		boolean sameDayOfYear = calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR);
		return sameYear && sameDayOfYear;
	}

}
