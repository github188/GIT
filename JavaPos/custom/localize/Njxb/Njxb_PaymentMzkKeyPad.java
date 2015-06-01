package custom.localize.Njxb;

import java.io.BufferedReader;

import org.eclipse.swt.events.KeyEvent;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Payment.PaymentMzkEvent;

public class Njxb_PaymentMzkKeyPad extends Njxb_PaymentMzkNew
{
	private final static String PATH = "C:\\GMC";

	private boolean getKeyPadPwd(StringBuffer pwd)
	{
		ProgressBox pb = null;
		try
		{
			if (PathFile.fileExist(PATH + "\\reskeypad.txt"))
			{
				PathFile.deletePath(PATH + "\\reskeypad.txt");
				if (PathFile.fileExist(PATH + "\\reskeypad.txt"))
				{
					new MessageBox("删除上次密码键盘应答文件失败，请联系管理员");
					return false;
				}
			}

			pb = new ProgressBox();
			pb.setText("正在输入密码,请等待...");

			if (PathFile.fileExist(PATH + "\\reqkeypad.txt"))
			{
				// 开始调用密码键盘
				if (PathFile.fileExist(PATH + "\\javaposkeypad.exe"))
				{
					CommonMethod.waitForExec(PATH + "\\javaposkeypad.exe PSK");
				}
				else
				{
					new MessageBox("密码键盘调用模块 javaposkeypad.exe 不存在");
					return false;
				}
			}
			// 读取密码键盘返回
			BufferedReader br = null;
			if (!PathFile.fileExist(PATH + "\\reskeypad.txt") || ((br = CommonMethod.readFileGBK(PATH + "\\reskeypad.txt")) == null))
			{
				new MessageBox("读取密码失败!", null, false);
				return false;
			}
			String line = br.readLine();

			if (line.length() <= 0) { return false; }
			String result[] = line.split(",");

			if (result == null) return false;

			String retCode = result[0];

			if (retCode.equals("0"))
			{
				pwd.append(result[1]);
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if (pb != null)
			{
				pb.close();
				pb = null;
			}

			if (PathFile.fileExist(PATH + "\\reskeypad.txt"))
			{
				PathFile.deletePath(PATH + "\\reskeypad.txt");
			}
		}
	}

	public void setPwdAndYe(PaymentMzkEvent event, KeyEvent e)
	{
		if (isPasswdInput())
		{
			// 显示密码
			event.yeTips.setText(getPasswdLabel());
			event.yeTxt.setVisible(false);
			event.pwdTxt.setVisible(true);
			event.yeTxt.setText(ManipulatePrecision.doubleToString(getAccountYe()));

			if (e != null) e.data = "focus";
			event.pwdTxt.setFocus();

			StringBuffer pwd = new StringBuffer();
			if (!getKeyPadPwd(pwd))
			{
				NewKeyListener.sendKey(GlobalVar.Exit);
			}
			else
			{
				event.pwdTxt.setText(pwd.toString());
				NewKeyListener.sendKey(GlobalVar.Enter);
			}
		}
		else
		{
			// 显示余额
			event.yeTips.setText("账户余额");
			event.yeTxt.setVisible(true);
			event.pwdTxt.setVisible(false);
			event.yeTxt.setText(ManipulatePrecision.doubleToString(getAccountYe()));

			// 输入金额
			if (e != null) e.data = "focus";
			event.moneyTxt.setFocus();
			event.moneyTxt.selectAll();
		}
	}
}
