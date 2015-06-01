package device.Printer;

import com.efuture.javaPos.Global.Language;


public class U288T70Parallel_Printer extends Parallel_Printer
{
	public String getDiscription() 
	{
		return Language.apply("U288-T70标准并口打印机");
	}
	
    public void cutPaper_Normal()
    {
    	for (int i = 0;i < cutLine;i++)
    	{
    		printLine_Normal("\n");
    	}
    	
    	char[] cmd = {(char)0x1D,(char)0x56,(char)0x00};
    	//port.sendString(String.valueOf(cmd));
    	
    	for (int i = 0; i < cmd.length; i++)
    	{
    		port.sendChar(cmd[i]);
    	}
    }
    
}
