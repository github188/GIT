package com.royalstone.pos.core;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.*;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.royalstone.pos.card.MemberCard;
import com.royalstone.pos.card.LoanCardDisc;
import com.royalstone.pos.card.LoanCardProcess;
import com.royalstone.pos.card.ICCardProcess;
import com.royalstone.pos.common.*;
import com.royalstone.pos.complex.DiscComplex;
import com.royalstone.pos.complex.DiscComplexList;
import com.royalstone.pos.data.BatchnoData;
import com.royalstone.pos.data.PosPriceData;
import com.royalstone.pos.favor.BulkFavor;
import com.royalstone.pos.favor.BulkPriceList;
import com.royalstone.pos.favor.DiscCriteria;
import com.royalstone.pos.favor.DiscPrice;
import com.royalstone.pos.favor.DiscRate;
import com.royalstone.pos.favor.Discount;
import com.royalstone.pos.favor.DiscountList;
import com.royalstone.pos.invoke.realtime.RealTimeException;
import com.royalstone.pos.io.PosDevOut;
import com.royalstone.pos.io.PosInputGoods;
import com.royalstone.pos.io.PosInputPayment;
import com.royalstone.pos.journal.JournalLog;
import com.royalstone.pos.journal.JournalLogList;
import com.royalstone.pos.journal.JournalManager;
import com.royalstone.pos.journal.JournalWriter;
import com.royalstone.pos.journal.LogManager;
import com.royalstone.pos.journal.LogWriter;
import com.royalstone.pos.notify.GoodsUpdate;
import com.royalstone.pos.notify.UnitOfWork;
import com.royalstone.pos.util.Exchange;
import com.royalstone.pos.util.ExchangeList;
import com.royalstone.pos.util.FileUtil;
import com.royalstone.pos.util.Formatter;
import com.royalstone.pos.util.InvalidDataException;
import com.royalstone.pos.util.PosConfig;
import com.royalstone.pos.util.getCardType;
import com.royalstone.pos.shell.pos;
import com.royalstone.pos.gui.BatchnoUI;
import com.royalstone.pos.gui.DispWaiter;

/**
 * POS 系统分为多个层次: shell, core, IO...
 * PosCore 实现POS的core
   @version 1.0 2004.05.14
   @author  Mengluoyi, Royalstone Co., Ltd.
 */

public class PosCore {

	/**
	 * 初始化PosCore的数据结构: exch_lst, goods_lst, goodsext_lst, sheet_lst...
	 *
	 */ 
	public PosCore() {
		exch_lst = new ExchangeList();
		goods_lst = new GoodsList();
		goodsext_lst = new GoodsExtList();
		favor_lst = new DiscComplexList();
		discount_lst = new DiscountList();
		bulk_lst = new BulkPriceList();
		sheet = new PosSheet();
		context = PosContext.getInstance();
        accurateList=new AccurateList();
        goodsCombList=new GoodsCombList();
        goodsCutList=new GoodsCutList();
        payModeList = new PayModeList();
		int m = PosConfig.getInstance().getInteger("HANGMAX");
		System.out.println("Max Held sheet: " + m);
		if (m < 1 || m > 100)
			m = 10;
		MAX_SHEETS = m + 1;
		sheet_lst = new String[MAX_SHEETS];
		for (int i = 0; i < MAX_SHEETS; i++)
			sheet_lst[i] = "work" + File.separator + "sheet#" + i;
		last_sold = null;
	}

	/**
	 * @throws InvalidDataException
	 */
	public void init() throws InvalidDataException {

		context = PosContext.getInstance();

		System.err.println("Init Exchange List ...");
		exch_lst.fromXML("pos.xml");

		System.err.println("Set Currency rate for RMB");
		context.setCurrency("RMB", 1.0);
        String isFast=PosConfig.getInstance().getString("ISFASTLOAD");
        String ifSupportOffLine=PosConfig.getInstance().getString("IFSUPPORTOFFLINE");
       //--------------
//        if("ON".equals(isFast)){
//		 System.err.println("Init Price List ...");
//            if("ON".equals(ifSupportOffLine)){
//		        goods_lst.fromXMLFile("price.xml");
//            }
//        }
	   //-----------------------
        payModeList.fromXML("promo/paymode.xml");
        if("ON".equals(ifSupportOffLine)){
        System.err.println("Init offline data List ...");
		//goodsext_lst.fromXMLFile("promo/priceExt.xml");
		discount_lst.load("promo/discount.lst");
        //accurateList.fromXMLFile("promo/accurate.xml");
        goodsCombList.fromXMLFile("promo/pricecomb.xml");
        goodsCutList.fromXMLFile("promo/pricecut.xml");
        //System.err.println("Init Favor List ...");
		//favor_lst.load("promo/favor.lst");
        //System.err.println("Init Bulk List ...");
		//bulk_lst.load("promo/bulkprice.lst");
        File priceFile=new File("price_offline");
            String[] fileList=priceFile.list();
        if(fileList!=null){
             for(int i=0;i<fileList.length;i++){
               goods_lst.fromXMLFile("price_offline/"+fileList[i]);
             }
        }
        File priceExtFile=new File("priceExt_offline");
            String[] fileExtList=priceExtFile.list();
        if(fileExtList!=null){
             for(int i=0;i<fileExtList.length;i++){
               goodsext_lst.fromXMLFile("priceExt_offline/"+fileExtList[i]);
             }
        }

        System.err.println("finish offline data List ...");
        }
		createSheetFile();
		loadSheet(sheetFile());
		setHeldCount();

		last_sold = null;

		if (new File(FILE4BASKET).exists()) {
			try {
				cashbasket = CashBasket.load(FILE4BASKET);
			} catch (Exception ex) {
				ex.printStackTrace();

				FileUtil.fileError(FILE4BASKET);
				cashbasket = new CashBasket();

			}

		} else {
			cashbasket = new CashBasket();
		}

		cashbasket.setExchange(exch_lst);
		PosConfig config = PosConfig.getNewInstance();
		int cash_limit = config.getInteger("CASH_LIMIT");
		if (cash_limit < 1)
			cash_limit = 5000;
		cashbasket.setCashLimit(cash_limit * 100);
	}

	public void goodsUpdate(ArrayList goodsUpdateList)
		throws RealTimeException {

		UnitOfWork.getInstance().begin();

		for (int i = 0; i < goodsUpdateList.size(); i++) {

			GoodsUpdate goodsUpdate = (GoodsUpdate) goodsUpdateList.get(i);
			String goodsNo = goodsUpdate.getGoodsNo();

			goods_lst.update(goodsNo, goodsUpdate.getGoods());
			goodsext_lst.update(goodsNo, goodsUpdate.getGoodsExtList());
			discount_lst.update(goodsNo, goodsUpdate.getDiscCriteria());
			favor_lst.update(goodsNo, goodsUpdate.getComplexList());
			bulk_lst.update(goodsNo, goodsUpdate.getBulkPrice());
		}

		UnitOfWork.getInstance().commit(
			goods_lst,
			goodsext_lst,
			discount_lst,
			favor_lst,
			bulk_lst);

	}

	/** POS前台程序退出前的工作:删除工作文件(sheet#1, sheet#2, sheet#3, ...).
	 *
	 */
	public void exit() {
		for (int i = 0; i < MAX_SHEETS; i++) {
			(new File(sheet_lst[i])).delete();
		}
	}

