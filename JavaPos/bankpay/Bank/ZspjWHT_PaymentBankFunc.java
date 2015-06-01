package bankpay.Bank;

import java.util.Vector;
import java.io.*;
import javax.comm.SerialPortEvent;
import com.efuture.javaPos.Device.SerialPort.*;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.SalePayDef;

public class ZspjWHT_PaymentBankFunc extends PaymentBankFunc
{
	public static final int ACTION_CHECK = 1; // 自检

	public static final int ACTION_INQUERY = 2; // 查询

	public static final int ACTION_SALE = 3; // 消费

	public static final int ACTION_TIMEOUT = 4; // 超时

	public static final int ACTION_LOCK = 5; // 锁卡

	public static String retDataString = ""; // 串口返回字符串

	protected static String TIMEOUTFILE = "c:\\javapos\\wht.ini";

	// 串口配置参数
	protected String COMPORT = "COM3";

	protected int BAUDRATE = 9600;

	protected int DATABITS = 8;

	protected int STOPBITS = 1;

	protected int PARITY = 0;

	protected long READTIMEOUT = 1500; // 读超时时间

	protected long WRITETIMEOUT = 3000; // 写超时时间

	private String cmdLog = ""; // 存放命令日志信息

	private String logDir = ""; // 日志路径

	private Javax_SerialParameters comPara = null; // 串口参数

	private WhtSerialConnection comConn = null; // 串口连接

	private char[] sendCmd = null; // 存放命令

	private OutputData comRetData = null; // 用于存储分解数据

	private String backupTimeoutCmd = ""; // 备份超时指令

	public String[] getFuncItem()
	{
		String[] func = new String[3];

		func[0] = "[" + PaymentBank.XYKQD + "]" + "设备自检";
		func[1] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[2] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
		// func[3] = "[" + PaymentBank.XKQT1 + "]" + "超时补入";
		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		// 0-4对应FORM中的5个输入框
		// null表示该不用输入
		switch (type)
		{
			case PaymentBank.XYKQD: // 交易签到
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
			case PaymentBank.XYKQD: // 自检
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始设备自检";
				break;
			case PaymentBank.XYKXF: // 消费
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;

				break;

			case PaymentBank.XYKYE: // 交易签到
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始查余";

				break;
		}
		return true;
	}

	public String doTimeoutFile(String cmdStr, boolean flag)
	{
		String cmd = "";
		PrintWriter pw = null;
		BufferedReader br = null;

		try
		{
			// 写超时命令至文件中
			if (cmdStr != null && cmdStr.length() > 0 && flag)
			{
				// 先删除之前的超时文件
				if (PathFile.fileExist(TIMEOUTFILE))
				{
					// debug模式下备份出超时指令
					if (ConfigClass.DebugMode)
						PathFile.copyPath(TIMEOUTFILE,
								"c:\\javapos\\whtback.ini");
					PathFile.deletePath(TIMEOUTFILE);
				}
				// 写指令
				pw = CommonMethod.writeFileAppend(TIMEOUTFILE);

				if (pw != null)
				{
					pw.print(cmdStr);
					pw.flush();
				}
			}

			// 读取超时文件中的超时指令
			if (cmdStr == null && flag)
			{
				br = CommonMethod.readFile(TIMEOUTFILE);
				if (br != null)
					cmd = br.readLine();
			}

			// debug模式不删除指令文件
			if (!ConfigClass.DebugMode)
			{
				// 删除超时指令文件
				if (cmdStr == null && !flag)
				{
					if (PathFile.fileExist(TIMEOUTFILE))
						PathFile.deletePath(TIMEOUTFILE);
				}
			}
			return cmd;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			cmd = "";
			return cmd;
		}
		finally
		{
			if (pw != null)
			{
				pw.close();
				pw = null;
			}
			if (br != null)
			{
				try
				{
					br.close();
					br = null;
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
					br = null;
				}
			}
		}
	}

	private boolean writeTradeLog(String cmdStr)
	{
		PrintWriter pw = null;
		String filename = "";
		try
		{
			if (!PathFile.fileExist(logDir))
			{
				if (!PathFile.createDir(logDir))
					return false;
			}

			filename = logDir + "\\"
					+ GlobalInfo.balanceDate.replaceAll("/", "-") + ".log";
			cmdStr = "[" + ManipulateDateTime.getCurrentDateTime() + "]  "
					+ cmdStr + "\r\n";

			pw = CommonMethod.writeFileAppend(filename); // 以追加方式写日志

			if (pw != null)
			{
				pw.println(cmdStr);
				pw.flush();
			}
			return true;
		}
		catch (Exception ex)
		{
			pw = null;
			return false;
		}
		finally
		{
			if (pw != null)
			{
				pw.close();
			}
		}
	}

	private boolean isSameCard(Vector memo, String cardno)
	{
		boolean flag = false;
		if (memo.size() == 0)
			return false;
		Vector tmpPay = ((SaleBS) memo.elementAt(2)).salePayment;

		for (int i = 0; i < tmpPay.size(); i++)
		{
			SalePayDef pay = (SalePayDef) tmpPay.elementAt(i);
			if (pay.payno.equals(cardno))
			{
				flag = true;
				break;
			}
		}
		return flag;
	}

