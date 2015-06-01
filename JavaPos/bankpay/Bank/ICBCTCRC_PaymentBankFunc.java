package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

public class ICBCTCRC_PaymentBankFunc extends PaymentBankFunc
{
	protected String bankpath = ConfigClass.BankPath;

	public String[] getFuncItem()
	{
		String[] func = new String[6];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		//func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[2] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
		func[3] = "[" + PaymentBank.XYKJZ + "]" + "交易结账";
		func[4] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
		func[5] = "[" + PaymentBank.XYKCD + "]" + "签购单重打";

		return func;
	}
	
	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
        try
        {
            if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) &&
                    (type != PaymentBank.XYKQD) && (type != PaymentBank.XYKJZ) &&
                    (type != PaymentBank.XYKYE) && (type != PaymentBank.XYKCD))
            {
                errmsg = "银联接口不支持该交易";
                new MessageBox(errmsg);

                return false;
            }
            
            searchBankPath("ICBCTCRC");
            //System.err.println("ICBCTCRC "+bankpath);
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
			XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo,new StringBuffer());
            
			// 调用接口模块
			if (PathFile.fileExist(bankpath + "\\javaposbank.exe"))
			{
				CommonMethod.waitForExec(bankpath + "\\javaposbank.exe ICBC","javaposbank.exe");
			}
			else
			{
				new MessageBox("找不到金卡工程模块 javaposbank.exe");
				XYKSetError("XX", "找不到金卡工程模块 javaposbank.exe");
				return false;
			}

            // 读取应答数据
            if (!XYKReadResult(type))
            {
                return false;
            }

            // 检查交易是否成功
            XYKCheckRetCode();

            //无论是否成功，都检查打印
