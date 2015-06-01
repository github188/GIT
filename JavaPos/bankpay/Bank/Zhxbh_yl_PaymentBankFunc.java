package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import org.eclipse.swt.widgets.Text;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

//北京资和信百货——银联卡——接口
//调用动态库（模块名：ZHXBH；动态库(dll文件）：ZhxComDll.dll；函数：int __stdcall BankTrans(char *StrIn,char *StrOut);）
public class Zhxbh_yl_PaymentBankFunc extends PaymentBankFunc{
	    String path = null;
	    String TermId = null;
		public String[] getFuncItem()
	    {
	        String[] func = new String[11];
	        
	        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
	        func[1] = "[" + PaymentBank.XYKCX + "]" + "撤销";
	        func[2] = "[" + PaymentBank.XYKTH + "]" + "退货";
	        func[3] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
	        func[4] = "[" + PaymentBank.XYKCD + "]" + "重打签购单";
	        func[5] = "[" + PaymentBank.XYKQD + "]" + "签到/脱机退货";
	        func[6] = "[" + PaymentBank.XYKJZ + "]" + "交易结算";
	        func[7] = "[" + PaymentBank.XKQT1 + "]" + "积分消费";
	        func[8] = "[" + PaymentBank.XKQT2 + "]" + "积分查询";
	        func[9] = "[" + PaymentBank.XKQT3 + "]" + "积分撤销";
	        func[10] = "[" + PaymentBank.XKQT4 + "]" + "脱机退货";
	        
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
	                grpLabelStr[1] = "凭证号";
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "交易金额";
	            break;
	        	case PaymentBank.XYKTH://隔日退货   
					grpLabelStr[0] = "原交易参考号";
					grpLabelStr[1] = null;
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
	                grpLabelStr[1] = "凭证号";
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "重打签购单";
	            break;
	        	case PaymentBank.XYKQD: //签到   
	                grpLabelStr[0] = "原批次号";
	                grpLabelStr[1] = "凭证号";
	                grpLabelStr[2] = "原交易日期";
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "交易金额";
	            break;
	        	case PaymentBank.XYKJZ: //结算  
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "交易结算";
	            break;
	        	case PaymentBank.XKQT1: //积分消费  
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "积分数量";
	            break;
	        	case PaymentBank.XKQT2: //积分查询  
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "积分查询";
	            break;
	        	case PaymentBank.XKQT3: //积分撤销  
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = "凭证号";
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "积分数量";
	            break;
	        	case PaymentBank.XKQT4: //脱机退货  
	                grpLabelStr[0] = "原批次号";
	                grpLabelStr[1] = "凭证号";
	                grpLabelStr[2] = "原交易日期";
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "交易金额";
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
			 	case PaymentBank.XKQT1: 	//积分消费
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = null;
	            break;
			 	case PaymentBank.XKQT2: 	//积分查询
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = "按回车键开始查询";
	            break;
			 	case PaymentBank.XKQT3: 	//积分撤销
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = null;
	            break;
			 	case PaymentBank.XKQT4: 	//脱机退货
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = null;
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
					  type == PaymentBank.XKQT1 ||
					  type == PaymentBank.XKQT2 ||
					  type == PaymentBank.XKQT3 ||
					  type == PaymentBank.XKQT4 ))
					{			
						  new MessageBox("银联接口不支持此交易类型！！！");
						  
						  return false;
				    }
				
//				获得金卡文件路径
				path = "C:\\zhxpos";
				
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
					
				
//////////		选择：0-普通消费，1-积分消费/////////////
				if(type == PaymentBank.XYKXF)
				{
					//多种支付方式，选择
					String code = "";
					String[] title = { "代码","消费类型 "};
					int[] width = { 60, 440 };
					Vector contents = new Vector();
					contents.add(new String[] { "0", "普通消费" });
					contents.add(new String[] { "1", "积分消费" });
					int choice = new MutiSelectForm().open("请选择消费类型", title, width, contents, true);
					if (choice == -1)
					{
						errmsg = "没有选择消费类型";
						return false;
					}else {
						String[] row = (String[]) (contents.elementAt(choice));
						code = row[0];
						if(code.equals("1"))
						{
							type = PaymentBank.XKQT1;
						}	
					}
				}
			
