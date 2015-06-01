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

public class YlswTCRC_PaymentBankFunc extends PaymentBankFunc
{
	String bankpath = ConfigClass.BankPath;
    public String[] getFuncItem()
    {
    	String[] func = new String[7];
    	
    	func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
    	func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
    	func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
    	func[3] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
    	func[4] = "[" + PaymentBank.XYKJZ + "]" + "交易结账";
    	func[5] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
    	func[6] = "[" + PaymentBank.XYKCD + "]" + "重打印";
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
				grpLabelStr[0] = "原流水号";
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

			case PaymentBank.XYKTH://隔日退货   
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易金额";
				break;

            case PaymentBank.XYKCD: //签购单重打
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "重打签购单";
                break;
			case PaymentBank.XKQT1://交易签到
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易签到";
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
                grpTextStr[4] = "签购单重打";

                break;
            case PaymentBank.XKQT1: //交易签到
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始明细";

                break;
        }

        return true;
    }
    
    public void searchBankPath(String path)
    {	
		String file = path + ".ini";
		if (!PathFile.fileExist(file)) return;

		BufferedReader br  = CommonMethod.readFile(file);
		try
		{
			String line = br.readLine();
			bankpath = line;
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }

    public boolean XYKExecute(int type, double money, String track1,
                              String track2, String track3, String oldseqno,
                              String oldauthno, String olddate, Vector memo)
    {
        try
        {
            if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && (type != PaymentBank.XYKTH) &&
                    (type != PaymentBank.XYKQD) && (type != PaymentBank.XYKJZ) &&
                    (type != PaymentBank.XYKYE) && (type != PaymentBank.XYKCD) &&
                    (type != PaymentBank.XKQT1) && (type != PaymentBank.XKQT2) &&
                    (type != PaymentBank.XKQT3))
            {
                errmsg = "银联接口不支持该交易";
                new MessageBox(errmsg);

                return false;
            }
            
            //设定路径
            searchBankPath("YlswTCRC");
            System.err.println("YlswTCRC "+bankpath);
			if (PathFile.fileExist(bankpath + "\\result.txt"))
			{
				PathFile.deletePath(bankpath + "\\result.txt");
			}
            
            // 先删除上次交易数据文件
			/**
            if (PathFile.fileExist("print.txt"))
            {
            	//XYKPrintDoc();
                
                if (PathFile.fileExist("print.txt"))
                {
            		errmsg = "交易print.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            */
            
            //先删除上次交易数据文件
            if (PathFile.fileExist(bankpath + "\\request.txt"))
			{
				PathFile.deletePath(bankpath + "\\request.txt");
			}

            // 写入请求数据
            XYKgetRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo);
			
			// 调用接口模块
			if (PathFile.fileExist(bankpath + "\\javaposbank.exe"))
			{
				CommonMethod.waitForExec(bankpath + "\\javaposbank.exe YLSWSTBH","javaposbank.exe");
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
           // XYKPrintDoc();
            

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
                (type == PaymentBank.XYKJZ) || (type == PaymentBank.XKQT1))
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
        if (bld.retcode.trim().equals("0"))
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

            String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh,' ', 8);
            String type1 = "";
            
            String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
            jestr = Convert.increaseCharForward(jestr,'0',12);
            
            String flg = "";
            if (paycode != null) flg = paycode;
            	
            if (memo != null && memo.size() > 0) flg = (String) memo.elementAt(0);
         
            System.err.println("paycode:   "+flg);
            if (flg.length() <=0)
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
                    	flg = ((String[])v.elementAt(0))[0];
                    }
                    else
                    {
                    	flg = ((String[])v.elementAt(choice))[0];
                    }
            	}
            	else
            	{
            		flg = ((String[])v.elementAt(0))[0];
            	}
            }
            
            String flg1 = "1";
            if (ConfigClass.BankConfig.length() > 0)
            {
            	String line1[] =  ConfigClass.BankConfig.split(",");
            	System.err.println(ConfigClass.BankConfig);
            	for (int k = 0 ; k < line1.length; k++)
            	{
            		String line2 = line1[k];
            		if (line2.split(":")[0].equals(flg))
            		{
            			flg1 = line2.split(":")[1];
            			break;
            		}
            	}
            }
           
            if (flg1.equals("SVC"))
            {
	            switch (type)
	            {
	                case PaymentBank.XYKXF:
	                    type1 = "601";
	                    break;
	                case PaymentBank.XYKTH:
	                    type1 = "602";
	                    break;
	                case PaymentBank.XYKCX:
	                    type1 = "602";
	                    break;
	                case PaymentBank.XYKYE:
	                	type1 = "605";
	                    break;
	                case PaymentBank.XYKQD:
	                	type1 = "606";
	                    break;
	                case PaymentBank.XYKCD:
	                	type1 = "604";
	                    break;
	                default:
	                    type1 = "603";
	                	break;
	            }
            }
            else
            {
	            switch (type)
	            {
	                case PaymentBank.XYKXF:
	                    type1 = "1";
	                    break;
	                case PaymentBank.XYKTH:
	                    type1 = "2";
	                    break;
	                case PaymentBank.XYKCX:
	                    type1 = "2";
	                    break;
	                case PaymentBank.XYKYE:
	                	type1 = "5";
	                    break;
	                case PaymentBank.XYKQD:
	                	type1 = "6";
	                    break;
	                case PaymentBank.XYKCD:
	                	type1 = "4";
	                    break;
	                default:
	                    type1 = "3";
	                	break;
	            }
            }
            
            line = type1+",,"+syyh+","+money+","+oldauthno+","+oldseqno+","+olddate ;
            
			PrintWriter pw = CommonMethod.writeFile(bankpath + "\\request.txt");

			if (pw != null)
			{
				pw.println(line);
				pw.flush();
				pw.close();
			}
			
            return line;
        }
        catch (Exception ex)
        {
            new MessageBox("写入金卡工程请求数据异常!\n\n" + ex.getMessage(), null, false);
            ex.printStackTrace();

            return null;
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
			String line = br.readLine();
			
			String lines = line.substring(line.indexOf(",")+1);
			
			lines = lines.replace((char)0x1c, ',');
			String[] lines1 = lines.split(String.valueOf(','));
			
            bld.retcode = line.substring(0,line.indexOf(","));
            
            if (!bld.retcode.equals("0"))
            {
            	bld.retmsg  = lines;
            	errmsg = lines;
            	return false;
            }
            else
            {
            	bld.retmsg = "交易成功";

            }
            
            if (type == PaymentBank.XYKJZ || type == PaymentBank.XYKCD || type == PaymentBank.XYKQD)
            {
            	
            }
            else
            {
            	//String shdm = Convert.newSubString(line, 6, 21);
            	//String temno = Convert.newSubString(line, 21, 29);
            	if (type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XYKTH)
            	{
	            	bld.trace=  Long.parseLong(lines1[2]);
		            bld.cardno  = lines1[7];
		            bld.bankinfo = lines1[6];
		            String bankname = XYKReadBankName(bld.bankinfo);
	
		            bld.bankinfo = bld.bankinfo+bankname;
            	}
            	
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
/**
					if (PathFile.fileExist(bankpath + "\\request.txt"))
					{
						PathFile.deletePath(bankpath + "\\request.txt");
					}

					if (PathFile.fileExist(bankpath + "\\result.txt"))
					{
						PathFile.deletePath(bankpath + "\\result.txt");
					}*/
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
            if (!PathFile.fileExist(GlobalVar.ConfigPath+"\\bankError.txt") ||
                    !rtf.loadFile(GlobalVar.ConfigPath+"\\bankError.txt"))
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
        	String printName = "print.txt";
        	if (!PathFile.fileExist(printName))
        	{
        		return ;
        	}
        	
            pb = new ProgressBox();
            pb.setText("正在打印银联签购单,请等待...");
            
            for (int i = 0; i < 1; i++)
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

                    //
                    String line = null;
                    System.err.println(line);
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

                //XYKPrintDoc_End();
                
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
            Printer.getDefault().printLine_Journal(printStr);
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
    
    public String XYKReadBankName(String bankid)
    {
        String line = "";

        try
        {        	
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
            
            return "储值卡";
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return bankid;
        }
    }
}
