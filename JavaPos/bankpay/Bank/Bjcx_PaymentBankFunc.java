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
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

////北京城乡(北京工行接口，在款机键盘上刷卡)
public class Bjcx_PaymentBankFunc extends PaymentBankFunc
{
	public String[] getFuncItem()
    {
        String[] func = new String[8];

        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
        func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
        func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
        func[3] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
        func[4] = "[" + PaymentBank.XYKJZ + "]" + "银联结算";
        func[5] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
        func[6] = "[" + PaymentBank.XYKCD + "]" + "重打签购单";
        func[7] = "[" + PaymentBank.XKQT1 + "]" + "按流水号打印";;

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
        	case PaymentBank.XYKTH://隔日退货   
				grpLabelStr[0] = "原检索号";
				grpLabelStr[1] = "原终端号";
				grpLabelStr[2] = "原交易日";
				grpLabelStr[3] = "请 刷 卡";
				grpLabelStr[4] = "交易金额";
			break;
        	case PaymentBank.XYKQD: //交易签到
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易签到";
            break;
        	case PaymentBank.XYKJZ: //银联结账
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "银联结账";
            break;
        	case PaymentBank.XYKYE: //余额查询    
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = "请 刷 卡";
                grpLabelStr[4] = "余额查询";
            break;
        	case PaymentBank.XYKCD: //签购单重打
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "重打上笔签购单";
            break;
        	case PaymentBank.XKQT1:		// 按流水号打印
        		grpLabelStr[0] = "原流水号";
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "按凭证号打印";
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
		 	case PaymentBank.XYKCX: 	// 消费撤销
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = null;
            break;
		 	case PaymentBank.XYKTH:		//隔日退货   
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
			break;
		 	case PaymentBank.XYKQD: 	//交易签到
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始交易签到";
            break;
		 	case PaymentBank.XYKJZ: 	//银联结账
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始银联结账";
            break;
		 	case PaymentBank.XYKYE: 	//余额查询    
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始余额查询";
            break;
		 	case PaymentBank.XYKCD: 	//签购单重打
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键签购单重打";
            break;
		 	case PaymentBank.XKQT1:		// 按凭流水号打印
		 		grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键凭流水号打印";
		 	break;		
		}
		
