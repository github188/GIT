package custom.localize.Wdgc;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;

import org.eclipse.swt.widgets.Label;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ExpressionDeal;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Device.RdPlugins;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.StatusType;

import custom.localize.Bcrm.Bcrm_LoadSysInfo;

public class Wdgc_LoadSysInfo extends Bcrm_LoadSysInfo
{
	public boolean checkServiceDate()
	{

		String key = ManipulatePrecision.getRegisterCodeKey(ConfigClass.CDKey);
		String strdate = "";
		ManipulateDateTime dt = new ManipulateDateTime();
		String curdate = dt.getDateBySign();

		// 兼容已上线的项目没有定义有效期,自动生成90天有效期文件
		if (GlobalInfo.sysPara.validservicedate == null || GlobalInfo.sysPara.validservicedate.trim().length() <= 0)
		{
			PrintWriter pw = null;
			BufferedReader br = null;
			try
			{
				String name = ConfigClass.LocalDBPath + "/ServiceDate.dat";
				File indexFile = new File(name);
				if (!indexFile.exists())
				{
					strdate = ManipulatePrecision.EncodeString(dt.skipDate(curdate, 90).replace('/', '-') + ",15", key);
					pw = CommonMethod.writeFile(name);
					pw.println(strdate);
					pw.flush();
					pw.close();
					pw = null;
				}

				// 读取有效期
				br = CommonMethod.readFile(name);
				String line = null;
				while ((line = br.readLine()) != null)
				{
					if (line.length() <= 0)
					{
						continue;
					}
					else
					{
						GlobalInfo.sysPara.validservicedate = line.trim();
						break;
					}
				}
				br.close();
				br = null;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				new MessageBox("系统有效期数据不正确,不能进入系统!");
				return false;
			}
			finally
			{
				try
				{
					if (pw != null) pw.close();
					if (br != null) br.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		// 解密有效期
		if (!GlobalInfo.ModuleType.equals("") && GlobalInfo.ModuleType.equalsIgnoreCase("wdgc"))
		{
			key = GlobalInfo.ModuleType;
		}
		strdate = ManipulatePrecision.DecodeString(GlobalInfo.sysPara.validservicedate, key);
		String[] s = strdate.split(",");
		if (s.length < 2 || !ManipulateDateTime.checkDate(s[0]) || Convert.toInt(s[1]) <= 0)
		{
			new MessageBox("系统有效期格式不正确,不能进入系统!");
			return false;
		}
		if (dt.compareDate(curdate, s[0]) > 0)
		{
			new MessageBox("本系统有效期为 " + s[0] + "\n目前已超过使用有效期,不能进入系统!\n\n请马上联系富基公司延长使用有效期!");
			return false;
		}
		if (s.length > 2 && !checkRegisterCount(Convert.toInt(s[2]))) // 检查款机注册数量
		{ return false; }
		long n = dt.compareDate(curdate, dt.skipDate(s[0], Convert.toInt(s[1]) * -1));
		if (n >= 0)
		{
			new MessageBox("本系统还有 " + (Convert.toInt(s[1]) - n) + " 天就过期了\n\n请尽快联系富基公司延长使用有效期!");
		}

		return true;

	}

	public boolean loadLocalDB(Label lbl_message, int type)
	{
		Sqldb sql = null;
		ManipulateDateTime mdt = null;

		try
		{
			// Base
			if (type == 0 || type == 1)
			{
				sql = new Sqldb(getJdbcDriverName(), getJdbcConnurlName() + ConfigClass.LocalDBPath + getBaseDBName());

				// 执行一次SQL检查数据库是否能正常访问,若不能访问则关闭数据库
				if (sql.isOpen() && sql.selectOneData("select count(*) from goods where 1=2") == null)
				{
					sql.Close();
				}

				if (!sql.isOpen())
				{
					AccessDayDB.getDefault().writeWorkLog("开机时连接BASE数据库失败", StatusType.WORK_SENDERROR);

					MessageBox me = new MessageBox("连接最新基础数据库失败,是否恢复上次数据库?\n\n任意键-是 / 退出键-否", null, false);

					if (me.verify() != GlobalVar.Exit)
					{
						setLabelHint(lbl_message, "正在恢复上次数据库,请等待......");

						PathFile.deletePath(ConfigClass.LocalDBPath + getBaseDBName());
						if (PathFile.fileExist(ConfigClass.LocalDBPath + "Bak//" + getBaseDBName()))
						{
							PathFile.copyPath(ConfigClass.LocalDBPath + "Bak//" + getBaseDBName(), ConfigClass.LocalDBPath + getBaseDBName());
						}
						else
						{
							checkLocalDB(lbl_message);
						}

						setLabelHint(lbl_message, "正在重新连接本地数据库,请等待......");

						sql.startCreate(getJdbcDriverName(), getJdbcConnurlName() + ConfigClass.LocalDBPath + getBaseDBName());

						// 执行一次SQL检查数据库是否能正常访问,若不能访问则关闭数据库
						if (sql.isOpen() && sql.selectOneData("select count(*) from goods where 1=2") == null)
						{
							sql.Close();
						}

						if (!sql.isOpen())
						{
							new MessageBox("开机恢复BASE数据库失败,开始恢复初始库......", null, false);

							PathFile.deletePath(ConfigClass.LocalDBPath + getBaseDBName());
							PathFile.deletePath(ConfigClass.LocalDBPath + "Bak//" + getBaseDBName());
							checkLocalDB(lbl_message);
							setDBDate();
							AccessDayDB.getDefault().writeWorkLog("开机恢复BASE数据库失败，已恢复初始库", StatusType.WORK_SENDERROR);
							new MessageBox("恢复初始BASE数据库成功！", null, false);
							GlobalInfo.baseDB = sql;
							//	return false;
						}
						else
						{
							AccessDayDB.getDefault().writeWorkLog("开机恢复BASE数据库成功", StatusType.WORK_SENDERROR);

							GlobalInfo.baseDB = sql;
						}
					}
					else
					{
						return false;
					}
				}
				else
				{
					GlobalInfo.baseDB = sql;
				}
			}

			// Local
			if (type == 0 || type == 2)
			{
				sql = new Sqldb(getJdbcDriverName(), getJdbcConnurlName() + ConfigClass.LocalDBPath + getLocalDBName());

				// 执行一次SQL检查数据库是否能正常访问,若不能访问则关闭数据库
				if (sql.isOpen() && sql.selectOneData("select count(*) from syjmain where 1=2") == null)
				{
					sql.Close();
				}

				if (!sql.isOpen())
				{
					AccessDayDB.getDefault().writeWorkLog("开机时连接LOCAL数据库失败", StatusType.WORK_SENDERROR);

					MessageBox me = new MessageBox("连接最新本地数据库失败,是否恢复上次数据库?\n\n任意键-是 / 退出键-否", null, false);

					if (me.verify() != GlobalVar.Exit)
					{
						setLabelHint(lbl_message, "正在恢复上次数据库,请等待......");

						PathFile.deletePath(ConfigClass.LocalDBPath + getLocalDBName());
						if (PathFile.fileExist(ConfigClass.LocalDBPath + "Bak//" + getLocalDBName()))
						{
							PathFile.copyPath(ConfigClass.LocalDBPath + "Bak//" + getLocalDBName(), ConfigClass.LocalDBPath + getLocalDBName());
						}
						else
						{
							checkLocalDB(lbl_message);
						}

						setLabelHint(lbl_message, "正在重新连接本地数据库,请等待......");

						sql.startCreate(getJdbcDriverName(), getJdbcConnurlName() + ConfigClass.LocalDBPath + getLocalDBName());

						// 执行一次SQL检查数据库是否能正常访问,若不能访问则关闭数据库
						if (sql.isOpen() && sql.selectOneData("select count(*) from syjmain where 1=2") == null)
						{
							sql.Close();
						}

						if (!sql.isOpen())
						{
							new MessageBox("开机恢复LOCAL数据库失败,开始恢复初始库...", null, false);
							PathFile.deletePath(ConfigClass.LocalDBPath + getLocalDBName());
							PathFile.deletePath(ConfigClass.LocalDBPath + "Bak//" + getLocalDBName());
							checkLocalDB(lbl_message);
							AccessDayDB.getDefault().writeWorkLog("开机恢复LOCAL数据库失败,已恢复初始库", StatusType.WORK_SENDERROR);

							new MessageBox("恢复LOCAL数据库连接成功,请重新启动系统...", null, false);
							return false;
						}
						else
						{
							AccessDayDB.getDefault().writeWorkLog("开机恢复LOCAL数据库成功", StatusType.WORK_SENDERROR);

							GlobalInfo.localDB = sql;
						}
					}
					else
					{
						return false;
					}
				}
				else
				{
					GlobalInfo.localDB = sql;

					// 备份
					PathFile.deletePath(ConfigClass.LocalDBPath + "Bak//" + getLocalDBName());
					PathFile.copyPath(ConfigClass.LocalDBPath + getLocalDBName(), ConfigClass.LocalDBPath + "Bak//" + getLocalDBName());
				}
			}

			// Day,设置记账日期
			if (type == 0 || type == 3)
			{
				// 记账日期
				mdt = new ManipulateDateTime();
				GlobalInfo.balanceDate = mdt.getDateBySlash();

				// 判断通宵营业时间,确定当日本地库 
				String newdate = getOverNight(true);
				if (newdate != null && !newdate.equals("")) GlobalInfo.balanceDate = newdate;

				// 连接每日库
				String date = ExpressionDeal.replace(GlobalInfo.balanceDate, "/", "");
				sql = new Sqldb(getJdbcDriverName(), getJdbcConnurlName() + ConfigClass.LocalDBPath + "Invoice//" + date + "//" + getDayDBName());

				// 执行一次SQL检查数据库是否能正常访问,若不能访问则关闭数据库
				if (sql.isOpen() && sql.selectOneData("select count(*) from salehead where 1=2") == null)
				{
					sql.Close();
				}

				if (!sql.isOpen())
				{
					MessageBox me = new MessageBox("连接每日数据库失败，是否恢复上次数据库?\n\n任意键-是 / 退出键-否", null, false);

					if (me.verify() != GlobalVar.Exit)
					{
						setLabelHint(lbl_message, "正在恢复上次数据库,请等待......");

						// 先备份当前损坏的DAY数据库
						String errname = mdt.getTimeByEmpty() + "_Day";
						PathFile.deletePath(ConfigClass.LocalDBPath + "Invoice//" + date + "//Err//" + errname);
						PathFile.copyPath(ConfigClass.LocalDBPath + "Invoice//" + date + "//" + getDayDBName(), ConfigClass.LocalDBPath + "Invoice//"
								+ date + "//Err//" + errname);

						// 再恢复上次正常的DAY数据库
						PathFile.deletePath(ConfigClass.LocalDBPath + "Invoice//" + date + "//" + getDayDBName());
						if (PathFile.fileExist(ConfigClass.LocalDBPath + "Invoice//" + date + "//Bak//" + getDayDBName()))
						{
							PathFile.copyPath(ConfigClass.LocalDBPath + "Invoice//" + date + "//Bak//" + getDayDBName(), ConfigClass.LocalDBPath
									+ "Invoice//" + date + "//" + getDayDBName());
						}
						else
						{
							PathFile.copyPath(ConfigClass.LocalDBPath + getDayDBName(), ConfigClass.LocalDBPath + "Invoice//" + date + "//"
									+ getDayDBName());
						}

						setLabelHint(lbl_message, "正在重新连接每日数据库,请等待......");

						sql.startCreate(getJdbcDriverName(), getJdbcConnurlName() + ConfigClass.LocalDBPath + "Invoice//" + date + "//"
								+ getDayDBName());

						// 执行一次SQL检查数据库是否能正常访问,若不能访问则关闭数据库
						if (sql.isOpen() && sql.selectOneData("select count(*) from salehead where 1=2") == null)
						{
							sql.Close();
						}

						if (!sql.isOpen())
						{
							new MessageBox("开机恢复DAY数据库失败,开始恢复初始库...", null, false);

							PathFile.deletePath(ConfigClass.LocalDBPath + "Invoice//" + date + "//" + getDayDBName());
							PathFile.deletePath(ConfigClass.LocalDBPath + "Invoice//" + date + "//Bak//" + getDayDBName());
							PathFile.copyPath(ConfigClass.LocalDBPath + getDayDBName(), ConfigClass.LocalDBPath + "Invoice//" + date + "//"
									+ getDayDBName());
							AccessDayDB.getDefault().writeWorkLog("开机恢复DAY数据库失败,已恢复初始库", StatusType.WORK_SENDERROR);

							new MessageBox("恢复DAY数据库连接成功,请重新启动系统...", null, false);

							return false;
						}
						else
						{
							AccessDayDB.getDefault().writeWorkLog("开机恢复DAY数据库成功", StatusType.WORK_SENDERROR);

							GlobalInfo.dayDB = sql;

							// 导入未连接数据库之前的日志到数据库
							AccessDayDB.getDefault().writeWorkLogByHistory();

							// 标记需要恢复交易数据
							needimportsale = true;

							new MessageBox("恢复每日数据库成功\n\n请在登录以后进行销售导入,避免丢失销售数据!");
						}
					}
					else
					{
						return false;
					}

					new MessageBox("连接day数据库完成", null, false);
				}
				else
				{
					GlobalInfo.dayDB = sql;

					// 备份
					PathFile.deletePath(ConfigClass.LocalDBPath + "Invoice//" + date + "//Bak//" + getDayDBName());
					PathFile.copyPath(ConfigClass.LocalDBPath + "Invoice//" + date + "//" + getDayDBName(), ConfigClass.LocalDBPath + "Invoice//"
							+ date + "//Bak//" + getDayDBName());

					// 导入未连接数据库之前的日志到数据库
					AccessDayDB.getDefault().writeWorkLogByHistory();
				}
			}

			// 创建商品查询预编译SQL对象,加快本地查询商品速度
			if (type == 0 || type == 1) AccessBaseDB.getDefault().createPreparedSql();
		}
		catch (Exception er)
		{
			er.printStackTrace();
			new MessageBox("连接本地数据库出现异常\n\n" + er.getMessage());

			return false;
		}
		finally
		{
			mdt = null;
			sql = null;
		}

		return true;
	}

	private static boolean setDBDate()
	{
		PrintWriter pw = null;

		pw = CommonMethod.writeFile(ConfigClass.LocalDBPath + "BaseDownload.ini");
		if (pw == null) { return false; }

		pw.print("2011/07/05");
		pw.flush();
		pw.close();

		PrintWriter pw1 = CommonMethod.writeFileUTF(ConfigClass.LocalDBPath + "BaseDate.ini");
		pw1.println("2011-07-05 10:17:58");
		pw1.close();

		return true;
	}

	//自动签到添加在这里（万达要求每次启动时自动签到）
	public void checkDeviceMessage()
	{
		super.checkDeviceMessage();
		ProgressBox pb = null;
		try
		{
			pb = new ProgressBox();
			pb.setText("正在签到，请等待。。。。");
			String check = "";
			if (RdPlugins.getDefault().getPlugins1().exec(10, GlobalInfo.syjStatus.syjh + "," + GlobalInfo.posLogin.gh)) check = (String) RdPlugins.getDefault().getPlugins1().getObject();

			if (check == null || check.length() < 0 )
			{
				new MessageBox("Of_CheckIn()接口调用失败!\n请手动签到");
				return;
			}
			else if (!check.substring(0, 2).equals("00"))
			{
				new MessageBox("Of_CheckIn()接口调用失败!\n请手动签到\n" + check.replaceAll("\\s+", " ").trim());
				return;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			new MessageBox("自动签到出现异常\n\n" + e.getMessage());
		}
		finally{
			if (pb != null)
			{
				pb.close();
				pb = null;
			}
		}
	}
}
