package custom.localize.Cqdr;

import java.sql.ResultSet;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Struct.MenuFuncDef;
import com.efuture.javaPos.Struct.SalePaySummaryDef;
import com.efuture.javaPos.Struct.SaleSummaryDef;
import com.efuture.javaPos.UI.MenuFuncEvent;

public class Cqdr_MenuFuncBS extends MenuFuncBS
{
	  
	public boolean execExtendFuncMenu(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		if (Integer.parseInt(mfd.code)== 221)
		{
			PrintXSD();
			return true;
		}
		else if (Integer.parseInt(mfd.code)== 222)
		{
			printSummary();
			return true;
		}
		return false;
	}
	
	public void PrintXSD()
	{
		ResultSet rs = null;
		String gzName = null;
		
	/*	if(gzName==null){
			this.GetGZ();
		}
		*/
		try//查询柜组名
    	{
	    	rs = GlobalInfo.localDB.selectData("select NAME from MANAFRAME where GZ='" +GlobalInfo.posLogin.yyygz+ "'");
	    	
	    	while (rs != null && rs.next())
	    	{
	    		gzName = rs.getString("NAME"); 
	    	}
	    	
	    	GlobalInfo.localDB.resultSetClose();
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	finally
    	{
    		GlobalInfo.localDB.resultSetClose();
    	}
		
		Printer.getDefault().printLine_Normal(Convert.appendStringSize("", "收银员日销售明细", 0, 38, 38,2));
		Printer.getDefault().printLine_Normal("门店号:"+GlobalInfo.sysPara.mktcode+"       日期:"+GlobalInfo.balanceDate);
		Printer.getDefault().printLine_Normal("收银机:"+ConfigClass.CashRegisterCode+"       收银员:"+GlobalInfo.posLogin.gh);
		Printer.getDefault().printLine_Normal("商铺号/名  称:"+GlobalInfo.posLogin.yyygz+"/"+gzName);
		String line = Convert.appendStringSize("", "序", 0, 2, 38);
		line = Convert.appendStringSize(line, "小票号", 4, 8, 38);
		line = Convert.appendStringSize(line, "时间", 14, 10, 38);
		line = Convert.appendStringSize(line, "数量", 25, 6, 38);
		line = Convert.appendStringSize(line, "金额", 32, 6, 38);
		Printer.getDefault().printLine_Normal(line);
		Printer.getDefault().printLine_Normal("========================================");

		double zje = 0;
		double zsl = 0;
    	int num = 1;
    	try
    	{
	    	rs = GlobalInfo.dayDB.selectData("select * from SALEHEAD WHERE SYYH ='"+GlobalInfo.posLogin.gh+"'");
	    	
	    	while (rs != null && rs.next())
	    	{
	    		String fphm = rs.getString("FPHM");
	    		String RQSJ = rs.getString("RQSJ");
	    		String sl	= rs.getString("HJZSL");
	    		zsl += Convert.toDouble(sl);
	    		String hjje	= rs.getString("HJZJE");
	    		String hjzk	= rs.getString("HJZKE");
	    		zje += (Convert.toDouble(hjje) - Convert.toDouble(hjzk));
	    		String DJLB = rs.getString("DJLB");
	    		String hhflg= rs.getString("HHFLAG");
	    		
	    		line = Convert.appendStringSize("", String.valueOf(num), 0, 2, 38);
	    		line = Convert.appendStringSize(line, fphm, 4, 8, 38);
	    		line = Convert.appendStringSize(line, RQSJ.split(" ")[1], 14, 10, 38);
	    		line = Convert.appendStringSize(line, sl, 25, 6, 38);
	    		line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(Convert.toDouble(hjje) - Convert.toDouble(hjzk)), 32, 6, 38);
	    		Printer.getDefault().printLine_Normal(line);
	    		Printer.getDefault().printLine_Normal(SellType.getDefault().typeExchange(DJLB, hhflg.charAt(0), null));
	    		num++;
	    	}
	    	
	    	GlobalInfo.dayDB.resultSetClose();
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	finally
    	{
    		GlobalInfo.dayDB.resultSetClose();
    	}
    	
		Printer.getDefault().printLine_Normal("=========================================");
		Printer.getDefault().printLine_Normal("总数量:"+ManipulatePrecision.doubleToString(zsl)+"       总金额:"+ManipulatePrecision.doubleToString(zje));
	
		try
    	{
	    	rs = GlobalInfo.dayDB.selectData("select * from SALEPAYSUMMARY WHERE SYYH ='"+GlobalInfo.posLogin.gh+"'");
	    	
	    	while (rs != null && rs.next())
	    	{
	    		String payname = rs.getString("PAYNAME");
	    		String je = rs.getString("JE");
	    		Printer.getDefault().printLine_Normal(payname+"   "+je);
	    	}
	    	
	    	GlobalInfo.dayDB.resultSetClose();
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	finally
    	{
    		GlobalInfo.dayDB.resultSetClose();
    	}
    
    	Printer.getDefault().printLine_Normal("");
    	Printer.getDefault().printLine_Normal("");
    	Printer.getDefault().printLine_Normal("");
    	Printer.getDefault().printLine_Normal("");
    	Printer.getDefault().printLine_Normal("");
    	Printer.getDefault().printLine_Normal("");
    	Printer.getDefault().printLine_Normal("");
	}
	
	/*private void GetGZ() {
		ResultSet rs = null;
		String gz = null;
		String name = null;
		try
    	{
	    	rs = GlobalInfo.dayDB.selectData("select GZ from SALEGOODS where SYJH='" +ConfigClass.CashRegisterCode+"'"+"order by ROWNO ASC");
	    	
	    	if (rs != null && rs.next())
	    	{
	    		gz = rs.getString("GZ"); 
	    	}
	    	
	    	GlobalInfo.dayDB.resultSetClose();
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	finally
    	{
    		GlobalInfo.dayDB.resultSetClose();
    	}
    	
    	try//查询柜组名
    	{
	    	rs = GlobalInfo.localDB.selectData("select NAME from MANAFRAME where GZ='" +gz+ "'");
	    	
	    	while (rs != null && rs.next())
	    	{
	    		name = rs.getString("NAME"); 
	    	}
	    	gzName = gz+"/"+name;
	    	
	    	GlobalInfo.localDB.resultSetClose();
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
*/
	public void printSummary()
	{
		String line = "";
		ResultSet rs = null;
		
		Vector salepaysummary = new Vector();
		try
    	{
	    	rs = GlobalInfo.dayDB.selectData("select * from SALEPAYSUMMARY");
	    	
	    	while (rs != null && rs.next())
	    	{
	    		SalePaySummaryDef smsd = new SalePaySummaryDef();
            	
            	if (!GlobalInfo.dayDB.getResultSetToObject(smsd))
            	{
            		new MessageBox("营业员报表对象失败!", null, false);
            		return ;
            	}
            	
            	salepaysummary.add(smsd);
	    	}
	    	
	    	GlobalInfo.dayDB.resultSetClose();
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	finally
    	{
    		GlobalInfo.dayDB.resultSetClose();
    	}

    	Vector salesummary = new Vector();
		try
    	{
	    	rs = GlobalInfo.dayDB.selectData("select * from SALESUMMARY");
	    	
	    	while (rs != null && rs.next())
	    	{
	    		SaleSummaryDef smsd = new SaleSummaryDef();
            	
            	if (!GlobalInfo.dayDB.getResultSetToObject(smsd))
            	{
            		new MessageBox("营业员报表对象失败!", null, false);
            		return ;
            	}
            	
            	salesummary.add(smsd);
	    	}
	    	
	    	GlobalInfo.dayDB.resultSetClose();
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	finally
    	{
    		GlobalInfo.dayDB.resultSetClose();
    	}

    /*	SaleSummaryDef ssd1 = (SaleSummaryDef) salesummary.elementAt(0);
    	String gz = null;
    	try // 查询营业员所在柜组
    	{
	    	rs = GlobalInfo.baseDB.selectData("select YYYGZ from OPERUSER where GH='" +ssd1.syyh+ "'");
	    	
	    	while (rs != null && rs.next())
	    	{
	    		gz = rs.getString("YYYGZ"); 
	    	}
	    	
	    	GlobalInfo.baseDB.resultSetClose();
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	finally
    	{
    		GlobalInfo.baseDB.resultSetClose();
    	}
    	
    	String gzName = null;
		try//查询柜组名
    	{
	    	rs = GlobalInfo.localDB.selectData("select NAME from MANAFRAME where GZ='" +gz+ "'");
	    	
	    	while (rs != null && rs.next())
	    	{
	    		gzName = rs.getString("NAME"); 
	    	}
	    	
	    	GlobalInfo.localDB.resultSetClose();
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	finally
    	{
    		GlobalInfo.localDB.resultSetClose();
    	}
    */
    	String gzName = null; 
    	try//查询柜组名
    	{
	    	rs = GlobalInfo.localDB.selectData("select NAME from MANAFRAME where GZ='" +GlobalInfo.posLogin.yyygz+ "'");
	    	
	    	while (rs != null && rs.next())
	    	{
	    		gzName = rs.getString("NAME"); 
	    	}
	    	
	    	GlobalInfo.localDB.resultSetClose();
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	finally
    	{
    		GlobalInfo.localDB.resultSetClose();
    	}
    	
    	Printer.getDefault().printLine_Normal(Convert.appendStringSize("", "收银员日销售汇总", 0, 38, 38,2));
    	Printer.getDefault().printLine_Normal("门店号:"+GlobalInfo.sysPara.mktcode+"       日期:"+GlobalInfo.balanceDate);
		Printer.getDefault().printLine_Normal("收银机:"+ConfigClass.CashRegisterCode+"      商铺号/名称:"+GlobalInfo.posLogin.yyygz+"/"+gzName);
		Printer.getDefault().printLine_Normal(Convert.increaseChar("", '=', 38));
		
		SaleSummaryDef sd = null;
		for (int i =0; i < salesummary.size(); i++)
		{
			SaleSummaryDef ssd = (SaleSummaryDef) salesummary.elementAt(i);
			if (!ssd.syyh.equals("全天"))
			{
				int xx=1;
				Printer.getDefault().printLine_Normal("收银员:"+ssd.syyh+"  数量:"+ssd.xsbs+" 金额:"+ssd.xsje);
				line = "";
				for (int j = 0 ; j <salepaysummary.size(); j++)
				{
					SalePaySummaryDef spsd = (SalePaySummaryDef) salepaysummary.elementAt(j);
					if (spsd.syyh.equals(ssd.syyh))
					{
						
						if (xx%2==1)
						{
							line+=Convert.appendStringSize("", spsd.payname, 0, 9, 9)+" "+Convert.appendStringSize("", ManipulatePrecision.doubleToString(spsd.je), 0, 9, 9,1);						
						}
						else
						{
							line+=" "+Convert.appendStringSize("", spsd.payname, 0, 9, 9)+" "+Convert.appendStringSize("", ManipulatePrecision.doubleToString(spsd.je), 0, 9, 9,1);
							Printer.getDefault().printLine_Normal(line);
							line="";
						}
						xx++;
					}
				}

				if (line.length() > 0)
				{
					Printer.getDefault().printLine_Normal(line);
				}
				
				Printer.getDefault().printLine_Normal("退货:"+ssd.thje+"   红冲:"+ssd.hcje);
			}
			else
			{
				sd = ssd;
			}
		}
		Printer.getDefault().printLine_Normal(Convert.increaseChar("", '=', 38));
		if (sd != null)
		{
			line = "";
			int xx = 1;
			Printer.getDefault().printLine_Normal("总数量:"+sd.xsbs+"          金额:"+sd.xsje);
			for (int j = 0 ; j <salepaysummary.size(); j++)
			{
				SalePaySummaryDef spsd = (SalePaySummaryDef) salepaysummary.elementAt(j);
				if (spsd.syyh.equals(sd.syyh))
				{
					
					if (xx%2==1)
					{
						line+=Convert.appendStringSize("", spsd.payname, 0, 9, 9)+" "+Convert.appendStringSize("", ManipulatePrecision.doubleToString(spsd.je), 0, 9, 9,1);						
					}
					else
					{
						line+=" "+Convert.appendStringSize("", spsd.payname, 0, 9, 9)+" "+Convert.appendStringSize("", ManipulatePrecision.doubleToString(spsd.je), 0, 9, 9,1);
						Printer.getDefault().printLine_Normal(line);
						line="";
					}
					xx++;
				}
			}
			

			if (line.length() > 0)
			{
				Printer.getDefault().printLine_Normal(line);
			}
			
			Printer.getDefault().printLine_Normal("退货:"+sd.thje+"   红冲:"+sd.hcje);

		}
		Printer.getDefault().printLine_Normal("");
		Printer.getDefault().printLine_Normal("");
		Printer.getDefault().printLine_Normal("");
	}
}
