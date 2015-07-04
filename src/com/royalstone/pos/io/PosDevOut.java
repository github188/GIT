package com.royalstone.pos.io;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;

import com.royalstone.pos.card.MemberCard;
import com.royalstone.pos.card.SHCardQueryVO;
import com.royalstone.pos.common.CashBox;
import com.royalstone.pos.common.Payment;
import com.royalstone.pos.common.PosContext;
import com.royalstone.pos.common.Sale;
import com.royalstone.pos.common.SheetBrief;
import com.royalstone.pos.common.SheetValue;
import com.royalstone.pos.core.PaymentList;
import com.royalstone.pos.core.PosSheet;
import com.royalstone.pos.core.SaleList;
import com.royalstone.pos.favor.Discount;
import com.royalstone.pos.gui.HoldList;
import com.royalstone.pos.gui.MainUI;
import com.royalstone.pos.hardware.IPrinter;
import com.royalstone.pos.hardware.POSCustDisplay;
import com.royalstone.pos.hardware.POSPrinter;
import com.royalstone.pos.hardware.POSPrinter1;
import com.royalstone.pos.journal.LoanCard4Reprint;
import com.royalstone.pos.journal.MemberCard4Reprint;
import com.royalstone.pos.shell.pos;
import com.royalstone.pos.ticket.CreatePosSheet;
import com.royalstone.pos.ticket.PosInvoice;
import com.royalstone.pos.ticket.PosTicket;
import com.royalstone.pos.ticket.createrandomnum;
import com.royalstone.pos.util.Exchange;
import com.royalstone.pos.util.Formatter;
import com.royalstone.pos.util.PosConfig;
import com.royalstone.pos.util.Value;
import com.royalstone.pos.core.SaleList;
/**
   @version 1.0 2004.05.14
   @author  Mengluoyi, Royalstone Co., Ltd.
 */

public class PosDevOut {

	private StringBuffer inputLine = new StringBuffer();
	private MainUI mainUI;
	private static PosTicket posTicket;
	private static PosInvoice posInvoice;

	private static POSCustDisplay custDisplay;

	public PosDevOut() {
	}

	public PosDevOut(MainUI mainUI) {
		this.mainUI = mainUI;
	}

	public void setMainUI(MainUI mainUI) {
		this.mainUI = mainUI;
	}

	public MainUI getMainUI() {
		return this.mainUI;
	}

	public void displayAtInputLine(int value) {
		inputLine.append((char) value);
		mainUI.setInputField(inputLine.toString());
	}

	public void clearInputLine() {
		if (inputLine.length() > 0) {
			inputLine.delete(0, inputLine.length());
		}
		mainUI.setInputField(inputLine.toString());
	}

	public void setInputLine(String s) {
		inputLine = new StringBuffer(s);
		mainUI.setInputField(inputLine.toString());

	}

	public void setStep(int i) {
		mainUI.setStep(i);
	}

	public void clear() {
		mainUI.clear();
	}

	public void init() {
		// Initialize GUI compoments here ...
	}

	public static synchronized PosDevOut getInstance() {
		if (out == null) {
			out = new PosDevOut();
			//printer = POSPrinter.getInstance();
			//printer1 = POSPrinter1.getInstance();
			//POSScanner scanner = POSScanner.getInstance();
			custDisplay = POSCustDisplay.getInstance();
			try {
				
				printer = POSPrinter.getInstance();
				
				//printer = new POSPrinterLast();
				posTicket = new PosTicket("posticket.xml", printer);
				
			} catch (Exception ex) {
				posInvoice = null;
				posTicket = null;
				ex.printStackTrace();
			}

		}

		return out;
	}

	public static PosDevOut getInstance(MainUI mainUI) {
		if (out == null)
			out = new PosDevOut(mainUI);
		return out;
	}

	// added on 2004.05.30 by Mengluoyi
	public void displayState(String state_name) {
		mainUI.setStatus(state_name);
	}

	public void display(PosContext con) {
		if (mainUI != null) {
			mainUI.setPosNo(con.getPosid());
			//TODO 屏蔽班次信息  沧州富达 by fire  2005_5_11 
			//mainUI.setDutyNo(Integer.toString(con.getShiftid()));
			mainUI.setCashier(con.getCashierid());
			mainUI.setTransNo(Integer.toString(con.getSheetid()));
			mainUI.setConnStatus(con.getModeStr());
			mainUI.setHoldNo(Integer.toString(con.getHeldCount()));
			//TODO 屏蔽日期信息 沧州富达 by fire  2005_5_11 
			//mainUI.setWorkDay(con.getWorkDate().toString());
		}
	}

	public void displayConnStatus(PosContext con) {
		if (mainUI != null) {
			mainUI.setConnStatus(con.getModeStr());
		}

	}

	/**在mainUI中显示营业员信息
	 * @param waiter_show
	 * @return void
	 * */
	public void dispWaiter(String waiter_show) {
		mainUI.setWaiterNo(waiter_show);

	}

	/**设置营业员信息
	 * @param waiter_show
	 * @return void
	 * */
	public void setWaiter(String waiter_show) {
		PosContext con = PosContext.getInstance();
		con.setWaiterid(waiter_show);
	}

	/**打印销售清单，为深圳真冰娱乐场而开发
	 * @param context
	 * */
	public void printInvoice() {

		PosContext con = PosContext.getInstance();
		int items =
			Integer.parseInt(PosConfig.getInstance().getString("ITEMS"));
		String filename = "reprint/reprintsheet.xml";
		System.out.println("文件名：" + filename);
		CreatePosSheet createpossheet = new CreatePosSheet();
		SaleList falsesalelist =
			createpossheet.CreateSalelst(filename, "falsesale");
		SaleList salelist = createpossheet.CreateSalelst(filename, "sale");
		PaymentList paymentlist = createpossheet.CreatePaylst(filename);
		String storeid = createpossheet.getstoreid(filename);
		String posid = createpossheet.getposid(filename);
		String cashierid = createpossheet.getcashierid(filename);
		String sheetid = createpossheet.getsheetid(filename);
		String workdate = createpossheet.getworkdate(filename);

		PosSheet possheet = new PosSheet();
		possheet.setFalseSaleList(falsesalelist);
		possheet.setSaleList(salelist);
		possheet.setPaymentList(paymentlist);
		possheet.updateValue();
		SheetValue sheetvalue = possheet.getValue();
		int pages = pages(falsesalelist, paymentlist);

		possheet.print();

		for (int k = 0; k < pages; k++) {

			printHeader(
				storeid,
				posid,
				cashierid,
				sheetid,
				workdate,
				Integer.toString(k + 1));

			if ((k + 1) * items >= possheet.getFalseSaleLen()) {
				for (int i = k * items; i < possheet.getFalseSaleLen(); i++) {
					printdisplay(possheet.getFalseSale(i), sheetvalue);
				}

				for (int i = 0;
					i < possheet.getPayLen();
					i++) { //it must be estimated for payment here
					printdisplay(possheet.getPayment(i), sheetvalue);
				}

			} else {
				for (int i = k * items; i < (k + 1) * items; i++) {
					printdisplay(possheet.getFalseSale(i), sheetvalue);
				}
				printTrail(k, pages);
			}

			if (k == pages - 1)
				printTrail(sheetvalue, k, pages);
		}

	}

