package com.efuture.javaPos.Global;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.swtdesigner.SWTResourceManager;

//这个类用来加载系统配置文件信息
public class ConfigClass
{
	public static String CashRegisterCode = null;
	public static String CDKey = null;
	public static String Market = null;
	public static String MarketText = "mkt";
	public static String AliasMarket = "";
	
	public static String ServerOS = "Windows";
	public static String ServerIP = null;
	public static String ServerPath = null;
	public static int ServerPort = 0;
	public static int ConnectTimeout = 10000;
	public static int ReceiveTimeout = 30000;
	public static boolean DebugMode = false;
	public static String DebugModeString = "";
	public static boolean KeepActive = false;
	public static String BackImagePath = null;
	public static String SaleBackImage = null;
	public static String SaleWarnImage = null;
	public static Vector BackImageVector = null;
	public static String LocalDBPath = null;
	public static String LocalDBType = "Derby";
	public static String Printer1 = null;
	public static String Msr1 = null;
	public static String KeyBoard1 = null;
	public static String CashBox1 = null;
	public static String ICCard1 = null;
	public static String ElectronicScale1 = null;
	public static String LineDispaly1 = null;
	public static String BankTracker1 =null;
	
	public static String Scanner1 = "";
	public static String LogFile = null;
	public static String FtpIP = null;
	public static int FtpPort = 21;
	public static String FtpPath = "";
	public static String FtpUser = "anonymous";
	public static String FtpPwd = "";
	public static int FtpTimeout = 0;
	public static int FtpDefaultTimeout = 0;
	public static int FtpDataTimeout = 0;
	public static String Ftppasv = "N";
	public static String FtpDayEnd = "N";
	public static String Bankfunc = "";
	public static String BankPath = "c:\\gmc";
	public static String BankConfig = "";
	public static int BankPageSize = 0;
	public static String TitleStyle = "Windows";
	public static String Language = null;
	public static String Language_Font = "宋体";
	public static String DisplayMode = "Fobs";
	public static String SoundSuccess = null;
	public static String SoundFail = null;
	public static String FontName = null;
	public static String curRunning = null;
	public static String EnableCmdLog = null;
	public static String HaltCmd = null;
	public static String RebootCmd = null;
	public static String FastrunCmd = null;
	public static boolean MouseMode = false;
	public static String TouchModelCfg = "";
	public static String DataBaseEnable = "N";
	public static String DataBaseDriver = "";
	public static String DataBaseUrl = "";
	public static String DataBaseUser = "";
	public static String DataBasePwd = "";
	public static String MultiInstanceMode ="N";

	public static String IsOpenDisplay = "N";
	public static String DisplayText = "";
	public static boolean IsSecMonitorDisplay = false;
	public static boolean IsBotton = false;
	public static int Timeout = 10;
	public static int X_POSITION = 0;
	public static int Y_POSITION = 0;
	public static String DisplayChooses = "";
	public static int textWidth = 0;
	public static int lineheight_new = 0;
	public static int textSize = 0;
	
	public static char ck = 'N';

	final Color groupbkmouseup = SWTResourceManager.getColor(253, 242, 108);
	final Color groupfemouseup = SWTResourceManager.getColor(0, 64, 128);

	final Color groupbkmousedown = SWTResourceManager.getColor(0, 64, 128);
	final Color groupfemousedown = SWTResourceManager.getColor(255, 255, 255);

	final Color goodsbkmouseup = SWTResourceManager.getColor(255, 142, 142);
	final Color goodsfemouseup = SWTResourceManager.getColor(0, 64, 128);

	final Color goodsbkmousedown = SWTResourceManager.getColor(0, 64, 128);
	final Color goodsfemousedown = SWTResourceManager.getColor(255, 255, 255);

	public static String TouchSaleForm = "";
	public static int SplitScrSize = 0;
	public static int ScreenKeyboard = 0;
	public static String httpErrorLength[] = null;

	public static Vector CustomPayment = null;
	public static Vector MessageBoxVerify = null;

	// 报表打印栈
	public static int RepPrintTrack = 2;

	// 第2显示器
	public static String SecMonitor_Open = "";
	public static String ServerID = "";
	public static int Port;
	public static String MediaPath = "";