	public boolean XYKWriteRequest(int type, double money, String track1,
			String track2, String track3, String oldseqno, String oldauthno,
			String olddate, Vector memo)
	{
		String cmdLine = "";

		sendCmd = null; // 清空sendCmd;

		try
		{
			switch (type)
			{
				// 自检
				case PaymentBank.XYKQD:
					sendCmd = getInputCmd(ACTION_CHECK, "");

					writeTradeLog("check:" + cmdLog);

					break;

				// 查余
				case PaymentBank.XYKYE:
					cmdLine = ManipulateDateTime.getCurrentDate().replace("/",
							"")
							+ ManipulateDateTime.getCurrentTime().replace(":",
									"");
					cmdLine = stringToBcd(cmdLine, true);
					sendCmd = getInputCmd(ACTION_INQUERY, cmdLine);

					writeTradeLog("query:" + cmdLine);

					break;

				// 消费
				case PaymentBank.XYKXF:
					// 判断saleBS是否为NULL
					if (memo == null || memo.size() < 3
							|| memo.elementAt(2) == null)
					{
						comRetData.comm_ErrMsg = "消费操作失败";
						return false;
					}

					comRetData.clear(ACTION_SALE);

					if (!inquery())
					{
						return false;
					}
					else
					{
						if (isSameCard(memo, comRetData.query_CardNoSelf))
						{
							// new MessageBox("不允许同一张卡多次付款");
							comRetData.comm_ErrMsg = "不允许同一张卡多次付款";
							return false;
						}
						new MessageBox(
								"卡号: "
										+ comRetData.query_CardNoSelf
										+ "  余额: "
										+ String
												.valueOf((Double
														.parseDouble(comRetData.query_YeOnCard) / 100)));
					}

					// 校验黑名单-锁卡
					if (checkBlacklist(comRetData.comm_LogicCardNo.trim()))
					{
						if (lock())
						{
							if (comRetData.comm_ErrCode.equals("00"))
							{
								// 发送锁卡信息
								if (sendLockedCardInfo(comRetData.comm_TermNo,
										comRetData.sale_TermFlag,
										comRetData.sale_CurrentTradeTime,
										comRetData.comm_LogicCardNo,
										comRetData.comm_PhysicCardNo,
										comRetData.comm_PrimaryCardType,
										comRetData.comm_SubCardType,
										comRetData.sale_Memo))
								{
									new MessageBox("该卡在黑名单中 - 锁卡成功");
								}
								else
								{
									new MessageBox("锁卡成功但上传锁卡信息失败");
								}
							}
							else
							{
								new MessageBox("该卡在黑名单中 - 锁卡失败");
							}

							comRetData.comm_ErrMsg = "该卡在黑名单内,不允许消费";
							return false;
						}
					}

					cmdLine = ManipulateDateTime.getCurrentDate().replace("/",
							"")
							+ ManipulateDateTime.getCurrentTime().replace(":",
									"");
					String strJe = Convert.increaseCharForward(String
							.valueOf((long) ManipulatePrecision.doubleConvert(
									money * 100, 2, 1)), '0', 8);
					// 填充命令
					cmdLine = stringToBcd(cmdLine, false);
					cmdLine += stringToBcd(strJe, false);
					cmdLine += comRetData.comm_ChipType + ","
							+ comRetData.comm_BackLogicCardNo
							+ comRetData.comm_BackPhysicCardNo + "00,";

					sendCmd = getInputCmd(ACTION_SALE, cmdLine);

					writeTradeLog("sale:" + cmdLog);

					break;

				// 超时
				case PaymentBank.XKQT1:
					String[] timeoutCmd = null;

					backupTimeoutCmd = doTimeoutFile(null, true);
					timeoutCmd = backupTimeoutCmd.split(";");

					if (timeoutCmd == null || timeoutCmd.length != 3
							|| timeoutCmd[0].equals(""))
					{
						new MessageBox("超时指令有误 \n系统无法获取交易数据 \n请确认是否发生交易");
						comRetData.comm_ErrMsg = "超时补入失败";

						// 超时文件非法,直接删除
						doTimeoutFile(null, false);
						return false;
					}

					backupTimeoutCmd = timeoutCmd[0];
					comRetData.query_CardNoSelf = timeoutCmd[1];
					comRetData.query_LogicCardNo = timeoutCmd[2];

					sendCmd = getInputCmd(ACTION_TIMEOUT, backupTimeoutCmd);

					writeTradeLog("overtime: " + cmdLog);

					break;

				default:
					break;
			}
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean XYKExecute(int type, double money, String track1,
			String track2, String track3, String oldseqno, String oldauthno,
			String olddate, Vector memo)
	{
		boolean ret = false;

		try
		{
			// 初始化串口
			if (!initSerialPort())
			{
				errmsg = "串口初始化失败";
				return false;
			}

			// 初始化返回数据接收对象
			comRetData = new OutputData();

			// 检测是否存在超时文件
			if (type == PaymentBank.XYKXF)
				type = selectTimeoutTrade();

			// 组合串口命令
			if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno,
					oldauthno, olddate, memo))
			{
				errmsg = comRetData.comm_ErrMsg;
				return false;
			}

			// 检测命令是否为空
			if (sendCmd == null)
			{
				errmsg = "无效指令";
				return false;
			}

			// 打开串口
			if (!openComPort())
			{
				errmsg = "串口打开失败";
				return false;
			}

			// 发送串口命令
			for (int i = 0; i < sendCmd.length; i++)
				comConn.sendChar(sendCmd[i]);

			// 解析串口返回数据
			switch (type)
			{
				case PaymentBank.XYKQD: // 自检会耗时约5秒
					Thread.sleep(WRITETIMEOUT); // 等待串口返回，时间若短，则有可能返回空串

					comRetData.clear(ACTION_CHECK);
					ret = analysisRetData(ACTION_CHECK,
							ZspjWHT_PaymentBankFunc.retDataString);

					writeTradeLog("checkRet:" + cmdLog);

					break;

				case PaymentBank.XYKYE:
					Thread.sleep(READTIMEOUT); // 等待串口返回，时间若短，则有可能返回空串

					comRetData.clear(ACTION_INQUERY);
					ret = analysisRetData(ACTION_INQUERY,
							ZspjWHT_PaymentBankFunc.retDataString);// comRetData);

					writeTradeLog("inqueryRet:" + cmdLog);

					break;

				case PaymentBank.XYKXF:
					Thread.sleep(READTIMEOUT);// 等待串口返回，时间若短，则有可能返回空串

					comRetData.clear(ACTION_SALE);
					ret = analysisRetData(ACTION_SALE,
							ZspjWHT_PaymentBankFunc.retDataString);

					// 仅对扣款进行超时处理
					if (!ret)
					{
						// 99 - 表示未读取到数据，但款已扣，采取超时自动获取
						if (comRetData.comm_CurTradeType == ACTION_SALE
								&& comRetData.comm_ErrCode.equals("99"))
						{
							// 连续发送3次超时
							for (int i = 0; i < 3; i++)
							{
								// 如果数据返回则退出
								if (ret = doTimeout())
									break;
							}
							if (!ret)
								errmsg = "数据接收超时,请检查设备后进行超时补入";
						}
					}

					if (comRetData.comm_ErrCode.equals("55"))
						doTimeoutFile(null, false);

					writeTradeLog("saleRet:" + cmdLog);

					break;
				case PaymentBank.XKQT1:
					Thread.sleep(READTIMEOUT);// 等待串口返回，时间若短，则有可能返回空串

					comRetData.clear(ACTION_TIMEOUT);
					ret = analysisRetData(ACTION_TIMEOUT,
							ZspjWHT_PaymentBankFunc.retDataString);

					writeTradeLog("overtimeRet:" + cmdLog);

					break;
			}

			if (!ret)
			{
				errmsg = comRetData.comm_ErrMsg;
				return false;
			}

			// 读取返回的结果
			if (!XYKReadResult()) { return false; }

			// 检查交易是否成功
			XYKCheckRetCode();

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
			return false;
		}
		finally
		{
			comRetData = null;
			closeComPort(); // 关闭串口
		}
	}

