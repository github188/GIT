package device.Printer;

import com.efuture.javaPos.Global.Language;

public class EpsonTMU375_Printer extends Serial_Printer
{
	public void printLine_Normal(String printStr)
	{
		if (printStr != null)
		{
			char[] opencmd = printStr.toCharArray();
			
			char curChar;
			
	        for (int i = 0; i < opencmd.length; i++)
	        {
	        	curChar = opencmd[i];
	        	if (curChar > 127)
	        	{
	        		// 调用中文字库
	        		port.sendChar((char)0x1c);
	        		port.sendChar((char)0x26);
	        		
		        	byte a[] = String.valueOf(curChar).getBytes();
		        	System.out.println(Integer.toHexString(Integer.parseInt(String.valueOf(a[0])) - 128));
		        	System.out.println(Integer.toHexString(Integer.parseInt(String.valueOf(a[1])) - 128));
		        	
		        	port.sendChar((char)(Integer.parseInt(String.valueOf(a[0])) - 128));
		        	port.sendChar((char)(Integer.parseInt(String.valueOf(a[1])) - 128));
		        	
		        	// 关闭字库
		        	port.sendChar((char)0x1c);
		        	port.sendChar((char)'.');
	        	}
	        	else 
	        	{
	        		port.sendChar(curChar);
	        	}
	        }
		}
	}
	
    public String getDiscription()
    {
        return Language.apply("EPSON TM-U375的串口打印机");
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
