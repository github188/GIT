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

//鞍山中合工行银联接口(无界面)
//调用动态库（模块名：YLGM；动态库(dll文件）：ICBCMIS.dll ；函数：int BankTrans(char *  InputData, char* OutPutData)；）
public class ICBCLD_PaymentBankFunc extends PaymentBankFunc{
		public String[] getFuncItem()
	    {
	        String[] func = new String[8];
	        
	        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
	        func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
	        func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
	        func[3] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
	        func[4] = "[" + PaymentBank.XYKCD + "]" + "重打签购单";
	        func[5] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
	        func[6] = "[" + PaymentBank.XYKJZ + "]" + "交易结算";
	        //func[7] = "[" + PaymentBank.XKQT1 + "]" + "快速消费";
	        //func[8] = "[" + PaymentBank.XKQT2 + "]" + "分期付款";
	        func[7] = "[" + PaymentBank.XKQT1 + "]" + "其它交易";
	        
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
	                grpLabelStr[1] = "原终端号";
					grpLabelStr[2] = "原交易日";
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "交易金额";
	            break;
	        	case PaymentBank.XYKTH://隔日退货   
					grpLabelStr[0] = "原参考号";
					grpLabelStr[1] = "原终端号";
					grpLabelStr[2] = "原交易日";
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
	                grpLabelStr[0] = "原参考号";
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "重打签购单";
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
	        	case PaymentBank.XKQT1: //快速消费  
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "交易金额";
	            break;
	        	case PaymentBank.XKQT2: //分期付款  
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "交易金额";
	            break;
	        	case PaymentBank.XKQT3: //其他交易  
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "其他交易";
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
			 	case PaymentBank.XKQT1: 	// 快速消费
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = null;
	            break;
			 	case PaymentBank.XKQT2: 	// 分期付款
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = null;
	            break;
			 	case PaymentBank.XKQT3: 	// 其他交易
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
					  type == PaymentBank.XKQT1  ))
					{			
						  new MessageBox("银联接口不支持此交易类型！！！");
						  
						  return false;
				    }
								
				if (PathFile.fileExist(ConfigClass.BankPath + "\\request.txt"))
				{
					PathFile.deletePath(ConfigClass.BankPath + "\\request.txt");
					if (PathFile.fileExist(ConfigClass.BankPath + "\\request.txt"))
					{
						errmsg = "交易“request.txt”文件删除失败，请重试！！！";
						XYKSetError("XX",errmsg);
						new MessageBox(errmsg);
						
						return false;
					}				
				}
				if (PathFile.fileExist(ConfigClass.BankPath + "\\result.txt"))
				{
					PathFile.deletePath(ConfigClass.BankPath + "\\result.txt");
					if (PathFile.fileExist(ConfigClass.BankPath + "\\result.txt"))
					{
						errmsg = "交易“result.txt”文件删除失败，请重试！！！";
						XYKSetError("XX",errmsg);
						new MessageBox(errmsg);
						
						return false;
					}				
				}
				if (PathFile.fileExist(ConfigClass.BankPath + "\\P_TackSingle.txt"))
	            {
	                PathFile.deletePath(ConfigClass.BankPath + "\\P_TackSingle.txt");
	            }
				
				
				
				//多种支付方式，选择
				//////////选择：消费、快速消费,分期付款/////////////
//				if(type == PaymentBank.XYKXF)
//				{
//					//多种支付方式，选择
//					String code = "";
//					String[] title = { "代码","消费类型 "};
//					int[] width = { 60, 440 };
//					Vector contents = new Vector();
//					contents.add(new String[] { "1", "普通消费" });
//					contents.add(new String[] { "2", "快速消费" });
//					contents.add(new String[] { "3", "分期付款" });
//					int choice = new MutiSelectForm().open("请选择消费类型", title, width, contents, true);
//					if (choice == -1)
//					{
//						errmsg = "没有选择消费类型";
//						return false;
//					}else {
//						String[] row = (String[]) (contents.elementAt(choice));
//						code = row[0];
//						if(code.equals("2"))
//						{
//							type = PaymentBank.XKQT1;
//						}
//						if(code.equals("3"))
//						{
//							type = PaymentBank.XKQT2;
//						}
//					}
//				}
//				
//				
//	            //输入分期期数
//				if(type == PaymentBank.XKQT2)
//				{
//					StringBuffer bf = new StringBuffer();
//		            if(type == PaymentBank.XKQT1 || type == PaymentBank.XKQT2 || type == PaymentBank.XKQT3)
//		            {
//		            	TextBox txt = new TextBox();
//		            	if (!txt.open("请刷分期付款的期数", "分期期数", "请填正整数", bf, 0, 0, false, -1))
//		    			{
//		    				return true;
//		    			}
//		            	fq = bf.toString();
//		            }
//				}
	
				

	            // 写入请求数据
	            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
	            {
	                return false;
	            }
//	              调用接口模块
	            if (PathFile.fileExist(ConfigClass.BankPath+"\\javaposbank.exe"))
	            {
	            	CommonMethod.waitForExec(ConfigClass.BankPath+"\\javaposbank.exe YLGM","javaposbank");
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
	                default:
	                    type1 = "52";			// 其他交易
	                break;
				 }
