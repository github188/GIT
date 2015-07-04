package com.royalstone.pos.gui;

import com.royalstone.pos.card.MemberCard;
import com.royalstone.pos.common.Payment;
import com.royalstone.pos.common.Sale;
import com.royalstone.pos.common.SheetValue;

/*
 * 创建日期 2004-5-20
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */

/**
 * 主界面的接口，让PosDevOut来控制主界面的显示
 * @author liangxinbiao
 */
public interface MainUI {
	
	/**
	 * @param value 收银员号
	 */
	public abstract void setCashier(String value);
	/**
	 * @param value 日期时间
	 */
	public abstract void setDatetime(String value);
	
	/**
	 * @param value 工作日期
	 */
	public abstract void setWorkDay(String value);

	/**
	 * @param value 班次号
	 */
	public abstract void setDutyNo(String value);
	/**
	 * @param value 挂单数
	 */
	public abstract void setHoldNo(String value);
	
	/**
	 * @param value 营业员号
	 */
	public abstract void setWaiterNo(String value);
	
	/**
	 * @param value 输入栏
	 */
	public abstract void setInputField(String value);
	
	/**
	 * @param value 已收款
	 */
	public abstract void setPaid(String value);
	
	/**
	 * @param value POS机号
	 */
	public abstract void setPosNo(String value);
	
	/**
	 * @param value 提示信息
	 */
	public abstract void setPrompt(String value);
	
	/**
	 * @param value 放大区域的商品编码
	 */
	public abstract void setSpCode(String value);
	
	/**
	 * @param value 放大区域的商品名称
	 */
	public abstract void setSpName(String value);
	
	/**
	 * @param value 放大区域的商品单价
	 */
	public abstract void setSpPrice(String value);
	
	/**
	 * @param value 放大区域的商品数量
	 */
	public abstract void setSpQuantity(String value);
	
	/**
	 * @param value 应收款
	 */
	public abstract void setTotal(String value);
	
	/**
	 * @param value 单据号
	 */
	public abstract void setTransNo(String value);
	
	/**
	 * @param value 待收款
	 */
	public abstract void setUnPaid(String value);

	/**
	 * 
	 * @param s 销售记录
	 */
	public abstract void display(Sale s);

	/**
	 * 
	 * @param p 支付记录
	 */
	public abstract void display(Payment p);

	/**
	 * 清除主界面
	 */
	public abstract void clear();
	
	/**
	 * 
	 * @param value 网络状态（联机、脱机）
	 */
	public abstract void setConnStatus(String value);
	
	/**
	 * 
	 * @param value 状态（销售、退货等）
	 */
	public abstract void setStatus(String value);
	
	/**
	 * 
	 * @param value 
	 */
	public abstract void setStep(int value);
	
	/**
	 * 
	 * @param value 待收的显示（待收、找回）
	 */
	public abstract void setUnPaidLabel(String value);
	
	/**
	 * 
	 * @param s 
	 */
	public abstract void displayDiscount(Sale s);
	
	/**
	 * @param s
	 * @param sheet
	 */
	public abstract void displayDiscount4correct(Sale s, SheetValue sheet);

	/**
	 * 显示合计
	 * @param v
	 * @return
	 */
	public abstract int disptotal(SheetValue v);
	
	/**
	 * 显示折扣
	 * @param s 销售记录，包括折扣信息
	 */
	public abstract void displayprom(Sale s);
	
	/**
	 * 显示挂账卡信息
	 * @param loancardnum 挂账卡信息 
	 * @return
	 */
	public abstract int dispMemberCard(MemberCard memberCard);
	
	
	/**
	 * 显示总销售数量
	 * @param value 总销售数量
	 */
	public abstract void setTotalQty(String value);
	
	
	public abstract void setPrinterStatus(boolean value);
	
}
