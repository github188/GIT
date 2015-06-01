package com.efuture.javaPos.Payment.Bank;

import java.io.BufferedReader;
import java.io.File;
import java.util.Vector;

import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Payment.PaymentBank;

import custom.localize.Cczz.Cczz_AccessDayDB;


public class Cczz_PaymentBankFunc extends PaymentBankFunc
{
	 static{
		    System.loadLibrary("CCYL");
	 }
	 
	 public native String creditTransABC(String input);
	 
    public String XYKWriteRequest(int type, double money, String track1,
                                   String track2, String track3,
                                   String oldseqno, String oldauthno,
                                   String olddate, Vector memo,StringBuffer arg)
    {
        String crc = "";

        try
        {
            // 调用接口模块
            if (PathFile.fileExist("c:\\gmc\\posinf.dll"))
            {
                if (PathFile.fileExist("C:\\gmc\\receipt.txt"))
                {
                    PathFile.deletePath("C:\\gmc\\receipt.txt");
                }

                String cmd;
                String typecode = "";
                String jestr = "";

                //交易类型
                switch (type)
                {
                    case PaymentBank.XYKXF: //消费
                        typecode = "00";

                        break;

                    case PaymentBank.XYKCX: //消费撤销
                        typecode = "01";

                        break;

                    case PaymentBank.XYKTH: //隔日退货   
                        typecode = "02";

                        break;

                    case PaymentBank.XYKYE: //余额查询    
                        typecode = "03";

                        break;

                    case PaymentBank.XYKCD: //签购单重打
                        typecode = "04";

                        break;

                    case PaymentBank.XYKQD: //交易签到
                        typecode = "05";

                        break;

                    case PaymentBank.XYKJZ: //交易结账
                        typecode = "06";

                        break;
                    case PaymentBank.XKQT1: //其他交易
                        typecode = "07";

                        break;
                    default:
                        return null;
                }

                //收银机号
                String syjh = Convert.increaseChar(GlobalInfo.syjDef.syjh, 8);

                //操作员号
                String gh;
                gh = Convert.increaseChar(GlobalInfo.posLogin.gh, 8);

                //磁道数据
                if ((track1 != null) && (track1.length() > 0))
                {
                    track1 = Convert.increaseChar(track1, 78);
                }
                else
                {
                    track1 = Convert.increaseChar("", 78);
                }

                if (track2 != null)
                {
                    track2 = Convert.increaseChar(track2, 37);
                }
                else
                {
                    track2 = Convert.increaseChar("", 37);
                }

                if (track3 != null)
                {
                    track3 = Convert.increaseChar(track3, 104);
                }
                else
                {
                    track3 = Convert.increaseChar("", 104);
                }

                //金额
                jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,
                                                                                2,
                                                                                1));

                for (int i = jestr.length(); i < 12; i++)
                {
                    jestr = "0" + jestr;
                }

                // 交易校验数据
                crc = XYKGetCRC();

                if (crc.length() > 3)
                {
                    crc = crc.substring(0, 3);
                }

                bld.crc = crc;

                //流水号
                String seq = "";

                if ((oldseqno == null) || (oldseqno.length() <= 0))
                {
                    seq = Convert.increaseInt(0, 6);
                }
                else
                {
                    try
                    {
                        int num_seq = Integer.parseInt(oldseqno);
                        seq = Convert.increaseInt(num_seq, 6);
                    }
                    catch (Exception er)
                    {
                        seq = Convert.increaseInt(0, 6);
                    }
                }

                if (olddate != null)
                {
                    olddate = Convert.increaseChar(olddate, 8);
                }
                else
                {
                    olddate = Convert.increaseChar("", 8);
                }

                if (oldauthno != null)
                {
                    oldauthno = Convert.increaseChar(oldauthno, 12);
                }
                else
                {
                    oldauthno = Convert.increaseChar("", 12);
                }

                cmd = syjh + gh + typecode + jestr + olddate + oldauthno + seq +
                      track2 + track3 + crc;
                System.out.println("send "+cmd);
                return cmd;
            }
            else
            {
                new MessageBox("找不到金卡工程模块 posinf.DLL");

                return null;
            }
        }
        catch (Exception ex)
        {
            new MessageBox("写入金卡工程请求数据异常!\n\n" + ex.getMessage(), null, false);
            ex.printStackTrace();

            return null;
        }
    }
    
	public boolean checkDate(Text date)
	{
		String date1 = null;
		if (date.getText().length() == 4 )
		{
			date1 = ManipulateDateTime.getCurrentDate();
			String date2[]  = date1.split("/");
			
			int year = Convert.toInt(date2[0]);
			int month = Convert.toInt(date2[1]);
			
			String nowdate = date.getText();
			
			int month1 = Convert.toInt(nowdate.substring(0,2));
			
			if (month1 == 0) 
			{
				new MessageBox("日期的输入格式必须为:\n20080203（YYYYMMDD）\n或\n0203(MMDD)");
				return false;
			}
			
			if ( month1 > month )
			{
				year --;
			}
			
			date.setText(year+nowdate);
		}
		else if (date.getText().length() == 8)
		{
			date1 = ManipulateDateTime.getConversionDate(date.getText());
			
			if (ManipulateDateTime.checkDate(date1))
			{
				return true;
			}
			else
			{
				new MessageBox("日期的输入格式必须为:\n20080203（YYYYMMDD）\n或\n0203(MMDD)");
				return false;
			}
		}
		else
		{
			new MessageBox("日期的输入格式必须为:\n20080203（YYYYMMDD）\n或\n0203(MMDD)");
			return false;
		}
		return true;
	}

    public boolean XYKExecute(int type, double money, String track1,
                              String track2, String track3, String oldseqno,
                              String oldauthno, String olddate, Vector memo)
    {
        try
        {
        	String line = null;
            //        	 写入请求数据
            if ( (line = XYKWriteRequest(type, money, track1, track2, track3, oldseqno,
                                     oldauthno, olddate, memo,new StringBuffer())) == null)
            {
            	new MessageBox("生成交易请求失败！");
            	return false;
            }

            String line1 = null;
            ProgressBox box = new ProgressBox();
            try
            {
            	box.setText("请输入银行卡密码.......");
            	line1 = creditTransABC(line);
            }
            catch (Exception e)
            {
                // handle exception
                e.printStackTrace();
                new MessageBox(e.getMessage());
            }
            finally
            {
            	if (box != null)
            	{
            		box.close();
            		box = null;
            	}
            }

            // 读取应答数据
            if (!XYKReadResult(line1))
            {
                return false;
            }
            
            if (type == PaymentBank.XKQT1)
            {
            	getXYKSummary(bld.retmsg);
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
    
    public void getXYKSummary(String msg)
    {
    	if (!bld.retcode.equals("00"))
    	{
    		new MessageBox("获取返回信息失败，对账失败");
    		return ;
    	}
    	// 交易核对数据结果 消费笔数(3位)消费金额（12位）退货笔数（3位）退货金额（12位）
    	int xfbs = Convert.toInt(msg.substring(0,3));
    	String xfje = ManipulatePrecision.doubleToString(Convert.toInt(msg.substring(3, 15)) * 0.01);
    	
    	int thbs = Convert.toInt(msg.substring(15,18));
    	String thje = ManipulatePrecision.doubleToString(Convert.toInt(msg.substring(18, 33)) * 0.01);
    	
    	String line1 = Convert.appendStringSize("", "消费笔数:", 1, 10, 40);
    	line1 = Convert.appendStringSize(line1, String.valueOf(xfbs), 11, 3, 40);
    	line1 = Convert.appendStringSize(line1, "消费金额:", 18, 10, 40);
    	line1 = Convert.appendStringSize(line1, xfje, 28, 12, 40);
    	
    	String line2 = Convert.appendStringSize("", "退货笔数:", 1, 10, 40);
    	line2 = Convert.appendStringSize(line2, String.valueOf(thbs), 11, 3, 40);
    	line2 = Convert.appendStringSize(line2, "退货金额:", 18, 10, 40);
    	line2 = Convert.appendStringSize(line2, thje, 28, 12, 40);
    	
    	int[] bs = new int[2];
    	double[] je = new double[2];
    	((Cczz_AccessDayDB)AccessDayDB.getDefault()).getBankLogSummary(bs, je);
    	String line = Convert.appendStringSize("", "=====================================================", 1, 40, 40);
    	String line3 = Convert.appendStringSize("", "消费笔数:", 1, 10, 40);
    	line3 = Convert.appendStringSize(line3, String.valueOf(bs[0]), 11, 3, 40);
    	line3 = Convert.appendStringSize(line3, "消费金额:", 18, 10, 40);
    	line3 = Convert.appendStringSize(line3, ManipulatePrecision.doubleToString(je[0]), 28, 12, 40);
    	
    	String line4 = Convert.appendStringSize("", "退货笔数:", 1, 10, 40);
    	line4 = Convert.appendStringSize(line4, String.valueOf(bs[1]), 11, 3, 40);
    	line4 = Convert.appendStringSize(line4, "退货金额:", 18, 10, 40);
    	line4 = Convert.appendStringSize(line4, ManipulatePrecision.doubleToString(je[1]), 28, 12, 40);
    	
    	new MessageBox(line1 +"\n"+line2+"\n"+line+"\n"+line3+"\n"+line4);
    	
    	
    }
    
    public boolean XYKCheckRetCode()
    {
        if (bld.retcode.trim().equals("00"))
        {
            bld.retbz = 'Y';

            return true;
        }
        else
        {
            bld.retbz = 'N';
            return false;
        }
    }

    public boolean XYKReadResult(String line)
    {
        System.out.println(line);
        if (line == null) return false;
        try
        {
            //
        	System.out.println(Convert.newSubString(line, 0, 91));
            bld.retcode  = Convert.newSubString(line, 0, 2);
            bld.bankinfo = Convert.newSubString(line, 2, 6) +
                           XYKReadBankName(Convert.newSubString(line, 2, 6));
            bld.cardno   = Convert.newSubString(line, 6, 26);

            if (Convert.newSubString(line, 30, 36).length() > 0)
            {
                //当使用重打印小票时,30到36会显示出EEEEEE
                try
                {
                    bld.trace = Long.parseLong(Convert.newSubString(line, 30, 36)
                                                      .trim());
                }
                catch (Exception er)
                {
                    er.printStackTrace();
                    bld.trace = -1;
                }
            }

            bld.retmsg = Convert.newSubString(line, 48, 88);
            System.out.println(bld.retmsg);
            //
            if (!bld.crc.equals(Convert.newSubString(line, 88, 91).trim()))
            {
                new MessageBox("交易校验码不匹配", null, false);

                return false;
            }

            errmsg = bld.retmsg;

            return true;
        }
        catch (Exception ex)
        {
            new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);
            ex.printStackTrace();

            return false;
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

                return "银联";
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

                if (a[0].trim().equals(bankid.trim()))
                {
                    return a[1].trim();
                }
            }

            rtf.close();
            
            return "银联";
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return "银联";
        }
    }

    public void XYKPrintDoc()
    {
        ProgressBox pb = null;

        try
        {
            if (!PathFile.fileExist("C:\\gmc\\receipt.txt"))
            {
                new MessageBox("找不到签购单打印文件!");

                return;
            }

            pb = new ProgressBox();
            pb.setText("正在打印银联签购单,请等待...");

            for (int i = 0; i < GlobalInfo.sysPara.bankprint; i++)
            {
                BufferedReader br = null;

                //
                Printer.getDefault().startPrint_Normal();

                try
                {
                    //
                    br = CommonMethod.readFileGBK("C:\\gmc\\receipt.txt");

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
                        
                        if (line.trim().toLowerCase().equals("cut"))
                        {
                            // 切纸
                            Printer.getDefault().cutPaper_Normal();
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
    
	public boolean getFuncLabel(int type,String[] grpLabelStr)
	{
		//0-4对应FORM中的5个输入框
		//null表示该不用输入
		switch(type)
		{
			case PaymentBank.XYKXF://消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = "请 刷 卡";
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKCX://消费撤销
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = "请 刷 卡";
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKTH://隔日退货   
				grpLabelStr[0] = null;
				grpLabelStr[1] = "原参考号";
				grpLabelStr[2] = "原交易日";
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
				grpLabelStr[3] = "请 刷 卡";
				grpLabelStr[4] = "余额查询";
				break;           				
			case PaymentBank.XYKCD://签购单重打
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打签单";
				break;
		}
		
		return true;
	}
}
