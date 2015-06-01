package custom.localize.Jnyz;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Logic.SyySaleStatBS;
import com.efuture.javaPos.PrintTemplate.SyySaleBillMode;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.SalePaySummaryDef;
import com.efuture.javaPos.Struct.SaleSummaryDef;


public class Jnyz_SyySaleStatBS extends SyySaleStatBS {
	
	private String bcValue = null;
	private String syyhValue = null;
	private SaleSummaryDef ssd = null;
	private ArrayList payList = null;
	
	private double tj;//打印劵退劵金额
	

	public boolean init(Table tabBaseInfo,Label lblSaleAmount,Label lblSaleMoney,Label lblReturnGoodsAmount,Label lblReturnGoodsMoney,Label lblRedCancelAmount,Label lblRedCancelMoney,Label lblCancelAmount,Label lblCancelMoney, Label lblSpoilageMoney,Label lblGiveChangeMoney,Table tabPayInfo,Label lblShouldInceptMoney,Label lblFactInceptMoney,String date)
	{
		try
		{
			tabBaseInfo.removeAll();
			tabPayInfo.removeAll();
			clear(lblSaleAmount,lblSaleMoney,lblReturnGoodsAmount,lblReturnGoodsMoney,lblRedCancelAmount,lblRedCancelMoney,lblCancelAmount,lblCancelMoney,lblSpoilageMoney,lblGiveChangeMoney,lblShouldInceptMoney,lblFactInceptMoney);
			
			if (!getSaleSummaryKey(tabBaseInfo,date))
			{
				return false;
			}
			
			tj = getTjje();//获取退劵金额
			
			getSaleSummary(bcValue,syyhValue,lblSaleAmount,lblSaleMoney,lblReturnGoodsAmount,lblReturnGoodsMoney,lblRedCancelAmount,lblRedCancelMoney,lblCancelAmount,lblCancelMoney,lblSpoilageMoney,lblGiveChangeMoney,tabPayInfo,lblShouldInceptMoney,lblFactInceptMoney,date);
			
			return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	public boolean getSaleSummaryKey(Table tabBaseInfo,String date)
	{
		ResultSet rs = null;
		String sqlstr = null;
		String syyname = null;
		
		try
		{		 
			 if (GlobalInfo.posLogin.operrange == 'Y')
	         {
				 sqlstr = "select bc,syyh from SALESUMMARY";
	         }
			 else
			 {
				 sqlstr = "select bc,syyh from SALESUMMARY where syyh = '"+ GlobalInfo.posLogin.gh +"'";
				 if (GlobalInfo.sysPara.isshowAllBcData == 'N')
				 {
					 sqlstr += " and bc='"+ GlobalInfo.syjStatus.bc +"'";
				 }
				 syyname = "(" +  GlobalInfo.posLogin.name + ")";
			 }
			 
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
			 
			if ((rs = sql.selectData(sqlstr)) != null)
			{
				if (rs.next())
				{
					String[] baseinfo = { DataService.getDefault().getTimeNameByCode(rs.getString("bc").charAt(0)),rs.getString("syyh")};
                	TableItem item = new TableItem(tabBaseInfo, SWT.NONE);
                	item.setText(baseinfo);
                	
                	bcValue =  DataService.getDefault().getTimeNameByCode(rs.getString("bc").charAt(0));
                	syyhValue = rs.getString("syyh");
				}
				else
				{
					new MessageBox(Language.apply("当前数据库无数据!"), null, false);
					return false;
				}
				
				while(rs.next())
				{
	                 OperUserDef staff = new OperUserDef();

	                 if (!DataService.getDefault().getOperUser(staff, rs.getString("syyh").trim()))
	                 {
	                	 syyname = "";
	                 }
	                 else
	                 {
	                	 syyname =  "(" + staff.name + ")";
	                 }
	                 
					String[] baseinfo = { DataService.getDefault().getTimeNameByCode(rs.getString("bc").charAt(0)),rs.getString("syyh") + syyname};
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
			if (sql != null)
			{
				sql.resultSetClose();
			}
		}
		
	}
	
	private double getTjje(){
		
		ResultSet rs = null;
		String sqlstr = null;
		double tjje=0;
		try
		{
			
			if (GlobalInfo.posLogin.operrange == 'Y')
	        {
				 sqlstr = "select sum(JE) AS JE from salepay where paycode = '0502' and  payno ='0000'";
				 if ((rs = sql.selectData(sqlstr)) != null)
					{
						if(rs.next())
						{
							tjje = rs.getDouble("JE");
						}
					}
	        }
			else
			{
				sqlstr = "select fphm from salehead where syyh='"+ GlobalInfo.posLogin.gh +"'";
//				sqlstr = "select fphm from salehead where syyh='828001'";
				Vector fphmlist= new Vector();
				if ((rs = sql.selectData(sqlstr)) != null)
				{
					while(rs.next())
					{
						fphmlist.add(rs.getString("fphm"));
					}
				}
				
				for(int j = 0;j< fphmlist.size();j++)
				{
					String fphm = (String) fphmlist.elementAt(j);
					sqlstr = "select sum(JE) AS JE from salepay where paycode = '0502' and  payno ='0000' and fphm='"+fphm+"'";
					
					if ((rs = sql.selectData(sqlstr)) != null)
					{
						if(rs.next())
						{
							tjje = tjje+rs.getDouble("JE");
						}
					}
				}
				 
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (sql != null)
			{
				sql.resultSetClose();
			}
		}
		
		return tjje;
		
	}
	
	public void getSaleSummary(String bc,String syyh,Label lblSaleAmount,Label lblSaleMoney,Label lblReturnGoodsAmount,Label lblReturnGoodsMoney,Label lblRedCancelAmount,Label lblRedCancelMoney,Label lblCancelAmount,Label lblCancelMoney, Label lblSpoilageMoney,Label lblGiveChangeMoney,Table tabPayInfo,Label lblShouldInceptMoney,Label lblFactInceptMoney,String date)
	{
		try
		{
			String newSyyh = getSyyhFromText(syyh);
			
			getSaleSummaryInfo(bc,newSyyh,lblSaleAmount,lblSaleMoney,lblReturnGoodsAmount,lblReturnGoodsMoney,lblRedCancelAmount,lblRedCancelMoney,lblCancelAmount,lblCancelMoney,lblSpoilageMoney,lblGiveChangeMoney,lblShouldInceptMoney,lblFactInceptMoney,date);
			
			getSalePaySummary(bc,newSyyh,tabPayInfo,date);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	private void getSaleSummaryInfo(String bc,String syyh,Label lblSaleAmount,Label lblSaleMoney,Label lblReturnGoodsAmount,Label lblReturnGoodsMoney,Label lblRedCancelAmount,Label lblRedCancelMoney,Label lblCancelAmount,Label lblCancelMoney, Label lblSpoilageMoney,Label lblGiveChangeMoney,Label lblShouldInceptMoney,Label lblFactInceptMoney,String date)
	{
		ResultSet rs = null;
		
		try
		{
			if (sql == null)
			{
				new MessageBox(Language.apply("连接当前数据库对象为空!"), null, false);
				return ;
			}
			
			if ((rs = sql.selectData("select xsbs,xsje,thbs,thje,hcbs,hcje,qxbs,qxje,sysy,zl,ysje,sjfk,zkje from SALESUMMARY where bc = '"+ DataService.getDefault().getTimeCodeByName(bc) + "' and syyh = '"+ syyh +"'")) != null)
			{
				if (rs.next())
				{
					lblSaleAmount.setText(rs.getString("xsbs"));
					lblSaleMoney.setText(ManipulatePrecision.doubleToString(rs.getDouble("xsje")));
					lblReturnGoodsAmount.setText(rs.getString("thbs"));
					lblReturnGoodsMoney.setText(ManipulatePrecision.doubleToString(rs.getDouble("thje")));
					lblRedCancelAmount.setText(rs.getString("hcbs"));
					lblRedCancelMoney.setText(ManipulatePrecision.doubleToString(rs.getDouble("hcje")));
					lblCancelAmount.setText(ManipulatePrecision.doubleToString(rs.getDouble("zkje")));//将原来的[取消笔数]改为[折扣金额]
					lblCancelMoney.setText(rs.getString("qxbs") + "/" + ManipulatePrecision.doubleToString(rs.getDouble("qxje")));
					lblSpoilageMoney.setText(ManipulatePrecision.doubleToString(rs.getDouble("sysy")));
					lblGiveChangeMoney.setText(ManipulatePrecision.doubleToString(rs.getDouble("zl")));
					lblShouldInceptMoney.setText(ManipulatePrecision.doubleToString(rs.getDouble("ysje")));
					lblFactInceptMoney.setText(ManipulatePrecision.doubleToString(rs.getDouble("sjfk")+tj));
					
				}
				else
				{
					clear(lblSaleAmount,lblSaleMoney,lblReturnGoodsAmount,lblReturnGoodsMoney,lblRedCancelAmount,lblRedCancelMoney,lblCancelAmount,lblCancelMoney,lblSpoilageMoney,lblGiveChangeMoney,lblShouldInceptMoney,lblFactInceptMoney);
				}
			}
			else
			{
				clear(lblSaleAmount,lblSaleMoney,lblReturnGoodsAmount,lblReturnGoodsMoney,lblRedCancelAmount,lblRedCancelMoney,lblCancelAmount,lblCancelMoney,lblSpoilageMoney,lblGiveChangeMoney,lblShouldInceptMoney,lblFactInceptMoney);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (sql != null)
			{
				sql.resultSetClose();
			}
		}
	}
	
	private void getSalePaySummary(String bc,String syyh,Table tabPayInfo,String date)
	{
		ResultSet rs = null;
		
		try
		{
			if (sql == null)
			{
				new MessageBox(Language.apply("连接当前数据库对象为空!"), null, false);
				return ;
			}
			
			if ((rs = sql.selectData("select payname,bs,je from SALEPAYSUMMARY where bc = '"+ DataService.getDefault().getTimeCodeByName(bc) + "' and syyh = '"+ syyh +"'")) != null)
			{
				tabPayInfo.removeAll();
				while(rs.next())
				{
					String[] payinfo = {rs.getString("payname"),String.valueOf(rs.getInt("bs")),ManipulatePrecision.doubleToString(rs.getDouble("je"))};
					if(payinfo[0].equals("打印券"))
					{
						payinfo[2] = ManipulatePrecision.doubleToString(Double.parseDouble(payinfo[2])+tj);
					}
                	TableItem item = new TableItem(tabPayInfo, SWT.NONE);
                	item.setText(payinfo);
                	tabPayInfo.select(0);
				}
			}
			else
			{
				tabPayInfo.removeAll();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (sql != null)
			{
				sql.resultSetClose();
			}
		}
	}
	
	//打印
	public void  printSyySale(String bc,String syycode,String date)
	{
		try
		{
			String syyh = getSyyhFromText(syycode);
			
			if (!createSyySaleHead(bc,syyh,date)) return ;
		
			if (!createSyySaleDeatil(bc,syyh,date)) return ;
		
			ProgressBox pb = new ProgressBox();
			pb.setText(Language.apply("正在打印收银员销售报表,请等待..."));
		
			printPayJk();
			
			pb.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			payList.clear();
		}
	}
	
	 //打印
    private void printPayJk()
    {
    	try
    	{
    		if (ssd == null || payList == null)
    		{
    			new MessageBox(Language.apply("未发现收银员销售报表对象,不能打印"));
    			return ; 
    		}

    		// 打印
    		SyySaleBillMode.getDefault().setTemplateObject(ssd, payList);
    		SyySaleBillMode.getDefault().printBill();
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    }
    
	public boolean createSyySaleHead(String bc,String syycode,String date)
	{
		ResultSet rs = null;
		
		try
		{
			if (sql == null)
			{
				new MessageBox(Language.apply("连接当前数据库对象为空!"), null, false);
				return false;
			}
			
			if ((rs = sql.selectData("select * from SALESUMMARY where bc = '"+ DataService.getDefault().getTimeCodeByName(bc) + "' and syyh = '"+ syycode +"'")) == null)
			{
				new MessageBox(Language.apply("收银员销售报表主单查询出现失败!"), null, false);
    			return false;
			}
			
            if (rs.next())
            {
            	ssd = new SaleSummaryDef();
            	
            	if (!sql.getResultSetToObject(ssd))
                {
					new MessageBox(Language.apply("生成收银员销售报表主单失败!"), null, false);
					return false;
                }
            }
            else
            {
            	new MessageBox(Language.apply("查无此收银员销售报表主单!"), null, false);
    			return false;
            }
            
            ssd.sjfk += tj;//实际付款加退劵金额
            
			return true;
		}
		catch(Exception ex)
		{
			new MessageBox(Language.apply("创建收银员销售报表主单异常!"), null, false);
			ex.printStackTrace();
			return false;
		}
		finally
    	{
			if (sql != null)
			{
				sql.resultSetClose();
			}
    	}
	}
	
	public boolean createSyySaleDeatil(String bc,String syycode,String date)
	{
		ResultSet rs = null;
		
		try
		{
			if (sql == null)
			{
				new MessageBox(Language.apply("连接当前数据库对象为空!"), null, false);
				return false;
			}
			
			if ((rs = sql.selectData("select * from SALEPAYSUMMARY where bc = '"+ DataService.getDefault().getTimeCodeByName(bc) + "' and syyh = '"+ syycode +"'")) == null)
    		{
    			new MessageBox(Language.apply("销售明细付款查询出现失败!"), null, false);
    			return false;
    		}
			
			//
            boolean ret = false;
            payList = new ArrayList();
            while(rs.next())
            {
            	SalePaySummaryDef spsd = new SalePaySummaryDef();
            	
            	if (!sql.getResultSetToObject(spsd))
            	{
            		new MessageBox(Language.apply("销售明细付款失败!"), null, false);
            		return false;
            	}
            	
            	if(spsd.paycode.equals("0502"))spsd.je += tj;//明细有打印劵要加上退劵金额
            	
            	payList.add(spsd);
            	
            	ret = true;
            }
            if (!ret)
            {
            	new MessageBox(Language.apply("查无此销售明细付款!"), null, false);
    			return false;
            }
            
			return true;
		}
		catch(Exception ex)
		{
			new MessageBox(Language.apply("创建销售明细付款异常!"), null, false);
			
			ex.printStackTrace();
			return false;
		}
		finally
		{
			if (sql != null)
			{
				sql.resultSetClose();
			}
		}
	}
	
	public void Close()
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
	
	public void clear (Label lblSaleAmount,Label lblSaleMoney,Label lblReturnGoodsAmount,Label lblReturnGoodsMoney,Label lblRedCancelAmount,Label lblRedCancelMoney,Label lblCancelAmount,Label lblCancelMoney, Label lblSpoilageMoney,Label lblGiveChangeMoney,Label lblShouldInceptMoney,Label lblFactInceptMoney)
	{
		lblSaleAmount.setText("");
		lblSaleMoney.setText("");
		lblReturnGoodsAmount.setText("");
		lblReturnGoodsMoney.setText("");
		lblRedCancelAmount.setText("");
		lblRedCancelMoney.setText("");
		lblCancelAmount.setText("");
		lblCancelMoney.setText("");
		lblSpoilageMoney.setText("");
		lblGiveChangeMoney.setText("");
		lblShouldInceptMoney.setText("");
		lblFactInceptMoney.setText("");
	}
	
	public String getSyyhFromText (String text)
	{
		String syyh = text;
		
		if (text.indexOf("(") > -1)
		{
			syyh = text.substring(0,text.indexOf("("));
		}
		
		return syyh;
	}
}
