package bankpay.Bank;

import java.io.BufferedReader;
import java.io.File;
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
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.WString;

//岱山日达广场（工商银行）
public class GsyhDsrdGc_PaymentBankFunc extends PaymentBankFunc
{

	public String[] getFuncItem()
	{
		String[] func = new String[7];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[3] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
		func[4] = "[" + PaymentBank.XYKJZ + "]" + "交易结账";
		func[5] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
		func[6] = "[" + PaymentBank.XYKCD + "]" + "重打签购单";
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
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";

				break;

			case PaymentBank.XYKCX: //消费撤销
				grpLabelStr[0] = "原检索参考号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";

				break;

			case PaymentBank.XYKYE: //余额查询    
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "余额查询";

				break;

			case PaymentBank.XYKTH: //隔日退货   
				grpLabelStr[0] = "原检索参考号";
				grpLabelStr[1] = "原交易终端号";
				grpLabelStr[2] = "原交易日期";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";

				break;

			case PaymentBank.XYKQD: //签到
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易签到";
				break;
			case PaymentBank.XYKCD: //签到
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "签购单重打";
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
			case PaymentBank.XYKCD: //交易签到
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
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && (type != PaymentBank.XYKTH) && (type != PaymentBank.XYKQD)
					&& (type != PaymentBank.XYKYE)&& (type != PaymentBank.XYKCD))
			{
				errmsg = "银联接口不支持该交易";
				new MessageBox(errmsg);

				return false;
			}
			
			
			// 写入请求数据
//			if(type != PaymentBank.XYKCD){
//				 先删除上次交易数据文件
				if (PathFile.fileExist("C:\\JavaPos\\Print.txt"))
				{
					PathFile.deletePath("C:\\JavaPos\\Print.txt");

					if (PathFile.fileExist("C:\\JavaPos\\Print.txt"))
					{
						errmsg = "交易打印文件Print.txt无法删除,请重试";
						XYKSetError("XX", errmsg);
						new MessageBox(errmsg);
						return false;
					}
				}
				XYKgetRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo);
//				 检查交易是否成功
				XYKCheckRetCode();
