package com.royalstone.pos.ticket;
import java.io.FileInputStream;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.sql.Time;
import java.text.SimpleDateFormat;

import com.royalstone.pos.common.Payment;

import com.royalstone.pos.util.Day;
import com.royalstone.pos.util.PosTime;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class RePrint{
	
	private SimpleDateFormat sdfTime=new SimpleDateFormat("HH:mm:ss");
	private SimpleDateFormat sdfDate=new SimpleDateFormat("yyyy-MM-dd");
	
	public RePrint(PosTicket posTicket){
    this.posTicket = posTicket;
	}
    
    private PosTicket posTicket;	 

     public void ToPrint(String file){
		try{
     	Document doc = (new SAXBuilder()).build( new FileInputStream(file) );
     	Element root = doc.getRootElement();
     	Element sheet = root.getChild("sheet");
     	Element salelist = sheet.getChild("salelist");
     	Element paymentlist = sheet.getChild("paymentlist");
     	List sale = salelist.getChildren("sale");
     	List payment = paymentlist.getChildren("payment");
     	

     	
     	for (int i=0 ; i < sale.size() ; i++){
     		
			Element goods = ((Element)(sale.get(i))).getChild("goods");
			Element elm_name = goods.getChild("name");
					
			Element item = (Element) sale.get(i);
     		Element elm_vgno = item.getChild("vgno");
     		Element elm_qty = item.getChild("qty");
     		Element elm_price = item.getChild("stdprice");
     		Element elm_value = item.getChild("itemvalue");
     		    		     		
  
			String name = elm_name.getTextTrim();
			System.out.println("name:"+name);
            String vgno = elm_vgno.getTextTrim();
            System.out.println("vgno:"+vgno);
			String qty = elm_qty.getTextTrim();
			System.out.println("qyt:"+qty);
			String price = elm_price.getTextTrim();
			System.out.println("price:"+price);
			String value = elm_value.getTextTrim();
			System.out.println("value:"+value);

            HashMap params = new HashMap();
            params.clear();
			params.put("${GoodsName}",  name);
			params.put("${Barcode}", vgno);
			params.put("${Quantity}", qty);
			params.put("${Price}", price);
			params.put("${Amount}", value);

			posTicket.parseSale(params);
	
            
     	}
     	
     	for (int i=0 ; i < payment.size() ; i++){
     		Element item = (Element) payment.get(i);
     		Element elm_reason = item.getChild("reason");
     		Element elm_type = item.getChild("type");
     		Element elm_currency = item.getChild("currency");
     		Element elm_value = item.getChild("value");
     		Element elm_value_equiv = item.getChild("value_equiv");
     		Element elm_cardno = item.getChild("cardno");
     		Element elm_trainflag = item.getChild("trainflag");
     		Element elm_sysdate = item.getChild("sysdate");
     		Element elm_systime = item.getChild("systime");
     		
     		int reason = elm_reason.getTextTrim().charAt(0);
     		int type = Integer.parseInt(elm_type.getTextTrim());
     		String currency = elm_currency.getTextTrim();
     		int value = Integer.parseInt(elm_value.getTextTrim());
     		int value_equiv = Integer.parseInt(elm_value_equiv.getTextTrim());
     		String cardno = elm_cardno.getTextTrim();
     		int trainflag = Integer.parseInt(elm_trainflag.getTextTrim());
     		Date sysdate = sdfDate.parse(elm_sysdate.getTextTrim());
     		Day date = new Day(sysdate);
			Date systime = sdfTime.parse(elm_systime.getTextTrim());
			Time time = new Time(systime.getTime());
			PosTime postime = new PosTime(time);
			
			Payment Lpayment = new Payment(reason, type, currency, value,
			                               value_equiv, cardno, trainflag, date, postime);    		
     	}
     	     			
	 } catch ( Exception e ){
				 e.printStackTrace();
     }
     }
}
