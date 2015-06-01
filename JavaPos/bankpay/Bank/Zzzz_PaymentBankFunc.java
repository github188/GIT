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

//此类是一个接口模板，仅供参考。（希望写接口把调用的情况写清楚，方便以后写时查找是否已经写过）
//重庆环球，调用动态库（模块名：CQXDS；动态库(dll文件）：posinf.dll；函数：int umsbankproc(char * REQ, char * RSP)；）
public class Zzzz_PaymentBankFunc extends PaymentBankFunc
{
	String path = null;
	public String[] getFuncItem()
	{
		String[] func = new String[9];
		
		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[3] = "[" + PaymentBank.XYKQD + "]"	+ "交易签到";
		func[4] = "[" + PaymentBank.XYKJZ + "]" + "交易结算";
		func[8] = "[" + PaymentBank.XKQT2 + "]" + "重打结算单";
		func[5] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
		func[6] = "[" + PaymentBank.XYKCD + "]" + "重打签购单";
		func[7] = "[" + PaymentBank.XKQT1 + "]" + "重打上笔签单";
		
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
				grpLabelStr[1] = "原参考号";
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKTH :
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = "参考号";
				grpLabelStr[2] = "原交易日期";
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
			case PaymentBank.XKQT2 : //重打结算单
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打结算单";
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
				grpLabelStr[4] = "重打签购单";
				break;
			case PaymentBank.XKQT1 : 
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打上笔签单";
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
			case PaymentBank.XKQT2: //重打结算单
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始重打结算单";
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
				grpTextStr[4] = "按回车键开始重打签购单";
				break;	
			case PaymentBank.XKQT1: //重打结算单
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始重打上笔签单";
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
				  type == PaymentBank.XKQT2)  )
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
			
			//多种支付方式，选择
