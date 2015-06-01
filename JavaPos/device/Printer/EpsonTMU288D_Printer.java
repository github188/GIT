package device.Printer;

import com.efuture.javaPos.Global.Language;

public class EpsonTMU288D_Printer extends Serial_Printer
{
	public String getDiscription()
	{
		return Language.apply("EPSON TM-U288D串口黑标打印机(每页行数固定)");
	}

	public void printLine_Slip(String printStr)
	{
		port.sendChar((char) 0x1b);
		port.sendChar((char) 0x63);
		port.sendChar((char) 0x30);
		port.sendChar((char) 0x04);
		printLine_Normal(printStr);
	}

	public boolean passPage_Normal()
	{
		// 找到黑标位置
		port.sendChar((char) 0x1C);
		port.sendChar((char) 0x28);
		port.sendChar((char) 0x4C);
		port.sendChar((char) 0x02);
		port.sendChar((char) 0x00);
		port.sendChar((char) 0x42);
		port.sendChar((char) 0x31);
		// 切纸
		port.sendChar((char) 0x1b);
		port.sendChar((char) 0x69);

		printLine_Normal("\n");
		
		return true;
	}

	public void cutPaper_Normal()
	{

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
		// super.setEnable(enable);
		port.sendChar((char) 0x1b);
		port.sendChar((char) 0x63);
		port.sendChar((char) 0x38);
		port.sendChar((char) 0x01);
	}

}
