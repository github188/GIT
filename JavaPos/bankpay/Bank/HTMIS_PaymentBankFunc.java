package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

public class HTMIS_PaymentBankFunc extends PaymentBankFunc
{
	public String[] getFuncItem()
	{
		String[] func = new String[6];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[3] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
		func[4] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
		func[5] = "[" + PaymentBank.XYKCD + "]" + "重打签购单";
		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		//0-4对应FORM中的5个输入框
		//null表示该不用输入
		switch (type)
		{
			case PaymentBank.XYKXF: //消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = "请 刷 卡";
				grpLabelStr[4] = "交易金额";

				break;

			case PaymentBank.XYKCX: //消费撤销
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = "请 刷 卡";
				grpLabelStr[4] = "交易金额";

				break;

			case PaymentBank.XYKYE: //余额查询    
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = "请 刷 卡";
				grpLabelStr[4] = "余额查询";

				break;

			case PaymentBank.XYKTH: //隔日退货   
				grpLabelStr[0] = null;
				grpLabelStr[1] = "参 考 号";
				grpLabelStr[2] = "原交易日期";
				grpLabelStr[3] = "请 刷 卡";
				grpLabelStr[4] = "交易金额";

				break;

			case PaymentBank.XYKQD: //签到
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易签到";

				break;

			case PaymentBank.XYKCD: //重打印
				grpLabelStr[0] = "原流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易签到";

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

			case PaymentBank.XYKTH: //退货
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;

				break;

			case PaymentBank.XYKYE: //余额查询    
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始余额查询";

				break;

			case PaymentBank.XYKQD: //交易签到
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易签到";

				break;

			case PaymentBank.XYKCD: //重打印
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始重打印签购单";

				break;
		}

		return true;
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKTH) && (type != PaymentBank.XYKCX) && (type != PaymentBank.XYKQD)
					&& (type != PaymentBank.XYKYE) && (type != PaymentBank.XYKCD))
			{
				errmsg = "银联接口不支持该交易";
				new MessageBox(errmsg);

				return false;
			}

			//	先删除上次交易数据文件
			if (PathFile.fileExist("C:\\mis\\request.txt"))
			{
				PathFile.deletePath("C:\\mis\\request.txt");

				if (PathFile.fileExist("C:\\mis\\request.txt"))
				{
					errmsg = "交易请求文件request.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist("C:\\mis\\result.txt"))
			{
				PathFile.deletePath("C:\\mis\\result.txt");

				if (PathFile.fileExist("C:\\mis\\result.txt"))
				{
					errmsg = "交易结果文件result.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist("C:\\mis\\PrintBill1.txt"))
			{
				PathFile.deletePath("C:\\mis\\PrintBill1.txt");

				if (PathFile.fileExist("C:\\mis\\PrintBill1.txt"))
				{
					errmsg = "签购单文件PrintBill1.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist("C:\\mis\\PrintBill2.txt"))
			{
				PathFile.deletePath("C:\\mis\\PrintBill2.txt");

				if (PathFile.fileExist("C:\\mis\\PrintBill2.txt"))
				{
					errmsg = "签购单文件PrintBill2.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			// 写入请求数据
			String line = XYKgetRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo);

			PrintWriter pw = CommonMethod.writeFile("C:\\mis\\request.txt");

			if (pw != null)
			{
				pw.println(line);
				pw.flush();
				pw.close();
			}

			// 调用接口模块
			if (PathFile.fileExist("C:\\mis\\javaposbank.exe"))
			{
				CommonMethod.waitForExec("C:\\mis\\javaposbank.exe SLMYSHOP");
			}
			else
			{
				new MessageBox("找不到金卡工程模块 javaposbank.exe");
				XYKSetError("XX", "找不到金卡工程模块 javaposbank.exe");
				return false;
			}

			// 读取应答数据

			if (!XYKReadResult(type))
			{
				bld.retbz = 'N';
				return false;
			}

			// 检查交易是否成功
			if (!XYKCheckRetCode()) return false;

			if (XYKNeedPrintDoc(type))
			{
				if(Printer.getDefault().open()){
					Printer.getDefault().setEnable(true);
					XYKPrintDoc();
				}else{
					new MessageBox("打开打印机设备连接失败!");
				}
				
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

	public String XYKgetRequest(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{

			String line = "";
			String szCardNo = "";
			if (track2.trim().length() > 0 && track2.indexOf("=") != -1)
			{
				szCardNo = track2.trim().substring(0, track2.indexOf("="));
			}
			else
			{
				szCardNo = track2.trim();
			}
			bld.cardno = szCardNo;
			char nTransCode = ' ';

			//String szMoney = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1));
			String szMoney = String.valueOf(money);
			szMoney = Convert.increaseCharForward(szMoney, '0', 12);

			//根据不同的类型生成文本结构
			switch (type)
			{
				case PaymentBank.XYKXF:
					nTransCode = '3';
					break;
				case PaymentBank.XYKCX:
					nTransCode = '4';
					break;
				case PaymentBank.XYKTH:
					nTransCode = '5';
					break;
				case PaymentBank.XYKYE:
					nTransCode = '2';
					break;
				case PaymentBank.XYKCD:
					nTransCode = '6';
					break;
				default:
					nTransCode = '1';
					break;
			}
			/*1 交易类型代码 1签到，2查询，3消费，4撤销，5退货，6改密*/
			/*2 卡号 16位或19位*/
			/*3 2磁信息*/
			/*4 金额*/
			/*5 密码 6位数字*/
			/*6 新密码 6位数字*/
			/*7 流水号 */
			/*8 参考号 */
			/*9 交易日期 */
			/*10 返回码 2位*/
			/*11 余额*/
			/*12 返回字符串*/

			line = nTransCode + "," + szCardNo + "," + track2 + "," + szMoney + ",,," + oldseqno + "," + oldauthno + "," + olddate + ",,,";
			return line;
		}
		catch (Exception ex)
		{
			new MessageBox("写入金卡工程请求数据异常!\n\n" + ex.getMessage(), null, false);
			ex.printStackTrace();

			return null;
		}
	}

	public boolean XYKReadResult(int type)
	{
		BufferedReader br = null;
		try
		{
			if (!PathFile.fileExist("C:\\mis\\result.txt")
					|| ((br = CommonMethod.readFileGBK("C:\\mis\\result.txt")) == null))
			{
				XYKSetError("XX", "读取金卡工程应答数据失败!");
				new MessageBox("读取金卡工程应答数据失败!", null, false);

				return false;
			}
			String line = br.readLine();
			if (line.indexOf(",") == -1)
			{
				new MessageBox(line.trim());
				bld.retmsg = line.trim();
				return false;
			}
			String line1[] = line.split(",");
			if (line1.length > 12 && !line1[12].trim().equals("")) bld.retmsg = line1[12].trim();

			if (line1.length > 10 && !line1[10].trim().equals(""))
			{
				bld.retcode = line1[10].trim();
			}
			else
			{

				return false;
			}

			if (!bld.retcode.equals("00")) { return false; }

			//if (line1.length > 2) bld.cardno = line1[2].trim();
			if (line1.length > 4 && !line1[4].trim().equals(""))
			{
				String je = line1[4].trim();
				//double j = Double.parseDouble(je);
				//j = ManipulatePrecision.mul(j, 0.01);
				bld.je = Double.parseDouble(je);
			}
			if (line1.length > 7 && !line1[7].trim().equals(""))
			{
				bld.trace = Long.parseLong(line1[7].trim());
			}
			if (line1.length > 8 && !line1[8].trim().equals(""))
			{
				bld.authno = line1[8].trim();
			}

			if (type==5)
			{
				if (line1.length > 11 && !line1[11].trim().equals(""))
				{
					String je = line1[11].trim();
					//double j = Double.parseDouble(je);
					//j = ManipulatePrecision.mul(j, 0.01);
					//new MessageBox("卡余额为:" + je, GlobalVar.Validation);
					new MessageBox("卡余额为:" + je);
				}else{
					new MessageBox("返回信息中卡余额为空！");
				}
			}

			errmsg = bld.retmsg;
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
					/*
					 if (PathFile.fileExist("C:\\mis\\request.txt"))
					 {
					 PathFile.deletePath("C:\\mis\\request.txt");
					 }

					 if (PathFile.fileExist("C:\\mis\\result.txt"))
					 {
					 PathFile.deletePath("C:\\mis\\result.txt");
					 }
					 */
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	protected boolean XYKNeedPrintDoc(int type)
	{
		if (type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XYKTH || type == PaymentBank.XYKCD) { return true; }
		return false;
	}

	public void XYKPrintDoc()
	{
		ProgressBox pb = null;
		String printName1 = "C:\\mis\\PrintBill1.txt";
		String printName2 = "C:\\mis\\PrintBill2.txt";
		for (int i = 0; i < GlobalInfo.sysPara.bankprint; i++)
		{
			try
			{
				if (!PathFile.fileExist(printName1))
				{
					new MessageBox("找不到签购单打印PrintBill1.txt文件!");
					return;
				}

				pb = new ProgressBox();
				pb.setText("正在打印银联签购单,请等待...");

				BufferedReader br = null;

				//
				Printer.getDefault().startPrint_Journal();

				try
				{
					//由于发现在windows环境下,用GBK读取文件会产生BUG,改为GB2310
					br = CommonMethod.readFileGB2312(printName1);

					if (br == null)
					{
						new MessageBox("打开签购单打印PrintBill1.txt文件失败!");

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

				

				//打印第二联客户联
				if (!PathFile.fileExist(printName2))
				{
					new MessageBox("找不到签购单打印PrintBill2.txt文件!");
					return;
				}

				BufferedReader br1 = null;

				//
				Printer.getDefault().startPrint_Journal();

				try
				{
					//由于发现在windows环境下,用GBK读取文件会产生BUG,改为GB2310
					br1 = CommonMethod.readFileGB2312(printName2);

					if (br1 == null)
					{
						new MessageBox("打开签购单打印PrintBill2.txt文件失败!");

						return;
					}

					//
					String line = null;

					while ((line = br1.readLine()) != null)
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
					if (br1 != null)
					{
						br1.close();
					}
				}

				// 切纸
				Printer.getDefault().cutPaper_Journal();

			
			}
			catch (Exception ex)
			{
				new MessageBox("打印签购单发生异常\n\n" + ex.getMessage());
				ex.printStackTrace();
			}
			finally
			{
				Printer.getDefault().close();
				//doClosePrint = true;
				if (pb != null)
				{
					pb.close();
					pb = null;
				}
			}
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
}
