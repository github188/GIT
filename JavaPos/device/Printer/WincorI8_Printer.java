package device.Printer;

import com.efuture.javaPos.Device.Interface.Interface_Printer;
import com.efuture.javaPos.Global.Language;


public class WincorI8_Printer extends Serial_Printer implements Interface_Printer
{
    public boolean open()
    {
        if (super.open())
        {
            //选择简体中文
            char[] opencmd = { 0x1b, 0x74, 0x80, 0x1b, 0x52, 0x80 };
            printLine_Normal(String.valueOf(opencmd));

            return true;
        }
        else
        {
            return false;
        }
    }
    
    public void printLine_Normal(String printStr)
    {
        boolean done = false;

        if (printStr.indexOf("Big&") == 0)
        {
            done = true;
            setBigChar(true);
        }

        if (done)
        {
            if (printStr.length() > 4)
            {
                printStr = printStr.substring(4);
            }
            else
            {
                printStr = "\n";
            }
        }

        super.printLine_Normal(printStr);

        if (done)
        {
            setBigChar(false);
        }
    }

    public void setBigChar(boolean status)
    {
        if (status)
        {
            char[] con = { 0x1b, 'E', '1' };
            port.sendString(String.valueOf(con));
        }
        else
        {
            char[] con = { 0x1b, 'E', 0x00 };
            port.sendString(String.valueOf(con));
        }
    }
    
	public String getDiscription()
	{
		return Language.apply("WincorI8款机的串口打印机");
	}
}
