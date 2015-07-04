/*
 * Created on 2004-6-24
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.royalstone.pos.util;

/**
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class UserCancelException extends Exception 
{

	/**
	 * 
	 */
	public UserCancelException() {
		super();
	}

	/**
	 * @param message
	 */
	public UserCancelException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public UserCancelException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UserCancelException(String message, Throwable cause) {
		super(message, cause);
	}
}
