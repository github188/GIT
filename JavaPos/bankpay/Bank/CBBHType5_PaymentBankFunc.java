package bankpay.Bank;

import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;

//重百--统一预付款（5），调用动态库（模块名：CQXDS；动态库(dll文件）：posinf.dll；函数：int umsbankproc(char * REQ, char * RSP)；）
public class CBBHType5_PaymentBankFunc extends CBBHType1_PaymentBankFunc
{
	String path ;
	public String cardno ;
	String crc ;
	public String[] getFuncItem()
	{
		String[] func = new String[8];
		
		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "当日撤消";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[3] = "[" + PaymentBank.XYKQD + "]"	+ "交易签到";
		func[4] = "[" + PaymentBank.XYKJZ + "]" + "交易结算";
		func[5] = "[" + PaymentBank.XKQT1 + "]" + "重打结算单";  
		func[6] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
		func[7] = "[" + PaymentBank.XYKCD + "]" + "重打指定流水";
		/*func[8] = "[" + PaymentBank.XKQT2 + "]" + "翼支付消费";
		func[9] = "[" + PaymentBank.XKQT3 + "]" + "翼支付撤销";
		func[10] = "[" + PaymentBank.XKQT4 + "]" + "翼支付退货";
		func[11] = "[" + PaymentBank.XKQT5 + "]" + "翼支付查余";*/
		
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
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = "原参考号";
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
			case PaymentBank.XKQT1 : //重打结算单
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
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打签购单";
				break;
			/*case PaymentBank.XKQT2 : //翼支付消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XKQT3 : //翼支付消费撤销
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XKQT4 : //翼支付退货
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = "原参考号";
				grpLabelStr[2] = "原交易日期";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XKQT5 : //翼支付查询余额
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "查询余额";
				break;*/
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
			case PaymentBank.XKQT1: //重打结算单
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
			/*case PaymentBank.XKQT2 : //翼支付消费
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始翼支付消费";
				break;
			case PaymentBank.XKQT3 : //翼支付撤销
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键翼支付撤销";
				break;
			case PaymentBank.XKQT4: //
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XKQT5: //
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键翼支付查余";
				break;*/
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
				  type == PaymentBank.XYKCD || type == PaymentBank.XKQT1 /*||
				  type == PaymentBank.XKQT2 || type == PaymentBank.XKQT3 ||
				  type == PaymentBank.XKQT4 || type == PaymentBank.XKQT5 */))
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
				CommonMethod.waitForExec(path + "\\javaposbank.exe CQXDS");
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
		
	public boolean XYKWriteRequest(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			String app = "5";
				
				/*//在付款时，统一预付卡 选择消费或者翼支付消费
				if (type == PaymentBank.XYKXF || type ==PaymentBank.XKQT2) 
				{
					String[] title = { "代码", "消费类型" };
					int[] width = { 60, 440 };
					Vector contents = new Vector();
					contents.add(new String[] { "1", "消费" });
					contents.add(new String[] { "2", "翼支付消费" });
					
					int choice = new MutiSelectForm().open("请选择交易卡类型", title, width, contents, true);
					if (choice == -1)
					{
						errmsg = "没有选择应用类型";
						return false;
					}
					else {
						String[] row = (String[]) (contents.elementAt(choice));
						if (row[0].equals("2"))
						{
							type = PaymentBank.XKQT2;
						}
					}
				}
				//在付款时撤消时，统一预付卡 选择消费撤消或者翼支付消费撤消
				if (type == PaymentBank.XYKCX )
				{
					String[] title = { "代码", "消费类型" };
					int[] width = { 60, 440 };
					Vector contents = new Vector();
					contents.add(new String[] { "1", "消费撤消" });
					contents.add(new String[] { "2", "翼支付消费撤消" });
					
					int choice = new MutiSelectForm().open("请选择交易卡类型", title, width, contents, true);
					if (choice == -1)
					{
						errmsg = "没有选择应用类型";
						return false;
					}
					else {
						String[] row = (String[]) (contents.elementAt(choice));
						if (row[0].equals("2"))
						{
							type = PaymentBank.XKQT3;
						}
					}
				}
				
//				在付款时撤消时，统一预付卡 选择消费退货类型
				if (type == PaymentBank.XYKTH )
				{
					String[] title = { "代码", "退货类型" };
					int[] width = { 60, 440 };
					Vector contents = new Vector();
					contents.add(new String[] { "1", "当日退货" });
					contents.add(new String[] { "2", "翼支付退货" });
					
					int choice = new MutiSelectForm().open("请选择交易卡类型", title, width, contents, true);
					if (choice == -1)
					{
						errmsg = "没有选择应用类型";
						return false;
					}
					else {
						String[] row = (String[]) (contents.elementAt(choice));
						if (row[0].equals("2"))
						{
							type = PaymentBank.XKQT4;
						}
					}
				}
				
//				在付款时撤消时，统一预付卡 选择查询类型
				if (type == PaymentBank.XYKYE)
				{
					String[] title = { "代码", "查询类型" };
					int[] width = { 60, 440 };
					Vector contents = new Vector();
					contents.add(new String[] { "1", "余额查询" });
					contents.add(new String[] { "2", "翼支付余额查询" });
					
					int choice = new MutiSelectForm().open("请选择交易卡类型", title, width, contents, true);
					if (choice == -1)
					{
						errmsg = "没有选择应用类型";
						return false;
					}
					else {
						String[] row = (String[]) (contents.elementAt(choice));
						if (row[0].equals("2"))
						{
							type = PaymentBank.XKQT5;
						}
					}
				}*/
				
			
			String trans = "";
			String line = "";
			String jestr = String.valueOf((long)ManipulatePrecision.doubleConvert(money*100, 2, 1));
			String je = Convert.increaseCharForward(jestr, '0', 12);
			String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 8); //收银员号
			String syjh = Convert.increaseChar(GlobalInfo.syjDef.syjh, ' ', 8); //收银机号
			String seqno = Convert.increaseCharForward(oldseqno, '0', 6);  //对应FORM中的5个输入框第一个Text
			String authno = Convert.increaseCharForward(oldauthno, '0', 12); //第二个Text
			String date = Convert.increaseCharForward(olddate, '0', 8); //第三个Text
			crc = XYKGetCRC();
			
			
				switch(type)
				{
					case PaymentBank.XYKXF:
						trans = "00"  ;
						break;
					case PaymentBank.XYKCX:
						trans = "01"  ;
						break;
					case PaymentBank.XYKTH:
						trans = "02"  ;
						break;
					case PaymentBank.XYKQD:
						trans = "05"  ;
						break;
					case PaymentBank.XYKJZ:
						trans = "06"  ;
						break;
					case PaymentBank.XYKYE:
						trans = "03"  ;
						break;
					case PaymentBank.XYKCD:
						trans = "04"  ;
						break;
					case PaymentBank.XKQT1:
						trans = "07"  ;
						break;
					/*case PaymentBank.XKQT2:
						trans = "43"  ;
						break;
					case PaymentBank.XKQT3:
						trans = "44"  ;
						break;
					case PaymentBank.XKQT4:
						trans = "45"  ;
						break;
					case PaymentBank.XKQT5:
						trans = "46"  ;
						break;*/
				}
			
			
			line = app + syjh + syyh + trans + je + date + authno + seqno + crc;
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
	
}