	protected boolean XYKReadResult()
	{
		try
		{
			if (comRetData == null)
				return false;

			bld.retcode = comRetData.comm_ErrCode;
			bld.retmsg = comRetData.comm_ErrMsg;

			errmsg = comRetData.comm_ErrMsg;

			// 自检-不做任何处理

			// 查余
			if (bld.type.equals(String.valueOf(PaymentBank.XYKYE)))
			{
				if (bld.retcode.equals("00"))
				{
					bld.cardno = comRetData.query_CardNoSelf; // 卡号
					String je = String.valueOf(Double
							.parseDouble(comRetData.query_YeOnCard) / 100); // 金额

					new MessageBox("卡号:" + bld.cardno + "  余额:" + je);

				}
			}
			// 扣款 +超时
			if (bld.type.equals(String.valueOf(PaymentBank.XYKXF))
					|| bld.type.equals(String.valueOf(PaymentBank.XKQT1)))
			{
				if (bld.retcode.equals("00"))
				{
					// 对超时返回的数据进行金额,卡号校验
					if (comRetData.comm_CurTradeType == ACTION_TIMEOUT)
					{
						if (bld.je != ManipulatePrecision.doubleConvert(Double
								.parseDouble(comRetData.sale_JeThisTime) / 100,
								2, 1)
								|| !comRetData.query_LogicCardNo
										.equals(comRetData.comm_LogicCardNo))
						{
							bld.retcode = "99";
							bld.retmsg = "交易金额或卡号不匹配,";
							errmsg = bld.retmsg;
							return false;
						}
						// 如果是超时补入操作，则记录
						bld.crc = "超时补入";
					}

					// 交易成功后立即清除备份的指令
					backupTimeoutCmd = "";
					doTimeoutFile(null, false);

					bld.cardno = comRetData.query_CardNoSelf; // 卡号
					bld.je = ManipulatePrecision.doubleConvert(Double
							.parseDouble(comRetData.sale_JeThisTime) / 100, 2,
							1);
					bld.trace = (long) Double
							.parseDouble(comRetData.sale_TradeSerialId); // 流水号

					String je = String.valueOf(Double
							.parseDouble(comRetData.sale_JeThisTime) / 100);
					String ye = String.valueOf(Double
							.parseDouble(comRetData.sale_JeNowTime) / 100);

					new MessageBox(comRetData.comm_ErrMsg + "\n  金额:" + je
							+ "  余额:" + ye);
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

	public boolean XYKCheckRetCode()
	{
		// 根据返回值置返回标志
		if (bld.retcode.equals("00"))
		{
			bld.retbz = 'Y';
			bld.retmsg = "武汉通交易成功";

			return true;
		}
		else
		{
			bld.retbz = 'N';
			return false;
		}
	}

	public boolean XYKNeedPrintDoc()
	{
		if (bld.type.equals(String.valueOf(PaymentBank.XYKXF))
				|| bld.type.equals(String.valueOf(PaymentBank.XKQT1)))
		{
			if (!comRetData.comm_ErrCode.equals("00"))
				return false;

			StringBuffer sb = new StringBuffer();
			PrintWriter pw = null;
			String saleJe = "", ye = "";
			try
			{
				saleJe = String.valueOf(Double
						.parseDouble(comRetData.sale_JeThisTime) / 100);
				ye = String.valueOf(Double
						.parseDouble(comRetData.sale_JeNowTime) / 100);

				sb.append("          武汉通缴款凭证              \r\n");
				sb.append("---------------------------------\r\n");
				sb.append("收银机号         收银员号         发票号\r\n");
				sb.append(bld.syjh + "           " + bld.syyh + "           "
						+ String.valueOf(bld.fphm) + "\r\n");
				sb.append("---------------------------------\r\n");
				sb.append("交易时间:");
				sb.append(comRetData.sale_CurrentTradeTime + "\r\n");
				sb.append("交易卡号: ");
				sb.append(comRetData.query_CardNoSelf + "\r\n");
				sb.append("交易金额: ");
				sb.append(saleJe + "\r\n");
				sb.append("当前余额: ");
				sb.append(ye + "\r\n");
				sb.append("终端号: ");
				sb.append(replaceZero(comRetData.comm_TermNo).trim() + "\r\n");
				sb.append("联机计数: ");
				sb.append(replaceZero(comRetData.comm_TimesOnline).trim()
						+ "\r\n");
				sb.append("脱机计数: ");
				sb.append(replaceZero(comRetData.comm_TimesOffline).trim()
						+ "\r\n");
				sb.append("打印时间: ");
				sb.append(ManipulateDateTime.getCurrentDateTime() + "\r\n");

				sb.toString();

				if (PathFile.fileExist("c:\\javapos\\whtprn.txt"))
					PathFile.deletePath("c:\\javapos\\whtprn.txt");

				pw = CommonMethod.writeFileAppendGBK("c:\\javapos\\whtprn.txt");

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

	public void XYKPrintDoc()
	{
		ProgressBox pb = null;
		BufferedReader br = null;

		try
		{
			String printName = "";

			if ((bld.type.equals(String.valueOf(PaymentBank.XYKXF)) || bld.type
					.equals(String.valueOf(PaymentBank.XKQT1))))
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
				br = CommonMethod.readFileGBK(printName);

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

	private boolean loadConfigFile()
	{
		try
		{
			String line = GlobalVar.ConfigPath + "//WhtConfig.ini";
			Vector v = readGBKFileByVector(line);

			for (int i = 0; i < v.size(); i++)
			{
				String[] row = (String[]) v.elementAt(i);
				if ("ComPort".equalsIgnoreCase(row[0]))
				{
					COMPORT = row[1].trim();
				}
				else if ("BaudRate".equalsIgnoreCase(row[0]))
				{
					BAUDRATE = Integer.parseInt(row[1]);
				}
				else if ("Databits".equalsIgnoreCase(row[0]))
				{
					DATABITS = Integer.parseInt(row[1]);
				}
				else if ("Stopbits".equalsIgnoreCase(row[0]))
				{
					STOPBITS = Integer.parseInt(row[1]);
				}
				else if ("Parity".equalsIgnoreCase(row[0]))
				{
					PARITY = Integer.parseInt(row[1]);
				}
				else if ("ReadTimeout".equalsIgnoreCase(row[0]))
				{
					READTIMEOUT = Long.parseLong(row[1]);
				}
				else if ("WriteTimeout".equalsIgnoreCase(row[0]))
				{
					WRITETIMEOUT = Long.parseLong(row[1]);
				}
				else
				{
					continue;
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

	private boolean initSerialPort()
	{
		comPara = new Javax_SerialParameters();
		try
		{
			// 日志路径 \javaPos.Database\Invoice\datetime
			logDir = ConfigClass.LocalDBPath + "Invoice\\"
					+ GlobalInfo.balanceDate.replaceAll("/", "") + "\\whtLog";

			loadConfigFile();

			comPara.setPortName(COMPORT);
			comPara.setBaudRate(BAUDRATE);
			comPara.setDatabits(DATABITS);
			comPara.setStopbits(STOPBITS);
			comPara.setParity(PARITY);

			comConn = new WhtSerialConnection(comPara);

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	private boolean openComPort()
	{
		try
		{
			if (comConn.isOpen())
			{
				return true;
			}
			else
			{
				comConn.openConnection();
				return true;
			}
		}
		catch (Exception ex)
		{
			comConn = null;
			return false;
		}
	}

	private boolean closeComPort()
	{
		try
		{
			if (comConn.isOpen())
			{
				comConn.closeConnection();
			}
			return true;
		}
		catch (Exception ex)
		{
			comConn = null;
			return false;
		}
	}

	private boolean inquery()
	{
		char[] sendCmd = null;
		String dt = "", bcd = "";
		try
		{
			dt = ManipulateDateTime.getCurrentDate().replace("/", "")
					+ ManipulateDateTime.getCurrentTime().replace(":", "");
			bcd = stringToBcd(dt, true);

			sendCmd = getInputCmd(ACTION_INQUERY, bcd);

			writeTradeLog("Sale[inquery]:" + cmdLog);

			// 开串口
			if (!openComPort())
			{
				errmsg = "串口打开失败";
				return false;
			}

			if (sendCmd == null)
			{
				comRetData.comm_ErrMsg = "上位机发送命令为空";
				return false;
			}

			// 发送命令
			for (int i = 0; i < sendCmd.length; i++)
				comConn.sendChar(sendCmd[i]);

			// 查询速度稍快
			Thread.sleep(READTIMEOUT);
			// 接收命令
			if (!analysisRetData(ACTION_INQUERY,
					ZspjWHT_PaymentBankFunc.retDataString))
				return false;

			if (!comRetData.comm_ErrCode.equals("00"))
				return false;

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			writeTradeLog("Sale[inquery]Ret:" + cmdLog);
			closeComPort();
		}
	}

	private boolean doTimeout()
	{
		char[] sendCmd = null;
		try
		{
			if (backupTimeoutCmd.equals(""))
				return false;

			// 对备份的超时指令进行拆解
			String[] timeoutCmd = backupTimeoutCmd.split(";");

			if (!(timeoutCmd.length > 1))
				return false;

			backupTimeoutCmd = timeoutCmd[0].trim();

			// 此处发送TIMEOUT指令
			sendCmd = getInputCmd(ACTION_TIMEOUT, backupTimeoutCmd);

			if (sendCmd == null)
				return false;

			// 打开串口
			if (!openComPort())
				return false;

			for (int i = 0; i < sendCmd.length; i++)
				comConn.sendChar(sendCmd[i]);

			// 等待串口返回，时间若短，则有可能返回是空串
			Thread.sleep(READTIMEOUT);

			// 初始化采用ACTION_SALE
			comRetData.clear(ACTION_SALE);
			if (!analysisRetData(ACTION_SALE,
					ZspjWHT_PaymentBankFunc.retDataString))
				return false;

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			closeComPort();
		}
	}

	private boolean sendLockedCardInfo(String termno, String flag, String time,
			String logic, String physic, String prytype, String subtype,
			String memo)
	{
		try
		{
			if (comRetData.comm_CurTradeType == ACTION_LOCK)
			{
				if (!DataService.getDefault().doWhtBlackList("1", termno, flag,
						time, logic, physic, prytype, subtype, memo)) { return false; }
			}
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	/*
	 * public boolean checkTimeout(int tradetype, String start) { long interval =
	 * 0; try { String[] starttimes = start.split(":"); String[] endtimes = new
	 * ManipulateDateTime().getTime().split(":");
	 * 
	 * GregorianCalendar gc1 = new GregorianCalendar(0, 0, 0,
	 * Integer.parseInt(starttimes[0]), Integer.parseInt(starttimes[1]),
	 * Integer.parseInt(starttimes[2]));
	 * 
	 * while (interval < READTIMEOUT) { GregorianCalendar gc2 = new
	 * GregorianCalendar(0, 0, 0, Integer.parseInt(endtimes[0]),
	 * Integer.parseInt(endtimes[1]), Integer.parseInt(endtimes[2])); interval =
	 * gc2.getTimeInMillis() - gc1.getTimeInMillis();
	 * 
	 * switch (tradetype) { case ACTION_SALE: case ACTION_TIMEOUT: if
	 * (ZspjWHT_PaymentBankFunc.retDataString.length() > 99) { break; } default:
	 * break; } return false; }
	 * 
	 * return true; } catch (Exception ex) { ex.printStackTrace(); return false; } }
	 */

	private boolean lock()
	{
		char[] sendCmd = null;
		String cmdLine = "";
		try
		{
			cmdLine = ManipulateDateTime.getCurrentDate().replace("/", "")
					+ ManipulateDateTime.getCurrentTime().replace(":", "");

			// 填充命令
			cmdLine = stringToBcd(cmdLine, false);
			cmdLine += comRetData.comm_ChipType + ","
					+ comRetData.comm_BackLogicCardNo
					+ comRetData.comm_BackPhysicCardNo + "00,";

			sendCmd = getInputCmd(ACTION_LOCK, cmdLine);

			writeTradeLog("Sale[lock]:" + cmdLog);

			if (sendCmd == null)
				return false;

			if (!openComPort())
			{
				errmsg = "打开串口失败";
				return false;
			}

			for (int i = 0; i < sendCmd.length; i++)
				comConn.sendChar(sendCmd[i]);

			// 等待串口返回
			Thread.sleep(READTIMEOUT);

			comRetData.clear(ACTION_LOCK);
			if (!analysisRetData(ACTION_LOCK,
					ZspjWHT_PaymentBankFunc.retDataString))
				return false;

			if (!comRetData.comm_ErrCode.equals("00"))
				return false;

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			writeTradeLog("Sale[lock]Ret:" + cmdLog);
			closeComPort();
		}
	}

	private int selectTimeoutTrade()
	{
		int seltype = PaymentBank.XYKXF;
		int ret = 0;
		if (PathFile.fileExist(TIMEOUTFILE))
		{

			ret = new MessageBox(
					"系统检测到武汉通上笔交易超时,是否现场补入?\n\n 任意键-超时补入 / 付款键-正常消费 ", null,
					false).verify();
			if (ret != GlobalVar.Pay)
			{
				seltype = PaymentBank.XKQT1;
			}
		}
		return seltype;
	}

	private boolean checkBlacklist(String cardno)
	{
		try
		{
			if (!DataService.getDefault().doWhtBlackList("0", cardno, "", "",
					"", "", "", "", "")) { return false; }
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public char[] getInputCmd(int cmdType, String cmd)
	{
		char[] sendCmd = null;
		cmdLog = "";

		try
		{
			// 因为超时指令是之前的扣款指令，已经对AA,BB做了处理，所以不能再对其做处理
			if (cmdType != ACTION_TIMEOUT)
			{
				cmd = cmd.replace("AA", "AA,00");
				cmd = cmd.replace("BB", "BB,00");
			}

			switch (cmdType)
			{
				case ACTION_CHECK:
					sendCmd = new char[6];
					sendCmd[0] = 0xAA;
					sendCmd[1] = 0x03;
					sendCmd[2] = 0xAF;
					sendCmd[3] = 0xAD;
					sendCmd[4] = 0x01;
					sendCmd[5] = 0xCC;
					cmdLog = "AA,03,AF,AD,01,CC";

					break;
				case ACTION_INQUERY:
					cmd = "AA,09,AB," + cmd + "CC";
					cmdLog = "Before: " + cmd + "\r\n";

					sendCmd = convertCmd(cmd);
					cmdLog += "After: " + convertHexstrLog(sendCmd);

					break;
				case ACTION_SALE:
					cmd = "AA,1A,AC," + cmd + "CC";
					cmdLog = "Before: " + cmd + "\r\n";

					backupTimeoutCmd = cmd;

					// 备份成超时指令
					backupTimeoutCmd = backupTimeoutCmd.replace("AC", "AD")
							+ ";" + comRetData.query_CardNoSelf + ";"
							+ comRetData.query_LogicCardNo;
					doTimeoutFile(backupTimeoutCmd, true);// 将超时指令（未算校验码）写入本地

					sendCmd = convertCmd(cmd); // 计算校验码
					cmdLog += "After: " + convertHexstrLog(sendCmd); // 记录最终的命令

					break;
				case ACTION_TIMEOUT:
					cmdLog = "Before: " + cmd + "\r\n";

					sendCmd = convertCmd(cmd);
					cmdLog += "After: " + convertHexstrLog(sendCmd);

					break;
				case ACTION_LOCK:
					cmd = "AA,16,D8," + cmd + "CC";
					cmdLog = "Before: " + cmd + "\r\n";

					sendCmd = convertCmd(cmd);
					cmdLog += "After: " + convertHexstrLog(sendCmd);

					break;

				default:
					break;
			}
			return sendCmd;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	private char[] convertCmd(String srcCmd)
	{
		byte[] byteCmd = null;
		char[] charCmd = null;

		byteCmd = packageCmd(srcCmd);
		charCmd = new char[byteCmd.length];

		for (int i = 0; i < byteCmd.length; i++)
			charCmd[i] = (char) byteCmd[i];

		return charCmd;
	}

	private String convertHexstrLog(char[] srcCmd)
	{
		String strHexString = "";
		for (int i = 0; i < srcCmd.length; i++)
		{
			String tmpstr = Integer.toHexString(srcCmd[i]).toUpperCase();

			if (tmpstr.indexOf("F") != -1)
				strHexString += tmpstr.replace('F', ' ').trim() + ",";
			else if (tmpstr.length() == 1)
				strHexString += "0" + tmpstr + ",";
			else
				strHexString += tmpstr + ",";
		}

		return strHexString;
	}

	private String stringToBcd(String code, boolean isAdd)
	{
		String dtBCD = "";
		String tmpStr = "";
		int first = 0, mid = 0, second = 0;
		boolean isEnd = false;

		try
		{
			char[] dtChr = code.toCharArray();

			for (int i = 0; i < dtChr.length; i++)
			{
				if (i == dtChr.length - 1)
					isEnd = true;

				mid = Integer.parseInt(String.valueOf(dtChr[i]));

				if ((i % 2) == 0)
				{
					first = (mid & 0x0F) << 4;
				}
				else
				{
					if (isEnd)
					{
						second = mid & 0x0F;

						tmpStr = Integer.toHexString(first | second);

						if (tmpStr.length() == 1)
							tmpStr = "0" + tmpStr;
						if (isAdd)
							dtBCD = dtBCD + tmpStr + ",00,"; // 多加一个00是为了算校验码
						else
							dtBCD = dtBCD + tmpStr + ",";
					}
					else
					{
						second = mid & 0x0F;

						tmpStr = Integer.toHexString(first | second);
						if (tmpStr.length() == 1)
							tmpStr = "0" + tmpStr;

						dtBCD += tmpStr + ",";
					}
				}
			}
			return dtBCD;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return "";
		}
	}

	private byte[] packageCmd(String cmd)
	{
		String[] splitCmd = null;
		byte[] allCmd = null;
		byte crc = 0x00;
		try
		{
			splitCmd = cmd.split(",");
			allCmd = new byte[splitCmd.length];

			for (int i = 0; i < splitCmd.length; i++)
			{
				splitCmd[i] = "0x" + splitCmd[i];
				allCmd[i] = Integer.decode(splitCmd[i]).byteValue();
			}

			for (int i = 1; i < allCmd.length - 2; i++)
				crc = (byte) ((byte) (crc) ^ allCmd[i]);

			allCmd[allCmd.length - 2] = crc;

			return allCmd;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	private boolean analysisRetData(int tradeType, String dataStr)
	{
		String retData[] = null;
		String retDataLine = "";
		String retDataString = "";
		int specialChars = 0;

		try
		{
			// 字符为空的情况
			if (dataStr == null || dataStr.equals(""))
			{
				comRetData.comm_ErrCode = "99";
				comRetData.comm_ErrMsg = "设备返回数据为空-设备可能超时";
				return false;
			}

			// 记录串口返回的数据串
			// 转换成大写避免后期数据解析后的转换（因为武汉通对解析出的校验码只认大写)
			dataStr = dataStr.toUpperCase();
			cmdLog = dataStr;

			if (dataStr.indexOf("AA,00") != -1)
			{
				dataStr = dataStr.replace("AA,00", "AA");
				specialChars++;
			}

			if (dataStr.indexOf("BB,00") != -1)
			{
				dataStr = dataStr.replace("BB,00", "BB");
				specialChars++;
			}

			retData = dataStr.split(",");
			int len = Integer.decode("0x" + retData[1]).byteValue() + 3;
			int passLen = Integer.parseInt(retData[retData.length - 1])
					- specialChars;

			// 读取到的长度不一致时的情况
			if (len != passLen || retData.length < len)
			{
				comRetData.comm_ErrCode = "99";
				comRetData.comm_ErrMsg = "设备返回数据有误--设备可能超时";
				return false;
			}

			// 串口正常返回数据, 只是返回的数据为设备错误码
			if ((retData.length <= 7) && retData[1].equalsIgnoreCase("03"))
			{
				comRetData.comm_ErrCode = "55";// retData[3];
				comRetData.comm_ErrMsg = retData[3] + ":"
						+ getErrMsg(retData[3].trim());
				return true;
			}

			// 根据comm_RepStatus 来判断数据是否正常返回 (00-表正常返回)
			switch (tradeType)
			{
				case ACTION_CHECK:
					comRetData.comm_RepStatus = retData[3];

					for (int i = 4; i < 7; i++)
					{
						comRetData.check_DeviceVersion += retData[i];
					}
					for (int i = 7; i < 15; i++)
					{
						comRetData.comm_TermNo += retData[i];
					}

					if (comRetData.comm_RepStatus.equals("00"))
					{
						comRetData.comm_ErrCode = comRetData.comm_RepStatus;
						comRetData.comm_ErrMsg = "自检成功";
					}

					break;

				case ACTION_INQUERY:
					comRetData.comm_RepStatus = retData[3]; // 响应状态

					comRetData.comm_ChipType = retData[4]; // 获取卡芯片类型

					for (int i = 5; i < 13; i++)
					{
						comRetData.comm_LogicCardNo += retData[i];
						comRetData.query_LogicCardNo += retData[i];
						comRetData.comm_BackLogicCardNo += retData[i] + ",";
					}
					for (int i = 13; i < 17; i++)
					{
						comRetData.comm_PhysicCardNo += retData[i];
						comRetData.comm_BackPhysicCardNo += retData[i] + ",";
					}
					for (int i = 17; i < 21; i++)
					{
						comRetData.query_YeOnCard += retData[i];
					}

					if (comRetData.comm_ChipType.equalsIgnoreCase("01"))
					{
						for (int i = 23; i < 26; i++)
							comRetData.query_timesOperation += retData[i];
					}
					if (comRetData.comm_ChipType.equalsIgnoreCase("02"))
					{
						for (int i = 23; i < 26; i++)
							comRetData.comm_TimesOnline += retData[i];
						for (int i = 26; i < 29; i++)
							comRetData.comm_TimesOffline += retData[i];
						for (int i = 29; i < 34; i++)
							comRetData.query_CardNoSelf += retData[i];
					}
					if (comRetData.comm_RepStatus.equals("00"))
					{
						cmdLog += "\r\n CARDNO:" + comRetData.query_CardNoSelf
								+ "   YE:" + comRetData.query_YeOnCard;
						comRetData.comm_ErrCode = comRetData.comm_RepStatus;
						comRetData.comm_ErrMsg = "查询成功";
					}

					break;

				case ACTION_LOCK:
					// 有半字节出现，采用字符截取
					retDataLine = dataStr.replace(",", "");

					comRetData.comm_RepStatus = retDataLine.substring(6, 8); // 响应状态

					comRetData.comm_ChipType = retDataLine.substring(8, 10); // 芯片类型

					// 此解析仅针对芯片类型为CPU（即02)
					if (comRetData.comm_ChipType.equals("02"))
					{
						comRetData.comm_TermNo = replaceZero(retDataLine
								.substring(10, 26));
						comRetData.sale_TermFlag = retDataLine
								.substring(26, 27);
						comRetData.sale_CurrentTradeTime = retDataLine
								.substring(27, 41);
						comRetData.comm_LogicCardNo = retDataLine.substring(41,
								57);
						comRetData.comm_PhysicCardNo = retDataLine.substring(
								57, 65);
						comRetData.comm_PrimaryCardType = retDataLine
								.substring(65, 67);
						comRetData.comm_SubCardType = retDataLine.substring(67,
								69);
						comRetData.sale_Memo = retDataLine.substring(69, 85);

						if (comRetData.comm_RepStatus.equalsIgnoreCase("00"))
						{
							comRetData.comm_ErrCode = comRetData.comm_RepStatus;
							comRetData.comm_ErrMsg = "锁卡成功";
						}
					}
					break;

				// 消费和超时返回数据相同
				case ACTION_TIMEOUT:
				case ACTION_SALE:
					comRetData.comm_RepStatus = retData[3]; // 响应状态
					comRetData.comm_ChipType = retData[4]; // 芯片类型

					// 针对测试卡的解析
					if (comRetData.comm_ChipType.equalsIgnoreCase("01"))
					{
						for (int i = 5; i < 9; i++)
						{
							comRetData.sale_TradeDeviceCode += retData[i];
						}

						comRetData.sale_TradeSerialGroupId = retData[9];

						for (int i = 10; i < 14; i++)
						{
							comRetData.sale_TradeSerialId += retData[i];
						}

						comRetData.sale_ElecBagTypeCode = retData[14];
						comRetData.sale_TradeType = retData[15];
						comRetData.comm_PrimaryCardType = retData[16];
						comRetData.comm_SubCardType = retData[17];
						comRetData.sale_AreaCode = retData[18] + retData[19];

						for (int i = 20; i < 24; i++)
						{
							comRetData.sale_SAMCardId += retData[i];
						}
						for (int i = 24; i < 32; i++)
						{
							comRetData.comm_LogicCardNo += retData[i];
						}
						for (int i = 32; i < 36; i++)
						{
							comRetData.comm_PhysicCardNo += retData[i];
						}
						for (int i = 36; i < 39; i++)
						{
							comRetData.query_timesOperation += retData[i];
						}
						for (int i = 39; i < 43; i++)
						{
							comRetData.sale_JeThisTime += retData[i];
						}
						for (int i = 43; i < 47; i++)
						{
							comRetData.sale_JeLastTime += retData[i];
						}
						for (int i = 47; i < 51; i++)
						{
							comRetData.sale_JeNowTime += retData[i];
						}
						for (int i = 51; i < 58; i++)
						{
							comRetData.sale_CurrentTradeTime += retData[i];
						}
						for (int i = 58; i < 62; i++)
						{
							comRetData.sale_LastTradeDeviceCode += retData[i];
						}
						for (int i = 62; i < 66; i++)
						{
							comRetData.sale_LastTradeSerialId += retData[i];
						}
						for (int i = 66; i < 70; i++)
						{
							comRetData.sale_LastTradeMoney += retData[i];
						}
						for (int i = 70; i < 77; i++)
						{
							comRetData.sale_LastTradeTime += retData[i];
						}
						for (int i = 77; i < 81; i++)
						{
							comRetData.sale_CheckCode += retData[i];
						}
					}
					// 当类型为02时，会有半字节出现，采用按字符截取
					if (comRetData.comm_ChipType.equalsIgnoreCase("02"))
					{
						retDataLine = dataStr.replace(",", "");
						// 终端号
						comRetData.comm_TermNo = retDataLine.substring(10, 26);
						retDataString = replaceZero(comRetData.comm_TermNo)
								+ "#";

						// 终端标志
						comRetData.sale_TermFlag = retDataLine
								.substring(26, 27);
						retDataString += comRetData.sale_TermFlag + "#";

						// 交易时间
						comRetData.sale_CurrentTradeTime = retDataLine
								.substring(27, 41);
						retDataString += comRetData.sale_CurrentTradeTime + "#";

						// 终端交易流水号
						comRetData.sale_TradeSerialId = retDataLine.substring(
								41, 49);
						retDataString += replaceZero(comRetData.sale_TradeSerialId)
								+ "#";

						// 票卡逻辑卡号
						comRetData.comm_LogicCardNo = retDataLine.substring(49,
								65);
						retDataString += comRetData.comm_LogicCardNo + "#";

						// 票卡物理卡号
						comRetData.comm_PhysicCardNo = retDataLine.substring(
								65, 73);
						retDataString += comRetData.comm_PhysicCardNo + "#";

						// 票卡主类型
						comRetData.comm_PrimaryCardType = retDataLine
								.substring(73, 75);
						retDataString += comRetData.comm_PrimaryCardType + "#";

						// 票卡子类型
						comRetData.comm_SubCardType = retDataLine.substring(75,
								77);
						retDataString += comRetData.comm_SubCardType + "#";

						// 上次交易终端编号
						comRetData.sale_LastTradeDeviceCode = retDataLine
								.substring(77, 93);
						if (comRetData.sale_LastTradeDeviceCode
								.equals("0000000000000000"))
							comRetData.sale_LastTradeDeviceCode = comRetData.comm_TermNo;
						retDataString += replaceZero(comRetData.sale_LastTradeDeviceCode)
								+ "#";

						// 上次交易日期时间
						comRetData.sale_LastTradeTime = retDataLine.substring(
								93, 107);
						if (comRetData.sale_LastTradeTime
								.equals("00000000000000"))
							comRetData.sale_LastTradeTime = comRetData.sale_CurrentTradeTime;
						retDataString += comRetData.sale_LastTradeTime + "#";

						// 交易金额
						comRetData.sale_JeThisTime = retDataLine.substring(107,
								115);
						retDataString += replaceZero(comRetData.sale_JeThisTime)
								+ "#";

						// 本次余额
						comRetData.sale_JeNowTime = retDataLine.substring(115,
								123);
						retDataString += replaceZero(comRetData.sale_JeNowTime)
								+ "#";

						// 交易类型
						comRetData.sale_TradeType = retDataLine.substring(123,
								125);
						retDataString += comRetData.sale_TradeType + "#";

						// 本次入口终端编号
						comRetData.sale_CurrentTermNo = retDataLine.substring(
								125, 141);
						if (comRetData.sale_CurrentTermNo
								.equals("0000000000000000"))
							comRetData.sale_CurrentTermNo = comRetData.comm_TermNo;
						retDataString += replaceZero(comRetData.sale_CurrentTermNo)
								+ "#";

						// 本次入口日期时间
						comRetData.sale_CurrentDatetime = retDataLine
								.substring(141, 155);
						if (comRetData.sale_CurrentDatetime
								.equals("00000000000000"))
							comRetData.sale_CurrentDatetime = comRetData.sale_CurrentTradeTime;
						retDataString += comRetData.sale_CurrentDatetime + "#";

						// 票卡联机交易计数
						comRetData.comm_TimesOnline = retDataLine.substring(
								155, 161);
						retDataString += replaceZero(comRetData.comm_TimesOnline)
								+ "#";

						// 票卡脱机交易计数
						comRetData.comm_TimesOffline = retDataLine.substring(
								161, 167);
						retDataString += replaceZero(comRetData.comm_TimesOffline)
								+ "#";

						// 交易认证码
						comRetData.sale_CheckCode = retDataLine.substring(167,
								175);
						// retDataString +=
						// upperHexChar(comRetData.sale_CheckCode) + "#";
						retDataString += comRetData.sale_CheckCode + "#";

						// 是否测试卡
						comRetData.sale_AreaCode = retDataLine.substring(175,
								176);
						retDataString += comRetData.sale_AreaCode + "#";

						// 预留字段
						comRetData.sale_Memo = retDataLine.substring(176, 192);
						retDataString += comRetData.sale_Memo;
					}
					if (comRetData.comm_RepStatus.equals("00"))
					{
						// bld.memo =retDataString;
						if (retDataString.replaceAll("#", "").length() >= 182) // &&
						// retDataString.endsWith("0000000000000000"))
						{
							bld.memo = retDataString;
							cmdLog += "\r\nDataField: " + retDataString; // 记录解析出的字段
							cmdLog += "\r\n  TradeType: "
									+ String
											.valueOf(comRetData.comm_CurTradeType)
									+ "   CardNO: "
									+ comRetData.query_CardNoSelf
									+ "   SaleJe: "
									+ comRetData.sale_JeThisTime + "   Ye: "
									+ comRetData.sale_JeNowTime;
							comRetData.comm_ErrCode = comRetData.comm_RepStatus;
							comRetData.comm_ErrMsg = "扣款成功";
						}
						else
						{
							// 若返回的报文数据有问题，则超时重取
							comRetData.comm_ErrCode = "99";
							return false;
						}
					}

					break;

				default:
					break;
			}
			return true;
		}
		catch (Exception ex)
		{
			// 异常时,仅针对ACTION_SALE置超时标志"99",避免与超时补入相冲突
			if (comRetData.comm_CurTradeType == ACTION_SALE)
			{
				comRetData.comm_ErrCode = "99";
			}
			ex.printStackTrace();
			return false;
		}
		finally
		{
			// 处理完后将返回串置为空
			ZspjWHT_PaymentBankFunc.retDataString = "";
		}
	}

	private Vector readGBKFileByVector(String fileName)
	{
		BufferedReader br = null;

		br = CommonMethod.readFileGBK(fileName);

		if (br == null) { return null; }

		Vector v = new Vector();
		String line;
		String[] content = null;

		try
		{
			while ((line = br.readLine()) != null)
			{
				content = new String[3];

				line = line.trim();

				if ((line == null) || (line.trim().length() <= 0))
				{
					v.add(new String[3]);

					continue;
				}

				String[] lines = new String[2];

				if (line.indexOf("&&") < 0)
				{
					lines[0] = line;
					lines[1] = null;
				}
				else
				{
					lines[0] = line.substring(0, line.indexOf("&&"));
					lines[1] = line.substring(line.indexOf("&&") + 2);
				}

				if (lines[1] == null)
				{
					content[2] = null;
				}
				else
				{
					content[2] = lines[1].trim();
				}

				if (lines[0].indexOf("=") < 0)
				{
					content[0] = lines[0].trim();
					content[1] = null;
				}
				else
				{
					content[0] = lines[0].substring(0, lines[0].indexOf("="))
							.trim();
					content[1] = lines[0].substring(lines[0].indexOf("=") + 1)
							.trim();
				}

				v.add(content);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			try
			{
				if (br != null)
				{
					br.close();
				}
			}
			catch (Exception e)
			{
			}
		}
		return v;
	}

	private String replaceZero(String line)
	{
		char[] tmpChr = null;
		try
		{
			tmpChr = line.toCharArray();

			for (int i = 0; i < tmpChr.length; i++)
			{
				if (tmpChr[i] != '0')
					break;
				tmpChr[i] = ' ';
			}
			return String.valueOf(tmpChr);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return line;
		}
	}

	/*
	 * private String upperHexChar(String hex) { char[] tmpChr = null;
	 * 
	 * try { tmpChr = hex.toCharArray();
	 * 
	 * for (int i = 0; i < tmpChr.length; i++) { if (tmpChr[i] >= 'a' &&
	 * tmpChr[i] <= 'z') tmpChr[i] = (char) (tmpChr[i] - 32);
	 * 
	 * else if (tmpChr[i] >= 'A' && tmpChr[i] <= 'Z') tmpChr[i] = (char)
	 * (tmpChr[i] + 32); } return String.valueOf(tmpChr); } catch (Exception ex) {
	 * ex.printStackTrace(); return hex; } }
	 */

	public static String getValueFromVector(Vector v, String attr)
	{
		for (int i = 0; v != null && i < v.size(); i++)
		{
			String[] s = (String[]) v.elementAt(i);
			if (attr.equalsIgnoreCase(s[0])) { return s[1]; }
		}
		return null;
	}

	private String getErrMsg(String code)
	{
		String err = "未知错误";
		try
		{
			String filename = GlobalVar.ConfigPath + "//whtrsp.ini";

			if (!PathFile.fileExist(filename))
				return err;

			Vector v = readGBKFileByVector(filename);

			for (int i = 0; i < v.size(); i++)
			{
				String[] row = (String[]) v.elementAt(i);
				if (code.equalsIgnoreCase(row[0]))
					err = row[1].trim();
			}
			return err;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return err;
		}
	}

	public static void main(String[] args)
	{
		ZspjWHT_PaymentBankFunc func = new ZspjWHT_PaymentBankFunc();
		String dataStr = "BB,21,AB,00,02,90,27,11,01,00,00,10,09,05,96,21,A6,00,09,74,59,11,01,00,00,01,00,00,35,91,00,BB,00,10,09,AA,00,CC,38";
		func.getInputCmd(1, dataStr);
	}

	// 串口返回数据
	public class OutputData
	{
		// 记录当前交易类型
		public int comm_CurTradeType = 0;

		// 公共返回数据
		public String comm_RepStatus = ""; // 响应状态

		public String comm_TermNo = ""; // 终端编号

		public String comm_ChipType = ""; // 芯片类型

		public String comm_LogicCardNo = ""; // 票卡逻辑卡号

		public String comm_BackLogicCardNo = "";// 备份

		public String comm_PhysicCardNo = ""; // 票卡物理卡号

		public String comm_BackPhysicCardNo = ""; // 备份

		public String comm_TimesOnline = ""; // 卡内联机交易次数

		public String comm_TimesOffline = ""; // 卡内脱机交易次数

		public String comm_PrimaryCardType = ""; // 票卡主类型

		public String comm_SubCardType = ""; // 票卡子类型

		// 自检返回数据
		public String check_DeviceVersion = ""; // 设备版本

		// 查询返回数据
		public String query_YeOnCard = ""; // 卡上余额

		public String query_LogicCardNo = ""; // 备份逻辑卡号，用于超时对比

		// 当芯片为01时
		public String query_timesOperation = ""; // 票卡操作计数

		// 当芯片为02时
		public String query_CardNoSelf = ""; // 卡面印刻号

		// 扣款返回数据
		public String sale_TradeDeviceCode = ""; // 交易设备代码

		public String sale_TradeSerialGroupId = ""; // 交易序列组号

		public String sale_TradeSerialId = ""; // 交易序列号 - 交易流水

		public String sale_ElecBagTypeCode = ""; // 电子钱包类型代码

		public String sale_TradeType = ""; // 交易类型

		public String sale_AreaCode = ""; // 区域代码 -是否测试卡

		public String sale_SAMCardId = ""; // SAM卡编号

		public String sale_JeThisTime = ""; // 本次票卡操作金额

		public String sale_JeLastTime = ""; // 交易原票价

		public String sale_JeNowTime = ""; // 票卡最新金额

		public String sale_CurrentTradeTime = ""; // 本次交易时间

		public String sale_CurrentTermNo = ""; // 本次入口终端编号

		public String sale_CurrentDatetime = ""; // 本次入口时期时间

		public String sale_LastTradeDeviceCode = ""; // 上次交易设备号

		public String sale_LastTradeSerialId = ""; // 上次交易序列号

		public String sale_LastTradeMoney = ""; // 上次交易金额

		public String sale_LastTradeTime = ""; // 上次交易时间

		public String sale_CheckCode = ""; // 交易验证码

		public String sale_TermFlag = ""; // 终端标志

		public String sale_Memo = "";

		// 错误信息
		public String comm_ErrCode = "";

		public String comm_ErrMsg = "";

		public void clear(int tradeType)
		{
			comm_CurTradeType = tradeType;

			// 公共数据
			comm_RepStatus = ""; // 响应状态
			comm_TermNo = ""; // 终端编号
			comm_ChipType = ""; // 芯片类型
			comm_LogicCardNo = ""; // 票卡逻辑卡号
			comm_BackLogicCardNo = "";// 备份
			comm_PhysicCardNo = ""; // 票卡物理卡号
			comm_BackPhysicCardNo = ""; // 备份
			comm_TimesOnline = ""; // 卡内联机交易次数
			comm_TimesOffline = ""; // 卡内脱机交易次数
			comm_PrimaryCardType = ""; // 票卡主类型
			comm_SubCardType = ""; // 票卡子类型

			if (tradeType == 1)
			{
				check_DeviceVersion = ""; // 设备版本
			}

			if (tradeType == 2)
			{ // 查询返回数据
				query_YeOnCard = ""; // 卡上余额
				// 当芯片为01时
				query_LogicCardNo = "";
				query_timesOperation = ""; // 票卡操作计数
				// 当芯片为02时
				// comm_TimesOnline = ""; // 卡内联机交易次数
				// comm_TimesOffline = ""; // 卡内脱机交易次数
				query_CardNoSelf = ""; // 卡面印刻号
			}
			if (tradeType == 3 || tradeType == 4 || tradeType == 5)
			{
				// 扣款返回数据
				sale_TradeDeviceCode = ""; // 交易设备代码
				sale_TradeSerialGroupId = ""; // 交易序列组号
				sale_TradeSerialId = ""; // 交易序列号 - 交易流水
				sale_ElecBagTypeCode = ""; // 电子钱包类型代码
				sale_TradeType = ""; // 交易类型
				sale_AreaCode = ""; // 区域代码 -是否测试卡
				sale_SAMCardId = ""; // SAM卡编号
				sale_JeThisTime = ""; // 本次票卡操作金额
				sale_JeLastTime = ""; // 交易原票价
				sale_JeNowTime = ""; // 票卡最新金额
				sale_CurrentTradeTime = ""; // 本次交易时间
				sale_CurrentTermNo = ""; // 本次入口终端编号
				sale_CurrentDatetime = ""; // 本次入口时期时间
				sale_LastTradeDeviceCode = ""; // 上次交易设备号
				sale_LastTradeSerialId = ""; // 上次交易序列号
				sale_LastTradeMoney = ""; // 上次交易金额
				sale_LastTradeTime = ""; // 上次交易时间
				sale_CheckCode = ""; // 交易验证码
				sale_TermFlag = ""; // 终端标志
				sale_Memo = ""; // 预留字段
			}
			comm_ErrCode = "XX";
			comm_ErrMsg = "未知错误";
		}
	}

	// 接收串口数据

	public class WhtSerialConnection extends Javax_SerialConnection
	{
		public WhtSerialConnection(Javax_SerialParameters param)
		{
			super(param);
		}

		public void serialEvent(SerialPortEvent e)
		{
			int newData = 0;
			int count = 0;
			int sumLen = 0;
			String tmpStr = "";

			switch (e.getEventType())
			{
				case SerialPortEvent.DATA_AVAILABLE:
					while (newData != -1)
					{
						try
						{
							newData = getInputStream().read();

							if (newData != -1)
							{
								tmpStr = Integer.toHexString(newData);
								if (tmpStr.length() == 1)
									tmpStr = "0" + tmpStr;
								ZspjWHT_PaymentBankFunc.retDataString += tmpStr + ',';
								count++;
							}

							// 返回的第二个字节为响应数据的长度 + 3 即为整个报文的长度
							if (count == 2)
								sumLen = newData + 3;

							if (count >= sumLen && newData == -1)
							{ // 将返回的长度放到字符末尾用于后期长度判断
								ZspjWHT_PaymentBankFunc.retDataString += String
										.valueOf(count);
								break;
							}
						}
						catch (Exception ex)
						{
							System.err.println(ex);
							return;
						}
					}

					break;

				// If break event append BREAK RECEIVED message.
				case SerialPortEvent.BI:
					break;

				default:
					break;
			}
			return;
		}
	}
}
