package device.ICCard;

import java.util.Vector;

import mwcard.CardReader.RF_EYE_U010.MWRFJavaAPI;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.DeviceName;

public class JL_ICCARD extends Mwic_ICCard
{
	public Vector getPara()
	{

		//v.add(new String[]{"javaposbank.exe参数行"});
		return null;
	}

	public String findCard()
	{
		String ret = "";
		try{
		
//		 以下DEMO仅针对M1卡
		short port = 100; // 串口号(从0开始计数)
		int baud = 15200; // 波特率
		short sector = 15; // 扇区
		short block = 61; // 块号
		short cerMode = 0; // 密码认证模式(0-2为读， 4-6为写)
		String pwd = "FFFFFFFFFFFF";// "343033373936"; //"72657A696E2A"; // 读密码
		boolean isReadHexMode = true; // 读取方式是否以16进制方式读取（若数据长度大于16则采用16进制读取方式,小于等于16位，则设为false）
		short beeftime = 30; // 蜂鸣时长

		System.out.println("Begining of running M1 card example.");

		// 若此地报错，请加载 org.javapos.jar/Windows下的nwrf_IC.jar
		// 1. 打开串口
		int icdev = MWRFJavaAPI.getDefault().rf_init(port, baud);// Open_USB()
		if (icdev < 0)
		{
			// 没有接通、没有上电、设备坏了
			System.out.println("\nCann't get device id.");
			return "error";
		}

		/**
		 * rf_request 循环寻卡芯片号，读出卡的ID号 （若不循环寻卡，去掉do...while） s1 参数值含义如下：
		 * 0——表示IDLE模式，一次只对一张卡操作； 1——表示ALL模式，一次可对多张卡操作；
		 * 2——表示指定卡模式，只对序列号等于snr的卡操作（高级函数才有）
		 * */
		short s1 = (short) 0;
		do
		{
			if (MWRFJavaAPI.getDefault().rf_request(icdev, s1) == 0)
				break;
		} while (true);

		// 3. 防冲突 (第二个参数Bcn,写死为0)
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
							System.out.println(data);
							ret=  String.valueOf(data);
						}
						else
						{
							st = MWRFJavaAPI.getDefault().rf_read(icdev, (short) block);
							data = MWRFJavaAPI.getDefault().getData();
							String cardno = String.copyValueOf(data);
							System.out.println(cardno);
							ret = cardno;
						}
						
						MWRFJavaAPI.getDefault().rf_beep(icdev, beeftime);
					}
				}
			}
		}

		// 9. 中止对卡操作
		MWRFJavaAPI.getDefault().rf_halt(icdev);

		// 10.关闭串口
		MWRFJavaAPI.getDefault().rf_exit(icdev);// Close_USB(icdev);

		System.out.println("End of running M1 card example.");
		}catch (Exception er)
		{
			er.printStackTrace();
		}
		ret = ret.replaceAll("F", "");
		return ret.trim();
	}

	public String getDiscription()
	{
		return "明华JL卡设备";
	}
}
