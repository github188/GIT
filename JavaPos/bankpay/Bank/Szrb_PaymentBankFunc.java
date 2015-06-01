package bankpay.Bank;

import java.io.BufferedReader;
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
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

public class Szrb_PaymentBankFunc extends PaymentBankFunc
{

	class ResponseData
	{
		public String TransType = " "; //交易类型
		public String PinFlag = " "; //密码标志
		public String Amount = " "; //交易金额/查询余额
		public String BudgetPlan = " "; //分期期数
		public String OperNo = " "; //操作员工号
		public String TerminalId = " "; //收银机终端号
		public String CardId = " "; //卡号
		public String OldDate = " "; //原交易日期
		public String OldTrace = " "; //原终端流水号
		public String OldBatchNo = " "; //原批次号
		public String OldAuthNo = " "; //原授权码
		public String OldRrn = " "; //原系统参考号
		public String Track2 = " "; //磁道二
		public String Track3 = " "; //磁道三
		public String IssueId = " "; //发卡行代码
		public String IssueName = " "; //发卡行名称
		public String AcqId = " "; //收单行代码
		public String AcqName = " "; //收单行名称
		public String FirstAmt = " "; //首期金额
		public String LastAmt = " "; //末期金额
		public String Fee = " "; //手续费
		public String RetCode = " "; //响应码
		public String RetInfo = " "; //响应信息
		public String Remark = " "; //备用
	}

	ResponseData response = new ResponseData();

	public String[] getFuncItem()
	{
		String[] func = new String[6];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		//		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[2] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
		func[3] = "[" + PaymentBank.XYKCD + "]" + "签购单重打";
		func[4] = "[" + PaymentBank.XYKJZ + "]" + "交易结算";
		func[5] = "[" + PaymentBank.XYKYE + "]" + "余额查询";

		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		//0-4对应FORM中的5个输入框
		//null表示该不用输入
		switch (type)
		{
			case PaymentBank.XYKXF://消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = "请 刷 卡";
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKCX://消费撤销
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = "原系统参考号";
				grpLabelStr[2] = "原批次号";
				grpLabelStr[3] = "请 刷 卡";
				grpLabelStr[4] = "交易金额";
				break;
			//			case PaymentBank.XYKTH://隔日退货   
			//				grpLabelStr[0] = "原参考号";
			//				grpLabelStr[1] = null;
			//				grpLabelStr[2] = "原交易日";
			//				grpLabelStr[3] = null;
			//				grpLabelStr[4] = "交易金额";
			//				break;
			case PaymentBank.XYKQD://交易签到
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易签到";
				break;
			case PaymentBank.XYKJZ://交易结账
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易结账";
				break;
			case PaymentBank.XYKYE://余额查询    
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = "请 刷 卡";
				grpLabelStr[4] = "余额查询";
				break;
			case PaymentBank.XYKCD://签购单重打
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打签单";
				break;
		}

		return true;
	}

	public boolean getFuncText(int type, String[] grpTextStr)
	{
		//0-4对应FORM中的5个输入框
		//null表示该需要用户输入,不为null用户不输入
		switch (type)
		{
			case PaymentBank.XYKXF://消费
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKCX://消费撤销
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "";
				break;
			//			case PaymentBank.XYKTH://隔日退货   
			//				grpTextStr[0] = null;
			//				grpTextStr[1] = null;
			//				grpTextStr[2] = null;
			//				grpTextStr[3] = null;
			//				grpTextStr[4] = "";
			//				break;
			case PaymentBank.XYKQD://交易签到
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "";
				break;
			case PaymentBank.XYKJZ://交易结账
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "";
				break;
			case PaymentBank.XYKYE://余额查询    
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "";
				break;
			case PaymentBank.XYKCD://签购单重打
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "";
				break;
		}

		return true;
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if (type != PaymentBank.XYKXF && type != PaymentBank.XYKCX && type != PaymentBank.XYKQD && type != PaymentBank.XYKJZ
					&& type != PaymentBank.XYKYE && type != PaymentBank.XYKCD)
			{
				errmsg = "银联接口不支持该交易";
				new MessageBox(errmsg);
				return false;
			}

			// 先删除上次交易数据文件
			if (PathFile.fileExist("c:\\bank\\request.txt"))
			{
				PathFile.deletePath("c:\\bank\\request.txt");

				if (PathFile.fileExist("c:\\bank\\request.txt"))
				{
					errmsg = "交易请求文件无法删除,请重试";
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist("c:\\bank\\result.txt"))
			{
				PathFile.deletePath("c:\\bank\\result.txt");

				if (PathFile.fileExist("c:\\bank\\result.txt"))
				{
					errmsg = "交易应答文件无法删除,请重试";
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist("c:\\bank\\ToPrint\\Print.txt"))
			{
				PathFile.deletePath("c:\\bank\\ToPrint\\Print.txt");

				if (PathFile.fileExist("c:\\bank\\ToPrint\\Print.txt"))
				{
					errmsg = "交易应答文件无法删除,请重试";
					new MessageBox(errmsg);
					return false;
				}
			}

			// 写入请求数据
			if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo)) { return false; }

			// 调用接口模块
			// 调用接口模块
			if (PathFile.fileExist("c:\\bank\\javaposbank.exe"))
			{
				CommonMethod.waitForExec("c:\\bank\\javaposbank.exe SZRB");
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
			
			if (type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XYKCD)
			{
				XYKPrintDoc();
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);

			return false;
		}
	}

