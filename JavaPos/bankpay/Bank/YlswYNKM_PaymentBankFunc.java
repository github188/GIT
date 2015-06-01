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
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
/**
*复制江苏扬州银联：中亚糖酒(无界面模式)
*/
public class YlswYNKM_PaymentBankFunc extends PaymentBankFunc
{	
	String path = "";
	Vector errVector;
	 
	public String[] getFuncItem()
	{
		String[] func = new String[9];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[3] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
		func[4] = "[" + PaymentBank.XYKJZ + "]" + "交易结账";
		func[5] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
		func[6] = "[" + PaymentBank.XYKCD + "]" + "重打上笔签购单";
		func[7] = "[" + PaymentBank.XKQT1 + "]" + "重打指定签购单";
		func[8] = "[" + PaymentBank.XKQT2 + "]" + "当日明细查询";
		
		return func;
	}
	
	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		//0-4对应FORM中的5个输入框
		//null表示该不用输入
		switch (type)
		{
			case PaymentBank.XYKXF://消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKCX://消费撤销
				grpLabelStr[0] = "凭证号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKTH://隔日退货   
				grpLabelStr[0] = "原凭证号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = "原交易日";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKQD://交易签到
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易签到";
				break;
			case PaymentBank.XYKJZ://交易结账
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易结账";
				break;
			case PaymentBank.XYKYE://余额查询    
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "余额查询";
				break;
			case PaymentBank.XYKCD://重打上笔签购单
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打上笔签单";
				break;
			case PaymentBank.XKQT1://重打指定签购单
				grpLabelStr[0] = "原凭证号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打指定签购单";
				break;
			case PaymentBank.XKQT2://重打指定签购单
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "查看当日明细";
				break;
				
		}

