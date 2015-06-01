package bankpay.Payment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class AjMzk_PaymentMzk extends PaymentMzk
{
	// 爱家保源IC卡
	public AjMzk_PaymentMzk()
	{
		super();
	}
	
	public AjMzk_PaymentMzk(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode,sale);
	}
	
	public AjMzk_PaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay,head);
	}

	protected boolean getPasswdBeforeFindMzk(StringBuffer passwd)
	{
    	TextBox txt = new TextBox();
    	
        if (!txt.open("请输入密码", "PASSWORD", "请输入卡密码,没有密码按退出键!", passwd, 0, 0,false, TextBox.AllInput))
        {
            return true;
        }
        
	    return true;
	}
	
	public boolean findMzkInfo(String track1, String track2, String track3)
	{
		if ((track1 == null || track1.trim().length() <= 0) && 
				(track2 == null || track2.trim().length() <= 0) && 
				(track3 == null || track3.trim().length() <=0))
			{
				new MessageBox("磁道数据为空!");
				return false;
			}
			
			// 解析磁道
			String[] s = parseTrack(track1,track2,track3);
			if (s == null) return false;
			track1 = s[0];
			track2 = s[1];
			track3 = s[2];
			
			// 设置请求数据
			setRequestDataByFind(track1,track2,track3);

			// 设置用户输入密码
			StringBuffer passwd = new StringBuffer();
			if (!getPasswdBeforeFindMzk(passwd))
			{
				return false;
			}
			else
			{
				mzkreq.passwd = passwd.toString();
			}
			
			return XYKExecute(mzkreq, mzkret);
	}
	
	public boolean findMzk(String track1, String track2, String track3)
	{
		if (!SellType.ISSALE(salehead.djlb))
		{
			new MessageBox("该付款只支持消费交易!");
			return false;
		}
		
		if ((track1 == null || track1.trim().length() <= 0) && 
				(track2 == null || track2.trim().length() <= 0) && 
				(track3 == null || track3.trim().length() <=0))
			{
				new MessageBox("磁道数据为空!");
				return false;
			}
			
			// 解析磁道
			String[] s = parseTrack(track1,track2,track3);
			if (s == null) return false;
			track1 = s[0];
			track2 = s[1];
			track3 = s[2];
			
			// 设置请求数据
			setRequestDataByFind(track1,track2,track3);
			
			// 设置用户输入密码
			StringBuffer passwd = new StringBuffer();
			if (!getPasswdBeforeFindMzk(passwd))
			{
				return false;
			}
			else
			{
				mzkreq.passwd = passwd.toString();
			}
			
			//
			return XYKExecute(mzkreq, mzkret);
	}
	
	public boolean mzkAccount(boolean isAccount)
	{	
		do 
		{
			// 退货交易卡号为空时提示刷卡
			paynoMsrflag = false;
			if (!paynoMSR()) return false;
			
			// 设置交易类型,isAccount=true是记账,false是撤销
			if (isAccount)
			{
				if (SellType.SELLSIGN(salehead.djlb) > 0) mzkreq.type = "01";	// 消费,减
				else mzkreq.type = "03";										// 退货,加
			}
			else
			{
				if (SellType.SELLSIGN(salehead.djlb) > 0) mzkreq.type = "03";	// 退货,加
				else mzkreq.type = "01";										// 消费,减
			}
			
			// 保存交易数据进行交易
			if (!setRequestDataByAccount()) 
			{
				if (paynoMsrflag) 
				{
					salepay.payno = "";
					continue;
				}
				return false;
			}
			
			// 先写冲正文件
			if (!writeMzkCz()) 
			{
				if (paynoMsrflag) 
				{
					salepay.payno = "";
					continue;
				}
				return false;
			}
	
			// 记录面值卡交易日志
			BankLogDef bld = mzkAccountLog(false,null,mzkreq,mzkret);
			
			// 发送交易请求
			if (!XYKExecute(mzkreq,mzkret)) 
			{
				if (paynoMsrflag) 
				{
					salepay.payno = "";
					continue;
				}
				return false;
			}
	
			// 记录应答信息, batch标记本付款方式已记账,这很重要
			saveAccountMzkResultToSalePay();
					
			// 记账完成操作,可用于记录记账日志或其他操作
			return mzkAccountFinish(isAccount,bld);
		} while(true);
	}
	
	public boolean writeMzkCz()
	{
    	return true;
	}
	
	public boolean XYKWriteRequest(MzkRequestDef req, MzkResultDef ret)
	{
		try
		{
			 String line = "";
			 
	         String jestr = String.valueOf(ManipulatePrecision.doubleConvert(req.je,2,1));
	         
	         //	 根据不同的类型生成文本结构
	         if (req.type.equals("01"))
	        {		 //消费
	        		 String cardno = this.mzkret.cardno;
	        		 line = "1," + GlobalInfo.sysPara.mktcode + "," + saleBS.saleHead.fphm + "," + saleBS.saleHead.syyh + "," + cardno + ":" + jestr + ",储值卡" + ",";
	        }
	         else if (req.type.equals("05"))
	         {		 //余额
	        		 line = "0,"+req.track2+"," + mzkreq.passwd;
	         }
	         
	         PrintWriter pw = null;
	            
	         try
	         {
	            pw = CommonMethod.writeFile("c:\\JavaPOS\\mzkcard\\request.txt");
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
			new MessageBox("写入面值卡请求数据异常!\n\n" + ex.getMessage(), null, false);
	        ex.printStackTrace();
	         
	        return false;
		}
	}
	
	public boolean XYKExecute(MzkRequestDef req, MzkResultDef ret)
	{
		try
		{
            //  写入请求数据
            if (!XYKWriteRequest(req,ret))
            {
                return false;
            }
            
            // 调用接口模块
            if (PathFile.fileExist("c:\\JavaPOS\\mzkcard\\javaposbank.exe"))
            {
            	CommonMethod.waitForExec("c:\\JavaPOS\\mzkcard\\javaposbank.exe AJMZKMSR");
            }
            else
            {
                new MessageBox("找不到付款模块 javaposbank.exe");
                return false;
            }
            
            // 读取应答数据
            if (!XYKReadResult(req,ret))
            {
                return false;
            }
                
            
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
            new MessageBox("调用面值卡处理模块异常!\n\n" + ex.getMessage(), null, false);
			return false;
		}
	}

	public boolean XYKReadResult(MzkRequestDef req, MzkResultDef ret)
	{
		
       BufferedReader br = null;
       
       try
       {
    	   mzkret.cardname = "";
    	   mzkret.ye = 0;
    	   if (!PathFile.fileExist("c:\\JavaPOS\\mzkcard\\result.txt") || ((br = CommonMethod.readFileGBK("c:\\JavaPOS\\mzkcard\\result.txt")) == null))
           {
           		new MessageBox("读取面值卡应答数据失败!", null, false);

           		return false;
           }
    	   
    	   String line = br.readLine();

           if (line.length() <= 0)
           {
               return false;
           }
           
           String result[] = line.split(",");
           if (result.length <=0)
           {
        	   new MessageBox("面值卡应答数据为空!", null, false);

          	   return false;
           }
           
           if (!result[0].equals("00"))
           {
        	   String err = result.length>0?result[1]:"";
        	   new MessageBox("查找面值卡失败!" + err, null, false);

          	   return false;
           }
           
           if (result.length > 1) mzkret.cardno = result[1];
           if (result.length > 2) mzkret.ye = Convert.toDouble(result[2]);
		   
    	   return true;
       }
       catch (Exception ex)
       {
           new MessageBox("读取面值卡应答数据异常!" + ex.getMessage(), null, false);
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
                   /*
                   if (PathFile.fileExist("c:\\JavaPOS\\mzkcard\\request.txt"))
		            {
		                PathFile.deletePath("c:\\JavaPOS\\mzkcard\\request.txt");
		            }
					
					if (PathFile.fileExist("c:\\JavaPOS\\mzkcard\\result.txt"))
		            {
		                PathFile.deletePath("c:\\JavaPOS\\mzkcard\\result.txt");
		            }
		            */
               }
               catch (IOException e)
               {
                   e.printStackTrace();
               }
           }
       }
	}
}
