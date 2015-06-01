package com.efuture.javaPos.Payment.Bank;

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


public class Bjyd_PaymentBankFunc extends PaymentBankFunc
{
    public boolean XYKExecute(int type, double money, String track1,
                              String track2, String track3, String oldseqno,
                              String oldauthno, String olddate, Vector memo)
    {
        try
        {
            // 调用接口模块
            if (PathFile.fileExist("c:\\bmp\\bankmis.exe"))
            {
                if (PathFile.fileExist("C:\\bmp\\PRINT.TXT"))
                {
                    PathFile.deletePath("C:\\bmp\\PRINT.TXT");
                    
                    if (PathFile.fileExist("C:\\bmp\\PRINT.TXT"))
                    {
                		errmsg = "交易请求文件PRINT.txt无法删除,请重试";
                		XYKSetError("XX",errmsg);
                		new MessageBox(errmsg);
                		return false;   	
                    }
                }

                if (PathFile.fileExist("C:\\BMP\\PFACE.TXT"))
                {
                	PathFile.copyPath("C:\\BMP\\PFACE.TXT","C:\\BMP\\LastPFACE.TXT");
                    PathFile.deletePath("C:\\BMP\\PFACE.TXT");
                    
                    if (PathFile.fileExist("C:\\BMP\\PFACE.TXT"))
                    {
                		errmsg = "交易请求文件PFACE.TXT无法删除,请重试";
                		XYKSetError("XX",errmsg);
                		new MessageBox(errmsg);
                		return false;   	
                    }
                }
                
                if (PathFile.fileExist("c:\\bmp\\param.txt"))
                {
                	PathFile.copyPath("C:\\BMP\\PFACE.TXT","C:\\BMP\\Lastparam.TXT");
                    PathFile.deletePath("c:\\bmp\\param.txt");
                    
                    if (PathFile.fileExist("c:\\bmp\\param.txt"))
                    {
                		errmsg = "交易请求文件param.txt无法删除,请重试";
                		XYKSetError("XX",errmsg);
                		new MessageBox(errmsg);
                		return false;   	
                    }
                }

                String cmd;
                String typecode = "";
                String jestr = "";

                switch (type)
                {
                    case PaymentBank.XYKXF: //消费
                        typecode = "00";

                        break;

                    case PaymentBank.XYKCX: //消费撤销
                        typecode = "02";

                        break;

                    case PaymentBank.XYKTH: //隔日退货   
                        typecode = "20";

                        break;

                    case PaymentBank.XYKQD: //交易签到
                        return false;

                    case PaymentBank.XYKJZ: //交易结账
                        return false;

                    case PaymentBank.XYKYE: //余额查询    
                        typecode = "31";

                        break;

                    case PaymentBank.XYKCD: //签购单重打
                        typecode = "84";

                        break;

                    default:
                        return false;
                }

                String syjh = Convert.increaseChar(GlobalInfo.syjDef.syjh +
                                                   (char) 0x00, 7);
                
                bld.crc     = XYKGetCRC();
                String crc  = bld.crc +(char) 0x00;

                String gh;

                if (GlobalInfo.posLogin.gh.length() > 6)
                {
                    gh = Convert.increaseChar(GlobalInfo.posLogin.gh.substring(GlobalInfo.posLogin.gh.length() -
                                                                               6) +
                                              (char) 0x00, 7);
                }
                else
                {
                    gh = Convert.increaseChar(GlobalInfo.posLogin.gh +
                                              (char) 0x00, 7);
                }

                if ((track1 != null) && (track1.length() > 0))
                {
                    track1 = Convert.increaseChar(track1 + (char) 0x00, 78);
                }
                else
                {
                    track1 = Convert.increaseChar("" + (char) 0x00, 78);
                }

                if (track2 != null)
                {
                    track2 = Convert.increaseChar(track2 + (char) 0x00, 38);
                }
                else
                {
                    track2 = Convert.increaseChar("" + (char) 0x00, 38);
                }

                if (track3 != null)
                {
                    track3 = Convert.increaseChar(track3 + (char) 0x00, 105);
                }
                else
                {
                    track3 = Convert.increaseChar("" + (char) 0x00, 105);
                }

                jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,
                                                                                2,
                                                                                1));

                for (int i = jestr.length(); i < 12; i++)
                {
                    jestr = "0" + jestr;
                }

                jestr += (char) 0x00;

                String pzh = null;
                String ckh = null;

                if (typecode.equals("20"))
                {
                    pzh = Convert.increaseCharForward("" + (char) 0x00, 7);

                    if (oldseqno != null)
                    {
                        ckh = Convert.increaseChar(oldseqno + (char) 0x00, 13);
                    }
                    else
                    {
                        ckh = Convert.increaseChar("" + (char) 0x00, 13);
                    }
                }
                else
                {
                    ckh = Convert.increaseChar("" + (char) 0x00, 13);

                    if (oldseqno != null)
                    {
                        pzh = Convert.increaseChar(oldseqno + (char) 0x00, 7);
                    }
                    else
                    {
                        pzh = Convert.increaseChar("" + (char) 0x00, 7);
                    }
                }

                typecode += (char) 0x00;

                String pch = Convert.increaseChar(oldauthno + (char) 0x00, 7);

                if (olddate != null)
                {
                    olddate = Convert.increaseChar(olddate + (char) 0x00, 9);
                }
                else
                {
                    olddate = Convert.increaseChar("" + (char) 0x00, 9);
                }

                cmd = syjh + gh + typecode + jestr + track1 + track2 + track3 +
                      pzh + pch + olddate + ckh + crc;

                PrintWriter pw = null;
                
                try{
	                pw = CommonMethod.writeFile("c:\\bmp\\param.txt");
	
	                if (pw != null)
	                {
	                    pw.print(cmd);
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

                CommonMethod.waitForExec("c:\\bmp\\bankmis.exe");
            }
            else
            {
                new MessageBox("找不到金卡工程模块 bankmis.exe");
                XYKSetError("XX","找不到金卡工程模块 bankmis.exe");
                return false;
            }

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
        	XYKSetError("XX","金卡异常XX:"+ex.getMessage());
            new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);

            return false;
        }
    }

    public boolean XYKReadResult()
    {
    	BufferedReader br = null;

        try
        {
            if (!PathFile.fileExist("C:\\BMP\\PFACE.TXT") ||
                    ((br = CommonMethod.readFileGBK("C:\\BMP\\PFACE.TXT")) == null))
            {
            	XYKSetError("XX","读取金卡工程应答数据失败!");
                new MessageBox("读取金卡工程应答数据失败!", null, false);
                
                return false;
            }

            // 读取请求数据
            String line = br.readLine();
            
            //
            bld.retcode = Convert.newSubString(line, 0, 2);

            bld.retmsg   = Convert.newSubString(line, 2, 42).trim();
            bld.bankinfo = Convert.newSubString(line, 42, 44) +
                           XYKReadBankName(Convert.newSubString(line, 42, 44));
            bld.cardno   = Convert.newSubString(line, 50, 70).trim();

            //String je = Convert.newSubString(line, 70, 82);
            if (Convert.newSubString(line, 82, 88).length() > 0)
            {
                bld.trace = Long.parseLong(Convert.newSubString(line, 82, 88)
                                                  .trim());
            }

            //double je1 = ManipulatePrecision.mul(Integer.parseInt(je),0.01);
            
            //if (je1 == bld.je)
            //{
            	//new MessageBox("返回金额("+je1+")和输入金额不符("+bld.je+"),请");
            //}
            
            errmsg = bld.retmsg;
            String CRC = Convert.newSubString(line,88,91);
            if (!CRC.equals(bld.crc))
            {
            	errmsg = "返回效验码"+CRC+"同原始效验码"+bld.crc+"不一致";
            	XYKSetError("XX",errmsg);
            	new MessageBox(errmsg);
            	try{
	            	//保存返回文件
	            	ManipulateDateTime mdt  = new ManipulateDateTime();
	            	String date = GlobalInfo.balanceDate.replaceAll("/", "");
	                String name =ConfigClass.LocalDBPath + "Invoice//" + date+"//"+mdt.getTime().replaceAll(":", "_")+"_Bank.txt";
	            	PathFile.copyPath("C:\\BMP\\PFACE.TXT",name);
            	}catch(Exception er)
            	{
            		er.printStackTrace();
            	}
            	return false;
            }
            
            
            
            return true;
        }
        catch (Exception ex)
        {
            new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);    
            XYKSetError("XX","读取应答XX:"+ex.getMessage());
            ex.printStackTrace();
            return false;
        }
        finally
        {
        	if (br != null)
        	{
				try {
					br.close();
				} catch (IOException e) {
					// TODO 自动生成 catch 块
					new MessageBox("PFACE.TXT 关闭失败\n重试后如果仍难失败，请联系信息部");
					e.printStackTrace();
				}
        	}
        }
    }

    public void XYKPrintDoc()
    {
        ProgressBox pb = null;

        try
        {
            if (!PathFile.fileExist("C:\\BMP\\PRINT.TXT"))
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
                Printer.getDefault().startPrint_Journal();

                try
                {
                    //由于发现在windows环境下,用GBK读取文件会产生BUG,改为GB2310
                    br = CommonMethod.readFileGB2312("C:\\BMP\\PRINT.TXT");

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

                        Printer.getDefault().printLine_Journal(line + "\n");
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
                Printer.getDefault().cutPaper_Journal();
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
}
