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
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
/**
 * 乌海万联
 * @author Administrator
 *
 */
		
public class Whwl_PaymentBankFunc extends PaymentBankFunc
{
	public static int billcount = 0;
	
	public String[] getFuncItem()
	{
		String[] func = new String[8];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[3] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
		func[4] = "[" + PaymentBank.XYKJZ + "]" + "交易结账";
		func[5] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
		func[6] = "[" + PaymentBank.XYKCD + "]" + "重打上笔签购单";
		func[7] = "[" + PaymentBank.XKQT1 + "]" + "重打结算单";
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
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";

				break;

			case PaymentBank.XYKCX: // 消费撤销
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";

				break;

			case PaymentBank.XYKYE: // 余额查询
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "余额查询";

				break;

			case PaymentBank.XYKJZ: // 结账
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "结账";

				break;

			case PaymentBank.XYKTH: // 隔日退货
				grpLabelStr[0] = null;
				grpLabelStr[1] = "原检索参考号";
				grpLabelStr[2] = "原交易日";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";

				break;

			case PaymentBank.XYKCD: // 签购单重打
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打上笔签购单";
				break;

			case PaymentBank.XKQT1: // 签购单重打
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
		// 0-4对应FORM中的5个输入框
		// null表示该需要用户输入,不为null用户不输入
		switch (type)
		{
			case PaymentBank.XYKXF: // 消费
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;

				break;

			case PaymentBank.XYKCX: // 消费撤销
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;

				break;

			case PaymentBank.XYKTH: // 退货
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;

				break;

			case PaymentBank.XYKYE: // 余额查询
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始余额查询";

				break;

			case PaymentBank.XYKQD: // 交易签到
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易签到";

				break;

			case PaymentBank.XYKJZ: // 内卡结账
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始结账";

				break;

			case PaymentBank.XYKCD: // 签购单重打上笔
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "开始重打上笔签购单";

				break;

			case PaymentBank.XKQT1: // 签购单重打指定
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "开始结算单重打";

				break;
		}

		return true;
	}

	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && 
					(type != PaymentBank.XYKTH) && (type != PaymentBank.XYKYE) && (type != PaymentBank.XYKQD)&& (type != PaymentBank.XYKJZ)&& (type != PaymentBank.XYKCD) &&
					(type != PaymentBank.XKQT1))
	            {
	                errmsg = "银联接口不支持该交易";
	                new MessageBox(errmsg);

	                return false;
	            }
			 
			 // 先删除上次交易数据文件
            if (PathFile.fileExist("C:\\JavaPos\\request.txt"))
            {
                PathFile.deletePath("C:\\JavaPos\\request.txt");
                
                if (PathFile.fileExist("C:\\JavaPos\\request.txt"))
                {
            		errmsg = "交易请求文件request.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            if (PathFile.fileExist("C:\\JavaPos\\result.txt"))
            {
                PathFile.deletePath("C:\\JavaPos\\result.txt");
                
                if (PathFile.fileExist("C:\\JavaPos\\result.txt"))
                {
            		errmsg = "交易请求文件result.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            if (PathFile.fileExist("C:\\JavaPos\\xyprint.txt"))
            {
                PathFile.deletePath("C:\\JavaPos\\xyprint.txt");
            }
            
            // 写入请求数据
            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
            {
                return false;
            }
            
            if (bld.retbz != 'Y')
            {
                // 调用接口模块
                if (PathFile.fileExist("C:\\JavaPos\\javaposbank.exe"))
                {
                	CommonMethod.waitForExec("C:\\JavaPos\\javaposbank.exe 	BJWL","javaposbank");
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
		
		int type = Integer.parseInt(bld.type.trim());
		
		if ((type == PaymentBank.XYKXF) || (type == PaymentBank.XYKCX) || 
				(type == PaymentBank.XYKTH) || (type == PaymentBank.XYKYE) || (type == PaymentBank.XYKQD) || (type == PaymentBank.XYKJZ) || (type == PaymentBank.XYKCD) ||
				(type == PaymentBank.XKQT1))
            {
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
		 String line = "";
		 String type1 = "";
		 PrintWriter pw = null;
		  String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
          jestr = Convert.increaseCharForward(jestr,'0',12);
          
         String  oldseqno1 = Convert.increaseCharForward(oldseqno,'0',6);
        
         String oldauthno1 = Convert.increaseChar(oldauthno,'0',12);
         
         String olddate1 = Convert.increaseChar(olddate,'0',8);
      
         String  syyh = Convert.increaseChar(GlobalInfo.posLogin.gh,' ', 20);
         
         String  syjh = Convert.increaseChar(ConfigClass.CashRegisterCode,' ',20);
		 
		 try
		 {
			 switch (type)
			 {
			 	case PaymentBank.XYKXF: 	// 消费
                    type1 = "S1";
                break;
			 	case PaymentBank.XYKCX: 	// 消费撤销
			 		type1 = "S2";
			 	break;	
                case PaymentBank.XYKTH:		// 隔日退货
                	type1 = "S3";
                break;
                case PaymentBank.XYKQD: 	// 签到交易
                    type1 = "Q1";
                break;
			 	case PaymentBank.XYKJZ: 	// 结算交易
			 		type1 = "Q2";
			 	break;	
                case PaymentBank.XYKCD:		// 重打印交易
                	type1 = "Q3";
                break;
                case  PaymentBank.XYKYE: 	// 余额查询
                	type1 = "S4";
                break;	
                case  PaymentBank.XKQT1: 	// 重打印结算单交易
                	type1 = "Q6";
                break;	
			 }
			 
			 //line = type1+",111111111111,111111,111111,"+oldseqno1+","+oldauthno1+","+olddate1+","+syyh+","+syjh;
			 line = type1+","+jestr+","+","+","+oldseqno1+","+oldauthno1+","+olddate1+","+syyh+","+syjh;
	         try
	         {
	            pw = CommonMethod.writeFile("C:\\JavaPos\\request.txt");
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
			if (!PathFile.fileExist("C:\\JavaPos\\result.txt") || ((br = CommonMethod.readFileGBK("C:\\JavaPos\\result.txt")) == null))
            {
            	XYKSetError("XX","读取金卡工程应答数据失败!");
                new MessageBox("读取金卡工程应答数据失败!", null, false);
                
                return false;
            }
			
			String line = br.readLine();

            if (line == null || line.length() <= 0)
            {
                return false;
            }
            
            String result[] = line.split(",");
            if (result == null) return false;
            
            	bld.retcode     = result[0];
            	if(bld.retcode.equals("00"))
            	{
            		bld.retmsg		= result[1];
            		if(result.length>5)bld.cardno 	= result[6];
            		if(result.length>9 && !result[10].trim().equals(""))bld.trace 	= Long.parseLong(result[10]);
            		if(result.length>11)bld.authno 	= result[12];
            		if(result.length>16)bld.je = ManipulatePrecision.doubleConvert(Double.parseDouble(result[17]) / 100,2,1);
            	}else{
            		bld.retmsg		= result[1];
            		return false;
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
		if(GlobalInfo.sysPara.bankprint<1) return;
		try
		{
			String printName = "";
			
			//int type = Integer.parseInt(bld.type.trim());
			 
			if (!PathFile.fileExist("C:\\JavaPos\\xyprint.txt"))
            {	
				new MessageBox("签购单文本不存在无法打印!", null, false);
                return;
            }
			else
            {
                printName = "C:\\JavaPos\\xyprint.txt";
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
	                 int num = 0;
	                 
	            	 while ((line = br.readLine()) != null)
	            	 {
	            		 if (line.length() <= 0)
	            		 {
                            continue;
	            		 }
	            		 
	            		 if (line.indexOf("?") >= 0) continue;
	            			 
	            		 if (line.charAt(0) == (char)0x0c)
	            		 {
	            			 if (num >= 1)
	            			 {
	            				 break;
	            			 }
	            			 
	            			 for (int j = 1;j <= 5;j++)
	            			 {
	            				 XYKPrintDoc_Print("\n");
	            			 }
	            			 
	            			 new MessageBox("请撕下商户签购单" );
	            			 num = num + 1;
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
			
			if (PathFile.fileExist("C:\\JavaPos\\xyprint.txt"))
            {
                PathFile.deletePath("C:\\JavaPos\\xyprint.txt");
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
