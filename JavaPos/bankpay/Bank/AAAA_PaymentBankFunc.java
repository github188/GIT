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

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

//XXXX银联接口(无界面)
//调用动态库（模块名：XXXX；动态库(dll文件）：posinf.dll；函数：int bankall (char * request,char *response)；）
public class AAAA_PaymentBankFunc extends PaymentBankFunc{
	    String path = null;
		private SaleBS saleBS = null;
		public String[] getFuncItem()
	    {
	        String[] func = new String[7];
	        
	        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
	        func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
	        func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
	        func[3] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
	        func[4] = "[" + PaymentBank.XYKCD + "]" + "重打签购单";
	        func[5] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
	        func[6] = "[" + PaymentBank.XYKJZ + "]" + "交易结算";
	        
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
					  type == PaymentBank.XYKCD ))
					{			
						  new MessageBox("银联接口不支持此交易类型！！！");
						  
						  return false;
				    }
				
				//避免从菜单直接进行交易
				if (memo.size() >= 2)
					saleBS = (SaleBS) memo.elementAt(2);
				if (saleBS == null && (type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XYKTH))
				{
					errmsg = "该交易必须在付款的时候使用,请正常操作！";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
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
				if (PathFile.fileExist(path + "\\print.txt"))
	            {
	                PathFile.deletePath(path + "\\print.txt");
	            }
				
				
				
//				多种支付方式，选择
//				String code = "";
//				String[] title = { "代码", "应用类型" };
//				int[] width = { 60, 440 };
//				Vector contents = new Vector();
//				contents.add(new String[] { "1", "银行卡" });
//				contents.add(new String[] { "2", "卡、DCC" });
//				contents.add(new String[] { "3", "工行积分、分期" });
//				contents.add(new String[] { "4", "银商预付卡" });
//				contents.add(new String[] { "5", "亚盟卡" });
//				contents.add(new String[] { "6", "重庆通卡" });
//				contents.add(new String[] { "7", "其他应用" });
//				contents.add(new String[] { "8", "统一预付卡" });
//				
//				int choice = new MutiSelectForm().open("请选择交易卡类型", title, width, contents, true);
//				if (choice == -1)
//				{
//					errmsg = "没有选择应用类型";
//					return false;
//				}else {
//					String[] row = (String[]) (contents.elementAt(choice));
//					code = row[0];
//				}
				
				
	
				
				
	            // 写入请求数据
	            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
	            {
	                return false;
	            }
	            //  调用接口模块
	            if (PathFile.fileExist(path+"\\javaposbank.exe"))
	            {
	            	CommonMethod.waitForExec(path+"\\javaposbank.exe JXNX","javaposbank");
	            	//CommonMethod.waitForExec(path+"\\gmc.exe");
	            }
	            else
	            {
	                new MessageBox("找不到金卡工程模块 javaposbank.exe");
	                XYKSetError("XX","找不到金卡工程模块 javaposbank.exe");
	                return false;
	            }
				
	            // 读取应答数据
	            if (!XYKReadResult(type))
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
				 }
//				 char posid[8];    /*1～8位 收银机号（最多8字节，左对齐，不足部分补空格*/
//				 char operid[8];   /*9～16位	操作员号（最多8字节，左对齐，不足部分补空格）*/
//				 char trans[2];    /*17～18位	  交易类型:*/
//				 	/*（'00'-消费  '01'-撤销  '02'-退货  '03'-查余额  '04'重打指定流水  '05'签到 '06'结算）*/
//				 char amount[12];  /*19～30位 金额（12字节，无小数点，左补0，单位：分）*/
//				 char old_date[8];	/*31～38位	原交易日期（8字节,yyyymmdd格式，退货时用*/
//				 char old_reference[12];   /*39～50位	原交易参考号*/ (12字节，右对齐，左补0，退货时用)
//				 char old_trace[6];   /*51～56位	流水号（6字节，右对齐，左补0，撤销或重打印），‘000000’为重打印上一笔*/
//				 char trk2[37];	 	/*57～93位	二磁道数据（37字节，左对齐，不足部分补空格）*/
//				 char trk3[104]; 	/*94～197位  三磁道数据（104字节, 左对齐，不足部分补空格*/
//				 char lrc[3];    	/*198～200位 交易校验数据（3位从0～9的随机字符）*/

		         String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh,' ', 8);
		         
		         String syjh = Convert.increaseChar(ConfigClass.CashRegisterCode,' ', 8);
		         
				 String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
		         jestr = Convert.increaseCharForward(jestr,'0',12);
		         
		         String date = Convert.increaseChar(olddate, '0', 8); //交易日期
		         
		         String seqno = Convert.increaseCharForward(oldseqno, '0', 12); //参考号
		         
		         String authno = Convert.increaseCharForward(oldauthno, '0', 6); //流水号   
		         
		         String strack2 = Convert.increaseChar(track2, ' ', 37); //二磁道数据
		         
		         String strack3 = Convert.increaseChar(" ", ' ', 104); //三磁道数据

		         bld.crc = XYKGetCRC();
		         
		        
		         //收银机号+操作员号+交易类型+金额+原交易日期+原交易参考号+流水号+二磁道数据+三磁道数据+交易校验数据
		         line = syjh + syyh + type1 + jestr + date + seqno + authno + strack2 + strack3 + bld.crc;
		     
		         