	public boolean XYKWriteRequest(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		StringBuffer sbstr = null;

		try
		{
			sbstr = new StringBuffer();

			// 组织请求数据
			// 操作类型,交易类型,卡类型,收银机编号,操作员,金额,收银流水号,原交易流水号,预留字段

			if (type == PaymentBank.XYKXF) response.TransType = "11"; //消费
			else if (type == PaymentBank.XYKCX) response.TransType = "30"; //消费撤销
			else if (type == PaymentBank.XYKTH) response.TransType = "12"; //隔日退货
			else if (type == PaymentBank.XYKQD) response.TransType = "01"; //交易签到
			else if (type == PaymentBank.XYKJZ) response.TransType = "06"; //交易结账
			else if (type == PaymentBank.XYKYE) response.TransType = "10"; //余额查询
			else if (type == PaymentBank.XYKCD) response.TransType = "27"; //签购单重打
			else
			{
				throw new Exception("无效的交易类型!");
			}

			String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1));
			jestr = Convert.increaseCharForward(jestr, '0', 12);
			response.Amount = jestr;

			response.OperNo = Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 6);

			response.TerminalId = Convert.increaseChar(ConfigClass.CashRegisterCode, ' ', 8);

			if (track2 != null)
			{
				response.Track2 = Convert.increaseChar(track2, 37);
			}
			else
			{
				response.Track2 = Convert.increaseChar("", 37);
			}

			if (track3 != null)
			{
				response.Track3 = Convert.increaseChar(track3, 104);
			}
			else
			{
				response.Track3 = Convert.increaseChar("", 104);
			}

			if (type == PaymentBank.XYKCX)
			{
				response.OldTrace = oldseqno;
				response.OldRrn = oldauthno;
				response.OldBatchNo = olddate;
			}

			if (type == PaymentBank.XYKCD)
			{
				response.OldTrace = oldseqno;
			}

			sbstr.append(response.TransType + "," + response.PinFlag + "," + response.Amount + "," + response.BudgetPlan + "," + response.OperNo
					+ "," + response.TerminalId + "," + response.CardId + "," + response.OldDate + "," + response.OldTrace + ","
					+ response.OldBatchNo + "," + response.OldAuthNo + "," + response.OldRrn + "," + response.Track2 + "," + response.Track3 + ","
					+ response.IssueId + "," + response.IssueName + "," + response.AcqId + "," + response.AcqName + "," + response.FirstAmt + ","
					+ response.LastAmt + "," + response.Fee + "," + response.RetCode + "," + response.RetInfo + "," + response.Remark);

			// 写入请求数据
			if (!rtf.writeFile("c:\\bank\\request.txt", sbstr.toString()))
			{
				new MessageBox("写入金卡工程请求数据失败!", null, false);

				return false;
			}

			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("写入金卡工程请求数据异常!\n\n" + ex.getMessage(), null, false);
			ex.printStackTrace();

			return false;
		}
		finally
		{
			if (sbstr != null)
			{
				sbstr.delete(0, sbstr.length());
				sbstr = null;
			}

		}
	}

	public boolean XYKReadResult()
	{
		try
		{
			if (!PathFile.fileExist("c:\\bank\\result.txt") || !rtf.loadFileByGBK("c:\\bank\\result.txt"))
			{
				new MessageBox("读取金卡工程应答数据失败!", null, false);

				return false;
			}

			// 读取请求数据
			String line = rtf.nextRecord();
			rtf.close();
			String[] result;
			if (line.indexOf(',') > -1)
			{
				result = line.split(",");

				if (result.length != 25)
				{
					new MessageBox("金卡工程应答数据格式错误", null, false);

					return false;
				}
				else 
				{
					response.TransType = result[1].trim();
					response.PinFlag = result[2].trim();
					response.Amount = result[3].trim();
					response.BudgetPlan = result[4].trim();
					response.OperNo = result[5].trim();
					response.TerminalId = result[6].trim();
					response.CardId = result[7].trim();
					response.OldDate = result[8].trim();
					response.OldTrace = result[9].trim();
					response.OldBatchNo = result[10].trim();
					response.OldAuthNo = result[11].trim();
					response.OldRrn = result[12].trim();
					response.Track2 = result[13].trim();
					response.Track3 = result[14].trim();
					response.IssueId = result[15].trim();
					response.IssueName = result[16].trim();
					response.AcqId = result[17].trim();
					response.AcqName = result[18].trim();
					response.FirstAmt = result[19].trim();
					response.LastAmt = result[20].trim();
					response.Fee = result[21].trim();
					response.RetCode = result[22].trim();
					response.RetInfo = result[23].trim();
					response.Remark = result[24].trim();
				}
				if (!response.RetCode.equals("00") || !result[0].trim().equals("0"))
				{
					bld.retcode = response.RetCode;
					bld.retmsg = response.RetInfo;
					errmsg = bld.retmsg;
					return false;
				}
			}

			//
			bld.retcode = response.RetCode;
			bld.retmsg = response.RetInfo;
			bld.cardno = response.CardId;
			bld.bankinfo = response.IssueId + response.IssueName;
			double j = Double.parseDouble(response.Amount);
			j = ManipulatePrecision.mul(j, 0.01);
			bld.je = j;
			//
			errmsg = bld.retmsg;

			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);
			ex.printStackTrace();

			return false;
		}
