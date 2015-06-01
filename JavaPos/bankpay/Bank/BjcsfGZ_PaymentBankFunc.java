package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;


//广众优惠券接口
public class BjcsfGZ_PaymentBankFunc extends PaymentBankFunc
{
	private SaleBS saleBS = null;
	
	public String[] getFuncItem()
	{
	    String[] func = new String[3];

	    func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
	    func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
	    func[2] = "[" + PaymentBank.XYKTH + "]" + "退货";
	    
	    return func;
	}
	
	public boolean getFuncLabel(int type, String[] grpLabelStr)
    {
        //0-4对应FORM中的5个输入框
        //null表示该不用输入
        switch (type)
        {
            case PaymentBank.XYKXF: //消费
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = "优惠券号";
                grpLabelStr[4] = "执行操作";

            break;
            case PaymentBank.XYKCX: //消费撤销
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "执行操作";

            break;
            case PaymentBank.XYKTH: //隔日退货   
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = "优惠券号";
                grpLabelStr[4] = "执行操作";
            break;
        }

        return true;
    }
	
	public boolean getFuncText(int type, String[] grpTextStr)
    {
        //0-4对应FORM中的5个输入框
        //null表示该需要用户输入,不为null用户不输入
        switch (type)
        {
            case PaymentBank.XYKXF: //消费
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "回车消费";

            break;
            case PaymentBank.XYKCX: //消费撤销
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "回车撤销";

            break;
            case PaymentBank.XYKTH: //退货
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "回车退货";
            break;
        }

        return true;
    }
	
	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && (type != PaymentBank.XYKTH))
            {
                errmsg = "广众接口不支持该交易";
                new MessageBox(errmsg);

                return false;
            }
			
			bld.cardno = track2;
			
			if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
			{
        		return false;
			}
			 
        	// 生成签购单
			XYKWriteQgd();
            
			return true;
		}
		catch (Exception ex)
		{
			 ex.printStackTrace();
			 XYKSetError("XX","广众异常XX:"+ex.getMessage());
			 new MessageBox("调用广众工程处理模块异常!\n\n" + ex.getMessage(), null, false);

            return false;
		}
	}
		
	public boolean XYKCheckRetCode()
    {
        if (bld.retcode.trim().equals("1"))
        {
            bld.retbz  = 'Y';
            
            if (PaymentBank.XYKCX == Integer.parseInt(bld.type))
            {
            	bld.retmsg = "广众工程调用成功\n" + bld.retmsg;
            }
            else
            {
            	bld.retmsg = "广众工程调用成功";
            }
            
            return true;
        }
        else
        {
            bld.retbz = 'N';
            
            if (Integer.parseInt(bld.type) == PaymentBank.XYKCX)
     	   	{
     		   new MessageBox("撤消失败,请与广众客服联系!", null, false);
     		   return true;
     	   	}
            
            return false;
        }
    }
	
	public boolean XYKWriteRequest(int type, double money, String track1,String track2, String track3,String oldseqno, String oldauthno,String olddate, Vector memo)
	{
		String line = "";
		int flag = 0;
		double hjje = 0;
		try
        {
			// 根据不同的类型生成文本结构
            switch (type)
            {	
                case PaymentBank.XYKXF:
                case PaymentBank.XYKTH:
                	
                	if (!delOldFile()) return false;
                		
                	saleBS = (SaleBS)memo.elementAt(2);
                		
                	if (SellType.ISBACK(saleBS.saleHead.djlb))
        			{
        				flag = 1;
        			}
                	
                	bld.trace 		= saleBS.saleHead.fphm;
                	
                	if (!saleBS.saleHead.memo.equals("F_XSXP_ADD"))
                	{
                		line = "1," + GlobalInfo.sysPara.mktcode + "," + saleBS.saleHead.fphm + "," + GlobalInfo.syjDef.syjh + "," + saleBS.saleHead.hykh + "," + saleBS.saleHead.syyh + ",";
                		
                		for (int i = 0;i < saleBS.saleGoods.size();i++)
                		{
                			
                			SaleGoodsDef saleGoods = (SaleGoodsDef)saleBS.saleGoods.get(i);
                			line = line + "|" +saleGoods.rowno + "|^|" + saleGoods.barcode + "|^|" + saleGoods.name.replaceAll(",","").replaceAll(";","").replaceAll("'","") + "|^";
                			
                			hjje = saleGoods.hjje ;
                			
                			if (type == PaymentBank.XYKTH) hjje =  hjje * -1;
                			
                			line = line + saleGoods.sl + "^" + hjje + ";";
                			
                		}
                		        		
                		line = line + "," + flag;
                		
                		if (!createRequestFile(line)) return false;
                			
                		if (!execuFunction()) return false;
                		
                		line = "";
                		
                		saleBS.saleHead.memo = "F_XSXP_ADD";	
                		
                		bld.retbz = 'N';
                	}
                	 		
            		if (!delOldFile()) return false;
            		
            		line = "4," + track2 + "," + GlobalInfo.sysPara.mktcode + "," + saleBS.saleHead.fphm + "," + GlobalInfo.syjDef.syjh + "," + flag;
            		
            		if (!createRequestFile(line)) return false;
        			
            		if (!execuFunction())
            		{
            			if (saleBS.saleHead.num4 <= 0)
                		{
            				line = "2," + GlobalInfo.sysPara.mktcode + "," + saleBS.saleHead.fphm + "," + GlobalInfo.syjDef.syjh + "," + flag;
            				
            				bld.type = String.valueOf(PaymentBank.XYKCX);
            				
            				if (!createRequestFile(line)) return false;
            				
                    		if (!execuFunction()) return false;
                    		
                			saleBS.saleHead.memo = "";
                			
                			bld.type = String.valueOf(type);
                			
                			bld.retbz = 'N';
                			
                		}
            			
            			return false;
            		}
                	
            		// 记录用了几个优惠券付款
                	saleBS.saleHead.num4 = saleBS.saleHead.num4 + 1;
                	
                break;
                case PaymentBank.XYKCX:
                	if (!delOldFile()) return false;
                
                	saleBS = (SaleBS)memo.elementAt(2);
                	
                	if (saleBS == null)
                	{
                		saleBS = GlobalInfo.saleform.getSaleEvent().saleBS;
                	}
                	
                	if (SellType.ISBACK(saleBS.saleHead.djlb))
        			{
        				flag = 1;
        			}
                	
                	saleBS.saleHead.num4 = saleBS.saleHead.num4 -  1;
                	bld.trace 		= saleBS.saleHead.fphm;
                	
            		if (saleBS.saleHead.num4 <= 0)
            		{
            			line = "2," + GlobalInfo.sysPara.mktcode + "," + saleBS.saleHead.fphm + "," + GlobalInfo.syjDef.syjh + "," + flag;
            		}
            		else
            		{
            			SalePayDef salepay  = (SalePayDef)memo.elementAt(3);
            			line = "6," + salepay.payno + "," + GlobalInfo.sysPara.mktcode + "," + saleBS.saleHead.fphm + "," + GlobalInfo.syjDef.syjh + "," + flag;
            		}
            		
            		if (!createRequestFile(line))
            		{
            			saleBS.saleHead.num4 = saleBS.saleHead.num4 +  1;
            			return false;
            		}
        			
            		if (!execuFunction())
            		{
            			saleBS.saleHead.num4 = saleBS.saleHead.num4 +  1;
            			return false;
            		}
            		
            		if (saleBS.saleHead.num4 <= 0)
            		{
            			saleBS.saleHead.memo = "";
            		}
            		
            		if (PathFile.fileExist("C:\\JavaPos\\GZCMCard\\GZCMCard_" + bld.cardno + ".txt"))
                    {
                        PathFile.deletePath("C:\\JavaPos\\GZCMCard\\GZCMCard_" + bld.cardno + ".txt");
                    }
                	
                break;
                default:
                    bld.retbz = 'Y';

                    return true;
            }
 
			return true;
        }
		catch (Exception ex)
		{
			new MessageBox("写入广众工程请求数据异常!\n\n" + ex.getMessage(), null, false);
	        ex.printStackTrace();

	        return false;
		}
	}
	
	private boolean delOldFile()
	{
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
        
        return true;
	}
	
	private boolean execuFunction()
	{
		try
		{
			if (bld.retbz != 'Y')
	        {
	        	 // 调用接口模块
	            if (PathFile.fileExist("C:\\JavaPos\\javaposbank.exe"))
	            {
	            	CommonMethod.waitForExec("C:\\JavaPos\\javaposbank.exe BJCSFGZ");
	            }
	            else
	            {
	                new MessageBox("找不到广众工程模块 javaposbank.exe");
	                XYKSetError("XX","找不到广众工程模块 javaposbank.exe");
	                
	                if (Integer.parseInt(bld.type) == PaymentBank.XYKCX)
	         	   	{
	         		   new MessageBox("撤消失败,请与广众客服联系!", null, false);
	         		   return true;
	         	   	}
	                
	                return false;
	            }
	            
	            // 读取应答数据
	            if (!XYKReadResult())
	            {
	            	if (Integer.parseInt(bld.type) == PaymentBank.XYKCX)
	         	   	{
	         		   new MessageBox("撤消失败,请与广众客服联系!", null, false);
	         		   return true;
	         	   	}
	            	
	                return false;
	            }
	            
	            //  检查交易是否成功
	            return XYKCheckRetCode();
	        }
		
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	private boolean createRequestFile(String line)
	{
		 PrintWriter pw = null;
         
         try
         {
	            pw = CommonMethod.writeFile("C:\\JavaPos\\request.txt");
	            if (pw != null)
	            {
	                pw.print(line);
	                pw.flush();
	            }
	            
	            return true;
	      }
          catch (Exception ex)
          {
        	 XYKSetError("XX","写入广众工程请求数据失败!");
             new MessageBox("写入广众工程请求数据失败!" + ex.getMessage(), null, false);
        	 ex.printStackTrace();
        	 return false;
          }
	      finally
	      {
	    	  if (pw != null)
	    	  {
	    		  pw.close();
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
	            	XYKSetError("XX","读取广众工程应答数据失败!");
	                new MessageBox("读取广众工程应答数据失败!", null, false);

	                return false;
	            }
	        	
	        	String line = br.readLine();

	            if (line.length() <= 0)
	            {
	                return false;
	            }
	              
	            String result[] = line.split(",");
	            
	            if (result == null) return false;
	                  
	            switch (Integer.parseInt(bld.type))
	            {
	            	case PaymentBank.XYKXF:
	            	case PaymentBank.XYKTH:
	                	if (saleBS != null && !saleBS.saleHead.memo.equals("F_XSXP_ADD"))
	                	{
	                		bld.retcode 	= result[0];
	                	}
	                	else
	                	{
	                		bld.retcode 	= result[0];
	                		
	                		if (Integer.parseInt(bld.retcode) < 0)
	                		{
	                			bld.retmsg 		= 	result[3];
	                		}
	                		else
	                		{
	                			bld.je 			=  	Double.parseDouble(result[1].trim());
	                			
	                			if (bld.je  <= 0) bld.retcode = "0";
	                			
	                			bld.memo 		= 	result[2];
	                			bld.retmsg 		= 	result[3];
	                			bld.trace 		= 	GlobalInfo.syjStatus.fphm;
	                		}
	     
	                	}
	            	break;
	            	case PaymentBank.XYKCX:
	            		bld.retcode 	= result[0];	
	                break;
	            }
	            
	            bld.bankinfo	= "广众传媒";
	            
	        	return true;
	        }
	        catch (Exception ex)
	        {
	        	XYKSetError("XX","读取应答XX:"+ex.getMessage());
	            new MessageBox("读取广众工程应答数据异常!" + ex.getMessage(), null, false);
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
	                    
	                    if (PathFile.fileExist("C:\\JavaPos\\request.txt"))
			            {
			                PathFile.deletePath("C:\\JavaPos\\request.txt");
			            }
						
						if (PathFile.fileExist("C:\\JavaPos\\result.txt"))
			            {
			                PathFile.deletePath("C:\\JavaPos\\result.txt");
			            }
	                }
	                catch (IOException e)
	                {
	                    e.printStackTrace();
	                }
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
			 
			if (type  != PaymentBank.XYKXF && type  != PaymentBank.XYKTH) return false;
			
			if (!PathFile.fileExist("C:\\JavaPos\\GZCMCard"))
			{
				PathFile.createDir("C:\\JavaPos\\GZCMCard");
			}
					 
			 pw = CommonMethod.writeFile("C:\\JavaPos\\GZCMCard\\GZCMCard_" + bld.cardno + ".txt");
			 
			 ManipulateDateTime mdt = new ManipulateDateTime();
			 String date = mdt.getDateByEmpty()+mdt.getTimeByEmpty();
				
			 
			 line = "       广众优惠券签购单\n";
			 //line = line + "客户名称: " + GlobalInfo.sysPara.mktname + "\n";
			 //line = line + "门店号: " + GlobalInfo.sysPara.mktcode + "\n";
			 line = line + "日期时间: " + date + "   交易类型: "+ getSaleType(Integer.parseInt(bld.type)) +"\n";
			 //line = line + "收银机号: " + GlobalInfo.syjDef.syjh + "   收银员号 " +  GlobalInfo.posLogin.gh +"\n";
			 line = line + "卡号: " + bld.cardno + "\n";
			 //line = line + "消费流水: " + bld.trace + "\n";
			 //line = line + "消费金额: "   + ManipulatePrecision.doubleToString(bld.je) + "\n";
			 line = line + "商品条码      商品名称\n";
			 
			 String goods[] = bld.memo.substring(0,bld.memo.length() - 1).split(";");
			 for (int i = 0;i < goods.length;i++)
			 {
				 line = line + goods[i] + "\n";
			 }
			 
			 line = line + 	"---凭本单进行退货,请勿丢失---";
			 //line = line + 	"\n\n持卡人签名: __________________\n\n\n";
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
			 new MessageBox("生成广众签购单失败!\n\n" + ex.getMessage(), null, false);
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
	
	public boolean checkBankSucceed()
	{
       if (bld.retbz == 'N')
       {
    	   errmsg = bld.retmsg;
    	   
    	   if (Integer.parseInt(bld.type) == PaymentBank.XYKCX)
    	   {
    		   return true;
    	   }
    		   
           return false;
       }
       else
       {
           errmsg = "广众交易成功";

           return true;
       }
	 }
	
	private String getSaleType(int type)
	{
		switch (type)
		{
			case PaymentBank.XYKXF:
		    return "消费";	
        	case PaymentBank.XYKTH:
        	return "退货";
        	default:
        		return "未知消费";	
		}
	}
}
