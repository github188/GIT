package custom.localize.Cbbh;

import java.io.Serializable;
import java.util.*;

public class Cbbh_WCCRuleDef implements Serializable, Cloneable {
	
	
	/**
	 * 微信券收券规则 
	 */
	private static final long serialVersionUID = 1L;

	public static String[] ref={"seqno","billno","isyq","mode","gdid","catid","ppid","value","sdate","edate"};	

	public static final String WCC_CALC_MODE_ALL  			 =  "0";
	public static final String WCC_CALC_MODE_GDID    		 =  "1";
	public static final String WCC_CALC_MODE_CATID_AND_PPID  =  "2";
	public static final String WCC_CALC_MODE_CATID  		 =  "3";
	public static final String WCC_CALC_MODE_PPID  			 =  "4";
	
	public static final String WCC_CALC_MARK_FREE			 =  "0";
	public static final String WCC_CALC_MARK_SUM			 =  "S";
	public static final String WCC_CALC_MARK_MATCH			 =  "M";
	public static final String WCC_CALC_MARK_USED			 =  "U";
	
	public long  	seqno;		//序号
	public String 	billno;		//单据号
	public char		isyq;		//是否用券
	public String 	mode;		//匹配类型
	public String 	gdid;		//商品编码
	public String 	catid;		//类别
	public String 	ppid;		//品牌
	public double 	value;		//规则门槛
	public Date		sdate;		//开始日期
	public Date 	edate;		//技术日期
	
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
