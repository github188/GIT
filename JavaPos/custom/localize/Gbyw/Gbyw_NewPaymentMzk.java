package custom.localize.Gbyw;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Gbyw_NewPaymentMzk extends Gbyw_PaymentMzk
{
	public Gbyw_NewPaymentMzk()
	{

	}

	public Gbyw_NewPaymentMzk(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public Gbyw_NewPaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public boolean createSalePay(String cardno, String money)
	{
		String line = "";

		for (int i = 0; i < this.saleBS.salePayment.size(); i++)
		{
			SalePayDef spd = (SalePayDef) this.saleBS.salePayment.get(i);
			if ((!spd.paycode.equals("0408")) || (!spd.payno.equals(cardno)))
				continue;
			new MessageBox("同一张卡不允许多次付款!");
			return false;
		}

		if (super.createSalePay(money))
		{
			if (SellType.ISBACK(this.saleBS.saletype))
			{
				StringBuffer seqno = new StringBuffer();
				TextBox txt = new TextBox();

				if (!txt.open("请输入原流水号", "流水号", "请输入面值卡交易原始流水号(小票号)", seqno, 0.0D, 0.0D, false, -1))
					return false;

				line = "03," + this.track + "," + money + "," + this.pass + "," + GlobalInfo.syjStatus.syjh + "," + ManipulateStr.PadLeft(String.valueOf(this.salepay.fphm), 12, '0') + "," + GlobalInfo.sysPara.commMerchantId + "," + GlobalInfo.syjStatus.syyh + "," + ManipulateStr.PadLeft(seqno.toString(), 12, '0');
			}
			else
			{
				line = "02," + this.track + "," + String.valueOf(money) + "," + this.pass + "," + GlobalInfo.syjStatus.syjh + "," + ManipulateStr.PadLeft(String.valueOf(this.salepay.fphm), 12, '0') + "," + GlobalInfo.sysPara.commMerchantId + "," + GlobalInfo.syjStatus.syyh;
			}

			line = Gbyw_MzkVipModule.getDefault().sendData(line);

			if (line == null) { return false; }
			String[] item = line.split(",");

			if (item == null) { return false; }
			if (item.length > 0)
			{
				if (!item[0].equals("203"))
				{
					new MessageBox(Gbyw_MzkVipModule.getDefault().getError(item[0]));
					return false;
				}
			}

			this.salepay.payno = cardno;

			if (item.length > 2)
			{
				this.salepay.je = ManipulatePrecision.doubleConvert(Convert.toDouble(item[2]), 2, 1);
			}
			if (item.length > 3)
			{
				this.salepay.kye = ManipulatePrecision.doubleConvert(Convert.toDouble(item[3]), 2, 1);

				if (SellType.ISBACK(this.saleBS.saletype))
					new MessageBox("退货后卡余额:" + this.salepay.kye);
				else
				{
					new MessageBox("消费后卡余额:" + this.salepay.kye);
				}
			}
			this.salepay.str2 = this.track;
			this.salepay.str3 = this.pass;
			return true;
		}

		new MessageBox(Gbyw_MzkVipModule.getDefault().getError(line));
		this.salepay = null;

		return false;
	}

	public boolean cancelPay()
	{
		String line = "03," + this.salepay.str2 + "," + String.valueOf(this.salepay.je) + "," + this.salepay.str3 + "," + GlobalInfo.syjStatus.syjh + "," + ManipulateStr.PadLeft(String.valueOf(this.salepay.fphm), 12, '0') + "," + GlobalInfo.sysPara.commMerchantId + "," + GlobalInfo.syjStatus.syyh + "," + ManipulateStr.PadLeft(String.valueOf(this.salepay.fphm), 12, '0');

		line = Gbyw_MzkVipModule.getDefault().sendData(line);

		if (line == null) { return false; }
		String[] item = line.split(",");

		if (item == null) { return false; }
		if (item.length > 0)
		{
			if (!item[0].equals("203"))
			{
				new MessageBox(Gbyw_MzkVipModule.getDefault().getError(item[0]));
				return false;
			}
		}

		if (item.length > 2)
		{
			if (this.salepay.je != ManipulatePrecision.doubleConvert(Convert.toDouble(item[2]), 2, 1))
			{
				new MessageBox("消费与撤销前后金额不一致!");
				return false;
			}
		}

		if (item.length > 3)
		{
			new MessageBox("撤销后卡余额:" + ManipulatePrecision.doubleConvert(Convert.toDouble(item[3]), 2, 1));
		}
		return true;
	}
}
