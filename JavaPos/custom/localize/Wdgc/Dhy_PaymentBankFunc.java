package custom.localize.Wdgc;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.RdPlugins;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.CustomerDef;
		
public class Dhy_PaymentBankFunc extends PaymentBankFunc
{
	 String  syyh = null;    
     String  syjh = null;  
	 String result = "";
	 String path = null;
	 SaleBS saleBS = null;//用于获取会员卡信息
	 
	public String[] getFuncItem()
	{
		String[] func = new String[4];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "积分消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "会员撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "会员退货";
		func[3] = "[" + PaymentBank.XKQT1 + "]" + "增加积分";
		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		// 0-4对应FORM中的5个输入框
		// null表示该不用输入
		switch (type)
		{
			case PaymentBank.XYKXF: // 消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;//"请 刷 卡";
				grpLabelStr[4] = "积分消费";//"交易金额";

				break;

			case PaymentBank.XYKCX: // 消费撤销
				grpLabelStr[0] = null;//"原主机流水号";
				grpLabelStr[1] = null;//"原批次号";
				grpLabelStr[2] = null;//"原交易日期";
				grpLabelStr[3] = null;//"请 刷 卡";
				grpLabelStr[4] = "交易金额";

				break;

			case PaymentBank.XYKTH: // 隔日退货
				grpLabelStr[0] = null;//"原主机流水号";
				grpLabelStr[1] = null;//"原批次号";
				grpLabelStr[2] = null;//"原交易日期";
				grpLabelStr[3] = null;//"请 刷 卡";
				grpLabelStr[4] = "交易金额";

				break;
		
		}

		return true;
	}

