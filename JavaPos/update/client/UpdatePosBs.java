package update.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.efuture.javaPos.Global.Language;

public class UpdatePosBs
{

	private Shell shell = null;

	private ProgressBar pbDownLoad = null;
	private ProgressBar pbUpdate = null;
	private Label lblDownLoad = null;
	private Label lblUpdate = null;

	private final int max = 100;
	private final int min = 0;

	private String CashRegisterCode = null;
	private String FtpIP = null;
	private int FtpPort = 21;
	private String FtpPath = "";
	private String FtpUser = "anonymous";
	private String FtpPwd = "";
	private int FtpDefaultTimeout = 30000;
	private int FtpDataTimeout = 60000;
	private String Ftppasv = "N";

	private String code = null;
	private String installpath = null;
	private String decompress = null;
	private String del = null;
	private String execute = null;
	private String isdeladv = null;
	private String datetime = null;
	private String name = null;
	private String ftppath = null;
	private String filename = null;
	private String executefilename = null;

	// private SimpleDateFormat sdf = null;
	private NodeList newlist = null;
	private NodeList oldlist = null;
	private ArrayList filelist = null;
	private Ftp ftp = null;

	public UpdatePosBs(UpdatePosForm upf)
	{
		// sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		shell = upf.getShell();
		pbDownLoad = upf.getPbDownLoad();
		pbUpdate = upf.getPbUpdate();
		lblDownLoad = upf.getLblDownLoad();
		lblUpdate = upf.getLblUpdate();

		// 设置最小值
		pbDownLoad.setMinimum(min);
		pbUpdate.setMinimum(min);

		// 设置最大值
		pbDownLoad.setMaximum(max);
		pbUpdate.setMaximum(max);

		init();
	}

	// 初始化
	private void init()
	{
		try
		{
			// N-不自动更新 A-自动静默 S-起动更新前提示 F-发现新版本提示
			if (GlobalVar.UpdateMode.equals("S"))
			{
				MessageBox msgbx = new MessageBox(Language.apply("是否查找程序最新版本?") + "\n\n");
				if (!msgbx.Choice)
				{
					close();
					return;
				}
			}

			if (!readUpdateConfigFile())
			{
				close();
				return;
			}

			if (!readPosIdConfigFile())
			{
				close();
				return;
			}

			if (!downLoadFtp())
			{
				close();
				return;
			}

			if (!updatePrc())
			{
				close();
				return;
			}

			close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	// 读取更新配置文件
	private boolean readUpdateConfigFile()
	{
		try
		{
			BufferedReader br = ReadXmlFile.readFile(GlobalVar.UpdateConfig);

			if (br == null)
			{
				new MessageBox(Language.apply("更新配置文件导入错误,程序终止"), false);

				return false;
			}

			String line = null;
			String[] sp = null;

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

				if (sp[0].trim().compareToIgnoreCase("FtpUpdateIP") == 0)
				{
					FtpIP = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("FtpUpdatePath") == 0)
				{
					FtpPath = sp[1].trim();
					System.out.println(FtpPath);
				}
				else if (sp[0].trim().compareToIgnoreCase("FtpUpdatePort") == 0)
				{
					FtpPort = Integer.parseInt(sp[1].trim());
				}
				else if (sp[0].trim().compareToIgnoreCase("FtpUpdateUser") == 0)
				{
					FtpUser = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("FtpUpdatePwd") == 0)
				{
					FtpPwd = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("FtpDefaultTimeout") == 0)
				{
					FtpDefaultTimeout = Integer.parseInt(sp[1].trim());
				}
				else if (sp[0].trim().compareToIgnoreCase("FtpDataTimeout") == 0)
				{
					FtpDataTimeout = Integer.parseInt(sp[1].trim());
				}
				else if (sp[0].trim().compareToIgnoreCase("Ftppasv") == 0)
				{
					Ftppasv = sp[1].trim();
				}
			}

			br.close();

			return true;
		}
		catch (Exception ex)
		{
			new MessageBox(Language.apply("更新配置读取异常,程序终止"), false);
			ex.printStackTrace();
			return false;
		}
	}

	// 读取收银机号配置文件
	private boolean readPosIdConfigFile()
	{
		try
		{
			BufferedReader br = ReadXmlFile.readFile(GlobalVar.PosIdConfig);

			if (br == null)
			{
				new MessageBox(Language.apply("收银机号配置文件导入错误,程序终止"), false);

				return false;
			}

			String line = null;
			String[] sp = null;

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
			}

			br.close();

			return true;
		}
		catch (Exception ex)
		{
			new MessageBox(Language.apply("收银机号配置读取异常,程序终止"), false);
			ex.printStackTrace();
			return false;
		}
	}

