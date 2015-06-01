package bankpay.Payment;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Jel_PaymentMzk extends Shop_PaymentMzk
{
	public Jel_PaymentMzk()
	{
	}

	public Jel_PaymentMzk(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public Jel_PaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public boolean findMzkInfo(String track1, String track2, String track3)
	{
		if (!(checkCardFaceNo(track2)))
			return false;

		return super.findMzkInfo(track1, track2, track3);
	}

	public boolean findMzk(String track1, String track2, String track3)
	{
		if (!(checkCardFaceNo(track2)))
			return false;

		return super.findMzk(track1, track2, track3);
	}

	public boolean checkCardFaceNo(String track2)
	{
		StringBuffer buffer = new StringBuffer();
		if (new TextBox().open("请输入" + GlobalInfo.sysPara.mzkChkLength + "位校验码", "校验码", "提示:请输入交易检验码", buffer, 0D, 0D, false))
		{
			String chkCode = track2;

			if (chkCode.indexOf("=") > 0)
				chkCode = chkCode.split("=")[0];

			chkCode = chkCode.substring(chkCode.length() - GlobalInfo.sysPara.mzkChkLength);
			if (!buffer.toString().trim().substring(0, GlobalInfo.sysPara.mzkChkLength).equals(chkCode))
			{
				new MessageBox("校验码输入有误");
				return false;
			}

			return true;
		}

		new MessageBox("请输入校验码");
		return false;
	}
}