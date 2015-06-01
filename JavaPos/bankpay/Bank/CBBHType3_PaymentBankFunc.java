package bankpay.Bank;

import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

//重庆环球，调用动态库（模块名：CQXDS；动态库(dll文件）：posinf.dll；函数：int umsbankproc(char * REQ, char * RSP)；）
public class CBBHType3_PaymentBankFunc extends CBBHType1_PaymentBankFunc
{
	String path ;
	public String cardno ;
	String crc ;
	public String[] getFuncItem()
	{
		String[] func = new String[7];
		
		func[0] = "[" + PaymentBank.XYKXF + "]" + "积分消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "积分消费撤消"; 
		func[2] = "[" + PaymentBank.XYKYE + "]" + "积分余询";
		func[3] = "[" + PaymentBank.XKQT4 + "]" + "积分兑换"; 
		func[4] = "[" + PaymentBank.XKQT5+ "]" + "积分兑换撤销";
		func[5] = "[" + PaymentBank.XKQT2 + "]" + "分期消费";
		func[6] = "[" + PaymentBank.XKQT3 + "]" + "分期消费取消";
		
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
			case PaymentBank.XYKYE : //查询余额
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "查询余额";
				break;
				
			case PaymentBank.XKQT2 : //分期消费
				grpLabelStr[0] = null;//"分期期数";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "分期总金额";
				break;
			case PaymentBank.XKQT3 : //分期消费取消
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "分期消费取消";
				break;
			case PaymentBank.XKQT4 : //积分兑换
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "积分数";
				break;
			case PaymentBank.XKQT5 : //积分兑换撤销
				grpLabelStr[0] = "原流水号";
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
		
			case PaymentBank.XYKYE: //查询余额
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始查询余额";
				break;
			case PaymentBank.XKQT2 : //分期消费
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始分期消费";
				break;
			case PaymentBank.XKQT3 : //分期消费取消
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键分期消费取消";
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
				grpTextStr[4] = "按回车键取消分期消费";
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
				  type == PaymentBank.XYKCD || type == PaymentBank.XKQT2 ||
				  type == PaymentBank.XKQT3 || type == PaymentBank.XKQT4 || type == PaymentBank.XKQT5))
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
			String app = "3";//积分、分期应用
			
				//在付款时，积分、分期应用 选择消费或者分期消费
				if (type == PaymentBank.XYKXF || type ==PaymentBank.XKQT2)
				{
					String[] title = { "代码", "消费类型" };
					int[] width = { 60, 440 };
					Vector contents = new Vector();
					contents.add(new String[] { "1", "积分消费" });
					contents.add(new String[] { "2", "分期消费" });
					
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
				//在付款时撤消时，积分、分期应用 选择消费或者分期消费
				if (type == PaymentBank.XYKCX )
				{
					String[] title = { "代码", "消费类型" };
					int[] width = { 60, 440 };
					Vector contents = new Vector();
					contents.add(new String[] { "1", "积分消费撤消" });
					contents.add(new String[] { "2", "分期消费撤消" });
					
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
				
			//分期付款时输入分期数
			if (type ==PaymentBank.XKQT2)
			{
				StringBuffer sb = new StringBuffer();
				TextBox txt = new TextBox();
				txt.open("请输入分期消费的分期数", "分期数", null, sb, 0, 99, true, 0);
				oldseqno = sb.toString();
			}
			
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
						trans = "30"  ;
						break;
					case PaymentBank.XYKCX:
						trans = "31"  ;
						break;
					case PaymentBank.XYKYE:
						trans = "34"  ;
						break;
					case PaymentBank.XKQT4:
						trans = "32"  ;
						break;
					case PaymentBank.XKQT5:
						trans = "33"  ;
						break;
					case PaymentBank.XKQT2:
						trans = "35"  ;
						break;
					case PaymentBank.XKQT3:
						trans = "36"  ;
						break;
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