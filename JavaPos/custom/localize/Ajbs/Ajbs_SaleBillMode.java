package custom.localize.Ajbs;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bstd.Bstd_SaleBillMode;

public class Ajbs_SaleBillMode extends Bstd_SaleBillMode
{
	protected final static int OriginalJF = 202;

	protected String extendCase(PrintTemplateItem item, int index)
	{
		String line = null;

		switch (Integer.parseInt(item.code))
		{
			case OriginalJF: // 原始积分
				if (salehead.hykh != null && !salehead.hykh.equals(""))
					line = ManipulatePrecision.doubleToString(Ajbs_ICCard.getDefault().getOriginalJF());

				break;
			case SBM_ljjf: // 累计积分
				if (salehead.hykh != null && !salehead.hykh.equals(""))
					line = ManipulatePrecision.doubleToString(Ajbs_ICCard.getDefault().getOriginalJF() + salehead.bcjf);

				break;
			case SBM_bcjf: // 本次积分
				if (salehead.hykh != null && !salehead.hykh.equals(""))
					line = ManipulatePrecision.doubleToString(salehead.bcjf);

				break;
		}

		return line;
	}

	public void printBankBill()
	{
		// 在原始付款清单中,查找是否有银联卡付款方式
		for (int i = 0; i < originalsalepay.size(); i++)
		{
			SalePayDef pay = (SalePayDef) originalsalepay.elementAt(i);
			PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);

			if ((mode.isbank == 'Y') && (pay.batch != null) && (pay.batch.length() > 0))
			{
				PaymentBankFunc bank = CreatePayment.getDefault().getPaymentBankFunc(mode.code);
				bank.setOnceXYKPrintDoc(true);
				bank.XYKPrintDoc();
			}
		}
	}

	// 打印面值卡联
	public void printMZKBill(int type)
	{
		try
		{
			// 先检查是否有需要打印的付款方式
			for (int i = 0; i < salepay.size(); i++)
			{
				SalePayDef pay = (SalePayDef) salepay.get(i);
				if (pay == null)
					continue;

				if (pay.paycode.equals("0402") && GlobalInfo.sysPara.isprinticandmzk == 'Y')
				{
					printStart();

					printLine(" 商户存根   请保存票据" + "\n");
					printLine("===  ===  ===  ===  ===" + "\n");
					printLine("票号：" + GlobalInfo.syjStatus.fphm + "\n");
					printLine("商户：" + GlobalInfo.sysPara.mktcode + "  " + GlobalInfo.sysPara.mktname + "\n");
					printLine("收银机：" + GlobalInfo.syjDef.syjh + "\n");
					printLine("操作员：" + GlobalInfo.posLogin.gh + "\n");
					printLine("卡号：" + pay.payno + "\n");
					printLine("交易：" + pay.je + "  余额：" + pay.kye + "\n");
					printLine("日期：" + ManipulateDateTime.getCurrentDate() + "  " + ManipulateDateTime.getCurrentTime() + "\n");
					printLine("签名：" + "\n\n\n\n\n\n");

					printCutPaper();
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
