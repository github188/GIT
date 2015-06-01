package custom.localize.Zmjc;

/**
 * 顾客信息常量
 * @author wy
 *
 */
public class CustInfoDef
{
	/**
	 * 顾客信息_姓名
	 */
	public final static String CUST_SCNAME = "SCNAME";
	
	/**
	 * 顾客信息_护照
	 */
	public final static String CUST_SCPASSPORTNO = "SCPASSPORTNO";
	
	/**
	 * 顾客信息_国籍
	 */
	public final static String CUST_SCNATIONALITY = "SCNATIONALITY";
	
	/**
	 * 顾客信息_航班号
	 */
	public final static String CUST_SCNUMBER = "SCNUMBER";
	
	/**
	 * 身份证号
	 */
	public final static String CUST_SCID = "SCID";
	
	/**
	 * 其他证件
	 */
	public final static String CUST_SCOTHERNO = "SCOTHERNO";
	
	/**
	 * 性别
	 */
	public final static String CUST_SCSEX = "SCSEX";
		
	/**
	 * 备用传递字段(存储SCMEMO1-20,中间用 @ 符号隔开)
	 */
	public final static String CUST_SCMEMO = "SCMEMO";
	
	/**
	 * 备用字段
	 */
	public final static String CUST_SCMEMO1 = "SCMEMO1";//出生年份(后两位)
	public final static String CUST_SCMEMO2 = "SCMEMO2";//VIP卡号
	public final static String CUST_SCMEMO3 = "SCMEMO3";
	public final static String CUST_SCMEMO4 = "SCMEMO4";
	public final static String CUST_SCMEMO5 = "SCMEMO5";
	public final static String CUST_SCMEMO6 = "SCMEMO6";
	public final static String CUST_SCMEMO7 = "SCMEMO7";
	public final static String CUST_SCMEMO8 = "SCMEMO8";
	public final static String CUST_SCMEMO9 = "SCMEMO9";
	public final static String CUST_SCMEMO10 = "SCMEMO10";
	public final static String CUST_SCMEMO11 = "SCMEMO11";
	public final static String CUST_SCMEMO12 = "SCMEMO12";
	public final static String CUST_SCMEMO13 = "SCMEMO13";
	public final static String CUST_SCMEMO14 = "SCMEMO14";
	public final static String CUST_SCMEMO15 = "SCMEMO15";
	public final static String CUST_SCMEMO16 = "SCMEMO16";
	public final static String CUST_SCMEMO17 = "SCMEMO17";
	public final static String CUST_SCMEMO18 = "SCMEMO18";
	public final static String CUST_SCMEMO19 = "SCMEMO19";
	public final static String CUST_SCMEMO20 = "SCMEMO20";
	
	
	/**
	 * memo分开符
	 * @return
	 */
	public static String getSplitRegex()
	{
		return "@";
	}
}
