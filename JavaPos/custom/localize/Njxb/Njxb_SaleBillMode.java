package custom.localize.Njxb;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.CardSaleBillMode;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.GiftGoodsDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Cmls.Cmls_SaleBillMode;

public class Njxb_SaleBillMode extends Cmls_SaleBillMode
{
	protected final static int SBM_MSLIMITINFO = 202;
	// 当前柜组-总柜组数
	protected final static int SBM_GZINFO = 203;
	// 当前柜组应收金额小计
	protected final static int SBM_GZSUB = 204;
	// 当前柜组号
	protected final static int SBM_CURGZ = 205;
	// 当前柜组营业员号
	protected final static int SBM_CURYYYH = 206;
	protected final static int SBM_WXJF = 207;

	private int totalGzCount = 0; // 总柜组数
	private int curGzCount = 0; // 当前正在打印第几个柜组分组
	private String curGzNo = null; // 当前正在打印的柜组编号
	private double subTotal = 0;

	public HashMap totalSg = null;
	private boolean isAppendBillPrint = false;

	private double couponJe = 0;
	private Vector couponCodeList = null;

	protected String extendCase(PrintTemplateItem item, int index)
	{
		String line = null;
		int printCode = Integer.parseInt(item.code);
		if (printCode == SBM_payname || printCode == SBM_ybje || printCode == SBM_payno || printCode == SBM_ye || printCode == SBM_payfkje
				|| printCode == SBM_paycode || printCode == SBM_fkyy)
		{
			String code = ((SalePayDef) salepay.elementAt(index)).paycode;
			if (isCouponPay(code)) return "&!";
		}

		switch (printCode)
		{
			// 满增限额提示打印
			case SBM_WXJF:
				if (salehead.memo != null)
				{
					line = salehead.memo;
				}

				break;
			case SBM_MSLIMITINFO:
				if (salemsgift != null)
				{
					for (int i = 0; i < salemsgift.size(); i++)
					{
						GiftGoodsDef def = (GiftGoodsDef) this.salemsgift.elementAt(i);
						if (def.type.equals("025"))
						{
							line = def.memo;
							break;
						}
					}
				}
				break;

			case SBM_GZINFO:
				if (totalGzCount > 0)
				{
					line = curGzCount + "-" + totalGzCount;
				}
				else
				{
					line = "&!";
				}
				break;

			case SBM_GZSUB:
				if (subTotal > 0)
				{
					line = ManipulatePrecision.doubleToString(subTotal * SellType.SELLSIGN(salehead.djlb));
				}
				else
				{
					line = "&!";
				}
				break;

			case SBM_CURGZ:
				if (curGzNo != null)
				{
					line = curGzNo;
				}
				else
				{
					line = "&!";
				}
				break;

			case SBM_CURYYYH:
				line = ((SaleGoodsDef) salegoods.get(0)).yyyh;
				break;

			case SBM_sjfk:
				double sjfk = (salehead.sjfk - salehead.zl) * SellType.SELLSIGN(salehead.djlb);
				if (this.couponJe > 0) sjfk -= this.couponJe * SellType.SELLSIGN(salehead.djlb);
				line = ManipulatePrecision.doubleToString(sjfk);
				break;

			case SBM_ybje:
				SalePayDef sp = (SalePayDef) salepay.elementAt(index);
				if (sp.paycode.equals("01"))
				{
					line = ManipulatePrecision.doubleToString((sp.ybje - salehead.zl) * SellType.SELLSIGN(salehead.djlb));
				}
				else
				{
					line = ManipulatePrecision.doubleToString(sp.ybje * SellType.SELLSIGN(salehead.djlb));
				}
				break;
		}
		return line;
	}

	/*public void printPay()
	{
		if (GlobalInfo.syjDef.issryyy != 'N' && getCurGzCount() == getTotalGzCount())
		{
			super.printPay();
		}
		else if (GlobalInfo.syjDef.issryyy == 'N')
		{
			super.printPay();
		}

		if (this.couponJe > 0 && this.couponCodeList != null)
		{
			StringBuffer sb = new StringBuffer();
			StringBuffer sb1 = new StringBuffer();
			sb.append("券折扣");
			String blank = "                  ";
			sb.append(blank);
			sb.append(this.couponJe * -1);
			sb1.append(" (");
			for (int i = 0; i < this.couponCodeList.size(); i++)
			{
				sb1.append((String) this.couponCodeList.get(i));
				if (i < this.couponCodeList.size() - 1) sb1.append(",");
			}
			sb1.append(")");
			// 开始打印
			if (SellType.ISSALE(salehead.djlb))
			{
				Printer.getDefault().printLine_Normal(sb.toString());
				Printer.getDefault().printLine_Normal(sb1.toString());
			}
			else
			{
				Printer.getDefault().printLine_Journal(sb.toString());
				Printer.getDefault().printLine_Journal(sb1.toString());
			}
		}
	}*/
	
	
	public void printPay()
	  {
	    if ((GlobalInfo.syjDef.issryyy != 'N') && (getCurGzCount() == getTotalGzCount())) {
	      super.printPay();
	    }
	    else if (GlobalInfo.syjDef.issryyy == 'N')
	      super.printPay();
	    Vector codeList = new Vector();
	    double couponJe = calcCouponPay(codeList) * -1.0D;
	    if (couponJe < 0D)
	    {
	      StringBuffer sb = new StringBuffer();
	      StringBuffer sb1 = new StringBuffer();
	      sb.append("券折扣");
	      String blank = "                  ";
	      sb.append(blank);
	      sb.append(couponJe);
	      sb1.append(" (");
	      for (int i = 0; i < codeList.size(); ++i)
	      {
	        sb1.append((String)codeList.get(i));
	        if (i < codeList.size() - 1)
	          sb1.append(",");
	      }

	      sb1.append(")");
	      if (SellType.ISSALE(this.salehead.djlb)) {
	        Printer.getDefault().printLine_Normal(sb.toString());
	        Printer.getDefault().printLine_Normal(sb1.toString());
	      } else {
	        Printer.getDefault().printLine_Journal(sb.toString());
	        Printer.getDefault().printLine_Journal(sb1.toString());
	      }
	    }
	  }


