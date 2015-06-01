package device.CashBox;

import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Interface.Interface_CashBox;
import com.efuture.javaPos.Global.Language;

//DLL要传入主板类型
//iType:1:370; 2:9575; 3:460/560; 4:563/760/761/85610/6921 5:3710

public class HisenseDLL_CashBox implements Interface_CashBox
{
	int iType = 4;
	
    public boolean canCheckStatus()
    {
        return false;
    }

    public void close()
    {
    }

    public boolean getOpenStatus()
    {
    	return false;
    }

    public boolean open()
    {
        // 得到配置的主板类型
        if (DeviceName.deviceCashBox.length() > 0)
        {
        	iType = Convert.toInt(DeviceName.deviceCashBox);
        }
        
        return true;
    }

    public void openCashBox()
    {
    	try
		{
			CommonMethod.waitForExec("HK600OpenCashBox.exe " + iType);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
    }

    public void setEnable(boolean enable)
    {
    }

	public Vector getPara() {
		Vector v = new Vector();
		v.add(new String[]{ Language.apply("主板类型") + "(1:370|2:9575|3:460/560|4:563/760/761/85610/6921|5:3710)", "1", "2", "3", "4", "5"});
		return v;
	}

	public String getDiscription() 
	{
		return Language.apply("海信接主机的钱箱");
	}
}
