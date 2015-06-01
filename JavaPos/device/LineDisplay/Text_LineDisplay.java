package device.LineDisplay;

import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.javaPos.Device.Interface.Interface_LineDisplay;
import com.efuture.javaPos.Global.Language;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.PathFile;


public class Text_LineDisplay implements Interface_LineDisplay
{

	public void clearText()
	{
		PathFile.deletePath("Line_file.ini");
		
	}

	public void close()
	{
		// TODO 自动生成方法存根
		
	}

	public void display(String message)
	{
		 displayAt(0, 0, message);
		
	}

	public void displayAt(int row, int col, String message)
	{
		System.out.println(message);

        PrintWriter pw = null;
        if (System.getProperties().getProperty("os.name").substring(0, 5).equals("Linux"))
        {
        	pw = CommonMethod.writeFileAppend("Line_file.ini");
        }
        else
        {
        	pw = CommonMethod.writeFileAppendGBK("Line_file.ini");
        	message = message.replaceAll("\n","\r\n");
        }
        
        pw.print(message);
        pw.flush();
        pw.close();
		
	}

	public String getDiscription()
	{
		return Language.apply("文本顾客显示牌");
	}

	public Vector getPara()
	{
		// TODO 自动生成方法存根
		return null;
	}

	public boolean open()
	{
		// TODO 自动生成方法存根
		return true;
	}

	public void setEnable(boolean enable)
	{
		// TODO 自动生成方法存根
		
	}

}
