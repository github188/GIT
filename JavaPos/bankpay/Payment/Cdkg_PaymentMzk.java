package bankpay.Payment;

import java.io.BufferedReader;
import java.io.PrintWriter;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.BankTracker;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Cdkg_PaymentMzk  extends PaymentMzk
{
	public Cdkg_PaymentMzk()
	{
		super();
	}

	public Cdkg_PaymentMzk(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public Cdkg_PaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	protected static boolean checkCard(String track)
	{
		StringBuffer sb = new StringBuffer();

		if (!new TextBox().open("请输入卡面号后5位数字", "卡号验证", "", sb, 0, 0, false))
			return false;

		if (sb.toString().trim().equals(""))
			return false;

		if (sb.toString().trim().length() < 5)
		{
			new MessageBox("请输入5位数字");
			return false;
		}

		String checkcode = "";
		
		try
		{
			String[] code = track.split("=");

			if (code != null && code.length > 1)
			{
				checkcode = code[0].substring(code[0].length() - 5);
				
				if (!checkcode.equals(sb.toString().trim()))
				{
					new MessageBox("输入的验证码不合法,请重新输入验证码");
					return false;
				}
				return true;
			}

			if (code != null && code.length == 1)
			{
				if (code[0].length() <= 10)
				{
					checkcode = code[0].substring(code[0].length() - 5);
			
					if (!checkcode.equals(sb.toString().trim()))
					{
						new MessageBox("输入的验证码不合法,请重新输入验证码");
						return false;
					}
					return true;
				}

				if (code[0].length() > 10)
				{
					checkcode = code[0].substring(code[0].length() - 9);
					checkcode = checkcode.substring(0, 5);
		
					if (!checkcode.equals(sb.toString().trim()))
					{
						new MessageBox("输入的验证码不合法,请重新输入验证码");
						return false;
					}
					return true;
				}
			}

			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public static void main(String[] args)
	{
		
		checkCard("500721543=0271624");
	}
	public boolean findMzkInfo(String track1, String track2, String track3)
	{
		if (!checkCard(track2))
			return false;

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

		if (track2.indexOf("=") > 0)
		{
			// 验证磁道信息
			if (s.length >= 2 && s[1].equals(verifyTrack(s[0], mzkret.cardpwd)))
				return true;
			else
			{
				new MessageBox("卡磁道数据不正确!");
				return false;
			}
		}
		return true;
	}

	public boolean findMzk(String track1, String track2, String track3)
	{
		if (!checkCard(track2))
			return false;

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

		if (track2.indexOf("=") > 0)
		{
			// 验证磁道信息
			if (s.length >= 2 && s[1].equals(verifyTrack(s[0], mzkret.cardpwd)))
				return true;
			else
			{
				new MessageBox("卡磁道数据不正确!");
				return false;
			}
		}
		return true;
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