//////////		选择：0-签到，1-脱机退货/////////////
				if(type == PaymentBank.XYKQD)
				{
					//多种支付方式，选择
					String code = "";
					String[] title = { "代码","交易类型 "};
					int[] width = { 60, 440 };
					Vector contents = new Vector();
					contents.add(new String[] { "0", "签到" });
					contents.add(new String[] { "1", "脱机退货" });
					int choice = new MutiSelectForm().open("请选择消费类型", title, width, contents, true);
					if (choice == -1)
					{
						errmsg = "没有选择消费类型";
						return false;
					}else {
						String[] row = (String[]) (contents.elementAt(choice));
						code = row[0];
						if(code.equals("1"))
						{
							type = PaymentBank.XKQT4;
						}
					}
				}
				

				
	            // 写入请求数据
	            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
	            {
	                return false;
	            }
	            //  调用接口模块
	            if (PathFile.fileExist(path+"\\javaposbank.exe"))
	            {
	            	CommonMethod.waitForExec(path+"\\javaposbank.exe ZHXBH","javaposbank");
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
	                    type1 = "01";
	                break;
				 	case PaymentBank.XYKCX: 	// 消费撤销
				 		type1 = "02";
				 	break;	
	                case PaymentBank.XYKTH:		// 隔日退货
	                	type1 = "05";
	                break;
	                case  PaymentBank.XYKYE: 	// 余额查询
	                	type1 = "03";
	                break;	
	                case  PaymentBank.XYKCD: 	// 重打签购单
	                	type1 = "07";
	                break;
	                case  PaymentBank.XYKQD: 	// 交易签到
	                	type1 = "06";
	                break;
	                case  PaymentBank.XYKJZ: 	// 交易结算
	                	type1 = "04";
	                break;
	                case  PaymentBank.XKQT1: 	// 积分消费
	                	type1 = "18";
	                break;
	                case  PaymentBank.XKQT2: 	// 积分查询
	                	type1 = "19";
	                break;
	                case  PaymentBank.XKQT3: 	// 积分撤销
	                	type1 = "20";
	                break;
	                default:
	                    type1 = "23";			// 脱机退货
	                break;
				 }
		         
		         type1 = Convert.increaseChar(type1, ' ', 3); 

		         String appType = Convert.increaseChar("1", ' ', 2); //应用类型（固定为银联）
		         
		         String inputway = Convert.increaseChar("", ' ', 2); //输入方式
		         
		         String strack2 = Convert.increaseChar(track2, ' ', 38); //二磁道数据
		         
		         String strack3 = Convert.increaseChar(" ", ' ', 105); //三磁道数据
		         
				 String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
		         jestr = Convert.increaseCharForward(jestr,'0',12);
		         jestr = Convert.increaseChar(jestr, ' ', 13);        //交易金额
		         
		         String authno = "";							//流水号(撤销、退货（或脱机退货）、重打印) || 授权码（隔日退货）  			
		         String AuthId = "";                           //流水号(撤销、退货（或脱机退货）、重打印)                
		         if(type == PaymentBank.XYKCX || type == PaymentBank.XYKCD ||
		        		 type == PaymentBank.XKQT3 ||type == PaymentBank.XKQT4)
		         {
			         if(oldauthno != null)
			         {
				         authno = Convert.increaseCharForward(oldauthno, '0', 6); 
				         authno = Convert.increaseChar(authno, ' ', 7);
				         AuthId = Convert.increaseChar("", ' ', 7);
			         }else
			         {
			        	 authno = Convert.increaseChar("", ' ', 7);
			         	 AuthId = Convert.increaseChar("", ' ', 7);
			         }
		         }
		         else
		         {
		        	 authno = Convert.increaseChar("", ' ', 7);
		         	 AuthId = Convert.increaseChar("", ' ', 7);	
		         }
		         
  	 
			         String seqno = "";							 //参考号  			
			         String BatchNo = "";                        //批次号          
			         if(type == PaymentBank.XYKTH )
			         {
				         if(oldauthno != null)
				         {
				        	 seqno = Convert.increaseCharForward(oldseqno, '0', 12); 
				        	 seqno = Convert.increaseChar(seqno, ' ', 13);
				        	 BatchNo = Convert.increaseChar("", ' ', 7);
				         }else
				         {
				        	 seqno = Convert.increaseChar("", ' ', 13);
				         	 BatchNo = Convert.increaseChar("", ' ', 7); 
				         }
			         }else if(type == PaymentBank.XKQT4)
			         {
			        	 	BatchNo = Convert.increaseCharForward(oldseqno, '0', 6);
			        	 	BatchNo = Convert.increaseChar(BatchNo, ' ', 7);
			        	 	seqno = Convert.increaseChar("", ' ', 13);
			         }
			         else
			         {
			        	 seqno = Convert.increaseChar("", ' ', 13);
			         	 BatchNo = Convert.increaseChar("", ' ', 7);	 
			         }

         
         
		         String date = Convert.increaseChar(olddate, ' ', 5); //交易日期

		         String syjh = Convert.increaseChar(ConfigClass.CashRegisterCode,' ', 9);
		         
		         String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh,' ', 10);
		         
		         String Append = "";
		         if(type == PaymentBank.XKQT4 )
		         {
						StringBuffer zd = new StringBuffer();
			            TextBox txt = new TextBox();
			            if (!txt.open("请输入原终端号", "原终端号", "8位原终端号", zd, 0, 0, false, -1))
			    		{
			    			return true;
			    		}
			            TermId = zd.toString();
			            TermId = "9Z" + Convert.increaseCharForward(String.valueOf(TermId.length()), '0', 3) + TermId;
			            Append = Convert.increaseChar(TermId,' ', 170);
		         }else{
		        	 Append = Convert.increaseChar("",' ', 170);    //附件信息
		         }		        	 
		         
