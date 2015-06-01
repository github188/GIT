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
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

//新华都东海湾购物广场金卡工程
//调用动态库（模块名：BANKCBC；动态库(dll文件）：BankPos.dll；函数：int bankcbc(char *str1,char *str2)）

public class Xhd_PaymentBankFunc extends PaymentBankFunc {
	
	String path="";
	String fq ="  "; 
	
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
        
//        func[7] = "[" + PaymentBank.XKQT1 + "]" + "积分消费";
//        func[8] = "[" + PaymentBank.XKQT2 + "]" + "分期消费";
//        func[9] = "[" + PaymentBank.XKQT3 + "]" + "积分撤销";
//        func[10] = "[" + PaymentBank.XKQT4 + "]" + "分期撤销";
             
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
                grpLabelStr[1] = "原交易终端流水";
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
                grpLabelStr[1] = "原交易终端流水";
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
//        	case PaymentBank.XKQT1: //	积分消费
//        		grpLabelStr[0] = null;
//        		grpLabelStr[1] = null;
//        		grpLabelStr[2] = null;
//        		grpLabelStr[3] = null;
//        		grpLabelStr[4] = "交易金额";
//        	break;
//        	case PaymentBank.XKQT2: //	分期消费
//        		grpLabelStr[0] = null;
//        		grpLabelStr[1] = null;
//        		grpLabelStr[2] = null;
//        		grpLabelStr[3] = null;
//        		grpLabelStr[4] = "交易金额";
//        	break;
//        	case PaymentBank.XKQT3: //消费撤销
//                grpLabelStr[0] = null;
//                grpLabelStr[1] = "原交易终端流水";
//                grpLabelStr[2] = null;
//                grpLabelStr[3] = null;
//                grpLabelStr[4] = "交易金额";
//            break;
//        	case PaymentBank.XKQT4: //消费撤销
//                grpLabelStr[0] = null;
//                grpLabelStr[1] = "原交易终端流水";
//                grpLabelStr[2] = null;
//                grpLabelStr[3] = null;
//                grpLabelStr[4] = "交易金额";
//            break;
        	
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
//		 	case PaymentBank.XKQT1: 	// 积分消费
//		        grpTextStr[0] = null;
//		        grpTextStr[1] = null;
//		        grpTextStr[2] = null;
//		        grpTextStr[3] = null;
//		        grpTextStr[4] = null;
//		    break;
//		 	case PaymentBank.XKQT2: 	// 分期消费
//		        grpTextStr[0] = null;
//		        grpTextStr[1] = null;
//		        grpTextStr[2] = null;
//		        grpTextStr[3] = null;
//		        grpTextStr[4] = null;
//		    break;
//		 	case PaymentBank.XKQT3: 	// 积分撤销
//                grpTextStr[0] = null;
//                grpTextStr[1] = null;
//                grpTextStr[2] = null;
//                grpTextStr[3] = null;
//                grpTextStr[4] = null;
//            break;
//		 	case PaymentBank.XKQT4: 	// 分期撤销
//                grpTextStr[0] = null;
//                grpTextStr[1] = null;
//                grpTextStr[2] = null;
//                grpTextStr[3] = null;
//                grpTextStr[4] = null;
//            break;
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
			
//			获得金卡文件路径
			path = ConfigClass.BankPath;
//			path = getBankPath(paycode);
			
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
			
			
//			//////////选择：消费、积分消费、分期消费/////////////
//			if(type == PaymentBank.XYKXF)
//			{
//				//多种支付方式，选择
//				String code = "";
//				String[] title = { "代码","消费类型 "};
//				int[] width = { 60, 440 };
//				Vector contents = new Vector();
//				contents.add(new String[] { "1", "消费" });
//				contents.add(new String[] { "2", "积分消费" });
//				contents.add(new String[] { "3", "分期消费" });
//				int choice = new MutiSelectForm().open("请选择消费类型", title, width, contents, true);
//				if (choice == -1)
//				{
//					errmsg = "没有选择消费类型";
//					return false;
//				}else {
//					String[] row = (String[]) (contents.elementAt(choice));
//					code = row[0];
//					if(code.equals("2"))
//					{
//						type = PaymentBank.XKQT1;  //积分消费
//					}
//					else if(code.equals("3"))
//					{
//						type = PaymentBank.XKQT2;  //分期消费
//					}
//				}
//			}
//			
//	//////////选择：撤销、积分撤销、分期撤销/////////////
//				if(type == PaymentBank.XYKCX)
//				{
//					//多种支付方式，选择
//					String code = "";
//					String[] title = { "代码","消费类型 "};
//					int[] width = { 60, 440 };
//					Vector contents = new Vector();
//					contents.add(new String[] { "1", "撤销" });
//					contents.add(new String[] { "2", "积分撤销" });
//					contents.add(new String[] { "3", "分期撤销" });
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
//							type = PaymentBank.XKQT3;  //积分撤销
//						}
//						else if(code.equals("3"))
//						{
//							type = PaymentBank.XKQT4;  //分期撤销
//						}
//					}
//				}
//			
//            //输入分期期数
//			if(type == PaymentBank.XKQT2)
//			{
//				StringBuffer bf = new StringBuffer(); 
//	            TextBox txt = new TextBox();
//	            if (!txt.open("请刷分期付款的期数", "分期期数", "请填正整数", bf, 0, 0, false, -1))
//	    		{
//	    			return true;
//	    		}
//	            fq = bf.toString();
//	            
//			}
			

			
			
