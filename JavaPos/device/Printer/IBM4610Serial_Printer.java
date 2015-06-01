package device.Printer;

import com.efuture.javaPos.Global.Language;


// IBM 4694
public class IBM4610Serial_Printer extends Serial_Printer
{
    public void cutPaper_Normal()
    {
    	for (int i = 0;i < cutLine;i++)
    	{
    		printLine_Normal("\n");
    	}
    	
        port.sendChar((char) 0x1b);
        port.sendChar('d');
        port.sendChar((char) 0x01);
    }
    
	public boolean passPage_Normal() 
	{
		port.sendChar((char) 0x1D);
        port.sendChar((char) 0x0C);
        port.sendChar((char) 0x01);
        
		return true;
	}
	
	public String getDiscription() 
	{
		//类名写错了，应该是4679
		return Language.apply("IBM4679带黑标功能串口打印机");
	}
}
