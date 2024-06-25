package com.dev_cbj.springsocketio.util.func;

import java.util.List;

/**
 * 컬렉션들에 사용하는 유틸리티 클래스
 */
public class CollectionUtil {
	/**
	 * 다음 요소를 리턴하는 메서드
	 * @param list 대상 리스트
	 * @param element 찾을 요소
	 * @return 다음 요소
	 */
	public static <T> T nextElement(List<T> list, T element){
		if (list == null) return null;
		int nextIndex = list.indexOf(element) + 1;
		return list.size() <= nextIndex ? null : list.get(nextIndex);
		
	}
	
	/**
	 * 이전 요소를 리턴하는 메서드
	 * @param list 대상 리스트
	 * @param element 찾을 요소
	 * @return 이전 요소
	 */
	public static <T> T previousElement(List<T> list,T element){
		int previousIndex = list.indexOf(element) - 1;
		if (previousIndex < 0) return null;
		return list.size() <= previousIndex ? null : list.get(previousIndex);
		
	}
}