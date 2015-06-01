package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

public class ZsGHSF_PaymentBankFunc extends PaymentBankFunc
{
	String path = "c:\\ICBC";

	public String[] getFuncItem()
	{
		String[] func = new String[7];
		func[0] = "[" + PaymentBank.XYKXF + "]" + "快速消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "退货";
		func[3] = "[" + PaymentBank.XYKCD + "]" + "重打签购单";
		func[4] = "[" + PaymentBank.XYKJZ + "]" + "结算交易";
		func[5] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
		func[6] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		switch (type)
		{
			case PaymentBank.XYKXF: // 快速消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKCX:// 当日撤销
				grpLabelStr[0] = "系统检索号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKTH:// 隔日退货
				grpLabelStr[0] = "系统检索号";
				grpLabelStr[1] = "交易终端号";
				grpLabelStr[2] = "交易日期";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKCD: // 重打签购单
				grpLabelStr[0] = "系统检索号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打签购单";
				break;
			case PaymentBank.XYKJZ: // 结算
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易结算";
				break;
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
				grpLabelStr[3] = null;
				grpLabelStr[4] = "余额查询";
				break;

		}

		return true;
	}

	public boolean getFuncText(int type, String[] grpTextStr)
	{
		switch (type)
		{
			case PaymentBank.XYKXF: // 快速消费
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKCX: // 当日撤销
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
				break;
			case PaymentBank.XYKCD: // 重打签购单
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始重打印";
				break;
			case PaymentBank.XYKJZ: // 交易结算
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始结算";
				break;
			case PaymentBank.XYKQD: // 交易签到
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKYE: // 余额查询
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
		}

		return true;
	}

