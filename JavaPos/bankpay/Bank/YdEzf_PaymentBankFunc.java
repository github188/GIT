package bankpay.Bank;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;


public class YdEzf_PaymentBankFunc extends PaymentBankFunc
{
	private String dirPath = "";
	private String dirPathReq = "";
	private String reqfileName = "";
	private String reqfileNameFull = "";
	private String resfileName = "";
	private String resfileNameFull = "";
	private String exetimeStr = "";
	private int exeindex = 1;
	private String exefilaName = "";
	private String exefilaNameFull = "";
	
	private SaleBS saleBS = null;
	
	public String[] getFuncItem()
    {
        String[] func = new String[1];

        func[0] = "[" + PaymentBank.XYKXF + "]" + "电子券消费";
		//func[1] = "[" + PaymentBank.XKQT4 + "]" + "充值";
		//func[2] = "[" + PaymentBank.XKQT1 + "]" + "查询上一笔交易";
		//func[3] = "[" + PaymentBank.XKQT2 + "]" + "查询当天交易";

        return func;
    }
	
	public boolean getFuncLabel(int type, String[] grpLabelStr)
    {
		switch (type)
        {
        	case PaymentBank.XYKXF: //	消费
        		grpLabelStr[0] = null;
        		grpLabelStr[1] = null;
        		grpLabelStr[2] = null;
        		grpLabelStr[3] = null;
        		grpLabelStr[4] = "交易金额";
        	break;
        	case PaymentBank.XKQT4: //充值
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "充值金额";
            break;
        	case PaymentBank.XKQT1://查询上一笔交易 
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "查询交易";
			break;
        	case PaymentBank.XKQT2://查询当天交易 
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "查询交易";
			break;
        }
		
		return true;
    }
	
