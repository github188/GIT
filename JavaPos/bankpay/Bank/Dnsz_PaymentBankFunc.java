package bankpay.Bank;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.Key;
import java.security.Security;
import java.util.Vector;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

//迪诺水镇购物中心储值卡接口  报文传输

public class Dnsz_PaymentBankFunc extends PaymentBankFunc
{
	String path = null;
	String confpath = null;
	String tcppath = null;

	static String mac = "";
	static String TcpHostIp = "";
	static int TcpHostPort = 1;

	static Socket socket = null;
	static DataOutputStream out = null;
	static DataInputStream in = null;

	static byte[] RenData = null; // 请求
	static byte[] recData = null; // 结果
	static String result = null;

	static String charge_code = "06"; // 交易码
	static String terminalNo = "999999999999999"; // 商户终端号
	static String sysSerialNum = "000058"; // 终端流水号
	static String versionNo = "00"; // 系统版本号

	static String track = ""; // 磁道信息50
	static String pici = ""; // 批次号6
	static String passmark = ""; // 密码标志1
	static String ye = ""; // 账户余额13
	static String jestr = ""; // 金额13
	static String password = ""; // 密码8
	static String jy = ""; // 本次结余13
	static String liushui = ""; // 系统流水12
	static String inday = ""; // 起始日期8
	static String outday = ""; // 截止日期8
	static String totalnum = "0000"; // 交易总笔数13
	static String totalje = ""; // 总金额13
	static int datanum = 0; // 已接受的数据包数4
	static String mark = ""; // 后续标志1

	static String datetime = ""; // 交易时间BCD7
	static String cardno = ""; // 交易卡号BCD8
	static String je = ""; // 交易金额BCD6
	static String log = null;

	public String[] getFuncItem()
	{
		String[] func = new String[7];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "撤销";
		func[2] = "[" + PaymentBank.XYKQD + "]" + "签到";
		func[3] = "[" + PaymentBank.XYKTH + "]" + "冲正";
		func[4] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
		func[5] = "[" + PaymentBank.XKQT3 + "]" + "统计查询";
		func[6] = "[" + PaymentBank.XKQT4 + "]" + "明细查询";

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
		case PaymentBank.XYKCX: // 消费撤销
			grpLabelStr[0] = null;
			grpLabelStr[1] = "系统流水号";
			grpLabelStr[2] = null;
			grpLabelStr[3] = "请刷卡";
			grpLabelStr[4] = "交易金额";
			break;
		case PaymentBank.XYKQD: // 签到
			grpLabelStr[0] = null;
			grpLabelStr[1] = "终端流水号";
			grpLabelStr[2] = null;
			grpLabelStr[3] = null;
			grpLabelStr[4] = "交易签到";
			break;
		case PaymentBank.XYKTH:// 冲正
			grpLabelStr[0] = null;
			grpLabelStr[1] = "终端流水号";
			grpLabelStr[2] = null;
			grpLabelStr[3] = "请刷卡";
			grpLabelStr[4] = "交易金额";
			break;
		case PaymentBank.XYKYE: // 账户查询
			grpLabelStr[0] = null;
			grpLabelStr[1] = null;
			grpLabelStr[2] = null;
			grpLabelStr[3] = "请刷卡";
			grpLabelStr[4] = "余额查询";
			break;
		case PaymentBank.XKQT3: // 统计查询
			grpLabelStr[0] = "开始日期";
			grpLabelStr[1] = "截止日期";
			grpLabelStr[2] = null;
			grpLabelStr[3] = null;
			grpLabelStr[4] = "统计查询";
			break;
		case PaymentBank.XKQT4: // 明细查询
			grpLabelStr[0] = "开始日期";
			grpLabelStr[1] = "截止日期";
			grpLabelStr[2] = null;
			grpLabelStr[3] = null;
			grpLabelStr[4] = "明细查询";
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
		case PaymentBank.XYKCX: // 消费撤销
			grpTextStr[0] = null;
			grpTextStr[1] = null;
			grpTextStr[2] = null;
			grpTextStr[3] = null;
			grpTextStr[4] = null;
			break;
		case PaymentBank.XYKQD: // 交易签到
			grpTextStr[0] = null;
			grpTextStr[1] = null;
			grpTextStr[2] = null;
			grpTextStr[3] = null;
			grpTextStr[4] = "按回车键开始签到";
			break;
		case PaymentBank.XYKTH: // 冲正
			grpTextStr[0] = null;
			grpTextStr[1] = null;
			grpTextStr[2] = null;
			grpTextStr[3] = null;
			grpTextStr[4] = null;
			break;
		case PaymentBank.XYKYE: // 账户查询
			grpTextStr[0] = null;
			grpTextStr[1] = null;
			grpTextStr[2] = null;
			grpTextStr[3] = null;
			grpTextStr[4] = "按回车键开始余额查询";
			break;
		case PaymentBank.XKQT3: // 统计查询
			grpTextStr[0] = null;
			grpTextStr[1] = null;
			grpTextStr[2] = null;
			grpTextStr[3] = null;
			grpTextStr[4] = "按回车键开始统计查询";
			break;
		case PaymentBank.XKQT4: // 明细查询
			grpTextStr[0] = null;
			grpTextStr[1] = null;
			grpTextStr[2] = null;
			grpTextStr[3] = null;
			grpTextStr[4] = "按回车键开始明细查询";
			break;
		}

