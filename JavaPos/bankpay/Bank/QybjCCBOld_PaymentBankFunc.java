package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

public class QybjCCBOld_PaymentBankFunc extends PaymentBankFunc
{

	private SaleBS saleBS = null;

	public String[] getFuncItem()
	{
		String[] func = new String[4];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		//func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		//func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[1] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
		func[2] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
		func[3] = "[" + PaymentBank.XYKJZ + "]" + "结算";

		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		switch (type)
		{
			case PaymentBank.XYKXF: // 消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = "请刷卡";
				grpLabelStr[4] = "交易金额";
				break;
		/*	case PaymentBank.XYKCX: // 消费撤销
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = "原参考号";
				grpLabelStr[2] = "原交易日";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKTH:// 隔日退货
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = "原参考号";
				grpLabelStr[2] = "原交易日";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;*/
			case PaymentBank.XYKQD: // 交易签到
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易签到";
				break;
			case PaymentBank.XYKYE: // 余额查询
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = "请刷卡";
				grpLabelStr[4] = "余额查询";
				break;
				
			case PaymentBank.XYKJZ: // 
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "结  算";
				break;
				

		}

		return true;
	}

	public boolean getFuncText(int type, String[] grpTextStr)
	{
		switch (type)
		{
			case PaymentBank.XYKXF: // 消费
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
/*			case PaymentBank.XYKCX: // 消费撤销
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKTH: // 隔日退货
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;*/
			case PaymentBank.XYKQD: // 交易签到
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易签到";
				break;
			case PaymentBank.XYKYE: // 余额查询
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始余额查询";
				break;
			case PaymentBank.XYKJZ: // 
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始结账";
				break;

		}

		return true;
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKQD) && (type != PaymentBank.XYKYE)&&(type != PaymentBank.XYKJZ))
			{
				errmsg = "银联接口不支持该交易";
				new MessageBox(errmsg);

				return false;
			}

			// 先删除上次交易数据文件
			if (PathFile.fileExist(".\\request.txt"))
			{
				PathFile.deletePath(".\\request.txt");

				if (PathFile.fileExist(".\\request.txt"))
				{
					errmsg = "交易请求文件request.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist(".\\result.txt"))
			{
				PathFile.deletePath(".\\result.txt");

				if (PathFile.fileExist(".\\result.txt"))
				{
					errmsg = "交易请求文件result.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			// 写入请求数据
			if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo)) { return false; }

			if (bld.retbz != 'Y')
			{

				// 调用接口模块
				if (PathFile.fileExist(".\\javaposbank.exe"))
				{
					CommonMethod.waitForExec(".\\javaposbank.exe BJCCBOLD");
				}
				else
				{
					new MessageBox("找不到金卡工程模块 javaposbank.exe");
					XYKSetError("XX", "找不到金卡工程模块 javaposbank.exe");
					return false;
				}

				// 读取应答数据
				if (!XYKReadResult()) { return false; }

				// 检查交易是否成功
				XYKCheckRetCode();
			}

			// 打印签购单
			if (XYKNeedPrintDoc())
			{
				XYKPrintDoc();
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			XYKSetError("XX", "金卡异常XX:" + ex.getMessage());
			new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);
			return false;
		}
	}

	public boolean XYKNeedPrintDoc()
	{
		if (!checkBankSucceed()) { return false; }

		return true;
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
			//. 交易类型；
		    //. 操作员工号；
			//. 收银机终端
			//. 密码标志；
			//. 卡号（手动输入卡号时）；
			//. 磁道2(刷卡时)；
			// . 磁道3(刷卡时)；
			//. 交易金额；

			//. 原终端流水号；
			//. 原批次号；
			//. 原系统参考号；
			//. 原交易日期；
			
		//	new MessageBox(track2 + "\n"+track3);
			String line = "";

			String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1));

			for (int i = jestr.length(); i < 12; i++)
			{
				jestr = "0" + jestr;
			}

			if (memo.size() >= 2)
				saleBS = (SaleBS) memo.elementAt(2);

			// 根据不同的类型生成文本结构
			switch (type)
			{
				case PaymentBank.XYKXF:
					if (saleBS != null)
					{
						line = "11" + "," + ManipulateStr.PadRight(saleBS.saleHead.syyh,6,' ') + "," +
								ManipulateStr.PadRight(saleBS.saleHead.syjh,6,' ') +",1"
								+","+""+","+track2+"," + track3+"," +jestr;
						
					}
					break;
	/*			case PaymentBank.XYKCX:
					if (saleBS != null)
					{
						olddate = olddate.substring(0, 4);
						line = "30" + "," + ManipulateStr.PadRight(saleBS.saleHead.syyh,6,' ')  + "," + 
								ManipulateStr.PadRight(saleBS.saleHead.syjh,6,' ')+",1"
								+","+""+","+track2+"," + track3+"," +jestr + ","+ 
								ManipulateStr.PadLeft(oldseqno,6,'0')+","
								+ ManipulateStr.PadLeft("?",6,'0') + "," + ManipulateStr.PadRight(oldauthno,12,'0')+"," + olddate;
					}
					
					break;
				case PaymentBank.XYKTH:
					if (saleBS != null)
					{
						olddate = olddate.substring(0, 4);
						line = "12" + "," + ManipulateStr.PadRight(saleBS.saleHead.syyh,6,' ')  + "," + 
								ManipulateStr.PadRight(saleBS.saleHead.syjh,6,' ')+",1"
								+","+""+","+track2+"," + track3+"," +jestr + ","+ 
								ManipulateStr.PadLeft(oldseqno,6,'0')+","
								+ ManipulateStr.PadLeft("?",6,'0') + "," + ManipulateStr.PadRight(oldauthno,12,'0')+"," + olddate;
					}
					break;*/
				case PaymentBank.XYKQD:
					line = "01" + "," +ManipulateStr.PadRight(GlobalInfo.syjStatus.syyh,6,' ');
					break;
				case PaymentBank.XYKYE:
					line = "10" + "," + ManipulateStr.PadRight(GlobalInfo.syjStatus.syyh,6,' ') + "," + ManipulateStr.PadRight(GlobalInfo.syjStatus.syjh,6,' ') +",1"
							+","+""+","+track2+"," + track3;
					break;
				case PaymentBank.XYKJZ:
					line = "06" + "," + ManipulateStr.PadRight(GlobalInfo.syjStatus.syyh,6,' ');
					break;
			}

			PrintWriter pw = null;

			try
			{
				pw = CommonMethod.writeFile(".\\request.txt");
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
			if (!PathFile.fileExist(".\\result.txt") || ((br = CommonMethod.readFileGBK(".\\result.txt")) == null))
			{
				XYKSetError("XX", "读取金卡工程应答数据失败!");
				new MessageBox("读取金卡工程应答数据失败!", null, false);

				return false;
			}

			String line = br.readLine();

			if (line.length() <= 0) 
				return false; 

			String result[] = line.split(",");
			
			if (result == null)
				return false;
			
			if(result.length>0 && result[0]!=null)
			{
				if(!result[0].trim().equals("0"))
				{
					if(result.length>1 && result[1] !=null)
						bld.retcode = result[1].equals("")?"99":result[1];
					if(result.length>2 && result[2]!=null)
						bld.retmsg = result[2].equals("")?"金卡交易返回失败":result[2];
					return false;
				}
			}
			
			if(result.length>1 && result[1]!=null)
			{
				bld.retcode = result[1];
			}
			if(result.length>2 && result[2]!=null)
			{
				bld.retmsg = result[2];
			}
			if(result.length>3 && result[3]!=null)
			{
				bld.je = ManipulatePrecision.doubleConvert(Convert.toDouble(result[3].trim())/100,2,1);
			}
			
			if(result.length>4 && result[4]!=null)
			{
				bld.cardno = result[4].trim();
			}
			
			if(result.length>5 && result[5]!=null)
			{
				bld.trace = Convert.toLong(result[5].trim());
			}
			if(result.length>7 && result[7]!=null)
			{
				bld.authno = result[7];
			}
	
			if(result.length>10 && result[10]!=null)
			{
				bld.bankinfo = result[10];
			}

			return true;
		}
		catch (Exception ex)
		{
			XYKSetError("XX", "读取应答XX:" + ex.getMessage());
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

					if (PathFile.fileExist(".\\request.txt"))
					{
						PathFile.deletePath(".\\request.txt");
					}

					if (PathFile.fileExist(".\\result.txt"))
					{
						PathFile.deletePath(".\\result.txt");
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

		try
		{
			String printName = "";

			int type = Integer.parseInt(bld.type.trim());

			if ((type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XYKTH || type == PaymentBank.XYKCD || type == PaymentBank.XKQT1))
			{
				if (!PathFile.fileExist(".\\toPrint\\Print.txt"))
				{
					new MessageBox("找不到签购单打印文件!");

					return;
				}
				else
				{
					printName = ".\\toPrint\\Print.txt";
				}
			}
			else
			{
				return;
			}

			pb = new ProgressBox();
			pb.setText("正在打印银联签购单,请等待...");

			for (int i = 0; i < GlobalInfo.sysPara.bankprint; i++)
			{
				XYKPrintDoc_Start();

				BufferedReader br = null;

				try
				{
					br = CommonMethod.readFileGBK(printName);

					if (br == null)
					{
						new MessageBox("打开" + printName + "打印文件失败!");

						return;
					}

					String line = null;

					while ((line = br.readLine()) != null)
					{

						if (line.trim().equals("CUTPAPPER"))
						{
							break;
						}

						XYKPrintDoc_Print(line);
					}

				}
				catch (Exception ex)
				{
					new MessageBox(ex.getMessage());
				}
				finally
				{
					if (br != null)
					{
						br.close();
					}
				}

				XYKPrintDoc_End();
			}
		}
		catch (Exception ex)
		{
			new MessageBox("打印签购单发生异常\n\n" + ex.getMessage());
			ex.printStackTrace();
		}
		finally
		{
			if (pb != null)
			{
				pb.close();
				pb = null;
			}
		}
	}

}
