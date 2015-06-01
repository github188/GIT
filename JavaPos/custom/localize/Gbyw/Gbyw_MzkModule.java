package custom.localize.Gbyw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;

//先扣电子红包，再扣积分返利，最后扣储值余额

public class Gbyw_MzkModule
{
	private String path = "c:\\javapos";
	private String cardno;
	private String track2;
	private String track3;
	private String password;
	private RetInfoDef constRetinfo;

	public static Gbyw_MzkModule module = new Gbyw_MzkModule();

	public static Gbyw_MzkModule getDefault()
	{
		return module;
	}

	static class RetInfoDef
	{
		public static int LENGTH = 295;

		public String retcode; // 返回码2
		public double tradeAmount; // 交易金额12
		public double realAmount;// 实扣金额12
		public String tradeCardno;// 交易卡号19
		public String checkId;// 检索号12
		public String merId;// 商户号15
		public String termId;// 终端号8
		public String date;// 交易日期8
		public String time;// 交易时间8
		public String tr2;// 二磁道37
		public String tr3;// 三磁道104
		public String pwd;// 密码8
		public double ye;// 余额13
		public double scoreYe;// 积分余额13
		public double elecbagYe;// 电子红包余额13
		public double scoreRebateYe;// 积分返利余额13

		public static RetInfoDef parse(String retinfo)
		{
			if (retinfo == null || retinfo.length() == 0)
				return null;

			if (retinfo.length() < LENGTH)
			{
				new MessageBox("返回数据不合法!");
				return null;
			}

			try
			{
				RetInfoDef info = new RetInfoDef();

				info.retcode = retinfo.substring(0, 2);
				info.tradeAmount = Convert.toDouble(retinfo.substring(2, 14)) / 100;
				info.realAmount = Convert.toDouble(retinfo.substring(14, 26)) / 100;
				info.tradeCardno = retinfo.substring(26, 45);
				info.checkId = retinfo.substring(45, 57);
				info.merId = retinfo.substring(57, 72);
				info.termId = retinfo.substring(72, 80);
				info.date = retinfo.substring(80, 88);
				info.time = retinfo.substring(88, 94);
				info.tr2 = retinfo.substring(94, 131);
				info.tr3 = retinfo.substring(131, 235);
				info.pwd = retinfo.substring(235, 243);

				String tmpye = retinfo.substring(243, 256);
				double kye = 0;

				if (tmpye.length() > 0)
				{
					kye = Convert.toDouble(tmpye.substring(1)) / 100;

					if (tmpye.charAt(0) == '0')
					{
						info.ye = ManipulatePrecision.doubleConvert(kye, 2, 1);
					}
					else
					{
						info.ye = ManipulatePrecision.doubleConvert(kye, 2, 1) * -1;
					}
				}

				tmpye = retinfo.substring(256, 269);
				if (tmpye.length() > 0)
				{
					kye = Convert.toDouble(tmpye.substring(1));
					if (tmpye.charAt(0) == '0')
					{
						info.scoreYe = ManipulatePrecision.doubleConvert(kye, 2, 1);
					}
					else
					{
						info.scoreYe = ManipulatePrecision.doubleConvert(kye, 2, 1) * -1;
					}
				}

				tmpye = retinfo.substring(269, 282);
				if (tmpye.length() > 0)
				{
					kye = Convert.toDouble(tmpye.substring(1)) / 100;
					if (tmpye.charAt(0) == '0')
					{
						info.elecbagYe = ManipulatePrecision.doubleConvert(kye, 2, 1);
					}
					else
					{
						info.elecbagYe = ManipulatePrecision.doubleConvert(kye, 2, 1) * -1;
					}
				}

				tmpye = retinfo.substring(282, 295);
				if (tmpye.length() > 0)
				{
					kye = Convert.toDouble(tmpye.substring(1)) / 100;
					if (tmpye.charAt(0) == '0')
					{
						info.scoreRebateYe = ManipulatePrecision.doubleConvert(kye, 2, 1);
					}
					else
					{
						info.scoreRebateYe = ManipulatePrecision.doubleConvert(kye, 2, 1) * -1;
					}
				}

				return info;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				return null;
			}
		}
	}

	public Gbyw_MzkModule()
	{
		initData();
	}

	public void setTrack(String cardno, String track2, String track3)
	{
		this.cardno = cardno;
		this.track2 = track2;
		this.track3 = track3;

	}

	public void setPass(String pass)
	{
		this.password = pass;

	}

	public void initData()
	{
		setTrack("", "", "");
		setPass("");
		constRetinfo = null;
	}

