package com.efuture.javaPos.Struct;

import java.io.Serializable;

public class WebServiceFunDef implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4359719397835554580L;
	
	private String cmdcode 			= null;
	private String methodname			= null;
	private String javaposmappingname	= null;
	private String parametername		= null;
	private String parametertype		= null;
	private String returntype			= null;
	private String retSuccessError		= null;
	
	public void setCmdCode(String cmdcode)
	{
		this.cmdcode = cmdcode;
	}
	
	public String getCmdCode()
	{
		return cmdcode;
	}
	
	public void setMethodName(String methodname)
	{
		this.methodname		= methodname;
	}
	
	public String getMethodName()
	{
		return methodname	;
	}
	
	public void setJavaPosMappingName(String javaposmappingname)
	{
		this.javaposmappingname = javaposmappingname;
	}
	
	public String getJavaPosMappingName()
	{
		return javaposmappingname;
	}
	
	public void setParameterName(String parametername)
	{
		this.parametername	= parametername;
	}
	
	public String getParameterName()
	{
		return parametername;
	}
	
	public void setParameterType(String parametertype)
	{
		this.parametertype	= parametertype;
	}
	
	public String getParameterType()
	{
		return parametertype;
	}
	
	public void setReturnType(String returntype)
	{
		this.returntype	 = returntype;
	}
	
	public String getReturnType()
	{
		return returntype;
	}
	
	public void setRetSuccessError(String retSuccessError)
	{
		this.retSuccessError = retSuccessError;
	}
	
	public String getRetSuccessError()
	{
		return retSuccessError;
	}
}
