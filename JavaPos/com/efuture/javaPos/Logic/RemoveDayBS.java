package com.efuture.javaPos.Logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.sql.ResultSet;

import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;

public class RemoveDayBS
{
	public RemoveDayBS()
	{

	}

	public void init(Text txtDirName)
	{
		txtDirName.setText(String.valueOf(Integer.parseInt(new ManipulateDateTime().getDateByEmpty()) - 1));
	}

	public boolean execRemove(Text txtDirName)
	{
		MessageBox me = null;
		ProgressBox pb = null;

		String date = null;

		try
		{
			if (txtDirName.getText() == null || txtDirName.getText().trim().equals(""))
			{
				new MessageBox(Language.apply("数据库名不能为空"), null, false);
				return false;
			}

			ManipulateDateTime dt = new ManipulateDateTime();
			date = dt.getDateByEmpty();

			if (Integer.parseInt(txtDirName.getText().trim()) > Integer.parseInt(date))
			{
				new MessageBox(Language.apply("您输入的数据库名不合法,超出当天日期范围"), null, false);
				return false;
			}

			//
			me = new MessageBox(Language.apply("您确定要删除包括输入日期之前所有的Day数据库吗?"), null, true);
			if (me.verify() != GlobalVar.Key1) { return false; }

			if (Integer.parseInt(txtDirName.getText().trim()) == Integer.parseInt(date))
			{
				// 检查当日数据
				if (isDayData())
				{
					me = new MessageBox(Language.apply("当天数据库中存在小票,您真的要删除吗?"), null, true);
					if (me.verify() != GlobalVar.Key1) { return false; }
				}
			}

			//
			pb = new ProgressBox();
			pb.setText(Language.apply("正在清除本地销售数据,请等待..."));
			if (Integer.parseInt(txtDirName.getText().trim()) == Integer.parseInt(date))
			{
				// 删除当日数据
				if (!clearDayData())
				{
					pb.close();
					pb = null;
					new MessageBox(Language.apply("本地清除数据失败"), null, false);
					return false;
				}

				// 日期向前一天
				date = dt.skipDate(dt.getDateBySlash(), -1).replaceAll("/", "");
			}
			else
			{
				date = txtDirName.getText().trim();
			}

			// 删除包括本日期之前的历史数据
			if (!remvoeDataBase(date))
			{
				pb.close();
				pb = null;
				new MessageBox(Language.apply("本地清除数据失败"), null, false);
				return false;
			}

			//
			pb.close();
			pb = null;
			new MessageBox(Language.apply("本地销售已全部清除"), null, false);
			return true;
		}
		catch (Exception ex)
		{
			pb.close();
			pb = null;
			new MessageBox(Language.apply("本地清除数据出现异常:") + ex.getMessage(), null, false);
			ex.printStackTrace();
			return false;
		}
	}

	private boolean remvoeDataBase(String date)
	{
		boolean ret = remvoeDataBasePath(date);

		// 记录删除标志,便于下次开机时删除被占用无法删除的文件
		createRemovePathFlag(date);

		return ret;
	}

