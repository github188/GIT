package custom.localize.Zspj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.swt.events.KeyEvent;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Zspj_PaymentDzj extends PaymentMzk
{
	private static String Host;
	private static String Port;
	private static String Timeout;
	private static String Groupno;
	private static String Shopno;
	private static String PosNo;
	private static String Linktype;
	public static String Path;
	private static String ShopperNo;
	private static String iniStr;
	private static String retOldseqno;//交易流水号
	static String retCardType="";//卡状态
	private static double retJe;//金额  yebk 原来为Double类型，现在修改为double类型，1.4的编译环境有区别
	private static int index = -1;
	private static String retShopperSponsor; //购物券发行商

	// 武汉中商电子劵
	public Zspj_PaymentDzj()
	{
		super();
	}
	
	public Zspj_PaymentDzj(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode,sale);
	}
	
	public Zspj_PaymentDzj(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay,head);
	}
	
	
	public SalePayDef inputPay(String money)
	{
		try
		{
			// 退货小票不能使用,退货扣回按销售算
			if (checkMzkIsBackMoney() && GlobalInfo.sysPara.thmzk != 'Y')
			{
				new MessageBox("退货时不能使用" + paymode.name);
				return null;
			}

			// 先检查是否有冲正未发送
			if (!sendAccountCz())
				return null;

			// 打开明细输入窗口
			new Zspj_PaymentDzjForm().open(this, saleBS);

			// 如果付款成功,则salepay已在窗口中生成
			return salepay;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return null;
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
			/*
			// 设置用户输入密码
			StringBuffer passwd = new StringBuffer();
			if (!getPasswdBeforeFindMzk(passwd))
			{
				return false;
			}
			else
			{
				mzkreq.passwd = passwd.toString();
			}*/
			
			return Execute(mzkreq, mzkret,'0',String.valueOf(saleBS.saleHead.ysje));
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
			
			/*// 设置用户输入密码
			StringBuffer passwd = new StringBuffer();
			if (!getPasswdBeforeFindMzk(passwd))
			{
				return false;
			}
			else
			{
				mzkreq.passwd = passwd.toString();
			}*/
			
			//加载配置信息
			LoadConfigSet();
			
			//
			return Execute(mzkreq, mzkret,'0',String.valueOf(saleBS.saleHead.ysje));
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
			
			/*// 发送交易请求
			if (!Execute(mzkreq,mzkret,'0')) 
			{
				if (paynoMsrflag) 
				{
					salepay.payno = "";
					continue;
				}
				return false;
			}*/
	
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
	
	public static boolean LoadConfigSet()
	{
		BufferedReader br;
		try
		{

			// 读取ZsdzjConfig.ini
			br = CommonMethod.readFile("C:\\JavaPos\\ZsdzjConfig.ini");

			String line;
			String[] sp;
			
			if ((line = br.readLine()) == null)
		    {
				new MessageBox("配置文件数据为空!", null, false);
			    return false;
		    }

			while ((line = br.readLine()) != null)
			{
				if ((line == null) || (line.length() <= 0))
				{
					continue;
				}

				String[] lines = line.split("&&");
				sp = lines[0].split("=");
				if (sp.length < 2)
					continue;

				if (sp[0].trim().compareToIgnoreCase("Host") == 0)
				{
					Host = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("Port") == 0)
				{
					Port = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("Timeout") == 0)
				{
					Timeout = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("Groupno") == 0)
				{
					Groupno = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("Shopno") == 0)
				{
					Shopno = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("PosNo") == 0)
				{
					PosNo = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("Linktype") == 0)
				{
					Linktype = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("Path") == 0)
				{
					Path = sp[1].trim();
				}
			}
			
		}
		catch (Exception ex)
	       {
	           new MessageBox("读取配置文件失败!" + ex.getMessage(), null, false);
	           ex.printStackTrace();

	           return false;
	       }
		
		return true;
	}
	
	//写入查余请求数据
	public boolean QueryWriteRequest(MzkRequestDef req, MzkResultDef ret,String money)
	{
		try
		{
			
			 String line = "";
			 
	         String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(Double.parseDouble(money) * 100,2,1));
	         
	         //	 根据不同的类型生成文本结构
	        /* if (req.type.equals("01"))
	        {*/		 //消费
	        		
	         ShopperNo = req.track2;
	         
	         if(PosNo == null) PosNo = req.syjh;
	        
	         //初始化数据
	         iniStr = "0," + Host + "," + Port + "," + Timeout + "," + Groupno + "," + Shopno + "," + PosNo + "," + Linktype;
	         
	         line = iniStr + "," + req.fphm + "," + req.syyh + "," + ShopperNo + "," + jestr + ",32";
	         
	       /* }
	         else if (req.type.equals("05"))
	         {		 //余额
	        		 line = "0,"+req.track2+"," + mzkreq.passwd;
	         }
	         else if (req.type.equals("05"))*/
	         
	         PrintWriter pw = null;
	            
	         try
	         {
	            pw = CommonMethod.writeFile(Path+"\\request.txt");
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
			new MessageBox("写入电子劵请求数据异常!\n\n" + ex.getMessage(), null, false);
	        ex.printStackTrace();
	         
	        return false;
		}
	}
	
	//读取查余返回数据
	public boolean QueryReadResult(MzkRequestDef req, MzkResultDef ret)
	{
		
	   BufferedReader br = null;
	
	   try
	   {
		   if (!PathFile.fileExist(Path+"\\result.txt") || ((br = CommonMethod.readFileGBK(Path+"\\result.txt")) == null))
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
	       
	       if (!result[0].equals("0"))
	       {
	    	   String err = result.length>=2?result[2]:"查询失败！";
  	    	   new MessageBox(err, null, false);
	
	      	   return false;
	       }
	       
	       //retJe = Double.valueOf(result[1])/100;
	       retJe = Double.parseDouble(result[1])/100; //yebk 原来的在1.4下有问题
	       retCardType = result[2];
	       if(ShopperNo == null){ShopperNo = "";}
	       if (result.length > 1) mzkret.cardno = ShopperNo;
	       if (result.length > 2) mzkret.ye = Convert.toDouble(result[1])/100;
	       if(result.length > 3) retShopperSponsor = result[3];
	       if(result.length > 4) retOldseqno = result[4];
		   

			index++;
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
	           }
	           catch (IOException e)
	           {
	               e.printStackTrace();
	           }
	       }
	   }
	}

	
	public boolean Execute(MzkRequestDef req, MzkResultDef ret,char type,String money)
	{
		try
		{

	   		 if (PathFile.fileExist(Path+"\\request.txt"))
		            {
		                PathFile.deletePath(Path+"\\request.txt");
		            }
	
	   		
	   		 if (PathFile.fileExist(Path+"\\result.txt"))
		            {
		                PathFile.deletePath(Path+"\\result.txt");
		            }
			if(type == '0')
			{
	            //  写入查余请求数据
	            if (!QueryWriteRequest(req,ret,money))
	            {
	                return false;
	            }
	        }
	        else if(type == 'Y')
	        {
	        	 //  写入确认请求数据
	            if (!ConfirmWriteRequest(req,ret,money))
	            {
	                return false;
	            }
	        }
	        else if(type == 'N')
	        {
	        	//  写入取消请求数据
	            if (!CancelWriteRequest(req,ret))
	            {
	                return false;
	            }
	        }

            // 调用接口模块
            if (PathFile.fileExist(Path+"\\javaposbank.exe"))
            {
            	CommonMethod.waitForExec(Path+"\\javaposbank.exe ZSDZJ");
            }
            else
            {
                new MessageBox("找不到付款模块 javaposbank.exe");
                return false;
            }
            
            if (PathFile.fileExist(Path+"\\result.txt"))
            {

				if(type == '0')
				{
		            // 读取查余应答数据
		            if (!QueryReadResult(req,ret))
		            {
		                return false;
		            }
				}
	            else if(type == 'Y')
		        {
	            	 //  读取确认应答数据
		            if (!ConfirmReadResult(req,ret))
		            {
		                return false;
		            }
		        }
		        else if(type == 'N')
		        {
		        	//读取取消应答数据
		            if (!CancelReadResult(req,ret))
		            {
		                return false;
		            }
		        }
            }
            else
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
	
	//写入取消请求数据
	public static boolean CancelWriteRequest(MzkRequestDef req, MzkResultDef ret) {
		try
		{
			 String line = "";
	       
//	         ShopperNo = req.track2;
	         
			 if(req == null && index < 0)return false;
			 
	         if(PosNo == null) PosNo = GlobalInfo.syjStatus.syjh;
	        
	         //初始化数据
	         iniStr = "N," + Host + "," + Port + "," + Timeout + "," + Groupno + "," + Shopno + "," + PosNo + "," + Linktype;
	         
	         line = iniStr + "," + GlobalInfo.syjStatus.fphm + "," + GlobalInfo.posLogin.gh + "," + ShopperNo + "," + retOldseqno;
	         
	         PrintWriter pw = null;
	         PrintWriter pwlog = null;
	            
	         try
	         {
	            pw = CommonMethod.writeFile(Path+"\\request.txt");
	            pwlog = CommonMethod.writeFile(Path+"\\dzjLog.log");
	            if (pw != null)
	            {
	                pw.println(line);
	                pw.flush();
	            }
	            if (pwlog != null)
	            {
	            	pwlog.println("撤销日志："+line);
	            	pwlog.flush();
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
			new MessageBox("写入电子劵请求数据异常!\n\n" + ex.getMessage(), null, false);
	        ex.printStackTrace();
	         
	        return false;
		}
	}

	//读取取消返回数据
	public static boolean CancelReadResult(MzkRequestDef req, MzkResultDef ret) {
		
		BufferedReader br = null;
		
		   try
		   {
			   if (!PathFile.fileExist(Path+"\\result.txt") || ((br = CommonMethod.readFileGBK(Path+"\\result.txt")) == null))
		       {
		       		new MessageBox("读取面值卡应答数据失败!", null, false);
		
		       		return false;
		       }
			   
			   String line = br.readLine();
		
		       if (line.length() <= 0)
		       {
		    	   new MessageBox("面值卡应答数据为空!");
		           return false;
		       }
		       
		       String result[] = line.split(",");
		       
		       if (result[0].equals("1"))
		       {
		    	   String err = result.length>=1?result[1]:"处理失败！";
		    	   new MessageBox(err, null, false);
		
		      	   return false;
		       }
		       else
		       {
		    	   new MessageBox("撤销执行成功！");
		       }

		        index = -1;
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
		           }
		           catch (IOException e)
		           {
		               e.printStackTrace();
		           }
		       }
		   }
	}
	
	//读取确认返回数据
	private boolean ConfirmReadResult(MzkRequestDef req, MzkResultDef ret) {
		
		BufferedReader br = null;
		
		
		   try
		   {
			   if (!PathFile.fileExist(Path+"\\result.txt") || ((br = CommonMethod.readFileGBK(Path+"\\result.txt")) == null))
		       {
		       		new MessageBox("读取面值卡应答数据失败!", null, false);
		
		       		return false;
		       }
			   
			   String line = br.readLine();
		
		       if (line.length() <= 0)
		       {
		    	   new MessageBox("面值卡应答数据为空!");
		           return false;
		       }
		       
		       String result[] = line.split(",");
		       
		       if (!result[0].equals("0"))
		       {
		    	   String err = result.length>=1?result[1]:"处理失败！";
		    	   new MessageBox(err, null, false);
		
		      	   return false;
		       }
		       
		       
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
		           }
		           catch (IOException e)
		           {
		               e.printStackTrace();
		           }
		       }
		   }
	}

	//写入确认请求数据
	private boolean ConfirmWriteRequest(MzkRequestDef req, MzkResultDef ret,String money) {
		try
		{
			 String line = "";
			 
	         String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(Double.parseDouble(money) * 100,2,1));
	       
//	         ShopperNo = req.track2;
	         
	         if(PosNo == null) PosNo = req.syjh;
	        
	         //初始化数据
	         iniStr = "Y," + Host + "," + Port + "," + Timeout + "," + Groupno + "," + Shopno + "," + PosNo + "," + Linktype;
	         
	         line = iniStr + "," + GlobalInfo.syjStatus.fphm + "," + GlobalInfo.posLogin.gh + "," + ShopperNo + "," +jestr+","+ retOldseqno;
	         
	         PrintWriter pw = null;
	            
	         try
	         {
	            pw = CommonMethod.writeFile(Path+"\\request.txt");
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
			new MessageBox("写入电子劵请求数据异常!\n\n" + ex.getMessage(), null, false);
	        ex.printStackTrace();
	         
	        return false;
		}
	}

	
	public boolean createSalePay(String money)
	{
		if(new MessageBox("你确定要消费吗?", null, true).verify() == GlobalVar.Key1)
		{
			if(Execute(mzkreq, mzkret,'Y',money))
			{
				if(super.createSalePay(money))
				{
					if(retCardType.equals("A01"))
			        {
						mzkret.ye = 0.00;
			        }
			        else if(retCardType.equals("B01"))
			        {
			        	mzkret.ye = mzkret.ye-Double.parseDouble(money);
			        }
					salepay.kye = mzkret.ye;
					salepay.idno = retCardType+","+retOldseqno+","+retShopperSponsor;
					return true;
				}
			}
		}
		else
		{
			if(Execute(mzkreq, mzkret,'N',money))
			{
				Zspj_PaymentDzjEvent.shell.close();
				Zspj_PaymentDzjEvent.shell.dispose();
			}
		}
		return false;
	}
	
	
	public void showAccountYeMsg()
	{
		
		if(retJe > 0)
		{
	          if(retCardType.equals("B01"))
	           {
				if (!messDisplay)
					return;
		
				StringBuffer info = new StringBuffer();
		
				String text = Language.apply("付");
				double ye = getAccountYe() - salepay.je;
				if (checkMzkIsBackMoney())
				{
					text = Language.apply("退");
					ye = getAccountYe() + salepay.je;
				}
		//		info.append("卡内余额为: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(getAccountYe()), 0, 12, 12, 1) + "\n");
				info.append(Language.apply("卡内余额为: {0}\n" ,new Object[]{Convert.appendStringSize("", ManipulatePrecision.doubleToString(getAccountYe()), 0, 12, 12, 1)}));
				info.append(Language.apply("本次") + text + Language.apply("款额: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(salepay.je), 0, 12, 12, 1) + "\n");
				if (ye > 0)
					info.append(text + Language.apply("款后余额: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(ye), 0, 12, 12, 1) + "\n");
		
				new MessageBox(info.toString());

	           }
		}
	}


	protected String getDisplayAccountInfo()
	{
		return "卡      号";
	}
	
	protected boolean needFindAccount()
	{
		return true;
	}
	
	public void specialDeal(Zspj_PaymentDzjEvent event)
	{
	}
	
	protected String getDisplayStatusInfo()
	{
		try
		{
			String line = "";
			if(retJe > 0)
			{
				 if(retCardType.equals("A01"))
		           {
					 line = "一次性消费劵！";
		           }
		           else if(retCardType.equals("B01"))
		           {
		        	 line = "多次性消费卡！";
		           }
		          
			}

			return line;
		}
		catch (Exception er)
		{
			er.printStackTrace();
			return "";
		}
	}
	
	public void setMoneyVisible(Zspj_PaymentDzjEvent paymentDzjEvent)
	{
	}
	
	public void setPwdAndYe(Zspj_PaymentDzjEvent event, KeyEvent e)
	{
		if (isPasswdInput())
		{
			// 显示密码
			event.yeTips.setText(getPasswdLabel());
			event.yeTxt.setVisible(false);
			event.pwdTxt.setVisible(true);
			event.yeTxt.setText(ManipulatePrecision.doubleToString(getAccountYe()));

			if (e != null)
				e.data = "focus";
			event.pwdTxt.setFocus();
			event.pwdTxt.selectAll();
		}
		else
		{
			// 显示余额
			event.yeTips.setText("账户余额");
			event.yeTxt.setVisible(true);
			event.pwdTxt.setVisible(false);
			event.yeTxt.setText(ManipulatePrecision.doubleToString(getAccountYe()));

			// 输入金额
			if (e != null)
				e.data = "focus";
			event.moneyTxt.setFocus();
			event.moneyTxt.selectAll();
		}
	}
	
	public void doAfterFail(Zspj_PaymentDzjEvent dzjEvent)
	{
		dzjEvent.shell.close();
		dzjEvent.shell.dispose();
	}
	
	public boolean cancelPay()
	{
		// 撤销交易
		try
		{
					if (new MessageBox("存在需要线下途径撤销交易的付款方式，确定取消吗？", null, true).verify() == GlobalVar.Key1)
					{
						return true;
					}

			return false;
		}
		catch (Exception er)
		{
			new MessageBox(er.getMessage());
			return false;
		}
	}
	
	
}
