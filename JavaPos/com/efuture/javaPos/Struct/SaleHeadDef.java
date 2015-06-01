package com.efuture.javaPos.Struct;

import java.io.Serializable;

// 交易小票头定义
public class SaleHeadDef implements Cloneable,Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static String[] ref={"syjh","fphm","djlb","mkt","bc","rqsj","syyh","hykh","hytype","jfkh","thsq","ghsq","hysq"
			,"sqkh","sqktype","sqkzkfd","ysje","sjfk","zl","sswr_sysy","fk_sysy","hjzje","hjzsl","hjzke","hyzke"
			,"yhzke","lszke","netbz","printbz","hcbz","hhflag","buyerinfo","jdfhdd","salefphm","printnum","bcjf","ljjf"
			,"memo","str1","str2","str3","str4","str5","str6","str7","str8","str9","str10","num1","num2","num3","num4","num5","num6","num7","num8","num9","num10","hykname","ysyjh","yfphm","hymaxdate","billno"};
	
	
	
	public String syjh;				// 收银机号,主键
	public long fphm;				// 小票号,主键
	public String djlb;				// 小票类别
	public String mkt;				// 门店号
	public char bc;				// 班次代码
	public String rqsj;				// 交易时间，发送时在数据库中还记录发送时间，YYYY/MM/DD 00:00:00	
	public String syyh;				// 收银员号，发送时在数据库中转换为帐号		
	public String hykh;				// 会员卡号
	public String hytype;			// 会员卡类型
	public String jfkh;				// 积分卡号
	public String thsq;				// 退货授权卡号				
	public String ghsq;				// 员工授权卡号				
	public String hysq;				// 顾客授权卡号				
	public String sqkh;				// 总折扣授权卡号	
	public char sqktype;			// 总折扣授权卡类别,'1'-员工卡，'2'-顾客卡		
	public double sqkzkfd;			// 总折扣授权卡的折扣分担
	public double ysje;			// 应收金额
	public double sjfk;			// 实际付款					
	public double zl;				// 找零						
	public double sswr_sysy;		// 四舍五入收银损溢					
	public double fk_sysy;			// 付款收银损溢
	public double hjzje;			// 合计总金额
	public int hjzsl;				// 合计商品数
	public double hjzke;			// 合计折扣额
	public double hyzke;			// 会员折扣额(来自会员优惠)	
	public double yhzke;			// 优惠折扣额(来自营销优惠)	
	public double lszke;			// 零时折扣额(来自手工打折)
	public char netbz;				// 送网标志					
	public char printbz;			// 打印标志					
	public char hcbz;				// 是否已红冲
	public char hhflag;			// 是否为换货小票
	public String buyerinfo;		// 顾客信息
									// BJYS:旅游团号
									// CCZZ:是否打印办VIP卡联
	public String jdfhdd;			// 家电发货地点
	public String salefphm;			// 发票号码,由收银员手工输入
	public int printnum;			// 重打次数
	public double bcjf;			// 本次积分
	public double ljjf;			// 累计积分
	public String memo;				// 备用
									// BCRM指定小票退货:单号
									// BCSF标记是否已经向广众接口发送商品
	public String str1;				// 备用
									// HZJB:收银员名称
									// BJSP:是否PDA或网站销售标志
									// android返回SEQNO
	public String str2;				// 备用
									// BCSF标记是否已经向知而行发送商品
									// ZMJC 暂存单打印信息（回程航班编码|回程航班号|回程日期|回程时间|联系方式）
	public String str3;				// 备用
									// BXMX:取货必须付全款，并且已取货
									// CCZZ:打印进场排名;消费排名
									// BHCM:saleok返回memo打印
									// ZMJC:常旅卡号
	public String str4;				// 备用
									// BXMX:促销信息
	public String str5;				// 备用 支付宝商户订单号
	
	public String str6;				//ZMSY:购物卡号（证件类型+证件号）HTSC定金是否打印换购凭证
	public String str7;				//ZMSY：税金承担(1为中免承担,2为顾客承担)
	public String str8;				//ZMSY:charAt(0)是否为免税机 charAt(1)是否即购即提
	public String str9;				//ZMSY（此字段不上传到POSDB）:0姓 名|1国 籍(格式：中国)|2证件类别|3证件号码|4顾客类别（格式：离岛）|5离境日期（格式：2013-08-15 10:47:27）|6提货地点（格式：三亚国内出发厅）|7离境航班|8出生日期（格式：1957-2-27）|9手机号码|10提货地点简称
//									CBBH:记录一张小票满赠规则
	public String str10;			//CBBH:记录一张小票返劵规则
									// 积分返回memo提示
									// ZMJC （满赠）礼品打印信息
	public double num1;			// 付款合计中,不记入付款的溢余合计	
									// BJYS:积分折扣
	public double num2;			// 备用
								// CCZZ:1-是预售小票
								//ZMSY:税金金额
	public double num3;			// 备用
								// CCZZ:零钞转存的金额
								//ZMSY:行邮税店承担金额
	public double num4;			// 备用
									// CCZZ 倍享积分
	public double num5;			// 备用
								//ZMJC:当天小票流水
	
	public double num6;		    //ZMSY:行邮税厂家承担金额
	public double num7;			//ZMSY:(打印用)税金序号,即税单号
	public double num8;			//ZMSY:(打印用)航班序号,即分货号
	public double num9;			//ZMSY:暂缴税金合计 BY 2014.01.10
	public double num10;		//退换货事务号
	
	public String hykname;			// 会员卡名称
	public String hymaxdate;        // 会员卡有效期
	// 原收银机号和原小票号，此信息不上传了不记录本地库
	public String ysyjh;			// 原收银机号
	public String yfphm;			// 原小票号
	public String billno;			// 原序号
	
	public String cczz_zktmp; 		// CCZZ客户化
	public String cczz_custID;		// CCZZ客户化
	public Object zmsy_gwk;			// ZMSY客户化
	public boolean ismemo = true;			//CBBH:判断一张单是否有不同返劵规则
	//public String tjj ="N";			//石家庄定制需求 特价键 默认N	 执行促销
									// 				   Y 不执行促销
	//public String out_trade_no;     //支付宝商户订单号
	
	public Object clone()
	{
		try 
		{
			return super.clone();
		} catch (CloneNotSupportedException e) 
		{
			e.printStackTrace();
			return this;
		}
	}
}
