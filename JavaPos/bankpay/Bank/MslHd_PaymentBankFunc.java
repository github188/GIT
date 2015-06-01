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
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

//美食林前台银行接口。接口公司为石家庄瑞柏。接口银行为邯郸商业银行
//美食林，调用动态库（模块名：HdYl；动态库(dll文件）：POSMIS.dll；函数：int  bankpos(REQUEST *vData, RESPONSE *vRspData)；）
public class MslHd_PaymentBankFunc extends PaymentBankFunc
{
	String path;
	public String[] getFuncItem()
	{
		String[] func = new String[11];
		
		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[3] = "[" + PaymentBank.XYKQD + "]"	+ "交易签到";
		func[4] = "[" + PaymentBank.XYKJZ + "]" + "交易结算";
		func[5] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
		func[6] = "[" + PaymentBank.XYKCD + "]" + "重打上笔签购单";
		func[7] = "[" + PaymentBank.XKQT1 + "]" + "重打签购单";
		func[8] = "[" + PaymentBank.XKQT2 + "]" + "打印交易汇总";
		func[9] = "[" + PaymentBank.XKQT3 + "]" + "打印交易明细";
		func[10] = "[" + PaymentBank.XKQT4 + "]" + "远程更新";
		
		return func;
	}
	
	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
         //		0-4对应FORM中的5个输入框
		//null表示该不用输入
		switch(type)
		{
			case PaymentBank.XYKXF : // 消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKCX : //消费撤销
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKTH :
				grpLabelStr[0] = null;
				grpLabelStr[1] = "原参考号";
				grpLabelStr[2] = "交易日期";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKQD : //签到
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易签到";
				break;
			case PaymentBank.XYKJZ : //交易结账
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易结算";
				break;

			case PaymentBank.XYKYE : //查询余额
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "查询余额";
				break;
				
			case PaymentBank.XYKCD : //重打签购单
				grpLabelStr[0] = null;//"原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打上笔签单";
				break;
			case PaymentBank.XKQT1 : 
				grpLabelStr[0] = "原票据号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打签购单";
				break;
				
			case PaymentBank.XKQT2 : //重打结算单
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "打印交易汇总";
				break;
				
			case PaymentBank.XKQT3 : //重打结算单
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "打印交易明细";
				break;
				
			case PaymentBank.XKQT4 : 
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "远程更新";
				break;
		}
		
