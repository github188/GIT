package com.efuture.javaPos.Global;

public class StatusType
{
	// 收银状态
    public final static char STATUS_START = '1';						//开机
    public final static char STATUS_LOGIN = '2';						//登录
    public final static char STATUS_DISCONNET = '3';					//脱网
    public final static char STATUS_LEAVE = '4';						//离开
    public final static char STATUS_SHUTDOWN = '5';					//关机
    public final static char STATUS_SALEING = '6';					//销售
    public final static char STATUS_CHECK = '7';						//盘点
    public final static char STATUS_MORECASH = '8';					//现金过量
    public final static char STATUS_PRESALE = '9';					//预销售
    
    // 任务类型
    public final static char TASK_SENDINVOICE = '1';					//发送销售小票		
    public final static char TASK_SENDPAYJK = '2';					//发送缴款信息
    public final static char TASK_SENDWORKLOG = '3';					//发送工作日志
    public final static char TASK_SENDERRORLOG = '4';				//发送异常日志
    public final static char TASK_SENDINVOICEAGAIN = '5';			//重发销售小票
    public final static char TASK_SENDPAYJKAGAIN = '6';				//重发缴款信息
    public final static char TASK_SENDWORKLOGAGAIN = '7';			//重发工作日志
    public final static char TASK_SENDBANKLOGAGAIN = '8';			//重发金卡工程日志
    public final static char TASK_DELETEDATA = '9';					//删除本地数据
    public final static char TASK_POSINIT = '0';						//POS机初始化
    public final static char TASK_ORDERSHUTDOWN = 'A';				//命令关机
    public final static char TASK_ORDERDISCONNECT = 'B';				//命令脱网
    public final static char TASK_EXECPROC = 'C';					//执行程序
    public final static char TASK_SENDMZKLOG = 'D';					//发送面值卡日志
    public final static char TASK_ORDERCONNECT = 'E';				//命令联网
    public final static char TASK_DOWNLOADAGAIN = 'F';				//重新下载基本库
    public final static char TASK_SENDBANKLOG = 'G';					//发送金卡工程日志
    public final static char TASK_SETINVNO = 'H';					//重新设定小票号
    public final static char TASK_SENDINVTOEXTEND = 'I';				//发送销售小票到CRM服务器
    public final static char TASK_DELETELOCALTASK = 'J';				//删除本地历史任务表
    public final static char TASK_SENDWEBSERVICE = 'K';				//发送WebServcie数据
    public final static char TASK_SENDSALEAPPEND = 'L';             //发送小票附加数据
    public final static char TASK_SENDHYKJF 		= 'M';             //发送会员卡积分同步
    public final static char TASK_SENDSALETOJSTORE = 'N';             //发送小票到JSTORE
    public final static char TASK_SENDTOTALAMOUNT = 'O';				//广缘重发卡消费总金额		 
    
    public static boolean isMustTask(char type)
    {
    	if (type < '9' || type == 'I' || type == 'K' || type == 'M' || type == 'L' || type == 'N') return true;
    	else return false;
    }
    
