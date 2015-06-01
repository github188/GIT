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

//海印又一城商务有限公司银联接口(无界面)
//调用动态库（模块名：HY；动态库(dll文件）：Proxydll.dll ；函数：int PosProxyDoTrade(PosProxyHandle proxyhandle, TradeReq *ptradereq,  TradeRsp *ptradersp)）
public class HY_PaymentBankFunc extends PaymentBankFunc{
	    String path = null;
//	    String szCommPort=null;
//	    String szComTimeOut=null;
//	    String szComTestTime=null;
//	    String szServerIP=null;
//	    String szServerPort=null;
//		String s=null;
	    
		public String[] getFuncItem()
	    {
	        String[] func = new String[6];
	        
	        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
	        func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
	        func[2] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
//	        func[3] = "[" + PaymentBank.XKQT1 + "]" + "微信支付";
	        func[3] = "[" + PaymentBank.XKQT2 + "]" + "联机初始化";
	        func[4] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
	        func[5] = "[" + PaymentBank.XYKJZ + "]" + "交易结算";
	        
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
	                grpLabelStr[1] = "流水号";
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
//	        	case PaymentBank.XKQT1: //微信支付
//	                grpLabelStr[0] = null;
//	                grpLabelStr[1] = null;
//	                grpLabelStr[2] = null;
//	                grpLabelStr[3] = null;
//	                grpLabelStr[4] = "交易金额";
//	            break;
	        	case PaymentBank.XKQT2: //联机初始化
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "联机初始化";
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
			 	case PaymentBank.XYKCX: 	// 消费撤销
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
//			 	case PaymentBank.XKQT1: 	//微信支付    
//	                grpTextStr[0] = null;
//	                grpTextStr[1] = null;
//	                grpTextStr[2] = null;
//	                grpTextStr[3] = null;
//	                grpTextStr[4] = null;
//	            break;
			 	case PaymentBank.XKQT2: 	//联机初始化   
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = "按回车键开始联机";
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
					  type == PaymentBank.XYKCX || 
					  type == PaymentBank.XYKQD ||
					  type == PaymentBank.XYKJZ || 
					  type == PaymentBank.XYKYE ||
					  type == PaymentBank.XKQT2))
//					  ||type == PaymentBank.XKQT1 
					  
					{			
						  new MessageBox("银联接口不支持此交易类型！！！");
						  
						  return false;
				    }
				
//				获得金卡文件路径
	   		    path = ConfigClass.BankPath;
