package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import org.eclipse.swt.widgets.Text;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

//万商通联银行卡（中免三亚）
public class TLBank_PaymentBankFunc extends PaymentBankFunc
{
	String path = "C:\\ALLINPAY\\ALLINPAY_LIGHT";
	public String getBankPath()
	{
		return "C:\\ALLINPAY\\ALLINPAY_LIGHT";
	}
	
	public String[] getFuncItem()
    {
        String[] func = new String[8];
        
        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
        func[1] = "[" + PaymentBank.XYKCX + "]" + "撤销";
        func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
        func[3] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
        func[4] = "[" + PaymentBank.XYKCD + "]" + "重打签购单";
        func[5] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
        func[6] = "[" + PaymentBank.XYKJZ + "]" + "交易结算";
        func[7] = "[" + PaymentBank.XKQT1 + "]" + "重打结算单";
        
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
        	case PaymentBank.XYKCX: //消费撤销
                grpLabelStr[0] = "交易流水号";
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易金额";
            break;
        	case PaymentBank.XYKTH://隔日退货   
				grpLabelStr[0] = null;
				grpLabelStr[1] = "系统参考号";
				grpLabelStr[2] = "交易日期";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
			break;
        	case PaymentBank.XYKYE: //余额查询    
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "余额查询";
            break;
        	case PaymentBank.XYKCD: //重打签购单    
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "重打签购单";
            break;
        	case PaymentBank.XYKQD: //签到   
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易签到";
            break;
        	case PaymentBank.XYKJZ: //结算  
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易结算";
            break;
        	case PaymentBank.XKQT1: //重打结算单  
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "重打结算单";
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
		 	case PaymentBank.XYKCX: 	// 消费撤销
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = null;
            break;
		 	case PaymentBank.XYKTH:		//隔日退货   
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
			break;
		 	case PaymentBank.XYKYE: 	//余额查询    
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始余额查询";
            break;
		 	case PaymentBank.XYKCD: 	//重打签购单    
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始重打印";
            break;
		 	case PaymentBank.XYKQD: 	//交易签到   
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始签到";
            break;
		 	case PaymentBank.XYKJZ: 	//交易结算
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始结算";
            break;
		 	case PaymentBank.XKQT1: 	//交易一览
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键重打结算单";
            break;
		}
		
