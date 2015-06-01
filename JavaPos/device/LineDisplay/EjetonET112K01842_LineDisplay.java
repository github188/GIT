package device.LineDisplay;

import com.efuture.javaPos.Global.Language;


public class EjetonET112K01842_LineDisplay extends Serial_LineDisplay
{
    public String getDiscription()
    {
        return Language.apply("易捷通ET112K01842串口顾客显示牌");
    }    
    
    public void displayAt(int row, int col, String message)
    {
		char[] com = { 0x1b, 0x51, 0x41 };
		message = message.substring(message.indexOf(":") + 1).trim();
		port.sendString(String.valueOf(com) + message + (char) 0x0D);
    }
}
