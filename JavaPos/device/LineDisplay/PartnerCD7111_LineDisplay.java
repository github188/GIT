package device.LineDisplay;

import com.efuture.javaPos.Global.Language;

//CD7111  无校验,9600,None,8,1
public class PartnerCD7111_LineDisplay extends HisenseVC110A_LineDisplay
{
    public void displayAt(int row, int col, String message)
    {
        char[] com = { 0x1b, 0x51, 0x41 };

        if (message.indexOf("Welcome") != -1)
        {
        	clearText();
        	
            // 点亮“谢谢惠顾”灯
            char[] com1 = { 0x1b, 0x73, 0x01,0x01 };
            port.sendString(String.valueOf(com1));
        }
        else
        if (message.indexOf("Price") != -1)
        {
        	clearText();
        	
            message = message.substring(message.indexOf(":")+1).trim();

            // 点亮“单价”灯
            char[] com1 = { 0x1b, 0x73, 0x02,0x01 };
            port.sendString(String.valueOf(com1));
        }
        else if (message.indexOf("Total") != -1)
        {
        	clearText();
        	
        	message = message.substring(message.indexOf(":")+1).trim();

            // 点亮“合计”灯
            char[] com1 = { 0x1b, 0x73, 0x03,0x01 };
            port.sendString(String.valueOf(com1));
        }
        else if (message.indexOf("Pay") != -1)
        {
        	clearText();
        	
        	message = message.substring(message.indexOf(":")+1).trim();

            // 点亮“收款”灯
            char[] com1 = { 0x1b, 0x73, 0x04,0x01 };
            port.sendString(String.valueOf(com1));
        }
        else if (message.indexOf("Change") != -1)
        {
        	clearText();
        	
        	message = message.substring(message.indexOf(":")+1).trim();

            // 点亮“找零”灯
            char[] com1 = { 0x1b, 0x73, 0x05,0x01 };
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
        return Language.apply("PARTNER款机CD7111串口顾客显示牌");
    }
}
