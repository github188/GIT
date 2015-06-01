package com.efuture.javaPos.Payment.Bank;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Vector;

import org.jawin.COMException;
import org.jawin.FuncPtr;
import org.jawin.ReturnFlags;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;


public class Ylswdll_PaymentBankFunc extends PaymentBankFunc
{

    public boolean XYKWriteRequest(int type, double money, String track1,
                                      String track2, String track3,
                                      String oldseqno, String oldauthno,
                                      String olddate, Vector memo)
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

                if (PathFile.fileExist("C:\\gmc\\answer.TXT"))
                {
                    PathFile.deletePath("C:\\gmc\\answer.TXT");
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

                    default:
                        return false;
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

                cmd = syjh + gh + typecode + jestr + olddate +oldauthno+ seq + track2 +
                      track3 + crc;

                PrintWriter pw = CommonMethod.writeFile("c:\\gmc\\request.txt");

                if (pw != null)
                {
                    pw.println(cmd);
                    pw.flush();
                    pw.close();
                }

                return true;
            }
            else
            {
                new MessageBox("找不到金卡工程模块 posinf.DLL");

                return false;
            }
        }
        catch (Exception ex)
        {
            new MessageBox("写入金卡工程请求数据异常!\n\n" + ex.getMessage(), null, false);
            ex.printStackTrace();

            return false;
        }
    }

    public boolean XYKExecute(int type, double money, String track1,
                              String track2, String track3, String oldseqno,
                              String oldauthno, String olddate, Vector memo)
    {
        try
        {
            //        	 写入请求数据
            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno,
                                     oldauthno, olddate, memo))
            {
                return false;
            }

            FuncPtr msgBox = null;

            try
            {
                msgBox = new FuncPtr("C:\\gmc\\posinf.DLL", "bank");
                msgBox.invoke_I("bank", ReturnFlags.CHECK_NONE);
            }
            catch (COMException e)
            {
                // handle exception
                e.printStackTrace();
                new MessageBox(e.getMessage());
            }
            finally
            {
                if (msgBox != null)
                {
                    msgBox.close();
                    msgBox = null;
                }
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
            ex.printStackTrace();
            new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);

            return false;
        }
    }

    public boolean XYKReadResult()
    {
        BufferedReader br = null;

        try
        {
            if (!PathFile.fileExist("C:\\gmc\\answer.txt") ||
                    ((br = CommonMethod.readFileGBK("C:\\gmc\\answer.txt")) == null))
            {
                new MessageBox("读取金卡工程应答数据失败!", null, false);

                return false;
            }

            // 读取请求数据
            String line = br.readLine();

            br.close();

            //
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

            bld.retmsg = Convert.newSubString(line, 48, 88).trim();

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

    public void XYKPrintDoc()
    {
        ProgressBox pb = null;

        try
        {
            if (!PathFile.fileExist("C:\\gmc\\receipt.TXT"))
            {
                new MessageBox("找不到签购单打印文件!");

                return;
            }

            pb = new ProgressBox();
            pb.setText("正在打印银联签购单,请等待...");

            for (int i = 0; i < 2; i++)
            {
                BufferedReader br = null;

                //
                Printer.getDefault().startPrint_Normal();

                try
                {
                    //
                    br = CommonMethod.readFileGBK("C:\\gmc\\receipt.TXT");

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
}
