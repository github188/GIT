package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import org.eclipse.swt.widgets.Text;

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

//金客隆交通银行第三方支付银联接口
public class Pboc3_PaymentBankFunc extends PaymentBankFunc
{	
	String cardType = "";
	 
	public String[] getFuncItem()
	{
		String[] func = new String[9];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货"; 
		func[3] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
		func[4] = "[" + PaymentBank.XYKJZ + "]" + "交易结算";
		func[5] = "[" + PaymentBank.XKQT1 + "]" + "重打结算单";
		func[6] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
		func[7] = "[" + PaymentBank.XYKCD + "]" + "重打上笔签购单";
		func[8] = "[" + PaymentBank.XKQT2 + "]" + "重打任意签购单";
		
		
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
				grpLabelStr[0] = "查询号";
				grpLabelStr[1] = "交易参考号";
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKTH://退货
				grpLabelStr[0] = "查询号";
				grpLabelStr[1] = "交易参考号";
				grpLabelStr[2] = "交易日期";
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
			case PaymentBank.XKQT1://重打结算单
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打结算单";
				break;
			case PaymentBank.XYKJZ://交易结账
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易结算";
				break;
			case PaymentBank.XYKYE://余额查询    
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "余额查询";
				break;
			case PaymentBank.XYKCD://重打签购单
				grpLabelStr[0] = null;//"原票据号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打上笔签购单";
				break;
			case PaymentBank.XKQT2://重打任意签购单
				grpLabelStr[0] = "查询号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打任意签购单";
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
			case PaymentBank.XYKTH://退货
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XKQT1:// 重打结算单  
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始重打结算单";
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
				grpTextStr[4] = "按回车键开始交易结算";
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
			case PaymentBank.XKQT2:// 重打任意一笔 
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始重打签购单";
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
			if (!((type == PaymentBank.XYKXF) || 
				(type == PaymentBank.XYKCX) || 
				(type == PaymentBank.XYKYE) || 
				(type == PaymentBank.XYKCD) || 
				(type == PaymentBank.XYKQD) ||
				(type == PaymentBank.XYKJZ) ||
				(type == PaymentBank.XYKTH) ||
				(type == PaymentBank.XKQT1) || 
				(type == PaymentBank.XKQT2))  )
	            {
	                errmsg = "银联接口不支持该交易";
	                new MessageBox(errmsg);

	                return false;
	            }
			
			 // 先删除上次交易数据文件
            if (PathFile.fileExist(ConfigClass.BankPath  + "\\request.txt"))
            {
                PathFile.deletePath(ConfigClass.BankPath  + "\\request.txt");
                
                if (PathFile.fileExist(ConfigClass.BankPath  + "\\request.txt"))
                {
            		errmsg = "交易请求文件request.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            if (PathFile.fileExist(ConfigClass.BankPath  + "\\response.txt"))
            {
                PathFile.deletePath(ConfigClass.BankPath  + "\\response.txt");
                
                if (PathFile.fileExist(ConfigClass.BankPath  + "\\response.txt"))
                {
            		errmsg = "交易请求文件response.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            /*
			String[] title = { "代码", "卡类型" };
			int[] width = { 60, 440 };
			Vector contents = new Vector();
			contents.add(new String[] { "01", "银行卡" });
			contents.add(new String[] { "02", "储值卡" });
			
			int choice = new MutiSelectForm().open("请选择交易卡类型", title, width, contents, true);
			if (choice == -1)
			{
				errmsg = "没有选择交易卡类型";
				return false;
			}else {
				String[] row = (String[]) (contents.elementAt(choice));
				cardType = row[0];
			}
            */
            //          写入请求数据
			 if(!XYKwriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo)) return false;
            
			// 调用接口模块
			if (PathFile.fileExist(ConfigClass.BankPath + "\\bjbocom.exe"))//bjbocom.exe
			{
				CommonMethod.waitForExec(ConfigClass.BankPath + "\\bjbocom.exe");
				
			}
			else
			{
				new MessageBox("找不到金卡工程模块 bjbocom.exe");
				XYKSetError("XX", "找不到金卡工程模块 bjbocom.exe");
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

	protected boolean XYKNeedPrintDoc()
	{
        if (!checkBankSucceed())
        {
            return false;
        }

        int type = Integer.parseInt(bld.type.trim());

        // 消费，消费撤销，重打签购单
        if ((type == PaymentBank.XYKXF) || (type == PaymentBank.XYKCX) ||
                (type == PaymentBank.XYKTH) || (type == PaymentBank.XYKCD) ||
                (type == PaymentBank.XYKJZ) || (type == PaymentBank.XKQT1)||(type == PaymentBank.XKQT2))
        {
            return true;
        }
        else
        {
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

	public boolean checkDate(Text date)
	{
		String date1 = date.getText();
		if (date1.length() > 6)
		{
			new MessageBox("请输入日期\n日期格式《YYMMDD》");
			return false;
		}
		return true;
	}
	              
	public boolean XYKwriteRequest(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		 try
		 {
			 String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1));
			 jestr = Convert.increaseCharForward(jestr, '0', 12);
			 
			 String line = "";
			 oldseqno = Convert.increaseChar(oldseqno, '0',6);
			 oldauthno = Convert.increaseChar(oldauthno, '0',12);
			 olddate = Convert.increaseChar(olddate, '0',6);

			 switch (type)
	         {
	         	case PaymentBank.XYKXF:				// 消费
	         		line = "02"+"|" + "30|"  + jestr + "|000000|000000000000|000000|00000000000000000000|00000000000000000000|00000000000000000000";
	         		break;
	         	case PaymentBank.XYKCX:				// 撤销	

	         		line = "02"+"|" + "40|"  + jestr + "|" + oldseqno + "|" + oldauthno + "|000000|00000000000000000000|00000000000000000000|00000000000000000000";
	         		break;
	         	case PaymentBank.XYKTH:				// 退货    		
	         		line = "02"+"|" + "50|"  + jestr + "|" + oldseqno + "|" + oldauthno + "|" + olddate + "|00000000000000000000|00000000000000000000|00000000000000000000";
		         	break;
	         	case PaymentBank.XYKQD:				// 签到
	         		line = "02"+"|" + "91" + "|000000000000|000000|000000000000|000000|00000000000000000000|00000000000000000000|00000000000000000000";
	         		break;
	         	case PaymentBank.XYKJZ:				// 结算
	         		line = "02"+"|" + "95" + "|000000000000|000000|000000000000|000000|00000000000000000000|00000000000000000000|00000000000000000000";
	         		break;
	         	case PaymentBank.XKQT1:				// 重打结算单
	         		line = "02"+"|" + "62" + "|000000000000|000000|000000000000|000000|00000000000000000000|00000000000000000000|00000000000000000000";
	         		break;
	         	case PaymentBank.XYKYE:				// 查询

	         		line = "02"+"|" + "80" + "|000000000000|000000|000000000000|000000|00000000000000000000|00000000000000000000|00000000000000000000";
	         		break;
	         	case PaymentBank.XYKCD:				// 重打签购单

	         		line = "02"+"|" + "64|" + jestr + "|000000|000000000000|000000|00000000000000000000|00000000000000000000|00000000000000000000";
	         		break;
	         	case PaymentBank.XKQT2:				// 重打任意笔签购单

	         		line = "01"+"|" + "61|" + "000000000000|" +oldseqno+ "|000000000000|000000|00000000000000000000|00000000000000000000|00000000000000000000";
	         		break;
	         }
	         
	         PrintWriter pw = null;
	         try
	         {
	            pw = CommonMethod.writeFile(ConfigClass.BankPath + "\\request.txt");
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

	public boolean XYKReadResult( int type)
	{
		BufferedReader br = null;
		
		try
        {
			if (!PathFile.fileExist(ConfigClass.BankPath  + "\\response.txt") || ((br = CommonMethod.readFileGB2312(ConfigClass.BankPath  + "\\response.txt")) == null))
            {
            	XYKSetError("XX","读取金卡工程应答数据失败!");
                new MessageBox("读取金卡工程应答数据失败!", null, false);
                
                return false;
            }
			
			//String newLine = null ;				
			String [] result = br.readLine().split("\\|");
			if(result.length < 1)
				return false;
			
//			 返回码
			bld.retcode = result[2];
			if (!bld.retcode.equals("00"))
			{
				bld.retmsg = result[3].trim();
				errmsg = bld.retmsg;
				bld.retbz = 'N';
				return false;
			}
			else
			{
				bld.retmsg = "银联交易成功";
				errmsg = bld.retmsg;
				bld.retbz = 'Y';
			}

			//查询余额，重打印，签到，重打结算单不需要获取详细信息
			if ( 	(type == PaymentBank.XYKYE) || 
					(type == PaymentBank.XYKCD) || 
					(type == PaymentBank.XYKQD) ||
					(type == PaymentBank.XYKJZ) ||
					(type == PaymentBank.XKQT1) ||
					(type == PaymentBank.XKQT2)
					)
				return true;
				
			// 卡名称
			bld.bankinfo = result[8].trim();
			// 卡号
			bld.cardno = result[7].trim();
			// 流水号
			//注意，当获取的字符全为空是，将导致转换异常
			bld.trace = Convert.toLong(result[14].trim());	
			
			
			String je = result[9].trim();			
			double j = Convert.toDouble(je);
			j = ManipulatePrecision.mul(j, 0.01);
			bld.je = j;
			// 系统参考号
			bld.authno = result[12].trim();
		
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
	

	
	public boolean XYKNeedPrintDoc(int type)
    {
        if (!checkBankSucceed())
        {
            return false;
        }

        // 消费，消费撤销，隔日退货，重打签购单
        if ((type == PaymentBank.XYKXF) || (type == PaymentBank.XYKCX) ||
                (type == PaymentBank.XYKTH) || (type == PaymentBank.XYKCD) ||
                (type == PaymentBank.XYKJZ) ||  (type == PaymentBank.XKQT1) || (type == PaymentBank.XKQT2))
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

        String printName = ConfigClass.BankPath  + "\\receipt.txt";
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
                        else if (line.trim().equalsIgnoreCase("cutpaper"))
                        {
                        	XYKPrintDoc_Print("\n");
                        	XYKPrintDoc_Print("\n");
                        	XYKPrintDoc_Print("\n");
                        	XYKPrintDoc_End();
                        	
                        	new MessageBox("请撕下商户签购单");
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

            	XYKPrintDoc_Print("\n");
            	XYKPrintDoc_Print("\n");
            	XYKPrintDoc_Print("\n");
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

