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

//长春卓展中行接口
//调用动态库（模块名：CCZZ；动态库(dll文件）：libmpos.dll；函数：int Yada_Mpos(  struct REQUEST * ,  struct RESPONSE *);
public class Cczz_zh_PaymentBankFunc extends PaymentBankFunc{
	    String path = null;
		public String[] getFuncItem()
	    {
	        String[] func = new String[10];
	        
	        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
	        func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
	        func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
	        func[3] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
	        func[4] = "[" + PaymentBank.XYKCD + "]" + "重打签购单";	       
	        func[5] = "[" + PaymentBank.XYKJZ + "]" + "交易结算";
	        func[6] = "[" + PaymentBank.XKQT1 + "]" + "非接消费";
	        func[7] = "[" + PaymentBank.XKQT2 + "]" + "非接退货";
	        func[8] = "[" + PaymentBank.XKQT3 + "]" + "非接结算";
	        func[9] = "[" + PaymentBank.XKQT4 + "]" + "非接统计";
	        
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
	                grpLabelStr[1] = "原票据号";
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "交易金额";
	            break;
	        	case PaymentBank.XYKTH://隔日退货   
					grpLabelStr[0] = "原授权号";
					grpLabelStr[1] = "原票据号";
					grpLabelStr[2] = "日期时间";
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
	        	case PaymentBank.XYKCD: //重打签购单    
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "重打签购单";
	            break;
	        	case PaymentBank.XYKJZ: //结算  
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "交易结算";
	            break;
	        	case PaymentBank.XKQT1: //非接消费  
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "交易金额";
	            break;	        	
	            case PaymentBank.XKQT2: //非接退货   
					grpLabelStr[0] = "原交易参考号";
					grpLabelStr[1] = "原票据号";
					grpLabelStr[2] = "原终端号";
					grpLabelStr[3] = null;
					grpLabelStr[4] = "交易金额";
	            break;	        	
	            case PaymentBank.XKQT3: //非接结算  
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "非接结算";
	            break;       	
	            case PaymentBank.XKQT4: //非接统计  
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "非接统计";
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
			 	case PaymentBank.XYKCD: 	//重打签购单    
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = "按回车键开始重打印";
	            break;
			 	case PaymentBank.XYKJZ: 	//交易结算
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = "按回车键开始结算";
	            break;
			 	case PaymentBank.XKQT1: 	//非接消费  
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = null;
	            break;			 	
	            case PaymentBank.XKQT2: 	//非接退货   
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = null;
	            break;			 	
	            case PaymentBank.XKQT3: 	//非接结算    
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = "按回车键开始非接结算";
	            break;			 	
	            case PaymentBank.XKQT4: 	//非接统计 
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = "按回车键开始非接统计";
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
					  type == PaymentBank.XYKTH || 
					  type == PaymentBank.XYKJZ || 
					  type == PaymentBank.XYKYE ||
					  type == PaymentBank.XYKCD ||
					  type == PaymentBank.XKQT1 ||
					  type == PaymentBank.XKQT2 ||
					  type == PaymentBank.XKQT3 ||
					  type == PaymentBank.XKQT4 ))
					{			
						  new MessageBox("银联接口不支持此交易类型！！！");
						  
						  return false;
				    }
				
//				获得金卡文件路径
				path = ConfigClass.BankPath;
				
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
	            	CommonMethod.waitForExec(path+"\\javaposbank.exe CCZZ","javaposbank");
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
	                    type1 = "00";
	                break;
				 	case PaymentBank.XYKCX: 	// 消费撤销
				 		type1 = "01";
				 	break;	
	                case PaymentBank.XYKTH:		// 隔日退货
	                	type1 = "05";
	                break;
	                case  PaymentBank.XYKYE: 	// 余额查询
	                	type1 = "02";
	                break;	
	                case  PaymentBank.XYKCD: 	// 重打签购单
	                	type1 = "15";
	                break;
	                case  PaymentBank.XYKJZ: 	// 交易结算
	                    type1 = "18";			
	                break;
	                case  PaymentBank.XKQT1: 	// 非接消费
	                    type1 = "30";			
	                break;
	                case  PaymentBank.XKQT2: 	// 非接退货
	                    type1 = "28";			
	                break;
	                case  PaymentBank.XKQT3: 	// 非接结算
	                    type1 = "33";			
	                break;
	                case  PaymentBank.XKQT4: 	// 非接统计
	                    type1 = "34";			
	                break;
				 }


		         String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh,' ', 9);
		         
		         String syjh = Convert.increaseChar(ConfigClass.CashRegisterCode,' ', 8);
		         
				 String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
		         jestr = Convert.increaseCharForward(jestr,'0',12);
		         
		         String track = Convert.increaseChar(oldauthno, '0', 6); //原票据号
		         
		         String szAuthId = "";             //原授权号
		         String datetime = "";           //日期时间
		         
		         String seqno = "";                 //参考号
		         String authno = "";             //原终端号
		         if(oldseqno != null && olddate != null)
		         {
		        	 if(type == PaymentBank.XYKTH )
		        	 {
		        		 szAuthId = Convert.increaseChar(oldseqno, ' ', 6);
		        		 datetime = Convert.increaseChar(olddate, ' ', 14);
		        		 seqno = Convert.increaseChar("", ' ', 12);
		        		 authno = Convert.increaseChar("", ' ', 6);
		        	 }
		        	 else if(type == PaymentBank.XKQT2)
		        	 {
		        		 szAuthId = Convert.increaseChar("", ' ', 6);
		        		 datetime = Convert.increaseChar("", ' ', 14);
		        		 seqno = Convert.increaseChar(oldseqno, ' ', 12);
		        		 authno = Convert.increaseChar(olddate, ' ', 6);
		        	 }
		         }
		         else
		         {
		        	 szAuthId = Convert.increaseChar("", ' ', 6);
	        		 seqno = Convert.increaseChar("", ' ', 12);
		         }   
		         
		         String date = datetime.substring(0, 8); //原交易日期  
		         String time = datetime.substring(8, 14); //原交易时间
		         
		         String cardno = Convert.increaseChar("", ' ', 19);
		         String szExpr = Convert.increaseChar("", ' ', 4);
		         
