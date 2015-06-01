package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Payment.PaymentBank;

public class BjjhJl_PaymentBankFunc extends Bjjh_PaymentBankFunc
{
	int printflg =0;
	public boolean callBankFunc(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		if (type == PaymentBank.XYKJZ)
		{
			printflg =1;
			super.callBankFunc(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo);
			
			printflg =2;
			return super.callBankFunc(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo);
		}
		else
		{
			return super.callBankFunc(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo);
		}
	}
	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && 
				 (type != PaymentBank.XYKQD) && (type != PaymentBank.XYKJZ) && 
				 (type != PaymentBank.XYKYE) && (type != PaymentBank.XYKCD) )
	            {
	                errmsg = "银联接口不支持该交易";
	                new MessageBox(errmsg);

	                return false;
	            }
			
			 // 先删除上次交易数据文件
            if (PathFile.fileExist("c:\\bjjh\\request.txt"))
            {
                PathFile.deletePath("c:\\bjjh\\request.txt");
                
                if (PathFile.fileExist("c:\\bjjh\\request.txt"))
                {
            		errmsg = "交易请求文件request.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            if (PathFile.fileExist("c:\\bjjh\\result.txt"))
            {
                PathFile.deletePath("c:\\bjjh\\result.txt");
                
                if (PathFile.fileExist("c:\\bjjh\\result.txt"))
                {
            		errmsg = "交易请求文件result.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            if (PathFile.fileExist("c:\\bjjh\\javaposbankprint.txt"))
            {
                PathFile.deletePath("c:\\bjjh\\javaposbankprint.txt");
                
                if (PathFile.fileExist("c:\\bjjh\\javaposbankprint.txt"))
                {
            		errmsg = "打印文件javaposbankprint.txt无法删除,请重试";
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
                if (PathFile.fileExist("c:\\bjjh\\javaposbank.exe"))
                {
                	CommonMethod.waitForExec("c:\\bjjh\\javaposbank.exe BJJHJL");
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
                if (!XYKCheckRetCode()) return false;
                
                XYKPrintDoc();
                
            }
            
            
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	protected boolean XYKNeedPrintDoc()
	{
		if (bld.type.equals(String.valueOf(PaymentBank.XYKXF)))
			return true;
		else
			return super.XYKNeedPrintDoc();
		
	}
	
	public void XYKPrintDoc()
	{
		ProgressBox pb = null;
		try
		{
			if (GlobalInfo.sysPara.bankprint <= 0) { return; }

			if (!PathFile.fileExist("c:\\bjjh\\javaposbankprint.txt"))
			{
				//new MessageBox("找不到签购单打印文件!");
				return;
			}

			pb = new ProgressBox();
			pb.setText("正在打印银联签购单,请等待...");

			int num = 1;
			if (bld.type.equals(String.valueOf(PaymentBank.XYKJZ)))
			{
				
			}
			else
			{
				num = GlobalInfo.sysPara.bankprint;
			}
			num = 1;
			for (int i = 0; i < num; i++)
			{
				BufferedReader br = null;

				//
				Printer.getDefault().startPrint_Journal();

				try
				{
					//由于发现在windows环境下,用GBK读取文件会产生BUG,改为GB2310
					br = CommonMethod.readFileGB2312("c:\\bjjh\\javaposbankprint.txt");

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

						if (line.indexOf("cutpaper") >= 0) 
						{
							Printer.getDefault().cutPaper_Journal();
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
				if (printflg != 1)Printer.getDefault().cutPaper_Journal();
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
	
	public boolean XYKWriteRequest(int type, double money, String track1,String track2, String track3,String oldseqno, String oldauthno,String olddate, Vector memo)
	{
		try
		{
			 String line = "";
	 		 
	         String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
	         
	         for (int i = jestr.length(); i < 12; i++)
             {
                 jestr = "0" + jestr;
             }
	         
	         //	根据不同的类型生成文本结构
	         switch (type)
	         {
	         	case PaymentBank.XYKXF:
	         		line = PaymentBank.XYKXF + "," + jestr;
	         	break;
	         	case PaymentBank.XYKCX:
	         		line = PaymentBank.XYKCX+ "," + jestr + "," + oldseqno + "," + olddate;
	         	break;
	         	case PaymentBank.XYKQD:
	         		line = String.valueOf(PaymentBank.XYKQD);
	         	break;
	         	case PaymentBank.XYKJZ:
	         		line = String.valueOf(PaymentBank.XYKJZ);
	         	break;
	         	case PaymentBank.XYKYE:
	         		line = String.valueOf(PaymentBank.XYKYE);
	         	break;
	         	case PaymentBank.XYKCD:
	         		int ret = new MessageBox("重打签购单\n\n 任意键-消费或撤消交易凭单 / 2- 结算凭单 ", null, false).verify();
	    			if (ret == GlobalVar.Key2)
	    			{
	    				line = String.valueOf(PaymentBank.XYKCD) + ",0";
	    			}
	    			else
	    			{
	    				line = String.valueOf(PaymentBank.XYKCD) + ",1";
	    			}
	         	break;	
	         	
	         }
	         
	         PrintWriter pw = null;
	            
	         try
	         {
	            pw = CommonMethod.writeFile("c:\\bjjh\\request.txt");
	            
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
    	   if (!PathFile.fileExist("c:\\bjjh\\result.txt") || ((br = CommonMethod.readFileGBK("c:\\bjjh\\result.txt")) == null))
           {
           		XYKSetError("XX","读取金卡工程应答数据失败!");
           		new MessageBox("读取金卡工程应答数据失败!", null, false);

           		return false;
           }
    	   
    	   String line = br.readLine();
           if (line.length() <= 0)
           {
               return false;
           }
           
           String result[] = line.split(",");
           
           if (result == null) return false;
           
           if (Integer.parseInt(result[0]) != 0 && Integer.parseInt(result[0]) != 1)
           {
        	   bld.retcode 		= result[0];
        	   bld.retmsg		= "调用金卡函数发生异常!";
        	   
        	   return false;
           }
           
           bld.retcode 		= result[1];
           
           if (!bld.retcode.equals("00"))
           {
        	   bld.retmsg 		= result[2];
        	   return false;
           }
           
           int row =  1;
           
           String strprint = "";
           
           if (result.length > 3)
           {
        	   strprint = strprint + result[3] + "\n";
        	    
	           Vector v = new Vector();
	           v.add(result[3]);
	           
	           while ((line = br.readLine()) != null)
	           {
	        	   
	        	   row = row + 1;
	        	   
	        	   if (row > 1 && line.trim().length() > 0)
	        	   {
	        		   strprint = strprint + line + "\n";
	        		   v.add(line.trim());
	        	   }
	        	   
	        	   if (bld.type.equals(String.valueOf(PaymentBank.XYKXF)) || bld.type.equals(String.valueOf(PaymentBank.XYKCX)) || bld.type.equals(String.valueOf(PaymentBank.XYKTH)))
	        	   {
		        	   switch (row)
		        	   {
		        		   case 5:
		        			   bld.bankinfo = line.trim().substring(line.indexOf("：") + 1);
		        		   break;
		        		   case 6:
		        			   bld.cardno = line.trim().substring(line.indexOf("：") + 1);
		        		   break;
		        		   case 9:
		        			   bld.trace = Long.parseLong(line.trim().substring(line.indexOf("：") + 1));
		        		   break;
		        	   }
	        	   }
	        	   
	        	   //if (row >= 9) break;
	           }
	           
	           if (strprint.trim().length() > 0)
	           {
	        	   writePrintDoc(strprint,v);
	           }
           }
           
    	   return true;
       }
       catch (Exception ex)
       {
    	   ex.printStackTrace();
    	   
    	   XYKSetError("XX","读取应答XX:"+ex.getMessage());
           new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);
           
    	   
    	   return false;
       }
       finally
       {
           if (br != null)
           {
               try
               {
                   br.close();
               }
               catch (IOException e)
               {
                   e.printStackTrace();
               }
           }
       }
	 }
	
	public void writePrintDoc(String printstr,Vector v)
	{
        if (PathFile.fileExist("c:\\bjjh\\javaposbankprint.txt"))
        {
             PathFile.deletePath("c:\\bjjh\\javaposbankprint.txt");
        }
        
        PrintWriter pw = null;
        if (bld.type.equals(String.valueOf(PaymentBank.XYKCD)))
        {
        	v.removeAllElements();
        	BufferedReader br = CommonMethod.readFileGBK("c:\\bjjh\\record.ccb");
        	String li = null;
        	try
			{
				while ((li = br.readLine()) != null)
				   {
					 	if (li.indexOf("商户名称") >= 0)
					 	{
					 		li = li.substring(li.indexOf("商户名称"));
					 	}
					 	v.add(li);
					   //if (row >= 9) break;
				   }
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	 
        	 String line ="              中国建设银行\n";
 	    	line +="        China Construction Bank\n";
 	    	line +="        商户存根  MERCHANT   COPY\n";
 	    	line +="        *********重打印*******\n";
 	    	line +="\n";
 	    	line +="    "+String.valueOf(v.elementAt(0)).trim()+"\n";
 	    	line +="\n";
 	    	line +=String.valueOf(v.elementAt(1)).trim()+"\n";
 	    	line +=String.valueOf(v.elementAt(2)).trim()+"\n";
 	    	line +=String.valueOf(v.elementAt(3)).trim()+"\n";
 	    	line +=String.valueOf(v.elementAt(4)).trim()+"\n";
 	    	line +=String.valueOf(v.elementAt(5)).trim()+"\n";
 	    	line +=String.valueOf(v.elementAt(11)).trim()+"\n";
 	    	line +=String.valueOf(v.elementAt(6)).trim()+"\n";
 	    	line +=String.valueOf(v.elementAt(8)).trim()+"\n"; 
 	    	line +=String.valueOf(v.elementAt(13)).trim()+"\n";
 	    	line +=String.valueOf(v.elementAt(14)).trim()+"\n";                                                                                                                                                                
 	    	line +=String.valueOf(v.elementAt(10)).trim()+"\n"; 
 	    	line +=String.valueOf(v.elementAt(7)).trim()+"\n"; 
 	    	line +="\n";
 	    	line +=String.valueOf(v.elementAt(12))+"\n"; 
 	       	line +="\n";
 	       	line +="备注：请妥善保存、退货时请出示此单据"+"\n";
 	       	line +="REFERRAL:PLESE KEEP THE RECEIPT" +"\n";
 	       	line +="WELL AND SHOW ITWHEN YOU RETURN" +"\n";
 	       	line +="RELATIVE MERCHANDISE"+"\n";
 	       	line +="本人对此签购单有关之账务核对无误"+"\n";
 	       	line +="LACKNOWLEDGE SATISFACTORY RECEIPT OF"+"\n";
 	       	line +="RELATIVE GOODS/SERVICES"+"\n";
 	       	line +=" \n";
 	    	line +=" \n";
 	       	line +="持卡人签名"+"\n";
 	       	line +=" \n";
 	       	line +=" \n";
 	       	line +="CARDHOLEDER SIGNATURE"+"\n";
 	       	line +=" \n";
 	       	line +=" \n";
 	       	line +="不用现金，照样消费，电子付款，安全便利。"+"\n";
 	       	line +=" \n";
 	       	line +="cutpaper\n";
 	       	line +=" \n";
 	        line +="              中国建设银行\n";
	    	line +="        China Construction Bank\n";
	    	line +="        持卡人存根  CARDHOLDER COPY\n";
	    	line +="        *********重打印*******\n";
	    	line +="\n";
	    	line +="    "+String.valueOf(v.elementAt(0)).trim()+"\n";
	    	line +="\n";
	    	line +=String.valueOf(v.elementAt(1)).trim()+"\n";
	    	line +=String.valueOf(v.elementAt(2)).trim()+"\n";
	    	line +=String.valueOf(v.elementAt(3)).trim()+"\n";
	    	line +=String.valueOf(v.elementAt(4)).trim()+"\n";
	    	line +=String.valueOf(v.elementAt(5)).trim()+"\n";
	    	line +=String.valueOf(v.elementAt(11)).trim()+"\n";
	    	line +=String.valueOf(v.elementAt(6)).trim()+"\n";
	    	line +=String.valueOf(v.elementAt(8)).trim()+"\n"; 
	    	line +=String.valueOf(v.elementAt(13)).trim()+"\n";
	    	line +=String.valueOf(v.elementAt(14)).trim()+"\n";                                                                                                                                                                
	    	line +=String.valueOf(v.elementAt(10)).trim()+"\n"; 
	    	line +=String.valueOf(v.elementAt(7)).trim()+"\n"; 
	    	line +="\n";
	    	line +=String.valueOf(v.elementAt(12))+"\n"; 
	       	line +="\n";
	       	line +="备注：请妥善保存、退货时请出示此单据"+"\n";
	       	line +="REFERRAL:PLESE KEEP THE RECEIPT" +"\n";
	       	line +="WELL AND SHOW ITWHEN YOU RETURN" +"\n";
	       	line +="RELATIVE MERCHANDISE"+"\n";
	       	line +="本人对此签购单有关之账务核对无误"+"\n";
	       	line +="LACKNOWLEDGE SATISFACTORY RECEIPT OF"+"\n";
	       	line +="RELATIVE GOODS/SERVICES"+"\n";
	       	line +=" \n";
	    	line +=" \n";
	       	line +="持卡人签名"+"\n";
	       	line +=" \n";
	       	line +=" \n";
	       	line +="CARDHOLEDER SIGNATURE"+"\n";
	       	line +=" \n";
	       	line +=" \n";
	       	line +="不用现金，照样消费，电子付款，安全便利。"+"\n";
        
	        try
	        {
	           pw = CommonMethod.writeFile("c:\\bjjh\\javaposbankprint.txt");
	           
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
        }
        else if (bld.type.equals(String.valueOf(PaymentBank.XYKXF)) || bld.type.equals(String.valueOf(PaymentBank.XYKCX)) || bld.type.equals(String.valueOf(PaymentBank.XYKTH)) || bld.type.equals(String.valueOf(PaymentBank.XYKCD)))
        {
	       String line ="              中国建设银行\n";
	    	line +="        China Construction Bank\n";
	    	line +="        商户存根  MERCHANT COPY\n";
	    	line +="\n";
	    	line +=String.valueOf(v.elementAt(0)).trim()+"\n";
	    	line +="\n";
	    	line +=String.valueOf(v.elementAt(1)).trim()+"\n";
	    	line +=String.valueOf(v.elementAt(2)).trim()+"\n";
	    	line +=String.valueOf(v.elementAt(3)).trim()+"\n";
	    	line +=String.valueOf(v.elementAt(4)).trim()+"\n";
	    	line +=String.valueOf(v.elementAt(5)).trim()+"\n";
	    	line +=String.valueOf(v.elementAt(11)).trim()+"\n";
	    	line +=String.valueOf(v.elementAt(6)).trim()+"\n";
	    	line +=String.valueOf(v.elementAt(8)).trim()+"\n"; 
	    	line +=String.valueOf(v.elementAt(13)).trim()+"\n";
	    	line +=String.valueOf(v.elementAt(14)).trim()+"\n";                                                                                                                                                                
	    	line +=String.valueOf(v.elementAt(10)).trim()+"\n"; 
	    	line +=String.valueOf(v.elementAt(7)).trim()+"\n"; 
	    	line +="\n";
	    	line +=String.valueOf(v.elementAt(12))+"\n"; 
	       	line +="\n";
	       	line +="备注：请妥善保存、退货时请出示此单据"+"\n";
	       	line +="REFERRAL:PLESE KEEP THE RECEIPT" +"\n";
	       	line +="WELL AND SHOW ITWHEN YOU RETURN" +"\n";
	       	line +="RELATIVE MERCHANDISE"+"\n";
	       	line +="本人对此签购单有关之账务核对无误"+"\n";
	       	line +="LACKNOWLEDGE SATISFACTORY RECEIPT OF"+"\n";
	       	line +="RELATIVE GOODS/SERVICES"+"\n";
	       	line +=" \n";
	    	line +=" \n";
	       	line +="持卡人签名"+"\n";
	       	line +=" \n";
	       	line +=" \n";
	       	line +="CARDHOLEDER SIGNATURE"+"\n";
	       	line +=" \n";
	       	line +=" \n";
	       	line +="不用现金，照样消费，电子付款，安全便利。"+"\n";
	       	line +=" \n";
	       	line +="cutpaper \n";
	       	line +=" \n";
	       	line +="              中国建设银行\n";
	    	line +="        China Construction Bank\n";
	    	line +="        持卡人存根 CARDHOLDER COPY\n";
	    	line +="\n";
	    	line +=String.valueOf(v.elementAt(0)).trim()+"\n";
	    	line +="\n";
	    	line +=String.valueOf(v.elementAt(1)).trim()+"\n";
	    	line +=String.valueOf(v.elementAt(2)).trim()+"\n";
	    	line +=String.valueOf(v.elementAt(3)).trim()+"\n";
	    	line +=String.valueOf(v.elementAt(4)).trim()+"\n";
	    	line +=String.valueOf(v.elementAt(5)).trim()+"\n";
	    	line +=String.valueOf(v.elementAt(11)).trim()+"\n";
	    	line +=String.valueOf(v.elementAt(6)).trim()+"\n";
	    	line +=String.valueOf(v.elementAt(8)).trim()+"\n"; 
	    	line +=String.valueOf(v.elementAt(13)).trim()+"\n";
	    	line +=String.valueOf(v.elementAt(14)).trim()+"\n";                                                                                                                                                                
	    	line +=String.valueOf(v.elementAt(10)).trim()+"\n"; 
	    	line +=String.valueOf(v.elementAt(7)).trim()+"\n"; 
	    	line +="\n";
	    	line +=String.valueOf(v.elementAt(12))+"\n"; 
	       	line +="\n";
	       	line +="备注：请妥善保存、退货时请出示此单据"+"\n";
	       	line +="REFERRAL:PLESE KEEP THE RECEIPT" +"\n";
	       	line +="WELL AND SHOW ITWHEN YOU RETURN" +"\n";
	       	line +="RELATIVE MERCHANDISE"+"\n";
	       	line +="本人对此签购单有关之账务核对无误"+"\n";
	       	line +="LACKNOWLEDGE SATISFACTORY RECEIPT OF"+"\n";
	       	line +="RELATIVE GOODS/SERVICES"+"\n";
	       	line +=" \n";
	    	line +=" \n";
	       	line +="持卡人签名"+"\n";
	       	line +=" \n";
	       	line +=" \n";
	       	line +="CARDHOLEDER SIGNATURE"+"\n";
	       	line +=" \n";
	       	line +=" \n";
	       	line +="不用现金，照样消费，电子付款，安全便利。"+"\n";
        
	        try
	        {
	           pw = CommonMethod.writeFile("c:\\bjjh\\javaposbankprint.txt");
	           
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
        }
        else
        {
        	String line = "";
        	for (int i = 0 ; i < v.size(); i++)
        	{
        		line+=(String)v.elementAt(i)+"\n";
        	}
        	
	        try
	        {
	           pw = CommonMethod.writeFile("c:\\bjjh\\javaposbankprint.txt");
	           
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
        }
	}
}
