package com.royalstone.pos.ticket;

import java.sql.Time;
import java.text.SimpleDateFormat;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.royalstone.pos.common.BankCardTransReturnValue;
import com.royalstone.pos.common.Goods;
import com.royalstone.pos.common.Payment;
import com.royalstone.pos.common.Sale;
import com.royalstone.pos.core.PaymentList;
import com.royalstone.pos.core.SaleList;

import com.royalstone.pos.util.Day;
import com.royalstone.pos.util.PosTime;

import java.io.FileInputStream;
import java.util.Date;
import java.util.List;

public class CreatePosSheet {
	private SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
	private SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");

	public Goods CreateGoods(Element item) {
		try {

			Element elm_vgno = item.getChild("vgno");
			Element elm_barcode = item.getChild("barcode");
			Element elm_name = item.getChild("name");
			Element elm_deptid = item.getChild("deptid");
			Element elm_spec = item.getChild("spec");
			Element elm_unit = item.getChild("unit");
			Element elm_price = item.getChild("price");
			Element elm_type = item.getChild("type");
			Element elm_x = item.getChild("x");
			Element elm_ptype = item.getChild("ptype");
			Element elm_batchno = item.getChild("batchno");
			//Element elm_cflag = item.getChild("cflag");
			Element elm_manufact = item.getChild("manufact");

			String vgno = elm_vgno.getTextTrim();
			String barcode = elm_barcode.getTextTrim();
			String name = elm_name.getTextTrim();
			String deptid = elm_deptid.getTextTrim();
			String spec = elm_spec.getTextTrim();
			String unit = elm_unit.getTextTrim();
			String manufact = elm_manufact.getTextTrim();
			int price = Integer.parseInt(elm_price.getTextTrim());
			int type = Integer.parseInt(elm_type.getTextTrim());
			int x = Integer.parseInt(elm_x.getTextTrim());
			int cflag = 0;
			String ptype = elm_ptype.getTextTrim();
			String batchno = elm_batchno.getTextTrim();

			Goods goods =
				new Goods(
					vgno,
					barcode,
					name,
					deptid,
					spec,
					unit,
					price,
					type,
					x,
					ptype,
					batchno,
					cflag,
					manufact);
			return goods;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public Sale CreateSale(Goods goods, Element item) {
		try {

			Element elm_orgcode = item.getChild("orgcode");
			Element elm_qty = item.getChild("qty");
			Element elm_type = item.getChild("type");
			Element elm_disctype = item.getChild("disctype");
			Element elm_waiter = item.getChild("waiter");
			Element elm_authorizer = item.getChild("authorizer");
			Element elm_placeno = item.getChild("placeno");
			Element elm_colorsize = item.getChild("colorsize");
			Element elm_itemvalue = item.getChild("itemvalue");
			Element elm_discvalue = item.getChild("discvalue");
			Element elm_factvalue = item.getChild("factvalue");
			Element elm_trainflag = item.getChild("trainflag");
			Element elm_sysdate = item.getChild("sysdate");
			Element elm_systime = item.getChild("systime");

			String orgcode = elm_orgcode.getTextTrim();
			int qty = Integer.parseInt(elm_qty.getTextTrim());
			int type = elm_type.getTextTrim().charAt(0);
			int disctype = elm_disctype.getTextTrim().charAt(0);
			String waiter = elm_waiter.getTextTrim();
			String authorizer = elm_authorizer.getTextTrim();
			String placeno = elm_placeno.getTextTrim();
			String colorsize = elm_colorsize.getTextTrim();
			int itemvalue = Integer.parseInt(elm_itemvalue.getTextTrim());
			int discvalue = Integer.parseInt(elm_discvalue.getTextTrim());
			int factvalue = Integer.parseInt(elm_factvalue.getTextTrim());
			int trainflag = Integer.parseInt(elm_trainflag.getTextTrim());
			Date sysdate = sdfDate.parse(elm_sysdate.getTextTrim());
			Day date = new Day(sysdate);
			Date systime = sdfTime.parse(elm_systime.getTextTrim());
			Time time = new Time(systime.getTime());
			PosTime postime = new PosTime(time);
			Sale sale =
				new Sale(
					goods,
					orgcode,
					qty,
					type,
					disctype,
					waiter,
					authorizer,
					placeno,
					colorsize,
					itemvalue,
					discvalue,
					factvalue,
					trainflag,
					date,
					postime);

			Element elm_factprice = item.getChild("factprice");

			if (elm_factprice != null) {
				long factprice = Long.parseLong(elm_factprice.getTextTrim());
				sale.setFactPrice(factprice);
			}

			return sale;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public SaleList CreateSalelst(String file, String flag) {
		try {
			SaleList sale_lst = new SaleList();
			List sale;
			Document doc = (new SAXBuilder()).build(new FileInputStream(file));
			Element root = doc.getRootElement();
			Element sheet = root.getChild("sheet");
			Element salelist = sheet.getChild("salelist");
			if (flag.equals("falsesale")) {
				sale = salelist.getChildren();
			} else {
				sale = salelist.getChildren("sale");
			}

			for (int i = 0; i < sale.size(); i++) {
				Element goods = ((Element) (sale.get(i))).getChild("goods");
				Goods Lgoods = CreateGoods(goods);
				Element item = (Element) sale.get(i);
				Sale Lsale = CreateSale(Lgoods, item);

				sale_lst.add(Lsale);

			}
			return sale_lst;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public Payment CreatePayment(Element item) {
		try {

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
			int type = elm_type.getTextTrim().charAt(0);
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
			Payment payment =
				new Payment(
					reason,
					type,
					currency,
					value,
					value_equiv,
					cardno,
					trainflag,
					date,
					postime);

			Element elm_bankvalue = item.getChild("bankvalue");
			Element elm_banktranstype = item.getChild("banktranstype");

			if (elm_bankvalue != null
				&& !elm_bankvalue.getTextTrim().equals("") && elm_banktranstype!=null) {

				BankCardTransReturnValue bankCardTransReturnValue =
					new BankCardTransReturnValue(elm_bankvalue.getTextTrim().replaceAll("s"," "));
				bankCardTransReturnValue.setTransType(elm_banktranstype.getTextTrim());
				payment.setBankCardTransReturnValue(bankCardTransReturnValue);
			}

			return payment;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public PaymentList CreatePaylst(String file) {
		try {
			PaymentList pay_lst = new PaymentList();

			Document doc = (new SAXBuilder()).build(new FileInputStream(file));
			Element root = doc.getRootElement();
			Element sheet = root.getChild("sheet");
			Element paymentlist = sheet.getChild("paymentlist");
			List payment = paymentlist.getChildren("payment");

			for (int i = 0; i < payment.size(); i++) {
				Element item = (Element) payment.get(i);
				Element elm_reason = item.getChild("reason");
				int reason = elm_reason.getTextTrim().charAt(0);
				if (reason == Payment.PAY
					|| reason == Payment.CASHOUT
					|| reason == Payment.CASHIN
					|| reason == Payment.FLEE
					|| reason == Payment.SAMPLE
					|| reason == Payment.OILTEST) {
					Payment Lpayment = CreatePayment(item);
					pay_lst.add(Lpayment);
				}

			}
			return pay_lst;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public String getstoreid(String file) {
		try {

			Document doc = (new SAXBuilder()).build(new FileInputStream(file));
			Element root = doc.getRootElement();
			List context = root.getChildren("context");
			Element item = (Element) context.get(0);
			Element elm_storeid = item.getChild("storeid");
			String storeid = elm_storeid.getTextTrim();
			return storeid;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getposid(String file) {
		try {

			Document doc = (new SAXBuilder()).build(new FileInputStream(file));
			Element root = doc.getRootElement();
			List context = root.getChildren("context");
			Element item = (Element) context.get(0);
			Element elm_posid = item.getChild("posid");
			String posid = elm_posid.getTextTrim();
			return posid;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getcashierid(String file) {
		try {

			Document doc = (new SAXBuilder()).build(new FileInputStream(file));
			Element root = doc.getRootElement();
			List context = root.getChildren("context");
			Element item = (Element) context.get(0);
			Element elm_cashierid = item.getChild("cashierid");
			String cashierid = elm_cashierid.getTextTrim();
			return cashierid;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getsheetid(String file) {
		try {

			Document doc = (new SAXBuilder()).build(new FileInputStream(file));
			Element root = doc.getRootElement();
			List context = root.getChildren("context");
			Element item = (Element) context.get(0);
			Element elm_sheetid = item.getChild("sheetid");
			String sheetid = elm_sheetid.getTextTrim();
			return sheetid;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getworkdate(String file) {
		try {

			Document doc = (new SAXBuilder()).build(new FileInputStream(file));
			Element root = doc.getRootElement();
			List context = root.getChildren("context");
			Element item = (Element) context.get(0);
			Element elm_workdate = item.getChild("workdate");
			String workdate = elm_workdate.getTextTrim();
			return workdate;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}