package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

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
////迪诺水镇购物中心银联卡接口
public class Dnsz1_PaymentBankFunc extends PaymentBankFunc
{
	String path = null;
//	String fq = "";
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
//        func[7] = "[" + PaymentBank.XKQT1 + "]" + "分期付款";
//        func[8] = "[" + PaymentBank.XKQT2 + "]" + "分期撤销";
//        func[9] = "[" + PaymentBank.XKQT3 + "]" + "分期退货";
        
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
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易金额";
            break;
        	case PaymentBank.XYKTH://隔日退货   
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
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
//        	case PaymentBank.XKQT1: //分期付款    
//                grpLabelStr[0] = null;
//                grpLabelStr[1] = null;
//                grpLabelStr[2] = null;
//                grpLabelStr[3] = null;
//                grpLabelStr[4] = "交易金额";
//            break;
//        	case PaymentBank.XKQT2: //分期撤销    
//                grpLabelStr[0] = null;
//                grpLabelStr[1] = null;
//                grpLabelStr[2] = null;
//                grpLabelStr[3] = null;
//                grpLabelStr[4] = "交易金额";
//            break;
//          	case PaymentBank.XKQT3: //分期退货    
//                grpLabelStr[0] = null;
//                grpLabelStr[1] = null;
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
		 	case PaymentBank.XYKQD: 	// 签到
		        grpTextStr[0] = null;
		        grpTextStr[1] = null;
		        grpTextStr[2] = null;
		        grpTextStr[3] = null;
		        grpTextStr[4] = "按回车键开始签到";
		    break;
		 	case PaymentBank.XYKJZ: 	// 结算
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始结算";
            break;
//		 	case PaymentBank.XKQT1:		//分期付款   
//				grpTextStr[0] = null;
//				grpTextStr[1] = null;
//				grpTextStr[2] = null;
//				grpTextStr[3] = null;
//				grpTextStr[4] = null;
//			break;
//		 	case PaymentBank.XKQT2: 	// 分期撤销
//		        grpTextStr[0] = null;
//		        grpTextStr[1] = null;
//		        grpTextStr[2] = null;
//		        grpTextStr[3] = null;
//		        grpTextStr[4] = null;
//		    break;
//		 	case PaymentBank.XKQT3: 	// 分期退货
//                grpTextStr[0] = null;
//                grpTextStr[1] = null;
//                grpTextStr[2] = null;
//                grpTextStr[3] = null;
//                grpTextStr[4] = null;
//            break;
		}
		
		return true;
    }
	
	//小票付款方式栏没有付款方式名称,重载
