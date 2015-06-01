package bankpay.Bank;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentBank;


public class CdcsfICCard_PaymentBankFunc extends BjcsfICCard_PaymentBankFunc
{
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
            if (PathFile.fileExist("C:\\JavaPos\\ickcl.in"))
            {
                PathFile.deletePath("C:\\JavaPos\\ickcl.in");
                
                if (PathFile.fileExist("C:\\JavaPos\\ickcl.in"))
                {
            		errmsg = "交易请求文件ickcl.in无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            if (PathFile.fileExist("C:\\JavaPos\\ickcl.OUT"))
            {
                PathFile.deletePath("C:\\JavaPos\\ickcl.OUT");
                
                if (PathFile.fileExist("C:\\JavaPos\\ickcl.OUT"))
                {
            		errmsg = "交易请求文件ickcl.OUT无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            // 写入请求数据
            if (PaymentBank.XYKXF == type)
            {
            	bool = false;
            	saleBS = (SaleBS)memo.elementAt(2);
            	
            	bld.trace = saleBS.saleHead.fphm;
            	
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
                if (PathFile.fileExist("C:\\JavaPos\\ick_main_xs.exe"))
                {
                	CommonMethod.waitForExec("C:\\JavaPos\\ick_main_xs.exe");
                }
                else
                {
                    new MessageBox("找不到储值工程模块 ick_main_xs.exe");
                    XYKSetError("XX","找不到储值工程模块 ick_main_xs.exe");
                    return false;
                }
                
                // 读取应答数据
                if (!XYKReadResult())
                {
                    return false;
                }
                
                // 检查交易是否成功
                if (!XYKCheckRetCode()) 
                {
                	new MessageBox("当前交易失败....");
                	return false;
                }
                
                if (PaymentBank.XYKXF == type)
                {
                	bld.je = money;
                	
                    bld.type = String.valueOf(PaymentBank.XYKYE);
                	if (!XYKExecute(PaymentBank.XYKYE, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
                	{
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
			date = mdt.getDateByEmpty();
			
			//	根据不同的类型生成文本结构
	         switch (type)
	         {
	         	case PaymentBank.XYKXF:
	         		bld.crc = "100001";
	         		line = bld.crc + "\r\n" +  Convert.increaseChar(GlobalInfo.sysPara.mktcode,' ',4) +  Convert.increaseChar(GlobalInfo.syjDef.syjh,' ',6) + Convert.increaseChar(String.valueOf(GlobalInfo.syjStatus.fphm),' ',10) + 
	         		date + "      " + Convert.increaseCharForward(ManipulatePrecision.doubleToString(money),' ',12) + " " + Convert.increaseCharForward(port,' ',3);
	         	break;
	         	case PaymentBank.XYKYE:
	         		bld.crc = "100002";
	         		line = bld.crc + "\r\n" +  Convert.increaseChar(GlobalInfo.sysPara.mktcode,' ',4) + Convert.increaseChar(GlobalInfo.syjDef.syjh,' ',6) + Convert.increaseChar(date,' ',16) + port;	
	         	break;	
	         	
	         }
	         
	         try
	         {
	            pw = CommonMethod.writeFile("C:\\JavaPos\\ickcl.in");
	            
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
			if (!PathFile.fileExist("C:\\JavaPos\\ickcl.OUT") || ((br = CommonMethod.readFileGBK("C:\\JavaPos\\ickcl.OUT")) == null))
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
             
            int i = 1;
            
			while ((line = br.readLine()) != null)
	        {
				 if (i == 1)
				 {
					 bld.retcode = line.substring(0,4);
					 bld.retmsg  = line.substring(line.indexOf(" ") + 1);
				 }
				 else if(line.substring(0,4).equals("0004"))
				 {
					 if (!bool)
            		 {
						 bld.allotje = Double.parseDouble(line.substring(4));
            		 }
					 else
					 {
						 bld.tempstr = line.substring(4);
					 }
				 }
				 else if (line.substring(0,4).equals("0005"))
				 {
					 // 卡状态以及有效期
					 bld.memo = line.substring(4);
				 }
				 else if (line.substring(0,4).equals("0002"))
				 {
					 bld.cardno = line.substring(4);
				 }
				 
				 i = i + 1;
	        }
			
			if (Integer.parseInt(bld.type) == PaymentBank.XYKXF)
            {
				
            }
			else if (Integer.parseInt(bld.type) == PaymentBank.XYKYE)
            {
				 if (bld.retcode.trim().equals("0000"))
            	 {
					 if (!bool)
            		 {
						 bool = true;
            		 }
					 
					 bld.retmsg 	= "余额:" + bld.allotje; 
            	 }
				 else
				 {
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
	       if (Integer.parseInt(bld.retcode.trim()) == 0)
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
			if (bld.retcode.equals("0000"))
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
			 line = line +	"交易前余额: " + ManipulatePrecision.doubleToString(bld.allotje) + "\n";
			 line = line + 	"交易后余额: " + ManipulatePrecision.doubleToString(Double.parseDouble(bld.tempstr)) + "\n";
			// line = line + 	"\n\n\n持卡人签名: __________________\n";
			 line = line +  "CUTPAPPER";
			 
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
