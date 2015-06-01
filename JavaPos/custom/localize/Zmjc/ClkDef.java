package custom.localize.Zmjc;

import java.io.Serializable;

/**
 * 常旅卡定义
 * @author yw
 *
 */
public class ClkDef implements Cloneable,Serializable
{
	private static final long serialVersionUID = 0L;
	
	public static String[] ref = { "cardno", "cardname", "nation", "passport", "ljhb", "iszk", "zklb",
	                               "isaq" , "je_aq" , "isbq" , "je_bq" , "str1", "str2" , "str3" , "str4" , "num1" , "num2" , "num3" , "num4" };
    
	/**
	 * 常旅卡号
	 */
	public String cardno;
	/**
	 * 姓名
	 */
	public String cardname;
	
	/**
	 * 国籍
	 */
	public String nation;
	
	/**
	 * 护照
	 */
	public String passport;
	
	/**
	 * 离境航班
	 */
	public String ljhb;
	
	/**
	 * 是否折扣
	 */
	public char iszk;
	
	/**
	 * 折扣类别
	 */
	public String zklb;
	
	/**
	 * 是否A券
	 */
	public char isaq;
	
	/**
	 * A券金额
	 */
	public double je_aq;
	
	/**
	 * 是否B券
	 */
	public char isbq;
	
	/**
	 * B券金额
	 */
	public double je_bq;
	
	/**
	 * 备用
	 */
	public String str1;
	
	/**
	 * 备用
	 */
	public String str2;
	
	/**
	 * 备用
	 */
	public String str3;
	
	/**
	 * 备用
	 */
	public String str4;

	
	/**
	 * 备用：卡折扣率（柬埔寨用2014.12.09）
	 */
	public double num1;
	
	/**
	 * 备用
	 */
	public double num2;
	
	/**
	 * 备用
	 */
	public double num3;
	
	/**
	 * 备用
	 */
	public double num4;
	
	
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
