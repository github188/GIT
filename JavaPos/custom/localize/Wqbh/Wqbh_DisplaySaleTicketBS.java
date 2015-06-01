package custom.localize.Wqbh;

import java.util.Vector;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Struct.RefundMoneyDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.alibaba.fastjson.JSONObject;

public class Wqbh_DisplaySaleTicketBS extends DisplaySaleTicketBS
{
	 public void getSaleAllInfo(String xsdate,String code,int type, Text txtTicketCode,StyledText txtSaleTime, StyledText txtSyy,StyledText txtSaleType,Table tabTicketDeatilInfo, Table tabPay,StyledText txtMemberCardCode,StyledText txtGrantCardCode,StyledText txtShouldInceptMoney,StyledText txtAgioMoney,StyledText txtFactInceptMoney,StyledText txtGiveChangeMoney,StyledText txtSpoilageMoney, Label lblNet,Group group,StyledText khje)
	    {
	        try
	        {
	            if ((code == null) || code.trim().equals(""))
	            {
	                new MessageBox("小票号不能为空", null, false);
	                clear(txtSaleTime, txtSyy, txtSaleType, tabTicketDeatilInfo,tabPay, txtMemberCardCode, txtGrantCardCode,txtShouldInceptMoney, txtAgioMoney, txtFactInceptMoney,txtGiveChangeMoney, txtSpoilageMoney, lblNet, group);
	                return;
	            }

	            if (code.indexOf("↑")!=-1 || code.indexOf("|")!=-1)
	            {
	            	code = code.substring(1);
	            }
	            //网上查询,NEEDADD

	            //以下是查询本地
	            int findnum = 0;
	            int findcnt = 0;
	            boolean findtip = true;
	            
	            ProgressBox pb = new ProgressBox();

	            ManipulateDateTime dt = new ManipulateDateTime();

	            pb.setText("开始查找小票操作.....");

	            while (findnum < GlobalInfo.syjDef.datatime)
	            {
	            	pb.setText("正在查找 " + dt.getDateBySlash() + " 的小票数据");
	            	
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
	                	if (new MessageBox("在当日的交易数据中找不到 " + code + " 号小票\n\n你要继续在前一周的交易数据中查找该小票吗？",null,true).verify() != GlobalVar.Key1)
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
	                    	int sel = new MessageBox("在 " + dt.getDateBySlash() + " 这周的交易数据中找不到 " + code + " 号小票\n\n你要继续在前一周的交易数据中查找该小票吗？\n\n1-是 / 2-否 / 3-不再提示").verify();
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
	                new MessageBox("没有查找到小票号", null, false);
	                clear(txtSaleTime, txtSyy, txtSaleType, tabTicketDeatilInfo,tabPay, txtMemberCardCode, txtGrantCardCode,txtShouldInceptMoney, txtAgioMoney, txtFactInceptMoney,txtGiveChangeMoney, txtSpoilageMoney, lblNet, group);

	                return;
	            }

	            if (!this.getSaleHead(sql, code, txtSaleTime, txtSyy, txtSaleType,txtMemberCardCode, txtGrantCardCode,txtShouldInceptMoney, txtAgioMoney,txtFactInceptMoney, txtGiveChangeMoney,txtSpoilageMoney, lblNet, group))
	            {
	            	pb.close();
	                pb = null;
	                new MessageBox("小票主单查找失败", null, false);
	                clear(txtSaleTime, txtSyy, txtSaleType, tabTicketDeatilInfo,tabPay, txtMemberCardCode, txtGrantCardCode,txtShouldInceptMoney, txtAgioMoney, txtFactInceptMoney,txtGiveChangeMoney, txtSpoilageMoney, lblNet, group);

	                return;
	            }

	            if (!this.getSaleDetail(sql, code, tabTicketDeatilInfo))
	            {
	            	pb.close();
	                pb = null;
	                new MessageBox("小票明细查找失败", null, false);
	                clear(txtSaleTime, txtSyy, txtSaleType, tabTicketDeatilInfo,tabPay, txtMemberCardCode, txtGrantCardCode,txtShouldInceptMoney, txtAgioMoney, txtFactInceptMoney,txtGiveChangeMoney, txtSpoilageMoney, lblNet, group);

	                return;
	            }

	            if (!this.getPayDetail(sql, code, tabPay))
	            {
	            	pb.close();
	                pb = null;
	                new MessageBox("付款明细查找失败", null, false);
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
	                    if (spd.paycode.equals("0610")){
	                    	tempSaleHead.num2=1;
	                    }
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
					    	boolean iskh = false;
					    	if(tempSaleHead.num1 == 1){    //num5 = 1 表示大会员
					    		Wqbh_SaleBS ws = new Wqbh_SaleBS();
					    		String jf = "";
					    		boolean b = false;
					    		double dhyjf = 0;
								while(jf==null || jf.trim().equals("")){
//									 读取会员卡
									HykInfoQueryBS bs = CustomLocalize.getDefault().createHykInfoQueryBS();
									String track = bs.readMemberCard();
									if(track==null||track.trim().equals("")){
										if(new MessageBox("本笔小票有大会员刷卡,必须刷大会员卡进行红冲!\n是否继续刷大会员？",null,true).verify() != GlobalVar.Key2){
											continue;
										}else{
											return false;
										}
										
									}
									tempSaleHead.str6 = track;
									jf = ws.findDHYJF(track);
									if((jf==null || jf.trim().equals(""))&&new MessageBox("本笔小票有大会员刷卡，查询大会员积分失败！\n是否继续查询会员积分？",null,true).verify() == GlobalVar.Key2){
										jf = "0";
										b = true;
										break;
									}
								}
								if(b) return false;
								dhyjf = Double.parseDouble(jf.trim());
					    		Wqbh_NetService wn = new Wqbh_NetService();
					    		iskh = wn.getDHYRefundMoney(tempSaleHead.mkt, tempSaleHead.syjh, tempSaleHead.fphm,dhyjf, rmd);
					    	}else {
					    		iskh = NetService.getDefault().getRefundMoney(tempSaleHead.mkt,tempSaleHead.syjh,tempSaleHead.fphm,rmd);
					    	}
					    		
					    	if (!iskh)
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
		        }else{
		        	while(tempSaleHead.num1 == 1){    //num5 = 1 表示大会员刷卡){
		        		HykInfoQueryBS bs = CustomLocalize.getDefault().createHykInfoQueryBS();
						String track = bs.readMemberCard();
						if(track==null||track.trim().equals("")){
							if(new MessageBox("本笔小票有大会员刷卡,必须刷大会员卡进行红冲!\n是否继续刷大会员？",null,true).verify() != GlobalVar.Key2){
								continue;
							}else{
								return false;
							}
							
						}else{
							tempSaleHead.str6 = track;
							break;
						}
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
	            tempSaleHead.str1="";
	            if (!AccessDayDB.getDefault().writeSale(tempSaleHead,tempSaleGoods,tempSalePay))
	            {
	                new MessageBox("写入红冲小票错误,红冲失败", null, false);

	                // 记账失败,及时把冲正发送出去
	                pb.setText("正在发送付款冲正数据,请等待.....");
	                CreatePayment.getDefault().sendAllPaymentCz();
	                pb.close();pb = null;
	                
	                return false;
	            }
	            if (tempSaleHead.num1 == 1||tempSaleHead.num2 == 1){//表示有大会员刷卡
	            	pb.setText("正在调用大会员取消交易接口,请等待...");
	            	Wqbh_DHYInterface DHY = new Wqbh_DHYInterface();
					String mktcode = GlobalInfo.sysPara.mktcode;
					if(GlobalInfo.sysPara.mktcode.indexOf(",")!=-1){
						mktcode = GlobalInfo.sysPara.mktcode.substring(GlobalInfo.sysPara.mktcode.indexOf(",")+1);
					}
	            	String tradeNo = mktcode+"|"+GlobalInfo.syjDef.syjh+"|"+tempSaleHead.fphm+"|"+"0";
			 		String oldorder =mktcode+"|"+GlobalInfo.syjDef.syjh+"|"+tempSaleHead.yfphm+"|"+"0";
					int type=0;
		 			if (salehead.djlb.equals(SellType.RETAIL_SALE)){
		 				 type=1;//取消交易  
		 			}else
		 				 type=2;//取消退款
		 			String memberReqInput = "orderNo=" + tradeNo + "&oldOrderNo="
					+ oldorder + "&tradeCode=8002&tradeSrc=4&type="+type;
			        String memberResOutput = DHY.CancelJY(memberReqInput);
			        if (memberResOutput != null
							&& memberResOutput.trim().length() > 0) {
						JSONObject js = JSONObject.parseObject(memberResOutput);
						if (!js.getString("status").equals("0"))// 取消交易接口返回成功
						{
							pb.close();pb = null;
				            new MessageBox("大会员取消交易失败！");
				            return false;
						}
			        }
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
