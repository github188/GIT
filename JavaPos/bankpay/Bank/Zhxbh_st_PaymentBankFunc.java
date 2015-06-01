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

//北京资和信百货——商通卡——接口
//调用动态库（模块名：ZHXBH；动态库(dll文件）：ZhxComDll.dll；函数：int __stdcall BankTrans(char *StrIn,char *StrOut);）
public class Zhxbh_st_PaymentBankFunc extends PaymentBankFunc{
	    String path = null;
	    String inputway = "";
	    String JID = "";
	    String sm = "";
	    String Append1 = "";
		public String[] getFuncItem()
	    {
	        String[] func = new String[16];
	        
	        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
	        func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
	        func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
	        func[3] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
	        func[4] = "[" + PaymentBank.XYKCD + "]" + "重打签购单";
	        func[5] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
	        func[6] = "[" + PaymentBank.XYKJZ + "]" + "交易结算";
	        func[7] = "[" + PaymentBank.XKQT1 + "]" + "优惠券查询";
	        func[8] = "[" + PaymentBank.XKQT2 + "]" + "优惠券消费";
	        func[9] = "[" + PaymentBank.XKQT3 + "]" + "优惠券充值";
	        func[10] = "[" + PaymentBank.XKQT4 + "]" + "优惠券退货";
	        func[11] = "[" + PaymentBank.XKQT5 + "]" + "优惠券充值退货";
	        func[12] = "[" + PaymentBank.XKQT6 + "]" + "卡充值";
	        func[13] = "[" + PaymentBank.XKQT7 + "]" + "卡充值退货";
	        func[14] = "[" + PaymentBank.XKQT8 + "]" + "售卡";     //（批量售卡）
	        func[15] = "[" + PaymentBank.XKQT9 + "]" + "退卡";
	        
	        return func;
	    } 
		
