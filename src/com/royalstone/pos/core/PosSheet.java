package com.royalstone.pos.core;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.math.BigDecimal;
import java.nio.DoubleBuffer;
import java.text.SimpleDateFormat;

import com.royalstone.pos.card.*;
import com.royalstone.pos.common.*;
import com.royalstone.pos.complex.DiscComplex;
import com.royalstone.pos.complex.DiscComplexList;
import com.royalstone.pos.favor.BulkFavor;
import com.royalstone.pos.favor.DiscPrice;
import com.royalstone.pos.favor.DiscRate;
import com.royalstone.pos.invoke.realtime.RealTimeException;
import com.royalstone.pos.util.FileUtil;
import com.royalstone.pos.util.PosConfig;
import com.royalstone.pos.shell.pos;

/**
   @version 1.0 2004.05.17
   @author  Mengluoyi, Royalstone Co., Ltd.
 */

public class PosSheet implements Serializable {
	/**
	 * 
	 */
	public PosSheet() {
		sheet_value = new SheetValue();
		falsesale_lst = new SaleList();
		sale_lst = new SaleList();
		pay_lst = new PaymentList();

	}

	/**
	 * @param salelist
	 */
	public void setSaleList(SaleList salelist) {
		this.sale_lst = salelist;
	}

	/**
	 * @param falsesalelist
	 */
	public void setFalseSaleList(SaleList falsesalelist) {
		this.falsesale_lst = falsesalelist;
	}

	/**
	 * @param paylist
	 */
	public void setPaymentList(PaymentList paylist) {
		this.pay_lst = paylist;
	}

