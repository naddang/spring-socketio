package com.dev_cbj.springsocketio.util.func;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Field;
import java.util.*;

public class ClassUtil {
	/**
	 * 객체 내 null, 0이 아닌 property만 Map으로 변환
	 *
	 * @param obj : Map으로 변환할 객체
	 * @return : Map<String, Object>
	 */
	public static Map<String, Object> getExistPropertiesMap(Object obj) {
		Map<String, Object> resultMap = new HashMap<>(); // 결과값을 담을 Map
		Class<?> clazz; // 객체의 클래스
		List<Field> properties; // 객체의 property 목록
		Object value; // property의 값
		
		if (obj == null) return resultMap;
		
		clazz = obj.getClass();
		properties = getAllFields(clazz);
		
		//객체의 property를 순회하며 값이 null, 0이 아닌 경우 Map에 추가
		for (Field property : properties) {
			try {
				//private 접근 권한 허용
				property.setAccessible(true);
				value = property.get(obj);
				
				//if: 값이 null 이거나 0인 경우
				if (value == null || value.equals(0)) continue;
				
				resultMap.put(property.getName(), value);
				
				//private 접근 권한 제한
				property.setAccessible(false);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		return resultMap;
	}
	
	/**
	 * 객체 내 null, 0이 아닌 property만 Map으로 변환 후 list에 담아 반환
	 * @param list : Map으로 변환할 객체 리스트
	 * @return : List<Map<String, Object>>
	 */
	public static List<Map<String, Object>> getExistPropertiesMap(List<?> list) {
		List<Map<String, Object>> resultList = new ArrayList<>();
		
		if (list == null) return resultList;
		
		list.forEach(obj -> resultList.add(getExistPropertiesMap(obj)));
		
		return resultList;
	}
	
	public static JsonElement getExistPropertiesJson(Object obj) {
		return new Gson().toJsonTree(getExistPropertiesMap(obj));
	}
	public static JsonElement getExistPropertiesJson(List<?> list) {
		return new Gson().toJsonTree(getExistPropertiesMap(list));
	}
	
	public static List<Field> getAllFields(Class<?> clazz) {
		List<Field> fields = new ArrayList<>();
		
		while (clazz != null) {
			fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
			clazz = clazz.getSuperclass();
		}
		
		return fields;
	}
	
	/**
	 * 객체를 JsonElement로 변환
	 *
	 * @param obj : 변환할 객체
	 * @return : JsonElement
	 */
	public static JsonElement objectToJsonElement(Object obj) {
		return new Gson().toJsonTree(obj);
	}
}