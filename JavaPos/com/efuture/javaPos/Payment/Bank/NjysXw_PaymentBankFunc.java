package com.efuture.javaPos.Payment.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import org.eclipse.swt.widgets.Display;

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
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class NjysXw_PaymentBankFunc extends PaymentBankFunc {
	
	public String[] getFuncItem() {
        String[] func = new String[5];
        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
        func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
        func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
        func[3] = "[" + PaymentBank.XYKYE + "]" + "查询余额交易";
        func[4] = "[" + PaymentBank.XYKJZ + "]" + "其他";
        return func;
	}
	
	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo) {
		
		try {
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
		    
	        // 调用接口模块
	        if (PathFile.fileExist("c:\\gmc\\softpos.exe"))
	        {
	            if (PathFile.fileExist("c:\\gmc\\SOFTPOSREQ.TXT"))
	            {
	                PathFile.deletePath("c:\\gmc\\SOFTPOSREQ.TXT");
	                
	                if (PathFile.fileExist("c:\\gmc\\SOFTPOSREQ.TXT"))
	                {
	            		errmsg = "交易请求文件SOFTPOSREQ.TXT无法删除,请重试";
	            		XYKSetError("XX",errmsg);
	            		new MessageBox(errmsg);
	            		return false;   	
	                }
	            }

	            if (PathFile.fileExist("c:\\gmc\\SOFTPOSRESP.TXT"))
	            {
	            	PathFile.copyPath("c:\\gmc\\SOFTPOSRESP.TXT","c:\\gmc\\LastSOFTPOSRESP.TXT.txt");
	                PathFile.deletePath("c:\\gmc\\SOFTPOSRESP.TXT");
	                
	                if (PathFile.fileExist("c:\\gmc\\SOFTPOSRESP.TXT"))
	                {
	            		errmsg = "交易请求文件SOFTPOSRESP.TXT无法删除,请重试";
	            		XYKSetError("XX",errmsg);
	            		new MessageBox(errmsg);
	            		return false;   	
	                }
	            }
	            
			    // 先删除上次交易数据文件
			    if (PathFile.fileExist("c:\\gmc\\toprint.txt"))
			    {
			    	XYKPrintDoc();
			        
			        if (PathFile.fileExist("c:\\gmc\\toprint.txt"))
			        {
			    		errmsg = "交易请求文件toprint.txt无法删除,请重试";
			    		XYKSetError("XX",errmsg);
			    		new MessageBox(errmsg);
			    		return false;   	
			        }
			    }
	        }
	        else
	        {
	            new MessageBox("找不到金卡工程模块 GMC.exe");
	            XYKSetError("XX","找不到金卡工程模块 GMC.exe");
	        	return false;
	        }
		    		    
            // 写入请求数据
            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno,oldauthno,olddate,memo))
            {
                return false;
            }
            
            CommonMethod.waitForExec("c:\\gmc\\softpos.exe","softpos.exe");
            
            // 读取应答数据
            if (!XYKReadResult1(type))
            {
                return false;
            }

            // 检查交易是否成功
            XYKCheckRetCode();

            //无论是否成功，都检查打印
            XYKPrintDoc();
		    
		}
        catch (Exception ex)
        {
            ex.printStackTrace();
            XYKSetError("XX","金卡异常XX:"+ex.getMessage());
            new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);

            return false;
        }
		    
	       
		return true;
		
	}
	
	public boolean XYKWriteRequest(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo) 
	{
        try
        {
            String line = "";
            
            //收银机号 最大长度10位
            String syjh = Convert.increaseCharForward(ConfigClass.CashRegisterCode,' ', 10);
            //收银员号 最大长度10位
            String syyh = Convert.increaseCharForward(GlobalInfo.posLogin.gh,' ', 10);
            //交易类型 最大长度1位 
            //为C，表示是正数（消费交易）；
            //为I，表示是正数（查询余额交易）；
            //为D，表示是负数（取消交易）；
            //为R，表示是负数（退货交易）；
            //为0[零]，表示（结帐、交易一览和重打票据）。
            char type1 = ' ';
            //金额
            String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
            jestr = Convert.increaseCharForward(jestr,'0',12);
            
            //自定义 最大长度6为
            String cardType = "";
            cardType = Convert.increaseCharForward(oldauthno,' ', 6);
            
            //根据不同的类型生成文本结构
            switch (type)
            {
            	//消费
                case PaymentBank.XYKXF:
                    type1 = 'C';
                    break;
                //查询余额
                case PaymentBank.XYKYE:
                	type1 = 'I';
                	break;
                //取消交易
                case PaymentBank.XYKCX:
                	type1 = 'D';
                    break;
                //退货交易
                case PaymentBank.XYKTH:
                	type1 = 'R';
                	break;
                //结帐、交易一览和重打票据	
                default:
                    type1 = '0';
                	break;
            }
            
            line = syjh + syyh + type1 + jestr + cardType;
            
            PrintWriter pw = null;

            try
            {
                pw = CommonMethod.writeFile("c:\\gmc\\SOFTPOSREQ.TXT");

                if (pw != null)
                {
                    pw.print(line.toString());
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
	
	public boolean XYKReadResult1(int type) 
	{
		BufferedReader br = null;
		
        try
        {
            if (!PathFile.fileExist("c:\\gmc\\SOFTPOSRESP.TXT") ||
                    ((br = CommonMethod.readFileGBK("c:\\gmc\\SOFTPOSRESP.TXT")) == null))
            {
            	XYKSetError("XX","读取金卡工程应答数据失败!");
                new MessageBox("读取金卡工程应答数据失败!", null, false);
                
                return false;
            }

            String line = br.readLine();
            
            bld.retcode = line.substring(0,2);
            
            if (type != PaymentBank.XYKXF && type != PaymentBank.XYKCX && type != PaymentBank.XYKTH) 
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
            //卡号
            bld.cardno  = Convert.newSubString(line, 12, 31);
            //System.out.println("卡号： " + bld.cardno);
            //交易类型
            //String type2 = Convert.newSubString(line, 31, 32);

            String je   = Convert.newSubString(line, 32, 44);
            double j = Double.parseDouble(je);
            j = ManipulatePrecision.mul(j, 0.01);
            bld.je = j;
            //System.out.println("交易金额： " + bld.je);
            //流水号
            bld.trace   = Long.parseLong(Convert.newSubString(line, 44, 50));
            //System.out.println("交易流水号： " + bld.trace);
            //银行代码
            bld.bankinfo= Convert.newSubString(line, 58, 61);
            //System.out.println("银行代码： " + bld.bankinfo);
            
            String bankname = XYKReadBankName(bld.bankinfo);
            
            bld.bankinfo = bld.bankinfo+bankname;
        	errmsg = bld.retmsg;
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
				try {
					br.close();
				} catch (IOException e) {
					// TODO 自动生成 catch 块
					e.printStackTrace();
				}
        }
	}
	
    public String getErrorInfo(String retcode)
    {
        String line = "";

        try
        {
        	
            if (!PathFile.fileExist("c:\\gmc\\bankError.txt") ||
                    !rtf.loadFile("C:\\gmc\\bankError.txt"))
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

            return retcode;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return retcode;
        }
        finally
        {
        	rtf.close();
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
    
    public void XYKPrintDoc()
    {
        ProgressBox pb = null;

        try
        {
        	String printName = "c:\\gmc\\toprint.txt";
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
                        if (line.trim().equals("/CUT"))
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
    
    public void XYKPrintDoc_Start()
    {
    	Printer.getDefault().startPrint_Normal();
    }
    
    public void XYKPrintDoc_End()
    {
    	Printer.getDefault().cutPaper_Normal();
    }
    
    public void XYKPrintDoc_Print(String printStr)
    {
    	Printer.getDefault().printLine_Normal(printStr);
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
                grpLabelStr[4] = "退货";

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
    
    public boolean isCardType(BankLogDef bankLogDef, PayModeDef payModeDef) 
    {
    	String memo = bankLogDef.memo;
    	
    	if(memo == null)
    	{
    		return false;
    	}
    	
    	String payCode = payModeDef.code;
    	
    	String bankCode = compareBankCode(payCode);
    	
    	if (bankCode.equals(memo))
    	{
    		return true;
    	}
    	return false;
    }
    
    public String compareBankCode (String memo)
    {
    	if (memo.equals("0300"))
    	{
    		return  "1";
    	}
    	else if (memo.equals("0310"))
    	{
    		return "2";
    	}
    	else if (memo.equals("0311"))
    	{
    		return "3";
    	}
    	else
    	{
    		return "1";
    	}
    }
    
    public boolean callBankFunc(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo) 
    {
    	
	    if (memo == null || memo.size() < 1)
        {
	    	String[] title = {"代码","卡类型"};
			int[]    width = {60,440};
			Vector contents = new Vector();
			contents.add(new String[]{"1","人民币卡"});
			contents.add(new String[]{"2","VISA/MASTER国际卡"});
			contents.add(new String[]{"3","其它国际卡"});
			
			int choice = new MutiSelectForm().open("请选择交易卡类型", title, width, contents,true);
			if (choice == -1)
			{
				errmsg = "没有选择交易卡类型";
				return false;
			}
			else
			{
				memo = new Vector();
				String[] row = (String[])(contents.elementAt(choice));
				oldauthno = row[0];
			}
			
			// 刷新界面
			while (Display.getCurrent().readAndDispatch()); 
        }
	    else
	    {
	    	oldauthno = compareBankCode((String)memo.get(0));
	    }
	    
    	return super.callBankFunc(type, money, track1, track2, track3, oldseqno,
    			oldauthno, olddate, memo);
    }
    
    public String getMemo(int type, double money, String oldseqno, String oldauthno, String olddate) 
    {
    	return oldauthno;
    }
}
