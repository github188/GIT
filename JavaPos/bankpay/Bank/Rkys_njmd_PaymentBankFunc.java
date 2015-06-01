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
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
//南京门东项目  瑞康接口
//调用动态库（模块名：RKYS；动态库(dll文件）：trans.dll；函数：int MenuApp(char *In, char *Out)；）
public class Rkys_njmd_PaymentBankFunc extends PaymentBankFunc
{
	String path = null;
	public String[] getFuncItem()
    {
        String[] func = new String[5];
        
        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
        func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
        func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
        func[3] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
        func[4] = "[" + PaymentBank.XKQT1 + "]" + "其它交易";
        
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
        	case PaymentBank.XYKCX: //消费撤销
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易金额";
            break;
        	case PaymentBank.XYKTH://隔日退货   
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
			break;
        	case PaymentBank.XYKYE: //余额查询    
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "余额查询";
            break;
        	case PaymentBank.XKQT1: //其它交易    
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "其它交易";
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
		 	case PaymentBank.XYKYE: 	//余额查询    
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始余额查询";
            break;
		 	case PaymentBank.XKQT1: 	//其它交易    
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始其它交易";
            break;
			
		}
		
		return true;
    }
	
	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{
		path = ConfigClass.BankPath;
		try
		{
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && 
				(type != PaymentBank.XYKTH) && (type != PaymentBank.XYKYE) && 
				(type != PaymentBank.XKQT1))
	        {
	                errmsg = "银联接口不支持该交易";
	                new MessageBox(errmsg);

	                return false;
	        }
			
			 // 先删除上次交易数据文件
            if (PathFile.fileExist(path + "\\request.txt"))
            {
                PathFile.deletePath(path + "\\request.txt");
                
                if (PathFile.fileExist(path + "\\request.txt"))
                {
            		errmsg = "交易请求文件request.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            if (PathFile.fileExist(path + "\\result.txt"))
            {
                PathFile.deletePath(path + "\\result.txt");
                
                if (PathFile.fileExist(path + "\\result.txt"))
                {
            		errmsg = "交易请求文件result.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            if (PathFile.fileExist(path + "\\toprint.txt"))
            {
                PathFile.deletePath(path + "\\toprint.txt");
            }
            
            // 写入请求数据
            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
            {
                return false;
            }
            
            if (bld.retbz != 'Y')
            {
                // 调用接口模块
                if (PathFile.fileExist(path + "\\javaposbank.exe"))
                {
                	CommonMethod.waitForExec(path + "\\javaposbank.exe RKYS", "javaposbank");
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
	
	public boolean XYKNeedPrintDoc()
	{
		
		if (!checkBankSucceed())
        {
            return false;
        }
		
		int type = Integer.parseInt(bld.type.trim());
		
		if ((type == PaymentBank.XYKXF) || (type == PaymentBank.XYKCX) || 
				(type == PaymentBank.XYKTH) || (type == PaymentBank.XYKYE) || 
				(type == PaymentBank.XKQT1))
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
		 String line = "";
		 String type1 = "";
		 PrintWriter pw = null;
		 
		 try
		 {
			 switch (type)
			 {
			 	case PaymentBank.XYKXF: 	// 消费
                    type1 = "05";
                break;
			 	case PaymentBank.XYKCX: 	// 消费撤销
			 		type1 = "07";
			 	break;	
                case PaymentBank.XYKTH:		// 隔日退货
                	type1 = "08";
                break;
                case  PaymentBank.XYKYE: 	// 余额查询
                	type1 = "06";
                break;	
                default:
                    type1 = "ff";			// 其它交易
                break;
			 }
			 
			 String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
	         jestr = Convert.increaseCharForward(jestr,'0',12);
	         
	         String fphm = Convert.increaseChar(String.valueOf(GlobalInfo.syjStatus.fphm),' ',16);
	         
	         String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh,' ', 8);
	         
	         String syjh = Convert.increaseChar(ConfigClass.CashRegisterCode,' ', 8);
	         
	         line = type1 + jestr + syjh + syyh + fphm;
	    
	         try
	         {
	            pw = CommonMethod.writeFile(path + "\\request.txt");
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
			if (!PathFile.fileExist(path + "\\result.txt") || ((br = CommonMethod.readFileGBK(path + "\\result.txt")) == null))
            {
            	XYKSetError("XX","读取金卡工程应答数据失败!");
                new MessageBox("读取金卡工程应答数据失败!", null, false);
                
                return false;
            }
			
			String line = br.readLine();

            if (line == null || line.length() <= 0)
            {
                return false;
            }
            
            String result[] = line.split(",");
            if (result == null) return false;
           
            
            int type = Integer.parseInt(bld.type.trim());
            
            
        	bld.retcode  = result[1].substring(0,2);  //返回码2
        	if(!bld.retcode.equals("00"))
        	{
        		bld.retmsg = bld.retcode + "," + Convert.newSubString(result[1], 119, 151).trim();   //错误说明
        		return false;
        	}
        	
            if (result.length >= 2 && (type != PaymentBank.XKQT1))
            {
            	bld.cardno 		= result[1].substring(14,33);
            	bld.je	   		= ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Double.parseDouble(result[1].substring(2,14) ),100),2,1);
            	//bld.bankinfo 	= result[1].substring(31,43);
            	bld.trace       =  Convert.toInt(result[1].substring(49, 55));
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
                    
                    if (PathFile.fileExist(path + "\\request.txt"))
		            {
		                PathFile.deletePath(path + "\\request.txt");
		            }
					
					if (PathFile.fileExist(path + "\\result.txt"))
		            {
		                PathFile.deletePath(path + "\\result.txt");
		            }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
	}
	
//	public void XYKPrintDoc()
//	{
//
//		ProgressBox pb = null;
//		String name = null;
//		name = path + "\\toprint.txt";
//
//		try
//		{
//			if (!PathFile.fileExist(name))
//			{
//					new MessageBox("找不到签购单打印文件！！！");				
//				return ;
//			}
//			pb = new ProgressBox();
//			pb.setText("正在打印,请等待..." + "\t OY : " + GlobalInfo.sysPara.issetprinter);
//			
//			for (int i = 0; i < GlobalInfo.sysPara.bankprint; i ++)
//			{
//				BufferedReader br = null;
//				XYKPrintDoc_Start();
//				try
//				{
//					br = CommonMethod.readFileGB2312(name);
//					if (br == null)
//					{							
//						new MessageBox("打开签购单文件失败");						
//						return ;
//					}
//					
//					String line = null;
//					while ((line = br.readLine()) != null)
//					{
//						if (line.length() <= 0)
//							continue;
//						//银行签购单模板添加 "CUTPAPER" 标记
//						//当程序里面读取到这个字符是，打印机切纸
//						if (line.indexOf("CUTPAPER")>0)
//						{
//							XYKPrintDoc_End();
//							new MessageBox("请撕下客户签购单！！！");
//							
//							continue;
//						}
//						
//						XYKPrintDoc_Print(line);
//					}					
//				}
//				catch(Exception e)
//				{
//					new MessageBox(e.getMessage());
//				}
//				finally
//				{
//					if (br != null)
//					try
//					{
//						br.close();
//					}
//					catch(IOException ie)
//					{
//						ie.printStackTrace();
//					}					
//				}
//				XYKPrintDoc_End();
//			}
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			new MessageBox("打印签购单异常!!!\n" + e.getMessage());		
//		}
//		finally
//		{
//			if (pb != null)
//			{
//				pb.close();
//				pb = null;
//			}
//			if (PathFile.fileExist(name))
//			{
//				PathFile.deletePath(name);	
//			}
//		}
	
//	}
	
	
	public void XYKPrintDoc()
	{
		ProgressBox pb = null;
		String name = null;
		name = path + "\\toprint.txt";
		
		try
		{
			if (GlobalInfo.sysPara.bankprint <=0 ) return;
									
			int type = Integer.parseInt(bld.type.trim());
			 
			if (!PathFile.fileExist(name))
            {	
				if (type == PaymentBank.XKQT1) return ;
				
				new MessageBox("签购单文本不存在无法打印!", null, false);
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
	            	 br = CommonMethod.readFileGBK(name);

	            	 if (br == null)
	            	 {
                        new MessageBox("打开" + name + "打印文件失败!");

                        return;
	            	 }
	            	 
	            	 String line = null;
	                 int num = 0;
	                 
	            	 while ((line = br.readLine()) != null)
	            	 {
	            		 if (line.length() <= 0)
	            		 {
                            continue;
	            		 }
	            		 
	            		 if (line.indexOf("?") >= 0) continue;
	            			 
	            		 if (line.charAt(0) == (char)0x0c)
	            		 {
	            			 if (num >= 1)
	            			 {
	            				 break;
	            			 }
	            			 
	            			 for (int j = 1;j <= 5;j++)
	            			 {
	            				 XYKPrintDoc_Print("\n");
	            			 }
	            			 
	            			 //new MessageBox("请撕下商户签购单" );
	            			 num = num + 1;
	            		 }

	            		 XYKPrintDoc_Print(line.trim());
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
			
			if (PathFile.fileExist(name))
            {
                PathFile.deletePath(name);
            }
		}
	}
}