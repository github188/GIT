package com.efuture.javaPos.Logic;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import net.sf.json.JSONObject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import bankpay.alipay.service.AliPayOService;
import bankpay.alipay.service.SuNingPayService;
import bankpay.alipay.tools.AliPrintMode;
import bankpay.alipay.tools.ParseIni;
import bankpay.alipay.tools.SuNingPrintMode;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.Sqldb;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.CashBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.RefundMoneyDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.UI.Design.SaleMemoForm;
import com.swtdesigner.SWTResourceManager;

public class DisplaySaleTicketBS
{
    protected boolean saleExist = true;
    protected Sqldb sql = null;
    
    //当前小票
    protected SaleHeadDef salehead = null;
    protected Vector salegoods = null;
    protected Vector salepay = null;
    protected Vector payAssistant = null;
    
    //当前单号
    protected String shbillno = null;
    protected boolean isth = false;
    
    public DisplaySaleTicketBS()
    {
    }
    
    //*************************************************查询小票版块************************************
    public void getSaleAllInfo(String xsdate,String code,int type, Text txtTicketCode,StyledText txtSaleTime, StyledText txtSyy,StyledText txtSaleType,Table tabTicketDeatilInfo, Table tabPay,StyledText txtMemberCardCode,StyledText txtGrantCardCode,StyledText txtShouldInceptMoney,StyledText txtAgioMoney,StyledText txtFactInceptMoney,StyledText txtGiveChangeMoney,StyledText txtSpoilageMoney, Label lblNet,Group group,StyledText khje)
    {
        try
        {
            if ((code == null) || code.trim().equals(""))
            {
                new MessageBox(Language.apply("小票号不能为空"), null, false);
                clear(txtSaleTime, txtSyy, txtSaleType, tabTicketDeatilInfo,tabPay, txtMemberCardCode, txtGrantCardCode,txtShouldInceptMoney, txtAgioMoney, txtFactInceptMoney,txtGiveChangeMoney, txtSpoilageMoney, lblNet, group);
                return;
            }

            //网上查询,NEEDADD

            //以下是查询本地
            int findnum = 0;
            int findcnt = 0;
            boolean findtip = true;
            
            ProgressBox pb = new ProgressBox();

            ManipulateDateTime dt = new ManipulateDateTime();

            pb.setText(Language.apply("开始查找小票操作....."));

            while (findnum < GlobalInfo.syjDef.datatime)
            {
            	pb.setText(Language.apply("正在查找{0}的小票数据",new Object[]{dt.getDateBySlash()}));
            	
                // 打开每日数据库
            	if (xsdate != null && !xsdate.trim().equals(""))
            	{
            		if (xsdate.equals(dt.getDateByEmpty()))
            			sql = GlobalInfo.dayDB;
            		else
            			sql = LoadSysInfo.getDefault().loadDayDB(xsdate);
            	}
            	else
            	{
	                if (findnum == 0)
	                {
	                    sql = GlobalInfo.dayDB;
	                }
	                else
	                {
	                	dt.skipDate(dt.getDateBySlash(),-1);
	                	
	                    sql = LoadSysInfo.getDefault().loadDayDB(dt.getDateBySlash());
	                }
            	}
            	
                if (sql != null)
                {
	                saleExist = isExist(sql, code);
	                if (saleExist)
	                {
	                    pb.close();
	                    pb = null;
	
	                    break;
	                }
	                else
	                {
		                if (sql != GlobalInfo.dayDB)
		                {
		                    sql.Close();
		                    sql = null;
		                }
	                }
                }
                
                if (type == StatusType.MN_SALEHC) break;
                if (xsdate != null && !xsdate.trim().equals("")) break;
                
                findnum++;
                
                //
                if (findnum <= 1)
                {
                	if (new MessageBox(Language.apply("在当日的交易数据中找不到{0}号小票\n\n你要继续在前一周的交易数据中查找该小票吗？",new Object[]{code}),null,true).verify() != GlobalVar.Key1)
                	{
                		break;
                	}
                }
                else
                {
                	findcnt++;
                	if (findcnt >= 7 && findtip)
                	{
                		findcnt = 0;
                    	int sel = new MessageBox(Language.apply("在{0}这周的交易数据中找不到{1}号小票\n\n你要继续在前一周的交易数据中查找该小票吗？\n\n1-是 / 2-否 / 3-不再提示", new Object[]{dt.getDateBySlash(),code})).verify();
//                    	int sel = new MessageBox("在 " + dt.getDateBySlash() + " 这周的交易数据中找不到 " + code + " 号小票\n\n你要继续在前一周的交易数据中查找该小票吗？\n\n1-是 / 2-否 / 3-不再提示").verify();
                    	if (sel == GlobalVar.Key3) findtip = false;
                    	if (sel != GlobalVar.Key1 && sel != GlobalVar.Enter && sel != GlobalVar.Key3)
                    	{
                    		break;
                    	}
                	}
                }
            }

            if (!saleExist)
            {
                pb.close();
                pb = null;
                new MessageBox(Language.apply("没有查找到小票号"), null, false);
                clear(txtSaleTime, txtSyy, txtSaleType, tabTicketDeatilInfo,tabPay, txtMemberCardCode, txtGrantCardCode,txtShouldInceptMoney, txtAgioMoney, txtFactInceptMoney,txtGiveChangeMoney, txtSpoilageMoney, lblNet, group);

                return;
            }

            if (!this.getSaleHead(sql, code, txtSaleTime, txtSyy, txtSaleType,txtMemberCardCode, txtGrantCardCode,txtShouldInceptMoney, txtAgioMoney,txtFactInceptMoney, txtGiveChangeMoney,txtSpoilageMoney, lblNet, group))
            {
            	pb.close();
                pb = null;
                new MessageBox(Language.apply("小票主单查找失败"), null, false);
                clear(txtSaleTime, txtSyy, txtSaleType, tabTicketDeatilInfo,tabPay, txtMemberCardCode, txtGrantCardCode,txtShouldInceptMoney, txtAgioMoney, txtFactInceptMoney,txtGiveChangeMoney, txtSpoilageMoney, lblNet, group);

                return;
            }

            if (!this.getSaleDetail(sql, code, tabTicketDeatilInfo))
            {
            	pb.close();
                pb = null;
                new MessageBox(Language.apply("小票明细查找失败"), null, false);
                clear(txtSaleTime, txtSyy, txtSaleType, tabTicketDeatilInfo,tabPay, txtMemberCardCode, txtGrantCardCode,txtShouldInceptMoney, txtAgioMoney, txtFactInceptMoney,txtGiveChangeMoney, txtSpoilageMoney, lblNet, group);

                return;
            }

            if (!this.getPayDetail(sql, code, tabPay))
            {
            	pb.close();
                pb = null;
                new MessageBox(Language.apply("付款明细查找失败"), null, false);
                clear(txtSaleTime, txtSyy, txtSaleType, tabTicketDeatilInfo,tabPay, txtMemberCardCode, txtGrantCardCode,txtShouldInceptMoney, txtAgioMoney, txtFactInceptMoney,txtGiveChangeMoney, txtSpoilageMoney, lblNet, group);

                return;
            }

            txtTicketCode.setText(code);
            
            // 查询扣回金额
            if (salepay != null && salepay.size() > 0)
            {
            	double je = 0;
            	for (int k = 0 ; k < salepay.size(); k++)
            	{
            		SalePayDef pay = (SalePayDef) salepay.elementAt(k);
            		if (AccessDayDB.getDefault().isBuckleMoney(pay))
            		{
            			je += pay.je; 
            		}
            	}
            	
            	khje.setText(ManipulatePrecision.doubleToString(je));
            }
        }
        finally
        {
            if (sql != GlobalInfo.dayDB)
            {
                if (sql != null)
                {
                    sql.Close();
                }
            }
        }
    }

