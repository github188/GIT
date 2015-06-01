package device.ElectronicScale;

import java.io.PrintWriter;
import java.util.Vector;
import javax.comm.SerialPortEvent;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Interface.Interface_ElectronicScale;
import com.efuture.javaPos.Device.SerialPort.*;
import com.efuture.javaPos.Global.ConfigClass;

public class ToledoVIVA_ElectronicScale implements Interface_ElectronicScale
{
	private Javax_SerialParameters para = null; // 串口参数
	private ToledoVIVASerialConnection port = null;

	private double weight = 0;
	// private double money = 0;

	public static boolean isRecvData = false;
	public static boolean isOk = false;
	public static StringBuffer retDataString = new StringBuffer(); // 串口返回字符串

	public String islog;

	public boolean open()
	{
		if (DeviceName.deviceElectronicScale.length() <= 0)
			return false;

		try
		{
			// 9600，奇，7，1
			String[] arg = DeviceName.deviceElectronicScale.split(",");
			para = new Javax_SerialParameters();

			if (arg.length > 0)
			{
				// 端口名
				para.setPortName(arg[0]);

				if (arg.length > 1)
				{
					// 波特率
					para.setBaudRate(arg[1]);
				}

				if (arg.length > 2)
				{
					// 奇偶效验位
					para.setParity(arg[2]);
				}

				if (arg.length > 3)
				{
					// 数据位
					para.setDatabits(arg[3]);
				}

				if (arg.length > 4)
				{
					// 停止位
					para.setStopbits(arg[4]);
				}

				// 是否记录日志
				if (arg.length > 5)
				{
					islog = arg[5].trim();
				}
			}

			port = new ToledoVIVASerialConnection(para);

			if (port == null)
			{
				log("Port Initialize Failed");
				return false;
			}

			port.openConnection();

			if (!port.isOpen())
			{
				log("Port Open Failed");
				return false;
			}

			log("Port Open Successed");
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("打开串口电子秤异常:\n" + ex.getMessage());
		}

		return false;
	}

