package com.efuture.javaPos.Device.Interface;

/**
 * 
 * 定义第三方插件接口
 * 
 */
public interface Interface_RdPlugins
{
	public boolean loadPlugins();

	public boolean exec(String param);

	public boolean exec(int code, String param);

	public String getErrorCode();

	public String getErrorMsg();

	public Object getObject();

	public boolean releasePlugins();

}
