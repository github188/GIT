package com.efuture.javaPos.Communication;


//这个类是用来定义执行SQL的命令类
public class CmdDef
{
	public static final int CHECKREGCODE = 0;			//检查款机注册码
    public static final int GETSERVERTIME = 1; 			//获取服务器时间
    public static final int FINDSYJ = 2; 					//获取收银机定义
    public static final int GETSYSPARA = 3; 				//获取系统参数设置
    public static final int GETNEWS = 4; 					//获取网上通知
    public static final int DELNEWS = 5; 					//删除网上通知
    public static final int GETTASK = 6; 					//获取任务
    public static final int DELTASK = 7; 					//删除任务
    public static final int FINDGOODS = 8;	 			//查找商品信息
    public static final int FINDOPERUSER = 9; 			//查找人事信息
   
    public static final int FINDCUSTOMER = 10; 			//查找会员信息 
    public static final int FINDCRMPOP = 11; 				//查找CRM优惠信息
    public static final int GETMSINFO = 12; 				//查找CRM满送信息
    public static final int GETCRMPARA = 13; 				//查找CRM私有参数
    public static final int CHANGEPASS = 14; 				//修改员工密码
    public static final int GETINVOICE = 15; 				//获取已送网小票信息
    public static final int GETSGOODSUNITSLIST = 16; 		//获取商品多单位列表
    public static final int FINDGOODSAMOUNT = 17; 		//查找商品批量信息
    public static final int GETSYJPAYMODE = 18; 			//获得收银机付款模板
    public static final int GETSYJGRANGE = 19; 			//获得收银机收银范围
    public static final int GETCHILDGOODSLIST = 20; 		//获得子商品列表
    public static final int CHECKLOGIN = 21; 				//检查收银员是否已在其他收银机登录
    public static final int FINDRULEPOPNEW = 22; 			//新模式的满减规则促销查询
    public static final int GETCRMVIPZK = 23; 			//获取按卡类、商品设置的VIP折扣率
    public static final int GETREFUNDMONEY = 24;			//获得退货小票扣回金额
    public static final int GETPOSTIME = 25; 				//获得收银班次定义
    public static final int GETCUSTOMERTYPE = 26; 		//获得会员卡类别定义
    public static final int GETOPERROLE = 27; 			//获得角色定义
    public static final int GETWITHDRAWMODE = 28; 		//获得缴款模板定义
    public static final int GETDZCMODE = 29; 				//获得电子秤条码格式定义
    public static final int GETMANAGEFRAME = 30; 			//获得管理架构信息
    public static final int GETBUYERINFO = 31; 			//获得消费顾客信息
    public static final int GETCALLINFO = 32; 			//获得呼叫信息定义 
    public static final int GETGOODSLIST = 33; 			//获得商品信息列表
    public static final int GETGOODSAMOUNTLIST = 34; 		//获得商品批量信息列表
    public static final int GETBACKGOODSINFO = 35; 		//查找退货原商品信息
    public static final int GETCUSTSELLJF = 36; 			//获得本次积分和累计积分
    public static final int SENDSYJSTATUS = 37; 			//上传收银机当前收银状态
    public static final int SENDSELL = 38; 				//上传销售小票信息
    public static final int SENDPAYIN = 39; 				//上传交款单信息
    public static final int GETGOODSBARCODELIST = 40; 	//获取条码对应商品列表(Bstd)
    public static final int SENDWORK = 41; 				//上传工作日志
    public static final int SENDCALLINFO = 42; 			//上传呼叫信息
    public static final int SENDMZK = 43; 				//上传面值卡消费数据
    public static final int FINDMZKINFO = 44; 			//查询面值卡余额
    public static final int SENDCRMSELL = 45; 			//上传CRM销售小票信息
    public static final int GETGOODSPOPLIST = 46; 		//获得商品优惠信息列表
    public static final int GETMENUINFO = 47; 			//获得菜单信息定义
    public static final int SENDBANKCARD =48; 			//上传金卡日志信息
    public static final int SENDFJK = 49;  				//上传返券卡信息
    public static final int FINDRULEPOP = 50; 			//查找规则促销
    public static final int FINDRULEPOPGIFT = 51; 		//获得编码商品赠品促销
    public static final int FINDFJKINFO = 52;	 			//查找反券卡信息
    public static final int GETBACKSALEHEAD = 53;			//获得后台退货小票头
    public static final int GETBACKSALEDETAIL = 54;  		//获得后台退货小票明细
    public static final int GETBACKPAYSALEDETAIL = 55;	//获得后台退货小票付款明细
    public static final int CHECKINVOHC = 56; 			//检查小票是否已经被红冲
    public static final int FINDGIFTPOPNEW = 57; 			//新模式的满赠规则促销查询
    public static final int GETSELLREALFQ = 58; 			//获得小票实时返券
    public static final int PRESENDCRMSELL = 59; 			//预上传CRM销售小票信息
    public static final int GETPRESALEHEADINFO = 60; 		//为预售提货，获取预售小票头
    public static final int GETPRESALEDETAILINFO = 61; 	//为预售提货，获取预售小票明细
    public static final int GETPRESALEPAYINFO = 62; 		//为预售提货，获取预售小票付款明细
    public static final int PREGETMSINFO = 63; 			//预算小票返券
    public static final int GETCOUPON = 64;				//获取买券信息 
    public static final int FINDGOODSPAYRULE = 65;		//获得付款方式收款规则
    public static final int FINDVIPMAXSL = 66;			//获得VIP商品已销售数量
    public static final int GETCHECKGOODS = 67;			//获得盘点单商品列表
    public static final int SENDCHECKGOODS = 68;			//上传商品盘点数据
    public static final int FINDCREDITZK = 69;			//查询银联追送折扣率
    public static final int FINDCREDITLIST = 70;		//获取银联卡磁道解析规则类表
    public static final int SENDSALEGD = 71;            //上传挂单信息
    public static final int GETSALEGDHEAD = 72;				//获得挂单头信息
    public static final int GETSALEGDDETAIL = 73;				//获得挂单明细信息
    public static final int DELSALEGDINFO = 74;				//获得挂单明细信息
    public static final int SENDSALEAPPEND = 75;			// 上传附加信息数据
    public static final int FINDCHARGEFEE = 76;			// 查询结算列表
    public static final int SENDHYKJF		= 77;			// 上传会员卡积分进行同步
    public static final int GETONECOMMONVALUES	= 78;			//(通用方法)从网上取一个值
    public static final int GETMEMOINFO = 79; 			//获得备用信息定义 
    public static final int FINDWHTBLACKLIST = 80;		//查找武汉通黑名单
    public static final int COMMONCOMMAND = 81;         //通用命令
    public static final int GETOLDQPAYDET = 82;			//退货获取原券益余金额
    public static final int SENDDZQ = 83;			//获得电子券
    public static final int FINDDZQINFO = 84;			//获得电子券信息
    public static final int ZSFQINFO = 85;			//保存赠券到返券卡当中
    public static final int GETOLDCHECKINFO = 86;			//查询原盘点单
    public static final int GETPAYMENTLIMIT = 87; 			//获得付款上限信息
    public static final int GETCUSTXF = 88;			//获取会员信息
    public static final int GETGROUPBUYINFO = 89;			//获取团购信息 