    public static String taskTypeExchange(char type)
    {
    	switch(type)
    	{
    		case TASK_SENDINVOICE:
    			return Language.apply("发送销售小票任务");
    			
    		case TASK_SENDPAYJK:
    			return Language.apply("发送缴款信息任务");
    			
    		case TASK_SENDWORKLOG:					
    			return Language.apply("发送工作日志任务");
    			
    		case TASK_SENDERRORLOG:				
    			return Language.apply("发送异常日志任务");
    			
    		case TASK_SENDINVOICEAGAIN:			
    			return Language.apply("重发销售小票任务");
    			
    		case TASK_SENDPAYJKAGAIN:	
    			return Language.apply("重发缴款信息任务");
    			
    		case TASK_SENDWORKLOGAGAIN:			
    			return Language.apply("重发工作日志任务");
    			
    		case TASK_SENDBANKLOGAGAIN:			
    			return Language.apply("重发金卡工程日志任务");
    			
    		case TASK_DELETEDATA:				
    			return Language.apply("删除本地数据任务");
    			
    		case TASK_POSINIT:				
    			return Language.apply("POS机初始化");
    			
    		case TASK_ORDERSHUTDOWN:				
    			return Language.apply("命令关机任务");
    			
    		case TASK_ORDERDISCONNECT:				
    			return Language.apply("命令脱网任务");
    			
    		case TASK_EXECPROC:						
    			return Language.apply("上传文件任务");
    			
    		case TASK_SENDMZKLOG:				
    			return Language.apply("发送面值卡日志任务");
    			
    		case TASK_ORDERCONNECT:				
    			return Language.apply("命令联网任务");
    			
    		case TASK_DOWNLOADAGAIN:				
    			return Language.apply("重新下载基本库任务");
    			
    		case TASK_SENDBANKLOG:					
    			return Language.apply("发送金卡工程日志任务");
    			
    		case TASK_SETINVNO:						
    			return Language.apply("重新设定小票号任务");
    			
    		case TASK_SENDINVTOEXTEND:
    			return Language.apply("发送小票到CRM任务");
    			
    		case TASK_DELETELOCALTASK:
    			return Language.apply("删除本地历史任务");
    			
    		case TASK_SENDWEBSERVICE:
    			return Language.apply("发送WebServcie数据任务");
    		case TASK_SENDSALEAPPEND:
    			return Language.apply("发送小票附加数据");
    		case TASK_SENDHYKJF:
    			return Language.apply("发送会员卡积分同步");
    		case TASK_SENDSALETOJSTORE:
    			return Language.apply("发送小票到JSTORE任务");
    		default:
    			return Language.apply("未知任务");
    	}
    }
    
    // 工作日志代码,菜单操作日志代码为菜单功能码
    public final static String WORK_BOOT = "701"; 						//开机
    public final static String WORK_LOGIN = "702"; 						//登录成功
    //public final static String WORK_DISCONNECT = "703"; 				//断开网络连接
    //public final static String WORK_CONNECT = "704"; 					//连接网络
    //public final static String WORK_BROKEN = "705"; 					//记录断电保护
    //public final static String WORK_PASSERROR = "706"; 				//密码错误3次
    public final static String WORK_FULLYSPACE = "707"; 				//磁盘空间不够
    public final static String WORK_DELETESALE = "708"; 				//删除单品
    public final static String WORK_CLEARSALE = "709"; 					//取消整单
    public final static String WORK_ORDERRESETTICKETID = "710"; 		//命令重设小票号
    public final static String WORK_OPENCASHDRAWER = "711"; 			//无销售开钱箱
    public final static String WORK_SHUTDOWN = "712"; 					//关机
    public final static String WORK_ORDERSHUTDOWN = "713"; 				//命令关机
    public final static String WORK_ORDERDISCONNECT = "714"; 			//命令脱网
    public final static String WORK_ORDERDELETETICKET = "715"; 			//命令删除小票
    public final static String WORK_LEAVER = "716"; 					//收银员离开
    public final static String WORK_COMEBACK = "717"; 					//收银员返回
    public final static String WORK_ORDERSENDDATA = "718"; 				//命令重发数据
    //public final static String WORK_AUTHRETURN = "719"; 				//授权退货
    //public final static String WORK_AUTHCHEQPAY = "720"; 				//授权支票付款
    //public final static String WORK_AUTHREBATE = "721"; 				//授权折扣
    //public final static String WORK_PRINTDISCONNECT = "722"; 			//打印机未连接
    //public final static String WORK_AUTHDESALE = "723"; 				//授权红冲
    public final static String WORK_RELOGIN = "724"; 					//收银员重登陆
    public final static String WORK_SENDERROR = "725"; 					//发送数据产生错误
    public final static String WORK_CANCELBANK = "726"; 				//通过其他途径撤销银联交易
    public final static String WORK_POSINIT= "727"; 					//收银机初始化操作
    