	/**
	 * @param input
	 * @param baseprice		基本码的单价.
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws RealTimeException
	 */
	public Sale sell(
		PosInputGoods input,
		int baseprice,
		MemberCard memberCard)
		throws FileNotFoundException, IOException, RealTimeException {
		//MemberCard membercard;
        PosConfig config = PosConfig.getInstance();
		String  ifInputWaiter=config.getString("IF_INPUT_WAITER");
        if(sheet.getSaleLen()==0&&"ON".equals(ifInputWaiter))
            if(!showWaiter()){
                context.setWarning("必需输入营业员,按清除键继续!");
                return null;
            }

		int qty_top = config.getInteger("MAXAMOUNT");
		int value_top = config.getInteger("MAXCASH") * 100;
		int sheetlen_top = config.getInteger("MAXITEM");
		int value_top_line = config.getInteger("MAXVALUE") * 100;
		long value_cents = input.getCents();
		int qty = input.getQty();
		String colorsize = input.getColorSize();
		Sale sale_rec;

		/*
		if (sheet.getValue().getValueTotal() > value_top) {
		context.setWarning("售出商品金额已超过上限,请及时结算,按清除键继续!");
		return null;
		}
		 */
		if (sheet.getSaleLen() >= sheetlen_top) {
			context.setWarning("此单已超长,请及时结算,按清除键继续!");
			return null;
		}

		try {
			// Look for goods info in goods_lst by scanned code.
			if(input.getCode().length()<6){
				StringBuffer buf=new StringBuffer();
				
				for(int i=6;i>input.getCode().length();i--){
					buf.append("0");	
				}
				
				buf.append(input.getCode());
				input.setCode(buf.toString());
				
			}
			PosPriceData codenew = new PosPriceData();
			codenew.setSaleCode(input.getCode());
			Goods g = goods_lst.find(codenew);
			//查找削价商品
           // if (g == null&&PosContext.getInstance().isOnLine()) {
             if (g == null) {
				Goods goodsCut = goodsCutList.findCut(input.getCode());
              // GoodsCut goodsCut= RealTime.getInstance().findGoodsCut(input.getCode());
				if (goodsCut != null)
					g = goodsCut;
            }
            //增加捆绑商品
            if (g == null) {
				//Goods goodsCut = goodsCutList.find(input.getCode());
               Goods goodsComb= goodsCombList.find(input.getCode());
				if (goodsComb != null)
					g = goodsComb;
            }
            if (baseprice != 0) {
				g.setPrice(baseprice);
			}
			if (g == null) {
				GoodsExt goodsExt = goodsext_lst.find(input.getCode());
				if (goodsExt != null) {
					g = goodsExt;
					qty = input.getQty() * goodsExt.getPknum();
				}
			}

			if (g == null) {
				context.setWarning("价格表中无此商品,按清除键继续!");
				return null;
			}
			
			if(g.getX()>1){
				qty=(int)Math.rint(input.getAmountEx()*g.getX());
			}

			if (qty > qty_top * g.getX()) {
				context.setWarning("商品数量过大,按清除键继续!");
				return null;
			}

			long v =
				(long) Math.round(
					(long) g.getPrice() * (long) qty * 100 / (long) g.getX()
						+ 50)
					/ 100;

			if (v < 0 || v > value_top_line) {
				context.setWarning("此笔商品金额过大,按清除键继续!");
				return null;
			}

			if (v + sheet.getValue().getValueTotal() > value_top) {
				context.setWarning("售出商品金额已超过上限,请及时结算,按清除键继续!");
				return null;
			}

			if (g.getX() == 1
				&& g.getDeptid() != null
//				&& !g.getDeptid().equals("040201")
				&& (input.getAmountEx()-input.getQty())>0) {
				context.setWarning("该商品数量必须为整数,按清除键继续!");
				return null;
			}

			if (qty <= 0) {
				context.setWarning("商品数量不得为零,按清除键继续!");
				return null;
			}

			if(sheet.getSaleLen()==0 && sheet.getMemberCard()==null){
				PosDevOut.getInstance().displayHeader(context);
			}
			
			/*药品限购*/
		    PosPriceData code = new PosPriceData();
		    code.setSaleCode(g.getVgno());
		    code.setShopid(context.getStoreid());
		    code.setFlag(1);
		    code.setSaleAmount(getcodeamount(g.getVgno())+qty/g.getX());
		    String msg1 = null;
		    msg1 = findGoods(code).getName().trim();
			if(msg1.equals("9"))
			  {	
			    	context.setWarning("此商品为含麻黄碱类复方制剂,予与登记身份证,限购2个最小包装!");
			    	return null;
			  }else 
			  {
					if(msg1.equals("1"))
					  {	
						    JOptionPane.showMessageDialog(null,"此商品为含麻黄碱类复方制剂,予于登记身份证号码,限购2个最小包装!");
					  }
			  }
			/*GSP限购*/
			ArrayList batch = findBatchno(code);
			if(batch.size()==1)
			{
				BatchnoData  batchdata1 = (BatchnoData)batch.get(0);
				g.setBatchno(batchdata1.getBatchon().trim());
			}
			if(batch.size()>=1)
			{
			BatchnoUI ui = new BatchnoUI(batch);
			ui.show();
			if (ui.isConfrim())
			{
				AbstractTableModel theTableModel =
					ui.getTheTableModel();
				JTable theTable = ui.getTheTable();
				theTable.getSelectedRow();
				BatchnoData  batchdata = (BatchnoData)batch.get(theTable.getSelectedRow());
				g.setBatchno(batchdata.getBatchon().trim());
				
				if(batchdata.getFlag()==1)
				{
				//	JOptionPane.showMessageDialog(null,"该批次商品近效期["+g.getBatchno()+"]");
					if(batchdata.getSaleflag()==1)
					{
						JOptionPane.showMessageDialog(null,"该批次商品近效期["+g.getBatchno()+"]");
					}else
					{
				    	context.setWarning("该批次商品近效期,不允许销售!!");
				    	return null;
					}
				}
			}else
			{
			  	context.setWarning("请选择批次号，退出!");
		    	return null;
			}
			}
			if(g.getCflag()==1)
			{
				JOptionPane.showMessageDialog(null,"处方药请凭处方销售["+g.getName()+"]");
			}
			
			clearDiscount(g);

			Sale sale;
			sale = new Sale(g, qty, Sale.SALE);


			sale.setOriginalCode(input.getOrgCode());
			sale.setWaiter(context.getWaiterid());
			sale.setAuthorizer(context.getAuthorizerid());
			sale.setPlaceno(context.getPlaceno());
			sale.setColorSize(input.getColorSize());
			if (input.getGoodsType() == Goods.WEIGHT_VALUE) {

				if (sale.getDiscValue() == 0) {
					sale.setStdValue(input.getCents());
					sale.setFactValue(input.getCents());
				} else {
					sale.setFactValue(input.getCents());
					sale.setStdValue(input.getCents()+sale.getDiscValue());
				}


			}

			//if(input.getGoodsType() == Goods.WEIGHT){ 处理地磅交易，不计算折扣
			if (input.getDeptid() != null
				&& input.getDeptid().equals(Goods.LOADOMETER)) {
				sale.setFactValue(input.getCents());
				sale.setStdValue(input.getCents());
			}

			sale_rec = sheet.sell(sale);

		//处理促销－－－－－－－－－－－－－－－
				performDiscount(g);


			sheet.updateValue();

			dump();

			last_sold = sale_rec;
			return sale_rec;

		} catch (RealTimeException ex) {
			ex.printStackTrace();
			loadSheet(sheetFile());
			throw ex;
		}
	}

	public boolean checkLoanCardCanConsume(ArrayList deptList, Goods g) {

		DecimalFormat df = new DecimalFormat("000000");
		for (int i = 0; i < deptList.size(); i++) {
			deptList.set(
				i,
				df.format(Integer.parseInt((String) deptList.get(i))));
		}

		String strDepid = g.getDeptid();
		String strBigGroup = "";
		String strMidGroup = "";

		if (strDepid != null && strDepid.length() == 6) {
			strBigGroup = df.format(Integer.parseInt(strDepid.substring(0, 2)));
			strMidGroup = df.format(Integer.parseInt(strDepid.substring(1, 4)));
		}

		if (deptList.contains("000000")) {
			return true;
		}

		if (!deptList.contains(strBigGroup)) {
			if (!deptList.contains(strMidGroup)) {
				if (!deptList.contains(g.getDeptid())) {
					return false;
				}
			}
		}

		return true;

	}

	public void performLoanDisc(ArrayList discs, Sale sale) {

		DecimalFormat df = new DecimalFormat("000000");

		String strDepid = sale.getDeptid();
		String strBigGroup = "";
		String strMidGroup = "";

		if (strDepid != null && strDepid.length() == 6) {
			strBigGroup = df.format(Integer.parseInt(strDepid.substring(0, 2)));
			strMidGroup = df.format(Integer.parseInt(strDepid.substring(1, 4)));
		}

		for (int i = 0; i < discs.size(); i++) {

			LoanCardDisc disc = (LoanCardDisc) discs.get(i);

			if ((disc.getItemType() == LoanCardDisc.SINGLE_ITEMTYPE
				&& df.format(disc.getItemID()).equals(sale.getGoods().getVgno()))
				|| (disc.getItemType() == LoanCardDisc.SMALLDEPT_ITEMTYPE
					&& df.format(disc.getItemID()).equals(strDepid))
				|| (disc.getItemType() == LoanCardDisc.MIDDEPT_ITEMTYPE
					&& df.format(disc.getItemID()).equals(strMidGroup))
				|| (disc.getItemType() == LoanCardDisc.BIGDEPT_ITEMTYPE
					&& df.format(disc.getItemID()).equals(strBigGroup))
				|| (df.format(disc.getItemID()).equals("000000"))) {

				switch (disc.getDiscType()) {
					case LoanCardDisc.DISC_PRICE :
						long newPrice =
							sale.getPrice()
								- (disc
									.getDiscCount()
									.multiply(new BigDecimal(100))
									.longValue());
						DiscPrice discPrice =
							new DiscPrice(Discount.LOANDISC, newPrice);
						sale.setDiscount(discPrice);
						break;

					case LoanCardDisc.DISC_RATE :
						DiscRate discRate =
							new DiscRate(
								Discount.LOANDISC,
								disc.getDiscCount().intValue());
						sale.setDiscount(discRate);
						break;
				}

				break;

			}

		}

	}

	/**
	 * @param g
	 * @return
	 * @throws RealTimeException
	 */
	private ArrayList getMatchDiscComplex(Goods g) throws RealTimeException {
		return sheet.getMatchDiscComplex(favor_lst, g);
	}

