package device.LineDisplay;

import com.efuture.javaPos.Global.Language;


// I8客显设备为奇校验,9600,Odd,8,1
public class WincorI8_LineDisplay extends Serial_LineDisplay
{
    public void clearText()
    {
        char[] com = { 0x1b, 0x5b, 0x32, 0x4a };
        port.sendString(String.valueOf(com));
    }

    public void display(String message)
    {
        displayAt(0, 0, message);
    }

    public void displayAt(int row, int col, String message)
    {
        String line = (char) 0x1b + "[" + (row + 1) + ";" + col + "H" +
                      message;
        port.sendString(line);
    }
    
	public String getDiscription() 
	{
		return Language.apply("WincorI8款机串口顾客显示牌");
	}
}
