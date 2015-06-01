package com.efuture.javaPos.Struct;

import java.io.Serializable;

// 交易小票商品明细定义f
public class SaleGoodsDef implements Cloneable, Serializable
{
	private static final long serialVersionUID = 1L;

	public static String[] ref = {
									"syjh",
									"fphm",
									"rowno",
									"yyyh",
									"barcode",
									"code",
									"type",
									"gz",
									"catid",
									"ppcode",
									"uid",
									"batch",
									"yhdjbh",
									"name",
									"unit",
									"bzhl",
									"sl",
									"lsj",
									"jg",
									"hjje",
									"hjzk",
									"hyzke",
									"hyzkfd",
									"yhzke",
									"yhzkfd",
									"lszke",
									"lszre",
									"lszzk",
									"lszzr",
									"lszkfd",
									"plzke",
									"plzkfd",
									"zsdjbh",
									"zszke",
									"zszkfd",
									"sqkh",
									"sqktype",
									"sqkzkfd",
									"hyzklje",
									"cjzke",
									"ltzke",
									"qtzke",
									"qtzre",
									"sswr_sysy",
									"fk_sysy",
									"fph",
									"isvipzk",
									"xxtax",
									"flag",
									"yjhxcode",
									"ysyjh",
									"yfphm",
									"yrowno",
									"jfrule",
									"fhdd",
									"hydjbh",
									"inputbarcode",
									"memo",
									"str1",
									"str2",
									"str3",
									"str4",
									"str5",
									"str6",
									"str7",
									"str8",
									"str9",
									"str10",
									"str11",
									"str12",
									"str13",
									"str14",
									"str15",
									"num1",
									"num2",
									"num3",
									"num4",
									"num5",
									"num6",
									"num7",
									"num8",
									"num9",
									"num10",
									"num11",
									"num12",
									"num13",
									"num14",
									"num15",
									"rulezke",
									"rulezkfd",
									"ruledjbh",
									"mjzke",
									"mjzkfd",
									"mjdjbh",
									"spzkfd",
									"ysl" };

	
	