	/**
	 * @param input
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws RealTimeException
	 */
	public Sale findprice(PosInputGoods input)
		throws FileNotFoundException, IOException, RealTimeException {
		PosConfig config = PosConfig.getInstance();
		int qty_top = config.getInteger("MAXAMOUNT");
		int value_top = config.getInteger("MAXCASH") * 100;
		int sheetlen_top = config.getInteger("MAXITEM");
		long value_cents = input.getCents();
		int qty = input.getQty();
		String colorsize = input.getColorSize();
		Sale sale_rec;

		// Look for goods info in goods_lst by scanned code.
		try {
			PosPriceData codenew = new PosPriceData();
			codenew.setSaleCode(input.getCode());
			Goods g = goods_lst.find(codenew);
			if (g == null) {
				GoodsExt goodsExt = goodsext_lst.find(input.getCode());
				if (goodsExt != null) {
					g = goodsExt;
					qty = input.getQty() * goodsExt.getPknum();
				}
			}

			if (g == null) {
				context.setWarning("价格表中无此商品,按清除键继续!");
				return null;
			}

			if (qty > qty_top * g.getX()) {
				context.setWarning("商品数量过大,按清除键继续!");
				return null;
			}

			int v = g.getPrice() * qty / g.getX();

			if (v < 0 || v > value_top) {
				context.setWarning("商品金额过大,按清除键继续!");
				return null;
			}

			if (g.getX() == 1
				&& input.getQty() * 1000 != input.getMilliVolume()) {
				context.setWarning("该商品数量必须为整数,按清除键继续!");
				return null;
			}

			if (qty <= 0) {
				context.setWarning("商品数量不得为零,按清除键继续!");
				return null;
			}

			Sale sale = new Sale(g, qty, Sale.SALE);
			sale.setOriginalCode(input.getOrgCode());
			sale.setWaiter(context.getWaiterid());
			sale.setAuthorizer(context.getAuthorizerid());
			sale.setPlaceno(context.getPlaceno());
			sale.setColorSize(input.getColorSize());
			if (input.getGoodsType() == Goods.WEIGHT_VALUE) {
				sale.setStdValue(input.getCents());
				sale.setFactValue(input.getCents());
			}
			sale_rec = sheet.sell(sale);

			clearDiscount(g);
			performDiscount(g);

			sheet.updateValue();

			return sale_rec;
		} catch (RealTimeException ex) {
			ex.printStackTrace();
			loadSheet(sheetFile());
			throw ex;
		}
	}

	/**
	 * @param disc_type 折扣类型
	 * @param name  商品名称或者挂帐卡卡号
	 * @param value 销售价值
	 */
	public void sell(int disc_type, String name, int value) {
		Goods g = new Goods(name);
		Sale sale = new Sale(disc_type, g, value);
		//System.out.println("NEW 出来的SALE type 为："+ (char) disc_type );
		sheet.falsesell(sale);
	}