    public final static String WORK_BASEDOWN= "728"; 					//脱网数据包时候下载情况
    public final static String WORK_SALESEND= "729"; 					//小票上传情况
    public final static String WORK_POSVERSION= "730"; 					//版本信息
    public final static String WORK_WRITEBRODATA= "750"; 					//写入断电保护文件
    public final static String WORK_READBRODATA= "751"; 					//读取断电保护文件
    public final static String WORK_CLEARBRODATA= "752"; 					//清除断电保护文件

    
    //菜单定义
    public final static int RT_SALE = 0001;								//收银
    public final static int MN_LSSALE = 101;							//销售开票
    public final static int MN_LXSALE = 102;							//收银练习
    public final static int MN_SALEHC = 103;							//收银红冲
    public final static int MN_PFSALE = 105;							//批发开票
    public final static int MN_DJSALE = 106;							//预收定金
    public final static int MN_CHECK = 108;								//商品盘点
    public final static int MN_YSSALE = 110;							//预售开票
    public final static int MN_SHSALE = 111;							//预售提货
    public final static int MN_BACKSALE = 112;							//后台退货
    public final static int MN_JDFHSALE = 113;							//家电销售(家电看板销售，每个商品检查发货地点库存)
    public final static int MN_MQSALE = 114;							//买券交易
    public final static int MN_JDXXFK = 115;							//家电下乡返款
    public final static int MN_JDXXTH = 116;							//家电下乡退返款
    public final static int MN_JSSALE = 117;							//结算交易
    public final static int MN_JFSALE = 118;							//缴费交易
    public final static int MN_GROUPBUYSALE = 119;						//团购销售
    public final static int MN_CARDSALE = 120;							//前台售卡
    public final static int MN_STAMPCHANGE = 121;						//前台印花换购
    

    public final static int MN_JJEARNEST = 130;							//家居定金
    public final static int MN_JJFINAL = 131;							//家居补余款
    

    //以下为客户化功能菜单

    
    public final static int MN_BYICCARDRECHARGE =  120;					//爱家IC卡充值菜单 //
    public final static int MN_BYICCARDROLLING =  121;					//爱家IC卡充值轧账
    public final static int MN_DAILYFEE = 122;							//超市发缴款菜单 //龙南百易写授权卡菜单
    public final static int MN_MZKRECHARGE	 = 123;						//储值卡充值
    public final static int MN_MZKCHGPASS	 = 124;						//储值卡修改密码
    public final static int MN_MJFSALE	 = 125;						//买积分交易

    public final static int MN_YSSALE1 = 199;							//预售开票 NMZD客户化
   
    
    public final static int RT_CX = 0002;								//报表查询
    public final static int MN_QTXSTJ = 201;							//收银员销售统计
    public final static int MN_YYYTJ = 202;								//营业员销售统计
    public final static int MN_GZXSTJ	 = 203;							//柜组销售统计
    public final static int MN_XSCX = 205;								//销售小票查询
    public final static int MN_XSLIST	 = 206;							//当日小票列表
    public final static int MN_SCSM	 = 207;								//储值卡收款统计
    public final static int MN_ALIXSLIST = 210;							//阿里小票列表
    


    public final static int RT_INFO = 0003;								//信息查询
    public final static int MN_MZKXX = 301;								//面值卡余额查询
    public final static int MN_FJKXX = 302;								//返券卡信息查询
    public final static int MN_GOODSFIND = 307;							//商品查询
    public final static int MN_NEWS	= 303;								//查询网上通知
    public final static int MN_JFXX	= 304;								//积分信息查询
    public final static int MN_WORK	= 305;								//查询工作日志
    public final static int MN_HYK = 308;								//查询会员卡信息
    public final static int MN_HDQ = 309;								//活动券激活
    public final static int MN_DHY = 310;								//大会员（万达用）
    public final static int MN_BACKPWD =311;                         // 发送短信验证码（万达用）
    public final static int MN_REGIST=312;                           //会员注册（大会员）
    
