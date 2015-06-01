package bankpay.Bank;


import java.io.BufferedReader;
import java.io.File;
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
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class NjysST_PaymentBankFunc extends PaymentBankFunc
{
		protected String bankpath = ConfigClass.BankPath;
		private char iscardtype = 'N';
		private String paycode = "";
		
	    public String[] getFuncItem()
	    {
	    	String[] func = new String[6];
	    	
	    	func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
	    	func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
	    	func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
	    	func[3] = "[" + PaymentBank.XYKJZ + "]" + "交易结账";
	    	func[4] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
	    	func[5] = "[" + PaymentBank.XYKCD + "]" + "其他交易";

	        return func;
	    }
	    
	    public boolean getFuncLabel(int type, String[] grpLabelStr)
	    {
	        //0-4对应FORM中的5个输入框
	        //null表示该不用输入
	        switch (type)
	        {
	            case PaymentBank.XYKXF: //消费
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "交易金额";

	                break;

	            case PaymentBank.XYKCX: //消费撤销
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
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

	            case PaymentBank.XYKJZ: //结账
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "结账";

	                break;

	            case PaymentBank.XYKTH: //隔日退货   
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "金额";

	                break;

	            case PaymentBank.XYKCD: //签购单重打
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "重打上笔签购单";
	                break;
	        }

	        return true;
	    }

	    public boolean getFuncText(int type, String[] grpTextStr)
	    {
	        //0-4对应FORM中的5个输入框
	        //null表示该需要用户输入,不为null用户不输入
	        switch (type)
	        {
	            case PaymentBank.XYKXF: //消费
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = null;

	                break;

	            case PaymentBank.XYKCX: //消费撤销
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = null;

	                break;

	            case PaymentBank.XYKTH: //退货
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = null;

	                break;

	            case PaymentBank.XYKYE: //余额查询    
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = "按回车键开始余额查询";

	                break;

	            case PaymentBank.XYKQD: //交易签到
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = "按回车键开始交易签到";

	                break;

	            case PaymentBank.XYKJZ: //内卡结账
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = "按回车键开始结账";

	                break;

	            case PaymentBank.XYKCD: //签购单重打
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = "开始签购单重打";

	                break;
	        }

	        return true;
	    }

	    public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	    {
	        try
	        {
	            if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && (type != PaymentBank.XYKTH) &&
	                 (type != PaymentBank.XYKJZ) && (type != PaymentBank.XYKYE) && (type != PaymentBank.XYKCD)) 
	            {
	                errmsg = "银联接口不支持该交易";
	                new MessageBox(errmsg);

	                return false;
	            }
	            
	            if (memo != null && memo.size() > 0)
	            {
	            	paycode = String.valueOf(memo.elementAt(0));
	            	
	            	searchBankPath(paycode);
	            }
	            else
	            {
	            	Vector v = new Vector();
	            	
	            	// 查询是否定义了银联付款方式
	            	for (int k =0 ; k < GlobalInfo.payMode.size(); k++)
	            	{
	            		PayModeDef mdf = (PayModeDef) GlobalInfo.payMode.elementAt(k);
	            		
	            		if (mdf.isbank == 'Y')
	            		{
	            			v.add(new String[]{mdf.code,mdf.name});
	            		}
	            	}
	            	
	            	if (v.size() > 1)
	            	{
	                    String[] title = { "付款代码", "付款名称" };
	                    int[] width = { 100, 400 };
	                    int choice = new MutiSelectForm().open("请选择交易方式", title, width, v);
	                    
	                    if (choice == -1 )
	                    {
	                    	paycode = ((String[])v.elementAt(0))[0];
	                    }
	                    else
	                    {
	                    	paycode = ((String[])v.elementAt(choice))[0];
	                    }
	            	}
	            	else
	            	{
	            		paycode = ((String[])v.elementAt(0))[0];
	            	}
	            	
	            	searchBankPath(paycode);
	            }
	            
	            if (iscardtype == 'Y' && (type == PaymentBank.XYKYE || type == PaymentBank.XYKTH))
	            {
	            	errmsg = "外卡模式时不支持当前交易!";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		
	            	return false;
	            }
	            
	            // 先删除上次交易数据文件
	            if (PathFile.fileExist(bankpath + "\\toprint.txt"))
	            {
	                PathFile.deletePath(bankpath + "\\toprint.txt");		
	            }
	            
				if (PathFile.fileExist(bankpath + "\\request.txt"))
				{
					PathFile.deletePath(bankpath + "\\request.txt");
				}

				if (PathFile.fileExist(bankpath + "\\result.txt"))
				{
					PathFile.deletePath(bankpath + "\\result.txt");
				}
				
				if (PathFile.fileExist(bankpath + "\\answer.txt"))
				{
					PathFile.deletePath(bankpath + "\\answer.txt");
				}

	            // 写入请求数据
	            String line =XYKgetRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo);
	            
				PrintWriter pw = CommonMethod.writeFile(bankpath + "\\request.txt");

				if (pw != null)
				{
					pw.println(line);
					pw.flush();
					pw.close();
				}
				
				// 调用接口模块
				if (PathFile.fileExist(bankpath + "\\javaposbank.exe"))
				{
					CommonMethod.waitForExec(bankpath + "\\javaposbank.exe NJYS","javaposbank.exe");
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

                //无论是否成功，都检查打印
                
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

	        // 消费，消费撤销，重打签购单
	        if ((type == PaymentBank.XYKXF) || (type == PaymentBank.XYKCX) ||
	                (type == PaymentBank.XYKTH) || (type == PaymentBank.XYKCD) ||
	                (type == PaymentBank.XYKJZ) )
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
	            bld.retbz  = 'Y';
	            bld.retmsg = "金卡工程调用成功";

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
	            //errmsg = bld.retmsg;

	            return false;
	        }
	        else
	        {
	            errmsg = "交易成功";
	        	
	    		//加入解百特殊金卡工程断电保护
	        	String path = bankpath + "\\answer.txt";
	    		if (PathFile.fileExist(path))
	    		{
	    			PathFile.deletePath(path);
	    			if (PathFile.fileExist(path))
	    			{
	    				new MessageBox(path+"已经被其他程序锁住，请联系电脑部解决");
	    			}
	    		}
	    		
	            return true;
	        }
	    }

	    public String XYKgetRequest(int type, double money, String track1,
	                                      String track2, String track3,
	                                      String oldseqno, String oldauthno,
	                                      String olddate, Vector memo)
	    {
	        try
	        {
	            String line = "";

	            String syjh = Convert.increaseChar(ConfigClass.CashRegisterCode,' ', 10);
	            String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh,' ', 10);
	            char type1 = ' ';
	            
	            String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
	            jestr = Convert.increaseCharForward(jestr,'0',12);
	            String memo1= "";
	            
	            /**
	            if (oldseqno == null || oldseqno.length() <= 0)
	            {
	            	ysyjh = "00000000";
	            }
	            else
	            {
	            	ysyjh = Convert.increaseChar(oldseqno, 8);
	            }*/
	            
  
	            memo1 = Convert.increaseChar(paycode, 6);
	            
	            //根据不同的类型生成文本结构
	            switch (type)
	            {
	                case PaymentBank.XYKXF:
	                    type1 = 'C';
	                    
	                    if (iscardtype == 'Y') type1 = 'E';
	                break;
	                case PaymentBank.XYKCX:
	                    type1 = 'D';
	                    
	                    if (iscardtype == 'Y') type1 = 'F';
	                break;
	                case PaymentBank.XYKTH:
	                	type1 = 'R';
	                break;
	                case PaymentBank.XYKYE:
	                	type1 = 'I';
	                break;
	                default:
	                    type1 = 'O';
	                break;
	            }
	            
	            line = syjh + syyh + type1 + jestr + memo1;
	            return line;
	        }
	        catch (Exception ex)
	        {
	            new MessageBox("写入金卡工程请求数据异常!\n\n" + ex.getMessage(), null, false);
	            ex.printStackTrace();

	            return null;
	        }
	    }
	    
	    public String XYKReadBankName(String bankid)
	    {
	        String line = "";

	        try
	        {
	        	if (bankid.charAt(0) == '4')
	        	{
	        		return "商行";
	        	}
	        	
	            if (!PathFile.fileExist(GlobalVar.ConfigPath + File.separator +
	                                        "BankInfo.ini") ||
	                    !rtf.loadFile(GlobalVar.ConfigPath + File.separator +
	                                      "BankInfo.ini"))
	            {
	                new MessageBox("找不到BankInfo.ini", null, false);

	                return bankid;
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

	                if (Convert.toInt(a[0]) == Convert.toInt(bankid.trim()))
	                {
	                    return a[1].trim();
	                }
	            }

	            rtf.close();
	            
	            return "未知银行";
	        }
	        catch (Exception ex)
	        {
	            ex.printStackTrace();

	            return bankid;
	        }
	    }

	    public boolean XYKReadResult1(int type)
	    {
	    	BufferedReader br = null;
	    	
	        try
	        {
				if (!PathFile.fileExist(bankpath + "\\result.txt") || ((br = CommonMethod.readFileGBK(bankpath + "\\result.txt")) == null))
				{
					XYKSetError("XX", "读取金卡工程应答数据失败!");
					new MessageBox("读取金卡工程应答数据失败!", null, false);

					return false;
				}
				
				String newLine = br.readLine();
				
				String[] lines = newLine.split(",");
				
				if (lines == null || lines.length != 2)
				{
					return true;
				}
				
				String line = lines[1];
	            bld.retcode = line.substring(0,2);
	            
	            if ( type != PaymentBank.XYKXF && type != PaymentBank.XYKCX && type != PaymentBank.XYKTH) 
	            {
	            	bld.retcode = "00";
	            	bld.retmsg  = "银联交易成功";
	            	return true;
	            }
	            
	            if (!bld.retcode.equals("00"))
	            {
	            	bld.retmsg  = getErrorInfo(bld.retcode);
	            	return false;
	            }
	            else
	            {
	            	bld.retmsg = "交易成功";

	            }
	            
	            bld.cardno  = Convert.newSubString(line, 2, 21);
	            //String type1     = Convert.newSubString(line, 21, 22);
	            String je   = Convert.newSubString(line, 22, 34);
	            double j = Double.parseDouble(je);
	            j = ManipulatePrecision.mul(j, 0.01);
	            bld.je = j;
	            
	            bld.oldtrace = Convert.toLong(Convert.newSubString(line, 34, 40));
	            bld.trace   = Convert.toLong(Convert.newSubString(line, 40, 46));
	           // bld.bankinfo= Convert.newSubString(line, 46, 49);
	            //String bankname = line.substring(55, line.length()).trim();
	            
	           // bld.bankinfo = bld.bankinfo +","+ XYKReadBankName(bld.bankinfo);
	            
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
	    
	    public String getErrorInfo(String retcode)
	    {
	        String line = "";

	        try
	        {
	        	
	            if (!PathFile.fileExist(GlobalVar.ConfigPath + "\\bankError.txt") ||
	                    !rtf.loadFile(GlobalVar.ConfigPath + "\\bankError.txt"))
	            {
	                new MessageBox("找不到bankError.txt", null, false);

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

	    public void XYKPrintDoc()
	    {
	        ProgressBox pb = null;

	        try
	        {
	        	String printName = bankpath + "\\toprint.txt";
	        	if (!PathFile.fileExist(printName))
	        	{
	        		return ;
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
	                    	
	                        if (line.trim().equals("CUTPAPER"))
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
	            
	            PathFile.deletePath(printName);
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
	    
	    public void searchBankPath(String paycode)
		{
			String line = null; 
			
			if (!PathFile.fileExist(GlobalVar.ConfigPath + "\\BankPayCode.ini") || !rtf.loadFileByGBK(GlobalVar.ConfigPath + "\\BankPayCode.ini"))
			{
				return ;
			}
			
			while ((line = rtf.nextRecord()) != null)
			{
				if (line.length() <= 0)
				{
					continue;
				}
				
				String paycodes[] = line.split("=");
				
				if (paycode.length() >= 2 && paycodes[0].equals(paycode)) 
				{
					// 接口路径
					if (paycodes[1].split(",").length > 0) bankpath = paycodes[1].split(",")[0];
					
					// 卡类别,内卡或外卡
					if (paycodes[1].split(",").length > 1) iscardtype = paycodes[1].split(",")[1].trim().charAt(0);
					
					break;
				}
			}

			rtf.close();
			
		}
}
