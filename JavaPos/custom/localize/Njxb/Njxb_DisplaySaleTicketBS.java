package custom.localize.Njxb;

import java.util.Vector;

import bankpay.Payment.AlipayO_Payment;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Njxb_DisplaySaleTicketBS extends DisplaySaleTicketBS
{
	protected boolean saleCollectAccountPay(Vector newSalePay, SaleHeadDef newSaleHead)
	{
		Payment p = null;
		boolean czsend = true;

		// 红冲交易的付款要先记账扣回行，再记账付款行,以保证储值卡等付款账务记账顺序反向
		if (SellType.ISHC(newSaleHead.djlb))
		{
			for (int i = payAssistant.size() - 1; i >= 0; i--)
			{
				p = (Payment) payAssistant.elementAt(i);
				if (p == null) continue;

				// 第一次记账前先检查是否有冲正需要发送
				if (czsend)
				{
					czsend = false;
					if (!p.sendAccountCz()) return false;
				}

				// 付款记账
				if (!p.collectAccountPay()) { return false; }
			}
		}
		else
		{
			boolean hasGecrmPay = false;
			for (int i = 0; i < payAssistant.size(); i++)
			{
				p = (Payment) payAssistant.elementAt(i);
				if (p == null) continue;

				// 第一次记账前先检查是否有冲正需要发送
				if (czsend)
				{
					czsend = false;
					if (!p.sendAccountCz()) return false;
				}

				if (p.paymode.code.equals("0401"))
				{
					hasGecrmPay = true;
					GecrmFunc gecrmFunc = new GecrmFunc(GecrmFunc.CHECKOLD, newSaleHead, null);
					while (true)
					{
						new MessageBox("请刷金鹰卡(旧)");
						GecrmCard card = new GecrmCard();
						if (gecrmFunc.doGecrm(card))
						{
							SalePayDef sp = (SalePayDef)newSalePay.get(i);
							sp.payno = card.card_no; // 卡号
							sp.str2 = card.valid_date;
							sp.kye = card.balance; // 余额
							break;
						}
						else
						{
							if (gecrmFunc.isEsc == 'Y')
							{
								return false;
							}
							else
							{
								continue;
							}
						}
					}
				}
				else if (p.paymode.code.equals("0402"))
				{
					hasGecrmPay = true;
					GecrmFunc gecrmFunc = new GecrmFunc(GecrmFunc.CHECKNEW, newSaleHead, null);
					while (true)
					{
						new MessageBox("请刷金鹰卡(新)");
						GecrmCard card = new GecrmCard();
						if (gecrmFunc.doGecrm(card))
						{
							SalePayDef sp = (SalePayDef)newSalePay.get(i);
							sp.payno = card.card_no; // 卡号
							sp.str2 = card.valid_date;
							sp.kye = card.balance; // 余额
							break;
						}
						else
						{
							if (gecrmFunc.isEsc == 'Y')
							{
								return false;
							}
							else
							{
								continue;
							}
						}
					}
				}
				else
				{
					// 付款记账
					if (!p.collectAccountPay()) { return false; }
				}
			}
			// 记账			
			if (hasGecrmPay)
			{
				GecrmFunc func = new GecrmFunc(GecrmFunc.BACK, newSaleHead, newSalePay);
				if (!func.doGecrm(null)) { return false; }
				Njxb_SaleBS.reCalcGecrmBalance(newSalePay, true);
			}
		}

		return true;
	}
}