	// Ftp下载程序
	private boolean downLoadFtp()
	{
		boolean findFlag = true;
		int num = 0;

		try
		{
			if (FtpIP == null)
			{
				new MessageBox(Language.apply("无IP地址出现错误,程序终止"), false);
				return false;
			}

			ftp = new Ftp();

			if (!ftp.connect(FtpIP, FtpPort, FtpUser, FtpPwd, Ftppasv))
			{
				new MessageBox(Language.apply("服务器登录失败,程序终止"), false);
				return false;
			}

			// 设置超时
			ftp.setTimeout(FtpDefaultTimeout, FtpDataTimeout);

			if (ReadXmlFile.isExist(GlobalVar.Installpath + GlobalVar.temp))
			{
				ReadXmlFile.delDir(GlobalVar.Installpath + GlobalVar.temp);
			}

			if (!ReadXmlFile.createDir(GlobalVar.Installpath + GlobalVar.temp))
			{
				new MessageBox(Language.apply("创建Temp目录失败,程序终止"), false);
				return false;
			}

			if (!ftp.getFile(GlobalVar.ftpUpdateFile, GlobalVar.localUpdateFile, GlobalVar.downloadFile, null))
			{
				ReadXmlFile.delDir(GlobalVar.ConfigPath + GlobalVar.temp);

				return false;
			}

			newlist = ReadXmlFile.getReadData(GlobalVar.localUpdateFile);

			if (newlist == null)
			{
				new MessageBox(Language.apply("读取配置文件失败,程序终止"), false);
				return false;
			}

			filelist = new ArrayList();

			for (int i = 0; i < newlist.getLength(); i++)
			{
				ftppath = ((Element) newlist.item(i)).getAttribute("ftppath").trim();
				installpath = ((Element) newlist.item(i)).getAttribute("installpath").trim();
				decompress = ((Element) newlist.item(i)).getAttribute("decompress").trim();
				del = ((Element) newlist.item(i)).getAttribute("del").trim();
				execute = ((Element) newlist.item(i)).getAttribute("execute").trim();
				isdeladv = ((Element) newlist.item(i)).getAttribute("isdeladv").trim();
				filename = ((Element) newlist.item(i)).getAttribute("filename").trim();
				executefilename = ((Element) newlist.item(i)).getAttribute("executefilename").trim();
				datetime = ((Element) newlist.item(i)).getAttribute("datetime").trim();
				code = ((Element) newlist.item(i)).getAttribute("code").trim();
				name = ((Element) newlist.item(i)).getFirstChild().getNodeValue();

				String[] files = { ftppath, installpath, decompress, del, execute, isdeladv, filename, name, executefilename, code, datetime };
				System.out.println("aaaaaaaaa " + name);
				filelist.add(files);
			}

			if (filelist.size() <= 0)
				return false;

			// 判断是否需要更新如果需要更新则提示，用户有新程序需要更新是否进行更新
			// N-不自动更新 A-自动静默 S-起动更新前提示 F-发现新版本提示
			if (GlobalVar.UpdateMode.equals("F"))
			{
				if (!ReadXmlFile.isExist(GlobalVar.oldUpdateFile))
				{
					MessageBox msgbx = new MessageBox(Language.apply("更新服务器上存在新版本程序是否更新?") + "\n\n");
					if (!msgbx.Choice) { return true; }
				}
				else
				{
					for (int i = 0; i < filelist.size(); i++)
					{
						String[] files = (String[]) filelist.get(i);

						ftppath = files[0];
						code = files[9];
						datetime = files[10];
						name = files[7];
						findFlag = false;

						if (oldlist != null)
						{
							for (int j = 0; j < oldlist.getLength(); j++)
							{
								String tempdatetime = ((Element) oldlist.item(j)).getAttribute("datetime").trim();
								String tempftppath = ((Element) oldlist.item(j)).getAttribute("ftppath").trim();
								String tempname = ((Element) oldlist.item(j)).getFirstChild().getNodeValue().trim();

								if (!name.equals(tempname) || !ftppath.equals(tempftppath))
									continue;

								if (compareDateTime(tempdatetime, datetime))
								{
									findFlag = true;
									break;
								}
							}
						}

						if (findFlag)
						{
							MessageBox msgbx = new MessageBox(Language.apply("更新服务器上存在新版本程序是否更新?") + "\n\n");
							if (!msgbx.Choice) { return true; }

							break;
						}
					}
				}
			}

			// 更新程序
			if (ReadXmlFile.isExist(GlobalVar.oldUpdateFile))
			{
				oldlist = ReadXmlFile.getReadData(GlobalVar.oldUpdateFile);

				for (int i = 0; i < filelist.size(); i++)
				{
					String[] files = (String[]) filelist.get(i);

					ftppath = files[0];
					code = files[9];
					datetime = files[10];
					name = files[7];
					findFlag = true;
					System.out.println("bbbbbbbbb " + name);
					if (oldlist != null)
					{
						for (int j = 0; j < oldlist.getLength(); j++)
						{
							String tempdatetime = ((Element) oldlist.item(j)).getAttribute("datetime").trim();
							String tempftppath = ((Element) oldlist.item(j)).getAttribute("ftppath").trim();
							String tempname = ((Element) oldlist.item(j)).getFirstChild().getNodeValue().trim();

							if (!name.equals(tempname) || !ftppath.equals(tempftppath))
								continue;

							if (compareDateTime(tempdatetime, datetime))
							{
								findFlag = true;
								break;
							}
							else
							{
								findFlag = false;
								break;
							}
						}
					}

					num = num + 1;

					double value = (double) num / newlist.getLength() * max;

					pbDownLoad.setSelection((int) value);

					if (findFlag)
					{
						setLabelHint(lblDownLoad, Language.apply("正在下载") + ftppath + "/" + name + Language.apply("最新程序..........."));

						if (code.equals(""))
						{
							if (!ftp.getFile(name, GlobalVar.downloadFile + "/" + name, GlobalVar.downloadFile, ftppath))
							{
								new MessageBox(Language.apply("下载") + ftppath + "/" + name + Language.apply("\n文件失败程序终止"), false);
								ReadXmlFile.delDir(GlobalVar.downloadFile);
								return false;
							}
						}
						else
						{
							if (!isCashRegisterCode(code))
								continue;

							if (!ftp.getFile(name, GlobalVar.downloadFile + "/" + name, GlobalVar.downloadFile, ftppath))
							{
								new MessageBox(Language.apply("下载") + ftppath + "/" + name + Language.apply("\n文件失败程序终止"), false);
								ReadXmlFile.delDir(GlobalVar.downloadFile);
								return false;
							}
						}
					}

					findFlag = true;
				}
			}
			else
			{
				for (int i = 0; i < filelist.size(); i++)
				{
					String[] files = (String[]) filelist.get(i);

					code = files[9];
					name = files[7];
					ftppath = files[0];

					setLabelHint(lblDownLoad, Language.apply("正在下载") + ftppath + "/" + name + Language.apply("最新程序..........."));

					num = num + 1;

					double value = (double) num / newlist.getLength() * max;

					pbDownLoad.setSelection((int) value);

					if (code.equals(""))
					{
						if (!ftp.getFile(name, GlobalVar.downloadFile + "/" + name, GlobalVar.downloadFile, ftppath))
						{
							new MessageBox(Language.apply("下载") + ftppath + "/" + name + Language.apply("\n文件失败程序终止"), false);
							ReadXmlFile.delDir(GlobalVar.downloadFile);
							return false;
						}
					}
					else
					{
						if (!isCashRegisterCode(code))
							continue;

						if (!ftp.getFile(name, GlobalVar.downloadFile + "/" + name, GlobalVar.downloadFile, ftppath))
						{
							new MessageBox(Language.apply("下载") + ftppath + "/" + name + Language.apply("\n文件失败程序终止"), false);
							ReadXmlFile.delDir(GlobalVar.downloadFile);
							return false;
						}
					}
				}
			}

			setLabelHint(lblDownLoad, Language.apply("已全部下载最新程序..........."));

			return true;
		}
		catch (Exception ex)
		{
			new MessageBox(Language.apply("下载程序异常,程序终止\n\n") + ex.getMessage(), false);
			ReadXmlFile.delDir(GlobalVar.downloadFile);
			ex.printStackTrace();
			return false;
		}
		finally
		{
			if (ftp != null)
			{
				ftp.close();
			}
		}
	}

