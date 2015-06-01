package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Vector;

import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

//万达百货杭州拱墅店金卡工程
//调用动态库（模块名：HZGS；动态库(dll文件）：KeeperClient.dll ；函数：int __stdcall MisPosInterface (void* input, void* output)）
public class Hzgs_PaymentBankFunc extends PaymentBankFunc
{
	String path = null;
	
	public String[] getFuncItem()
    {
        String[] func = new String[8];
        
        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
        func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
        func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
        func[3] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
        func[4] = "[" + PaymentBank.XYKCD + "]" + "重打任意笔签购单";
        func[5] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
        func[6] = "[" + PaymentBank.XYKJZ + "]" + "交易结算";    
        func[7] = "[" + PaymentBank.XKQT1 + "]" + "重打上笔签购单";  
        
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
                grpLabelStr[1] = "原交易流水号";
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易金额";
            break;
        	case PaymentBank.XYKTH://隔日退货   
				grpLabelStr[0] = "原交易检索号";
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
                grpLabelStr[1] = "原交易流水号";
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "重打任意笔签购单";
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
        	case PaymentBank.XKQT1: //重打上笔  
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "重打上笔";
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
		 	case PaymentBank.XYKCD: 	//重打任意笔签购单    
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始重打任意笔";
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
		 	case PaymentBank.XKQT1: 	//重打上笔
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始重打上笔";
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
				  type == PaymentBank.XKQT1	))
				{			
					  new MessageBox("银联接口不支持此交易类型！！！");
					  
					  return false;
			    }
			
//			path = getBankPath(paycode);
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
			if (PathFile.fileExist(path + "\\xyprint.txt"))
            {
                PathFile.deletePath(path + "\\xyprint.txt");
            }
             //写入请求数据
            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
            {
                return false;
            }
            //  调用接口模块
            if (PathFile.fileExist(path+"\\javaposbank.exe"))
            {
//            	CommonMethod.waitForExec(path+"\\javaposbank.exe JXNX","javaposbank");
            	CommonMethod.waitForExec(path+"\\javaposbank.exe HZGS");
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
//				XYKPrintDoc();
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
                    type1 = "S1";
                break;
			 	case PaymentBank.XYKCX: 	// 消费撤销
			 		type1 = "S2";
			 	break;	
                case PaymentBank.XYKTH:		// 隔日退货
                	type1 = "S3";
                break;
                case  PaymentBank.XYKYE: 	// 余额查询
                	type1 = "S4";
                break;	
                case  PaymentBank.XYKCD: 	// 重打任意笔签购单
                	type1 = "Q3";
                break;
                case  PaymentBank.XYKQD: 	// 交易签到
                	type1 = "Q1";
                break;
                case  PaymentBank.XYKJZ:
                    type1 = "Q2";			// 交易结算
                break;
                case  PaymentBank.XKQT1:
                    type1 = "Q3";			// 重打上笔签购单
                break;
            
			 }
			 
			 
