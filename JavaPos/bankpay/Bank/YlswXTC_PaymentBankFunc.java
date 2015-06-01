package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Payment.PaymentBank;
//杭州西田城 javapos打印银联签购单
public class YlswXTC_PaymentBankFunc extends YlswTCRC_PaymentBankFunc
{
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
			
            if (PathFile.fileExist(bankpath + "\\print.txt"))
            {
            	PathFile.deletePath(bankpath + "\\print.txt");
                if (PathFile.fileExist("print.txt"))
                {
            		errmsg = "交易print.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            
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
            	if(type == PaymentBank.XYKCD) {
            		bld.retcode = "0";
            		bld.retmsg  = "交易成功";
                	errmsg = "交易成功";
                	return true;
            	}
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

}
