package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;

/*
 * 中免(建行接口)
 */
public class SnYlZMJH_PaymentBankFunc_BAK extends SnYl1_PaymentBankFunc {
	
	public String getbankfunc()
	{
		return "C:\\ftipos\\";
	}
	
	public String[] getFuncItem()
    {
        String[] func = new String[6];
        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
        func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
        func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
        func[3] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
        func[4] = "[" + PaymentBank.XYKJZ + "]" + "银联结算";
        func[5] = "[" + PaymentBank.XYKYE + "]" + "余额查询";

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
                grpLabelStr[0] = "原凭证号";
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易金额";
            break;
        	case PaymentBank.XYKTH://隔日退货   
				grpLabelStr[0] = null;
				grpLabelStr[1] = "原参考号";;
				grpLabelStr[2] = "原交易日";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
			break;
        	case PaymentBank.XYKQD: //交易签到
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易签到";
            break;
        	case PaymentBank.XYKJZ: //银联结账
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "银联结账";
            break;
        	case PaymentBank.XYKYE: //余额查询    
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "余额查询";
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
		 	case PaymentBank.XYKQD: 	//交易签到
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始交易签到";
            break;
		 	case PaymentBank.XYKJZ: 	//银联结账
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始银联结账";
            break;
		 	case PaymentBank.XYKYE: 	//余额查询    
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始余额查询";
            break;
		 
		
		}
		
