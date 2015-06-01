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

//长寿实际广场银联接口
//调用动态库（模块名：ICBCKPCLIENT；动态库(dll文件）：KeeperClient.dll；函数：int __stdcall misposTrans(void* input, void* output)。）
public class Cssjgc_PaymentBankFunc extends PaymentBankFunc{
	    String path = null;
		public String[] getFuncItem()
	    {
	        String[] func = new String[7];
	        
	        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
	        func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
	        func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
	        func[3] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
	        func[4] = "[" + PaymentBank.XYKCD + "]" + "重打上笔签购单";
	        func[5] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
//	        func[6] = "[" + PaymentBank.XYKJZ + "]" + "交易结算";
	        func[6] = "[" + PaymentBank.XKQT1 + "]" + "重打指定签购单";
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
	                grpLabelStr[0] = "原参考号";
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = "原交易日期";
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "交易金额";
	            break;
	        	case PaymentBank.XYKTH://隔日退货   
					grpLabelStr[0] = "原参考号";
					grpLabelStr[1] = "原终端号";
					grpLabelStr[2] = "原交易日期";
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
	                grpLabelStr[4] = "重打上笔签购单";
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
	        	case PaymentBank.XKQT1: //重打指定签购单  
	                grpLabelStr[0] = "原参考号";
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "重打指定签购单";
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
			 	case PaymentBank.XKQT1: 	//重打指定签购单
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = "按回车键重打指定签购单";
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
					  type == PaymentBank.XYKQD ||
					  type == PaymentBank.XYKJZ || 
					  type == PaymentBank.XYKYE ||
					  type == PaymentBank.XYKCD ||
					  type == PaymentBank.XKQT1 ))
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
				if (PathFile.fileExist(path + "\\ICBCPRTTKT.txt"))
	            {
	                PathFile.deletePath(path + "\\ICBCPRTTKT.txt");
	            }
				
				
			
				
	            // 写入请求数据
	            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
	            {
	                return false;
	            }
	            //  调用接口模块
	            if (PathFile.fileExist(path+"\\javaposbank.exe"))
	            {
	            	CommonMethod.waitForExec(path+"\\javaposbank.exe ICBCKPCLIENT","javaposbank");
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
	                    type1 = "05";
	                break;
				 	case PaymentBank.XYKCX: 	// 消费撤销
				 		type1 = "04";
				 	break;	
	                case PaymentBank.XYKTH:		// 隔日退货
	                	type1 = "04";
	                break;
	                case  PaymentBank.XYKYE: 	// 余额查询
	                	type1 = "10";
	                break;	
	                case  PaymentBank.XYKCD: 	// 重打签购单
	                	type1 = "13";
	                break;
	                case  PaymentBank.XYKQD: 	// 交易签到
	                	type1 = "09";
	                break;
	                case  PaymentBank.XYKJZ: 	// 交易结算
	                    type1 = "14";			
	                break;
	                case  PaymentBank.XKQT1: 	// 重打指定一笔
	                    type1 = "13";			
	                break;
				 }
					/**
				 typedef struct
				 {
				    	char TransType[2];		//交易类型（收银机MIS系统传入），消费:"05"  消费撤销:"04" 查询余额"10"
				 	char CardNo[19];		//交易卡号：MISPOS系统返回需记录到数据库中作为对账内容之一
				 	char Amount[12];		//交易金额（收银机MIS系统传入，需记录到数据库中作为对账内容之一）
				 	char Tip[12];			//小费金额（暂时不用，空格补齐）
				 	char MisBatchNo[6];		//MIS批次号（暂时不用，空格补齐）
				 	char MisTraceNo[6];		//MIS流水号（暂时不用，空格补齐）
				 	char TransTime[6];		//交易时间（MISPOS系统返回）
				 	char TransDate[8];		//交易日期（MISPOS系统返回）
				     	char ExpDate[4];		//卡片有效期（MISPOS系统返回）
				 	char Track2[37];		//二磁道信息（预留，暂时不用）
				 	char Track3[104];		//三磁道信息（预留，暂时不用）
				 	char ReferNo[8];		//系统检索号（MISPOS系统返回，收银机MIS系统在撤销交易时需传给MISPOS系统，需记录到数据库中作为对账内容之一）
				 	char AuthNo[6];			//MISPOS系统返回 暂时不用，空格补齐
				 	char ReturnCode[2];		//返回码（MISPOS系统返回，返回码为“00”表示交易成功，否则表示交易失败）
				 	char TerminalId[15];		//MISPOS系统返回交易终端号
				 	char MerchantId[12];		//MISPOS系统返回商户号
				 	char InstallmentTimes[2];	//MISPOS系统返回 暂时不用，空格补齐
				 	char TC[16];			//MISPOS系统返回 暂时不用，空格补齐
				 	char OldAuthDate[8];		//原交易日期，撤销交易时候传送给MISPOS系统
				 	char MerchantNameEng[50];	//商户名称（英文）MISPOS系统返回
				 	char MerchantNameChs[40];	//商户中文名称MISPOS系统返回
				 	char TerminalTraceNo[6];	//终端流水号MISPOS系统返回
				 	char TerminalBatchNo[6];	//终端批次号MISPOS系统返回
				 	char IcCardId[4];		//MISPOS系统返回，暂时无需处理
				 	char CardType[20];		//MISPOS系统返回
				 	char TransName[20];		//MISPOS交易中文名称
				 	char DeviceInitFlag[1];		//MISPOS系统返回，暂时无需处理
				 	char Message[100];		//交易失败时，MISPOS系统返回中文错误描述信息
				 	char Remark[300];		//MISPOS系统返回，暂时无需处理
				 	char ForeignCardTraceNo[24];	//MISPOS系统返回，暂时无需处理
				 	char Ttotal[800];		//交易信息汇总，为交易总账信息
				 	char PlatId[20];		//收银台号
				 	char OperId[20];		//操作员号
				 } ST_ICBC_MIS;
				 				 */				

					// 交易卡号【19】
					String cardno = Convert.increaseChar("", 19);
					// 交易金额【12】
					String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1));
					jestr = Convert.increaseCharForward(jestr, '0', 12);
					// 消费金额【12】
					String Tip = Convert.increaseChar("", 12);
					// MIS批次号【6】
					String MisBatchNo = Convert.increaseChar("", 6);
					// MIS流水号【6】
					String MisTraceNo = Convert.increaseChar("", 6);
					// 交易时间【6】
					String TransTime = Convert.increaseChar("", 6);
					// 交易日期【8】
					String TransDate = Convert.increaseChar(olddate , 8);
					// 卡片有效期【4】
					String ExpDate = Convert.increaseChar("", 4);
					// 二磁道信息【37】
					String Track2 = Convert.increaseChar("", 37);
					// 三磁道信息【104】
					String Track3 = Convert.increaseChar("", 104);
					// 系统检索号【8】
					String ReferNo = Convert.increaseChar(oldseqno , 8);
					// MISPOS系统返回【6】
					String AuthNo = Convert.increaseChar("", 6);
					// 返回
					String retmsg = ","+Convert.increaseChar("", 2)+
									","+Convert.increaseChar(oldauthno,' ', 15)+
									","+Convert.increaseChar("", 12)+
									","+Convert.increaseChar("", 15)+
									","+Convert.increaseChar("", 2)+
									","+Convert.increaseChar("",16)+
									","+TransDate+
									"," +Convert.increaseChar("", 50)+
									","+Convert.increaseChar("", 40)+
									","+Convert.increaseChar("", 6)+
									","+Convert.increaseChar("", 6)+
									","+Convert.increaseChar("", 4)+
									","+Convert.increaseChar("", 20)+
									","+Convert.increaseChar("", 20)+
									","+Convert.increaseChar("", 800)+
									","+Convert.increaseChar("", 1)+
									","+Convert.increaseChar("", 100)+
									","+Convert.increaseChar("", 300)+
									","+Convert.increaseChar("", 24)+
									","+Convert.increaseChar(ConfigClass.CashRegisterCode, 20)+
									","+Convert.increaseChar(GlobalInfo.posLogin.gh, 20);
					bld.type = type1;
					line = type1 +","+ cardno +","+ jestr +","+ Tip +","+ MisBatchNo +","+ MisTraceNo +","+ TransTime +","+ Convert.increaseChar("" , 8) +","+ ExpDate +","+ Track2 +","+ Track3 +","+ ReferNo +","+ AuthNo+retmsg;

		         
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
	            
	            int type = Integer.parseInt(bld.type.trim());
	             
	            if (result.length >= 2)
	            {
	            	bld.retcode  = result[13];  //返回码2
	            	if(!bld.retcode.equals("00"))
	            	{
	            		bld.retmsg = result[29].trim();   //错误说明
	            		return false;
	            	}
	            	if(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX ||  type == PaymentBank.XYKTH )
	            	{
	                	bld.cardno = result[1].trim();   //卡号
	                	bld.trace = Convert.toInt((result[11]));   //检索号
	                	bld.je = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Convert.toDouble((result[2]) ),100),2,1);   //交易金额		
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
			if ( bld.retcode.trim().equals("00"))
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
				  type == PaymentBank.XYKCD || type == PaymentBank.XKQT1 )
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
				new MessageBox("日期格式错误\n日期格式《YYYYMMDD》");
				return false;
			}
			
			return true;
		}
		
		
		
		public void XYKPrintDoc(int type)
		{
			ProgressBox pb = null;
			String name = path + "\\ICBCPRTTKT.txt";

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
		
		
	}

