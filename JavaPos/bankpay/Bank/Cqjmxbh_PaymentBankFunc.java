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

import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;

import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

//重庆金美西百货银联接口
//调用动态库（模块名：PosAdapter；动态库(dll文件）：posmis.dll；函数：Int DoTrans(REQ_MSG_NULL *requestMsg,RSP_MSG_NULL *responseMsg,char *strParamFileName);）

public class Cqjmxbh_PaymentBankFunc extends PaymentBankFunc{

	    String path = null;
		public String[] getFuncItem()
	    {
	        String[] func = new String[12];
	        
	        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
	        func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
	        func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
	        func[3] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
	        func[4] = "[" + PaymentBank.XYKCD + "]" + "重打最后一笔";
	        func[5] = "[" + PaymentBank.XKQT1 + "]" + "重打任意一笔";
	        func[6] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
	        func[7] = "[" + PaymentBank.XYKJZ + "]" + "交易结算";
	        func[8] = "[" + PaymentBank.XKQT2 + "]" + "积分消费";
	        func[9] = "[" + PaymentBank.XKQT3 + "]" + "积分消费撤销";
	        func[10] = "[" + PaymentBank.XKQT4 + "]" + "分期消费";
	        func[11] = "[" + PaymentBank.XKQT5 + "]" + "分期消费撤销";
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
	                grpLabelStr[1] = "交易凭证号";
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "交易金额";
	            break;
	        	case PaymentBank.XYKTH://隔日退货   
					grpLabelStr[0] = "原交易参考号";
					grpLabelStr[1] = "交易凭证号";
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
	        	case PaymentBank.XYKCD: //重打最后一笔    
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "重打最后一笔";
	            break;
	        	case PaymentBank.XKQT1: //重打任意一笔   
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = "交易凭证号";
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "重打任意一笔";
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
	        	case PaymentBank.XKQT2: //	积分消费
	        		grpLabelStr[0] = null;
	        		grpLabelStr[1] = null;
	        		grpLabelStr[2] = null;
	        		grpLabelStr[3] = null;
	        		grpLabelStr[4] = "交易金额";
	        	break;
	        	case PaymentBank.XKQT3: //	积分消费撤销
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = "交易凭证号";
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "交易金额";
	        	break;
	        	case PaymentBank.XKQT4: //	分期消费
	        		grpLabelStr[0] = null;
	        		grpLabelStr[1] = null;
	        		grpLabelStr[2] = null;
	        		grpLabelStr[3] = null;
	        		grpLabelStr[4] = "交易金额";
	        	break;
	        	case PaymentBank.XKQT5: //	分期消费撤销
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = "交易凭证号";
	                grpLabelStr[2] = null;
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
			 	case PaymentBank.XKQT1: 	//重打任意一笔  
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
			 	case PaymentBank.XKQT2: 	//积分消费
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = null;
	            break;
			 	case PaymentBank.XKQT3: 	//积分消费撤销
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = null;
	            break;
			 	case PaymentBank.XKQT4: 	//分期消费 
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = null;
	            break;
			 	case PaymentBank.XKQT5: 	//分期消费撤销 
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
					  type == PaymentBank.XKQT4 ||
					  type == PaymentBank.XKQT5 ))
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
//				if (PathFile.fileExist(path + "\\print.txt"))
//	            {
//	                PathFile.deletePath(path + "\\print.txt");
//	            }
				
				
				
				//////////选择：0-普通消费，1-积分消费，2-分期付款/////////////
				if(type == PaymentBank.XYKXF)
				{
					//多种支付方式，选择
					String code = "";
					String[] title = { "代码","消费类型 "};
					int[] width = { 60, 440 };
					Vector contents = new Vector();
					contents.add(new String[] { "0", "普通消费" });
					contents.add(new String[] { "1", "积分消费" });
					contents.add(new String[] { "2", "分期付款" });
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
							type = PaymentBank.XKQT2;
						}else if(code.equals("2"))
						{
							type = PaymentBank.XKQT4;
						}	
					}
				}
				
				
