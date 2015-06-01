package com.efuture.javaPos.Communication;

public class CmdDosDef
{

	public static final int GETSERVERTIME = 1;			//握手通讯 1
	public static final int FINDCUSTOMER = 10; 			//查找会员信息 10
    public static final int FINDCRMPOP = 75; 				//查找CRM优惠信息 11
    public static final int GETSELLREALFQ = 76; 			//获得小票实时返券 58
    public static final int GETCRMPARA = 77; 				//查找CRM私有参数 13
    public static final int FINDFJK = 79;  				//查询返券卡信息 49
    public static final int SENDFJK = 80;  				//券交易送网（电子券查询/扣款） 49
    public static final int GETREFUNDMONEY = 83;			//获得退货小票扣回金额 24
    public static final int GETREFUNDMONEY_EX = 92;			//获取退换、货的付款与商品信息(同时检查是否能退）,原编号为85，现改为92 WANGYONG BY 2014.9.24
    public static final int CHECKSALETH = 86;			//检查小票是否能退货
    public static final int SENDCRMSELL = 52; 			//上传CRM销售小票信息 45
    
    public static final int SENDMZK = 60; 				//重百/重百新世纪，上传面值卡消费数据 43
    
    public static final int CANCEL_TRANSID = 90; 				//取消退换货
	
	
}
