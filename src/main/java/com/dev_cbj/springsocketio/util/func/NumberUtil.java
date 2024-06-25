package com.dev_cbj.springsocketio.util.func;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

public class NumberUtil {
	public static String numberCommaFormat(int number) {
		return new DecimalFormat("###,###,###").format(number);
	}
	public static String numberCommaFormat(String number) {
		if (!StringUtil.isInteger(number)) return number;
		return new DecimalFormat("###,###,###").format(Integer.parseInt(number));
	}
	public static String getEksysFormatNowDate() {
		LocalDateTime now = LocalDateTime.now();
		return new SimpleDateFormat("yyyyMMddHHmmss").format(now);
	}
}