	private int pages(SaleList salelst, PaymentList paymentlst) {
		int pages = 1;
		PosConfig config = PosConfig.getInstance();
		int items =
			Integer.parseInt(PosConfig.getInstance().getString("ITEMS"));

		int temp = salelst.size() + paymentlst.size();
		if (temp % items == 0) {
			pages = temp / items;
		} else {
			pages = temp / items + 1;
		}

		return pages;
	}

	/**重打上一单小票,called by pos.java
	 * @param context
	 * @return void
	 * */
	public void dispLastPrint(PosContext context) {

		PosContext con = PosContext.getInstance();

		String filename = "reprint/reprintsheet.xml";
		System.out.println("文件名：" + filename);
		CreatePosSheet createpossheet = new CreatePosSheet();
		SaleList falsesalelist =
			createpossheet.CreateSalelst(filename, "falsesale");
		SaleList salelist = createpossheet.CreateSalelst(filename, "sale");
		PaymentList paymentlist = createpossheet.CreatePaylst(filename);
		String storeid = createpossheet.getstoreid(filename);
		String posid = createpossheet.getposid(filename);
		String cashierid = createpossheet.getcashierid(filename);
		String sheetid = createpossheet.getsheetid(filename);
		String workdate = createpossheet.getworkdate(filename);

		PosSheet possheet = new PosSheet();
		possheet.setFalseSaleList(falsesalelist);
		possheet.setSaleList(salelist);
		possheet.setPaymentList(paymentlist);
		possheet.updateValue();
		SheetValue sheetvalue = possheet.getValue();

		possheet.print();

		reprintHeader(storeid, posid, cashierid, sheetid, workdate, "重打小票");
		//-----------------
		MemberCard4Reprint memberCard =
			MemberCard4Reprint.getInstance("reprint/reprintsheet.xml");
		if (memberCard != null) {
			this.dispMemberCardHeader(memberCard);
		}
		//
		for (int i = 0; i < possheet.getFalseSaleLen(); i++) {

			reprintdisplay(possheet.getFalseSale(i), sheetvalue);

			if (possheet.getFalseSale(i).getDiscType() == Discount.MEMBERPRICE
				&& (i + 1) < possheet.getFalseSaleLen()) {
				if (possheet.getFalseSale(i + 1).getType() == Sale.AUTODISC) {
					i++;
				}
			}

		}

		LoanCard4Reprint loancard =
			new LoanCard4Reprint("reprint/reprintsheet.xml");
		boolean isShowTotalInfo = true;
		for (int i = 0; i < possheet.getPayLen(); i++) {
			reprintdisplay(possheet.getPayment(i), sheetvalue);
			if (possheet.getPayment(i).getType() == Payment.CARDLOAN
				|| possheet.getPayment(i).getType() == Payment.CARDSHOP) {
				String cardno = possheet.getPayment(i).getCardno();
				reprintdisplay(loancard);
			}
			if (possheet.getPayment(i).getReason() == Payment.CASHIN
				|| possheet.getPayment(i).getReason() == Payment.CASHOUT) {
				isShowTotalInfo = false;
				break;
			}
			if (possheet.getPayment(i).getReason() == Payment.FLEE
				|| possheet.getPayment(i).getReason() == Payment.OILTEST
				|| possheet.getPayment(i).getReason() == Payment.SAMPLE) {
				sheetvalue = new SheetValue();
			}
		}
		if (memberCard != null) {
			this.display(memberCard);
		}
		reprintdisplayTrail(possheet, sheetvalue, "重打小票", isShowTotalInfo);

		System.out.println("完成小票重打........");
	}

	/**打印销售清单头
	 * @param storeid,posid,cashierid,sheetid,workdate,tag
	 * @return void
	 * */
	public void printHeader(
		String storeid,
		String posid,
		String cashierid,
		String sheetid,
		String workdate,
		String pageno) {

		if (posInvoice != null) {
			HashMap params = new HashMap();

			String info = PosConfig.getInstance().getString("HD1_INFO");
			params.put("${Header1}", "");
			info = PosConfig.getInstance().getString("HD2_INFO");
			params.put("${Header2}", "");
			params.put("${ShopID}", storeid);
			params.put("${PosId}", posid);
			params.put("${InvoiceNo}", sheetid);
			params.put("${Cashier}", cashierid);
			params.put("${Date}", workdate);
			String remark = PosConfig.getInstance().getString("REMARK");
			params.put("${Remark}", remark);
			DecimalFormat df = new DecimalFormat("00");
			String pageNo = df.format(Integer.parseInt(pageno));
			params.put("${Page}", pageNo);

			posInvoice.parseHeader(params);
		}
	}

	/**重打小票头
	 * @param storeid,posid,cashierid,sheetid,workdate,tag
	 * @return void
	 * */
	public void reprintHeader(
		String storeid,
		String posid,
		String cashierid,
		String sheetid,
		String workdate,
		String tag) {

		if (posTicket != null) {
			HashMap params = new HashMap();

			String info = PosConfig.getInstance().getString("HD1_INFO");
			params.put("${Header1}", info);
			info = PosConfig.getInstance().getString("HD2_INFO");
			params.put("${Header2}", info);
			params.put("${ShopID}", storeid);
			params.put("${PosID}", posid);
			createrandomnum randomnum = new createrandomnum();
			String num = Integer.toString(randomnum.getrandomnum());
			//params.put("${SheetID}", sheetid + num);
			params.put("${SheetID}", sheetid);
			params.put("${Cashier}", cashierid);
			params.put("${Date}", Formatter.getDate(new Date()));

			MemberCard4Reprint memberCard =
				MemberCard4Reprint.getInstance("reprint/reprintsheet.xml");
			if (memberCard != null) {
				params.put("${ShowMemberCard}", "true");
				params.put("${memberCardNo}", memberCard.getCardNo());
			} else {
				params.put("${ShowMemberCard}", "false");
			}

			posTicket.reprintparseHeader(params, tag);
		}
	}

