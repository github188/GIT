package custom.localize.Bgtx;

public class Bgtx_CustomGlobalInfo
{
	private static Bgtx_CustomGlobalInfo defaultObj = null;
	
	 public static Bgtx_CustomGlobalInfo getDefault()
	    {
	        if (defaultObj == null)
	        {
	        	defaultObj = new Bgtx_CustomGlobalInfo();
	        }

	        return defaultObj;
	    }
	    
		public class SysPara
		{
			 public char isprintflk;//SC 是否打印福利卡文本文件
		}
		
		public SysPara sysPara = new SysPara();
}
