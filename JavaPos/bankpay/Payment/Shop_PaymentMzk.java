package bankpay.Payment;

import java.io.BufferedReader;
import java.io.PrintWriter;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.BankTracker;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Shop_PaymentMzk extends PaymentMzk
{
	public Shop_PaymentMzk()
	{
		super();
	}

	public Shop_PaymentMzk(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public Shop_PaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public boolean findMzkInfo(String track1, String track2, String track3)
	{
		// 从二轨分解出卡号
		String[] s = track2.split("=");

		// 找卡
		if (!super.findMzkInfo(track1, s[0], track3))
			return false;

		// 不用验证磁道的卡直接返回
		if (mzkret.cardpwd != null && mzkret.cardpwd.startsWith("#######"))
		{
			if (mzkret.cardpwd.length() > 7)
				mzkret.cardpwd = mzkret.cardpwd.substring(7);
			return true;
		}

		// 验证磁道信息
		if (s.length >= 2 && s[1].equals(verifyTrack(s[0], mzkret.cardpwd)))
			return true;
		else
		{
			new MessageBox("卡磁道数据不正确!");
			return false;
		}
	}

	// 修改密码
	public boolean changeMzkPass(String track1, String track2, String track3)
	{
		if ((track1 == null || track1.trim().length() <= 0) && (track2 == null || track2.trim().length() <= 0) && (track3 == null || track3.trim().length() <= 0))
		{
			new MessageBox("磁道数据为空!");
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

		TextBox txt = new TextBox();

		if (!txt.open("请输入密码", "PASSWORD", "请输入面值卡原始密码", passwd, 0, 0, false, TextBox.AllInput))
			return false;
		else
			mzkreq.passwd = passwd.toString();

		passwd.delete(0, passwd.toString().length() - 1);
		String tmpPwd = "";

		if (!txt.open("请输入密码", "PASSWORD", "请输入面值卡新密码", passwd, 0, 0, false, TextBox.AllInput))
			return false;
		else
			tmpPwd = passwd.toString();

		passwd.delete(0, passwd.toString().length() - 1);
		if (!txt.open("请再次输入密码", "PASSWORD", "请再次输入面值卡新密码,以便系统进行确认", passwd, 0, 0, false, TextBox.AllInput))
			return false;
		else
		{
			if (!tmpPwd.equals(passwd.toString()))
			{
				new MessageBox("两次输入的密码不一致");
				return false;
			}
			mzkreq.memo = tmpPwd;
		}

		return DataService.getDefault().getMzkInfo(mzkreq, mzkret);
	}

	protected boolean getPasswdBeforeFindMzk(StringBuffer passwd)
	{
		if (!super.getPasswdBeforeFindMzk(passwd))
			return false;

		return inputPass(passwd);
	}

	protected boolean inputPass(StringBuffer passwd)
	{
		if (GlobalInfo.sysPara.defaultmzkpass.length() > 0 && passwd.toString().equals(GlobalInfo.sysPara.defaultmzkpass))
		{
			StringBuffer newpwd = new StringBuffer();
			TextBox txt = new TextBox();
			String tmpPwd = "";

			if (!txt.open("系统需要您更改密码", "PASSWORD", "请输入面值卡新密码", newpwd, 0, 0, false, TextBox.AllInput))
				return false;
			else
				tmpPwd = newpwd.toString();

			newpwd.delete(0, passwd.toString().length() - 1);
			if (!txt.open("请再次输入密码", "PASSWORD", "请再次输入面值卡新密码,以便系统进行确认", newpwd, 0, 0, false, TextBox.AllInput))
				return false;
			else
			{
				if (!tmpPwd.equals(newpwd.toString()))
				{
					new MessageBox("两次输入的密码不一致");
					return false;
				}
				mzkreq.memo = tmpPwd;
			}
		}

		return true;
	}

	public boolean findMzk(String track1, String track2, String track3)
	{
		// 从二轨分解出卡号
		String[] s = track2.split("=");

		// 找卡
		if (!super.findMzk(track1, s[0], track3))
			return false;

		// 不用验证磁道的卡直接返回
		if (mzkret.cardpwd != null && mzkret.cardpwd.startsWith("#######"))
		{
			if (mzkret.cardpwd.length() > 7)
				mzkret.cardpwd = mzkret.cardpwd.substring(7);
			return true;
		}

		// 验证磁道信息
		if (s.length >= 2 && s[1].equals(verifyTrack(s[0], mzkret.cardpwd)))
			return true;
		else
		{
			new MessageBox("卡磁道数据不正确!");
			return false;
		}
	}

	public String verifyTrack(String cardno, String pwd)
	{
		try
		{
			// 调用接口EXE计算验证码
			if (!PathFile.fileExist("javaposbank.exe"))
			{
				new MessageBox("找不到验证程序 javaposbank.exe");
				return null;
			}
			else
			{
				if (PathFile.fileExist("request.txt"))
				{
					PathFile.deletePath("request.txt");

					if (PathFile.fileExist("request.txt"))
					{
						new MessageBox("验证文件request.txt无法删除,请重试");
						return null;
					}
				}

				if (PathFile.fileExist("result.txt"))
				{
					PathFile.deletePath("result.txt");

					if (PathFile.fileExist("result.txt"))
					{
						new MessageBox("验证文件result.txt无法删除,请重试");
						return null;
					}
				}

				// 写入文件
				PrintWriter pw = null;
				try
				{
					pw = CommonMethod.writeFile("request.txt");
					if (pw != null)
					{
						pw.print(cardno + "," + pwd);
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

				// 计算验证码
				CommonMethod.waitForExec("javaposbank.exe SHOP");

				// 读取验证码
				String verify = null;
				BufferedReader br = null;
				try
				{
					br = CommonMethod.readFile("result.txt");
					if (br == null)
					{
						new MessageBox("读取验证码失败!");
						return null;
					}

					while ((verify = br.readLine()) != null)
					{
						if (verify == null || verify.trim().length() <= 0)
							continue;
						else
							break;
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

				// 返回验证码
				return verify;
			}
		}
		catch (Exception ex)
		{
			new MessageBox("调用验证模块异常!\n\n" + ex.getMessage());
			return null;
		}
	}

	public int getAccountInputMode()
	{
		BankTracker.autoMSR();
		return super.getAccountInputMode();
	}
}