    public final static int RT_MAINT = 0004;							//系统维护    
    public final static int MN_JSQ = 401;								//计算器
    public final static int MN_KJJDEF = 402;							//快捷键定义
    public final static int MN_LWCZ = 404;								//联网操作
    public final static int MN_TWCZ = 405;								//脱网操作
    public final static int MN_XPHSZ = 407;								//小票号设置
    public final static int MN_CXSZXTSJ = 408;							//重新设置系统时间
    public final static int MN_SCXSSJ = 410;							//删除销售数据
    public final static int MN_ZLBDSJK = 411;							//整理本地数据库
    public final static int MN_JPDY = 412;								//键盘定义
    public final static int MN_POSIPLIST = 413;							//POS服务器IP列表
    
    public final int MN_MODIFYGWKIP = 414;								//修改购物卡IP地址，梦之岛专用
    public final int MN_MODIFYZHKIP = 415;								//修改中行卡IP地址，梦之岛专用
    public final int MN_MODIFYGMCIP = 416;								//修改银联卡IP地址，梦之岛专用
    
    public final static int RT_EXIT = 0005;								//退出 
    public final static int MN_SYYDL = 501;								//收银员登录
    public final static int MN_SYYLK = 502;								//收银员离开
    public final static int MN_MMXG = 503;								//密码修改
    public final static int MN_JKDSL = 505;								//缴款单输入
    public final static int MN_EXITST = 507;							//退出系统
    public final static int MN_FASTRERUN = 508;							//快速重启系统

    public final static int RT_HELP = 0006;								//帮助 
    public final static int MN_HELP = 601;								//帮助
    public final static int MN_GY = 602;								//关于
    
    public final static int RT_XYK = 8;									//信用卡交易
    public final static int MN_XYKXF = 801;								//信用卡消费
    public final static int MN_XYKCX = 802;								//信用卡消费撤销
    public final static int MN_XYKTH = 803;								//信用卡隔日退货
    public final static int MN_XYKQD = 804;								//信用卡交易签到
    public final static int MN_XYKJZ = 805;								//信用卡交易结账
    public final static int MN_XYKYE = 806;								//信用卡余额查询
    public final static int MN_XYKCD = 807;								//信用卡签购单重打    
    public final static int MN_XYKLOG = 808;							//信用卡交易日志查询
    public final static int MN_XYKQT1 = 809;							//信用卡其他交易1
    public final static int MN_XYKQT2 = 810;							//信用卡其他交易2
    public final static int MN_XYKQT3 = 811;							//信用卡其他交易3
    public final static int MN_XYKQT4 = 812;							//信用卡其他交易4
    public final static int MN_XYKQT5 = 813;							//信用卡其他交易5
    public final static int MN_XYKQT6 = 814;							//信用卡其他交易6
    public final static int MN_XYKQT7 = 815;							//信用卡其他交易7
    public final static int MN_XYKQT8 = 816;							//信用卡其他交易8
    public final static int MN_XYKQT9 = 817;							//信用卡其他交易9
    
    public final static int RT_OTHMAINT = 9;							//其他维护
    public final static int MN_MZKSEQNORESET = 901;						//重设面值卡交易流水
    public final static int MN_MZKDELETECZ = 902;						//删除面值卡交易冲正
    public final static int MN_CUSTFUNC = 903;							//执行自定义三方程序，通过修改javaPos.ConfigFile下的custFunc.ini文件进行配置
    public final static int MN_RELOADBASE = 904;						//重新下载BASE库
    public final static int MN_IMPORTSMALLTICKET = 905;					//导入小票流水备份
    public final static int MN_MODIFYINVNO = 906;						//修改冲突的小票号
    public final static int MN_MODIFYJKD = 907;							//修改冲突的缴款单号
    
   
}
