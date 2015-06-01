package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.sosoPay.cashRegisterInterface.*;

//杭州水晶城  自定义付款接口   调用jar包     jar包名：cashRegisterInterface.jar
/*
 * 1.	连接建立接口 getConnect
 * 2.	下单接口 trad
 * 3.	查询接口 queryTrade
 * 4.	交易撤销接口 revoke
 * 5.	连接释放接口  disconnect
 * 
 */
public class HzSJC_PaymentBankFunc extends PaymentBankFunc {
	
	String path = "C:\\JavaPos";
	String querystate;   //M:保存查询返回的结果STATE
	int loop = 1;  //M:控制查询循环的次数
	com.sosoPay.cashRegisterInterface.sosoPayGateway.ServiceResult resultTrad = null;  //M:保存调用下单接口返回的结果
	com.sosoPay.cashRegisterInterface.sosoPayGateway.ServiceResult resultQueryTrade = null; //M:保存调用查询接口返回的结果
	com.sosoPay.cashRegisterInterface.sosoPayGateway.ServiceResult resultRevoke = null; //M:保存调用撤销接口返回的结果
	
	public String[] getFuncItem()
    {
        String[] func = new String[2];
        
        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";  
        func[1] = "[" + PaymentBank.XYKCX + "]" + "交易撤销";
        
        return func;
    } 
	
	public boolean getFuncLabel(int type, String[] grpLabelStr)
    {
		switch (type)
        {
        	case PaymentBank.XYKXF: //	消费
        		grpLabelStr[0] = null;
        		grpLabelStr[1] = null;
        		grpLabelStr[2] = null;
        		grpLabelStr[3] = null;
        		grpLabelStr[4] = "交易金额";
        	break;
//        	case PaymentBank.XYKYE: //余额查询    
//                grpLabelStr[0] = "登记流水号";
//                grpLabelStr[1] = null;
//                grpLabelStr[2] = null;
//                grpLabelStr[3] = null;
//                grpLabelStr[4] = "余额查询";
//            break;
        	case PaymentBank.XYKCX: //交易撤销
                grpLabelStr[0] = null;
                grpLabelStr[1] = "登记流水号";
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易撤销";
            break;
        }
		
		return true;
    }
	
	public boolean getFuncText(int type, String[] grpTextStr)
    {
		switch (type)
		{
		 	case PaymentBank.XYKXF: 	// 消费
		        grpTextStr[0] = null;
		        grpTextStr[1] = null;
		        grpTextStr[2] = null;
		        grpTextStr[3] = null;
		        grpTextStr[4] = null;
		    break;
//		 	case PaymentBank.XYKYE: 	//余额查询    
//                grpTextStr[0] = null;
//                grpTextStr[1] = null;
//                grpTextStr[2] = null;
//                grpTextStr[3] = null;
//                grpTextStr[4] = "按回车键开始余额查询";
//            break;
		 	case PaymentBank.XYKCX: 	//交易撤销
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始交易撤销";
            break;
		}
		
		return true;
    }
	
	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if (!(type == PaymentBank.XYKXF || 
				  type == PaymentBank.XYKCX  ))
				{			
					  new MessageBox("银联接口不支持此交易类型！！！");
					  
					  return false;
			    }
//			path = getBankPath(paycode);
//			path = ConfigClass.BankPath;
			
//			String url = ConfigClass.ServerIP+":"+ConfigClass.ServerPort;
//			String newURL="http://"+url+"/sosoPayGateway/soap/busiService?wsdl";
			
			SosoPayGatewayService.getConnect("http://www.sssyin.cn:9000/sosoPayGateway/soap/busiService?wsdl", 10);
			
			
			String charge_code;
			
