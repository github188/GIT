package com.efuture.javaPos.Logic;

import java.sql.ResultSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Global.StatusType;

public class QueryWorkLogBS 
{
	protected Sqldb sql = null;
	
	public QueryWorkLogBS()
	{
		
	}
	
	//查询工作日志
	public boolean initWorkLog(Table tabWorkLog,String date, int inputdjlb)
	{
		ResultSet rs = null;

		try
		{
			tabWorkLog.removeAll();
			
			if (!PathFile.isPathExists(ConfigClass.LocalDBPath + "Invoice/" + date.trim() + "/" + LoadSysInfo.getDefault().getDayDBName()))
			{
				new MessageBox(Language.apply("您输入的本地数据库不存在,请重新输入!"), null, false);
				return false;
			}
			
			if (date.trim().equals(new ManipulateDateTime().getDateByEmpty()))
			{
				sql = GlobalInfo.dayDB;
			}
			else
			{
				sql = LoadSysInfo.getDefault().loadDayDB(ManipulateDateTime.getConversionDate(date.trim()));
			}
			
			String sqlstr = "select netbz,seqno,rqsj,syyh,memo from WORKLOG ";
			
			String sqlwhere = " where ";
			
			if (inputdjlb >= 1)
			{
				switch(inputdjlb)
				{
					case 1:
						sqlwhere += "(code = '" + StatusType.WORK_LOGIN + "' or ";
						sqlwhere += "code = '" + StatusType.WORK_RELOGIN + "' or ";
						sqlwhere += "code = '" + StatusType.WORK_LEAVER + "' or ";
						sqlwhere += "code = '" + StatusType.WORK_COMEBACK + "' or ";
						sqlwhere += "code = '" + StatusType.WORK_BOOT + "' or ";
						sqlwhere += "code = '" + StatusType.WORK_SHUTDOWN + "') and ";
						break;
					case 2:
						sqlwhere += "code = '" + StatusType.WORK_SENDERROR + "' and ";
						break;
				}
			}
			
			if (!sqlwhere.equals(" where "))
			{
				sqlstr += sqlwhere.substring(0,sqlwhere.length() - 5);
			}
			
			if ((rs = sql.selectData(sqlstr + "order by seqno desc")) != null)
			{
				boolean ret = false;
				while(rs.next())
				{
					String rqsj[] = rs.getString("rqsj").split(" ");
					
					String[] workinfo = { (rs.getString("netbz").equals("Y")?"↑":"  ") + String.valueOf(rs.getLong("seqno")),rqsj[1], rs.getString("syyh"),rs.getString("memo") };
                	TableItem item = new TableItem(tabWorkLog, SWT.NONE);
                	item.setText(workinfo);
                	
                	ret = true;
				}
				if(!ret)
				{
					new MessageBox(Language.apply("当前数据库无数据!"), null, false);
					return false;
				}
				
				return true;
			}
			else
			{
				return false;
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			if (sql != GlobalInfo.dayDB)
	        {
                if (sql != null)
                {
                    sql.Close();
                    sql = null;
                }
	        }
			else
			{
				if (sql != null)
				{
					sql.resultSetClose();
					sql = null;
				}
			}
		}
	}
}