		return true;
	}
	
	public boolean getFuncText(int type, String[] grpTextStr)
	{
//		null表示必须用户输入,不为null表示缺省显示无需改变
		switch(type)
		{
			case PaymentBank.XYKXF: //消费
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKCX: //消费撤销
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKTH: //隔日退货
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKQD: //交易签到
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易签到";
				break;
			case PaymentBank.XYKJZ: //交易结算
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易结算";
				break;
			case PaymentBank.XYKYE: //查询余额
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始查询余额";
				break;
			case PaymentBank.XYKCD: //重打签购单
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始重打上笔签购单";
				break;	
			case PaymentBank.XKQT1: //重打结算单
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始重打签单";
				break;
			case PaymentBank.XKQT2: //重打结算单
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始打印交易汇总";
				break;
			case PaymentBank.XKQT3: //重打结算单
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始打印交易明细";
				break;
			case PaymentBank.XKQT4: //重打结算单
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始远程更新";
				break;
		}
		
		return true;
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if (!(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || 
				  type == PaymentBank.XYKTH || type == PaymentBank.XYKQD ||
				  type == PaymentBank.XYKJZ || type == PaymentBank.XYKYE ||
				  type == PaymentBank.XYKCD || type == PaymentBank.XKQT1 ||
				  type == PaymentBank.XKQT2 || type == PaymentBank.XKQT3)  )
			{			
				  new MessageBox("银联接口不支持此交易类型！！！");
				  
				  return false;
		    }
			
			//获得金卡文件路径
			//path = getBankPath(paycode);
			path = ConfigClass.BankPath;
			if (PathFile.fileExist(path + "\\request.txt"))
			{
				PathFile.deletePath(path + "\\request.txt");
				if (PathFile.fileExist(path + "\\reques.txt"))
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
			
			if (!XYKWriteRequest(type,money,track1,track2,track3,oldseqno,oldauthno,olddate,memo)) return false;
			
			if (PathFile.fileExist(path + "\\javaposbank.exe"))
			{
				CommonMethod.waitForExec(path + "\\javaposbank.exe HDYL");
			}
			else
			{
				errmsg = "金卡接口文件javaposbank.exe不存在！！！";
				XYKSetError("XX", errmsg);
				new MessageBox(errmsg);
				
				return false;
			}
			
			if (!XYKReadResult(type)) return false;
			
			if (XYKNeedPrintDoc(type))
			{
				XYKPrintDoc(type);
			}
			
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			XYKSetError("XX", "金卡异常XX" + e.getMessage());
			new MessageBox("调用金卡工程处理模块异常!!!\n" + e.getMessage() , null, false);
			
			return false;
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
	
	public boolean checkBankSucceed() {
		if (bld.retbz == 'N') {
			errmsg = bld.retmsg;

			return false;
		} else {
			errmsg = "交易成功";

			return true;
		}
	}
	
	public boolean XYKWriteRequest(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			String line = "";
			String jestr = String.valueOf((long)ManipulatePrecision.doubleConvert(money*100, 2, 1));
			String je = Convert.increaseCharForward(jestr, '0', 12);
			String syyh = GlobalInfo.posLogin.gh; //收银员号
			String syjh = GlobalInfo.syjDef.syjh; //收银机号
			String seqno = Convert.increaseCharForward(oldseqno,'\0',6);  //对应FORM中的5个输入框第一个Text
			String authno = Convert.increaseCharForward(oldauthno,'\0',12); //第二个Text
			String date = Convert.increaseCharForward(olddate,'\0',4); //第三个Text
			
			String trans = "";
			switch(type)
			{
				case PaymentBank.XYKXF:
					trans = "03"  ;
					break;
				case PaymentBank.XYKCX:
					trans = "05"  ;
					break;
				case PaymentBank.XYKTH:
					trans = "18"  ;
					break;
				case PaymentBank.XYKQD:
					trans = "00"  ;
					break;
				case PaymentBank.XYKJZ:
					trans = "25"  ;
					break;
				case PaymentBank.XYKYE:
					trans = "02"  ;
					break;
				case PaymentBank.XYKCD:
					trans = "45"  ;
					break;
				case PaymentBank.XKQT1:
					trans = "46"  ;
					break;
				case PaymentBank.XKQT2:
					trans = "47"  ;
					break;
				case PaymentBank.XKQT3:
					trans = "48"  ;
					break;
				case PaymentBank.XKQT4:
					trans = "52"  ;
					break;
			}
			line = trans + "," + je + "," + seqno + "," + authno + "," + date + "," + syjh + "," +syyh;

			PrintWriter pw = null;
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
		catch(Exception e)
		{
			e.printStackTrace();
			new MessageBox("写入金卡工程数据异常!!!\n" + e.getMessage(), null, false);
			
			return false;
		}
	}
	
	public boolean XYKReadResult(int type)
	{
		BufferedReader br = null;
		try
		{
			if ( PathFile.fileExist(path + "result.txt") || (br = CommonMethod.readFileGB2312(path + "\\result.txt")) == null)
			{
				errmsg = "读取金卡应答数据失败！！！\n	";
				XYKSetError("XX", errmsg);
				new MessageBox(errmsg, null, false);
				
				return false;
			}
			
			String line = null;
			line = br.readLine();
			
			if (line == null || line.length() < 0)
			{
				errmsg = "返回数据错误!";
				return false;
			}				
			String[] str = line.split(",");
			
			bld.retcode = str[1];
			
			if (!bld.retcode.equals("00"))
			{
				bld.retmsg = str[2];
				errmsg = bld.retmsg ;
				bld.retbz = 'N';
				
				return false;
			}
			else
			{
				bld.retmsg = "金卡工程调用成功！！！";
				bld.retbz = 'Y';
			}
			
		//签到，结账，重打不需要获取详细信息
			if (type == PaymentBank.XYKQD || type == PaymentBank.XYKJZ || 
				type == PaymentBank.XYKCD ||type == PaymentBank.XKQT1 ||
				type == PaymentBank.XKQT2 ||type == PaymentBank.XKQT3 ||
				type == PaymentBank.XKQT4 )
			{
				return true;
			}
			
			//卡名称
			//bld.bankinfo = str[3];
			//卡号
			bld.cardno = str[3];
			//金额
			double d = Double.parseDouble(str[4]);
			bld.je = ManipulatePrecision.mul(d,0.01);
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			XYKSetError("XX", "读取应答数据XX" + e.getMessage());
			new MessageBox("读取金卡工程数据异常" + e.getMessage(), null, false);
			
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
	
	public boolean XYKNeedPrintDoc(int type)
	{
	if (  type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || 
		  type == PaymentBank.XYKTH || type == PaymentBank.XYKJZ || 
		  type == PaymentBank.XYKCD || type == PaymentBank.XKQT1 ||
		  type == PaymentBank.XKQT2 || type == PaymentBank.XKQT3 )
	{
		return true;
	}
	else
		return false;
	
	}
	
	public void XYKPrintDoc(int type)
	{
		ProgressBox pb = null;
		String name = "";
		if (PaymentBank.XYKCD == type)
		{
			name = path + "\\rejcbkprint.txt";
		}
		else
		{
			name = path + "\\jcbkprint.txt";
		}			
		try
		{
			if (!PathFile.fileExist(name))
			{
				new MessageBox("找不到签购单打印文件！！！");
				
				return ;
			}
			
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
	
	
	//当打印机配置的不是2栈时，导致无法打印，重写
//    public void XYKPrintDoc_Start()
//	{
//    	if (onceprint)
//		{
//			int pagesize = ConfigClass.BankPageSize;
//
//				Printer.getDefault().startPrint_Normal();
//				if (pagesize > 0)
//					Printer.getDefault().setPagePrint_Normal(true, pagesize);
//		}
//		else
//		{
//			// 此地改为增加模式，防止在多个金卡工程同时存在时，可能序号相同
//			printdoc = CommonMethod.writeFileAppend("bankdoc_" + String.valueOf(bld.trace) + ".txt");
//		}
//	}
//    
//	public void XYKPrintDoc_Print(String printStr)
//	{
//		if (onceprint)
//        {
//            Printer.getDefault().printLine_Normal(printStr);
//        }
//        else
//        {
//            printdoc.println(printStr);
//        }
//	}
//	
//	public void XYKPrintDoc_End()
//	{
//		 if (onceprint)
//        {
//            Printer.getDefault().cutPaper_Normal();
//        }
//        else
//        {
//            printdoc.flush();
//            printdoc.close();
//            printdoc = null;
//        }
//    }
	
}


