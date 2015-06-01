package device.Printer;

import javax.comm.CommDriver;
import javax.comm.ParallelPort;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.SerialPort.ParallelConnection;
import com.efuture.javaPos.Device.SerialPort.ParallelConnectionException;
import com.efuture.javaPos.Global.Language;

public class Parallel_Printer1 extends Parallel_Printer
{
	ParallelPort serialPort = null; 
    public boolean open()
    {
    	if (DeviceName.devicePrinter.length() <= 0) return false;
    	
        try
        {    	
        	String[] s = DeviceName.devicePrinter.split(",");
        	
//        	连接LPT1口
        	String driverName = "com.sun.comm.Win32Driver";
        	CommDriver driver = null;
        	System.loadLibrary("win32com");
        	driver = (CommDriver)Class.forName(driverName).newInstance();
        	driver.initialize();

        	serialPort = (ParallelPort)driver.getCommPort(s[0], javax.comm.CommPortIdentifier.PORT_PARALLEL);
        	System.out.println( serialPort.getName() );
	        
	        if (s.length > 1) cutMsg = (s[1].equalsIgnoreCase("Y") ? true : false);
	        if (s.length > 2) cutLine = Convert.toInt(s[2]);
	        if (s.length > 3) cutCmd = s[3];
	        if (s.length > 4) initCmd = s[4];
	        if (s.length > 5) passCmd = s[5];
	        
            return true;
        }
        catch (Exception ex)
        {
        	ex.printStackTrace();
        	new MessageBox(Language.apply("打开并口打印机异常:\n") + ex.getMessage());
        }
        
        return false;
    }
}
