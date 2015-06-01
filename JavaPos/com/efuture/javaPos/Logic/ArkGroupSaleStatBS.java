package com.efuture.javaPos.Logic;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.PrintTemplate.ArkGroupBillMode;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PosTimeDef;
import com.efuture.javaPos.Struct.SaleGzSummaryDef;

public class ArkGroupSaleStatBS 
{
	private ArrayList arkGroupList = null;
	protected Sqldb sql = null;
	private String curQuerySql = null;
	private String strdate = "";
	
	public ArkGroupSaleStatBS()
	{
		
	}
	
	public boolean init(Table tabArkStatInfo,String date,int cmbsyyh,int cmbbc,Label LblSaleJe,Label LblSaleBS,Label LblThJe,Label LblThBS,Label LblZke)
	{
		try
		{
			if(!getArkGroupInfo(tabArkStatInfo,date,cmbsyyh,cmbbc,LblSaleJe,LblSaleBS,LblThJe,LblThBS,LblZke)) return false;
			
			return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	public boolean getArkGroupInfo(Table tabArkStatInfo,String date,int cmbsyyh,int cmbbc,Label LblSaleJe,Label LblSaleBS,Label LblThJe,Label LblThBS,Label LblZke)
	{
		ResultSet rs = null;
		
		try
		{
			tabArkStatInfo.removeAll();
			
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
			
			String sqlstr = "select '全部款员' as syyh,'#' as bc,gz,max(name) as name,sum(xsje) as xsje,sum(xsbs) as xsbs,sum(xszk) as xszk,sum(thje) as thje,sum(thbs) as thbs,sum(thzk) as thzk from SALEGZSUMMARY ";
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
			
			curQuerySql = sqlstr + " group by gz";
			
			// 更新柜组名称信息
			Vector vc = new Vector();
			if ((rs = sql.selectData(curQuerySql)) != null)
			{
				while (rs.next())
				{
					String strgz = rs.getString("gz");
					String strgzname = AccessLocalDB.getDefault().getGzName(strgz);
					if (strgzname.length() >0)
					{
						String[] str =  new String[]{strgz,strgzname};
						vc.add(str);
					}
				}
			}
			
			sql.resultSetClose();
			
			Iterator ir = vc.iterator();
			while(ir.hasNext())
			{
				String[] strgzgzname = (String[])ir.next();
				
				if (!sql.executeSql("Update SALEGZSUMMARY set name = '" + strgzgzname[1] + "' where gz = '" + strgzgzname[0] + "'"))
				{
					new MessageBox(Language.apply("更新柜组名称失败!"));
				}
			}
			
			if ((rs = sql.selectData(curQuerySql)) != null)
			{
				boolean havedate = false;
				double saleje = 0;
				int salebs = 0;
				double thje = 0;
				int thbs = 0;
				double zke = 0;
				while(rs.next())
				{
					String[] bpsiInfo = {rs.getString("gz")+" "+rs.getString("name"),ManipulatePrecision.doubleToString(rs.getDouble("xsje")),rs.getString("xsbs"),ManipulatePrecision.doubleToString(rs.getDouble("thje")),rs.getString("thbs"),ManipulatePrecision.doubleToString(ManipulatePrecision.sub(rs.getDouble("xszk"), rs.getDouble("thzk")))};
                	TableItem item = new TableItem(tabArkStatInfo, SWT.NONE);
                	item.setText(bpsiInfo);
                	
                	saleje += rs.getDouble("xsje");
                	salebs += Convert.toInt(rs.getString("xsbs"));
                	thje += rs.getDouble("thje");
                	thbs += Convert.toInt(rs.getString("thbs"));
                	zke += ManipulatePrecision.sub(rs.getDouble("xszk"), rs.getDouble("thzk"));
                	                	
                	havedate = true;
				}
				
				if (!havedate)
				{
					new MessageBox(Language.apply("当前数据库无数据!"), null, false);
					return false;
				}
				else
				{
					LblSaleJe.setText(ManipulatePrecision.doubleToString(saleje));
                	LblSaleBS.setText(String.valueOf(salebs));
                	LblThJe.setText(ManipulatePrecision.doubleToString(thje));
                	LblThBS.setText(String.valueOf(thbs));
                	LblZke.setText(ManipulatePrecision.doubleToString(zke));
				}
			}
			
			return true;
		}
		catch(Exception ex)
		{
			curQuerySql = "";
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
	
	//打印
	public void  printGzSale()
	{
		try
		{
			if (!createArkGroupSale()) return ;
			
			ProgressBox pb = new ProgressBox();
			pb.setText(Language.apply("正在打印柜组对帐单,请等待..."));
			
			printArkGroup();
			pb.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (arkGroupList != null)
			{
				arkGroupList.clear();
				arkGroupList = null;
			}
		}
	}
	
	 //打印
    private void printArkGroup()
    {
    	try
    	{
    		if (arkGroupList == null || arkGroupList.size() <= 0)
    		{
    			new MessageBox(Language.apply("未发现柜组对帐单对象,不能打印"));
    			return ; 
    		}
    		
    		ArkGroupBillMode.getDefault().setTemplateObject(arkGroupList);
    		ArkGroupBillMode.getDefault().printBill();
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    }
    
    public boolean createArkGroupSale()
	{
    	ResultSet rs = null;
    	
    	try
    	{
    		if (curQuerySql.equals(""))
    		{
    			new MessageBox(Language.apply("请先查询柜组对帐单数据!"), null, false);
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
				new MessageBox(Language.apply("未能获得柜组对帐单数据!"), null, false);
    			return false;
			}
    		
    		//
            boolean ret = false;
            arkGroupList = new ArrayList();
            while(rs.next())
            {
            	SaleGzSummaryDef sgsd = new SaleGzSummaryDef();
            	
            	if (!sql.getResultSetToObject(sgsd))
            	{
            		new MessageBox(Language.apply("生成柜组对帐单对象失败!"), null, false);
            		return false;
            	}
            	
            	arkGroupList.add(sgsd);
            	
            	ret = true;
            }
            
            if (!ret)
            {
            	new MessageBox(Language.apply("没有查询到柜组统计数据!"), null, false);
    			return false;
            }
            
    		return ret;
    	}
    	catch(Exception ex)
    	{
    		new MessageBox(Language.apply("创建柜组对帐单出现异常!"), null, false);
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
