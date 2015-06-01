package custom.localize.Cbcp;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.util.Vector;

import org.eclipse.swt.widgets.Label;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.Ftp;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.Unzip;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Communication.UpdateBaseInfo;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Struct.TasksDef;

public class Cbcp_DownloadData
{
	public static void setLabelHint(Label status, String msg)
	{
		if (status != null)
		{
			status.setText(msg);
			status.getDisplay().update();
		}
	}

	public static boolean downloadBaseDB(final Label lbl_message)
	{
		File path = null;

		try
		{
			if (!GlobalInfo.isOnline)
				return false;
			if (ConfigClass.FtpIP == null || ConfigClass.FtpIP.trim().length() <= 0)
				return false;

			// 检查有没有重新下载数据库的命令,有则先把数据库戳删除,重新下载库
			TasksDef t = null;
			if ((t = NetService.getDefault().getTask()) != null && t.type == StatusType.TASK_DOWNLOADAGAIN)
			{
				NetService.getDefault().deleteTask(t.seqno);
				deleteDBDate();
			}

			// 检查当天是否已经下载过数据
			setLabelHint(lbl_message, Language.apply("正在检查当天数据库是否已下载......"));
			if (checkDBDate())
				return true;

			boolean showdateinfo = true;
			boolean delflag = true;
			boolean ret = true;
			
			try
			{
				if (!ConfigClass.FtpDayEnd.equals("Y"))
				{
					File file = new File(ConfigClass.LocalDBPath + "baseflag");
					if (file.exists())
					{
						if (new MessageBox(Language.apply("可能上次下载本地库异常，本次是否重新下载"), null, true).verify() != GlobalVar.Key1)
						{
							delflag = false;
							return false;
						}
					}
					else
					{
						file.createNewFile();
					}

					// 检查是否直接用更新文件来更新BASE日终库,以减少数据下载量,如果一次日终都没下载(BaseDownload.ini)则要下载一次日终,再按增量更新模式处理
					String dayuptfile = ConfigClass.LocalDBPath + "BaseDateSet.ini";
					if (PathFile.fileExist(dayuptfile) && PathFile.fileExist(ConfigClass.LocalDBPath + "BaseDownload.ini"))
					{
						int daynum = 0;
						int dayidx = -1;
						Vector v = CommonMethod.readFileByVector(dayuptfile);
						for (int i = 0; v != null && i < v.size(); i++)
						{
							String[] s = (String[]) v.elementAt(i);
							if (s.length > 0 && s[0].equalsIgnoreCase("DAYSKIP"))
							{
								if (s.length > 1)
									daynum = Convert.toInt(s[1]);
							}
							if (s.length > 0 && s[0].equalsIgnoreCase("DAYLIST"))
							{
								dayidx = i;
							}
						}

						// 本地库时间与当前时间相差天数超过daynum,则直接下载日终库,否则采用增量XML进行更新
						if (daynum > 0)
						{
							String date = readLocalDBDate().split(" ")[0].trim();
							if (ManipulateDateTime.checkDate(date))
							{
								ManipulateDateTime mdt = new ManipulateDateTime();
								long dn = mdt.compareDate(mdt.getDateBySign(), date);
								if (dn > 0 && dn <= daynum)
								{
									// 写入需要下载的日期列表,直到昨天为止
									StringBuffer sb = new StringBuffer();
									for (int i = 0; i < dn; i++)
										sb.append(mdt.skipDate(date, i) + ",");
									if (dayidx >= 0)
										((String[]) v.elementAt(dayidx))[1] = sb.toString();
									else
										v.add(new String[] { "DAYLIST", sb.toString() });
									CommonMethod.writeFileByVector(dayuptfile, v);

									// 不提示脱网数据日期不正确,通过更新刷新
									showdateinfo = false;

									// 标记当天已下载
									setDBDate();
									return true;
								}
							}
						}

						// 清楚更新日期列表
						if (dayidx >= 0)
						{
							v.remove(dayidx);
							CommonMethod.writeFileByVector(dayuptfile, v);
						}
					}
					else
					{
						// 如果存在增量下载配置备份文件,则恢复配置，待本次下载完成以后重新启用增量下载模式
						// 删除BaseDownload.ini和改名BaseDateSet.ini即可执行一次日终库全量下载
						if (PathFile.fileExist(ConfigClass.LocalDBPath + "BaseDateSet.bak"))
						{
							PathFile.copyPath(ConfigClass.LocalDBPath + "BaseDateSet.bak", ConfigClass.LocalDBPath + "BaseDateSet.ini");
							PathFile.deletePath(ConfigClass.LocalDBPath + "BaseDateSet.bak");
						}
					}

					// 当下载的文件大小为0时，重新尝试下载,循环3次
					for (int i = 1; i <= 3; i++)
					{
						setLabelHint(lbl_message, Language.apply("正在第{0}次从FTP下载数据库......",new Object[]{i + ""}));

						// 创建路径
						if (PathFile.isPathExists(ConfigClass.LocalDBPath + "Temp"))
						{
							PathFile.deletePath(ConfigClass.LocalDBPath + "Temp");
						}
						path = new File(ConfigClass.LocalDBPath + "Temp");
						path.mkdirs();

						// FTP下载文件
						final Ftp ftp = new Ftp();
						if (!ftp.connect(ConfigClass.FtpIP, ConfigClass.FtpPort, ConfigClass.FtpUser, ConfigClass.FtpPwd))
						{
							ret = false;
							return ret;
						}

						// 定义了门店号且没明确指定下载路径，则缺省分门店目录进行下载
						String mkt = "";
						if (ConfigClass.Market != null && ConfigClass.Market.trim().length() > 0 && ConfigClass.FtpPath.trim().length() <= 0)
						{
							mkt = "/" + ConfigClass.Market + "/";
						}

						// 下载并刷新进度
						if (!ftp.getFile(ConfigClass.FtpPath + mkt + "Base.zip", ConfigClass.LocalDBPath + "Temp//Base.zip", new Runnable()
						{
							public void run()
							{
								System.out.println("日终库已下载 " + ftp.getRetrieveDataLength() + " KB (" + ftp.getRetrieveDataSpeed() + " K/S),请等待...");
								//setLabelHint(lbl_message, "日终库已下载 " + ftp.getRetrieveDataLength() + " KB (" + ftp.getRetrieveDataSpeed() + " K/S),请等待...");
								//while (Display.getCurrent().readAndDispatch());
							}
						}))
						{
							ftp.close();
							ret = false;
							return ret;
						}

						if (!ftp.getFile(ConfigClass.FtpPath + mkt + "DayEndLog.txt", ConfigClass.LocalDBPath + "Temp//BaseDate.ini"))
						{
							ftp.close();
							ret = false;
							return ret;
						}

						// 关闭FTP连接
						ftp.close();

						// 解压数据库
						setLabelHint(lbl_message, Language.apply("正在解压缩数据库......"));
						Unzip zip = new Unzip();

						// 为true时需要创建derby数据库外层目录
						if (zip.needCreateDerbyDir(ConfigClass.LocalDBPath + "Temp//Base.zip"))
						{
							// 由于POSSERVER生成的Derby数据库压缩的Base.zip不含有Base目录，所以解压时要解压到Base目录中
							zip.unzipAnt(ConfigClass.LocalDBPath + "Temp//Base.zip", ConfigClass.LocalDBPath + "Temp//" + LoadSysInfo.getDefault().getBaseDBName(), 1);
						}
						else
						{
							zip.unzipAnt(ConfigClass.LocalDBPath + "Temp//Base.zip", ConfigClass.LocalDBPath + "Temp", 1);
						}

						// 检查文件大小
						PathFile.pathSize(ConfigClass.LocalDBPath + "Temp//" + LoadSysInfo.getDefault().getBaseDBName());
						if (PathFile.getPathLength() > 0)
						{
							break;
						}
					}
				}
				else
				{
					// 当下载的文件大小为0时，重新尝试下载,循环3次
					for (int i = 1; i <= 3; i++)
					{
						setLabelHint(lbl_message, Language.apply("正在第{0}次从FTP下载数据库......",new Object[]{i + ""}));

						// 创建路径
						if (PathFile.isPathExists(ConfigClass.LocalDBPath + "Temp"))
						{
							PathFile.deletePath(ConfigClass.LocalDBPath + "Temp");
						}
						path = new File(ConfigClass.LocalDBPath + "Temp");
						path.mkdirs();

						// FTP下载文件
						final Ftp ftp = new Ftp();
						if (!ftp.connect(ConfigClass.FtpIP, ConfigClass.FtpPort, ConfigClass.FtpUser, ConfigClass.FtpPwd))
						{
							ret = false;
							return ret;
						}

						// 定义了门店号且没明确指定下载路径，则缺省分门店目录进行下载
						String mkt = "";
						if (ConfigClass.Market != null && ConfigClass.Market.trim().length() > 0 && ConfigClass.FtpPath.trim().length() <= 0)
						{
							mkt = ConfigClass.Market + "/";
						}

						// 下载并刷新进度
						if (!ftp.getFile(ConfigClass.FtpPath + mkt + "Base.zip", ConfigClass.LocalDBPath + "Temp//Base.zip", new Runnable()
						{
							public void run()
							{
								System.out.println("日终库已下载 " + ftp.getRetrieveDataLength() + " KB (" + ftp.getRetrieveDataSpeed() + " K/S),请等待...");
								//setLabelHint(lbl_message, "日终库已下载 " + ftp.getRetrieveDataLength() + " KB (" + ftp.getRetrieveDataSpeed() + " K/S),请等待...");
								//while (Display.getCurrent().readAndDispatch());
							}
						}))
						{
							ftp.close();
							ret = false;
							return ret;
						}

						if (!ftp.getFile(ConfigClass.FtpPath + mkt + "DayEndLog.txt", ConfigClass.LocalDBPath + "Temp//BaseDate.ini"))
						{
							ftp.close();
							ret = false;
							return ret;
						}

						// 关闭FTP连接
						ftp.close();

						// 解压数据库
						setLabelHint(lbl_message, Language.apply("正在解压缩数据库......"));
						Unzip zip = new Unzip();

						// 为true时需要创建derby数据库外层目录
						if (zip.needCreateDerbyDir(ConfigClass.LocalDBPath + "Temp//Base.zip"))
						{
							// 由于POSSERVER生成的Derby数据库压缩的Base.zip不含有Base目录，所以解压时要解压到Base目录中
							zip.unzipAnt(ConfigClass.LocalDBPath + "Temp//Base.zip", ConfigClass.LocalDBPath + "Temp//" + LoadSysInfo.getDefault().getBaseDBName(), 1);
						}
						else
						{
							zip.unzipAnt(ConfigClass.LocalDBPath + "Temp//Base.zip", ConfigClass.LocalDBPath + "Temp", 1);
						}

						// 检查文件大小
						PathFile.pathSize(ConfigClass.LocalDBPath + "Temp//" + LoadSysInfo.getDefault().getBaseDBName());
						if (PathFile.getPathLength() > 0)
						{
							break;
						}
					}
				}
				// 检查文件大小
				PathFile.pathSize(ConfigClass.LocalDBPath + "Temp//" + LoadSysInfo.getDefault().getBaseDBName());
				if (PathFile.getPathLength() <= 0)
				{
					MessageBox me = new MessageBox(Language.apply("新下载的本地数据库大小为0,您想继续使用上次的本地库吗?"), null, true);

					if (me.verify() != GlobalVar.Key1)
					{
						return false;
					}
					else
					{
						return true;
					}
				}

				// 准备对前一天的数据备份
				setLabelHint(lbl_message, Language.apply("正在进行本地数据库备份......"));
				if (!PathFile.fileExist(ConfigClass.LocalDBPath + "Bak"))
//				{
//					path = new File(ConfigClass.LocalDBPath + "Bak");
//					path.mkdirs();
//				}

				if (PathFile.fileExist(ConfigClass.LocalDBPath + "Bak//" + LoadSysInfo.getDefault().getBaseDBName()))
				{
					PathFile.deletePath(ConfigClass.LocalDBPath + "Bak//" + LoadSysInfo.getDefault().getBaseDBName());
				}

				if (PathFile.fileExist(ConfigClass.LocalDBPath + LoadSysInfo.getDefault().getBaseDBName()))
				{
					PathFile.copyPath(ConfigClass.LocalDBPath + LoadSysInfo.getDefault().getBaseDBName(), ConfigClass.LocalDBPath + "Bak//" + LoadSysInfo.getDefault().getBaseDBName());
				}

				// 删除老基础库
				setLabelHint(lbl_message, Language.apply("正在删除旧数据库......"));
				PathFile.deletePath(ConfigClass.LocalDBPath + LoadSysInfo.getDefault().getBaseDBName());

				// 拷贝新基础库
				setLabelHint(lbl_message, Language.apply("正在拷贝新数据库......"));
				PathFile.copyPath(ConfigClass.LocalDBPath + "Temp//" + LoadSysInfo.getDefault().getBaseDBName(), ConfigClass.LocalDBPath + LoadSysInfo.getDefault().getBaseDBName());

				// 设置本地数据日期
				setLabelHint(lbl_message, Language.apply("正在标记当天已下载数据库......"));
				if (PathFile.fileExist(ConfigClass.LocalDBPath + "Temp//BaseDate.ini"))
				{
					PathFile.copyPath(ConfigClass.LocalDBPath + "Temp//BaseDate.ini", ConfigClass.LocalDBPath + "BaseDate.ini");
				}

				// 标记当天已下载
				setDBDate();

				// 清空增量下载标准,以便重新更新整天数据
				UpdateBaseInfo.deleteUpdateInfoDate();

				return true;
			}
			catch (Exception ex)
			{
				PosLog.getLog("DownloadData").error(ex);
				ex.printStackTrace();

				ret = false;
				return ret;
			}
			finally
			{
				// 保留temp下次下载前删除
				// PathFile.deletePath(ConfigClass.LocalDBPath + "Temp");

				//
				if (!ret)
				{
					AccessDayDB.getDefault().writeWorkLog(Language.apply("开机时下载基础数据库失败"), StatusType.WORK_SENDERROR);

					new MessageBox(Language.apply("下载基础数据库失败,本地数据可能不是最新数据!\n\n本地脱网数据时间是 ") + readLocalDBDate());
				}
				else
				{
					if (showdateinfo)
					{
						ManipulateDateTime dt = new ManipulateDateTime();
						String dbdate = readLocalDBDate();
						if ((dbdate!=null &&!dbdate.equals("")) && dt.compareDate(ManipulateDateTime.getCurrentDate(), dbdate.substring(0, 10)) > 1)
						{
							AccessDayDB.getDefault().writeWorkLog(Language.apply("下载的基础数据库是过期数据," )+ dbdate, StatusType.WORK_SENDERROR);

							new MessageBox(Language.apply("下载的脱网基础数据库数据可能已经过期!\n\n今天是{0}但脱网数据时间为 ", new Object[]{ManipulateDateTime.getCurrentDateBySign()}) + dbdate.substring(0, 10));
//							new MessageBox("下载的脱网基础数据库数据可能已经过期!\n\n今天是 " + ManipulateDateTime.getCurrentDateBySign() + " 但脱网数据时间为 " + dbdate.substring(0, 10));
						}
					}
				}

				// 删除已开始下载的标记文件
				if (delflag)
				{
					File file = new File(ConfigClass.LocalDBPath + "baseflag");
					if (file.exists())
					{
						file.delete();
					}
				}
			}
		}
		finally
		{
			// 显示本地数据库版本
			showLocalDBDate();
			AccessDayDB.getDefault().writeWorkLog(Language.apply("POS脱网数据版本") + "[" + readLocalDBDate() + "]", StatusType.WORK_BASEDOWN);//(checkDBDate() ? "为最新":"不是最新") + 
		}
	}

