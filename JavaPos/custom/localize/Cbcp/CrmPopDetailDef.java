package custom.localize.Cbcp;

import java.io.Serializable;

//重百新商品促销临时表
public class CrmPopDetailDef implements Cloneable, Serializable
{

	
	private static final long serialVersionUID = 1L;

	public static String[] ref={"billno","rowno","gdid","barcode","gbname","vpec","guid","unit","unitname","catid","ppid","sj","minsj","sl","sjje","gdpopsj","gdpopzk","gdpopzkfd","gdpopno","rulepopzk","rulepopzkfd","rulepopno","rulepopmemo",
									"rulepopyhfs","rulesupzkfd","ruleticketno","zdrulepopzk","zdrulepopzkfd","zdrulepopno","zdrulepopmome","zdrulepopyhfs","zdrulesupzkfd","zdruleticketno","shopcard","shopcardzk","catcard","catcardzk","ppcardzk","lszk","ticketno","ticketname",
									"ticketzk","jdsl","cjj","cjje","num1","num2","str1","str2"};
	
	public static String[] sendsaleref={"billno","syjh","fphm","rowno","gdid","barcode","gbname","vpec","guid","unit","unitname","catid","ppid","sj","minsj","sl","sjje","gdpopsj","gdpopzk","gdpopzkfd","gdpopno",
									"rulepopzk","rulepopzkfd","rulepopno","rulepopmemo",
									"rulepopyhfs","rulesupzkfd","ruleticketno","zdrulepopzk","zdrulepopzkfd","zdrulepopno","zdrulepopmome","zdrulepopyhfs","zdrulesupzkfd","zdruleticketno","shopcard","shopcardzk","catcard","catcardzk","ppcardzk","lszk","ticketno","ticketname",
									"ticketzk","jdsl","cjj","cjje","num1","ysyjid","yinvno"};
	
	public int billno;//单号
	public int rowno;//行号  salegoods.rowno
	public String gdid;//代码 salegoods.code
	public String barcode;//条码 salegoods.barcode
	
	
	public String gbname;//品名 salegoods.name
	public String vpec;//规格		salegoods.uid
	public String guid;//单位ID		salegoods.str3
	public String unit;//单位			salegoods.unit
	public String unitname;//单位名称 salegoods.str2
	
	
	
	public String catid;//类别	salegoods.catid
	public String ppid;//品牌	salegoods.ppcode
	public double sj;//建议售价	salegoods.jg
	public double minsj;//最低售价	salegoods.jg
	public double sl;//销售数量	salegoods.sl
	public double sjje;//销售金额 salegoods.jg
	public double gdpopsj;//常规促销售价
	public double gdpopzk;//常规促销折扣
	public double gdpopzkfd;//常规促销折扣供应商分担
	public String gdpopno;//常规促销单号			salegoods.str9=gdpopsj,gdpopzk,gdpopzkfd,gdpopno
	public double rulepopzk;//规则促销折扣
	public double rulepopzkfd;//规则促销折扣分担
	public String rulepopno;//规则促销单号
	public String rulepopmemo;//规则促销描述
	public String rulepopyhfs;//规则促销优惠方式
	public double rulesupzkfd;//规则促销供应商分担
	public double ruleticketno;//规则促销返券类型		salegoods.str10=rulepopzk,rulepopzkfd,rulepopno,rulepopmemo,rulepopyhfs,rulesupzkfd,ruleticketno
	public double zdrulepopzk;//整单规则促销折扣
	public double zdrulepopzkfd;//整单规则促销折扣分担
	public String zdrulepopno;//整单规则促销单号
	public String zdrulepopmome;//整单规则促销描述
	public String zdrulepopyhfs;//整单规则促销优惠方式
	public double zdrulesupzkfd;//整单规则促销供应商分担
	public double zdruleticketno;//整单规则促销返券类型 salegoods.str11=zdrulepopzk,zdrulepopzkfd,zdrulepopno,zdrulepopmome,zdrulepopyhfs,zdrulesupzkfd,zdruleticketno
	public String shopcard;//店长卡号
    public double shopcardzk;//店长卡折扣 		salegoods.str12=shopcard,shopcardzk
    public String catcard;//品类卡号
    public double catcardzk;//品类卡折扣	 salegoods.str13=catcard,catcardzk
    public double ppcardzk;//品牌折扣 	salegoods.str14=ppcardzk
    public double lszk;//	salegoods.num14=lszk
	public double ticketno;//所用电子券编号 
	public String ticketname;//所用电子券名称 
	public double ticketzk;//所用电子券抵扣金额 salegoods.str15=ticketno,ticketname,ticketzk
	public double jdsl;//积点数量 salegoods.num15=jdsl
	public double cjj;//成交价  sjje-(gdpopzk-rulepopzk-zdrulepopzk-lszk)
	public double cjje;//成交金额   cjje / sl
	
	public String syjh; //salegoods.syjh
	public int fphm;	//salegoods.fphm
	public String ysyjid;//原收银机号 salegoods.ysyjh
	public int yinvno;//原小票号 salegoods.yfphm
	
	
	public double num1;//备用
						//CBBH thhs 标记是否退货商品
	public double num2;//备用
	public double num3;//备用
	public double num4;//备用
	public double num5;//备用
	public String str1;//备用 salegoods.ysyjh
	public String str2;//备用 salegoods.yfphm
	public String str3;//备用
	public String str4;//备用
	public String str5;//备用
	
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