    public static final int GETSHOPPRESALE = 90;			//超市获取预售单明细
    public static final int GETGOODSJFRULE = 91;			//获取商品积分规则
    public static final int SENDHYKNEW = 92;			//查询积分NEW
    public static final int FINDJFINFO = 93;			//查询积分信息
    public static final int FINDLIMITVIPZK = 100;			//SFKS,查找限量折上折定义,上海FOXTOWN客户化
    public static final int FINDFWQRANGE = 101;			//SFKS,查找活动券的收券范围,上海FOXTOWN客户化
    public static final int GETPOSJFRULE = 102;			//BJYS,获得pos积分规则,北京燕莎客户化
    public static final int GETBACKSELLLIMIT = 103;  		//WJJY,退货限额控制
    //public static final int GETBUCKLEINFO = 104;			//HZJB,获得退货扣回信息
    public static final int GETACCEPTFJKRULE = 105;		//BCRM,获得收返券卡规则
    public static final int SENDHYK = 106;				//BCRM,发送会员卡消费交易
    public static final int GETGOODSEXCHANGE	= 107;		//BCRM,查找商品是否存在换购规则
    public static final int GETKEYVALUE = 108;			//HZJB,查询KEY值
    public static final int SENDDZQXFMX = 109;			//GZBH,电子券交易前发送交易商品明细
    public static final int GETDZQCANUSE = 110;			//GZBH,电子券交易前得到本交易可用券
    public static final int GETDZKINFO = 111;		    	//GZBH,打印电子卡交易信息
    public static final int REPRIDZKINFO = 113;			//GZBH,补打电子卡交易信息
    public static final int FINDGOODSCMPOP = 114;			//BSTD,查找商品促销单
    public static final int FINDPOPGROUP   = 115;			//BSTD,查找促销分组
    public static final int FINDPOPGIFT	  = 116;		//BSTD,查找促销赠品
   
