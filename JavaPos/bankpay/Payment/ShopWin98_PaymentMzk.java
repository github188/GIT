package bankpay.Payment;

import java.io.BufferedReader;
import java.io.PrintWriter;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;

//解决98下Javaposbank.exe调不起来的问题
public class ShopWin98_PaymentMzk extends Shop_PaymentMzk
{
	public String verifyTrack(String cardno, String pwd)
	{
		try
		{
			// 调用接口EXE计算验证码
			if (!PathFile.fileExist("ShopMzk.exe"))
			{
				new MessageBox("找不到验证程序 ShopMzk.exe");
				return null;
			}
			else
			{
				if (PathFile.fileExist("mzkrequest.txt"))
				{
					PathFile.deletePath("mzkrequest.txt");

					if (PathFile.fileExist("mzkrequest.txt"))
					{
						new MessageBox("验证文件mzkrequest.txt无法删除,请重试");
						return null;
					}
				}

				if (PathFile.fileExist("mzkresult.txt"))
				{
					PathFile.deletePath("mzkresult.txt");

					if (PathFile.fileExist("mzkresult.txt"))
					{
						new MessageBox("验证文件mzkresult.txt无法删除,请重试");
						return null;
					}
				}

				// 写入文件
				PrintWriter pw = null;
				try
				{
					pw = CommonMethod.writeFile("mzkrequest.txt");
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
				CommonMethod.waitForExec("ShopMzk.exe SHOP");

				// 读取验证码
				String verify = null;
				BufferedReader br = null;
				try
				{
					br = CommonMethod.readFile("mzkresult.txt");
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
}