//					switch(type)
//					{
//						case PaymentBank.XYKXF:
//							line = "" + syyh + syjh + jestr  ;
//							break;
//						case PaymentBank.XYKCX:
//							line = "" + syyh + syjh + jestr + seqno + authno ;
//							break;
//						case PaymentBank.XYKTH:
//							line = "" + syyh + syjh + jestr + seqno + authno + date;
//							break;
//						case PaymentBank.XYKQD:
//							line = "" + syyh + syjh ;
//							break;
//						case PaymentBank.XYKJZ:
//							line = "" + syyh + syjh ;
//							break;
//						case PaymentBank.XKQT2:
//							line = "" + syyh + syjh ;
//							break;
//						case PaymentBank.XYKYE:
//							line = "" + syyh + syjh;
//							break;
//						case PaymentBank.XYKCD:
//							line = "" + syyh + syjh + seqno;
//							break;
//						case PaymentBank.XKQT1:
//							line = "" + syyh + syjh + seqno;
//							break;
//					}
		         
		         
		         
		         
//		         String seqno = "";							 //参考号  			
//		         String BatchNo = "";                        //批次号          
//		         if(type == PaymentBank.XYKTH )
//		         {
//			         if(oldauthno != null)
//			         {
//			        	 seqno = Convert.increaseCharForward(oldseqno, '0', 12); 
//			        	 seqno = Convert.increaseChar(seqno, ' ', 13);
//			        	 BatchNo = Convert.increaseChar("", ' ', 7);
//			         }else
//			         {
//			        	 seqno = Convert.increaseChar("", ' ', 13);
//			         	 BatchNo = Convert.increaseChar("", ' ', 7); 
//			         }
//		         }else if(type == PaymentBank.XKQT4)
//		         {
//		        	 	BatchNo = Convert.increaseCharForward(oldseqno, '0', 6);
//		        	 	BatchNo = Convert.increaseChar(BatchNo, ' ', 7);
//		        	 	seqno = Convert.increaseChar("", ' ', 13);
//		         }
//		         else
//		         {
//		        	 seqno = Convert.increaseChar("", ' ', 13);
//		         	 BatchNo = Convert.increaseChar("", ' ', 7);	 
//		         }
		         
		         
		         
		         
		         
		         
		         
		         
		         
		         
		         
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
		public boolean XYKReadResult(int type)
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
	            
	            if (result.length >= 2)
	            {
	    			//当银行返回字符以字节计算，而字符中汉字出现导致line的长度和字节书不等时，汉字后面的内容倒着计算位置
	    			// int len = line.length();
	    			//防止字节和字符不一致问题
	    			//bld.memo = Convert.newSubString(line, 2, 21);
	            	bld.retcode  = result[1].substring(0,2);  //返回码2
	            	if(!bld.retcode.equals("00"))
	            	{
	            		bld.retmsg = bld.retcode + "," +result[1].substring(48,88).trim();   //错误说明
	            		return false;
	            	}
	            	if(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX ||  type == PaymentBank.XYKTH )
	            	{
	                	bld.cardno = result[1].substring(6,26);   //卡号20
	                	bld.trace = Convert.toInt((result[1].substring(30,36)));   //流水号16
	                	bld.je = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Convert.toDouble((result[1].substring(36,48)) ),100),2,1);   //交易金额		
	            	}  	
	            	
	            	String lrc = result[1].substring(88,91);   //交易数据校验码
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
		
		//传8位不用重写 传4位需要重写
//		public boolean checkDate(Text date)
//		{
//			String d = date.getText();
//			if (d.length() != 4)
//			{
//				new MessageBox("日期格式错误\n日期格式《MMDD》");
//				return false;
//			}
//			
//			return true;
//		}
		
		
		
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
				if (PathFile.fileExist(name2))
				{
					PathFile.deletePath(name2);
				}
			}
		}
		
		
	}

