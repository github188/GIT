package bankpay.Payment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import bankpay.Payment.PlmWxc_PaymentMzk.WxcRequestDef;
import bankpay.Payment.PlmWxc_PaymentMzk.WxcResponseDef;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Payment.PaymentMzkForm;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.SalePayDef;

// 北国的储值卡(怀特商城使用)
public class Ht_PaymentMzk extends PaymentMzk
{
	public SalePayDef inputPay(String money)
	{
		try
		{
			// 退货小票不能使用,退货扣回按销售算
			if (checkMzkIsBackMoney() && GlobalInfo.sysPara.thmzk != 'Y')
			{
//				new MessageBox("退货时不能使用" + paymode.name);
				new MessageBox(Language.apply("退货时不能使用{0}" ,new Object[]{paymode.name}));
				return null;
			}

			// 先检查是否有冲正未发送
//			if (!sendAccountCz())
//				return null;

			// 打开明细输入窗口
			new PaymentMzkForm().open(this, saleBS);

			// 如果付款成功,则salepay已在窗口中生成
			return salepay;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return null;
	}

	public boolean mzkAccount(boolean isAccount)
	{
		do
		{
			// 退货交易卡号为空时提示刷卡
			paynoMsrflag = false;
			if (!paynoMSR())
				return false;

			// 设置交易类型,isAccount=true是记账,false是撤销
			if (isAccount)
			{
				if (SellType.SELLSIGN(salehead.djlb) > 0)
					mzkreq.type = "01"; // 消费,减
				else
					mzkreq.type = "03"; // 退货,加
			}
			else
			{
				if (SellType.SELLSIGN(salehead.djlb) > 0)
					mzkreq.type = "03"; // 退货,加
				else
					mzkreq.type = "01"; // 消费,减
			}

			// 保存交易数据进行交易
			if (!setRequestDataByAccount())
			{
				if (paynoMsrflag)
				{
					salepay.payno = "";
					continue;
				}
				return false;
			}

			// 发送交易请求
			if (!sendMzkSale(mzkreq, mzkret))
			{
				if (paynoMsrflag)
				{
					salepay.payno = "";
					continue;
				}
				return false;
			}

			// 先写冲正文件
			if (!writeMzkCz())
			{
				if (paynoMsrflag)
				{
					salepay.payno = "";
					continue;
				}
				return false;
			}
			
			// 记录面值卡交易日志
			BankLogDef bld = mzkAccountLog(false, null, mzkreq, mzkret);
			
			// 记录应答信息, batch标记本付款方式已记账,这很重要
			saveAccountMzkResultToSalePay();

			// 记账完成操作,可用于记录记账日志或其他操作
			return mzkAccountFinish(isAccount, bld);
		} while (true);
	}
	
	public boolean findMzkInfo(String track1, String track2, String track3)
	{
		if ((track1 == null || track1.trim().length() <= 0) && (track2 == null || track2.trim().length() <= 0) && (track3 == null || track3.trim().length() <= 0))
		{
			new MessageBox(Language.apply("磁道数据为空!"));
			return false;
		}

		// 解析磁道
		String[] s = parseTrack(track1, track2, track3);
		if (s == null)
			return false;
		track1 = s[0];
		track2 = s[1];
		track3 = s[2];

		// 设置请求数据
		setRequestDataByFind(track1, track2, track3);

		// 设置用户输入密码
		StringBuffer passwd = new StringBuffer();
		if (!getPasswdBeforeFindMzk(passwd))
		{
			return false;
		}
		else
		{
			mzkreq.passwd = passwd.toString();
		}

		//
		return sendMzkSale(mzkreq, mzkret);
	}
	
	
	public boolean sendMzkSale(MzkRequestDef mzkreq, MzkResultDef mzkret)
	{
		
		if (!callJavaPosBank(mzkreq, mzkret))
			return false;
		
		return true;
	}
	
	public boolean callJavaPosBank(MzkRequestDef req, MzkResultDef ret)
	{
		try
		{
			// 先删除上次交易数据文件
			if (PathFile.fileExist("c:\\JavaPOS\\request.txt"))
			{
				PathFile.deletePath("c:\\JavaPOS\\request.txt");

				if (PathFile.fileExist("c:\\JavaPOS\\request.txt"))
				{
					new MessageBox("面值卡交易请求文件request.txt无法删除,请重试");
					return false;
				}
			}

			if (PathFile.fileExist("c:\\JavaPOS\\result.txt"))
			{
				PathFile.deletePath("c:\\JavaPOS\\result.txt");

				if (PathFile.fileExist("c:\\JavaPOS\\result.txt"))
				{
					new MessageBox("面值卡交易请求文件result.txt无法删除,请重试");
					return false;
				}
			}
		
			if (!writeRequst(req)) return false;
			// 调用接口模块
			if (PathFile.fileExist("c:\\JavaPOS\\javaposbank.exe"))
			{
				CommonMethod.waitForExec("c:\\JavaPOS\\javaposbank.exe HtTran");
			}
//			if (PathFile.fileExist("D:\\Code_Java\\tfsworkspace\\JavaPos\\javaposbank.exe"))
//			{
//				CommonMethod.waitForExec("D:\\Code_Java\\tfsworkspace\\JavaPos\\javaposbank.exe HtTran");
//			}
			else
			{
				new MessageBox("找不到模块 javaposbank.exe");
				return false;
			}
			
			if (!readResult(ret)) return false;
			
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			if (!ConfigClass.DebugMode)
			{
				if (PathFile.fileExist("c:\\JavaPOS\\request.txt"))
				{
					PathFile.deletePath("c:\\JavaPOS\\request.txt");

					if (PathFile.fileExist("c:\\JavaPOS\\request.txt"))
					{
						new MessageBox("面值卡交易请求文件request.txt无法删除,请重试");
						return false;
					}
				}

				if (PathFile.fileExist("c:\\JavaPOS\\result.txt"))
				{
					PathFile.deletePath("c:\\JavaPOS\\result.txt");

					if (PathFile.fileExist("c:\\JavaPOS\\result.txt"))
					{
						new MessageBox("面值卡交易请求文件result.txt无法删除,请重试");
						return false;
					}
				}
			}
		}
	}
	
	
	private boolean writeRequst(MzkRequestDef req)
	{
		
		String line = "";
		String mzkno = req.track2;
		String steelno = req.passwd;
		String mkt = req.mktcode;
		String syjh = req.syjh;
		String syyh = req.syyh;
		String payno = req.paycode;
		ManipulateDateTime mdt = new ManipulateDateTime();
		String rqsj = mdt.getDateTimeByEmpty();
		String xfseq = req.fphm + "";
		String seq  = "";
		if (req.type.equals("03") && salehead != null && null != salehead.djlb && SellType.ISBACK(salehead.djlb))
		{
			StringBuffer sb = new StringBuffer();
			TextBox txt = new TextBox();
			if (!txt.open("消费序号", "", "请输入消费序号", sb, 3)) { return false; }
			seq = sb.toString();
		}
		else
			seq = xfseq;
		
		String je = req.je + "";
		try{	
			if (req.type.equals("05"))
			{
//				 输入钢印号
//				StringBuffer sb = new StringBuffer();
//				TextBox txt = new TextBox();
//				if (!txt.open("钢印号", "", "请输入钢印号", sb, 3)) { return false; }
//				req.str2 = sb.toString();
//				steelno = req.str2;
				
				line = "05," +  mzkno + "," + steelno + "," + mkt + "," + syjh + "," + syyh + "," + payno;
			}
			else if (req.type.equals("01"))
			{
				line = "01," +  mzkno + "," + steelno + "," + mkt + "," + syjh + "," + syyh + "," + payno + "," + rqsj + "," + xfseq + "," + seq + "," + je + "," + "O";
			}
			else if (req.type.equals("03"))
			{
				line = "03," +  mzkno + "," + steelno + "," + mkt + "," + syjh + "," + syyh + "," + payno + "," + rqsj + "," + xfseq + "," + seq + "," +je + "," + "P";
			}
			else
			{
				new MessageBox("不支持该交易类型");
				return false;
			}
			PrintWriter pw = null;
			try
			{
				pw = CommonMethod.writeFile("c:\\JavaPOS\\request.txt");
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
			
			return true;
	}
	catch(Exception e)
	{
		e.printStackTrace();
		new MessageBox("写入金卡工程数据异常!!!\n" + e.getMessage(), null, false);
		
		return false;
	}
	}
	
	private boolean readResult(MzkResultDef ret)
	{
		BufferedReader br = null;
		try{
			
				if (!PathFile.fileExist("c:\\JavaPOS\\result.txt") || ((br = CommonMethod.readFileGBK("c:\\JavaPOS\\result.txt")) == null))
				{
					new MessageBox("读取面值卡应答数据失败!", null, false);
					return false;
				}
		
				String line = br.readLine().trim();
		
				if (line == null || line.length() <= 0)
				{
					new MessageBox("未读取到面值卡应答数据!");
					return false;
				}
				
				String str[] = line.split(",");

				if (str[0].equals("05"))
				{
					//先检查是否正确
					if (!str[1].equals("1"))
					{
						new MessageBox(str[2].trim());
						
						return false;
					}
					
					ret.cardno = mzkreq.track2;
					ret.cardname = "无";
					ret.status = "Y";
					ret.ye = Double.parseDouble(str[4].trim());
					ret.money = Double.parseDouble(str[5].trim());
				}
				else if (str[0].equals("01") || str[0].equals("03"))
				{
					//先检查是否正确
					if (!str[1].equals("1"))
					{
						new MessageBox(str[4].trim());
						
						return false;
					}
					ret.cardno = str[2].trim();
					ret.str3 = str[3];
					mzkreq.seqno = Long.parseLong(str[3].trim());
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
				try
				{
					if (br != null)
						br.close();
	
					br = null;
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
	}
}
