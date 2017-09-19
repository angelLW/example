package com.tjresearch.api.common;

public class ApiException extends ReflectiveOperationException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ApiException(String e){
		System.out.println(e);
	}
}
