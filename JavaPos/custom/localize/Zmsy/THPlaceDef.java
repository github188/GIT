package custom.localize.Zmsy;

import java.io.Serializable;

/**
 * 提货地点定义
 * @author yw
 *
 */
public class THPlaceDef implements Serializable
{

	private static final long serialVersionUID = 0L;
	
	public static String[] ref = { "thbillno", "thsp", "thtime", "thjc", "thhg", "thgklb", "str1", "str2", "num1", "num2" };
	
	/**
	 * 提货地点编号
	 */
	public String thbillno;
	
	/**
	 * 提货地点
	 */
	public String thsp;
	
	/**
	 * 提货时间参数(小时)
	 */
	public double thtime;
	
	/**
	 * 简称
	 */
	public String thjc;
	
	/**
	 * 主管海关
	 */
	public String thhg;
	
	/**
	 * 顾客类别
	 */
	public String thgklb;
	
	public String str1;//备用
	public String str2;//备用
	public double num1;//备用
	public double num2;//备用
}
