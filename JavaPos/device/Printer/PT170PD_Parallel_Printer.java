package device.Printer;

import com.efuture.javaPos.Global.Language;

public class PT170PD_Parallel_Printer extends Parallel_Printer
{
	public String getDiscription() 
	{
		return Language.apply("PT170PD并口打印机");
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
            char[] con1 = { 0x1b, 0x45,0x01};
            port.sendString(String.valueOf(con1));
            
            char[] con2 = { 0x1c, 0x21,0x04};
            port.sendString(String.valueOf(con2));
            
            char[] con3 = { 0x1c, 0x21,0x08};
            port.sendString(String.valueOf(con3));
        }
        else
        {	
            char[] con = { 0x1b, 0x45,0x00};
            port.sendString(String.valueOf(con));
            
            //char[] con2 = { 0x1c, 0x57,0x00};
            //port.sendString(String.valueOf(con2));
        	char[] con3 = { 0x1c, 0x21,0x00};
            port.sendString(String.valueOf(con3));
        }
    }
}
