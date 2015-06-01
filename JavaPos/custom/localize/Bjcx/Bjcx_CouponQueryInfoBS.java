package custom.localize.Bjcx;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Logic.CouponQueryInfoBS;

public class Bjcx_CouponQueryInfoBS extends CouponQueryInfoBS
{

	public boolean findHYK(String track1, String track2, String track3)
	{
		try
		{
			return super.findHYK(track1, track2, track3);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("操作异常：" + ex.getMessage());
			return false;
		}
	}
}
