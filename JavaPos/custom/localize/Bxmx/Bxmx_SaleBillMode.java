package custom.localize.Bxmx;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.PrintTemplate.CardSaleBillMode;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bstd.Bstd_SaleBillMode;

public class Bxmx_SaleBillMode extends Bstd_SaleBillMode
{
	protected final static int SBM_popnews = 203;
	protected final static int SBM_fetmkt = 204; // 取货门店
	protected final static int SBM_fetmktname = 205; // 取货门店
	protected final static int SBM_fetmktdate = 206; // 取货时间
	protected String[] fetchinfo = null;

	public void setTemplateObject(SaleHeadDef h, Vector s, Vector p)
	{
		super.setTemplateObject(h, s, p);

		if (salehead.str2.length() > 0 && salehead.str2.indexOf("#") != -1)
			fetchinfo = salehead.str2.split("#");
	}

	protected String extendCase(PrintTemplateItem item, int index)
	{

		String line = super.extendCase(item, index);

		if (line != null)
			return line;

		switch (Integer.parseInt(item.code))
		{
			case SBM_sjfk: // 实收金额
				if (SellType.ISEARNEST(salehead.djlb) && salehead.num1 > 0.0)
				{
					double syje = 0.0;

					for (int i = 0; i < salepay.size(); i++)
					{
						SalePayDef paydef = (SalePayDef) salepay.elementAt(i);
						if (paydef.flag == '1')
							syje += paydef.je;
					}

					line = ManipulatePrecision.doubleToString(syje * SellType.SELLSIGN(salehead.djlb));
				}
				else if (SellType.ISPREPARETAKE(salehead.djlb))
				{
					double syje = 0.0;

					for (int i = 0; i < salepay.size(); i++)
					{
						SalePayDef paydef = (SalePayDef) salepay.elementAt(i);
						if (paydef.flag == '1')
							syje += paydef.je;
					}
					line = ManipulatePrecision.doubleToString(syje * SellType.SELLSIGN(salehead.djlb));
				}
				else
				{
					line = ManipulatePrecision.doubleToString(salehead.sjfk * SellType.SELLSIGN(salehead.djlb));
				}

				break;

			case SBM_fphm: // 小票号码
				if (SellType.ISPREPARETAKE(salehead.djlb))
					line = Convert.increaseLong(GlobalInfo.syjStatus.fphm - 1, 8);
				else
					line = Convert.increaseLong(salehead.fphm, 8);

				break;
			case SBM_payname: // 付款方式名称
				if (SellType.ISEARNEST(this.salehead.djlb))
				{
					if (((SalePayDef) salepay.elementAt(index)).flag == '4')
						line = "未付金额";
				}
				else
					line = ((SalePayDef) salepay.elementAt(index)).payname;

				break;
			case SBM_popnews:
				line = salehead.str4;
				break;
			case SBM_fetmkt:
				if (SellType.ISEARNEST(salehead.djlb))
				{
					if (fetchinfo != null && fetchinfo.length > 0 && fetchinfo[0] != null)
						// 收定金
						line = fetchinfo[0];
					else
						line = "";
				}

				if (SellType.ISPREPARETAKE(salehead.djlb))
					line = salehead.mkt;

				break;
			case SBM_fetmktname:
				if (SellType.ISEARNEST(salehead.djlb))
				{
					if (fetchinfo != null && fetchinfo.length > 6 && fetchinfo[6] != null)
						// 收定金
						line = fetchinfo[6];
					else
						line = "";
				}
				else if (SellType.ISPREPARETAKE(salehead.djlb))
				{
					line = GlobalInfo.sysPara.mktname;
				}
				else
				{
					line = null;
				}

				break;
			case SBM_fetmktdate:
				if (SellType.ISEARNEST(salehead.djlb))
				{
					if (fetchinfo != null && fetchinfo.length > 6 && fetchinfo[1] != null)
						// 收定金
						line = fetchinfo[1];
					else
						line = "";
				}
				else
				{
					line = null;
				}

				break;
		}
		return line;
	}

	protected void printAppendBill()
	{

		// 检查是否有未打印的银联签购单
		if (PaymentBank.haveXYKDoc)
			printBankBill();

		/*
		 * // 检查是否有 if (CardSaleBillMode.getDefault().isLoad()) {
		 * printMZKBillPrintMode(); } else { // 打印面值卡联 printMZKBill(1);
		 * 
		 * // 打印返券卡联 printMZKBill(2); }
		 */

		// 打印赠券联
		printSaleTicketMSInfo();
	}

}
