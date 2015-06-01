package bankpay.Bank;

import com.sun.jna.Library;
import com.sun.jna.Native;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
 //浙江金华一百店
public class ICBCZj_PaymentBankFunc extends PaymentBankFunc
{
	protected String bankpath = "C:\\Windows\\ICBC";	
	  
	public String[] getFuncItem()
	  	{
	  		String[] func = new String[7];

	  		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
	  		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
	  		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
	  		func[3] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
	  		func[4] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
	  		func[5] = "[" + PaymentBank.XYKJZ + "]" + "交易结账";
     	  	func[6] = "[" + PaymentBank.XYKCD + "]" + "重打签购单";


	  		return func;
	  	}	  	
	  	
	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		//0-4对应FORM中的5个输入框
		//null表示该不用输入
		switch (type)
		{
			case PaymentBank.XYKXF://消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKCX://消费撤销
				grpLabelStr[0] = "系统检索号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "消费撤销";
				break;
			case PaymentBank.XYKTH://隔日退货
				grpLabelStr[0] = "系统检索号";
				grpLabelStr[1] = "原交易终端号";
				grpLabelStr[2] = "原交易日期";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;   
			case PaymentBank.XYKYE://余额查询    
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "余额查询";
				break;
			case PaymentBank.XYKJZ://余额查询    
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易结账";
				break;
			case PaymentBank.XYKQD://交易签到
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易签到";
				break;
			case PaymentBank.XYKCD://重打签购单
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打签购单";
				break;
				
		}

