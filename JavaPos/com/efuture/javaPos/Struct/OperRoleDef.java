package com.efuture.javaPos.Struct;

import java.io.Serializable;

// 人员角色定义
public class OperRoleDef implements Serializable
{
	private static final long serialVersionUID = 1L;

	public static String[] ref = {"code","operrange","isgrant","privth","privqx","privdy",
		"privgj","priv","dpzkl","zpzkl","thxe","privje1","privje2","privje3","privje4","privje5",
		"grantgz","funcmenu"};
	
	public String code;				// 角色代码,主键					
	public char operrange;			// 操作数据范围，Y-全部，N-自身
	public char isgrant;			// 是否允许授权				
	public char privth;				// 是否能退货(Y/N-允许/不允许,T-只能退货)		
	public char privqx;				// 是否能取消交易(Y/N-允许/不允许,Q-只能取消单品)
	public char privdy;				// 重新打印小票权限
	public char privgj;				// 议价权限,可改价
	public String priv;				// 保留,其他权限
									// priv[0],Y-可红冲,N-不可红冲
									// priv[1],重打印赠品联包含赠券
									// priv[2],是否允许突破商品最低折扣控制
									// priv[3],扣回权限
									// priv[4],查看报表权限
									// priv[5],钱箱权限(Y-可打开,N-不可打开)
									// priv[6],看挂单权限(Y-可挂单解挂,N-不能挂单解挂,A-只能挂单,B-只能解挂)
	
									// HZJB 重打印定金单
									// priv[7],备用
									// priv[8],备用
									// priv[9],备用
									// priv[10],备用
	public double dpzkl;			// 单品折扣限额，>=1-不允许折扣
	public double zpzkl;			// 总品折扣限额，>=1-不允许折扣	
	public double thxe;				// 退货限额					
	public double privje1;			// 保留						
	public double privje2;			// 保留						
	public double privje3;			// 保留						
	public double privje4;			// 保留						
	public double privje5;			// 保留
	public String grantgz;			// 授权范围，柜组用','分割		
	public String funcmenu;			// 菜单功能, 功能用','分割
}