    public static final int SENDJKLCARD	  = 117;		//JLBH,发送储值卡交易确认信息
    
    public static final int SENDCHKINFO = 118;		//ZSPJ,发送盘点柜组及盘点日期到后台进行验证
    public static final int SENDJFTOPOS = 119;			//ZSPJ,发送CRM积分到POS库进行同步
    
    public static final int GETGOODSCATECOUNT = 120; //XMX,查找商品类别最大记录数
    public static final int GETGOODSCATEPAGES = 121 ;//XMX分页查找商品记录
    public static final int SENDCOUPONLIST = 122;	//XMX发送小票活动券
    
    public static final int FINDREBATECARD = 120;  //广缘超市查找员工卡/惠民卡
    public static final int SENDTOTALAMOUNT = 121; //同步累积金额
    public static final int GETGWLINFO = 122; //同步累积金额
    
    public static final int FINDGOODEWMSCMPOP = 123; //吴裕泰查找二维码促销
    
    public static final int GETDAILYFEEBILL= 123;	//北京超市发获取卖场日常缴费单
    public static final int SENDDAILYFEEBILL = 124;//北京超市发发送卖专卖日常缴费单
    public static final int GETSTAMPCOUNT = 125;//北京超市发获取印花数量
    public static final int GETSTAMPGOODS = 126;//北京超市发获取印花数量
    
    public static final int GETGOODSSTOCK = 122;    //CJMX,查库存
    public static final int GETCUSTSALEINFO = 123;    //CJMX,查询会员消费信息
    
    public static final int java_FINDMOBILE2CARD = 125; //南宁梦之岛，通过手机号查询会员卡
    
    public static final int GETBACKSALEISUSEDCOUPON = 126; //汇德隆退货查找收券
    public static final int HHDL_GETGOODSSHELFLIFE  = 127;//汇德隆获取商品保质期
    		
    public static final int SENDFAXINFO = 127;            //WQBH万千百货 大庆店  发送税控信息
    
    public static final int JAVA_SEND_YGDZQ = 128;            //BSTD，面值卡方式超市用百货卷
    
    public static final int CHECKCOUPONSALELIMIT = 129;            // 查找买券金额和次数的上限

    public static final int GETSALEGOODSAPPORTION = 130;        //TYGC,获取小票分摊
    
    public static final int GETSALEJFLIST = 131;        // CCZZ,获取积分列表
    public static final int GETJF = 132;        // 获取买积分信息
    public static final int sendMemberInfo = 133;                //WQBH 发送大会员信息
    public static final int getDHYRefundMoney = 134;             //WQBH 获取大会员扣回信息
    public static final int sendEvaluation = 135;                //发送顾客评价信息
    public static final int findmzkscope = 136;                //发送顾客评价信息
    public static final int sendNewCustomer = 137;              //BZHX,发送手机号办理临时会员
    
