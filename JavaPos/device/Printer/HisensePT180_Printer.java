package device.Printer;

import com.efuture.javaPos.Global.Language;


public class HisensePT180_Printer extends WincorTH200_Printer 
{
	public String getDiscription()
	{
		return Language.apply("HisensePT180款机的串口黑标打印机");
	}
	
	public boolean passPage_Normal() 
	{
		// 指定坐标
	/*	port.sendChar((char)0x1D);
    	port.sendChar((char)0x28);
    	port.sendChar((char)0x46);
    	port.sendChar((char)0x04);
    	port.sendChar((char)0x00);
    	
    	port.sendChar((char)0x01);
    	port.sendChar((char)0x30);
    	port.sendChar((char)0x90);
    	port.sendChar((char)0x01);
    	
//		 执行指令
    	port.sendChar((char)0x1D);
    	port.sendChar((char)0x0C);*/
		
		port.sendChar((char)0x1D);
    	port.sendChar((char)0x28);
    	port.sendChar((char)0x46);
    	port.sendChar((char)0x04);
    	port.sendChar((char)0x00);
    	
    	port.sendChar((char)0x02);
    	port.sendChar((char)0x30);
    	port.sendChar((char)0xC8);
    	port.sendChar((char)0x00);
    	
    	port.sendChar((char)0x1D);
    	port.sendChar((char)0x0C);
    	
    	// 执行指令
    	/*port.sendChar((char)0x1D);
    	port.sendChar((char)0x56);
    	port.sendChar((char)0x42);
    	port.sendChar((char)0x00);*/
    	
		return true;
	}
	
	public void cutPaper_Normal() 
	{
		// 切纸命令
    	port.sendChar((char)0x1b);
    	port.sendChar((char)0x69);
	}
}
