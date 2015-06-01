package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

public class Bjjw_PaymentBankFunc extends PaymentBankFunc
{
	public String[] getFuncItem()
    {
        String[] func = new String[10];

        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
        func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
        func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
        func[3] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
        func[4] = "[" + PaymentBank.XYKJZ + "]" + "银联结算";
        func[5] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
        func[6] = "[" + PaymentBank.XYKCD + "]" + "重打签购单";
        func[7] = "[" + PaymentBank.XKQT1 + "]" + "按凭证号打印";
        func[8] = "[" + PaymentBank.XKQT2 + "]" + "参数管理";
        func[9] = "[" + PaymentBank.XKQT3 + "]" + "打印交易统计";

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
				grpLabelStr[0] = "原参考号";
				grpLabelStr[1] = null;
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
        	case PaymentBank.XYKCD: //签购单重打
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "重打上笔签购单";
            break;
        	case PaymentBank.XKQT1:		// 按凭证号打印
        		grpLabelStr[0] = "原凭证号";
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "按凭证号打印";
            break;    
        	case PaymentBank.XKQT2:		// 参数管理
        		grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "参数管理";
        	break;	
        	case PaymentBank.XKQT3:		// 打印交易统计
        		grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "打印交易统计";
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
		 	case PaymentBank.XYKCD: 	//签购单重打
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键签购单重打";
            break;
		 	case PaymentBank.XKQT1:		// 按凭证号打印
		 		grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键凭证号打印";
		 	break;	
		 	case PaymentBank.XKQT2:		// 参数管理
		 		grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键参数管理";
		 	break;	
		 	case PaymentBank.XKQT3:		// 打印交易统计
		 		grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键打印交易统计";
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
				(type != PaymentBank.XYKJZ) && (type != PaymentBank.XYKYE) && 
				(type != PaymentBank.XYKCD) && (type != PaymentBank.XKQT1) && 
				(type != PaymentBank.XKQT2) && (type != PaymentBank.XKQT3))
            {
                errmsg = "银联接口不支持该交易";
                new MessageBox(errmsg);

                return false;
            }
			
