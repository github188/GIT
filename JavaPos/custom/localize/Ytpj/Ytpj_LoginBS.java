package custom.localize.Ytpj;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.RdPlugins;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.LoginBS;

public class Ytpj_LoginBS  extends LoginBS
{

	public boolean loginDone()
	{
		if (super.loginDone())
		{
			//当不使用新的大会员接口，老会员接口自动签到参数开始时就自动签到
			if((GlobalInfo.sysPara.isAutoCheckIn.equals("Y")))
			{
				try
				{
					if (null == RdPlugins.getDefault().getPlugins1())
					{
						new MessageBox("加载第三方插件错误，请检查插件配置信息是否正确。。。");
						return false;
					}
					String check = "";
					if (RdPlugins.getDefault().getPlugins1().exec(9, GlobalInfo.sysPara.mktcode + "," + GlobalInfo.posLogin.gh)) check = (String) RdPlugins.getDefault().getPlugins1().getObject();

					if (check == null || check.length() < 0 )
					{
						new MessageBox("Of_CheckIn()接口调用失败!\n请手动签到");
					}
					else if (!check.substring(0, 2).equals("00"))
					{
						new MessageBox("Of_CheckIn()接口调用失败!\n请手动签到\n" + check.replaceAll("\\s+", " ").trim());
					}
					return true;
				}
				catch (Exception e)
				{
					e.printStackTrace();
					new MessageBox("自动签到出现异常\n\n" + e.getMessage());
				}	
			}
			return true;
		}
		return false;
	}

}
