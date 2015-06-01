package com.efuture.configure;

import com.efuture.javaPos.Global.GlobalVar;
import com.swtdesigner.SWTResourceManager;

public class setConfigureMain
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		if (args != null)
		{
			if (args.length > 0)
			{
				GlobalVar.RefushConfPath(args[0].trim());
			}
		}

		new setConfigureMain();
		
		SWTResourceManager.dispose();
		System.exit(0);
	}

	public setConfigureMain()
	{
		setConfigureForm window = new setConfigureForm();
		window.open();
	}
}
