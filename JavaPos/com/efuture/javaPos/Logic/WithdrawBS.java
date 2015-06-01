package com.efuture.javaPos.Logic;


import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.PrintTemplate.PayinBillMode;
import com.efuture.javaPos.Struct.PayinDetailDef;
import com.efuture.javaPos.Struct.PayinHeadDef;
import com.efuture.javaPos.Struct.PayinModeDef;
import com.efuture.javaPos.Struct.PosTimeDef;
import com.swtdesigner.SWTResourceManager;

public class WithdrawBS
{
    protected HashMap hmPayMode = null;
    protected String[][] paymoddef = null;
    protected ArrayList payListMode = null;
    protected PayinHeadDef phd = null;
    protected Sqldb sql = null;
    
    public WithdrawBS()
    {
        hmPayMode   = new HashMap();
        payListMode = new ArrayList();
    }

    //进入缴款
    public boolean comeInto(Table tabBeFore, Table tabInputMoney, StyledText txtCode,Text txtTime,Combo combopostime,Text txtQueryDate, Label lblCountAmount,Label lblCountMoney)
    {
        try
        {
            txtTime.setText(new ManipulateDateTime().getDateByEmpty());
            
            if (txtQueryDate.getText().equals(""))
            {
            	txtQueryDate.setText(new ManipulateDateTime().getDateByEmpty());
            }
            
            // 得到最大流水号
            if (setWithdrawCode(txtCode) < 1)
            {
            	return false;
            }
            
            // 获得当天缴款单信息
            if (!getCurrWithdrawInfo(tabBeFore,txtQueryDate.getText()))
            {
                return false;
            }
            
            // 设置班次信息
            if (!InitPosTimeCombo(combopostime))
            {
            	return false;
            }
            
            // 获得项目信息
            if (!getPayInMode(tabInputMoney))
            {
                return false;
            }
            
            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return false;
        }
    }

    // 设置班次信息
    private boolean InitPosTimeCombo(Combo combopostime)
    {
        String[] content = null;
        PosTimeDef postime = null;
        int assign = 0;

        try
        {
            if ((GlobalInfo.posTime != null) &&
                    (GlobalInfo.posTime.size() > 0))
            {
                content = new String[GlobalInfo.posTime.size()];

                for (int i = 0; i < GlobalInfo.posTime.size(); i++)
                {
                    postime = (PosTimeDef) GlobalInfo.posTime.elementAt(i);

                    content[i] = postime.name;

                    if (GlobalInfo.syjStatus.bc == postime.code)
                    {
                    	assign = i;
                    }
                }

                combopostime.setItems(content);
                combopostime.select(assign);
            }
        }
        catch (Exception er)
        {
            er.printStackTrace();
            
            return false;
        }
        finally
        {
            content = null;
            postime = null;
        }
        
        return true;
    }
    
    //判断日期合法性
    public boolean checkDate(String date)
    {
        try
        {
        	if ((date == null) || date.trim().equals(""))
            {
                new MessageBox(Language.apply("日期不能为空,请重新输入!"), null, false);

                return false;
            }
        	
        	if (date.trim().length() < 8)
	        {
	            new MessageBox(Language.apply("不合法的日期输入,请检查是否有8位长\n请重新输入(YYYYMMDD)!"), null, false);
	
	            return false;
	        }
        	
            if (!ManipulateDateTime.checkDate(ManipulateDateTime.getConversionDate(date)))
            {
            	new MessageBox(Language.apply("日期输入不合法,请重新在输!"), null, false);
                return false;
            }
            
            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            new MessageBox(Language.apply("输入日期出现异常:") + ex.getMessage(), null, false);
            return false;
        }
    }

