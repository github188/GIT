package device.LineDisplay;

import com.efuture.javaPos.Global.Language;



//VC210A 奇校验,9600,Odd,8,1
//VC110  无校验,9600,None,8,1
//VC108  无校验,9600,None,8,1
public class HisenseVG210A_LineDisplay extends Serial_LineDisplay
{
    public void clearText()
    {
        char[] com = { 0x0c };
        port.sendString(String.valueOf(com));
    }

    public void display(String message)
    {
        displayAt(0, 0, message);
    }

    public void displayAt(int row, int col, String message)
    {
        if (row == 0)
        {
            char[] com = { 0x1b, 0x51, 0x41 };
            port.sendString(String.valueOf(com) + message + (char) 0x0D);
        }
        else
        {
            char[] com = { 0x1b, 0x51, 0x42 };
            port.sendString(String.valueOf(com) + message + (char) 0x0D);
        }
    }

    public void setEnable(boolean enable)
    {
        if (enable)
        {
            char[] com = { 0x1b, 0x40 };
            port.sendString(String.valueOf(com));
        }
    }

    public String getDiscription()
    {
        return Language.apply("海信VG210A串口顾客显示牌");
    }
}