	/**打印销售清单销售记录
	* @param Sale s 销售记录
	* @param SheetValue v 销售价值
	* @return void
	* */
	public void printdisplay(Sale s, SheetValue v) {

		if (posInvoice != null) {
			long discDelta = s.getDiscValue();
			HashMap params = new HashMap();

			if (s.getType() != Sale.TOTAL
				&& s.getType() != Sale.AlTPRICE
				&& s.getType() != Sale.SINGLEDISC
				&& s.getType() != Sale.TOTALDISC
				&& s.getType() != Sale.MONEYDISC
				&& s.getType() != Sale.AUTODISC
				&& s.getType() != Sale.LOANCARD
				&& s.getType() != Sale.LOANDISC) {
				String prefix = "";
				if (s.getType() == Sale.WITHDRAW) {
					prefix = "退货 ";
				}
				//params.put("${ItemCode}", s.getVgno());
				params.put("${ItemCode}", s.getBarcode());
				params.put("${Description}", prefix + s.getName());
				params.put(
					"${UnitPrice}",
					(new Value(s.getStdPrice())).toValStr());
				params.put(
					"${Discount}",
					(new Value(discDelta * (-1))).toValStr());
				params.put("${QTY}", s.getQtyStr());
				params.put(
					"${Amount}",
					(new Value(s.getStdValue())).toValStr());

				posInvoice.parseSale(params);

				if (s.getType() == Sale.WITHDRAW) {
					params.clear();
					params.put("${DiscDesc}", "");
					PosContext context = PosContext.getInstance();
					if (context.getAuthorizerid() != null
						&& !context.getAuthorizerid().equals("")) {
						params.put(
							"${DiscValue}",
							"退货授权人:" + context.getAuthorizerid());
					} else {
						params.put(
							"${DiscValue}",
							"退货授权人:" + context.getCashierid());
					}
					//				   posInvoice.parseDiscount(params);
				}

			} else if (s.getType() == Sale.LOANCARD) {
				params.clear();
				params.put("${DiscDesc}", "挂帐卡");
				params.put("${DiscValue}", s.getName());
				posInvoice.parseDiscount(params);
			}

		}

	}

	/**重打小票销售记录
	 * @param Sale s 销售记录
	 * @param SheetValue v 销售价值
	 * @return void
	 * */
	public void reprintdisplay(Sale s, SheetValue v) {

		if (posTicket != null) {

			HashMap params = new HashMap();

			if (s.getType() != Sale.TOTAL
				&& s.getType() != Sale.AlTPRICE
				&& s.getType() != Sale.SINGLEDISC
				&& s.getType() != Sale.TOTALDISC
				&& s.getType() != Sale.MONEYDISC
				&& s.getType() != Sale.AUTODISC
				&& s.getType() != Sale.LOANCARD
				&& s.getType() != Sale.LOANDISC) {
				String prefix = "";
				if (s.getType() == Sale.WITHDRAW) {
					prefix = "退货 ";
				}

				String suffix = "";

				params.put("${GoodsName}", prefix + s.getName() + suffix);

				params.put("${Barcode}", s.getVgno());
				params.put("${Quantity}", s.getQtyStr());

				if (s.getDiscType() == Discount.MEMBERPRICE) {
					params.put(
						"${Price}",
						(new Value(s.getPriceFact())).toValSpecStr());
					params.put(
						"${Amount}",
						(new Value(s.getFactValue())).toValSpecStr());
				} else {
					params.put(
						"${Price}",
						(new Value(s.getStdPrice())).toValSpecStr());
					params.put(
						"${Amount}",
						(new Value(s.getStdValue())).toValSpecStr());
				}
				
				params.put("${Batchno}",s.getBatchno());   
				params.put("${manufact}",s.getmanufact()); 

				posTicket.parseSale(params);

				if (s.getType() == Sale.WITHDRAW) {
					params.clear();
					params.put("${DiscDesc}", "");
					PosContext context = PosContext.getInstance();
					if (context.getAuthorizerid() != null
						&& !context.getAuthorizerid().equals("")) {
						params.put(
							"${DiscValue}",
							"退货授权人:" + context.getAuthorizerid());
					} else {
						params.put(
							"${DiscValue}",
							"退货授权人:" + context.getCashierid());
					}
					posTicket.parseDiscount(params);
				}
			} else if (
				(s.getType() == Sale.TOTAL
					|| s.getType() == Sale.AlTPRICE
					|| s.getType() == Sale.SINGLEDISC
					|| s.getType() == Sale.TOTALDISC
					|| s.getType() == Sale.MONEYDISC
					|| s.getType() == Sale.AUTODISC
					|| s.getType() == Sale.LOANDISC)) {
				params.clear();
				params.put("${SubtotalDesc}", s.getName());
				if (s.getType() == Sale.TOTAL) {
					params.put(
						"${SubtotalValue}",
						new Value(v.getValueTotal()).toString());
				} else {
					params.put(
						"${SubtotalValue}",
						new Value(s.getStdValue() * (-1)).toString());
				}
				posTicket.parseSubtotal(params);
			} else if (s.getType() == Sale.LOANCARD) {
				params.clear();
				params.put("${DiscDesc}", "挂帐卡");
				params.put("${DiscValue}", s.getName());
				posTicket.parseDiscount(params);
			}

		}

	}

	/**打印支付信息（用于打印销售清单）
	 * @param p支付信息和v整单价值
	 **/
	public void printdisplay(Payment p, SheetValue v) {

		if (posInvoice != null) {
			HashMap params = new HashMap();

			params.put("${PayType}", Payment.getTypeName(p.getType()));
			params.put("${Currency}", p.getCurrenCode());
			params.put("${PayAmount}", (new Value(p.getValue())).toValStr());

			posInvoice.parsePayment(params);

		}

	}

