package custom.localize.Yzlj;

public class Yzlj_CustomGlobalInfo {
private static Yzlj_CustomGlobalInfo defaultObj = null;
	
    public static Yzlj_CustomGlobalInfo getDefault()
    {
        if (defaultObj == null)
        {
        	defaultObj = new Yzlj_CustomGlobalInfo();
        }

        return defaultObj;
    }
    
	class SysPara
	{
		char isrebate;			/* 是否允许单品打折				*/
		char dpsswr;			/* 产生折扣后是否按单品四舍五入	*/
		char cardflag;			/* 卡系统门店代码前缀				*/
		String fpje;	 //  FP 除开发票金额的付款方式
		char isth;  //TH 是否打印退货小票
		char ishc;  //HC 是否打印红冲小票
		String ggkUrl;	 //  GI 刮刮卡的IP地址
	}
	
	public SysPara sysPara = new SysPara();
}