	// 更新程序
	private boolean updatePrc()
	{
		String currpath = null;

		try
		{
			int num = 0;

			for (int i = 0; i < filelist.size(); i++)
			{
				String[] files = (String[]) filelist.get(i);

				// String[] files = { ftppath, installpath, decompress, del,
				// execute,isdeladv, filename, name, executefilename, code,
				// datetime };
				ftppath = files[0];
				installpath = files[1];
				decompress = files[2];
				del = files[3];
				execute = files[4];
				isdeladv = files[5];
				filename = files[6];
				name = files[7];
				executefilename = files[8];

				num = num + 1;

				double value = (double) num / newlist.getLength() * max;

				pbUpdate.setSelection((int) value);

				String temppath = null;

				if (ftppath != null && !ftppath.trim().equals(""))
				{
					temppath = ReadXmlFile.SystemChangeChar(GlobalVar.downloadFile + "/" + ftppath + "/" + name);
				}
				else
				{
					temppath = ReadXmlFile.SystemChangeChar(GlobalVar.downloadFile + "/" + name);
				}

				if (!ReadXmlFile.isExist(temppath))
					continue;

				if (isdeladv.equals("yes"))
				{
					ReadXmlFile.delDir("c:\\video");
				}
				
				setLabelHint(lblUpdate, Language.apply("正在更新") + ftppath + "/" + name + Language.apply("最新程序..........."));

				if (installpath.trim().equals(""))
				{
					currpath = ReadXmlFile.SystemChangeChar(GlobalVar.Installpath);
				}
				else
				{
					currpath = ReadXmlFile.SystemChangeChar(installpath);

					if (!ReadXmlFile.isExist(currpath))
					{
						ReadXmlFile.createDir(currpath);
					}
				}

				String tempfilename = null;

				try
				{
					if (decompress.trim().equals("yes"))
					{
						int index = name.indexOf(".");

						if (index > -1)
						{
							tempfilename = name.substring(0, index);
						}

						if (tempfilename.trim().equals("Day") && del.trim().equals("yes") && isDay(currpath))
						{
							new MessageBox(Language.apply("更新Day库应在第一次开机的时候\n否则可能造成系统异常..."), false);
						}

						if (del.trim().equals("yes"))
						{
							if (filename.trim().equals(""))
							{
								ReadXmlFile.delDir(ReadXmlFile.SystemChangeChar(currpath + "/" + tempfilename));
							}
							else
							{
								ReadXmlFile.delDir(ReadXmlFile.SystemChangeChar(currpath + "/" + filename));
							}
						}

						if (filename.trim().equals(""))
						{
							ReadXmlFile.unzipPrc(ReadXmlFile.SystemChangeChar(temppath), ReadXmlFile.SystemChangeChar(currpath) + "/", 1);
						}
						else
						{
							ReadXmlFile.unzipPrc(ReadXmlFile.SystemChangeChar(temppath), ReadXmlFile.SystemChangeChar(currpath + "/" + filename), 1);
						}

						if (!executefilename.trim().equals(""))
						{
							String execfile = null;
							if (executefilename.indexOf('/') >= 0 || executefilename.indexOf('\\') >= 0)
							{
								execfile = executefilename;
							}
							else
							{
								execfile = ReadXmlFile.SystemChangeChar(currpath) + "/" + executefilename;
							}
							if (!executeFile(ReadXmlFile.SystemChangeChar(execfile)))
							{
								// 删除XML中未更新成功的ITEM
								ReadXmlFile.delXmlData(ReadXmlFile.SystemChangeChar(GlobalVar.localUpdateFile), ReadXmlFile.SystemChangeChar(ftppath), name);

								new MessageBox(Language.apply("执行当前") + "/" + name + Language.apply("文件失败"), false);
							}
						}

						continue;
					}
				}
				catch (Exception ex)
				{
					if (filename.trim().equals(""))
					{
						new MessageBox(Language.apply("解压") + ftppath + "/" + name + Language.apply("失败") + "\n\n" + ex.getMessage(), false);
					}
					else
					{
						new MessageBox(Language.apply("解压") + filename + Language.apply("失败") + "\n\n" + ex.getMessage(), false);
					}

					// 删除XML中未更新成功的ITEM
					ReadXmlFile.delXmlData(ReadXmlFile.SystemChangeChar(GlobalVar.localUpdateFile), ReadXmlFile.SystemChangeChar(ftppath), name);

					ex.printStackTrace();
					continue;
				}

				if (filename.equals(""))
				{
					filename = name;
				}

				if (del.trim().equals("yes"))
				{
					ReadXmlFile.delDir(ReadXmlFile.SystemChangeChar(currpath) + "/" + filename);
				}

				if (!ReadXmlFile.copyPrc(ReadXmlFile.SystemChangeChar(temppath), ReadXmlFile.SystemChangeChar(currpath + "/" + filename)))
				{
					// 删除XML中未更新成功的ITEM
					ReadXmlFile.delXmlData(ReadXmlFile.SystemChangeChar(GlobalVar.localUpdateFile), ReadXmlFile.SystemChangeChar(ftppath), name);

					new MessageBox(Language.apply("拷贝") + ftppath + "/" + name + Language.apply("失败"), false);

					continue;
				}

				if (execute.trim().equals("yes"))
				{
					if (!executeFile(ReadXmlFile.SystemChangeChar(ReadXmlFile.SystemChangeChar(currpath) + "/" + filename)))
					{
						// 删除XML中未更新成功的ITEM
						ReadXmlFile.delXmlData(ReadXmlFile.SystemChangeChar(GlobalVar.localUpdateFile), ReadXmlFile.SystemChangeChar(ftppath), name);

						new MessageBox(Language.apply("执行当前") + "/" + name + Language.apply("文件失败"), false);
					}
				}
			}

			setLabelHint(lblUpdate, Language.apply("更新最新程序完成............"));

			ReadXmlFile.copyPrc(ReadXmlFile.SystemChangeChar(GlobalVar.localUpdateFile), ReadXmlFile.SystemChangeChar(GlobalVar.oldUpdateFile));

			ReadXmlFile.delDir(ReadXmlFile.SystemChangeChar(GlobalVar.downloadFile));

			return true;
		}
		catch (Exception ex)
		{
			new MessageBox(Language.apply("更新程序异常,程序终止\n\n") + ex.getMessage(), false);
			ReadXmlFile.delDir(GlobalVar.downloadFile);
			ex.printStackTrace();
			return false;
		}
	}