		public boolean getFuncLabel(int type, String[] grpLabelStr)
	    {
//			if(type == PaymentBank.XKQT1)
//			{
//				String code = "";
//				String[] title = { "代码", "交易类型" };
//				int[] width = { 60, 440 };
//				Vector contents = new Vector();
//				contents.add(new String[] { "1", "优惠券查询" });
//				contents.add(new String[] { "2", "优惠券充值" });
//				contents.add(new String[] { "3", "优惠券使用退货" });
//				contents.add(new String[] { "4", "优惠券充值退货" });
//				contents.add(new String[] { "5", "卡充值" });
//				contents.add(new String[] { "6", "卡充值退货" });
//				contents.add(new String[] { "7", "售卡" });
//				contents.add(new String[] { "8", "退卡" });
//				int choice = new MutiSelectForm().open("请选择交易类型", title, width, contents, true);
//				if (choice == -1)
//				{
//					errmsg = "没有选择交易方式";
//					return false;
//				}else {
//					String[] row = (String[]) (contents.elementAt(choice));
//					code = row[0];
//					if(code.equals("2"))
//					{
//						type = PaymentBank.XKQT3;
//					}
//					if(code.equals("3"))
//					{
//						type = PaymentBank.XKQT4;
//					}
//					if(code.equals("4"))
//					{
//						type = PaymentBank.XKQT5;
//					}
//					if(code.equals("5"))
//					{
//						type = PaymentBank.XKQT6;
//					}
//					if(code.equals("6"))
//					{
//						type = PaymentBank.XKQT7;
//					}
//					if(code.equals("7"))
//					{
//						type = PaymentBank.XKQT8;
//					}
//					if(code.equals("8"))
//					{
//						type = PaymentBank.XKQT9;
//					}
//				}
//			}
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
					grpLabelStr[0] = null;
					grpLabelStr[1] = "原交易授权码";
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
	        	case PaymentBank.XKQT1: //优惠券查询 
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "优惠券查询";
	            break;
	        	case PaymentBank.XKQT2: //优惠券使用  
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "优惠券数量";
	            break;
	        	case PaymentBank.XKQT3: //优惠券充值  
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "优惠券数量";
	            break;
	        	case PaymentBank.XKQT4: //优惠券消费退货  
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = "原授权码";
	                grpLabelStr[2] = "原交易日期";
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "优惠券数量";
	            break;
	        	case PaymentBank.XKQT5: //优惠券充值退货  
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = "原授权码";
	                grpLabelStr[2] = "原交易日期";
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "优惠券数量";
	            break;
	        	case PaymentBank.XKQT6: //卡充值  
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "交易金额";
	            break;
	        	case PaymentBank.XKQT7: //卡充值退货
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = "原授权码";
	                grpLabelStr[2] = "原交易日期";
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "交易金额";
	            break;
	        	case PaymentBank.XKQT8: //批量售卡 
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = null;
	                grpLabelStr[2] = null;
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "售卡";
	            break;
	            
	        	case PaymentBank.XKQT9: //退卡
	                grpLabelStr[0] = null;
	                grpLabelStr[1] = "原授权码";
	                grpLabelStr[2] = "原交易日期";
	                grpLabelStr[3] = null;
	                grpLabelStr[4] = "退卡";
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
	                grpTextStr[4] = "按回车键开始查询";
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
			 	case PaymentBank.XKQT1: 	//优惠券查询
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = "按回车键开始查询";
	            break;
			 	case PaymentBank.XKQT2: 	//优惠券使用
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = null;
	            break;
			 	case PaymentBank.XKQT3: 	//优惠券充值
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = null;
	            break;
			 	case PaymentBank.XKQT4: 	//优惠券使用退货
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = null;
	            break;
			 	case PaymentBank.XKQT5: 	//优惠券充值退货
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = null;
	            break;
			 	case PaymentBank.XKQT6: 	//卡充值
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = null;
	            break;
			 	case PaymentBank.XKQT7: 	//卡充值退货
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = null;
	            break;
			 	case PaymentBank.XKQT8: 	//批量售卡
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = "按回车键开始售卡";
	            break;
			 	case PaymentBank.XKQT9: 	//退卡
	                grpTextStr[0] = null;
	                grpTextStr[1] = null;
	                grpTextStr[2] = null;
	                grpTextStr[3] = null;
	                grpTextStr[4] = "按回车键开始退卡";
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
					  type == PaymentBank.XKQT5 ||
					  type == PaymentBank.XKQT6 ||
					  type == PaymentBank.XKQT7 ||
					  type == PaymentBank.XKQT8 ||
					  type == PaymentBank.XKQT9 ))
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
		
			
						if(type == PaymentBank.XYKXF)
						{
							String code = "";
							String[] title = { "代码", "消费方式" };
							int[] width = { 60, 440 };
							Vector contents = new Vector();
							contents.add(new String[] { "1", "普通消费" });
							contents.add(new String[] { "2", "优惠券消费" });
							int choice = new MutiSelectForm().open("请选择消费方式", title, width, contents, true);
							if (choice == -1)
							{
								errmsg = "没有选择消费方式";
								return false;
							}else {
								String[] row = (String[]) (contents.elementAt(choice));
								code = row[0];
								if(code.equals("2"))
								{
									type = PaymentBank.XKQT2;
								}
							}
						}
						
						if(type == PaymentBank.XYKYE)
						{
							String code = "";
							String[] title = { "代码", "查询方式" };
							int[] width = { 60, 440 };
							Vector contents = new Vector();
							contents.add(new String[] { "1", "余额查询" });
							contents.add(new String[] { "2", "优惠券查询" });
							int choice = new MutiSelectForm().open("请选择消费方式", title, width, contents, true);
							if (choice == -1)
							{
								errmsg = "没有选择消费方式";
								return false;
							}else {
								String[] row = (String[]) (contents.elementAt(choice));
								code = row[0];
								if(code.equals("2"))
								{
									type = PaymentBank.XKQT1;
								}
							}
						}
				
						if(type == PaymentBank.XKQT4)
						{
							String code = "";
							String[] title = { "代码", "优惠券退货方式" };
							int[] width = { 60, 440 };
							Vector contents = new Vector();
							contents.add(new String[] { "1", "优惠券退货" });
							contents.add(new String[] { "2", "优惠券充值退货" });
							int choice = new MutiSelectForm().open("请选择优惠券退货方式", title, width, contents, true);
							if (choice == -1)
							{
								errmsg = "没有选择优惠券退货方式";
								return false;
							}else {
								String[] row = (String[]) (contents.elementAt(choice));
								code = row[0];
								if(code.equals("2"))
								{
									type = PaymentBank.XKQT5;
								}
							}
						}
					
						