//				strncpy(inputData.transType,szRequest[0],2+1);//交易指令
//				strncpy(inputData.transAmount,szRequest[1],12+1);//交易金额
//				strncpy(inputData.tipAmount,szRequest[2],12+1);//小费
//				strncpy(inputData.loyalty,szRequest[3],12+1);//积分
//				strncpy(inputData.MisTrace,szRequest[4],6+1);//MIS流水号
//				strncpy(inputData.Period,szRequest[5],2+1);//分期期数
//				strncpy(inputData.OrgAuthNo,szRequest[6],6+1);//原交易授权号
//				strncpy(inputData.OrgTraceNO,szRequest[7],6+1);	//原交易流水号
//				strncpy(inputData.OrgRetRefNo,szRequest[8],12+1);//原系统检索号
//				strncpy(inputData.OrgTransDate,szRequest[9],8+1);//原交易日期
//				strncpy(inputData.cashPcNum,szRequest[10],20+1);//收银台号
//				strncpy(inputData.cashierNum,szRequest[11],20+1);//收银员号
			 
			 String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
	         jestr = Convert.increaseCharForward(jestr,'0',12);   //交易金额12
			 
			 String tipAmount = Convert.increaseChar("", ' ', 12);//小费12
			 
			 String loyalty = Convert.increaseChar("", ' ', 12);//积分12
			 
			 String misTrace = Convert.increaseCharForward("", '0', 6); //Mis流水号6
			 
			 String period = Convert.increaseChar("", ' ', 2);//分期期数 2
			 
			 String orgAuthNo = Convert.increaseChar("", ' ', 6);//原交易授权号6
			 
			 String orgTraceNO = Convert.increaseCharForward(oldauthno, '0', 6); //原交易流水号6
			 
			 String orgRetRefNo = Convert.increaseCharForward(oldseqno, '0', 12); //原系统检索号12
			 
			 String date = Convert.increaseChar(olddate, '0', 8); //交易日期8
			 
			 String syjh = Convert.increaseChar(ConfigClass.CashRegisterCode,' ', 20); //收银机号20

	         String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh,' ', 20);    //收银员号20
	         
	     	         	        
	         //交易指令+交易金额+小费+积分+Mis流水号+分期期数+原交易授权号+原交易流水号+参考号+交易日期+收银机号+收银员号
	         line = type1 + "," + jestr + "," + tipAmount + "," + loyalty + "," + misTrace + "," + period + "," + orgAuthNo + "," + orgTraceNO + "," + orgRetRefNo + "," + date + "," + syjh  + "," + syyh;
	     
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
            
//        	strncpy(szResult[0],outputData.rspCode,2);  //char rspCode[2 + 1];//交易结果        
//        	strncpy(szResult[1],outputData.rspMsg,40);    //char rspMsg[40 + 1];//交易结果描述       
//        	strncpy(szResult[2],outputData.transType,2);    //char transType[2 + 1];//交易类型              	
//        	strncpy(szResult[3],outputData.transAmount,12);//char transAmount[12 + 1];//交易金额
//        	strncpy(szResult[4],outputData.transCardNum,19);//char transCardNum[19 + 1];//交易卡号
//        	strncpy(szResult[5],outputData.TraceNum,6);//char TraceNum[6 + 1];//交易流水号
//        	strncpy(szResult[6],outputData.RetRefNo,12);//char RetRefNo[12 + 1];//系统检索号
//        	strncpy(szResult[7],outputData.transDate,8);//char transDate[8 + 1];//交易日期
//        	strncpy(szResult[8],outputData.cardName,20);//char cardName[20 + 1];//发卡行名称
        	
            String result[] = line.split(",");
            if (result == null) return false;                  
            
            int type = Integer.parseInt(bld.type.trim());
             
            if (result.length >= 9)
            {
            	bld.retcode  =  result[0].trim();  //返回码
            	
            	if(!(bld.retcode.trim().equals("00")))
            	{
            		
            		bld.retmsg = bld.retcode + "," +result[1].trim();
            		return false;
            	}
            	if(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX ||  type == PaymentBank.XYKTH )
            	{
            		
                	bld.cardno = result[5].trim();   //卡号20
//                	bld.trace = Long.parseLong(Convert.newSubString(result[1], 0, 16));   
                	bld.trace = Long.parseLong(result[5].trim());   //流水号
                	
                	bld.je = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Double.parseDouble(result[3].trim() ),100),2,1);   //交易金额	
//                	new MessageBox("bld.je:"+bld.je);
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
			  type == PaymentBank.XYKCD || type == PaymentBank.XKQT1  )
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
		if (d.length() != 8)
		{
			new MessageBox("日期格式错误\n日期格式: YYYYMMDD");
			return false;
		}
		
		return true;
	}

	
	public void XYKPrintDoc()
	{
		ProgressBox pb = null;
		String name =path + "\\xyprint.txt";
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