//	public boolean getReplaceBankNameMode()
//	{
//		return false;
//	}
	
	
	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{
		
		try
		{
			if (!(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || 
				  type == PaymentBank.XYKTH || type == PaymentBank.XYKYE || 
			      type == PaymentBank.XYKCD || type == PaymentBank.XYKQD ||
			      type == PaymentBank.XYKJZ || type == PaymentBank.XKQT1 ||
			      type == PaymentBank.XKQT2 || type == PaymentBank.XKQT3 ))
	            {
	                errmsg = "银联接口不支持该交易";
	                new MessageBox(errmsg);

	                return false;
	            }
//			path = ConfigClass.BankPath;
			path = getBankPath(paycode);
			 // 先删除上次交易数据文件
            if (PathFile.fileExist(path + "\\request.txt"))
            {
                PathFile.deletePath(path + "\\request.txt");
                
                if (PathFile.fileExist(path + "\\request.txt"))
                {
            		errmsg = "交易请求文件request.txt无法删除,请重试";
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
            		errmsg = "交易请求文件result.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            if (PathFile.fileExist(path + "\\toprint.txt"))
            {
                PathFile.deletePath(path + "\\toprint.txt");
            }
            
            
            
//			//////////选择：消费、分期付款/////////////
//			if(type == PaymentBank.XYKXF)
//			{
//				//多种支付方式，选择
//				String code = "";
//				String[] title = { "代码","消费类型 "};
//				int[] width = { 60, 440 };
//				Vector contents = new Vector();
//				contents.add(new String[] { "1", "普通消费" });
//				contents.add(new String[] { "2", "分期消费" });
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
//						type = PaymentBank.XKQT1;
//					}
//				}
//			}
//            
//			//////////选择：撤销、分期撤销/////////////
//			if(type == PaymentBank.XYKCX)
//			{
//				//多种支付方式，选择
//				String code = "";
//				String[] title = { "代码","消费类型 "};
//				int[] width = { 60, 440 };
//				Vector contents = new Vector();
//				contents.add(new String[] { "1", "普通撤销" });
//				contents.add(new String[] { "2", "分期撤销" });
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
//						type = PaymentBank.XKQT2;
//					}
//				}
//			}
//            
//			//////////选择：退货、分期退货/////////////
//			if(type == PaymentBank.XYKTH)
//			{
//				//多种支付方式，选择
//				String code = "";
//				String[] title = { "代码","消费类型 "};
//				int[] width = { 60, 440 };
//				Vector contents = new Vector();
//				contents.add(new String[] { "1", "普通退货" });
//				contents.add(new String[] { "2", "分期退货" });
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
//						type = PaymentBank.XKQT3;
//					}
//				}
//			}
//            
//            
//            
//            //输入分期期数
//			StringBuffer bf = new StringBuffer();
//            if(type == PaymentBank.XKQT1 || type == PaymentBank.XKQT2 || type == PaymentBank.XKQT3)
//            {
//            	TextBox txt = new TextBox();
//            	if (!txt.open("请刷分期付款的期数", "分期期数", "请填正整数", bf, 0, 0, false, -1))
//    			{
//    				return true;
//    			}
//            	fq = bf.toString();
//            }

            
            // 写入请求数据
            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
            {
                return false;
            }
            
                // 调用接口模块
                if (PathFile.fileExist(path + "\\javaposbank.exe"))
                {
                	CommonMethod.waitForExec(path + "\\javaposbank.exe RKYS","javaposbank");
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
            
           
            // 	打印签购单
            if (XYKNeedPrintDoc())
            {
            	
//                XYKPrintDoc();
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
	
	public boolean XYKNeedPrintDoc()
	{
		int type = Integer.parseInt(bld.type.trim());
		
		if(type == PaymentBank.XYKYE || type == PaymentBank.XYKQD)
		{
			return false;
		}
		
		if (!checkBankSucceed())
        {
			
            return false;
        }
		
		if ((type == PaymentBank.XYKXF) || (type == PaymentBank.XYKCX) || 
			(type == PaymentBank.XYKTH) || (type == PaymentBank.XYKYE) || 
			(type == PaymentBank.XYKCD) || (type == PaymentBank.XKQT1) ||
			(type == PaymentBank.XKQT2) || (type == PaymentBank.XKQT3))
        {
			return true;
        }
		else
		{
			return false;
		}
	}
	
	public boolean XYKCheckRetCode()
	{
		if (bld.retcode.trim().equals("1") && bld.memo.trim().equals("00"))
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
			 		type1 = "07";
			 	break;	
                case PaymentBank.XYKTH:		// 隔日退货
                	type1 = "08";
                break;
                case  PaymentBank.XYKYE: 	// 余额查询
                	type1 = "06";
                break;	
                case  PaymentBank.XYKCD:
                    type1 = "ff";			// 重打签购单
                break;
                case  PaymentBank.XYKQD:
                    type1 = "ff";			// 签到
                break;
                case  PaymentBank.XYKJZ:
                    type1 = "ff";			// 结算
                break;
                case  PaymentBank.XKQT1:
                    type1 = "12";			// 分期付款
                break;
                case  PaymentBank.XKQT2:
                    type1 = "13";			// 分期撤销
                break;
                case  PaymentBank.XKQT3:
                    type1 = "14";			// 分期退货
                break;
			 }
			 
			 String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
	         jestr = Convert.increaseCharForward(jestr,'0',12);
	         
	         String fphm = Convert.increaseChar(String.valueOf(GlobalInfo.syjStatus.fphm),' ',16);
	         
	         String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh,' ', 8);
	         
	         String syjh = Convert.increaseChar(ConfigClass.CashRegisterCode,' ', 8);
	         
	         String seqno = Convert.increaseCharForward("0",'0', 12); //检索号
	         String date = Convert.increaseCharForward("0",'0', 8);  //原交易日期
	         String terminal = Convert.increaseCharForward("0",'0', 15);  //原交易终端号	
	         String trace = Convert.increaseCharForward("0",'0', 6);  //原交易流水号
	         String sq = Convert.increaseCharForward("0",'0', 6);  //原交易授权号
	         String fqno = "";                                   //分期期数
//	         if(type == PaymentBank.XKQT1 || type == PaymentBank.XKQT2 || type == PaymentBank.XKQT3)
//	         {
//	        	fqno = Convert.increaseCharForward(fq,'0', 2);  
//	         }else
	        	fqno = Convert.increaseCharForward("0",'0', 2);  
	         
	         
	         line = type1 + jestr + syjh + syyh + fphm + seqno + date + terminal + trace + sq + fqno;
	     
	         try
	         {
	            pw = CommonMethod.writeFile(path + "\\request.txt");
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
	
	public boolean XYKReadResult()
	{
		BufferedReader br = null;
		
		try
        {
			if (!PathFile.fileExist(path + "\\result.txt") || ((br = CommonMethod.readFileGBK(path + "\\result.txt")) == null))
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
            
            String result[] = line.split(",");
            if (result == null) return false;
            
            bld.retcode		= result[0];
            
            int type = Integer.parseInt(bld.type.trim());
             
            if (result.length >= 2)
            {
            	bld.memo     = result[1].substring(0,2);
            	
            	if(type != PaymentBank.XYKYE && type != PaymentBank.XYKQD && type != PaymentBank.XYKJZ && type != PaymentBank.XYKCD){
            		bld.cardno 		= result[1].substring(14,33);
            		bld.je	   		= ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Double.parseDouble(result[1].substring(2,14) ),100),2,1);
            		if(result[1].length() > 45)
            		{
            			bld.trace = Convert.toLong(result[1].substring(33,45));
            		}

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
	
	public void XYKPrintDoc()
	{
		ProgressBox pb = null;
		String name = null;
		name =path + "\\toprint.txt";

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
			if (PathFile.fileExist(name))
			{
				PathFile.deletePath(name);	
			}
		}
	}
}