	/**
	 * @return Sale 销售记录
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws RealTimeException
	 */
	public Sale quick_correct()
		throws FileNotFoundException, IOException, RealTimeException {

		// 此处不宜直接调用 posFrame 方法,应作修改.
		int qcorrectrow = pos.posFrame.quickcorrectrow();
	    //TODO 获取单据最后一行的序号
//		int lastrow=pos.posFrame.getLastrow();
//		System.out.println("qcorrectrow:"+qcorrectrow+"...lastrow:"+lastrow);
//		if(qcorrectrow!=lastrow ){
//            context.setWarning("无效即更,只能使用更正功能,按清除键继续!");
//			return null;
//		}
		System.out.println("号码:qcorrect" + qcorrectrow);
		last_sold = sheet.getSale(qcorrectrow);

		System.out.println(last_sold.getquickcorrect());
		if (last_sold.getquickcorrect() != 'y'
			&& (last_sold.getType() == 's' || last_sold.getType() == 'r')) {
			pos.posFrame.quickcorrectchangerow();
			last_sold.setquickcorrect();
		} else if (
			last_sold.getquickcorrect() == 'y'
				&& (last_sold.getType() == 's' || last_sold.getType() == 'r')) {
			//writelog("即更", "1", 0);
			//context.setWarning("该商品已进行即更,按清除键继续!");
			writelog("更即", "1", 0);
			context.setWarning("该商品已进行更正,按清除键继续!");
			return null;
		} else {
			//writelog("即更", "1", 0);
			writelog("更即", "1", 0);
			context.setWarning("无效操作,按清除键继续!");
			return null;
		}

		if (last_sold == null) {
			//writelog("即更", "1", 0);
			writelog("更即", "1", 0);
			context.setWarning("无效操作,按清除键继续!");
			return null;
		}

		int sold = sheet.getSoldQty(last_sold.getGoods().getVgno());
		if (sold < last_sold.getQty()) {
			//writelog("即更", "1", 0);
			writelog("更即", "1", 0);
			context.setWarning("无效操作,按清除键继续!");
			return null;
		}

		Goods g = last_sold.getGoods();
		if (last_sold.getType() != Sale.WITHDRAW) {
			clearDiscount(g);
		}
		Sale sale = new Sale(g, -last_sold.getQty(), Sale.QUICKCORRECT);
		sale.setOriginalCode(last_sold.getOrgCode());
		sale.setColorSize(last_sold.getColorSize());
		sale.setPlaceno(context.getPlaceno());
		sale.setAuthorizer(context.getAuthorizerid());
        sale.setWaiter(context.getWaiterid());
		sale.setStdValue(-last_sold.getStdValue());
		sale.setFactValue(-last_sold.getFactValue());

		if (last_sold.getDiscType() == Discount.ALTPRICE) {
			sale.setDiscValue(
				new DiscPrice(
					Discount.ALTPRICE,
					- (last_sold.getStdValue() - last_sold.getDiscValue())));
		}
		if (last_sold.getDiscType() == Discount.SINGLE) {
			sale.setDiscValue(
				new DiscPrice(
					Discount.SINGLE,
					- (last_sold.getStdValue() - last_sold.getDiscValue())));
		}
		if (last_sold.getDiscType() == Discount.TOTAL) {
			int percent = 0;
			percent =
				(int) Math.rint(
					100
						- last_sold.getFactValue()
							* 100.0
							/ last_sold.getStdValue());
			DiscRate discrate = new DiscRate(Discount.TOTAL, percent);
			sale.setDiscount(discrate);
		}
		if (last_sold.getDiscType() == Discount.MONEY) {
			long itemvalue = 0;
			itemvalue = last_sold.getFactValue();
			DiscPrice discprice = new DiscPrice(Discount.MONEY, -itemvalue);
			sale.setDiscValue(discprice);
		}
		if (last_sold.getDiscType() == Discount.LOANDISC) {
			sale.setDiscValue(
				new DiscPrice(
					Discount.LOANDISC,
					- (last_sold.getStdValue() - last_sold.getDiscValue())));
		}
		// zhouzhou add 20070306 会员折扣
//		if (last_sold.getDiscType() == Discount.MEMBERDISC){
//			sale.setDiscValue(
//					new DiscPrice(
//						Discount.MEMBERDISC,
//						- (last_sold.getStdValue() - last_sold.getDiscValue())));
//			}

		sale.setquickcorrect();
		Sale s = sheet.correct(sale);
		if (last_sold.getType() != Sale.WITHDRAW) {
			performDiscount(g);
		}

		sheet.updateValue();
		dump();

		/* add by lichao 8/24/2004*/
		try {

			int value = (int) sale.getDiscValue();
			int value1 = sheet.getValue().getDiscDelta();
			if (value1 != 0) {
				if (last_sold.getDiscType() == Discount.ALTPRICE) {
					String name = "变价";
					sell(Sale.AlTPRICE, name, value);
				} else if (last_sold.getDiscType() == Discount.SINGLE) {
					String name = "单项折扣";
					sell(Sale.SINGLEDISC, name, value);
				} else if (last_sold.getDiscType() == Discount.TOTAL) {
					String name = "总额折扣";
					sell(Sale.TOTALDISC, name, value);
				} else if (last_sold.getDiscType() == Discount.MONEY) {
					String name = "金额折扣";
					sell(Sale.MONEYDISC, name, value);
				} else if (last_sold.getDiscType() == Discount.LOANDISC) {
					String name = "挂帐卡折扣";
					sell(Sale.LOANDISC, name, value);
				} else {
					String name = new Discount(s.getDiscType()).getTypeName();
					sell(Sale.AUTODISC, name, value1);
				}
			}

			dump();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*end*/

		//writelog("即更", "0", 0);
		writelog("即更", "1", 0);
		last_sold = null;
		return s;
	}
   /**
    * 即更功能
    * @return
    * @throws FileNotFoundException
    * @throws IOException
    * @throws RealTimeException
    */
	public Sale correct()
	throws FileNotFoundException, IOException, RealTimeException {

	// 此处不宜直接调用 posFrame 方法,应作修改.
	int qcorrectrow = pos.posFrame.quickcorrectrow();
	System.out.println("号码:qcorrect" + qcorrectrow);
	last_sold = sheet.getSale(qcorrectrow);

	System.out.println(last_sold.getquickcorrect());
	if (last_sold.getquickcorrect() != 'y'
		&& (last_sold.getType() == 's' || last_sold.getType() == 'r')) {
		pos.posFrame.quickcorrectchangerow();
		last_sold.setquickcorrect();
	} else if (
		last_sold.getquickcorrect() == 'y'
			&& (last_sold.getType() == 's' || last_sold.getType() == 'r')) {
		//writelog("即更", "1", 0);
		//context.setWarning("该商品已进行即更,按清除键继续!");
		writelog("更正", "1", 0);
		context.setWarning("该商品已进行更正,按清除键继续!");
		return null;
	} else {
		//writelog("即更", "1", 0);
		writelog("更正", "1", 0);
		context.setWarning("无效操作,按清除键继续!");
		return null;
	}

	if (last_sold == null) {
		//writelog("即更", "1", 0);
		writelog("更正", "1", 0);
		context.setWarning("无效操作,按清除键继续!");
		return null;
	}

	int sold = sheet.getSoldQty(last_sold.getGoods().getVgno());
	if (sold < last_sold.getQty()) {
		//writelog("即更", "1", 0);
		writelog("更正", "1", 0);
		context.setWarning("无效操作,按清除键继续!");
		return null;
	}

	Goods g = last_sold.getGoods();
	if (last_sold.getType() != Sale.WITHDRAW) {
		clearDiscount(g);
	}
	Sale sale = new Sale(g, -last_sold.getQty(), Sale.QUICKCORRECT);
	sale.setOriginalCode(last_sold.getOrgCode());
	sale.setColorSize(last_sold.getColorSize());
	sale.setPlaceno(context.getPlaceno());
	sale.setAuthorizer(context.getAuthorizerid());
    sale.setWaiter(context.getWaiterid());
	sale.setStdValue(-last_sold.getStdValue());
	sale.setFactValue(-last_sold.getFactValue());

	if (last_sold.getDiscType() == Discount.ALTPRICE) {
		sale.setDiscValue(
			new DiscPrice(
				Discount.ALTPRICE,
				- (last_sold.getStdValue() - last_sold.getDiscValue())));
	}
	if (last_sold.getDiscType() == Discount.SINGLE) {
		sale.setDiscValue(
			new DiscPrice(
				Discount.SINGLE,
				- (last_sold.getStdValue() - last_sold.getDiscValue())));
	}
	if (last_sold.getDiscType() == Discount.TOTAL) {
		int percent = 0;
		percent =
			(int) Math.rint(
				100
					- last_sold.getFactValue()
						* 100.0
						/ last_sold.getStdValue());
		DiscRate discrate = new DiscRate(Discount.TOTAL, percent);
		sale.setDiscount(discrate);
	}
	if (last_sold.getDiscType() == Discount.MONEY) {
		long itemvalue = 0;
		itemvalue = last_sold.getFactValue();
		DiscPrice discprice = new DiscPrice(Discount.MONEY, -itemvalue);
		sale.setDiscValue(discprice);
	}
	if (last_sold.getDiscType() == Discount.LOANDISC) {
		sale.setDiscValue(
			new DiscPrice(
				Discount.LOANDISC,
				- (last_sold.getStdValue() - last_sold.getDiscValue())));
	}

	sale.setquickcorrect();
	Sale s = sheet.correct(sale);
	if (last_sold.getType() != Sale.WITHDRAW) {
		performDiscount(g);
	}

	sheet.updateValue();
	dump();

	/* add by lichao 8/24/2004*/
	try {

		int value = (int) sale.getDiscValue();
		int value1 = sheet.getValue().getDiscDelta();
		if (value1 != 0) {
			if (last_sold.getDiscType() == Discount.ALTPRICE) {
				String name = "变价";
				sell(Sale.AlTPRICE, name, value);
			} else if (last_sold.getDiscType() == Discount.SINGLE) {
				String name = "单项折扣";
				sell(Sale.SINGLEDISC, name, value);
			} else if (last_sold.getDiscType() == Discount.TOTAL) {
				String name = "总额折扣";
				sell(Sale.TOTALDISC, name, value);
			} else if (last_sold.getDiscType() == Discount.MONEY) {
				String name = "金额折扣";
				sell(Sale.MONEYDISC, name, value);
			} else if (last_sold.getDiscType() == Discount.LOANDISC) {
				String name = "挂帐卡折扣";
				sell(Sale.LOANDISC, name, value);
			} else {
				String name = new Discount(s.getDiscType()).getTypeName();
				sell(Sale.AUTODISC, name, value1);
			}
		}

		dump();
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	/*end*/

	//writelog("即更", "0", 0);
	writelog("更正", "1", 0);
	last_sold = null;
	return s;
}

	/** 清除折扣数据. 为了支持多种折扣,POS中折扣算法十分复杂. 在"售出商品清单"有变化时,需要先把顾客已享受的折扣清零,再重新计算应享受的折扣.
	 * @param g
	 * @throws RealTimeException
	 */
	private void clearDiscount(Goods g) throws RealTimeException {

		if (g.getPType().equals(DiscCriteria.DISCCOMPLEX)) {
			SaleList saleList = sheet.getSalelst();
			for (int i = 0; i < saleList.size(); i++) {
				if (saleList.get(i).getType() != Sale.WITHDRAW
					&& saleList.get(i).getGoods().getPType().equals(
						DiscCriteria.DISCCOMPLEX)) {
					saleList.get(i).clearFavor();
				}
			}
		} else {
			sheet.clearDiscount(g);
		}
	}

	/**
	 * @param g
	 * @throws RealTimeException
	 */
	private void performDiscount(Goods g) throws RealTimeException {

		if (g.getPType().equals(DiscCriteria.BULKPRICE)) {
			sheet.clearDiscount(g);
			int qty_favored = 0;
			int qty_goods = sheet.getSoldQty(g);
			for (int i = 6; i >= 1 && qty_goods > 0; i--) {
				qty_goods -= sheet.getQtyDisc(g);
				BulkFavor favor_total = bulk_lst.getBulkFavor(g, qty_goods);
				sheet.consumeBulkFavorDesc(g, favor_total);
			}
		} else if (g.getPType().equals(DiscCriteria.DISCCOMPLEX)) {
			ArrayList complexGoods = getComplexGoods();
			ArrayList discList = new ArrayList();

			for (int i = 0; i < complexGoods.size(); i++) {
				discList.addAll(
					getMatchDiscComplex((Goods) complexGoods.get(i)));
			}

			Object[] discAry = orderComplexByLevel(splitDiscComplex(filterDiscComplex(discList)));

//			Object[] discAry = orderComplexByLevel(filterDiscComplex(discList));

//			if (discAry != null && discAry.length > 0) {
//				DiscComplex disc = (DiscComplex) discAry[0];
//				disc.computeFavor(sheet.getSalelst());
//				consumeFavor(disc);

				for (int i = 0; i < discAry.length; i++) {
					DiscComplex disc = (DiscComplex) discAry[i];
					disc.computeFavorAfter(sheet.getSalelst());
					if (sheet.getSalelst().caculateFavorAfter(disc) > 0) {
						sheet.getSalelst().consumeFavorAfter(disc);
					}
				}
//			}
		} else {
			int qty = sheet.getSoldQty(g);

			if (discount_lst.matches(g, qty, sheet.getMemberLevel())) {
                 Discount disc=null;
                 if(sheet.getMemberCard()!=null)
				        disc = discount_lst.getDiscount(g, qty, sheet.getMemberLevel());
                 else
                        disc = discount_lst.getDiscount(g, qty, -1);
				 if (disc instanceof DiscRate) {
					System.out.println("DiscRate FOUND!");
					DiscRate r = (DiscRate) disc;
					sheet.setGoodsDisc(g, r);
				 }

				if (disc instanceof DiscPrice) {
					System.out.println("DiscPrice FOUND!");
					DiscPrice p = (DiscPrice) disc;
					sheet.setGoodsDisc(g, p);
				}
			} else {
				sheet.clearDiscount(g);
			}
		}
	}

	private ArrayList filterDiscComplex(ArrayList list) {

		ArrayList result = new ArrayList();

		for (int i = 0; i < list.size(); i++) {
			DiscComplex disc = (DiscComplex) list.get(i);
			boolean isIn = false;
			for (int j = 0; j < result.size(); j++) {
				DiscComplex disc2 = (DiscComplex) result.get(j);
				if (disc.getGroupID().equals(disc2.getGroupID())) {
					isIn = true;
				}
			}
			if (!isIn) {
				result.add(disc);
			}
		}

		return result;
	}

	private ArrayList splitDiscComplex(ArrayList list){

		ArrayList result = new ArrayList();

		for(int i=0;i<list.size();i++){
			DiscComplex disc = (DiscComplex) list.get(i);
			result.addAll(Arrays.asList(disc.split()));
			//result.add(disc);
		}

		return result;
	}

	/**
	 * @return
	 */
	private ArrayList getComplexGoods() {
		ArrayList result = new ArrayList();
		ArrayList vgNoList = new ArrayList();
		SaleList saleList = sheet.getSalelst();
		for (int i = 0; i < saleList.size(); i++) {
			if (saleList.get(i).getType() != Sale.WITHDRAW
				&& saleList.get(i).getGoods().getPType().equals(
					DiscCriteria.DISCCOMPLEX)
				&& !vgNoList.contains(saleList.get(i).getGoods().getVgno())) {
				vgNoList.add(saleList.get(i).getGoods().getVgno());
				result.add(saleList.get(i).getGoods());
			}
		}
		return result;
	}

	/**	更正处理
	 * 按业务规则,系统在进行商品更正处理时需要作以下工作:
	 * 记录更正操作.对已写入流水的销售数据不作修改,并在流水中插入一条记录更正行为的记录.
	 * 商品的更正数量不得大于已售出的数量.
	 * 商品的折扣通常与售出数量有关.所以,在作更正处理后,需要重新计算相关商品的折扣.
	 * @param input		待更正的商品数据(商品编码,商品数量)
	 * @return			为更正操作而生成的记录
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Sale correct(PosInputGoods input)
		throws FileNotFoundException, IOException {
		// Look for the sold items in sale_lst by code.
		// if sold number less than corrected number requested, refuse.
		// if sold number greater, correct it .
		SaleList salelst = getPosSheet().getSalelst();
		int sold = sheet.getSoldQty(input.getCode());

		try {
			PosPriceData codenew = new PosPriceData();
			codenew.setSaleCode(input.getCode());
			Goods g = goods_lst.find(codenew);
			
			//查找削价商品
		   // if (g == null&&PosContext.getInstance().isOnLine()) {
			 if (g == null) {
				Goods goodsCut = goodsCutList.findCut(input.getCode());
			  // GoodsCut goodsCut= RealTime.getInstance().findGoodsCut(input.getCode());
				if (goodsCut != null)
					g = goodsCut;
			}
			//增加捆绑商品
			if (g == null) {
				//Goods goodsCut = goodsCutList.find(input.getCode());
			   Goods goodsComb= goodsCombList.find(input.getCode());
				if (goodsComb != null)
					g = goodsComb;
			}

			
			GoodsExt goodsExt = null;
			if (g == null) {
				goodsExt = goodsext_lst.find(input.getCode());
				if (goodsExt != null) {
					g = goodsExt;

				}
			}
			if (g == null) {
				context.setWarning("找不到要更正的商品,按清除键继续!");
				return null;
			} else {
				
				if (g.getX() == 1
					&& g.getDeptid() != null
					&& !g.getDeptid().equals("040201")
					&& (input.getAmountEx()-input.getQty())>0) {
					context.setWarning("该商品数量必须为整数,按清除键继续!");
					return null;
				}

				
				if (goodsExt != null) {
					input.setQty(input.getQty() * goodsExt.getPknum());
				}
				
				if(g.getX()>1){
					input.setQty((int)Math.rint(input.getAmountEx()*g.getX()));
				}
				
				if (sold < input.getQty()) {
					context.setWarning("更正数量不得大于已售出数量,按清除键继续!");
					return null;
				}

				clearDiscount(g);
				Sale correctSale = salelst.getItemBCode(input.getCode());

				Sale sale = new Sale(g, -input.getQty(), Sale.CORRECT);
				sale.setOriginalCode(input.getOrgCode());
				sale.setColorSize(input.getColorSize());
				sale.setPlaceno(context.getPlaceno());
				sale.setAuthorizer(context.getAuthorizerid());

				if (input.getGoodsType() == Goods.WEIGHT_VALUE) {
					sale.setStdValue(-input.getCents());
					sale.setFactValue(-input.getCents());
				}

				long cordisc = correctSale.getDiscValue();
				long inqty = input.getQty();
				long totalqty = correctSale.getQty();
				long stdprice = correctSale.getStdPrice();
				long stdvalue = correctSale.getStdValue();
				long factvalue = correctSale.getFactValue();

				if (correctSale.getDiscType() == Discount.ALTPRICE) {
					sale.setDiscValue(
						new DiscPrice(
							Discount.ALTPRICE,
							- (
								correctSale.getStdPrice() * input.getQty()
									- (int) Math.rint(
										correctSale.getDiscValue()
											* 1.0
											* input.getQty()
											/ correctSale.getQty()))));
				}
				if (correctSale.getDiscType() == Discount.SINGLE) {
					long itemvalue = 0;
					long itemvalue_v = 0;
					itemvalue =
						(int) Math.rint(factvalue * 1.0 * inqty / totalqty);
					itemvalue_v =
						(int) Math.rint(factvalue * 1.0  / totalqty);
					DiscPrice discprice =
						new DiscPrice(Discount.SINGLE, -itemvalue);
					sale.setDiscValue(discprice); 
					sheet.setGoodsDisc(g,new DiscPrice(Discount.SINGLE, itemvalue_v));
					
					// 20070207 屏蔽
//					sale.setDiscValue(
//						new DiscPrice(
//							Discount.SINGLE,
//							- (
//								correctSale.getStdPrice() * input.getQty()
//									- (int) Math.rint(
//										correctSale.getDiscValue()
//											* 1.0
//											* input.getQty()
//											/ correctSale.getQty()))));
//					sheet.setGoodsDisc(
//						g,
//						new DiscPrice(
//							Discount.SINGLE,
//							(// zhouzhou del 一个负号2007
//								correctSale.getStdPrice() * input.getQty()
//									- (int) Math.rint(
//										correctSale.getDiscValue()
//											* 1.0
//											* input.getQty()
//											/ correctSale.getQty()))));
				}
				if (correctSale.getDiscType() == Discount.TOTAL) {
					int percent = 0;
					percent =
						(int) Math.rint(100 - factvalue * 100.0 / stdvalue);
					DiscRate discrate = new DiscRate(Discount.TOTAL, percent);
					sale.setDiscount(discrate);
					sheet.setGoodsDisc(g, discrate);
				}
				if (correctSale.getDiscType() == Discount.MONEY) {
					long itemvalue = 0;
					long itemvalue_v = 0;
					itemvalue =
						(int) Math.rint(factvalue * 1.0 * inqty / totalqty);
					itemvalue_v =
						(int) Math.rint(factvalue * 1.0  / totalqty);
					DiscPrice discprice =
						new DiscPrice(Discount.MONEY, -itemvalue);
					// zhouzhou DEL 2007
					sale.setDiscValue(discprice); 
//					sheet.setGoodsDisc(g, discprice);
					sheet.setGoodsDisc(g,new DiscPrice(Discount.MONEY, itemvalue_v));
				}

				Sale s = sheet.correct(sale);
				performDiscount(g);

				sheet.updateValue();
				dump();
				last_sold = null;
				return s;
			}
		} catch (RealTimeException ex) {

		}

		return null;
	}

	/**
	 * @param source
	 * @return
	 */
	private Object[] orderComplexByLevel(ArrayList source) {

		Object[] dest = source.toArray();
		for (int i = 0; i < dest.length - 1; i++) {
			for (int j = i + 1; j < dest.length; j++) {
				if (((DiscComplex) dest[i]).getLevel()
					< ((DiscComplex) dest[j]).getLevel()) {
					Object disc = dest[i];
					dest[i] = dest[j];
					dest[j] = disc;
				}
			}
		}
		return dest;
	}

	/**
	 * @param input
	 * @param baseprice
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws RealTimeException
	 */
	public Sale withdraw(PosInputGoods input, int baseprice)
		throws FileNotFoundException, IOException, RealTimeException {
		PosConfig config = PosConfig.getInstance();
		PosPriceData codenew = new PosPriceData();
		codenew.setSaleCode(input.getCode());
		Goods g = goods_lst.find(codenew);
		int qty_top = config.getInteger("MAXAMOUNT");
		int value_top = config.getInteger("MAXCASH") * 100;
		int sheetlen_top = config.getInteger("MAXITEM");
		int value_top_line = config.getInteger("MAXVALUE") * 100;
		int qty = input.getQty();
		/*
		 //退货时增加判断,不允许每单商品金额大于最大限额passed
		if ( Math.abs(sheet.getValue().getValueTotal()) > value_top) {
		        context.setWarning("退货商品金额已超过上限,请及时结算,按清除键继续!");
		        return null;
		}
		 */
		//退货时增加判断,不允许每单商品条数大于最大条数
        	String  ifInputWaiter=config.getString("IF_INPUT_WAITER");
        if(sheet.getSaleLen()==0&&"ON".equals(ifInputWaiter))
            if(!showWaiter()){
                context.setWarning("必需输入营业员,按清除键继续!");
                return null;
            }


		if (sheet.getSaleLen() >= sheetlen_top) {
			context.setWarning("此退货单已超长,请及时结算,按清除键继续!");
			return null;
		}

		/*
		if(baseprice != 0){
			g.setPrice(baseprice);
		}
		*/
        if (g == null) {
				Goods goodsCut = goodsCutList.findCut(input.getCode());
              // GoodsCut goodsCut= RealTime.getInstance().findGoodsCut(input.getCode());
				if (goodsCut != null)
					g = goodsCut;
        }
            //增加捆绑商品
        if (g == null) {
				//Goods goodsCut = goodsCutList.find(input.getCode());
               Goods goodsComb= goodsCombList.find(input.getCode());
				if (goodsComb != null)
					g = goodsComb;
        }

		GoodsExt goodsExt = null;
		if (g == null) {
			goodsExt = goodsext_lst.find(input.getCode());
			if (goodsExt != null) {
				g = goodsExt;
			}
		}
        if (baseprice != 0) {
			g.setPrice(baseprice);
		} 
		if (g == null) {
			context.setWarning("商品价格表中找不到您输入的商品编码,按清除键继续!");
			return null;
		}
		//删除油品的退货
		else if (
			g.getX() == 1 && input.getQty() * 1000 != input.getMilliVolume()) {
			context.setWarning("该商品数量必须为整数,按清除键继续!");
			return null;
		} else {
			//退货时增加判断,不允许每笔商品数量大于最大每笔商品数量
            if(g.getX()>1){
				qty=(int)Math.rint(input.getAmountEx()*g.getX());
			}

			if (qty > qty_top * g.getX()) {
				context.setWarning("商品数量过大,按清除键继续!");
				return null;
			}
			//退货时增加判断,不允许每笔商品销售金额大于最大每笔销售金额
			//   int v = g.getPrice() * qty / g.getX();
			long v =
				(long) Math.round(
					(long) g.getPrice() * (long) qty * 100 / (long) g.getX()
						+ 50)
					/ 100;

			// System.out.println("1 商品的金额是v ：　"+v);
			//System.out.println("1 商品的最大金额是value_top：　"+value_top);
			// System.out.println("现在此单总额为sheet.getValue().getValueTotal()　　　　：　"+sheet.getValue().getValueTotal());

			if (v < 0 || v > value_top_line) {
				context.setWarning("退货商品金额过大,按清除键继续!");
				return null;
			}
			// System.out.println("若增加此笔的总额为：　"+(v-sheet.getValue().getValueTotal()));
			// if(Math.abs(v)+sheet.getValue().getValueTotal()>value_top){
			if (v - sheet.getValue().getValueTotal() > value_top) {
				context.setWarning("退货商品金额已超过上限,请及时结算,按清除键继续!");
				return null;
			}

			if (goodsExt != null) {
				input.setQty(input.getQty() * goodsExt.getPknum());
			}
			
			if(sheet.getSaleLen()==0 && sheet.getMemberCard()==null){
				PosDevOut.getInstance().displayHeader(context);
			}

			//Sale sale = new Sale(g, -input.getQty(), Sale.WITHDRAW);
			Sale sale = new Sale(g, -qty, Sale.WITHDRAW);
            sale.setAuthorizer(context.getAuthorizerid());
			sale.setPlaceno(context.getPlaceno());
			sale.setColorSize(input.getColorSize());
			sale.setOriginalCode(input.getOrgCode());
            sale.setWaiter(context.getWaiterid());

			if (input.getDeptid() != null
				&& input.getDeptid().equals(Goods.LOADOMETER)) {
				sale.setFactValue(-input.getCents());
				sale.setStdValue(-input.getCents());
			}

			sheet.withdraw(sale);
			sheet.updateValue();
			dump();
			last_sold = null;
			return sale;
		}
	}

	/**
	 * @param input
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Payment pay(PosInputPayment input)
		throws FileNotFoundException, IOException,RealTimeException {
		PosConfig config = PosConfig.getInstance();
		int value_top = config.getInteger("MAXCASH") * 100;
		Payment p;
		String curr_code = context.getCurrenCode();
		double rate = context.getCurrenRate();
		int value = input.getCents();
		int value_equiv = (int) Math.rint(value * rate);
		String media_no = input.getMediaNumber();
        System.out.println("Media_no"+media_no);
		if (value_equiv > value_top) {
			PosContext.getInstance().setWarning("支付金额已超过系统上限,按清除键继续!");
			return null;
		}

		if (input.getType() == Payment.CARDLOAN) {
			LoanCardProcess lcp = input.getLoanCardProcess();
			if (lcp == null) {
				PosContext.getInstance().setWarning("无效操作,按清除键继续!");
				return null;
			}

			if (!lcp.performPay()) {
				if (lcp.getExceptionInfo() != null) {
					PosContext.getInstance().setWarning(lcp.getExceptionInfo());
				} else {
					PosContext.getInstance().setWarning("无效操作,按清除键继续!");
				}
				return null;
			}
		}

		if (input.getType() == Payment.ICCARD) {
			ICCardProcess icp = input.getICCardProcess();
			if (icp == null) {
				PosContext.getInstance().setWarning("无效操作,按清除键继续!");
				return null;
			}
			if (!icp.performPay()) {
				if (icp.getExceptionInfo() != null) {
					PosContext.getInstance().setWarning(icp.getExceptionInfo());
				} else {
					PosContext.getInstance().setWarning("无效操作,按清除键继续!");
				}
				return null;
			}
		}

		//add by lichao 2004/08/02

		if (input.getType() == 'R') {
			if (value_equiv > getValue().getValueTotal() && getValue().getValueTotal()>0) {
				System.out.println(
					"银行卡支付不能大于应收金额"
						+ "\n银行卡金额："
						+ value_equiv
						+ "\n应收为:"
						+ getValue().getValueTotal());
				setWarning("银行卡支付不能大于应收金额,按清除键继续!");
				return null;
			}
		}
		
        if(sheet.getShopCard()!=null&&"".equals(media_no))
            media_no=sheet.getShopCard().getCardNO();
		
        p = new Payment(
				Payment.PAY,
				input.getType(),
				curr_code,
				value,
				value_equiv,
				media_no);

		p.setBankCardTransReturnValue(input.getBankCardTransReturnValue());
		sheet.pay(p);
		cashbasket.put(input.getType(), curr_code, value);

        try {
 //           if(sheet.getMemberCard()!=null&&input.getCents()!=0)
            if(sheet.getMemberCard() !=null && input.getCents() == 0 )
              sheet.handlePoint();
        } catch (RealTimeException e) {
            e.printStackTrace();
            sheet.setPaymentList(new PaymentList());
            this.setWarning(e.getMessage());
            throw(e);
        } catch (IOException e) {
            e.printStackTrace();
            sheet.setPaymentList(new PaymentList());
           this.setWarning("服务器更新数据失败，按清除键继续尝试或取消!");
            return null;
        }

        sheet.updateValue();
		dump();
		last_sold = null;
		return p;
	}

	/**
	 * @param input
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Payment cashin(PosInputPayment input)
		throws FileNotFoundException, IOException {
		String curr_code = context.getCurrenCode();
		double rate = context.getCurrenRate();
		int value_top = PosConfig.getInstance().getInteger("CASH_LIMIT") * 100;
		int value = input.getCents();
		int value_equiv = (int) (value * rate);

		if (value_equiv > value_top) {
			PosContext.getInstance().setWarning("入款金额超过钱箱金额上限,按清除键继续!");
			return null;
		}

		Payment p =
			new Payment(
				Payment.CASHIN,
				input.getType(),
				curr_code,
				value,
				value_equiv);
		sheet.addPayment(p);
		cashbasket.put(input.getType(), curr_code, value);
		dump();
		writeJournal();
		last_sold = null;
		return p;
	}

	/**
	 * @param input
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Payment cashout(PosInputPayment input)
		throws FileNotFoundException, IOException {
		String curr_code = context.getCurrenCode();
		double rate = context.getCurrenRate();
		int value = input.getCents();
		int box_value = 0;
		int value_equiv = (int) (value * rate);

		CashBox box = cashbasket.getBox(input.getType(), curr_code);
		if (box != null)
			box_value = box.getValue();
		if (box == null || box_value < value) {
			PosContext.getInstance().setWarning("钞箱内的金额不足,请检查输入是否正确,按清除键继续!");
			return null;
		}

		Payment p =
			new Payment(
				Payment.CASHOUT,
				input.getType(),
				curr_code,
				-value,
				-value_equiv);
		sheet.addPayment(p);
		cashbasket.put(input.getType(), curr_code, -value);
		dump();
		writeJournal();
		last_sold = null;
		return p;
	}

	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void dump() throws FileNotFoundException, IOException {
		sheet.dump(sheetFile());
		context.dump();
		cashbasket.dump(FILE4BASKET);
	}

	/**	把当前的"待结销售单"中的内容写入到本地硬盘的文件(名为fname)中.
	 * @param fname	存放sheet对象的文件名称.
	 */
	public void dumpSheet(String fname) {
		try {
			ObjectOutputStream out =
				new ObjectOutputStream(new FileOutputStream(fname));
			out.writeObject(sheet);
			out.close();
		} catch (Exception e) {
			System.out.println("ERROR: " + e);
		}
	}

	/**	从名为fname 的文件中取出sheet 对象.
	 * @param fname
	 */
	public void loadSheet(String fname) {
		ObjectInputStream in = null;
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(fname);
			in = new ObjectInputStream(fin);
			sheet = (PosSheet) in.readObject();
			in.close();
		} catch (java.io.FileNotFoundException e) {
			System.out.println("WARNING: " + fname + " Not Found.");
			sheet = new PosSheet();
		} catch (Exception e) {
			System.out.println("WARNING: " + e);
			try {
				if (fin != null) {
					fin.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			FileUtil.fileError(fname);
			sheet = new PosSheet();

		}
		last_sold = null;
	}

	/**
	 * @param type
	 * @return
	 */
	public Payment closeSheetWithoutMoney(int type) {
		int value_unpaid = sheet.getValue().getValueUnPaid();
		Payment payment = new Payment(type, type, value_unpaid);
		sheet.updateValue();
		sheet.pay(payment);
		sheet.setTrainFlag(9);
		writeJournal();
		last_sold = null;
		return payment;
	}

	/**
	 *
	 */
	public void closeSheet(PosInputPayment input) {
		int value_topay = sheet.getValue().getValueToPay();
		if (value_topay < 0) {
			sheet.pay(Payment.CHANGE, value_topay);
			cashbasket.put(Payment.CASH, "RMB", value_topay);
		}

		int value_unpaid = sheet.getValue().getValueUnPaid();
		if (value_unpaid != 0)
			sheet.pay(Payment.PSEUDO, value_unpaid);
		if (context.isTraining()) {
			sheet.setTrainFlag(1);
		}
		//writeJournal();
		writeJournal(input);
		last_sold = null;
	}

	/**
	 *
	 */
	public void deleteSheet() {
		sheet.setAsDeleted();
		writeJournal();
		last_sold = null;
	}

	/**
	 *
	 */
	public void deleteFindresult() {
		sheet.setAsDeleted();
		last_sold = null;
	}

	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void openSheet() throws FileNotFoundException, IOException {
		sheet = new PosSheet();
		context.incrSheetid();
		dump();
		last_sold = null;
	}

	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void findpriceSheet() throws FileNotFoundException, IOException {
		sheet = new PosSheet();
		dump();
		last_sold = null;
	}

	/**
	 * @return
	 */
	public SheetValue getValue() {
		return sheet.getValue();
	}

	/**
	 * @return
	 */
	public int getValue4Indicator() {
		return sheet.getValue4Indicator();
	}

	/**
	 * @return
	 */
	public int getCount4Indicator() {
		return sheet.getCount4Indicator();
	}

	/**
	 * @return
	 */
	public boolean isSheetEmpty() {
		return sheet.isEmpty();
	}

	/**
	 * @param i
	 * @return
	 */
	public Sale getSale(int i) {
		return sheet.getSale(i);
	}

	/**
	 * @param i
	 * @return
	 */
	public Sale getFalseSale(int i) {
		return sheet.getFalseSale(i);
	}

	/**
	 * @param i
	 * @return
	 */
	public Payment getPayment(int i) {
		return sheet.getPayment(i);
	}

	/**
	 * @return
	 */
	public int getSaleLen() {
		return sheet.getSaleLen();
	}

	/**
	 * @return
	 */
	public int getFalseSaleLen() {
		return sheet.getFalseSaleLen();
	}

	/**
	 * @return
	 */
	public int getPayLen() {
		return sheet.getPayLen();
	}

	/**
	 * @param code
	 */
	public void setCurrency(String code) {
		Exchange e = exch_lst.find(code);
		if (e == null)
			return;
		context.setCurrency(code, e.getRate());
	}

	/**
	 *
	 */
	public void writeJournal(PosInputPayment input) {
		final String jfile = context.getName4Journal();
		Element root = new Element("journal");
		//root.addContent(new SheetElement(sheet));
		root.addContent(new SheetElement(sheet, input));
		root.addContent(new SheetElement(context));

		FileOutputStream owriter;
		try {
			owriter = new FileOutputStream("journal" + File.separator + jfile);
			XMLOutputter outputter = new XMLOutputter("  ", true, "GB2312");
			outputter.setTextTrim(true);
			outputter.output(new Document(root), owriter);
			owriter.close();

			JournalLog journalLog = new JournalLog();
			journalLog.setJournalName(jfile);
			journalLog.setCreateTime(Formatter.getDateFile(new Date()));
			journalLog.setStatus(JournalLog.CREATE);
			JournalLogList.getInstance().addLog(journalLog);
			JournalLogList.dump();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*add by lichao*/
		try {

			PosContext con = PosContext.getInstance();
			String srcfile = "journal" + File.separator + con.getName4Journal();
			File dir = new File("reprint/");
			if (!dir.exists() && !dir.isDirectory()) {
				dir.mkdir();
			}
			String destfile = "reprint/reprintsheet.xml";

			FileChannel infile = new FileInputStream(srcfile).getChannel();
			FileChannel outfile = new FileOutputStream(destfile).getChannel();
			infile.transferTo(0, infile.size(), outfile);
			infile.close();
			outfile.close();
		} catch (java.io.EOFException e) {
			System.out.println("EOF!");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

			pos.activeUploader();

	}

	public void writeJournal() {
		final String jfile = context.getName4Journal();
		Element root = new Element("journal");
		root.addContent(new SheetElement(sheet));
		root.addContent(new SheetElement(context));

		FileOutputStream owriter;
		try {
			owriter = new FileOutputStream("journal" + File.separator + jfile);
			XMLOutputter outputter = new XMLOutputter("  ", true, "GB2312");
			outputter.setTextTrim(true);
			outputter.output(new Document(root), owriter);
			owriter.close();

			JournalLog journalLog = new JournalLog();
			journalLog.setJournalName(jfile);
			journalLog.setCreateTime(Formatter.getDateFile(new Date()));
			journalLog.setStatus(JournalLog.CREATE);
			JournalLogList.getInstance().addLog(journalLog);
			JournalLogList.dump();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {

			PosContext con = PosContext.getInstance();
			String srcfile = "journal" + File.separator + con.getName4Journal();
			File dir = new File("reprint/");
			if (!dir.exists() && !dir.isDirectory()) {
				dir.mkdir();
			}
			String destfile = "reprint/reprintsheet.xml";

			FileChannel infile = new FileInputStream(srcfile).getChannel();
			FileChannel outfile = new FileOutputStream(destfile).getChannel();
			infile.transferTo(0, infile.size(), outfile);
			infile.close();
			outfile.close();
		} catch (java.io.EOFException e) {
			System.out.println("EOF!");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (context.isOnLine()) {
			Thread t = new Thread(new Runnable() {
				public void run() {
					String ip = context.getServerip();
					int port = context.getPort();
					synchronized (pos.uploadLock) {
						JournalWriter writer = new JournalWriter(ip, port);
						writer.writeJournal(jfile);
						JournalManager journalManager = new JournalManager();
						journalManager.upload();
					}
				}
			});
			t.start();
		}

	}

	/**
	 * @param action
	 * @param flag
	 * @param cashierid
	 */
	public void writelog(String action, String flag, int cashierid) {
		final String logfile = context.getNameLog();
		Element root = new Element("log");
		if (cashierid == 0) {
			root.addContent(new SheetElement(context, action, flag));
		} else {
			root.addContent(new SheetElement(context, action, flag, cashierid));
		}

		FileOutputStream owriter;
		try {
			owriter = new FileOutputStream("poslog" + File.separator + logfile);
			XMLOutputter outputter = new XMLOutputter("  ", true, "GB2312");
			outputter.setTextTrim(true);
			outputter.output(new Document(root), owriter);
			owriter.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param action
	 * @param flag
	 * @param cashierid
	 */
	public void writelogexit(String action, String flag, int cashierid) {
		final String logfile = context.getNameLog();
		Element root = new Element("log");
		if (cashierid == 0) {
			root.addContent(new SheetElement(context, action, flag));
		} else {
			root.addContent(new SheetElement(context, action, flag, cashierid));
		}

		FileOutputStream owriter;
		try {
			owriter = new FileOutputStream("poslog" + File.separator + logfile);
			XMLOutputter outputter = new XMLOutputter("  ", true, "GB2312");
			outputter.setTextTrim(true);
			outputter.output(new Document(root), owriter);
			owriter.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Thread t = new Thread(new Runnable() {
			public void run() {
				String ip = context.getServerip();
				int port = context.getPort();
				LogWriter log = new LogWriter(ip, port);
				log.writeLog(logfile);
				LogManager logManager = new LogManager();
				logManager.upload();
			}

		});

		t.start();

	}

	/*获取数量*/
	public int getcodeamount(String code)
	{	
		int amount=0;
		SaleList sales=sheet.getSalelst();
		
        if(sales!=null&&sales.size()>0){
            for(int i=0;i<sales.size();i++){
            	Sale thisSale=sales.get(i);
            	if(thisSale.getVgno().equals(code))
            	{
            	   amount+=(thisSale.getQty()/thisSale.getGoods().getX());	
            	}
            }
        }
        
        return amount;
	}
	
	/**		取当前工作文件的名称.
	 * @return	当前正在使用的工作文件.
	 */
	public String sheetFile() {
		return context.sheetFile();
	}

	/**
	 * @return	SheetBrief 数组. 在该数组中, 存有全部已挂起的POS工作单的特征.
	 */
	public SheetBrief[] getSheetBrief() {
		SheetBrief[] briefs = new SheetBrief[sheet_lst.length];
		for (int i = 0; i < sheet_lst.length; i++) {
			String file = sheet_lst[i];
			if (!file.equals(this.sheetFile())) {
				PosSheet t = PosSheet.load(file);
				briefs[i] = new SheetBrief(t);
			}
		}
		return briefs;
	}

	/**
	 * 设定"当前挂单数".
	 * POS需要随时显示"当前挂单数". 在系统中,sheet_lst 中保存有POS可以使用的所有工作文件名.
	 * 每次作挂单处理后,系统会对sheet_lst 中所有的文件进行检查,重新计算出当前挂单数,并修改context中的挂单数.
	 */
	private void setHeldCount() {
		int count = 0;
		for (int i = 0; i < sheet_lst.length; i++) {
			String file = sheet_lst[i];
			if (!file.equals(sheetFile())) {
				PosSheet t = PosSheet.load(file);
				if (t != null && !t.isEmpty())
					count++;
			}
		}
		context.setHeldCount(count);
	}

	/**
	 * @return
	 */
	public int holdSheet() {
		boolean done = false;
		String new_file = "";
		int n = -1; // 此变量记录挂起的工作文件在sheet_lst 中的位置.
		
		for (int i = 0; i < MAX_SHEETS; i++) {
			if (!PosSheet.isSheetInFile(sheet_lst[i])) {
				new_file = sheet_lst[i];
				context.setSheetFile(new_file);
				done = true;
				n = i;
				break;
			}
		}
		setHeldCount();

		if (done) {
			sheet = new PosSheet();
			sheet.dump(new_file);
			return n;
		} else {
			return -1;
		}
	}

	/**	解挂单处理.
	 * @param n		需要作"解挂"处理的文件编号.
	 */
	public void unholdSheet(int n) {
		String new_file = sheet_lst[n]; // 根据挂单编号取出挂单文件名.
		context.setSheetFile(new_file); // 修改当前工作文件名.
		sheet = PosSheet.load(new_file); // 把文件中保存的sheet对象装入当前sheet中.
		setHeldCount(); // 重新设置"当前挂单数量".
	}

	/**
	 *
	 */
	public void unholdFirst() {
		for (int i = 0; i < sheet_lst.length; i++) {
			String file = sheet_lst[i];
			if (!file.equals(sheetFile()) && PosSheet.isSheetInFile(file))
				unholdSheet(i);
		}
	}

	/**
	 * @param warning
	 */
	public void setWarning(String warning) {
		context.setWarning(warning);
	}

	/**
	 * @param id
	 */
	public void setCashierid(String id) {
		context.setCashierid(id);
	}

	/**
	 *
	 */
	private void createSheetFile() {
		for (int i = 0; i < sheet_lst.length; i++)
			if (!(new File(sheet_lst[i])).exists()) {
				PosSheet t = new PosSheet();
				t.dump(sheet_lst[i]);
			}
	}

	/**
	 *
	 */
	public void updateValue() {
		sheet.updateValue();
	}

	/**
	 * 计算
	 */
	private void computeFavor() {
		sheet.computeFavor(favor_lst);
	}

	/**
	 *
	 */
	private void consumeFavor() {
		sheet.consumeFavor(favor_lst);
	}

	/**
	 * @param disc
	 */
	private void consumeFavor(DiscComplex disc) {
		sheet.consumeFavor(disc);
	}

	/**
	 * @return
	 */
	public PosSheet getPosSheet() {
		return sheet;
	}

	/**
	 * @return
	 */
	public PosContext getPosContext() {
		return context;
	}

	/**
	 * @return
	 */
	public GoodsList getGoodList() {
		return goods_lst;
	}

	/**
	 * @return
	 */
	public Sale getLastSale() {
		return last_sold;
	}

	/**	判断商品是否需要录入补充信息.该方法主要针对油品.油品销售时,要求录入以下信息:油岛号.
	 * 判断方法: 在商品资料表中查询商品的deptid, 然后查看配置表的Indicator 表中是否包括该商品的deptid.
	 * 如果包括,则要求录入补充信息,否则不要求录入.
	 * @param code		商品编码.
	 * @return	true	要求录入补充信息;<br/>false	不要求录入补充信息;
	 * @throws RealTimeException
	 */
	public boolean requireDetail(String code) throws RealTimeException {
		PosPriceData codenew = new PosPriceData();
		codenew.setSaleCode(code);
		Goods g = goods_lst.find(codenew);
		PosConfig config = PosConfig.getInstance();
		if (g != null && config.isIndicatorDept(g.getDeptid()))
			return true;
		return false;
	}

	/**
	 * @return
	 */
	public boolean exceedCashLimit() {
		return cashbasket.exceedCashLimit();
	}

	/**
	 * @return
	 */
	public boolean exceedCashMaxLimit() {
		return cashbasket.exceedCashMaxLimit();
	}
	/**
	 * @return
	 */
	public int CompareLimit() {
		return cashbasket.CompareLimit();
	}

	/**
	 *
	 */
	public void resetCashBasket() {
		cashbasket.reset();
	}

	/**
	 * @return
	 */
	public CashBasket getCashBasket() {
		return cashbasket;
	}

	/**
	 * @param code
	 * @return
	 * @throws RealTimeException
	 */
    /*TODO 药品限购*/
	public Goods findGoods(PosPriceData code) throws RealTimeException {
		return goods_lst.find(code);
	}
	
    /*TODO 药品限购*/
	public ArrayList findBatchno(PosPriceData code) throws RealTimeException {
		return goods_lst.findBatchno(code);
	}

	/**
	 * @param cardno
	 * @return
	 * @throws RealTimeException
	 */
	public int getLoanCardDiscCount(String cardno) throws RealTimeException {
		return goods_lst.getLoanCardDiscCount(cardno);
	}

	/**
	 * @param code
	 * @return
	 * @throws RealTimeException
	 */
	public GoodsExt findGoodsExt(String code) throws RealTimeException {
		return goodsext_lst.find(code);
	}

	/**
	 * <code>MAX_SHEETS</code>		POS程序所支持的"销售工作单"数量上限.
	 */
	final public int MAX_SHEETS;
	/**
	 * <code>FILE4BASKET</code>		存放钱箱信息的文件名.
	 */
	final private String FILE4BASKET = "work" + File.separator + "basket.xml";

	/**
	 * <code>exch_lst</code>		汇率表.
	 */
	private ExchangeList exch_lst;
	/**
	 * <code>goods_lst</code>		商品资料表.
	 */
	private GoodsList goods_lst;
	/**
	 * <code>goodsext_lst</code>	商品扩展资料表.
	 */
	private GoodsExtList goodsext_lst;

	/**
	 * <code>discount_lst</code>	商品折扣表.
	 */
	private DiscountList discount_lst;
	/**
	 * <code>favor_lst</code>		组合折扣表.
	 */
	private DiscComplexList favor_lst;
	/**
	 * <code>bulk_lst</code>
	 */
	private BulkPriceList bulk_lst;
	/**
	 * <code>context</code>			POS工作的上下文信息.
	 */
	private PosContext context;
	/**
	 * <code>cashbasket</code>		钱箱信息.
	 */
	private CashBasket cashbasket;
	/**
	 * <code>sheet</code>			当前工作"单".
	 */
	private PosSheet sheet;
	/**
	 * <code>sheet_lst</code>		与sheet对应的工作文件的清单.
	 */
	private String[] sheet_lst;
	/**
	 * <code>last_sold</code>		最后一笔进入销售单的销售记录.
	 */
	private Sale last_sold = null;
	//商品价格映射表
    public static HashMap priceListMap=new HashMap();
    public static HashMap barcodeMap=new HashMap();
    public AccurateList accurateList;
    public GoodsCombList goodsCombList;
    public GoodsCutList goodsCutList;
    public PayModeList payModeList;
   	private boolean showWaiter() {
		String waiter_show;

		DispWaiter dispWaiter = new DispWaiter();
		dispWaiter.show();

		if (dispWaiter.isConfirm()) {

			waiter_show = dispWaiter.getShowWaiter();
			System.out.println("输入营业员完成");
			PosDevOut.getInstance().dispWaiter("营业员:" + waiter_show);
			PosDevOut.getInstance().setWaiter(waiter_show);
            return true;
		} return false;

	}
}
