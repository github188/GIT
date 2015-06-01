package device.Printer;

import com.efuture.javaPos.Global.Language;

public class IBM4610TF7_Printer extends Serial_Printer
{
    public void cutPaper_Normal()
    {
        super.cutPaper_Normal();

        char[] con = { 0x1b, 'i' };

        port.sendString(String.valueOf(con));
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
            char[] con = { 0x1b, 'W', '1' };
            char[] con1 = { 0x1b, 'h', '1' };
            port.sendString(String.valueOf(con));
            port.sendString(String.valueOf(con1));
        }
        else
        {
            char[] con = { 0x1b, 'W', 0x00 };
            char[] con1 = { 0x1b, 'h', 0x00 };
            port.sendString(String.valueOf(con));
            port.sendString(String.valueOf(con1));
        }
    }

    public String getDiscription()
    {
        return Language.apply("4610-TF7串口打印机");
    }
}
