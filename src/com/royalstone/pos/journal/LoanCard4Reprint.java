/*
 * Created on 2004-10-14
 */
package com.royalstone.pos.journal;

import java.io.FileInputStream;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**为重打上一单功能增加的记录挂帐卡卡号和余额包装类
 * 从XML文件生成卡号和卡余额信息
 * @author huangxuean
 */
public class LoanCard4Reprint {
	
	private String cardno;
	private String resultValue;
	//public static String filename = "reprint/reprintsheet.xml";
	
	public LoanCard4Reprint(String file){
		createInstace(file);
	}
	
	public void setResultValue(String value){
		resultValue = value;
	}
	
	public String getCardno(){
		return cardno;
	}
		
	public String getResultValue(){
		return resultValue;
	}
	/**从文件中生成卡余额和卡号信息
	 * @param file 上一单的本地文件名
	 * */
	private void createInstace(String file){
		
		try{			
			Document doc = (new SAXBuilder()).build( new FileInputStream(file) );
			Element root = doc.getRootElement();
			Element sheet = root.getChild("sheet");
			Element paymentlist = sheet.getChild("paymentlist");	
			List payments = paymentlist.getChildren("payment");		
		
			for (int i=0 ; i < payments.size() ; i++){
				Element type = ((Element)(payments.get(i))).getChild("type");
				Element card = ((Element)(payments.get(i))).getChild("cardno");
				Element cardResult = ((Element)(payments.get(i))).getChild("cardResult");	
							
				if(type.getTextTrim().charAt(0)=='V'){
					cardno = card.getTextTrim();
					resultValue = cardResult.getTextTrim();
				}
			   
			}
			return ;
		
			} catch ( Exception e ){
				e.printStackTrace();
			}
			return ;
		}

	public static void main(String[] args) {
		LoanCard4Reprint test = new LoanCard4Reprint("reprint/reprintsheet.xml");
		System.out.println("test.getCardno():  "+test.getCardno());
		System.out.println("test.getResultValue():  "+test.getResultValue());
	}
}