	/**
	 * @param fname
	 */
	public void dump(String fname) {
		try {
			ObjectOutputStream out =
				new ObjectOutputStream(new FileOutputStream(fname));
			out.writeObject(this);
			out.flush();
			out.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	/**	从指定文件中装入sheet 信息.
	 * @param fname	保存sheet 信息的文件.
	 * @return	PosSheet对象.
	 */
	public static PosSheet load(String fname) {
		PosSheet s = null;
		ObjectInputStream in = null;
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(fname);
			in = new ObjectInputStream(fin);
			s = (PosSheet) in.readObject();
			in.close();
		}catch (FileNotFoundException e) {
			e.printStackTrace();
			s = new PosSheet();
			s.dump(fname);
		}
		catch (Exception e) {
			e.printStackTrace();
			try {
				if (fin != null) {
					fin.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException e1) {
			}

			FileUtil.fileError(fname);
			s = new PosSheet();
			s.dump(fname);
			
		}
		return s;
	}

	/**	查看指定的文件中是否保存有 PosSheet 对象.
	 * @param file
	 * @return
	 */
	public static boolean isSheetInFile(String file) {
		boolean found = false;
		PosSheet t = null;
		try {
			ObjectInputStream in =
				new ObjectInputStream(new FileInputStream(file));
			t = (PosSheet) in.readObject();
			in.close();
			if (t == null || t.isEmpty())
				found = false;
			else
				found = true;
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
			found = false;
		} catch (Exception e) {
			e.printStackTrace();
			found = false;
		}
		return found;
	}

	/**
	 * @param g
	 * @param q
	 * @return
	 */
	public Sale sell(Goods g, int q) {
		Sale s = new Sale(g, q, Sale.SALE);
		sale_lst.add(s);
		falsesale_lst.add(s);
		return s;
	}

	/**
	 * @param sale
	 * @return
	 */
	public Sale sell(Sale sale) {
		falsesale_lst.add(sale);
		sale_lst.add(sale);
		return sale;
	}

	/**
	 * @param sale
	 * @return
	 */
	public Sale falsesell(Sale sale) {
		falsesale_lst.add(sale);
		return sale;
	}

	/**
	 * @param sale
	 * @return
	 */
	public Sale correct(Sale sale) {
		int sold = sale_lst.getSoldQty(sale.getGoods());

		if (sold + sale.getQty() < 0)
			return null;
		sale_lst.add(sale);
		falsesale_lst.add(sale);
		return sale;
	}

	/**		即更处理.<br/>
	 * NOTE: 销售更正的处理方法已经改变. 更正方法不再使用.
	 * @deprecated
	 * @return
	 */
	public Sale quickCorrect() {
		Sale sale = sale_lst.getLastItem();
		if (sale.getType() == Sale.SALE) {
			Sale qc =
				new Sale(sale.getGoods(), -sale.getQty(), Sale.QUICKCORRECT);
			sale_lst.add(qc);
			falsesale_lst.add(qc);
			return qc;
		} else {
			return null;
		}
	}

	/**
	 * @return
	 */
	public Sale altPrice() {
		Sale sale = sale_lst.getLastItem();
		if (sale.getType() == Sale.SALE) {
			Sale qc =
				new Sale(sale.getGoods(), -sale.getQty(), Sale.QUICKCORRECT);
			sale_lst.add(qc);
			falsesale_lst.add(qc);
			return qc;
		} else {
			return null;
		}
	}

	/**
	 * @param code
	 * @return
	 */
	public int getSoldQty(String code) {
		return sale_lst.getSoldQty(code);
	}

	/**
	 * @param g
	 * @return
	 */
	public int getSoldQty(Goods g) {
		return sale_lst.getSoldQty(g);
	}

	/**
	 * @param g
	 * @return
	 */
	public int getQtyDisc(Goods g) {
		return sale_lst.getQtyDisc(g);
	}

	/**
	 * @param g
	 * @param q
	 * @return
	 */
	public Sale withdraw(Goods g, int q) {
		Sale s = new Sale(g, -q, Sale.WITHDRAW);
		sale_lst.add(s);
		falsesale_lst.add(s);
		return s;
	}

	/**
	 * @param sale
	 * @return
	 */
	public Sale withdraw(Sale sale) {
		falsesale_lst.add(sale);
		sale_lst.add(sale);
		return sale;
	}

	/**
	   @param r payment reason.
	   @param t payment type.
	   @param curren_code currency code.
	   @param v value in the currency paid.
	   @param cardno card number, cheque number.
	 */
	/**
	 * @param r
	 * @param t
	 * @param curren
	 * @param v
	 * @param equiv
	 * @param no
	 */
	public void pay(int r, int t, String curren, int v, int equiv, String no) {
		Payment p = new Payment(r, t, curren, v, equiv, no);
		pay_lst.add(p);
	}

	/**
	 * @param p
	 */
	public void pay(Payment p) {
		pay_lst.add(p);
	}

	/**
	 * @param p
	 */
	public void addPayment(Payment p) {
		pay_lst.add(p);
	}

	/**
	 * @param r
	 * @param v
	 */
	public void pay(int r, int v) {
		Payment p = new Payment(r, v);
		pay_lst.add(p);
		updateValue();
	}

	/**
	 * 
	 */
	public void updateValue(){
		sheet_value.setValue(
			sale_lst.getTotalValue(),
			pay_lst.getValueSum(),
			pay_lst.getCashPaid(),
			sale_lst.getTotalDisc(),
			sale_lst.getSoldQty());
//        if(this.getMemberCard()!=null)
//           this.handlePoint();
	}
    //Todo
    /**
     * 计算此单的积分
     */
    public void handlePoint()throws RealTimeException,IOException{
      SaleList sales=this.getSalelst();

       PosConfig config=PosConfig.getInstance();
        //是否会员价积分
        String isMemberPoint=config.getString("MBERPOINT_FLAG");
        //是否促销商品积分
        String isPromPoint=config.getString("PROMPOINT_FLAG");
        //是否四舍五入
        String isRounding=config.getString("POINT_ROUNDING");
        double totalPoint=0.00;
        if(sales!=null&&sales.size()>0){
           for(int i=0;i<sales.size();i++){
               Sale thisSale=sales.get(i);
               String precentage="0";
               long thisSalePrice=0;
               double thisPoint=0.00;
               int pType=thisSale.getDiscType();
               int sheetType=thisSale.getType();

               if(pType!='n'&&sheetType!='r'){
               	
                     if(pType=='h'){
						if(isMemberPoint.equals("ON")){
							thisSalePrice=(thisSale.getFactValue());	
						}
                     }else if(isPromPoint.equals("ON")){
						thisSalePrice=(thisSale.getFactValue());
                     }
                         
                     if(pType=='c'&&!isPromPoint.equals("ON"))
                        thisSalePrice=(thisSale.getFactValue());


               } else{

                     thisSalePrice=(thisSale.getFactValue());
               }

               //查询积分计算比例

                  //积分计算数据访问对象
                  int cardLevel=this.getMemberLevel();
                  int deptID=-1;
                   try {
                       deptID=Integer.parseInt(thisSale.getDeptid());
                   } catch (NumberFormatException e) {}
               try {
                   precentage=pos.core.accurateList.findPrecentage(cardLevel,deptID);
               } catch (RealTimeException e) {
                   e.printStackTrace();
                   throw(e);
               }
              if(precentage==null)
                  precentage="0";
               //
              if(isRounding.equals("ON"))
                   thisPoint=(double)Math.round(thisSalePrice*Double.parseDouble(precentage));
              else
                   thisPoint=Math.ceil(thisSalePrice*Double.parseDouble(precentage.trim()));
              if(thisPoint!=0)
                 totalPoint=totalPoint+thisPoint/100;
              else
                  totalPoint=totalPoint+thisPoint;

           }
           this.getMemberCard().setCurrentPoint(new BigDecimal(totalPoint));
//			if(config.getString("PRINT_CURPOINT").equals("ON")){
           // zhouzhou add 
			if(isMemberPoint.equals("ON")){

				PosContext context=PosContext.getInstance();
				MemberCardUpdate mcu=new MemberCardUpdate();

				 mcu.setCardno(this.getMemberCard().getCardNo());
				 mcu.setCashierid(context.getCashierid());
				 mcu.setCdseq("0");
//				 mcu.setPayvalue(Double.toString((double)this.getValue().getValueToPay()/100));
				 // zhouzhou Add
				 mcu.setPayvalue(Double.toString((double)this.getValue().getValueTotal()/100));
				 mcu.setPosid(context.getPosid());
				 mcu.setShopid(context.getStoreid());
				 SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				 mcu.setTime(sdf.format(new Date()));
				 mcu.setListno(context.getSheetid());
				 //原来上版本屏蔽的
//				 mcu.setPoint(Double.toString(this.getMemberCard().getTotalPoint().add(this.getMemberCard().getCurrentPoint()).doubleValue()));
//				 mcu.setCourrentPoint(Double.toString(this.getMemberCard().getCurrentPoint().doubleValue()));
				 
				 mcu.setPoint(Double.toString(this.getMemberCard().getTotalPoint().doubleValue()+this.getMemberCard().getCurrentPoint().doubleValue()));
				 mcu.setCourrentPoint(Double.toString(totalPoint)); 
				 MemberCardMgr mcm=null;
				 try {
					mcm=MemberCardMgrFactory.createInstance();
				 } catch (Exception e) {
					 e.printStackTrace();
					 throw new RealTimeException(e.getMessage());
				 }
				 String result=null;
				 try {
					result= mcm.updateCardInfo(mcu);
				 } catch (IOException e) {
					 e.printStackTrace();
					 throw new IOException(e.getMessage());
				 }
				 if(result!=null&&!result.equals("1"))
					 throw new IOException(result);
				 if(result==null)
					 throw new IOException("更新服务器数据失败！");	
			}
			

        }


    }

	/**
	 * @return
	 */
	public SheetValue getValue() {
		return new SheetValue(sheet_value);
	}

	/**
	 * @param i
	 * @return
	 */
	public Sale getFalseSale(int i) {
		return falsesale_lst.get(i);
	}

	/**
	 * @param i
	 * @return
	 */
	public Sale getSale(int i) {
		return sale_lst.get(i);
	}

	/**
	 * @return
	 */
	public SaleList getSalelst() {
		/*
		for( int num=0; num < falsesale_lst.size(); num++ ){
			Sale sale = sale_lst.get(num);
			if(sale.getType() != Sale.AlTPRICE 
			&& sale.getType() != Sale.AUTODISC 
			&& sale.getType() != Sale.MONEYDISC
			&& sale.getType() != Sale.SINGLEDISC 
			&& sale.getType() != Sale.TOTALDISC 
			&& sale.getType() != Sale.TOTAL){
				sale_lst.add(sale);
			}
		}
		*/
		return sale_lst;
	}

	/**
	 * @return
	 */
	public SaleList getFalseSalelst() {
		return falsesale_lst;
	}

	/**
	 * @param i
	 * @return
	 */
	public Payment getPayment(int i) {
		return pay_lst.get(i);
	}

	/**
	 * @return
	 */
	public boolean isEmpty() {
		return (sale_lst.size() == 0);
	}

	/**
	 * @return
	 */
	public int getSaleLen() {
		return sale_lst.size();
	}

	/**
	 * @return
	 */
	public int getFalseSaleLen() {
		return falsesale_lst.size();
	}

	/**
	 * @return
	 */
	public int getPayLen() {
		return pay_lst.size();
	}

	/**
	 * @return
	 */
	public int getMemberLevel() {
		return member_card!=null?member_card.getMemberLevel():0;
	}

	/**
	 * @return
	 */
	public MemberCard getMemberCard() {
		return member_card;
	}

	/**
	 * @param g
	 * @param favor_total
	 */
	public void consumeBulkFavorDesc(Goods g, BulkFavor favor_total) {
		sale_lst.consumeBulkFavor(g, favor_total);
	}

	/**
	 * @param g
	 * @param disc
	 */
	public void setGoodsDisc(Goods g, DiscRate disc) {
		sale_lst.setGoodsDisc(g, disc);
	}

	/**
	 * @param g
	 * @param disc
	 */
	public void setGoodsDisc(Goods g, DiscPrice disc) {
		sale_lst.setGoodsDisc(g, disc);
	}

	/**
	 * @param g
	 */
	public void clearDiscount(Goods g) {
		sale_lst.clearDiscount(g);
	}

	/**
	 * @param disc_lst
	 */
	public void consumeFavor(DiscComplexList disc_lst) {
		sale_lst.consumeFavor(disc_lst);
	}

	/**
	 * @param disc
	 */
	public void consumeFavor(DiscComplex disc) {
		
		long totalDisc=sale_lst.caculateFavor(disc);
		
		if(totalDisc>0){
			sale_lst.consumeFavor(disc);
		}
		
	}

	/**
	 * @param disc_lst
	 */
	public void computeFavor(DiscComplexList disc_lst) {
		disc_lst.computeFavor(sale_lst);
	}

	/**
	 * @param disc_lst
	 * @param g
	 * @return
	 * @throws RealTimeException
	 */
	public ArrayList getMatchDiscComplex(DiscComplexList disc_lst, Goods g)
		throws RealTimeException {
		return disc_lst.getMatchDiscComplex(sale_lst, g);
	}

	/**
	 * 
	 */
	public void setAsDeleted() {
		for (int i = 0; i < sale_lst.size(); i++)
			 ((Sale) sale_lst.get(i)).setAsDeleted();
		for (int i = 0; i < pay_lst.size(); i++)
			 ((Payment) pay_lst.get(i)).setAsDeleted();
	}

	/**
	 * @param flag
	 */
	public void setTrainFlag(int flag) {
		for (int i = 0; i < sale_lst.size(); i++)
			 ((Sale) sale_lst.get(i)).setTrainFlag(flag);
		for (int i = 0; i < pay_lst.size(); i++)
			 ((Payment) pay_lst.get(i)).setTrainFlag(flag);
	}

	/**	查看标志性商品的数量.
	 * @return
	 */
	public int getCount4Indicator() {
		int count = 0;
		PosConfig config = PosConfig.getInstance();

		for (int i = 0; i < sale_lst.size(); i++) {
			Sale sale = (Sale) sale_lst.get(i);
			if (config.isIndicatorDept(sale.getGoods().getDeptid()))
				count++;
		}

		return count;
	}

	/**	查看标志性商品的金额.
	 * @return
	 */
	public int getValue4Indicator() {
		int cents = 0;
		PosConfig config = PosConfig.getInstance();

		for (int i = 0; i < sale_lst.size(); i++) {
			Sale sale = (Sale) sale_lst.get(i);
			if (config.isIndicatorDept(sale.getGoods().getDeptid()))
				cents += sale.getStdValue();
		}

		return cents;
	}

	/**查看挂帐卡资料
	 * @return 返回挂帐卡资料
	 */
	public LoanCardQueryVO getLoanCardQuery() {
		return loanCardQuery;
	}

	/**设置挂帐卡资料
	 * @param query 挂帐卡资料
	 */
	public void setLoanCardQuery(LoanCardQueryVO query) {
		loanCardQuery = query;
	}
	public void setMemberCard(MemberCard memberCard) {
		this.member_card = memberCard;
	}

	/**
	 * 
	 */
	public void print() {
		System.out.println("Sale List:");
		for (int i = 0; i < getSaleLen(); i++) {
			System.out.println(getSale(i));
		}

		System.out.println("Payment List:");
		for (int i = 0; i < getPayLen(); i++) {
			System.out.println(getPayment(i));
		}

		System.out.println("Sheet Over");
	}

	/**
	 * <code>sale_lst</code>	用于记录售出商品流水的数据结构.
	 */
	private SaleList sale_lst;
	/**
	 * <code>falsesale_lst</code>	专用于存放收银员显示屏(和收银小票)上的商品信息.
	 */
	private SaleList falsesale_lst;
	/**
	 * <code>pay_lst</code>	存放支付(和找赎)信息.
	 */
	private PaymentList pay_lst;
	/**
	 * <code>sheet_value</code>当前工作单的金额信息(已付/待付/折扣).
	 */
	private SheetValue sheet_value;
	/**
	 * Comment for <code>member_card</code>	此对象维护会员卡信息.
	 */
	private MemberCard member_card = null;
	/**
	 * Comment for <code>loanCardNum</code>	此对象维护挂帐卡信息.
	 */
	private LoanCardQueryVO loanCardQuery = null;
    private SHCardQueryVO shopCard=null;

    public SHCardQueryVO getShopCard() {
        return shopCard;
    }

    public void setShopCard(SHCardQueryVO shopCard) {
        this.shopCard = shopCard;
    }
    
}