//						多种输入方式，选择
						if(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX ||
						   type == PaymentBank.XYKTH || type == PaymentBank.XYKYE ||
						   type == PaymentBank.XKQT1 || type == PaymentBank.XKQT2 ||
						   type == PaymentBank.XKQT3 || type == PaymentBank.XKQT4 ||
						   type == PaymentBank.XKQT5 )
						{
							String code = "";
							String[] title = { "代码", "MISPOS输入方式" };
							int[] width = { 60, 440 };
							Vector contents = new Vector();
							contents.add(new String[] { "1", "MISPOS刷卡" });
							contents.add(new String[] { "2", "MISPOS扫码" });
							contents.add(new String[] { "3", "MISPOS输串码" });
							
							int choice = new MutiSelectForm().open("请选择MISPOS输入方式", title, width, contents, true);
							if (choice == -1)
							{
								errmsg = "没有选择输入方式";
								return false;
							}else {
								String[] row = (String[]) (contents.elementAt(choice));
								code = row[0];
								if(code.equals("2") || code.equals("3"))
								{
									code = row[0];
								}
								inputway = code;
							}
							
							if(inputway.equals("2"))
							{
								StringBuffer bf = new StringBuffer();
					            TextBox txt = new TextBox();
					            	if (!txt.open("请扫码", "PASSWORD", "仅扫一个", bf, 0, 0, false, -1))
					    			{
					    				return true;
					    			}
					            	sm = bf.toString();
					            	if(sm.length()<= 60)
					            	{
					            		sm = "9F" + Convert.increaseCharForward(String.valueOf(sm.length()), '0', 3) + sm;
					            	}
					            	else{
					            		new MessageBox("扫码有误");
					            		return false;
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
	                case  PaymentBank.XKQT1: 	// 优惠券查询
	                	type1 = "08";
	                break;
	                case  PaymentBank.XKQT2: 	// 优惠券使用
	                	type1 = "09";
	                break;
	                case  PaymentBank.XKQT3: 	// 优惠券充值	
	                	type1 = "10";
	                break;
	                case  PaymentBank.XKQT4: 	// 优惠券使用退货
	                	type1 = "11";
	                break;
	                case  PaymentBank.XKQT5: 	// 优惠券充值退货
	                	type1 = "12";
	                break;
	                case  PaymentBank.XKQT6: 	// 卡充值
	                	type1 = "13";
	                break;
	                case  PaymentBank.XKQT7: 	// 卡充值退
	                	type1 = "14";
	                break;
	                case  PaymentBank.XKQT8: 	// 售卡（批量售卡）
	                	type1 = "16";
	                break;
	                default:
	                    type1 = "17";			// 退卡
	                break;
				 }
		         
		         type1 = Convert.increaseChar(type1, ' ', 3); 

		         String appType = Convert.increaseChar("3", ' ', 2); //应用类型（固定为商通）
		         
		         inputway = Convert.increaseChar(inputway, ' ', 2); //输入方式
		         
		         String strack2 = Convert.increaseChar(track2, ' ', 38); //二磁道数据
		         
		         String strack3 = Convert.increaseChar(" ", ' ', 105); //三磁道数据
		         
		         
				 String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
		         jestr = Convert.increaseCharForward(jestr,'0',12);
		         jestr = Convert.increaseChar(jestr, ' ', 13);        //交易金额
		         
		         String authno = "";							//流水号(撤销、退货（或脱机退货）、重打印) || 授权码（隔日退货）  			
		         String AuthId = "";                            //  授权码（隔日退货）     ||     流水号(撤销、退货（或脱机退货）、重打印)                
		         if(type == PaymentBank.XYKCX || type == PaymentBank.XYKCD || type == PaymentBank.XKQT3 )
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

		         }else if(type == PaymentBank.XYKTH || type == PaymentBank.XKQT4 || type == PaymentBank.XKQT5 || type == PaymentBank.XKQT7 || type == PaymentBank.XKQT9 )
		         {
		        	 	AuthId = Convert.increaseCharForward(oldauthno, '0', 6);
		        	 	AuthId = Convert.increaseChar(AuthId, ' ', 7);
		        	 	authno = Convert.increaseChar("", ' ', 7);
		         }
		         else
		         {
		        	 authno = Convert.increaseChar("", ' ', 7);
		         	 AuthId = Convert.increaseChar("", ' ', 7);	
		         }
     	 
			     String seqno = Convert.increaseChar("", ' ', 13);  //参考号
				  
				 String BatchNo = Convert.increaseChar("", ' ', 7);  //批次号
                
		         String date = Convert.increaseChar(olddate, ' ', 5); //交易日期

		         String syjh = Convert.increaseChar(ConfigClass.CashRegisterCode,' ', 9);
		         
		         String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh,' ', 10);
		         
		         String Append = "";                         //附件信息
		         if(type == PaymentBank.XKQT1 || type == PaymentBank.XKQT2 || type == PaymentBank.XKQT3 ||
		        	type == PaymentBank.XKQT4 || type == PaymentBank.XKQT5 )
		         {
			            //输入劵ID
							StringBuffer bf = new StringBuffer();
			            	TextBox txt = new TextBox();
			            	if (!txt.open("请输入劵ID", "劵ID", "请填写正确的劵ID", bf, 0, 0, false, -1))
			    			{
//			    				return true;
			    			}
			            	if(bf.length() <= 0 || bf == null)
			            	{
				            	Append = Convert.increaseChar(sm,' ', 170);
			            	}else
			            	{
				            	JID = bf.toString();
				            	JID = "9Z" + Convert.increaseCharForward(String.valueOf(JID.length()), '0', 3) + JID;
				            	Append = Convert.increaseChar(sm+JID,' ', 170);
			            	}
		         }
		         else if(inputway.equals("2 ") && (type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XYKTH || type == PaymentBank.XYKYE))
		        {
		        	 Append = Convert.increaseChar(sm,' ', 170);  
		        }
		        else if(type == PaymentBank.XKQT8 || type == PaymentBank.XKQT9)
		        {
		        	StringBuffer bf = new StringBuffer();
		        	StringBuffer bf1 = new StringBuffer();
		            TextBox txt = new TextBox();
		            TextBox txt1 = new TextBox();
		            
		            	if (!txt.open("请扫码", "PASSWORD", "最多扫5个(多个码之间必须按 + 号键)", bf, 0, 0, false, -1))
		    			{
//		    				return true;
		    			}
		            	sm = bf.toString();
		            	
		            	if (!txt1.open("请输入劵ID", "劵ID", "请填写正确的劵ID", bf1, 0, 0, false, -1))
		    			{
//		    				return true;
		    			}
		            	JID = bf1.toString();
		            	
		            	
		            	if(sm.length()<= 170)
		            	{
//			            	if(bf.length() <= 0 || bf == null)
//			            	{
//			            		sm = "9F" + Convert.increaseCharForward(String.valueOf(sm.length()), '0', 3) + sm;
//				            	Append = Convert.increaseChar(sm,' ', 170);
//			            	}else
//			            	{
				            	JID = "9Z" + Convert.increaseCharForward(String.valueOf(JID.length()), '0', 3) + JID;
				            	sm = "9F" + Convert.increaseCharForward(String.valueOf(sm.length()), '0', 3) + sm;
				            	Append = Convert.increaseChar(sm+JID,' ', 170);
//			            	}
		            	}
		            	else{
		            		new MessageBox("扫码有误");
		            		return false;
		            	}
		        }
		        else
		        {
		        	 Append = Convert.increaseChar("",' ', 170);  
		        }
		         Append = Append.replaceAll("[+]", "=");
		         
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
	            
	            bld.memo		= result[0];
	            
	            int type = Integer.parseInt(bld.type.trim());
	             
	            if (result.length >= 2)
	            {
	            	bld.retcode  = result[1].substring(3,6).trim();  //返回码
	            	if(!bld.retcode.equals("00"))
	            	{
	            		bld.retmsg = Convert.newSubString(result[1], 6, 47).trim();   //错误说明
	            		return false;
	            	}
	            	if(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XYKTH )
	            	{
	                	bld.cardno = Convert.newSubString(result[1], 47, 67).trim();   //卡号20
//	                	if(type == PaymentBank.XYKCD || type == PaymentBank.XYKCX || type == PaymentBank.XKQT3)
//	                	{
//	                		bld.trace = Integer.parseInt(Convert.newSubString(result[1], 163, 170).trim());   //流水号
//	                	}else
//	                		bld.trace = Integer.parseInt(Convert.newSubString(result[1], 156, 163).trim());   //授权号
	                	
	                	
	                	bld.je = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Double.parseDouble(Convert.newSubString(result[1], 184, 197).trim() ),100),2,1);   //交易金额			
	                	
