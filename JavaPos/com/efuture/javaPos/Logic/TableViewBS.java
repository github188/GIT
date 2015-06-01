package com.efuture.javaPos.Logic;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.UI.Design.MutiSelectForm;
import com.efuture.javaPos.UI.Design.TableViewForm;

public class TableViewBS 
{
	// 数据库类型
	public static class DbType
	{	
		public static String Local = "LOCAL";
		public static String Base = "BASE";
		public static String Day = "DAY";
	}
	
	public class TableVeiwStruct
	{
		public String Name = "";
		public String DB = ""; // Local,Base,Day:yyyymmdd
		public String TableName = "";
		public String SqlText = "";
	}

	public Vector vcTableView = new Vector();
	
	public Vector contents = new Vector();
	public String[] cols = null;
	public int[] widths = null;
	
	public TableViewBS()
	{
		init();
	}
	
	public boolean init()
	{
		try
		{
			// 读配置文件
			readCfg();
			
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	public boolean readCfg()
	{
		try
		{
			// 读配置文件
			Vector v = CommonMethod.readFileByVector(GlobalVar.ConfigPath+"/TableView.ini");
			
			if (v == null || v.size() <= 0)
			{
				// 如果没有配置文件,则直接读系统参数表
				TableVeiwStruct tvs = new TableVeiwStruct();
				tvs.Name = Language.apply("系统参数表");
				tvs.DB = DbType.Local;
				tvs.TableName = "SysPara";
				tvs.SqlText = "Select * from SysPara";
				
				vcTableView.add(tvs);
			}
			else
			{
				// 解析TableView.ini的配置信息
				TableVeiwStruct tvs = null;
				for (int i = 0;i < v.size();i++)
				{
					String[] str = (String[])v.get(i);
					
					if (str[0] == null || str[0].length() <= 0 || str[0].startsWith(";")) continue;
						
					if (str[0].startsWith("[") && str[0].endsWith("]") && str[0].length() > 2) 
					{
						tvs = new TableVeiwStruct();
						vcTableView.add(tvs);
						tvs.Name = str[0].substring(1,str[0].length() - 1);
						tvs.DB = DbType.Local;
						tvs.TableName = "";
						tvs.SqlText = "";
					}
					
					if (tvs != null)
					{
						if (str[0].equals("DB"))
						{
							tvs.DB = str[1]==null?"":str[1].toUpperCase();
						}
						else if (str[0].equals("TableName"))
						{
							tvs.TableName = str[1];
						}
						else if (str[0].equals("SqlText"))
						{
							tvs.SqlText = str[1];
						}
					}
				}
				
				// 判断如果未配置Sql语句,则通过表名自动生成一个Sql语句
				for(int i = 0;i < vcTableView.size();i++)
				{
					TableVeiwStruct tvs1 = (TableVeiwStruct)vcTableView.get(i);
					
					if (tvs1.SqlText.length() <= 0)
					{
						tvs1.SqlText = "select * from " + tvs1.TableName + "";
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
	}
	
	public void tableInfoView()
	{
		try
		{			
			// 显示已配置的功能
			String[] m_title = { Language.apply("描述"), Language.apply("数据库"), Language.apply("表名") };
			int[] m_width = { 120, 170, 250 };
			Vector m_content = new Vector();
			for (int i = 0;i < vcTableView.size();i++)
			{
				TableVeiwStruct tvs = (TableVeiwStruct)vcTableView.get(i);
				m_content.add(new String[]{tvs.Name,tvs.DB,tvs.TableName});
			}
			
			int index = 0;
			if (m_content.size() > 1) 
			{
				index = new MutiSelectForm().open(Language.apply("查询数据"), m_title, m_width, m_content, false);
			}
			
			// 如果有选择则执行配置的Sql语句,并显示出数据
			if (index >= 0 && index < vcTableView.size())
			{
				TableVeiwStruct tvs = (TableVeiwStruct)vcTableView.get(index);
				if (execSql(tvs))
				{
					new TableViewForm().open(tvs,cols,widths,contents);
					
					if (m_content.size() > 1) 
					{
						tableInfoView();
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
		
	public boolean execSql(TableVeiwStruct tvs)
	{	
		cols = null;
		widths = null;
		contents.clear();
		
		Sqldb sqldb = null;
		
		if (tvs.DB.equals("LOCAL"))
		{
			sqldb = GlobalInfo.localDB;
		}
		else if (tvs.DB.equals("BASE"))
		{
			sqldb = GlobalInfo.baseDB;
		}
		else if (tvs.DB.startsWith("DAY"))
		{
			// Day:yyyymmdd
			String[] strs = tvs.DB.split(":");
			
			if (strs.length > 1)
			{
				if (!PathFile.isPathExists(ConfigClass.LocalDBPath + "Invoice/" + strs[1].trim() + "/" + LoadSysInfo.getDefault().getDayDBName()))
				{
					new MessageBox(Language.apply("您要查找的本地数据库不存在[{0}]!", new Object[]{strs[1].trim()}), null, false);
					return false;
				}
				
				sqldb = LoadSysInfo.getDefault().loadDayDB(ManipulateDateTime.getConversionDate(strs[1].trim()));
			}
			else
			{
				sqldb = GlobalInfo.dayDB;
			}
		}

		if (sqldb == null) 
		{
			new MessageBox(Language.apply("[{0}]库名称不正确!", new Object[]{tvs.DB}));
			return false;
		}
		
		ResultSet rs = null;
		try
		{
			if (!tvs.SqlText.trim().toLowerCase().startsWith("select"))
			{
				if (new MessageBox(Language.apply("你将要执行的SQL不是SELECT语句\n可能会改变数据库的数据\n\n你确定要执行吗？"),null,true).verify() == GlobalVar.Key1)
				{
					sqldb.beginTrans();
					if (sqldb.executeSql(tvs.SqlText))
					{
						if (new MessageBox(sqldb.getAffectRow() + Language.apply(" 行数据发生改变\n\n你确定要提交执行结果吗？"),null,true).verify() == GlobalVar.Key1)
						{
							sqldb.commitTrans();
							new MessageBox(Language.apply("SQL执行结果提交完成"));
						}
						else 
						{
							sqldb.rollbackTrans();
							new MessageBox(Language.apply("SQL执行结果已被撤销"));
						}
					}
				}
				return false;	//不刷新表
			}
			else
			{
				rs = sqldb.selectData(tvs.SqlText);
	
				if (rs == null) 
				{ 
					return false;
				}
				
				ResultSetMetaData rsmd = rs.getMetaData();
				int colcon = rsmd.getColumnCount();
	
				// columns
				cols = new String[colcon];
				for (int i = 1;i <= colcon;i ++)
				{
					cols[i-1] = rsmd.getColumnName(i);
				}
					
					// width
				widths = new int[colcon];
				for (int i = 1;i <= colcon;i ++)
				{
					widths[i-1] = 200;
				}
				
				// rows
				while (rs.next())
				{
					String[] rows = new String[colcon];
					contents.add(rows);
					
	 				for (int i = 1;i <= colcon;i ++)
					{
	 					rows[i-1] = rs.getString(i);
					}
				}

				return true;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
			return false;
		}
		finally
		{
			if (sqldb != null) sqldb.resultSetClose();
		}
	}
}