//		                     交易类别+应用类型+输入方式+二磁道信息+三磁道信息+交易金额+原始交易流水+原始授权码+原参考号+批次号+原交易日期+款台号+操作员号+附加信息
		         line = type1 + appType + inputway + strack2 + strack3 + jestr + authno + AuthId + seqno + BatchNo + date + syjh + syyh + Append ;

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
	            
	            bld.memo = result[0];
	            
	            int type = Integer.parseInt(bld.type.trim());
	             
	            if (result.length >= 2)
	            {
	            	bld.retcode  = result[1].substring(3,6).trim();  //返回码
	            	if(!bld.retcode.equals("00"))
	            	{
	            		bld.retmsg = Convert.newSubString(result[1], 6, 47).trim();   //错误说明
	            		return false;
	            	}
	            	if(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XYKTH ||
	            			type == PaymentBank.XKQT1 || type == PaymentBank.XKQT3 || type == PaymentBank.XKQT4)
	            	{
	                	bld.cardno = Convert.newSubString(result[1], 47, 67).trim();   //卡号20
//	                	if(type == PaymentBank.XYKTH)
//	                	{
//	                		bld.trace = Integer.parseInt(Convert.newSubString(result[1], 156, 163).trim());   //授权号
//	                	}else
//	                		bld.trace = Integer.parseInt(Convert.newSubString(result[1], 163, 170).trim());   //流水号
//	                	
//	                	
//	                	bld.je = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Double.parseDouble(Convert.newSubString(result[1], 184, 197).trim() ),100),2,1);   //交易金额		
//	                	
//	                	if(type == PaymentBank.XKQT4)
//	                	{
//	                		bld.authno = Convert.newSubString(result[1], 177, 184).trim();  //批次号
//	                	}else
//	                		bld.authno = Convert.newSubString(result[1], 143, 156).trim();  //参考号             	
	            	}  	
	            	bld.trace = Convert.toInt(Convert.newSubString(result[1], 170, 177).trim());   //凭证号
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
				  type == PaymentBank.XYKCD	|| type == PaymentBank.XKQT1 ||
				  type == PaymentBank.XKQT3 || type == PaymentBank.XKQT4 )
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
			if (d.length() > 4)
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
			if(type == PaymentBank.XYKJZ)
			{
				name =path + "\\data\\printsett.txt";
			}
			else{
				name =path + "\\data\\print.txt";
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
				GlobalInfo.sysPara.bankprint=1;
				PosLog.getLog(this.getClass()).info("正在打印,请等待..." + "\t OY : " + GlobalInfo.sysPara.issetprinter);
				pb = new ProgressBox();
				pb.setText("正在打印,请等待..." + "\t OY : " + GlobalInfo.sysPara.issetprinter);
				
				XYKPrintDoc_Start();
				for (int i = 0; i < GlobalInfo.sysPara.bankprint; i ++)
				{
					PosLog.getLog(this.getClass()).info(String.valueOf(i) + "start");
					BufferedReader br = null;
					//XYKPrintDoc_Start();
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
						
						//int pcount=1;
						String line = null;
						while ((line = br.readLine()) != null)
						{
							/*if(pcount>7) 
							{
								PosLog.getLog(this.getClass()).info("退出打印");
								break;
							}*/
							if (line==null || line.length() <= 0)
								continue;
							//银行签购单模板添加 "CUTPAPER" 标记
							//当程序里面读取到这个字符是，打印机切纸
							if (line.trim().equals("CUTPAPER"))
							{
								PosLog.getLog(this.getClass()).info(line + "_st_start1");
								XYKPrintDoc_End();
								PosLog.getLog(this.getClass()).info(line + "_st_start2");
								//Printer.getDefault().cutPaper_Normal();
								XYKPrintDoc_Start();
								PosLog.getLog(this.getClass()).info(line + "_st_start3");
								new MessageBox("请撕下客户签购单！！！");
								
								continue;
							}
							
							XYKPrintDoc_Print(line);
							PosLog.getLog(this.getClass()).info(line);
							//pcount++;
						}					
					}
					catch(Exception e)
					{
						PosLog.getLog(this.getClass()).info(e);
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
							PosLog.getLog(this.getClass()).info(ie);
							ie.printStackTrace();
						}					
					}
					//XYKPrintDoc_End();

					PosLog.getLog(this.getClass()).info(String.valueOf(i) + "end");
				}
			}
			catch(Exception e)
			{
				PosLog.getLog(this.getClass()).info(e);
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
				PosLog.getLog(this.getClass()).info("bank print end");
				XYKPrintDoc_End();
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
		
		public void XYKPrintDoc_Start()
		{

			try
			{
				PosLog.getLog(this.getClass()).info("XYKPrintDoc_Start");
				if (onceprint)
				{
					if(Printer.getDefault().getStatus()){
						PosLog.getLog(this.getClass()).info("printer.close_Start");
						Printer.getDefault().close();
						PosLog.getLog(this.getClass()).info("printer.close");
					}
					else
					{
						Printer.getDefault().close();
						PosLog.getLog(this.getClass()).info("printer.close2");
					}
					Printer.getDefault().open();
					Printer.getDefault().setEnable(true);
					
					int pagesize = ConfigClass.BankPageSize;

					String port = getBankClassConfig("PRINTPORT");
					PosLog.getLog(this.getClass()).info("port=" + port + ",pagesize=" + pagesize);
					if (port == null || port.length() <= 0)
					{
						Printer.getDefault().startPrint_Journal();
						if (pagesize > 0)
							Printer.getDefault().setPagePrint_Journal(false, pagesize);
					}
					else if (port.trim().equals("1"))
					{
						Printer.getDefault().startPrint_Normal();
						if (pagesize > 0)
							Printer.getDefault().setPagePrint_Normal(false, pagesize);
					}
					else if (port.trim().equals("2"))
					{
						Printer.getDefault().startPrint_Journal();
						if (pagesize > 0)
							Printer.getDefault().setPagePrint_Journal(false, pagesize);
					}
					else if (port.trim().equals("3"))
					{
						Printer.getDefault().startPrint_Slip();
						if (pagesize > 0)
							Printer.getDefault().setPagePrint_Slip(false, pagesize);
					}
				}
				else
				{

					PosLog.getLog(this.getClass()).info("bankdoc_" + String.valueOf(bld.trace) + ".txt");
					// 此地改为增加模式，防止在多个金卡工程同时存在时，可能序号相同
					printdoc = CommonMethod.writeFileAppend("bankdoc_" + String.valueOf(bld.trace) + ".txt");
				}
			}
			catch(Exception ex)
			{
				PosLog.getLog(this.getClass()).error(ex);
			}
		}
		
	}