    //得到缴款单号最大值
    private int setWithdrawCode(StyledText txtCode)
    {
        Object obj = null;
        long maxno = 0;

        try
        {
        	obj = GlobalInfo.dayDB.selectOneData("select max(seqno) from PAYINHEAD");

            if (obj != null)
            {
            	//从本地取最大缴款单号
            	maxno = Convert.toLong(String.valueOf(obj));              
            }
            if (GlobalInfo.sysPara.isGetNetMaxJkdNo == 'Y')
            {
            	//从网上取最大缴款单号,并与本地最大单号进行比较
            	String[] arr = DataService.getDefault().getOneCommonValues('1', GlobalInfo.syjDef.syjh, "", "");
            	if (arr != null)
            	{
                	long maxno_net = 0;
            		maxno_net = Convert.toLong(arr[0]);
                	if (maxno_net > maxno)
                	{
                		maxno = maxno_net;
                	}
            	}            	
            }
            
            if (maxno < 1)
            {
            	maxno = 1;
            }
            else
            {
            	maxno++;
            }
            txtCode.setText(String.valueOf(maxno));
        	
            return Convert.toInt(txtCode.getText());
            
            /*以下为旧的查找方法
            obj = GlobalInfo.dayDB.selectOneData("select max(seqno) from PAYINHEAD");

            if (obj != null)
            {
           
               txtCode.setText(String.valueOf(Long.parseLong(String.valueOf(obj)) + 1));
               
               return Integer.parseInt(txtCode.getText());
            }
            else
            {
            	txtCode.setText("1");
            	
                return 1;
            }*/
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return -1;
        }
    }