	/**重新打印支付信息（用于重打小票）
	 * @param p支付信息和v整单价值
	 * */
	public void reprintdisplay(Payment p, SheetValue v) {

		if (posTicket != null) {

			if (p.getReason() == Payment.CASHOUT) {

				HashMap params = new HashMap();

				params.put("${DiscDesc}", "出款");
				params.put("${DiscValue}", new Value(-p.getValue()).toString());

				posTicket.parseDiscount(params);

			} else if (p.getReason() == Payment.CASHIN) {

				HashMap params = new HashMap();

				params.put("${DiscDesc}", "入款");
				params.put("${DiscValue}", new Value(p.getValue()).toString());

				posTicket.parseDiscount(params);

			} else if (p.getType() == 'h') {

			} else {

				HashMap params = new HashMap();

				params.put("${PayType}", Payment.getTypeName(p.getType()));
				params.put("${Currency}", p.getCurrenCode());
				params.put(
					"${PayAmount}",
					(new Value(p.getValue())).toValStr());

				posTicket.parsePayment(params);

			}
		}

	}

	/**重新打印挂帐卡卡号和余额
	 * @param pinput 支付信息，从中取卡号和卡余额以供重打显示
	 * */
	public void reprintdisplay(LoanCard4Reprint loancard) {
		//if (p.getType() == Payment.CARDLOAN || p.getType() == Payment.CARDSHOP) {
		HashMap params = new HashMap();

		params.put("${loadCardNo}", loancard.getCardno());
		params.put("${loadCardBalance}", loancard.getResultValue());
		posTicket.parseLoanCard(params);
		//}

	}

	/**显示合计信息
	 * @param v
	 * @return
	 * @deprecated
	 * */
	public void disptotal(SheetValue v) {

		if (mainUI.disptotal(v) == 0) {

			if (posTicket != null) {

				HashMap params = new HashMap();

				params.clear();
				params.put("${SubtotalDesc}", "合计");
				params.put(
					"${SubtotalValue}",
					new Value(v.getValueTotal()).toString());
				posTicket.parseSubtotal(params);

			}
		}
	}

	public void dispMemberCardHeader(MemberCard query) {

		if (mainUI.dispMemberCard(query) == 0) {

			//			if (posTicket != null) {
			//
			//				HashMap params = new HashMap();
			//
			//				String name = "会员卡";
			//				params.clear();
			//				params.put("${DiscDesc}", name);
			//				params.put("${DiscValue}", query.getCardNo());
			//                posTicket.parseDiscount(params);
			//                params.clear();
			//                name = "会员卡级别";
			//				params.put("${DiscDesc}", name);
			//				params.put("${DiscValue}", Integer.toString(query.getMemberLevel()));
			//				posTicket.parseDiscount(params);
			//
			//			}

		}
	}

	public void dispMemberCardHeader(MemberCard4Reprint card4Print) {

		if (posTicket != null && card4Print != null) {
			// 原来屏蔽掉的

			                HashMap params = new HashMap();
			
			                String name = "会员卡";
			                params.clear();
			                params.put("${DiscDesc}", name);
			                params.put("${DiscValue}", card4Print.getCardNo());
			                posTicket.parseDiscount(params);
			                params.clear();
			                name = "会员卡级别";
			                params.put("${DiscDesc}", name);
			                params.put("${DiscValue}", card4Print.getMemberLevel());
			                posTicket.parseDiscount(params);

		}

	}
	public void display(MemberCard memberCard) {
			if (posTicket != null&&memberCard!=null) {
		            	HashMap params = new HashMap();
		              PosConfig config=PosConfig.getInstance();
		              if (config.getString("PRINT_CURPOINT").equals("ON")){
		              	
		              if(config.getString("PRINT_SUMPOINT").equals("ON")){
						String name = "上日累计积分：";
						params.clear();
						params.put("${DiscDesc}", name);
						params.put("${DiscValue}",Formatter.toMoney(memberCard.getTotalPoint().toString()));
		                posTicket.parseDiscount(params);
		              }else{
						String name = "累计积分：";
						params.clear();
						params.put("${DiscDesc}", name);
						params.put("${DiscValue}", Formatter.toMoney( memberCard.getTotalPoint().add(memberCard.getCurrentPoint()).toString()));
		                posTicket.parseDiscount(params);
		              }
		              	params.clear();
		              	String  name = "此单积分：";
						params.put("${DiscDesc}", name);
						params.put("${DiscValue}", Formatter.toMoney(memberCard.getCurrentPoint().toString()));
						posTicket.parseDiscount(params);
		
				}
		}
	}

	public void display(SHCardQueryVO shopCard) {

		if (posTicket != null && shopCard != null) {

			HashMap params = new HashMap();
			String name = "储值卡号：";
			params.clear();
			params.put("${DiscDesc}", name);
			params.put("${DiscValue}", shopCard.getCardNO());
			posTicket.parseDiscount(params);

			params.clear();
			name = "储值卡余额：";
			params.clear();
			params.put("${DiscDesc}", name);
			params.put("${DiscValue}", Formatter.toMoney(shopCard.getDetail()));
			posTicket.parseDiscount(params);

		}

	}
	