			switch(type)
			{
			   case  PaymentBank.XYKXF: 	// 消费
				   
				   String pos_dev_id = ConfigClass.CashRegisterCode; //收银机编号
				   String pos_op_id = GlobalInfo.posLogin.gh;        //收银员工号
				   String pos_op_name = GlobalInfo.posLogin.name;    //收银员姓名
				   int je = (int)ManipulatePrecision.doubleConvert(money*100, 2, 1);//金额
				   String pos_code = pos_dev_id + GlobalInfo.syjStatus.fphm;      //收银流水号=收银机编号+小票号
				   resultTrad=	SosoPayGatewayService.trad(pos_dev_id,pos_op_id, je,pos_code ,null);
				   
				   if(resultTrad.getRESULTCODE() == 0)
				   {
					   
					   charge_code = resultTrad.getCHARGECODE();					   
					   
					   do
					   {
						   Thread.sleep(6000);
						   
						   resultQueryTrade = SosoPayGatewayService.queryTrade(charge_code);
						   
						   if(resultQueryTrade.getRESULTCODE() != 0)
						   {
							   break;
						   }						 
						  
						   querystate = String.valueOf(resultQueryTrade.getRESULTOBJ().getSTATE());
						   loop ++;
						   
						   
					   }while( (resultQueryTrade.getRESULTCODE() == 0 ) && (querystate.equals("0")) && loop <= 30 );	            	      
	            	   
				   }
				   
				   XYKReadResultTrade();
				  			   
               break;
//               case  PaymentBank.XYKYE: 	// 余额查询	
//            	   
//            	   charge_code = oldseqno;
//            	   result2=SosoPayGatewayService.queryTrade(charge_code);
//            	   
//               break;	
               case  PaymentBank.XYKCX: 	// 交易撤销	
            	   
            	   charge_code = oldauthno;
            	   resultRevoke = SosoPayGatewayService.revoke(charge_code);
            	   XYKReadResultRevoke();
            	   
               break;
			
			}
								
   
            // 检查交易是否成功
            XYKCheckRetCode(type);
			
            //打印签购单
			if (XYKNeedPrintDoc(type))
			{
				XYKPrintDoc(type);
			}
            return true;		
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
            XYKSetError("XX","金卡异常XX:"+ex.getMessage());
            new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);
            
			return false;
		}
	}
	
	public boolean XYKReadResultTrade()
	{
		try
		{
			if (resultTrad == null || resultQueryTrade == null )  //调用下单接口 和查询接口 返回结果 为空 则 调用失败
            {
                return false;
            }
           
            int type = Integer.parseInt(bld.type.trim());
           		
            bld.retcode  = String.valueOf(resultTrad.getRESULTCODE());  // 下单接口 返回码     
            
            if(!bld.retcode.equals("0"))
            {
            	bld.retmsg = bld.retcode + "," + resultTrad.getRESULTMSG(); //下单接口调用没成功 则保存返回信息
            	return false;
            }
                		
            bld.retcode  = String.valueOf(resultQueryTrade.getRESULTCODE());  //查询接口 返回码     
            
            if(!bld.retcode.equals("0"))
            {
            	bld.retmsg = bld.retcode + "," + resultQueryTrade.getRESULTMSG();//查询接口调用没成功 则保存返回信息
            	return false;
            }
            
            if(loop > 30)  //查询超时
            {
            	bld.retmsg = "调用查询接口时查询超时";
            	return false;
            }
            
            if(querystate.equals("2")) //已撤单
            {
            	bld.retmsg = "客户已撤单";
            	return false;
            }
            
            if(type == PaymentBank.XYKXF)
            {
                bld.trace = Convert.toInt(resultTrad.getPOSCODE());   //M:收银流水号
                bld.authno = resultTrad.getCHARGECODE();  //M:登记流水号
                bld.cardno = resultQueryTrade.getRESULTOBJ().getPAYID(); //M:支付账号
                bld.je = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Convert.toDouble(resultQueryTrade.getRESULTOBJ().getAMT()),100),2,1);   //交易金额	
            }  	
            
            createPrintFile(); //消费成功则生成打印签购单
            
            return true;		
        }
		catch (Exception ex)
		{
			XYKSetError("XX","读取应答XX:"+ex.getMessage());
            new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);
            ex.printStackTrace();
            
			return false;
		}
		finally
        {
			SosoPayGatewayService.disconnect(); //释放连接
        }
	}
	
	
	public boolean XYKReadResultRevoke() //撤销返回信息只有返回码和返回信息
	{
		try
		{
			if (resultRevoke == null )  //调用撤销接口返回为空 则 调用失败
            {
                return false;
            }
           
            int type = Integer.parseInt(bld.type.trim());
           		
            bld.retcode  = String.valueOf(resultRevoke.getRESULTCODE());  // 下单接口 返回码     
            
            if(!bld.retcode.equals("0"))
            {
            	bld.retmsg = bld.retcode + "," + resultRevoke.getRESULTMSG(); //下单接口调用没成功 则保存返回信息
            	return false;
            } 
            
            bld.je = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Convert.toDouble(resultRevoke.getRESULTOBJ().getAMT()),100),2,1);   //交易金额