		return true;
    }
	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{
		try
		{
			//获得金卡文件路径			
			path = getBankPath();
			
			if (!(type == PaymentBank.XYKXF || 
				  type == PaymentBank.XYKCX || 
				  type == PaymentBank.XYKTH || 
				  type == PaymentBank.XYKQD ||
				  type == PaymentBank.XYKJZ || 
				  type == PaymentBank.XYKYE ||
				  type == PaymentBank.XYKCD ||
				  type == PaymentBank.XKQT1 ))
				{			
					  new MessageBox("银联接口不支持此交易类型！！！");
					  
					  return false;
			    }
			
			
			if (PathFile.fileExist(path + "\\request.txt"))
			{
				PathFile.deletePath(path + "\\request.txt");
				if (PathFile.fileExist(path + "\\request.txt"))
				{
					errmsg = "交易“request.txt”文件删除失败，请重试！！！";
					XYKSetError("XX",errmsg);
					new MessageBox(errmsg);
					
					return false;
				}				
			}
			if (PathFile.fileExist(path + "\\result.txt"))
			{
				PathFile.deletePath(path + "\\result.txt");
				if (PathFile.fileExist(path + "\\result.txt"))
				{
					errmsg = "交易“result.txt”文件删除失败，请重试！！！";
					XYKSetError("XX",errmsg);
					new MessageBox(errmsg);
					
					return false;
				}				
			}
						
            // 写入请求数据
            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
            {
                return false;
            }
            //  调用接口模块
            if (PathFile.fileExist(path+"\\javaposbank.exe"))
            {
            	CommonMethod.waitForExec(path+"\\javaposbank.exe TLZF","javaposbank");            	
            }
            else
            {
            	PosLog.getLog(this.getClass()).info("找不到金卡工程模块 javaposbank.exe");
                XYKSetError("XX","找不到金卡工程模块 javaposbank.exe");
                new MessageBox("找不到金卡工程模块 javaposbank.exe");
                return false;
            }
			
            // 读取应答数据
            if (!XYKReadResult())
            {
                return false;
            }
            
            
            // 检查交易是否成功
            return XYKCheckRetCode();
			
            /*//打印签购单
			if (XYKNeedPrintDoc(type))
			{
				//不打印
				//XYKPrintDoc(type);
			}*/
            //return true;		
		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass()).info(ex);
            XYKSetError("XX","金卡异常XX:"+ex.getMessage());
            new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);
            
			return false;
		}
	}
	
	//卡类型
	public String getBankType()
	{
		return "01";
	}
	
	//交易唯一标识
	public String paySeq()
	{
		//MKT+SYJH+FPHM+DATETIME
		return GlobalInfo.sysPara.mktcode + GlobalInfo.syjDef.syjh + GlobalInfo.syjStatus.fphm + ManipulateDateTime.getCurrentDateTimeMilliSencond();
	}

	public boolean XYKWriteRequest(int type, double money, String track1,String track2, String track3,String oldseqno, String oldauthno,String olddate, Vector memo)
	{
		 String payType = "";//交易类型
		 PrintWriter pw = null;
		 
		 try
		 {
			 switch (type)
			 {
			 	case PaymentBank.XYKXF: 	// 消费
                    payType = "2";
                break;
			 	case PaymentBank.XYKCX: 	// 消费撤销
			 		payType = "3";
			 	break;	
                case PaymentBank.XYKTH:		// 隔日退货
                	payType = "4";
                break;
                case  PaymentBank.XYKYE: 	// 余额查询
                	payType = "18";
                break;	
                case  PaymentBank.XYKCD: 	// 重打签购单
                	payType = "16";
                break;
                case  PaymentBank.XYKQD: 	// 交易签到
                	payType = "1";
                break;
                case  PaymentBank.XYKJZ: 	// 交易结算
                	payType = "14";
                break;
                case  PaymentBank.XKQT1:    // 重打结算单
                    payType = "19";			
                break;
			 }
			 
			 /*
			 请求字符串（各字段长度为变长）
			 卡类型^收银机号^门店号^操作员号^交易类型^
			 交易金额^原始流水号^交易唯一标识^备注^系统参考号^
			  交易日期^卡号^有效期^授权号^产品编码^
			 支付活动^手机号^券名称^券编码^电子卡编码
			 01^001^123456^12345678^01^0.01^123456^12345678901234567890^abcdefghijklmnopqrstuvwxyz^123456789012^20110202^40091236478709871234^20110303^A77B88^6000^1234^^^^
			  */
			 StringBuffer sbReq = new StringBuffer();
			 sbReq.append(getBankType() + "^" + GlobalInfo.syjDef.syjh + "^" + GlobalInfo.sysPara.mktcode + "^" + GlobalInfo.posLogin.gh + "^" + payType + "^");
			 sbReq.append(ManipulatePrecision.doubleToString(money) + "^" + oldseqno + "^" + paySeq() + "^" + "" + "^" + oldauthno + "^");
			 sbReq.append(olddate + "^^^^^");
			 sbReq.append("^^^^");
			 PosLog.getLog(this.getClass()).info("TLBank_req=[" + sbReq.toString() + "]");
			
	         try
	         {
	            pw = CommonMethod.writeFile(path+"\\request.txt");
	            if (pw != null)
	            {
	                pw.println(sbReq.toString());
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
	         
			 return true;
		 }
		 catch (Exception ex)
		 {
			 PosLog.getLog(this.getClass()).info(ex);
			 new MessageBox("写入金卡工程请求数据异常!\n\n" + ex.getMessage(), null, false);			 
			 return false;
		 }
	}
	
	//读取result文件
	public boolean XYKReadResult()
	{
		BufferedReader br = null;
		
		try
        {
			if (!PathFile.fileExist(path+"\\result.txt") || ((br = CommonMethod.readFileGBK(path+"\\result.txt")) == null))
            {
            	XYKSetError("XX","读取应答失败,交易失败!");
                new MessageBox("读取金卡工程应答数据失败!", null, false);
                
                return false;
            }
			
			String line = br.readLine();

            if (line == null || line.length() <= 0)
            {
                return false;
            }
            
            /*
	                     返回字符串（各字段长度为变长）
	                     
	            授权号^余额^批次号^卡回收标志^卡种名称^卡号^卡类型^有效期^系统参考号^发卡行名称^
	            发卡行号^备注^商户名^商户号^原始流水号^操作员号^收银机号^流水号^产品名称^产品号码^
	            返回码^返回码解释^门店号^终端号^小费^总计金额^交易金额^交易唯一标识^交易日期^交易时间^
	            交易类型^产品编码^支付活动^手机号^券名称^券编码^电子卡编码
	
	           123456^0^501099^0^银行卡^^01^0000^536552289188^^^abcdefghijklmnopqrstuvwx^通联支付^309310083980001^^^^501151^^^00^交易成功^^00010001^0^0^0^^20110615^115052^1^6000^1234^^^^
	            
            */
			
            PosLog.getLog(this.getClass()).info("TLBank_res=[" + line + "]");
            String[] arrResult = line.split("^");            
            if (arrResult.length<37)
            {
            	XYKSetError("XX","失败：银行返回结果不符合规范!");
                new MessageBox("失败：银行返回结果不符合规范!", null, false);
            	return false;
            }

            int type = Integer.parseInt(bld.type.trim());
            bld.retcode  = arrResult[20];  //返回码
            bld.retmsg  = bld.retcode + "," + arrResult[21];  //返回码
            if(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX ||  type == PaymentBank.XYKTH )
        	{
            	bld.cardno = arrResult[5];   //卡号
            	bld.trace = Convert.toInt(arrResult[17]);   //流水号
            	bld.je = ManipulatePrecision.doubleConvert(Convert.toDouble(arrResult[26]));   //交易金额	
            	
    			//流水号|交易日期|交易时间|系统参考号|原始流水号|流水号
    			bld.tempstr=bld.trace + "|" + arrResult[28] + "|" + arrResult[29] + "|" + arrResult[8] + "|" + arrResult[14] + "|" + arrResult[17];
    		
    			PosLog.getLog(this.getClass().getSimpleName()).info("NSH_NEW_log=[" + bld.tempstr + "].");
        	}            
    	            
			return true;
        }
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass()).info(ex);
			XYKSetError("XX","读取应答XX:"+ex.getMessage());
            new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);            
			return false;
		}
		finally
        {
            if (br != null)
            {
                try
                {
                    br.close();
                    if (PathFile.fileExist(path + "\\request.txt"))
					{
						PathFile.deletePath(path + "\\request.txt");
					}

					if (PathFile.fileExist(path + "\\result.txt"))
					{
						PathFile.deletePath(path + "\\result.txt");
					}
                }
                catch (IOException e)
                {
                	PosLog.getLog(this.getClass()).info(e);
                }
            }
        }
	}
	
	public void XYKSetError(String errCode, String errInfo)
	{
		PosLog.getLog(this.getClass()).info("errCode:" + errCode + ",errInfo:" + errInfo);
		super.XYKSetError(errCode, errInfo);
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
	
	public boolean XYKNeedPrintDoc(int type)
	{
		if (!checkBankSucceed())
	    {
	        return false;
	    }
		if (  type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || 
			  type == PaymentBank.XYKTH || type == PaymentBank.XYKJZ || 
			  type == PaymentBank.XYKCD || type == PaymentBank.XKQT1 )
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
	
	
	public boolean checkDate(Text date)
	{
		String d = date.getText();
		if (d.length() > 8)
		{
			new MessageBox("日期格式错误\n日期格式《YYYYMMDD》");
			return false;
		}
		
		return true;
	}
	
	
	
	
}
