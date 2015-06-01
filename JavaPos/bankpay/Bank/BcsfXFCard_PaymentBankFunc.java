package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Vector;

import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

import custom.localize.Bcsf.Bcsf_XFCard;

public class BcsfXFCard_PaymentBankFunc extends PaymentBankFunc
{
	public String[] getFuncItem()
	{
		String[] func = new String[3];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消  费";
		func[1] = "[" + PaymentBank.XYKYE + "]" + "查  余";
		func[2] = "[" + PaymentBank.XYKJZ + "]" + "对  账";

		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		switch (type)
		{
			case PaymentBank.XYKXF: // 消费
				grpLabelStr[0] = "电子码";
				grpLabelStr[1] = "密  码";
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
			case PaymentBank.XYKYE:
				grpLabelStr[0] = "电子码";
				grpLabelStr[1] = "密  码";
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "余额查询";
				break;
			case PaymentBank.XYKJZ:
				grpLabelStr[0] = "订单号";
				grpLabelStr[1] = "开始时间";
				grpLabelStr[2] = "结束时间";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "对账查询";
				break;
		}
		return true;
	}

	public boolean getFuncText(int type, String[] grpTextStr)
	{
		switch (type)
		{
			case PaymentBank.XYKXF: // 消费
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
			case PaymentBank.XYKYE:
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "回车进行余额查询";
				break;
			case PaymentBank.XYKJZ:
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按时间段查询订单号处输入0";
				break;
		}
		return true;
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String eticket, String epwd, String olddate, Vector memo)
	{
		try
		{
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKYE) && (type != PaymentBank.XYKJZ))
			{
				errmsg = "不支持该交易";
				new MessageBox(errmsg);

				return false;
			}

			switch (type)
			{
				case PaymentBank.XYKXF:
					Bcsf_XFCard.getDefault().sale(eticket, epwd, String.valueOf(money), bld);
					break;
				case PaymentBank.XYKYE:
					Bcsf_XFCard.getDefault().query(eticket, epwd, bld);
					break;
				case PaymentBank.XYKJZ:
					if (Bcsf_XFCard.getDefault().checkAmount(eticket, epwd, olddate, bld))
						XYKPrintDoc();
					break;
			}

			XYKCheckRetCode();

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			XYKSetError("XX", "接口调用异常XX:" + ex.getMessage());
			new MessageBox("调用处理模块异常!\n\n" + ex.getMessage(), null, false);
			return false;
		}
	}

	public void XYKPrintDoc()
	{
		ProgressBox pb = null;
		BufferedReader br = null;
		String printName = "c:\\JavaPOS\\xfjz.txt";

		try
		{
			pb = new ProgressBox();
			pb.setText("正在打印薪福卡对帐单,请等待...");

			XYKPrintDoc_Start();

			br = CommonMethod.readFile(printName);

			if (br == null)
			{
				new MessageBox("打开对帐打印文件失败");
				return;
			}

			String line = null;

			while ((line = br.readLine()) != null)
				XYKPrintDoc_Print(line);

			XYKPrintDoc_End();

		}
		catch (Exception ex)
		{
			new MessageBox("打印薪福卡对帐单发生异常\n\n" + ex.getMessage());
			ex.printStackTrace();
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
					br = null;
				}
				catch (IOException e)
				{
					e.printStackTrace();
					br = null;
				}
			}

			if (pb != null)
			{
				pb.close();
				pb = null;
			}
		}
	}

	public boolean XYKCheckRetCode()
	{
		if (bld.retcode.trim().equals("00"))
		{
			bld.retbz = 'Y';

			return true;
		}
		else
		{
			bld.retbz = 'N';

			return false;
		}
	}

	public boolean checkDate(Text date)
	{
		return true;
	}
}