//			String code = "";
//			String[] title = { "代码", "应用类型" };
//			int[] width = { 60, 440 };
//			Vector contents = new Vector();
//			contents.add(new String[] { "1", "银行卡" });
//			contents.add(new String[] { "2", "卡、DCC" });
//			contents.add(new String[] { "3", "工行积分、分期" });
//			contents.add(new String[] { "4", "银商预付卡" });
//			contents.add(new String[] { "5", "亚盟卡" });
//			contents.add(new String[] { "6", "重庆通卡" });
//			contents.add(new String[] { "7", "其他应用" });
//			contents.add(new String[] { "8", "统一预付卡" });
//			
//			int choice = new MutiSelectForm().open("请选择交易卡类型", title, width, contents, true);
//			if (choice == -1)
//			{
//				errmsg = "没有选择应用类型";
//				return false;
//			}else {
//				String[] row = (String[]) (contents.elementAt(choice));
//				code = row[0];
//			}
			
			if (!XYKWriteRequest(type,money,track1,track2,track3,oldseqno,oldauthno,olddate,memo)) return false;
			
			if (PathFile.fileExist(path + "\\gmc.exe"))
			{
				CommonMethod.waitForExec(path + "\\gmc.exe");
				//CommonMethod.waitForExec("C:\\JavaPos\\javaposbank.exe YSAL1");
			}
			else
			{
				errmsg = "金卡接口文件gmc.exe不存在！！！";
				errmsg = "找不到金卡工程模块 ICBC";
				XYKSetError("XX", errmsg);
				new MessageBox(errmsg);
				
				return false;
			}
			
			if (!XYKReadResult(type)) return false;
			
            //通过收银机参数 （OY）设置为银行POS机打印，则JavaPos不打印   
            if (GlobalInfo.sysPara.issetprinter == 'Y')
            	return true;
            
			if (XYKNeedPrintDoc(type))
			{
				XYKPrintDoc();
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
		if (d.length() > 8)
		{
			new MessageBox("日期格式错误\n日期格式《YYYYMMDD》");
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
			String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 10); //收银员号
			String syjh = Convert.increaseChar(GlobalInfo.syjDef.syjh, ' ', 10); //收银机号
			String seqno = Convert.increaseChar(oldseqno, ' ', 6);  //对应FORM中的5个输入框第一个Text
			String authno = Convert.increaseChar(oldauthno, ' ', 6); //第二个Text
			String date = Convert.increaseChar(olddate, ' ', 8); //第三个Text
			bld.crc = XYKGetCRC();
			
			switch(type)
			{
				case PaymentBank.XYKXF:
					line = "" + syyh + syjh + je  ;
					break;
				case PaymentBank.XYKCX:
					line = "" + syyh + syjh + je + seqno + authno ;
					break;
				case PaymentBank.XYKTH:
					line = "" + syyh + syjh + je + seqno + authno + date;
					break;
				case PaymentBank.XYKQD:
					line = "" + syyh + syjh ;
					break;
				case PaymentBank.XYKJZ:
					line = "" + syyh + syjh ;
					break;
				case PaymentBank.XKQT2:
					line = "" + syyh + syjh ;
					break;
				case PaymentBank.XYKYE:
					line = "" + syyh + syjh;
					break;
				case PaymentBank.XYKCD:
					line = "" + syyh + syjh + seqno;
					break;
				case PaymentBank.XKQT1:
					line = "" + syyh + syjh + seqno;
					break;
			}
			
//			String trans = "";
//			switch(type)
//			{
//				case PaymentBank.XYKXF:
//					trans = "05"  ;
//					break;
//				case PaymentBank.XYKCX:
//					trans = "04"  ;
//					break;
//			    case PaymentBank.XYKTH:
//			        trans = "04"  ;
//			        break;
//				case PaymentBank.XYKQD:
//					trans = "09"  ;
//					break;
//				case PaymentBank.XYKJZ:
//					trans = "15"  ;
//					break;
//				case PaymentBank.XYKYE:
//					trans = "10"  ;
//					break;
//				case PaymentBank.XYKCD:
//					trans = "13"  ;
//					break;
//				case PaymentBank.XKQT1:
//					trans = "13"  ;
//					break;
//				case PaymentBank.XKQT2:
//					trans = "53"  ;
//					break;
//			}
//			line = trans + "," + je + "," + seqno + "," + authno + "," + date + "," + syjh + "," +syyh;

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
			if(str.length > 1)
			{
				line = str[1];
			}
			
			bld.retcode = line.substring(0,2);
			//当银行返回字符以字节计算，而字符中汉字出现导致line的长度和字节书不等时，汉字后面的内容倒着计算位置
			// int len = line.length();
			//防止字节和字符不一致问题
			//bld.memo = Convert.newSubString(line, 2, 21);
			if (!bld.retcode.equals("00"))
			{
				bld.retmsg = line.substring(40,80).trim();
				//bld.retmsg = line.subString(40, len - 64).trim();
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
				type == PaymentBank.XKQT2 )
			{
				return true;
			}
			
			//卡名称 如果返回了银行名称，就取出，没有，就不取。程序会将此接口对于的付款名称赋给它
			bld.bankinfo = line.substring(10,20);
			//卡号
			bld.cardno = line.substring(2,21);
			//流水号
			String s = line.substring(30,40);			
			//判断截取的流水号信息是否为不全为空格字符
            //当返回的流水号全部为空格字符时，会导致数据类型转换异常
			if (s.matches("^\\s*\\d+\\s*$"))
			{
				bld.trace = Long.parseLong(s);
			}			
			//金额
			double d = Double.parseDouble(line.substring(40,50));
			bld.je = ManipulatePrecision.mul(d,0.01);
			//余额
			d = Double.parseDouble(line.substring(40,50));
			bld.kye = ManipulatePrecision.mul(d,0.01);
			//系统参考号
			bld.authno = line.substring(80,90);
			//LRC
			String LRC = line.substring(90,100);
			
			if(!LRC.equals(bld.crc))
			{
				errmsg = "返回效验码" + LRC + "同原效验码" + bld.crc + "不一致";
				XYKSetError("XX", errmsg);
				new MessageBox(errmsg);
				
				return false;
			}
			
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
		  type == PaymentBank.XKQT2)
	{
		return true;
	}
	else
		return false;
	
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