	public boolean getFuncText(int type, String[] grpTextStr)
	{
		// 0-4对应FORM中的5个输入框
		// null表示该需要用户输入,不为null用户不输入
		switch (type)
		{
			case PaymentBank.XYKXF: // 消费
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始积分消费";

				break;

			case PaymentBank.XYKCX: // 消费撤销
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;//null;
				break;

			case PaymentBank.XYKTH: // 退货
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始隔日退货";//null;
				break;

		
		}

		return true;
	}

	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && 
					(type != PaymentBank.XYKTH) && (type != PaymentBank.XKQT1))
	            {
	                errmsg = "万达大会员不支持该交易";
	                new MessageBox(errmsg);

	                return false;
	            }
			if(memo==null && type == PaymentBank.XYKCX){
				errmsg = "万达大会员不支持该交易";
                new MessageBox(errmsg);

                return false;
			}
			
            if (PathFile.fileExist( "C:\\JavaPOS\\MemberRecpipt.TXT"))
            {
                PathFile.deletePath( "C:\\JavaPOS\\MemberRecpipt.TXT");
            }
            
            // 写入请求数据
            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
            {
                return false;
            }
                          
            // 读取应答数据
            if (!XYKReadResult(type))
            {
                return false;
            }
                      
            // 检查交易是否成功
            XYKCheckRetCode();
           
            // 	打印签购单
            if (XYKNeedPrintDoc())
            {           	
                XYKPrintDoc();
            }
            
            if (type != PaymentBank.XYKCX )
            	transComplete();
            
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
            XYKSetError("XX","金卡异常XX:"+ex.getMessage());
            new MessageBox("调用万达打会员处理模块异常!\n\n" + ex.getMessage(), null, false);
            
			return false;
		}
	}
	
	public boolean transComplete()
	{
		String transCompleteReturn = null;
		String code = "";
		
		for (int i = 0; i < 3 && !code.equals("00"); i++ )
		{
			if(RdPlugins.getDefault().getPlugins1().exec(7,syjh + "," + syyh + "," + bld.trace))
			{
				transCompleteReturn = (String)RdPlugins.getDefault().getPlugins1().getObject();
			}
			
			if (transCompleteReturn != null)
			{
				code = transCompleteReturn.substring(0,2);
			}
		}
		
		if (!code.equals("00"))
		{
			if (new MessageBox(code.trim().replaceAll("\\S+", " ") + "\n未能完成本次交易.\n\n1-重新发送 2-取消").verify() != GlobalVar.Key2)
			{
				for (int i = 0; i < 3 && !code.equals("00"); i++ )
				{
					if(RdPlugins.getDefault().getPlugins1().exec(7,syjh + "," + syyh + "," + bld.trace))
					{
						transCompleteReturn = (String)RdPlugins.getDefault().getPlugins1().getObject();
					}
					
					if (transCompleteReturn != null)
					{
						code = transCompleteReturn.substring(0,2);
					}
				}
			}
		}
		
		return true;
	}
	
	public boolean XYKNeedPrintDoc()
	{
		
		if (!checkBankSucceed())
        {
            return false;
        }
		
		int type = Integer.parseInt(bld.type.trim());
		
		if ((type == PaymentBank.XYKXF) || (type == PaymentBank.XYKCX) || 
				(type == PaymentBank.XYKTH) ||(type == PaymentBank.XKQT1))
            {
			if (saleBS.curCustomer.status.equals("3"))//人工处理撤消,不打印小票
        	{
				return false;
        	}
			return true;
        }
		else
		{
			return false;
		}
	}
	
	
	public boolean checkBankSucceed()
	{
		if (bld.retbz == 'N')
		{
			errmsg = bld.retmsg = "错误信息：" + result.trim();

			return false;
		}
		else
		{
			errmsg = "交易成功";

			return true;
		}
	}
	
	public boolean XYKWriteRequest(int type, double money, String track1,String track2, String track3,String oldseqno, String oldauthno,String olddate, Vector memo)
	{
		 
         String  oldseqno1 =oldseqno;
         String oldauthno1 =oldauthno;       
         String olddate1 = olddate;
      
         syyh = GlobalInfo.posLogin.gh;
         syjh = ConfigClass.CashRegisterCode;    

	 	 saleBS = (SaleBS) memo.elementAt(2);
	 	double money1;
	 	 if (money == 0)
	 	 {
	 		money1 = saleBS.saleHead.ysje;
	 	 }
	 	 else
	 	 {
	 		 money1 = money;
	 	 }
	 	String jestr = String.valueOf(money1);
        jestr = Convert.increaseCharForward(jestr,'0',12);
	 	 //获取会员磁道信息
        String track = "";
        if(null != saleBS.curCustomer && null != saleBS.curCustomer.memo)
        {
        	track = saleBS.curCustomer.memo;
        }
        else
        {
        	new MessageBox("请刷卡后再使用万达大会员支付");
        	return false;
        }
         
         if (type == PaymentBank.XYKTH || type == PaymentBank.XYKCX )
         {
        	 oldseqno1 = saleBS.curCustomer.str1; //主机流水号
        	 oldauthno1 = saleBS.curCustomer.str2; //主机流水号
        	 olddate1 = saleBS.curCustomer.str3; //主机流水号
         }

         String trace = String.valueOf(GlobalInfo.syjStatus.fphm); 
		 
         String line = "";
         int code = 0;
         result = null;
		 try
		 {
			 
			 switch (type)
			 {
			 	case PaymentBank.XYKXF: 	// 消费 
			 		code = 3;
			 		line = syjh + "," + syyh + "," + track + "," + trace + "," + "1" + "," + jestr ;
			 		break;
			 	case PaymentBank.XKQT1: 	// 积分，是大会员，没用打会员付款，调用这个增加积分
			 		code = 3;
			 		line = syjh + "," + syyh + "," + track + "," + trace + "," + "2" + "," + jestr ;
			 		break;
			 	case PaymentBank.XYKCX: 	
			 		if(SellType.ISBACK(saleBS.saleHead.djlb)){ // 会员购物退货撤销
			 			if (saleBS.curCustomer.status.equals("3"))//人工处理撤消
	                	{
	                		code = 9;
			 				trace = Convert.increaseCharForward(trace,'0',12);
			 				result = "00" + trace +"000000" + "0000        0           0           0001        " + jestr;
	                	}
	                	else
	                	{
			 			code = 6;  //由于大会员中本次收银流水号每天必须唯一，所以这里用“ ”代替。			 			
			 			line = syjh + "," + syyh + "," + track + "," + " " +","+ oldseqno1+","+ oldauthno1+","+ olddate1 + "," + Convert.increaseChar(trace,' ',11);	 			
	                	}
	                }
			 		else{  // 会员购物消费撤销
			 			code = 4;
			 			line = syjh + "," + syyh + "," + track + "," + " " + "," + oldseqno1+","+oldauthno1+","+ olddate1  + "," + Convert.increaseChar(" ",' ',11) ;		
			 		}
			 	break;	
                case PaymentBank.XYKTH:		// 隔日退货
                	if (null != saleBS.curCustomer && null != saleBS.curCustomer.status && saleBS.curCustomer.status.equals("3")) //人工处理
                	{
                		code = 9;
		 				trace = Convert.increaseCharForward(trace,'0',12);
		 				String kh = Convert.increaseCharForward(saleBS.curCustomer.code,' ',19) ;
		 				result = "00" + trace +"000000" + kh + "0000        0           0           0001        " + jestr + "000000000000退货失败，人工处理";
                	}
                	else
                	{
                		code = 5;
            			line = syjh + "," + syyh + "," + track + "," + trace+","+ oldseqno1+","+ oldauthno1+","+ olddate1 + ", " + Convert.increaseChar(track2,' ',11) + "," + jestr;
                	}               	
                break;             
			 }
			 if (code == 0)  return false;
			 else if (code == 9 ) return true;
			 if (RdPlugins.getDefault().getPlugins1().exec(code, line ))
		 		{
		 			result = (String)RdPlugins.getDefault().getPlugins1().getObject();
		 		}			 
			 return true;
		 }
		 catch (Exception ex)
		 {
			 new MessageBox("调用DLL方法异常!\n\n" + ex.getMessage(), null, false);
			 ex.printStackTrace();
			 return false;
		 }
	}
	
	public boolean XYKReadResult(int type)
	{
		
		try
        {
			if (result==null||result.trim().equals(""))
            {
            	XYKSetError("XX","大会员应答数据为空!");
                new MessageBox("获取大会员应答数据失败!", null, false);
                
                return false;
            }
			
			String line = result;
			
			if (!line.substring(0,2).equals("00"))
			{
				if (type == PaymentBank.XYKTH)
				{
					if (new MessageBox("会员退货不成功，请顾客到服务台处理或营业员处理！！！" +
					"\n如果已经处理好，请按确定.\n然后重新选择 大会员 付款方式，填入积分付款金额，进行重新付款。\n1-确定\t2-取消").verify() == GlobalVar.Key1)
					{
						saleBS.curCustomer.status = "3";
					}
				}
				return false;
			}
				

			 switch (type)
			 {
				
			 	case PaymentBank.XYKXF: 	// 消费
			 		if(line.length()>2) bld.retcode = line.substring(0,2);
			 		if(line.length()>14) bld.trace = Long.parseLong(line.substring(2, 14).trim());
			 		if(line.length()>20) bld.memo = line.substring(14,20).trim();// 批次号
			 		if(line.length()>39) bld.cardno = line.substring(20,39).trim();
			 		if(line.length()>51) bld.kye = Double.parseDouble(line.substring(39,51).trim());//当前积分余额
			 		if(line.length()>63) bld.ylzk = Double.parseDouble(line.substring(51,63).trim());// 当前可用积分
			 		if(line.length()>99) bld.je = Double.parseDouble(line.substring(87,99).trim());
			 		
			 		saleBS.curCustomer.str1 = line.substring(2, 14).trim();//主机流水号
			 		saleBS.curCustomer.str2 = line.substring(14,20).trim();//批次
			 		ManipulateDateTime mdt = new ManipulateDateTime();
			 		saleBS.curCustomer.str3 = mdt.getTimeByEmpty();//日期
			 		
			 		saleBS.curCustomer.status = "0"; //消费成功之后标记；0-消费;1-消费撤销;2-隔日退货
			 		break;
			 	case PaymentBank.XKQT1: 	// 积分
			 		if(line.length()>2) bld.retcode = line.substring(0,2);
			 		if(line.length()>14) bld.trace = Long.parseLong(line.substring(2, 14).trim());
			 		if(line.length()>20) bld.memo = line.substring(14,20).trim();// 批次号
			 		if(line.length()>39) bld.cardno = line.substring(20,39).trim();
			 		if(line.length()>51) bld.kye = Double.parseDouble(line.substring(39,51).trim()) ;//当前积分余额
			 		if(line.length()>63) bld.ylzk =  Double.parseDouble(line.substring(51,63).trim()) ;// 当前可用积分
			 		if(line.length()>99) bld.je = Double.parseDouble(line.substring(87,99).trim());
			 		
			 		break;
			 	case PaymentBank.XYKCX: 	// 消费撤销
			 		if(line.length()>2) bld.retcode = line.substring(0,2);
			 		if(line.length()>14) bld.trace = Long.parseLong(line.substring(2, 14).trim());
			 		if(line.length()>20) bld.memo = line.substring(14,20).trim();// 批次号
			 		if(line.length()>32) bld.kye =  Double.parseDouble(line.substring(20,32).trim()) ;//当前积分余额
			 		if(line.length()>44) bld.ylzk =  Double.parseDouble(line.substring(32,44).trim()) ;// 当前可用积分
			 		saleBS.curCustomer.status = "Y"; //撤消成功，返回原始状态
			 	break;	
                case PaymentBank.XYKTH:		// 隔日退货
                	if(line.length()>2) bld.retcode = line.substring(0,2);
			 		if(line.length()>14) bld.trace = Long.parseLong(line.substring(2, 14).trim());
			 		if(line.length()>20) bld.memo = line.substring(14,20).trim();// 批次号
			 		if(line.length()>39) bld.cardno = line.substring(20,39).trim();
			 		if(line.length()>51) bld.kye = Double.parseDouble(line.substring(39,51).trim()) ;//当前积分余额
			 		if(line.length()>63) bld.ylzk =  Double.parseDouble(line.substring(51,63).trim()) ;// 当前可用积分
			 		if(line.length()>99) bld.je = Double.parseDouble(line.substring(87,99).trim());

			 	    if (line.length()>111) bld.retmsg = line.substring(111).trim();
			 		
			 	    saleBS.curCustomer.str1 = line.substring(2, 14).trim();//主机流水号
			 		saleBS.curCustomer.str2 = line.substring(14,20).trim();//批次
			 		ManipulateDateTime mdt1 = new ManipulateDateTime();
			 		saleBS.curCustomer.str3 = mdt1.getTimeByEmpty();//日期
			 		//saleBS.curCustomer.num1= 2;
			 		if (!saleBS.curCustomer.status.equals("3"))//非人工处理
                	{
			 			saleBS.curCustomer.status = "2"; //标记为退货状态
                	}
			 		break;
               
			 }
            
            
			return true;
        }
		catch (Exception ex)
		{
			XYKSetError("XX","读取应答XX:"+ex.getMessage());
            new MessageBox("读取万达大会员凭条应答数据异常!" + ex.getMessage(), null, false);
            ex.printStackTrace();
            
			return false;
		}
	}
	
	public void XYKPrintDoc()
	{
		ProgressBox pb = null;
		if(GlobalInfo.sysPara.bankprint<1) return;    
		String printName = "C:\\WandaMember\\MemberReceipt.TXT";
		try
		{
			 
			if (!PathFile.fileExist(printName))
            {	
				new MessageBox("万达大会员凭条不存在，无法打印!", null, false);
                return;
            }			
			pb = new ProgressBox();
	        pb.setText("正在打印万达大会员凭条,请等待...");
	        
	        for (int i = 0; i < GlobalInfo.sysPara.bankprint; i++)
	        {
	        	
	        	XYKPrintDoc_Start();

	            BufferedReader br = null;
	            
	            try
	            {
	            	 br = CommonMethod.readFileGBK(printName);

	            	 if (br == null)
	            	 {
                        new MessageBox("打开" + printName + "打印文件失败!");

                        return;
	            	 }
	            	 
	            	 String line = null;
	            	 
	            	 while ((line = br.readLine()) != null)
	            	 {	            		    
	            		 if ( line == null || line.length() < 0)
	            			 continue;
	            		 if (line.indexOf("CUTPAPER") >= 0 )
	            		 {
	            			 XYKPrintDoc_End();

	            			 continue;
	            		 }

	            		 XYKPrintDoc_Print(line);
	            		 
	            	 }
	            }
	            catch (Exception ex)
	            {
	            	new MessageBox(ex.getMessage());
	            }
	            finally
	            {
	            	if (br != null)
	            	{
	            		br.close();
	            	}
	            }

	            XYKPrintDoc_End();
	            
	        }
			
		}
		catch (Exception ex)
		{
			new MessageBox("打印万达大会员凭条发生异常\n\n" + ex.getMessage());
			ex.printStackTrace();
		}
		finally
		{
			if (pb != null)
			{
				pb.close();
				pb = null;
			}
		}
	}
	
	public boolean XYKCheckRetCode()
	{
		if (bld.retcode.trim().equals("00"))
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
}