//		     	char szPackType [2+1];             /* 交易类型 */ 
//		    	char szAmount[12+1];               /* 交易金额，前补0，精确到分 */
//		        char szCardNo[19+1];               /* 卡号 （撤销、预授权完成时）*/
//		     	char szExpr[4+1];		              /* 有效期 （撤销、预授权完成时）*/
//		    	char szTraceNo[6+1];               /* 原流水号（退货，非接退货用）or 原票据号 （重打印，撤销用）*/
//		    	char szAuthId[6+1];                /* 原授权码（退货用） */
//		    	char szRefNo[12+1];                /* 原参考号（退货，非接退货用） */
//		    	char szDateDate[8+1];              /* 原日期（退货用）格式为 YYYYMMDD*/
//		    	char szDateTime[6+1];              /* 原时间（退货用）格式为 MMDDSS  */
//		    	char szDesktopNo[8+1];             /* 款台号 */
//		    	char szOperator[9+1];              /* 收款员号 */
//		    	char szTermId[8+1];                /* 原终端号（非接退货用） */
        
		         line = type1 + "," + jestr + "," + cardno + "," + szExpr + "," + track + "," + szAuthId + "," + seqno + "," + date + "," + time + "," + syjh + "," + syyh + "," + authno ;
    
		         
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

//		strncpy(szResult[1],outputData.szPackType ,2+1);  /* 交易类型 */ 
//		strncpy(szResult[2],outputData.szResult,2+1);      /* 交易结果返回码 */
//		strncpy(szResult[3],outputData.szCHNResult,40+1);    /* 中文结果 */
//		strncpy(szResult[4],outputData.szCardNo,19+1);          /* 卡号 */
//		strncpy(szResult[5],outputData.szTermId,8+1);         /* 终端号 */
//		strncpy(szResult[6],outputData.szChargeDateTime,14+1);     /* 交易时间 */
//		strncpy(szResult[7],outputData.szRefNo,12+1);             /* 系统参考号 */
//		strncpy(szResult[8],outputData.szAuthId,6+1);	        /* 授权号 */
//	    strncpy(szResult[9],outputData.szTraceNo,6+1);           /* 流水号 */
//		strncpy(szResult[10],outputData.szSeqNo,6+1);              /* 票据号 */
//		strncpy(szResult[11],outputData.szAmount,12+1);            /* 交易金额 */
		
		
		
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
	            
	            bld.memo = result[0];
	            
	            int type = Integer.parseInt(bld.type.trim());
	             
	            if (result.length >= 2)
	            {
	            	bld.retcode  = result[1];  //返回码2
	            	if(!bld.retcode.equals("00"))
	            	{
	            		bld.retmsg = result[1].trim();   //错误说明
	            		return false;
	            	}
	            	if(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX ||  type == PaymentBank.XYKTH || 
	            	   type == PaymentBank.XKQT1 || type == PaymentBank.XKQT2 )
	            	{
	                	bld.cardno = result[1];   //卡号20
	                	bld.je = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Double.parseDouble(result[1]),100),2,1);   //交易金额		
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
			if (bld.memo.trim().equals("0") && bld.retcode.trim().equals("00"))
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
				  type == PaymentBank.XYKCD || type == PaymentBank.XKQT1 ||
				  type == PaymentBank.XKQT2 || type == PaymentBank.XKQT3 ||
				  type == PaymentBank.XKQT4 )
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
			if (d.length() != 14)
			{
				new MessageBox("日期格式错误\n日期格式《YYYYMMDDMMDDSS》");
				return false;
			}
			
			return true;
		}
		
		
		
		public void XYKPrintDoc(int type)
		{
			ProgressBox pb = null;
			String name =path + "\\print.txt";


			try
			{
				if (!PathFile.fileExist(name))
				{
					if(type == PaymentBank.XYKJZ || type == PaymentBank.XKQT3)
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
							if(type == PaymentBank.XYKJZ || type == PaymentBank.XKQT3)
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
							if (line.indexOf("CUTPAPER") >= 0)
//							if (line.trim().equals("CUTPAPER"))
							{
								XYKPrintDoc_End();
								new MessageBox("请客户撕下银联票据！！！");
								
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
				if(type == PaymentBank.XYKJZ || type == PaymentBank.XKQT3)
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
		
		
	}

