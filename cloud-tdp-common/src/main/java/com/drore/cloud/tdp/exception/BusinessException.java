package com.drore.cloud.tdp.exception;
/**
 * 说明:自定义错误异常
 * 创建日期: 2016年11月2日 下午8:43:45
 * 作者: ZENLIN
 */
public class BusinessException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7493711492820795133L;

	public BusinessException(Throwable cause) {
	    super(cause);
	}

	public BusinessException(String message) {
	    super(message);
	}

	public BusinessException(String message, Throwable cause) {
	    super(message , cause);
	}


}