    protected boolean isExist(Sqldb sql, String code)
    {
        ResultSet rs = null;

        try
        {
            if (sql == null)
            {
                return false;
            }

            //syjh = '" + ConfigClass.CashRegisterCode + "' and  
            if ((rs = sql.selectData("select fphm from SALEHEAD where fphm =" + code)) != null)
            {
                if (!rs.next())
                {
                	return false;
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
    
    protected boolean getSaleHead(Sqldb sql, String code, StyledText txtSaleTime,StyledText txtSyy, StyledText txtSaleType,StyledText txtMemberCardCode,StyledText txtGrantCardCode,StyledText txtShouldInceptMoney,StyledText txtAgioMoney,StyledText txtFactInceptMoney,StyledText txtGiveChangeMoney,StyledText txtSpoilageMoney, Label lblNet,Group group)
    {
        ResultSet rs = null;

        try
        {
            if (sql == null)
            {
                return false;
            }

            // syjh = '" + ConfigClass.CashRegisterCode + "' and  
            if ((rs = sql.selectData("select * from SALEHEAD where fphm=" + code)) != null)
            {
                boolean ret = false;
                if (rs.next())
                {
                	salehead = new SaleHeadDef();
                	  
                	if (!sql.getResultSetToObject(salehead))
                    {
                		return false;
                    }
                	
                	if (salehead.syjh != null)
                	{
                		group.setText(Language.apply("收银机:") + salehead.syjh);
                	}
               
                	if (salehead.rqsj != null)
                	{
                		txtSaleTime.setText(salehead.rqsj);
                	}
                
                	if (salehead.syyh != null)
                	{
                		txtSyy.setText(salehead.syyh);
                	}
                
                	if (salehead.djlb != null && salehead.djlb.length() > 0)
                	{
               			if (!SellType.getDefault().COMMONBUSINESS(salehead.djlb, salehead.hhflag,salehead))
                		{
                			txtSaleType.setForeground(SWTResourceManager.getColor(255, 0, 128));
                        	txtSaleType.setBackground(SWTResourceManager.getColor(255, 255, 0));                			
                		}
                		else
                		{
                			txtSaleType.setForeground(SWTResourceManager.getColor(0, 0, 0));
                        	txtSaleType.setBackground(SWTResourceManager.getColor(255, 255, 255));                			
                		}
                		txtSaleType.setText(SellType.getDefault().typeExchange(salehead.djlb, salehead.hhflag,salehead));
                	}
                	
                	if (salehead.hykh != null)
                	{
                		txtMemberCardCode.setText(salehead.hykh);
                	}
                	
                	if (salehead.sqkh != null)
                	{
                		txtGrantCardCode.setText(salehead.sqkh);
                	}
                    
                    txtShouldInceptMoney.setText(ManipulatePrecision.doubleToString(salehead.ysje));
                    txtAgioMoney.setText(ManipulatePrecision.doubleToString(salehead.hjzke));
                    txtFactInceptMoney.setText(ManipulatePrecision.doubleToString(salehead.sjfk));
                    txtGiveChangeMoney.setText(ManipulatePrecision.doubleToString(salehead.zl));
                    txtSpoilageMoney.setText(ManipulatePrecision.doubleToString(salehead.sswr_sysy + salehead.fk_sysy));

                    if (salehead.netbz != ' ')
                    {
                        if (rs.getString("netbz").charAt(0) == 'Y')
                        {
                            lblNet.setForeground(SWTResourceManager.getColor(0,0,0));
                            String line = Language.apply("已送网");
                            //显示积分信息
                            if (salehead.bcjf > 0) line = Language.apply("积分:")+salehead.bcjf*SellType.SELLSIGN(salehead.djlb)+line;
                            lblNet.setText(line);
                        }
                        else
                        {
                            lblNet.setForeground(SWTResourceManager.getColor(255,0,0));
                            String line = Language.apply("未送网");
                            lblNet.setText(line);
                        }
                    }
                    
                    ret = true;
                }

                return ret;
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
        finally
        {
           if (sql != null) sql.resultSetClose();
        }
    }

    protected boolean getSaleDetail(Sqldb sql, String code,Table tabTicketDeatilInfo)
    {
        ResultSet rs = null;
        String vyyyh = null;
        String vbarcode = null;
        String vname = null;
        
        try
        {
            if (sql == null)
            {
                return false;
            }

            //syjh = '" + ConfigClass.CashRegisterCode + "' and 
            if ((rs = sql.selectData("select * from SALEGOODS where fphm = " + code + " order by rowno")) != null)
            {
                boolean ret = false;
                salegoods = new Vector();
                tabTicketDeatilInfo.removeAll();
                while (rs.next())
                {
                    SaleGoodsDef sg = new SaleGoodsDef();

                    if (!sql.getResultSetToObject(sg))
                    {
                        return false;
                    }

                    salegoods.add(sg);
                    
                    ret = true;
                }
                if (!ret) return ret;
                
                for (int i = 0;i < salegoods.size();i++)
                {
                	SaleGoodsDef sgd = (SaleGoodsDef)salegoods.get(i);
                	
                	if (sgd.yyyh != null)
                	{
                		vyyyh = sgd.yyyh; 
                	}
                	else
                	{
                		vyyyh = "";
                	}
                	
                	vbarcode =  getInputBarCode(sgd);
                	
                	if (sgd.name != null)
                	{
                		vname = sgd.name;
                	}
                	else
                	{
                		vname = "";
                	}
                	
                    String[] saleinfo = {vyyyh,vbarcode,vname,ManipulatePrecision.doubleToString(sgd.jg),ManipulatePrecision.doubleToString(sgd.sl,4,1,true),ManipulatePrecision.doubleToString(sgd.hjzk),ManipulatePrecision.doubleToString(ManipulatePrecision.sub(sgd.hjje,sgd.hjzk))};
                    TableItem item = new TableItem(tabTicketDeatilInfo, SWT.NONE);
                    item.setText(saleinfo);
                }

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
        finally
        {
            sql.resultSetClose();
        }
    }

    public String getInputBarCode(SaleGoodsDef sgd)
    {
    	String vbarcode = null;
    	if(GlobalInfo.sysPara.saleTicketBarcodeStyle.equals("B")){
    		vbarcode = sgd.barcode;
    	}else if(GlobalInfo.sysPara.saleTicketBarcodeStyle.equals("C")){
    		vbarcode = sgd.code;
    	}else{
    		if (sgd.inputbarcode != null && sgd.inputbarcode.trim().length() > 0)
        	{
        		vbarcode = sgd.inputbarcode;
        	}
        	else if (GlobalInfo.syjDef.issryyy == 'N')
        	{
        		vbarcode = sgd.barcode;
        	}
        	else
        	{
        		vbarcode = sgd.code;
        	}
    	}
    	
    	
    	
    	return vbarcode;
    }
    
    protected boolean getPayDetail(Sqldb sql, String code, Table tabPay)
    {
        ResultSet rs = null;
        
        String vpayname = null;
        String vpayno = null;
        
        try
        {
            if (sql == null)
            {
                return false;
            }

            //syjh = '" + ConfigClass.CashRegisterCode +"' and 
            if ((rs = sql.selectData("select * from SALEPAY where fphm = " + code +" Order By (case when rowno > 0 then 1 else -1 end) Desc,ABS(rowno) Asc")) != null)
            {
            	boolean ret = false;
                salepay = new Vector();
                tabPay.removeAll();
                while (rs.next())
                {
                	SalePayDef sp = new SalePayDef();
                	
                	if (!sql.getResultSetToObject(sp))
                    {
                        return false;
                    }

                	salepay.add(sp);
                	
                	ret = true;
                }
                if (!ret) return ret;
                
                for (int i = 0;i < salepay.size();i++)
                {
                	SalePayDef spd = (SalePayDef)salepay.get(i);
                	
                	vpayname = checkName(spd);
                	
                	spd.payname = vpayname;
                	
                	if (spd.payno != null)
                	{
                		vpayno = spd.payno;
                	}
                	else
                	{
                		vpayno = "";
                	}
                	
                    String[] payinfo = {vpayname,vpayno,ManipulatePrecision.doubleToString(spd.ybje)};
                    TableItem item = new TableItem(tabPay, SWT.NONE);
                    item.setText(payinfo);
                }

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
        finally
        {
            sql.resultSetClose();
        }
    }

    protected void clear(StyledText txtSaleTime, StyledText txtSyy,StyledText txtSaleType, Table tabTicketDeatilInfo,Table tabPay, StyledText txtMemberCardCode,StyledText txtGrantCardCode,StyledText txtShouldInceptMoney,StyledText txtAgioMoney, StyledText txtFactInceptMoney,StyledText txtGiveChangeMoney,StyledText txtSpoilageMoney, Label lblNet, Group group)
    {
        group.setText(Language.apply("收银机号"));
        txtSaleTime.setText("");
        txtSyy.setText("");
        txtSaleType.setText("");
        tabTicketDeatilInfo.removeAll();
        tabPay.removeAll();
        txtMemberCardCode.setText("");
        txtGrantCardCode.setText("");
        txtShouldInceptMoney.setText("");
        txtAgioMoney.setText("");
        txtFactInceptMoney.setText("");
        txtGiveChangeMoney.setText("");
        txtSpoilageMoney.setText("");
        lblNet.setText("");
    }
    
    // 显示商品附加信息
    public void showAppendInfo()
    {
    	if (salehead == null || salegoods == null || salepay == null)
    	{
    		new MessageBox(Language.apply("没有小票信息,查看单据附加信息!"), null, false);
    		return;
    	}
    	
        // 显示代码
    	new SaleMemoForm(this.salehead,this.salegoods,this.salepay,null,1);
    }
    
    //************************************打印调用*******************************************************
    
    public void printSaleTicket()
    {
        try
        {
            // 检查发票是否打印完,打印完未设置新发票号则不能交易
            if (Printer.getDefault().getSaleFphmComplate())
            {
            	return ;
            }
            
        	if (GlobalInfo.posLogin.privdy != 'Y')
        	{
        		OperUserDef user = null;
        		if ((user =DataService.getDefault().personGrant(Language.apply("授权重打印小票")))!=null)
        		{
        			if (user.privdy == 'Y')
        			{
        				String log = Language.apply("授权重打印小票: 小票号") + salehead.fphm + Language.apply(",授权:") + user.gh;
        				AccessDayDB.getDefault().writeWorkLog(log);
        			}
        			else
        			{
        				new MessageBox(Language.apply("操作失败,该工号没有重打印权限!"), null, false);
        				return ;
        			}
        		}
        		else
        		{
        			return;
        		}
        	}
        	
        	salehead.printnum++;
    		AccessDayDB.getDefault().updatePrintNum(salehead.syjh, String.valueOf(salehead.fphm), String.valueOf(salehead.printnum));
    		ProgressBox pb = new ProgressBox();
    		pb.setText(Language.apply("现在正在重打印小票,请等待....."));
    		printSaleTicket(salehead,salegoods,salepay,false);
    		pb.close();
        }
        catch (Exception er)
        {
            er.printStackTrace();
        }
    }
    
    //重打印阿里签购单
    public void printSaleTicketAli()
    {
        try
        {
            // 检查发票是否打印完,打印完未设置新发票号则不能交易
            if (Printer.getDefault().getSaleFphmComplate())
            {
            	return ;
            }
            
        	if (GlobalInfo.posLogin.privdy != 'Y')
        	{
        		OperUserDef user = null;
        		if ((user =DataService.getDefault().personGrant(Language.apply("授权重打印小票")))!=null)
        		{
        			if (user.privdy == 'Y')
        			{
        				String log = Language.apply("授权重打印小票: 小票号") + salehead.fphm + Language.apply(",授权:") + user.gh;
        				AccessDayDB.getDefault().writeWorkLog(log);
        			}
        			else
        			{
        				new MessageBox(Language.apply("操作失败,该工号没有重打印权限!"), null, false);
        				return ;
        			}
        		}
        		else
        		{
        			return;
        		}
        	}
        	
        	salehead.printnum++;
    		AccessDayDB.getDefault().updatePrintNum(salehead.syjh, String.valueOf(salehead.fphm), String.valueOf(salehead.printnum));
    		if(null != salepay)
    		{
    			SalePayDef salepayS = null;
    			String queryFlag = null;
    			JSONObject json =null;
    			AliPayOService aliPayOService = new AliPayOService();
    			ParseIni parseIni = new ParseIni();
    			Map mapN = new HashMap();
    			ProgressBox pb = new ProgressBox();
    			// 获取请求地址
    			mapN = parseIni.Parse();
    			String url = mapN.get("aliPayUrl").toString();
    			String printFlag = mapN.get("printFlag").toString();
    			for(int i =0;i<salepay.size();i++)
    			{
    				salepayS = (SalePayDef) salepay.elementAt(i);
    				//重打阿里签购单
    				if("ALISALE".equals(salepayS.batch)||"ALISALEBCK".equals(salepayS.batch))
    				{
    					 queryFlag = aliPayOService.query(salepayS.payno, "",salehead.syyh, url,salehead.mkt);
    	    			 json = JSONObject.fromObject(queryFlag);
    	    			 pb.setText(Language.apply("现在正在重打印签购单,请等待....."));
    	    			
    	    				AliPrintMode apm = new AliPrintMode();
    	    				//打印签购单
    	    				if(!apm.aliPrint(json,salehead,salegoods,salepay,printFlag))
    	    				{
    	    					new MessageBox(Language.apply("打印签购单失败"));
    	    					return;
    	    				}
    	 					pb.close();
    	 				}
    	     			
    				}
    			}
    		
        }
        catch (Exception er)
        {
            er.printStackTrace();
        }
    }
    
    
  //重打印苏宁签购单
    public void printSaleTicketSuNing()
    {
        try
        {
            // 检查发票是否打印完,打印完未设置新发票号则不能交易
            if (Printer.getDefault().getSaleFphmComplate())
            {
            	return ;
            }
            
        	if (GlobalInfo.posLogin.privdy != 'Y')
        	{
        		OperUserDef user = null;
        		if ((user =DataService.getDefault().personGrant(Language.apply("授权重打印小票")))!=null)
        		{
        			if (user.privdy == 'Y')
        			{
        				String log = Language.apply("授权重打印小票: 小票号") + salehead.fphm + Language.apply(",授权:") + user.gh;
        				AccessDayDB.getDefault().writeWorkLog(log);
        			}
        			else
        			{
        				new MessageBox(Language.apply("操作失败,该工号没有重打印权限!"), null, false);
        				return ;
        			}
        		}
        		else
        		{
        			return;
        		}
        	}
        	
        	salehead.printnum++;
    		AccessDayDB.getDefault().updatePrintNum(salehead.syjh, String.valueOf(salehead.fphm), String.valueOf(salehead.printnum));
    		if(null != salepay)
    		{
    			SalePayDef salepayS = null;
    			HashMap<String,String> queryFlag = null;
    			SuNingPayService sn = new SuNingPayService();
    			HashMap<String,String> mapN = new HashMap<String,String>();
    			ProgressBox pb = new ProgressBox();
    			// 获取请求地址
    			mapN =sn.parseIni();
    			String printFlag = mapN.get("printFlag").toString();
    			for(int i =0;i<salepay.size();i++)
    			{
    				salepayS = (SalePayDef) salepay.elementAt(i);
    				//重打阿里签购单
    				if("SUNINGSALE".equals(salepayS.batch)|"SUNINGSALEBCK".equals(salepayS.batch)|"SUNINGSALEBCKB".equals(salepayS.batch))
    				{
    					 queryFlag =sn.query(salepayS.payno, mapN); 
    	    			 pb.setText(Language.apply("现在正在重打印签购单,请等待....."));
    	    			
    	    			 SuNingPrintMode spm = new SuNingPrintMode();
    	    				//打印签购单
    	    				if(!spm.suNingPrint(queryFlag,salehead,salegoods,salepay,printFlag))
    	    				{
    	    					new MessageBox(Language.apply("打印签购单失败"));
    	    					return;
    	    				}
    	 					pb.close();
    	 				}
    	     			
    				}
    			}
    		
        }
        catch (Exception er)
        {
            er.printStackTrace();
        }
    }
    
    public void printSaleTicket(SaleHeadDef vsalehead ,Vector vsalegoods ,Vector vsalepay,boolean isRed)
    {
    	// 打印小票前先查询满赠信息
		try
    	{
			// 联网获取赠送打印清单
	    	DataService dataservice = (DataService)DataService.getDefault();
	    	Vector gifts = dataservice.getSaleTicketMSInfo(vsalehead,vsalegoods,vsalepay);
	    	SaleBillMode.getDefault().setSaleTicketMSInfo(vsalehead,gifts);
	    	
			// 检查是否需要重打印赠品联授权
	    	boolean bok = true;
			if (vsalehead.printnum > 0 && SaleBillMode.getDefault().needMSInfoPrintGrant())
			{
				if (GlobalInfo.posLogin.priv.charAt(1) != 'Y')
				{
					OperUserDef staff = DataService.getDefault().personGrant(Language.apply("重打印赠券授权"));
		    		
		        	if (staff == null || staff.priv.charAt(1) != 'Y')
		        	{
		        		new MessageBox(Language.apply("此交易存在赠券或者赠品\n该审批员无重打印赠品或者赠券权限"));
		        		bok = false;
		        	}
				}
			}
			if (!bok)
			{
				SaleBillMode.getDefault().setSaleTicketMSInfo(vsalehead,null);
			}
    	}
		catch(Exception er)
    	{
    		er.printStackTrace();
    	}

		// 打印小票
    	try
    	{
    		if (vsalehead != null && vsalegoods != null && vsalepay != null)
            {
    			SaleBillMode.getDefault().setTemplateObject(vsalehead, vsalegoods, vsalepay);
    			SaleBillMode.getDefault().printBill();
            }
    		else
    		{
    			new MessageBox(Language.apply("未发现小票对象，不能打印\n或\n打印模版读取失败"));
    		}
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    }
    
    //******************************************红冲版块***************************************************
    
    public boolean saleRedQuash()
    {
        try
        {
            if (!checkSaleRedQuash())
            {
                return false;
            }

            if (!setSaleRedQuash())
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

    protected boolean checkSaleRedQuash()
    {
        try
        {
            if (salehead == null)
            {
            	new MessageBox(Language.apply("小票主单未找到,不能红冲"), null, false);
            	return false;
            }
            
            // 查找是否是今天的小票,不是今天的小票不能红冲
            if (salehead.rqsj != null)
            {
                String[] rqsjtwo = salehead.rqsj.split(" ");
                ManipulateDateTime dt = new ManipulateDateTime();

                if (dt.compareDate(rqsjtwo[0], dt.getDateBySlash()) != 0)
                {
                    new MessageBox(Language.apply("该笔交易不是今天的交易,不能红冲"), null, false);

                    return false;
                }
            }
            else
            {
                new MessageBox(Language.apply("该笔交易日期有误,不能红冲"), null, false);

                return false;
            }
            
            // 检查发票是否打印完,打印完未设置新发票号则不能交易
            if (Printer.getDefault().getSaleFphmComplate())
            {
            	return false;
            }

            if (!SellType.VALIDTYPE(salehead.djlb))
            {
                new MessageBox(Language.apply("交易类型无效,不能红冲"), null, false);

                return false;
            }

            // 该笔交易是红冲交易不能红冲
            if (SellType.ISHC(salehead.djlb))
            {
                new MessageBox(Language.apply("该笔交易是红冲交易,不能红冲"), null, false);

                return false;
            }

            // 该笔交易是取消交易不能红冲
            if (SellType.ISClEAR(salehead.djlb))
            {
                new MessageBox(Language.apply("该笔交易是取消交易,不能红冲"), null, false);

                return false;
            }
/*			不控制
            // 该笔交易是预销售不能红冲
            if (SellType.ISPREPARE(salehead.djlb))
            {
                new MessageBox("该笔交易是预销售,不能红冲", null, false);

                return false;
            }

            // 该笔交易是预售提货不能红冲
            if (SellType.ISPREPARETAKE(salehead.djlb))
            {
                new MessageBox("该笔交易是预售提货,不能红冲", null, false);

                return false;
            }
*/
            // 该笔交易已经红冲不能在次红冲
            if (salehead.hcbz == 'Y')
            {
                new MessageBox(Language.apply("该笔交易已经红冲,不能再次红冲"), null, false);

                return false;
            }
            
            // 检查哪些付款方式存在是不让红冲
            if (GlobalInfo.sysPara.HCcontrol != null && GlobalInfo.sysPara.HCcontrol.length() > 0)
            {
            	String[] paylimit = GlobalInfo.sysPara.HCcontrol.split(";");
            	String key = "";
            	String key1 = "NOHC|ALL|";
            	String linekey = "";
            	if (SellType.ISSALE(salehead.djlb))
            	{
            		key = "NOHC|SALE|";
            	}
            	else
            	{
            		key = "NOHC|BACK|";
            	}
            	
            	for (int i = 0 ; i < paylimit.length; i++)
            	{
            		String line = paylimit[i];
            		if (line!= null && (line.indexOf(key) >= 0))
            		{
            			linekey = line.substring(10)+",";
            		}
            		
            		if (line!= null && (line.indexOf(key1) >= 0))
            		{
            			linekey = line.substring(9)+",";
            		}
            		if (linekey.length() > 0)
            		{
            			linekey = ","+linekey;
            			for (int j = 0; j < salepay.size(); j++)
            			{
            				SalePayDef spd = (SalePayDef)salepay.get(j);
            				if (linekey.indexOf(","+spd.paycode+",") >=0)
            				{
            					new MessageBox(Language.apply("红冲小票中存在付款方式[{0}]\n不允许红冲!",new Object[]{spd.payname}), null, false);
            					return false;
            				}
            			}
            		}
            	}
           }
            
	        // 退打印券的退货交易,不允许红冲
            if (SellType.ISBACK(salehead.djlb) && GlobalInfo.sysPara.calcfqbyreal == 'A')
            {
	    		for (int i = 0; i < salepay.size(); i++)
	    		{
	    			SalePayDef spd = (SalePayDef)salepay.get(i);
			    	if (GlobalInfo.sysPara.fjkyetype != null && !GlobalInfo.sysPara.fjkyetype.equals(""))
			    	{
			    		String s[] = GlobalInfo.sysPara.fjkyetype.split(";");
			    		for (int j=0;j<s.length;j++)
			    		{
			    			String p[] = s[j].split("=");
			    			if (spd.paycode.trim().equals(p[0].trim()) && p.length >= 2)
			    			{
			    				//T为退打印券的付款方式，后台根据这个付款方式，生成退券
			    				String t[] = p[1].split("\\|");
			    				if (t[0].trim().equals("T"))
			    				{
			    					 new MessageBox(Language.apply("该退货交易退了打印纸券,不允许红冲!\n请通过销售交易收回打印纸券"), null, false);
			    					 return false;
			    				}
			    			}
			    		}
			    	}
	    		}
            }
            
	        // 新coupon付款方式
            /**
            if (SellType.ISBACK(salehead.djlb) && GlobalInfo.sysPara.calcfqbyreal == 'A')
            {
            	String line1 = "";
            	for(int i = 0 ; i < ConfigClass.CustomPayment.size(); i++)
            	{
            		String line = (String) ConfigClass.CustomPayment.elementAt(i);
            		if (line.indexOf("PaymentCoupon") >= 0)
            		{
            			line1 = line;
            			break;
            		}
            	}
            	
            	if (line1.length() > 0)
            	{
		    		for (int i = 0; i < salepay.size(); i++)
		    		{
		    			SalePayDef spd = (SalePayDef)salepay.get(i);
		    			if (line1.indexOf(spd.paycode) >=0)
		    			{
		    				new MessageBox("该退货交易退了打印纸券,不允许红冲!\n请通过销售交易收回打印纸券", null, false);
		    				return false;
		    			}
		    		}
            	}
            }*/
            
            //授权移动到确认询问以后
            //if (!isGrantRedClear())
            //{
            //	return false;
            //}                       

            return true;
        }
        catch (Exception ex)
        {
        	ex.printStackTrace();
        	
            new MessageBox(Language.apply("检查红冲时发生未知异常,红冲失败"), null, false);

            return false;
        }
        finally
        {
            GlobalInfo.dayDB.resultSetClose();
        }
    }
    
    public boolean mzkRedClearMsg(int flag)
    {
    	if (flag ==  1)
    	{
 			if (new MessageBox(Language.apply("该小票有储值卡(券)的付款,红冲会对相应的卡券冲减记帐\n该操作需要授权,你确定要继续红冲吗？"),null,true).verify() != GlobalVar.Key1)
 			{
 				return false;
 			}
    	}
    	
    	return true;
    }
    
    //是否允许授权红冲
    public boolean isGrantRedClear()
    {
    	OperUserDef staff = null;
    	
    	// 授权红冲
    	if (GlobalInfo.posLogin.priv.charAt(0) != 'Y')
        {
        	staff = DataService.getDefault().personGrant();
        	if (staff == null) return false;
        	
        	if (staff.priv.charAt(0) != 'Y')
        	{
        		new MessageBox(Language.apply("该收银员无红冲权限,不能红冲"));
        		return false;
        	}
        	
			String log = Language.apply("授权红冲, 小票号:{0}  授权工号:",new Object[]{salehead.fphm + " "}) + staff.gh;
			AccessDayDB.getDefault().writeWorkLog(log);
        }
    	
        // 检查哪些付款方式存在时需要授权
    	boolean grant = false;
        if (GlobalInfo.sysPara.HCcontrol != null && GlobalInfo.sysPara.HCcontrol.length() > 0)
        {
        	String[] paylimit = GlobalInfo.sysPara.HCcontrol.split(";");
        	String key = "";
        	String key1 = "GRANT|ALL|";
        	String linekey = "";
        	if (SellType.ISSALE(salehead.djlb))
        	{
        		key = "GRANT|SALE|";
        	}
        	else
        	{
        		key = "GRANT|BACK|";
        	}
        	
        	for (int i = 0 ; i < paylimit.length; i++)
        	{
        		String line = paylimit[i];
        		if (line!= null && (line.indexOf(key) >= 0))
        		{
        			linekey = line.substring(11)+",";
        		}
        		
        		if (line!= null && (line.indexOf(key1) >= 0))
        		{
        			linekey = line.substring(10)+",";
        		}
        	}
        	
        	if (linekey.length() > 0)
        	{
        		linekey = ","+linekey;
        		for (int i = 0; i < salepay.size(); i++)
	    		{
        			SalePayDef spd = (SalePayDef)salepay.get(i);
        			if (linekey.indexOf(","+spd.paycode+",") >=0)
        			{
        				if (!mzkRedClearMsg(2)) return false;
        				grant = true;
        				break;
        			}
	    		}
        	}
        }

    	// 授权红冲储值卡
    	if (isMzkSell())
    	{
    		if (!mzkRedClearMsg(1)) return false;
 			
    		grant = true;
    	}
    	
    	if (grant)
    	{
	 		if (GlobalInfo.sysPara.ismzkgh != null && !GlobalInfo.sysPara.ismzkgh.trim().equals(""))
	 		{
	 			String s[] = GlobalInfo.sysPara.ismzkgh.split(",");
	 			boolean ok = false;
	 			for (int i=0;i<s.length;i++)
	 			{
	 				if (s[i].trim().equals(GlobalInfo.posLogin.gh) || (staff != null && s[i].trim().equals(staff.gh)))
	 				{
	 					ok = true;
	 					break;
	 				}
	 			}
	 			if (!ok)
	 			{
	 	        	staff = DataService.getDefault().personGrant(Language.apply("红冲面值卡授权"));
	 	        	if (staff == null) return false;
	 	        	
	 	 			for (int i=0;i<s.length;i++)
	 	 			{
	 	 				if (s[i].trim().equals(staff.gh))
	 	 				{
	 	 					ok = true;
	 	 					break;
	 	 				}
	 	 			}
	 	 			if (!ok)
	 	 			{
	 	        		new MessageBox(Language.apply("该收银员无红冲储值卡(券)权限,不能红冲"));
	 	        		return false;
	 	        	}
	 	        	
	 				String log = Language.apply("授权红冲储值卡(券), 小票号:{0}授权工号:", new Object[]{salehead.fphm + " "}) + staff.gh;
	 				AccessDayDB.getDefault().writeWorkLog(log); 				
	 			}
	 		}
    	}
    	
 		// 该笔交易不是当班收银员所销售不能红冲
        if (!GlobalInfo.posLogin.gh.equals(salehead.syyh) && !(GlobalInfo.posLogin.operrange == 'Y' || (staff != null && staff.operrange == 'Y')))
        {
			staff = DataService.getDefault().personGrant();
			if (staff == null) return false;
			if (staff.operrange != 'Y')
        	{
    			new MessageBox(Language.apply("该笔交易不是当班收银员的销售,不能红冲"), null, false);
    			return false;
        	}
			
			String log = Language.apply("授权红冲当班收银员小票,小票号:{0}  授权工号:", new Object[]{salehead.fphm + " "}) + staff.gh;
			AccessDayDB.getDefault().writeWorkLog(log);
        }
        
    	return true;
    }
    
    protected boolean isMzkSell()
    {
    	PayModeDef pmd= null;
    	
    	try
    	{
    		for (int i = 0; i < salepay.size(); i++)
    		{
    			SalePayDef spd = (SalePayDef)salepay.get(i);
    			
    			if ((pmd = DataService.getDefault().searchMainPayMode(spd.paycode)) == null) return false;
    			
    			// 储值卡付款要授权
    			if (pmd.type == '4') return true;
    			
    			// 电子券和打印券也需要授权
		    	if (GlobalInfo.sysPara.fjkyetype != null && !GlobalInfo.sysPara.fjkyetype.equals(""))
		    	{
		    		String s[] = GlobalInfo.sysPara.fjkyetype.split(";");
		    		for (int j=0;j<s.length;j++)
		    		{
		    			String p[] = s[j].split("=");
		    			if (spd.paycode.trim().equals(p[0].trim()) && p.length >= 2)
		    			{
		    				return true;
		    			}
		    		}
		    	}
    		}
    		
    		return false;
    	}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    		return false;
    	}
    }
    
    protected boolean setSaleRedQuash()
    {
        ProgressBox pb = null;
        SaleHeadDef tempSaleHead = null;
        Vector tempSaleGoods = null;
        Vector tempSalePay = null;
        boolean retok = false;
        
        try
        {
            MessageBox me = new MessageBox(Language.apply("你确定要将此笔交易红冲吗?"), null, true);
            if (me.verify() != GlobalVar.Key1)
            {
                return false;
            }
            
            // 是否允许需要授权
            if (!isGrantRedClear())
            {
            	return false;
            }

            // 向小票商品库中插入红冲销售
            pb = new ProgressBox();
            pb.setText(Language.apply("正在读取原交易数据,请等待..."));
            
            // 查询小票主单
            if (salehead != null)
            {
            	tempSaleHead = (SaleHeadDef)salehead.clone(); 
            		
            	tempSaleHead.fphm = GlobalInfo.syjStatus.fphm;
            	tempSaleHead.djlb = SellType.getReFlush(tempSaleHead.djlb);//(char) (tempSaleHead.djlb + 1);
            	
            	tempSaleHead.syjh = ConfigClass.CashRegisterCode;
            	tempSaleHead.mkt = GlobalInfo.sysPara.mktcode;
            	tempSaleHead.bc   = GlobalInfo.syjStatus.bc;
            	tempSaleHead.syyh = GlobalInfo.posLogin.gh;
            	tempSaleHead.rqsj = ManipulateDateTime.getCurrentDateTime();
            	tempSaleHead.netbz = 'N';			
            	tempSaleHead.printbz = 'N';
            	tempSaleHead.hcbz = 'Y';
            	tempSaleHead.printnum = 0;
                GlobalInfo.dayDB.resultSetClose();
            }
            else
            {
            	pb.close();pb = null;
                new MessageBox(Language.apply("小票主单未找到,红冲失败"), null, false);

                return false;
            }

            //查询小票明细
            if (salegoods != null && salegoods.size() > 0)
            {
            	tempSaleGoods = (Vector)salegoods.clone();
            	
                for (int i = 0;i < tempSaleGoods.size() ;i++)
                {
                    SaleGoodsDef sgd = (SaleGoodsDef)tempSaleGoods.get(i);
                    sgd.ysyjh = sgd.syjh;
                    sgd.yfphm = sgd.fphm;
                    sgd.syjh = ConfigClass.CashRegisterCode;
                    sgd.fphm = GlobalInfo.syjStatus.fphm;
                    tempSaleHead.ysyjh = sgd.ysyjh;
                    tempSaleHead.yfphm = String.valueOf(sgd.yfphm);
                }
            }
            else
            {
                new MessageBox(Language.apply("小票明细未找到,红冲失败"), null, false);

                return false;
            }

            // 查询销售付款明细
            if (salepay != null && salepay.size() > 0)
            {
            	tempSalePay = (Vector)salepay.clone();
            	
            	for (int i = 0;i < tempSalePay.size();i++)
                {
                    SalePayDef spd = (SalePayDef)tempSalePay.get(i);
                    spd.syjh = ConfigClass.CashRegisterCode;
                    spd.fphm = GlobalInfo.syjStatus.fphm;
                }

                GlobalInfo.dayDB.resultSetClose();
            }
            else
            {
            	pb.close();pb = null;
                new MessageBox(Language.apply("小票付款明细,红冲失败"), null, false);

                return false;
            }
            
            // 创建付款记账对象
            if (!createPayAssistant(tempSalePay,tempSaleHead))
            {
            	pb.close();pb = null;
                new MessageBox(Language.apply("付款对象生成错误,红冲失败"));
            	
            	return false;
            }
            
            // 校验数据
	        if (!AccessDayDB.getDefault().checkSaleData(tempSaleHead,tempSaleGoods,tempSalePay))
	        {
	        	pb.close();pb = null;
	            new MessageBox(Language.apply("交易数据校验错误!"));
	
	            return false;
	        }
	        
	        // 发送当前红冲销售的小票以计算扣回
	        if (GlobalInfo.sysPara.refundByPos != 'N' && SellType.ISSALE(salehead.djlb))
	        {
	        	char oldbc = tempSaleHead.bc;
	        	try
	        	{
		        	// jdfhdd标记当前发送的是用于计算扣回的小票信息
	        		tempSaleHead.bc = '#';
		        	String oldfhdd = tempSaleHead.jdfhdd;
		        	tempSaleHead.jdfhdd = "KHINV";

		        	// 发送扣回小票
			    	if (DataService.getDefault().doRefundExtendSaleData(tempSaleHead, tempSaleGoods, tempSalePay, null) != 0)
					{
			    		tempSaleHead.jdfhdd = oldfhdd;
			    		if (new MessageBox(Language.apply("发送扣回小票失败，可能导致积分和券错误！\n是否强行红冲"),null,true).verify() != GlobalVar.Key1)
			    		{
			    			return false;
			    		}
					}
			    	else
			    	{
			    		// 恢复标记
				    	tempSaleHead.jdfhdd = oldfhdd;
				    	
				    	// 获取小票扣回金额
				    	RefundMoneyDef rmd = new RefundMoneyDef();
				    	if (!NetService.getDefault().getRefundMoney(tempSaleHead.mkt,tempSaleHead.syjh,tempSaleHead.fphm,rmd))
						{
				    		if (new MessageBox(Language.apply("小票扣回计算失败，可能计算积分和券错误！\n是否强行红冲"),null,true).verify() != GlobalVar.Key1)
				    		{
				    			return false;
				    		}
						}
				    	else
				    	{
					    	// 有扣回不允许红冲
					    	double refundTotal = rmd.jfkhje + rmd.fqkhje + rmd.qtkhje + rmd.jdxxfkje;
					    	if (refundTotal > 0) 
					    	{
					    		new MessageBox(Language.apply("本笔交易存在扣回，请使用退货功能"));
					    		return false;
					    	}
				    	}
			    	}
	        	}
	        	catch(Exception er)
	        	{
	        		er.printStackTrace();
	        	}
	        	finally
	        	{
	        		tempSaleHead.bc = oldbc;
	        	}
	        }
	        
            // 付款记账
            pb.setText(Language.apply("正在记账付款数据,请等待....."));
            if (!saleCollectAccountPay(tempSalePay,tempSaleHead))
            {
                new MessageBox(Language.apply("付款数据记账错误,红冲失败"));

                // 记账失败,及时把冲正发送出去
                pb.setText(Language.apply("正在发送付款冲正数据,请等待....."));
                CreatePayment.getDefault().sendAllPaymentCz();
                pb.close();pb = null;
                
            	return false;
            }
            
            // 银联记账可改变付款金额，从而由于误操作导致付款金额发生变化，因此重新检查一次小票平衡关系
	        if (!AccessDayDB.getDefault().checkSaleData(tempSaleHead,tempSaleGoods,tempSalePay))
	        {
	        	pb.close();pb = null;
	            new MessageBox(Language.apply("付款记账后交易数据校验错误!"));
	
	            return false;
	        }
	        
            // 开始插入小票
            pb.setText(Language.apply("正在写入红冲交易,请等待..."));
            if (!AccessDayDB.getDefault().writeSale(tempSaleHead,tempSaleGoods,tempSalePay))
            {
                new MessageBox(Language.apply("写入红冲小票错误,红冲失败"), null, false);

                // 记账失败,及时把冲正发送出去
                pb.setText(Language.apply("正在发送付款冲正数据,请等待....."));
                CreatePayment.getDefault().sendAllPaymentCz();
                pb.close();pb = null;
                
                return false;
            }
            
            // 小票已写盘,本次交易就要认为完成,即使后续处理异常也要返回成功
            retok = true;
            
            // 删除付款记账冲正
            pb.setText(Language.apply("正在清除付款冲正,请等待......"));
            if (!saleCollectAccountClear())
            {
                new MessageBox(Language.apply("付款冲正清除有错误"));
            }
            
            // 将原小票更新
            salehead.hcbz = 'Y';
            pb.setText(Language.apply("正在更新原交易数据,请等待..."));
            if (!GlobalInfo.dayDB.executeSql("update salehead set hcbz = 'Y' where syjh = '" + ConfigClass.CashRegisterCode + "' and  fphm = " + salehead.fphm))
            {
                new MessageBox(Language.apply("更改原小票红冲标志错误"));
            }
            
            // 上传红冲小票
            pb.setText(Language.apply("正在发送红冲交易,请等待..."));
            DataService.getDefault().sendSaleData(tempSaleHead,tempSaleGoods,tempSalePay);

            // 发送当前收银状态
            pb.setText(Language.apply("正在发送收银交易汇总,请等待..."));
            DataService.getDefault().sendSyjStatus();
            
            //打印红冲小票
            if (GlobalInfo.sysPara.isHcPrintBill == 'Y')
            {
            	pb.setText(Language.apply("正在打印红冲小票,请等待..."));
            	printSaleTicket(tempSaleHead,tempSaleGoods,tempSalePay,true);
            }
            
            // 红冲完成
            pb.close();pb = null;
            new MessageBox(Language.apply("该笔交易已红冲成功!"), null, false);
            
            return retok;
        }
        catch (Exception ex)
        {    
            ex.printStackTrace();
            
            GlobalInfo.dayDB.rollbackTrans();

            new MessageBox(Language.apply("红冲过程中发生异常!\n\n")+ ex.getMessage());
            
            return retok;
        }
        finally
        {
            if (pb != null)
            {
                pb.close();
                pb = null;
            }
            
        	if (tempSaleHead != null)
        	{
        		tempSaleHead = null;
        	}
        	
            if (tempSaleGoods != null)
            {
            	tempSaleGoods.clear();
            	tempSaleGoods = null;
            }
            
            if (tempSalePay != null)
            {
            	tempSalePay.clear();
            	tempSalePay = null;
            }
             
            GlobalInfo.dayDB.resultSetClose();
        }
    }
    
    protected boolean createPayAssistant(Vector newSalePay,SaleHeadDef newSaleHead)
	{
    	SalePayDef sp = null;
    	
    	if (payAssistant == null)
    	{
    		payAssistant = new Vector();
    	}
    	else
    	{
    		payAssistant.removeAllElements();
    	}
    	
    	for (int i=0;i<newSalePay.size();i++)
    	{
    		sp = (SalePayDef)newSalePay.elementAt(i);
    		
    		// 找零方式不创建付款对象
    		if (sp.flag == '2') continue;
    		
    		// 标记本付款方式未记账,必须要在付款对象的集中记账方法collectAccountPay中进行记账处理,这很重要
    		sp.batch = "";
    		
    		// 创建付款对象
    		Payment pay = CreatePayment.getDefault().createPaymentBySalePay(sp,newSaleHead);
    		if (pay == null) return false;
    		
    		payAssistant.add(pay);
    	}
    	
    	return true;
	}

    protected boolean saleCollectAccountPay(Vector newSalePay,SaleHeadDef newSaleHead)
    {
    	Payment p = null;
    	boolean czsend = true;
    	
    	// 红冲交易的付款要先记账扣回行，再记账付款行,以保证储值卡等付款账务记账顺序反向
    	if (SellType.ISHC(newSaleHead.djlb))
    	{
    		for (int i=payAssistant.size()-1;i>=0;i--)
    		{
	    		p = (Payment)payAssistant.elementAt(i);
	    		if (p == null) continue;
	    		
				// 第一次记账前先检查是否有冲正需要发送
				if (czsend)
				{
					czsend = false;
					if (!p.sendAccountCz()) return false;
				}
				
				// 付款记账
	    		if (!p.collectAccountPay())
	    		{
	    			return false;
	    		}
    		}
    	}
    	else
    	{
    		for (int i=0;i<payAssistant.size();i++)
    		{
    			p = (Payment)payAssistant.elementAt(i);
	    		if (p == null) continue;
	    		
				// 第一次记账前先检查是否有冲正需要发送
				if (czsend)
				{
					czsend = false;
					if (!p.sendAccountCz()) return false;
				}
				
				// 付款记账
	    		if (!p.collectAccountPay())		
	    		{
	    			return false;
	    		}
    		}
    	}
    	
    	return true;
    }    
    
    protected boolean saleCollectAccountClear()
    {
    	Payment p = null;
    	boolean ok = true;
    	
    	for (int i=0;i<payAssistant.size();i++)
    	{
    		p = (Payment)payAssistant.elementAt(i);
    		if (p == null) continue;
    		
    		// 取消冲正,取消冲正失败继续取消剩余的冲正
    		if (!p.collectAccountClear()) ok = false;
    	}
    	
    	return ok;
    }   
    
    //******************************************退货版块***************************************************
    
    //查询退货小票信息
    public void getBackSaleInfo(String code, Text txtTicketCode,StyledText txtSaleTime, StyledText txtSyy,StyledText txtSaleType,Table tabTicketDeatilInfo, Table tabPay,StyledText txtMemberCardCode,StyledText txtGrantCardCode,StyledText txtShouldInceptMoney,StyledText txtAgioMoney,StyledText txtFactInceptMoney,StyledText txtGiveChangeMoney,StyledText txtSpoilageMoney, Label lblNet,Group group,StyledText khje)
    {
    	String vyyyh = null;
        String vbarcode = null;
        String vname = null;
        String vpayname = null;
        String vpayno = null;
        ProgressBox pb = null;
        
    	try
    	{
    		salehead = new SaleHeadDef();
    		salegoods = new Vector();
    		salepay = new Vector();
    		
    		pb = new ProgressBox();
    		pb.setText(Language.apply("开始查找退货小票操作....."));
    		
    		if (! DataService.getDefault().getBackSaleInfo("",code,salehead,salegoods,salepay))
    		{
    			salehead = null;
    			salegoods.clear();
    			salegoods = null;
    			salepay.clear();
    			salepay = null;
    			pb.close();
                pb = null;
    			this.clear(txtSaleTime, txtSyy, txtSaleType, tabTicketDeatilInfo, tabPay, txtMemberCardCode, txtGrantCardCode, txtShouldInceptMoney, txtAgioMoney, txtFactInceptMoney, txtGiveChangeMoney, txtSpoilageMoney, lblNet, group);
    			return ;
    		}
    		
    		 pb.close();
             pb = null;
             
    		if (salehead.syjh != null)
        	{
        		group.setText(Language.apply("收银机:") + salehead.syjh);
        	}
       
        	if (salehead.rqsj != null)
        	{
        		txtSaleTime.setText(salehead.rqsj);
        	}
        
        	if (salehead.syyh != null)
        	{
        		txtSyy.setText(salehead.syyh);
        	}
        
        	if (salehead.djlb != null && salehead.djlb.length() > 0)
        	{
        		txtSaleType.setText(SellType.getDefault().typeExchange(salehead.djlb,salehead.hhflag,salehead));
        	}
        	
        	if (salehead.hykh != null)
        	{
        		txtMemberCardCode.setText(salehead.hykh);
        	}
        	
        	if (salehead.sqkh != null)
        	{
        		txtGrantCardCode.setText(salehead.sqkh);
        	}
            
        	
            txtShouldInceptMoney.setText(ManipulatePrecision.doubleToString(salehead.ysje));
            txtAgioMoney.setText(ManipulatePrecision.doubleToString(salehead.hjzke));
            txtFactInceptMoney.setText(ManipulatePrecision.doubleToString(salehead.sjfk));
            txtGiveChangeMoney.setText(ManipulatePrecision.doubleToString(salehead.zl));
            txtSpoilageMoney.setText(ManipulatePrecision.doubleToString(salehead.sswr_sysy + salehead.fk_sysy));
            
            
            tabTicketDeatilInfo.removeAll();
            
            for (int i = 0;i < salegoods.size();i++)
            {
            	SaleGoodsDef sgd = (SaleGoodsDef)salegoods.get(i);
            	
            	if (sgd.yyyh != null)
            	{
            		vyyyh = sgd.yyyh; 
            	}
            	else
            	{
            		vyyyh = "";
            	}
            	
            	if (sgd.barcode != null)
            	{
            		vbarcode = sgd.barcode;
            	}
            	else
            	{
            		vbarcode = "";
            	}
            	
            	if (sgd.name != null)
            	{
            		vname = sgd.name;
            	}
            	else
            	{
            		vname = "";
            	}
            	
                String[] saleinfo = {vyyyh,vbarcode,vname,ManipulatePrecision.doubleToString(sgd.jg),ManipulatePrecision.doubleToString(sgd.sl,4,1,true),ManipulatePrecision.doubleToString(sgd.hjzk),ManipulatePrecision.doubleToString(ManipulatePrecision.sub(sgd.hjje,sgd.hjzk))};
                TableItem item = new TableItem(tabTicketDeatilInfo, SWT.NONE);
                item.setText(saleinfo);
            }
            
            tabPay.removeAll();

            double je = 0;
            for (int i = 0;i < salepay.size();i++)
            {
            	SalePayDef spd = (SalePayDef)salepay.get(i);
            	
            	
            	spd.payname = checkName(spd);
            	vpayname = spd.payname;
            	
            	if (spd.payno != null)
            	{
            		vpayno = spd.payno;
            	}
            	else
            	{
            		vpayno = "";
            	}
            	         	
        		if (AccessDayDB.getDefault().isBuckleMoney(spd))
        		{
        			je += spd.je; 
        		}
            		
                String[] payinfo = {vpayname,vpayno,ManipulatePrecision.doubleToString(spd.ybje)};
                TableItem item = new TableItem(tabPay, SWT.NONE);
                item.setText(payinfo);
            }
            
            khje.setText(ManipulatePrecision.doubleToString(je));
            
            shbillno = code;
            
            //显示功能提示
    		GlobalInfo.statusBar.setHelpMessage(Language.apply("'付款键'切换商品付款,'确认键'退货小票"));
    	}
    	catch (Exception ex)
    	{
    		salehead = null;
			salegoods.clear();
			salegoods = null;
			salepay.clear();
			salepay = null;
			if (pb != null)
			{
				pb.close();
				pb = null;
			}
    		ex.printStackTrace();
    	}
    	finally
    	{
    		isth = false;
    	}
    }
    
    public String checkName(SalePayDef spd)
    {
    	String vpayname = "";
    	
    	if (spd.payname != null)
    	{
    		vpayname = spd.payname;
    	}
    	else
    	{
    		vpayname = "";
    	}
    	
    	String type = "";
    	if (spd.paycode.equals("0500"))
    	{
    		switch(Convert.toInt(spd.idno))
    		{
    		case 1:
    			type = "A";
    			break;
    		case 2:
    			type = "B";
    			break;
    		case 3:
    			type = "F";
    			break;
    		default:
    			type = "";
    		}
    	}
    	
    	vpayname += type;
    	
    	return vpayname;
    }
        
    public boolean saleBackSale()
    {
    	ProgressBox pb = null;
        SaleHeadDef tempSaleHead = null;
        Vector tempSaleGoods = null;
        Vector tempSalePay = null;
        
    	try
    	{
    		if (isth)
    		{
    			 new MessageBox(Language.apply("该笔交易已经退货,不能再次退货"), null, false);
    			 return false;
    		}
    		if (salehead == null || salegoods == null || salepay == null)
    		{
    			 new MessageBox(Language.apply("没有退货信息,不能退货"), null, false);
    			 return false;
    		}
    		if (shbillno == null)
    		{
	   			 new MessageBox(Language.apply("请重新查询退货小票信息"), null, false);
				 return false;
    		}
    		
            // 检查发票是否打印完,打印完未设置新发票号则不能交易
            if (Printer.getDefault().getSaleFphmComplate())
            {
            	return false;
            }

    		// 检查授权
    		if (!isGrantBack()) return false;
    		MessageBox me = new MessageBox(Language.apply("你确定要将此笔交易退货吗?"), null, true);
            if (me.verify() != GlobalVar.Key1)
            {
                return false;
            }
    		
            // 开钱箱
        	CashBox.getDefault().openCashBox();

        	//
            pb = new ProgressBox();
            
            // 向小票商品库中插入退货销售
            pb.setText(Language.apply("正在读取原交易数据,请等待..."));
            
            tempSaleHead = (SaleHeadDef)salehead.clone(); 
    		
        	tempSaleHead.fphm = GlobalInfo.syjStatus.fphm;
        
        	tempSaleHead.syjh = ConfigClass.CashRegisterCode;
        	tempSaleHead.mkt = GlobalInfo.sysPara.mktcode;
        	tempSaleHead.bc   = GlobalInfo.syjStatus.bc;
        	tempSaleHead.syyh = GlobalInfo.posLogin.gh;
        	tempSaleHead.rqsj = ManipulateDateTime.getCurrentDateTime();
        	tempSaleHead.netbz = 'N';			
        	tempSaleHead.printbz = 'N';
        	tempSaleHead.hcbz = 'N';
        	tempSaleHead.printnum = 0;
        	
        	tempSaleGoods = (Vector)salegoods.clone();
        	
            for (int i = 0;i < tempSaleGoods.size() ;i++)
            {
                SaleGoodsDef sgd = (SaleGoodsDef)tempSaleGoods.get(i);
                sgd.ysyjh = sgd.syjh;
                sgd.syjh = ConfigClass.CashRegisterCode;
                sgd.yfphm = sgd.fphm;
                sgd.fhdd = shbillno;
                sgd.fphm = GlobalInfo.syjStatus.fphm;
                tempSaleHead.ysyjh = sgd.ysyjh;
                tempSaleHead.yfphm = String.valueOf(sgd.yfphm);
            }
            
            tempSalePay = (Vector)salepay.clone();
        	
        	for (int i = 0;i < tempSalePay.size();i++)
            {
                SalePayDef spd = (SalePayDef)tempSalePay.get(i);
                spd.fphm = GlobalInfo.syjStatus.fphm;
                spd.syjh = ConfigClass.CashRegisterCode;
            }
        	
        	// 创建付款记账对象
            if (!createPayAssistant(tempSalePay,tempSaleHead))
            {
            	pb.close();pb = null;
                new MessageBox(Language.apply("付款对象生成错误,后台退货失败"));
            	
            	return false;
            }
            
            // 校验数据平衡
	        if (!AccessDayDB.getDefault().checkSaleData(tempSaleHead,tempSaleGoods,tempSalePay))
	        {
	        	pb.close();pb = null;
	            new MessageBox(Language.apply("交易数据校验错误!"));
	
	            return false;
	        }
	        
            // 付款记账
            pb.setText(Language.apply("正在记账付款数据,请等待....."));
            if (!saleCollectAccountPay(tempSalePay,tempSaleHead))
            {
                new MessageBox(Language.apply("付款数据记账错误,后台退货失败"));
            	
                // 记账失败,及时把冲正发送出去
                pb.setText(Language.apply("正在发送付款冲正数据,请等待....."));
                CreatePayment.getDefault().sendAllPaymentCz();
                pb.close();pb = null;
                
            	return false;
            }
            
            // 银联记账可改变付款金额，从而由于误操作导致付款金额发生变化，因此重新检查一次小票平衡关系
	        if (!AccessDayDB.getDefault().checkSaleData(tempSaleHead,tempSaleGoods,tempSalePay))
	        {
	        	pb.close();pb = null;
	            new MessageBox(Language.apply("付款记账后交易数据校验错误!"));
	
	            return false;
	        }
	        
            //开始插入小票
            pb.setText(Language.apply("正在写入退货交易,请等待..."));
            if (!AccessDayDB.getDefault().writeSale(tempSaleHead,tempSaleGoods,tempSalePay))
            {
                new MessageBox(Language.apply("写入退货小票错误,退货失败"), null, false);

                // 记账失败,及时把冲正发送出去
                pb.setText(Language.apply("正在发送付款冲正数据,请等待....."));
                CreatePayment.getDefault().sendAllPaymentCz();
                pb.close();pb = null;
                
                return false;
            }
            
	        // 小票已写盘,本次交易就要认为完成,即使后续处理异常也要返回成功
	        isth = true;
	        shbillno = null;
	        
            // 删除付款记账冲正
            pb.setText(Language.apply("正在清除付款冲正,请等待......"));
            if (!saleCollectAccountClear())
            {
                new MessageBox(Language.apply("付款冲正清除有错误!"));
            }
            
            // 上传退货小票
            pb.setText(Language.apply("正在发送退货交易,请等待..."));
            DataService.getDefault().sendSaleData(tempSaleHead,tempSaleGoods,tempSalePay);

            // 发送当前收银状态
            DataService.getDefault().sendSyjStatus();
            
            //打印退货小票
            pb.setText(Language.apply("正在打印退货小票,请等待..."));
            printSaleTicket(tempSaleHead,tempSaleGoods,tempSalePay,true);
            
            // 退货完成
            pb.close();pb = null;
            new MessageBox(Language.apply("该笔交易已退货成功!"), null, false);
           
            // 等待钱箱关闭
            int cnt = 0;
            while(GlobalInfo.sysPara.closedrawer == 'Y' && CashBox.getDefault().getOpenStatus() && cnt < 30)
	        {
	        	Thread.sleep(2000);
	        	
	        	cnt++;
	        }
                        
            // 显示功能提示
    		GlobalInfo.statusBar.setHelpMessage(Language.apply("'付款键'切换商品付款,'确认键'退货小票"));
    		
    		isth = true;
    		return isth;
    	}
    	catch (Exception ex)
    	{
    		if (pb != null)
            {
                pb.close();
                pb = null;
            }
            
            ex.printStackTrace();
            
            GlobalInfo.dayDB.rollbackTrans();

            new MessageBox(Language.apply("退货过程中发生异常!\n\n") + ex.getMessage());
            
            return isth;
    	}
    	finally
    	{
    		if (tempSaleHead != null)
        	{
        		tempSaleHead = null;
        	}
        	
            if (tempSaleGoods != null)
            {
            	tempSaleGoods.clear();
            	tempSaleGoods = null;
            }
            
            if (tempSalePay != null)
            {
            	tempSalePay.clear();
            	tempSalePay = null;
            }
            
            GlobalInfo.dayDB.resultSetClose();
    	}
    }
    
    //是否允许授权退货
    public boolean isGrantBack()
    {
    	if (GlobalInfo.posLogin.privth == 'N' || GlobalInfo.syjDef.isth != 'Y')
        {
        	OperUserDef staff = DataService.getDefault().personGrant();
        	if (staff == null) return false;
        	
        	if (staff.privth == 'N')
        	{
        		new MessageBox(Language.apply("该员工授权卡不能授权退货交易"));
        		return false;
        	}
        	
			String log ="授权退货, 小票号:" + salehead.fphm  + "  授权工号:" + staff.gh;
			AccessDayDB.getDefault().writeWorkLog(log);
        }
    	
    	return true;
    }
    
    //修改小票号
    public boolean modifyInvno(StringBuffer newinvno)
    {
    	if (sql == null)
    	{
    		return false;
    	}
    	
    	if (salehead == null)
    	{
    		return false;
    	}
    	
    	try
    	{
	    	StringBuffer buffer = new StringBuffer();
	    	if (new TextBox().open(Language.apply("请输入新小票号(原小票[") + salehead.syjh + "-" + salehead.fphm + "])", "", Language.apply("请确定输入的新小票号网上不存在,否则可能造成\n\n交易不能正常送网,请慎重修改!"), buffer, 0, 99999999, true, TextBox.IntegerInput))
	    	{
	    		if (Convert.toLong(buffer) == salehead.fphm)
	    		{
	    			return false;
	    		}
	    		
//	    		if (GlobalVar.Key1 != new MessageBox("是否要将原小票[" + salehead.syjh + "-" + salehead.fphm + "]" + "的小票号修改成[" + salehead.syjh + "-" + buffer + "]?\n\n请慎重修改，否则可能造成交易不能正常送网!",null,true).verify())
	    		if (GlobalVar.Key1 != new MessageBox(Language.apply("是否要将原小票[{0}-{1}]的小票号修改成[{2}-{3}]?\n\n请慎重修改，否则可能造成交易不能正常送网!", new Object[]{salehead.syjh,salehead.fphm + " ",salehead.syjh,buffer}),null,true).verify())
	    		{
	    			return false;
	    		}
	    		 
	    		Object obj = sql.selectOneData("select count(*) from SALEHEAD where fphm=" + buffer + " and syjh = '" + salehead.syjh + "'");
	    		if (obj == null)
	    		{
	    			new MessageBox(Language.apply("修改小票号失败!/n/n") + sql.getErrorlog());
	    			return false;
	    		}
	    		
	    		int invcount = Convert.toInt(obj);
	    		
	    		if (invcount >= 1)
	    		{
	    			new MessageBox(Language.apply("小票[{0}-{1}]的已存在,请重新输入小票号!",new Object[]{salehead.syjh,buffer.toString()}));
	    			return false;
	    		}
	    		
	    		sql.beginTrans();
	    		
	       		if (!sql.executeSql("update SALEHEAD set fphm = " + buffer + " where syjh = '" + salehead.syjh + "' and fphm = " + salehead.fphm))
	    		{
	    			new MessageBox(Language.apply("修改单据头的小票号失败!/n/n") + sql.getErrorlog());
	    			return false;
	    		}
	    		
	    		if (!sql.executeSql("update SALEGOODS set fphm = " + buffer + " where syjh = '" + salehead.syjh + "' and fphm = " + salehead.fphm))
	    		{
	    			new MessageBox(Language.apply("修改单据明细的小票号失败!/n/n") + sql.getErrorlog());
	    			return false;
	    		}
	    		
	    		if (!sql.executeSql("update SALEPAY set fphm = " + buffer + " where syjh = '" + salehead.syjh + "' and fphm = " + salehead.fphm))
	    		{
	    			new MessageBox(Language.apply("修改付款方式的小票号失败!/n/n") + sql.getErrorlog());
	    			return false;
	    		}
	    		
	    		sql.commitTrans();
	    		
	    		salehead.fphm = Convert.toLong(buffer);
	    		newinvno.append(String.valueOf(salehead.fphm));
	    		
	    		if (salehead.fphm >= GlobalInfo.syjStatus.fphm)
	    		{
	    			GlobalInfo.syjStatus.fphm = salehead.fphm + 1;
	    		}
	    		
	    		String log = Language.apply("修改小票号,原小票:[{0}-{1}] 新小票:[" ,new Object[]{salehead.syjh,salehead.fphm  + ""})+ salehead.syjh + "-" + buffer + "]";
	    		AccessDayDB.getDefault().writeWorkLog(log);
	    		
	    		return true;
	    	}
    	}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    		return false;
    	}
    	finally
    	{
    		if (sql != null)
    		{
    			sql.rollbackTrans();
    		}
    	}
    	 //

    		 
    	return false;
    }
}
