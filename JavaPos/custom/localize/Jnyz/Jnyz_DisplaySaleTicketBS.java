package custom.localize.Jnyz;

import java.util.Vector;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Struct.RefundMoneyDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Jnyz_DisplaySaleTicketBS extends DisplaySaleTicketBS {

	
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
	
	protected boolean setSaleRedQuash()
    {
        ProgressBox pb = null;
        SaleHeadDef tempSaleHead = null;
        Vector tempSaleGoods = null;
        Vector tempSalePay = null;
        boolean retok = false;
        
        try
        {
            MessageBox me = new MessageBox("你确定要将此笔交易红冲吗?", null, true);
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
            pb.setText("正在读取原交易数据,请等待...");
            
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
                new MessageBox("小票主单未找到,红冲失败", null, false);

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
                new MessageBox("小票明细未找到,红冲失败", null, false);

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
                new MessageBox("小票付款明细,红冲失败", null, false);

                return false;
            }
            
            // 创建付款记账对象
            if (!createPayAssistant(tempSalePay,tempSaleHead))
            {
            	pb.close();pb = null;
                new MessageBox("付款对象生成错误,红冲失败");
            	
            	return false;
            }
            
            // 校验数据
	        if (!AccessDayDB.getDefault().checkSaleData(tempSaleHead,tempSaleGoods,tempSalePay))
	        {
	        	pb.close();pb = null;
	            new MessageBox("交易数据校验错误!");
	
	            return false;
	        }
	        
	        // 发送当前红冲销售的小票以计算扣回
	        if (GlobalInfo.sysPara.refundByPos != 'N' && salehead.hykh.length() > 0)
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
			    		if (new MessageBox("发送扣回小票失败，可能导致积分和券错误！\n是否强行红冲",null,true).verify() != GlobalVar.Key1)
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
				    		if (new MessageBox("小票扣回计算失败，可能计算积分和券错误！\n是否强行红冲",null,true).verify() != GlobalVar.Key1)
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
					    		new MessageBox("本笔交易存在扣回，请使用退货功能");
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
            pb.setText("正在记账付款数据,请等待.....");
            if (!saleCollectAccountPay(tempSalePay,tempSaleHead))
            {
                new MessageBox("付款数据记账错误,红冲失败");

                // 记账失败,及时把冲正发送出去
                pb.setText("正在发送付款冲正数据,请等待.....");
                CreatePayment.getDefault().sendAllPaymentCz();
                pb.close();pb = null;
                
            	return false;
            }
            
            // 银联记账可改变付款金额，从而由于误操作导致付款金额发生变化，因此重新检查一次小票平衡关系
	        if (!AccessDayDB.getDefault().checkSaleData(tempSaleHead,tempSaleGoods,tempSalePay))
	        {
	        	pb.close();pb = null;
	            new MessageBox("付款记账后交易数据校验错误!");
	
	            return false;
	        }
	        
            // 开始插入小票
            pb.setText("正在写入红冲交易,请等待...");
            if (!AccessDayDB.getDefault().writeSale(tempSaleHead,tempSaleGoods,tempSalePay))
            {
                new MessageBox("写入红冲小票错误,红冲失败", null, false);

                // 记账失败,及时把冲正发送出去
                pb.setText("正在发送付款冲正数据,请等待.....");
                CreatePayment.getDefault().sendAllPaymentCz();
                pb.close();pb = null;
                
                return false;
            }
            
            // 小票已写盘,本次交易就要认为完成,即使后续处理异常也要返回成功
            retok = true;
            
            // 删除付款记账冲正
            pb.setText("正在清除付款冲正,请等待......");
            if (!saleCollectAccountClear())
            {
                new MessageBox("付款冲正清除有错误");
            }
            
            // 将原小票更新
            salehead.hcbz = 'Y';
            pb.setText("正在更新原交易数据,请等待...");
            if (!GlobalInfo.dayDB.executeSql("update salehead set hcbz = 'Y' where syjh = '" + ConfigClass.CashRegisterCode + "' and  fphm = " + salehead.fphm))
            {
                new MessageBox("更改原小票红冲标志错误");
            }
            
            // 上传红冲小票
            pb.setText("正在发送红冲交易,请等待...");
            DataService.getDefault().sendSaleData(tempSaleHead,tempSaleGoods,tempSalePay);

            // 发送当前收银状态
            pb.setText("正在发送收银交易汇总,请等待...");
            DataService.getDefault().sendSyjStatus();
            
            //打印红冲小票
            if (GlobalInfo.sysPara.isHcPrintBill == 'Y')
            {
            	pb.setText("正在打印红冲小票,请等待...");
            	printSaleTicket(tempSaleHead,tempSaleGoods,tempSalePay,true);
            }
            
            // 红冲完成
            pb.close();pb = null;
            new MessageBox("该笔交易已红冲成功!", null, false);
            
            return retok;
        }
        catch (Exception ex)
        {    
            ex.printStackTrace();
            
            GlobalInfo.dayDB.rollbackTrans();

            new MessageBox("红冲过程中发生异常!\n\n"+ ex.getMessage());
            
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
}
