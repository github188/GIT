package custom.localize.Bjys;

public class Bjys_CustomGlobalInfo 
{
	private static Bjys_CustomGlobalInfo  defaultObj = null;
		
    public static Bjys_CustomGlobalInfo  getDefault()
    {
        if (defaultObj == null)
        {
        	defaultObj = new Bjys_CustomGlobalInfo ();
        }

        return defaultObj;
    }
    
	class SysPara
	{
		char isinputcode = 'Y';			/* 是否直接输入商品编码,不输入柜组	,Y1			*/
	}
	
	public SysPara sysPara = new SysPara();
}
