package bankpay.Payment;

import java.io.BufferedReader;
import java.io.PrintWriter;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Dzbh_PaymentMzk extends PaymentMzk
{
	String msg = "";
	
	public Dzbh_PaymentMzk()
	{
		super();
	}
	
	public Dzbh_PaymentMzk(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode,sale);
	}
	
	public Dzbh_PaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay,head);
	}

	public boolean findMzkInfo(String track1, String track2, String track3)
	{
		// 找卡
		msg = "";
		String strtrack2 = Decode(track2).trim();
		
		if (strtrack2.length() <= 0 )
		{
			new MessageBox("磁道解密后为空!");
			return false;
		}
		
		if (!super.findMzkInfo(track1, strtrack2, track3))
		{
			return false;
		}
		
		msg = getMessage(mzkret);
		
		return true;
	}
	
	public boolean findMzk(String track1, String track2, String track3)
	{
		msg = "";

		String strtrack2 = Decode(track2).trim();
			
		if (strtrack2.length() <= 0 )
		{
			new MessageBox("磁道解密后为空!");
			return false;
		}

		// 找卡
		if (!super.findMzk(track1, strtrack2, track3))
		{
			 return false;
		}
		
		msg = getMessage(mzkret);

		while(true)
		{
			StringBuffer buffer = new StringBuffer();
			
			if (!new TextBox().open("请输入卡号", "卡号", "刮刮卡区:" + msg, buffer, TextBox.AllInput,30)) {return false;};
			
			if (!buffer.toString().trim().equals(strtrack2))
			{
				new MessageBox("你输入的卡号与系统记录的卡号不一致,请重新输入!");
			}
			else
			{
				break;
			}
		}
		
		return true;
	}
	
	// 解密磁道
	public String Decode(String track2)
	{
		try
		{
			//  调用接口EXE计算验证码
            if (!PathFile.fileExist("javaposbank.exe"))
            {
            	new MessageBox("找不到验证程序 javaposbank.exe");
            	return "";
            }
            else
            {
            	if (PathFile.fileExist("request.txt"))
                {
                    PathFile.deletePath("request.txt");
                    
                    if (PathFile.fileExist("request.txt"))
                    {
                		new MessageBox("验证文件request.txt无法删除,请重试");
                		return "";   	
                    }
                }

                if (PathFile.fileExist("result.txt"))
                {
                    PathFile.deletePath("result.txt");
                    
                    if (PathFile.fileExist("result.txt"))
                    {
                		new MessageBox("验证文件result.txt无法删除,请重试");
                		return "";   	
                    }
                }

                // 写入文件
                PrintWriter pw = null;
                try
                {
	                pw = CommonMethod.writeFile("request.txt");
	                if (pw != null)
	                {
	                    pw.print(track2);
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

                // 解密二轨
                CommonMethod.waitForExec("javaposbank.exe DZBHMZKDECODE");

                // 读取验证码
                String massage = "";
                BufferedReader br = null;
                try
                {
                    br = CommonMethod.readFile("result.txt");
                    if (br == null)
                    {
                        new MessageBox("读取卡号失败!");
                        return "";
                    }

                    while ((massage = br.readLine()) != null)
                    {
                    	if (massage == null || massage.trim().length() <= 0) continue;
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

                return massage==null?"":massage; 
            }
		}
        catch (Exception ex)
        {
            new MessageBox("调用验证模块异常!\n\n" + ex.getMessage());
            return "";
        }
        finally
        {
        	if (PathFile.fileExist("request.txt"))
            {
                PathFile.deletePath("request.txt");
            }

            if (PathFile.fileExist("result.txt"))
            {
                PathFile.deletePath("result.txt");
            }
        }
	}
	
	// 获得面值卡的刷刷层的信息
	public String getMessage(MzkResultDef mrd)
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
                if (mrd == null) return "";
            	String cardnostr = mrd.cardno.length() <= 8?mrd.cardno:mrd.cardno.substring(mrd.cardno.length() - 8);
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
                
                if (mrd.money == 100)
                {
                	cardnostr = "E" +  cardnostr;
                }
                else if (mrd.money == 200)
                {
                	cardnostr = "A" + cardnostr;
                }
                else if (mrd.money == 500)
                {
                	cardnostr = "B" + cardnostr;
                }
                else if (mrd.money == 1000)
                {
                	cardnostr = "C" + cardnostr;
                }
                else if (mrd.money == 2000)
                {
                	cardnostr = "D" + cardnostr;
                }
                else if (mrd.money == 5000)
                {
                	cardnostr = "F" + cardnostr;
                }
                else if (mrd.money == 10000)
                {
                	cardnostr = "H" + cardnostr;
                }
                else if (mrd.money == 20000)
                {
                	cardnostr = "I" + cardnostr;
                }
                else 
                {
                	if (mrd.money == 80 
                	 || mrd.money == 20 
                	 || mrd.money == 50
                	 || mrd.money == 28
                	 || mrd.money == 30
                	 || mrd.money == 38
                	 || mrd.money == 58
                	 || mrd.money == 60
                	 || mrd.money == 70
                	 )
                	{
                		cardnostr = "M" + cardnostr;
                	}
                	else
                	{
                		cardnostr = "M" + cardnostr;
                	}
                }
                
                // 写入文件
                PrintWriter pw = null;
                try
                {
	                pw = CommonMethod.writeFile("request.txt");
	                if (pw != null)
	                {
	                    pw.print(cardnostr);
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
                CommonMethod.waitForExec("javaposbank.exe DZBHMZKMSG");
                
                // 读取验证码
                String massage = "";
                BufferedReader br = null;
                try
                {
                    br = CommonMethod.readFile("result.txt");
                    if (br == null)
                    {
                        new MessageBox("读取验证码失败!");
                        return null;
                    }

                    while ((massage = br.readLine()) != null)
                    {
                    	if (massage == null || massage.trim().length() <= 0) continue;
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
                return massage;
            }
        }
        catch (Exception ex)
        {
            new MessageBox("调用验证模块异常!\n\n" + ex.getMessage());
            return null;
        }
        finally
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
        }
	}
	
	protected String getDisplayStatusInfo()
	{
		String str = super.getDisplayStatusInfo();
		str = "刮刮卡区:" + msg + "\n\n" + str;
		
		return str;
	}
}
