package custom.localize.Zmjc;

import java.sql.ResultSet;
import java.util.Vector;

import org.eclipse.swt.widgets.Table;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.CashBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.ParaNodeDef;
import com.efuture.javaPos.Struct.RefundMoneyDef;
import com.efuture.javaPos.Struct.SaleCustDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Zmjc_DisplaySaleTicketBS extends DisplaySaleTicketBS
{

    public SaleCustDef saleCust = null;
    
	//红冲
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
            
            //查询销售顾客信息
            //红冲时,暂不考虑顾客信息
            
            
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
            if (!((Zmjc_AccessDayDB)AccessDayDB.getDefault()).writeSale(tempSaleHead,tempSaleGoods,tempSalePay, null))
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
            DataService.getDefault().sendSaleDataCust(tempSaleHead,tempSaleGoods,tempSalePay,null);

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
	
    //退货
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
        	
            //查询销售顾客信息
            //红冲时,暂不考虑顾客信息
        	
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
            if (!((Zmjc_AccessDayDB)AccessDayDB.getDefault()).writeSale(tempSaleHead,tempSaleGoods,tempSalePay,null))
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
            DataService.getDefault().sendSaleDataCust(tempSaleHead,tempSaleGoods,tempSalePay,null);

            // 发送当前收银状态
            DataService.getDefault().sendSyjStatus();
            
            //打印退货小票
            pb.setText(Language.apply("正在打印退货小票,请等待..."));
            printSaleTicket(tempSaleHead,tempSaleGoods,tempSalePay,true);
            
            // 退货完成
            pb.close();pb = null;
            new MessageBox(Language.apply("该笔交易已退货成功!"), null, false);
/*            
            // 等待钱箱关闭
            int cnt = 0;
            while(GlobalInfo.sysPara.closedrawer == 'Y' && CashBox.getDefault().getOpenStatus() && cnt < 30)
	        {
	        	Thread.sleep(2000);
	        	
	        	cnt++;
	        }
*/                        
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
    
    protected boolean getPayDetail(Sqldb sql, String code, Table tabPay)
    {
    	if (super.getPayDetail(sql, code, tabPay))
    	{
    		//商品明细读取成功之后,再顺便把顾客信息取出来
    		getCustDetail(sql, code, tabPay);
    		return true;
    	}
    	return false;
    }
    
    protected boolean getCustDetail(Sqldb sql, String code, Table tabPay)
    {
        ResultSet rs = null;
        
        try
        {
            saleCust = null;
            
            if (sql == null)
            {
                return false;
            }
            
            if ((rs = sql.selectData("select * from SALECUST where fphm = " + code + " ")) != null)
            {
            	boolean ret = false;
            	saleCust = new SaleCustDef();
            	ParaNodeDef node;
            	
                while (rs.next())
                {                	
                	node = new ParaNodeDef();
	            	node.code=CustInfoDef.CUST_SCPASSPORTNO;
	            	node.name = "";
	            	node.value = CommonMethod.isNull(rs.getString(node.code),"");
					saleCust.custAdd(node.code, node);		
					
					node = new ParaNodeDef();
	            	node.code=CustInfoDef.CUST_SCNATIONALITY;
	            	node.name = "";
	            	node.value = CommonMethod.isNull(rs.getString(node.code),"");
					saleCust.custAdd(node.code, node);
														
					node = new ParaNodeDef();
	            	node.code=CustInfoDef.CUST_SCID;
	            	node.name = "";
	            	node.value = CommonMethod.isNull(rs.getString(node.code),"");
					saleCust.custAdd(node.code, node);

					node = new ParaNodeDef();
	            	node.code=CustInfoDef.CUST_SCOTHERNO;
	            	node.name = "";
	            	node.value = CommonMethod.isNull(rs.getString(node.code),"");
					saleCust.custAdd(node.code, node);

					node = new ParaNodeDef();
	            	node.code=CustInfoDef.CUST_SCNUMBER;
	            	node.name = "";
	            	node.value = CommonMethod.isNull(rs.getString(node.code),"");
					saleCust.custAdd(node.code, node);


					node = new ParaNodeDef();
	            	node.code=CustInfoDef.CUST_SCNAME;
	            	node.name = "";
	            	node.value = CommonMethod.isNull(rs.getString(node.code),"");
					saleCust.custAdd(node.code, node);

					node = new ParaNodeDef();
	            	node.code=CustInfoDef.CUST_SCSEX;
	            	node.name = "";
	            	node.value = CommonMethod.isNull(rs.getString(node.code),"");
					saleCust.custAdd(node.code, node);

					node = new ParaNodeDef();
	            	node.code=CustInfoDef.CUST_SCMEMO;
	            	node.name = "";
	            	node.value = CommonMethod.isNull(rs.getString(node.code),"");
					saleCust.custAdd(node.code, node);
                	
                	ret = true;
                }
                
                if (!ret) saleCust = null;
          
            }

            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return true;
        }
        finally
        {
            sql.resultSetClose();
        }
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
        				String log = Language.apply("授权重打印小票: 小票号{0}授权:", new Object[]{salehead.fphm + ""}) + user.gh;
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
    		printSaleTicket(salehead,salegoods,salepay, saleCust, false);
    		pb.close();
        }
        catch (Exception er)
        {
            er.printStackTrace();
        }
    }
    
    public void printSaleTicket(SaleHeadDef vsalehead ,Vector vsalegoods ,Vector vsalepay, SaleCustDef saleCust, boolean isRed)
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
    			((Zmjc_SaleBillMode)SaleBillMode.getDefault()).setTemplateObject(vsalehead, vsalegoods, vsalepay, saleCust);
    			SaleBillMode.getDefault().printBill();
    			printSaleTicket_Msg(vsalehead.syjh, vsalehead.fphm, vsalehead.djlb);
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
    
    public void printSaleTicket_Msg(String strSyjh, long lngFphm, String djlb)
    {
    	
    }
    
}
