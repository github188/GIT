package device.Printer;

import com.efuture.javaPos.Device.Interface.Interface_Printer;
import com.efuture.javaPos.Global.Language;

public class WincorTH80_Printer extends Serial_Printer implements Interface_Printer
{
    public void printLine_Normal(String line)
    {
        //选择简体中文
        port.sendChar((char) 0x1b);
        port.sendChar((char) 0x74);
        port.sendChar((char) 0x90);
         
        port.sendString(line);
        
    }
    
	public String getDiscription() 
	{
		return Language.apply("WincorI8款机TH80串口打印机");
	}
}
