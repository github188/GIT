package com.efuture.javaPos.Logic;

import java.sql.ResultSet;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Global.SellType;
import com.swtdesigner.SWTResourceManager;

public class AliSaleTicketListBS 
{
	protected Sqldb sql = null;
	public HashMap colorMap = new HashMap();
	
	public AliSaleTicketListBS()
	{
		
	}
	
	public boolean init(Table tabTickList,Group group,Label lblmessge,String date,String fphm,int djlb)
	{
		try
		{
			if (!getSaleTicketList(tabTickList,group,lblmessge,date,fphm,djlb))
			{	
				return false;
			}
			
			return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	public boolean getSaleTicketList(Table tabTickList,Group group,Label lblmessge,String date,String inputfphm,int inputdjlb)
	{
		colorMap.clear();
		ResultSet rs = null;
		
		String fphm = null;
		String payno = null;
		String bc = null;
		String rqsj = null;
		String syyh = null;
		String djlb = null;
		String netbz = null;
		String djlbFlag = null;
		
		try
		{
			tabTickList.removeAll();
			
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
			
			String sqlstr = "";
//			String count = "0";
//			String maxfphm = "0";
//			String minfphm = "0";
//			String memo	=	"";
			
			double allysje = 0;
			double allssje = 0;
			
//			Object obj = null;
		
			String sqlwhere = "";
			
//			if (inputfphm != null && !inputfphm.trim().equals(""))
//			{
//				sqlwhere += "syyh = " + inputfphm + " and ";
//			}
			
//			if (inputdjlb >= 1)
//			{
//				if (inputdjlb == 1)
//				{
//					sqlwhere += "(djlb = '" + SellType.RETAIL_SALE + "' or ";
//					sqlwhere += "djlb = '" + SellType.BATCH_SALE + "' or ";
//					sqlwhere += "djlb = '" + SellType.EARNEST_SALE + "' or ";
//					sqlwhere += "djlb = '" + SellType.PREPARE_TAKE + "' or ";
//					sqlwhere += "djlb = '" + SellType.PREPARE_SALE + "' or ";
//					sqlwhere += "djlb = '" + SellType.EXERCISE_SALE + "') and ";
//				}
//				else if (inputdjlb == 2)
//				{
//					sqlwhere += "(djlb = '" + SellType.RETAIL_BACK + "' or ";
//					sqlwhere += "djlb = '" + SellType.BATCH_BACK + "' or ";
//					sqlwhere += "djlb = '" + SellType.EARNEST_BACK + "' or ";
//					sqlwhere += "djlb = '" + SellType.PREPARE_BACK + "' or ";
//					sqlwhere += "djlb = '" + SellType.EXERCISE_BACK + "') and ";				
//				}
//				else if (inputdjlb == 3)
//				{
//					sqlwhere += "(djlb = '" + SellType.RETAIL_SALE_HC + "' or ";
//					sqlwhere += "djlb = '" + SellType.RETAIL_BACK_HC + "' or ";
//					sqlwhere += "djlb = '" + SellType.BATCH_SALE_HC + "' or ";
//					sqlwhere += "djlb = '" + SellType.BATCH_BACK_HC + "' or ";
//					sqlwhere += "djlb = '" + SellType.EARNEST_SALE_HC + "' or ";
//					sqlwhere += "djlb = '" + SellType.EARNEST_BACK_HC + "' or ";
//					sqlwhere += "djlb = '" + SellType.PREPARE_TAKE_HC + "' or ";
//					sqlwhere += "djlb = '" + SellType.PREPARE_SALE_HC + "' or ";
//					sqlwhere += "djlb = '" + SellType.PREPARE_BACK_HC + "') and ";
//				}
//			}
//			
//			sqlstr ="select count(*) from salehead";
//			
//			if (!sqlwhere.equals(" where "))
//			{
//				sqlstr += sqlwhere.substring(0,sqlwhere.length() - 5);
//			}
//			
//			if ((obj  = sql.selectOneData(sqlstr)) == null)
//			{
//				count = "0";
//			}
//			else
//			{
//				count = String.valueOf(obj);
//			}
//				
//			sqlstr ="select max(fphm) from salehead";
//			
//			if (!sqlwhere.equals(" where "))
//			{
//				sqlstr += sqlwhere.substring(0,sqlwhere.length() - 5);
//			}
//			
//			if ((obj  = sql.selectOneData(sqlstr)) == null)
//			{
//				maxfphm = "0";
//			}
//			else
//			{
//				maxfphm = String.valueOf(obj);
//			}
			
//			sqlstr ="select min(fphm) from salehead";
//			
//			if (!sqlwhere.equals(" where "))
//			{
//				sqlstr += sqlwhere.substring(0,sqlwhere.length() - 5);
//			}
//			
//			if ((obj  = sql.selectOneData(sqlstr)) == null)
//			{
//				minfphm = "0";
//			}
//			else
//			{
//				minfphm = String.valueOf(obj);
//			}
			
			if (inputfphm != null && !inputfphm.trim().equals(""))
			{
				sqlwhere = "and syyh =" + inputfphm ;
			}
			
			sqlstr ="select " +
					"sh.fphm," +
					"sh.bc," +
					"sh.rqsj," +
					"sh.syyh," +
					"sh.djlb," +
					"sh.hhflag," +
					"sh.netbz," +
					"sh.ysje," +
					"sh.sjfk," +
					"sh.zl," +
					"sh.hjzke,"+
					"sp.payno," +
					"sp.batch" +
					"  from SALEHEAD sh,SALEPAY sp"+
					"  where sh.fphm=sp.fphm " 
					+sqlwhere+
					" and (sp.batch='ALISALE'or sp.batch='ALISALEBCK')"+
					" order by sh.netbz asc,sh.fphm desc";
			if ((rs = sql.selectData(sqlstr)) != null)
			{
				boolean ret = false;
				while(rs.next())
				{
					if(rs.getString("fphm") != null)
					{
						fphm = rs.getString("fphm");
					}
					else
					{
						fphm = "";
					}
					
					if(rs.getString("payno") != null)
					{
						payno = rs.getString("payno");
					}
					else
					{
						payno = "";
					}
					
					if (rs.getString("bc") != null)
					{
						bc = DataService.getDefault().getTimeNameByCode(rs.getString("bc").charAt(0));
					}
					else
					{
						bc = "";
					}
					
					if (rs.getString("rqsj") != null)
					{
						String time[] = rs.getString("rqsj").split(" ");
						rqsj = time[1].substring(0,5);
					}
					else
					{
						rqsj = "";
					}
					
					if(rs.getString("syyh") != null)
					{
						syyh = rs.getString("syyh");
					}
					else
					{
						syyh = "";
					}
					
					if(rs.getString("djlb") != null)
					{
						char hhflag = 'N';
						try 
						{
							if (rs.getString("hhflag") != null && rs.getString("hhflag").length()>0)
							{
								hhflag = rs.getString("hhflag").charAt(0);
							}
						} 
						catch (Exception e) 
						{
						}
							
						djlbFlag = rs.getString("djlb");
						djlb = SellType.getDefault().typeExchange(rs.getString("djlb"), hhflag,null);
					}
					else
					{
						djlb = "";
					}
					
					if(rs.getString("netbz") != null)
					{
						netbz = rs.getString("netbz");
					}
					else 
					{
						netbz = "";
					}
					
					allysje = rs.getDouble("ysje") * SellType.SELLSIGN(djlbFlag) + allysje;
					allssje = (rs.getDouble("sjfk") - rs.getDouble("zl")) * SellType.SELLSIGN(djlbFlag) + allssje;					
					
					if (netbz.equals("Y"))
					{
						String[] salelist = {"↑" + fphm,payno,/*bc,rqsj,*/syyh,/*ManipulatePrecision.doubleToString(rs.getDouble("ysje") * SellType.SELLSIGN(djlbFlag)),ManipulatePrecision.doubleToString(rs.getDouble("hjzke") * SellType.SELLSIGN(djlbFlag)),*/ManipulatePrecision.doubleToString(rs.getDouble("sjfk") * SellType.SELLSIGN(djlbFlag)),djlb};
						TableItem item = new TableItem(tabTickList, SWT.NONE);
						setFrontGround(djlbFlag, item, netbz, fphm);
						item.setText(salelist);
					}
					else
					{
						String[] salelist = {" " + fphm,payno,/*bc,rqsj,*/syyh,/*ManipulatePrecision.doubleToString(rs.getDouble("ysje") * SellType.SELLSIGN(djlbFlag)),ManipulatePrecision.doubleToString(rs.getDouble("hjzke") * SellType.SELLSIGN(djlbFlag)),*/ManipulatePrecision.doubleToString(rs.getDouble("sjfk") * SellType.SELLSIGN(djlbFlag)),djlb};
						//String[] salelist = {" " + fphm,bc,rqsj,syyh,ManipulatePrecision.doubleToString(rs.getDouble("ysje") * SellType.SELLSIGN(djlbFlag)),ManipulatePrecision.doubleToString(rs.getDouble("hjzke") * SellType.SELLSIGN(djlbFlag)),ManipulatePrecision.doubleToString(rs.getDouble("sjfk") * SellType.SELLSIGN(djlbFlag)),djlb};
						TableItem item = new TableItem(tabTickList, SWT.NONE);
						setFrontGround(djlbFlag, item, netbz, fphm);
						item.setText(salelist);
					}
					
					ret = true;
				}
				
//				if (Long.parseLong(count) > 0 && (Long.parseLong(maxfphm) - Long.parseLong(minfphm)) + 1 != Long.parseLong(count))
//				{
//					memo = Language.apply(" / 交易小票存在跳号现象");
//				}
				
//				lblmessge.setText(Language.apply("小票张数: ") + count + Language.apply(" / 应收合计: ") + ManipulatePrecision.doubleToString(allysje) + Language.apply(" / 实收合计: ") + ManipulatePrecision.doubleToString(allssje) + memo);
				
				//山西太原三晋商贸 为防止门店人员作弊，屏蔽应收合计和实收合计。
				//lblmessge.setText("小票张数: " + count);
				
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
	
	//设置前景色
	private void setFrontGround(String djlbFlag, TableItem item, String netbz, String fphm)
	{
		//退货或者红冲交易，高亮显示前景色
		if (SellType.ISBACK(djlbFlag) || SellType.ISHC(djlbFlag))
		{
			item.setForeground(SWTResourceManager.getColor(255, 0, 0));
			
			if (netbz.equals("Y"))
			{
				colorMap.put("↑" + fphm, item.getForeground());
			}
			else
			{
				colorMap.put(" " + fphm, item.getForeground());
			}
		}
		//没有送往的记录，高亮显示前景色
		else if (!netbz.equals("Y"))
		{
			item.setForeground(SWTResourceManager.getColor(222, 10, 158));
			colorMap.put(" " + fphm, item.getForeground());
		}
		
	}
}
