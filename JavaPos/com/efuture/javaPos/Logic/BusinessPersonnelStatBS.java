package com.efuture.javaPos.Logic;

import java.sql.ResultSet;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.PrintTemplate.BusinessPerBillMode;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PosTimeDef;
import com.efuture.javaPos.Struct.SaleManSummaryDef;

public class BusinessPersonnelStatBS 
{
	ArrayList bussinessPerList = null;
	protected Sqldb sql = null;
	private String curQuerySql = null;
	private String strdate = "";
	
	public BusinessPersonnelStatBS()
	{
		
	}
	
	public boolean init(Table tabBusinessPersonStatInfo,String date,int cmbsyyh,int cmbbc)
	{
		try
		{
			if (!getBusinessPerStat(tabBusinessPersonStatInfo,date,cmbsyyh,cmbbc)) return false;
			
			return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	public boolean getBusinessPerStat(Table tabBusinessPersonStatInfo,String date,int cmbsyyh,int cmbbc)
	{
		ResultSet rs = null;
		
		try
		{
			tabBusinessPersonStatInfo.removeAll();
			
			if (!PathFile.isPathExists(ConfigClass.LocalDBPath + "Invoice/" + date.trim() + "/" + LoadSysInfo.getDefault().getDayDBName()))
			{
				new MessageBox(Language.apply("您输入的本地数据库不存在,请重新输入!"), null, false);
				curQuerySql = "";
				return false;
			}
			
			if (cmbsyyh >= 1 && GlobalInfo.posLogin.operrange != 'Y')
			{
				if (new MessageBox(Language.apply("你没有权限查询其他收银员的数据!\n你要进行授权查询吗?"),null,true).verify() == GlobalVar.Key1)
				{
			    	OperUserDef staff = DataService.getDefault().personGrant();
					if (staff == null) 
					{
						curQuerySql = "";
						return false;
					}
					
					if (staff.operrange != 'Y')
					{
						new MessageBox(Language.apply("该员工授权卡无法授权查询"));
						curQuerySql = "";
						return false;
					}
				}
				else
				{
					curQuerySql = "";
					return false;
				}
			}
			
			strdate = date;
			
			if (date.trim().equals(new ManipulateDateTime().getDateByEmpty()))
			{
				sql = GlobalInfo.dayDB;
			}
			else
			{
				sql = LoadSysInfo.getDefault().loadDayDB(ManipulateDateTime.getConversionDate(date.trim()));
			}
			
			String sqlstr = "select '全部款员' as syyh,'#' as bc,yyyh,max(name) as name,sum(xsje) as xsje,sum(xsbs) as xsbs,sum(xszk) as xszk,sum(thje) as thje,sum(thbs) as thbs,sum(thzk) as thzk from SALEMANSUMMARY ";
			String sqlwhere = " where ";
			if (cmbsyyh == 0)
			{
				sqlwhere += "syyh = '" + GlobalInfo.posLogin.gh + "' and ";
				sqlstr = sqlstr.replaceAll("全部款员", GlobalInfo.posLogin.gh);
			}
			if (cmbbc >= 1)
			{
				char bc = ((PosTimeDef)GlobalInfo.posTime.elementAt(cmbbc - 1)).code;
				sqlwhere += "bc = '" + bc + "' and ";
				sqlstr = sqlstr.replace('#', bc);
			}
			if (!sqlwhere.equals(" where "))
			{
				sqlstr += sqlwhere.substring(0,sqlwhere.length() - 5);
			}			
			curQuerySql = sqlstr + " group by yyyh";
			
			if ((rs = sql.selectData(curQuerySql)) != null)
			{
				boolean havedata = false;
				while(rs.next())
				{
					String[] bpsiInfo = {rs.getString("yyyh"),ManipulatePrecision.doubleToString(rs.getDouble("xsje")),rs.getString("xsbs"),ManipulatePrecision.doubleToString(rs.getDouble("xszk")),ManipulatePrecision.doubleToString(rs.getDouble("thje")),rs.getString("thbs"),ManipulatePrecision.doubleToString(rs.getDouble("thzk"))};
                	TableItem item = new TableItem(tabBusinessPersonStatInfo, SWT.NONE);
                	item.setText(bpsiInfo);
                	
                	havedata = true;
				}
				if (!havedata)
				{
					new MessageBox(Language.apply("当前数据库无数据!"), null, false);
					return false;
				}
			}
			
			return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			curQuerySql = "";
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
	
	//打印
	public void  printBusinessPerSale()
	{
		try
		{
			if (!createBusinessPerSale()) return ;
			
			ProgressBox pb = new ProgressBox();
			pb.setText(Language.apply("正在打印营业员报表,请等待..."));
			
			printBusinessPerSaleBill();
			pb.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (bussinessPerList != null)
			{
				bussinessPerList.clear();
				bussinessPerList = null;
			}
		}
	}
	
	private void printBusinessPerSaleBill()
	{
		try
		{
			if (bussinessPerList == null || bussinessPerList.size() <= 0)
    		{
    			new MessageBox(Language.apply("未发现营业员报表对象,不能打印"));
    			return ; 
    		}
			
			BusinessPerBillMode.getDefault().setTemplateObject(bussinessPerList);
		
			BusinessPerBillMode.getDefault().printBill();
		}
		catch(Exception ex)
		{ 
			ex.printStackTrace();
		}
	}
	
	private boolean createBusinessPerSale()
	{
		ResultSet rs = null;
		
		try
		{	
			if (curQuerySql.equals(""))
			{
				new MessageBox(Language.apply("请先查询营业员报表数据!"), null, false);
				return false;
			}
			
			if (strdate.trim().equals(new ManipulateDateTime().getDateByEmpty()))
			{
				sql = GlobalInfo.dayDB;
			}
			else
			{
				sql = LoadSysInfo.getDefault().loadDayDB(ManipulateDateTime.getConversionDate(strdate.trim()));
			}
			
			rs = sql.selectData(curQuerySql);
			if (rs == null)
			{
				new MessageBox(Language.apply("未能获得营业员报表数据!"), null, false);
    			return false;
			}
			
			//
			boolean ret = false;
            bussinessPerList = new ArrayList();            
            while(rs.next())
            {
            	SaleManSummaryDef smsd = new SaleManSummaryDef();
            	
            	if (!sql.getResultSetToObject(smsd))
            	{
            		new MessageBox(Language.apply("营业员报表对象失败!"), null, false);
            		return false;
            	}
            	
            	bussinessPerList.add(smsd);
            	
            	ret = true;
            }
            
            if (!ret)
            {
            	new MessageBox(Language.apply("没有查询到营业员统计数据!"), null, false);
    			return false;
            }
            
			return true;
		}
		catch(Exception ex)
		{
			new MessageBox(Language.apply("创建营业员报表出现异常!"), null, false);
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
