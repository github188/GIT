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

import com.efuture.javaPos.Global.GlobalInfo;

import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.UI.Design.MutiSelectForm;


//锡林浩特高勒时代购物广场接口，调用动态库（模块名：XLHTGLSD；动态库(dll文件）：KeeperClient.dll；函数：int __stdcall misposTrans(void* input, void* output)；）
public class Xlhtglsd_PaymentBankFunc extends PaymentBankFunc
{
	String path = "C:\\gmc";
	String fq = "01";
	public String[] getFuncItem()
	{
		String[] func = new String[10];
		
		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "退货";
		func[3] = "[" + PaymentBank.XYKQD + "]"	+ "交易签到";
		func[4] = "[" + PaymentBank.XYKJZ + "]" + "交易结算";
		func[5] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
		func[6] = "[" + PaymentBank.XYKCD + "]" + "重打任意签购单";
		func[7] = "[" + PaymentBank.XKQT1 + "]" + "重打上笔签单";
		func[8] = "[" + PaymentBank.XKQT2 + "]" + "分期付款";
		func[9] = "[" + PaymentBank.XKQT3 + "]" + "快速消费";
		
		return func;
	}
	
	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
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
				grpLabelStr[0] = "系统检索号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = "原交易日期";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKTH : //退货
				grpLabelStr[0] = "系统检索号";
				grpLabelStr[1] = "原终端号";
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
			case PaymentBank.XYKYE : //查询余额
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "查询余额";
				break;
				
			case PaymentBank.XYKCD : //重打签购单
				grpLabelStr[0] = "系统检索号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打任意签购单";
				break;
				
			case PaymentBank.XKQT1 : //重打上笔签购单
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打上笔签购单";
				break;
			case PaymentBank.XKQT2 : //分期付款
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XKQT3 : //快速交易
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
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
				grpTextStr[4] = "按回车键重打任意签购单";
				break;	
			case PaymentBank.XKQT1: //重打上笔签购单
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键重打上笔签购单";
				break;
			case PaymentBank.XKQT2: //分期付款
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XKQT3: //快速交易
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车开始快速消费";
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
				  type == PaymentBank.XKQT2 || type == PaymentBank.XKQT3 ))
			{			
				  new MessageBox("银联接口不支持此交易类型！！！");
				  
				  return false;
		    }
			
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
			
			
//			//////////选择：消费、快速消费、分期付款/////////////
			if(type == PaymentBank.XYKXF)
			{
				String code = "";
				String[] title = { "代码","消费类型 "};
				int[] width = { 60, 440 };
				Vector contents = new Vector();
				contents.add(new String[] { "1", "消费" });
				contents.add(new String[] { "2", "分期付款" });
				contents.add(new String[] { "3", "快速消费" });
				int choice = new MutiSelectForm().open("请选择消费类型", title, width, contents, true);
				if (choice == -1)
				{
					errmsg = "没有选择消费类型";
					return false;
				}else {
					String[] row = (String[]) (contents.elementAt(choice));
					code = row[0];
					if(code.equals("2"))
					{
						type = PaymentBank.XKQT2;
					}
					else if(code.equals("3"))
					{
						type = PaymentBank.XKQT3;
					}
				}
			}
//			
//			//////////选择：退货、积分消费退货/////////////
//			if(type == PaymentBank.XYKTH)
//			{
//				//多种支付方式，选择
//				String code = "";
//				String[] title = { "代码","消费类型 "};
//				int[] width = { 60, 440 };
//				Vector contents = new Vector();
//				contents.add(new String[] { "1", "退货" });
//				contents.add(new String[] { "2", "积分消费退货" });
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
//						type = PaymentBank.XKQT4;
//					}
//				}
//			}

            //输入分期期数
			if(type == PaymentBank.XKQT2)
			{
				StringBuffer bf = new StringBuffer();
	            if(type == PaymentBank.XKQT1 || type == PaymentBank.XKQT2 || type == PaymentBank.XKQT3)
	            {
	            	TextBox txt = new TextBox();
	            	if (!txt.open("请刷分期付款的期数", "分期期数", "请填正整数", bf, 0, 0, false, -1))
	    			{
	    				return true;
	    			}
	            	fq = bf.toString();
	            }
			}

			
			
			
			
			if (!XYKWriteRequest(type,money,track1,track2,track3,oldseqno,oldauthno,olddate,memo)) return false;
			
			if (PathFile.fileExist(path + "\\javaposbank.exe"))
			{
				CommonMethod.waitForExec(path + "\\javaposbank.exe XLHTGLSD");
			}
			else
			{
				errmsg = "找不到金卡工程模块 XLHTGLSD";
				XYKSetError("XX", errmsg);
				new MessageBox(errmsg);
				
				return false;
			}
			
			if (!XYKReadResult(type)) return false;
			
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
		
