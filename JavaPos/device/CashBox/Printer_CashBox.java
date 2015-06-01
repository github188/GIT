package device.CashBox;

import java.io.File;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Device.Interface.Interface_CashBox;
import com.efuture.javaPos.Device.Interface.Interface_Printer;
import com.efuture.javaPos.Global.Language;

import device.DeviceInfo;

public class Printer_CashBox implements Interface_CashBox
{
	char[] opencmd = null;
	Interface_Printer printer = null;
	boolean first = false;

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

	public void setTempPrinter(Interface_Printer temp)
	{
		printer = temp;
	}

	public boolean open()
	{
		String printtype = "", openstr = "";
		if (DeviceName.deviceCashBox != null)// M:DeviceName.deviceCashBox:如"IBM4610-TF7,"
		{
			String[] arg = DeviceName.deviceCashBox.split(",");
			printtype = arg[0];
			if (arg.length > 1)
				openstr = arg[1];
		}

		if (printtype.equalsIgnoreCase("WincorI8"))
		{
			opencmd = new char[] { 0x1b, 0x70, 0x00, 0x10, 0x20 };
		}
		else if (printtype.equalsIgnoreCase("Hisense"))
		{
			opencmd = new char[] { 0x1b, 0x70, 0x00, 0x50, 0x10 };
		}
		else if (printtype.equalsIgnoreCase("EPSON320"))
		{
			opencmd = new char[] { 0x1b, 0x3d, 0x01, 0x1b, 0x70, 0x30, 0x20, 0x31, 0x30, 0x20, 0x32, 0x30 };
		}
		else if (printtype.equalsIgnoreCase("EPSONTM58")) // M:ESC,=,SOH,ESC,p,0,space,1
		{
			opencmd = new char[] { 0x1b, 0x3d, 0x01, 0x1b, 0x70, 0x30, 0x20, 0x31 };
		}
		else if (printtype.equalsIgnoreCase("IBM4679"))
		{
			opencmd = new char[] { (char) 28 };
		}
		// M：
		else if (printtype.equalsIgnoreCase("IBM4610-TF7"))
		{

			opencmd = new char[] { 0x1b, 0x70, 0x00, 0x10, 0x20, 0x1b, 0x70, 0x00, 0x10, 0x20 };

		}
		else if ((printtype.equalsIgnoreCase("Windows")))
		{
			opencmd = new char[] { 's' };
		}
		else
		// 其他
		{
			if (openstr != null && openstr.trim().length() > 0)
			{
				opencmd = DeviceInfo.convertCmdStringToCmdChar(openstr);
			}
			else
			{
				opencmd = new char[] { 0x1b, 0x70, 0x00, 0x32, 0xc8 };
			}
		}

		if (printer == null)
		{
			return Printer.getDefault().getStatus();
		}
		else
		{
			return true;
		}
	}

	public void openCashBox()
	{
		if (String.valueOf(opencmd).equals("s"))
		{
			Printer.getDefault().cutPaper_Normal();
			return;
		}

		if (printer == null)
		{
			Printer.getDefault().printer.printLine_Normal(String.valueOf(opencmd));
		}
		else
		{
			printer.printLine_Normal(String.valueOf(opencmd));

		}
	}

	public void setEnable(boolean enable)
	{
	}

	public Vector getPara()
	{
		Vector v = new Vector();
		// v.add(new String[] { Language.apply("打印机类型"), "WincorI8", "Hisense",
		// "EPSON320", "IBM4679","Else" });
		v.add(new String[] { Language.apply("打印机类型"), "WincorI8", "Hisense", "EPSON320", "EPSONTM58", "IBM4679", "IBM4610-TF7", "Windows", "Else" });
		v.add(new String[] { Language.apply("开钱箱命令") });

		return v;
	}

	public String getDiscription()
	{
		return Language.apply("各类接打印机的钱箱");
	}
}
