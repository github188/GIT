/*
 * 创建日期 2004-5-25
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
package com.royalstone.pos.card.test;

import com.royalstone.pos.card.*;

import java.net.URL;

/**
 * 储值卡测试用例
 * @deprecated
 * @author liangxinbiao
 */
public class MemberCardTest {
	
	private MemberCardMgr shoppingCard;
	
	public MemberCardTest(){
        try {
            URL servlet=new URL(
					"http://localhost:9090/pos41/DispatchServlet");
            shoppingCard=new MemberCardMgrImpl(servlet);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
    }
	
//	public void testQuery() throws Exception {
//
//		SHCardQueryVO query = shoppingCard.query("8800000003", "12345");
//		System.out.println("ExceptionInfo=" + query.getExceptioninfo());
//		System.out.println("Ifnewcard=" + query.getIfnewcard());
//		System.out.println("Detail=" + query.getDetail());
//
//	}
	
	public void testPay() throws Exception{
		
		MemberCardUpdate pay=new MemberCardUpdate();
		pay.setCardno("8800000003");
		pay.setCashierid("2323");
		pay.setCdseq("3");
		pay.setPayvalue("340");
		pay.setPosid("a");
		pay.setShopid("a4");
		pay.setTime("2004/4/3");
        pay.setPoint("123.05");

		System.out.println("Pay="+shoppingCard.updateCardInfo(pay));

		
	}
	
	public static void main(String[] args) throws Exception{
		MemberCardTest test=new MemberCardTest();
	//	test.testQuery();
		test.testPay();
	}

}