//		finally
//		{
//			if (PathFile.fileExist("c:\\bank\\result.txt"))
//			{
//				PathFile.deletePath("c:\\bank\\result.txt");
//			}
//
//			if (PathFile.fileExist("c:\\bank\\request.txt"))
//			{
//				PathFile.deletePath("c:\\bank\\request.txt");
//			}
//		}
	}

	public void XYKPrintDoc()
	{
		ProgressBox pb = null;
		try
		{
			if (!PathFile.fileExist("c:\\bank\\ToPrint\\Print.txt"))
			{
				new MessageBox("找不到签购单打印文件!");
				return;
			}

			pb = new ProgressBox();
			pb.setText("正在打印银联签购单,请等待...");

			for (int i = 0; i < GlobalInfo.sysPara.bankprint; i++)
			{
				BufferedReader br = null;

				//
				Printer.getDefault().startPrint_Journal();

				try
				{
					//由于发现在windows环境下,用GBK读取文件会产生BUG,改为GB2310
					br = CommonMethod.readFileGB2312("c:\\bank\\ToPrint\\Print.txt");

					if (br == null)
					{
						new MessageBox("打开签购单打印文件失败!");

						return;
					}

					//
					String line = null;

					while ((line = br.readLine()) != null)
					{
						Printer.getDefault().printLine_Journal(line + "\n");
					}
				}
				catch (Exception e)
				{
					new MessageBox(e.getMessage());
				}
				finally
				{
					if (br != null)
					{
						br.close();
					}
				}

				// 切纸
				Printer.getDefault().cutPaper_Journal();
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

	public boolean XYKCheckRetCode()
	{
		if (bld.retcode.trim().equals("00"))
		{
			bld.retbz = 'Y';
			bld.retmsg = "金卡工程调用成功";

			return true;
		}
		else
		{
			bld.retbz = 'N';

			return false;
		}
	}

	
	public boolean checkDate(Text date)
	{
		return true;
	}

}
