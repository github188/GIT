
package com.royalstone.pos.invoke.realtime;

/**
 * 实时查价时产生的异常
 * @author liangxinbiao
 */
public class RealTimeException extends Exception{
	
	private boolean isNetworkError=false;
	
	public RealTimeException(String message){
		super(message);
	}

	public RealTimeException(String message,boolean value){
		super(message);
		setNetworkError(value);
	}
	
	public boolean isNetworkError(){
		return isNetworkError;
	}
	
	public void setNetworkError(boolean value){
		isNetworkError=value;
	}
	
}
