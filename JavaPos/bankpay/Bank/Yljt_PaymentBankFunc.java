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

public class Yljt_PaymentBankFunc extends PaymentBankFunc
{	 
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
					grpLabelStr[0] = "原参考号";
					grpLabelStr[1] = null;
					grpLabelStr[2] = "原交易日";
					grpLabelStr[3] = "请 刷 卡";
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
	            
				if (PathFile.fileExist(ConfigClass.BankPath + "\\result.txt"))
				{
					PathFile.deletePath(ConfigClass.BankPath + "\\result.txt");
				}
	            
	            // 先删除上次交易数据文件
	            if (PathFile.fileExist("print.txt"))
	            {
	            	XYKPrintDoc();
	                
	                if (PathFile.fileExist("print.txt"))
	                {
	            		errmsg = "交易print.txt无法删除,请重试";
	            		XYKSetError("XX",errmsg);
	            		new MessageBox(errmsg);
	            		return false;   	
	                }
	            }
	            
	            //先删除上次交易数据文件
	            if (PathFile.fileExist(ConfigClass.BankPath + "\\request.txt"))
				{
					PathFile.deletePath(ConfigClass.BankPath + "\\request.txt");
				}

	            // 写入请求数据
	            XYKgetRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo);
				
				// 调用接口模块
				if (PathFile.fileExist(ConfigClass.BankPath + "\\misposbank.exe"))
				{
					CommonMethod.waitForExec(ConfigClass.BankPath + "\\misposbank.exe");
				}
				else
				{
					new MessageBox("找不到金卡工程模块 misposbank.exe");
					XYKSetError("XX", "找不到金卡工程模块 misposbank.exe");
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
	            XYKPrintDoc();
	            

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
	        if (bld.retcode.trim().equals("000000"))
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

	            String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh,' ', 20);
	            String type1 = "";
	            
	            String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
	            jestr = Convert.increaseCharForward(jestr,'0',12);

	            //String ltrack2 = Convert.appendStringSize("", track2, 0, 37, 37);
	            //String ltrack3 = Convert.appendStringSize("", track3, 0, 104, 104);
	            //根据不同的类型生成文本结构
	            switch (type)
	            {
	                case PaymentBank.XYKXF:
	                    type1 = "PCA";
	                    break;
	                case PaymentBank.XYKTH:
	                    type1 = "CAN";
	                    break;
	                case PaymentBank.XYKCX:
	                    type1 = "PRA";
	                    break;
	                case PaymentBank.XYKYE:
	                	type1 = "INQ";
	                    break;
	                case PaymentBank.XYKQD:
	                	type1 = "OPN";
	                    break;
	                case PaymentBank.XYKCD:
	                	type1 = "002";
	                    break;
	                default:
	                    type1 = "SET";
	                	break;
	            }
	            
	            String shdm = Convert.increaseLong(0, 15);
	            String yseqno = Convert.increaseCharForward(oldseqno,'0', 12);
	            //String yauth = Convert.increaseCharForward(oldauthno,'0', 6);
	            
	            String memo2 =  Convert.increaseChar(track2,20);
	            String time = olddate;
	            
	            line = type1+syyh+jestr+shdm+yseqno+memo2+time;
	            
	            String flg = "";
	            if (memo != null && memo.size() > 0) flg = (String) memo.elementAt(0);
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
	            System.out.println(ConfigClass.BankConfig+"----"+flg);
	            if (ConfigClass.BankConfig.length() > 0)
	            {
	            	String line1[] =  ConfigClass.BankConfig.split(",");
	            	System.out.println(ConfigClass.BankConfig);
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
	            
	            System.out.println(flg1);
				PrintWriter pw = CommonMethod.writeFile(ConfigClass.BankPath + "\\request.txt");

				if (pw != null)
				{
					pw.println(flg1+line);
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
				if (!PathFile.fileExist(ConfigClass.BankPath + "\\result.txt") || ((br = CommonMethod.readFileGBK(ConfigClass.BankPath + "\\result.txt")) == null))
				{
					new MessageBox("没有找到result.txt文件: "+ConfigClass.BankPath + "\\result.txt");
					XYKSetError("XX", "读取金卡工程应答数据失败!");
					//new MessageBox("读取金卡工程应答数据失败!", null, false);

					return false;
				}
				String line = br.readLine();

	            bld.retcode = line.substring(0,6);
	            
	            if (!bld.retcode.equals("000000"))
	            {
	            	bld.retmsg  = getErrorInfo(bld.retcode);
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
		            bld.bankinfo= Convert.newSubString(line, 29, 31);
		            
		            String bankname = XYKReadBankName(bld.bankinfo);
		            
		            bld.cardno  = Convert.newSubString(line, 31, 50);
		            bld.trace   = Long.parseLong(Convert.newSubString(line, 50, 62));
		            
		            //String je   = Convert.newSubString(line, 94, 106);
		            //double j = Double.parseDouble(je);
		            bld.bankinfo = bld.bankinfo+bankname;
		            //new MessageBox(line);
	            }
	            return true;
	        }
	        catch (Exception ex)
	        {
	        	new MessageBox("读取文件后，解析异常");
	        	XYKSetError("XX","读取应答XX:"+ex.getMessage());
	            //new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);
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
						if (PathFile.fileExist(ConfigClass.BankPath + "\\request.txt"))
						{
							PathFile.deletePath(ConfigClass.BankPath + "\\request.txt");
						}

						if (PathFile.fileExist(ConfigClass.BankPath + "\\result.txt"))
						{
							PathFile.deletePath(ConfigClass.BankPath + "\\result.txt");
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
}
