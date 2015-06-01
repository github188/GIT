package device.CashBox;

import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Interface.Interface_CashBox;

//DLL要传入主板类型
//iType:1:370; 2:9575; 3:460/560; 4:563/760/761/85610/6921;
//	5:3710;6:640;7:CLE266M; 8:EPIAPD

public class Hisense_CashBox implements Interface_CashBox
{
	String requestFile = "C:\\javaPos\\request.txt";
	String  iType = "4";
	
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
        if (DeviceName.deviceCashBox.trim().length() > 0)
        {
        	iType = DeviceName.deviceCashBox.trim();
        }
        
        return true;
    }

    public void openCashBox()
    {
    	try
		{PrintWriter pw = CommonMethod.writeFile(requestFile);
		pw.write(iType);
		pw.close();

		// 调用接口模块
		if (PathFile.fileExist("c:\\JavaPOS\\javaposbank.exe"))
		{
			CommonMethod.waitForExec("c:\\JavaPOS\\javaposbank.exe HKCashdrawer", "javaposbank.exe");
		}
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
		v.add(new String[]{"主板类型(1:370|2:9575|3:460/560|4:563/760/761/85610/6921|5:3710|6:640|7:CLE266M|8:EPIAPD)","1","2","3","4","5","6","7","8"});
		return v;
	}

	public String getDiscription() 
	{
		return "新海信接主机的钱箱";
	}
}