//			}

			if (XYKNeedPrintDoc())
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

	public boolean XYKNeedPrintDoc()
	{
		if (!checkBankSucceed()) { return false; }

		int type = Integer.parseInt(bld.type.trim());

		// 消费，消费撤销，重打签购单
		if ((type == PaymentBank.XYKXF) || (type == PaymentBank.XYKCX) || (type == PaymentBank.XYKTH) || (type == PaymentBank.XYKCD)
				|| (type == PaymentBank.XYKJZ))
		{
			return true;
		}
		else
		{
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

	public boolean checkBankSucceed()
	{
		if (bld.retbz == 'N')
		{
			//errmsg = bld.retmsg;

			return false;
		}
		else
		{
			errmsg = "交易成功";

			//加入解百特殊金卡工程断电保护
			String path = "c:\\gmc\\answer.txt";
			if (PathFile.fileExist(path))
			{
				PathFile.deletePath(path);
				if (PathFile.fileExist(path))
				{
					new MessageBox(path + "已经被其他程序锁住，请联系电脑部解决");
				}
			}

			return true;
		}
	}

	public String XYKgetRequest(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			String line = "";
			String type1 = "";
			String typename = "";
			String jestr = "AMT1=" + String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1));
			//jestr = Convert.increaseCharForward(jestr, '0', 12);

			String yjsh = "I1=" + oldseqno;
			String oldrq = "I2=" + olddate;
			String oldjsh = "I1=" + oldseqno;
			String oldzdh = "I3=" + oldauthno;
			//根据不同的类型生成文本结构
			switch (type)
			{
				case PaymentBank.XYKXF:
					type1 = "1001";
					typename = "消费";
					line = jestr;
					break;

				case PaymentBank.XYKCX:
					type1 = "1101";
					typename = "撤销";
					line = jestr + "," + yjsh;
					break;
				case PaymentBank.XYKTH:
					type1 = "1102";
					typename = "退货";
					line = jestr + "," +  oldjsh + "," + oldrq + "," +oldzdh;
					break;
				case PaymentBank.XYKYE:
					type1 = "2002";
					break;

				case PaymentBank.XYKQD:
					type1 = "4001";
					break;
					
				case PaymentBank.XYKCD:
					type1 = "4005";
					break;

			}
			/**
			 * 第1－2位 ，返回码，2bytes，00：成功，其他：失败（详细对照关系请看附件）
			 若返回码为其他，后续内容为错误信息，不定长；
			 若返回码为00，后续内容为：
			 第3－21位，卡号，19bytes ，右补空格
			 第22－29位，交易日期，8bytes，yyyymmdd，左补0
			 第30－35位，交易时间，6bytes，hhmiss，左补0
			 第36－41位，终端流水号，6bytes，左补0；
			 第42－47位，批次号，6bytes，左补0；
			 第48－53位，清算日期，6bytes，yymmdd，左补0；
			 第54－61位，检索参考号，8bytes，左补0；
			 第62－65位，卡片有效期，4bytes，左补0；
			 第66－77位，交易金额/积分，12bytes，左补0，单位：分
			 第78－92位，终端编号，15bytes，左补0；
			 第93－98位，授权号，6bytes，左补0；
			 第99－100位，分期期数，2bytes，左补0，仅分期付款交易时有效；
			 第101－120位，发卡行名称，20bytes，
			 第121－140位，卡片类型，20bytes，银联卡-"CUP" VISA外卡-"VISA"; MasterCard外卡-"MASTER"; 运通外卡-"AMEX"; JCB外卡-"JCB"; 大莱外卡-"DINERS"
			 第141－**位，备注信息（例如分期付款交易的分期信息等等），不定长。
			 */
			 
			System.out.println("类型："+type1+"  内容："+line);
			PosLog.getLog(this.getClass().getSimpleName()).info("Request:" +"类型："+type1+"  内容："+line);
			String result = sendData(type1, line).trim();
			System.out.println("返回："+result);
			PosLog.getLog(this.getClass().getSimpleName()).info("Result:" +"返回："+result);
			if (result != null && result.trim().length() > 2)
			{
				bld.retcode = result.substring(0, 2);
				if (bld.retcode.equals("00"))
				{					
					if(result.trim().length()>21)bld.cardno = result.substring(2, 21).trim();

					if(result.trim().length()>41)bld.trace = Long.parseLong(result.substring(35, 41).trim());

					if(result.trim().length()>77){
						String je = result.substring(65, 77).trim();
						double j = Double.parseDouble(je);
						j = ManipulatePrecision.mul(j, 0.01);
						bld.je = j;
					}
					if(result.trim().length()>92) bld.memo = result.substring(77, 92);
					if(result.trim().length()>120)bld.bankinfo = result.substring(101, 115);
					/**
					 * 第1－2位 ，返回码，2bytes，00：成功，其他：失败（详细对照关系请看附件）
					 若返回码为其他，后续内容为错误信息，不定长；
					 若返回码为00，后续内容为：
					 第3－21位，卡号，19bytes ，右补空格
					 第22－29位，交易日期，8bytes，yyyymmdd，左补0
					 第30－35位，交易时间，6bytes，hhmiss，左补0
					 第36－41位，终端流水号，6bytes，左补0；
					 第42－47位，批次号，6bytes，左补0；
					 第48－53位，清算日期，6bytes，yymmdd，左补0；
					 第54－61位，检索参考号，8bytes，左补0；
					 第62－65位，卡片有效期，4bytes，左补0；
					 第66－77位，交易金额/积分，12bytes，左补0，单位：分
					 第78－92位，终端编号，15bytes，左补0；
					 第93－98位，授权号，6bytes，左补0；
					 第99－100位，分期期数，2bytes，左补0，仅分期付款交易时有效；
					 第101－120位，发卡行名称，20bytes，
					 第121－140位，卡片类型，20bytes，银联卡-"CUP" VISA外卡-"VISA"; MasterCard外卡-"MASTER"; 运通外卡-"AMEX"; JCB外卡-"JCB"; 大莱外卡-"DINERS"
					 第141－**位，备注信息（例如分期付款交易的分期信息等等），不定长。
					 */
					if((type == PaymentBank.XYKXF) || (type == PaymentBank.XYKCX) || (type == PaymentBank.XYKTH) || (type == PaymentBank.XYKJZ)){
						PrintWriter pw = null;
						String printInfo = "";
						printInfo = printInfo + Convert.appendStringSize("", "", 0, 38, 38) + "\n";

						printInfo = printInfo + Convert.appendStringSize("", "POS交易凭证", 0, 38, 38) + "\n";
						printInfo = printInfo + Convert.appendStringSize("", Convert.increaseChar("", ' ', 36), 0, 38, 38) + "\n";
						printInfo = printInfo + Convert.appendStringSize("", "商 户: " + GlobalInfo.sysPara.mktname, 0, 38, 38) + "\n";
						printInfo = printInfo + Convert.appendStringSize("", "终端编号: " + result.substring(77, 92).trim(), 0, 38, 38) + "\n";
						printInfo = printInfo + Convert.appendStringSize("", "收银员号: " + GlobalInfo.posLogin.gh, 0, 38, 38) + "\n";
						printInfo = printInfo + Convert.appendStringSize("", Convert.increaseChar("", '-', 36), 0, 38, 38) + "\n";
						printInfo = printInfo + Convert.appendStringSize("", "卡号: " + result.substring(2, 21).trim(), 0, 38, 38) + "\n";
						printInfo = printInfo + Convert.appendStringSize("", "有效期: " + result.substring(61, 65).trim(), 0, 38, 38) + "\n";
						printInfo = printInfo
								+ Convert.appendStringSize("", "交易时间: " + result.substring(21, 29).trim() + " " + result.substring(29, 35).trim(), 0, 38,
															38) + "\n";
						printInfo = printInfo + Convert.appendStringSize("", "流水号: " + result.substring(35, 41).trim(), 0, 38, 38) + "\n";
						printInfo = printInfo + Convert.appendStringSize("", "检索参考号: " + result.substring(53, 61).trim(), 0, 38, 38) + "\n";
						printInfo = printInfo + Convert.appendStringSize("", "授权号: " + result.substring(92, 98).trim(), 0, 38, 38) + "\n";
						printInfo = printInfo + Convert.appendStringSize("", "批次号: " + result.substring(41, 47).trim(), 0, 38, 38) + "\n";

						printInfo = printInfo + Convert.appendStringSize("", "交易类型: " + typename, 0, 38, 38) + "\n";
						printInfo = printInfo + Convert.appendStringSize("", "交易金额: RMB " + bld.je, 0, 38, 38) + "\n";
						printInfo = printInfo + Convert.appendStringSize("", Convert.increaseChar("", '-', 36), 0, 38, 38) + "\n";
						printInfo = printInfo + Convert.appendStringSize("", "持卡人签字", 0, 38, 38) + "\n\n\n\n";
						printInfo = printInfo + Convert.appendStringSize("", Convert.increaseChar("", '-', 36), 0, 38, 38) + "\n";
						printInfo = printInfo + Convert.appendStringSize("", "本人接受单据金额及有关商品并愿意", 0, 38, 38) + "\n";
						printInfo = printInfo + Convert.appendStringSize("", "遵守发卡行的持卡人和约内一切条款", 0, 38, 38) + "\n";
						printInfo = printInfo + Convert.appendStringSize("", "", 0, 38, 38) + "\n";

						try
						{
							pw = CommonMethod.writeFile("C:\\JavaPos\\Print.txt");

							if (pw != null)
							{
								pw.println(printInfo);
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
					}
				}
				else
				{
					bld.retmsg = result;
				}
			}

			return line;
		}
		catch (Exception ex)
		{
			new MessageBox("写入金卡工程请求数据异常!\n\n" + ex.getMessage(), null, false);
			ex.printStackTrace();

			return null;
		}
	}

	public interface MagRO extends Library
	{

		/**

		 * 当前路径是在项目下，而不是bin输出目录下。

		 */
		MagRO INSTANCE = (MagRO) Native.loadLibrary("MposCore", MagRO.class);

		public WString DoICBCZJMisTranSTD(String value, String value1, String value2, String value3, String value4);

	}

	private String sendData(String type1, String line)
	{

		String i1 = null;
		try
		{
			WString i = MagRO.INSTANCE.DoICBCZJMisTranSTD(type1, line, "", "", "");

			new MessageBox(i.toString());
			i1 = new String(i.toString());
		
		}
		catch(Exception ex)
		{
			new MessageBox(ex.getMessage())	;
		}
		return i1;
	}

	public void XYKPrintDoc(int type)
	{
		ProgressBox pb = null;
		if (GlobalInfo.sysPara.bankprint < 1) return;

		try
		{
			String printName = "C:\\JavaPos\\Print.txt";
			if (!PathFile.fileExist(printName)) { return; }

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

					//
					String line = null;
					if(type == PaymentBank.XYKCD){
						XYKPrintDoc_Print(Convert.appendStringSize("", "重打印" , 0, 38, 38,2)+"\n");
					}
					while ((line = br.readLine()) != null)
					{
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
					{
						br.close();
					}
				}

				XYKPrintDoc_End();

			}

			//PathFile.deletePath(printName);
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

	public void XYKPrintDoc_Print(String printStr)
	{
		if (onceprint)
		{
			Printer.getDefault().printLine_Normal(printStr);
		}
		else
		{
			printdoc.println(printStr);
		}
	}

	public void XYKPrintDoc_End()
	{
		if (onceprint)
		{
			Printer.getDefault().cutPaper_Normal();
		}
		else
		{
			printdoc.flush();
			printdoc.close();
			printdoc = null;
		}
	}

	public String XYKReadBankName(String bankid)
	{
		String line = "";

		try
		{
			if (bankid.charAt(0) == '4') { return "商行"; }

			if (!PathFile.fileExist(GlobalVar.ConfigPath + File.separator + "BankInfo.ini")
					|| !rtf.loadFile(GlobalVar.ConfigPath + File.separator + "BankInfo.ini"))
			{
				new MessageBox("找不到BankInfo.ini", null, false);

				return bankid;
			}

			//
			while ((line = rtf.nextRecord()) != null)
			{
				if (line.length() <= 0)
				{
					continue;
				}

				String[] a = line.split("=");

				if (a.length < 2)
				{
					continue;
				}

				if (Convert.toInt(a[0]) == Convert.toInt(bankid.trim())) { return a[1].trim(); }
			}

			rtf.close();

			return "未知银行";
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return bankid;
		}
	}
}
