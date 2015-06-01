package device.ICCard;

import gnu.io.CommPortIdentifier;

import java.util.Enumeration;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Interface.Interface_ICCard;
import com.efuture.javaPos.Device.SerialPort.SerialConnection;
import com.efuture.javaPos.Device.SerialPort.SerialConnectionException;
import com.efuture.javaPos.Device.SerialPort.SerialInputEvent;
import com.efuture.javaPos.Device.SerialPort.SerialParameters;
import com.efuture.javaPos.Global.ConfigClass;

public class KTL512V implements Interface_ICCard
{

	//private Interface_ICCard KTL512V = null;
	protected SerialParameters para = null;
    protected SerialConnection port = null;
    private SerialInputEvent inevent = null;
    private StringBuffer dataStr = null;
    private int intstate = -1;
    private int timeout = 60;//密码等待超时时间
    
	public String getDiscription()
	{
		return "密码键盘KTL512V";
	}

	public Vector getPara()
	{

        Vector v = new Vector();
        String comlist = "端口号";
        Enumeration portList = CommPortIdentifier.getPortIdentifiers();

        while (portList != null)
        {
            CommPortIdentifier p = (CommPortIdentifier) portList.nextElement();

            if (p == null)
            {
                break;
            }
            else
            {
                comlist += ("," + p.getName());
            }
        }

        v.add(comlist.split(","));
        v.add(new String[] { "波特率", "9600", "110", "300", "600", "1200", "2400", "4800", "19200" });//9600
        v.add(new String[] { "奇偶效验位", "None", "Odd", "Even" });//None
        v.add(new String[] { "数据位", "8", "7", "6", "5", "4" });//8
        v.add(new String[] { "停止位", "1", "1.5", "2" });//1
        v.add(new String[] { "超时时间(秒)", "60", "90", "120" });//60

        return v;

	}

	public boolean open()
	{
		dataStr = new StringBuffer();
        /*if (DeviceName.deviceICCard.length() <= 0)
        {
            return false;
        }
*/
        try
        {
            String[] arg = ConfigClass.CustomItem4.toString().split("\\,");//DeviceName.deviceICCard.split(",");
            para = new SerialParameters();

            if (arg.length > 1)
            {
                para.setPortName(arg[1]);

                if (arg.length > 2)
                {
                    para.setBaudRate(arg[2]);
                }
                else
                {
                	para.setBaudRate("9600");
                }

                if (arg.length > 3)
                {
                    para.setParity(arg[3]);
                }
                else
                {
                	para.setParity("0");//N
                }

                if (arg.length > 4)
                {
                    para.setDatabits(arg[4]);
                }
                else
                {
                	para.setDatabits("8");
                }

                if (arg.length > 5)
                {
                    para.setStopbits(arg[5]);
                }
                else
                {
                	para.setStopbits("1");
                }
                if (arg.length > 6)
                {
                	timeout = Convert.toInt(arg[6]);
                }
                
                //超时时间(秒)
                if (timeout <= 0 || timeout >= 60*10)
                {
                	timeout = 60;
                }

            }
            port = new SerialConnection(para);            
            inevent = new SerialInputEvent()    		{
    		    public void inputData(int data)
    		    {
    		    	SerialInput(data);
    		    }
    		};
    		
    		port.openConnection();    		
    		port.inputevent = inevent;
    		//char[] chrCmd = { 0x1b, 0x49 };
			port.sendString(String.valueOf(getCmd()));
			return true;  			
    	 
        }
        catch (SerialConnectionException ex)
        {
            ex.printStackTrace();
            new MessageBox("打开串口密码键盘异常:\n" + ex.getMessage());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            new MessageBox("打开串口密码键盘异常:\n" + ex.getMessage());
        }

        return false;
	}
	
	//通讯命令
	public char[] getCmd()
	{
		char[] chrCmd = { 0x1b, 0x49 };
		return chrCmd;
	}
	
	public boolean close()
	{
		if (port != null)
        {
            port.closeConnection();
        }
		return true;
	}
	
	public void SerialInput(final int data)
	{		
		if (data == 2)
		{
			intstate = 2;//开始读取密码...
			return;
		}
		if (data == 3) 
		{
			intstate = 0;//密码读取完毕.
			return;
		}
		if ('\r' != (char)data) 
		{
			// 过滤非法字符
			if (data >= 32 && data <= 127)
			{
				dataStr.append((char)data);
			}
		}		
	}

	
	public String findCard()
	{
		String message = null;
		try
		{
			if (port == null || !port.isOpen())
			{
				if(!open()) 
				{
					new MessageBox("读取失败，未打开硬件设备！");
					return null;
				}
			}
			
			if (port.isOpen())
			{
				intstate = 1;
				
				int count = 0;//循环次数
				while(true)
				{
					try
					{
						if (intstate == 0)		//密码读取完毕 
						{
							break;
						}
						if (count >= (5*timeout))	//读取超时
						{
							new MessageBox("读取失败，操作超时！");
							break;
						}
						
						Thread.sleep(200);
						count++;					
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
					
				}
				
				message = dataStr.toString();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			intstate = -1;//还原到原始状态
			close();
		}
				
		System.out.println("sp_password=" + message);
		return message;
      
	}
	   
	public String updateCardMoney(String cardno, String operator, double ye)
	{
		return "error:该设备不支持本功能";
	}
}


