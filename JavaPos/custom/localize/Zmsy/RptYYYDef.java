package custom.localize.Zmsy;

/**
 * 营业员报表定义类
 *
 */
public class RptYYYDef
{
	private static final long serialVersionUID = 0L;
	
	public static String[] ref = { "yyyh", "saleje", "salebs", "salezk", "thje", "thbs", "thzk", "str1", "str2", "str3", "str4", "str5", "num1", "num2", "num3", "num4", "num5" };
	
	/**
	 * 营业员号
	 */
	public String yyyh;
	
	/**
	 * 销售金额
	 */
	public double saleje;
	
	/**
	 * 销售笔数
	 */
	public int salebs;
	
	/**
	 * 销售折扣金额
	 */
	public double salezk;
	
	/**
	 * 退货金额
	 */
	public double thje;
	
	/**
	 * 退货笔数
	 */
	public double thbs;
	
	/**
	 * 退货折扣
	 */
	public double thzk;
	
	public String str1;
	public String str2;
	public String str3;
	public String str4;
	public String str5;
	
	public double num1;
	public double num2;
	public double num3;
	public double num4;
	public double num5;
}