	// 原来屏蔽掉的！
	public void display(MemberCard4Reprint memberCard) {

//				if (memberCard!=null) {
		
//					HashMap params = new HashMap();
		
//					params.put("${memberCardTotalPoint}", memberCard.getTotalPoint());
//					params.put("${memberCardCurrentPoint}", memberCard.getCurrentPoint());
		
//					posTicket.parseMemberCard(params);
//		   }

		
		// 实时打印积分
		if (posTicket != null && memberCard != null) {

							HashMap params = new HashMap();
			              PosConfig config=PosConfig.getInstance();
			              if(config.getString("PRINT_SUMPOINT").equals("ON")){
							String name = "上日累计积分：";
			
							params.clear();
							params.put("${DiscDesc}", name);
							params.put("${DiscValue}", Formatter.toMoney(memberCard.getTotalPoint()));
			                posTicket.parseDiscount(params);
			             }else{
						String name = "累计积分：";
			                double totalPoint=Double.parseDouble(memberCard.getTotalPoint());
			                double currentPoint=Double.parseDouble(memberCard.getCurrentPoint());
							params.clear();
							params.put("${DiscDesc}", name);
							params.put("${DiscValue}",Formatter.toMoney(Double.toString(totalPoint+currentPoint)));
			                posTicket.parseDiscount(params);
			             }
			             if(config.getString("PRINT_CURPOINT").equals("ON")){
			                params.clear();
			               String name = "此单积分：";
							params.put("${DiscDesc}", name);
							params.put("${DiscValue}", Formatter.toMoney(memberCard.getCurrentPoint()));
							posTicket.parseDiscount(params);

		}

	}
	}

	
	// 支持舟山商品合计
	public void addDisplay(Sale s, SheetValue v) {

		if (posTicket != null) {

			HashMap params = new HashMap();

			if (s.getType() != Sale.TOTAL
				&& s.getType() != Sale.AlTPRICE
				&& s.getType() != Sale.SINGLEDISC
				&& s.getType() != Sale.TOTALDISC
				&& s.getType() != Sale.MONEYDISC
				&& s.getType() != Sale.AUTODISC
				&& s.getType() != Sale.LOANCARD
				&& s.getType() != Sale.LOANDISC) {
				String prefix = "";
				if (s.getType() == Sale.WITHDRAW) {
					prefix = "退货 ";
				}

				String suffix = "";

				params.put("${GoodsName}", prefix + s.getName() + suffix);
				params.put("${Barcode}", s.getVgno());

				params.put("${Quantity}", s.getQtyStr());

				if (s.getDiscType() == Discount.MEMBERPRICE) {
					params.put(
						"${Price}",
						(new Value(s.getPriceFact())).toValSpecStr());
					params.put(
						"${Amount}",
						(new Value(s.getFactValue())).toValSpecStr());
				} else {
					params.put(
						"${Price}",
						(new Value(s.getStdPrice())).toValSpecStr());
					params.put(
						"${Amount}",
						(new Value(s.getStdValue())).toValSpecStr());
				}

				posTicket.parseSale(params);

				if (s.getType() == Sale.WITHDRAW) {
					params.clear();
					params.put("${DiscDesc}", "");
					PosContext context = PosContext.getInstance();
					if (context.getAuthorizerid() != null
						&& !context.getAuthorizerid().equals("")) {
						params.put(
							"${DiscValue}",
							"退货授权人:" + context.getAuthorizerid());
					} else {
						params.put(
							"${DiscValue}",
							"退货授权人:" + context.getCashierid());
					}
					posTicket.parseDiscount(params);
				}

				int discDelta = v.getDiscDelta();

				if (discDelta != 0
					&& s.getDiscType() != Discount.MEMBERPRICE) {

					params.clear();
					if (s.getDiscType() == Discount.COMPLEX) {
						params.put("${DiscDesc}", s.getFavorName());
					} else {
						params.put(
							"${DiscDesc}",
							(new Discount(s.getDiscType())).getTypeName());
					}
					params.put(
						"${DiscValue}",
						(new Value(discDelta * (-1))).toString());
					posTicket.parseDiscount(params);

				}
			} else if (
				s.getType() == Sale.TOTAL
					|| s.getType() == Sale.AlTPRICE
					|| s.getType() == Sale.SINGLEDISC
					|| s.getType() == Sale.TOTALDISC
					|| s.getType() == Sale.MONEYDISC
					|| s.getType() == Sale.AUTODISC
					|| s.getType() == Sale.LOANCARD
					|| s.getType() == Sale.LOANDISC) {
				params.clear();
				params.put("${SubtotalDesc}", s.getName());
				if (s.getType() == Sale.TOTAL) {
					params.put(
						"${SubtotalValue}",
						new Value(v.getValueTotal()).toString());
				} else {
					params.put(
						"${SubtotalValue}",
						new Value(v.getValueTotal() * (-1)).toString());
				}
				posTicket.parseSubtotal(params);
			} else if (s.getType() == Sale.LOANCARD) {
				params.clear();
				params.put("${DiscDesc}", "挂帐卡");
				params.put("${DiscValue}", s.getName());
				posTicket.parseDiscount(params);
			}

		}

		if (custDisplay != null) {
			custDisplay.printGoods(s);
			custDisplay.printTotal((new Value(v.getValueUnPaid()).toString()));
		}

	}

