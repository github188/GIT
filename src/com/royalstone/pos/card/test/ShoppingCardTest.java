/*
 * 创建日期 2004-5-25
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
package com.royalstone.pos.card.test;

import com.royalstone.pos.card.SHCardPayVO;
import com.royalstone.pos.card.SHCardQueryVO;
import com.royalstone.pos.card.SHCard;

/**
 * 储值卡测试用例
 * @deprecated
 * @author liangxinbiao
 */
public class ShoppingCardTest {
	
	private SHCard shoppingCard;
	
	public ShoppingCardTest(){
		shoppingCard=new SHCard();
	}
	
	public void testQuery() throws Exception {

		SHCardQueryVO query = shoppingCard.query("8800000003", "12345");
		System.out.println("ExceptionInfo=" + query.getExceptioninfo());
		System.out.println("Ifnewcard=" + query.getIfnewcard());
		System.out.println("Detail=" + query.getDetail());

	}
	
	public void testPay() throws Exception{
		
		SHCardPayVO pay=new SHCardPayVO();
		pay.setCardno("8800000003");
		pay.setCashierid("2323");
		pay.setCdseq("3");
		pay.setPassword("12345");
		pay.setPayvalue("340");
		pay.setPosid("a");
		pay.setShopid("a4");
		pay.setTime("2004/4/3");
		
		System.out.println("Pay="+shoppingCard.pay(pay));

		
	}
	
	public static void main(String[] args) throws Exception{
		ShoppingCardTest test=new ShoppingCardTest();
		test.testQuery();
		test.testPay();
	}

}
