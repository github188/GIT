package com.royalstone.pos.journal;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.FileInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: yaopoqing
 * Date: 2005-5-26
 * Time: 10:46:18
 * To change this template use Options | File Templates.
 */
public class MemberCard4Reprint {
    private String cardNo;
	private String memberLevel;
    private String totalPoint;
    private String currentPoint;
	//public static String filename = "reprint/reprintsheet.xml";

	public MemberCard4Reprint(){

	}

    public String getCardNo() {
        return cardNo;
    }

    public String getMemberLevel() {
        return memberLevel;
    }

    public String getTotalPoint() {
        return totalPoint;
    }

    public String getCurrentPoint() {
        return currentPoint;
    }

    public static MemberCard4Reprint getInstance(String file){
        MemberCard4Reprint mc4r=null;
        try{
			Document doc = (new SAXBuilder()).build( new FileInputStream(file) );
			Element root = doc.getRootElement();
			Element sheet = root.getChild("sheet");
			Element memberCard = sheet.getChild("member_card");
			if(memberCard==null)
                return null;
            else
                mc4r=new MemberCard4Reprint();
				Element carNo = memberCard.getChild("cardno");
				Element memberLevel = memberCard.getChild("memberlevel");
				Element totalPoint = memberCard.getChild("totalpoint");
                Element currentPoint=  memberCard.getChild("currentpoint");

					mc4r.setCardNo(carNo.getTextTrim());
					mc4r.setMemberLevel(memberLevel.getTextTrim());
                    mc4r.setTotalPoint(totalPoint.getTextTrim());
                    mc4r.setCurrentPoint(currentPoint.getTextTrim());
              return mc4r;
			}

			 catch ( Exception e ){
				e.printStackTrace();
			}
        return null;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public void setMemberLevel(String memberLevel) {
        this.memberLevel = memberLevel;
    }

    public void setTotalPoint(String totalPoint) {
        this.totalPoint = totalPoint;
    }

    public void setCurrentPoint(String currentPoint) {
        this.currentPoint = currentPoint;
    }

//	/**从文件中生成卡余额和卡号信息
//	 * @param file 上一单的本地文件名
//	 * */
//	private void createInstace(String file){
//
//		try{
//			Document doc = (new SAXBuilder()).build( new FileInputStream(file) );
//			Element root = doc.getRootElement();
//			Element sheet = root.getChild("sheet");
//			Element paymentlist = sheet.getChild("member_card");
//			List payments = paymentlist.getChildren("payment");
//
//			for (int i=0 ; i < payments.size() ; i++){
//				Element type = ((Element)(payments.get(i))).getChild("type");
//				Element card = ((Element)(payments.get(i))).getChild("cardno");
//				Element cardResult = ((Element)(payments.get(i))).getChild("cardResult");
//
//				if(type.getTextTrim().charAt(0)=='V'){
//					cardno = card.getTextTrim();
//					resultValue = cardResult.getTextTrim();
//				}
//
//			}
//			return ;
//
//			} catch ( Exception e ){
//				e.printStackTrace();
//			}
//			return ;
//		}

	public static void main(String[] args) {
		MemberCard4Reprint test = MemberCard4Reprint.getInstance("reprint/reprintsheet.xml");
		System.out.println("test.getCardno():  "+test.getCardNo());
		System.out.println("test.getMemberLevel():  "+test.getMemberLevel());
        System.out.println("test.getCurrentPoint():  "+test.getCurrentPoint());
        System.out.println("test.getTotalPoint():  "+test.getTotalPoint());
	}
}