			 // 先删除上次交易数据文件
            if (PathFile.fileExist("c:\\request.txt"))
            {
                PathFile.deletePath("c:\\request.txt");
                
                if (PathFile.fileExist("c:\\request.txt"))
                {
            		errmsg = "交易请求文件request.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            if (PathFile.fileExist("c:\\result.txt"))
            {
                PathFile.deletePath("c:\\result.txt");
                
                if (PathFile.fileExist("c:\\result.txt"))
                {
            		errmsg = "交易请求文件result.txt无法删除,请重试";
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
                if (PathFile.fileExist("c:\\javaposbank.exe"))
                {
                	CommonMethod.waitForExec("c:\\javaposbank.exe BJJW");
                }
                else
                {
                    new MessageBox("找不到金卡工程模块 javaposbank.exe");
                    XYKSetError("XX","找不到金卡工程模块 javaposbank.exe");
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
            
            // 	打印签购单
            if (XYKNeedPrintDoc())
            {
                XYKPrintDoc();
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
	
	 public boolean XYKNeedPrintDoc()
	 {
        if (!checkBankSucceed())
        {
            return false;
        }
        
        return true;
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
	  
	 public boolean XYKWriteRequest(int type, double money, String track1,String track2, String track3,String oldseqno, String oldauthno,String olddate, Vector memo)
	 {
		 try
	     {
			 String line = "";
			 		 
	         String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
	         
	         for (int i = jestr.length(); i < 12; i++)
             {
                 jestr = "0" + jestr;
             }
	         
	         //流水号
	         String seq = "";
	         int length = 6;
	         
	         if (type == PaymentBank.XYKTH)
	         {
	        	 length = 12;
	         }
	         
	         if ((oldseqno == null) || (oldseqno.length() <= 0))
	         {
                seq = Convert.increaseLong(0,length);
	         }
	         else
	         {
                try
                {
                    long num_seq = Long.parseLong(oldseqno);
                    seq = Convert.increaseLong(num_seq,length);
                }
                catch (Exception er)
                {
                    seq = Convert.increaseLong(0,length);
                }
	         }
	        
	         if (olddate != null && !olddate.equals("") && olddate.length() >= 4)
	         {
	        	 olddate = olddate.substring(4);
	         }
	         else
	         {
	        	 olddate = "0000";
	         }
	         
	         //	根据不同的类型生成文本结构
	         switch (type)
	         {
	         	case PaymentBank.XYKXF:
	         		 type = 3;
	         		 line = Convert.increaseInt(type,2) + "," + jestr + "," + seq + "," + Convert.increaseInt(0,12) + "," + olddate + "," + "00" ;
	         	break;
	         	case PaymentBank.XYKCX:
	         		type = 4;
	         		line = Convert.increaseInt(type,2) + "," + jestr + "," + seq + "," + Convert.increaseInt(0,12) + "," + olddate + "," + "00" ;
	         	break;	
	         	case PaymentBank.XYKTH:
	         		type = 5;
	         		line = Convert.increaseInt(type,2) + "," + jestr + "," + Convert.increaseInt(0,6) + "," + seq + "," + olddate + "," + "00" ;
	         	break;	
	        	case PaymentBank.XKQT1:
	        		type = 23;
	        		line = Convert.increaseInt(type,2) + "," + jestr + "," + seq + "," + Convert.increaseInt(0,12) + "," + olddate + "," + "00" ;	
		        break;	
	         	case PaymentBank.XYKQD:	
	         		type = 0;
	         		line = Convert.increaseInt(type,2) + "," + jestr + "," + seq + "," + Convert.increaseInt(0,12) + "," + olddate + "," + "00" ;	
	         	break;	
	         	case PaymentBank.XYKJZ:
	         		type = 2;
	         		line = Convert.increaseInt(type,2) + "," + jestr + "," + seq + "," + Convert.increaseInt(0,12) + "," + olddate + "," + "00" ;	
	         	break;	
	         	case PaymentBank.XYKYE:
	         		type = 8;
	         		line = Convert.increaseInt(type,2) + "," + jestr + "," + seq + "," + Convert.increaseInt(0,12) + "," + olddate + "," + "00" ;	
	         	break;	
	         	case PaymentBank.XYKCD:
	         		type = 22;
	         		line = Convert.increaseInt(type,2) + "," + jestr + "," + seq + "," + Convert.increaseInt(0,12) + "," + olddate + "," + "00" ;	
	         	break;	
	         	case PaymentBank.XKQT2:
	         		type = 24;
	         		line = Convert.increaseInt(type,2) + "," + jestr + "," + seq + "," + Convert.increaseInt(0,12) + "," + olddate + "," + "00" ;	
	         	break;	
	         	case PaymentBank.XKQT3:
	         		type = 27;
	         		line = Convert.increaseInt(type,2) + "," + jestr + "," + seq + "," + Convert.increaseInt(0,12) + "," + olddate + "," + "00" ;	
	         	break;	
	         }
	         
	         PrintWriter pw = null;
	            
	         try
	         {
	            pw = CommonMethod.writeFile("c:\\request.txt");
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
        	if (!PathFile.fileExist("c:\\result.txt") || ((br = CommonMethod.readFileGBK("c:\\result.txt")) == null))
            {
            	XYKSetError("XX","读取金卡工程应答数据失败!");
                new MessageBox("读取金卡工程应答数据失败!", null, false);

                return false;
            }
        	
        	String line = br.readLine();

            if (line.length() <= 0)
            {
                return false;
            }
            
            line = line.replaceAll(",,",", ,");
            
            String result[] = line.split(",");
            
            if (result == null) return false;
                        
            bld.retcode 	= result[0];
            bld.retmsg 		= result[1];
            bld.bankinfo	= result[6];
            bld.cardno		= result[8];
            
            if (result[12] != null && !result[12].trim().equals(""))
            {
            	bld.trace		= Long.parseLong(result[12].trim());
            }
        		
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
	 
	 public void XYKPrintDoc()
	 {
		 ProgressBox pb = null;
		 
		 try
		 {
			 String printName = "";
			 
			 int type = Integer.parseInt(bld.type.trim());
			 
			 if ((type == PaymentBank.XYKXF || type == PaymentBank.XYKCX  || type == PaymentBank.XYKTH ||
					 type == PaymentBank.XYKCD || type == PaymentBank.XKQT1 
			 	))
			 {
				 if (!PathFile.fileExist("C:\\prnRecipt"))
	                {
	                    new MessageBox("找不到签购单打印文件!");

	                    return;
	                }
	                else
	                {
	                    printName = "C:\\prnRecipt";
	                }
			 }
			 else if(type == PaymentBank.XYKJZ || type == PaymentBank.XKQT3 )
			 {
				 if (!PathFile.fileExist("C:\\prnTotal"))
	                {
	                    new MessageBox("找不到统计单打印文件!");

	                    return;
	                }
	                else
	                {
	                    printName = "C:\\prnTotal";
	                }
			 }
			 else
			 {
	             return;
			 }
			 
			 pb = new ProgressBox();
	         pb.setText("正在打印银联签购单,请等待...");
	         
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
                        if (line.trim().equals("CUTPAPPER"))
                        {
                            break;
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
			 new MessageBox("打印签购单发生异常\n\n" + ex.getMessage());
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
	 
}
