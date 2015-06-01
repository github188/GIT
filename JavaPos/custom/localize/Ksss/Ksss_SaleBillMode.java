package custom.localize.Ksss;

import java.util.Vector;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.GiftGoodsDef;
import com.efuture.javaPos.Struct.SaleAppendDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;

import custom.localize.Cmls.Cmls_SaleBillMode;

public class Ksss_SaleBillMode extends Cmls_SaleBillMode
{
	protected final static int SBM_CustName = 201; // 顾客姓名
	protected final static int SBM_CustID = 202; // 顾客证件
	protected final static int SBM_HH = 203; // 货号

	protected String extendCase(PrintTemplateItem item, int index)
	{
		String line = null;
		switch (Integer.parseInt(item.code))
		{
			case SBM_CustName:
				Vector v1 = new Vector();
				String sellDate1 = salehead.rqsj.substring(0, 10);
				if (AccessDayDB.getDefault().getSaleAppendInfo(getDaySql(sellDate1), v1, salehead.syjh, salehead.fphm))
				{
					if (v1.size() < 1)
					{
						line = null;
					}
					else
					{
						SaleAppendDef saleAppend = (SaleAppendDef) v1.get(0);
						line = saleAppend.str1;
					}
				}
				break;

			case SBM_CustID:
				Vector v2 = new Vector();
				String sellDate2 = salehead.rqsj.substring(0, 10);
				if (AccessDayDB.getDefault().getSaleAppendInfo(getDaySql(sellDate2), v2, salehead.syjh, salehead.fphm))
				{
					if (v2.size() < 1)
					{
						line = null;
					}
					else
					{
						SaleAppendDef saleAppend = (SaleAppendDef) v2.get(0);
						line = saleAppend.str2;
					}
				}
				break;

			case SBM_HH:
				SaleGoodsDef sg = (SaleGoodsDef) salegoods.elementAt(index);
				if (sg.str6 != null && sg.str6.trim().length() < 1)
				{
					line = null;
				}
				else
				{
					line = ((SaleGoodsDef) salegoods.elementAt(index)).str6;
				}
				break;

			case SBM_yyyh: // 营业员号
				if (((SaleGoodsDef) salegoods.elementAt(index)).yyyh.length() < 1)
				{
					line = "&!";
				}
				else
				{
					line = ((SaleGoodsDef) salegoods.elementAt(index)).yyyh;
				}
				break;

			case SBM_gz: // 商品柜组
				if (((SaleGoodsDef) salegoods.elementAt(index)).gz.length() < 1)
				{
					line = "&!";
				}
				else
				{
					line = ((SaleGoodsDef) salegoods.elementAt(index)).gz;
				}
				break;

			case SBM_sl: // 数量
				if (((SaleGoodsDef) salegoods.elementAt(index)).sl * SellType.SELLSIGN(salehead.djlb) == 0)
				{
					line = "&!";
				}
				else
				{
					line = ManipulatePrecision.doubleToString(((SaleGoodsDef) salegoods.elementAt(index)).sl * SellType.SELLSIGN(salehead.djlb), 4,
																1, true);
				}
				break;

			case SBM_jg: // 售价
				if (((SaleGoodsDef) salegoods.elementAt(index)).jg == 0)
				{
					line = "&!";
				}
				else
				{
					line = ManipulatePrecision.doubleToString(((SaleGoodsDef) salegoods.elementAt(index)).jg);
				}
				break;

			case SBM_cjje: // 成交金额
				if ((((SaleGoodsDef) salegoods.elementAt(index)).hjje - ((SaleGoodsDef) salegoods	.elementAt(index)).hjzk)
													* SellType.SELLSIGN(salehead.djlb) == 0)
				{
					line = "&!";
				}
				else
				{
					line = ManipulatePrecision.doubleToString((((SaleGoodsDef) salegoods.elementAt(index)).hjje - ((SaleGoodsDef) salegoods	.elementAt(index)).hjzk)
																* SellType.SELLSIGN(salehead.djlb));					
				}
				break;
		}
		return line;
	}

	public Sqldb getDaySql(String date)
	{
		ManipulateDateTime dt = new ManipulateDateTime();
		if (date.equals(dt.getDateByEmpty()))
		{
			return GlobalInfo.dayDB;
		}
		else
		{
			return LoadSysInfo.getDefault().loadDayDB(date);
		}
	}

	public void printBottom()
	{
		if (zq != null)
		{
			StringBuffer line = new StringBuffer();
			double je = 0;
			for (int i = 0; i < this.zq.size(); i++)
			{
				GiftGoodsDef def = (GiftGoodsDef) this.zq.elementAt(i);

				if (def.type.equals("4"))
				{
					line.append(" " + def.info + "\n");
					line.append(" 有效期:" + def.memo + "\n");
					je += def.je;
				}
			}

			if (je > 0)
			{
				Printer.getDefault().printLine_Normal("\n");
				Printer.getDefault().printLine_Normal(" 本次小票有电子券返券，返券金额为:" + String.valueOf(je));
				Printer.getDefault().printLine_Normal(line.toString());
			}
		}

		super.printBottom();
	}

