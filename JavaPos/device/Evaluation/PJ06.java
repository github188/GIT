package device.Evaluation;

import gnu.io.CommPortIdentifier;

import java.util.Enumeration;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Interface.Interface_Evaluation;
import com.efuture.javaPos.Device.SerialPort.SerialConnection;
import com.efuture.javaPos.Device.SerialPort.SerialConnectionException;
import com.efuture.javaPos.Device.SerialPort.SerialInputEvent;
import com.efuture.javaPos.Device.SerialPort.SerialParameters;
import com.efuture.javaPos.Global.GlobalVar;

public class PJ06 implements Interface_Evaluation
{
	public static PJ06 info = null;
	protected static SerialParameters para = null;

	protected static SerialConnection port = null;

	private static SerialInputEvent inevent = null;

	private int intstate = -1;
	private int timeout = 10;// 密码等待超时时间
	private int dd = -1;
	private int level = 5;

	public static PJ06 getDefault()
	{
		if (info == null) info = new PJ06();
		return info;
	}

	public String getDiscription()
	{
		return "家乐园项目评价器PJ06";
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
		v.add(new String[] { "波特率", "9600", "110", "300", "600", "1200", "2400", "4800", "19200" });// 19200
		v.add(new String[] { "奇偶效验位", "None", "Odd", "Even" });// None
		v.add(new String[] { "数据位", "8", "7", "6", "5", "4" });// 8
		v.add(new String[] { "停止位", "1", "1.5", "2" }); // 1
		v.add(new String[] { "超时时间(秒)", "60", "90", "120" });// 60

		return v;

	}

	public boolean open()
	{
		try
		{
			if (DeviceName.deviceEvaluation.equals(""))
			{
				new MessageBox("评价器未设置，获取参数失败！");
				return false;
			}

			String[] arg = DeviceName.deviceEvaluation.split("\\,");
			// String str = "1,COM3,19200,N,8,1,";
			// String[] arg = str.split("\\,");
			try
			{
				para = new SerialParameters();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				close();
			}

			if (arg.length > 1)
			{
				para.setPortName(arg[1]);

				if (arg.length > 2)
				{
					para.setBaudRate(arg[2]);
				}
				else
				{
					para.setBaudRate("19200");
				}

				if (arg.length > 3)
				{
					para.setParity(arg[3]);
				}
				else
				{
					para.setParity("none");// N
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
				if (timeout == 0) { return false; }
				// 超时时间(秒)
				if (timeout < 0 || timeout >= 60 * 10)
				{
					timeout = 10;
				}
				if (arg.length > 7) // 设置星级
				{
					level = Convert.toInt(arg[7]);
				}
			}
			port = new SerialConnection(para);

			//port.isCurrentlyOwned();
			port.openConnection();

			byte[] a = setTimeoutCmd(timeout);
			port.sendByte(a);
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			byte[] b = setLevelCmd(level);
			port.sendByte(b);
			return true;
		}
		catch (SerialConnectionException ex)
		{
			ex.printStackTrace();
			close();
			new MessageBox("打开串口评价器异常:\n" + ex.getMessage());
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			close();
			new MessageBox("打开串口评价器异常:\n" + ex.getMessage());
		}

		return false;
	}

	// 通讯命令
	public byte[] getWelcomeCmd()
	{
		// 55 AA 02 1F 00 1A
		byte[] byteCmd = { 85, -86, 2, 31, 0, 26 };
		return byteCmd;
	}

	public byte[] getAppraisalCmd()
	{
		// 55 AA 02 20 00 1A
		byte[] byteCmd = { 85, -86, 2, 32, 0, 26 };
		return byteCmd;
	}

	public byte[] setTimeoutCmd(int a)
	{
		// 55 AA 03 13 3C 00 1A
		String b = null;
		if (a > 0 && a < 255)
		{
			b = Integer.toHexString(a);
		}
		byte[] byteCmd = { 85, -86, 3, 19, 10, 0, 26 };
		if (b != null) byteCmd[4] = (byte) a;
		return byteCmd;
	}

	public byte[] setLevelCmd(int a)
	{
		// 55 AA 02 15 05 1A
		byte[] byteCmd = { 85, -86, 2, 21, 5, 26 };
		if (a < 6) byteCmd[4] = (byte) a;
		return byteCmd;
	}

	public boolean close()
	{
		if (port != null)
		{
			port.closeConnection();
		}
		return true;
	}

	public void Welcome()
	{
		try
		{
			if (port == null || !port.isOpen())
			{
				if (!open())
				{
					new MessageBox("读取失败，未打开硬件设备！");
				}
			}
			if (port.isOpen())
			{
				byte[] b = getWelcomeCmd();
				port.sendByte(b);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public String getData()
	{
		String message = null;

		try
		{
			if (port == null || !port.isOpen())
			{
				if (!open())
				{
					new MessageBox("读取失败，未打开硬件设备！");
					return null;
				}
			}
			inevent = new SerialInputEvent()
			{
				public void inputData(int data)
				{
					SerialInput(data);
				}
			};
			port.inputevent = inevent;
			while (true)
			{
				if (port.isOpen())
				{
					intstate = 0;
					byte[] b = getAppraisalCmd();
					dd = -1;
					port.sendByte(b);
					Thread.sleep(500);
					int count = 0;//循环次数
					while (true)
					{
						try
						{
							if (intstate == 1) //密码读取完毕 
							{
								break;
							}
							if (count >= (timeout)) //读取超时
							{
								new MessageBox("读取失败，操作超时！");
								dd = -1;
								break;
							}
							port.sendBreak();

							count++;
						}
						catch (Exception ex)
						{
							ex.printStackTrace();
						}

					}
					if (dd < level + 2 && dd > 0)
					{
						message = String.valueOf(dd);
						break;
					}

					if (new MessageBox("操作超时，读取数据失败！\n\n 是否重新获取？", null, true).verify() == GlobalVar.Key2)
					{
						message = "0";
						break;
					}

					dd = -1;
					message = String.valueOf(dd);
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		System.out.println("PJ_message=" + message);
		return message;

	}

	public void SerialInput(final int data)
	{
		if (data > 0 && data < 7)
		{
			dd = data;
			intstate = 1;//开始读取密码...
			return;
		}
	}

	public String updateCardMoney(String cardno, String operator, double ye)
	{
		return "error:该设备不支持本功能";
	}

}
