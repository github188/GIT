package bankpay.Bank;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;

public class YlswZs_PaymentBankFunc extends YlswWhf_PaymentBankFunc {

	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{
		// PaymentBank.XYKXF: 	// 消费
		// PaymentBank.XYKCX: 	// 消费撤销
		// PaymentBank.XYKTH:		// 隔日退货
		// PaymentBank.XYKYE: 	// 余额查询
		// PaymentBank.XYKCD: 	// 重打签购单
		// PaymentBank.XYKQD:     // 交易签到
		// PaymentBank.XYKJZ:     // 交易结帐
        
		try
		{
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && 
					(type != PaymentBank.XYKYE) && 
					(type != PaymentBank.XYKCD) && (type != PaymentBank.XYKQD) &&
					(type != PaymentBank.XYKJZ))
	            {
	                errmsg = "银联接口不支持该交易";
	                new MessageBox(errmsg);

	                return false;
	            }
			
			 // 先删除上次交易数据文件
            if (PathFile.fileExist("C:\\gmc\\request.txt"))
            {
                PathFile.deletePath("C:\\gmc\\request.txt");
                
                if (PathFile.fileExist("C:\\gmc\\request.txt"))
                {
            		errmsg = "交易请求文件request.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            if (PathFile.fileExist("C:\\gmc\\result.txt"))
            {
                PathFile.deletePath("C:\\gmc\\result.txt");
                
                if (PathFile.fileExist("C:\\gmc\\result.txt"))
                {
            		errmsg = "交易请求文件result.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            // 写入请求数据
            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
            {
                return false;
            }
            
            if (bld.retbz != 'Y')
            {
                // 调用接口模块
                if (PathFile.fileExist("C:\\gmc\\javaposbank.exe"))
                {
                	CommonMethod.waitForExec("C:\\gmc\\javaposbank.exe YLSWWHF");
                	
                	/*ProgressBox pb = null;//测试黄冈店银联卡崩的情况，用查找银联返回文件的方式替代等待
                	try
                	{
                		pb = new ProgressBox();
                        pb.setText("正在接收银联返回信息,请等待...");

                    	Runtime.getRuntime().exec("C:\\gmc\\javaposbank.exe YLSWWHF");
                        
                		int times = 1;
                		while(true)
                		{                		 
                			if (times >= 400)
                            {
                				writeBankFileLog("times=[" + times + "]时退出");
                				break;
                            }
                			if (PathFile.fileExist("C:\\gmc\\result.txt"))
                			{
                				writeBankFileLog("times=[" + times + "]，result.txt返回文件已找到时退出");
                				break;
                				
                			}
                			writeBankFileLog("times=[" + times + "]");
                			Thread.sleep(300);
                			times++;
                		}
                	}
                	catch(Exception ex)
                	{
                		ex.printStackTrace();
                		writeBankFileLog("循环查找result.txt返回文件时异常：" + ex.getMessage());
                	}
                	finally
                	{
                		if (pb != null)
                        {
                            pb.close();
                            pb = null;
                        }
                	}*/
                }
                else
                {
                    new MessageBox("找不到金卡工程模块 javaposbank.exe");
                    XYKSetError("XX","找不到金卡工程模块 javaposbank.exe");
                    return false;
                }
                
                // 读取应答数据
                if (!XYKReadResult())
                {
                    return false;
                }
                
                
                // 检查交易是否成功
                XYKCheckRetCode();
            }

            // 	打印签购单
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

    public void XYKPrintDoc()
    {
    	//wangyong add 2010.12.13
    	//对于十堰店签购单打印：从银联生成的打印文件里读取信息后，保存到指定规则的文件里，当小票成交之后再读取打印；
    	//其它店不变（消费成功后立即打印）
    	if (ConfigClass.CustomItem5.split("\\|").length >= 4 
				&& ConfigClass.CustomItem5.split("\\|")[3].trim().equalsIgnoreCase("A"))//Y启用新银联（消费后立即打印），N启用老银联接口，A启用新银联打印（消费后不立即打印，而是生成新的打印文件，当小票成交之后打印在小票后面） 
		{
    		
    		if ((bld.type == "00") || 
    				(bld.type == "01") || 
					(bld.type == "02"))
	            {
    				XYKPrintDoc_ShiYan();
    				return; 
	            }
    		
		}
    	
        ProgressBox pb = null;

        String printName = "C:\\gmc\\receipt.TXT";
        try
        {
            if (!PathFile.fileExist(printName))
            {
                new MessageBox("找不到签购单打印文件!");

                return;
            }
            
            pb = new ProgressBox();
            pb.setText("正在打印银联签购单,请等待...");

            for (int i = 0; i < GlobalInfo.sysPara.bankprint; i++)
            {
                BufferedReader br = null;

                XYKPrintDoc_Start();

                try
                {
                    //
                    br = CommonMethod.readFileGBK(printName);

                    if (br == null)
                    {
                        new MessageBox("打开签购单打印文件失败!");

                        return;
                    }

                    //
                    String line = null;

                    //中商客户化,在打印银联签购单之前,先打以下行
                    XYKPrintDoc_Print("机号:" + bld.syjh + "小票号:" + String.valueOf(bld.fphm) + "收银员号:" + bld.syyh + "\n");
                    
                    while ((line = br.readLine()) != null)
                    {
                        if (line.length() <= 0)
                        {
                            continue;
                        }

                        XYKPrintDoc_Print(line + "\n");
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
            
			if (PathFile.fileExist(printName))
            {
                PathFile.deletePath(printName);
            }
        }
    }

    public void XYKPrintDoc_ShiYan()
    {
    	
        String printName = "C:\\gmc\\receipt.TXT";        
        try
        {
            if (!PathFile.fileExist(printName))
            {
                new MessageBox("找不到签购单打印文件!");

                return;
            }
            
           
            BufferedReader br = null;
            
            try
            {
                //
                br = CommonMethod.readFileGBK(printName);

                if (br == null)
                {
                    new MessageBox("打开签购单打印文件失败!");

                    return;
                }
                
                //
                String buffer = "";
                String line = null;
   
                while ((line = br.readLine()) != null)
                {
                    if (line.length() <= 0)
                    {
                        continue;
                    }

                    if( buffer.length() <= 0 )
                    {
                    	//第一行
                    	buffer = line;
                    }
                    else
                    {
                    	buffer = buffer + "\n" + line;
                    }
                }
                
                String printNameTmp = WritePrintDoc(buffer);
            	if (printNameTmp  == null)
            	{
            		new MessageBox("生成银联打印文件失败：当前银联流水号=[" + Convert.increaseCharForward(String.valueOf(bld.trace),'0', 6) + "]");
            		return;
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
        }
        catch (Exception ex)
        {
            new MessageBox("生成打印签购单发生异常\n\n" + ex.getMessage());
            ex.printStackTrace();
        }
        finally
        {
                       
			if (PathFile.fileExist(printName))
            {
                PathFile.deletePath(printName);
            }
        }
    }
    
    private String WritePrintDoc(String strPrintBuffer)
	{
		try
		{
			String strDocDir = ConfigClass.LocalDBPath + "//Invoice";
			String strDocFile = strDocDir + "//Bankdoc_" + bld.syjh + "_" + bld.fphm + "_" + Convert.increaseCharForward(String.valueOf(bld.trace),'0', 6) + ".txt";
			
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


//	private void writeBankFileLog(String content)
//	{		
//		writeBankFileLog(ConfigClass.LocalDBPath + "\\Invoice\\" + new ManipulateDateTime().getDateByEmpty() + "\\BankLog_" + new ManipulateDateTime().getDateByEmpty() + ".log", content);
//	}
	
	//记录日志（追加）
//	private void writeBankFileLog(String fileName, String content)
//	{
//		FileWriter writer = null;		
//		try
//		{			
//			writer = new FileWriter(fileName, true);
//			writer.write("[" + ManipulateDateTime.getCurrentTime() + "] " + content + "\n"); 
//			writer.close(); 
//		}
//		catch(Exception ex)
//		{
//			ex.printStackTrace();
//		}
//		finally
//		{
//			try
//			{
//				if (writer != null)
//				{
//					writer.close();
//				}
//			}
//			catch(Exception ex)
//			{
//				ex.printStackTrace();
//			}
//			
//		}
//	}
	

}
