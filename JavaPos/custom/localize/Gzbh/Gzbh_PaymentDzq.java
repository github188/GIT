package custom.localize.Gzbh;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Payment.PaymentMzkForm;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Gzbh_PaymentDzq extends PaymentMzk
{
	public Gzbh_PaymentDzq()
	{
	}

	public Gzbh_PaymentDzq(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	// 该构造函数用于红冲小票时,通过小票付款明细创建对象
	public Gzbh_PaymentDzq(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public String GetMzkCzFile()
	{
		return ConfigClass.LocalDBPath + "/Dzq_" + mzkreq.seqno + ".cz";
	}

	//判断是否电子券冲正文件
	public boolean isCzFile(String filename)
	{
		if (filename.startsWith("Dzq_") && filename.endsWith(".cz"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean checkMzkMoneyValid()
	{
		if (!super.checkMzkMoneyValid()) return false;

		// 纸券必须一次性使用完
		if (ManipulatePrecision.doubleCompare(salepay.ybje, this.getAccountYe(), 2) != 0)
		{
			if (new MessageBox(salepay.payname + "的每张券必须一次性付完!\n是否将剩余部分计入损溢？", null, true).verify() == GlobalVar.Key1)
			{
				// num1记录券付款溢余部分
				salepay.num1 = ManipulatePrecision.sub(ManipulatePrecision.mul(this.getAccountYe(), salepay.hl),
														Math.min(salepay.je, this.saleBS.calcPayBalance()));
				salepay.ybje = this.getAccountYe();
				salepay.je = ManipulatePrecision.doubleConvert(salepay.ybje * salepay.hl, 2, 1);
				return true;
			}
			else
			{
				return false;
			}
		}
		return true;
	}

	public SalePayDef inputPay(String money)
	{
		try
		{
			// 退货小票不能使用,退货扣回按销售算
			if (checkMzkIsBackMoney())
			{
				new MessageBox("退货时不能使用" + paymode.name);
				return null;
			}

			// 先检查是否有冲正未发送
			if (!sendAccountCz()) return null;

			// 打开明细输入窗口
			new PaymentMzkForm().open(this, saleBS);

			// 如果付款成功,则salepay已在窗口中生成
			return salepay;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return null;
	}

	public boolean findMzk(String track1, String track2, String track3)
	{
		if (track2 != null && track2.length() > 13)
		{
			new MessageBox("磁道不合法，请确认是否本公司券");
			return false;
		}
		return super.findMzk(track1, track2, track3);
	}
}