	public void display(Sale s, SheetValue v) {

		mainUI.display(s);

		if (posTicket != null) {

			HashMap params = new HashMap();

			if (s.getType() != Sale.TOTAL
				&& s.getType() != Sale.AlTPRICE
				&& s.getType() != Sale.SINGLEDISC
				&& s.getType() != Sale.TOTALDISC
				&& s.getType() != Sale.MONEYDISC
				&& s.getType() != Sale.AUTODISC
				&& s.getType() != Sale.LOANCARD
				&& s.getType() != Sale.LOANDISC
				&& s.getType() != Sale.Drug) {
				String prefix = "";
				if (s.getType() == Sale.WITHDRAW) {
					prefix = "退货 ";
				}

				String suffix = "";
				//屏蔽油岛号信息  沧州富达 by fire  2005_5_11 				
				//				if (PosConfig.getInstance().isIndicatorDept(s.getDeptid())) {
				//					if (s.getColorSize() != null
				//						&& !s.getColorSize().equals("")) {
				//						suffix = " 油岛号:" + s.getColorSize();
				//					}
				//				}
				params.put("${GoodsName}", prefix + s.getName() + suffix);
//				params.put("${Barcode}", s.getVgno());
				params.put("${Barcode}", s.getGoods().getBarcode());

				params.put("${Quantity}", s.getQtyStr());

				if (s.getDiscType() == Discount.MEMBERPRICE) {
					params.put(
						"${Price}",
						(new Value(s.getPriceFact())).toValSpecStr());
					params.put(
						"${Amount}",
						(new Value(s.getFactValue())).toValSpecStr());
				} else {
					params.put(
						"${Price}",
						(new Value(s.getStdPrice())).toValSpecStr());
					params.put(
						"${Amount}",
						(new Value(s.getStdValue())).toValSpecStr());
				}
				
				params.put("${Batchno}",s.getBatchno());
				params.put("${Specinfo}",s.getSpec());
				params.put("${manufact}",s.getmanufact());

				posTicket.parseSale(params);

				if (s.getType() == Sale.WITHDRAW) {
					params.clear();
					params.put("${DiscDesc}", "");
					PosContext context = PosContext.getInstance();
					if (context.getAuthorizerid() != null
						&& !context.getAuthorizerid().equals("")) {
						params.put(
							"${DiscValue}",
							"退货授权人:" + context.getAuthorizerid());
					} else {
						params.put(
							"${DiscValue}",
							"退货授权人:" + context.getCashierid());
					}
					posTicket.parseDiscount(params);
				}

				int discDelta = v.getDiscDelta();

				if (discDelta != 0
					&& s.getDiscType() != Discount.MEMBERPRICE) {

					params.clear();
					if (s.getDiscType() == Discount.COMPLEX) {
						params.put("${DiscDesc}", s.getFavorName());
					} else {
						params.put(
							"${DiscDesc}",
							(new Discount(s.getDiscType())).getTypeName());
					}
					params.put(
						"${DiscValue}",
						(new Value(discDelta * (-1))).toString());
					posTicket.parseDiscount(params);

				}
			} else if (
				s.getType() == Sale.TOTAL
					|| s.getType() == Sale.AlTPRICE
					|| s.getType() == Sale.SINGLEDISC
					|| s.getType() == Sale.TOTALDISC
					|| s.getType() == Sale.MONEYDISC
					|| s.getType() == Sale.AUTODISC
					|| s.getType() == Sale.LOANCARD
					|| s.getType() == Sale.LOANDISC
					|| s.getType() == Sale.Drug) {
				params.clear();
				params.put("${SubtotalDesc}", s.getName());
				if (s.getType() == Sale.TOTAL) {
					params.put(
						"${SubtotalValue}",
						new Value(v.getValueTotal()).toString());
				} else {
					params.put(
						"${SubtotalValue}",
						new Value(v.getValueTotal() * (-1)).toString());
				}
				posTicket.parseSubtotal(params);
			} else if (s.getType() == Sale.LOANCARD) {
				params.clear();
				params.put("${DiscDesc}", "挂帐卡");
				params.put("${DiscValue}", s.getName());
				posTicket.parseDiscount(params);
			}

		}//*/

		if (custDisplay != null) {
			custDisplay.printGoods(s);
			custDisplay.printTotal((new Value(v.getValueUnPaid()).toString()));
		}

	}

	public void displayUnhold(Sale s, SheetValue v) {

		mainUI.display(s);

		if (posTicket != null) {

			HashMap params = new HashMap();

			if (s.getType() != Sale.TOTAL
				&& s.getType() != Sale.AlTPRICE
				&& s.getType() != Sale.SINGLEDISC
				&& s.getType() != Sale.TOTALDISC
				&& s.getType() != Sale.MONEYDISC
				&& s.getType() != Sale.AUTODISC
				&& s.getType() != Sale.LOANCARD
				&& s.getType() != Sale.LOANDISC) {
				String prefix = "";

				String suffix = "";
				//屏蔽 油岛号信息 沧州富达 by fire  2005_5_11 				
				//				if (PosConfig.getInstance().isIndicatorDept(s.getDeptid())) {
				//					if (s.getColorSize() != null
				//						&& !s.getColorSize().equals("")) {
				//						suffix = " 油岛号:" + s.getColorSize();
				//					}
				//				}

				params.put("${GoodsName}", prefix + s.getName() + suffix);
				params.put("${Barcode}", s.getVgno());

				params.put("${Quantity}", s.getQtyStr());
				params.put(
					"${Price}",
					(new Value(s.getStdPrice())).toValSpecStr());
				params.put(
					"${Amount}",
					(new Value(s.getStdValue())).toValSpecStr());
				
				params.put("${Batchno}",s.getBatchno());
				

				posTicket.parseSale(params);

			} else if (
				s.getType() == Sale.TOTAL
					|| s.getType() == Sale.AlTPRICE
					|| s.getType() == Sale.SINGLEDISC
					|| s.getType() == Sale.TOTALDISC
					|| s.getType() == Sale.MONEYDISC
					|| s.getType() == Sale.AUTODISC
					|| s.getType() == Sale.LOANDISC) {
				params.clear();
				params.put("${SubtotalDesc}", s.getName());
				if (s.getType() == Sale.TOTAL) {
					params.put(
						"${SubtotalValue}",
						new Value(v.getValueTotal()).toString());
				} else {
					params.put(
						"${SubtotalValue}",
						new Value(s.getStdValue() * (-1)).toString());
				}
				posTicket.parseSubtotal(params);
			} else if (s.getType() == Sale.LOANCARD) {
				params.clear();
				params.put("${DiscDesc}", "挂帐卡");
				params.put("${DiscValue}", s.getName());
				posTicket.parseDiscount(params);
			}

		}

		if (custDisplay != null) {
			custDisplay.printGoods(s);
			custDisplay.printTotal((new Value(v.getValueUnPaid()).toString()));
		}

	}

	public void displayDiscount(Sale s, SheetValue v) {

		mainUI.displayDiscount(s);

		if (posTicket != null) {
			int discDelta = v.getDiscDelta();

			if (discDelta != 0) {

				HashMap params = new HashMap();

				params.clear();
				params.put(
					"${DiscDesc}",
					(new Discount(s.getDiscType())).getTypeName());
				params.put(
					"${DiscValue}",
					(new Value(discDelta * (-1))).toString());
				posTicket.parseDiscount(params);

			}
		}
	}

	public void displayprom(Sale s, SheetValue v) {

		mainUI.displayprom(s);
		/*
		if (posTicket != null) {
			int discDelta = v.getDiscDelta();
		
			if (discDelta != 0) {
		
				HashMap params = new HashMap();
		
				params.clear();
				params.put(
					"${DiscDesc}",
				     s.getFavorName());
				params.put(
					"${DiscValue}",
					(new Value(discDelta * (-1))).toString());
				posTicket.parseDiscount(params);
		
			}
		}
		*/
	}

	public void displayDiscount4correct(Sale s, SheetValue v) {

		mainUI.displayDiscount4correct(s, v);

		if (posTicket != null) {
			int discDelta = v.getDiscDelta();

			if (discDelta != 0) {

				HashMap params = new HashMap();

				params.clear();
				params.put(
					"${DiscDesc}",
					(new Discount(s.getDiscType())).getTypeName());
				params.put(
					"${DiscValue}",
					(new Value(discDelta * (-1))).toString());
				posTicket.parseDiscount(params);

			}
		}
	}