	public static String readLocalDBDate()
	{
		BufferedReader br = null;
		String line = "";

		try
		{
			if (!PathFile.fileExist(ConfigClass.LocalDBPath + "BaseDate.ini")) { return line; }

			br = CommonMethod.readFile(ConfigClass.LocalDBPath + "BaseDate.ini");
			if (br == null)
				return line;

			while ((line = br.readLine()) != null)
			{
				if (line.length() <= 0)
				{
					continue;
				}
				break;
			}
			br.close();

			return line;
		}
		catch (Exception e)
		{
			PosLog.getLog("DownloadData").error(e);
			e.printStackTrace();
			return line;
		}
	}

	private static void showLocalDBDate()
	{
		GlobalInfo.background.setLocalDBDate(readLocalDBDate());
	}

	public static void deleteDBDate()
	{
		File f = new File(ConfigClass.LocalDBPath + "BaseDownload.ini");
		f.delete();

		// 改名BaseDateSet.ini,以便下次重新下载了全量库以后，再次恢复为增量下载模式
		if (PathFile.fileExist(ConfigClass.LocalDBPath + "BaseDateSet.ini"))
		{
			PathFile.copyPath(ConfigClass.LocalDBPath + "BaseDateSet.ini", ConfigClass.LocalDBPath + "BaseDateSet.bak");
			PathFile.deletePath(ConfigClass.LocalDBPath + "BaseDateSet.ini");
		}
	}

	private static boolean checkDBDate()
	{
		BufferedReader br = null;
		String line = null;

		try
		{
			if (!PathFile.fileExist(ConfigClass.LocalDBPath + "BaseDownload.ini")) { return false; }

			br = CommonMethod.readFile(ConfigClass.LocalDBPath + "BaseDownload.ini");
			if (br == null)
				return false;

			while ((line = br.readLine()) != null)
			{
				if (line.length() <= 0)
				{
					continue;
				}

				if (line.equals(ManipulateDateTime.getCurrentDate()))
				{
					return true;
				}
				else
					break;
			}

			br.close();

			return false;
		}
		catch (Exception e)
		{
			PosLog.getLog("DownloadData").error(e);
			return false;
		}
	}

	private static boolean setDBDate()
	{
		PrintWriter pw = null;

		pw = CommonMethod.writeFile(ConfigClass.LocalDBPath + "BaseDownload.ini");
		if (pw == null) { return false; }

		pw.print(ManipulateDateTime.getCurrentDate());
		pw.flush();
		pw.close();

		return true;
	}
}
