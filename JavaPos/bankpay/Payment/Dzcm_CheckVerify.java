package bankpay.Payment;

import com.efuture.javaPos.Global.GlobalInfo;


public class Dzcm_CheckVerify
{
	public static boolean verifyDzcmCheckbit(String dzcm)
	{
		if (GlobalInfo.sysPara.verifyDzcmname.trim().equals("EAN13")) // 沈阳雍户
		{
			return EAN13_VerifyDzcmCheckbit(dzcm);
		}
		else if (GlobalInfo.sysPara.verifyDzcmname.trim().equals("UPCA")) 
		{
			return UPCA_VerifyDzcmCheckbit(dzcm);
		}
		else if (GlobalInfo.sysPara.verifyDzcmname.trim().equals("EAN13_18")) 
		{
			return EAN13_18_VerifyDzcmCheckbit(dzcm);
		}
		else if (GlobalInfo.sysPara.verifyDzcmname.trim().equals("EAN13_UPCA")) // 广州百货
		{
			if (EAN13_VerifyDzcmCheckbit(dzcm) || UPCA_VerifyDzcmCheckbit(dzcm)) return true;
			
			return false;
		}
		
		return true;
	}
	
	private static boolean EAN13_VerifyDzcmCheckbit(String dzcm)
	{
		int odd = 0;	// 奇数
		int even = 0;	// 偶数
		
		for (int i = 0; i < dzcm.length() - 1; i++)
		{
			if (i % 2 == 0)
			{
				odd = odd + Integer.parseInt(dzcm.substring(i, i + 1));
			}
			else
			{ 
				even = even + Integer.parseInt(dzcm.substring(i, i + 1));
			}
		}
		
		int a = odd + even * 3;
		
		int checkbit = Integer.parseInt(dzcm.substring(dzcm.length() - 1, dzcm.length()));
		
		if ((10 - (a % 10)) % 10 != checkbit)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	private static boolean UPCA_VerifyDzcmCheckbit(String dzcm)
	{
 		int odd = 0;	// 奇数
		int even = 0;	// 偶数
		
		for (int i = 0; i < dzcm.length() - 1; i++)
		{
			if (i % 2 == 0)
			{
				odd = odd + Integer.parseInt(dzcm.substring(i, i + 1));
			}
			else
			{ 
				even = even + Integer.parseInt(dzcm.substring(i, i + 1));
			}
		}
		
		int b = odd * 3 + even;
		
		int checkbit = Integer.parseInt(dzcm.substring(dzcm.length() - 1, dzcm.length()));
		
		if ((10 - (b % 10)) % 10 != checkbit)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	private static boolean EAN13_18_VerifyDzcmCheckbit(String dzcm)
	{
		int odd = 0;	// 奇数
		int even = 0;	// 偶数
		
		for (int i = 0; i < dzcm.length() - 1; i++)
		{
			if (i % 2 == 0)
			{
				odd = odd + Integer.parseInt(dzcm.substring(i, i + 1));
			}
			else
			{ 
				even = even + Integer.parseInt(dzcm.substring(i, i + 1));
			}
		}
		
		int a = odd + even * 3;
		
		int checkbit = Integer.parseInt(dzcm.substring(dzcm.length() - 1, dzcm.length()));
		
		int b = (10 - (a % 10)) % 10 ;
		
		int re = (b >= 10 ? 0 : b);
		
		if (re != checkbit)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
}
