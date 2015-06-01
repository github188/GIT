package custom.localize.Sdyz;


public class Sdyz_CustomGlobalInfo
{
	private static Sdyz_CustomGlobalInfo defaultObj = null;
	
    public static Sdyz_CustomGlobalInfo getDefault()
    {
        if (defaultObj == null)
        {
        	defaultObj = new Sdyz_CustomGlobalInfo();
        }

        return defaultObj;
    }
    
	class SysPara
	{
		char isrebate;			/* 是否允许单品打折				*/
		char dpsswr;			/* 产生折扣后是否按单品四舍五入	*/
		char cardflag;			/* 卡系统门店代码前缀				*/
	}
	
	public SysPara sysPara = new SysPara();
}
