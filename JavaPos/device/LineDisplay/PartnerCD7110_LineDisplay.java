package device.LineDisplay;

import com.efuture.javaPos.Global.Language;

public class PartnerCD7110_LineDisplay extends HisenseVG210A_LineDisplay
{
	public void displayAt(int row, int col, String message)
	{
		char[] com = { 0x1b, 0x51, 0x41 };

		if (message.indexOf("Price") != -1)
		{
			message = message.substring(message.indexOf(":") + 1).trim();

			// 点亮“单价”灯
			char[] com1 = { 0x1b, 0x73, 0x02, 0x01};
			port.sendString(String.valueOf(com1));
		}
		else if (message.indexOf("Total") != -1)
		{
			message = message.substring(message.indexOf(":") + 1).trim();

			// 点亮“合计”灯
			char[] com1 = { 0x1b, 0x73, 0x03 ,0x01};
			port.sendString(String.valueOf(com1));
		}
		else if (message.indexOf("Pay") != -1)
		{
			message = message.substring(message.indexOf(":") + 1).trim();

			// 点亮“收款”灯
			char[] com1 = { 0x1b, 0x73, 0x04 ,0x01};
			port.sendString(String.valueOf(com1));
		}
		else if (message.indexOf("Change") != -1)
		{
			message = message.substring(message.indexOf(":") + 1).trim();

			// 点亮“找零”灯
			char[] com1 = { 0x1b, 0x73, 0x05 ,0x01};
			port.sendString(String.valueOf(com1));
		}
		else
		{
			return;
		}

		port.sendString(String.valueOf(com) + message + (char) 0x0D);
	}

	public String getDiscription()
	{
		return Language.apply("拍档CD7110串口顾客显示牌");
	}
}
