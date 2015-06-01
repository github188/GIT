package bankpay.Bank;

import java.io.BufferedReader;
import java.io.File;
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
import com.efuture.javaPos.UI.Design.MutiSelectForm;

//销售小票即扫即打   后打印签购单
//调用动态库（模块名：ZYTJ；动态库(dll文件）：posinf.dll；函数：int bankall (char * request,char *response)；）
public class Cczz_hyzzgc1_PaymentBankFunc extends PaymentBankFunc{
	    String path = null;
		String name = path + "\\receipt.txt";
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
	        func[7] = "[" + PaymentBank.XKQT2 + "]" + "重打结算单";
	        
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
	                grpLabelStr[1] = "原凭证号";
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
	                grpLabelStr[1] = "原凭证号";
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
	        	case PaymentBank.XKQT2: //重打结算单  
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "重打结算单";
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
			 	case PaymentBank.XKQT2: 	//重打结算单
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = "按回车键开始重打结算单";
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
					  type == PaymentBank.XKQT2 ))
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
				if (PathFile.fileExist(path + "\\receipt.txt"))
	            {
	                PathFile.deletePath(path + "\\receipt.txt");
	            }
				
				
				File file=new File(path);
				String test[];
				test=file.list();
				for(int i=0;i<test.length;i++) 
				{   
					String a[] = test[i].split("\\.");
					if(a[1].equals("txt") && a[0].startsWith("bankdoc_"))
					{				
						PathFile.deletePath(test[i]);
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
	            	CommonMethod.waitForExec(path+"\\javaposbank.exe ZYTJ","javaposbank");
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
	            
	            
//	                即扫即打时将签购单备份
				if (GlobalInfo.syjDef.printfs == '1' && (type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XYKTH ||
						type == PaymentBank.XYKJZ || type == PaymentBank.XYKCD || type == PaymentBank.XKQT2))
				{
					if (PathFile.isPathExists(name))
					{
						String newbillname = "bankdoc_" + bld.trace + ".txt";

						PathFile.renameFile(name, path + "\\" +newbillname);

						//if (PathFile.fileExist("C:\\ricom\\" + newbillname))
						//	new MessageBox("billcount:" + String.valueOf(printtimes) + "  C:\\ricom\\" + newbillname);

					}
				}
	            
	            
	            //打印签购单(银联打印)
				if (XYKNeedPrintDoc(type))
				{
//					XYKPrintDoc(type);
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
	                	type1 = "02";
	                break;
	                case  PaymentBank.XYKYE: 	// 余额查询
	                	type1 = "03";
	                break;	
	                case  PaymentBank.XYKCD: 	// 重打签购单
	                	type1 = "04";
	                break;
	                case  PaymentBank.XYKQD: 	// 交易签到
	                	type1 = "05";
	                break;
	                case  PaymentBank.XYKJZ: 	// 交易结算
	                    type1 = "06";			
	                break;
	                case  PaymentBank.XKQT2: 	// 重打结算单
	                    type1 = "07";			
	                break;
				 }
				 
//				 public String mername; // SFKS,银联商户名称,SH（该系统参数用来添加多种银行卡交易编码）//刷卡类型 如：( 00,银行卡A|01,银行卡B.....)
				 String cardType = "00";
				 
					 String[] sys = GlobalInfo.sysPara.mername.trim().split("\\|");
					 String[] bankcardID = null;
					 
					 if(GlobalInfo.sysPara.mername.trim().equals("") || sys.length <= 1)
					 {
						 bankcardID = sys[0].split(",");
						 cardType = bankcardID[0];
					 }
					 else
					 {
//						多种卡方式，选择
						 String[] title = { "银行卡编码", "卡名称" };
						 int[] width = { 60, 440 };
						 Vector contents = new Vector();
						 
						 for(int i = 0 ; i< sys.length ;i++)
						 {
							 bankcardID = sys[i].split(",");
							 contents.add(new String[] { bankcardID[0], bankcardID[1]});	;
						 }
												
						 int choice = new MutiSelectForm().open("请选择交易卡类型", title, width, contents, true);
						 if (choice == -1)
						 {
							errmsg = "没有选择卡类型";
							new MessageBox(errmsg);
							return false;
						 }else {
							String[] row = (String[]) (contents.elementAt(choice));
						    cardType = row[0];
						 }
						 
						 if((type != PaymentBank.XYKCX || type != PaymentBank.XYKTH) && !cardType.equals("00"))
						 {
							 new MessageBox("不支持该功能");
							 
							 return false;	
						 }
					 }
				 
			
				
				 				        
				 
		         String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh,' ', 8);
		         
		         String syjh = Convert.increaseChar(ConfigClass.CashRegisterCode,' ', 8);
		         
				 String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
		         jestr = Convert.increaseCharForward(jestr,'0',12);
		         
		         String date = Convert.increaseChar(olddate, '0', 8); //交易日期
		         
		         String seqno = Convert.increaseCharForward(oldseqno, '0', 12); //参考号
		         
		         String authno = Convert.increaseCharForward(oldauthno, '0', 6); //流水号   

		         bld.crc = XYKGetCRC();
		         
		        
		         //刷卡类型 + 收银机号+操作员号+交易类型+金额+原交易日期+原交易参考号+流水号+交易校验数据
		         line = cardType + syjh + syyh + type1 + jestr + date + seqno + authno + bld.crc;
		     
		         
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
	            	bld.retcode  = Convert.newSubString(result[1], 0, 2);  //返回码2
	            	if(!bld.retcode.equals("00"))
	            	{
	            		bld.retmsg = bld.retcode + "," + Convert.newSubString(result[1], 56, 96).trim();   //错误说明
	            		return false;
	            	}
	            	if(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX ||  type == PaymentBank.XYKTH )
	            	{
	                	bld.cardno = Convert.newSubString(result[1], 18, 38);   //卡号20
	                	bld.trace = Convert.toInt((Convert.newSubString(result[1], 38, 44)));   //凭证号6
	                	bld.je = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Convert.toDouble(Convert.newSubString(result[1], 44, 56)),100),2,1);   //交易金额		
	            	}  	
	            	
	            	String lrc = Convert.newSubString(result[1], 657, 660);   //交易数据校验码
	    			if(!lrc.equals(bld.crc))
	    			{
	    				errmsg = "返回效验码" + lrc + "同原效验码" + bld.crc + "不一致";
	    				XYKSetError("XX", errmsg);
	    				new MessageBox(errmsg);
	    				
	    				return false;
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
//	                    if (PathFile.fileExist(path + "\\request.txt"))
//						{
//							PathFile.deletePath(path + "\\request.txt");
//						}
//
//						if (PathFile.fileExist(path + "\\result.txt"))
//						{
//							PathFile.deletePath(path + "\\result.txt");
//						}
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
			if (bld.retcode.trim().equals("00") && bld.memo.trim().equals("0"))
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
			
			if (GlobalInfo.syjDef.printfs == '1' && (type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XYKTH ||
					type == PaymentBank.XYKJZ || type == PaymentBank.XYKCD || type == PaymentBank.XKQT2))
			{
				setOnceXYKPrintDoc(false);
				return false;
			}

			if (  type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || 
				  type == PaymentBank.XYKTH || type == PaymentBank.XYKJZ || 
				  type == PaymentBank.XYKCD || type == PaymentBank.XKQT2 )
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

			try
			{
				if (!PathFile.fileExist(name))
				{
					new MessageBox("找不到签购单打印文件！！！");
					
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

							new MessageBox("打开签购单文件失败");

							return ;
						}
						
						String line = null;
						while ((line = br.readLine()) != null)
						{
							if (line.length() <= 0)
								continue;
							//银行签购单模板添加 "CUTPAPER" 标记
							//当程序里面读取到这个字符是，打印机切纸
							if (line.indexOf("CUTPAPER") >= 0)
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
				
				new MessageBox("打印签购单异常!!!\n" + e.getMessage());		
			}
			finally
			{
				if (pb != null)
				{
					pb.close();
					pb = null;
				}

//				if (PathFile.fileExist(name))
//				{
//					PathFile.deletePath(name);
//				}
				
				
			}
		}
		
		
	}


