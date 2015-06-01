package com.efuture.javaPos.Payment.Bank;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.RdPlugins;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Struct.SalePayDef;

public class WuHanTong_PaymentBankFunc extends PaymentBankFunc implements
		Serializable
{
	private Vector salepay;
	private static final long serialVersionUID = 2L;
	private QueryDef globalquery = null;
	private String timeout = ConfigClass.LocalDBPath + "/timeout.dat";

	public String[] getFuncItem()
	{
		String[] func = new String[7];

		func[0] = "[" + PaymentBank.XYKQD + "]" + "设备自检";
		func[1] = "[" + PaymentBank.XYKXF + "]" + "消    费";
		func[2] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
		func[3] = "[" + PaymentBank.XKQT1 + "]" + "卡片充值";
		func[4] = "[" + PaymentBank.XYKCX + "]" + "充值撤销";
		func[5] = "[" + PaymentBank.XYKJZ + "]" + "门店购卡";
		func[6] = "[" + PaymentBank.XKQT3 + "]" + "消费记录";

		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		switch (type)
		{
			case PaymentBank.XYKQD: // 设备自检
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "设备自检";
				break;
			case PaymentBank.XYKXF: // 消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKYE:// 查询余额
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "查询余额";
				break;
			case PaymentBank.XKQT1: // 卡片充值
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "充值金额";
				break;
			case PaymentBank.XYKCX: // 撤销
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "充值撤销";
				break;
			case PaymentBank.XKQT3: // 消费查询
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "查询条数";
				break;
			case PaymentBank.XYKJZ: // 充值
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "金  额";
				break;
		}

		return true;
	}

	public boolean getFuncText(int type, String[] grpTextStr)
	{
		switch (type)
		{
			case PaymentBank.XYKQD: // 自检
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键进行自检";
				break;
			case PaymentBank.XYKXF: // 消费
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
				grpTextStr[4] = "按回车键进行查余";
				break;
			case PaymentBank.XKQT1: // 卡片充值
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKCX: // 卡片撤销
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车撤销最后一笔";

			case PaymentBank.XKQT3: // 消费查询
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKJZ: // 消费查询
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
		}

		return true;
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		Request req = null;
		globalquery = null;

		
		if (type == PaymentBank.XYKXF && (memo == null || memo.size() == 0 || memo.get(2) == null))
		{
			bld.retmsg = "非法交易";
			return false;
		}

		if (memo != null && memo.size() > 2)
			this.salepay = ((SaleBS) memo.get(2)).salePayment;

		try
		{
			if (RdPlugins.getDefault().getPlugins1() == null)
			{
				GlobalInfo.useWhtCharge = false;

				errmsg = "武汉通未成功加载";
				new MessageBox(errmsg);
				return false;
			}

			GlobalInfo.useWhtCharge = true;

			if ((type != PaymentBank.XYKCX) && (type != PaymentBank.XYKJZ) && (type != PaymentBank.XYKXF) && (type != PaymentBank.XYKQD) && (type != PaymentBank.XYKYE) && type != PaymentBank.XKQT2 && (type != PaymentBank.XKQT1) && (type != PaymentBank.XKQT3))
			{
				errmsg = "武汉通不支持该交易";
				new MessageBox(errmsg);

				return false;
			}

			// 检测是否存在超时文件
			if (type == PaymentBank.XYKXF || type == PaymentBank.XKQT1)
				type = existTimeout(type);

			req = new Request();

			switch (type)
			{
				case PaymentBank.XYKQD: // 自检
					check(req);
					break;
				case PaymentBank.XYKYE: // 余额
					query(req);
					break;
				case PaymentBank.XYKXF: // 消费
					sale(req, money);
					break;
				case PaymentBank.XKQT1: // 充值
					rechange(req, money);
					break;
				case PaymentBank.XYKCX: // 撤销
					cancel(req);
					break;
				case PaymentBank.XKQT2: // 超时
					timeout(req);
					break;
				case PaymentBank.XKQT3: // 查找终端数据
					saleList(req);
					break;
				case PaymentBank.XYKJZ:
					saleCard(req, money);
					break;

				default:
					break;
			}

			XYKCheckRetCode();

			// 打印签购单
			if (XYKNeedPrintDoc())
				XYKPrintDoc();

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean XYKNeedPrintDoc()
	{
		if (bld.type.equals(String.valueOf(PaymentBank.XYKXF)) || bld.type.equals(String.valueOf(PaymentBank.XKQT1)))
		{
			if (!bld.retcode.equals("00"))
				return false;

			StringBuffer sb = new StringBuffer();
			PrintWriter pw = null;
			String saleJe = "", ye = "";
			try
			{
				saleJe = String.valueOf(bld.je);
				ye = String.valueOf(bld.kye);

				sb.append("          武汉通缴款凭证              \r\n");
				sb.append("---------------------------------\r\n");
				sb.append("收银机号      收银员号         发票号\r\n");
				sb.append(bld.syjh + "           " + bld.syyh + "           " + String.valueOf(bld.fphm) + "\r\n");
				sb.append("---------------------------------\r\n");
				sb.append("交易时间:");
				sb.append(bld.authno + "\r\n");
				sb.append("交易卡号: ");
				sb.append(bld.cardno + "\r\n");
				sb.append("交易金额: ");
				sb.append(saleJe + "\r\n");
				sb.append("当前余额: ");
				sb.append(ye + "\r\n");
				sb.append("打印时间: ");
				sb.append(ManipulateDateTime.getCurrentDateTime() + "\r\n");

				sb.toString();

				if (PathFile.fileExist("c:\\javapos\\whtprn.txt"))
					PathFile.deletePath("c:\\javapos\\whtprn.txt");

				pw = CommonMethod.writeFileUTF("c:\\javapos\\whtprn.txt");

				if (pw != null)
				{
					pw.println(sb);
					pw.flush();
				}
				return true;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				return false;
			}
			finally
			{
				if (pw != null)
					pw.close();
			}
		}
		return false;
	}

	private boolean check(Request req)
	{
		try
		{
			req.setTradetype("01");
			req.setCardno("");
			req.setTradeamount(0);
			req.setLeaveamount(0);
			req.setCounter(0);
			req.setReserve("");

			if (!RdPlugins.getDefault().getPlugins1().exec(req.getRequestString()))
			{
				new MessageBox("武汉通调用失败");
				return false;
			}

			if (!RdPlugins.getDefault().getPlugins1().getErrorCode().equals("0"))
			{
				new MessageBox(RdPlugins.getDefault().getPlugins1().getErrorMsg());
				return false;
			}

			String retinfo = RdPlugins.getDefault().getPlugins1().getErrorMsg();
			if (retinfo != null && retinfo.length() > 1)
			{
				CheckSelfDef check = new CheckSelfDef(req.getTradetype());
				if (check.splitResult(retinfo))
				{
					bld.retcode = check.retcode;
					bld.retmsg = check.retmsg;
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	private boolean sale(Request req, double money)
	{
		try
		{
			globalquery = new QueryDef();
			globalquery.setTimeoutType("03");

			if (!queryBeforeSale(globalquery))
				return false;

			if (salepay != null)
			{
				for (int i = 0; i < salepay.size(); i++)
				{
					SalePayDef spd = (SalePayDef) salepay.get(i);
					if (spd.payno.equals(globalquery.surfacecardno))
					{
						new MessageBox("武汉通不允许同一张卡多次付款");
						return false;
					}
				}
			}

			if (!doTimeoutFile(true))
			{
				bld.retcode = "99";
				bld.retmsg = "超时文件写入失败，交易终止";
				return false;
			}

			req.setTradetype("03");
			req.setCardno("0");
			req.setTradeamount(money);
			req.setLeaveamount(0);
			req.setCounter(0);
			req.setReserve("");

			if (!RdPlugins.getDefault().getPlugins1().exec(req.getRequestString()))
			{
				new MessageBox("武汉通消费失败\n请再次付款并选择补入操作\n系统将自动查询当前交易是否成功");
				return false;
			}

			if (!RdPlugins.getDefault().getPlugins1().getErrorCode().equals("0"))
			{
				new MessageBox(RdPlugins.getDefault().getPlugins1().getErrorMsg());
				return false;
			}

			String retinfo = RdPlugins.getDefault().getPlugins1().getErrorMsg();
			if (retinfo != null && retinfo.length() > 1)
			{
				SaleDef sale = new SaleDef(req.getTradetype());
				if (sale.splitResult(retinfo))
				{
					bld.retcode = sale.retcode;
					bld.cardno = sale.surfacecardno;
					bld.trace = Convert.toLong(sale.psamseqno);
					bld.allotje = sale.beforetradeamount;
					bld.je = sale.tradeamount;
					bld.kye = sale.aftertradeamount;
					bld.memo = sale.logiccardno + "|" + sale.physicacardno;
					bld.memo1 = sale.psamcardno + "|" + sale.primarycardtype + "|" + sale.subcardtype;
					bld.authno = sale.tradedate;
					bld.crc = sale.tradetype;

					removeTimeout();
				}
				else
				{
					bld.retcode = sale.retcode;
					bld.retmsg = sale.retmsg;

					removeTimeout();
				}
			}
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("武汉通消费异常\n请再次付款并选择补入操作\n系统将自动查询当前交易是否成功");
			return false;
		}

	}

	private boolean query(Request req)
	{

		try
		{
			req.setTradetype("02");
			req.setCardno("");
			req.setTradeamount(0);
			req.setLeaveamount(0);
			req.setCounter(0);
			req.setReserve("");

			if (!RdPlugins.getDefault().getPlugins1().exec(req.getRequestString()))
			{
				new MessageBox("武汉通调用失败");
				return false;
			}

			if (!RdPlugins.getDefault().getPlugins1().getErrorCode().equals("0"))
			{
				new MessageBox(RdPlugins.getDefault().getPlugins1().getErrorMsg());
				return false;
			}

			String retinfo = RdPlugins.getDefault().getPlugins1().getErrorMsg();
			if (retinfo != null && retinfo.length() > 1)
			{
				QueryDef query = new QueryDef(req.getTradetype());
				if (query.splitResult(retinfo))
				{
					bld.retcode = query.retcode;
				}
				else
				{
					bld.retcode = query.retcode;
					bld.retmsg = query.retmsg;
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	private boolean cancel(Request req)
	{
		try
		{
			req.setTradetype("08");
			req.setCardno("");
			req.setTradeamount(0);
			req.setLeaveamount(0);
			req.setCounter(0);
			req.setReserve("");

			if (!RdPlugins.getDefault().getPlugins1().exec(req.getRequestString()))
			{
				new MessageBox("武汉通调用失败");
				return false;
			}

			if (!RdPlugins.getDefault().getPlugins1().getErrorCode().equals("0"))
			{
				new MessageBox(RdPlugins.getDefault().getPlugins1().getErrorMsg());
				return false;
			}

			String retinfo = RdPlugins.getDefault().getPlugins1().getErrorMsg();
			if (retinfo != null && retinfo.length() > 1)
			{
				CancelDef cancel = new CancelDef(req.getTradetype());
				if (cancel.splitResult(retinfo))
				{
					bld.retcode = cancel.retcode;
					bld.cardno = cancel.surfacecardno;
					bld.trace = Convert.toLong(cancel.psamtrans);
					bld.allotje = cancel.beforetradeamount;
					bld.je = cancel.tradeamount;
					bld.kye = cancel.aftertradeamount;
					bld.memo = cancel.logiccardno + "|" + cancel.physicacardno;
					bld.memo1 = cancel.psamcardno + "|" + cancel.primarycardtype + "|" + cancel.subcardtype;
					bld.authno = cancel.tradedate;
					bld.crc = cancel.tradetype;

					removeTimeout();
				}
				else
				{
					bld.retcode = cancel.retcode;
					bld.retmsg = cancel.retmsg;

					removeTimeout();
				}
			}
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	private boolean rechange(Request req, double money)
	{
		try
		{
			globalquery = new QueryDef();
			globalquery.setTimeoutType("05");

			if (!queryBeforeSale(globalquery))
			{
				new MessageBox("武汉通查询失败,无法消费");
				return false;
			}

			if (!doTimeoutFile(true))
			{
				bld.retcode = "99";
				bld.retmsg = "超时文件写入失败，交易终止";
				return false;
			}

			req.setTradetype("05");
			req.setCardno("");
			req.setTradeamount(money);
			req.setLeaveamount(0);
			req.setCounter(0);
			req.setReserve("");

			if (!RdPlugins.getDefault().getPlugins1().exec(req.getRequestString()))
			{
				new MessageBox("武汉通调用失败");
				return false;
			}

			if (!RdPlugins.getDefault().getPlugins1().getErrorCode().equals("0"))
			{
				new MessageBox(RdPlugins.getDefault().getPlugins1().getErrorMsg());
				return false;
			}

			String retinfo = RdPlugins.getDefault().getPlugins1().getErrorMsg();
			if (retinfo != null && retinfo.length() > 1)
			{

				RechangeDef rechange = new RechangeDef(req.getTradetype());
				if (rechange.splitResult(retinfo))
				{
					bld.retcode = rechange.retcode;
					bld.cardno = rechange.surfacecardno;
					bld.trace = Convert.toLong(rechange.selfserverno);
					bld.allotje = rechange.beforetradeamount;
					bld.je = rechange.tradeamount;
					bld.kye = rechange.aftertradeamount;
					bld.memo = rechange.logiccardno + "|" + rechange.physicacardno;
					bld.memo1 = rechange.psamcardno + "|" + rechange.primarycardtype + "|" + rechange.subcardtype;
					bld.authno = rechange.tradedate;
					bld.crc = rechange.tradetype;

					removeTimeout();
				}
				else
				{
					bld.retcode = rechange.retcode;
					bld.retmsg = rechange.retmsg;

					removeTimeout();
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}

	}

	private boolean timeout(Request req)
	{

		try
		{
			req.setTradetype("04");
			req.setCardno("");
			req.setTradeamount(0);
			req.setLeaveamount(0);
			req.setCounter(0);
			req.setReserve("");

			if (!RdPlugins.getDefault().getPlugins1().exec(req.getRequestString()))
			{
				new MessageBox("武汉通调用失败");
				return false;
			}

			if (!RdPlugins.getDefault().getPlugins1().getErrorCode().equals("0"))
			{
				new MessageBox(RdPlugins.getDefault().getPlugins1().getErrorMsg());
				return false;
			}

			String retinfo = RdPlugins.getDefault().getPlugins1().getErrorMsg();
			if (retinfo != null && retinfo.length() > 1)
			{
				TimeoutDef timeoutdata = new TimeoutDef(req.getTradetype());
				if (!timeoutdata.splitResult(retinfo))
				{
					new MessageBox("武汉通获取超时数据失败");
					return false;
				}

				if (!confirmTimeout(timeoutdata))
				{
					new MessageBox("武汉通处理超时失败\n请再次确认交易是否成功");
					return false;
				}

				bld.crc = timeoutdata.primarytradetype; // 原始类型
				bld.retcode = timeoutdata.retcode;
				bld.cardno = timeoutdata.surfacecardno;
				bld.je = timeoutdata.saleamount;
				bld.kye = timeoutdata.leaveamount;
				bld.memo = timeoutdata.logiccardno;
				bld.authno = timeoutdata.saledate;

				removeTimeout();
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}

	}

	private boolean saleList(Request req)
	{

		try
		{
			req.setTradetype("06");
			req.setCardno("");
			req.setTradeamount(0);
			req.setLeaveamount(0);
			req.setCounter(0);
			req.setReserve("");
			req.setReserve(ManipulatePrecision.doubleToString(0, 0, 1));
			if (!RdPlugins.getDefault().getPlugins1().exec(req.getRequestString()))
			{
				new MessageBox("武汉通调用失败");
				return false;
			}

			if (!RdPlugins.getDefault().getPlugins1().getErrorCode().equals("0"))
			{
				new MessageBox(RdPlugins.getDefault().getPlugins1().getErrorMsg());
				return false;
			}

			String retinfo = RdPlugins.getDefault().getPlugins1().getErrorMsg();
			if (retinfo != null && retinfo.length() > 1)
			{
				QueryDef query = new QueryDef(req.getTradetype());
				if (query.splitResult(retinfo))
					bld.retcode = query.retcode;
				else
				{
					bld.retcode = query.retcode;
					bld.retmsg = query.retmsg;
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}

	}

	private boolean saleCard(Request req, double money)
	{
		try
		{
			req.setTradetype("07");
			req.setCardno("");
			req.setTradeamount(money);
			req.setLeaveamount(0);
			req.setCounter(0);
			req.setReserve("");

			if (!RdPlugins.getDefault().getPlugins1().exec(req.getRequestString()))
			{
				new MessageBox("武汉通调用失败");
				return false;
			}

			if (!RdPlugins.getDefault().getPlugins1().getErrorCode().equals("0"))
			{
				new MessageBox(RdPlugins.getDefault().getPlugins1().getErrorMsg());
				return false;
			}

			String retinfo = RdPlugins.getDefault().getPlugins1().getErrorMsg();
			if (retinfo != null && retinfo.length() > 1)
			{

				RechangeDef rechange = new RechangeDef(req.getTradetype());
				if (rechange.splitResult(retinfo))
				{
					bld.retcode = rechange.retcode;
					bld.cardno = rechange.surfacecardno;
					bld.trace = Convert.toLong(rechange.selfserverno);
					bld.allotje = rechange.beforetradeamount;
					bld.je = rechange.tradeamount;
					bld.kye = rechange.aftertradeamount;
					bld.memo = rechange.logiccardno + "|" + rechange.physicacardno;
					bld.memo1 = rechange.psamcardno + "|" + rechange.primarycardtype + "|" + rechange.subcardtype;
					bld.authno = rechange.tradedate;
					bld.crc = rechange.tradetype;

					removeTimeout();
				}
				else
				{
					bld.retcode = rechange.retcode;
					bld.retmsg = rechange.retmsg;

					// removeTimeout();
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}

	}

	public void XYKPrintDoc()
	{
		ProgressBox pb = null;
		BufferedReader br = null;

		try
		{
			String printName = "";

			if ((bld.type.equals(String.valueOf(PaymentBank.XYKXF)) || bld.type.equals(String.valueOf(PaymentBank.XKQT1))))
			{
				if (!PathFile.fileExist("c:\\javapos\\whtprn.txt"))
				{
					new MessageBox("未发现打印文件!");
					return;
				}
				else
				{
					printName = "c:\\javapos\\whtprn.txt";
				}
			}

			pb = new ProgressBox();
			pb.setText("正在打印消费凭证单,请等待...");

			XYKPrintDoc_Start();

			// 打印两份
			for (int i = 0; i < 2; i++)
			{
				br = CommonMethod.readFile(printName);

				if (br == null)
				{
					new MessageBox("打开" + printName + "打印文件失败!");
					return;
				}

				String line = null;

				while ((line = br.readLine()) != null)
				{
					XYKPrintDoc_Print(line);
				}

				XYKPrintDoc_Print("\n\n");
				br.close();
				br = null;
			}

			XYKPrintDoc_End();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return;
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
					br = null;
				}
			}
			if (pb != null)
			{
				pb.close();
			}
		}
	}

	public boolean confirmTimeout(TimeoutDef timeout)
	{
		try
		{
			if (!doTimeoutFile(false))
				return false;

			if (this.globalquery == null)
				return false;

			if (!globalquery.logiccardno.equals(timeout.logiccardno))
			{
				new MessageBox("卡号不匹配,超时业务失败");
				return false;
			}

			if (globalquery.cardcounter != timeout.cardcounter - 1)
			{
				if (globalquery.leaveamount != ManipulatePrecision.doubleConvert(timeout.leaveamount + timeout.saleamount, 2, 1))
				{
					new MessageBox("消费前后金额不匹配,超时业务失败");
					return false;
				}

			}
			else if (globalquery.cardcounter == timeout.cardcounter)
			{
				if (globalquery.leaveamount != ManipulatePrecision.doubleConvert(timeout.leaveamount, 2, 1))
				{
					new MessageBox("消费前后金额不匹配,超时业务失败");
					return false;
				}
			}

			timeout.primarytradetype = globalquery.timeouttype;

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean doTimeoutFile(boolean flag)
	{
		FileOutputStream out = null;
		FileInputStream in = null;
		int excptype = -1;

		try
		{
			if (flag)
			{
				excptype = 0;

				out = new FileOutputStream(timeout);
				ObjectOutputStream oos = new ObjectOutputStream(out);
				oos.writeObject(globalquery);

				oos.flush();
				oos.close();
				oos = null;
			}
			else
			{
				excptype = 1;

				in = new FileInputStream(timeout);
				ObjectInputStream ois = new ObjectInputStream(in);
				// ois.readObject();
				globalquery = (QueryDef) ois.readObject();
				ois.close();
				ois = null;
			}

			excptype = -1;
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return false;
		}
		finally
		{
			try
			{
				if (out != null)
					out.close();

				if (in != null)
					in.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			if (excptype == 0)
				removeTimeout();
		}
	}

	public void removeTimeout()
	{
		// 成功后将超时文件删掉
		if (PathFile.fileExist(timeout))
		{
			PathFile.deletePath(timeout);
			this.globalquery = null;
		}
	}

	private boolean queryBeforeSale(QueryDef query)
	{
		String retinfo = null;

		try
		{
			Request req = new Request();

			req.setTradetype("02");
			req.setCardno("");
			req.setTradeamount(0);
			req.setLeaveamount(0);
			req.setCounter(0);
			req.setReserve("");

			// 查询三次
			for (int i = 0; i < 3; i++)
			{
				if (i == 2)
				{
					new MessageBox("武汉通查询失败\n请确认武汉通POS机是否存在异常!");
					return false;
				}

				if (!RdPlugins.getDefault().getPlugins1().exec(req.getRequestString()))
					continue;

				if (!RdPlugins.getDefault().getPlugins1().getErrorCode().equals("0"))
				{
					if (i != 2)
						continue;
					else
						new MessageBox(RdPlugins.getDefault().getPlugins1().getErrorMsg());

					return false;
				}

				retinfo = RdPlugins.getDefault().getPlugins1().getErrorMsg();
				if (retinfo != null && retinfo.length() > 1)
				{
					query.tradetype = req.getTradetype();
					if (query.splitResult(retinfo, false))
						return true;

					return false;
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return false;
	}

	private int existTimeout(int type)
	{
		int ret = 0;
		if (PathFile.fileExist(timeout))
		{
			ret = new MessageBox("系统检测到武汉通上笔交易超时,是否现场补入?\n\n 任意键-超时补入 / 付款键-正常消费 ", null, false).verify();
			if (ret != GlobalVar.Pay)
				type = PaymentBank.XKQT2;
		}
		return type;
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

	public boolean wuHanTongLczc(double money)
	{
		return callBankFunc(PaymentBank.XKQT1, money, null, null, null, null, null, null, null);
	}

	public class Request
	{
		private String tradetype;
		private String syjh;
		private String fphm;
		private String jygs;
		private String mktcode;
		private String cardno;
		private String tradeamount;
		private String leaveamount;
		private String counter;
		private String reserve;

		public Request()
		{
			this.syjh = ManipulateStr.PadLeft(GlobalInfo.syjStatus.syjh, 6, '0');
			this.fphm = ManipulateStr.PadLeft(String.valueOf(GlobalInfo.syjStatus.fphm), 10, '0');
			this.jygs = ManipulateStr.PadLeft(GlobalInfo.sysPara.jygs, 8, '0');
			this.mktcode = ManipulateStr.PadLeft(GlobalInfo.sysPara.mktcode, 8, '0');
		}

		public void setTradetype(String tradetype)
		{
			this.tradetype = tradetype;
		}

		public void setCardno(String cardno)
		{
			this.cardno = ManipulateStr.PadLeft(cardno, 10, '0');
		}

		public void setTradeamount(double tradeamount)
		{
			this.tradeamount = Convert.increaseCharForward(String.valueOf((long) ManipulatePrecision.doubleConvert(tradeamount * 100, 2, 1)), '0', 12);
		}

		public void setLeaveamount(double leaveamount)
		{
			this.leaveamount = Convert.increaseCharForward(String.valueOf((long) ManipulatePrecision.doubleConvert(leaveamount * 100, 2, 1)), '0', 12);
		}

		public void setCounter(int counter)
		{
			this.counter = ManipulateStr.PadLeft(String.valueOf(counter), 8, '0');
		}

		public void setReserve(String reserve)
		{
			this.reserve = ManipulateStr.PadLeft(reserve, 10, '0');
		}

		public String getTradetype()
		{
			return tradetype;
		}

		public String getRequestString()
		{
			StringBuffer line = new StringBuffer();
			line.append(this.tradetype);
			line.append("#");
			line.append(this.syjh);
			line.append("#");
			line.append(this.fphm);
			line.append("#");
			line.append(this.jygs);
			line.append("#");
			line.append(this.mktcode);
			line.append("#");
			line.append(this.cardno);
			line.append("#");
			line.append(this.tradeamount);
			line.append("#");
			line.append(this.leaveamount);
			line.append("#");
			line.append(this.counter);
			line.append("#");
			line.append(this.reserve);

			System.out.println(line.toString());

			return line.toString();
		}
	}

	// 自检
	public class CheckSelfDef
	{
		public String tradetype;
		public String retcode;
		public String retmsg;

		public CheckSelfDef(String tradetype)
		{
			this.tradetype = tradetype;
		}

		public boolean splitResult(String data)
		{
			try
			{
				String[] array = data.split("#");

				if (array == null)
				{
					retcode = "XX";
					retmsg = "返回数据非法";
					return false;
				}

				if (array.length > 0 && array[0] != null)
				{
					if (!this.tradetype.equals(array[0]))
					{
						retcode = "XX";
						retmsg = "返回交易类型错误";
						return false;
					}
				}

				if (array.length > 1 && array[1] != null)
					retcode = array[1];

				if (array.length > 2 && array[2] != null)
					retmsg = array[2];

				if (array.length > 3 && array[3] != null)
					retmsg = retmsg + " " + array[2];

				return true;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();

				retcode = "XX";
				retmsg = "解析数据发生异常\n" + ex.getMessage();
				return false;
			}
		}
	}

	// 返回的查询内容
	public class SubQueryData
	{
		public String logiccardno;
		public String saledate;
		public String tradetype;
		public double saleamount;
		public long cardcounter;

		public boolean splitResult(String data, StringBuffer info)
		{
			try
			{
				String[] array = data.split("#");
				if (array == null && info != null)
					info.append(Convert.appendStringSize("", "消费记录返回错误", 1, 16, 16, 0) + "\n");

				for (int i = 0; i < array.length; i++)
				{
					if (array[i] == null)
						continue;

					String[] msg = array[i].split("\\|");

					if (msg == null)
						continue;

					if (msg.length > 0 && msg[0] != null)
						logiccardno = array[0];

					if (msg.length > 1 && msg[1] != null)
						saledate = msg[1];

					if (saledate != null && saledate.equals("00000000000000"))
						continue;

					if (msg.length > 2 && msg[2] != null)
						tradetype = msg[2];

					if (msg.length > 3 && msg[3] != null)
						saleamount = ManipulatePrecision.doubleConvert(Convert.toDouble(msg[3].trim()) / 100, 2, 1);

					if (msg.length > 4 && msg[4] != null)
						cardcounter = Convert.toLong(msg[4].trim());

					if (info != null)
					{
						if (tradetype == null)
							return true;

						String type = "未知消费";

						if (tradetype.equals("02"))
							type = "充    值";
						else if (tradetype.equals("06"))
							type = "消    费";
						else if (tradetype.equals("09"))
							type = "复合消费";

						// String line = "[时间]" + saledate + "  " + "[操作类型]" +
						// type + "  " + "[金额]" + String.valueOf(saleamount);
						String line = saledate + "  " + type + "  " + String.valueOf(saleamount);
						info.append(line + "\n");
					}
				}

				return true;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				if (info != null)
					info.append(Convert.appendStringSize("", "解析消费记录发生异常", 1, 16, 16, 0) + "\n");
				return false;
			}
		}
	}

	// 查询数据
	public class QueryDef implements Serializable
	{
		private static final long serialVersionUID = 1L;

		public String tradetype;
		public String timeouttype;
		public String retcode;
		public String retmsg;

		public String logiccardno;
		public String surfacecardno;
		public double leaveamount;
		public long offlineamount;
		public long onlineamount;
		public long cardcounter;

		private StringBuffer info;

		public QueryDef()
		{
		}

		public QueryDef(String tradetype)
		{
			info = new StringBuffer();
			this.tradetype = tradetype;
		}

		public void setTimeoutType(String type)
		{
			this.timeouttype = type;
		}

		public boolean splitResult(String data, boolean flag)
		{
			try
			{
				String[] array = data.split("#");

				if (array == null)
				{
					retcode = "XX";
					retmsg = "返回数据非法";
					return false;
				}

				if (array.length > 0 && array[0] != null)
				{
					if (!this.tradetype.equals(array[0]))
					{
						retcode = "XX";
						retmsg = "返回交易类型错误";
						return false;
					}
				}

				if (array.length > 1 && array[1] != null)
					retcode = array[1];

				if (!retcode.equals("00"))
				{
					if (array.length > 2 && array[2] != null)
						retmsg = array[2];

					return false;
				}

				if (array.length > 2 && array[2] != null)
				{
					// 查询终端数据
					if (array[2].indexOf("|") != -1)
					{
						for (int i = 2; i < array.length; i++)
						{
							SubQueryData queryinfo = new SubQueryData();
							queryinfo.splitResult(array[i], info);
						}
						new MessageBox(info.toString());
						return true;
					}
					else
					{
						logiccardno = array[2];
					}
				}

				if (array.length > 3 && array[3] != null)
					surfacecardno = array[3];

				if (array.length > 4 && array[4] != null)
					leaveamount = ManipulatePrecision.doubleConvert(Convert.toDouble(array[4].trim()) / 100, 2, 1);

				if (array.length > 5 && array[5] != null)
					offlineamount = Convert.toLong(array[5]);

				if (array.length > 6 && array[6] != null)
					onlineamount = Convert.toLong(array[6]);

				if (!flag)
				{
					if (array.length > 7 && array[7] != null)
					{
						SubQueryData queryinfo = new SubQueryData();
						queryinfo.splitResult(array[7], null);
					}
					return true;
				}

				String line = "卡   号:" + surfacecardno + "  余  额:" + leaveamount;
				info.append(line + "\n" + "最近5笔交易\n");

				if (array.length > 7 && array[7] != null)
				{
					for (int i = 7; i < array.length; i++)
					{
						SubQueryData queryinfo = new SubQueryData();
						queryinfo.splitResult(array[i], info);
					}
				}

				new MessageBox(info.toString());

				return true;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				return false;
			}
		}

		public boolean splitResult(String data)
		{
			return splitResult(data, true);
		}
	}

	// 消费数据
	public class SaleDef
	{
		public String tradetype;
		public String retcode;
		public String logiccardno;
		public String physicacardno;
		public String surfacecardno;
		public String psamcardno;
		public String psamseqno;
		public String primarycardtype;
		public String subcardtype;
		public double tradeamount;
		public double beforetradeamount;
		public double aftertradeamount;
		public String tradedate;
		public String reserve;
		public String retmsg;

		public SaleDef(String tradetype)
		{
			this.tradetype = tradetype;
		}

		public boolean splitResult(String data)
		{
			try
			{
				String[] array = data.split("#");

				if (array == null)
				{
					retcode = "XX";
					retmsg = "返回数据非法";
					return false;
				}

				if (array.length > 0 && array[0] != null)
				{
					if (!this.tradetype.equals(array[0]))
					{
						retcode = "XX";
						retmsg = "返回交易类型错误";
						return false;
					}
				}

				if (array.length > 1 && array[1] != null)
					retcode = array[1];

				if (!retcode.equals("00"))
				{
					if (array.length > 2 && array[2] != null)
						retmsg = array[2];

					return false;
				}

				if (array.length > 2 && array[2] != null)
					logiccardno = array[2];

				if (array.length > 3 && array[3] != null)
					physicacardno = array[3];

				if (array.length > 4 && array[4] != null)
					surfacecardno = array[4];

				if (array.length > 5 && array[5] != null)
					psamcardno = array[5];

				if (array.length > 6 && array[6] != null)
					psamseqno = array[6];

				if (array.length > 7 && array[7] != null)
					primarycardtype = array[7];

				if (array.length > 8 && array[8] != null)
					subcardtype = array[8];

				if (array.length > 9 && array[9] != null)
					tradeamount = ManipulatePrecision.doubleConvert(Convert.toDouble(array[9].trim()) / 100, 2, 1);

				if (array.length > 10 && array[10] != null)
					beforetradeamount = ManipulatePrecision.doubleConvert(Convert.toDouble(array[10].trim()) / 100, 2, 1);

				if (array.length > 11 && array[11] != null)
					aftertradeamount = ManipulatePrecision.doubleConvert(Convert.toDouble(array[11].trim()) / 100, 2, 1);

				if (array.length > 12 && array[12] != null)
					tradedate = array[12];

				if (array.length > 13 && array[13] != null)
					reserve = array[13];

				return true;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				return false;
			}
		}
	}

	// 超时数据
	public class TimeoutDef
	{
		public String tradetype;
		public String primarytradetype;
		public String logiccardno;
		public String physicacardno;
		public String surfacecardno;
		public double saleamount;
		public double leaveamount;
		public long cardcounter;
		public String saledate;
		public String retcode;
		public String retmsg;

		public TimeoutDef(String tradetype)
		{
			this.tradetype = tradetype;
		}

		public boolean splitResult(String data)
		{
			try
			{
				String[] array = data.split("#");

				if (array == null)
				{
					retcode = "XX";
					retmsg = "返回数据非法";
					return false;
				}

				if (array.length > 0 && array[0] != null)
				{
					if (!this.tradetype.equals(array[0]))
					{
						retcode = "XX";
						retmsg = "返回交易类型错误";
						return false;
					}
				}

				if (array.length > 1 && array[1] != null)
					retcode = array[1];

				if (!retcode.equals("00"))
				{
					if (array.length > 2 && array[2] != null)
						retmsg = array[2];

					return false;
				}

				if (array.length > 2 && array[2] != null)
					logiccardno = array[2];

				if (array.length > 3 && array[3] != null)
					physicacardno = array[3];

				if (array.length > 4 && array[4] != null)
					surfacecardno = array[4];

				if (array.length > 5 && array[5] != null)
					leaveamount = ManipulatePrecision.doubleConvert(Convert.toDouble(array[5].trim()) / 100, 2, 1);

				if (array.length > 6 && array[6] != null)
					cardcounter = Convert.toLong(array[6]);

				if (array.length > 7 && array[7] != null)
				{
					SubQueryData timeoutinfo = new SubQueryData();
					timeoutinfo.splitResult(array[7], null);

					saledate = timeoutinfo.saledate;
					primarytradetype = timeoutinfo.tradetype;
					saleamount = timeoutinfo.saleamount;
					cardcounter = timeoutinfo.cardcounter;
				}
				return true;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				return false;
			}
		}
	}

	public class CancelDef
	{
		public String tradetype;
		public String retcode;
		public String logiccardno;
		public String physicacardno;
		public String surfacecardno;
		public String psamcardno;
		public String psamtrans;
		public String primarycardtype;
		public String subcardtype;
		public double tradeamount;
		public double beforetradeamount;
		public double aftertradeamount;
		public String tradedate;
		public String reserve;
		public String retmsg;

		public CancelDef(String tradetype)
		{
			this.tradetype = tradetype;
		}

		public boolean splitResult(String data)
		{
			try
			{
				try
				{
					String[] array = data.split("#");

					if (array == null)
					{
						retcode = "XX";
						retmsg = "返回数据非法";
						return false;
					}

					if (array.length > 0 && array[0] != null)
					{
						if (!this.tradetype.equals(array[0]))
						{
							retcode = "XX";
							retmsg = "返回交易类型错误";
							return false;
						}
					}

					if (array.length > 1 && array[1] != null)
						retcode = array[1];

					if (!retcode.equals("00"))
					{
						if (array.length > 2 && array[2] != null)
							retmsg = array[2];

						return false;
					}

					if (array.length > 2 && array[2] != null)
						logiccardno = array[2];

					if (array.length > 3 && array[3] != null)
						physicacardno = array[3];

					if (array.length > 4 && array[4] != null)
						surfacecardno = array[4];

					if (array.length > 5 && array[5] != null)
						psamcardno = array[5];

					if (array.length > 6 && array[6] != null)
						psamtrans = array[6];

					if (array.length > 7 && array[7] != null)
						primarycardtype = array[7];

					if (array.length > 8 && array[8] != null)
						subcardtype = array[8];

					if (array.length > 9 && array[9] != null)
						tradeamount = ManipulatePrecision.doubleConvert(Convert.toDouble(array[9].trim()) / 100, 2, 1);

					if (array.length > 10 && array[10] != null)
						beforetradeamount = ManipulatePrecision.doubleConvert(Convert.toDouble(array[10].trim()) / 100, 2, 1);

					if (array.length > 11 && array[11] != null)
						aftertradeamount = ManipulatePrecision.doubleConvert(Convert.toDouble(array[11].trim()) / 100, 2, 1);

					if (array.length > 12 && array[12] != null)
						tradedate = array[12];

					if (array.length > 13 && array[13] != null)
						reserve = array[13];

					return true;
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
					return false;
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				return false;
			}
		}
	}

	// 充值数据
	public class RechangeDef
	{
		public String tradetype;
		public String retcode;
		public String logiccardno;
		public String physicacardno;
		public String surfacecardno;
		public String psamcardno;
		public String selfserverno;
		public String primarycardtype;
		public String subcardtype;
		public double tradeamount;
		public double beforetradeamount;
		public double aftertradeamount;
		public String tradedate;
		public String reserve;
		public String retmsg;

		public RechangeDef(String tradetype)
		{
			this.tradetype = tradetype;
		}

		public boolean splitResult(String data)
		{
			try
			{
				try
				{
					String[] array = data.split("#");

					if (array == null)
					{
						retcode = "XX";
						retmsg = "返回数据非法";
						return false;
					}

					if (array.length > 0 && array[0] != null)
					{
						if (!this.tradetype.equals(array[0]))
						{
							retcode = "XX";
							retmsg = "返回交易类型错误";
							return false;
						}
					}

					if (array.length > 1 && array[1] != null)
						retcode = array[1];

					if (!retcode.equals("00"))
					{
						if (array.length > 2 && array[2] != null)
							retmsg = array[2];

						return false;
					}

					if (array.length > 2 && array[2] != null)
						logiccardno = array[2];

					if (array.length > 3 && array[3] != null)
						physicacardno = array[3];

					if (array.length > 4 && array[4] != null)
						surfacecardno = array[4];

					if (array.length > 5 && array[5] != null)
						psamcardno = array[5];

					if (array.length > 6 && array[6] != null)
						selfserverno = array[6];

					if (array.length > 7 && array[7] != null)
						primarycardtype = array[7];

					if (array.length > 8 && array[8] != null)
						subcardtype = array[8];

					if (array.length > 9 && array[9] != null)
						tradeamount = ManipulatePrecision.doubleConvert(Convert.toDouble(array[9].trim()) / 100, 2, 1);

					if (array.length > 10 && array[10] != null)
						beforetradeamount = ManipulatePrecision.doubleConvert(Convert.toDouble(array[10].trim()) / 100, 2, 1);

					if (array.length > 11 && array[11] != null)
						aftertradeamount = ManipulatePrecision.doubleConvert(Convert.toDouble(array[11].trim()) / 100, 2, 1);

					if (array.length > 12 && array[12] != null)
						tradedate = array[12];

					if (array.length > 13 && array[13] != null)
						reserve = array[13];

					return true;
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
					return false;
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				return false;
			}
		}
	}
}
