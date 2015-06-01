package device.Printer;

import com.efuture.javaPos.Global.Language;

public class ND77_Printer extends Serial_Printer
{
    public void setEnable(boolean enable)
    {
    	//SendComPrinterChar(0x1b);
    	//SendComPrinterChar(0x74);
//    	SendComPrinterChar(0x80);	//单向打印模式，打印速度慢，打印字体精细
    	//SendComPrinterChar(0x81);	//双向打印模式，打印速度快，打印字体粗糙
    	port.sendChar((char)0x1b);
    	port.sendChar((char)0x74);
    	port.sendChar((char)0x81);
    }
    
    public void printLine_Normal(String printStr)
    {
    	port.sendChar((char)0x1b);
    	port.sendChar((char)0x63);
    	port.sendChar((char)0x30);
    	port.sendChar((char)0x2);//指定左栈
        port.sendString(printStr);
    }
    
    public void printLine_Journal(String printStr)
    {
    	port.sendChar((char)0x1b);
    	port.sendChar((char)0x63);
    	port.sendChar((char)0x30);
    	port.sendChar((char)0x1);//指定右栈
    	port.sendString(printStr);
    }
    
    public void printLine_Slip(String printStr)
    {	
    	port.sendChar((char)0x1b);
    	port.sendChar((char)0x63);
    	port.sendChar((char)0x30);
    	port.sendChar((char)0x4);//指定平推栈
    	port.sendString(printStr);
//        printLine_Normal(printStr);//山东银座
    }
    
    public void cutPaper_Slip()
    {
      super.cutPaper_Slip();
      port.sendChar((char)0x0c);
    }
    
	public String getDiscription()
	{
		return Language.apply("ND77款机的串口打印机");
	}
	
}
