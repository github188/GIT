package device.LineDisplay;

import com.efuture.javaPos.Global.Language;

//import com.efuture.commonKit.MessageBox;

public class CitaqOpoz6000_LineDisplay extends WintecCD320_LineDisplay
{
	public void displayAt(int row, int col, String message)
	{
		char[] com = { 0x1b, 0x51, 0x41 };

		// new MessageBox(message);
		if (message.indexOf("Price") != -1)
		{
			message = message.substring(message.indexOf(":") + 1).trim();
			// new MessageBox("price:" + message);
			// 点亮“单价”灯
			char[] com1 = { 0x1b, 0x73, 0x31 };
			port.sendString(String.valueOf(com1));
		}
		else if (message.indexOf("Total") != -1)
		{

			message = message.substring(message.indexOf(":") + 1).trim();
			// new MessageBox("total:" + message);
			// 点亮“合计”灯
			char[] com1 = { 0x1b, 0x73, 0x32 };
			port.sendString(String.valueOf(com1));
		}
		else if (message.indexOf("Pay") != -1)
		{
			message = message.substring(message.indexOf(":") + 1).trim();
			// new MessageBox("pay:" + message);
			// 点亮“收款”灯
			char[] com1 = { 0x1b, 0x73, 0x33 };
			port.sendString(String.valueOf(com1));
		}
		else if (message.indexOf("Change") != -1)
		{
			message = message.substring(message.indexOf(":") + 1).trim();
			// new MessageBox("change:" + message);
			// 点亮“找零”灯
			char[] com1 = { 0x1b, 0x73, 0x34 };
			port.sendString(String.valueOf(com1));
		}
		else if (message.indexOf("Welcome") != -1)
		{
			port.sendString(String.valueOf(com) + "0.00" + (char) 0x0D);
		}
		else
		{
			return;
		}

		port.sendString(String.valueOf(com) + message + (char) 0x0D);
	}

	public String getDiscription()
	{
		return Language.apply("川田Opoz6000 PD6108串口顾客显示牌");
	}

}