	public boolean getTrack()
	{
		ProgressBox box = new ProgressBox();
		try
		{
			box.setText("请在终端设备刷卡...");

			String cmd = getCmdString("25", 0);
			String retinfo = execute(cmd);

			RetInfoDef info = RetInfoDef.parse(retinfo);

			if (info != null && info.retcode.equals("00"))
			{
				box.setText("请在终端设备输入密码...");

				setTrack(info.tradeCardno, info.tr2, info.tr3);
				setPass("");

				return true;
				/*
				 * info = getPass();
				 * 
				 * if (info != null && info.retcode.equals("00"))
				 * {
				 * setPass(info.pwd);
				 * return true;
				 * }
				 * else
				 * {
				 * new MessageBox("校验密码失败!");
				 * return false;
				 * }
				 */
			}

			new MessageBox("刷卡失败!");
			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			if (box != null)
				box.close();
			box = null;
		}
	}

	public RetInfoDef getPass()
	{
		try
		{
			String cmd = getCmdString("26", 0);
			String retinfo = execute(cmd);

			RetInfoDef info = RetInfoDef.parse(retinfo);

			if (info != null && info.retcode.equals("00"))
				return info;

			return null;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}

	}

	public double getAccountKye(int code)
	{
		switch (code)
		{
		case 1:
			return constRetinfo.elecbagYe;
		case 2:
			return constRetinfo.scoreRebateYe;
		case 3:
			return constRetinfo.ye;
		}

		return 0;
	}

	public void updateAccount(int code, double money)
	{
		switch (code)
		{
		case 1:
			constRetinfo.elecbagYe = ManipulatePrecision.doubleConvert(constRetinfo.elecbagYe - money, 2, 1);
			break;
		case 2:
			constRetinfo.scoreRebateYe = ManipulatePrecision.doubleConvert(constRetinfo.scoreRebateYe - money, 2, 1);
			break;
		case 3:
			constRetinfo.ye = ManipulatePrecision.doubleConvert(constRetinfo.ye - money, 2, 1);
			break;
		}
	}

	public double getAccountMoney(int code, double money)
	{
		switch (code)
		{
		case 1:
			money = Math.min(money, constRetinfo.elecbagYe);
			break;
		case 2:
			money = Math.min(money, constRetinfo.scoreRebateYe);
			break;
		case 3:
			money = Math.min(money, constRetinfo.ye);
			break;
		}

		return money;
	}

	public int useWhichAccount()
	{
		if (constRetinfo == null)
			return 0;

		if (constRetinfo.elecbagYe > 0)
		{
			return 1;
		}
		else if (constRetinfo.scoreRebateYe > 0)
		{
			return 2;
		}
		else if (constRetinfo.ye > 0) { return 3; }

		return 0;
	}

