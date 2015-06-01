package bankpay.Bank;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;


// ICCARD储值
public class BjcsfICCard_PaymentBankFunc extends PaymentBankFunc
{
	public boolean bool = false;
	public String date = null;
	public SaleBS saleBS = null;
	
	public String[] getFuncItem()
    {
        String[] func = new String[2];

        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
        func[1] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
        
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
        	case PaymentBank.XYKYE: //余额查询    
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "余额查询";
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
		 	case PaymentBank.XYKYE: 	//余额查询    
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始余额查询";
            break;
		}
		
		return true;
    }
	
	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKYE))	
	        {
                errmsg = "IC卡储值接口不支持该交易";
                new MessageBox(errmsg);

                return false;
	        }
			
			 // 先删除上次交易数据文件
            if (PathFile.fileExist("C:\\JavaPos\\request.txt"))
            {
                PathFile.deletePath("C:\\JavaPos\\request.txt");
                
                if (PathFile.fileExist("C:\\JavaPos\\request.txt"))
                {
            		errmsg = "交易请求文件request.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            if (PathFile.fileExist("C:\\JavaPos\\result.txt"))
            {
                PathFile.deletePath("C:\\JavaPos\\result.txt");
                
                if (PathFile.fileExist("C:\\JavaPos\\result.txt"))
                {
            		errmsg = "交易请求文件result.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
              
            //  写入请求数据
            if (PaymentBank.XYKXF == type)
            {
            	bool = false;
            	saleBS = (SaleBS)memo.elementAt(2);
        		
            	bld.trace 		= saleBS.saleHead.fphm;
            	
            	bld.type = String.valueOf(PaymentBank.XYKYE);
            	if (!XYKExecute(PaymentBank.XYKYE, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo)) return false;
            	
            	bld.retbz = 'N';
            	
            	bld.type = String.valueOf(PaymentBank.XYKXF);
            	
            	if (bld.allotje <= 0)
            	{
            		bld.retbz = 'N';
            		bld.retcode = "1";
            		bld.retmsg = "当前卡余额为0,无法消费!";
            		
            		return false;
            	}
            	
            	if (money > bld.allotje) money = bld.allotje;
            }
            
            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
        	{
        		return false;
        	}
            
            if (bld.retbz != 'Y')
            {
            	//   调用接口模块
                if (PathFile.fileExist("C:\\JavaPos\\javaposbank.exe"))
                {
                	CommonMethod.waitForExec("C:\\JavaPos\\javaposbank.exe BJCSFICCARD");
                }
                else
                {
                    new MessageBox("找不到储值工程模块 javaposbank.exe");
                    XYKSetError("XX","找不到储值工程模块 javaposbank.exe");
                    return false;
                }
                
                // 读取应答数据
                if (!XYKReadResult())
                {
                    return false;
                }
                
                // 检查交易是否成功
                if (!XYKCheckRetCode()) return false;
                
                if (PaymentBank.XYKXF == type)
                {
                	bld.je = money;
                	
                    bld.type = String.valueOf(PaymentBank.XYKYE);
                	if (!XYKExecute(PaymentBank.XYKYE, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
                	{
                		 // 面额,余额
                		String memomoney[] = bld.memo.split(",");
               		 	bld.memo		= memomoney[0] + "," + (bld.allotje - money);
               		 	bld.retbz = 'Y';
               		 	bld.retcode = "0";
               		 	bld.retmsg = "消费成功";
                	}
                	
                	bld.type 		= String.valueOf(PaymentBank.XYKXF);
                	bld.trace 		= saleBS.saleHead.fphm;
                }
            }
            
            
            
            if (XYKNeedPrintDoc())
            {
            	// 生成签购单
            	if (XYKWriteQgd());
            	{
            		// 打印签购单
            		XYKPrintDoc();
            	}
            }
            
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	public boolean XYKWriteRequest(int type, double money, String track1,String track2, String track3,String oldseqno, String oldauthno,String olddate, Vector memo)
	{
		BufferedReader br = null;
		PrintWriter pw = null;
		
		try
	    {
			String line = "";
			String port = "0";
			
			br = CommonMethod.readFileGBK(GlobalVar.ConfigPath + "\\ICCardConfig.ini");
			
			if (br == null)
			{
				new MessageBox("打开 ICCardConfig.ini 文件失败!");
				return false;
			}
			
			if ((line = br.readLine()) == null)
			{
				new MessageBox("ICCardConfig.ini 没有配置端口!");
				return false;
			}
			
			if (line.indexOf("=") > -1 && line.trim().split("=").length == 2)
			{
				port = line.split("=")[1].trim();
			}
			
			ManipulateDateTime mdt = new ManipulateDateTime();
			date = mdt.getDateByEmpty()+mdt.getTimeByEmpty();
			
			//根据不同的类型生成文本结构
	         switch (type)
	         {
	         	case PaymentBank.XYKXF:
	         		line = type + "," + port + ","+ ManipulatePrecision.doubleToString(money) + "," + 
	         			   GlobalInfo.sysPara.mktcode + "," + GlobalInfo.syjDef.syjh + "," + GlobalInfo.syjStatus.fphm + "," + date.substring(0,12);;
	         	break;
	         	
	         	case PaymentBank.XYKYE:
	         		line = type +  "," + port;	
	         	break;	
	         	
	         }
			
	         try
	         {
	            pw = CommonMethod.writeFile("C:\\JavaPos\\request.txt");
	            
	            if (pw != null)
	            {
	                pw.print(line);
	                pw.flush();
	            }
	         }
	         finally
	         {
	        	if (pw != null)
	        	{
	        		pw.close();
	        		pw = null;
	        	}
	        	
				if (br != null)
				{
					try
					{
						br.close();
						br = null;
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
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
			if (br != null)
			{
				try
				{
					br.close();
					br = null;
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
			
			if (pw != null)
        	{
        		pw.close();
        		pw = null;
        	}
		}
	}
	
	public boolean XYKReadResult()
	{
		BufferedReader br = null;
		
		try
        {
			if (!PathFile.fileExist("C:\\JavaPos\\result.txt") || ((br = CommonMethod.readFileGBK("C:\\JavaPos\\result.txt")) == null))
            {
            	XYKSetError("XX","读取储值工程应答数据失败!");
                new MessageBox("读取储值工程应答数据失败!", null, false);

                return false;
            }
			
			String line = br.readLine();

            if (line.length() <= 0)
            {
                return false;
            }
            
            String result[] = line.split(",");
            
            if (result == null) return false;
            
            if (Integer.parseInt(bld.type) == PaymentBank.XYKXF)
            {
            	 bld.retcode 	= result[0];
                 bld.retmsg 	= getICCardXFCodeInfo(Integer.parseInt(result[0]));
            }
            else if (Integer.parseInt(bld.type) == PaymentBank.XYKYE)
            {
            	 bld.retcode 	= result[0];
            	 
            	 if (bld.retcode.trim().equals("1"))
            	 {
            		 bld.cardno		= result[1].substring(0,14);
            		 
            		 if (!bool)
            		 {
            			 bld.allotje	=  Double.parseDouble(result[1].substring(40,52));
            			 bld.memo		= result[1].substring(14,26) + "," + result[1].substring(40,52);
            			 bool = true;
            		 }
            		 
            		 // 面额,余额
            		 bld.memo		= result[1].substring(14,26) + "," + result[1].substring(40,52);
            		 
            		 bld.retmsg 	= "面额:" + result[1].substring(14,26) + "\n" + "余额:" + result[1].substring(40,52); 
            	 }
            	 else
            	 {
            		 if (bld.retcode.trim().equals("0"))
            		 {
            			 bld.retmsg = "未插卡";
            		 }
            		 else if (bld.retcode.trim().equals("2"))
            		 {
            			 bld.retmsg = "密码错误";
            		 }
            		 else
            		 {
            			 bld.retmsg = "未知错误";
            		 }
            		 
            		 return false;
            	 }
            }
            else
            {
            	new MessageBox("没有匹配的消费类型!", null, false);
            	return false;
            }
            
			return true;
        }
		catch (Exception ex)
		{
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
					br = null;
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}
	
	public boolean XYKCheckRetCode()
	{
		if (Integer.parseInt(bld.type) == PaymentBank.XYKXF)
        {
	       if (bld.retcode.trim().equals("0"))
	       {
	    	   // 由于消费还要调一次查询所以设为A
	           bld.retbz = 'A';
	
	           return true;
	       }
	       else
	       {
	           bld.retbz = 'N';
	
	           return false;
	       }
        }
		else
		{
			if (bld.retcode.trim().equals("1"))
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
    	   if (Integer.parseInt(bld.type) != PaymentBank.XYKYE)
    	   {
    		   errmsg = "交易成功";
    	   }
    	   else
    	   {
    		   errmsg = bld.retmsg;
    	   }
    		   
           return true;
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
	 
	private String getICCardXFCodeInfo(int num)
	{
		switch (num)
		{
			case 0:
				return "成功";
			case 1:
				return "不是消费设备，未插卡";
			case 2:
				return "密码错误，连续三次卡报废";
			case 3:
				return "尚未开卡";
			case 4:
				return "卡超过有效期";
			case 5:
				return "金额错误";
			case 6:
				return "读消费次数错误";
			case 7:
				return "当前卡余额为0,无法消费!";
			case 8:
				return "写消费位置错误";
			case 9:
				return "写消费记录错误";
			default:
				return "其它错误";
		}
	}
	
	public void XYKPrintDoc()
	 {
		 ProgressBox pb = null;
		 
		 try
		 {
			 String printName = "";
			 
			 int type = Integer.parseInt(bld.type.trim());
			 
			 if (type == PaymentBank.XYKXF)
			 {
				 if (!PathFile.fileExist("C:\\JavaPos\\ICCard.txt"))
	                {
	                    new MessageBox("找不到签购单打印文件!");

	                    return;
	                }
	                else
	                {
	                    printName = "C:\\JavaPos\\ICCard.txt";
	                }
			 }
			 else
			 {
	             return;
			 }
			 
			 pb = new ProgressBox();
	         pb.setText("正在打印ICCard签购单,请等待...");
	         
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
	            	   	 
	            	 String line = null;
	                   
	            	 while ((line = br.readLine()) != null)
	            	 {
                       if (line.trim().equals("CUTPAPPER"))
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
			 new MessageBox("打印ICCard签购单发生异常\n\n" + ex.getMessage());
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
	
	public boolean XYKWriteQgd()
	{
		PrintWriter pw = null;
		String line = "";
		
		try
	    {
			int type = Integer.parseInt(bld.type.trim());
			 
			if (type  != PaymentBank.XYKXF) return false;
			
			if (PathFile.fileExist("C:\\JavaPos\\ICCard.txt"))
            {
				 PathFile.deletePath("C:\\JavaPos\\ICCard.txt");
	                
	             if (PathFile.fileExist("C:\\JavaPos\\ICCard.txt"))
	             {
	            		errmsg = "交易ICCard.txt签购单无法删除,请重试";
	            		XYKSetError("XX",errmsg);
	            		new MessageBox(errmsg);
	            		return false;   	
	              }
             }
			 
			 pw = CommonMethod.writeFile("C:\\JavaPos\\ICCard.txt");
	         
			 line = "       ICCard储值卡签购单\n";
			 line = line + "客户名称: " + GlobalInfo.sysPara.mktname + "   门店号: " + GlobalInfo.sysPara.mktcode + "\n";
			 line = line + "日期时间: " + date + "   交易类型: 消费\n";
			 line = line + "收银机号: " + GlobalInfo.syjDef.syjh + "   收银员号 " +  GlobalInfo.posLogin.gh +"\n";
			 line = line + "卡号: " + bld.cardno + "\n";
			 line = line + "消费流水: " + bld.trace + "\n";
			 String str[] = bld.memo.split(",");
			 line = line + "卡面额: "   + ManipulatePrecision.doubleToString(Double.parseDouble(str[0])) + "\n";
			 line = line +	"交易前余额: " + ManipulatePrecision.doubleToString(bld.allotje) + "\n";
			 line = line + 	"交易后余额: " + ManipulatePrecision.doubleToString(Double.parseDouble(str[1])) + "\n";
			// line = line + 	"\n\n\n持卡人签名: __________________\n";
			 line = line +  "CUTPAPPER\n\n\n\n\n";
			 
			 if (pw != null)
	         {
                pw.println(line);
                pw.flush();
	         }
			 
			 return true;
	    }
		catch (Exception ex)
		{
			 new MessageBox("生成ICCard签购单失败!\n\n" + ex.getMessage(), null, false);
	         ex.printStackTrace();

	         return false;
		}
		finally
		{
			if (pw != null)
        	{
        		pw.close();
        		pw = null;
        	}
		}
	}
	
}
