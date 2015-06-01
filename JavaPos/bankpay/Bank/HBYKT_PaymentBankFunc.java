package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import org.eclipse.swt.widgets.Text;

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

//河北一卡通银联接口
//调用动态库（模块名：HBYKT；动态库(dll文件）：softpos.dll；
public class HBYKT_PaymentBankFunc extends PaymentBankFunc{
	    String path = null;
		public String[] getFuncItem()
	    {
	        String[] func = new String[4];
	        
	        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
	        func[1] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
	        func[2] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
	        func[3] = "[" + PaymentBank.XYKJZ + "]" + "交易结算";
	        
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
	        	case PaymentBank.XYKQD: //签到   
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "交易签到";
	            break;
	        	case PaymentBank.XYKJZ: //结算  
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "交易结算";
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
			 	case PaymentBank.XYKQD: 	//交易签到   
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = "按回车键开始签到";
	            break;
			 	case PaymentBank.XYKJZ: 	//交易结算
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = "按回车键开始结算";
	            break;
			}
			
			return true;
	    }
		public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
		{
			try
			{
				if (!(type == PaymentBank.XYKXF || 
					  type == PaymentBank.XYKQD ||
					  type == PaymentBank.XYKJZ || 
					  type == PaymentBank.XYKYE ))
					{			
						  new MessageBox("银联接口不支持此交易类型！！！");
						  
						  return false;
				    }
				
//				获得金卡文件路径
//				path = "D:\\javapos\\ykt";
				path = getBankPath(paycode);
				
				if (PathFile.fileExist(path + "\\request.txt"))
				{
					PathFile.deletePath(path + "\\request.txt");
					if (PathFile.fileExist(path + "\\request.txt"))
					{
						errmsg = "交易“request.txt”文件删除失败，请重试！！！";
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
						errmsg = "交易“result.txt”文件删除失败，请重试！！！";
						XYKSetError("XX",errmsg);
						new MessageBox(errmsg);
						
						return false;
					}				
				}
				if (PathFile.fileExist(path + "\\print.txt"))
	            {
	                PathFile.deletePath(path + "\\print.txt");
	            }
				
				
	            // 写入请求数据
	            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
	            {
	                return false;
	            }
	            //  调用接口模块
	            if (PathFile.fileExist(path+"\\javaposbank.exe"))
	            {
	            	CommonMethod.waitForExec(path+"\\javaposbank.exe HBYKT","javaposbank");
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
				
	            //打印签购单
				if (XYKNeedPrintDoc(type))
				{
					XYKPrintDoc(type);
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
		
		

		public boolean XYKWriteRequest(int type, double money, String track1,String track2, String track3,String oldseqno, String oldauthno,String olddate, Vector memo)
		{
			 String line = "";
			 PrintWriter pw = null;
			 String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
			 
			 try
			 {
				 switch (type)
				 {
				 	case PaymentBank.XYKXF: 	// 消费
				 		line = String.valueOf(type) + "," + jestr;
	                break;
	                case  PaymentBank.XYKYE: 	// 余额查询
	                	line = String.valueOf(type);
	                break;	
	                case  PaymentBank.XYKQD: 	// 交易签到
	                	line = String.valueOf(type);
	                break;
	                case  PaymentBank.XYKJZ: 	// 交易结算
	                	line = String.valueOf(type);			
	                break;
				 }
		         
		         try
		         {
		            pw = CommonMethod.writeFile(path+"\\request.txt");
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
	
		
		//读取result文件
		public boolean XYKReadResult()
		{
			BufferedReader br = null;
			
			try
	        {
				if (!PathFile.fileExist(path+"\\result.txt") || ((br = CommonMethod.readFileGBK(path+"\\result.txt")) == null))
	            {
	            	XYKSetError("XX","读取应答失败,交易失败!");
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
	            
	            bld.memo		= result[0];
	            
	            int type = Integer.parseInt(bld.type.trim());
	             
	            if (result.length >= 2)
	            {
	    			//当银行返回字符以字节计算，而字符中汉字出现导致line的长度和字节书不等时，汉字后面的内容倒着计算位置
	    			// int len = line.length();
	    			//防止字节和字符不一致问题
	    			//bld.memo = Convert.newSubString(line, 2, 21);
	            	bld.retcode  = result[1].substring(0,2);  //返回码2  0，xxxxxxxxxxxxxx 前面的0是调用函数的返回值
	            	if(!bld.retcode.equals("00"))
	            	{
	            		bld.retmsg =bld.retcode + ": "+ switchErrorMsg(bld.retcode); 
	            		return false;
	            	}
	            	if(type == PaymentBank.XYKXF)
	            	{
	                	bld.cardno = result[1].substring(2,18);   //卡号16
	                	bld.trace = Convert.toInt((result[1].substring(30,36)));   //流水号6
	                	bld.je = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Convert.toDouble((result[1].substring(22,30)) ),100),2,1);   //交易金额	
	                	bld.crc = result[1].substring(36,45); //保存9位终端号 
	                	bld.oldrq = result[1].substring(45, 59); //日期时间
	                	//bld.memo1= "xxxx";
	            	}  	
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
		public boolean XYKCheckRetCode()
		{
			if (bld.memo.trim().equals("0") && bld.retcode.trim().equals("00")) //函数返回值为0 ，并且返回串中 “00” 则交易成功
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
		
		public boolean XYKNeedPrintDoc(int type)
		{
			if (!checkBankSucceed())
		    {
		        return false;
		    }
			if (  type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || 
				  type == PaymentBank.XYKTH || type == PaymentBank.XYKJZ || 
				  type == PaymentBank.XYKCD)
			{
				return true;
			}
			else
				return false;
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
		
		
		public void XYKPrintDoc(int type)
		{
			ProgressBox pb = null;
			String name = null;

			name =path + "\\print.txt";


			try
			{
				if (!PathFile.fileExist(name))
				{
					if(type == PaymentBank.XYKJZ)
					{
						new MessageBox("找不到结算单打印文件！！！");
					}else
					{
						new MessageBox("找不到签购单打印文件！！！");
					}
					
					return ;
				}
				pb = new ProgressBox();
				pb.setText("正在打印,请等待..." + "\t OY : " + GlobalInfo.sysPara.issetprinter);
				
				for (int i = 0; i < GlobalInfo.sysPara.bankprint; i ++)
				{
					BufferedReader br = null;
					XYKPrintDoc_Start();
					try
					{
						br = CommonMethod.readFileGB2312(name);
						if (br == null)
						{							
							if(type == PaymentBank.XYKJZ)
							{
								new MessageBox("打开结算单文件失败");
							}else
							{
								new MessageBox("打开签购单文件失败");
							}
							
							return ;
						}
						
						String line = null;
						while ((line = br.readLine()) != null)
						{
							if (line.length() <= 0)
								continue;
							//银行签购单模板添加 "CUTPAPER" 标记
							//当程序里面读取到这个字符是，打印机切纸
//							if (line.indexOf("CUTPAPER") >= 0)
							if (line.trim().equals("CUTPAPER"))
							{
								XYKPrintDoc_End();
								new MessageBox("请撕下客户签购单！！！");
								
								continue;
							}
							
							XYKPrintDoc_Print(line);
						}					
					}
					catch(Exception e)
					{
						new MessageBox(e.getMessage());
					}
					finally
					{
						if (br != null)
						try
						{
							br.close();
						}
						catch(IOException ie)
						{
							ie.printStackTrace();
						}					
					}
					XYKPrintDoc_End();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				if(type == PaymentBank.XYKJZ)
				{
					new MessageBox("打印结算单异常!!!\n" + e.getMessage());
				}else
				{
					new MessageBox("打印签购单异常!!!\n" + e.getMessage());
				}			
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
		
		public String switchErrorMsg(String retcode){
			
			if(retcode.equals("00")){
				bld.retmsg = "交易成功";
			}else if(retcode.equals("E1")){
				bld.retmsg = "交易被取消";
			}else if(retcode.equals("E2")){
				bld.retmsg = "串口打开错误";
			}else if(retcode.equals("E3")){
				bld.retmsg = "网络连接错误";
			}else if(retcode.equals("E4")){
				bld.retmsg = "未签到";
			}else if(retcode.equals("E5")){
				bld.retmsg = "由于网络原因，账户扣款状态异常";
			}else if(retcode.equals("E6")){
				bld.retmsg = "账户余额查询失败";
			}else if(retcode.equals("E7")){
				bld.retmsg = "结算成功，但签退失败";
			}else if(retcode.equals("E8")){
				bld.retmsg = "下载参数文件失败";
			}else if(retcode.equals("E9")){
				bld.retmsg = "下载黑名单文件失败";
			}else if(retcode.equals("EA")){
				bld.retmsg = "未签退";
			}else if(retcode.equals("06")){
				bld.retmsg = "POS机出错";
			}else if(retcode.equals("0F")){
				bld.retmsg = "没有密钥卡";
			}else if(retcode.equals("30")){
				bld.retmsg = "格式错误";
			}else if(retcode.equals("41")){
				bld.retmsg = "此卡为挂失卡";
			}else if(retcode.equals("42")){
				bld.retmsg = "无此账户";
			}else if(retcode.equals("43")){
				bld.retmsg = "此卡为被窃卡";
			}else if(retcode.equals("54")){
				bld.retmsg = "此卡为过期卡";
			}else if(retcode.equals("62")){
				bld.retmsg = "此卡为受限制的卡";
			}else if(retcode.equals("72")){
				bld.retmsg = "余额不足";
			}else if(retcode.equals("A0")){
				bld.retmsg = "MAC鉴别失败";
			}else if(retcode.equals("D7")){
				bld.retmsg = "坏卡";
			}
			
			return  bld.retmsg;
		}

	}
