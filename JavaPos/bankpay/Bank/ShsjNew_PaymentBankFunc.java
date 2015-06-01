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

//万达烟台店金卡工程   
//调用动态库（模块名：SHSJ；动态库(dll文件）：libsand.dll；函数：void card_trans(int com,char * pUpdata,char * pDownData);）
public class ShsjNew_PaymentBankFunc extends PaymentBankFunc {
	
	String path = null;
	
	public String[] getFuncItem()
    {
        String[] func = new String[9];
        
        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
        func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
        func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
        func[3] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
        func[4] = "[" + PaymentBank.XKQT1 + "]" + "初始化";
        func[5] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
        func[6] = "[" + PaymentBank.XYKJZ + "]" + "交易结算";
        func[7] = "[" + PaymentBank.XYKCD + "]" + "重打印任意笔签单";
        func[8] = "[" + PaymentBank.XKQT2 + "]" + "重打印上笔签单";
        
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
        	case PaymentBank.XKQT1: //初始化
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "初始化";
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
        	case PaymentBank.XYKCD: //重打印任意笔
                grpLabelStr[0] = null;
                grpLabelStr[1] = "流水号";
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "重打签购单";
            break;
        	case PaymentBank.XKQT2: //重打印上笔
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "重打签购单";
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
		 	case PaymentBank.XKQT1: 	//初始化 
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始初始化";
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
		 	case PaymentBank.XYKCD: 	//交易结算
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始重打签购单";
            break;
		 	case PaymentBank.XKQT2: 	//初始化 
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始重打上笔签单";
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
				  type == PaymentBank.XYKCD ||
				  type == PaymentBank.XYKYE ||
				  type == PaymentBank.XKQT2 ||
				  type == PaymentBank.XKQT1 ))
				{			
					  new MessageBox("银联接口不支持此交易类型！！！");
					  
					  return false;
			    }
			
//			获得金卡文件路径
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
			if (PathFile.fileExist(path + "\\print\\sand_print.txt"))
            {
                PathFile.deletePath(path + "\\print\\sand_print.txt");
            }

			
            // 写入请求数据
            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
            {
                return false;
            }
                       
            
            //  调用接口模块
            if (PathFile.fileExist(path+"\\javaposbank.exe"))
            {
            	CommonMethod.waitForExec(path+"\\javaposbank.exe SHSJ","javaposbank");
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
			
            //打印签购单(银联打印)
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
                    type1 = "30";
                break;
			 	case PaymentBank.XYKCX: 	// 消费撤销
			 		type1 = "40";
			 	break;	
                case PaymentBank.XYKTH:		// 隔日退货
                	type1 = "50";
                break;
                case  PaymentBank.XYKYE: 	// 余额查询
                	type1 = "80";
                break;	
                case  PaymentBank.XKQT1: 	// 初始化
                	type1 = "90";
                break;
                case  PaymentBank.XYKQD: 	// 交易签到
                	type1 = "91";
                break;
                case  PaymentBank.XYKJZ: 
                    type1 = "92";			// 交易结算
                break;
                case  PaymentBank.XYKCD: 
                    type1 = "10";			// 重打印任意笔
                break;
                case  PaymentBank.XKQT2: 	
                	type1 = "10";          // 重打印上笔
                break;
			 }
//				char OperateType	[2];		//操作类型
//				char TransType	[2];			//交易类型
//				char CardType		[2];		//卡类型
//				char CashRegNo	[6];			//收银机编号
//				char CasherNo		[6];		//操作员工号
//				char Amount		[12];			//交易金额
//				char CashTraceNo	[6];		//收银流水号
//				char OriginTrace	[6];		//原交易流水号，撤销交易时使用
//				char Reserved      [48];		//预留字段

			 String OperateType = "A0";  
			 
			 String CardType = "00";
			 
	         String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh,' ', 6);
	         
	         String syjh = Convert.increaseChar(ConfigClass.CashRegisterCode,' ', 6);
	         
			 String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
	         jestr = Convert.increaseCharForward(jestr,'0',12);
	         
	         String CashTraceNo = Convert.increaseChar("", ' ', 6); 
	         
	         String authno = Convert.increaseCharForward(oldauthno, '0', 6); //流水号   
	         
	         if(type == PaymentBank.XKQT2)   //重打印上笔则传入6个空格
	         {
	        	 authno = Convert.increaseCharForward("", ' ', 6); //流水号
	         }
	         		        
	         String Reserved = "";
	         if(type == PaymentBank.XYKTH)
	         {
	        	 oldseqno = Convert.increaseChar(oldseqno, ' ', 12); //参考号12
	        	 if(olddate.length()>4)
	        	 {
	        		 olddate = olddate.substring(olddate.length()-4);
	        	 }
	        	 Reserved = Convert.increaseChar(oldseqno + olddate, ' ', 48);   
	         }
	         else
	         {
	        	 Reserved = Convert.increaseChar("", ' ', 48);
	         }
	         
	         line = OperateType + type1 + CardType + syjh + syyh + jestr + CashTraceNo + authno + Reserved;

	         
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
           
            
            int type = Integer.parseInt(bld.type.trim());
             
            if (line.length() >= 2)
            {
    			//当银行返回字符以字节计算，而字符中汉字出现导致line的长度和字节书不等时，汉字后面的内容倒着计算位置
    			// int len = line.length();
    			//防止字节和字符不一致问题
    			//bld.memo = Convert.newSubString(line, 2, 21);
            	
            	bld.retcode  = Convert.newSubString(line, 6, 8);  //返回码2
            	if(!bld.retcode.equals("00"))
            	{
            		bld.retmsg = Convert.newSubString(line, 8, 48).trim();   //错误说明
            		return false;
            	}
            	if(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX ||  type == PaymentBank.XYKTH )
            	{
                	bld.cardno = Convert.newSubString(line, 141, 160).trim();   //卡号20
//                	bld.trace = Integer.parseInt(Convert.newSubString(line, 220 ,226));   //流水号16
                	bld.je = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Convert.toDouble(Convert.newSubString(line, 60 ,72)),100),2,1);   //交易金额		
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
	
	public boolean XYKNeedPrintDoc(int type)
	{
		if (!checkBankSucceed())
	    {
	        return false;
	    }
		if (  type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XYKCD ||
			  type == PaymentBank.XYKTH || type == PaymentBank.XYKJZ || type == PaymentBank.XKQT2)
		{
			return true;
		}
		else
			return false;
	}
	
	public void XYKPrintDoc()
	{
		ProgressBox pb = null;
		String name =path + "\\print\\sand_print.txt";
		try
		{
			if (!PathFile.fileExist(name))
			{
				new MessageBox("找不到签购单打印文件！！！");
				
				return ;
			}
			pb = new ProgressBox();
			
//			pb.setText("OY+状态 : "+GlobalInfo.sysPara.issetprinter +"...."+ Printer.getDefault().getStatus());
			pb.setText("正在打印银联签购单,请等待...");
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
