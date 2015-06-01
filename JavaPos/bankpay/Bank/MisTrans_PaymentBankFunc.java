package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

import custom.localize.Bgtx.Bgtx_CustomGlobalInfo;

//北国福利卡接口
public class MisTrans_PaymentBankFunc extends PaymentBankFunc
{
	String path = null;
	String cardno = "";

	public String[] getFuncItem()
	{
		String[] func = new String[6];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[3] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
		func[4] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
		func[5] = "[" + PaymentBank.XYKCD + "]" + "重打签购单";

		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		//		0-4对应FORM中的5个输入框
		//null表示该不用输入
		switch (type)
		{
			case PaymentBank.XYKXF: // 消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = "请刷卡";
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKCX: //消费撤销
				grpLabelStr[0] = "原凭证号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = "请刷卡";
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKTH:
				grpLabelStr[0] = null;
				grpLabelStr[1] = "原参考号";
				grpLabelStr[2] = "交易日期";
				grpLabelStr[3] = "请刷卡";
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKQD: //签到
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易签到";
				break;

			case PaymentBank.XYKYE: //查询余额
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = "请刷卡";
				grpLabelStr[4] = "查询余额";
				break;

			case PaymentBank.XYKCD: //重打签购单
				grpLabelStr[0] = "原凭证号";
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
		//		null表示必须用户输入,不为null表示缺省显示无需改变
		switch (type)
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
		}

		return true;
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if (!(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XYKTH || type == PaymentBank.XYKQD
					|| type == PaymentBank.XYKYE || type == PaymentBank.XYKCD))
			{
				new MessageBox("银联接口不支持此交易类型！！！");

				return false;
			}
			
			/*
			//福利卡 先消费判断余额是否足够
			if (type == PaymentBank.XYKXF)
			{
				if (!XYKExecute(PaymentBank.XYKYE, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
					
					return false;
				
				if (bld.je > bld.kye)
				{
					if (new MessageBox("余额不足，当前可以余额：" + bld.kye + "元。\n是否使用当前可用余额继续消费？\n1-确认 2-取消").verify() != GlobalVar.Key1)
					{
					   return false;
					}
					money = bld.je = bld.kye;
					bld.retcode = "";
					bld.retbz = 'N';
				}
			}
			*/
			
			//获得金卡文件路径
			path = getBankPath(paycode);
			if (PathFile.fileExist(path + "\\request.txt"))
			{
				PathFile.deletePath(path + "\\request.txt");
				if (PathFile.fileExist(path + "\\reques.txt"))
				{
					errmsg = "交易“request.txt”文件删除失败，请重试！！！";
					XYKSetError("XX", errmsg);
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
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);

					return false;
				}
			}