		return true;
	}
  	
	public boolean getFuncText(int type, String[] grpTextStr)
	{
		//0-4对应FORM中的5个输入框
		//null表示必须用户输入,不为null表示缺省显示无需改变
		switch (type)
		{
			case PaymentBank.XYKXF://消费
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKCX://消费撤销
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKTH://隔日退货
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break; 
			case PaymentBank.XYKQD://交易签到
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易签到";
				break;
			case PaymentBank.XYKJZ://交易结账
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易结账";
				break;
			case PaymentBank.XYKYE://余额查询    
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始余额查询";
				break;
			case PaymentBank.XYKCD://余额查询    
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始重打签购单";
				break;
		}

		return true;
	}
	
  	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
  	{
  		String id = "";
  		String input = "";
          try
          {
              if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) &&
                      (type != PaymentBank.XYKQD) && (type != PaymentBank.XYKYE) 
                      && (type != PaymentBank.XYKCD) && (type != PaymentBank.XYKJZ))
              {
                  errmsg = "银联接口不支持该交易";
                  new MessageBox(errmsg);

                  return false;
              }

              if (PathFile.fileExist(bankpath + "\\request.txt"))
  			{
  				PathFile.deletePath(bankpath + "\\request.txt");
  			}

  			if (PathFile.fileExist(bankpath + "\\result.txt"))
  			{
  				PathFile.deletePath(bankpath + "\\result.txt");
  			}
  			

              // 写入请求数据
  			XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo,new StringBuffer());
              
  			// 调用接口模块
  			if (PathFile.fileExist(bankpath + "\\javaposbank.exe"))
  			{
  				CommonMethod.waitForExec(bankpath + "\\javaposbank.exe ICBC1","javaposbank.exe");
  			}
  			else
  			{
  				new MessageBox("找不到金卡工程模块 javaposbank.exe");
  				XYKSetError("XX", "找不到金卡工程模块 javaposbank.exe");
  				return false;
  			}

              // 读取应答数据
              if (!XYKReadResult())
              {
                  return false;
              }

              // 检查交易是否成功
              XYKCheckRetCode();
              
          	// 打印签购单
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
	  	
   public String XYKWriteRequest(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo, StringBuffer arg)
      	{
      		String line = "";
      		String syyid = Convert.increaseChar(GlobalInfo.posLogin.gh, 6);
      		
      		String id = "";
      		String input = "";
            String seqno;
            String date;
            String zdh;
              
              try{
//	  	  			交易类型
  	              switch (type)
  	              {
  	                  case PaymentBank.XYKXF: //消费
  	                	  id = "1001";
  	                	  String je = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
  	                	  je = Convert.increaseCharForward(je, '0', 12);
  	                  	  input = "AMT1=" + je ;
  	                      break;

  	                  case PaymentBank.XYKCX: //消费撤销
  	                  	   id = "1101";
  	                  	   seqno = Convert.increaseChar(oldseqno, '0',8);
  	                  	   input = "I1=" + seqno;
  	                	  break;

  	                 case PaymentBank.XYKTH: //    
  	                      id = "1102" ;
  	                      seqno = Convert.increaseChar(oldseqno, '0',8);
  	                      date = Convert.increaseChar(olddate, '0', 8);
  	                      zdh = Convert.increaseChar(oldauthno, '0', 3);
  	                      input = "I1=" + seqno + ",I2=" + date + ",I3=" +zdh;
  	                      break;
  	                  case PaymentBank.XYKYE: //余额查询    
  	                       id = "2002";
  	                      break;
  	                  case PaymentBank.XYKQD: //交易签到
  	                       id = "4001";
  	                      break;
  	                case PaymentBank.XYKJZ: //交易结账
	                       id = "4002";
	                      break;
  	                case PaymentBank.XYKCD: //重打签购单
	                       id = "4005";
	                      break;
  	              }
  	            line = id + "," + input + ", " + "," + "," + ",";
  	           }
  	           catch(Exception e)
  	           {
  	              e.printStackTrace();
  	           }
              
      		PrintWriter pw = CommonMethod.writeFile(bankpath + "\\request.txt");

      		if (pw != null)
      		{
      			pw.println(line);
      			pw.flush();
      			pw.close();
      		}
      		
      		return null;
      	}      
          
	          
	  	public boolean XYKReadResult()
	  	{	      	
	  		BufferedReader br = null;
	        try
	        {
				if (!PathFile.fileExist(bankpath + "\\result.txt") || ((br = CommonMethod.readFileGBK(bankpath + "\\result.txt")) == null))
				{
					XYKSetError("XX", "读取金卡工程应答数据失败!");
					new MessageBox("读取金卡工程应答数据失败!", null, false);

					return false;
				}
				String newLine = br.readLine();
				
				String line = newLine;
	            bld.retcode = line.substring(0,2);
	            //System.err.println(line);
	            //System.err.println(bld.retcode);
	            if (!bld.retcode.equals("00"))
	            {
	            	bld.retmsg  = "交易失败";
	            	errmsg = bld.retmsg;
	            	return false;
	            }
	            else
	            {
	            	bld.retmsg = "交易成功";
	            	errmsg = bld.retmsg;
	            }
	            
            	bld.cardno = Convert.newSubString(line, 2, 21);
            	bld.trace = Long.parseLong(Convert.newSubString(line, 35, 41));	           
	            
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
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
	        }
	  	}	  	
 
	  	/*
		public boolean XYKNeedPrintDoc(int type)
		{
			if ((type == PaymentBank.XYKCD) 
					)
		            {
		                
		                return true;
		            }
	       
	       return false;
		}
		
		public void XYKPrintDoc(String rs, int type)
		{
			try
			{
				String trans = "";
				
				
				if (type == 0)
					trans = "消费";
				else if(type == 1)
					trans = "撤销";
				else if(type == 2)
					trans = "退货";
					
				trans = "重打签购单";	
				StringBuffer sb = new StringBuffer("     POS交易签购单（");
				sb.append("#     ------------------------------");
				sb.append("#     商户名称：金华市第一百货集团股份有#     限公司江南店");
				//sb.append("#     商户编号：" + rs.substring(0 ,10));
				sb.append("#     终端编号：" + rs.substring(77, 92));
				sb.append("#     操作员号：" + GlobalInfo.posLogin.gh);
				//sb.append("#     收单银行：" + rs.substring(100, 120));
				sb.append("#     发卡银行：" + rs.substring(100, 120));
				sb.append("#     卡号：" + rs.substring(2, 8) + "******" + rs.substring(14,21));
				sb.append("#     交易：" + trans);
				sb.append("#     卡有效期：" + rs.substring(61, 65));
				sb.append("#     批次号：" + rs.substring(41, 47));
				sb.append("#     凭证号：" + rs.substring(0, 10));
				sb.append("#     参考号：" + rs.substring(53, 61));
				sb.append("#     授权号：" + rs.substring(92, 98));
				sb.append("#     日期时间：" + rs.substring(21, 29) + " " + rs.substring(29, 35));
				sb.append("#     金额：RMB   " + Double.parseDouble(rs.substring(65, 77))/100 );
				sb.append("#     --------------------------------");
				sb.append("#       持卡人签名 ####");
				sb.append("#     --------------------------------");
				sb.append("#     本人确认以上交易，同意将其记入本卡#     账户！");
				rs = sb.toString();
				//byte b[] = rs.getBytes();
				//rs = new String(b, "GBK");
				
				for (int i = 0; i < GlobalInfo.sysPara.bankprint; i++) {
					XYKPrintDoc_Start();
					int  j = 0, k = 0;
					while(true)
					{
						j = rs.indexOf("#", k);
						//查到最后一行时，退出
						if (j == -1) 
							break;
						String s = rs.substring(k, j);
						if (k == 0)
							s += (i < 1 ? "持卡人联）" : "商场存根）");
						if (s == "#")
							s = "";
						XYKPrintDoc_Print(s);
						k = j;
					}
					XYKPrintDoc_End();
				 }
		    }
			catch(Exception e)
			{
				 new MessageBox("打印签购单发生异常\n\n" + e.getMessage());
				e.printStackTrace();
			}
	}
	*/

  }
	
