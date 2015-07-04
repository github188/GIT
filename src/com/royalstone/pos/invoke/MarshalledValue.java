package com.royalstone.pos.invoke;

import java.io.Serializable;

/**
 * 封装需要在远程调用中传递的参数的类
 * @author liangxinbiao
 */
public class MarshalledValue implements Serializable{
	
	Object[] values;
	
	public MarshalledValue(Object[] values){
		this.values=values;
	}
	
	public Object[] getValues(){
		return values;
	}

}
