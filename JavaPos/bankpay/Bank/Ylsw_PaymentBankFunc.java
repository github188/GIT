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
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;


public class Ylsw_PaymentBankFunc extends PaymentBankFunc
{
    public String[] getFuncItem()
    {
        String[] func = new String[6];

        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
        func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
        func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
        func[3] = "[" + PaymentBank.XYKJZ + "]" + "交易结账";
        func[4] = "[" + PaymentBank.XYKCD + "]" + "单据重打印";
        func[5] = "[" + PaymentBank.XKQT1 + "]" + "其他功能";

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

            case PaymentBank.XYKTH: //隔日退货   
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易金额";

                break;

            case PaymentBank.XYKJZ: //交易结账
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易结账";

                break;

            case PaymentBank.XKQT1: //其他功能
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "其他功能";

                break;

            case PaymentBank.XYKCD: //单据重打
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "单据重打";

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

            case PaymentBank.XYKTH: //隔日退货   
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = null;

                break;

            case PaymentBank.XYKJZ: //交易结账
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始交易结账";

                break;

            case PaymentBank.XKQT1: //其他功能
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始其他功能";

                break;

            case PaymentBank.XYKCD: //签购单重打
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始单据重打";

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
        	if ((type != PaymentBank.XYKXF) && 
        		(type != PaymentBank.XYKCX) && 
        		(type != PaymentBank.XYKCD) && 
        		(type != PaymentBank.XKQT1) && 
        		(type != PaymentBank.XYKTH) && 
        		(type != PaymentBank.XYKJZ)
        		)
	            {
	                errmsg = "银联接口不支持该交易";
	                new MessageBox(errmsg);

	                return false;
	            }
        	
            // 写入请求数据
            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno,
                                     oldauthno, olddate, memo))
            {
                return false;
            }

            // 调用接口模块
            CommonMethod.waitForExec("c:\\gmc\\GMC.exe","GMC.exe");

            // 读取应答数据
            if (!XYKReadResult())
            {
                return false;
            }

            // 检查交易是否成功
            XYKCheckRetCode();

            // 打印签购单
            if (XYKNeedPrintDoc())
            {
                XYKPrintDoc();
            }

            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);

            return false;
        }
    }

    public boolean XYKWriteRequest(int type, double money, String track1,
                                   String track2, String track3,
                                   String oldseqno, String oldauthno,
                                   String olddate, Vector memo)
    {
		if ((type != PaymentBank.XYKXF) && 
			(type != PaymentBank.XYKCX) && 
			(type != PaymentBank.XYKCD) && 
			(type != PaymentBank.XKQT1) && 
			(type != PaymentBank.XYKTH) && 
			(type != PaymentBank.XYKJZ)
			)
            {
                errmsg = "银联接口不支持该交易";
                new MessageBox(errmsg);

                return false;
            }
		
        // 调用接口模块
        if (PathFile.fileExist("c:\\gmc\\GMC.exe"))
        {
            if (PathFile.fileExist("c:\\gmc\\request.txt"))
            {
            	PathFile.copyPath("c:\\gmc\\request.txt","c:\\gmc\\LastRequest.txt");
                PathFile.deletePath("c:\\gmc\\request.txt");
                
                if (PathFile.fileExist("c:\\gmc\\request.txt"))
                {
            		errmsg = "交易请求文件request.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }

            if (PathFile.fileExist("c:\\gmc\\response.txt"))
            {
            	PathFile.copyPath("c:\\gmc\\response.txt","c:\\gmc\\LastResponse.txt");
                PathFile.deletePath("c:\\gmc\\response.txt");
                
                if (PathFile.fileExist("c:\\gmc\\response.txt"))
                {
            		errmsg = "交易请求文件response.TXT无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            if (PathFile.fileExist("c:\\gmc\\Temp_prn.txt"))
            {
            	PathFile.copyPath("c:\\gmc\\Temp_prn.txt","c:\\gmc\\LastTemp_prn.txt");
                PathFile.deletePath("c:\\gmc\\Temp_prn.txt");
                
                if (PathFile.fileExist("c:\\gmc\\Temp_prn.txt"))
                {
            		errmsg = "交易请求文件Temp_prn.txt无法删除,请重试";
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
        
        StringBuffer sbstr = null;

        try
        {
            sbstr = new StringBuffer();

            // 组织请求数据
            // 收银机号;操作员号;交易类型;交易金额;原交易日;原流水号;一磁道;二磁道;三磁道;CRC
            String syjh = Convert.increaseChar(GlobalInfo.syjDef.syjh, 15);

            String gh  = Convert.increaseChar(GlobalInfo.posLogin.gh , 15);
            
            String typecode = "O";

            switch (type)
            {
                case PaymentBank.XYKXF: //消费
                    typecode = "C";

                    break;

                case PaymentBank.XYKCX: //消费撤销
                    typecode = "D";

                    break;

                case PaymentBank.XYKTH: //隔日退货   
                    typecode = "R";

                    break;

                case PaymentBank.XYKJZ: //交易结账
                    typecode = "E";

                    break;

                case PaymentBank.XKQT1: //其他功能  
                    typecode = "E";

                    break;

                case PaymentBank.XYKCD: //单据重打
                    typecode = "E";

                    break;

                default:
                    return false;
            }

            String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,
                                                                                   2,
                                                                                   1));

            for (int i = jestr.length(); i < 12; i++)
            {
                jestr = "0" + jestr;
            }

            String memo1 = "000000";

            bld.crc = XYKGetCRC();

            sbstr.append(syjh);
            sbstr.append(gh);
            sbstr.append(typecode);
            sbstr.append(jestr);
            sbstr.append(memo1);
            sbstr.append(bld.crc);

            PrintWriter pw = null;
            PrintWriter pw1 = null;
            try
            {
                pw = CommonMethod.writeFile("c:\\gmc\\request.txt");
                pw1 = CommonMethod.writeFileAppend("c:\\gmc\\logs_bak.txt");
                if (pw != null)
                {
                    pw.print(sbstr.toString());
                    pw.flush();
                }
                
                if (pw1 != null)
                {
                    pw1.print(ManipulateDateTime.getCurrentDateTime()+"  传入:"+sbstr.toString());
                    pw1.flush();
                }
            }
            finally
            {
                if (pw != null)
                {
                    pw.close();
                }
                
                if (pw1 != null)
                {
                    pw1.close();
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
        finally
        {
            if (sbstr != null)
            {
                sbstr.delete(0, sbstr.length());
                sbstr = null;
            }
        }
    }
    
    public boolean XYKReadResult()
    {
    	BufferedReader br = null;

        try
        {
            if (!PathFile.fileExist("c:\\gmc\\response.txt") ||
                    ((br = CommonMethod.readFileGBK("c:\\gmc\\response.txt")) == null))
            {
            	XYKSetError("XX","读取金卡工程应答数据失败!");
                new MessageBox("读取金卡工程应答数据失败!", null, false);
                
                return false;
            }

            // 读取请求数据
            String line = br.readLine();
            
            PrintWriter pw1 = null;
            try
            {
            	pw1 = CommonMethod.writeFileAppend("c:\\gmc\\logs_bak.txt");
	            if (pw1 != null)
	            {
	                pw1.print(ManipulateDateTime.getCurrentDateTime()+" 传出:"+line.toString());
	                pw1.flush();
	            }
            }
            catch(Exception er)
            {
            	er.printStackTrace();
            }
            finally
            {
                if (pw1 != null)
                {
                    pw1.close();
                }
            }
            //相应码
            bld.retcode = Convert.newSubString(line, 0, 2);
            
            //金额
            //String money =  Convert.newSubString(line, 2, 14).trim();
            //卡号
            bld.cardno = Convert.newSubString(line, 14, 33);
            //备用
            //String memo =  Convert.newSubString(line, 33, 52);
            //卡类型标志
            String cardtype   = Convert.newSubString(line, 52, 56).trim();
            //卡名称
            String cardName   = Convert.newSubString(line, 56, 64).trim();
            
            String CRC = Convert.newSubString(line,64,67);
            
            //商户号
            //终端号
            //批次号
            //流水号
            //系统参考号
            //日期
            //时间
            
            bld.bankinfo = cardtype+cardName;
            
            
            if (!CRC.equals(bld.crc))
            {
            	errmsg = "返回效验码"+CRC+"同原始效验码"+bld.crc+"不一致";
            	XYKSetError("XX",errmsg);
            	new MessageBox(errmsg);
            	try
            	{
	            	//保存返回文件
	            	ManipulateDateTime mdt  = new ManipulateDateTime();
	            	String date = GlobalInfo.balanceDate.replaceAll("/", "");
	                String name =ConfigClass.LocalDBPath + "Invoice//" + date+"//"+mdt.getTime().replaceAll(":", "_")+"_Bank.txt";
	            	PathFile.copyPath("c:\\gmc\\response.txt",name);
            	}
            	catch(Exception er)
            	{
            		er.printStackTrace();
            	}
            	return false;
            }
            
            if (bld.retcode.equals("00"))
            {
            	bld.retmsg = "交易成功";
            }
            
            errmsg = bld.retmsg;
            
            return true;
        }
        catch (Exception ex)
        {
            new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);    
            XYKSetError("XX","读取应答XX:"+ex.getMessage());
            ex.printStackTrace();
            
            PrintWriter pw1 = null;
            try
            {
            	pw1 = CommonMethod.writeFileAppend("c:\\gmc\\logs_bak.txt");
	            if (pw1 != null)
	            {
	                pw1.print(ManipulateDateTime.getCurrentDateTime()+" Exception:"+ex.getMessage());
	                pw1.flush();
	            }
            }
            catch(Exception er)
            {
            	er.printStackTrace();
            }
            finally
            {
                if (pw1 != null)
                {
                    pw1.close();
                }
            }
            
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
					// TODO 自动生成 catch 块
					new MessageBox("response.TXT 关闭失败\n重试后如果仍难失败，请联系信息部");
					e.printStackTrace();
				}
        	}
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
    	if (GlobalInfo.sysPara.bankprint <= 0) return;
    	
        ProgressBox pb = null;

        try
        {
            if (!PathFile.fileExist("C:\\gmc\\Temp_prn.txt"))
            {
                new MessageBox("找不到签购单打印文件!");

                return;
            }

            pb = new ProgressBox();
            pb.setText("正在打印银联签购单,请等待...");

            for (int i = 0; i <GlobalInfo.sysPara.bankprint; i++)
            {
                BufferedReader br = null;

                //
                Printer.getDefault().startPrint_Normal();

                try
                {
                    //
                    br = CommonMethod.readFileGBK("C:\\gmc\\Temp_prn.txt");

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

                        Printer.getDefault().printLine_Normal(line + "\n");
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
                
                // 切纸
                Printer.getDefault().cutPaper_Normal();
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
        if ((type == PaymentBank.XYKJZ) || 
    		(type == PaymentBank.XYKXF) || 
    		(type == PaymentBank.XYKCX) || 
    		(type == PaymentBank.XYKTH) || 
    		(type == PaymentBank.XYKCD))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
}