	public void printBottom()
	{
		if (GlobalInfo.syjDef.issryyy != 'N' && getCurGzCount() == getTotalGzCount())
		{
			super.printBottom();
		}
		else if (GlobalInfo.syjDef.issryyy == 'N')
		{
			super.printBottom();
		}
	}

	public int getCurGzCount()
	{
		return curGzCount;
	}

	public int getTotalGzCount()
	{
		return totalGzCount;
	}

	// 商品按照柜组重新排序
	private void reSortSaleGoods()
	{
		totalSg = new HashMap();
		SaleGoodsDef sgd = null;
		for (int i = 0; i < salegoods.size(); i++)
		{
			sgd = (SaleGoodsDef) salegoods.get(i);

			if (!totalSg.containsKey(sgd.gz))
			{
				Vector sgList = new Vector();
				sgList.add(sgd);
				totalSg.put(sgd.gz, sgList);
			}
			else
			{
				((Vector) totalSg.get(sgd.gz)).add(sgd);
			}
		}
	}

	public void printBill()
	{
		// 计算券折扣
		//calcCouponPay();
		// 超市模式不分柜组打印
		if (GlobalInfo.syjDef.issryyy != 'N')
		{
			reSortSaleGoods();
			initGzCount();
			Iterator iter = totalSg.entrySet().iterator();
			while (iter.hasNext())
			{
				curGzCount++;
				subTotal = 0;
				Map.Entry entry = (Map.Entry) iter.next();
				curGzNo = (String) entry.getKey();
				Vector curSaleGoods = (Vector) entry.getValue();
				// 计算小计金额
				for (int i = 0; i < curSaleGoods.size(); i++)
				{
					subTotal += ((SaleGoodsDef) curSaleGoods.get(i)).hjje - ((SaleGoodsDef) curSaleGoods.get(i)).hjzk;
				}
				SaleBillMode.getDefault().setTemplateObject(salehead, curSaleGoods, originalsalepay);
				super.printBill();
			}
		}
		else
		{
			super.printBill();
		}
	}

	private void initGzCount()
	{
		isAppendBillPrint = false;
		subTotal = 0;
		totalGzCount = totalSg.size();
		curGzCount = 0;
		curGzNo = "";
	}

	public void printAppendBill()
	{
		if (GlobalInfo.syjDef.issryyy != 'N' && !isAppendBillPrint)
		{
			super.printAppendBill();
			isAppendBillPrint = true;
		}
		else if (GlobalInfo.syjDef.issryyy == 'N')
		{
			super.printAppendBill();
		}
	}

	public void printMZKBillPrintMode()
	{
		boolean bool = false;

		if (GlobalInfo.sysPara.mzkbillnum <= 0) { return; }

		if ((GlobalInfo.sysPara.printpaymode == null) || GlobalInfo.sysPara.printpaymode.equals("")) { return; }

		CardSaleBillMode.getDefault().setTemplateObject(salehead, salegoods, originalsalepay);

		String[] printpaymode = GlobalInfo.sysPara.printpaymode.split("\\|");

		for (int i = 0; i < printpaymode.length; i++)
		{
			CardSaleBillMode.getDefault().setPayCodes(printpaymode[i]);

			for (int j = 0; j < originalsalepay.size(); j++)
			{
				SalePayDef pay = (SalePayDef) originalsalepay.elementAt(j);
				PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);

				if (mode == null) continue;

				if (!CardSaleBillMode.getDefault().isExistPaycode(mode.code)) continue;

				bool = true;

				CardSaleBillMode.getDefault().setPayName(mode.name);

				break;
			}

			for (int n = 0; (n < GlobalInfo.sysPara.mzkbillnum) && bool; n++)
			{
				CardSaleBillMode.getDefault().printBill();
				if (!printpaymode[i].equals("0400"))
				{
					break;
				}
			}

			bool = false;
		}
	}

	private boolean isCouponPay(String payCode)
	{
		if (payCode.indexOf("05") == 0) return true;
		else return false;
	}

	/*private void calcCouponPay()
	{
		SalePayDef sp = null;
		for (int i = 0; i < salepay.size(); i++)
		{
			sp = (SalePayDef) salepay.get(i);
			if (isCouponPay(sp.paycode) && sp.flag == '1')
			{
				if (this.couponCodeList == null) couponCodeList = new Vector();
				
				if (this.couponCodeList != null)
				{
					String type = "";
					if (sp.idno != null && sp.idno.length() > 0)
					{
						type = "/" + String.valueOf(sp.idno.charAt(0));
					}
					this.couponCodeList.add(sp.paycode + type);
				}
				this.couponJe += sp.je;
			}
		}
	}*/
	
	 private double calcCouponPay(Vector codeList)
	  {
	    SalePayDef sp = null;
	    double je = 0D;
	    for (int i = 0; i < this.salepay.size(); ++i)
	    {
	      sp = (SalePayDef)this.salepay.get(i);
	      if ((isCouponPay(sp.paycode)) && (sp.flag == '1'))
	      {
	        if (codeList != null)
	        {
	          String type = "";
	          if ((sp.idno != null) && (sp.idno.length() > 0))
	            type = "/" + String.valueOf(sp.idno.charAt(0));
	          codeList.add(sp.paycode + type);
	        }
	        je += sp.je;
	      }
	    }

	    return je;
	  }
}
