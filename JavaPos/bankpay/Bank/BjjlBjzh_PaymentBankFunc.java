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
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.Bzhx_PaymentBankFunc;


public class BjjlBjzh_PaymentBankFunc extends Bzhx_PaymentBankFunc
{
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
                grpLabelStr[0] = "原流水号";
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易金额";

            break;
            case PaymentBank.XYKTH: //隔日退货   
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "无效";

            break;
        	case PaymentBank.XYKQD: //交易签到
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易签到";
            break;
        	case PaymentBank.XYKJZ: //中行结账
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "中行结账";
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
        	case PaymentBank.XKQT1: //	资和信卡消费
        		grpLabelStr[0] = null;
 	            grpLabelStr[1] = null;
 	            grpLabelStr[2] = null;
 	            grpLabelStr[3] = null;
 	            grpLabelStr[4] = "交易金额";
        	break;	
        	case PaymentBank.XKQT2: //	积分卡消费
        		grpLabelStr[0] = null;
 	            grpLabelStr[1] = null;
 	            grpLabelStr[2] = null;
 	            grpLabelStr[3] = null;
 	            grpLabelStr[4] = "交易金额";
            break;	
        	case PaymentBank.XKQT3: //	信贷卡消费撤销
        		grpLabelStr[0] = "原流水号";
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易金额";
            break;
        	case PaymentBank.XKQT4: //积分卡消费撤销
                grpLabelStr[0] = "原流水号";
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易金额";

            break;
        	case PaymentBank.XKQT5: //信贷卡卡结账
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "信贷卡结账";
            break;
         	case PaymentBank.XKQT6: //积分卡结账
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "积分卡结账";
            break;
         	case PaymentBank.XKQT7: //重打中行结账单
         		grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "重打中行结账单";
         	break;	
         	case PaymentBank.XKQT8: //重打信贷卡结账单
         		grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "重打信贷卡结账单";
         	break;	
         	case PaymentBank.XKQT9: //重打资和信结账单
         		grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "重打积分卡结账单";
         	break;	
        }
		
		return true;
    }
	
	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{
		 try
		 { 
			 if (type == PaymentBank.XYKTH)
			 {
                errmsg = "银联接口不支持该交易";
                new MessageBox(errmsg);

                return false;
			 }
			 
			 //	先删除上次交易数据文件
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
            		errmsg = "交易结果文件result.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
			 }
			 
			 if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo)) return false;
			 
			 if (bld.retbz != 'Y')
			 {
                // 调用接口模块
                if (PathFile.fileExist("c:\\javaposbank.exe"))
                {
                	CommonMethod.waitForExec("c:\\javaposbank.exe BJJLBJZH");
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
			 
			 //	打印签购单
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
	
	public boolean XYKWriteRequest(int type, double money, String track1,String track2, String track3,String oldseqno, String oldauthno,String olddate, Vector memo)
	{
		 try
		 {
			 String line = "";

	         String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
	            
	         //	流水号
	         String seq = "";
	           
	         if ((oldseqno == null) || (oldseqno.length() <= 0))
	         {
                seq = Convert.increaseInt(0, 6);
	         }
	         else
	         {
                try
                {
                    int num_seq = Integer.parseInt(oldseqno);
                    seq = Convert.increaseInt(num_seq, 6);
                }
                catch (Exception er)
                {
                    seq = Convert.increaseInt(0, 6);
                }
	         }
	         
	         //根据不同的类型生成文本结构
	         switch (type)
	         {
	         	case PaymentBank.XYKXF:				// 银联消费
	         	case PaymentBank.XKQT2:				// 积分卡消费	
	         	case PaymentBank.XKQT1:				// 资合信卡消费
	         		line = String.valueOf(type) + "," + jestr;
	         	break;
	         	case PaymentBank.XYKCX:				// 银联撤消
	         	case PaymentBank.XKQT3:				// 资合信卡撤消
	         	case PaymentBank.XKQT4:				// 积分卡消费撤消		
	         		line = String.valueOf(type) + "," + seq + "," + jestr;
	         	break;
	         	case PaymentBank.XYKYE:				// 查询余额
	         		line = String.valueOf(type);
	         	break;
	         	case PaymentBank.XYKQD:				// 交易签到
	         		line = String.valueOf(type);
	         	break;
	         	case PaymentBank.XYKJZ:				// 银联卡结账
	         	case PaymentBank.XKQT5:				// 资和信卡结账
	         	case PaymentBank.XKQT6:				// 积分卡结账
	         		line = String.valueOf(type);
	         	break;	
	         	default:
                    bld.retbz = 'Y';
	         	return true;
	         	
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
				 new MessageBox("没有读到银联返回码!", null, false);
				 return false;
			 }
			 
             bld.retcode = line.trim();
			  
             BufferedReader br1 = null;
             String line1 = null;
             
             try
             {
	             if (bld.retcode.equals("0"))
	             {
	            	 if (PaymentBank.XYKQD == Integer.parseInt(bld.type)) return true;
	            	 
	            	 if (PaymentBank.XYKYE ==  Integer.parseInt(bld.type)) return true;
	            	 
	            	 if ( PaymentBank.XYKJZ ==  Integer.parseInt(bld.type)) return true;
	            	 
	            	 if (PaymentBank.XKQT5 ==  Integer.parseInt(bld.type)) return true;
	            	 
	            	 if (PaymentBank.XKQT6 ==  Integer.parseInt(bld.type)) return true;
	            		 
	            	 br1 = CommonMethod.readFileGBK("c:\\u\\log\\card.txt");
	            	 
	            	 if (br1 == null)
					 {
	                   new MessageBox("打开c:\\u\\log\\card.txt打印文件失败!");
	
	                   return false;
					 }
	            	 
	            	 line1 = br1.readLine();
	            	 
	            	 if (line1.length() <= 0)
	    			 {
	    				 new MessageBox("没有读到流水号和卡号,收银机无法记录日志!", null, false);
	    			 }
	            	 
	            	 if (line1.split(",").length >= 1)
	            	 {
	            		 bld.trace = Convert.toLong(line1.split(",")[0].trim());
	            	 }
	            	 
	            	 if (line1.split(",").length >= 2)
	            	 {
	            		 bld.cardno = line1.split(",")[1];
	            	 }
	            	 
	             }
	             else
	             {
	            	 br1 = CommonMethod.readFileGBK("c:\\u\\log\\err.txt");
	            	 
	            	 if (br1 == null)
					 {
	                   new MessageBox("打开c:\\u\\log\\err.txt打印文件失败!");
	
	                   return false;
					 }
	            	 
	            	 line1 = br1.readLine();
	            	 
	            	 if (line1.length() <= 0)
	    			 {
	    				 new MessageBox("没有读到错误日志!", null, false);
	    			 }
	            	 
	            	 bld.retmsg = line1.trim();
	             }
             }
             catch (Exception ex)
             {
            	 ex.printStackTrace();
            	 new MessageBox(ex.getMessage());
             }
             finally
			 {
            	 if (br1 != null)
            	 {
            		 br1.close();
            		 br1 = null;
            	 }
			 }
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
                   br = null;
               }
               catch (IOException e)
               {
                   e.printStackTrace();
               }
			 }
		 }

		 return true;
	 }
}
