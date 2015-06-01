package device.Printer;

import java.io.File;

import com.efuture.javaPos.Global.Language;

public class IBM4679Serial_Printer extends IBM4610Serial_Printer
{
	
	public boolean passPage_Normal() 
	{
		return false;
	}
	
	public String getDiscription() 
	{
		//类名写错了，应该是4679
		return Language.apply("IBM4679无黑标功能串口打印机");
	}
	
	public void printLine_Normal(String printStr)
	{
		super.printLine_Normal(printStr);
	}
}
