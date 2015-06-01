package device.Printer;

import com.efuture.javaPos.Global.Language;



public class SundarPT2800_Printer extends Parallel_Printer
{
	public String getDiscription()
	{
		return Language.apply("桑达PT2800/M180/M280款机的并口黑标打印机");
	}
	
	public boolean passPage_Normal() 
	{
		port.sendChar((char)0x1D);
    	port.sendChar((char)0x0C);
    	
		return true;
	}
	
}
