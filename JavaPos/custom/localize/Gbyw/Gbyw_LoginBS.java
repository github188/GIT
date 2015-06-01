package custom.localize.Gbyw;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.LoginBS;

public class Gbyw_LoginBS extends LoginBS
{
	public boolean loginDone()
	{
		if (super.loginDone())
		{
			if (Gbyw_MzkVipModule.getDefault().initConnection())
			{
				String line = "01," + GlobalInfo.syjStatus.syjh + "," + GlobalInfo.sysPara.commMerchantId;
				// String line = "01," + "15010001" + "," +
				// GlobalInfo.sysPara.commMerchantId;
				line = Gbyw_MzkVipModule.getDefault().sendData(line);

				if (line != null && !line.equals("0"))
					new MessageBox(Gbyw_MzkVipModule.getDefault().getError(line));
			}

			return true;
		}
		return false;
	}

}
