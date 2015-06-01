package custom.localize.Gbyw;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Gbyw_PaymentCoin extends Gbyw_PaymentMzk
{
	public Gbyw_PaymentCoin(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public Gbyw_PaymentCoin(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public void init(Gbyw_MzkEvent evt)
	{
		if (saleBS.curCustomer == null)
		{
			new MessageBox("未刷会员卡!");
			evt.shell.close();
			evt.shell.dispose();
			return;
		}

		evt.txtAcount.setText(saleBS.curCustomer.code);
		evt.txtAcount.setEnabled(false);
		evt.txtScore.setText(String.valueOf(saleBS.curCustomer.value1));

		evt.txtMoney.setText(String.valueOf(Math.min(saleBS.curCustomer.value1, saleBS.calcPayBalance())));
		evt.txtMoney.setFocus();
		evt.txtMoney.selectAll();
	}

	public SalePayDef inputPay(String money)
	{
		try
		{
			if (!Gbyw_MzkVipModule.getDefault().initConnection())
				return null;

			// 打开明细输入窗口
			new Gbyw_MzkForm().open(this, saleBS);

			return salepay;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return null;
	}
	
	public static boolean isPaymentLczc(SalePayDef sp)
	{
		if (sp.paycode.equals("0403") && sp.memo.trim().equals("3"))
			return true;

		return false;
	}
	
	public boolean createLczcSalePay(double zl)
	{
		// 创建SalePay
		if (!createSalePayObject(String.valueOf((zl))))
			return false;

		String line = "05," + saleBS.curCustomer.code + "," + saleBS.saleHead.ysje + "," + 0 + "," + GlobalInfo.syjStatus.syjh + "," + ManipulateStr.PadLeft(String.valueOf(salepay.fphm), 12, '0') + "," + GlobalInfo.sysPara.commMerchantId + "," + zl;
		line = Gbyw_MzkVipModule.getDefault().sendData(line);

		if (line == null)
			return false;

		String[] item = line.split(",");

		if (item == null)
			return false;

		if (item.length > 0)
		{
			if (!item[0].equals("0"))
			{
				new MessageBox(Gbyw_MzkVipModule.getDefault().getError(item[0]));
				return false;
			}
		}
		salepay.payno = saleBS.curCustomer.code;
		if (item.length > 5)
		{
			salepay.kye = ManipulatePrecision.doubleConvert(Convert.toDouble(item[5]), 2, 1);

			if (SellType.ISBACK(this.saleBS.saletype))
				new MessageBox("退货后零钱包余额:" + salepay.kye);
			else
				new MessageBox("消费后零钱包余额:" + salepay.kye);
		}

		// 零钞转存付款方式金额记负数
		salepay.ybje *= -1;
		salepay.je *= -1;

		// 代表零钞转存
		salepay.memo = "3";

		return true;
	}

	public boolean createSalePay(String cardno, String money)
	{
		String line = "";

		for (int i = 0; i < saleBS.salePayment.size(); i++)
		{
			SalePayDef spd = (SalePayDef) saleBS.salePayment.get(i);
			if (spd.paycode.equals("0403") && spd.payno.equals(cardno))
			{
				new MessageBox("同一张卡不允许多次付款!");
				return false;
			}
		}

		if (super.createSalePay(money))
		{
			if (SellType.ISBACK(this.saleBS.saletype))
			{
				line = "05," + cardno + "," + saleBS.saleHead.ysje + "," + 0 + "," + GlobalInfo.syjStatus.syjh + "," + ManipulateStr.PadLeft(String.valueOf(salepay.fphm), 12, '0') + "," + GlobalInfo.sysPara.commMerchantId + "," + money;
			}
			else
			{
				line = "05," + cardno + "," + saleBS.saleHead.ysje + "," + 0 + "," + GlobalInfo.syjStatus.syjh + "," + ManipulateStr.PadLeft(String.valueOf(salepay.fphm), 12, '0') + "," + GlobalInfo.sysPara.commMerchantId + "," + Convert.toDouble(money) * -1;
			}

			line = Gbyw_MzkVipModule.getDefault().sendData(line);

			if (line == null)
				return false;

			String[] item = line.split(",");

			if (item == null)
				return false;

			if (item.length > 0)
			{
				if (!item[0].equals("0"))
				{
					new MessageBox(Gbyw_MzkVipModule.getDefault().getError(item[0]));
					return false;
				}
			}

			salepay.payno = cardno;

			if (item.length > 5)
			{
				salepay.kye = ManipulatePrecision.doubleConvert(Convert.toDouble(item[5]), 2, 1);

				if (SellType.ISBACK(this.saleBS.saletype))
					new MessageBox("退货后零钱包余额:" + salepay.kye);
				else
					new MessageBox("消费后零钱包余额:" + salepay.kye);
			}

			return true;
		}

		return false;
	}

	public boolean cancelPay()
	{
		String line = "05," + salepay.payno + "," + saleBS.saleHead.ysje + "," + 0 + "," + GlobalInfo.syjStatus.syjh + "," + ManipulateStr.PadLeft(String.valueOf(salepay.fphm), 12, '0') + "," + GlobalInfo.sysPara.commMerchantId + "," + String.valueOf(salepay.je);

		line = Gbyw_MzkVipModule.getDefault().sendData(line);

		if (line == null)
			return false;

		String[] item = line.split(",");

		if (item == null)
			return false;

		if (item.length > 0)
		{
			if (!item[0].equals("0"))
			{
				new MessageBox(Gbyw_MzkVipModule.getDefault().getError(item[0]));
				return false;
			}
		}

		if (item.length > 5)
			new MessageBox("撤销后零钱包余额:" + ManipulatePrecision.doubleConvert(Convert.toDouble(item[5]), 2, 1));

		return true;
	}
}