//		strncpy(inputData.TransType,szRequest[0],2);
//		strncpy(inputData.TransAmount,szRequest[1],12);
//		strncpy(inputData.ReferNo,szRequest[2],8);
//		strncpy(inputData.TransDate,szRequest[3],8);
//		strncpy(inputData.TerminalId,szRequest[4],15);
//		strncpy(inputData.platId,szRequest[5],20);
//		strncpy(inputData.operId,szRequest[6],20);
//		strncpy(inputData.PreInput,szRequest[7],256);	
//		strncpy(inputData.AddDatas,szRequest[8],256);
//		strncpy(inputData.InstallmentTimes,szRequest[9],2);
		
		try
		{
			String line = "";
			String jestr = String.valueOf((long)ManipulatePrecision.doubleConvert(money*100, 2, 1));
			String je = Convert.increaseCharForward(jestr, '0', 12);
			String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 20); //收银员号
			String syjh = Convert.increaseChar(GlobalInfo.syjDef.syjh, ' ', 20); //收银机号
			String seqno = Convert.increaseCharForward(oldseqno, '0', 8);  //对应FORM中的5个输入框第一个Text
			String authno = Convert.increaseCharForward(oldauthno, '0', 15); //第二个Text
			String date = Convert.increaseChar(olddate, '0', 8); //第三个Text
	        String input1 = Convert.increaseChar("", ' ', 256); //预输入项256
	        String input2 = Convert.increaseChar("", ' ', 256); //固定输入项256
			String trans = "";
			fq = Convert.increaseCharForward(fq, '0', 2); 
			switch(type)
			{
				case PaymentBank.XYKXF:
					trans = "05"  ;
					break;
				case PaymentBank.XYKCX:  //撤销和退货同一交易指令

				case PaymentBank.XYKTH:
					trans = "04"  ;
					break;	
				case PaymentBank.XYKQD:
					trans = "09"  ;
					break;
				case PaymentBank.XYKJZ:
					trans = "15"  ;
					break;
				case PaymentBank.XYKYE:
					trans = "10"  ;
					break;
				case PaymentBank.XYKCD:  //重打印上笔和重打印指定一笔同一交易指令
					trans = "13"  ;
					break;
				case PaymentBank.XKQT1:
					trans = "13"  ;
					break;
				case PaymentBank.XKQT2:
					trans = "12"  ;
					break;	
				case PaymentBank.XKQT3:
					trans = "21"  ;
					break;
			}
			line = trans + "," + je + "," + seqno + "," + date + "," + authno + "," + syjh + "," +syyh + "," + input1 + "," + input2 + "," + fq;       
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
//		strncpy(szResult[0],outputData.TransType,2);
//		strncpy(szResult[1],outputData.CardNo,19);
//		strncpy(szResult[2],outputData.Amount,12);
//		strncpy(szResult[3],outputData.TransDate,8);
//		strncpy(szResult[4],outputData.ReferNo,8);
//		strncpy(szResult[5],outputData.AuthNo,6);
//		strncpy(szResult[6],outputData.RspCode,2);
//		strncpy(szResult[7],outputData.TerminalId,15);	
//	    strncpy(szResult[8],outputData.BankName,20);
//		strncpy(szResult[9],outputData.CardType,20);
//		strncpy(szResult[10],outputData.RspMessage,100);
//		strncpy(szResult[11],outputData.TerminalTraceNo,6);

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
			if(str.length != 12 )
			{
				errmsg = "返回数据错误!";
				return false;
			}
			
			bld.retcode = str[6];
			//当银行返回字符以字节计算，而字符中汉字出现导致line的长度和字节书不等时，汉字后面的内容倒着计算位置
			// int len = line.length();
			if (!bld.retcode.equals("00"))
			{
				bld.retmsg = str[10].trim();
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
				type == PaymentBank.XYKCD || type == PaymentBank.XKQT1 )
			{
				return true;
			}
			
			//发卡行名称
//			bld.bankinfo = str[8].trim();
			//卡号
			bld.cardno = str[1].trim();
			//系统检索号
			String s = str[4].trim();
			if (s.matches("^\\s*\\d+\\s*$"))
			{
				bld.trace = Long.parseLong(s);
			}			
			//金额
			if(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || 
				type == PaymentBank.XYKTH || type == PaymentBank.XKQT3 || type == PaymentBank.XKQT2){
				double d = Double.parseDouble(str[2]);
				bld.je = ManipulatePrecision.mul(d,0.01);
			}
			
			//授权号
			bld.authno = str[5].trim();
			
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
	if(bld.retbz != 'Y')
	{
		return false;
	}
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
	
	public void XYKPrintDoc()
	{
		ProgressBox pb = null;
		String name =path + "\\ICBCPRTTKT.txt";
		try
		{
			if (!PathFile.fileExist(name))
			{
				new MessageBox("找不到签购单打印文件！！！");
				
				return ;
			}
			pb = new ProgressBox();
			pb.setText("正在打印签购单文件，请等待。。。" + "\t OY : " + GlobalInfo.sysPara.issetprinter);
			
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