	// 客户化配置
	public static String Plugins1 = "";
	public static String Plugins2 = "";
	public static String Plugins3 = "";
	public static String Plugins4 = "";
	public static String Plugins5 = "";
	
	// 客户化配置
	public static String CustomItem1 = "";
	public static String CustomItem2 = "";
	public static String CustomItem3 = "";
	public static String CustomItem4 = "";
	public static String CustomItem5 = "";

	// 快速付款键配置
	public static HashMap QuickPay = null;
	
	//专卖行业脱网是否显示时间输入框
	public static boolean ShowDateDialog = true;

	public static boolean LoadConfigSet()
	{
		BufferedReader br;
		BufferedReader br1;
		try
		{
			if (PathFile.fileExist("miniplayer.exe") && PathFile.fileExist("mplayer.exe"))
			{
				if (PathFile.fileExist("success.wav"))
				{
					SoundSuccess = "miniplayer.exe success.wav";
				}
				if (PathFile.fileExist("fail.wav"))
				{
					SoundFail = "miniplayer.exe fail.wav";
				}
			}

			// 读取Config.ini
			br = CommonMethod.readFile(GlobalVar.ConfigPath + "/PosID.ini");
			br1 = CommonMethod.readFile(GlobalVar.ConfigFile);

			if (br == null || br1 == null)
			{
				new MessageBox(com.efuture.javaPos.Global.Language.apply("配置文件导入错误,马上退出"), null, false);

				return false;
			}

			String line;
			String[] sp;

			while ((line = br.readLine()) != null)
			{
				if ((line == null) || (line.length() <= 0))
				{
					continue;
				}

				String[] lines = line.split("&&");
				sp = lines[0].split("=");
				if (sp.length < 2)
					continue;

				if (sp[0].trim().compareToIgnoreCase("CashRegisterCode") == 0)
				{
					CashRegisterCode = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("CDKey") == 0)
				{
					CDKey = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("Market") == 0)
				{
					Market = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("MarketText") == 0)
				{
					MarketText = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("AliasMarket") == 0)
				{
					AliasMarket = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("CK") == 0)
				{
					ck = sp[1].trim().charAt(0);
				}
			}

			while ((line = br1.readLine()) != null)
			{
				if ((line == null) || (line.length() <= 0))
				{
					continue;
				}

				String[] lines = line.split("&&");
				sp = lines[0].split("=");
				if (sp.length < 2)
					continue;

				if (sp[0].trim().compareToIgnoreCase("ServerOS") == 0)
				{
					ServerOS = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("ServerIP") == 0)
				{
					ServerIP = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("ServerPath") == 0)
				{
					ServerPath = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("ServerPort") == 0)
				{
					ServerPort = Integer.parseInt(sp[1].trim());
				}
				else if (sp[0].trim().compareToIgnoreCase("ConnectTimeout") == 0)
				{
					ConnectTimeout = Integer.parseInt(sp[1].trim());
				}
				else if (sp[0].trim().compareToIgnoreCase("ReceiveTimeout") == 0)
				{
					ReceiveTimeout = Integer.parseInt(sp[1].trim());
				}
				else if (sp[0].trim().compareToIgnoreCase("FtpIP") == 0)
				{
					FtpIP = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("FtpPath") == 0)
				{
					FtpPath = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("FtpPort") == 0)
				{
					FtpPort = Integer.parseInt(sp[1].trim());
				}
				else if (sp[0].trim().compareToIgnoreCase("FtpUser") == 0)
				{
					FtpUser = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("FtpTimeout") == 0)
				{
					FtpTimeout = Integer.parseInt(sp[1].trim());
				}
				else if (sp[0].trim().compareToIgnoreCase("FtpDefaultTimeout") == 0)
				{
					FtpDefaultTimeout = Integer.parseInt(sp[1].trim());
				}
				else if (sp[0].trim().compareToIgnoreCase("FtpDataTimeout") == 0)
				{
					FtpDataTimeout = Integer.parseInt(sp[1].trim());
				}
				else if (sp[0].trim().compareToIgnoreCase("RepPrintTrack") == 0)
				{
					RepPrintTrack = Integer.parseInt(sp[1].trim());
				}
				else if (sp[0].trim().compareToIgnoreCase("FtpPwd") == 0)
				{
					FtpPwd = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("Ftppasv") == 0)
				{
					Ftppasv = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("FtpDayEnd") == 0)
				{
					FtpDayEnd = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("DebugMode") == 0)
				{
					// Y-调试模式/D-开发模式/S-不关闭任务栏
					DebugModeString = sp[1].trim();
					if (DebugModeString.equals("Y") || DebugModeString.equals("D"))
					{
						DebugMode = true;
					}
					else
					{
						DebugMode = false;
					}
				}
				else if (sp[0].trim().compareToIgnoreCase("LogFile") == 0)
				{
					LogFile = sp[1].trim();
					if (LogFile != null && LogFile.length() > 0)
						PublicMethod.createTraceDebugLog(LogFile);
				}
				else if (sp[0].trim().compareToIgnoreCase("KeepActive") == 0)
				{
					if (sp[1].trim().equals("Y"))
						KeepActive = true;
					else
						KeepActive = false;
				}
				else if (sp[0].trim().compareToIgnoreCase("BackImagePath") == 0)
				{
					BackImagePath = sp[1].trim();

					if (PathFile.fileExist(ConfigClass.BackImagePath + "salebkimage.jpg"))
					{
						SaleBackImage = ConfigClass.BackImagePath + "salebkimage.jpg";
					}
					if (PathFile.fileExist(ConfigClass.BackImagePath + "salebkwarn.jpg"))
					{
						SaleWarnImage = ConfigClass.BackImagePath + "salebkwarn.jpg";
					}
					if (PathFile.fileExist(ConfigClass.BackImagePath + "bkgroundimage.ini"))
					{
						BackImageVector = CommonMethod.readFileByVector(ConfigClass.BackImagePath + "bkgroundimage.ini");
					}
				}
				else if (sp[0].trim().compareToIgnoreCase("LocalDBPath") == 0)
				{
					LocalDBPath = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("LocalDBType") == 0)
				{
					LocalDBType = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("TitleStyle") == 0)
				{
					TitleStyle = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("Language") == 0)
				{
					Language = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("Printer1") == 0)
				{
					Printer1 = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("MSR1") == 0)
				{
					Msr1 = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("KeyBoard1") == 0)
				{
					KeyBoard1 = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("CashBox1") == 0)
				{
					CashBox1 = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("ICCard1") == 0)
				{
					ICCard1 = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("LineDisplay1") == 0)
				{
					LineDispaly1 = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("Scanner1") == 0)
				{
					Scanner1 = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("ElectronicScale1") == 0)
				{
					ElectronicScale1 = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("BankTracker1") == 0)
				{
					BankTracker1 = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("SecMonitor_Open") == 0)
				{
					SecMonitor_Open = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("MediaPath") == 0)
				{
					MediaPath = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("ServerID") == 0)
				{
					ServerID = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("Port") == 0)
				{
					Port = Integer.parseInt(sp[1].trim());
				}
				else if (sp[0].trim().compareToIgnoreCase("BankFunc") == 0)
				{
					Bankfunc = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("BankConfig") == 0)
				{
					BankConfig = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("BankPath") == 0)
				{
					BankPath = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("BankPageSize") == 0)
				{
					BankPageSize = Integer.parseInt(sp[1].trim());
				}
				else if (sp[0].trim().compareToIgnoreCase("DisplayMode") == 0)
				{
					DisplayMode = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("MultiInstanceMode") == 0)
				{
					MultiInstanceMode = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("SoundSuccess") == 0)
				{
					if (PathFile.fileExist("miniplayer.exe") && PathFile.fileExist("mplayer.exe"))
					{
						if (sp[1].trim().length() > 0 && PathFile.fileExist(sp[1].trim()))
						{
							SoundSuccess = "miniplayer.exe " + sp[1].trim();
						}
						else
						{
							SoundSuccess = null;
						}
					}
				}
				else if (sp[0].trim().compareToIgnoreCase("SoundFail") == 0)
				{
					if (PathFile.fileExist("miniplayer.exe") && PathFile.fileExist("mplayer.exe"))
					{
						if (sp[1].trim().length() > 0 && PathFile.fileExist(sp[1].trim()))
						{
							SoundFail = "miniplayer.exe " + sp[1].trim();
						}
						else
						{
							SoundFail = null;
						}
					}
				}
				else if (sp[0].trim().compareToIgnoreCase("FontName") == 0)
				{
					if (sp[1].trim().length() > 0)
						FontName = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("CustomItem1") == 0)
				{
					CustomItem1 = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("CustomItem2") == 0)
				{
					CustomItem2 = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("CustomItem3") == 0)
				{
					CustomItem3 = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("CustomItem4") == 0)
				{
					CustomItem4 = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("CustomItem5") == 0)
				{
					CustomItem5 = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("Plugins1") == 0)
				{
					Plugins1 = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("Plugins2") == 0)
				{
					Plugins2 = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("Plugins3") == 0)
				{
					Plugins3 = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("Plugins4") == 0)
				{
					Plugins4= sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("Plugins5") == 0)
				{
					Plugins5 = sp[1].trim();
				}
				
				else if (sp[0].trim().compareToIgnoreCase("QuickPay") == 0)
				{
					QuickPay = new HashMap();

					String[] quickPayArr = sp[1].trim().split(",");

					String[] eachPay = null;
					for (int i = 0; i < quickPayArr.length; i++)
					{
						eachPay = quickPayArr[i].split(":");
						if (eachPay.length >= 2)
						{
							QuickPay.put(eachPay[0], eachPay[1]);
						}
					}
				}
				else if (sp[0].trim().compareToIgnoreCase("ShowDateDialog")==0)
				{
					if (sp[1].trim().equalsIgnoreCase("N"))
						ShowDateDialog = false;
				}
				else if (sp[0].trim().length() >= 13 && sp[0].trim().substring(0, 13).compareToIgnoreCase("CustomPayment") == 0)
				{
					if (!sp[1].trim().equals(""))
					{
						if (CustomPayment == null)
							CustomPayment = new Vector();
						String[] s = sp[1].trim().split("\\|");
						for (int i = 0; i < s.length; i++)
							CustomPayment.add(s[i]);
					}
				}
				else if (sp[0].trim().compareToIgnoreCase("EnableCmdLog") == 0)
				{
					if (sp[1].trim().length() > 0)
						EnableCmdLog = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("HaltCmd") == 0)
				{
					if (sp[1].trim().length() > 0)
						HaltCmd = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("RebootCmd") == 0)
				{
					if (sp[1].trim().length() > 0)
						RebootCmd = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("FastrunCmd") == 0)
				{
					if (sp[1].trim().length() > 0)
						FastrunCmd = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("MouseMode") == 0)
				{
					String[] s = sp[1].split(",");
					if (s.length > 0 && s[0].trim().equalsIgnoreCase("Y"))
						MouseMode = true;
					else
						MouseMode = false;
					if (s.length > 1)
					{
						if (s[1].indexOf("|") > 0)
						{
							String[] item = s[1].split("\\|");

							if (item.length > 0)
								TouchSaleForm = item[0].trim();
						
							if (item.length>1)
								SplitScrSize = Convert.toInt(item[1].trim());

						}
						// && s[1].trim().equalsIgnoreCase("Touch"))
					}

					if (s.length > 2 && PathFile.fileExist("OnScreenKeyboard.exe"))
						ScreenKeyboard = Convert.toInt(s[2].trim());
					else
						ScreenKeyboard = 0;
				}
				else if (sp[0].trim().compareToIgnoreCase("TouchModelCfg") == 0)
				{
					TouchModelCfg = sp[1].trim();
				}
				else if (sp[0].trim().length() >= 16 && sp[0].trim().substring(0, 16).compareToIgnoreCase("MessageBoxVerify") == 0)
				{
					if (!sp[1].trim().equals(""))
					{
						if (MessageBoxVerify == null)
							MessageBoxVerify = new Vector();
						String s[] = null;
						s = sp[1].trim().split("\\|");
						for (int i = 0; i < s.length; i++)
						{
							MessageBoxVerify.add(s[i]);
						}

					}
				}
			}

			// 
			if (FtpIP == null || FtpIP == "")
				FtpIP = ServerIP;

			if (FtpDefaultTimeout <= 0) FtpDefaultTimeout=20000;
			if (FtpDataTimeout <=0 ) FtpDataTimeout=40000;
			
			br.close();
			br1.close();

			br1 = null;
			br = null;
			
			// 读取远程数据库配置文件
			try
			{
				if (PathFile.fileExist(GlobalVar.ConfigPath + "/RemoteDataBase.ini"))
				{
					br = CommonMethod.readFile(GlobalVar.ConfigPath + "/RemoteDataBase.ini");
					if (br != null)
					{
						while ((line = br.readLine()) != null)
						{
							if (line.trim().length() <= 0)
							{
								continue;
							}

							if (line.trim().charAt(0) == ';') // 判断是否为备注
							{
								continue;
							}
							
							String[] lines = line.split("&&");
							sp = lines[0].split("=");
							if (sp.length < 2)
								continue;
							
							if (sp[0].trim().compareToIgnoreCase("DataBaseEnable") == 0)
							{
								DataBaseEnable = sp[1].trim();
							}
							else if (sp[0].trim().compareToIgnoreCase("DataBaseDriver") == 0)
							{
								DataBaseDriver = sp[1].trim();
							}
							else if (sp[0].trim().compareToIgnoreCase("DataBaseUrl") == 0)
							{
								String strvalue = "";
								for (int i=1;i < sp.length;i++)
								{
									if (sp[i] == null || sp[i].trim().length() <= 0) break;
									strvalue = strvalue + (strvalue.length()>0?"=":"") + sp[i];
								}
								
								DataBaseUrl = strvalue.trim();
							}
							else if (sp[0].trim().compareToIgnoreCase("DataBaseUser") == 0)
							{
								DataBaseUser = sp[1].trim();
							}
							else if (sp[0].trim().compareToIgnoreCase("DataBasePwd") == 0)
							{
								DataBasePwd = sp[1].trim();
							}
							
						}
					}
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if (br != null) br.close();
			}
			
			br = null;
			
			// 读取命令定义发送服务器定义
			try
			{
				if (PathFile.fileExist(GlobalVar.ConfigPath + "/ServerCmd.ini"))
				{
					br = CommonMethod.readFile(GlobalVar.ConfigPath + "/ServerCmd.ini");
					if (br != null)
					{
						String url = null, cmd = null;
						GlobalInfo.otherHttp = new Vector();
						while ((line = br.readLine()) != null)
						{
							if (line.trim().length() <= 0)
							{
								continue;
							}

							if (line.trim().charAt(0) == ';') // 判断是否为备注
							{
								continue;
							}

							// 判断标记
							if ((line.trim().charAt(0) == '[') && (line.trim().charAt(line.trim().length() - 1) == ']'))
							{
								if (url != null && url.trim().length() > 0 && cmd != null && cmd.trim().length() > 0)
								{
									String[] s = new String[] { url, "," + cmd + "," };
									GlobalInfo.otherHttp.add(s);
								}
								url = null;
								cmd = null;
							}
							else
							{
								String[] lines = line.split("&&");
								sp = lines[0].split("=");
								if (sp.length < 2)
									continue;

								if (sp[0].trim().compareToIgnoreCase("URL") == 0)
								{
									url = sp[1].trim();
								}
								else if (sp[0].trim().compareToIgnoreCase("CMD") == 0)
								{
									cmd = sp[1].trim();
								}
							}
						}
						if (url != null && url.trim().length() > 0 && cmd != null && cmd.trim().length() > 0)
						{
							String[] s = new String[] { url, "," + cmd + "," };
							GlobalInfo.otherHttp.add(s);
						}
					}
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if (br != null) br.close();
			}

			// 读取HTTP空串长度配置
			ReadErrorLenStrConfigFile();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			new MessageBox(com.efuture.javaPos.Global.Language.apply("配置文件导入错误") + e.getMessage() + com.efuture.javaPos.Global.Language.apply(",马上退出系统"), null, false);

			return false;
		}

		return true;
	}

	public static boolean isDeveloperMode()
	{
		if (ConfigClass.DebugModeString.equals("D"))
			return true;
		else
			return false;
	}

	public static boolean ReadErrorLenStrConfigFile()
	{
		BufferedReader br = null;

		if (!CommonMethod.isFileExist(GlobalVar.ConfigPath + "//HttpErrorLen.ini"))
			return true;

		br = CommonMethod.readFile(GlobalVar.ConfigPath + "//HttpErrorLen.ini");

		if (br == null) { return false; }

		String line = null;
		String[] sp = null;

		try
		{
			while ((line = br.readLine()) != null)
			{
				if ((line == null) || (line.length() <= 0))
				{
					continue;
				}

				String[] lines = line.split("&&");

				sp = lines[0].split("=");
				if (sp.length < 2)
					continue;

				if (sp[0].trim().compareToIgnoreCase("ErrorLen") == 0)
				{
					if (sp[1].trim().length() > 0)
					{
						httpErrorLength = sp[1].trim().split(",");
					}
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			try
			{
				if (br != null)
				{
					br.close();
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public static String getDefineFontName(String fontname)
	{
		// 为宋体是标准字体可使用自定义字体
		if (FontName != null && FontName.trim().length() > 0 && fontname.equalsIgnoreCase("宋体") && curRunning != null && curRunning.equalsIgnoreCase("JavaPOS")) { return FontName; }

		return fontname;
	}

	public static String getBackgroundImageFile(Object form, String skin)
	{
		String file = null;

		// 检查是否按类名指定了背景图片
		String name = form.getClass().getName();
		if (skin != null && skin.trim().length() > 0)
			name += "_" + skin;
		for (int i = 0; BackImageVector != null && i < BackImageVector.size(); i++)
		{
			String[] s = (String[]) BackImageVector.elementAt(i);
			if (name.indexOf(s[0]) >= 0)
			{
				file = s[1];
				if (file.equalsIgnoreCase("NOIMAGE"))
					return null;
				if (file.indexOf("\\") <= 0 && file.indexOf("/") <= 0)
					file = ConfigClass.BackImagePath + s[1];
			}
		}

		// 未指定背景图片按缺省背景执行
		if (file == null || !PathFile.fileExist(file))
		{
			if (skin != null && skin.startsWith("warn") && ConfigClass.SaleWarnImage != null)
				file = ConfigClass.SaleWarnImage;
			else
				file = ConfigClass.SaleBackImage;
		}

		return file;
	}

	public static void disableTABListener(Composite shell)
	{
		// 鼠标模式直接返回
		if (ConfigClass.MouseMode || shell == null || shell.isDisposed())
			return;

		Control[] ctrls = shell.getChildren();

		for (int i = 0; i < ctrls.length; i++)
		{
			if (ctrls[i].getClass().getName().equals("org.eclipse.swt.widgets.Composite") || ctrls[i].getClass().getName().equals("org.eclipse.swt.widgets.Group"))
			{
				disableTABListener((Composite) ctrls[i]);
			}
			else
			{
				ctrls[i].addTraverseListener(new TraverseListener()
				{
					public void keyTraversed(TraverseEvent arg0)
					{
						// 屏蔽TAB键
						if (arg0.keyCode == 9)
						{
							arg0.doit = false;
						}
					}
				});
			}
		}
	}

	public static Image changeBackgroundImage(Object form, Composite shell, String skin)
	{
		disableTABListener(shell);

		String file = getBackgroundImageFile(form, skin);

		return changeBackgroundImage(shell, file);
	}

	// 加载背景图片
	public static Image changeBackgroundImage(Composite shell, String file)
	{
		Image bkimg = null;

		if (file != null && shell != null && !shell.isDisposed())
		{
			shell.setBackgroundMode(SWT.INHERIT_DEFAULT);
			ImageData imgdata = new ImageData(file);
			if (shell instanceof Composite && shell.getParent() != null)
			{
				imgdata = imgdata.scaledTo(shell.getParent().getClientArea().width, shell.getParent().getClientArea().height);
				bkimg = new Image(shell.getParent().getDisplay(), imgdata);
			}
			else
			{
				imgdata = imgdata.scaledTo(shell.getClientArea().width, shell.getClientArea().height);
				bkimg = new Image(shell.getDisplay(), imgdata);
			}
			shell.setBackgroundImage(bkimg);
		}

		return bkimg;
	}

	// 释放背景图片
	public static void disposeBackgroundImage(Image bkimg)
	{
		if (bkimg != null)
		{
			bkimg.dispose();
		}
	}
}