		return true;
    }
	
	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && 
				(type != PaymentBank.XYKTH) && (type != PaymentBank.XYKQD) && 
				(type != PaymentBank.XYKJZ) && (type != PaymentBank.XYKYE) && 
				(type != PaymentBank.XYKCD) && (type != PaymentBank.XKQT1))
            {
                errmsg = "银联接口不支持该交易";
                new MessageBox(errmsg);

                return false;
            }
			
			 // 先删除上次交易数据文件
            if (PathFile.fileExist("xyktr.in"))
            {
                PathFile.deletePath("xyktr.in");
                
                if (PathFile.fileExist("xyktr.in"))
                {
            		errmsg = "交易请求文件xyktr.in无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            if (PathFile.fileExist("xyktr.out"))
            {
                PathFile.deletePath("xyktr.out");
                
                if (PathFile.fileExist("xyktr.out"))
                {
            		errmsg = "交易请求文件xyktr.out无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            if (PathFile.fileExist("xykpr.tmp"))
            {
                PathFile.deletePath("xykpr.tmp");
                
                if (PathFile.fileExist("xykpr.tmp"))
                {
            		errmsg = "交易请求文件xykpr.tmp无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            //  写入请求数据
            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
            {
                return false;
            }
            
            if (bld.retbz != 'Y')
            {
            	
                // 调用接口模块 c:\javapos 目录
                if (PathFile.fileExist("posxyk.exe"))
                {
                	CommonMethod.waitForExec("posxyk.exe");
                }
                else
                {
                    new MessageBox("找不到金卡工程模块 posxyk.exe");
                    XYKSetError("XX","找不到金卡工程模块 posxyk.exe");
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
			 String line = "";
			 
			 //金额12
			 if (type == PaymentBank.XYKTH)
	         {
				 money = 0 - money;
	         }
	         String jestr = ManipulatePrecision.doubleToString(money,2,1);	         
	         jestr = Convert.appendStringSize("", jestr, 0, 12, 12, 1);
	        	 	 
	         //轨道信息	         
	         if (type == PaymentBank.XYKXF || 
	        		 type == PaymentBank.XYKCX ||
	        		 type == PaymentBank.XYKTH ||
	        		 type == PaymentBank.XYKYE
	         )
	         {
	        	 if (track2 == null)
	        	 {
	        		 track2 = "";
	        	 }
	        	 if (track3 == null)
	        	 {
	        		 track3 = "";
	        	 }
	        	 track2 = Convert.appendStringSize("", track2, 0, 37, 37);
	        	 track3 = Convert.appendStringSize("", track3, 0, 104, 104);
	         }
	        	 
	        
	         
	         //	根据不同的类型生成文本结构
	         switch (type)
	         {
	         	case PaymentBank.XYKXF://消费：调用的主功能号4右对齐 + 调用的副功能号4右对齐 + 金额12.2右对齐 + 二磁道37左对齐 + 三磁道104左对齐	         		 
	         		 line = "0001" + " " + "0000" + " " + jestr + " " + "          " + track2 + " " + track3;
	         		 
	         	break;	         	
	         	case PaymentBank.XYKCX://当日取消：调用的主功能号4右对齐 + 调用的副功能号4右对齐 + 金额12.2右对齐 + 原交易流水号6右对齐 + 二磁道37左对齐 + 三磁道104左对齐	         		
	         		line = "0001" + " " + "0001" + " " + jestr + " " + "   " + Convert.appendStringSize("", oldseqno, 0, 6, 6, 1) + " " + "       " + track2 + " " + track3;
	         		
	         	break;	
	         	case PaymentBank.XYKTH://隔日退货：调用的主功能号4右对齐 + 调用的副功能号4右对齐 + 金额12.2右对齐 + 原交易检索号8右对齐 + 原交易日期8右对齐 + 原交易终端号8右对齐 + 二磁道37左对齐 + 三磁道104左对齐	         		
	         		line = "0001" + " " + "0002" + " " + jestr + " " + "   " + Convert.appendStringSize("", oldseqno, 0, 8, 8, 1) + " "
	         		+ Convert.appendStringSize("", olddate, 0, 8, 8, 1) + " " + Convert.appendStringSize("", oldauthno, 0, 8, 8, 1) + " " + "       "
	         		+ track2 + " " + track3;
	         		
	         	break;	
	         	case PaymentBank.XYKQD:	//签到：调用的主功能号4右对齐 + 调用的副功能号4右对齐
	         		line = "0002" + " " + "0051";	
	         		
	         	break;	
	         	case PaymentBank.XYKJZ://结算：调用的主功能号4右对齐 + 调用的副功能号4右对齐
	         		line = "0002" + " " + "0058";	
	         		
	         	break;	
	         	case PaymentBank.XYKYE://查余：调用的主功能号4右对齐 + 调用的副功能号4右对齐 + 二磁道37左对齐 + 三磁道104左对齐
	         		line = "0002" + " " + "0021" + " " + "   " + track2 + " " + track3;	
	         		
	         	break;	
	         	case PaymentBank.XYKCD://重打最后一笔：调用的主功能号4右对齐 + 调用的副功能号4右对齐	         		
	         		line = "0002" + " " + "0011";	
	         		
	         	break;	
	        	case PaymentBank.XKQT1://重打指定一笔：调用的主功能号4右对齐 + 调用的副功能号4右对齐 + 交易流水号6右对齐   		
	        		line = "0002" + " " + "0012" + " " + "   " + Convert.appendStringSize("", oldseqno, 0, 6, 6, 1);	
	        		
		        break;	
	         	
	         }
	         
	         PrintWriter pw = null;
	            
	         try
	         {
	            pw = CommonMethod.writeFile("xyktr.in");
	            if (pw != null)
	            {
	                pw.println(line);
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
	 
	 public boolean XYKReadResult()
	 {
        BufferedReader br = null;
        
        try
        {
        	if (!PathFile.fileExist("xyktr.out") || ((br = CommonMethod.readFileGBK("xyktr.out")) == null))
            {
            	XYKSetError("XX","读取金卡工程应答数据失败!");
                new MessageBox("读取金卡工程应答数据失败!", null, false);

                return false;
            }
        	String line="";        	 
        	String strTmp="";
        	int i =0;
        	while ((line = br.readLine()) != null)
			{
        		if(i==0)
        		{
        			strTmp=line;
        		}
        		else
        		{
        			strTmp = strTmp + "," + line;
        		}
        		i++;
        		
			}
        	String[] result=strTmp.split(",");
        	if (result == null) return false;
        	
        	String type="";
        	for(int j =0; j< result.length; j++)
        	{
        		line = result[j];
        		if (line.length() < 4)
        		{
        			continue;
        		}
        		type = Convert.newSubString(line, 0, 4).trim();
        		line = line + "         ";
        		if (j==1)
        		{
        			bld.retcode = type;
        			bld.retmsg = Convert.newSubString(line, 4).trim();
        			continue;
        		}
        			 
        		if (type.equals("0001"))//功能号
        		{
        			
        		}
        		else if (type.equals("0002"))//卡号
        		{
        			bld.cardno = Convert.newSubString(line, 4, line.length()).trim();
        		}
        		else if (type.equals("0003"))//流水号
        		{
        			bld.trace = Long.parseLong(Convert.newSubString(line, 4, line.length()).trim());
        		}
        		else if (type.equals("0005"))//有效期
        		{
        			bld.bankinfo = Convert.newSubString(line, 4, line.length()).trim();
        		}
        		else if (type.equals("0099"))//版本号信息
        		{
        			
        		}
        		else if (type.equals("0007"))//卡类别
        		{
        			
        		}
        		else
        		{
        			
        		}
        	}
        	
            /*if (line.length() <= 0)
            {
                return false;
            }
            
            line = line.replaceAll(",,",", ,");
            
            String result[] = line.split(",");
            
            if (result == null) return false;
                        
            bld.retcode 	= result[0];
            bld.retmsg 		= result[1];
            bld.bankinfo	= result[6];
            bld.cardno		= result[8];
            
            if (result[12] != null && !result[12].trim().equals(""))
            {
            	bld.trace		= Long.parseLong(result[12].trim());
            }*/
        		
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
                    
                    /*if (PathFile.fileExist("xyktr.in"))
		            {
		                PathFile.deletePath("xyktr.in");
		            }
					
					if (PathFile.fileExist("xyktr.out"))
		            {
		                PathFile.deletePath("xyktr.out");
		            }*/
                }
                catch (IOException e)
                {
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
			 String printName = "";
			 
			 int type = Integer.parseInt(bld.type.trim());
			 
			 if ((type == PaymentBank.XYKXF || type == PaymentBank.XYKCX  || type == PaymentBank.XYKTH ||
					 type == PaymentBank.XYKCD || type == PaymentBank.XKQT1 || type == PaymentBank.XYKJZ
			 	))
			 {
				 if (!PathFile.fileExist("xykpr.tmp"))
	                {
	                    new MessageBox("找不到签购单打印文件!");

	                    return;
	                }
	                else
	                {
	                    printName = "xykpr.tmp";
	                }
			 }
			 else
			 {
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
                        if (line.trim().equals("|"))
                        {
                            break;
                        }
                       
                        XYKPrintDoc_Print(line);
	            	 }

	             }
	             catch (Exception ex)
	             {
	            	 new MessageBox(ex.getMessage());
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
		 }
	 }
	 
	 public void XYKPrintDoc_Start()
		{
			if (onceprint)
			{
				//城乡要在平推栈打印银行签购单
				String port = ConfigClass.BankConfig.trim();
				if (port == null || port.length() <= 0) Printer.getDefault().startPrint_Slip();
				else if (port.trim().equals("1"))
				{
					Printer.getDefault().startPrint_Normal();
				}
				else if (port.trim().equals("2"))
				{
					Printer.getDefault().startPrint_Journal();
				}
				else if (port.trim().equals("3"))
				{
					Printer.getDefault().startPrint_Slip();
				}			
				
			}
			else
			{
				
				// 此地改为增加模式，防止在多个金卡工程同时存在时，可能序号相同
				printdoc = CommonMethod.writeFileAppend("bankdoc_" + String.valueOf(bld.trace) + ".txt");	
			}
		}

	 public void XYKPrintDoc_Print(String printStr)
		{
			if (onceprint)
			{
				String port = ConfigClass.BankConfig.trim();
				if (port == null || port.length() <= 0) Printer.getDefault().printLine_Slip(printStr);
				else if (port.trim().equals("1"))
				{
					Printer.getDefault().printLine_Normal(printStr);
				}
				else if (port.trim().equals("2"))
				{
					Printer.getDefault().printLine_Journal(printStr);
				}
				else if (port.trim().equals("3"))
				{
					Printer.getDefault().printLine_Slip(printStr);
				}
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
				String port = ConfigClass.BankConfig.trim();
				if (port == null || port.length() <= 0) Printer.getDefault().cutPaper_Slip();
				else if (port.trim().equals("1"))
				{
					Printer.getDefault().cutPaper_Normal();
				}
				else if (port.trim().equals("2"))
				{
					Printer.getDefault().cutPaper_Journal();
				}
				else if (port.trim().equals("3"))
				{
					Printer.getDefault().cutPaper_Slip();
				}
			}
			else
			{
				if (printdoc == null) return ;
				
				printdoc.flush();
				printdoc.close();
				printdoc = null;
			}
		}
	 
}