//	                	bld.authno = Convert.newSubString(result[1], 143, 156).trim();  //参考号
	                	
	            	}   
	            	bld.trace = Convert.toInt(Convert.newSubString(result[1], 170, 177).trim());   //凭证号
	            	
	            	Append1 = Convert.newSubString(result[1], 200, 524);
	            	PosLog.getLog(this.getClass()).info(Append1);
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
				  type == PaymentBank.XYKCD	|| type == PaymentBank.XKQT2 ||
				  type == PaymentBank.XKQT3	|| type == PaymentBank.XKQT4 ||
				  type == PaymentBank.XKQT5	|| type == PaymentBank.XKQT6 ||
				  type == PaymentBank.XKQT7	|| type == PaymentBank.XKQT8 ||
				  type == PaymentBank.XKQT9 )
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
				pb = new ProgressBox();
				pb.setText("正在打印,请等待..." + "\t OY : " + GlobalInfo.sysPara.issetprinter);

				XYKPrintDoc_Start();
				for (int i = 0; i < GlobalInfo.sysPara.bankprint; i ++)
				{
					PosLog.getLog(this.getClass()).info(String.valueOf(i) + "【ST】start");
					BufferedReader br = null;
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
										
						}
						
						
						//打印二维码
		            	if((type == PaymentBank.XKQT8 || type == PaymentBank.XYKCD) && !Append1.equals("") && Append1 != null)
		            	{
		            		for(int x = 0 ;x<5 ;x++)
		            		{
		            			if(Convert.newSubString(Append1, 60*x, 60*(x+1)).trim() != null  &&  !Convert.newSubString(Append1, 60*x, 60*(x+1)).trim().equals(""))
		            			{
									XYKPrintDoc_Print(Convert.appendStringSize("", Convert.newSubString(Append1, 0+60*x, 24+60*x), 0, 37, 38));
									XYKPrintDoc_Print("#Qrcode:" + Convert.newSubString(Append1, 24+60*x, 60+60*x));
		            			}		            			
		            		}
		            		PosLog.getLog(this.getClass()).info("Append1：" + Append1);
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
					//XYKPrintDoc_End();
					PosLog.getLog(this.getClass()).info(String.valueOf(i) + "【ST】end");
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
				XYKPrintDoc_End();
				if (pb != null)
				{
					pb.close();
					pb = null;
					Append1 = "";
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
				PosLog.getLog(this.getClass()).info("XYKPrintDoc【ST】_Start");
				if (onceprint)
				{
					if(Printer.getDefault().getStatus()){
						PosLog.getLog(this.getClass()).info("printer.close【ST】_Start");
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