	public boolean getFuncText(int type, String[] grpTextStr)
    {
		switch (type)
		{
		 	case PaymentBank.XYKXF: 	// 消费
		        grpTextStr[0] = null;
		        grpTextStr[1] = null;
		        grpTextStr[2] = null;
		        grpTextStr[3] = null;
		        grpTextStr[4] = null;
		    break;
		 	case PaymentBank.XKQT4: 	// 消费撤销
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = null;
            break;
		 	case PaymentBank.XKQT1: 	//查询当天上一笔交易 
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键查询";
		 	case PaymentBank.XKQT2: 	//查询当天上一笔交易 
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键查询";
            break;
		}
		
		return true;
    }
	
	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKYE) && (type != PaymentBank.XKQT4)) 
	            {
	                errmsg = "银联接口不支持该交易";
	                new MessageBox(errmsg);

	                return false;
	            }
			
			// 生成文件名
			dirPath = "c:\\javapos\\";
			dirPathReq = "c:\\javapos\\";
			//dirPath = "D:\\Work\\Work Floder\\Source Code\\Eclipse WorkSpace\\JavaPos\\";
			ManipulateDateTime mdt = new ManipulateDateTime();
			exetimeStr = mdt.getDateByEmpty()+mdt.getTimeByEmpty();
			String strexeindex = String.valueOf(exeindex);
			if (strexeindex.length() == 1) strexeindex = "00" + strexeindex;
			else if (strexeindex.length() == 2) strexeindex = "0" + strexeindex;
			reqfileName = "REQ_100_" + exetimeStr + "_" + strexeindex + ".txt";
			reqfileNameFull = dirPathReq + reqfileName;
			resfileName = "RES_100_" + exetimeStr + "_" + strexeindex + ".txt";
			resfileNameFull = dirPathReq + resfileName;
			exefilaName = "emobile.exe";
			exefilaNameFull = dirPath + exefilaName;
			
			 // 先删除上次交易数据文件
			/*
			File file = new File(dirPath);
	        if (file.isDirectory())
	        {
	        	File[] fileList = file.listFiles();
		        if (fileList != null)
		        {
		            for (int i = 0; i < fileList.length; i++)
		            {
		                if (fileList[i].isFile())
		                {
		                	if (fileList[i].getName().startsWith("REQ_") && fileList[i].getName().endsWith(".txt"))
		                	{
		                		fileList[i].delete();
		                	}
		                }
		            }
		        }
	        }
	        */
	        File file = new File(dirPathReq);
	        if (file.isDirectory())
	        {
	        	File[] fileList = file.listFiles();
		        if (fileList != null)
		        {
		            for (int i = 0; i < fileList.length; i++)
		            {
		                if (fileList[i].isFile())
		                {
		                	if (fileList[i].getName().startsWith("REQ_") && fileList[i].getName().endsWith(".txt"))
		                	{
		                		fileList[i].delete();
		                	}
		                }
		            }
		        }
	        }
            
            //  写入请求数据
            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
            {
                return false;
            }
            
            if (bld.retbz != 'Y')
            {
            	
                // 调用接口模块
                if (PathFile.fileExist(exefilaNameFull))
                {
                	//CommonMethod.waitForExec(exefilaNameFull, exefilaName);
                	//new MessageBox("开始调用!");
                	//CommonMethod.waitForExec(exefilaNameFull, true);
                	CommonMethod.waitForExec(exefilaName);
                	//new MessageBox("结束调用!");
                }
                else
                {
                    new MessageBox("找不到移动E站模块 " + exefilaName);
                    XYKSetError("XX","找不到移动E站模块 " + exefilaName);
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
                XYKPrintDoc(type);
            }
            
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			XYKSetError("XX","金卡异常XX:"+ex.getMessage());
            new MessageBox("调用移动E站处理模块异常!\n\n" + ex.getMessage(), null, false);
			return false;
		}
	}
	
	public boolean XYKNeedPrintDoc()
	{
       if (!checkBankSucceed())
       {
           return false;
       }
       
       return true;
	 }
	
	public boolean XYKCheckRetCode()
	{
       if (bld.retcode.trim().equals("0"))
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
	
	public boolean checkBankSucceed()
	{
       if (bld.retbz == 'N')
       {
           errmsg = bld.retmsg;

           return false;
       }
       else
       {
           errmsg = "交易成功";

           return true;
       }
	}
	
	public boolean XYKWriteRequest(int type, double money, String track1,String track2, String track3,String oldseqno, String oldauthno,String olddate, Vector memo)
	{
		try
		{
			 //String mobile = "PHONE_NO=" + oldseqno;
			 String mobile = "";
			 
			 String line = "";

	         if (memo.size() >=2 ) saleBS = (SaleBS)memo.elementAt(2);
	         
	         //	 根据不同的类型生成文本结构
	         switch (type)
	         {
	        	 case PaymentBank.XYKXF:
	        		 if (saleBS != null)
	        			 line = "BUSI_TYPE=9600 " + mobile + "POS_ID=" + saleBS.saleHead.syjh + " PAY_FEE=" + String.valueOf((int)(ManipulatePrecision.doubleConvert(money,2,1)*100)) + " BUSI_SEQ=" + saleBS.saleHead.fphm + " PROXY_OPER="+saleBS.saleHead.syyh;
	        		 else
	        			 line = "BUSI_TYPE=9600 " + mobile + "POS_ID=" + saleBS.saleHead.syjh + " PAY_FEE=" + String.valueOf((int)(ManipulatePrecision.doubleConvert(money,2,1)*100)) + " BUSI_SEQ=() PROXY_OPER="+saleBS.saleHead.syyh;
	        	 break;
	        	 case PaymentBank.XKQT4:
	        		 if (saleBS != null)
	        			 line = "BUSI_TYPE=9605 POS_ID=" + saleBS.saleHead.syjh + " PAY_FEE=" + String.valueOf(ManipulatePrecision.doubleConvert(money,2,1)) + " BUSI_SEQ=" + saleBS.saleHead.fphm + " PROXY_OPER="+saleBS.saleHead.syyh;
	        		 else
	        			 line = "BUSI_TYPE=9605 POS_ID=" + saleBS.saleHead.syjh + " PAY_FEE=" + String.valueOf(ManipulatePrecision.doubleConvert(money,2,1)) + " BUSI_SEQ=() PROXY_OPER="+saleBS.saleHead.syyh;
	        	 break;	 
	        	 case PaymentBank.XKQT1:
	        		 line = "BUSI_TYPE=9603 QUERY_TYPE=LASTBILL POS_ID=" + saleBS.saleHead.syjh + "PROXY_OPER="+saleBS.saleHead.syyh;
	        	 break;
	        	 case PaymentBank.XKQT2:
	        		 line = "BUSI_TYPE=9603 QUERY_TYPE=TODAY POS_ID=" + saleBS.saleHead.syjh + "PROXY_OPER="+saleBS.saleHead.syyh;
		        	 break;

	         }
	         
	         PrintWriter pw = null;
	            
	         try
	         {
	            pw = CommonMethod.writeFile(reqfileNameFull);
	            if (pw != null)
	            {
	                pw.print(line);
	                pw.flush();
	                pw.close();
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
			new MessageBox("写入移动E站请求数据异常!\n\n" + ex.getMessage(), null, false);
	        ex.printStackTrace();
	         
	        return false;
		}
	}
	
	public boolean XYKReadResult()
	{
       BufferedReader br = null;
       
       bld.retcode = "XX";
       
       try
       {
    	   if (!PathFile.fileExist(resfileNameFull) || ((br = CommonMethod.readFileGBK(resfileNameFull)) == null))
           {
           		XYKSetError("XX","读取移动E站应答数据失败!");
           		new MessageBox("读取移动E站应答数据失败!", null, false);

           		return false;
           }
    	   
    	   String line = br.readLine();

           if (line.length() <= 0)
           {
               return false;
           }
           
           String result[] = line.split(" ");
           
           if (result == null) return false;
           
           if (bld.type.equals(String.valueOf(PaymentBank.XYKXF))) 
           {
   				for (int i = 0;i < result.length;i++)
   				{
   					String[] str = result[i].split("=");
   					if (str.length >= 2)
   					{
   						if (str[0].trim().equals("BUSI_TYPE")) 
   						{}
   						else if (str[0].trim().equals("BUSI_SEQ"))
   						{
   							if (str[1].length() > 0)
   							{
   								bld.authno = str[1].trim();
   							}
   						}
   						else if (str[0].trim().equals("PHONE_NO"))
   						{
   							if (str[1].length() > 0)
   							{
   								bld.cardno = str[1].trim();
   							}
   						}
   						else if (str[0].trim().equals("PAY_FEE"))
   						{
   							if (str[1].length() > 0)
   							{
   								bld.je = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Double.parseDouble(str[1].trim()),100),2,1);
   								//bld.je = ManipulatePr Double.parseDouble(str[1].trim())/100;
   							}
   						}
   						else if (str[0].trim().equals("POS_ID"))
   						{
   							if (str[1].length() > 0)
   							{
   								bld.syjh = str[1].trim();
   							}
   						}
   						else if (str[0].trim().equals("PROXY_OPER"))
   						{
   							if (str[1].length() > 0)
   							{
   								bld.syyh = str[1].trim();
   							}
   						}
   						else if (str[0].trim().equals("PAYFEE_DATE"))
   						{
   							if (str[1].length() > 0)
   							{
   								bld.cardno = str[1].trim();
   							}
   						}
   						else if (str[0].trim().equals("RETC"))
   						{
   							if (str[1].length() > 0)
   							{
   								bld.retcode = str[1].trim();
   							}
   						}
   						else if (str[0].trim().equals("DESC"))
   						{
   							if (str[1].length() > 0)
   							{
   								bld.retmsg = str[1].trim();
   							}
   						}
   					}
   				}
   		   }
           else if (bld.type.equals(String.valueOf(PaymentBank.XKQT1))) 
           {
        	   
           }
           else if (bld.type.equals(String.valueOf(PaymentBank.XKQT2))) 
           {
        	   
           }
           else if (bld.type.equals(String.valueOf(PaymentBank.XKQT4))) 
           {
        	   
           }
           
    	   return true;
       }
       catch (Exception ex)
       {
    	   XYKSetError("XX","读取应答XX:"+ex.getMessage());
           new MessageBox("读取移动E站应答数据异常!" + ex.getMessage(), null, false);
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
                   
                   if (PathFile.fileExist(reqfileNameFull))
		            {
		                PathFile.deletePath(reqfileNameFull);
		            }
					
					if (PathFile.fileExist(resfileNameFull))
		            {
		                PathFile.deletePath(resfileNameFull);
		            }
               }
               catch (IOException e)
               {
                   e.printStackTrace();
               }
           }
       }
	}
	
	public void XYKPrintDoc(int type)
	{
		String type1 = "";
		String line1 = "";

		if (type == PaymentBank.XYKXF)
		{
			type1 = "电子券消费";
			line1 = type1 + "\n";
			line1 = line1 + "交易时间:" + bld.rqsj + "\n";
			line1 = line1 + "流水号码:" + bld.authno + "\n";
			line1 = line1 + "手机号码:" + bld.cardno + "\n";
			line1 = line1 + "消费金额:" + String.valueOf(bld.je) + "\n";
			line1 = "\n\n\n";
			type1 = "电子券消费";
			line1 = type1 + "\n";
			line1 = line1 + "交易时间:" + bld.rqsj + "\n";
			line1 = line1 + "流水号码:" + bld.authno + "\n";
			line1 = line1 + "手机号码:" + bld.cardno + "\n";
			line1 = line1 + "消费金额:" + String.valueOf(bld.je) + "\n";
		}
		else if (type == PaymentBank.XKQT4)
		{
			type1 = "充值";
			line1 = type1 + "\n";
			line1 = line1 + "交易时间:" + bld.rqsj + "\n";
			line1 = line1 + "流水号码:" + bld.authno + "\n";
			line1 = line1 + "手机号码:" + bld.cardno + "\n";
			line1 = line1 + "充值金额:" + String.valueOf(bld.je) + "\n";
			line1 = "\n\n\n";
			type1 = "充值";
			line1 = type1 + "\n";
			line1 = line1 + "交易时间:" + bld.rqsj + "\n";
			line1 = line1 + "流水号码:" + bld.authno + "\n";
			line1 = line1 + "手机号码:" + bld.cardno + "\n";
			line1 = line1 + "充值金额:" + String.valueOf(bld.je) + "\n";
		}
		else if (type == PaymentBank.XKQT1)
		{
			return;
		}
		else if (type == PaymentBank.XKQT1)
		{
			return;
		}
		else
		{
			return;
		}
		
		for (int i = 0 ; i < 1;i++)
		{
			Printer.getDefault().printLine_Normal(line1);
			Printer.getDefault().cutPaper_Normal();
		}
	}
}
