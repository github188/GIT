package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

/**
 * 福瑞特超市银行卡
 * @author Administrator
 *
 */
public class Frt_PaymentBankFunc extends PaymentBankFunc
{	
	public String[] getFuncItem()
	{
		String[] func = new String[10];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[3] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
		func[4] = "[" + PaymentBank.XYKJZ + "]" + "交易结账";
		func[5] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
		func[6] = "[" + PaymentBank.XYKCD + "]" + "重打签购单";
		func[7] = "[" + PaymentBank.XKQT1 + "]" + "查询交易明细";
//		func[8] = "[" + PaymentBank.XKQT2 + "]" + "电子现金支付";
//		func[9] = "[" + PaymentBank.XKQT3 + "]" + "电子现金查余";
		func[8] = "[" + PaymentBank.XKQT4 + "]" + "重打结算单";
		func[9] = "[" + PaymentBank.XKQT5 + "]" + "提取交易信息";
		
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
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKTH://隔日退货   
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
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
			case PaymentBank.XKQT1://查询交易明细
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "查询交易明细";
				break;
//			case PaymentBank.XKQT2://电子现金支付
//				grpLabelStr[0] = null;
//				grpLabelStr[1] = null;
//				grpLabelStr[2] = null;
//				grpLabelStr[3] = null;
//				grpLabelStr[4] = "电子现金支付";
//				break;
//			case PaymentBank.XKQT3://电子现金查余
//				grpLabelStr[0] = null;
//				grpLabelStr[1] = null;
//				grpLabelStr[2] = null;
//				grpLabelStr[3] = null;
//				grpLabelStr[4] = "电子现金查余";
//				break;
			case PaymentBank.XKQT4://重打结算单
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打结算单";
				break;
			case PaymentBank.XKQT5://提取交易信息
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "提取交易信息";
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
				
			case PaymentBank.XKQT1://查询交易明细
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始查询交易明细";
				break;
//			case PaymentBank.XKQT2://电子现金支付
//				grpTextStr[0] = null;
//				grpTextStr[1] = null;
//				grpTextStr[2] = null;
//				grpTextStr[3] = null;
//				grpTextStr[4] = null;
//				break;
//			case PaymentBank.XKQT3://电子现金查余
//				grpTextStr[0] = null;
//				grpTextStr[1] = null;
//				grpTextStr[2] = null;
//				grpTextStr[3] = null;
//				grpTextStr[4] = "按回车键开始电子现金查余";
//				break;
			case PaymentBank.XKQT4://重打结算单
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始重打结算单";
				break;
			case PaymentBank.XKQT5://提取交易信息
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始提取交易信息";
				break;
		}
		
