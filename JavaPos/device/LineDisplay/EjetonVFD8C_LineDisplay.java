package device.LineDisplay;

import com.efuture.javaPos.Global.Language;


public class EjetonVFD8C_LineDisplay extends Serial_LineDisplay
{
    public String getDiscription()
    {
        return Language.apply("易捷通VFD8C串口顾客显示牌");
    }    
    
    public void displayAt(int row, int col, String message)
    {
		char[] com = { 0x1b, 0x51, 0x41 };
		
		if (message.indexOf("Price") != -1)
		{
			message = message.substring(message.indexOf(":") + 1).trim();

			// 点亮“单价”灯
			port.sendChar((char)0x31);
		}
		else if (message.indexOf("Total") != -1)
		{
			message = message.substring(message.indexOf(":") + 1).trim();

			// 点亮“总计”灯
			port.sendChar((char)0x32);
		}
		else if (message.indexOf("Pay") != -1)
		{
			message = message.substring(message.indexOf(":") + 1).trim();

			// 点亮“收款”灯
			port.sendChar((char)0x33);
		}
		else if (message.indexOf("Change") != -1)
		{
			message = message.substring(message.indexOf(":") + 1).trim();

			// 点亮“找零”灯
			port.sendChar((char)0x34);
		}
		else
		{
			return;
		}

		port.sendString(String.valueOf(com) + message + (char) 0x0D);
    }
}
