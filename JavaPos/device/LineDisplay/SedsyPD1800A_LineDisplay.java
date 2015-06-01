package device.LineDisplay;

import com.efuture.javaPos.Global.Language;

public class SedsyPD1800A_LineDisplay extends HisenseVG210A_LineDisplay
{
	public void clearText()
	{
		//此客显两个指令间需要延时，否则显示不正常。
		//我们一般先发清屏指令，再发显示这样有问题。所以改成清屏不做事情
	}

	public void displayAt(int row, int col, String message)
	{
		char[] com = { 0x1b, 0x51, 0x41 };

		if (message.indexOf("Thank") != -1)
		{
			message = message.substring(message.indexOf(":") + 1).trim();

			// 熄灭所有的灯
			char[] com1 = { 0x1b, 0x73, '0' };
			port.sendString(String.valueOf(com1));
		}
		else if (message.indexOf("Price") != -1)
		{
			message = message.substring(message.indexOf(":") + 1).trim();

			// 点亮“单价”灯
			char[] com1 = { 0x1b, 0x73, '1' };
			port.sendString(String.valueOf(com1));
		}
		else if (message.indexOf("Total") != -1)
		{
			message = message.substring(message.indexOf(":") + 1).trim();

			// 点亮“合计”灯
			char[] com1 = { 0x1b, 0x73, '2' };
			port.sendString(String.valueOf(com1));
		}
		else if (message.indexOf("Pay") != -1)
		{
			message = message.substring(message.indexOf(":") + 1).trim();

			// 点亮“收款”灯
			char[] com1 = { 0x1b, 0x73, '3' };
			port.sendString(String.valueOf(com1));
		}
		else if (message.indexOf("Change") != -1)
		{
			message = message.substring(message.indexOf(":") + 1).trim();

			// 点亮“找零”灯
			char[] com1 = { 0x1b, 0x73, '4' };
			port.sendString(String.valueOf(com1));
		}
		else
		{
			return;
		}

		port.sendString(String.valueOf(com) + message + (char) 0x0D);
		try
		{
			Thread.sleep(100);
		}
		catch (InterruptedException e)
		{
		}
	}

	public String getDiscription()
	{
		return Language.apply("深圳桑达龙金PD1800A串口顾客显示牌");
	}
}
