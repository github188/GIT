package custom.localize.Zmsy;

import custom.localize.Zmjc.Zmjc_CmdDef;

public class Zmsy_CmdDef extends Zmjc_CmdDef
{

    //public static final int ZMSY_GETGWK_CARDNO = 825;	//ZMSY 根据轨道号获取购物卡信息 与826合并
    public static final int ZMSY_GETGWKINFO = 826;	//ZMSY 根据卡号/证件等获取购物卡信息
    public static final int ZMSY_SENDGWK = 827;	//ZMSY 保存购物卡信息到POSDB
    public static final int ZMSY_SENDHGPT = 828;	//ZMSY 保存海关平台信息到POSDB
    public static final int ZMSY_FINDFLIGHT = 829;	//ZMSY 实时查找航班
    //public static final int ZMSY_FINDTHPLACE = 830;	//ZMSY 获取提货地点
    //public static final int ZMSY_FINDZJTYPE = 831;	//ZMSY 获取证件类型
    
    public static final int ZMSY_CHECKTHDH = 832;	//ZMSY 检查提货单号合法性
    public static final int ZMSY_SENDTHDH = 833;	//ZMSY 上传提货单号 
    
    public static final int ZMSY_CALUNUM = 834;	//ZMSY 检查非超额商品的限量JAVA_CALUNUM
    //public static final int ZMSY_CALUNUMDEL = 835;	//ZMSY 联网取消非超额商品的限量JAVA_CALUNUMDEL 此函数不再调用了for yans
    public static final int ZMSY_FINDTAX = 836;	//ZMSY 获取补税金额JAVA_FINDTAX_cx(SP_FINDTAX)
    
    //说明:=========825,837,838,839,840,860~870为ZMJC预留=========
    
    public static final int ZMSY_GETRPT_PPINFO = 841;	//ZMSY 获取品牌信息 for 报表 JAVA_GETSYKC.GETPP
    public static final int ZMSY_GETRPT_GZINFO = 842;	//ZMSY 获取柜组信息 for 报表 JAVA_GETSYKC.GETGZ
    public static final int ZMSY_GETRPT_KC = 843;	//ZMSY 单品库存查询 for 报表 JAVA_GETSYKC.GETGOODSLIST
    public static final int ZMSY_GETRPT_KCLIST = 844;	//ZMSY 商品库存列表 for 报表 JAVA_GETSYKC.GETKCLIST
    
    public static final int ZMSY_FIND_PRINTSEQ = 845;	//ZMSY 获取打印序号 sp_getseq
    public static final int ZMSY_GETGWKINFO_TH = 846;	// ZMSY 获取退货小票的购物卡信息(java_FINDGWK_TH) WANGYONG ADD BY 2013.8.21
    public static final int ZMSY_GETRPT_YYY = 847;		// ZMSY 获取营业员统计信息
    
    public static final int ZMSY_FINDTAX_ALL = 848;	//ZMSY 获取暂缴税金 add by 2014.01.11
    
}