	public void close()
	{
		try
		{
			if (port.isOpen())
			{
				port.closeConnection();
				port = null;
				log("Port Close Successed");
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			port = null;
		}
	}

	public void setEnable(boolean enable)
	{
	}

	public Vector getPara()
	{
		return null;
	}

	public String getDiscription()
	{
		return "接串口的电子秤";
	}

	// 去皮
	public boolean sendData1(String data)
	{
		try
		{
			if (!port.isOpen())
			{
				log("Port Not Opened");
				return false;
			}

			char[] com = new char[5];

			com[0] = 0x04;
			com[1] = 0x02;
			com[2] = 0x31;
			com[3] = 0x32;
			com[4] = 0x03;

			log("发送去皮指令[Start]......\n\tInput:" + String.valueOf(com));

			for (int i = 0; i < com.length; i++)
				port.sendChar(com[i]);

			log("发送去皮指令[End]......");

			return true;
		}
		catch (Exception ex)
		{
			new MessageBox(ex.getMessage());
			return false;
		}
	}

	// 发送轮循
	public boolean sendData2(String data)
	{
		try
		{
			if (!port.isOpen())
			{
				log("Port Not Opened");
				return false;
			}

			char[] com = new char[5];

			com[0] = 0x04;
			com[1] = 0x02;
			com[2] = 0x31;
			com[3] = 0x30;
			com[4] = 0x03;

			log("发送重量轮循指令[Start]......\n\tInput:" + String.valueOf(com));

			for (int i = 0; i < com.length; i++)
				port.sendChar(com[i]);

			log("发送重量轮循指令[End]......");

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			log("数据解析异常:" + ex.getMessage());
			return false;
		}
	}

	// 停止轮循
	public boolean sendData3(String data)
	{
		try
		{
			char[] com = { 0x04, 0x05 };

			log("发送轮循终止指令[Start]......\n\tInput:" + String.valueOf(com));

			port.sendString(String.valueOf(com));

			log("发送轮循终止指令[End]......");

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			log("发送轮循终止指令异常...");
			return false;
		}
	}

	public String readData()
	{
		String message = "";
		try
		{
			log("开始接收数据...");
			// 打开接收标志
			ToledoVIVA_ElectronicScale.isRecvData = true;
			ToledoVIVA_ElectronicScale.isOk = false;

			retDataString.delete(0, retDataString.length());
			log("retDataString:" + retDataString.toString());

			while (true)
			{
				if (ToledoVIVA_ElectronicScale.isOk)
					break;
			}

			// ToledoVIVA_ElectronicScale.isRecvData = false;

			log("\tOutput1:" + retDataString);

			// 重量占第32位
			if (ToledoVIVA_ElectronicScale.retDataString.toString().startsWith("2") && ToledoVIVA_ElectronicScale.retDataString.toString().length() < 32)
				return null;

			message = retDataString.substring(0, retDataString.length());
			retDataString.delete(0, retDataString.length());

			String str[] = message.split(" ");

			for (int i = 0; i < str.length; i++)
			{
				retDataString.append(Convert.increaseCharForward(str[i], '0', 2));
				retDataString.append(" ");
			}

			message = retDataString.toString().substring(0, retDataString.toString().length());

			log("\tOutput2:" + message);

			if (message == null)
				return null;

			if (message.length() <= 2 && Integer.toHexString(Integer.parseInt(message)).equals("15"))
			{
				log("发送串口数据失败,返回代码:\n" + Integer.toHexString(Integer.parseInt(message)));
				return null;
			}

			return message;

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			log("读取串口数据异常:" + ex.getMessage());
			return null;
		}
	}

	// 获取轮循数据
	public boolean recvData2()
	{
		String message = "";

		try
		{
			message = readData();

			if (message == null)
			{
				log("串口数据null");
				return false;
			}

			String data = ManipulateStr.getNumToHexStr(message);

			log("解析串口数据:" + data);

			if (data == null)
			{
				log("解析串口数据失败");
				return false;
			}

			String tmpWeight = ManipulateStr.getHexStrToChar(data.substring(18, 32));

			setWeight(ManipulatePrecision.div(Double.parseDouble(tmpWeight), 1000));

			log("重量:" + tmpWeight);

			return true;
		}
		catch (Exception ex)
		{
			// new MessageBox(ex.getMessage());
			log("解析数据异常:" + ex.getMessage());
			return false;
		}
	}

	public double getWeight()
	{
		return weight;
	}

	public double getMoney()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public void setWeight(double weight)
	{
		this.weight = weight;
	}

	public void setMoney(double money)
	{
		// TODO Auto-generated method stub

	}

	protected void log(String msg)
	{
		if (islog.equals("Y"))
		{
			PrintWriter pw = null;
			String datetime = "[" + ManipulateDateTime.getDateTimeAll() + "]: ";
			String file = new ManipulateDateTime().getDateByEmpty();
			file = file + "_scale.log";

			try
			{
				pw = CommonMethod.writeFileAppend(file);
				pw.print(datetime + msg + "\r\n");
				pw.flush();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if (pw != null)
					pw.close();
				pw = null;
			}
		}
	}

	public class ToledoVIVASerialConnection extends Javax_SerialConnection
	{

		public ToledoVIVASerialConnection(Javax_SerialParameters param)
		{
			super(param);
		}

		public void serialEvent(SerialPortEvent e)
		{
			if (ConfigClass.DebugMode)
				System.out.println("data coming");

			int newData = 0;
			int count = 0;
			boolean isStartRecv = false;

			switch (e.getEventType())
			{
				case SerialPortEvent.DATA_AVAILABLE:
					while (newData != -1 && count < 26)
					{
						try
						{
							newData = getInputStream().read();
							
							if (ConfigClass.DebugMode)
								System.out.print(newData + " ");

							// 接收标志为false时，不接收数据
							if (!ToledoVIVA_ElectronicScale.isRecvData)
								continue;

							// 为2表示数据段头,则开始接收数据
							if (!isStartRecv && newData == 2)
								isStartRecv = true;

							if (!isStartRecv)
								continue;

							if (newData == -1)
								break;

							ToledoVIVA_ElectronicScale.retDataString.append(newData);
							ToledoVIVA_ElectronicScale.retDataString.append(" ");
							count++;
							
							if (ConfigClass.DebugMode)
								System.out.print("|" + newData + " ");
						}
						catch (Exception ex)
						{
							System.err.println(ex);
							return;
						}
					}
					ToledoVIVA_ElectronicScale.isOk = true;
					ToledoVIVA_ElectronicScale.isRecvData = false;

					isStartRecv = false;
					
					if (ConfigClass.DebugMode)
						System.out.println("data finish");

					break;

				case SerialPortEvent.BI:

					break;

				default:
					break;
			}

			return;
		}
	}

	// 发送价格
	public boolean sendData4(String data)
	{
		return true;
		/*
		 * try { String jestr =
		 * Convert.increaseCharForward(String.valueOf((long)
		 * ManipulatePrecision.doubleConvert(money * 100, 2, 1)), '0', 6);
		 * 
		 * char[] com = new char[13];
		 * 
		 * com[0] = 0x04; com[1] = 0x02; com[2] = 0x30; com[3] = 0x31; com[4] =
		 * 0x1b;
		 * 
		 * int j = 5;
		 * 
		 * for (int i = 0; i < jestr.length(); i++) { switch (jestr.charAt(i)) {
		 * case '0': com[j] = 0x30; break; case '1': com[j] = 0x31; break; case
		 * '2': com[j] = 0x32; break; case '3': com[j] = 0x33; break; case '4':
		 * com[j] = 0x34; break; case '5': com[j] = 0x35; break; case '6':
		 * com[j] = 0x36; break; case '7': com[j] = 0x37; break; case '8':
		 * com[j] = 0x38; break; case '9': com[j] = 0x39; break;
		 * 
		 * }
		 * 
		 * j = j + 1; }
		 * 
		 * com[11] = 0x1b; com[12] = 0x03;
		 * 
		 * log("发送售价[Start]......\n\tInput:" + String.valueOf(com));
		 * 
		 * port.sendString(String.valueOf(com));
		 * 
		 * log("发送售价[End]......");
		 * 
		 * String str = readComm();
		 * 
		 * System.out.println("接收价格数据 "+str);
		 * 
		 * if (str == null) { new MessageBox("没有接收到串口数据"); return false; }
		 * 
		 * str = Integer.toHexString(Integer.parseInt(str));
		 * 
		 * if (!str.equals("6")) { new MessageBox("发送串口数据失败,返回代码:\n" + str);
		 * return false; }
		 * 
		 * return true;
		 */
	}

	public boolean sendData5(String data)
	{
		// TODO Auto-generated method stub
		return false;
	}

	// 获取重量
	public boolean recvData3()
	{
		try
		{
			char[] com = { 0x04, 0x05 };

			log("发送获取重量指令[Start]......\n\tInput:" + String.valueOf(com));

			port.sendString(String.valueOf(com));
			String str = readData();

			log("发送获取重量指令[End]......");

			if (str == null)
			{
				log("获取重量null");
				return false;
			}

			if (str.length() <= 2 && Integer.toHexString(Integer.parseInt(str)).equals("15"))
				return false;

			String data = ManipulateStr.getNumToHexStr(str);

			if (data == null)
				return false;

			setWeight(ManipulatePrecision.div(Double.parseDouble(ManipulateStr.getHexStrToChar(data.substring(18, 32))), 1000));

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			log("发送获取重量指令异常:" + ex.getMessage());
			return false;
		}
	}

	public void setData1(double memo1)
	{
		// TODO Auto-generated method stub

	}

	public void setData2(double memo2)
	{
		// TODO Auto-generated method stub

	}

	public void setData3(double memo3)
	{
		// TODO Auto-generated method stub

	}

	public boolean recvData1()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public double getData1()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public double getData2()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public double getData3()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public void setData4(double memo4)
	{
		// TODO Auto-generated method stub

	}

	public void setData5(double memo5)
	{
		// TODO Auto-generated method stub

	}

	public boolean recvData4()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean recvData5()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public double getData4()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public double getData5()
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
