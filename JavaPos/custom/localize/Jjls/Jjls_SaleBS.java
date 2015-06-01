package custom.localize.Jjls;

import java.util.Vector;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.PaymentCustJfSale;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;

public class Jjls_SaleBS extends Jjls_SaleBS0CRMPop
{
	// 满抵
	public void custMethod()
	{
		if (!SellType.ISBACK(saletype)) { return; }

		String mdCode[] = GlobalInfo.sysPara.mdcode.split(",");
		if (mdCode[0].trim().equals("")) { return; }
		PayModeDef paymode = DataService.getDefault().searchPayMode(mdCode[0]);
		if (paymode == null) { return; }

		for (int j = 0; j < saleGoods.size(); j++)
		{
			SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.elementAt(j);
			SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(j);

			if (sgd.num6 > 0)
			{
				double je = getDetailOverFlow((sgd.num6 * sgd.sl / sgd.ysl), GlobalInfo.syjDef.sswrfs);
				createMDPayment(je);

				SalePayDef sp = (SalePayDef) salePayment.elementAt(salePayment.size() - 1);
				String s[] = { String.valueOf(sp.num5), sp.paycode, sp.payname, String.valueOf(je) };
				if (spinfo.payft == null) spinfo.payft = new Vector();
				spinfo.payft.add(s);
			}
		}
		//		 重算应收
		calcHeadYsje();
	}

	// 满抵
	public void doBrokenData()
	{
		if (GlobalInfo.sysPara.mdcode.split(",")[0].trim().equals("")) { return; }
		SalePayDef sp = null;
		for (int i = salePayment.size() - 1; i > -1; i--)
		{
			sp = (SalePayDef) salePayment.elementAt(i);
			if (sp.paycode.equals(GlobalInfo.sysPara.mdcode.split(",")[0]))
			{
				salePayment.remove(i);
			}
		}
	}

	// 满抵
	public boolean checkDeleteSalePay(String ax, boolean isDelete)
	{
		String code = "";
		if (ax.trim().indexOf("]") > -1)
		{
			code = ax.substring(1, ax.trim().indexOf("]"));
		}
		else
		{
			code = ax;
		}
		if (isDelete && ("," + GlobalInfo.sysPara.ICCardPayment + ",").indexOf("," + code + ",") > -1) { return true; }
		if (code.equals(GlobalInfo.sysPara.mdcode.split(",")[0])) { return true; }
		return false;
	}

	public void takeBackTicketInfo(SaleHeadDef thsaleHead, Vector thsaleGoods, Vector thsalePayment)
	{
		//在断点保护的情况下，可能出现退货付款方式存入2次的情况，加以判断
		memoPayment.removeAllElements();

		//goodsSpare = new Vector();
		for (int i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.elementAt(i);

			//liwenjin Add 在退货时添加对应的分摊类 
			//goodsSpare.add(new SpareInfoDef());

			// 小票头记录原单号
			saleHead.yfphm = String.valueOf(sgd.yfphm);
			saleHead.ysyjh = sgd.ysyjh;

			// 积分换购付款方式处理
			if (sgd.str2.indexOf("0509") >= 0)
			{
				int st = sgd.str2.lastIndexOf(",", sgd.str2.indexOf("0509"));
				int end = sgd.str2.indexOf(",", sgd.str2.indexOf("0509"));
				if (st <= 0) st = 0;
				if (end <= 0) end = sgd.str2.length();
				String line = sgd.str2.substring(st, end);

				if (line.charAt(0) == ',') line.substring(1);

				String rowno = line.substring(0, line.indexOf(":"));
				int rowno1 = -1;
				try
				{
					rowno1 = Integer.parseInt(rowno);
					rowno1--;
				}
				catch (Exception er)
				{
					er.printStackTrace();
				}

				if (rowno1 >= 0)
				{
					SalePayDef spd1 = (SalePayDef) thsalePayment.elementAt(rowno1);
					spd1.syjh = ConfigClass.CashRegisterCode;
					if (spd1.paycode.equals("0509"))
					{
						PayModeDef pmd = DataService.getDefault().searchPayMode("0509");
						PaymentCustJfSale pay = new PaymentCustJfSale(pmd, this);

						if (sgd.sl < sgd.memonum1)
						{
							String salepaylist[] = spd1.idno.split(",");
							double jf = Double.parseDouble(salepaylist[0]);
							jf = ManipulatePrecision.doubleConvert(jf * (sgd.sl / sgd.memonum1));
							spd1.je = ManipulatePrecision.doubleConvert(spd1.je * (sgd.sl / sgd.memonum1));
							spd1.idno = jf + "," + spd1.idno.substring(spd1.idno.indexOf(",") + 1);
						}

						pay.salepay = spd1;
						memoPayment.add(pay);
					}
				}
			}

			// 满抵付款方式处理
			boolean hasMdPay = true;
			String mdCode[] = GlobalInfo.sysPara.mdcode.split(",");
			if (mdCode[0].trim().equals(""))
			{
				hasMdPay = false;
			}
			PayModeDef paymode = DataService.getDefault().searchPayMode(mdCode[0]);
			if (paymode == null)
			{
				hasMdPay = false;
			}

			if (hasMdPay && sgd.str2.trim().length() > 0)
			{
				String[] total = sgd.str2.trim().split(",");
				if (total.length > 0)
				{
					String[] deatil;
					for (int k = 0; k < total.length; k++)
					{
						deatil = total[k].split(":");
						if (deatil.length == 3 && deatil[1].equals(mdCode[0]))
						{
							sgd.num6 = Double.parseDouble(deatil[2]);
						}
					}
				}
			}

			// 将原付款分摊删除
			sgd.str2 = "";
		}
	}

	public boolean isAllowExitPay()
	{
		SalePayDef sp = null;
		for (int i = 0; i < salePayment.size(); i++)
		{
			sp = (SalePayDef) salePayment.get(i);
			if (("," + GlobalInfo.sysPara.ICCardPayment + ",").indexOf("," + sp.paycode + ",") > -1) { return false; }
		}
		return true;
	}

	public boolean exitPaySell()
	{
		if (isAllowExitPay())
		{
			return super.exitPaySell();
		}
		else
		{
			return false;
		}
	}
	
	// 记录会员卡类别描述用于打印
	public boolean memberGrantFinish(CustomerDef cust)
	{
		this.saleHead.buyerinfo = cust.valstr1;	// 会员卡类别描述
		return super.memberGrantFinish(cust);
	}
	
	// 界面显示会员卡类别描述
    public String getVipInfoLabel()
    {
    	if (curCustomer == null)
    		return "";
    	else 
    	{
        	String typeDesc = "";
        	if (curCustomer.valstr1 != null) typeDesc = curCustomer.valstr1;
    		return typeDesc + "[" + curCustomer.code + "]" + curCustomer.name;
    	}
    }
}