	// 比较时间
	private boolean compareDateTime(String datetime, String datetime1)
	{
		try
		{
			// if (sdf.parse(datetime).compareTo(sdf.parse(datetime1)) < 0)
			if (!datetime.trim().equals(datetime1.trim()))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return true; // 如果比较时间异常就认为要下载
		}
	}

	// 得到当前时间
	private String getDate()
	{
		Calendar calendar = null;

		try
		{
			calendar = Calendar.getInstance();

			String year = String.valueOf(calendar.get(Calendar.YEAR));
			String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);

			if (month.length() < 2)
			{
				month = "0" + month;
			}

			String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

			if (day.length() < 2)
			{
				day = "0" + day;
			}

			return year + month + day;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	// 判断day是否存在
	public boolean isDay(String currpath)
	{
		try
		{
			if (ReadXmlFile.isExist(currpath + GlobalVar.invoice + "/" + getDate()))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	// 判断收银机号是否可下载
	public boolean isCashRegisterCode(String code)
	{
		try
		{
			String tempcode[] = code.trim().split(",");

			for (int j = 0; j < tempcode.length; j++)
			{
				if (CashRegisterCode.trim().equals(tempcode[j].trim())) { return true; }
			}

			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	// 开始执行当前文件
	public boolean executeFile(String filename)
	{
		try
		{
			// Process p = Runtime.getRuntime().exec("cmd.exe /c start /min " +
			// filename);
			Process p = Runtime.getRuntime().exec(filename);

			if (p != null)
			{
				//
				CmdExecStream errorStream = new CmdExecStream(p.getErrorStream());
				CmdExecStream outputStream = new CmdExecStream(p.getInputStream());
				errorStream.start();
				outputStream.start();

				// 等待
				p.waitFor();
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public void setLabelHint(Label status, String msg)
	{
		status.setText(msg);
		status.getDisplay().update();
	}

	private void close()
	{
		if (shell != null)
		{
			shell.close();
			shell.dispose();
			shell = null;
		}
	}

}

class CmdExecStream extends Thread
{
	InputStream is;

	CmdExecStream(InputStream is)
	{
		this.is = is;
	}

	public void run()
	{
		try
		{
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			while (br.readLine() != null);
			br.close();
		}
		catch (Exception ioe)
		{
		}
	}
}