//            bld.trace = 0;
//            bld.authno = "";

            
            return true;		
        }
		catch (Exception ex)
		{
			XYKSetError("XX","读取应答XX:"+ex.getMessage());
            new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);
            ex.printStackTrace();
            
			return false;
		}
		finally
        {
			SosoPayGatewayService.disconnect();   //释放连接
        }
	}
	
	
	public boolean XYKCheckRetCode(int type)
	{
		if(type == PaymentBank.XYKXF){
			
			if ( bld.retcode.trim().equals("0") && querystate.equals("1"))
			{
				bld.retbz = 'Y';

				return true;
			} 
			else
			{
				bld.retbz = 'N';

				return false;
			}
		}
		
		if(type == PaymentBank.XYKCX){
			
			if ( bld.retcode.trim().equals("0"))
			{
				bld.retbz = 'Y';

				return true;
			} 
			else
			{
				bld.retbz = 'N';

				return false;
			}
		}
		
		return false;
		
	}
	
	public boolean XYKNeedPrintDoc(int type)
	{
		if (!checkBankSucceed())
	    {
	        return false;
	    }
		if (  type == PaymentBank.XYKXF )
		{
			return true;
		}
		else
			return false;
	}
	
	public boolean checkBankSucceed()
	{
		if (bld.retbz == 'N')
		{
			errmsg = bld.retmsg;

			return false;
		}
		else
		{
			errmsg = "交易成功";  

			return true;
		}
	}
	
	
	public void XYKPrintDoc(int type)
	{
		ProgressBox pb = null;
		String name = null;

		name =path + "\\toprint.txt";

		try
		{
			if (!PathFile.fileExist(name))
			{
				if(type == PaymentBank.XYKJZ)
				{
					new MessageBox("找不到结算单打印文件！！！");
				}else
				{
					new MessageBox("找不到签购单打印文件！！！");
				}
				
				return ;
			}
			pb = new ProgressBox();
			pb.setText("正在打印,请等待..." + "\t OY : " + GlobalInfo.sysPara.issetprinter);
			
			for (int i = 0; i < GlobalInfo.sysPara.bankprint; i ++)
			{
				BufferedReader br = null;
				XYKPrintDoc_Start();
				try
				{
					//br = CommonMethod.readFileGB2312(name);
					br = CommonMethod.readFile(name);
					if (br == null)
					{							
						if(type == PaymentBank.XYKJZ)
						{
							new MessageBox("打开结算单文件失败");
						}else
						{
							new MessageBox("打开签购单文件失败");
						}
						
						return ;
					}
					
					String line = null;
					while ((line = br.readLine()) != null)
					{
						if (line.length() <= 0)
							continue;
						//银行签购单模板添加 "CUTPAPER" 标记
						//当程序里面读取到这个字符是，打印机切纸
						if (line.indexOf("CUTPAPER") >= 0)
						{
							XYKPrintDoc_End();
//							new MessageBox("请撕下客户签购单！！！");
//							System.out.println("CUTPAPER");
							
							continue;
						}
						
						XYKPrintDoc_Print(line);
						
//						System.out.println(line);  
						
					}					
				}
				catch(Exception e)
				{
					new MessageBox(e.getMessage());
				}
				finally
				{
					if (br != null)
					try
					{
						br.close();
					}
					catch(IOException ie)
					{
						ie.printStackTrace();
					}					
				}
				XYKPrintDoc_End();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			if(type == PaymentBank.XYKJZ)
			{
				new MessageBox("打印结算单异常!!!\n" + e.getMessage());
			}else
			{
				new MessageBox("打印签购单异常!!!\n" + e.getMessage());
			}			
		}
		finally
		{
			if (pb != null)
			{
				pb.close();
				pb = null;
			}
//				if (PathFile.fileExist(name))
//				{
//					PathFile.deletePath(name);
//				}
		}
	}
	
	 ////////////////////////下面的方法生成打印文件///////////////////////////////
		// 生成签购单文件(消费)
		public void createPrintFile()
		{
			PrintWriter pw = null;
			String line = "";
			line = line + Convert.appendStringSize("", "杭州水晶城支付票据", 10, 38, 38) + "\r\n";
			line = line + Convert.appendStringSize("", "--------------持卡人联-------------", 0, 38, 38) + "\r\n";
			line = line + Convert.appendStringSize("", "收银流水:" + bld.trace, 0, 38, 38) + "\r\n";
			line = line + Convert.appendStringSize("", "登记流水:" + bld.authno, 0, 38, 38) + "\r\n";
			line = line + Convert.appendStringSize("", "消费金额:" + bld.je, 0, 38, 38) + "\r\n";				
			line = line + Convert.appendStringSize("", "打印时间:" + ManipulateDateTime.getDateTimeByClock(), 0, 38, 38) + "\r\n";
			line = line + Convert.appendStringSize("", "支付账号:" + bld.cardno, 0, 38, 38) + "\r\n";
			line = line + Convert.appendStringSize("", "-----------------------------------", 0, 38, 38) + "\r\n";
			line = line + Convert.appendStringSize("", "谢谢惠顾！欢迎下次光临！", 8, 38, 38) + "\r\n";

			line = line + Convert.appendStringSize("", " ", 0, 38, 38) + "\r\n";
			line = line + Convert.appendStringSize("", " ", 0, 38, 38) + "\r\n";
			line = line + Convert.appendStringSize("", "CUTPAPER", 0, 38, 38) + "\r\n";
			line = line + Convert.appendStringSize("", " ", 0, 38, 38) + "\r\n";
			line = line + Convert.appendStringSize("", "杭州水晶城支付票据", 10, 38, 38) + "\r\n";
			line = line + Convert.appendStringSize("", "---------------商户联--------------", 0, 38, 38) + "\r\n";
			line = line + Convert.appendStringSize("", "收银流水:" + bld.trace, 0, 38, 38) + "\r\n";
			line = line + Convert.appendStringSize("", "登记流水:" + bld.authno, 0, 38, 38) + "\r\n";
			line = line + Convert.appendStringSize("", "消费金额:" + bld.je, 0, 38, 38) + "\r\n";				
			line = line + Convert.appendStringSize("", "打印时间:" + ManipulateDateTime.getDateTimeByClock(), 0, 38, 38) + "\r\n";
			line = line + Convert.appendStringSize("", "支付账号:" + bld.cardno, 0, 38, 38) + "\r\n";
			line = line + Convert.appendStringSize("", "-----------------------------------", 0, 38, 38) + "\r\n";
			line = line + Convert.appendStringSize("", "谢谢惠顾！欢迎下次光临！", 8, 38, 38) + "\r\n";

			line = line + Convert.appendStringSize("", " ", 0, 38, 38) + "\r\n";
			try
			{
				pw = CommonMethod.writeFileUTF(path + "\\toprint.txt");
				if (pw != null)
				{
					pw.println(line);
					pw.flush();
				}
			}
			finally
			{
				if (pw != null)
				{
					pw.close();

				}
			}
		}

}