//////////		选择：0-普通消费撤销，1-积分消费撤销，2-分期付款撤销/////////////
				if(type == PaymentBank.XYKCX)
				{
					//多种支付方式，选择
					String code = "";
					String[] title = { "代码","消费类型 "};
					int[] width = { 60, 440 };
					Vector contents = new Vector();
					contents.add(new String[] { "0", "普通消费撤销" });
					contents.add(new String[] { "1", "积分消费撤销" });
					contents.add(new String[] { "2", "分期消费撤销" });
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
							type = PaymentBank.XKQT3;
						}else if(code.equals("2"))
						{
							type = PaymentBank.XKQT5;
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
	            	CommonMethod.waitForExec(path+"\\javaposbank.exe PosAdapter","javaposbank");
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
////////////////////////////////////////				
//	            //打印签购单
//				if (XYKNeedPrintDoc(type))
//				{
//					XYKPrintDoc();
//				}
///////////////////////////////////////////
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
	                    type1 = "22";
	                break;
				 	case PaymentBank.XYKCX: 	// 消费撤销
				 		type1 = "23";
				 	break;	
	                case PaymentBank.XYKTH:		// 隔日退货
	                	type1 = "25";
	                break;
	                case  PaymentBank.XYKYE: 	// 余额查询
	                	type1 = "03";
	                break;	
	                case  PaymentBank.XYKCD: 	// 重打上笔签购单
	                	type1 = "A0";
	                break;
	                case  PaymentBank.XKQT1: 	// 重打任意签购单
	                	type1 = "A1";
	                break;
	                case  PaymentBank.XYKQD: 	// 交易签到
	                	type1 = "00";
	                break;
	                case  PaymentBank.XKQT2: 	// 积分消费
	                	type1 = "51";
	                break;
	                case  PaymentBank.XKQT3: 	// 积分消费撤销
	                	type1 = "52";
	                break;
	                case  PaymentBank.XKQT4: 	// 分期消费
	                	type1 = "40";
	                break;
	                case  PaymentBank.XKQT5: 	// 分期消费撤销
	                	type1 = "41";
	                break;
	                default:
	                    type1 = "02";			// 交易结算
	                break;
				 }
				 
				 
//					strncpy(inputData.szTransType,szRequest[0],2+1);// 交易类型
//					strncpy(inputData.szAmount,szRequest[1],12+1);// 交易金额
//					strncpy(inputData.szOrgDate,szRequest[2],4+1);// 原交易日期
//					strncpy(inputData.szOrgTrace,szRequest[3],6+1);// 原终端流水号
//					strncpy(inputData.szOrgRrn,szRequest[4],12+1);// 原系统参考号
//					strncpy(inputData.szEcrTid,szRequest[5],8+1);// 收款台号
//					strncpy(inputData.szEcrUid,szRequest[6],8+1);// 收款台操作员号
//					strncpy(inputData.szTransMemo,szRequest[7],100+1);// 交易其他信息							 

		         String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh,' ', 8);
		         
		         String syjh = Convert.increaseChar(ConfigClass.CashRegisterCode,' ', 8);
		         
				 String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
		         jestr = Convert.increaseCharForward(jestr,'0',12);
		         
		         String date = Convert.increaseChar(olddate, '0', 4); //交易日期
		         
		         String seqno = Convert.increaseCharForward(oldseqno, '0', 12); //参考号
		         
		         String authno = Convert.increaseCharForward(oldauthno, '0', 6); //流水号   

		         bld.crc = Convert.increaseChar("",' ',100);
		         
		        
		         //交易类型+金额+原交易日期+流水号+原交易参考号+收银机号+操作员号+交易其他信息
		         line = type1 + "," + jestr + "," + date + "," + authno + "," + seqno + "," + syjh + "," + syyh + "," + bld.crc;		           
		         
		         
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
	            	            
	            
	            int type = Integer.parseInt(bld.type.trim());
	            
	            String result[] = line.split(",");
	            if (result == null) return false;
	            
	             
	            if(result.length < 2)
	            {
	            	new MessageBox("返回数据有误！");
	            	return false;
	            }
	            
	            	bld.retcode  = result[0];  //返回码2
	            	if(!bld.retcode.equals("00"))
	            		return false;
	            	bld.retmsg = result[1]; //结果描述
	            	if(type != PaymentBank.XYKYE && type != PaymentBank.XYKQD)
	            	{
		            	bld.je = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Double.parseDouble(result[2]),100),2,1);   //交易金额
//		            	bld.bankinfo = result[3];  //商户名称(此字段返回值长度太长，数据库报错，所以不记录)
		            	bld.cardno = result[5];   //卡号19
		            	bld.trace = Integer.parseInt(result[6]);   //凭证号6
		            	bld.memo = result[7];  //原交易凭证号6
		            	bld.authno = result[9]; //系统参考号12  	
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
			if (d.length() > 4)
			{
				new MessageBox("日期格式错误\n日期格式《MMDD》");
				return false;
			}
			
			return true;
		}
		
		
		
		public void XYKPrintDoc()
		{
			ProgressBox pb = null;
			String name =path + "\\print.txt";
			try
			{
				if (!PathFile.fileExist(name))
				{
					new MessageBox("找不到签购单打印文件！！！");
					
					return ;
				}
				pb = new ProgressBox();
				pb.setText("正在打印银联签购单,请等待..." + "\t OY : " + GlobalInfo.sysPara.issetprinter);
				
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
							//当程序里面读取到这个字符时，打印机切纸
							if (line.equals("CUTPAPER"))
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
				if (PathFile.fileExist(name))
				{
					PathFile.deletePath(name);
				}
			}
		}
		

	}

