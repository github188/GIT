package device.Printer;

import com.efuture.javaPos.Global.Language;


public class IBM4610Javax_Printer extends SerialJavax_Printer {
	public void cutPaper_Normal()
    {
    	// 切纸
        port.sendChar((char) 0x1b);
        port.sendChar('d');
        port.sendChar((char) 0x01);
    }
	
	public String getDiscription() 
	{
		//类名写错了，应该是4679
		return Language.apply("IBM4679JAVAX无黑标串口打印机");
	}
}
