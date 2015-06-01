package custom.localize.Cczz;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentFjk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Cczz_PaymentFjk extends PaymentFjk {
	
	public Cczz_PaymentFjk()
	{
	}
	
	public Cczz_PaymentFjk(PayModeDef mode, SaleBS sale)
	{
		super(mode,sale);
	}
	
	public Cczz_PaymentFjk(SalePayDef pay, SaleHeadDef head)
	{
		super(pay,head);
	}
	
	public String[] parseFjkTrack(String track1, String track2, String track3)
	{
		String[] s = new String[3];
		
		if (track2.length() > 16)
		{
			track2 = track2.substring(0, 16);
		}
		
		s[0] = track1;
		s[1] = track2;
		s[2] = track3;
		
		return s;
	}
	
	public boolean createSalePay(double moneyA,double moneyB,double moneyF)
	{
		try
		{
			StringBuffer buff = new StringBuffer();
			
			boolean issucc = false;
			
			if (moneyA > 0)
			{
				if (!issucc)
				{
					mzkreq.memo = FJK_A;				// 标记券类型
					issucc = super.createSalePay(String.valueOf(moneyA));
					
					if (!issucc) return false;
					
					salepay.payname = FJKNAME_A;		// 修改付款名称
					
					addMessage(this,buff);
				}
			}
			
			if (moneyB > 0)
			{
				if (!issucc)
				{
					mzkreq.memo = FJK_B;				// 标记券类型
					issucc = super.createSalePay(String.valueOf(moneyB));
					
					if (!issucc) return false;
					
					salepay.payname = FJKNAME_B;		// 修改付款名称
					
					addMessage(this,buff);
				}
				else
				{
					if (!CreateNewjPayment(FJK_B, FJKNAME_B, moneyB,buff)) return false;
				}
			}
			
			if (moneyF > 0)
			{
				if (!issucc)
				{
					mzkreq.memo = FJK_F;				// 标记券类型
					issucc = super.createSalePay(String.valueOf(moneyF));
					
					if (!issucc) return false;
					
					salepay.payname = FJKNAME_F;		// 修改付款名称
					
					addMessage(this,buff);
				}
				else
				{
					if (!CreateNewjPayment(FJK_F, FJKNAME_F, moneyF,buff)) return false;
				}
			}
			
			if (buff.length() > 0)  new MessageBox(buff.toString());
			
			return issucc;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	public void addMessage(PaymentFjk cpf,StringBuffer bufferStr)
	{
		
		try{
			bufferStr.append(cpf.salepay.payname+" 内余额为: " + Convert.appendStringSize("",ManipulatePrecision.doubleToString(cpf.salepay.kye - cpf.salepay.je),0,12,12,1) + "\n");
		}catch(Exception er)
		{
			er.printStackTrace();
		}
	}
}
