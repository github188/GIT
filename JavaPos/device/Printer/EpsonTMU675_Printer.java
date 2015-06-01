package device.Printer;

import com.efuture.javaPos.Global.Language;

public class EpsonTMU675_Printer extends Serial_Printer
{	
    public String getDiscription()
    {
        return Language.apply("EPSON TM-U675的串口打印机");
    }
    
    public void printLine_Slip(String printStr)
    {
    	port.sendChar((char) 0x1b);
    	port.sendChar((char) 0x63);
    	port.sendChar((char) 0x30);
    	port.sendChar((char) 0x04);
    	printLine_Normal(printStr);
    }    
    
    public void cutPaper_Slip()
    {
    	super.cutPaper_Slip();
    	port.sendChar((char) 0x1b);
    	port.sendChar((char) 0x63);
    	port.sendChar((char) 0x30);
    	port.sendChar((char) 0x03);
    }
    
    public void setEnable(boolean enable)
    {
    	super.setEnable(enable);
    	port.sendChar((char) 0x1b);
    	port.sendChar((char) 0x63);
    	port.sendChar((char) 0x30);
    	port.sendChar((char) 0x03);
    }
    
}
