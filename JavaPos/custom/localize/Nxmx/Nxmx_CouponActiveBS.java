package custom.localize.Nxmx;

import java.util.Vector;
import com.efuture.javaPos.Communication.NetService;
import  custom.localize.Bszm.Bszm_NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import custom.localize.Bszm.Bszm_AccessDayDB;

public class Nxmx_CouponActiveBS
{
	// 激活券
	public boolean activeCoupon(Vector list, double maxje,String[] retValue)
	{
		String track2 = "";		
	//	String[] retFlag = new String[list.size()];
		
		try
		{
			track2 = convertTrack2(list);

			if (track2.trim().equals(""))
				return false;
						
			Nxmx_PaymentCoupon activeCoupon = new Nxmx_PaymentCoupon();
		    activeCoupon.setCurTradeType("06");
		    activeCoupon.setActivemaxje(maxje);
		    
			if (activeCoupon.activeCoupon("", track2, ""))
			{
				for (int i=0; i<retValue.length; i++)
					retValue[i] = "1";
				
				//status=0,表示全部激活
				if (activeCoupon.mzkret.status.equals("0"))
				{
					//上传活动券
					Bszm_AccessDayDB dayDB = (Bszm_AccessDayDB)AccessDayDB.getDefault();
					Bszm_NetService netService = (Bszm_NetService)NetService.getDefault();
					if (dayDB.writeActivCoupon(activeCoupon.mzkreq.fphm, track2))
						netService.sendPopCoupon(String.valueOf(activeCoupon.mzkreq.fphm), track2);
					
					return true;
				}
				else if (!activeCoupon.mzkret.memo.trim().equals(""))
				{
					String[] tmpRetFlag = activeCoupon.mzkret.memo.trim().split(",");
					for (int i=0; i<tmpRetFlag.length; i++)
					{
						int index= list.indexOf(tmpRetFlag[i].trim());
						if (index !=-1)
							retValue[index] = "0";
					}
					return false;
				}
			}
			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	private String convertTrack2(Vector list)
	{
		String track2 = "";
		try
		{
			if (list == null && list.size() < 1)
				return track2;

			for (int i = 0; i < list.size(); i++)
			{
				String tmpStr = (String) list.get(i);
				track2 += tmpStr + ((i == list.size() - 1) ? "" : ",");
			}
			return track2;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return track2;
		}
	}
}