//				path = getBankPath(paycode);
				
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
				if (PathFile.fileExist(path + "\\Print1.txt"))
	            {
	                PathFile.deletePath(path + "\\Print1.txt");
	            }
				
	            // 写入请求数据
	            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
	            {
	                return false;
	            }         
	            //  调用接口模块
	            if (PathFile.fileExist(path+"\\javaposbank.exe"))
	            {
	            	CommonMethod.waitForExec(path+"\\javaposbank.exe HY","javaposbank");
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
			 String type1 = "";
			 PrintWriter pw = null;
			 
			 try
			 {
				 switch (type)
				 {
				 	case PaymentBank.XYKXF: 	// 消费
	                    type1 = "1";
	                break;
				 	case PaymentBank.XYKCX: 	// 消费撤销
				 		type1 = "2";
				 	break;	
	                case  PaymentBank.XYKYE: 	// 余额查询
	                	type1 = "3";
	                break;	
//	                case  PaymentBank.XKQT1: 	// 微信支付
//	                	type1 = "5";
//	                break;
	                case  PaymentBank.XKQT2: 	// 联机初始化
	                	type1 = "6";
	                break;
	                case  PaymentBank.XYKQD: 	// 交易签到
	                	type1 = "8";
	                break;
	                default:
	                    type1 = "7";			// 交易结算
	                break;
				 }

		         
				 
				 
				 String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
		         jestr = Convert.increaseCharForward(jestr,'0',12);
		         
		         String payType = "1";
		         
		         String date = Convert.increaseChar(olddate, '0', 8); //交易日期
		                
		         String authno = Convert.increaseCharForward(oldauthno, '0', 6); //流水号   
		         
		         String szCashTraceNo = Convert.increaseChar("", '0', 20); //收银机流水单号
		         
		         String szAuthCode = Convert.increaseChar("", '0', 6); //授权码
		         
//		         if(ReadConfig())
//		         {
		        	 //交易类型 + 支付方式 + 交易金额 + 收银机流水单号 + 原交易流水号 + 原交易日期 + 授权码
		        	 line = type1 + "," + payType + "," + jestr + "," + szCashTraceNo + "," + authno + "," + date + "," + szAuthCode;
//		        }
//		         else
//		        	 return false;
		        	 	         	         
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
	            	XYKSetError("XX","读取金卡工程应答数据失败!");
	                new MessageBox("读取金卡工程应答数据失败!", null, false);
	                
	                return false;
	            }
				
				String line = br.readLine();

	            if (line == null || line.length() <= 0)
	            {
	                return false;
	            }
	            
				String[] result = line.split(",");
				if(result.length < 2 )
				{
					errmsg = "返回数据错误!";
					return false;
				}
             
	            int type = Integer.parseInt(bld.type.trim());

	            	bld.retcode  = result[1];  //返回码2
	            	if( !(type == PaymentBank.XYKYE || type == PaymentBank.XYKQD || type == PaymentBank.XKQT2 || type == PaymentBank.XYKJZ))
	            	{
		            	bld.memo = result[2];  //收银机流水单号20
		            	bld.cardno = result[5];   //卡号20
		            	bld.je = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Double.parseDouble(result[6] ),100),2,1);   //交易金额	12
		            	bld.authno = result[7];//系统参考号12
		            	bld.trace = Integer.parseInt(result[8]); //流水号6
		            	bld.memo1 = result[9]; //授权号6
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
		
		public boolean XYKNeedPrintDoc(int type)
		{
			if (!checkBankSucceed())
		    {
		        return false;
		    }
			if (  type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || 
				  type == PaymentBank.XYKJZ )
//				  || type == PaymentBank.XKQT1
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
		
		
		public boolean checkDate(Text date)
		{
			String d = date.getText();
			if (d.length() > 8)
			{
				new MessageBox("日期格式错误\n日期格式《MMDD》");
				return false;
			}
			
			return true;
		}
		
				
		public void XYKPrintDoc(int type)
		{
			ProgressBox pb = null;
			String name = null;
			String name2 = null;
			if(type == PaymentBank.XYKJZ)
			{
				name =path + "\\Settle.txt";
			}
			else{
				name =path + "\\Print1.txt";
				name2 =path + "\\Print2.txt";
			}

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
							if (line.equals("CUTPAPER"))
							{
		            			 ///////////////////两联签购单之间走7个空行////////////////////		 
		            			 for (int j = 1;j <= 7;j++)
		            			 {
		            				 XYKPrintDoc_Print("\n");
		            			 }
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
				 ///////////////////签购单和小票之间走5个空行////////////////////
		            for (int j = 1; j <= 5; j++)
				     {
		            	XYKPrintDoc_Print("\n");
				     }
		          //////////////////////////////////////////////
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
				if(type == PaymentBank.XYKJZ)
				{
					if (PathFile.fileExist(name))
					{
						PathFile.deletePath(name);
					}
				}else
				{
					if (PathFile.fileExist(name))
					{
						PathFile.deletePath(name);
					}
					if (PathFile.fileExist(name2))
					{
						PathFile.deletePath(name2);
					}
				}	
			}
		}
		
//		public boolean ReadConfig(){
//			
//			//读取串口配置文件pszCommData.ini
//			if(s == null)
//			{
//				BufferedReader br = CommonMethod.readFile(path + "/pszCommData.ini");
//				String line;
//				String[] sp;
//		
//				if (br == null)
//				{
//					new MessageBox("串口配置文件导入错误,马上退出", null, false);
//
//					return false;
//				}
//				
//				try {
//					while ((line = br.readLine()) != null)
//					{
//						if ((line == null) || (line.length() <= 0))
//						{
//							continue;
//						}
//
//						String[] lines = line.split("&&");
//						sp = lines[0].split("=");
//						if (sp.length < 2)
//							continue;
//
//						if (sp[0].trim().compareToIgnoreCase("szCommPort") == 0)
//						{
//							szCommPort = sp[1].trim();
//						}
//						else if (sp[0].trim().compareToIgnoreCase("szComTimeOut") == 0)
//						{
//							szComTimeOut = sp[1].trim();
//						}
//						else if (sp[0].trim().compareToIgnoreCase("szComTestTime") == 0)
//						{
//							szComTestTime = sp[1].trim();
//						}
//						else if (sp[0].trim().compareToIgnoreCase("szServerIP") == 0)
//						{
//							szServerIP = sp[1].trim();
//						}
//						else if (sp[0].trim().compareToIgnoreCase("szServerPort") == 0)
//						{
//							szServerPort = sp[1].trim();
//						}
//						s = szCommPort + szComTimeOut + szComTestTime + szServerIP + szServerPort;
//					}
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				return true;
//			}else
//				return true;
//			
//		}
		
		
		
	}

