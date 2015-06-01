package device.ICCard;
//若此地报错，请加载 org.javapos.jar/Windows下的nwrf_IC.jar
import mwcard.CardReader.RF_EYE_U010.MWRFJavaAPI;

import com.efuture.commonKit.ManipulateStr;

/**
 * 若测试失败，请检查org.javapos.lib/windows下的nsvcr100.dll,nwrf32.dll,nwrfjavaapi.dll
 * 以上三个文件是否存在。
 * 
 * */
public class MWRF_Write_Demo
{
	public static void main(String[] args)
	{
		//以下DEMO仅针对M1卡
		short port = 3; // 串口号(从0开始计数)
		int baud = 15200; // 波特率
		short sector = 0; // 扇区
		short block = 2; // 块号
		short cerMode = 4; // 密码认证模式(0-2为读， 4-6为写)
		String pwd = "FFFFFFFFFFFF"; // 写密码

		short beeftime = 30; // 蜂鸣时长

		System.out.println("Begining of running M1 card example.");

		// 若此地报错，请加载 org.javapos.jar/Windows下的nwrf_IC.jar
		// 1. 打开串口
		int icdev = MWRFJavaAPI.getDefault().rf_init(port, baud);
		if (icdev < 0)
		{
			// 没有接通、没有上电、设备坏了
			System.out.println("\nCann't get device id.");
			return;
		}

		// 2. 循环寻卡,读出卡的ID号 （若不循环寻卡，去掉do...while）
		do
		{
			if (MWRFJavaAPI.getDefault().rf_request(icdev, (short) 0) == 0)
				break;
		} while (true);

		// 3. 防冲突 (第二个参数为防冲突模式，0：一次只读一张卡，1：一次可读多张)
		short st = MWRFJavaAPI.getDefault().rf_anticoll(icdev, (short) 0);

		if (st == 0)
		{
			// 4. 获取卡序列号地址snr
			long _Snr = MWRFJavaAPI.getDefault().getSnr();

			// 5. 用得到的snr去选卡
			st = MWRFJavaAPI.getDefault().rf_select(icdev, _Snr);
			if (st == 0)
			{
				// 6.载入读密码
				st = MWRFJavaAPI.getDefault().rf_load_key_hex(icdev, (short) cerMode, (short) sector, pwd.toCharArray());
				if (st == 0)
				{
					// 7.授权待读的扇区块号
					st = MWRFJavaAPI.getDefault().rf_authentication(icdev, (short) cerMode, (short) sector);
					if (st == 0)
					{
						// 8.写卡号
						String card = "888888";
						card = ManipulateStr.PadRight(card, 16, ' '); // 凑足16位
						st = MWRFJavaAPI.getDefault().rf_write(icdev, block, card.toCharArray());
						MWRFJavaAPI.getDefault().rf_beep(icdev, beeftime);
					}
				}
			}
		}

		// 9. 中止对卡操作
		MWRFJavaAPI.getDefault().rf_halt(icdev);

		// 10.关闭串口
		MWRFJavaAPI.getDefault().rf_exit(icdev);
		System.out.println("End of running M1 card example.");

	}

}
