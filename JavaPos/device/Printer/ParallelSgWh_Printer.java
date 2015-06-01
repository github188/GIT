package device.Printer;

import gnu.io.CommPortIdentifier;

import java.util.Enumeration;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.SerialPort.ParallelConnection;
import com.efuture.javaPos.Device.SerialPort.ParallelConnectionException;
import com.efuture.javaPos.Global.Language;

import device.DeviceInfo;

 //上海寺冈WH打印机
public class ParallelSgWh_Printer extends Parallel_Printer
{

    protected int printType = 0; //0- 打印文本类型，1 - 打印图片类型
    private StringBuilder data ;
    
    public boolean open()
    {
    	if (DeviceName.devicePrinter.length() <= 0) return false;
    	
        try
        {    	
        	String[] s = DeviceName.devicePrinter.split(",");
        	
        	port = new ParallelConnection(s[0]);
        	
	        port.openConnection();
	        
	        if (s.length > 1) cutMsg = (s[1].equalsIgnoreCase("Y") ? true : false);
	        if (s.length > 2) cutLine = Convert.toInt(s[2]);
	        if (s.length > 3) cutCmd = s[3];
	        if (s.length > 4) initCmd = s[4];
	        if (s.length > 5) passCmd = s[5];
	        if (s.length > 6) printType = Convert.toInt(s[6]);
	        
            return true;
        }
        catch (ParallelConnectionException ex)
        {
            ex.printStackTrace();
            new MessageBox(Language.apply("打开并口打印机异常:\n") + ex.getMessage());
        }
        catch (Exception ex)
        {
        	ex.printStackTrace();
        	new MessageBox(Language.apply("打开并口打印机异常:\n") + ex.getMessage());
        }
        
        return false;
    }
        
    public void printLine_Normal(String printStr)
    {
    	//以图片内容的形式打印文本
    	if (printType == 1)
    	{
    		if (null ==  data)
    			data = new StringBuilder();
    		data.append(printStr + "\n");
    		//port.sendImgae(printStr);    		
    	}
    	else //默认打印文本类型
    	{
    		
    		port.sendString(printStr);
    	}
    		
    }
    
    public void cutPaper_Normal()
    {
    	//以图片内容的形式打印文本
    	if (printType == 1)
    	{
    		port.sendImgae(data.toString());
    	}
    	for (int i = 0;i < cutLine;i++)
    	{
    		printLine_Normal("\n");
    	}
    	
    	// 发送切纸命令
    	if (cutCmd != null && cutCmd.trim().length() > 0)
    	{
    		char[] c = DeviceInfo.convertCmdStringToCmdChar(cutCmd);
    		for (int i=0;i<c.length;i++)
    		{
    			port.sendChar(c[i]);
    		}
    	}
    	
    	//
    	if (cutMsg)
    	{
    		new MessageBox(Language.apply("请从打印机撕下已打印的单据"));
    	}
    }
    
	public Vector getPara()
	{
		Vector v = new Vector();
		String comlist = Language.apply("端口号");
		Enumeration portList = CommPortIdentifier.getPortIdentifiers();
    	while(portList != null)
    	{
    		CommPortIdentifier p = (CommPortIdentifier)portList.nextElement();
    		if (p == null) break;
    		else
    		{
    			comlist +=","+p.getName();
    		}
    	}
    	
    	v.add(comlist.split(","));
    	v.add(new String[]{Language.apply("是否显示切纸提示"),"N","Y"});
    	v.add(new String[]{Language.apply("切纸前走纸的行数"),"0"});
    	v.add(new String[]{Language.apply("切纸命令")});
    	v.add(new String[]{Language.apply("初始化命令")});
    	v.add(new String[]{Language.apply("分页走纸命令")});
    	v.add(new String[]{Language.apply("打印内容类型(0-文本/1-图片"),"0","1"});
    	
    	return v;
	}

	public String getDiscription() 
	{
		return Language.apply("上海寺冈WH打印机");
	}
}