		return true;
	}
	
	public boolean getFuncText(int type, String[] grpTextStr)
	{
		//0-4对应FORM中的5个输入框
		//null表示必须用户输入,不为null表示缺省显示无需改变
		switch (type)
		{
			case PaymentBank.XYKXF://消费
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKCX://消费撤销
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKTH://隔日退货   
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKQD://交易签到
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易签到";
				break;
			case PaymentBank.XYKJZ://交易结账
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易结账";
				break;
			case PaymentBank.XYKYE://余额查询    
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始余额查询";
				break;
			case PaymentBank.XYKCD://签购单上笔重打
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始签购单重打";
				break;
			case PaymentBank.XKQT1://签购单上笔重打
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始重打指定签单";
				break;
			case PaymentBank.XKQT2://签购单上笔重打
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始查看当日明细";
				break;
		}	
		
		return true;
	}
		
	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if ((type != PaymentBank.XYKXF) && 
				(type != PaymentBank.XYKCX) && 
				(type != PaymentBank.XYKYE) && 
				(type != PaymentBank.XYKCD) && 
				(type != PaymentBank.XYKQD) &&
				(type != PaymentBank.XYKJZ) &&
				(type != PaymentBank.XKQT1) &&
				(type != PaymentBank.XYKTH) &&
				(type != PaymentBank.XKQT2)
				
				)
	            {
	                errmsg = "银联接口不支持该交易";
	                new MessageBox(errmsg);

	                return false;
	            }
			path = getBankPath(paycode);
			 // 先删除上次交易数据文件
            if (PathFile.fileExist(path  + "\\request.txt"))
            {
                PathFile.deletePath(path  + "\\request.txt");
                
                if (PathFile.fileExist(path  + "\\request.txt"))
                {
            		errmsg = "交易请求文件request.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            if (PathFile.fileExist(path  + "\\result.txt"))
            {
                PathFile.deletePath(path  + "\\result.txt");
                
                if (PathFile.fileExist(path  + "\\result.txt"))
                {
            		errmsg = "交易请求文件result.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            //查询当日明细不需传入数据，直接调用gmc.exe
            if (type == PaymentBank.XKQT2)
            {
//            	 调用接口模块
    			if (PathFile.fileExist(path + "\\gmc.exe"))
    			{
    				CommonMethod.waitForExec(path + "\\gmc.exe");
    			}
    			else
    			{
    				new MessageBox("找不到金卡工程模块 gmc.exe");
    				XYKSetError("XX", "找不到金卡工程模块 gmc.exe");
    				return false;
    			}
    			
    			bld.retcode = "00";
				bld.retmsg = "银联交易成功";
				errmsg = bld.retmsg;
  			
				return true;
            }
            
//          写入请求数据
			String line = XYKgetRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo);

			PrintWriter pw = CommonMethod.writeFile(path + "\\request.txt");

			if (pw != null)
			{
				pw.println(line);
				pw.flush();
				pw.close();
			}
            
			// 调用接口模块
			if (PathFile.fileExist(path + "\\javaposbank.exe"))
			{
				CommonMethod.waitForExec(path + "\\javaposbank.exe ZYTJ");
			}
			else
			{
				new MessageBox("找不到金卡工程模块 javaposbank.exe");
				XYKSetError("XX", "找不到金卡工程模块 javaposbank.exe");
				return false;
			}
                
                // 读取应答数据
                if (!XYKReadResult1(type))
                {
                    return false;
                }
                
                
                // 检查交易是否成功
                XYKCheckRetCode();
        
            //通过收银机参数 （OY）设置为银行POS机打印，则JavaPos不打印   
            if (GlobalInfo.sysPara.issetprinter == 'Y')
            	return true;
            
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

	public boolean XYKCheckRetCode()
	{
		if (bld.retcode.trim().equals("00"))
		{
			bld.retbz = 'Y';
			bld.retmsg = "金卡工程调用成功";

			return true;
		}
		else
		{
			bld.retbz = 'N';

			return false;
		}
	}

	
	public String XYKgetRequest(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		 String line = "";
		 String mtype1 ="";
		 try
		 {
			 switch (type)
			 {
			 	case PaymentBank.XYKXF: 	// 消费
			 		mtype1 = "00";
                break;
			 	case PaymentBank.XYKCX: 	// 消费撤销
			 		mtype1 = "01";
			 	break;	
                case PaymentBank.XYKTH:		// 隔日退货
                	mtype1 = "02";
                break;
                case  PaymentBank.XYKYE: 	// 余额查询
                	mtype1 = "03";
                break;	
                case PaymentBank.XYKCD: 	// 重打上笔签购单
                	mtype1 = "04";
                break;
                case PaymentBank.XKQT1: 	// 重打上笔签购单
                	mtype1 = "04";
                break;
                case PaymentBank.XYKQD:     // 交易签到
                	mtype1 = "05";
                break;
                case PaymentBank.XYKJZ:     // 交易结帐
                	mtype1 = "06";
                break;
			 }
			 
				// 收银机号
				String syjh = Convert.increaseChar(ConfigClass.CashRegisterCode, ' ', 8);
				// 收银员号
				String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 8);
		         //交易类型
		         String type1 = mtype1;
		     	//交易金额
				String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1));
				jestr = Convert.increaseCharForward(jestr, '0', 12);

				   //原交易日期
		         String olddate1 = Convert.increaseChar(olddate,' ',8);
		         
		         //原系统参考号
		         String oldsysref = "";
		         if (type == PaymentBank.XYKTH)
		         {
		        	 oldsysref = oldseqno;
		         }
		         else
		         {
		        	 oldsysref = Convert.increaseChar(oldsysref,' ',12);	 
		         }
		         
		         String authno;
		         if (type == PaymentBank.XYKXF)
		         {
		        	 //原流水号
		        	 authno = "000000";
		         }
		         else
		         {
			         //原流水号
		        	 authno = Convert.increaseCharForward(oldseqno,'0',6);
		         }

				bld.crc = XYKGetCRC();

				// 根据要求拼接传入串
				line = syjh + syyh + type1 + jestr + olddate1 + oldsysref + authno + bld.crc;
				return line;
		 }
		 catch (Exception ex)
		 {
			 new MessageBox("写入金卡工程请求数据异常!\n\n" + ex.getMessage(), null, false);
			 ex.printStackTrace();
			 return null;
		 }
	}

	public boolean XYKReadResult1( int type)
	{
		BufferedReader br = null;
		try
        {
			if (!PathFile.fileExist(path  + "\\result.txt") || ((br = CommonMethod.readFileGBK(path  + "\\result.txt")) == null))
            {
            	XYKSetError("XX","读取金卡工程应答数据失败!");
                new MessageBox("读取金卡工程应答数据失败!", null, false);
                
                return false;
            }
			
			String newLine = null ;
			
			
			String [] result = br.readLine().split(",");
			if(result.length > 1)
			{
				newLine = result[1];
			}
			else
			{
				return false;
				//newLine = result[0];
			}
			
			int len = newLine.length();
//			 返回码
			bld.retcode = newLine.substring(0, 2);
			if (!bld.retcode.equals("00"))
			{
				//bld.retmsg = bld.retcode + " : " + newLine.substring(44,len - 64).trim();
				bld.retmsg = bld.retcode + " : " + getErrorMsg(path + "\\rsp.ini", bld.retcode);
				errmsg = bld.retmsg;
				return false;
			}
			else
			{
				bld.retmsg = "银联交易成功";
				errmsg = bld.retmsg;
			}
//			签到，结账，重打不需要获取详细信息
			if (type == PaymentBank.XYKQD || type == PaymentBank.XYKJZ || 
				type == PaymentBank.XYKCD ||type == PaymentBank.XYKYE ||
				type == PaymentBank.XKQT1)
			{
				return true;
			}
			// 卡名称
			//bld.bankinfo = newLine.substring(2,6);
			// 卡号
			bld.cardno = newLine.substring(6,26);
			// 流水号
			bld.trace = Long.parseLong(newLine.substring(26, 32));
			// 金额
			String je = newLine.substring(32, 44);
			double j = Double.parseDouble(je);
			j = ManipulatePrecision.mul(j, 0.01);
			bld.je = j;
			// 系统参考号
			bld.authno = newLine.substring(len - 25, len - 13);
			// LRC
			String LRC = newLine.substring(len - 3, len);
			if (!LRC.equals(bld.crc) && type != PaymentBank.XYKTH)
			{
				errmsg = "返回效验码" + LRC + "同原始效验码" + bld.crc + "不一致";
				XYKSetError("XX", errmsg);
				new MessageBox(errmsg);
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
	
	public String getErrorMsg(String path, String code)
	{
		BufferedReader br = null;
		try
        {
			if (errVector == null)
			{
				if (PathFile.fileExist(path))
				{
					errVector = CommonMethod.readFileByVector(path, "GBK");
				}
			}
			for (int i = 0; errVector != null && i < errVector.size(); i++)
			{
				String[] s = (String[]) errVector.elementAt(i);
				if (code.equalsIgnoreCase(s[0])) { return s[1]; }
			}
				
        }
		catch (Exception ex)
		{
			XYKSetError("XX","读取错误信息文件异常XX:"+ex.getMessage());
            new MessageBox("读取错误信息文件数据异常!" + ex.getMessage(), null, false);
            ex.printStackTrace();
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
		return "";
	}
	
	public boolean XYKNeedPrintDoc()
    {
        if (!checkBankSucceed())
        {
            return false;
        }

        int type = Integer.parseInt(bld.type.trim());

        // 消费，消费撤销，隔日退货，重打签购单
        if ((type == PaymentBank.XYKXF) || (type == PaymentBank.XYKCX) ||
                (type == PaymentBank.XYKTH) || (type == PaymentBank.XYKCD) ||
                (type == PaymentBank.XYKJZ || (type == PaymentBank.XKQT1)))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
	
    public void XYKPrintDoc()
    {
        ProgressBox pb = null;

        String printName = path  + "\\receipt.TXT";
        try
        {
            if (!PathFile.fileExist(printName))
            {
                new MessageBox("找不到签购单打印文件!");

                return;
            }
            
            pb = new ProgressBox();
            pb.setText("正在打印银联签购单,请等待...");

            for (int i = 0; i < GlobalInfo.sysPara.bankprint; i++)
            {
                BufferedReader br = null;

                XYKPrintDoc_Start();

                try
                {
                    //
                    br = CommonMethod.readFileGBK(printName);

                    if (br == null)
                    {
                        new MessageBox("打开签购单打印文件失败!");

                        return;
                    }

                    //
                    String line = null;

                    while ((line = br.readLine()) != null)
                    {
                        if (line.length() <= 0)
                        {
                            continue;
                        }
                        else if (line.indexOf("CUTPAPER") >= 0)
						{
							XYKPrintDoc_End();							
							continue;
						}

                        XYKPrintDoc_Print(line);
                    }
                }
                catch (Exception e)
                {
                    new MessageBox(e.getMessage());
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
            
			if (PathFile.fileExist(printName))
            {
                PathFile.deletePath(printName);
            }
        }
    }
    
    public void XYKPrintDoc_Start()
	{
    	if (onceprint)
		{
			int pagesize = ConfigClass.BankPageSize;

				Printer.getDefault().startPrint_Normal();
				if (pagesize > 0)
					Printer.getDefault().setPagePrint_Normal(true, pagesize);
		}
		else
		{
			// 此地改为增加模式，防止在多个金卡工程同时存在时，可能序号相同
			printdoc = CommonMethod.writeFileAppend("bankdoc_" + String.valueOf(bld.trace) + ".txt");
		}
	}
    
	public void XYKPrintDoc_Print(String printStr)
	{
		if (onceprint)
        {
            Printer.getDefault().printLine_Normal(printStr);
        }
        else
        {
            printdoc.println(printStr);
        }
	}
	
	public void XYKPrintDoc_End()
	{
		 if (onceprint)
        {
            Printer.getDefault().cutPaper_Normal();
        }
        else
        {
            printdoc.flush();
            printdoc.close();
            printdoc = null;
        }
    }
}
