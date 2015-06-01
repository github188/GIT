package device.ICCard;

import java.util.Vector;

import mwcard.CardReader.RF_EYE_U010.MWRFJavaAPI;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.DeviceName;

public class MwicKRF35_ICCard extends Mwic_ICCard
{
	public Vector getPara()
	{
		Vector v = new Vector();
		v.add(new String[] { "端口号", "0", "1", "2", "3" });
		v.add(new String[] { "波特率", "9600", "110", "300", "600", "1200", "2400", "4800", "19200", "115200" });
		v.add(new String[] { "扇区" });
		v.add(new String[] { "块号" });
		v.add(new String[] { "密码" });
		v.add(new String[] { "读卡成功蜂鸣时长(ms)" });
		v.add(new String[] { "读卡失败蜂鸣时长(ms)" });
		v.add(new String[] { "是否十六进制读取(Y/N)" });
		//v.add(new String[]{"javaposbank.exe参数行"});
		return v;
	}

	public String findCard()
	{
		if (DeviceName.deviceICCard.length() <= 0) return null;
		try
		{
			String[] arg = DeviceName.deviceICCard.split(",");
			short port = 100; // 串口号(从0开始计数)
			int baud = 15200; // 波特率
			short sector = 2; // 扇区
			short block = 8; // 块号           一个扇区有4块，3块数据区域（0~2），1块保存密码（3）  若读取3扇区1块号，应设置为13
			short cerMode = 0; // 密码认证模式(0-2为读， 4-6为写)
			String pwd = "343033373936";// "343033373936"; //"72657A696E2A"; // 读密码
			short beeftime = 30; // 蜂鸣时长
			short falsebeeftime = 60;
			boolean isReadHexMode = true;// 读取方式是否以16进制方式读取（若数据长度大于16则采用16进制读取方式,小于等于16位，则设为false）
			if (arg.length > 0) port = (short) Integer.parseInt(arg[0].trim());

			if (arg.length > 1) baud = Integer.parseInt(arg[1].trim());

			if (arg.length > 2) sector = (short) Integer.parseInt(arg[2].trim());

			if (arg.length > 3) block = (short) Integer.parseInt(arg[3].trim());

			if (arg.length > 4) pwd = arg[4].trim();

			if (arg.length > 5) beeftime = (short) Integer.parseInt(arg[5].trim());

			if (arg.length > 6) falsebeeftime = (short) Integer.parseInt(arg[6].trim());

			if (arg.length > 7) if(arg[6].trim().equals("N")){
				isReadHexMode = false;
			}
			String icCardNo = null;
			boolean ok = false;

			System.out.println("Begining of running M1 card example.");

			// 若此地报错，请加载 org.javapos.jar/Windows下的nwrf_IC.jar
			// 1. 打开串口
			int icdev = MWRFJavaAPI.getDefault().rf_init(port, baud);// Open_USB()
			if (icdev < 0)
			{
				// 没有接通、没有上电、设备坏了
				new MessageBox("\nCann't get device id.");
				System.out.println("\nCann't get device id.");
				return null;
			}

			/**
			 * rf_request 循环寻卡芯片号，读出卡的ID号 （若不循环寻卡，去掉do...while） s1 参数值含义如下：
			 * 0——表示IDLE模式，一次只对一张卡操作； 1——表示ALL模式，一次可对多张卡操作；
			 * 2——表示指定卡模式，只对序列号等于snr的卡操作（高级函数才有）
			 * */
			short s1 = (short) 0;

			if (MWRFJavaAPI.getDefault().rf_request(icdev, s1) != 0)
			{
				System.out.println("函数rf_request调用失败");
			}
			else
			{
				//    			 3. 防冲突 (第二个参数Bcn,写死为0)
				short st = MWRFJavaAPI.getDefault().rf_anticoll(icdev, (short) 0);

				// st代表成功
				if (st == 0)
				{
					// 4. 得到snr
					long _Snr = MWRFJavaAPI.getDefault().getSnr();

					// 5. 用得到的snr去选卡
					st = MWRFJavaAPI.getDefault().rf_select(icdev, _Snr);
					if (st == 0)
					{
						// 6.载入读密码
						// pwd 只能为16进制密码，并且必须是12位
						// sector代表扇区号
						st = MWRFJavaAPI.getDefault().rf_load_key_hex(icdev, (short) cerMode, (short) sector, pwd.toCharArray());
						if (st == 0)
						{
							// 7.授权待读的扇区块号
							st = MWRFJavaAPI.getDefault().rf_authentication(icdev, (short) cerMode, (short) sector);
							if (st == 0)
							{
								char[] data = null;
								// 8.读出卡号
								// block 为块号，一个扇区有4块，3块数据区域，1块保存密码,取值范围 0-2

								if (isReadHexMode)
								{
									st = MWRFJavaAPI.getDefault().rf_read_hex(icdev, (short) block);
									data = MWRFJavaAPI.getDefault().getHexData();
									icCardNo = String.valueOf(data);
									System.out.println(icCardNo);
								}
								else
								{
									st = MWRFJavaAPI.getDefault().rf_read(icdev, (short) block);
									data = MWRFJavaAPI.getDefault().getData();
									icCardNo = String.copyValueOf(data);
									System.out.println(icCardNo);
								}
								ok = true;
								MWRFJavaAPI.getDefault().rf_beep(icdev, beeftime);
							}
							else
							{
								System.out.println("函数rf_authentication调用失败");
							}
						}
						else
						{
							System.out.println("函数rf_load_key_hex调用失败");
						}
					}
					else
					{
						System.out.println("函数rf_select调用失败");
					}
				}
				else
				{
					System.out.println("函数rf_anticoll调用失败");
				}
			}

			if (!ok)
			{
				MWRFJavaAPI.getDefault().rf_beep(icdev, falsebeeftime);
				new MessageBox("读卡失败,请重试!");
			}
			// 9. 中止对卡操作
			MWRFJavaAPI.getDefault().rf_halt(icdev);

			// 10.关闭串口
			MWRFJavaAPI.getDefault().rf_exit(icdev);// Close_USB(icdev);

			System.out.println("End of running M1 card example.");
			System.out.println(icCardNo);
			icCardNo = icCardNo.replace("E", "=");
			icCardNo = icCardNo.replace("F", "");
			System.out.println(icCardNo);
			return icCardNo;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public String getDiscription()
	{
		return "明华KRF-35IC卡设备";
	}
}