	public static boolean remvoeDataBasePath(String date)
	{
		String dirname[] = null;

		try
		{
			dirname = PathFile.getAllDirName(ConfigClass.LocalDBPath + "Invoice");

			for (int i = 0; i < dirname.length; i++)
			{
				if (dirname[i].trim().length() != date.trim().length() || dirname[i].trim().compareTo(date.trim()) > 0) continue;

				if (!PathFile.fileExist(ConfigClass.LocalDBPath + "Invoice/" + dirname[i].trim())) continue;

				PathFile.deletePath(ConfigClass.LocalDBPath + "Invoice/" + dirname[i].trim());
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public static boolean createRemovePathFlag(String date)
	{
		PrintWriter pw = CommonMethod.writeFile(ConfigClass.LocalDBPath + "Invoice/AutoDelete.ini");
		if (pw != null)
		{
			pw.print(date);
			pw.flush();
			pw.close();
		}

		return true;
	}

	public static boolean autoRemoveDataBase()
	{
		String file = ConfigClass.LocalDBPath + "Invoice/AutoDelete.ini";

		if (!PathFile.fileExist(file)) return false;

		BufferedReader br = null;
		String date = null;
		String line = null;

		// 读取标记文件
		try
		{
			br = CommonMethod.readFile(file);
			if (br == null) return false;

			while ((line = br.readLine()) != null)
			{
				if (line.length() <= 0)
				{
					continue;
				}

				date = line;
				break;
			}

			br.close();

			//
			new File(file).delete();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}

		boolean flag = false;
		
		if ("posinit".equalsIgnoreCase(date))
		{
			if (new MessageBox(Language.apply("收银机被命令进行初始化操作，删除所有历史数据。\n是否执行此项操作？"), null, true).verify() == GlobalVar.Key1)
			{
				if (new MessageBox(Language.apply("再次确认是否删除所有保存在本机中的历史数据？\n\n2-是 / 任意键-否"), null, false).verify() == GlobalVar.Key2)
				{
					flag = PathFile.delPathFile("C:\\JavaPOS\\javaPos.Logs", "");
					flag = PathFile.delPathFile(ConfigClass.LocalDBPath, "Base.zip,Day.zip,Local.zip");

					if (flag) new MessageBox(Language.apply("POS初始化成功！\r\n本地数据清理完毕。"));
				}
			}

			return flag;
		}
		else
		{
			if (new MessageBox(Language.apply("收银机被命令删除包括{0}之前的历史数据!\n是否执行此项操作？", new Object[]{date}), null, true).verify() == GlobalVar.Key1)
			{
				// 删除文件
				flag = remvoeDataBasePath(date);
				if (flag) new MessageBox(Language.apply("成功删除包括{0}之前的历史数据", new Object[]{date}));
			}

			return flag;
		}
	}

	//判断小票表中是否有数据
	private boolean isDayData()
	{
		Object value = null;

		try
		{
			value = GlobalInfo.dayDB.selectOneData("select count(*) from SALEHEAD");

			if (value == null) return false;

			if (Integer.parseInt(String.valueOf(value).trim()) < 1) return false;

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	//清除当前数据库信息
	private boolean clearDayData()
	{
		ResultSet rs = null;

		try
		{

			if (ConfigClass.LocalDBType.equalsIgnoreCase("SQLite"))
			{
				GlobalInfo.dayDB.executeSql("delete from SALEHEAD");
				GlobalInfo.dayDB.executeSql("delete from SALEGOODS");
				GlobalInfo.dayDB.executeSql("delete from SALEPAY");
				GlobalInfo.dayDB.executeSql("delete from SALESUMMARY");
				GlobalInfo.dayDB.executeSql("delete from SALEMANSUMMARY");
				GlobalInfo.dayDB.executeSql("delete from SALEPAYSUMMARY");
				GlobalInfo.dayDB.executeSql("delete from SALEGZSUMMARY");
				GlobalInfo.dayDB.executeSql("delete from PAYINHEAD");
				GlobalInfo.dayDB.executeSql("delete from PAYINDETAIL");
				GlobalInfo.dayDB.executeSql("delete from WORKLOG");
				GlobalInfo.dayDB.executeSql("delete from BANKLOG");
				GlobalInfo.dayDB.executeSql("delete from MEMOINFO");
			}
			else
			{
				rs = GlobalInfo.dayDB.selectData("select TABLENAME from SYS.SYSTABLES where TABLETYPE = 'T'");

				if (rs != null)
				{
					while (rs.next())
					{
						GlobalInfo.dayDB.setSql("delete from " + rs.getString(1));
						GlobalInfo.dayDB.executeSql();
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
			GlobalInfo.dayDB.resultSetClose();
		}
	}

}
