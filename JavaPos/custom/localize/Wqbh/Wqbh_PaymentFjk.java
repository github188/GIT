package custom.localize.Wqbh;


import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentFjk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Wqbh_PaymentFjk extends PaymentFjk
{
	public Wqbh_PaymentFjk()
	{
		super();
	}

	public Wqbh_PaymentFjk(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public Wqbh_PaymentFjk(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public String[] parseFjkTrack(String track1, String track2, String track3)
	{
		//解析磁道
		if (fjktypeChoice == 0)
		{
			String[] s = new String[3];
			s[0] = track1;
			s[1] = track2;
			s[2] = track3;
			return s;
		}
		else if (fjktypeChoice == 1)
		{
			if (track3 == null || track3.length() < 84)
			{
				new MessageBox("无效联名VIP卡,请重新刷卡!");
				return null;
			}

			//从三磁道的第69位起读出13位的会员卡号
			String[] s = new String[3];
			s[0] = track1;
			s[1] = track3.substring(69, 82);
			s[2] = track2;
			return s;
		}
		else
		{
			new MessageBox("请选择有效的返券卡类型!");
			return null;
		}
	}
}
