package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

/**
 * 金华一百储值卡
 * @author tj
 *
 */
public class JhybCzk_PaymentBankFunc extends PaymentBankFunc
{
	protected String bankpath = ConfigClass.BankPath;
	protected double zje = 0;
	protected Vector cardnov = new Vector();
	protected String data = "";
	public String[] getFuncItem()
	{
		String[] func = new String[2];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
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

			case PaymentBank.XYKYE: //消费撤销
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = null;

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

			case PaymentBank.XYKYE: //消费撤销
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "回车查询余额";

				break;

		}

		return true;
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			
			// 调用接口模块
			if (PathFile.fileExist(bankpath + "\\javaposbank.exe"))
			{
				if (PathFile.fileExist(bankpath + "\\PRINT.TXT"))
				{
					PathFile.deletePath(bankpath + "\\PRINT.TXT");

					if (PathFile.fileExist(bankpath + "\\PRINT.TXT"))
					{
						errmsg = "交易请求文件PRINT.txt无法删除,请重试";
						XYKSetError("XX", errmsg);
						new MessageBox(errmsg);
						return false;
					}
				}

				if (PathFile.fileExist(bankpath + "\\request.TXT"))
				{
					PathFile.deletePath(bankpath + "\\request.TXT");

					if (PathFile.fileExist(bankpath + "\\request.TXT"))
					{
						errmsg = "交易请求文件request.TXT无法删除,请重试";
						XYKSetError("XX", errmsg);
						new MessageBox(errmsg);
						return false;
					}
				}

				if (PathFile.fileExist(bankpath + "\\JCRequest.TXT"))
				{
					PathFile.deletePath(bankpath + "\\JCRequest.TXT");

					if (PathFile.fileExist(bankpath + "\\JCRequest.TXT"))
					{
						errmsg = "交易请求文件JCRequest.TXT无法删除,请重试";
						XYKSetError("XX", errmsg);
						new MessageBox(errmsg);
						return false;
					}
				}

				if (PathFile.fileExist(bankpath + "\\result.TXT"))
				{
					PathFile.deletePath(bankpath + "\\result.TXT");

					if (PathFile.fileExist(bankpath + "\\result.TXT"))
					{
						errmsg = "交易请求文件result.TXT无法删除,请重试";
						XYKSetError("XX", errmsg);
						new MessageBox(errmsg);
						return false;
					}
				}

				if (PathFile.fileExist(bankpath + "\\JCResponse.txt"))
				{
					PathFile.deletePath(bankpath + "\\JCResponse.txt");

					if (PathFile.fileExist(bankpath + "\\JCResponse.txt"))
					{
						errmsg = "交易应答文件JCResponse.txt无法删除,请重试";
						XYKSetError("XX", errmsg);
						new MessageBox(errmsg);
						return false;
					}
				}
				
				String line = "";
				String type1 = "";
				String syjh = Convert.increaseCharForward(ConfigClass.CashRegisterCode, '0', 10);
				String syyh = Convert.increaseCharForward(GlobalInfo.posLogin.gh, '0', 10);

				String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1));
				jestr = Convert.increaseCharForward(jestr, '0', 12);
				String date = ManipulateDateTime.getDateTimeByClock();
				//根据不同的类型生成文本结构
				//消费-00,预授权-30,离线-10,确认-05,查询余额-31,撤消-02,退货-20,结算-92
				//[重打票据-84]该交易可根据需要进行调整 
				switch (type)
				{
					case PaymentBank.XYKXF:
						//Pos机号(10字符必须是数字字符)+’|’
						//+员工号(10字符必须是数字字符)+’|’
						//+交易流水(20字符)+’|’
						//+日期时间(格式YYYY-MM-DD HH:NN:SS 19个字符)+’|’
						//+金额(12个字符精确到分不足前补0) 共75个字符一行
						type1 = "S";
						line = syjh + "|" + syyh + "|" + Convert.increaseCharForward(String.valueOf(GlobalInfo.syjStatus.fphm), '0', 20) + "|"
								+ date.substring(0, 19) + "|" + jestr;
						break;
					case PaymentBank.XYKYE:
						type1 = "Q";
						break;
					default:
						return false;
				}

				PrintWriter pw = null;
				PrintWriter pw1 = null;
				try
				{
					pw = CommonMethod.writeFile(bankpath + "\\request.txt");

					if (pw != null)
					{
						pw.print(type1);
						pw.flush();
					}

					pw1 = CommonMethod.writeFile(bankpath + "\\JCRequest.txt");

					if (pw1 != null)
					{
						pw1.print(line);
						pw1.flush();
					}
				}
				finally
				{
					if (pw != null)
					{
						pw.close();
					}
					if (pw1 != null)
					{
						pw1.close();
					}
				}

					CommonMethod.waitForExec(bankpath + "\\javaposbank.exe JCONLINE ");
			}
			else
			{
				new MessageBox("找不到金卡工程模块 javaposbank.exe");
				XYKSetError("XX", "找不到金卡工程模块 javaposbank.exe");
				return false;
			}

			// 读取应答数据
			if (XYKReadResult1())
			{
				if (!XYKReadResult()) { 
					bld.retbz = 'N';
					errmsg = "result.txt返回信息为失败";
					return false; 
				}
				if(bld.je <=0){ 
					bld.retbz = 'N';
					errmsg = "银联接口返回金额等于0";
					return false; 
				}
			}
			else
			{
				return false;
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
			XYKSetError("XX", "金卡异常XX:" + ex.getMessage());
			new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);

			return false;
		}
	}

	protected boolean XYKNeedPrintDoc()
	{
		// 交易未成功不打印
		if (!checkBankSucceed()) { return false; }
		return true;
	}

	public boolean XYKReadResult()
	{
		BufferedReader br = null;

		/**
		 * 
		 * 第一行：
		 * 返回值（1字符,00正常；其他异常）+’|’
		 * + Pos机号(10字符必须是数字字符)+’|’
		 * +员工号(10字符必须是数字字符)+’|’
		 * +原交易流水(20字符) +日期时间(格式YYYY-MM-DD HH:NN:SS 19个字符)+’|’
		 * +已付金额(12个字符精确到分不足前补0)
		 * 第2行：
		 * 卡号(15字节字符不足右边补空格)+’|’+卡消费序号(6字节字符)+’|’+卡存款序号（6字节字符）+’|’
		 * +扣款额(12个字符)+’|’+余额(12个字符精确到分不足前补0)+’|’+押金回收额(6字节字符精确到分不足前补0)
		 */

		try
		{
			if (!PathFile.fileExist(bankpath + "\\JCResponse.TXT") || ((br = CommonMethod.readFileGBK(bankpath + "\\JCResponse.TXT")) == null))
			{
				XYKSetError("XX", "读取金卡工程应答数据失败!");
				new MessageBox("读取金卡工程应答数据失败!", null, false);

				return false;
			}
			int num = 0;
			String line = "";
			while ((line = br.readLine()) != null)
			{
				num++;
				//				 读取请求数据

				if (line == null || line.trim().equals("")) continue;

				if (num == 1)
				{
					String temp[] = line.split("\\|");
					bld.retcode = temp[0];
					if (bld.retcode.equals("0"))
					{
						bld.retbz = 'Y';
						data = temp[4];
						if (bld.retmsg.trim().length() <= 0) bld.retmsg = "第三方支付处理成功";
						continue;
					}
					else
					{
						bld.retbz = 'N';
						errmsg = bld.retmsg;
						//WriteResultLog();
						return false;
					}

				}
				else
				{
					/** 第2行：
					 * 卡号(15字节字符不足右边补空格)+’|’+卡消费序号(6字节字符)+’|’+卡存款序号（6字节字符）+’|’+扣款额(12个字符)+’|’+余额(12个字符精确到分不足前补0)+’|’+押金回收额(6字节字符精确到分不足前补0)
					 */
					if(bld.retbz == 'Y') {
						bld.retcode = "00";
					}else{
						return false;
					}
					//bld.retbz = 'Y';
					//bld.rowcode = bld.rowcode +1;
					String temp[] = line.split("\\|");
					
					if(num==2)bld.cardno = temp[0];
					else bld.cardno = bld.cardno+","+temp[0];
					//bld.trace =  temp[1];
					bld.memo = temp[2];
					String je = temp[3];
					double j = Double.parseDouble(je);
					j = ManipulatePrecision.mul(j, 0.01);
					bld.je = j;
					
					zje = zje+j;
					
					String ye = temp[4];
					double y = Double.parseDouble(ye);
					y = ManipulatePrecision.mul(y, 0.01);
					bld.kye = y;

					String yj = temp[5];
					double ya = Double.parseDouble(yj);
					ya = ManipulatePrecision.mul(ya, 0.01);
					bld.memo1 = String.valueOf(ya);
					cardnov.add(new String[]{temp[0],String.valueOf(bld.je),String.valueOf(bld.kye)});
				}
				errmsg = bld.retmsg;
			}
			//bld.cardno = bld.cardno.trim().substring(1,bld.cardno.length()-1);
			bld.je = zje;
			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);
			XYKSetError("XX", "读取应答XX:" + ex.getMessage());
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
				}
				catch (IOException e)
				{
					// TODO 自动生成 catch 块
					new MessageBox("PFACE.TXT 关闭失败\n重试后如果仍然失败，请联系信息部");
					e.printStackTrace();
				}
			}
		}
	}

	public boolean XYKReadResult1()
	{
		BufferedReader br = null;

		try
		{
			if (!PathFile.fileExist(bankpath + "\\result.TXT") || ((br = CommonMethod.readFileGBK(bankpath + "\\result.TXT")) == null))
			{
				XYKSetError("XX", "读取金卡工程应答数据失败!");
				new MessageBox("读取金卡工程应答数据失败!", null, false);

				return false;
			}
			String line = "";
			while ((line = br.readLine()) != null)
			{

				//				 读取请求数据

				if (line == null || line.trim().equals("")) break;

				bld.retcode = line.trim();
				if (bld.retcode.trim().equals("0"))
				{
					bld.retbz = 'Y';
					if (bld.retmsg.trim().length() <= 0) bld.retmsg = "第三方支付处理成功";
					WriteResultLog();
					return true;
				}
				else
				{
					bld.retbz = 'N';
					bld.retmsg = "交易失败，返回码:"+bld.retcode;
					errmsg = bld.retmsg;
					WriteResultLog();
					return false;
				}
			}
			return false;
		}
		catch (Exception ex)
		{
			new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);
			XYKSetError("XX", "读取应答XX:" + ex.getMessage());
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
				}
				catch (IOException e)
				{
					// TODO 自动生成 catch 块
					new MessageBox("PFACE.TXT 关闭失败\n重试后如果仍然失败，请联系信息部");
					e.printStackTrace();
				}
			}
		}
	}

	public void XYKPrintDoc()
	{
		ProgressBox pb = null;
		//new MessageBox("开始打印");
		try
		{
						
			for(int c=0;c<2;c++){
				Printer.getDefault().printLine_Normal("             储值卡联        \n");
				
				Printer.getDefault().printLine_Normal("门店号:" + GlobalInfo.sysPara.mktcode + "    交易时间:" + data+ "\n");
				Printer.getDefault().printLine_Normal("交易号:" + GlobalInfo.syjStatus.fphm + "     收银机号:" + GlobalInfo.syjDef.syjh + "\n");
				Printer.getDefault().printLine_Normal("收银员号:" + GlobalInfo.posLogin.gh +"     交易类型:消费"+ "\n");
				
				Printer.getDefault().printLine_Normal("--------------------------------------\n");
				Printer.getDefault().printLine_Normal("本次共"+cardnov.size()+"张\n");
				Printer.getDefault().printLine_Normal("合计金额"+zje+"元\n");
				Printer.getDefault().printLine_Normal("--------------------------------------\n");
				Printer.getDefault().printLine_Normal("卡号                消费金额   余额");
				for(int v =0;v<cardnov.size();v++){
					String a[] = (String[]) cardnov.elementAt(v);
					Printer.getDefault().printLine_Normal(a[0]+"    "+a[1]+"   "+a[2]+"\n");
				}
				Printer.getDefault().printLine_Normal("\n");
				Printer.getDefault().printLine_Normal("\n");
				Printer.getDefault().printLine_Normal("\n");
				Printer.getDefault().printLine_Normal("\n");
				Printer.getDefault().printLine_Normal("\n");
				Printer.getDefault().printLine_Normal("\n");
//				 切纸
				Printer.getDefault().cutPaper_Normal();
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
