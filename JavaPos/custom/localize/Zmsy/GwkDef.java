package custom.localize.Zmsy;

import java.io.Serializable;


/**
 * 购物卡
 * @author yw
 *
 */
public class GwkDef implements Cloneable,Serializable
{
	
	private static final long serialVersionUID = 0L;
	
	public static String[] ref = { "syjh", "syyh", "fphm", "ismsj", "code", "zkl", "name", "nation" , "passport" , "ljhb" , "ljrq" , "ljsj", "gklb" , "zjlb" , "gender" , "birth" , "age" , "email" , "mobile" , "isdx" , "fzjg" , "xe" , "sxje" , "status" , "ispdxe" , "sjcd" , "bsjs" , "xgjs" , "shts" , "qje" , "isAccessHG" , "thdd", "message", "address", "num1" , "num2"  , "num3" , "num4"  , "num5" , "num6"  , "num7" , "str1"  , "str2" , "str3"  , "str4" , "str5"  , "str6"  , "str7" };
    
	
	/**
	 * 收银机号
	 */
	public String syjh;
	
	/**
	 * 收银员号
	 */
	public String syyh;
	
	/**
	 * 小票号
	 */
	public long fphm;
	
	/**
	 * 1.是否免税款机(Y免税款机，N有税款机）2.是否即购即提
	 * GlobalInfo.syjDef.priv.charAt(0) saleHead.str8..charAt(1)
	 * YN
	 */
	public String ismsj;//char -->String by 2014.2.8
	
	/**
	 * 卡号
	 */
	public String code;
	
	/**
	 * 折扣率
	 */
	public double zkl=-1;
	
	/**
	 * 姓名
	 */
	public String name;
	
	/**
	 * 国藉
	 */
	public String nation;
	
	/**
	 * 护照号(证件号码)
	 */
	public String passport;
	
	/**
	 * 离境航班
	 */
	public String ljhb;
	
	/**
	 * 离境日期
	 */
	public String ljrq;
	
	/**
	 * 离境时间
	 */
	public String ljsj;
	
	/**
	 * 离境地点
	 *//*
	public String ljdd;*/
	
	/**
	 * 顾客类别
	 */
	public String gklb;
	
	/**
	 * 证件类别
	 */
	public String zjlb;
	
	/**
	 * 性别(0-男,1-女)
	 */
	public String gender;
	
	/**
	 * 生日
	 */
	public String birth;
	
	/**
	 * 年龄
	 */
	public String age;
	
	/**
	 * email
	 */
	public String email;
	
	/**
	 * 手机
	 */
	public String mobile;
	
	/**
	 * 是否短信
	 */
	public String isdx;
	
	/**
	 * 发证机关
	 */
	public String fzjg;
	
	/**
	 * 消费限额(还可以用的金额):余额
	 */
	public double xe;
	
	/**
	 * 消费上限额度(系统参数定义金额)
	 */
	public double sxje;
	
	/**
	 * 用可用次数判断是否可用
	 */
	public String status;
	
	/**
	 * 是否限额:N时不判断限额,Y时要断限额
	 * (ispdxe=N时表示离境顾客)
	 */
	public String ispdxe;
	
	/**
	 * 税金承担(1为中免承担,2为顾客承担)
	 */
	public String sjcd;
	
	/**
	 * 可购买的补税商品件数
	 */
	public double bsjs;
	
	/**
	 * 18类别可购买件数,格式为: 序号,品类,规定件数,已购件数,可购件数,,规定重量 ,已购重量 ,可购重量|序号,品类,规定件数,已购件数,可购件数|...
	 */
	public String xgjs;
	
	/**
	 * 送货提示
	 */
	public String shts;
	
	/**
	 * 券金额
	 */
	public double qje;
	
	/**
	 * 'Y'时，不调用平台接口，'N'时，调用平台接口
	 */
	public String isAccessHG;
	
	/**
	 * 提货地点
	 */
	public String thdd;
	
	/**
	 * 性别(0-男,1-女)
	 *//*
	public String sex;*/
	
	/**
	 * 提示信息
	 */
	public String message;
	
	/**
	 * 家庭住址(从身份证设备里读取)
	 */
	public String address;
	
	public double num1;//本年度已购物次数
	public double num2;//当前MGR的航班行数,与本地比较,当一样时,则实时下载航班信息
	public double num3;//备用
	public double num4;//备用
	public double num5;//备用
	public double num6;//备用
	public double num7;//备用
	public String str1;//备用
					   // 'Y'时，不调用平台接口，'N'时，调用平台接口
	public String str2;//备用
	   				   // 是否执行购物卡折扣率(Y执行[超级VIP],其它不执行) for yans by 2013.9.8
	public String str3;//备用
	public String str4;//备用
	   				   // 用于(本地判断)是否使用购物卡折扣率(Y使用购物卡折扣率,N不使用),另外FINDGWKINFO里的STR1用于平台,所以两边各用各的
	public String str5;//备用
	public String str6;//备用
	public String str7;//备用
	
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