		return true;
	}
		
	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{
		// PaymentBank.XYKXF: 	// 消费
		// PaymentBank.XYKCX: 	// 消费撤销
		// PaymentBank.XYKTH:		// 隔日退货
		// PaymentBank.XYKYE: 	// 余额查询
		// PaymentBank.XYKCD: 	// 重打签购单
		// PaymentBank.XYKQD:     // 交易签到
		// PaymentBank.XYKJZ:     // 交易结帐
	
		try
		{
			
			if ((type != PaymentBank.XYKXF) && 
				(type != PaymentBank.XYKCX) && 
				(type != PaymentBank.XYKYE) && 
				(type != PaymentBank.XYKCD) && 
				(type != PaymentBank.XYKQD) &&
				(type != PaymentBank.XYKJZ) &&
				(type != PaymentBank.XYKTH) &&
				(type != PaymentBank.XKQT1) &&
				(type != PaymentBank.XKQT4) &&
				(type != PaymentBank.XKQT5)
				)
	            {
	                errmsg = "银联接口不支持该交易";
	                new MessageBox(errmsg);

	                return false;
	            }
			
			 // 先删除上次交易数据文件
            if (PathFile.fileExist("C:\\ALLINPAY\\ALLINPAY\\request.txt"))
            {
                PathFile.deletePath("C:\\ALLINPAY\\ALLINPAY\\request.txt");
                
                if (PathFile.fileExist("C:\\ALLINPAY\\ALLINPAY\\request.txt"))
                {
            		errmsg = "交易请求文件request.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            if (PathFile.fileExist("C:\\ALLINPAY\\ALLINPAY\\response.txt"))
            {
                PathFile.deletePath("C:\\ALLINPAY\\ALLINPAY\\response.txt");
                
                if (PathFile.fileExist("C:\\ALLINPAY\\ALLINPAY\\response.txt"))
                {
            		errmsg = "交易请求文件response.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
//          写入请求数据
			String line = XYKgetRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo);

			PrintWriter pw = CommonMethod.writeFile("C:\\ALLINPAY\\ALLINPAY\\request.txt");

			if (pw != null)
			{
				pw.println(line);
				pw.flush();
				pw.close();
			}
            
			// 调用接口模块
			if (PathFile.fileExist("C:\\ALLINPAY\\ALLINPAY\\javaposbank.exe"))
			{
				CommonMethod.waitForExec("C:\\ALLINPAY\\ALLINPAY\\javaposbank.exe misposNew");
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
		 String mtype1="" ;
		 try
		 {
			 switch (type)
			 {
			 	case PaymentBank.XYKXF: 	// 消费
			 		String[] title = { "消费类型", "描述" };
					String[] xf = {"2","普通消费"};
					String[] xfcx = {"26","电子现金消费"};
					
					Vector v = new Vector();
					v.add(xf);
					v.add(xfcx);
					int[] width = { 200, 400 };
					int choice = new MutiSelectForm().open("请选择消费种类("+GlobalInfo.ModuleType+")", title, width, v,false,660,319,false);
					if (choice < 0) 
						mtype1 = "2";
					else
					mtype1 = ((String[]) v.elementAt(choice))[0];
                break;
			 	case PaymentBank.XYKCX: 	// 消费撤销
			 		mtype1 = "3";
			 	break;	
                case PaymentBank.XYKTH:		// 隔日退货
                	mtype1 = "4";
                break;
                case  PaymentBank.XYKYE: 	// 余额查询
                	String[] title1 = { "查余类型", "描述" };
					String[] cx = {"18","普通余额查询"};
					String[] dzcs = {"27","电子现金查余"};
					
					Vector v1 = new Vector();
					v1.add(cx);
					v1.add(dzcs);
					int[] width1 = { 200, 400 };
					int choice1 = new MutiSelectForm().open("请选择查余种类("+GlobalInfo.ModuleType+")", title1, width1, v1,false,660,319,false);
					if (choice1 < 0) 
						mtype1 = "18";
					else
					mtype1 = ((String[]) v1.elementAt(choice1))[0];
                    break;
                case PaymentBank.XYKCD: 	// 重打上笔签购单
                	mtype1 = "16";
                    break;
                case PaymentBank.XYKQD:     // 交易签到
                	mtype1 = "1";
                    break;
                case PaymentBank.XYKJZ:     // 交易结帐
                	mtype1 = "14";
                break;
                case PaymentBank.XKQT1:     // 查询交易明细
                	mtype1 = "20";
                break;
                case PaymentBank.XKQT4:     // 重打结算单
                	mtype1 = "19";
                break;
                case PaymentBank.XKQT5:     // 提取交易信息
                	mtype1 = "15";
                break;
			 }
			 
			
			 
				// 收银机号
				String syjh = Convert.increaseChar(ConfigClass.CashRegisterCode, ' ', 4);
				//门店号
				String mkt = Convert.increaseChar(ConfigClass.Market, ' ', 4);
				// 收银员号
				String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 4);
	    
				// 根据要求拼接传入串
				String flag = "^";
				line ="01"+flag+syjh+flag+mkt + flag + syyh+flag +mtype1+flag+ money + flag + oldseqno + "^^^"+oldauthno+flag+olddate+flag+track2+flag+flag;
				return line;
		 }
		 catch (Exception ex)
		 {
			 new MessageBox("写入金卡工程请求数据异常!\n\n" + ex.getMessage(), null, false);
			 ex.printStackTrace();
			 return null;
		 }
	}
	
	public String getErrorInfo(String retcode)
    {
        String line = "";

        try
        {
        	
            if (!PathFile.fileExist("C:\\ALLINPAY\\ALLINPAY\\bankcode.txt") ||
                    !rtf.loadFile("C:\\ALLINPAY\\ALLINPAY\\bankcode.txt"))
            {
                new MessageBox("找不到C:\\ALLINPAY\\ALLINPAY\\bankcode.txt", null, false);

                return retcode;
            }

            //
            while ((line = rtf.nextRecord()) != null)
            {
                if (line.length() <= 0)
                {
                    continue;
                }

                String[] a = line.split("=");

                if (a.length < 2)
                {
                    continue;
                }

                if (a[0].trim().equals(retcode.trim()))
                {
                    return a[1].trim();
                }
            }

            rtf.close();
            
            return retcode;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return retcode;
        }
    }

	public boolean XYKReadResult1( int type)
	{
		BufferedReader br = null;
		
		try
        {
			if (!PathFile.fileExist("C:\\ALLINPAY\\ALLINPAY\\Response.txt") || ((br = CommonMethod.readFileGBK("C:\\ALLINPAY\\ALLINPAY\\Response.txt")) == null))
            {
            	XYKSetError("XX","读取金卡工程应答数据失败!");
                new MessageBox("读取金卡工程应答数据失败!", null, false);
                
                return false;
            }
			
			String newLine = null;
			String newline1 = "";
			while((newLine = br.readLine()) != null){
					newline1 = newline1 + newLine;
			}
			newLine = newline1.trim();
			
			String result[] = newLine.split("\\^");
			System.err.println(newLine+"\n"+bld.retcode);
			if (result.length !=31)
			{
				XYKSetError("XX", "金卡工程应答数据长度有误!");
				new MessageBox("金卡工程应答数据长度有误!", null, false);
				return false;
			}
			
//			 返回码
			bld.retcode = result[20];
			System.err.println(newLine+"\n"+bld.retcode);
			if (bld.retcode.equals("00"))
			{
				bld.retmsg = "银联交易成功";
				errmsg = bld.retmsg;
				
			}
			else
			{
				bld.retmsg = getErrorInfo(bld.retcode);
				errmsg = bld.retmsg;
				new MessageBox(errmsg);
				return false;
			}
			//卡余额
			bld.kye =Convert.toDouble(result[1]);
			// 卡号
			bld.cardno = result[5];
////			// 流水号
			bld.trace =Convert.toLong(result[17]);
////			 原流水号
			bld.oldtrace = Convert.toLong(result[14]);

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
                (type == PaymentBank.XYKJZ))
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

        String printName = "C:\\ALLINPAY\\ALLINPAY\\Print.txt";
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
    
}
