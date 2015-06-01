package custom.localize.Szxw;

import java.util.Vector;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Szxw_DisplaySaleTicketBS extends DisplaySaleTicketBS
{
	public void printSaleTicket()
	{
		if (salehead != null && salehead.hcbz == 'Y')
		{
			new MessageBox("本单已经红冲，不能重打印");
			return;
		}

		super.printSaleTicket();
	}

	protected boolean createPayAssistant(Vector newSalePay, SaleHeadDef newSaleHead)
	{
		// 先去掉原小票付款列表中的定金签发
		for (int i = 0; i < newSalePay.size(); i++)
		{
			SalePayDef sp = (SalePayDef) newSalePay.elementAt(i);
			if (sp.paycode.equals("DJQF"))
			{
				newSalePay.remove(i);
				i--;
			}
		}

		return super.createPayAssistant(newSalePay, newSaleHead);
	}

	protected boolean saleCollectAccountPay(Vector newSalePay, SaleHeadDef newSaleHead)
	{
		// 发送定金单签发交易,根据原交易的单据类别判断
		if (SellType.ISEARNEST(salehead.djlb))
		{
			if (newSaleHead.jdfhdd == null || newSaleHead.jdfhdd.trim().equals(""))
			{
				new MessageBox("定金签发交易的定金单号无效\n\n定金单撤销失败");
				return false;
			}

			PaymentMzk pay = null;
			SalePayDef spay = new SalePayDef();
			spay.syjh = ((SalePayDef) newSalePay.elementAt(0)).syjh;
			spay.fphm = ((SalePayDef) newSalePay.elementAt(0)).fphm;
			spay.paycode = "DJQF";
			spay.payname = "定金撤销";
			spay.flag = '1';
			spay.ybje = newSaleHead.ysje;
			spay.hl = 1;
			spay.je = ManipulatePrecision.doubleConvert(spay.ybje * spay.hl, 2, 1);
			spay.payno = newSaleHead.jdfhdd; // 撤销定金单
			spay.batch = "";
			spay.kye = 0;

			pay = CreatePayment.getDefault().getPaymentMzk();
			pay.initPayment(spay, newSaleHead);

			// 加入付款列表
			payAssistant.add(pay);
			newSalePay.add(spay);
		}

		// 付款记账
		Payment p = null;
		boolean czsend = true;

		for (int i = 0; i < payAssistant.size(); i++)
		{
			p = (Payment) payAssistant.elementAt(i);
			if (p == null) continue;

			if (p.paymode != null && p.paymode.isbank == 'Y') continue;

			// 第一次记账前先检查是否有冲正需要发送
			if (czsend)
			{
				czsend = false;
				if (!p.sendAccountCz()) return false;
			}

			if (!p.collectAccountPay()) // 付款记账
			{ return false; }
		}
		return true;
		//return super.saleCollectAccountPay(newSalePay,newSaleHead);
	}

	protected boolean checkSaleRedQuash()
	{
		if (salehead == null)
		{
			new MessageBox("小票主单未找到,不能红冲", null, false);
			return false;
		}

		//退定金退到定金卡上的小票不能被红冲
		if (salehead.djlb .equals( SellType.EARNEST_BACK))
		{
			for (int i = 0; i < salepay.size(); i++)
			{
				SalePayDef salePayDef = (SalePayDef) salepay.elementAt(i);
				String paycode = salePayDef.paycode;
				if (paycode.equals("0400") || paycode.equals("0401"))
				{
					new MessageBox("不允许红冲定金单转定金卡", null, false);
					return false;
				}
			}
		}

		//定金单消费不能红冲
		for (int i = 0; i < salepay.size(); i++)
		{
			SalePayDef salePayDef = (SalePayDef) salepay.elementAt(i);
			String paycode = salePayDef.paycode;
			if (paycode.equals("0411"))
			{
				new MessageBox("该笔交是定金单消费,不能红冲", null, false);
				return false;
			}
		}

		return super.checkSaleRedQuash();
	}
}
