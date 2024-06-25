package com.dev_cbj.springsocketio.util.func;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
	public static String formatNowDate(String format) {
		LocalDateTime now = LocalDateTime.now();
		return now.format(DateTimeFormatter.ofPattern(format));
	}
	
	public static String formatDate(String date, String fromFormat, String toFormat) {
		try {
			return new SimpleDateFormat(toFormat).format(new SimpleDateFormat(fromFormat).parse(date));
		} catch (Exception e) {
			return date;
		}
	}
}