			if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo)) return false;

			ProgressBox box = new ProgressBox(0,-50);
			box.setText("请等待................");
			try
			{
				if (PathFile.fileExist(path + "\\javaposbank.exe"))
				{
					CommonMethod.waitForExec(path + "\\javaposbank.exe MISTrans", "javaposbank.exe");
				}
				else
				{
					errmsg = "找不到金卡工程模块 MISTrans";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
	
					return false;
				}
			}
			catch(Exception er)
			{
				er.printStackTrace();
			}
			finally
			{
				box.close();
			}
			if (!XYKReadResult(type)) 
			{
				if (bld.kye > 0 && new MessageBox("卡余额为"+bld.kye+" 是否继续",null,true).verify() == GlobalVar.Key1)
				{
					bld.je = bld.kye;
					money = bld.je;
					return XYKExecute(type,  money,  track1,  track2,  track3,  oldseqno,  oldauthno,  olddate,  memo);
				}
				return false;	
			}
			
			
			if (XYKNeedPrintDoc(type))
			{
				if(Bgtx_CustomGlobalInfo.getDefault().sysPara.isprintflk == 'Y')
				{
					XYKPrintDoc();
				}
				
			}

			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			XYKSetError("XX", "金卡异常XX" + e.getMessage());
			new MessageBox("调用金卡工程处理模块异常!!!\n" + e.getMessage(), null, false);

			return false;
		}
	}

	public boolean checkDate(Text date)
	{
		String d = date.getText();
		if (d.length() < 4)
		{
			new MessageBox("日期格式错误\n日期格式《YYYYMMDD》");
			return false;
		}

		return true;
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

	public boolean XYKWriteRequest(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{			
			String line = "";
			String jestr = String.valueOf(money);
			String je = Convert.increaseCharForward(jestr, '0', 19);
			String seqno = Convert.increaseChar(oldseqno, ' ', 6); //对应FORM中的5个输入框第一个Text
			String authno = Convert.increaseChar(oldauthno, ' ', 12); //第二个Text
			String date = Convert.increaseChar(olddate, ' ', 8); //第三个Text
			String track = Convert.increaseChar(track2, ' ', 37);

			if (track2.indexOf('=') > 0)
			{
				cardno = track2.substring(0, track2.indexOf('='));
			}
			cardno = Convert.increaseChar(cardno, ' ', 19);
			String trans = "";
			switch (type)
			{
				case PaymentBank.XYKXF:
					trans = "3";
					break;
				case PaymentBank.XYKCX:
					trans = "4";
					;
					break;
				case PaymentBank.XYKTH:
					trans = "5";
					break;
				case PaymentBank.XYKQD:
					trans = "1";
					break;
				case PaymentBank.XYKYE:
					trans = "2";
					break;
				case PaymentBank.XYKCD:
					trans = "6";
					break;
			}
			line = "1" + "," + trans + "," + cardno + "," + track + "," + je + "," + seqno + "," +  authno + "," + date;
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
		catch (Exception e)
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
			if (!PathFile.fileExist(path + "\\result.txt") || (br = CommonMethod.readFileGB2312(path + "\\result.txt")) == null)
			{
				errmsg = "读取金卡应答数据失败！！！\n	";
				XYKSetError("XX", errmsg);
				new MessageBox(errmsg, null, false);

				return false;
			}

			String line = null;
			line = br.readLine();
			//liwj
			//new MessageBox(line);
			String[] str = line.split(",");
			if (str == null || str.length != 8)
			{
				errmsg = "返回数据错误!";
				return false;
			}

			if (!str[0].equals("0"))
			{
				errmsg = "接口调用返回失败!";
				return false;
			}
			bld.retcode = str[1];
			//当银行返回字符以字节计算，而字符中汉字出现导致line的长度和字节书不等时，汉字后面的内容倒着计算位置
			// int len = line.length();

			if (!bld.retcode.equals("00") && type == PaymentBank.XYKCD)
			{ 
				bld.retmsg = "卡序号输入错误，请重新输入";
				bld.retbz = 'N';
				return false;
			}
			else if (!bld.retcode.equals("00"))
			{
				bld.retmsg = str[7].replaceAll("\\s*", " ").trim();
				errmsg = bld.retmsg;
				bld.retbz = 'N';
				
				//当余额不足时，获取到当前余额
				if (type == PaymentBank.XYKXF)
				{
					bld.kye = Convert.toDouble(str[2]);
				}
				return false;
			}
			else
			{
				bld.retmsg = "金卡工程调用成功！！！";
				bld.retbz = 'Y';
			}

			//签到，结账，余额，重打不需要获取详细信息

			if (type == PaymentBank.XYKQD || type == PaymentBank.XYKCD ) { return true; }
			
			if (type == PaymentBank.XYKYE)
			{
				bld.kye = Double.parseDouble(str[2]);
				return true;
			}

			//卡名称
			//bld.bankinfo = line.substring(10,20);
			//卡号
			bld.cardno = cardno;
			//流水号
			String s = str[3];
			if (s.matches("^\\s*\\d+\\s*$"))
			{
				bld.trace = Long.parseLong(s);
			}
			//金额
			//bld.je = Double.parseDouble(str[2]);
			//卡余额
			bld.kye = Convert.toDouble(str[2]);
			//系统参考号
			bld.authno = str[5];
			bld.cardno = bld.cardno;
			bld.memo = "流水:"+str[3]+" 参考:"+bld.authno;
//			new MessageBox(bld.memo);
			return true;
		}
		catch (Exception e)
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
		if (type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XYKTH || type == PaymentBank.XYKCD)
		{
			return true;
		}
		else return false;

	}

	public void XYKPrintDoc()
	{
		Print(path + "\\PrintBill1.txt");	
		XYKPrintDoc_Print("\n");
		XYKPrintDoc_Print("\n");
		XYKPrintDoc_Print("\n");
		XYKPrintDoc_Print("\n");
		XYKPrintDoc_Print("\n");
		new MessageBox("请撕下签购单！！！");
		/*Print(path + "\\PrintBill2.txt");
		XYKPrintDoc_Print("\n");
		XYKPrintDoc_Print("\n");
		XYKPrintDoc_Print("\n");
		XYKPrintDoc_Print("\n");
*/

	}
	
	public void Print(String name)
	{
		ProgressBox pb = null;
		try
		{
			if (!PathFile.fileExist(name))
			{
				new MessageBox("找不到签购单" + name + "打印文件！！！");

				return;
			}

			pb = new ProgressBox();
			pb.setText("正在打印签购单文件，请等待。。。");

			BufferedReader br = null;
			XYKPrintDoc_Start();
			try
			{
				br = CommonMethod.readFileGB2312(name);
				if (br == null)
				{
					new MessageBox("打开签购单文件失败");

					return;
				}

				String line = null;
				while ((line = br.readLine()) != null)
				{
					//if (line.length() <= 0) continue;

					XYKPrintDoc_Print(line);
				}
			}
			catch (Exception e)
			{
				new MessageBox(e.getMessage());
			}
			finally
			{
				if (br != null) try
				{
					br.close();
				}
				catch (IOException ie)
				{
					ie.printStackTrace();
				}
			}
			XYKPrintDoc_End();
			
		}
		catch (Exception e)
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
