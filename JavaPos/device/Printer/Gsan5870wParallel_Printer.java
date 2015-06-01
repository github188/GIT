package device.Printer;

import com.efuture.javaPos.Global.Language;

public class Gsan5870wParallel_Printer extends Parallel_Printer
{
    public void cutPaper_Normal()
    {
    	super.cutPaper_Normal();
    	
    	// 关闭设备后，自动打印缓存内容
        close();
        open();
    }
    
    // 钱箱的命令格式(Hisence的钱箱命令格式)
    String str = String.valueOf(new char[] { 0x1b, 0x70, 0x00, 0x50, 0x10 });
    public void printLine_Normal(String printStr)
    {
    	super.printLine_Normal(printStr);

    	// 控制打印机钱箱
    	if (str.equals(printStr))
    	{
    		// 关闭设备后，自动打印缓存内容
    		close();
    		open();
    	}
    }
    
	public String getDiscription() 
	{
		return Language.apply("吉成5870w并口打印机");
	}
}