	public void displayTotalDiscount(Sale s, SheetValue v) {

		mainUI.displayDiscount(s);

		if (posTicket != null) {
			int discDelta = v.getDiscTotal();

			if (discDelta != 0) {

				HashMap params = new HashMap();

				params.clear();
				params.put(
					"${DiscDesc}",
					(new Discount(s.getDiscType())).getTypeName());
				params.put(
					"${DiscValue}",
					(new Value(discDelta * (-1))).toString());
				posTicket.parseDiscount(params);

			}
		}
	}

	public void display(Payment p, SheetValue v) {
		mainUI.display(p);

		if (posTicket != null) {
			HashMap params = new HashMap();

			params.put("${PayType}", Payment.getTypeName(p.getType()));
			params.put("${Currency}", p.getCurrenCode());
			params.put("${PayAmount}", (new Value(p.getValue())).toValStr());
			posTicket.parsePayment(params);

		}

		if (custDisplay != null) {
			custDisplay.printPayment(new Value(p.getValue()));
			if (v.getValueUnPaid() > 0) {
				custDisplay.printTotal(new Value(v.getValueUnPaid()));
			}
		}

	}

	public void display(Payment p, PosInputPayment pinput) {

		if (posTicket != null
			&& (p.getType() == Payment.CARDLOAN
				|| p.getType() == Payment.ICCARD)) {

			HashMap params = new HashMap();

			params.put("${loadCardNo}", pinput.getMediaNo());
			params.put("${loadCardBalance}", pinput.getExtraData());

			posTicket.parseLoanCard(params);

		}

	}

	public void display(SheetValue v, Exchange e) {

		String code = e.getCode();
		double rate = e.getRate();

		Value disc_delta = new Value(v.getDiscDelta());

		mainUI.setTotal((new Value(v.getValueTotal()).toString()));
		mainUI.setPaid((new Value(v.getValuePaid()).toString()));
		int rmb_topay = v.getValueToPay();

		int money_topay =
			(code.equals("RMB"))
				? rmb_topay
				: (int) Math.rint((double) rmb_topay / rate);

		if (money_topay == 0 && rmb_topay > 0)
			money_topay = 1;

		if (money_topay <= 0) {
			money_topay = -money_topay;
			mainUI.setUnPaidLabel("  找  赎  ");
		} else {
			mainUI.setUnPaidLabel("  待  收  ");
		}

		if (code.equals("RMB"))
			mainUI.setUnPaid((new Value(money_topay)).toString());
		else
			mainUI.setUnPaid(code + " " + (new Value(money_topay)).toString());

		mainUI.setTotalQty(Integer.toString(v.getQty()));

	}

	public void display(CashBox box) {
		if (posTicket != null) {
			HashMap params = new HashMap();

			params.put("${PayType}", Payment.getTypeName(box.getType()));
			params.put("${Currency}", box.getCurrenCode());
			params.put("${PayAmount}", (new Value(box.getValue())).toValStr());

			posTicket.parsePayment(params);
		}
	}

	public void display(SheetBrief briefs[]) {

		HoldList holdList = new HoldList(briefs);
		holdList.show();

	}

	public void prompt(String s) {
		mainUI.setPrompt(s);
	}

	public void displayHeader(PosContext context) {

		if (posTicket != null) {
			HashMap params = new HashMap();

			String info = PosConfig.getInstance().getString("HD1_INFO");
			params.put("${Header1}", info);
			info = PosConfig.getInstance().getString("HD2_INFO");
			params.put("${Header2}", info);
			params.put("${ShopID}", context.getStoreid());
			params.put("${PosID}", context.getPosid());
			createrandomnum randomnum = new createrandomnum();
			String num = Integer.toString(randomnum.getrandomnum());
			//			params.put(
			//				"${SheetID}",
			//				Integer.toString(context.getSheetid()) + num);
			params.put("${SheetID}", Integer.toString(context.getSheetid()));

			params.put("${Cashier}", context.getCashierid());
			params.put("${Date}", Formatter.getDate(new Date()));

			MemberCard memberCard = pos.core.getPosSheet().getMemberCard();

			if (memberCard != null) {
				params.put("${ShowMemberCard}", "true");
				params.put("${memberCardNo}", memberCard.getCardNo());
			} else {
				params.put("${ShowMemberCard}", "false");
			}

			posTicket.parseHeader(params);

		}
	}

	public void displayTrail(SheetValue sheetValue) {

		if (posTicket != null) {
			PosConfig config = PosConfig.getInstance();

			HashMap params = new HashMap();

			int paid = sheetValue.getValuePaid();
			int topay = sheetValue.getValueToPay();
			int total = sheetValue.getValueTotal();

			params.put("${TotalPayAmount}", (new Value(paid)).toValStr());
			params.put("${Change}", (new Value(topay * -1)).toValStr());
			params.put("${ActualPayAmount}", (new Value(total)).toValStr());

			if (sheetValue.getDiscTotal() > 0) {
				params.put("${ShowDiscountTotal}", "true");
				params.put(
					"${DiscountTotal}",
					(new Value(sheetValue.getDiscTotal())).toValStr());
			} else {
				params.put("${ShowDiscountTotal}", "false");
			}

//			if (config.getString("PRINT_TALCOUNT").equals("ON")){
				params.put("${TotalQty}", Integer.toString(sheetValue.getQty()));
//			}else{
//				params.put("${TotalQty}", "0");
//				}
			params.put("${Trail1}", config.getString("TL1_INFO"));
			params.put("${Trail2}", config.getString("TL2_INFO"));
			params.put("${Trail3}", config.getString("TL3_INFO"));
			posTicket.parseTrail(params);

		}

	}
	
	// 打正价商品价
	public void displaySaleInfo(PosSheet sheet,SheetValue sheetValue){
		if (posTicket != null) {
			int value = 0;
			for (int i = 0; i < sheet.getSaleLen(); i ++){
				if(sheet.getSale(i).getDiscType() == Discount.NONE ||
					sheet.getSale(i).getDiscType() == Discount.MEMBERDISC  ||
					sheet.getSale(i).getDiscType() == Discount.MEMBERPRICE ||
					sheet.getSale(i).getDiscType() == Discount.MEMBERDEPT ){
						value += sheet.getSale(i).getFactValue();
					}
				}
			if (value != sheetValue.getValueTotal()){
			HashMap params = new HashMap();
			
			params.put("${SaleM}",(new Value(value)).toValStr());
			params.put("${SaleN}",(new Value(sheetValue.getValueTotal()-value)).toValStr());
			
			posTicket.parseSaleMoney(params);
			}
		 }
		}
	