	// 其他交易
	public void exeOtherFunc()
	{
		try
		{
			initData();

			if (track2 == "" && track3 == "")
			{
				if (!getTrack())
					return;
			}

			String cmd = getCmdString("ff", 0);

			execute(cmd);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

	}

	// 查询
	public RetInfoDef cardQuery(boolean flag)
	{
		try
		{
			initData();

			if (track2 == "" || track3 == "")
			{
				if (!getTrack())
					return null;
			}

			String cmd = getCmdString("06", 0);

			String retinfo = execute(cmd);

			RetInfoDef info = RetInfoDef.parse(retinfo);

			if (info != null && info.retcode.equals("00"))
			{
				if (flag)
				{
					constRetinfo = info;
					// cardno = info.tradeCardno;
				}
				return info;
			}

			return null;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public RetInfoDef sendScore(String djlb, double score)
	{
		String cmd = null;
		if (SellType.ISSALE(djlb))
		{
			cmd = getCmdString("11", score);
		}
		else if (SellType.ISBACK(djlb))
		{
			cmd = getCmdString("13", score);
		}

		String retinfo = execute(cmd);

		RetInfoDef info = RetInfoDef.parse(retinfo);
		if (info != null && info.retcode.equals("00"))
			return info;

		new MessageBox("发送累计积分失败!");
		return null;
	}

	// 储值交易

	public RetInfoDef mzkSale(int code, double money)
	{
		try
		{
			String cmd = null;

			switch (code)
			{
			case 1:
				cmd = getCmdString("05", money);
				break;
			case 2:
				cmd = getCmdString("07", money);
				break;
			case 3:
				cmd = getCmdString("08", money);
				break;
			}

			String retinfo = execute(cmd);

			RetInfoDef info = RetInfoDef.parse(retinfo);

			if (info != null && info.retcode.equals("00"))
				return info;

			return null;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}

	}

	// 电子红包交易
	public RetInfoDef elecPagSale(boolean flag, double money)
	{
		try
		{
			String cmd = null;

			if (flag)
				cmd = getCmdString("17", money);
			else
				cmd = getCmdString("18", money);

			String retinfo = execute(cmd);

			RetInfoDef info = RetInfoDef.parse(retinfo);

			if (info != null && info.retcode.equals("00"))
				return info;

			return null;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}

	}

	// 积分返利
	public RetInfoDef scoreRebateSale(int code, double money)
	{
		try
		{
			String cmd = null;

			switch (code)
			{
			case 1:
				cmd = getCmdString("19", money);
				break;
			case 2:
				cmd = getCmdString("20", money);
				break;
			case 3:
				cmd = getCmdString("21", money);
				break;
			}

			String retinfo = execute(cmd);

			RetInfoDef info = RetInfoDef.parse(retinfo);

			if (info != null && info.retcode.equals("00"))
				return info;

			return null;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}

	}

	public RetInfoDef scoreSale(int code, double money)
	{
		try
		{
			String cmd = null;

			switch (code)
			{
			case 1:
				cmd = getCmdString("13", money);
				break;
			case 2:
				cmd = getCmdString("15", money);
				break;
			case 3:
				cmd = getCmdString("16", money);
				break;
			case 4:
				cmd = getCmdString("14", 0);
			}

			String retinfo = execute(cmd);

			RetInfoDef info = RetInfoDef.parse(retinfo);

			if (info != null && info.retcode.equals("00"))
				return info;

			return null;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}

	}

	public boolean isAllowLczc()
	{
		if (constRetinfo == null)
			return false;

		return true;
	}

	// 零钱包
	public RetInfoDef changeSale(boolean flag, double money)
	{
		try
		{
			String cmd = null;

			if (flag)
				cmd = getCmdString("23", money);
			else
				cmd = getCmdString("24", money);

			String retinfo = execute(cmd);

			RetInfoDef info = RetInfoDef.parse(retinfo);

			if (info != null && info.retcode.equals("00"))
				return info;

			return null;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}

	}

	private String getCmdString(String type, double money)
	{
		String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1));
		jestr = Convert.increaseCharForward(jestr, '0', 12);
		String fphm = Convert.increaseChar(String.valueOf(GlobalInfo.syjStatus.fphm), ' ', 16);
		String syyh = Convert.increaseChar(GlobalInfo.syjStatus.syyh, ' ', 8);
		String syjh = Convert.increaseChar(GlobalInfo.syjStatus.syjh, ' ', 8);
		cardno = Convert.increaseChar(cardno, ' ', 19);
		track2 = Convert.increaseChar(track2, ' ', 37);
		track3 = Convert.increaseChar(track3, ' ', 104);
		password = Convert.increaseChar(password, ' ', 8);

		return type + jestr + syyh + syjh + fphm + cardno + track2 + track3 + password;
	}

	private String execute(String cmd, boolean flag)
	{
		BufferedReader br = null;

		try
		{
			if (!PathFile.fileExist(path + "\\result.txt") || ((br = CommonMethod.readFileGBK(path + "\\result.txt")) == null))
			{
				new MessageBox("读取应答数据失败!");
				return null;
			}

			String line = br.readLine();
			System.out.println("response:" + line);

			if (line == null || line.length() <= 0)
				return null;

			String result[] = line.split(",");
			if (result == null)
				return null;

			if (result.length > 0 && result[0].trim().equals("0"))
				return null;

			return result[1];
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	private String execute(String cmd)
	{
		// 先删除上次交易数据文件
		if (PathFile.fileExist(path + "\\request.txt"))
		{
			PathFile.deletePath(path + "\\request.txt");

			if (PathFile.fileExist(path + "\\request.txt"))
			{
				new MessageBox("交易请求文件request.txt无法删除,请重试");
				return null;
			}
		}

		if (PathFile.fileExist(path + "\\result.txt"))
		{
			PathFile.deletePath(path + "\\result.txt");

			if (PathFile.fileExist(path + "\\result.txt"))
			{
				new MessageBox("交易请求文件result.txt无法删除,请重试");
				return null;
			}
		}

		if (PathFile.fileExist(path + "\\toprint.txt"))
			PathFile.deletePath(path + "\\toprint.txt");

		System.out.println("request:" + cmd);

		PrintWriter pw = null;
		try
		{
			pw = CommonMethod.writeFile(path + "\\request.txt");
			if (pw != null)
			{
				pw.println(cmd);
				pw.flush();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("写入请求数据失败!");
			return null;
		}
		finally
		{
			if (pw != null)
				pw.close();

			pw = null;
		}

		BufferedReader br = null;

		try
		{
			// 调用接口模块
			if (PathFile.fileExist(path + "\\javaposbank.exe"))
			{
				CommonMethod.waitForExec(path + "\\javaposbank.exe HYJW");
			}
			else
			{
				new MessageBox("找不到数据处理模块 javaposbank.exe");
				return null;
			}

			if (!PathFile.fileExist(path + "\\result.txt") || ((br = CommonMethod.readFile(path + "\\result.txt", "ISO8859-1")) == null))
			{
				new MessageBox("读取应答数据失败!");
				return null;
			}

			String line = br.readLine();
			System.out.println("response:" + line);

			if (line == null || line.length() <= 0)
				return null;

			String result[] = line.split(",");
			if (result == null)
				return null;

			if (result.length > 0 && result[0].trim().equals("0"))
				return null;

			return result[1];
		}
		catch (Exception ex)
		{
			new MessageBox("读取应答数据异常!" + ex.getMessage());
			ex.printStackTrace();

			return null;
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
				}
				finally
				{
					br = null;
				}
			}
		}

	}

}
