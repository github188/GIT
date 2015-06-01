package com.efuture.javaPos.Struct;


// 人员资料定义
public class OperUserDef implements Cloneable
{
	public static String[] ref={"gh","name","passwd","type","islogin","maxdate","role","yyygz","memo",
		"operrange","isgrant","privth","privqx","privdy","privgj","priv","dpzkl","zpzkl","thxe","privje1","privje2","privje3","privje4","privje5","grantgz","funcmenu","ists"};

	public static String[] refLocal={"gh","name","passwd","type","islogin","maxdate","role","yyygz","memo","ists"};
	
	public static String[] key = {"gh"};

	public String gh;				// 工号,主键					
	public String name;				// 姓名						
	public String passwd;			// 密码						
	public char type;				// 员工类别，1-收银员，2-营业员，3-维护员，4-授权员工  
	public char islogin;			// 是否允许登录（Y/N）		
	public String maxdate;			// 有效期,YYYY/MM/DD	
	public String role;				// 角色代码
	public String yyygz;			// 营业员营业柜组
	public String memo;				// 备注
	public String ists;             // 是否通收
	
	// 本地数据库无以下字段 参看OperRoleDef
	public char operrange;			// 操作数据范围，Y-全部,解锁权限，N-自身
	public char isgrant;			// 是否允许授权				
	public char privth;			// 是否能退货(Y/N-允许/不允许,T-只能退货)		
	public char privqx;			// 是否能取消交易(Y/N-允许/不允许,Q-只能取消单品)
	public char privdy;			// 重新打印小票权限 N-无重印权(既不能重印上笔也不能查询小票重印) Y-有重印权 L-只允许重印上一笔
	public char privgj;			// 议价权限,可改价
	public String priv;				// 保留,其他权限 
									// priv[0],Y-可红冲,N-不可红冲
									// priv[1],重打印赠品联包含赠券
									// priv[2],是否允许突破商品最低折扣控制
									// priv[3],扣回权限
									// priv[4],查看报表权限
									// priv[5],钱箱权限		Y-可打开,N-不可打开
									// priv[6],看挂单权限	Y-可挂单解挂,N-不能挂单解挂,A-只能挂单,B-只能解挂 
									// HZJB 重打印定金单
									// priv[7],备用
									// priv[8],备用
									// priv[9],备用
									// priv[10],备用
	public double dpzkl;			// 单品折扣限额，>=1-不允许折扣
	public double zpzkl;			// 总品折扣限额，>=1-不允许折扣	
	public double thxe;			// 退货限额					
	public double privje1;			// 保留							
	public double privje2;			// 保留						
	public double privje3;			// 保留						
	public double privje4;			// 保留						
	public double privje5;			// 保留
	public String grantgz;			// 授权范围，柜组用','分割		
	public String funcmenu;			// 菜单功能, 功能用','分割
	
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
