package bankpay.Payment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;


public class Yldf_PaymentMzk extends PaymentMzk
{
	public Yldf_PaymentMzk()
	{
		super();
	}
	
	public Yldf_PaymentMzk(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode,sale);
	}
	
	public Yldf_PaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay,head);
	}
	
	public boolean findMzkInfo(String track1, String track2, String track3)
	{
		if (!findMzk(track1, track2, track3)) return false;
		
		return true;
	}
	
	public boolean findMzk(String track1, String track2, String track3)
	{
		// 新卡还是老卡
		boolean newCard = true;
		//	从二轨分解出卡号
		String[] s = track2.split("=");
		String cardcode = s[0];
		
		if (cardcode.length() > 20)
		{
			newCard = false;
			
			//	调用浪潮工程
			if (!WriteRequest(cardcode)) return false;
			
			try
			{
				//调用接口模块
				if (PathFile.fileExist("C:\\JavaPos\\cardlsck.exe"))
				{
					CommonMethod.waitForExec("C:\\JavaPos\\cardlsck.exe", "cardlsck.exe");
				}
				else
				{
					new MessageBox("找不到 cardlsck.exe");
				
					return false;
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			
			//读取卡号
			BufferedReader br = CommonMethod.readFile("C:\\JavaPos\\out.TXT");
			try
			{
				String line = br.readLine();
				cardcode = line;
				
				//若解密后卡号长度为9位则截取卡号前6位与输入的卡号进行验证；若解密后卡号长度为10位则截取卡号前7位与输入的卡号进行验证
				if (cardcode.length() >=13) cardcode = cardcode.substring(3,10);
				else if (cardcode.length() <=12) cardcode = cardcode.substring(3,9);
				
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					if (br != null)
					{
						br.close();
						br = null;
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		
		do
		{
			StringBuffer buffer = new StringBuffer();
			if (new TextBox().open("请输入外卡号", "卡号", "请输入外卡号", buffer, 0, 0, false))
			{
				if (buffer.toString().trim().length() <= 0) continue;
				
				if (!buffer.toString().trim().equals(cardcode))
				{
					new MessageBox("串码验证数据不相等!");
					return false;
				}
				
				break;
			}
			
		}while (true);
		
		if (!newCard)
		{	
			StringBuffer buffer = new StringBuffer();
			do
			{
				if (new TextBox().open("请输入售卡店储值卡代码", "代码", "请输入售卡店储值卡代码", buffer, 0, 0, false))
				{
					if (buffer.toString().trim().length() <= 0) continue;
					
					break;
				}
			}while (true);
			
			//输入代码后新系统前台处理程序根据输入的代码+已截取卡号的前6位或前7位组
			cardcode = buffer.toString()+cardcode;
		}
		//	找卡
		if (!super.findMzk(track1,cardcode, track3)) return false;
		
		//	不用验证磁道的卡直接返回
		if (mzkret.cardpwd != null && mzkret.cardpwd.startsWith("#######"))
		{
			if (mzkret.cardpwd.length() > 7) mzkret.cardpwd = mzkret.cardpwd.substring(7);
			return true;
		}
		
		// 验证磁道信息
		if (s.length >= 2 && s[1].equals(verifyTrack(s[0],mzkret.cardpwd))) 
		{
			return true;
		}
		else
		{
			new MessageBox("卡磁道数据不正确!");
			return false;
		}
	}
	
	public boolean WriteRequest(String card)
	{
		//	先删除上次交易数据文件
		if (PathFile.fileExist("C:\\JavaPos\\in.txt"))
		{
			PathFile.deletePath("C:\\JavaPos\\in.txt");

			if (PathFile.fileExist("C:\\JavaPos\\in.txt"))
			{
				new MessageBox("交易请求文件in.txt无法删除,请重试");
				return false;
			}
		}

		if (PathFile.fileExist("C:\\JavaPos\\out.TXT"))
		{
			PathFile.deletePath("C:\\JavaPos\\out.TXT");

			if (PathFile.fileExist("C:\\JavaPos\\out.TXT"))
			{
				new MessageBox("交易请求文件out.TXT无法删除,请重试");
				return false;
			}
		}
		
		PrintWriter pw = null;

		try
		{
			pw = CommonMethod.writeFile("C:\\JavaPos\\in.txt");

			if (pw != null)
			{
				pw.print(card);
				pw.flush();
			}
			
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("写入面值卡请求数据异常!\n\n" + ex.getMessage(), null, false);
			
			return false;
		}
		finally
		{
			if (pw != null)
			{
				pw.close();
				pw = null;
			}
		}

	}
	
	public String verifyTrack(String cardno,String pwd)
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
                    	if (verify == null || verify.trim().length() <= 0) continue;
                    	else break;
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
