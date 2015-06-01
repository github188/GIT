package custom.localize.Sxtx;


public class Sxtx_CustomGlobalInfo {
	
	private static Sxtx_CustomGlobalInfo defaultObj = null;
	
	 public static Sxtx_CustomGlobalInfo getDefault()
	    {
	        if (defaultObj == null)
	        {
	        	defaultObj = new Sxtx_CustomGlobalInfo();
	        }

	        return defaultObj;
	    }
	    
		public class SysPara
		{
			 public char isprintflk;//SC 是否打印福利卡文本文件
		}
		
		public SysPara sysPara = new SysPara();
}