    //得到当天缴款单信息
    public boolean getCurrWithdrawInfo(Table tabBeFore,String date)
    {
        ResultSet rs = null;
        String sqlstr = null;
        
        try
        {
        	tabBeFore.removeAll();
        	
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
        	
        	if (GlobalInfo.posLogin.operrange == 'Y')
            {
        		sqlstr = "select * from PAYINHEAD";
            }
        	else
        	{
        		sqlstr = "select * from PAYINHEAD where syyh ='" + GlobalInfo.posLogin.gh +"'";
        	}
        	
            if ((rs = sql.selectData(sqlstr)) != null)
            {
                while (rs.next())
                {
                	PayinHeadDef payinhead = new PayinHeadDef();
                	if (sql.getResultSetToObject(payinhead))
                	{
	                    String[] tempstr = payinhead.rqsj.split(" ");
	
	                    if (payinhead.netbz == 'Y')
	                    {
	                        String[] withdrawinfo = 
	                                                {
	                                                    "↑" +
	                                                    payinhead.seqno,
	                                                    tempstr[1],
	                                                    payinhead.syyh,
	                                                    ManipulatePrecision.doubleToString(payinhead.je)
	                                                };
	                        TableItem item = new TableItem(tabBeFore, SWT.NONE);
	                        
	                        if (payinhead.hcbz == 'Y')
	                        {
	                    		item.setBackground(SWTResourceManager.getColor(255, 0, 0));
	                    		
	                    		item.setForeground(SWTResourceManager.getColor(255, 255, 255));
	                        }
	                        
	                        item.setText(withdrawinfo);
	                    }
	                    else
	                    {
	                        String[] withdrawinfo = 
	                                                {
	                                                    "  " +
	                                                    payinhead.seqno,
	                                                    tempstr[1],
	                                                    payinhead.syyh,
	                                                    ManipulatePrecision.doubleToString(payinhead.je)
	                                                };
	                        TableItem item = new TableItem(tabBeFore, SWT.NONE);
	                        
	                        if (payinhead.hcbz == 'Y')
	                        {
	                    		item.setBackground(SWTResourceManager.getColor(255, 0, 0));
	                    		
	                    		item.setForeground(SWTResourceManager.getColor(255, 255, 255));
	                        }
	                        
	                        item.setText(withdrawinfo);
	                    }
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
        	sql.resultSetClose();
        }
    }

    //得到缴款项目信息
    public boolean getPayInMode(Table tabInputMoney)
    {
    	 ResultSet rs = null;
         Object obj = null;
         int i = 0;
         String count;
         
         try
         {
         	 if ((obj = GlobalInfo.localDB.selectOneData("select count(*) from PAYINMODE")) != null)
         	 {
         		 count = String.valueOf(obj);
         		 
         		 if (Long.parseLong(count) <=0) return false;
         		 
         		 paymoddef = new String[Integer.parseInt(count)][5];
         	 }
         	 else
         	 {
         		 return false;
         	 }
         	 
             if ((rs = GlobalInfo.localDB.selectData("select code,name,type,base,hl from PAYINMODE")) != null)
             {
                 while (rs.next())
                 {
                     PayinModeDef pmd = new PayinModeDef();
                     pmd.code = rs.getString("code");
                     pmd.name = rs.getString("name");
                     pmd.type = rs.getString("type").charAt(0);
                     pmd.base = rs.getDouble("base");
                     pmd.hl   = rs.getDouble("hl");
                     hmPayMode.put(pmd.code, pmd);

                     String[] payInMode = { rs.getString("name"), "", "" };
                     TableItem item = new TableItem(tabInputMoney, SWT.NONE);
                     item.setText(payInMode);

                     paymoddef[i][0] = pmd.code;

                     if (pmd.base > 0)
                     {
                         paymoddef[i][1] = "Y";
                         paymoddef[i][2] = "N";
                         paymoddef[i][3] = "N";
                         paymoddef[i][4] = "N";
                     }
                     else
                     {
                         paymoddef[i][1] = "Y";
                         paymoddef[i][2] = "Y";
                         paymoddef[i][3] = "N";
                         paymoddef[i][4] = "N";
                     }

                     i++;
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
         	GlobalInfo.localDB.resultSetClose();
         }
    }
    
    public boolean getQueryPayinDetail(Table tabInputMoney,Table tabBeFore,Label lblCountAmount, Label lblCountMoney,int index,String date)
    {
    	ResultSet rs = null;
    	int amount = 0;
    	double money = 0;
    	
    	try
    	{
	    	TableItem tableItem = tabBeFore.getItem(index);
	    		
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
	    	
	    	clearQueryDeatil(tabInputMoney,lblCountAmount,lblCountMoney);
	    	
	    	if ((rs = sql.selectData("select * from payindetail where seqno = " + tableItem.getText(0).substring(1).trim() + " order by seqno")) != null)
	        {
	    		while (rs.next())
                {
	    			PayinDetailDef pdd = new PayinDetailDef();
	    			
	    			if (!sql.getResultSetToObject(pdd))
	    			{
	    				new MessageBox(Language.apply("查询缴款明细时出现错误!"), null, false);
	    				return false;
	    			}
	    			
	    			for (int i = 0; i <  paymoddef.length;i++)
	    			{
	    				String code = paymoddef[i][0];
	    				
	    				if (!code.equals(pdd.code)) continue;
	    				
	    				TableItem tableItem1 = tabInputMoney.getItem(i);
	    				
	    				amount = pdd.zs + amount;
	    				money = pdd.je + money;
	    				
	    				tableItem1.setText(1,String.valueOf(pdd.zs));
	    				tableItem1.setText(2,ManipulatePrecision.doubleToString(pdd.je));
	    			}
                }
	        }
	    	
	    	lblCountAmount.setText(String.valueOf(amount));
	    	lblCountMoney.setText(ManipulatePrecision.doubleToString(money));
	    	return true;
    	}
    	catch (Exception ex)
    	{
    		new MessageBox(ex.getMessage(), null, false);
    		ex.printStackTrace();
    		return false;
    	}
    	finally
    	{
    		sql.resultSetClose();
    	}
    }
    
    public void clearQueryDeatil(Table tabInputMoney,Label lblCountAmount, Label lblCountMoney)
    {
    	lblCountAmount.setText("0");
    	lblCountMoney.setText("0.00");
    	
    	for (int i = 0;i < tabInputMoney.getItemCount();i++)
    	{
    		TableItem tableItem = tabInputMoney.getItem(i);
    		tableItem.setText(1,"");
    		tableItem.setText(2,"");
    	}
    }
    
    //	判断是否能移动flag:上代表1,下代表2,左代表3,右边代表4
    public boolean isPayMoneyMove(int xpar, int ypar, int flag)
    {
        try
        {
            int x = 0;
            int y = 0;

            if (paymoddef != null)
            {
                if (flag == 1)
                {
                    x = xpar - 1;
                    y = ypar;

                    if (x < 0)
                    {
                        return false;
                    }
                }
                else if (flag == 2)
                {
                    x = xpar + 1;
                    y = ypar;

                    if (x >= paymoddef.length)
                    {
                        return false;
                    }
                }
                else if (flag == 3)
                {
                    x = xpar;
                    y = ypar - 1;

                    if (y <= 0)
                    {
                        return false;
                    }
                }
                else if (flag == 4)
                {
                    x = xpar;
                    y = ypar + 1;

                    if (y >= paymoddef[x].length)
                    {
                        return false;
                    }
                }
                else
                {
                    return false;
                }

                if (paymoddef[x][y].trim().equals("Y"))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return false;
        }
    }

    public void setConfirmSaveAmout(int xpar, String ok)
    {
        paymoddef[xpar][3] = ok;
    }

    public boolean isConfirmSaveAmout(int xpar)
    {
        if (paymoddef[xpar][3].equals("Y"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void setConfirmSaveMoney(int xpar, String ok)
    {
        paymoddef[xpar][4] = ok;
    }

    public boolean isConfirmSaveMoney(int xpar)
    {
        if (paymoddef[xpar][4].equals("Y"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    //判断是否有基数
    public boolean isBase(int xpar)
    {
        try
        {
            PayinModeDef pmd = (PayinModeDef) hmPayMode.get(paymoddef[xpar][0]);

            if (pmd.base != 0)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return false;
        }
    }

    //获得并设置缴款明细
    public String getPayMoney(int xpar, int amount, double moneypar, String code)
    {
        PayinDetailDef pdd = null;
        boolean boolflag = false;

        try
        {
            double money = 0;

            PayinModeDef pmd = (PayinModeDef) hmPayMode.get(paymoddef[xpar][0]);

            if (pmd.base != 0)
            {
                money = ManipulatePrecision.mul(pmd.base,amount);
            }
            else
            {
                money = moneypar;
            }

            if (payListMode.size() == 0)
            {
                pdd       = new PayinDetailDef();
                pdd.syjh  = GlobalInfo.syjDef.syjh;
                pdd.seqno = Integer.parseInt(code);
                pdd.rowno = payListMode.size() + 1;
                pdd.code  = paymoddef[xpar][0];
                pdd.zs    = amount;
                pdd.je    = money;
                pdd.hl    = pmd.hl;
                payListMode.add(pdd);
            }
            else
            {
                for (int i = 0; i < payListMode.size(); i++)
                {
                    pdd = (PayinDetailDef) payListMode.get(i);

                    if (pdd.code.trim().equals(paymoddef[xpar][0]))
                    {
                        pdd.zs   = amount;
                        pdd.je   = money;
                        boolflag = true;
                    }
                }

                if (!boolflag)
                {
                    pdd       = new PayinDetailDef();
                    pdd.syjh  = GlobalInfo.syjDef.syjh;
                    pdd.seqno = Integer.parseInt(code);
                    pdd.rowno = payListMode.size() + 1;
                    pdd.code  = paymoddef[xpar][0];
                    pdd.zs    = amount;
                    pdd.je    = money;
                    pdd.hl    = pmd.hl;
                    payListMode.add(pdd);
                }
            }

            return ManipulatePrecision.doubleToString(money);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return null;
        }
    }

    //得到总数
    public void getSum(Label lblCountAmount, Label lblCountMoney)
    {
        try
        {
            lblCountAmount.setText(String.valueOf(getPayAmount()));
            lblCountMoney.setText(getPayTotalMoney());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    //获得缴款合计张数
    protected int getPayAmount()
    {
        try
        {
            int num = 0;

            if (payListMode == null || payListMode.size() <= 0)
            {
                return 0;
            }
            else
            {
                Iterator iterator = payListMode.iterator();

                while (iterator.hasNext())
                {
                    PayinDetailDef pdd = (PayinDetailDef) iterator.next();
                    num = num + pdd.zs;
                }

                return num;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return -1;
        }
    }

    //获得缴款单的总金额
    protected String getPayTotalMoney()
    {
        try
        {
            double totalmoney = 0.00;

            if (payListMode == null || payListMode.size() <= 0)
            {
                return "0.00";
            }
            else
            {
                Iterator iterator = payListMode.iterator();

                while (iterator.hasNext())
                {
                    PayinDetailDef pdd = (PayinDetailDef) iterator.next();
                    totalmoney = totalmoney + (pdd.je * pdd.hl);
                }

                return ManipulatePrecision.doubleToString(totalmoney);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return null;
        }
    }

    //确认方法
    public boolean validationFunc(StyledText txtCode, Text txtTime,Combo combopostime,Label lblCountAmount, Label lblCountMoney)
    {
        try
        {
        	if (!checkDate(txtTime.getText())) return false;
        	
        	boolean printJk = false;
        	PayinDetailDef pdd = null;
        	if (GlobalInfo.sysPara.printJKList == 'Y' )
        	{
        		if (payListMode != null)
        		{
	        		for (int i = 0; i < payListMode.size(); i++)
	        		{
	        			pdd = (PayinDetailDef) payListMode.get(i);
	        			if ((pdd.zs == 0) || (pdd.je == 0))
	        			{
	        				payListMode.remove(i);
	                        i--;
	                        pdd = null;
	        			}
	        		}
        		}
        		else
        		{
        			payListMode = new ArrayList();
        		}
        		
        		if (payListMode.size() <= 0)
        		{        		
	        		for (int i = 0; i < paymoddef.length; i++)
	        		{
		        		pdd       = new PayinDetailDef();
		                pdd.syjh  = GlobalInfo.syjDef.syjh;
		                pdd.seqno = -1;
		                pdd.rowno = payListMode.size() + 1;
		                pdd.code  = paymoddef[i][0];
		                pdd.zs    = 0;
		                pdd.je    = 0;
		                pdd.hl    = 1;
		                payListMode.add(pdd);
	        		}
	        		printJk = true;
        		}
        	}
        	
    		if ( payListMode == null || payListMode.size() <= 0 )
    		{
    			new MessageBox(Language.apply("必须输入缴款明细后，才能成功进行缴款"),null,false);
    			return false;
    		}
            else
            {
            	PosTimeDef postime = (PosTimeDef) GlobalInfo.posTime.elementAt(combopostime.getSelectionIndex());
            	String code = txtCode.getText();
            	if (printJk) code = "-1";
                if (savePayin(code, ManipulateDateTime.getConversionDate(txtTime.getText()), postime.code))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return false;
        }
    }

    protected boolean savePayin(String code, String date, char jkbc)
    {
    	ProgressBox pb = null;
    	 
        try
        {
            if (!createPaylistmode(code, date, jkbc))
            {
                return false;
            }

            if (GlobalInfo.sysPara.printJKList == 'Y' && Integer.parseInt(code) == -1)
            {
            	printPayJk();
            	return true;
            }
            else
            {
	            // save
	            if (!AccessDayDB.getDefault().writePayin(phd, payListMode))
	            {
	                new MessageBox(Language.apply("缴款保存本地失败"), null, false);
	              
	                return false;
	            }
	            
	            //成功缴款后,更改当前现金存量
	            for (int i = 0;i<payListMode.size();i++)
	            {
	            	PayinDetailDef pdd = (PayinDetailDef) payListMode.get(i);
	            	
	            	Object obj = GlobalInfo.localDB.selectOneData("select type from PayinMode where code = '" + pdd.code + "'");
	            	
	            	if (obj != null && String.valueOf(obj).equals("1"))
	            	{
	            		GlobalInfo.syjStatus.xjje -= ManipulatePrecision.doubleConvert(pdd.je * pdd.hl,2,1);
	            	}
	            }
	            
	            GlobalInfo.dayDB.resultSetClose();
	            
	            if (GlobalInfo.syjStatus.xjje < 0) GlobalInfo.syjStatus.xjje = 0; 
	            
	            pb = new ProgressBox();
	            
	            // send
	            pb.setText(Language.apply("正在发送缴款信息,请等待..."));
	           
	            if (NetService.getDefault().sendPayin(phd, payListMode))
	            {
	                GlobalInfo.dayDB.setSql("update PAYINHEAD set NETBZ = ? where SYJH = ? and SEQNO = ?");
	                GlobalInfo.dayDB.paramSetString(1, "Y");
	                GlobalInfo.dayDB.paramSetString(2, phd.syjh);
	                GlobalInfo.dayDB.paramSetInt(3, phd.seqno);
	
	                GlobalInfo.dayDB.executeSql();
	            }
	            
	            //打印缴款单
	            pb.setText(Language.apply("正在打印缴款单,请等待..."));
	    		//for (int i = 0; i< GlobalInfo.sysPara.printjknum; i++)
	    		//{
	        	printPayJk();
	    		//}
	            pb.close();
	            
	            new MessageBox(Language.apply("本次缴款已完成!"), null, false);
	            
	            return true;
            }
        }
        catch (Exception ex)
        {
        	if (pb != null)
        	{
        		pb.close();
        		pb = null;
        	}
        	  
            ex.printStackTrace();
            return false;
        }
        finally
        {
        	clear();
        }
    }

    //生成缴款主单与明细
    public boolean createPaylistmode(String code, String date, char jkbc)
    {
        try
        {
            double totalmoney = 0;
            PayinDetailDef pdd = null;
             
            for (int j = 0; j < payListMode.size(); j++)
            {
                pdd = (PayinDetailDef) payListMode.get(j);

                if (((pdd.zs == 0) || (pdd.je == 0)) && Integer.parseInt(code) != -1)
                {
                    payListMode.remove(j);
                    j--;
                    pdd = null;
                }
            }
            
            if (payListMode.size() < 1 )
            {
            	new MessageBox(Language.apply("没有输入有效的缴款明细!"), null, false);
            	
            	return false;
            }
            
            for (int j = 0; j < payListMode.size(); j++)
            {
                pdd        = (PayinDetailDef) payListMode.get(j);
                pdd.rowno  = j + 1;
                totalmoney = totalmoney + ManipulatePrecision.mul(pdd.je , pdd.hl);
            }

            phd       = new PayinHeadDef();
            phd.jkbc  = jkbc;
            phd.syjh  = GlobalInfo.syjDef.syjh;
            phd.seqno = Integer.parseInt(code);

            ManipulateDateTime mdt = new ManipulateDateTime();
            phd.rqsj  = mdt.getDateBySlash() + " " + mdt.getTime();
            mdt       = null;
            phd.syyh  = GlobalInfo.posLogin.gh;
            phd.jkrq  = date;
            phd.je    = totalmoney;
            phd.netbz = 'N';
            phd.hcbz  = 'N';
            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            new MessageBox(Language.apply("生成缴款主单或明细失败"), null, false);
            
            return false;
        }
    }
    
    private boolean createPayinHead(int code,String syycode,String date)
    {
    	ResultSet rs = null;
    	
    	try
    	{
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
	    	
    		if ((rs = sql.selectData("select * from payinhead where seqno =" + code + " and  syyh='" + syycode + "'")) == null)
    		{
    			new MessageBox(Language.apply("缴款主单查询出现失败!"), null, false);
    			return false;
    		}
            
            if (rs.next())
            {
            	phd = new PayinHeadDef();
            	
            	if (!sql.getResultSetToObject(phd))
                {
					new MessageBox(Language.apply("生成缴款主单失败!"), null, false);
					return false;
                }
            }
            else
            {
            	new MessageBox(Language.apply("查无此缴款主单!"), null, false);
    			return false;
            }
            
    		return true;
    	}
    	catch(Exception ex)
    	{
    		new MessageBox(Language.apply("创建缴款主单异常!"), null, false);
    		
    		ex.printStackTrace();
    		return false;
    	}
    	finally
    	{
    		sql.resultSetClose();
    	}
    }
    
    
    private boolean  createPayindeatil(int code,String syycode,String date)
    {
    	ResultSet rs = null;
    	
    	try
    	{
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
	    	
    		if ((rs = sql.selectData("select * from payindetail where seqno =" + code )) == null)
    		{
    			new MessageBox(Language.apply("缴款明细查询出现失败!"), null, false);
    			return false;
    		}
    		
    		//
            boolean ret = false;
            while(rs.next())
            {
            	PayinDetailDef pdd = new PayinDetailDef();
            	
            	if (!sql.getResultSetToObject(pdd))
            	{
            		new MessageBox(Language.apply("生成缴款明细失败!"), null, false);
            		return false;
            	}
            	
            	payListMode.add(pdd);
            	
            	ret = true;
            }
            if (!ret)
            {
            	new MessageBox(Language.apply("查无此缴款明细!"), null, false);
    			return false;
            }
            
    		return true;
    	}
    	catch(Exception ex)
    	{
    		new MessageBox(Language.apply("创建缴款明细异常!"), null, false);
    		
    		ex.printStackTrace();
    		return false;
    	}
    	finally
    	{
    		sql.resultSetClose();
    	}
    }
    
    public boolean checkReprint()
    {
    	boolean check = true;
        for (int j = 0; j < payListMode.size(); j++)
        {
        	PayinDetailDef pdd = (PayinDetailDef) payListMode.get(j);

            if ((pdd.zs != 0) && (pdd.je != 0))
            {
            	check = false;
            	break;
            }
        }        
        return check;
    }
    //打印
    public void  printPayJk(int code,String syjcode,String querydate)
    {
    	try
    	{
    		if (!checkDate(querydate)) return ;
    		
    		if (!createPayinHead(code,syjcode,querydate)) return ;
    		
    		if (!createPayindeatil(code,syjcode,querydate)) return ;
    		
    		ProgressBox pb = new ProgressBox();
    		pb.setText(Language.apply("正在打印缴款单,请等待..."));
    		
    		phd.reprint = "Y";
    		
    		//for (int i = 0; i< GlobalInfo.sysPara.printjknum; i++)
    		//{
        		printPayJk();
    		//}
//    		printPayJk();
//    		printPayJk();
    		
    		pb.close();
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	finally
    	{
    		payListMode.clear();
    	}
    }
    
    //打印
    public void printPayJk()
    {
    	try
    	{
    		if (hmPayMode == null || phd == null || payListMode == null)
    		{
    			new MessageBox(Language.apply("未发现缴款单对象,不能打印"));
    			return ; 
    		}
    		
    		for (int i = 0; i< GlobalInfo.sysPara.printjknum; i++)
    		{
    			PayinBillMode.getDefault().setTemplateObject(phd, payListMode, hmPayMode);
        		PayinBillMode.getDefault().printBill();
    		}
    		// 打印
    		//PayinBillMode.getDefault().setTemplateObject(phd, payListMode, hmPayMode);
    		//PayinBillMode.getDefault().printBill();
    	}
    	catch(Exception ex)
    	{
    		new MessageBox(Language.apply("打印时出现异常,不能打印"));
    		ex.printStackTrace();
    	}
    }
    
    public boolean hcFunction(int old_code,String old_syycode,String new_code,String date,String querydate)
    {
    	if (!checkDate(date)) return false;
    	
    	try
    	{
    		if (!createPayinHead(old_code,old_syycode,querydate)) return false;
    		
    		if (!createPayindeatil(old_code,old_syycode,querydate)) return false;
    		
    		if (phd != null && phd.hcbz == 'Y')
    		{
    			new MessageBox(Language.apply("此笔缴款单已红冲，不能被再次红冲"));
    			return false;
    		}
    		
    		//将金额和张数设定为负
    		PayinDetailDef pdd = null;
            for (int j = 0; j < payListMode.size(); j++)
            {
                pdd        = (PayinDetailDef) payListMode.get(j);
                pdd.zs	   = pdd.zs * (-1);
                pdd.je     = ManipulatePrecision.mul(pdd.je , (-1));
                pdd.seqno  = Integer.parseInt(new_code);
            }
                   
            if (savePayin(new_code, phd.jkrq, phd.jkbc))
            {
            	AccessDayDB.getDefault().updateSaleBz(old_code, 10, 'Y');
            	AccessDayDB.getDefault().updateSaleBz(Convert.toInt(new_code), 10, 'Y');
            	return true;
            }
            
            return false;

    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	finally
    	{
    		if (payListMode != null) payListMode.clear();
    	}
    	
    	return false;
    }
   
    public void clear()
    {
        try
        {
            if (hmPayMode != null)
            {
                Set set = hmPayMode.keySet();
                Iterator iterator = set.iterator();

                while (iterator.hasNext())
                {
                    PayinModeDef pmd = (PayinModeDef) hmPayMode.get(iterator.next());
                    hmPayMode.remove(pmd);
                    pmd = null;
                }

                hmPayMode = null;
            }

            if (payListMode != null)
            {
                payListMode.clear();
                payListMode = null;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    public void AutoDisplayWithdrawBsMoney(Table tabInputMoney,Label lblCountAmount, Label lblCountMoney,String date,String code)
    {
    	ResultSet rs = null;
    	StringBuffer tempstr = null;
    	 PayinDetailDef pdd = null;
    	 
    	if (GlobalInfo.sysPara.withdrawauotbsmoney == null || GlobalInfo.sysPara.withdrawauotbsmoney.trim().equals("")) return ;
    	
    	try
    	{
    		if (!PathFile.isPathExists(ConfigClass.LocalDBPath + "Invoice/" + date.trim() + "/" + LoadSysInfo.getDefault().getDayDBName()))
 			{
 				new MessageBox(Language.apply("您输入的本地数据库不存在,请重新输入!"), null, false);
 				
 				return ;
 			}
    		 
    		if (date.trim().equals(new ManipulateDateTime().getDateByEmpty()))
			{
				sql = GlobalInfo.dayDB;
			}
			else
			{
				sql = LoadSysInfo.getDefault().loadDayDB(ManipulateDateTime.getConversionDate(date.trim()));
			}
    		
    		payListMode.clear();
    		
	    	String payincodes[] = GlobalInfo.sysPara.withdrawauotbsmoney.split("\\|");
	    	
	    	for (int i = 0;i < payincodes.length;i++)
	    	{
	    		// 缴款代码
	    		String payincode = payincodes[i].trim().split("=")[0];
	    		
	    		// 付款代码集
	    		String paycodes = payincodes[i].trim().split("=")[1];
	    		
	    		// 付款代码
	    		String paycode[] = paycodes.split(",");
	    		
	    		tempstr = new StringBuffer();
	    		
	    		for (int j = 0;j < paycode.length;j++)
	    		{
	    			tempstr.append(" paycode='" + paycode[j] + "'");
	    			
	    			if (j < paycode.length - 1) tempstr.append(" or");
	    		}
	    		
	    		int bs = 0;
    			double je = 0;
    			
	    		if ((rs = sql.selectData("select sum(bs) as bs,sum(je) as je from salepaysummary where syyh = '"+ GlobalInfo.syjStatus.syyh + "' and  bc ='" + GlobalInfo.syjStatus.bc + "' and (" + tempstr.toString() + ")")) != null)
	    		{
	    			while (rs.next())
					{
	    				bs = rs.getInt("bs");
	    				je = rs.getDouble("je");
					}
	    		}
	    		
	    		sql.resultSetClose();
	    		
	    		boolean bool = false;
	    		
	    		int k = 0;
	    		for (k = 0;k < paymoddef.length;k++)
	    		{
	    			if (payincode.equals(paymoddef[k][0])) 
	    			{
	    				bool = true;
	    				break;
	    			}
	    		}
	    		
	    		if (bool)
	    		{
	    			tabInputMoney.getItem(k).setText(1,ManipulatePrecision.doubleToString(bs));
	    			tabInputMoney.getItem(k).setText(2,ManipulatePrecision.doubleToString(je));
	    			
	    			 pdd       = new PayinDetailDef();
	                 pdd.syjh  = GlobalInfo.syjDef.syjh;
	                 pdd.seqno = Integer.parseInt(code);
	                 pdd.rowno = payListMode.size() + 1;
	                 pdd.code  = payincode;
	                 pdd.zs    = bs;
	                 pdd.je    = je;
	                 pdd.hl    = 1;
	                 payListMode.add(pdd);
	    		}
	    	}
	    	
	    	this.getSum(lblCountAmount, lblCountMoney);
    	}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	finally
    	{
    		if (tempstr != null)
    		{
    			tempstr.delete(0,tempstr.length());
    			tempstr = null;
    		}
    		
    		if (sql!= null)
    		{
    			sql.resultSetClose();
    			sql = null;
    		}
    	}
    }
}
