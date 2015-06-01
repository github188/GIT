package device.Printer;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Interface.Interface_Printer;
import com.efuture.javaPos.Global.Language;


public class Epson320_Printer extends Serial_Printer implements Interface_Printer
{
	//支持下列打印机
	//epson tm-u220p
	
    public boolean open()
    {
        if (super.open())
        {
            char[] opencmd = { 0x1b, 0x3d, 0x01 };
            printLine_Normal(String.valueOf(opencmd));

            return true;
        }
        else
        {
            return false;
        }
    }

    public String getDiscription()
    {
        return Language.apply("EPSON320款机的串口打印机");
    }

    public void cutPaper_Normal()
    {
        for (int i = 0; i < cutLine; i++)
        {
            printLine_Normal("\n");
        }

        char[] opencmd = { 0x1d, 0x56, 0x42, 0x00 };
        printLine_Normal(String.valueOf(opencmd));
        
    	//
    	if (cutMsg)
    	{
    		new MessageBox(Language.apply("请从打印机撕下已打印的单据"));
    	}
    }
}