		return true;
    }
	
	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && 
				(type != PaymentBank.XYKTH) && (type != PaymentBank.XYKQD) && 
				(type != PaymentBank.XYKJZ) && (type != PaymentBank.XYKYE) && (type != PaymentBank.XYKCD) && (type != PaymentBank.XKQT1) && (type != PaymentBank.XKQT2) && (type != PaymentBank.XKQT3) && (type != PaymentBank.XKQT4))
            {
                errmsg = "银联接口不支持该交易";
                new MessageBox(errmsg);

                return false;
            }
			
			 // 先删除上次交易数据文件
            if (PathFile.fileExist(getbankfunc() + "request.txt"))
            {
                PathFile.deletePath(getbankfunc() + "request.txt");
                
                if (PathFile.fileExist(getbankfunc() + "request.txt"))
                {
            		errmsg = "交易请求文件" + getbankfunc() + "request.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            if (PathFile.fileExist(getbankfunc() + "result.txt"))
            {
                PathFile.deletePath(getbankfunc() + "result.txt");
                
                if (PathFile.fileExist(getbankfunc() + "result.txt"))
                {
            		errmsg = "交易请求文件" + getbankfunc() + "result.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            if (PathFile.fileExist(getbankfunc() + "print.txt"))
            {
                PathFile.deletePath(getbankfunc() + "print.txt");
                
                if (PathFile.fileExist(getbankfunc() + "print.txt"))
                {
            		errmsg = "交易请求文件" + getbankfunc() + "print.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            //  写入请求数据
            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
            {
                return false;
            }
            
            if (bld.retbz != 'Y')
            {
            	
                // 调用接口模块
                if (PathFile.fileExist(getbankfunc() + "javaposbank.exe"))
                {
                	CommonMethod.waitForExec(getbankfunc() + "javaposbank.exe ZMSNYL");
                }
                else
                {
                    new MessageBox("找不到金卡工程模块 " + getbankfunc() + "javaposbank.exe");
                    XYKSetError("XX","找不到金卡工程模块 " + getbankfunc() + "javaposbank.exe");
                    return false;
                }
                
                // 读取应答数据
                if (!XYKReadResult())
                {
                    return false;
                }
                
                // 检查交易是否成功
                XYKCheckRetCode();
            }
            
//            // 	打印签购单
//            if (XYKNeedPrintDoc())
//            {
//                XYKPrintDoc();
//            }
//            
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
	
	 public boolean XYKWriteRequest(int type, double money, String track1,String track2, String track3,String oldseqno, String oldauthno,String olddate, Vector memo)
	 {
		 try
	     {
			 String line = "";
			 String type1 = "";
			 		 
	         String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
	         
	         for (int i = jestr.length(); i < 12; i++)
             {
                 jestr = "0" + jestr;
             }
	         
	     	//流水号
	         String strseqno="";
				if (oldseqno != null)
				{
					strseqno = Convert.increaseChar(oldseqno,'0', 6);
				}
				else
				{
					strseqno = Convert.increaseChar("0", 6);
				}
	        
//				终端号
				String stroldterm="";
				if (oldauthno != null)
				{
					stroldterm = Convert.increaseChar(oldauthno,'0', 12);
				}
				else
				{
					stroldterm = Convert.increaseChar("0", 12);
				}
				
//				 原交易日期
				String strolddate="";
				if (olddate != null)
				{
					strolddate = Convert.increaseChar(olddate, 4);
				}
				else
				{
					strolddate = Convert.increaseChar("", 4);
				}
	         
		         String syjh = Convert.increaseChar(ConfigClass.CashRegisterCode,' ', 4);
		         
			    String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh,' ', 4);
			    
			   // String qt = Convert.increaseChar("", 100);
	         
	         //	根据不同的类型生成文本结构
	         switch (type)
	         {
	         	case PaymentBank.XYKXF:
	         		type1 = "22";
	         	break;
	         	case PaymentBank.XYKCX:
	         		type1 = "23";
	         	break;	
	         	case PaymentBank.XYKTH:
	         		type1 = "25";
	         	break;	
	         	case PaymentBank.XYKQD:	
	         		type1 = "00";	
	         	break;	
	         	case PaymentBank.XYKJZ:
	         		type1 = "02";	
	         	break;	
	         	case PaymentBank.XYKYE:
	         		type1 = "03";	
	         	break;
	         }
	         
	         line = type1+","+jestr+","+strolddate+","+strseqno+","+stroldterm+","+syjh+","+syyh;
	         
	         PrintWriter pw = null;
	            
	         try
	         {
	            pw = CommonMethod.writeFile(getbankfunc() + "request.txt");
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
		        
			 return true;
	     }
		 catch (Exception ex)
		 {
			 new MessageBox("写入金卡工程请求数据异常!\n\n" + ex.getMessage(), null, false);
	         ex.printStackTrace();

	         return false;
		 }
	 }
	 
	 public boolean XYKReadResult()
	 {
        BufferedReader br = null;
        
        try
        {
        	if (!PathFile.fileExist(getbankfunc() + "result.txt") || ((br = CommonMethod.readFileGBK(getbankfunc() + "result.txt")) == null))
            {
            	XYKSetError("XX","读取金卡工程应答数据失败!");
                new MessageBox("读取金卡工程应答数据失败!", null, false);

                return false;
            }
        	
        	String line = br.readLine();
        	PosLog.getLog(this.getClass().getSimpleName()).info("XYKReadResult_JH result=[" + line + "].");
            if (line.length() <= 0)
            {
                return false;
            }
            
            String result[] = line.split(",");
            
            if (result == null) return false;
             
            bld.retcode 	= result[0];
            if(bld.retcode.equals("00")){
            	bld.je = ManipulatePrecision.doubleConvert(Double.parseDouble(result[2])/100, 2, 1);
            	bld.cardno = result[6];
                bld.retmsg = result[1];
            }
            else{
            	bld.retmsg = result[1];
            	return false;
            }
            

			//wangyong add by 2014.01.13
            bld.trace = Convert.toLong(result[9].trim());//流水号
            bld.authno = result[14].trim();//参考号
            String strSQH = result[13].trim();//授权号
            String batchNo = result[8].trim();//批次号
            String strDate = result[11].trim();//交易日期
            //String strTime = result[12].trim();//交易时间            			
			String strInfo="";//交易信息,旧接口里没有,则为空
			
			//原流水号|原参考号|原授权号|原批次号|原交易日期|交易信息
            bld.tempstr = bld.trace + "|" + bld.authno + "|" + strSQH + "|" + batchNo + "|" + strDate + "|" + strInfo;

			PosLog.getLog(this.getClass().getSimpleName()).info("JH_log=[" + bld.tempstr + "].");
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
            if (br != null)
            {
                try
                {
                    br.close();
                    
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
	 }
/*	 
//	 输出参数
		strncpy(szResult[0],responseMsg.szRspCode,2);
		strncpy(szResult[1],responseMsg.szRspMsg,40);
		strncpy(szResult[2],responseMsg.szAmount,12);
		strncpy(szResult[3],responseMsg.szMerchName,40);
		strncpy(szResult[4],responseMsg.szMerchId,15);

		strncpy(szResult[5],responseMsg.szTermId,8);
		strncpy(szResult[6],responseMsg.szCardNo,19);
		strncpy(szResult[7],responseMsg.szExpDate,4);
		strncpy(szResult[8],responseMsg.szBatchNo,6);
		strncpy(szResult[9],responseMsg.szTraceNo,6);

		strncpy(szResult[10],responseMsg.szOrgTraceNo,6);
		strncpy(szResult[11],responseMsg.szTransDate,8);
		strncpy(szResult[12],responseMsg.szTransTime,6);
		strncpy(szResult[13],responseMsg.szAuthCode,6);
		strncpy(szResult[14],responseMsg.szRRN,12);

		strncpy(szResult[15],responseMsg.szIssurId,11);
		strncpy(szResult[16],responseMsg.szCardName,20);
		strncpy(szResult[17],responseMsg.szCardIdx,3);
*/
	 
	/* typedef struct { 
		    char szTransType[2+1];       // 交易类型 
		    char szAmount[12+1];         // 交易金额 
		    char szOrgDate[4+1];         // 原交易日期 
		    char szOrgTrace[6+1];        // 原终端流水号 
		    char szOrgRrn[12+1];         // 原系统参考号 
		    char szEcrTid[4+1];          // 收款台号 
		    char szEcrUid[4+1];          // 收款台操作员号 
		   // char szTransMemo[100+1];      // 交易其他信息    如位数不够，左对齐，右补空格 
		} REQ_MSG_NULL2; 

		typedef struct 
		{ 
		    交易结果 
		    char  szRspCode[2+1];        //交易结果（00 表示交易成功） 
		    char  szRspMsg[40+1];        //结果描述（字符串，不足补空格） 
		    交易数据 
		    char  szAmount[12+1];        //交易金额（撤销、退货时为原交易金额）
		    char  szMerchName[40+1];     //商户名称  
		    char  szMerchId[15+1];       //商户编号 

		    char  szTermId[8+1];         //终端编号 
		    char  szCardNo[19+1];        //交易卡号 
		    char  szExpDate[4+1];        //卡片有效期 
		    char  szBatchNo[6+1];        //交易批次号 
		    char  szTraceNo[6+1];        //交易凭证号 

		    char  szOrgTraceNo[6+1];     //原交易凭证号（撤销、退货时存在） 
		    char  szTransDate[8+1];      //交易日期 
		    char  szTransTime[6+1];      //交易时间 
		    char  szAuthCode[6+1];       //授权号 
		    char  szRRN[12+1];           //系统参考号 

		    char  szIssurId[11+1];       //收单机构代码 
		    char  szCardName[20+1];      //发卡行名称 
		    char  szCardIdx[11+1];        //卡标识  CUP /VIS 
		}RSP_MSG_NULL2; 
*/
	
}
