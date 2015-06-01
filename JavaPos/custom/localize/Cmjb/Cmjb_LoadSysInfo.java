package custom.localize.Cmjb;

import java.io.BufferedReader;

import org.eclipse.swt.widgets.Label;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bcrm.Bcrm_LoadSysInfo;
import custom.localize.Hzjb.Hzjb_ICCardCaller;

public class Cmjb_LoadSysInfo extends Bcrm_LoadSysInfo
{

	public boolean getConfigTemplate(Label lbl_message)
	{
		setLabelHint(lbl_message, "正在读取联华小票打印模版......");

		if (!PathFile.fileExist("lh.ini") && !Cmjb_LHSaleBillMode.getInstance().ReadTemplateFile())
			new MessageBox("读取小票打印模版文件错误!");

		return super.getConfigTemplate(lbl_message);
	}

	public boolean getNetNewData(Label lbl_message)
	{
		if (super.getNetNewData(lbl_message))
		{
			setLabelHint(lbl_message, "正在检查是否有银联卡签购单未打印");
			printQGD();

			if (GlobalInfo.sysPara != null && GlobalInfo.sysPara.isEnableLHCard == 'Y')
			{
				if (PathFile.fileExist("C:\\javapos\\Hzjb_LHICCard.dll"))
				{
					setLabelHint(lbl_message, "正在加载联华储值卡模块.....");
					if (Hzjb_ICCardCaller.getDefault().start() == 0)
					{
						Hzjb_ICCardCaller.getDefault().setEnable(true);
					}
					else
					{
						new MessageBox(Hzjb_ICCardCaller.getDefault().getLastError());
					}
				}
				else
				{
					new MessageBox("未发现联华储值卡模块");
				}
			}

			return true;
		}

		return false;
	}

	public boolean startBroken(boolean b)
	{
		if (super.startBroken(b))
		{
			if (!PathFile.fileExist("lh.ini") && Hzjb_ICCardCaller.getDefault().getEnable())
			{
				if (Hzjb_ICCardCaller.getDefault().login(GlobalInfo.posLogin.gh) != 0)
					new MessageBox(Hzjb_ICCardCaller.getDefault().getLastError());
			}
			return true;
		}
		
		return false;
	}

	public boolean quitLoadInfo(Label lbl_message)
	{
		if (!PathFile.fileExist("lh.ini") && Hzjb_ICCardCaller.getDefault().getEnable())
		{
			setLabelHint(lbl_message, "正在注销联华储值卡用户......");
			if (Hzjb_ICCardCaller.getDefault().logout() != 0)
				new MessageBox(Hzjb_ICCardCaller.getDefault().getLastError());
		}
		return super.quitLoadInfo(lbl_message);
	}

	public void ExitSystem(String msg)
	{
		super.ExitSystem(msg);

		// Java释放dll时，会导致主程序资源释放，所以对dll的释放，始终要放在主程序结束处，切记
		if (!PathFile.fileExist("lh.ini") && Hzjb_ICCardCaller.getDefault().getEnable())
			Hzjb_ICCardCaller.getDefault().stop();
	}

	public void printQGD()
	{
		ProgressBox pb = null;

		try
		{
			String printName = "c:\\gmc\\toprint.txt";
			if (!PathFile.fileExist(printName)) { return; }

			pb = new ProgressBox();
			pb.setText("正在打印银联签购单,请等待...");

			for (int i = 0; i < 1; i++)
			{

				BufferedReader br = null;

				try
				{
					br = CommonMethod.readFileGBK(printName);

					if (br == null)
					{
						new MessageBox("打开" + printName + "打印文件失败!");

						return;
					}

					//
					String line = null;

					while ((line = br.readLine()) != null)
					{
						if (line.trim().equals("/CUT"))
						{
							Printer.getDefault().cutPaper_Normal();
							continue;
						}

						Printer.getDefault().printLine_Normal(line);
					}
				}
				catch (Exception e)
				{
					new MessageBox(e.getMessage());
				}
				finally
				{
					if (br != null)
					{
						br.close();
					}
				}

				// XYKPrintDoc_End();

			}

			PathFile.deletePath(printName);
		}
		catch (Exception ex)
		{
			new MessageBox("打印签购单发生异常\n\n" + ex.getMessage());
			ex.printStackTrace();
		}
		finally
		{
			if (pb != null)
			{
				pb.close();
				pb = null;
			}
		}
	}
}
