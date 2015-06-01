package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateByte;
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

//重百--银行卡（1），调用动态库（模块名：CQXDS；动态库(dll文件）：posinf.dll；函数：int umsbankproc(char * REQ, char * RSP)；）
public class CBBHType1_PaymentBankFunc extends PaymentBankFunc
{
	String crc ;
	public String[] getFuncItem()
	{
		String[] func = new String[10];
		
		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "当日撤消";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[3] = "[" + PaymentBank.XYKQD + "]"	+ "交易签到";
		func[4] = "[" + PaymentBank.XYKJZ + "]" + "交易结算";
		func[5] = "[" + PaymentBank.XKQT1 + "]" + "重打结算单";  
		func[6] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
		func[7] = "[" + PaymentBank.XYKCD + "]" + "重打指定流水";
		func[8] = "[" + PaymentBank.XKQT2 + "]" + "分期消费";
		func[9] = "[" + PaymentBank.XKQT3 + "]" + "分期消费取消";
		
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
			
			//获得金卡文件路径
			//path = getBankPath(paycode);
			if (PathFile.fileExist(ConfigClass.BankPath + "\\request.txt"))
			{
				PathFile.deletePath(ConfigClass.BankPath + "\\request.txt");
				if (PathFile.fileExist(ConfigClass.BankPath + "\\reques.txt"))
				{
					errmsg = "交易“request.txt”文件删除失败，请重试！！！";
					XYKSetError("XX",errmsg);
					new MessageBox(errmsg);
					
					return false;
				}				
			}
			if (PathFile.fileExist(ConfigClass.BankPath + "\\result.txt"))
			{
				PathFile.deletePath(ConfigClass.BankPath + "\\result.txt");
				if (PathFile.fileExist(ConfigClass.BankPath + "\\result.txt"))
				{
					errmsg = "交易“result.txt”文件删除失败，请重试！！！";
					XYKSetError("XX",errmsg);
					new MessageBox(errmsg);
					
					return false;
				}				
			}
			
			if (!XYKWriteRequest(type,money,track1,track2,track3,oldseqno,oldauthno,olddate,memo)) return false;
			
			if (PathFile.fileExist(ConfigClass.BankPath + "\\javaposbank.exe"))
			{
				CommonMethod.waitForExec(ConfigClass.BankPath + "\\javaposbank.exe CQXDS");
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
	
	public boolean checkSeqno(Text seq)
	{
		
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
			String app = "1";//银行卡类型
			
				
				//在付款时，银行卡选择消费或者分期消费
				if (type == PaymentBank.XYKXF || type ==PaymentBank.XKQT2)
				{
					String[] title = { "代码", "消费类型" };
					int[] width = { 60, 440 };
					Vector contents = new Vector();
					contents.add(new String[] { "1", "消费" });
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
				//在付款时撤消时，银行卡 选择消费或者分期消费
				if (type == PaymentBank.XYKCX )
				{
					String[] title = { "代码", "消费类型" };
					int[] width = { 60, 440 };
					Vector contents = new Vector();
					contents.add(new String[] { "1", "消费撤消" });
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
				pw = CommonMethod.writeFile(ConfigClass.BankPath + "\\request.txt");
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
			
			if ( PathFile.fileExist(ConfigClass.BankPath + "result.txt") || (br = CommonMethod.readFileGB2312(ConfigClass.BankPath + "\\result.txt")) == null)
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
			 int len = line.length();
			if (!bld.retcode.equals("00"))
			{
				bld.retmsg =  "金卡工程调用失败";
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
				type == PaymentBank.XYKCD ||type == PaymentBank.XKQT1 || type == PaymentBank.XYKYE)
			{
				return true;
			}
			
//			通过长度判断是否是老接口
			if(line.getBytes().length < 429)
			{
				
			/*String rspCode = ManipulateByte.getString(ManipulateByte.getStringBytes(line,0,2));
			String bankinfo = ManipulateByte.getString(ManipulateByte.getStringBytes(line,6,26));
			String cardno = ManipulateByte.getString(ManipulateByte.getStringBytes(line,26,46));
			String trace = ManipulateByte.getString(ManipulateByte.getStringBytes(line,52,58));
			double je = Double.parseDouble(ManipulateByte.getString(ManipulateByte.getStringBytes(line,70,82)))/100;
			*/
			//卡名称
			bld.bankinfo = ManipulateByte.getString(ManipulateByte.getStringBytes(line,6,26));
			//卡号
			bld.cardno =ManipulateByte.getString(ManipulateByte.getStringBytes(line,26,46));
			//流水号
			bld.trace = Long.parseLong(ManipulateByte.getString(ManipulateByte.getStringBytes(line,52,58)));
			

			//金额
			bld.je = Double.parseDouble(ManipulateByte.getString(ManipulateByte.getStringBytes(line,70,82)))/100;
			
			}
			else
			{
//				卡名称
				bld.bankinfo = ManipulateByte.getString(ManipulateByte.getStringBytes(line,10,30));
				//卡号
				bld.cardno =ManipulateByte.getString(ManipulateByte.getStringBytes(line,30,50));
				//流水号
				bld.trace = Long.parseLong(ManipulateByte.getString(ManipulateByte.getStringBytes(line,56,62)));
				

				//金额
				bld.je = Double.parseDouble(ManipulateByte.getString(ManipulateByte.getStringBytes(line,74,86)))/100;
				
				
				bld.memo = ManipulateByte.getString(ManipulateByte.getStringBytes(line,2,10));//行号
				bld.authno = ManipulateByte.getString(ManipulateByte.getStringBytes(line,62,74));//参考号
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
		  type == PaymentBank.XYKCD || type == PaymentBank.XKQT1  )
	{
		return true;
	}
	else
		return false;
	
	}
	
	public void XYKPrintDoc()
	{
		ProgressBox pb = null;
		String name =ConfigClass.BankPath + "\\receipt.txt";
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