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
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

public class GzgyJFK_PaymentBankFunc extends PaymentBankFunc
{
	protected String bankpath = ConfigClass.BankPath;
	
    public String[] getFuncItem()
    {
		String[] func = new String[3];
    	
		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKQD + "]" + "交易签到";

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
                grpLabelStr[3] = "请 刷 卡";
                grpLabelStr[4] = "交易金额";

                break;

            case PaymentBank.XYKCX: //消费撤销
            	grpLabelStr[0] = "原流水号";
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = "请 刷 卡";
                grpLabelStr[4] = "交易金额";

                break;
                
			case PaymentBank.XYKQD://交易签到
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
        }

        return true;
    }

    public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
    {
        try
        {
        	bld.memo = "JFK";
        	
            if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && (type != PaymentBank.XYKQD))
            {
                errmsg = "银联接口不支持该交易";
                new MessageBox(errmsg);

                return false;
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
				CommonMethod.waitForExec(bankpath + "\\javaposbank.exe GZGA","javaposbank.exe");
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
        	String shh = Convert.increaseChar("",' ', 15);
        	
        	String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
        	jestr = Convert.increaseCharForward(jestr,'0', 12);
        	String zzh = "";
        	String pos_mode = "022";
        	String paycode1 = null;
        	if(memo != null && memo.size() > 0)  paycode1 = (String) memo.elementAt(0);
        	String order_no =  Convert.increaseChar(GlobalInfo.syjDef.syjh+";"+GlobalInfo.syjStatus.fphm+";"+paycode1,' ',20);
        	track2 = Convert.increaseChar(track2,' ', 37);
        	track3 = Convert.increaseChar(track3,' ', 104);
        	String mid = Convert.increaseChar("",' ', 15);
        	String addition_data = "01";
        	String pin = Convert.increaseChar("",' ', 64);
        	String bat_no = "000000";
        	String original_trace_no = Convert.increaseChar(oldseqno,' ', 6);
        	String line = "";
        	if (type == PaymentBank.XYKQD)
        	{
        		line = 0+","+shh.trim();
        		//line = 0+","+shh;
        	}
        	else 
        	if (type == PaymentBank.XYKXF)
        	{
        		line = 1+","+zzh.trim()+","+jestr.trim()+","+pos_mode.trim()+","+order_no.trim()+","+track2.trim()+","+track3.trim()+","+mid.trim()+","+addition_data.trim()+","+pin.trim()+","+bat_no.trim();
        		//line = 1+","+zzh+","+jestr+","+pos_mode+","+order_no+","+track2+","+track3+","+mid+","+addition_data+","+pin+","+bat_no;
        	}
        	else if (type == PaymentBank.XYKCX)
        	{
        		line = 2+"," + zzh.trim()+","+jestr.trim()+","+pos_mode.trim()+","+order_no.trim()+","+track2.trim()+","+track3.trim()+","+mid.trim()+","+pin.trim()+","+original_trace_no.trim()+","+bat_no.trim();
        		//line = 2+","+zzh+","+jestr+","+pos_mode+","+order_no+","+track2+","+track3+","+mid+","+pin+","+original_trace_no+","+bat_no;
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
    
//    private String account_out = "";          //交易金额
    private String trace_no_out = "";		  //流水号
    private String trans_time_out = "";		  //交易时间
    private String trans_date_out = "";		  //交易日期
//    private String order_no_out = "";		  //订单号
    private String sys_no_out = "";			  //系统参考号
//    private String authority_code_out = "";	  //授权码
//    private String resp_code_out = "";		  //交易响应码
    private String term_no_out = "";		  //终端号
    private String original_trace_no_out = "";//原流水号
    private String addition_account = "";	  //附加金额
    private String card_no = "";			  //主帐号

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
			
			//new MessageBox(newLine);
			
			if (Convert.toInt(lines[0]) != 0)
			{
				bld.retmsg  = "银联交易失败,函数返回"+lines[0];
				return false;
			}
			
			if (type == PaymentBank.XYKQD)
			{
				bld.trace = Convert.toLong(lines[1]);
				bld.retcode = lines[4];
				bld.memo = "," + lines[5].trim() + "," + paycode;
			}
			else
			if (type == PaymentBank.XYKXF)
			{
				bld.cardno = lines[1].trim();
				bld.trace = Convert.toLong(lines[2].trim());
				bld.memo = lines[6].trim() + "," + lines[8].trim() + "," + paycode;
				bld.retcode = lines[7].trim();
				
				card_no = lines[1].trim();
			    trace_no_out = lines[2].trim();
			    trans_time_out = lines[3].trim();
			    trans_date_out = lines[4].trim();
//			    order_no_out = lines[5].trim();
			    sys_no_out = lines[6].trim();
//			    resp_code_out = lines[7].trim();
			    term_no_out = lines[8].trim();
			    addition_account = lines[9].trim();
			    
			    original_trace_no_out = "";
//			    account_out = "";
//			    authority_code_out = "";
			}
			else if (type == PaymentBank.XYKCX)
			{
				//bld.cardno = lines[1].trim();
				bld.trace = Convert.toLong(lines[2].trim());
				bld.memo = lines[6].trim() + "," + lines[9].trim() + "," + paycode;
				bld.retcode = lines[8].trim();
				
//			    account_out = lines[1].trim();
			    trace_no_out = lines[2].trim();
			    trans_time_out = lines[3].trim();
			    trans_date_out = lines[4].trim();
//			    order_no_out = lines[5].trim();
			    sys_no_out = lines[6].trim();
//			    authority_code_out = lines[7].trim();
//			    resp_code_out = lines[8].trim();
			    term_no_out = lines[9].trim();
			    original_trace_no_out = lines[10].trim();
			    addition_account = lines[11].trim();
			    card_no = lines[12].trim();
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

	private String c_writePrintDoc(String strPrintBuffer)
	{
		try
		{
			String strDocDir = ConfigClass.LocalDBPath + "//Invoice";
			String strDocFile = strDocDir + "//Jfkdoc_" + bld.syjh + "_" + bld.fphm + "_" + Convert.increaseCharForward(String.valueOf(bld.trace),'0', 6) + ".txt";
			
			try
			{
				if (PathFile.fileExist(strDocFile))
				{
					PathFile.deletePath(strDocFile); 
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			
			PrintWriter pw = CommonMethod.writeFileAppendGBK(strDocFile);
			pw.print(strPrintBuffer.toString());
	        pw.flush();
	        pw.close();
	        
			return strDocFile;
		}
		catch(Exception ex)
		{
			return null;
		}
	}
	
    public void XYKPrintDoc()
    {
        ProgressBox pb = null;

        try
        {	
            int type = Integer.parseInt(bld.type.trim());

            StringBuffer sb = new StringBuffer();
        	for (int i = 0;i < 3;i++)
        	{        		
	        	sb.append("			积分卡			" + "\n");
	        	sb.append("商户名称:	PPT（1C076）" + "\n");
	        	sb.append("商户编号:	001440165130001" + "\n");
	        	sb.append("终 端 号:	" + term_no_out + "\n");
	        	sb.append("卡    号:	" + card_no + "\n");
	        	if (type == PaymentBank.XYKCX)
	        	{
	        		sb.append("交易类型:	消费撤销" + "\n");
	        	}
	        	else if (type == PaymentBank.XYKXF)
	        	{
	        		sb.append("交易类型:	消费" + "\n");
	        	}	
	        	sb.append("			RMB" + ManipulatePrecision.doubleToString(bld.je)  + "\n");
	        	//sb.append("批 次 号:	000002" + "\n");
	        	sb.append("凭 证 号:	" + trace_no_out + "\n");
	        	sb.append("参 考 号:	" + sys_no_out + "\n");
	        	sb.append("交易日期:	" + trans_date_out + " " + trans_time_out + "\n");
	        	if (type == PaymentBank.XYKCX)
	        	{
	        		sb.append("原凭证号:	" + original_trace_no_out + "\n");
	        	}
	        	else if (type == PaymentBank.XYKXF)
	        	{
	        		sb.append("				 " + "\n");
	        	}
	        	sb.append("余    额:	" + addition_account + "\n");
	        	sb.append("本人确认以上交易，同意将其记入本卡账户	" + "\n");
	        	
	        	switch(i)
	        	{
	        		case 0:
	        			sb.append("(MERCHANT COPY)	" + "\n");
	        			sb.append("------商户存根 请妥善保管--------" + "\n");
	        			break;
	        		case 1:
	        			sb.append("(BANK COPY)		" + "\n");
	        			sb.append("------银行存根 请妥善保管--------" + "\n");
	        			break;
	        		case 2:
	        			sb.append("(CUSTOMER COPY)	" + "\n");
	        			sb.append("------持卡人存根 请妥善保管------	" + "\n");
	        			break;
	        	}
	        	
	        	sb.append("CUTPAPER" + "\n");
        	}
        	
        	String printName = c_writePrintDoc(sb.toString());
        	if (printName == null)
        	{
        		new MessageBox("生成打印文件失败!");
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
    
   
}
