package com.efuture.javaPos.Logic;

import java.sql.ResultSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.efuture.javaPos.Global.GlobalInfo;

public class MessageQueryBS 
{
	private String datatime = null;
	private String syyh = null;
	
	public MessageQueryBS()
	{
		
	}
	
	public boolean init(Table tabBaseInfo,StyledText txtTitle,StyledText txtContent)
	{
		try
		{
			if(!this.getBaseInfo(tabBaseInfo)) return false;
			
			getMessage(datatime,syyh,txtTitle,txtContent);
			
			return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	private boolean getBaseInfo(Table tabBaseInfo)
	{
		ResultSet rs = null;
	
		try
		{
			if ((rs = GlobalInfo.localDB.selectData("select seqno,rqsj,syyh from NEWS order by seqno desc")) != null)
			{
				if(rs.next())
				{
					String[] baseinfo = {rs.getString("rqsj"),rs.getString("syyh")};
                	TableItem item = new TableItem(tabBaseInfo, SWT.NONE);
                	item.setText(baseinfo);
                	
                	datatime =  rs.getString("rqsj");
                	syyh = rs.getString("syyh");
				}
				else
				{
					return false;
				}
				
				while(rs.next())
				{
					String[] baseinfo = {rs.getString("rqsj"),rs.getString("syyh")};
                	TableItem item = new TableItem(tabBaseInfo, SWT.NONE);
                	item.setText(baseinfo);
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
			GlobalInfo.localDB.resultSetClose();
		}
	}
	
	public void getMessage(String rqsj,String syyh,StyledText txtTitle,StyledText txtContent)
	{
		ResultSet rs = null;
		try
		{
			if ((rs = GlobalInfo.localDB.selectData("select title,text from NEWS where rqsj = '"+ rqsj +"' and syyh = '"+ syyh + "'" )) != null)
			{
				if(rs.next())
				{
					txtTitle.setText(rs.getString("title"));
					txtContent.setText(rs.getString("text"));
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			GlobalInfo.localDB.resultSetClose();
		}
	}
}