            // 写入请求数据
            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
            {
                return false;
            }
            //  调用接口模块
            if (PathFile.fileExist(path+"\\javaposbank.exe"))
            {
            	CommonMethod.waitForExec(path+"\\javaposbank.exe BANKCBC","javaposbank");
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
				XYKPrintDoc();
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
//                case  PaymentBank.XKQT1: 	// 积分消费
//                    type1 = "10";			
//                break;
//                case  PaymentBank.XKQT3: 	// 积分撤销
//                    type1 = "11";			
//                break;
//                case  PaymentBank.XKQT2: 	// 分期消费
//                    type1 = "12";			
//                break;
//                case  PaymentBank.XKQT4: 	// 分期撤销
//                    type1 = "13";			
//                break;
			 }
			 
			 /*
			  * 交易类型   2位   （'00'-消费  '01'-撤销  '02'-退货  '03'-查余额  '04'重打指定流水  '05'签到 '06'结算）
			  * 交易金额   12位 
			  * MIS流水号  6位     （撤销  退货  重印用）  
			  * 原交易终端流水  6位    重打印时用  如果空，表示重打上一笔
			  * 原交易日期   4位       退货用
			  * 原交易参考号   12位    退货用
			  * 期数   2位     分期功能时使用
			  * 操作员号    15位
			  * 柜台号   15位
			  * 
			  */
			 
			 String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));//交易金额
		     jestr = Convert.increaseCharForward(jestr,'0',12);
		     
		     String mistrace = "      ";//Mis流水号
		     
		     String authno = Convert.increaseCharForward(oldauthno, '0', 6); //流水号//原交易终端流水
		     
		     String date = Convert.increaseChar(olddate, '0', 4); //交易日期
		     
		     String seqno = Convert.increaseCharForward(oldseqno, '0', 12); //参考号

	         String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh,' ', 15);//操作员号
	         	         	         
	         String syjh = Convert.increaseChar(ConfigClass.CashRegisterCode,' ', 15);//柜台号
	                             	        
	         //收银机号+操作员号+交易类型+金额+原交易日期+原交易参考号+流水号+二磁道数据+三磁道数据+交易校验数据
	         line = type1 + jestr + mistrace + authno + date + seqno +  fq + syyh + syjh ;
	     	         
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
		    			
		            	bld.retcode = Convert.newSubString(result[1], 0, 2);
		            	if(!bld.retcode.equals("00"))
		            	{
//		            		bld.retmsg = bld.retcode + "," + Convert.newSubString(result[1], 48, 88).trim();   //错误说明
		            		bld.retmsg = bld.retcode + "," + Convert.newSubString(result[1], 218);   //错误说明
		            		
		            		return false;
		            	}
		            	if(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX ||  type == PaymentBank.XYKTH )
		            	{
		                	bld.cardno = result[1].substring(149,169);   //卡号20
		                	bld.trace = Convert.toInt((result[1].substring(134,140)));   //流水号6
		                	bld.je = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Convert.toDouble(Convert.newSubString(result[1], 2, 14)),100),2,1);   //交易金额		
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
			
			public void XYKPrintDoc()
			{
				ProgressBox pb = null;
				String name =path + "\\receipt.txt";
				try
				{
					if (!PathFile.fileExist(name))
					{
						new MessageBox("找不到签购单打印文件！！！");
						
						return ;
					}
					
			        // if( new MessageBox("开始打印签购单！！" + "\n OY : " + GlobalInfo.sysPara.issetprinter).verify() == GlobalVar.Key1);
					
					pb = new ProgressBox();
					//后面显示OY参数值，以便查看
					pb.setText("正在打印签购单文件，请等待。。。" + "\n OY : " + GlobalInfo.sysPara.issetprinter);
					
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
					if (PathFile.fileExist(name))
					{
						PathFile.deletePath(name);
					}
				}
			}


}