//					char posid[8];    /*收银机号（最多8字节，左对齐，不足部分补空格*/
//					char operid[8];   /*	操作员号（最多8字节，左对齐，不足部分补空格）*/
//					char trans[2];    /*交易类型:*//*（'02'-快速消费 '03-当日撤销 ‘04'隔日退货  '05'-消费  '09'-签到 '10'-查询余额 '12'-分期付款 '13'-重打印 '14'-结算 '52'-其它交易）*/
//					char amount[12];  		/*金额（12字节，无小数点，左补0，单位：分）*/
//					char TipsAmount[12];/*小费金额（12字节，无小数点，左补0，单位：分）*/
//					char old_date[8];		/*原交易日期（8字节,yyyymmdd格式）*/
//					char old_reference[8];   /*原交易参考号*/
//					char PreAuthNo[6];   /*授权码（6字节，右对齐，左补0） */
//					char TerNo[15];    /*原交易终端号（隔日退货使用）*/
//					char PayNo[2];	/*分期付款期数*/	
//					char trk2[37];	 	/*二磁道数据（37字节，左对齐，不足部分补空格）*/
//					char trk3[104]; 	/*三磁道数据（104字节, 左对齐，不足部分补空*/


		         String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh,' ', 8);
		         
		         String syjh = Convert.increaseChar(ConfigClass.CashRegisterCode,' ', 8);
		         
				 String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
		         jestr = Convert.increaseCharForward(jestr,'0',12);
		         
		         String TipsAmount = Convert.increaseCharForward("0",'0',12); //小费金额
		         
		         String date = Convert.increaseChar(olddate, '0', 8); //交易日期
		         	         
				 String seqno="";             //参考号
					if (oldseqno != null)
					{
						seqno = Convert.increaseCharForward(oldseqno, '0', 8); 
					}
					else
					{
						seqno = Convert.increaseCharForward("0", '0', 8); 
					}
		         
		         String authno = Convert.increaseCharForward("0", '0', 6); //授权码   
		         
		         String TerNo = Convert.increaseCharForward(oldauthno, '0', 15); //终端号
		         
		         String strack2 = Convert.increaseChar(track2, ' ', 37); //二磁道数据
		         
		         String strack3 = Convert.increaseChar(" ", ' ', 104); //三磁道数据
		         
		         String fqno = "";                                   //分期期数
		         
        
		         //收银机号+操作员号+交易类型+金额+ 小费金额 +原交易日期+原交易参考号 +授权码+原交易终端号+分期付款期数+二磁道数据+三磁道数据
		         line = syjh + syyh + type1 + jestr + TipsAmount + date + seqno + authno + TerNo +fqno+ strack2 + strack3+"    ";
		     
       
		         try
		         {
		            pw = CommonMethod.writeFile(ConfigClass.BankPath+"\\request.txt");
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

		
//		char resp_code[2];/*返回码	( 2字节， "00"成功)*/
//		char bank_code[20];/*银行行号（20字节）*/
//		char card_no[20];/*卡号	(20字节，左对齐，不足部分补空格)*/
//		char expr[4];   	/*有效期	(4字节) */
//		char reference[8];	 /*参考号  (8字节，左对齐)*/
//		char trace[6];		 /*流水号  (6字节，左对齐)*/
//		char amount[12];/*金额（12字节，无小数点，左补0，单位：分）*/
//		char resp_chin[100];/*错误说明(100字节，左对齐，不足部分补空格)*/

		
		
		//读取result文件
		public boolean XYKReadResult()
		{
			BufferedReader br = null;
			
			try
	        {
				if (!PathFile.fileExist(ConfigClass.BankPath+"\\result.txt") || ((br = CommonMethod.readFileGBK(ConfigClass.BankPath+"\\result.txt")) == null))
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
	            
	            //bld.memo		= result[0];
	            
	            int type = Integer.parseInt(bld.type.trim());
	             
	            if (result.length >= 2)
	            {
	            	bld.retcode  = result[1].substring(0,2);  //返回码2
	            	if(!bld.retcode.equals("00")){
	            		bld.retmsg = Convert.newSubString(result[1], 71, 172).trim();
	            		return false;
	            	}
	            	if(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX ||  type == PaymentBank.XYKTH ||
	            	   type == PaymentBank.XKQT1 || type == PaymentBank.XKQT2 )
	            	{
	                	bld.cardno = Convert.newSubString(result[1], 22, 42).trim();   //卡号20
	                	bld.authno = Convert.newSubString(result[1], 46, 54).trim();   //参考号8
	                	bld.trace = Integer.parseInt(Convert.newSubString(result[1], 54, 60).trim());   //流水号6
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
				bld.retmsg = "交易失败";
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
				  type == PaymentBank.XKQT2	)
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
				new MessageBox("日期格式错误\n日期格式YYYYMMDD");
				return false;
			}
			
			return true;
		}
		
		
		
		public void XYKPrintDoc(int type)
		{
			ProgressBox pb = null;
			String name = ConfigClass.BankPath + "\\P_TackSingle.txt";
			if(GlobalInfo.sysPara.bankprint<1) return;

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
				//pb.setText("正在打印,请等待..." + "\t OY : " + GlobalInfo.sysPara.issetprinter);
				pb.setText("正在打印,请等待...");
				
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
								XYKPrintDoc_End();
								//new MessageBox("请撕下客户签购单！！！");
								
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
		