	public String syjh; // 收银机号,主键				
	public long fphm; // 小票号,主键	
	public int rowno; // 行号,主键
	public String yyyh; // 营业员
	public String barcode; // 商品条码					
	public String code; // 商品编码					
	public char type; // 编码类别					
	public String gz; // 商品柜组						
	public String catid; // 商品品类					
	public String ppcode; // 商品品牌						
	public String uid; // 多单位码						
	public String batch; // 批号		
	public String yhdjbh; // 优惠单据编号				
	public String name; // 名称						
	public String unit; // 单位						
	public double bzhl; // 包装含量					
	public double sl; // 销售数量					
	public double lsj; // 零售价					
	public double jg; // 销售价格					
	public double hjje; // 合计金额
	public double hjzk; // 合计折扣,等于各种折扣之和
	public double hyzke; // 会员折扣额(来自会员价优惠)
	public double hyzkfd; // 会员折扣分担
	public double yhzke; // 优惠折扣额(来自分期促销优惠)	
	public double yhzkfd; // 优惠折扣分担				
	public double lszke; // 零时折扣额(来自手工打折)	
	public double lszre; // 零时折让额(来自手工打折)	
	public double lszzk; // 零时总品折扣					
	public double lszzr; // 零时总品折让					
	public double lszkfd; // 临时折扣分担
	public double plzke; // 批量折扣
	// HZJB: K值的折扣
	public double plzkfd; // 批量折扣分担
	public String zsdjbh; // 赠送单据编号
	public double zszke; // 赠送折扣
	public double zszkfd; // 赠送折扣分担
	public String sqkh; // 单品授权卡号				
	public char sqktype; // 单品授权卡类别
	public double sqkzkfd; // 单品授权卡授权折扣分担
	public double hyzklje; // 会员折扣率折扣
	public double cjzke; // 厂家折扣额
	public double ltzke; // 零头折扣额
	public double qtzke; // 其他折扣额（银联追送折扣）
	public double qtzre; // 其他折让额
	public double sswr_sysy; // 四舍五入收银损溢					
	public double fk_sysy; // 付款收银损溢
	public String fph; // 手工单发票号
							//CCZZ 专柜商品=单据号;行号;是否跟随
	public char isvipzk; // 是否允许VIP折扣（Y/N）
	// NMZD，开票时，是否允许开票
	public double xxtax; // 税率				
	// BHLS:是否打印促销促销联	
	public char flag; // 商品标志，1-服务台赠品礼品,2-电子秤条码，3-削价，4-正常商品,5-促销赠送的正常商品,6-被手工议价商品
	public String yjhxcode; // 以旧换新条码				
	public String ysyjh; // 原收银机号			
	public long yfphm; // 原小票号		
	public int yrowno; // 原商品行
	public String jfrule; //商品接收积分的规则
	public String fhdd; // 发货地点														
	// BHLS:后台退货的退货单号
	public String hydjbh; // 会员折扣的产生单据号
	public String inputbarcode; // 商品输入时的条码
	public String memo; // BHLS:电子券A卷最大付款金额 + ',' + B卷最大付款金额									
	// BJYS:附加营业员一
	public String str1; // WJJY:VIP限量规则SEQNO 
	// SFKS:VIP限量规则SEQNO
	// BJYS:附加营业员二
	// BCRM：商品会员价
	public String str2; // BJYS:附加营业员三																												
	// HZJB:记录分摊金额(付款行号:付款代码:分摊金额,付款行号:付款代码:分摊金额)
	public String str3; // HZJB:促销类型;商品属性（goodsDef.specinfo）;满减规则,返券规则,返礼规则(逗号分格)
	// BJYS:临时保存营业员一
	public String str4; // BCRM:活动券收券规则（券，满的条件，收，分组柜组）
	public String str5; // 基类：银行追送规则编号
	public String str6; //ZMSY 即购即提字段值(暂缴税金|暂缴税率|完税（价）金额|使用免税额度) by 2014.01.10
	public String str7;
	public String str8; // 基类：盘点时的修改标志（A-新增 U-修改 D-删除）
	public String str9;	// CCZZ:专柜开票的单据号
	
	public String str10;//ZMSY:行邮税单据编号
	public String str11;//ZMSY:税号（税分类）
	public String str12;//ZMJC、ZMSY:货号 对应goodsdef.str2
	public String str13;//换货时，T表示换退商品，S表示换销商品
	public String str14;
	public String str15;//HTSC:打印换购凭证标识 1 打印 //渠道号
	
	public double num1; // BJYS:是否积分
						//	CBBH 0=非退货商品，1=退货商品，2=换货商品
	public double num2; // 退货时传回原商品行号 
	public double num3; // CCZZ 促销联金额
						//ZMSY:补税税率
						//ZMJC:常旅卡折扣率
	public double num4; //ZMSY:补税税额(总税金)
	public double num5; // HZJB:K值打折的基数
					    // ZMJC:商品最低售价
	public double num6;
	public double num7;//ZMSY:完税（价）金额
	public double num8;//ZMSY:行邮税店承担金额
	public double num9;
	
	public double num10;//ZMSY:行邮税厂家承担金额
	public double num11;//ZMSY:免税额度
	public double num12;//ZMSY:即购即提_补税税额(总税金) 对应num4
	public double num13;//ZMSY：重量
	public double num14;
	public double num15;
	
	public double ysl; // 本行商品原数量（退货时会用到）

	public double memonum1; // BSTD,BCRM里退货时记录原小票的商品数量
	public String custNo; // 团购销售时，单据返回的会员卡号
	public String custType; // 团购销售时，单据返回的会员类型	

	public char isSMPopCalced; // 是否已经计算过超市促销
	public char isPopExc; // 是否例外

	public double rulezke;
	public double rulezkfd;
	public double mjzke;
	public double mjzkfd;
	public double spzkfd;
	public String mjdjbh;
	public String mjcode;
	public String ruledjbh;
	public double costfactor; //超市散装商品的价格因子
	public double dblMaxYhSl;		//最大优惠数量（普通分期促销限量）
	
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
			return this;
		}
	}
}
