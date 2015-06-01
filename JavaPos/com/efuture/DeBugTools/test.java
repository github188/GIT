package com.efuture.DeBugTools;

public class test
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		new test();
	}
	
	public test()
	{
		PosLog.getLog(getClass()).debug("aaaaaaaaaaaaaaa");
		PosLog.getLog(getClass()).info("aaaaaaaaaaaaaaa");
		
		try{
			Integer.parseInt("aaa");
		}catch(Exception er)
		{
			PosLog.getLog(getClass()).fatal(er);
			PosLog.getLog(getClass()).debug(er);
			PosLog.getLog(getClass()).info(er);
			PosLog.getLog(getClass()).error(er);
			PosLog.getLog(getClass()).warn(er);
		}
	}

}