	public void displayTrail(PosSheet sheet) {

		if (posTicket != null) {
			PosConfig config = PosConfig.getInstance();
			
			MemberCard card = sheet.getMemberCard();

			HashMap params = new HashMap();
	
			if (card != null) {
             if(config.getString("PRINT_SUMPOINT").equals("ON")){
				String name = "上日累计积分：";

				params.clear();
				params.put("${DiscDesc}", name);
				params.put("${DiscValue}", Formatter.toMoney(Double.toString(card.getTotalPoint().doubleValue())));
             }else{
			String name = "累计积分：";
                double totalPoint=Double.parseDouble(Double.toString(card.getTotalPoint().doubleValue()));
                double currentPoint=Double.parseDouble(Double.toString(card.getCurrentPoint().doubleValue()));
				params.clear();
				params.put("${DiscDesc}", name);
				params.put("${DiscValue}",Formatter.toMoney(Double.toString(totalPoint+currentPoint)));
             }
             if(config.getString("PRINT_CURPOINT").equals("ON")){
                params.clear();
               String name = "此单积分：";
				params.put("${DiscDesc}", name);
				params.put("${DiscValue}",Formatter.toMoney(Double.toString(card.getCurrentPoint().doubleValue())));
				}
             
             posTicket.parseDiscount(params);
			}
		}

	}


	/**打印销售清单尾
	 *
	 * */
	public void printTrail(SheetValue sheetValue, int page, int pages) {

		if (posInvoice != null) {
			PosConfig config = PosConfig.getInstance();

			HashMap params = new HashMap();

			int paid = sheetValue.getValuePaid();
			int topay = sheetValue.getValueToPay();
			int total = sheetValue.getValueTotal();

			params.put("${TotalPayAmount}", (new Value(paid)).toValStr());
			params.put("${Change}", (new Value(topay * -1)).toValStr());
			params.put("${ActualPayAmount}", (new Value(total)).toValStr());

			if (sheetValue.getDiscTotal() > 0) {
				params.put("${ShowDiscountTotal}", "true");
				params.put(
					"${DiscountTotal}",
					(new Value(sheetValue.getDiscTotal())).toValStr());

			} else {
				params.put("${ShowDiscountTotal}", "false");
			}

			DecimalFormat df = new DecimalFormat("00");
			String pageNo = df.format(page + 1);
			String allPage = df.format(pages);
			params.put("${Trail1}", "");
			params.put("${Trail2}", "第" + pageNo + "页/总" + allPage + "页");
			posInvoice.parseTrail(params);
		}

	}

	public void printTrail(int page, int pages) {

		if (posInvoice != null) {

			HashMap params = new HashMap();

			DecimalFormat df = new DecimalFormat("00");
			String pageNo = df.format(page + 1);
			String allPage = df.format(pages);
			params.put("${Trail1}", "");
			params.put("${Trail2}", "第" + pageNo + "页/总" + allPage + "页");
			posInvoice.parseCommnet(params);

		}
	}

	public void reprintdisplayTrail(
		PosSheet posSheet,
		SheetValue sheetValue,
		String tag,
		boolean isShowTotalInfo) {

		if (posTicket != null) {
			PosConfig config = PosConfig.getInstance();

			HashMap params = new HashMap();

			if (isShowTotalInfo) {

				int paid = sheetValue.getValuePaid();
				int topay = sheetValue.getValueToPay();
				int total = sheetValue.getValueTotal();

				params.put("${TotalPayAmount}", (new Value(paid)).toValStr());
				params.put("${Change}", (new Value(topay * -1)).toValStr());
				params.put("${ActualPayAmount}", (new Value(total)).toValStr());

				if (sheetValue.getDiscTotal() > 0) {
					params.put("${ShowDiscountTotal}", "true");
					params.put(
						"${DiscountTotal}",
						(new Value(sheetValue.getDiscTotal())).toValStr());

				} else {
					params.put("${ShowDiscountTotal}", "false");
				}
			}

			params.put("${TotalQty}", Integer.toString(sheetValue.getQty()));

			params.put("${Trail1}", config.getString("TL1_INFO"));
			params.put("${Trail2}", config.getString("TL2_INFO"));
			params.put("${Trail3}", config.getString("TL3_INFO"));

			posTicket.reprintparseTrail(posSheet, params, tag, isShowTotalInfo);
		}

	}

	public void displayCash(String desc, String value) {
		if (posTicket != null) {
			PosConfig config = PosConfig.getInstance();
			HashMap params = new HashMap();

			params.put("${DiscDesc}", desc);
			params.put("${DiscValue}", value);
			params.put("${Trail1}", config.getString("TL1_INFO"));
			params.put("${Trail2}", config.getString("TL2_INFO"));
			params.put("${Trail3}", config.getString("TL3_INFO"));

			posTicket.parseDiscount(params);

			posTicket.parseButtom(params);

		}
	}

	public void displayWelcome() {
		if (custDisplay != null) {
			custDisplay.welcome();
		}
	}

	public void displayPrinterStatus() {

		if (mainUI != null) {
			mainUI.setPrinterStatus(POSPrinter.getInstance().isEnable());
		}

	}

	public void displayChange(SheetValue v) {
		if (custDisplay != null) {
			custDisplay.printReturn(
				((new Value(v.getValueToPay() * -1).toString())));
		}
	}

	public void print(String s) {
		if (posTicket != null) {
			posTicket.println(s);
		}
	}

	public void printWithoutSeperator(String s) {
		if (posTicket != null) {
			posTicket.printlnWithoutSeperator(s);
		}
	}

	public void cutPaper() {
		if (posTicket != null) {
			posTicket.cutPaper();
		}
	}

	public void printFeed(int line) {
		if (printer != null) {
			printer.feed(line);
		}
	}

	public int getFeedLines() {
		if (posTicket != null) {
			return posTicket.getFeedLines();
		}
		return 2;
	}

	private static PosDevOut out = null;
	private static IPrinter printer;
	private static POSPrinter1 printer1;
	/**
	 * @param waiter_show
	 */

}