    public static final int FINDCREDITZK_BANKZS = 138;			//NBBH,查询银联追送折扣率
    
    public static final int FINDCUSTOMER_SMZD = 810; 			//SNZD 查找会员信息 用于换卡
    public static final int CHANGECUSTOMER_SMZD = 811; 			//SNZD 换卡
    public static final int GETSYSPARAFORZSBH = 812 ;		//ZSBH 获取CRM私有参数
    public static final int SENDFLQFORZSBH = 813;				//ZSBH 发送返利券消费信息
    public static final int BatchRebate = 814;				//BJCX,价随量变
    public static final int SENDBILLTOREFUND = 815;			//ZSBH发送扣回小票
    public static final int SENDGONGMAOMZKLOG = 816;			//ZSBH发送工贸卡消费日志
    public static final int GETPFINFO = 817;			// JCGJ获取印花信息
    
    public static final int GETSMPOPBILLNO = 818;			// 获取超市规则促销单号
    public static final int GETSMPOPRULE = 819;			// 获取超市促销规则明细
    public static final int FINDSTAMP = 820;			// 查找印花促销规则
    
    
    public static final int XMX_SENDPRESELL = 900;				//上传预收定金小票
    public static final int XMX_FINDGOODCOUPON = 901;			//查找商品提货券
    public static final int XMX_SELLCOUPON = 902;				//发送卖券
    public static final int XMX_GETPREBACKSALEHEAD = 903;		//获得定金退货小票头
    public static final int XMX_GETPREBACKSALEDETAIL = 904;  	//获得定金退货小票明细
    public static final int XMX_GETPREBACKPAYSALEDETAIL = 905;	//获得定金小票付款明细
    public static final int XMX_UPDATEPRESALEHEAD = 906;		//更新预收定金标志
    public static final int XMX_CHECKSHOPID = 907;				//核验门店号
    public static final int XMX_GETPOPNEWS = 908;				//得到门店促销信息
    
    public static final int LYDF_SENDTAX = 910;				//临衣东方发送税控发票信息
    
    public static final int WYT_FINDGIFT =900;			//获取礼品
    public static final int WYT_FINDDEDUCT =901;			//获取抵扣规则
    public static final int WYT_SENDGIFTBILL =902;			//发送礼品单据
    public static final int WYT_FINDEWMCMPOP = 903;	//查找二维码促销
    public static final int WYT_SENDEWMLOG = 904;  //发送二维码日志
    public static final int WYT_FINDEWMCMPOPGROUP = 905;	//查找二维码促销分组
    public static final int WYT_FINDCMPOPGIFT	  = 906;		//BSTD,查找促销赠品
    
    public static final int XHD_GETOLDCARD = 701;				// 新华都换卡（找老卡）
    public static final int XHD_EXCHANGECARD = 702;				// 新华都换卡（换卡）
    

    // -------------石家庄定制需求  begin-----------------------------
    public static final int HTSC_VALIDSALENO = 1000;				// 验证销售单号（怀特商城）
    public static final int HTSC_VALIDSALENO1 = 1001;				
    public static final int HTSC_VALIDSALENO2 = 1002;				
    public static final int HTSC_VALIDSALENO3 = 1003;				
    public static final int HTSC_GETSALENO = 1004;	
    public static final int HTSC_UPCUSTINFO = 1005;	
    public static final int HTSC_SELECTCUSTINFO = 1006;	
    
    // -------------石家庄定制需求   end-------------------------------
    
   
    public static final int DXZY_GETGOODS = 868;	 			//DXZY 国际码查找商品信息
 
    public static final int JAVA_INVOICE_POSTWHY = 1000;		// 德基小票重打原因获取
    public static final int JAVA_INVOICE_GETWHY = 1001;			// 德基小票重打原因上传

}

