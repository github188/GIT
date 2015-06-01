package device.LineDisplay;

import com.efuture.javaPos.Global.Language;

public class WintecCD320_LineDisplay extends Serial_LineDisplay
{
	public void clearText()
    {
		char[] com = {0x0c};
		
		for (int i = 0;i < com.length;i++)
		{
			port.sendChar(com[i]);
		}
    }
	
	public void setEnable(boolean enable)
    {
    	// 发送初始化命令
		char[] com = {0x1b,0x40};
		
		for (int i = 0;i < com.length;i++)
		{
			try
		 	{
			 	Thread.sleep(15);
		 	}
		 	catch (Exception ex)
		 	{
		 		ex.printStackTrace();
		 	}
		 	
			port.sendChar(com[i]);
		}
    }
	
	public void displayAt(int row, int col, String message)
    {
		
		 if (row > 0) 
		 {
			 char [] com = new char[4];
			 
			 com[0] = 0x1F;
			 com[1] = 0x24;
			 com[2] = 0x01;
			 com[3] = 0x02;
			 
			 for (int i = 0;i < com.length;i++)
			 {
				 try
				 {
					 Thread.sleep(15);
				 }
				 catch (Exception ex)
				 {
					 ex.printStackTrace();
				 }
				 
				 port.sendChar(com[i]);
			 }
		 }
		 else
		 {
			/* com[0] = 0x1F;
			 com[1] = 0x24;
			 com[2] = 0x01;
			 com[3] = 0x01;*/
			 char [] com = new char[1];
			 com[0] = 0x0B;
			 port.sendChar(com[0]);
		 }
		 
		
		 
		 for (int i = 0;i < message.length();i++)
		 {
			 try
			 {
				 Thread.sleep(15);
			 }
			 catch (Exception ex)
			 {
				 ex.printStackTrace();
			 }
			 
			 port.sendChar(message.charAt(i));
		 }
    }
	
	public String getDiscription()
    {
        return Language.apply("中科英泰320串口顾客显示牌");
    }
}
