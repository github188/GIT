package custom.localize.Jcgj;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;

public class Jcgj_PaymentMzkPaper extends Jcgj_PaymentMzk
{
	public boolean findMzk(String track1, String track2, String track3)
	{
		StringBuffer cuponNo = new StringBuffer();
		boolean done = true;
		done = new TextBox().open("请扫描储值券", "券号", "扫描储值券", cuponNo, 0, 0, false);
		if (!done) return false;
		else
		{
			String cardSell = Convert.increaseChar(ConfigClass.Market + ConfigClass.CashRegisterCode, ' ', 10)
					+ Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 10) + Convert.increaseChar(cuponNo.toString(), ' ', 16);
			Jcgj_Svc svc;
			svc = new Jcgj_Svc("svc_inq", null, cardSell);
			Jcgj_YsCardDef card = new Jcgj_YsCardDef();
			if (svc.doYsCard(card))
			{
				mzkret.cardno = card.cardNo;
				mzkret.cardname = card.name;
				mzkret.ye = card.ye;
				return true;
			}
			else return false;
		}
	}

	protected String getDisplayAccountInfo()
	{
		return "输入券号";
	}

	public int getAccountInputMode()
	{
		return TextBox.IntegerInput; //允许键盘和刷卡输入	
	}

	public boolean checkMzkMoneyValid()
	{
		if (!super.checkMzkMoneyValid()) { return false; }

		// 券必须一次付完
		if (ManipulatePrecision.doubleCompare(salepay.ybje, this.getAccountYe(), 2) != 0)
		{
			if (new MessageBox(salepay.payname + "的每张券必须一次性付完!\n是否将剩余部分计入损溢？", null, true).verify() == GlobalVar.Key1)
			{
				// num1记录券付款溢余部分
				//salepay.num1 = ManipulatePrecision.sub(ManipulatePrecision.mul(Double.parseDouble(this.getAccountYe()), salepay.hl), ManipulatePrecision.mul(salepay.ybje, salepay.hl));
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
}