	public boolean checkBankOperType(int operType, SaleBS saleBS, PaymentBank payObj)
	{
		boolean ok = true;
		if (saleBS != null)
		{
			if (
			// 销售交易或者扣回时,只允许选择0(消费)
			// 退货交易且非扣回时,只允许选择1(撤销),2(退货)
			((SellType.ISSALE(saleBS.saletype) || saleBS.isRefundStatus()) && operType != PaymentBank.XYKXF)
					|| ((SellType.ISBACK(saleBS.saletype) && !saleBS.isRefundStatus()) && operType != PaymentBank.XYKCX && operType != PaymentBank.XYKTH))
			{
				ok = false;
			}
		}
		else
		{
			if (
			// 删除付款时只允许选择1(撤销),2(退货)
			// 交易红冲时只允许选择1(撤销)
			// 后台退货时只允许选择1(撤销),2(退货)
			// 非小票交易不允许选择0(消费)
			(payObj != null && operType != PaymentBank.XYKCX && operType != PaymentBank.XYKTH)
					|| (payObj != null && SellType.ISHC(payObj.salehead.djlb) && operType != PaymentBank.XYKCX)
					|| (payObj != null && SellType.ISBACK(payObj.salehead.djlb) && operType != PaymentBank.XYKCX && operType != PaymentBank.XYKTH)
					|| (operType == PaymentBank.XYKXF && !salebyself))
			{
				ok = false;
			}
		}
		if (!ok)
		{
			new MessageBox("不允许进行该银联操作,请重新选择");
			return false;
		}
		if ((operType == PaymentBank.XYKTH)
				&& (("," + GlobalInfo.sysPara.salesReturncodeList + ",").indexOf("," + GlobalInfo.posLogin.role + ",") < 0))
		{
			new MessageBox("该收银员没有退货权限！！！");
			return false;
		}
		return true;
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		String request = "";
		//String response = "";
		try
		{
			if (("," + GlobalInfo.sysPara.salesReturncodeList + ",").indexOf("," + GlobalInfo.posLogin.role + ",") >= 0)
			{
				if (!(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XYKTH || type == PaymentBank.XYKCD
						|| type == PaymentBank.XYKJZ || type == PaymentBank.XYKQD || type == PaymentBank.XYKYE))
				{
					new MessageBox("银联接口不支持此交易类型！！！");
					return false;
				}
			}
			else
			{
				if (type == PaymentBank.XYKTH)
				{
					new MessageBox("该收银员没有退货权限！！！");
					return false;
				}
				else if (!(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XYKCD || type == PaymentBank.XYKJZ
						|| type == PaymentBank.XYKQD || type == PaymentBank.XYKYE))
				{
					new MessageBox("银联接口不支持此交易类型！！！");
					return false;
				}
			}
			

			if (PathFile.fileExist(path + "\\request.txt"))
			{
				for (int i = 0; i <= 2; i++) {
					PathFile.deletePath(path + "\\request.txt");
					if (!PathFile.fileExist(path + "\\request.txt"))
						break;
				}
				if (PathFile.fileExist(path + "\\request.txt")) {
					errmsg = "交易“request.txt”文件删除失败，请联系电脑部人员手动进行删除！！！";
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
					for (int i = 0; i <= 2; i++) {
						PathFile.deletePath(path + "\\result.txt");
						if (!PathFile.fileExist(path + "\\result.txt"))
							break;
					}
					if (PathFile.fileExist(path + "\\result.txt")) {
						errmsg = "交易“result.txt”文件删除失败，请联系电脑部人员手动进行删除！！！";
						XYKSetError("XX", errmsg);
						new MessageBox(errmsg);
						return false;
					}
				}				
			}
			PosLog.getLog(this.getClass().getSimpleName()).info("XYKExecute start(syjh=" + bld.syjh + ",syyh=" + bld.syyh + ")");

			// 调用接口模块
			request = XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo);
			PosLog.getLog(this.getClass().getSimpleName()).info("request=[" + request + "]");
			
			// 调用接口模块
			if (PathFile.fileExist(path + "\\javaposbank.exe"))
			{
				CommonMethod.waitForExec(path + "\\javaposbank.exe HBGH", "javaposbank.exe");
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

			// 打印签购单
			if (XYKNeedPrintDoc(type))
			{
				Printer.getDefault().close();//释放打印机
				Printer.getDefault().open();//重新连接打印机
				Printer.getDefault().setEnable(true);
				XYKPrintDoc(type);
			}
			return true;
		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			XYKSetError("XX", "金卡异常XX:" + ex.getMessage());
			new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);

			return false;
		}
	}

	public String XYKWriteRequest(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		String line = "";
		String type1 = ""; // 交易类型
		try
		{
			String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 20);
			String syjh = Convert.increaseChar(ConfigClass.CashRegisterCode, ' ', 20);
			String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1));
			jestr = Convert.increaseCharForward(jestr, '0', 12);
			String date = Convert.increaseChar(" ", ' ', 8); // 交易日期
			String seqno = Convert.increaseCharForward(" ", ' ', 8); // 检索号
			String authno = Convert.increaseChar(" ", ' ', 15); // 原交易终端号
			String strack2 = Convert.increaseChar(" ", ' ', 37); // 二磁道数据
			String strack3 = Convert.increaseChar(" ", ' ', 104); // 三磁道数据
			String TipAmount = Convert.increaseChar(" ", ' ', 12);// 小费金额
			String InstallmentTimes = Convert.increaseCharForward("0", '0', 2); // 分期期数
			String MisTraceNo = Convert.increaseChar(" ", ' ', 6);// Mis流水号
			String strack1 = Convert.increaseChar(" ", ' ', 19);// 交易卡号
			String ExpDate = Convert.increaseChar(" ", ' ', 4);// 卡有效期
			String AuthNo = Convert.increaseChar(" ", ' ', 6);// 授权号
			String FuncID = Convert.increaseChar(" ", ' ', 4); // 特色脚本ID号
			String PreInput = Convert.increaseChar(" ", ' ', 256);// 预输入项
			String AddDatas = Convert.increaseChar(" ", ' ', 256);// 固定输入项
			switch (type)
			{
				case PaymentBank.XYKXF: // 快速消费
					type1 = "21";
					break;
				case PaymentBank.XYKCX: // 当日撤销
					type1 = "04";
					seqno = Convert.increaseCharForward(oldseqno, '0', 8);// 原交易检索号
					date = Convert.increaseChar(" ", ' ', 8); // 交易日期
					authno = Convert.increaseCharForward(" ", ' ', 15); // 原交易终端号
					break;
				case PaymentBank.XYKTH: // 隔日退货
					type1 = "04";
					seqno = Convert.increaseCharForward(oldseqno, '0', 8);// 原交易检索号
					date = Convert.increaseChar(olddate, '0', 8); // 交易日期
					authno = Convert.increaseCharForward(oldauthno, '0', 15); // 原交易终端号
					break;
				case PaymentBank.XYKCD: // 重打签购单
					type1 = "13";
					seqno = Convert.increaseCharForward(oldseqno, '0', 8);// 原交易检索号
					break;
				case PaymentBank.XYKJZ: // 交易结算
					type1 = "15";
					break;
				case PaymentBank.XYKQD: // 交易签到
					type1 = "52";
					break;
				case PaymentBank.XYKYE: // 余额查询
					type1 = "52";
					break;
			}

			// 交易指令+分行特色脚本ID号+交易金额 +小费金额+交易日期+MIS流水号+交易卡号+卡片有效期+二磁道数据
			// +三磁道数据+系统检索号+授权号+交易终端号+分期期数+预输入项+固定输入项+收银机号+操作员号
			line = type1 + ',' + FuncID + ',' + jestr + ',' + TipAmount + ',' + date + ',' + MisTraceNo + ',' + strack1 + ',' + ExpDate + ','
					+ strack2 + ',' + strack3 + ',' + seqno + ',' + AuthNo + ',' + authno + ',' + InstallmentTimes + ',' + PreInput + ',' + AddDatas
					+ ',' + syjh + ',' + syyh;

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

			return line;
		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			new MessageBox("写入金卡工程请求数据异常!\n\n" + ex.getMessage(), null, false);			
			return null;
		}
	}

	public boolean XYKReadResult()
	{
		BufferedReader br = null;

		try
		{
			if (!PathFile.fileExist(path + "\\result.txt") || ((br = CommonMethod.readFileGBK(path + "\\result.txt")) == null))
			{
				XYKSetError("XX", "读取应答失败,交易失败!");
				new MessageBox("读取金卡工程应答数据失败!", null, false);

				return false;
			}

			String line = br.readLine();
			PosLog.getLog(this.getClass().getSimpleName()).info("result=[" + line + "]");

			if (line == null || line.length() <= 0) { return false; }
			String result[] = line.split(",");
			if (result == null) return false;
			bld.retmsg = result[26].trim(); // 错误说明
			bld.memo = result[27];
			int type = Integer.parseInt(bld.type.trim());
			bld.retcode = result[11].trim();
			if (line.length() >= 2)
			{
				if (!result[11].equals("00"))
				{// 交易失败
					switch (type)
					{
						case PaymentBank.XYKXF: // 快速消费
							bld.memo1 = "快速消费失败";
							break;
						case PaymentBank.XYKCX: // 当日撤销
							bld.memo1 = "当日撤销失败";
							break;
						case PaymentBank.XYKTH: // 隔日退货
							bld.memo1 = "隔日退货失败";
							break;
						case PaymentBank.XYKCD: // 重打签购单
							bld.memo1 = "重打签购单失败";
							break;
						case PaymentBank.XYKJZ: // 交易结算
							bld.memo1 = "交易结算失败";
							break;
						case PaymentBank.XYKQD: // 交易签到
							bld.memo1 = "交易签到失败";
							break;
						case PaymentBank.XYKYE: // 余额查询
							bld.memo1 = "余额查询失败";
							break;
					}
					return false;
				}
				else
				{
					switch (type)
					{
						case PaymentBank.XYKXF: // 快速消费
							bld.memo1 = "快速消费成功";
							break;
						case PaymentBank.XYKCX: // 当日撤销
							bld.memo1 = "当日撤销成功";
							break;
						case PaymentBank.XYKTH: // 隔日退货
							bld.memo1 = "隔日退货成功";
							break;
						case PaymentBank.XYKCD: // 重打签购单
							bld.memo1 = "重打签购单成功";
							break;
						case PaymentBank.XYKJZ: // 交易结算
							bld.memo1 = "交易结算成功";
							break;
						case PaymentBank.XYKQD: // 交易签到
							bld.memo1 = "交易签到成功";
							break;
						case PaymentBank.XYKYE: // 余额查询
							bld.memo1 = "余额查询成功";
							break;
					}
				}
				if (type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XYKTH)
				{
					bld.cardno = result[1]; // 卡号
					bld.trace = Convert.toInt((result[9])); // 交易检索号
					double je = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Convert.toDouble((result[2])), 100), 2, 1);
					if (ManipulatePrecision.doubleConvert(bld.je) != je)
					{
						String strPay="消费";
						if(type == PaymentBank.XYKCX)
						{
							strPay="撤消";
						}
						else if(type == PaymentBank.XYKTH)
						{
							strPay="退货";
						}
						new MessageBox("收银系统发起的" + strPay + "金额（" + ManipulatePrecision.doubleConvert(bld.je) +"）与银行接口返回的金额（" + je + "）不匹配");
					}
					//bld.je = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Convert.toDouble((result[2])), 100), 2, 1); // 交易金额
					//bld.bankinfo = result[22];// 发卡行名称
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			XYKSetError("XX", "解析应答XX:" + ex.getMessage());
			new MessageBox("解析金卡工程应答数据异常!" + ex.getMessage(), null, false);			
			return false;
		}
		finally{
			if(br!=null){
				try {
					br.close();
					br=null;
				} catch (IOException e) {
					br=null;
					e.printStackTrace();
				}
			}
		}
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

	public boolean XYKNeedPrintDoc(int type)
	{
		if (!checkBankSucceed()) { return false; }
		if (type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XYKTH || type == PaymentBank.XYKCD
				|| type == PaymentBank.XYKJZ || type == PaymentBank.XYKQD || type == PaymentBank.XYKYE)
		{
			return true;
		}
		else return false;
	}

	public boolean checkBankSucceed()
	{
		if (bld.retbz == 'N')
		{
			errmsg = "交易失败" + bld.retcode + bld.retmsg;
			return false;
		}
		else
		{
			errmsg = "交易成功";

			return true;
		}
	}

	public void XYKPrintDoc(int type)
	{
		ProgressBox pb = null;
		String name = null;
		name = path + "\\ICBCPRTTKT.txt";

		try
		{
			if (!PathFile.fileExist(name))
			{
				PosLog.getLog(this.getClass().getSimpleName()).info("XYKPrintDoc 打印失败，签购单文件不存在：" + name);
				new MessageBox("找不到打印文件！！！");
				return;
			}
			pb = new ProgressBox();
			pb.setText("正在打印,请等待..." + "\t OY : " + GlobalInfo.sysPara.issetprinter);

			for (int i = 0; i < GlobalInfo.sysPara.bankprint; i++)
			{
				BufferedReader br = null;
				XYKPrintDoc_Start();
				try
				{
					PosLog.getLog(this.getClass().getSimpleName()).info("XYKPrintDoc" + i+1 + " start");
					br = CommonMethod.readFileGB2312(name);
					if (br == null)
					{
						if (type == PaymentBank.XYKJZ) new MessageBox("打开文件失败");
						return;
					}

					String line = null;
					while ((line = br.readLine()) != null)
					{
						if (line.length() <= 0) continue;
						PosLog.getLog(this.getClass().getSimpleName()).info("[" + line + "]");
						// 银行签购单模板添加 "CUTPAPER" 标记
						// 当程序里面读取到这个字符是，打印机切纸
						// if (line.indexOf("CUTPAPER") >= 0)
						if (line.trim().equals("CUTPAPER"))
						{
							XYKPrintDoc_End();
							new MessageBox("请撕下客户签购单！！！");

							continue;
						}

						XYKPrintDoc_Print(line);
					}
				}
				catch (Exception e)
				{
					PosLog.getLog(this.getClass().getSimpleName()).error(e);
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
					PosLog.getLog(this.getClass().getSimpleName()).info("XYKPrintDoc" + i+1 + " end");
				}
				XYKPrintDoc_End();
			}
		}
		catch (Exception e)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(e);
			if (type == PaymentBank.XYKJZ) new MessageBox("打印文件异常!!!\n" + e.getMessage());

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

	public void XYKSetError(String errCode, String errInfo)
	{
		PosLog.getLog(this.getClass().getSimpleName()).info("XYKSetError errCode=[" + errCode + "],errInfo=[" + errInfo + "]");
		super.XYKSetError(errCode, errInfo);
	}
}
