package device.LineDisplay;

import com.efuture.javaPos.Global.Language;

// LC415 9600,N,8,1
public class HisenseLC415_LineDisplay extends HisenseVG210A_LineDisplay
{
    public void displayAt(int row, int col, String message)
    {
        if (row == 0)
        {
            char[] com = { 0x1b, 0x51, 0x40 };
            port.sendString(String.valueOf(com) + message + (char) 0x0D);
        }
        else if (row == 1)
        {
            char[] com = { 0x1b, 0x51, 0x41 };
            port.sendString(String.valueOf(com) + message + (char) 0x0D);
        }
        else if (row == 2)
        {
            char[] com = { 0x1b, 0x51, 0x42 };
            port.sendString(String.valueOf(com) + message + (char) 0x0D);
        }
        else if (row == 3)
        {
            char[] com = { 0x1b, 0x51, 0x43 };
            port.sendString(String.valueOf(com) + message + (char) 0x0D);
        }
    }
    
    public String getDiscription()
    {
        return Language.apply("海信LC415串口顾客显示牌");
    }
}
