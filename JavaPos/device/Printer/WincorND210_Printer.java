package device.Printer;

import com.efuture.javaPos.Global.Language;



public class WincorND210_Printer extends Serial_Printer
{
	public String getDiscription()
	{
		return Language.apply("Wincor ND210串口黑标打印机");
	}  
	    
	    public void cutPaper_Normal()
	    {	    	
	    	port.sendChar((char) 0x1D);
	    	port.sendChar((char) 0x56);
	    	port.sendChar((char) 0x02);

//	    	port.sendChar((char) 0x0C);

	    	port.sendChar((char)0x1b);
	    	port.sendChar((char)0x69);
	    	
	    	setEnable(true);
	    }
	    
	    public void setEnable(boolean enable)
	    {
	    	port.sendChar((char) 0x1B);
	    	port.sendChar((char) 0x40);
	    	
	    	//找到黑标位置
	    	port.sendChar((char) 0x1B);
	    	port.sendChar((char) 0x77);
	    	port.sendChar((char) 0x6E);
	    	port.sendChar((char) 0x33);
	    	port.sendChar((char) 0x03);
	    	port.sendChar((char) 0x03);
	    }
}
