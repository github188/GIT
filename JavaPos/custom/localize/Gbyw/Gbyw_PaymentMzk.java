package custom.localize.Gbyw;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Gbyw_PaymentMzk extends Payment
{
	protected String track;
	protected String pass;

	public void setTrack(String track)
	{
		this.track = track;
	}

	public void setPass(String pass)
	{
		this.pass = pass;
	}

	public Gbyw_PaymentMzk()
	{

	}

	public void init(Gbyw_MzkEvent evt)
	{

	}

	public Gbyw_PaymentMzk(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public Gbyw_PaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public int getMsrInputMode()
	{
		return TextBox.MsrInput;
	}

	public String getYeLable()
	{
		return "余    额";
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

	public boolean createSalePay(String cardno, String money)
	{
		String line = "";

		for (int i = 0; i < saleBS.salePayment.size(); i++)
		{
			SalePayDef spd = (SalePayDef) saleBS.salePayment.get(i);
			if (spd.paycode.equals("0402") && spd.payno.equals(cardno))
			{
				new MessageBox("同一张卡不允许多次付款!");
				return false;
			}
		}

		if (super.createSalePay(money))
		{
			if (SellType.ISBACK(this.saleBS.saletype))
			{
				StringBuffer seqno = new StringBuffer();
				TextBox txt = new TextBox();

				if (!txt.open("请输入原流水号", "流水号", "请输入面值卡交易原始流水号(小票号)", seqno, 0, 0, false, TextBox.AllInput)) { return false; }

				line = "03," + track + "," + money + "," + pass + "," + GlobalInfo.syjStatus.syjh + "," + ManipulateStr.PadLeft(String.valueOf(salepay.fphm), 12, '0') + "," + GlobalInfo.sysPara.commMerchantId + "," + GlobalInfo.syjStatus.syyh + "," + ManipulateStr.PadLeft(seqno.toString(), 12, '0');
			}
			else
			{
				line = "02," + track + "," + String.valueOf(money) + "," + pass + "," + GlobalInfo.syjStatus.syjh + "," + ManipulateStr.PadLeft(String.valueOf(salepay.fphm), 12, '0') + "," + GlobalInfo.sysPara.commMerchantId + "," + GlobalInfo.syjStatus.syyh;
			}

			line = Gbyw_MzkVipModule.getDefault().sendData(line);

			if (line == null)
				return false;

			String[] item = line.split(",");

			if (item == null)
				return false;

			if (item.length > 0)
			{
				if (!item[0].equals("203"))
				{
					new MessageBox(Gbyw_MzkVipModule.getDefault().getError(item[0]));
					return false;
				}
			}

			salepay.payno = track; // 始终记录内卡号

			if (item.length > 2)
				salepay.je = ManipulatePrecision.doubleConvert(Convert.toDouble(item[2]), 2, 1);

			if (item.length > 3)
			{
				salepay.kye = ManipulatePrecision.doubleConvert(Convert.toDouble(item[3]), 2, 1);

				if (SellType.ISBACK(this.saleBS.saletype))
					new MessageBox("退货后卡余额:" + salepay.kye);
				else
					new MessageBox("消费后卡余额:" + salepay.kye);
			}

			salepay.str2 = track;
			salepay.str5 = pass;
			return true;
		}

		new MessageBox(Gbyw_MzkVipModule.getDefault().getError(line));
		salepay = null;

		return false;
	}

	public boolean cancelPay()
	{
		String line = "03," + salepay.str2 + "," + String.valueOf(salepay.je) + "," + salepay.str5 + "," + GlobalInfo.syjStatus.syjh + "," + ManipulateStr.PadLeft(String.valueOf(salepay.fphm), 12, '0') + "," + GlobalInfo.sysPara.commMerchantId + "," + GlobalInfo.syjStatus.syyh + "," + ManipulateStr.PadLeft(String.valueOf(salepay.fphm), 12, '0');

		line = Gbyw_MzkVipModule.getDefault().sendData(line);

		if (line == null)
			return false;

		String[] item = line.split(",");

		if (item == null)
			return false;

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
			if (salepay.je != ManipulatePrecision.doubleConvert(Convert.toDouble(item[2]), 2, 1))
			{
				new MessageBox("消费与撤销前后金额不一致!");
				return false;
			}
		}

		if (item.length > 3)
			new MessageBox("撤销后卡余额:" + ManipulatePrecision.doubleConvert(Convert.toDouble(item[3]), 2, 1));

		return true;
	}

}