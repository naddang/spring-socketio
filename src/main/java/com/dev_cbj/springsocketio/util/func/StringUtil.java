package com.dev_cbj.springsocketio.util.func;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.regex.Pattern;

public class StringUtil {
	private static final ObjectMapper om = new ObjectMapper();
	
	/**
	 * 문자열이 숫자인지 확인하는 메서드
	 * @param str
	 * @return
	 */
	public static boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException | NullPointerException nfe) {
			return false;
		}
	}
	
	/**
	 * 문자열이 yyyy-MM-dd 형식이 맞는지 확인하는 메서드
	 * @param str
	 * @return
	 */
	public static boolean isDate(String str) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		
		try {
			LocalDate.parse(str, dtf);
			return true;
		} catch (DateTimeParseException | NullPointerException e) {
			return false;
		}
	}
	
	/**
	 * 문자열이 0~24사이의 값이 맞는지 확인하는 메서드
	 * @param str
	 * @return
	 */
	public static boolean isHour(String str) {
		try {
			return Integer.parseInt(str) > -1 && Integer.parseInt(str) < 25;
		} catch (NumberFormatException | NullPointerException e) {
			return false;
		}
	}
	
	public static boolean isEmpty(String str) {
		return str == null || str.trim().equals("");
	}
	
	public static boolean isEmpty(String... str) {
		for (String s : str) {
			if (isEmpty(s)) return true;
		}
		return false;
	}
	
	/**
	 * 받은 문자열이 전화번호인지 여부를 리턴하는 메서드
	 * @param str
	 * @return
	 */
	public static boolean isTelNumber(String str) {
		return Pattern.matches("\\d{2,3}-\\d{3,4}-\\d{4}", str);
	}
	
	/**
	 * 받은 문자열이 메일형식인지 확인하는 메서드
	 * @param str
	 * @return
	 */
	public static boolean isMail(String str) {
		return Pattern.matches("([\\da-zA-Z_-]+)@([\\da-zA-Z_-]+)\\.([\\da-zA-Z._-]+)", str);
	}
	
	public static boolean isbNumb(String str) {
		return Pattern.matches("(\\d{3})-(\\d{2})-(\\d{5})", str);
	}
	
	public static String yyyyMMddToKorean(String dateString) {
		if (isEmpty(dateString) || dateString.trim().isEmpty()) return null;
		try {
			DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate date = LocalDate.parse(dateString, fmt);
			
			return date.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));
		} catch (DateTimeParseException e) {
			return null;
		}
	}
	
	public static String stringToHtmlSign(String str) {
		
		return str.replaceAll("&amp;", "&")
				.replaceAll("&lt;", "<")
				.replaceAll("&gt;", ">")
				.replaceAll("&quot;", "\"")
				.replaceAll("&#39;", "\'");
	}
	
	public static String getDATE_FORMAT(int to, int from, String data) {
		String format1 = null, format2 = null;
		
		if (to == 1) {
			format1 = "yyyyMMddHHmmss";
			
		} else if (to == 2) {
			format1 = "yyyy년 MM월 dd일 HH:mm";
		}
		
		if (from == 1) {
			format2 = "yyyyMMddHHmmss";
			
		} else if (from == 2) {
			format2 = "yyyy년 MM월 dd일 HH:mm";
		}
		
		SimpleDateFormat fm = new SimpleDateFormat(format1);
		Date date = null;
		try {
			date = fm.parse(data);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format2);
		return simpleDateFormat.format(date);
	}
	
	/**
	 * JSON 형식인지 여부를 확인하는 메서드
	 * @param string 문자열
	 * @return JSON 형식인지 여부
	 */
	public static boolean isJson(String string) {
		if (isEmpty(string)) return false;
		return string.trim().startsWith("{") && string.trim().endsWith("}");
	}
	
	public static JSONObject toJsonObj(Object obj) {
		try {
			String s = om.writeValueAsString(obj);
			return new JSONObject(s);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static <T> T toPojoObj(JSONObject jo, Class<T> clazz) {
		try {
			return om.readValue(jo.toString(), clazz);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String readInputStream(InputStream inputStream) {
		StringBuilder result = new StringBuilder();
		byte[] buffer = new byte[1024];
		int length;
		
		try {
			while ((length = inputStream.read(buffer)) != -1) {
				result.append(new String(buffer, 0, length));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result.toString();
	}
	
	
}