	/*
	 private Vector getBxGoods()
	 {
	 Vector v = new Vector();
	 
	 if (!SellType.ISSALE(salehead.djlb)) return v;
	 
	 SaleGoodsDef sg = null;
	 String bxName = "";
	 for (int i = 0; i < salegoods.size(); i++)
	 {
	 sg = (SaleGoodsDef)salegoods.get(i);
	 
	 if (sg.str9 != null && sg.str9.trim().length() > 0)
	 {
	 bxName = sg.str9.trim();
	 break;
	 }
	 }
	 
	 if (bxName.equals("")) return v;
	 
	 double hjje = 0;
	 double hjzk = 0;
	 
	 for (int i = 0; i < salegoods.size(); i++)
	 {
	 sg = (SaleGoodsDef)salegoods.get(i);
	 hjje = hjje + sg.hjje;
	 hjzk = hjzk + sg.hjzk;
	 }
	 
	 SaleGoodsDef newSg = (SaleGoodsDef)salegoods.get(0);
	 newSg.inputbarcode = "";
	 newSg.code = "";
	 newSg.barcode = "";
	 newSg.str6 = "";
	 newSg.name = bxName;
	 newSg.jg = 0;
	 newSg.hjje = hjje;
	 newSg.hjzk = hjzk;
	 newSg.sl = 0;
	 newSg.yyyh = "";
	 newSg.gz = "";
	 v.add(newSg);
	 
	 return v;
	 }
	 
	 public void printAppendBill()
	 {
	 Vector v = getBxGoods();
	 Vector oldSalegoods = this.salegoods;
	 if (v.size() > 0)
	 {
	 this.salegoods = v;
	 printSellBill();
	 }
	 this.salegoods = oldSalegoods;
	 super.printAppendBill();
	 }
	 */

	public String getFpName()
	{
		String fpName = "";

		SaleGoodsDef sg = null;
		for (int i = 0; i < salegoods.size(); i++)
		{
			sg = (SaleGoodsDef) salegoods.elementAt(i);

			if (sg.str9 != null && sg.str9.trim().length() > 0)
			{
				fpName = sg.str9.trim();
				break;
			}
		}
		return fpName;
	}

	protected void printSellBill()
	{
		// GlobalInfo.sysPara.fdprintyyy = (N-不打营业员联但打印小票联/Y-打印营业员联也打印小票/A-打印营业员联但不打印小票)
		// 非超市小票且系统参数定义只打印营业员分单，则不打印机制小票
		if (!((GlobalInfo.syjDef.issryyy == 'N') || (GlobalInfo.syjDef.issryyy == 'A' && ((SaleGoodsDef) salegoods.elementAt(0)).yyyh.equals("超市")))
				&& (GlobalInfo.sysPara.fdprintyyy == 'A')) { return; }

		String fpName = getFpName();
		if (SellType.ISSALE(salehead.djlb) && fpName.length() > 0)
		{
			Vector oldSaleGoods = (Vector)this.salegoods.clone();
			Vector newSaleGoods = new Vector();

			double hjje = 0;
			double hjzk = 0;

			SaleGoodsDef sg = null;
			for (int i = 0; i < salegoods.size(); i++)
			{
				sg = (SaleGoodsDef) salegoods.get(i);
				hjje = hjje + sg.hjje;
				hjzk = hjzk + sg.hjzk;
			}

			SaleGoodsDef newSg = (SaleGoodsDef)((SaleGoodsDef)salegoods.get(0)).clone();
			newSg.inputbarcode = "";
			newSg.code = "";
			newSg.barcode = "";
			newSg.str6 = "";
			newSg.name = fpName;
			newSg.jg = 0;
			newSg.hjje = hjje;
			newSg.hjzk = hjzk;
			newSg.sl = 0;
//			newSg.yyyh = "";
//			newSg.gz = "";
			newSaleGoods.add(newSg);
			this.salegoods = (Vector) newSaleGoods.clone();

			super.printSellBill();

			// 打印真实的商品明细
			// 先还原salegoods
			this.salegoods = (Vector) oldSaleGoods.clone();
			
			for (int i = 0; i < this.salegoods.size(); i++)
			{
				((SaleGoodsDef)this.salegoods.get(i)).jg = 0;
//				((SaleGoodsDef)this.salegoods.get(i)).yyyh = "";
//				((SaleGoodsDef)this.salegoods.get(i)).gz = "";
//				((SaleGoodsDef)this.salegoods.get(i)).hjje = 0;
//				((SaleGoodsDef)this.salegoods.get(i)).hjzk = 0;
			}
			
			// 打印头部区域
			printHeader();
			// 打印明细区域
			printDetail();
			// 切纸
			printCutPaper();
		}
		else
		{
			super.printSellBill();
		}

	}
	
	public void printMZKBillPrintMode()
	{
		int cardPrintTrack = ConfigClass.RepPrintTrack;
		ConfigClass.RepPrintTrack = 1;
		super.printMZKBillPrintMode();
		ConfigClass.RepPrintTrack = cardPrintTrack;
	}
}
