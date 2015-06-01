package custom.localize.Hzjb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Hzjb_ICCardCaller
{
	private String prnFile = GlobalVar.ConfigPath + "/lhmzk.ini";
	private String prnBill = "C:\\JAVAPOS\\lhmzkprn.txt";
	private boolean enable = false;

	static
	{
		if(PathFile.fileExist("C:\\javapos\\Hzjb_LHICCard.dll"))
			System.loadLibrary("Hzjb_LHICCard");
	}

	public native int start();

	public native int stop();

	public native int login(String username);

	public native int logout();

	public native String[] check(int checkmode, int scoresrc);

	public native String[] pay(String xid, String card_num, String pwd, long total, int clear_cost);

	public native String getLastError();

	public static Hzjb_ICCardCaller caller = new Hzjb_ICCardCaller();

	public static Hzjb_ICCardCaller getDefault()
	{
		return caller;
	}

	public boolean getEnable()
	{
		return enable;
	}

	public void setEnable(boolean enable)
	{
		this.enable = enable;
	}

	public void rePrintBill(SaleHeadDef h, SalePayDef p)
	{
		ProgressBox pb = null;
		BufferedReader br = null;
		try
		{
			if (PathFile.fileExist(prnBill))
				PathFile.deletePath(prnBill);

			if (!writeSaleBill(h, p))
			{
				new MessageBox("生成储值卡消费凭证文件失败!");
				return;
			}

			if (!PathFile.fileExist(prnBill))
			{
				new MessageBox("未发现联华储值卡消费凭证文件!");
				return;
			}

			pb = new ProgressBox();
			pb.setText("正在打印联华储值卡消费凭证,请稍等...");

			for (int i = 0; i < GlobalInfo.sysPara.bankprint; i++)
			{
				Printer.getDefault().startPrint_Journal();

				// 由于发现在windows环境下,用GBK读取文件会产生BUG,改为GB2310
				br = CommonMethod.readFile(prnBill);

				if (br == null)
				{
					new MessageBox("打开凭证文件失败!");

					return;
				}

				String line = null;

				while ((line = br.readLine()) != null)
				{
					if (line.length() <= 0)
					{
						continue;
					}

					Printer.getDefault().printLine_Journal(line);
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
			if (br != null)
			{
				try
				{
					br.close();
					br = null;
				}
				catch (IOException e)
				{
					e.printStackTrace();
					br = null;
				}
			}
		}

	}

	public void printBill(MzkRequestDef req, MzkResultDef ret)
	{
		ProgressBox pb = null;
		BufferedReader br = null;

		try
		{

			if (PathFile.fileExist(prnBill))
				PathFile.deletePath(prnBill);

			if (!writeSaleBill(req, ret))
			{
				new MessageBox("生成储值卡消费凭证文件失败!");
				return;
			}

			if (!PathFile.fileExist(prnBill))
			{
				new MessageBox("未发现联华储值卡消费凭证文件!");
				return;
			}

			pb = new ProgressBox();
			pb.setText("正在打印联华储值卡消费凭证,请稍等...");

			for (int i = 0; i < GlobalInfo.sysPara.bankprint; i++)
			{
				Printer.getDefault().startPrint_Journal();

				// 由于发现在windows环境下,用GBK读取文件会产生BUG,改为GB2310
				br = CommonMethod.readFile(prnBill);

				if (br == null)
				{
					new MessageBox("打开凭证文件失败!");

					return;
				}

				String line = null;

				while ((line = br.readLine()) != null)
				{
					if (line.length() <= 0)
					{
						continue;
					}

					Printer.getDefault().printLine_Journal(line);
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
			if (br != null)
			{
				try
				{
					br.close();
					br = null;
				}
				catch (IOException e)
				{
					e.printStackTrace();
					br = null;
				}
			}
		}

	}

	protected boolean writeSaleBill(SaleHeadDef h, SalePayDef p)
	{
		PrintWriter pw = null;
		Vector billVec = null;
		BufferedReader br = null;

		try
		{
			if (PathFile.isPathExists(prnFile))
			{
				br = CommonMethod.readFileGB2312(prnFile);
				if (br == null)
				{
					new MessageBox("打开模板文件失败!");

					return false;
				}

				billVec = new Vector();
				String line = null;

				while ((line = br.readLine()) != null)
				{
					if (line.length() <= 0)
					{
						billVec.add("\n");
					}

					billVec.add(line);
				}
				if (h.printnum > 0)
					billVec.insertElementAt("            ** 重印" + String.valueOf(h.printnum) + "**            ", 0);

				br.close();
				br = null;
				line = null;

				for (int i = 0; i < billVec.size(); i++)
				{
					line = (String) billVec.get(i);

					if (line.indexOf("#mkt") != -1)
					{
						line = line.replace("#mkt", GlobalInfo.sysPara.mktname);
					}
					if (line.indexOf("#seq") != -1)
					{
						line = line.replace("#seq", String.valueOf(h.fphm));
					}
					if (line.indexOf("#user") != -1)
					{
						line = line.replace("#user", h.syyh);
					}
					if (line.indexOf("#counter") != -1)
					{
						line = line.replace("#counter", h.syjh);
					}
					if (line.indexOf("#saletype") != -1)
					{
						// String.valueOf(SellType.getDefault().typeExchange(h.djlb,
						// h.hhflag, h))
						if (SellType.ISSALE(h.djlb))
							line = line.replace("#saletype", "销------售");
						else if (SellType.ISBACK(h.djlb))
							line = line.replace("#saletype", "退------货");
						else
							line = line.replace("#saletype", "未知交易类型");
					}
					if (line.indexOf("#count") != -1)
					{
						line = line.replace("#count", String.valueOf(h.hjzsl));
					}
					if (line.indexOf("#total") != -1)
					{
						line = line.replace("#total", String.valueOf(p.je));
					}
					if (line.indexOf("#amount") != -1)
					{
						line = line.replace("#amount", String.valueOf(SellType.SELLSIGN(h.djlb)*p.je));
					}
					if (line.indexOf("#cardno") != -1)
					{
						line = line.replace("#cardno", p.payno);
					}
					if (line.indexOf("#leave") != -1)
					{
						line = line.replace("#leave", String.valueOf(p.kye));
					}

					if (line.indexOf("#score") != -1)
					{
						line = line.replace("#score", "");
					}
					if (line.indexOf("#date") != -1)
					{
						line = line.replace("#date", ManipulateDateTime.getCurrentDate());
					}
					if (line.indexOf("#time") != -1)
					{
						line = line.replace("#time", ManipulateDateTime.getCurrentTime());
					}
					billVec.set(i, line);
				}

				pw = CommonMethod.writeFileUTF(prnBill);

				if (pw != null)
				{
					for (int i = 0; i < billVec.size(); i++)
						pw.println((String) billVec.get(i));

					pw.flush();
				}
			}
			else
			{
				StringBuffer sb = new StringBuffer();

				sb.append("欢迎光临世纪联华" + GlobalInfo.sysPara.mktname + "!\n");// "\n");
				sb.append("世纪联华官方旗舰店敬待您的访问,让您足不" + "\n");
				sb.append("出户,轻松购物!欢迎点击:http://lhgw.tmall.com. 留存联\n");
				sb.append("流水:" + h.fphm + "    工号:" + h.syyh + "     机号:" + h.syjh + "\n");

				if (SellType.ISSALE(h.djlb))
					sb.append("--------------销------售----------------" + "\n");
				else if (SellType.ISBACK(h.djlb))
					sb.append("--------------退------货----------------" + "\n");
				else
					sb.append("--------------未知交易类型--------------" + "\n");

				sb.append("货   号      品名          数量金额(元)" + "\n");
				sb.append("----------------------------------------" + "\n");

				sb.append("合计:" + p.je + "     " + h.hjzsl + "     " + p.je + "\n");
				sb.append("会员电子消费卡扣款:" + p.je + "\n");
				sb.append("卡号:" + p.payno + "    " + "余额:" + p.kye + "\n");
				sb.append("积分卡号:" + p.payno + "本次积分:" + "\n");
				sb.append("			" + ManipulateDateTime.getCurrentDate() + "    " + ManipulateDateTime.getCurrentTime() + "\n");
				sb.append("	谢谢惠顾,欢迎再次光临!" + "\n");
				sb.append("请仔细核对小票，如有质量问题请于一周内联系调换。" + "\n");
				sb.append("庆春店电话:87220255,联华总部咨询服务电话:4008571996" + "\n");
				sb.append("购物之日起一个月内至购物门店开具发票有效。" + "\n");

				if (PathFile.fileExist(prnBill))
					PathFile.deletePath(prnBill);

				pw = CommonMethod.writeFileUTF(prnBill);

				if (pw != null)
				{
					pw.println(sb.toString());
					pw.flush();
				}
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
				catch (IOException e)
				{
					e.printStackTrace();
					br = null;
				}
			}
		}

	}

	protected boolean writeSaleBill(MzkRequestDef req, MzkResultDef ret)
	{
		PrintWriter pw = null;
		Vector billVec = null;
		BufferedReader br = null;

		try
		{
			if (PathFile.isPathExists(prnFile))
			{
				br = CommonMethod.readFileGB2312(prnFile);
				if (br == null)
				{
					new MessageBox("打开模板文件失败!");

					return false;
				}

				billVec = new Vector();
				String line = null;

				while ((line = br.readLine()) != null)
				{
					if (line.length() <= 0)
					{
						billVec.add("\n");
					}

					billVec.add(line);
				}

				br.close();
				br = null;
				line = null;

				for (int i = 0; i < billVec.size(); i++)
				{
					line = (String) billVec.get(i);

					if (line.indexOf("#mkt") != -1)
					{
						line = line.replace("#mkt", GlobalInfo.sysPara.mktname);
					}
					if (line.indexOf("#seq") != -1)
					{
						line = line.replace("#seq", String.valueOf(GlobalInfo.syjStatus.fphm));
					}
					if (line.indexOf("#user") != -1)
					{
						line = line.replace("#user", GlobalInfo.posLogin.gh);
					}
					if (line.indexOf("#counter") != -1)
					{
						line = line.replace("#counter", GlobalInfo.syjDef.syjh);
					}
					if (line.indexOf("#saletype") != -1)
					{
						if (req.type.equals("01"))
							line = line.replace("#saletype", "销------售");
						else if (req.type.equals("03"))
							line = line.replace("#saletype", "退------货");
						else
							line = line.replace("#saletype", "未知交易类型");
					}
					if (line.indexOf("#count") != -1)
					{
						line = line.replace("#count", String.valueOf(req.num3));
					}
					if (line.indexOf("#total") != -1)
					{
						line = line.replace("#total", String.valueOf(req.je));
					}
					if (line.indexOf("#amount") != -1)
					{
						line = line.replace("#amount", String.valueOf(req.je));
					}
					if (line.indexOf("#cardno") != -1)
					{
						line = line.replace("#cardno", ret.cardno);
					}
					if (line.indexOf("#leave") != -1)
					{
						line = line.replace("#leave", String.valueOf(ret.ye));
					}

					if (line.indexOf("#score") != -1)
					{
						line = line.replace("#score", "");
					}
					if (line.indexOf("#date") != -1)
					{
						line = line.replace("#date", ManipulateDateTime.getCurrentDate());
					}
					if (line.indexOf("#time") != -1)
					{
						line = line.replace("#time", ManipulateDateTime.getCurrentTime());
					}
					billVec.set(i, line);
				}

				pw = CommonMethod.writeFileUTF(prnBill);

				if (pw != null)
				{
					for (int i = 0; i < billVec.size(); i++)
						pw.println((String) billVec.get(i));

					pw.flush();
				}
			}
			else
			{
				StringBuffer sb = new StringBuffer();

				sb.append("欢迎光临世纪联华" + GlobalInfo.sysPara.mktname + "!\n");// "\n");
				sb.append("世纪联华官方旗舰店敬待您的访问,让您足不" + "\n");
				sb.append("出户,轻松购物!欢迎点击:http://lhgw.tmall.com. 留存联\n");
				sb.append("流水:" + GlobalInfo.syjStatus.fphm + "    工号:" + GlobalInfo.posLogin.gh + "     机号:" + GlobalInfo.syjDef.syjh + "\n");

				if (req.type.equals("01"))
					sb.append("--------------销------售----------------" + "\n");
				else if (req.type.equals("03"))
					sb.append("--------------退------货----------------" + "\n");
				else
					sb.append("--------------未知交易类型--------------" + "\n");

				sb.append("货   号      品名          数量金额(元)" + "\n");
				sb.append("----------------------------------------" + "\n");

				sb.append("合计:" + req.je + "     " + req.num3 + "     " + req.je + "\n");
				sb.append("会员电子消费卡扣款:" + req.je + "\n");
				sb.append("卡号:" + ret.cardno + "    " + "余额:" + ret.ye + "\n");
				sb.append("积分卡号:" + ret.cardno + "本次积分:" + "\n");
				sb.append("			" + ManipulateDateTime.getCurrentDate() + "    " + ManipulateDateTime.getCurrentTime() + "\n");
				sb.append("	谢谢惠顾,欢迎再次光临!" + "\n");
				sb.append("请仔细核对小票，如有质量问题请于一周内联系调换。" + "\n");
				sb.append("庆春店电话:87220255,联华总部咨询服务电话:4008571996" + "\n");
				sb.append("购物之日起一个月内至购物门店开具发票有效。" + "\n");

				if (PathFile.fileExist(prnBill))
					PathFile.deletePath(prnBill);

				pw = CommonMethod.writeFileUTF(prnBill);

				if (pw != null)
				{
					pw.println(sb.toString());
					pw.flush();
				}
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
				catch (IOException e)
				{
					e.printStackTrace();
					br = null;
				}
			}
		}
	}

	public static void main(String[] args)
	{
		Hzjb_ICCardCaller caller = new Hzjb_ICCardCaller();
		long ret = caller.start();
		System.out.println(caller.getLastError());
		ret = caller.login("8888");
		System.out.println(caller.getLastError());
		String[] retary = caller.check(0, 0);
		System.out.println(caller.getLastError());
		String[] retpay = caller.pay("888", "2222", "", 10000, 0);
		System.out.println(caller.getLastError());
	}
}