//            XYKPrintDoc();
            

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
	
	public boolean XYKReadResult(int type)
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
			
			String line = newLine;
            bld.retcode = line.substring(0,2);
            //System.err.println(line);
            //System.err.println(bld.retcode);
            if (!bld.retcode.equals("00"))
            {
            	bld.retmsg  = getErrorInfo(bld.retcode);
            	errmsg = bld.retmsg;
            	return false;
            }
            else
            {
            	bld.retmsg = "交易成功";
            	errmsg = bld.retmsg;
            }
            
            switch (type)
            {
                case PaymentBank.XYKXF: //消费
                	bld.cardno = Convert.newSubString(line, 2, 21);
                	bld.trace = Long.parseLong(Convert.newSubString(line, 35, 41));
                    break;

                case PaymentBank.XYKCX: //消费撤销
                	bld.cardno = Convert.newSubString(line, 2, 21);
                	bld.trace = Long.parseLong(Convert.newSubString(line, 35, 41));

                    break;

                case PaymentBank.XYKYE: //余额查询    
                	bld.cardno = Convert.newSubString(line, 2, 21);
                    break;

                case PaymentBank.XYKCD: //签购单重打
                    break;

                case PaymentBank.XYKQD: //交易签到
                	break;
                case PaymentBank.XYKJZ:
                	break;
                default:
                    return false;
            }
            
            // 由于通过返回值打印小票，所以在此地调用\
            
            if (type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XYKCD || type == PaymentBank.XYKJZ)
            {
            	XYKPrintDoc(line,type);
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
	
	public String XYKWriteRequest(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo, StringBuffer arg)
	{
		String line = "";
		String syyid = Convert.increaseChar(GlobalInfo.posLogin.gh, 6);
        String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
        jestr = Convert.increaseCharForward(jestr,'0',12);
		try{
//			交易类型
            switch (type)
            {
                case PaymentBank.XYKXF: //消费
                	line ="2,"+Convert.increaseChar(track2, 37)+","+Convert.increaseChar(track3,104)+","+jestr+","+syyid+", , ";
                    break;

                case PaymentBank.XYKCX: //消费撤销
                	line ="3,"+Convert.increaseChar(track2, 37)+","+Convert.increaseChar(track3,104)+","+jestr+","+Convert.increaseCharForward(oldseqno,'0',6)+","+syyid+", , ";

                    break;

                case PaymentBank.XYKYE: //余额查询    
                    line ="1,"+Convert.increaseChar(track2, 37)+","+Convert.increaseChar(track3,104)+","+syyid+", , ";

                    break;

                case PaymentBank.XYKCD: //签购单重打
                	line ="4,"+Convert.increaseCharForward(oldseqno,'0',6)+","+syyid+", , ";
                    break;

                case PaymentBank.XYKQD: //交易签到
                    line = "0"+","+syyid+", , ";

                    break;

                case PaymentBank.XYKJZ: //交易结账
                	line ="6,"+syyid+", , ";
                    break;

            	/**
                case PaymentBank.XYKXF: //消费
                	line ="2,"+Convert.increaseChar(track2, 37)+","+Convert.increaseChar(track3,104)+","+jestr+ ","+syyid+", , ";
                    break;

                case PaymentBank.XYKCX: //消费撤销
                	line ="3,"+Convert.increaseChar(track2, 37)+","+Convert.increaseChar(track3,104)+","+jestr+","+Convert.increaseCharForward(oldseqno,'0',6)+ ","+syyid+", , ";

                    break;

                case PaymentBank.XYKYE: //余额查询    
                    line ="1,"+Convert.increaseChar(track2, 37)+","+Convert.increaseChar(track3,104)+ ","+syyid+", , ";

                    break;

                case PaymentBank.XYKCD: //签购单重打
                	line ="4,"+Convert.increaseCharForward(oldseqno,'0',6)+ ","+syyid+", , ";

                    break;

                case PaymentBank.XYKQD: //交易签到
                    line = "0"+ ","+syyid+", , ";

                    break;

                case PaymentBank.XYKJZ: //交易结账
                	line ="6,"+syyid+", , ";
                    break;
				*/
                default:
                    return null;
            }
			
		}catch(Exception er)
		{
			er.printStackTrace();
		}
		PrintWriter pw = CommonMethod.writeFile(bankpath + "\\request.txt");

		if (pw != null)
		{
			pw.println(line);
			pw.flush();
			pw.close();
		}
		
		return null;
	}
	
	public void XYKPrintDoc(String line,int type)
	{
		String type1 = "";
		String line1 = "";
		if (type == PaymentBank.XYKJZ)
		{
			//消费笔数|消费金额|撤销笔数|撤销金额
			line = line.substring(2);
			String[] infos = line.split("\\|");
			line1 = line1 + Convert.appendStringSize("", "银行卡交易日结统计", 0, 38, 38,2)+"\n";
			line1 = line1 + Convert.appendStringSize("", " ------------------------------------", 0, 38, 38,2)+"\n";
			line1 = line1 + Convert.appendStringSize("", "打印日期："+ManipulateDateTime.getCurrentDateBySign()+" "+ManipulateDateTime.getCurrentTime(), 0, 38, 38)+"\n";
			line1 = line1 + Convert.appendStringSize("", "消费笔数："+infos[0], 0, 38, 38)+"\n";
			line1 = line1 + Convert.appendStringSize("", "消费金额：RMB"+infos[1], 0, 38, 38)+"\n";
			line1 = line1 + Convert.appendStringSize("", "撤销笔数："+infos[2], 0, 38, 38)+"\n";
			line1 = line1 + Convert.appendStringSize("", "撤销金额：RMB"+infos[3], 0, 38, 38)+"\n";
		}
		else
		{
		
			if (type == PaymentBank.XYKXF)
			{
				type1 = "消费";
			}
			else if (type == PaymentBank.XYKCX)
			{
				type1 = "撤销";
			}
			else if (type == PaymentBank.XYKTH)
			{
				type1 = "隔日退货";
			}
			else if (type == PaymentBank.XYKCD)
			{
				String type2 = line.substring(2,6);
				if (type2.equals("0200")) 
				{
					type1 = "消费";
				}
				else if (type2.equals("0400")) 
				{
					type1 = "撤销";
				}
				else if (type2.equals("0405"))
				{
					type1 = "隔日退货";
				}
				else
				{
					type1 = "未知交易";
				}
				
				line = "00"+line.substring(6);
			}
			else
			{
				type1 = "消费";
			}
			
			String cardno = line.substring(2,21);
			String rq = line.substring(21,29);
			String sj = line.substring(29,35);
			String zdls = line.substring(35,41);
			String bch = line.substring(41,47);
			String sqh = line.substring(47,53);
			String jsckh = line.substring(53,61);
			String kpyxq = line.substring(61,65);
			String jyje = line.substring(65,77);
			String temno = line.substring(77);
			
			String hcardno = cardno.trim().substring(0, 6)+"******"+cardno.trim().substring(cardno.trim().length()-4);
			
			line1 = line1 + Convert.appendStringSize("", "", 0, 38, 38)+"\n";
			if (PaymentBank.XYKCD == type) 
			{
				line1 = line1 + Convert.appendStringSize("", "重打印" , 0, 38, 38,2)+"\n";
			}
			line1 = line1 + Convert.appendStringSize("", "中国工商银行POS交易凭证" , 0, 38, 38)+"\n";
			line1 = line1 + Convert.appendStringSize("", Convert.increaseChar("", '-', 36), 0, 38, 38)+"\n";
			line1 = line1 + Convert.appendStringSize("", "商 户: "+GlobalInfo.sysPara.shopname, 0, 38, 38)+"\n";
			line1 = line1 + Convert.appendStringSize("", "终端编号: "+temno, 0, 38, 38)+"\n";
			line1 = line1 + Convert.appendStringSize("", "收银员号: "+GlobalInfo.posLogin.gh, 0, 38, 38)+"\n";
			line1 = line1 + Convert.appendStringSize("", Convert.increaseChar("", '-', 36), 0, 38, 38)+"\n";
			line1 = line1 + Convert.appendStringSize("", "卡号: "+hcardno, 0, 38, 38)+"\n";
			line1 = line1 + Convert.appendStringSize("", "有效期: "+kpyxq, 0, 38, 38)+"\n";
			line1 = line1 + Convert.appendStringSize("", "交易时间: "+rq.substring(0,4)+"/"+rq.substring(4,6)+"/"+rq.substring(6)+" "+sj.substring(0,2)+":"+sj.substring(2,4)+":"+sj.substring(4), 0, 38, 38)+"\n";
			line1 = line1 + Convert.appendStringSize("", "流水号: "+zdls, 0, 38, 38)+"\n";
			line1 = line1 + Convert.appendStringSize("", "检索参考号: "+jsckh, 0, 38, 38)+"\n";
			line1 = line1 + Convert.appendStringSize("", "授权号: "+sqh, 0, 38, 38)+"\n";
			line1 = line1 + Convert.appendStringSize("", "批次号: "+bch, 0, 38, 38)+"\n";
			
			line1 = line1 + Convert.appendStringSize("", "交易类型: "+type1, 0, 38, 38)+"\n";
			line1 = line1 + Convert.appendStringSize("", "交易金额: RMB "+ManipulatePrecision.doubleToString(Convert.toDouble(jyje)/100), 0, 38, 38)+"\n";
			line1 = line1 + Convert.appendStringSize("", Convert.increaseChar("", '-', 36), 0, 38, 38)+"\n";
			line1 = line1 + Convert.appendStringSize("", "持卡人签字", 0, 38, 38)+"\n\n\n\n";
			line1 = line1 + Convert.appendStringSize("", Convert.increaseChar("", '-', 36), 0, 38, 38)+"\n";
			line1 = line1 + Convert.appendStringSize("", "本人接受单据金额及有关商品并愿意", 0, 38, 38)+"\n";
			line1 = line1 + Convert.appendStringSize("", "遵守发卡行的持卡人和约内一切条款", 0, 38, 38)+"\n";
			line1 = line1 + Convert.appendStringSize("", "", 0, 38, 38)+"\n";
			
		}
		
		for (int i = 0 ; i < 2;i++)
		{
			Printer.getDefault().printLine_Normal(line1);
			Printer.getDefault().cutPaper_Normal();
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
                
                //System.err.println(line); 
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

	public void searchBankPath(String className)
	{
		if (className.indexOf(".ini") < 0)
		{
			className+=".ini";
		}
		
		String line = null;
		if (!PathFile.fileExist(className)
				|| !rtf.loadFileByGBK(className))
		{
			return ;
		}
		
		//
		while ((line = rtf.nextRecord()) != null)
		{
			if (line.length() <= 0)
			{
				continue;
			}

			bankpath = line;
			break;
		}

		rtf.close();
	}
}
