package custom.localize.Ajbs;

import org.eclipse.swt.widgets.Label;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.BankTracker;
import com.efuture.javaPos.Device.CashBox;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.ElectronicScale;
import com.efuture.javaPos.Device.KeyBoard;
import com.efuture.javaPos.Device.LineDisplay;
import com.efuture.javaPos.Device.MSR;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Device.Scanner;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.LoadSysInfo;

public class Ajbs_LoadSysInfo extends LoadSysInfo
{
	public boolean initDevice(Label lbl_message)
	{
		try
		{
			// 读取设配逻辑名
			new DeviceName();

			//
			setLabelHint(lbl_message, "正在初始化键盘设备......");

			if (KeyBoard.getDefault().isValid())
			{
				if (!KeyBoard.getDefault().open())
				{
					new MessageBox("专业键盘设备初始化失败!\n" + ConfigClass.KeyBoard1 + " " + DeviceName.deviceKeyBoard);
				}
				else
				{
					// 开机即启用键盘设备
					KeyBoard.getDefault().setEnable(true);
				}
			}

			//
			setLabelHint(lbl_message, "正在初始化打印设备......");

			if (Printer.getDefault().isValid())
			{
				while (true)
				{
					if (!Printer.getDefault().open())
					{
						if (new MessageBox("打印机设备初始化失败! \n 是否尝试重新初始化设备?", null, true).verify() == GlobalVar.Key1)
						{
							continue;
						}
						else
						{
							break;
						}
					}
					else
					{
						// 开机即启用打印设备
						Printer.getDefault().setEnable(true);
						break;
					}
				}
			}

			//
			setLabelHint(lbl_message, "正在初始化刷卡设备......");

			if (MSR.getDefault().isValid())
			{
				while (true)
				{
					if (!MSR.getDefault().open())
					{
						if (new MessageBox("刷卡设备初始化失败! \n 是否尝试重新初始化设备?", null, true).verify() == GlobalVar.Key1)
						{
							continue;
						}
						else
						{
							break;
						}
					}
					else
					{
						// 开机即启用刷卡设备
						MSR.getDefault().setEnable(true);

						break;
					}
				}
			}

			//
			setLabelHint(lbl_message, "正在初始化客显设备......");

			if (LineDisplay.getDefault().isValid())
			{
				while (true)
				{
					if (!LineDisplay.getDefault().open())
					{
						if (new MessageBox("客显设备初始化失败! \n 是否尝试重新初始化设备?", null, true).verify() == GlobalVar.Key1)
						{
							continue;
						}
						else
						{
							break;
						}
					}
					else
					{
						// 开机即启用客显设备
						LineDisplay.getDefault().setEnable(true);

						break;
					}
				}
			}

			//
			setLabelHint(lbl_message, "正在初始化钱箱设备......");

			if (CashBox.getDefault().isValid())
			{
				while (true)
				{
					if (!CashBox.getDefault().open())
					{
						if (new MessageBox("钱箱设备初始化失败! \n 是否尝试重新初始化设备?", null, true).verify() == GlobalVar.Key1)
						{
							continue;
						}
						else
						{
							break;
						}
					}
					else
					{
						// 开机即启用钱箱设备
						CashBox.getDefault().setEnable(true);

						if (ConfigClass.MultiInstanceMode.equals("Y"))
						{
							if (Printer.getDefault() != null && Printer.getDefault().getStatus())
								Printer.getDefault().close();
						}
						break;
					}
				}
			}

			//
			setLabelHint(lbl_message, "正在初始化扫描设备......");

			if (Scanner.getDefault().isValid())
			{
				while (true)
				{
					if (!Scanner.getDefault().open())
					{
						if (new MessageBox("扫描设备初始化失败!\n 是否尝试重新初始化设备?", null, true).verify() == GlobalVar.Key1)
						{
							continue;
						}
						else
						{
							break;
						}
					}
					else
					{
						// 开机即启用串口设备
						Scanner.getDefault().setEnable(true);

						break;
					}
				}
			}

			setLabelHint(lbl_message, "正在初始化电子秤设备......");
			if (ElectronicScale.getDefault().isValid())
			{
				while (true)
				{
					if (!ElectronicScale.getDefault().open())
					{
						if (new MessageBox("电子秤设备初始化失败!\n 是否尝试重新初始化设备?", null, true).verify() == GlobalVar.Key1)
						{
							continue;
						}
						else
						{
							break;
						}
					}
					else
					{
						ElectronicScale.getDefault().setEnable(true);

						break;
					}
				}
			}

			setLabelHint(lbl_message, "正在初始化第三方刷卡设备......");
			if (BankTracker.getDefault().isValid())
			{
				while (true)
				{
					if (!BankTracker.getDefault().open())
					{
						if (new MessageBox("第三方刷卡设备始化失败!\n 是否尝试重新初始化设备?", null, true).verify() == GlobalVar.Key1)
						{
							continue;
						}
						else
						{
							break;
						}
					}
					else
					{
						BankTracker.getDefault().setEnable(true);

						break;
					}
				}
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
			new MessageBox(er.getMessage());

		}

		return true;
	}

	protected void checkDeviceMessage()
	{
		// 提示打印机是否有效
		if (!ConfigClass.DebugMode && !Printer.getDefault().getStatus())
		{
			if (!ConfigClass.MultiInstanceMode.equals("Y"))
			{
				AccessDayDB.getDefault().writeWorkLog("打印机未连接,打印操作将无法执行");

				new MessageBox("打印机未连接，打印操作将无法执行");
			}
		}

		// 调试模式下提示客显,打印机是否已启用
		if (ConfigClass.DebugMode && ConfigClass.LineDispaly1 != null && ConfigClass.LineDispaly1.length() > 12 && GlobalInfo.syjDef.isdisp == 'N')
		{
			new MessageBox("收银机定义中未启用客显");
		}

		if (ConfigClass.DebugMode && ConfigClass.Printer1 != null && ConfigClass.Printer1.length() > 8 && GlobalInfo.syjDef.isprint == 'N')
		{
			new MessageBox("收银机定义中未启用打印机");
		}
	}
}