		return true;
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		path = getBankPath(paycode);
		confpath = path + "\\conf.ini";
		tcppath = path + "\\tcp.ini";
		try
		{
			if (!(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XYKQD || type == PaymentBank.XYKTH || type == PaymentBank.XYKYE || type == PaymentBank.XKQT3 || type == PaymentBank.XKQT4))
			{
				new MessageBox("银联接口不支持此交易类型！！！");

				return false;
			}

			// 读取配置文件中的MAC
			if (type != PaymentBank.XYKQD)
			{
				if (!new File(confpath).exists())
				{
					new MessageBox("在进行交易之前请先签到！");
					return false;
				}
				BufferedReader br = CommonMethod.readFile(confpath);
				if (br == null)
				{
					new MessageBox("配置文件" + confpath + "导入错误,马上退出");

					return false;
				}
				String line = br.readLine();
				if ((line == null) || (line.length() <= 0))
				{
					new MessageBox("在进行交易之前请先签到！");
					return false;
				}
				else
				{
					mac = line.trim();
				}
			}

			// 读取配置文件中的TCPip,port
			BufferedReader br1 = CommonMethod.readFile(tcppath);
			if (br1 == null)
			{
				new MessageBox("配置文件" + tcppath + "导入错误,马上退出");

				return false;
			}
			String[] sp;
			String line;
			while ((line = br1.readLine()) != null)
			{
				if ((line == null) || (line.length() <= 0))
				{
					continue;
				}

				String[] lines = line.split("&&");
				sp = lines[0].split("=");
				if (sp.length < 2)
					continue;

				if (sp[0].trim().compareToIgnoreCase("TcpHostIp") == 0)
				{
					TcpHostIp = sp[1].trim();
				}
				if (sp[0].trim().compareToIgnoreCase("TcpHostPort") == 0)
				{
					TcpHostPort = Integer.parseInt(sp[1].trim());
				}
				if (sp[0].trim().compareToIgnoreCase("terminalNo") == 0)
				{
					terminalNo = sp[1].trim();
				}
			}

			// 在进行交易之前请先签到
			if (!(type == PaymentBank.XYKQD))
			{
				if (mac.trim() == null || mac.trim().equals("") || mac.trim().equals("0000000000000000"))
				{
					new MessageBox("在进行交易之前请先签到！");
					return false;
				}
			}

			if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo)) { return false; }

			// 检查交易是否成功
			XYKCheckRetCode(type);

			// 打印签购单
			if (XYKNeedPrintDoc(type))
			{
				XYKPrintDoc(type);
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

	public boolean XYKWriteRequest(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		String type1 = "";

		try
		{
			switch (type)
			{
			case PaymentBank.XYKXF: // 消费
				type1 = "02";
				// 先查余额
				charge_code = "01";
				track = ";" + track2 + "?";
				result = registerFlow(ye());
				bld.retcode = Dnsz_Descrypt.BytesToStr(Dnsz_Descrypt.str2cbcd(result.substring(4, 12)));
				if (!bld.retcode.equals("0000")) { return false; }
				ye = Dnsz_Descrypt.BytesToStr(Dnsz_Descrypt.hexStringToBytes(result.substring(110, 136))).trim(); // 账户余额
				passmark = Dnsz_Descrypt.BytesToStr(Dnsz_Descrypt.hexStringToBytes(result.substring(162, 164))); // 密码标志
				if (Double.parseDouble(ye) < money)
				{
					new MessageBox("您的账户余额为" + ye + "，请充值");
					return false;
				}
				// 再消费
				charge_code = type1;
				track = ";" + track2 + "?";
				jestr = Dnsz_Descrypt.addstring(Double.toString(money), 13); // 金额
				if (passmark.equals("1"))
				{
					if (getpassword(type))
					{
						password = password;
					}
				}
				else
					password = "00000000";
				result = registerFlow(xf());
				bld.retcode = Dnsz_Descrypt.BytesToStr(Dnsz_Descrypt.str2cbcd(result.substring(4, 12)));
				if (!bld.retcode.equals("0000"))
				{
					// registerFlow(readcz()); //冲正
					return false;
				}
				bld.trace = Integer.parseInt(Dnsz_Descrypt.BytesToStr(Dnsz_Descrypt.str2cbcd(result.substring(42, 54)))); // 终端流水6
				datetime = Dnsz_Descrypt.BytesToStr(Dnsz_Descrypt.str2cbcd(result.substring(54, 82))); // 交易时间
				bld.authno = Dnsz_Descrypt.BytesToStr(Dnsz_Descrypt.str2cbcd(result.substring(82, 106))); // 系统流水号12
				bld.je = Double.parseDouble(Dnsz_Descrypt.BytesToStr(Dnsz_Descrypt.hexStringToBytes(result.substring(110, 136))).trim()); // 付款金额
				jy = Dnsz_Descrypt.BytesToStr(Dnsz_Descrypt.hexStringToBytes(result.substring(136, 162))).trim(); // 本次结余
				if (track2.substring(0, 1).equals("7") || track2.substring(0, 1).equals("8"))
				{
					cardno = track2.substring(0, 16);
				}
				else if (track2.substring(0, 1).equals("9"))
				{
					cardno = Dnsz_Descrypt.AlertCardNo(track2).substring(0, 16);
				}
				setLog(log);
				// 生成打印文件
				createPrintFile(type);
				break;
			case PaymentBank.XYKCX: // 撤销
				type1 = "03";
				charge_code = type1;
				liushui = Dnsz_Descrypt.addstring(oldauthno, 13); // 系统流水
				track = ";" + track2 + "?";
				jestr = Dnsz_Descrypt.addstring(Double.toString(money), 13); // 金额

				password = "00000000";

				result = registerFlow(cx());
				bld.retcode = Dnsz_Descrypt.BytesToStr(Dnsz_Descrypt.str2cbcd(result.substring(4, 12)));
				if (!bld.retcode.equals("0000")) { return false; }
				bld.trace = Integer.parseInt(Dnsz_Descrypt.BytesToStr(Dnsz_Descrypt.str2cbcd(result.substring(42, 54)))); // 终端流水6
				datetime = Dnsz_Descrypt.BytesToStr(Dnsz_Descrypt.str2cbcd(result.substring(54, 82))); // 交易时间
				bld.authno = Dnsz_Descrypt.BytesToStr(Dnsz_Descrypt.str2cbcd(result.substring(82, 106))); // 系统流水号12
				bld.je = Double.parseDouble(Dnsz_Descrypt.BytesToStr(Dnsz_Descrypt.hexStringToBytes(result.substring(136, 162))).trim()); // 撤销金额
				jy = Dnsz_Descrypt.BytesToStr(Dnsz_Descrypt.hexStringToBytes(result.substring(110, 136))).trim(); // 本次结余
				if (track2.substring(0, 1).equals("7") || track2.substring(0, 1).equals("8"))
				{
					cardno = track2.substring(0, 16);
				}
				else if (track2.substring(0, 1).equals("9"))
				{
					cardno = Dnsz_Descrypt.AlertCardNo(track2).substring(0, 16);
				}
				setLog(log);
				// 生成打印文件
				createPrintFile(type);
				break;
			case PaymentBank.XYKQD: // 签到
				type1 = "06";
				charge_code = type1;
				sysSerialNum = oldauthno;
				result = registerFlow(register());
				mac = result.substring(186, 202).toUpperCase();
				saveMac();
				bld.retcode = Dnsz_Descrypt.BytesToStr(Dnsz_Descrypt.str2cbcd(result.substring(4, 12)));
				if (!bld.retcode.equals("0000")) { return false; }
				bld.trace = Integer.parseInt(Dnsz_Descrypt.BytesToStr(Dnsz_Descrypt.str2cbcd(result.substring(42, 54)))); // 终端流水6
				setLog(log);
				break;
			case PaymentBank.XYKTH: // 冲正
				type1 = "04";
				charge_code = type1;
				track = ";" + track2 + "?";
				jestr = Dnsz_Descrypt.addstring(Double.toString(money), 13); // 金额
				sysSerialNum = oldauthno;
				// if(track2.substring(0, 1).equals("9")) //密码卡
				// {
				// if(getpassword(type))
				// {
				// password = password;
				// }
				// }
				// else if(track2.substring(0, 1).equals("7") ||
				// track2.substring(0, 1).equals("8")) //无密码卡
				// {
				password = "00000000";
				// }
				result = registerFlow(cz());
				bld.retcode = Dnsz_Descrypt.BytesToStr(Dnsz_Descrypt.str2cbcd(result.substring(4, 12)));
				setLog(log);
				break;
			case PaymentBank.XYKYE: // 余额查询
				type1 = "01";
				charge_code = type1;
				track = ";" + track2 + "?";
				result = registerFlow(ye());
				bld.retcode = Dnsz_Descrypt.BytesToStr(Dnsz_Descrypt.str2cbcd(result.substring(4, 12)));
				ye = Dnsz_Descrypt.BytesToStr(Dnsz_Descrypt.hexStringToBytes(result.substring(110, 136))).trim(); // 账户余额
				passmark = Dnsz_Descrypt.BytesToStr(Dnsz_Descrypt.hexStringToBytes(result.substring(162, 164)));
				; // 密码标志
				if (bld.retcode.equals("0000") || bld.retcode == "0000")
				{
					new MessageBox("您的账户余额为：" + ye + "元");
				}
				setLog(log);
				break;
			case PaymentBank.XKQT3: // 统计查询
				type1 = "07";
				charge_code = type1;
				if (inday.length() <= 4)
				{
					inday = ManipulateDateTime.getCurrentDate().substring(0, 4) + oldseqno; // 开始日期
				}
				if (outday.length() <= 4)
				{
					outday = ManipulateDateTime.getCurrentDate().substring(0, 4) + oldauthno; // 结束日期
				}
				inday = Dnsz_Descrypt.addstring(inday, 8); // 开始日期
				outday = Dnsz_Descrypt.addstring(outday, 8); // 结束日期
				result = registerFlow(tj());
				bld.retcode = Dnsz_Descrypt.BytesToStr(Dnsz_Descrypt.str2cbcd(result.substring(4, 12)));
				if (!bld.retcode.equals("0000")) { return false; }
				bld.trace = Integer.parseInt(Dnsz_Descrypt.BytesToStr(Dnsz_Descrypt.str2cbcd(result.substring(42, 54)))); // 终端流水6
				bld.authno = Dnsz_Descrypt.BytesToStr(Dnsz_Descrypt.str2cbcd(result.substring(82, 106))); // 系统流水号12
				totalnum = Dnsz_Descrypt.BytesToStr(Dnsz_Descrypt.hexStringToBytes(result.substring(110, 136))).trim(); // 总笔数
				totalje = Dnsz_Descrypt.BytesToStr(Dnsz_Descrypt.hexStringToBytes(result.substring(136, 162))).trim(); // 总金额
				setLog(log);
				// 生成打印文件
				createPrint_tj(inday, outday);
				break;
			default:
				type1 = "08"; // 明细查询
				charge_code = type1;
				if (inday.length() <= 4)
				{
					inday = ManipulateDateTime.getCurrentDate().substring(0, 4) + oldseqno; // 开始日期
				}
				if (outday.length() <= 4)
				{
					outday = ManipulateDateTime.getCurrentDate().substring(0, 4) + oldauthno; // 结束日期
				}
				inday = Dnsz_Descrypt.addstring(inday, 8); // 开始日期
				outday = Dnsz_Descrypt.addstring(outday, 8); // 结束日期
				datanum = 0;
				do
				{
					result = registerFlow(mx());
					datanum++;
					bld.retcode = Dnsz_Descrypt.BytesToStr(Dnsz_Descrypt.str2cbcd(result.substring(4, 12)));
					if (!bld.retcode.equals("0000")) { return false; }
					bld.trace = Integer.parseInt(Dnsz_Descrypt.BytesToStr(Dnsz_Descrypt.hexStringToBytes(result.substring(42, 54)))); // 终端流水6
					bld.authno = Dnsz_Descrypt.BytesToStr(Dnsz_Descrypt.str2cbcd(result.substring(82, 106))); // 系统流水号12
					mark = Dnsz_Descrypt.BytesToStr(Dnsz_Descrypt.hexStringToBytes(result.substring(110, 112)));; // 后续标志
					totalnum = Dnsz_Descrypt.BytesToStr(Dnsz_Descrypt.hexStringToBytes(result.substring(112, 120)));; // 交易笔数
					sysSerialNum = Convert.increaseCharForward(Long.toString(bld.trace + 1), '0', 6);

					datetime = result.substring(120, 134) + "," + datetime;
					cardno = result.substring(134, 150) + "," + cardno;
					je = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Double.parseDouble(result.substring(150, 162)), 100), 2, 1) + "," + je;
				}
				while (mark.equals("1"));
				setLog(log);
				// 生成打印文件
				createPrint_mx(inday, outday);
				break;

			}

			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("交易数据异常!\n\n" + ex.getMessage(), null, false);
			ex.printStackTrace();
			return false;
		}
	}

	// 输入卡密码
	public static boolean getpassword(int type)
	{
		StringBuffer bf = new StringBuffer();
		TextBox txt = new TextBox();
		if (!txt.open("请输入储值卡密码", "PASSWORD", "储值卡密码", bf, 0, 0, false, -1)) { return true; }
		password = bf.toString();
		return true;
	}

	// 判断
	public boolean XYKCheckRetCode(int type)
	{
		if (type == PaymentBank.XYKQD)
		{
			if (mac.trim() != null && !mac.trim().equals("") && bld.retcode.equals("0000"))
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
		else
		{
			if (bld.retcode.equals("0000") || bld.retcode == "0000")
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

	}

	public boolean XYKNeedPrintDoc(int type)
	{
		if (!checkBankSucceed()) { return false; }
		if (type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XKQT3 || type == PaymentBank.XKQT4)
		{
			return true;
		}
		else
			return false;
	}

	public boolean checkBankSucceed()
	{
		if (bld.retbz == 'N')
		{
			errmsg = "交易失败";

			return false;
		}
		else
		{
			errmsg = "交易成功";

			return true;
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

	public void XYKPrintDoc(int type)
	{
		ProgressBox pb = null;
		String name = null;
		name = path + "\\toprint.txt";

		try
		{
			if (!PathFile.fileExist(name))
			{
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
					br = CommonMethod.readFileGB2312(name);
					if (br == null)
					{
						new MessageBox("打开打印文件失败");

						return;
					}

					String line = null;
					while ((line = br.readLine()) != null)
					{
						if (line.length() <= 0)
							continue;
						// 银行签购单模板添加 "CUTPAPER" 标记
						// 当程序里面读取到这个字符是，打印机切纸
						if (line.equals("CUTPAPER"))
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
					new MessageBox(e.getMessage());
				}
				finally
				{
					if (br != null)
						try
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
		}
		catch (Exception e)
		{
			e.printStackTrace();
			new MessageBox("打印异常!!!\n" + e.getMessage());
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

	// 总流程
	private static String registerFlow(byte[] input)
	{
		try
		{
			// 开始头
			recData = new byte[104];
			byte[] allpack = new byte[1024];// 总的包
			allpack[0] = 2; // 02打头
			socket = new Socket(TcpHostIp, TcpHostPort);
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			byte[] pack = input; // 签到数据
			// 签到数据包长度
			int n = pack.length;
			byte[] lenarr = Dnsz_Descrypt.str2cbcd(Dnsz_Descrypt.addint(String.valueOf(n), 4));
			// 报文长度
			System.arraycopy(lenarr, 0, allpack, 1, 2);
			// 交易数据
			System.arraycopy(pack, 0, allpack, 3, n);
			// 停止位
			allpack[n + 3] = 3;
			RenData = new byte[n + 5];
			byte t = 0;
			System.arraycopy(allpack, 0, RenData, 0, RenData.length - 1);
			// 异或
			for (int i = 1; i < RenData.length - 2; i++)
			{
				t ^= RenData[i];
			}
			RenData[n + 4] = t;// 异或位
			out.write(RenData);
			out.flush();
			in.read(recData);

		}
		catch (IOException ex)
		{

		}
		finally
		{
			try
			{
				out.close();
			}
			catch (Exception ex)
			{
			}
			try
			{
				in.close();
			}
			catch (Exception ex)
			{
			}
			try
			{
				socket.close();
			}
			catch (Exception ex)
			{
			}
		}
		return Dnsz_Descrypt.bytetoString(recData).toUpperCase();
	}

	// 签到数据组装
	public static byte[] register()
	{
		byte[] pack = new byte[33];
		try
		{
			sysSerialNum = Convert.increaseCharForward(sysSerialNum, '0', 6);
			byte[] publicString = (charge_code + terminalNo + sysSerialNum + versionNo).getBytes(); // 报文公共部分
			byte[] macByte = Dnsz_Descrypt.addstring(mac, 8).getBytes();

			// 交易数据组包
			System.arraycopy(publicString, 0, pack, 0, 25);
			System.arraycopy(macByte, 0, pack, 25, 8);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		log = "签到数据：\r\n" + "交易码" + charge_code + "终端号" + terminalNo + "终端流水" + sysSerialNum + "系统版本号" + versionNo + "MAC" + mac + "\r\n系统时间" + ManipulateDateTime.getDateTimeByClock() + "\r\n";
		return pack;
	}

	// 查余额数据组装
	public static byte[] ye()
	{
		byte[] pack = new byte[89];
		try
		{
			sysSerialNum = Convert.increaseCharForward(sysSerialNum, '0', 6);
			byte[] publicString = (charge_code + terminalNo + sysSerialNum + versionNo).getBytes(); // 报文公共部分
			byte[] macByte = Dnsz_Descrypt.hexStringToBytes(mac);
			Dnsz_Descrypt des = new Dnsz_Descrypt(mac);
			String keydata = Dnsz_Descrypt.BytesToStr(macByte);
			String tempcard;
			tempcard = des.encrypt(track, keydata);
			byte[] cardNo = Dnsz_Descrypt.addstring(tempcard, 50).getBytes();
			// 交易数据组包
			System.arraycopy(publicString, 0, pack, 0, 25); // 公共部分25
			System.arraycopy(cardNo, 0, pack, 25, 50); // 磁道信息50
			pici = Convert.increaseCharForward("", '0', 6); // 批次号 6
			System.arraycopy(pici.getBytes(), 0, pack, 75, 6);
			System.arraycopy(Dnsz_Descrypt.hexStringToBytes(mac), 0, pack, 81, 8); // mac
																				   // 8
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		log = "查余额数据：\r\n" + "交易码" + charge_code + "终端号" + terminalNo + "终端流水" + sysSerialNum + "系统版本号" + versionNo + "磁道信息" + track + "批次号" + pici + "\r\n系统时间" + ManipulateDateTime.getDateTimeByClock() + "\r\n";
		return pack;
	}

	// 消费数据组装
	public static byte[] xf()
	{
		byte[] pack = new byte[110];
		try
		{
			sysSerialNum = Convert.increaseCharForward(sysSerialNum, '0', 6);
			byte[] publicString = (charge_code + terminalNo + sysSerialNum + versionNo).getBytes(); // 报文公共部分
			byte[] macByte = Dnsz_Descrypt.hexStringToBytes(mac);
			Dnsz_Descrypt des = new Dnsz_Descrypt(mac);
			String keydata = Dnsz_Descrypt.BytesToStr(macByte);
			String tempcard;
			tempcard = des.encrypt(track, keydata);
			byte[] cardNo = Dnsz_Descrypt.addstring(tempcard, 50).getBytes();
			// 交易数据组包
			System.arraycopy(publicString, 0, pack, 0, 25);

			System.arraycopy(cardNo, 0, pack, 25, 50);

			pici = Convert.increaseCharForward("", '0', 6); // 批次号 6
			System.arraycopy(pici.getBytes(), 0, pack, 75, 6);

			System.arraycopy(jestr.getBytes(), 0, pack, 81, 13);

			password = Dnsz_Descrypt.addstring(password, 8);// 补齐到8位
			byte[] passByte = des.encrypt(password.getBytes());
			System.arraycopy(passByte, 0, pack, 94, 8);

			System.arraycopy(Dnsz_Descrypt.hexStringToBytes(mac), 0, pack, 102, 8);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		log = "消费数据：\r\n" + "交易码" + charge_code + "终端号" + terminalNo + "终端流水" + sysSerialNum + "系统版本号" + versionNo + "磁道信息" + track + "批次号" + pici + "交易金额" + jestr + "\r\n系统时间" + ManipulateDateTime.getDateTimeByClock() + "\r\n";
		// //保存这笔交易数据
		// byte[] pack1 = null;
		// System.arraycopy(sysSerialNum.getBytes() , 0, pack1, 0, 6);
		// System.arraycopy(pack , 0, pack1, 6, 110);
		// savaData(pack1);

		return pack;
	}

	// 撤销数据组装
	public static byte[] cx()
	{
		byte[] pack = new byte[122];
		try
		{
			sysSerialNum = Convert.increaseCharForward(sysSerialNum, '0', 6);
			byte[] publicString = (charge_code + terminalNo + sysSerialNum + versionNo).getBytes(); // 报文公共部分
			byte[] macByte = Dnsz_Descrypt.hexStringToBytes(mac);
			Dnsz_Descrypt des = new Dnsz_Descrypt(mac);
			String keydata = Dnsz_Descrypt.BytesToStr(macByte);
			String tempcard;
			tempcard = des.encrypt(track, keydata);
			byte[] cardNo = Dnsz_Descrypt.addstring(tempcard, 50).getBytes();
			// 交易数据组包
			System.arraycopy(publicString, 0, pack, 0, 25);

			System.arraycopy(liushui.getBytes(), 0, pack, 25, 12);

			System.arraycopy(cardNo, 0, pack, 37, 50);

			pici = Convert.increaseCharForward("", '0', 6); // 批次号 6
			System.arraycopy(pici.getBytes(), 0, pack, 87, 6);

			System.arraycopy(jestr.getBytes(), 0, pack, 93, 13);

			password = Dnsz_Descrypt.addstring(password, 8);// 补齐到8位
			byte[] passByte = des.encrypt(password.getBytes());
			System.arraycopy(passByte, 0, pack, 106, 8);

			System.arraycopy(Dnsz_Descrypt.hexStringToBytes(mac), 0, pack, 114, 8);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		log = "撤销数据：\r\n" + "交易码" + charge_code + "终端号" + terminalNo + "终端流水" + sysSerialNum + "系统版本号" + versionNo + "系统流水号" + liushui + "磁道信息" + track + "批次号" + pici + "交易金额" + jestr + "\r\n系统时间" + ManipulateDateTime.getDateTimeByClock() + "\r\n";
		return pack;
	}

	// 冲正数据组装
	public static byte[] cz()
	{
		byte[] pack = new byte[116];
		try
		{
			sysSerialNum = Convert.increaseCharForward(sysSerialNum, '0', 6);
			byte[] publicString = (charge_code + terminalNo + sysSerialNum + versionNo).getBytes(); // 报文公共部分
			byte[] macByte = Dnsz_Descrypt.hexStringToBytes(mac);
			Dnsz_Descrypt des = new Dnsz_Descrypt(mac);
			String keydata = Dnsz_Descrypt.BytesToStr(macByte);
			String tempcard;
			tempcard = des.encrypt(track, keydata);
			byte[] cardNo = Dnsz_Descrypt.addstring(tempcard, 50).getBytes();
			// 交易数据组包
			System.arraycopy(publicString, 0, pack, 0, 25);

			System.arraycopy(sysSerialNum.getBytes(), 0, pack, 25, 6);

			System.arraycopy(cardNo, 0, pack, 31, 50);

			pici = Convert.increaseCharForward("", '0', 6); // 批次号 6
			System.arraycopy(pici.getBytes(), 0, pack, 81, 6);

			System.arraycopy(jestr.getBytes(), 0, pack, 87, 13);

			password = Dnsz_Descrypt.addstring(password, 8);// 补齐到8位
			byte[] passByte = des.encrypt(password.getBytes());
			System.arraycopy(passByte, 0, pack, 100, 8);

			System.arraycopy(Dnsz_Descrypt.hexStringToBytes(mac), 0, pack, 108, 8);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		log = "冲正数据：\r\n" + "交易码" + charge_code + "终端号" + terminalNo + "终端流水" + sysSerialNum + "系统版本号" + versionNo + "磁道信息" + track + "批次号" + pici + "交易金额" + jestr + "\r\n系统时间" + ManipulateDateTime.getDateTimeByClock() + "\r\n";
		return pack;
	}

	// 统计数据组装
	public static byte[] tj()
	{
		byte[] pack = new byte[49];
		try
		{
			sysSerialNum = Convert.increaseCharForward(sysSerialNum, '0', 6);
			byte[] publicString = (charge_code + terminalNo + sysSerialNum + versionNo).getBytes(); // 报文公共部分
			byte[] macByte = Dnsz_Descrypt.hexStringToBytes(mac);
			Dnsz_Descrypt des = new Dnsz_Descrypt(mac);
			String keydata = Dnsz_Descrypt.BytesToStr(macByte);
			String tempcard;
			tempcard = des.encrypt(track, keydata);
			// 交易数据组包
			System.arraycopy(publicString, 0, pack, 0, 25);

			System.arraycopy(inday.getBytes(), 0, pack, 25, 8); // 起始日期

			System.arraycopy(outday.getBytes(), 0, pack, 33, 8); // 截止日期

			System.arraycopy(Dnsz_Descrypt.hexStringToBytes(mac), 0, pack, 41, 8);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		log = "统计数据：\r\n" + "交易码" + charge_code + "终端号" + terminalNo + "终端流水" + sysSerialNum + "系统版本号" + versionNo + "开始日期" + inday + "结束日期" + outday + "\r\n系统时间" + ManipulateDateTime.getDateTimeByClock() + "\r\n";
		return pack;
	}

	// 明细查询数据组装
	public static byte[] mx()
	{
		byte[] pack = new byte[53];
		try
		{
			sysSerialNum = Convert.increaseCharForward(sysSerialNum, '0', 6);
			byte[] publicString = (charge_code + terminalNo + sysSerialNum + versionNo).getBytes(); // 报文公共部分
			byte[] macByte = Dnsz_Descrypt.hexStringToBytes(mac);
			Dnsz_Descrypt des = new Dnsz_Descrypt(mac);
			String keydata = Dnsz_Descrypt.BytesToStr(macByte);
			String tempcard;
			tempcard = des.encrypt(track, keydata);
			// 交易数据组包
			System.arraycopy(publicString, 0, pack, 0, 25);

			System.arraycopy(inday.getBytes(), 0, pack, 25, 8); // 起始日期

			System.arraycopy(outday.getBytes(), 0, pack, 33, 8); // 截止日期

			System.arraycopy(Convert.increaseCharForward(String.valueOf(datanum), '0', 4).getBytes(), 0, pack, 41, 4); // 已接受数据包数

			System.arraycopy(Dnsz_Descrypt.hexStringToBytes(mac), 0, pack, 45, 8);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		log = "明细数据：\r\n" + "交易码" + charge_code + "终端号" + terminalNo + "终端流水" + sysSerialNum + "系统版本号" + versionNo + "开始日期" + inday + "结束日期" + outday + "已接收数据包数" + datanum + "\r\n系统时间" + ManipulateDateTime.getDateTimeByClock() + "\r\n";
		return pack;
	}

	// 保存签到生成的MAC
	public boolean saveMac()
	{
		PrintWriter pw = null;
		if (!new File(confpath).exists())
		{
			try
			{
				new File(confpath).createNewFile();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		try
		{
			pw = CommonMethod.writeFile(confpath);
			if (pw != null)
			{
				pw.println(mac);
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

	// ////////////////////////下面的方法生成打印文件///////////////////////////////
	// 生成签购单文件(消费和撤销)
	public void createPrintFile(int type)
	{
		PrintWriter pw = null;
		String line = "";
		line = line + Convert.appendStringSize("", "迪诺水镇储值卡票据", 10, 38, 38) + "\r\n";
		line = line + Convert.appendStringSize("", "-----------------------------------", 0, 38, 38) + "\r\n";
		line = line + Convert.appendStringSize("", "终 端 号:" + terminalNo, 0, 38, 38) + "\r\n";
		line = line + Convert.appendStringSize("", "终端流水:" + bld.trace, 0, 38, 38) + "\r\n";
		line = line + Convert.appendStringSize("", "系统流水:" + bld.authno, 0, 38, 38) + "\r\n";
		line = line + Convert.appendStringSize("", "交易时间:" + datetime, 0, 38, 38) + "\r\n";
		line = line + Convert.appendStringSize("", "卡    号:" + cardno, 0, 38, 38) + "\r\n";

		if (type == 0)
		{
			line = line + Convert.appendStringSize("", "消费金额:" + bld.je, 0, 38, 38) + "\r\n";
		}
		if (type == 1)
		{
			line = line + Convert.appendStringSize("", "撤销金额:" + bld.je, 0, 38, 38) + "\r\n";
		}

		line = line + Convert.appendStringSize("", "账户余额:" + jy, 0, 38, 38) + "\r\n";
		line = line + Convert.appendStringSize("", "打印时间:" + ManipulateDateTime.getDateTimeByClock(), 0, 38, 38) + "\r\n";
		line = line + Convert.appendStringSize("", "-----------------------------------", 0, 38, 38) + "\r\n";
		line = line + Convert.appendStringSize("", "谢谢惠顾！欢迎下次光临！", 8, 38, 38) + "\r\n";

		line = line + "                                 \r\n" + line;

		try
		{
			pw = CommonMethod.writeFile(path + "\\toprint.txt");
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
				datetime = "";
			}
		}
	}

	// 生成统计查询的打印文件
	public void createPrint_tj(String starttime, String endtime)
	{
		PrintWriter pw = null;
		String line = "";
		line = line + Convert.appendStringSize("", "迪诺水镇储值卡统计", 10, 38, 38) + "\r\n";
		line = line + Convert.appendStringSize("", "-----------------------------------", 0, 38, 38) + "\r\n";
		line = line + Convert.appendStringSize("", "终 端 号:" + terminalNo, 0, 38, 38) + "\r\n";
		line = line + Convert.appendStringSize("", "终端流水:" + bld.trace, 0, 38, 38) + "\r\n";
		line = line + Convert.appendStringSize("", "系统流水:" + bld.authno, 0, 38, 38) + "\r\n";
		line = line + Convert.appendStringSize("", "开始时间:" + starttime, 0, 38, 38) + "\r\n";
		line = line + Convert.appendStringSize("", "结束时间:" + endtime, 0, 38, 38) + "\r\n";
		line = line + Convert.appendStringSize("", "总笔数:" + totalnum, 0, 38, 38) + "\r\n";
		line = line + Convert.appendStringSize("", "总金额:" + totalje, 0, 38, 38) + "\r\n";
		line = line + Convert.appendStringSize("", "打印时间:" + ManipulateDateTime.getDateTimeByClock(), 0, 38, 38) + "\r\n";
		line = line + Convert.appendStringSize("", "-----------------------------------", 0, 38, 38) + "\r\n";
		line = line + Convert.appendStringSize("", "谢谢惠顾！欢迎下次光临！", 8, 38, 38) + "\r\n";

		try
		{
			pw = CommonMethod.writeFile(path + "\\toprint.txt");
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
				inday = "";
				outday = "";
			}
		}
	}

	// 生成明细查询的打印文件
	public void createPrint_mx(String starttime, String endtime)
	{
		PrintWriter pw = null;
		String line = "";
		String time[] = null;
		String no[] = null;
		String jyje[] = null;
		time = datetime.split(",");
		no = cardno.split(",");
		jyje = je.split(",");

		line = line + Convert.appendStringSize("", "迪诺水镇储值卡统计", 10, 38, 38) + "\r\n";
		line = line + Convert.appendStringSize("", "-----------------------------------", 0, 38, 38) + "\r\n";
		line = line + Convert.appendStringSize("", "终 端 号:" + terminalNo, 0, 38, 38) + "\r\n";
		line = line + Convert.appendStringSize("", "终端流水:" + bld.trace, 0, 38, 38) + "\r\n";
		line = line + Convert.appendStringSize("", "系统流水:" + bld.authno, 0, 38, 38) + "\r\n";
		line = line + Convert.appendStringSize("", "开始时间:" + starttime, 0, 38, 38) + "\r\n";
		line = line + Convert.appendStringSize("", "结束时间:" + endtime, 0, 38, 38) + "\r\n";
		line = line + Convert.appendStringSize("", "交易笔数:" + totalnum, 0, 38, 38) + "\r\n";

		for (int i = 0; i < time.length; i++)
		{
			line = line + Convert.appendStringSize("", "交易时间:" + time[i], 0, 38, 38) + "\r\n";
			line = line + Convert.appendStringSize("", "交易卡号:" + no[i], 0, 38, 38) + "\r\n";
			line = line + Convert.appendStringSize("", "交易金额:" + jyje[i], 0, 38, 38) + "\r\n";
		}

		line = line + Convert.appendStringSize("", "打印时间:" + ManipulateDateTime.getDateTimeByClock(), 0, 38, 38) + "\r\n";
		line = line + Convert.appendStringSize("", "-----------------------------------", 0, 38, 38) + "\r\n";
		line = line + Convert.appendStringSize("", "谢谢惠顾！欢迎下次光临！", 8, 38, 38) + "\r\n";

		try
		{
			pw = CommonMethod.writeFile(path + "\\toprint.txt");
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
				datanum = 0;
				datetime = "";
				cardno = "";
				je = "";
				inday = "";
				outday = "";
				sysSerialNum = "000058";
			}
		}
	}

	// 记录日志
	public void setLog(String log)
	{
		FileWriter fw = null;
		String day = new ManipulateDateTime().getDateByEmpty();
		File f = new File(path + "\\Poslog\\" + day + ".txt");
		try
		{
			if (!f.exists())
			{
				f.createNewFile();
			}
			fw = new FileWriter(f, true);
			if (fw != null)
			{
				fw.write(log);
				fw.flush();
			}
		}
		catch (Exception ex)
		{
			new MessageBox("记录日志数据异常!");
			ex.printStackTrace();
		}
	}

	// //保存交易数据以便冲正
	// public static void savaData(byte[] data)
	// {
	// FileWriter fw = null;
	// File f = new File(path + "\\Posdata.txt");
	// try
	// {
	// if(!f.exists())
	// {
	// f.createNewFile();
	// }
	// fw = new FileWriter(f ,true);
	// if (fw != null)
	// {
	// fw.write(data.toString());
	// fw.flush();
	// }
	// }
	// catch (Exception ex)
	// {
	// new MessageBox("记录交易数据异常!");
	// ex.printStackTrace();
	// }
	// }
	//
	// public static byte[] readcz()
	// {
	// BufferedReader br = null;
	// String line = null;
	// if(PathFile.fileExist(path + "\\Posdata.txt"))
	// {
	// try{
	// line = br.readLine();
	//
	// }catch(Exception e)
	// {
	// e.printStackTrace();
	// }
	// }
	// return line.getBytes();
	// }

	static class Dnsz_Descrypt
	{

		private static String strDefaultKey = "DES";

		private Cipher encryptCipher = null;

		private Cipher decryptCipher = null;

		/**
		 * 将byte数组转换为表示16进制值的字符串， 如：byte[]{8,18}转换为：0813， 和public static byte[]
		 * hexStr2ByteArr(String strIn) 互为可逆的转换过程
		 * 
		 * @param arrB
		 *            需要转换的byte数组
		 * @return 转换后的字符串
		 * @throws Exception
		 *             本方法不处理任何异常，所有异常全部抛出
		 */
		public static String dencryt(String strEncrypted, String strKey)
		{
			String strDecrypted = "";
			try
			{
				// 先將Base64字串轉碼為byte[]
				Base64 objBase64 = new Base64();
				byte[] bysDecoded = objBase64.decode(strEncrypted.getBytes());

				// 建立解密所需的Key. 因為加密時的key是用ASCII轉換, 所以這邊也用ASCII做
				DESKeySpec objDesKeySpec = new DESKeySpec(StrToBytes(strKey));
				SecretKeyFactory objKeyFactory = SecretKeyFactory.getInstance("DES");
				SecretKey objSecretKey = objKeyFactory.generateSecret(objDesKeySpec);

				// 設定一個DES/ECB/PKCS5Padding的Cipher
				// ECB對應到.Net的CipherMode.ECB
				// 用PKCS5Padding對應到.Net的PaddingMode.PKCS7
				Cipher objCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
				// 設定為解密模式, 並設定解密的key
				objCipher.init(Cipher.DECRYPT_MODE, objSecretKey);

				// 輸出解密後的字串. 因為加密時指定PaddingMode.PKCS7, 所以可以不用處理空字元
				// 不過若想保險點, 也是可以用trim()去處理過一遍
				strDecrypted = new String(objCipher.doFinal(bysDecoded), "utf-8").trim();
				// 輸出:[我是一個PaddingMode.PKCS7的測試字串！]
			}
			catch (Exception e)
			{

				e.printStackTrace(System.out);
			}
			return strDecrypted;
		}

		/**
		 * 转换成16进制
		 * 
		 * @param b
		 * @return
		 */
		public static String toHexString(byte b[])
		{
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < b.length; i++)
			{
				String plainText = Integer.toHexString(0xff & b[i]);
				if (plainText.length() < 2)
					plainText = "0" + plainText;
				hexString.append(plainText);
			}

			return hexString.toString();
		}

		public static String byteArr2HexStr(byte[] arrB) throws Exception
		{

			int iLen = arrB.length;

			// 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍

			StringBuffer sb = new StringBuffer(iLen * 2);

			for (int i = 0; i < iLen; i++)
			{

				int intTmp = arrB[i];

				// 把负数转换为正数

				while (intTmp < 0)
				{

					intTmp = intTmp + 256;

				}

				// 小于0F的数需要在前面补0

				if (intTmp < 16)
				{

					sb.append("0");

				}

				sb.append(Integer.toString(intTmp, 16));

			}

			return sb.toString();

		}

		/**
		 * 默认构造方法，使用默认密钥
		 * 
		 * @throws Exception
		 */

		public Dnsz_Descrypt() throws Exception
		{

			this(strDefaultKey);

		}

		/**
		 * 指定密钥构造方法
		 * 
		 * @param strKey
		 *            指定的密钥
		 * @throws Exception
		 */

		public Dnsz_Descrypt(String strKey) throws Exception
		{
			Security.addProvider(new com.sun.crypto.provider.SunJCE());
			Key key = getKey(hexStr2ByteArr(strKey));
			encryptCipher = Cipher.getInstance("DES");
			encryptCipher.init(Cipher.ENCRYPT_MODE, key);
			decryptCipher = Cipher.getInstance("DES");
			decryptCipher.init(Cipher.DECRYPT_MODE, key);
		}

		public static String encrypt(String encryptString, String encryptKey) throws Exception
		{
			SecretKeySpec key = new SecretKeySpec(StrToBytes(encryptKey), "DES");
			Cipher cipher = Cipher.getInstance("DES");
			try
			{
				cipher.init(Cipher.ENCRYPT_MODE, key);// Cipher.ENCRYPT_MODE（加密标识）
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			byte[] encryptedData = cipher.doFinal(encryptString.getBytes("UTF-8"));// 加密
			return Base64.encodeBase64String(encryptedData);
			// Base64加密生成在Http协议中传输的字符串
		}

		/**
		 * 加密字节数组
		 * 
		 * @param arrB
		 *            需加密的字节数组
		 * @return 加密后的字节数组
		 * @throws Exception
		 */

		public byte[] encrypt(byte[] arrB) throws Exception
		{

			return encryptCipher.doFinal(arrB);

		}

		/**
		 * 加密字符串
		 * 
		 * @param strIn
		 * 
		 *            需加密的字符串
		 * @return 加密后的字符串
		 * @throws Exception
		 */

		public String encrypt(String strIn) throws Exception
		{

			return byteArr2HexStr(encrypt(strIn.getBytes()));

		}

		/**
		 * 解密字节数组
		 * 
		 * @param arrB
		 *            需解密的字节数组
		 * @return 解密后的字节数组
		 * @throws Exception
		 */

		public byte[] decrypt(byte[] arrB) throws Exception
		{

			return decryptCipher.doFinal(arrB);

		}

		/**
		 * 解密字符串
		 * 
		 * @param strIn
		 *            需解密的字符串
		 * @return 解密后的字符串
		 * @throws Exception
		 */

		public String decrypt(String strIn) throws Exception
		{

			return new String(decrypt(hexStr2ByteArr(strIn)));

		}

		/**
		 * * 从指定字符串生成密钥，密钥所需的字节数组长度为8位 不足8位时后面补0，超出8位只取前8位
		 * 
		 * @param arrBTmp
		 *            构成该字符串的字节数组
		 * @return 生成的密钥
		 * @throws java.lang.Exception
		 */

		private Key getKey(byte[] arrBTmp) throws Exception
		{

			// 创建一个空的8位字节数组（默认值为0）

			byte[] arrB = new byte[8];

			// 将原始字节数组转换为8位

			for (int i = 0; i < arrBTmp.length && i < arrB.length; i++)
			{

				arrB[i] = arrBTmp[i];
			}
			// 生成密钥

			Key key = new javax.crypto.spec.SecretKeySpec(arrB, "DES");
			return key;

		}

		/*
		 * 16进制数字字符集
		 */
		private static String hexString = "0123456789ABCDEF";

		/*
		 * 将字符串编码成16进制数字,适用于所有字符（包括中文）
		 */
		public String encode(String str)
		{
			// 根据默认编码获取字节数组
			byte[] bytes = str.getBytes();
			StringBuilder sb = new StringBuilder(bytes.length * 2);
			// 将字节数组中每个字节拆解成2位16进制整数
			for (int i = 0; i < bytes.length; i++)
			{
				sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
				sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
			}
			return sb.toString();
		}

		public static String addint(String cmd, int len)
		{
			if (cmd.length() < len)
			{
				int l = cmd.length();
				for (int i = 0; i < len - l; i++)
				{
					cmd = "0" + cmd;
				}
			}
			return cmd;
		}

		// 字符串转BCD码
		public static byte[] str2cbcd(String s)
		{
			if (s.length() % 2 != 0)
			{
				s = "0" + s;
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			char[] cs = s.toCharArray();
			for (int i = 0; i < cs.length; i += 2)
			{
				int high = cs[i] - 48;
				int low = cs[i + 1] - 48;
				baos.write(high << 4 | low);
			}
			return baos.toByteArray();
		}

		// BCD码转字符串
		public static String bcd2Str(byte[] bytes)
		{
			StringBuffer temp = new StringBuffer(bytes.length * 2);
			for (int i = 0; i < bytes.length; i++)
			{
				temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
				temp.append((byte) (bytes[i] & 0x0f));
			}
			return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp.toString().substring(1) : temp.toString();
		}

		public static String bytetoString(byte[] digest)
		{
			String str = "";
			String tempStr = "";
			for (int i = 1; i < digest.length; i++)
			{
				tempStr = (Integer.toHexString(digest[i] & 0xff));
				if (tempStr.length() == 1)
				{
					str = str + "0" + tempStr;
				}
				else
				{
					str = str + tempStr;
				}
			}
			return str.toLowerCase();
		}

		public static String addstring(String cmd, int len)
		{
			if (cmd.length() < len)
			{
				int l = cmd.length();
				for (int i = 0; i < len - l; i++)
				{
					cmd = cmd + "\0";
				}
			}
			return cmd;
		}

		// 16进制字符串转字节数组
		public static byte[] hexStringToBytes(String hexString)
		{
			if (hexString == null || hexString.equals("")) { return null; }
			// hexString = hexString.toUpperCase(); //如果是大写形式
			int length = hexString.length() / 2;
			char[] hexChars = hexString.toCharArray();
			byte[] d = new byte[length];
			for (int i = 0; i < length; i++)
			{
				int pos = i * 2;
				d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
			}
			return d;
		}

		private static byte charToByte(char c)
		{
			// return (byte) "0123456789abcdef".indexOf(c);
			return (byte) "0123456789ABCDEF".indexOf(c);
		}

		public static String BytesToStr(byte[] target)
		{
			StringBuffer buf = new StringBuffer();
			for (int i = 0, j = target.length; i < j; i++)
			{
				buf.append((char) target[i]);
			}
			return buf.toString();
		}

		public static byte[] StrToBytes(String str)
		{
			byte[] buf = new byte[str.length()];
			for (int i = 0; i < str.length(); i++)
			{
				buf[i] = (byte) str.charAt(i);
			}
			return buf;
		}

		/**
		 * 将表示16进制值的字符串转换为byte数组， 和public static String byteArr2HexStr(byte[]
		 * arrB)
		 * 互为可逆的转换过程
		 * 
		 * @param strIn
		 *            需要转换的字符串
		 * @return 转换后的byte数组
		 * @throws Exception
		 *             本方法不处理任何异常，所有异常全部抛出
		 * @author
		 */

		public static byte[] hexStr2ByteArr(String strIn) throws Exception
		{

			byte[] arrB = strIn.getBytes();

			int iLen = arrB.length;

			// 两个字符表示一个字节，所以字节数组长度是字符串长度除以2

			byte[] arrOut = new byte[iLen / 2];

			for (int i = 0; i < iLen; i = i + 2)
			{

				String strTmp = new String(arrB, i, 2);

				arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);

			}

			return arrOut;

		}

		// 转换9开头的卡号为8开头的卡号
		private static final String CARD_DATA = "2014736958";

		public static String AlertCardNo(String preCardNo)
		{
			if (preCardNo.startsWith("8")) { return preCardNo; }
			String tempstr = "";
			int index = 0;
			if (preCardNo.length() != 20) { return null; }
			for (int i = 0; i < preCardNo.length(); i++)
			{
				index = Integer.parseInt(String.valueOf(preCardNo.toCharArray()[i])) + 1;
				tempstr = tempstr + "2014736958".toCharArray()[(index - 1)];
			}
			return tempstr;
		}

	}
}
