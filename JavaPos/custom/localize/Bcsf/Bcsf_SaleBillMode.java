package custom.localize.Bcsf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import bankpay.Bank.JavaZrx_PaymentBankFunc;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleAppendDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Bcsf_SaleBillMode extends SaleBillMode
{
	protected final int SBM_exchangegoods = 201; // 打印商品换购的名称
	protected final int SBM_usejf = 202; // 本次使用积分
	protected final int SBM_yhmoney = 203; // 本次优惠金额
	protected final int SBM_payjf = 204; // 当前付款所用的积分
	protected final int SBM_printpopmemo = 205; // 打印当前组合促销描述
	protected final int SBM_printyhsl = 206; // 打印印花派送信息

	protected JavaZrx_PaymentBankFunc jz = null; // 知而行对象

	protected String extendCase(PrintTemplateItem item, int index)
	{
		String line = null;
		String[] idno = null;
		double yhmoney = 0;
		SaleGoodsDef sgd = null;

		switch (Integer.parseInt(item.code))
		{
			case SBM_goodname: // 商品名称
				line = ((SaleGoodsDef) salegoods.elementAt(index)).name;

				if (((SaleGoodsDef) salegoods.elementAt(index)).str3 != null && ((SaleGoodsDef) salegoods.elementAt(index)).str3.split(":").length >= 3 && ((SaleGoodsDef) salegoods.elementAt(index)).str3.split(":")[2] != null && !((SaleGoodsDef) salegoods.elementAt(index)).str3.split(":")[2].equals("") && ((SaleGoodsDef) salegoods.elementAt(index)).str3.split(":")[2].charAt(0) == 'x')
				{
					line = "* " + line;
				}

				// 记录商品所能打印的最大长度
				goodnamemaxlength = item.length;
				break;
			case SBM_cjdj: // 售价

				sgd = (SaleGoodsDef) salegoods.elementAt(index);

				for (int i = 0; i < salepay.size(); i++)
				{
					SalePayDef spd = (SalePayDef) salepay.elementAt(i);

					idno = spd.idno.split(",");

					if (idno.length < 7)
						continue;

					if (sgd.rowno != Convert.toInt(idno[6]))
						continue;

					return ManipulatePrecision.doubleToString(ManipulatePrecision.div((sgd.hjje - sgd.hjzk), sgd.sl) - Double.parseDouble(idno[2]));

				}

				line = null;
				break;
			case SBM_cjje:
				sgd = (SaleGoodsDef) salegoods.elementAt(index);

				for (int i = 0; i < salepay.size(); i++)
				{
					SalePayDef spd = (SalePayDef) salepay.elementAt(i);

					idno = spd.idno.split(",");

					if (idno.length < 7)
						continue;

					if (sgd.rowno != Convert.toInt(idno[6]))
						continue;

					return ManipulatePrecision.doubleToString((sgd.hjje - sgd.hjzk) - spd.je);

				}

				line = null;

				break;
			case SBM_ysje: // 应收金额

				for (int i = 0; i < salepay.size(); i++)
				{
					SalePayDef spd = (SalePayDef) salepay.elementAt(i);

					idno = spd.idno.split(",");

					if (idno.length < 7)
						continue;

					yhmoney = yhmoney + spd.je;
				}

				line = ManipulatePrecision.doubleToString((salehead.ysje - yhmoney) * SellType.SELLSIGN(salehead.djlb));

				break;
			case SBM_sjfk: // 实收金额
				for (int i = 0; i < salepay.size(); i++)
				{
					SalePayDef spd = (SalePayDef) salepay.elementAt(i);

					idno = spd.idno.split(",");

					if (idno.length < 7)
						continue;

					yhmoney = yhmoney + spd.je;
				}

				line = ManipulatePrecision.doubleToString((salehead.sjfk - yhmoney) * SellType.SELLSIGN(salehead.djlb));
				break;
			case SBM_hykh: // 会员卡号

				if ((salehead.hykh == null) || (salehead.hykh.length() <= 0))
				{
					line = null;
				}
				else
				{
					if (salehead.hykh.length() >= 10)
					{
						line = salehead.hykh.substring(10);
					}
					else
					{
						line = salehead.hykh;
					}
				}

				break;
			case SBM_payno: // 付款方式帐号
				line = ((SalePayDef) salepay.elementAt(index)).payno;

				if ((line != null) && (line.length() > 0))
				{
					idno = ((SalePayDef) salepay.elementAt(index)).idno.split(",");

					if (idno.length < 7)
						break;

					line = line.substring(10);
				}
				else
				{
					line = null;
				}
				break;
			case SBM_exchangegoods:
				SalePayDef salepaydef = (SalePayDef) salepay.elementAt(index);
				idno = salepaydef.idno.split(",");

				if (idno.length < 7)
					return null;

				for (int i = 0; i < salegoods.size(); i++)
				{
					sgd = (SaleGoodsDef) salegoods.elementAt(i);

					if (sgd.rowno != Convert.toInt(idno[6]))
						continue;

					line = "换购商品:" + sgd.name;

					break;
				}
				break;
			case SBM_usejf:
				double usejf = 0;

				for (int i = 0; i < salepay.size(); i++)
				{
					SalePayDef spd = (SalePayDef) salepay.elementAt(i);

					idno = spd.idno.split(",");

					if (idno.length < 7)
						continue;

					usejf = usejf + Double.parseDouble(idno[0]);
				}

				if (usejf <= 0)
				{
					line = null;
				}
				else
				{
					line = "本次累计使用积分:" + ManipulatePrecision.doubleToString(usejf);
				}

				break;
			case SBM_yhmoney:
				for (int i = 0; i < salepay.size(); i++)
				{
					SalePayDef spd = (SalePayDef) salepay.elementAt(i);

					idno = spd.idno.split(",");

					if (idno.length < 7)
						continue;

					yhmoney = yhmoney + (Convert.toDouble(idno[0]) / Convert.toDouble(idno[1])) * Convert.toDouble(idno[2]);
					// yhmoney = yhmoney + Double.parseDouble(idno[2]);
				}

				if (yhmoney <= 0)
				{
					line = null;
				}
				else
				{
					line = "本次累计优惠金额:" + ManipulatePrecision.doubleToString(yhmoney);
				}
				break;
			case SBM_payjf:
				SalePayDef spd = (SalePayDef) salepay.elementAt(index);
				idno = spd.idno.split(",");

				if (idno.length < 7)
					return null;

				for (int i = 0; i < salegoods.size(); i++)
				{
					sgd = (SaleGoodsDef) salegoods.elementAt(i);

					if (sgd.rowno != Convert.toInt(idno[6]))
						continue;

					line = "换购使用积分:" + Double.parseDouble(idno[0]);

					break;
				}
				break;
			case SBM_printpopmemo:
				line = "";
				for (int i = 0; i < salegoods.size(); i++)
				{
					sgd = (SaleGoodsDef) salegoods.elementAt(i);

					if (sgd.str3 != null && sgd.str3.split(":").length >= 3 && sgd.str3.split(":")[2].charAt(0) == 'x')
					{
						line = "* " + sgd.str3.split(":")[2].substring(1);

						break;
					}

				}

				break;
			case SBM_printyhsl:
				if (GlobalInfo.sysPara.isenableyhps == 'Y')
					line = "本次消费获" + Convert.toInt(salehead.num1 + salehead.num2) + "枚印花，即时领取有效";
				break;
		}

		return line;
	}

	// 打印小票赠送清单
	public void printSaleTicketMSInfo()
	{
		// printSaleTicketZrxInfo();

		printSaleTicketGZInfo();
	}

	// 以下知而行接口 --------------------------------------------------

	public boolean isExistZRXPayMode()
	{
		if (ConfigClass.Bankfunc == null || ConfigClass.Bankfunc.length() <= 0)
			return false;

		String conf[] = ConfigClass.Bankfunc.split("\\|");

		for (int i = 0; i < conf.length; i++)
		{
			String[] s = conf[i].split(",");

			if (s == null || s.length < 1)
				continue;

			if (s[0].trim().equals("bankpay.Bank.JavaZrx_PaymentBankFunc"))
			{
				if (s.length == 1)
					return true;

				PayModeDef pm = DataService.getDefault().searchPayMode(s[1]);

				if (pm == null)
					return false;

				return true;
			}

		}

		return false;
	}

	/*
	 * private void printSaleTicketZrxInfo() { if (!isExistZRXPayMode()) return
	 * ;
	 * 
	 * try { if (salehead.printnum > 0) return ;
	 * 
	 * if (jz == null) { jz = new JavaZrx_PaymentBankFunc(); }
	 * 
	 * // 开始生成并打印优惠券 if (!salehead.str2.equals("F_XSXP_ADD")) { if
	 * (!jz.sendSaleGoods(salehead,salegoods)) return ; }
	 * 
	 * jz.SendFinish(salehead); } catch (Exception ex) { ex.printStackTrace(); }
	 * }
	 */
	// 以下广众接口 ----------------------------------------------------

	public boolean isExistGZPayMode()
	{
		if (ConfigClass.Bankfunc == null || ConfigClass.Bankfunc.length() <= 0)
			return false;

		String conf[] = ConfigClass.Bankfunc.split("\\|");

		for (int i = 0; i < conf.length; i++)
		{
			String[] s = conf[i].split(",");

			if (s == null || s.length < 1)
				continue;

			if (s[0].trim().equals("bankpay.Bank.BjcsfGZ_PaymentBankFunc"))
			{
				if (s.length == 1)
					return true;

				PayModeDef pm = DataService.getDefault().searchPayMode(s[1]);

				if (pm == null)
					return false;

				return true;
			}

		}

		return false;
	}

	private void printSaleTicketGZInfo()
	{
		String line = "";
		int flag = 0;
		double hjje = 0;

		// 是否存在广众付款方式
		if (!isExistGZPayMode())
			return;

		// 打印广众签购单
		if (salehead.memo.equals("F_XSXP_ADD") && (SellType.ISBACK(salehead.djlb) || SellType.ISSALE(salehead.djlb)))
		{
			for (int i = 0; i < salepay.size(); i++)
			{
				SalePayDef salePay = (SalePayDef) salepay.get(i);

				String filename = "C:\\JavaPos\\GZCMCard\\GZCMCard_" + salePay.payno + ".txt";

				if (!PathFile.fileExist(filename))
					continue;

				XYKPrintDoc(filename);
			}

			XYKPrintDoc(null);
		}

		// 断开打印机
		if (GlobalInfo.syjDef.isprint == 'Y' && Printer.getDefault() != null && Printer.getDefault().getStatus())
		{
			Printer.getDefault().close();
		}

		try
		{
			// 开始生成并打印优惠券
			if (!salehead.memo.equals("F_XSXP_ADD"))
			{
				delOldFile();

				line = "1," + GlobalInfo.sysPara.mktcode + "," + salehead.fphm + "," + GlobalInfo.syjDef.syjh + "," + salehead.hykh + "," + salehead.syyh + ",";

				for (int i = 0; i < salegoods.size(); i++)
				{
					SaleGoodsDef saleGoods = (SaleGoodsDef) salegoods.get(i);

					hjje = saleGoods.hjje;

					if (SellType.ISBACK(salehead.djlb))
					{
						flag = 1;

						hjje = hjje * -1;
					}

					line = line + "|" + saleGoods.rowno + "|^|" + saleGoods.barcode + "|^|" + saleGoods.name.replaceAll(",", "").replaceAll(";", "").replace("'", "") + "|^";

					line = line + saleGoods.sl + "^" + hjje + ";";
				}

				line = line + "," + flag;

				if (!createRequestFile(line))
					return;

				if (!execuFunction())
					return;

			}

			delOldFile();

			line = "5," + GlobalInfo.sysPara.mktcode + "," + salehead.fphm + "," + GlobalInfo.syjDef.syjh + ",";

			SalePayDef pay1 = null;

			int i = 0;
			for (i = 0; i < salepay.size(); i++)
			{
				SalePayDef salePay = (SalePayDef) salepay.get(i);

				if (salePay.flag == '1')
				{
					for (int j = 0; j < salepay.size(); j++)
					{
						pay1 = (SalePayDef) salepay.get(j);

						if ((pay1.flag == '2') && pay1.paycode.equals(salePay.paycode) && (pay1.je != 0))
						{
							if (salePay.je > pay1.je)
							{
								salePay.je = salePay.je - pay1.je;
								pay1.je = 0;
							}
							else if (salePay.je <= pay1.je)
							{
								pay1.je = pay1.je - salePay.je;
								salePay.je = 0;
								salepay.removeElementAt(i);

								i--;
							}

							break;
						}
					}
				}
			}

			for (int k = 0; k < salepay.size(); k++)
			{
				String payno = "";

				SalePayDef salePay = (SalePayDef) salepay.get(k);

				hjje = salePay.je;

				if (SellType.ISBACK(salehead.djlb))
				{
					flag = 1;

					hjje = hjje * -1;
				}

				if (salePay.payno != null && !salePay.payno.trim().equals(""))
				{
					payno = "|" + salePay.payno + "|";
				}

				line = line + "|" + salePay.paycode + "|^" + hjje + "^" + payno + ";";
			}

			line = line + ",," + flag;

			if (!createRequestFile(line))
				return;

			if (!execuFunction())
				return;
		}
		finally
		{
			// 连接打印机
			if (GlobalInfo.syjDef.isprint == 'Y' && Printer.getDefault() != null && !Printer.getDefault().getStatus())
			{
				Printer.getDefault().open();
				Printer.getDefault().setEnable(true);
			}
		}
	}

	private boolean delOldFile()
	{
		String errmsg = null;

		// 先删除上次交易数据文件
		if (PathFile.fileExist("C:\\JavaPos\\request.txt"))
		{
			PathFile.deletePath("C:\\JavaPos\\request.txt");

			if (PathFile.fileExist("C:\\JavaPos\\request.txt"))
			{
				errmsg = "交易请求文件request.txt无法删除,请重试";

				new MessageBox(errmsg);
				return false;
			}
		}

		if (PathFile.fileExist("C:\\JavaPos\\result.txt"))
		{
			PathFile.deletePath("C:\\JavaPos\\result.txt");

			if (PathFile.fileExist("C:\\JavaPos\\result.txt"))
			{
				errmsg = "交易请求文件result.txt无法删除,请重试";

				new MessageBox(errmsg);
				return false;
			}
		}

		if (PathFile.fileExist("C:\\JavaPos\\GZCMCard"))
		{
			PathFile.deletePath("C:\\JavaPos\\GZCMCard");
		}

		return true;
	}

	private boolean createRequestFile(String line)
	{
		PrintWriter pw = null;

		try
		{
			pw = CommonMethod.writeFile("C:\\JavaPos\\request.txt");
			if (pw != null)
			{
				pw.print(line);
				pw.flush();
			}

			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("写入广众工程请求数据失败!" + ex.getMessage(), null, false);
			ex.printStackTrace();
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

	private boolean execuFunction()
	{
		try
		{
			// 调用接口模块
			if (PathFile.fileExist("C:\\JavaPos\\javaposbank.exe"))
			{
				CommonMethod.waitForExec("C:\\JavaPos\\javaposbank.exe BJCSFGZ");
			}
			else
			{
				new MessageBox("找不到广众工程模块 javaposbank.exe");

				return false;
			}

			// 读取应答数据
			if (!XYKReadResult())
				return false;

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean XYKReadResult()
	{
		BufferedReader br = null;

		try
		{
			if (!PathFile.fileExist("C:\\JavaPos\\result.txt") || ((br = CommonMethod.readFileGBK("C:\\JavaPos\\result.txt")) == null))
			{
				new MessageBox("读取广众工程应答文本失败!", null, false);

				return false;
			}

			String line = br.readLine();

			if (line.length() <= 0) { return false; }

			String result[] = line.split(",");

			if (result == null)
			{
				new MessageBox("读取广众工程应答数据为空!", null, false);
				return false;
			}

			String retcode = result[0];

			if (!retcode.trim().equals("1"))
			{
				new MessageBox("读取广众工程应答数据失败!", null, false);
				return false;
			}

			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("读取广众工程应答数据异常!" + ex.getMessage(), null, false);
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

					if (PathFile.fileExist("C:\\JavaPos\\request.txt"))
					{
						PathFile.deletePath("C:\\JavaPos\\request.txt");
					}

					if (PathFile.fileExist("C:\\JavaPos\\result.txt"))
					{
						PathFile.deletePath("C:\\JavaPos\\result.txt");
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public void XYKPrintDoc(String printName)
	{
		ProgressBox pb = null;

		try
		{
			pb = new ProgressBox();
			pb.setText("正在打印广众签购单,请等待...");

			if (printName == null)
			{
				// 开始新打印
				printStart();

				for (int i = 0; i < 7; i++)
				{
					printLine(" ");
				}

				printCutPaper();

				return;
			}

			for (int i = 0; i < 1; i++)
			{
				// 开始新打印
				printStart();

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

						printLine(line);
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

				printCutPaper();
			}
		}
		catch (Exception ex)
		{
			new MessageBox("打印广众签购单发生异常\n\n" + ex.getMessage());
			ex.printStackTrace();
		}
		finally
		{
			if (pb != null)
			{
				pb.close();
				pb = null;
			}

			if (printName != null && PathFile.fileExist(printName))
			{
				PathFile.deletePath(printName);
			}
		}
	}

	public void sendSaleFpCode()
	{
		// 更改后台表中发票字段
		Vector saleappend = new Vector();

		SaleAppendDef sad = new SaleAppendDef();

		sad.syjh = salehead.syjh;
		sad.fphm = salehead.fphm;
		sad.rowno = 0;
		sad.str1 = "A";
		sad.str2 = salehead.salefphm;

		saleappend.add(sad);

		if (saleappend.size() > 0 && !NetService.getDefault().sendSaleAppend(saleappend))
		{
			// 记录小票未发送到WebService任务
			AccessLocalDB.getDefault().writeTask(StatusType.TASK_SENDSALEAPPEND, GlobalInfo.balanceDate + "," + salehead.fphm);
		}

		saleappend.clear();
		saleappend = null;
	}
}
