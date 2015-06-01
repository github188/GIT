package com.efuture.javaPos.Device;

/**
 * 第三方插件
 */

import com.efuture.javaPos.Device.Interface.Interface_RdPlugins;

public class RdPlugins
{
	public static RdPlugins plugin;

	private Interface_RdPlugins plugins1;
	private Interface_RdPlugins plugins2;
	private Interface_RdPlugins plugins3;
	private Interface_RdPlugins plugins4;
	private Interface_RdPlugins plugins5;

	public static RdPlugins getDefault()
	{
		if (RdPlugins.plugin == null)
			RdPlugins.plugin = new RdPlugins();

		return plugin;
	}

	private Interface_RdPlugins loadModule(String module)
	{
		Interface_RdPlugins obj = null;
		try
		{
			Class cl = Class.forName(module);

			obj = (Interface_RdPlugins) cl.newInstance();
			return obj;

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public Interface_RdPlugins getPlugins1()
	{
		return plugins1;
	}

	public Interface_RdPlugins getPlugins2()
	{
		return plugins2;
	}

	public Interface_RdPlugins getPlugins3()
	{
		return plugins3;
	}

	public Interface_RdPlugins getPlugins4()
	{
		return plugins4;
	}

	public Interface_RdPlugins getPlugins5()
	{
		return plugins5;
	}

	public void loadPlugins1(String module)
	{
		plugins1 = loadModule(module);
		plugins1.loadPlugins();
	}

	public void loadPlugins2(String module)
	{
		plugins2 = loadModule(module);
		plugins2.loadPlugins();
	}

	public void loadPlugins3(String module)
	{
		plugins3 = loadModule(module);
		plugins3.loadPlugins();
	}

	public void loadPlugins4(String module)
	{
		plugins4 = loadModule(module);
		plugins4.loadPlugins();
	}

	public void loadPlugins5(String module)
	{
		plugins5 = loadModule(module);
		plugins5.loadPlugins();
	}

	public void releasePlugins()
	{
		if (plugins1 != null)
			plugins1.releasePlugins();
		
		if (plugins2 != null)
			plugins2.releasePlugins();
		
		if (plugins3 != null)
			plugins3.releasePlugins();
		
		if (plugins4 != null)
			plugins4.releasePlugins();
		
		if (plugins5 != null)
			plugins5.releasePlugins();
	}
}
