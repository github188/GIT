/*
 * Created on 2004-6-24
 */
package com.royalstone.pos.util;

/**
 * 用于传递用户退出请求.
 * @author Mengluoyi
 *
 */
public class UserExitException extends Exception 
{

	public UserExitException() {
		super();
	}

	/**
	 * @param message	说明信息.
	 */
	public UserExitException(String message) {
		super(message);
	}

	/**
	 * @param cause		退出原因代码.
	 */
	public UserExitException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message	说明信息.
	 * @param cause		退出原因代码.
	 */
	public UserExitException(String message, Throwable cause) {
		super(message, cause);
	}
}
