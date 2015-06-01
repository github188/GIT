package custom.localize.Szxw;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.LineDisplay;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Szxw_MzkInfoQueryBS extends MzkInfoQueryBS
{
	public void QueryMzkInfo()
	{
		if (!GlobalInfo.isOnline)
		{
			new MessageBox("此功能必须联网使用");
			return;
		}

		String[] title = { "选择卡类型" };
		int[] width = { 500 };
		Vector contents = new Vector();
		contents.add(new String[] { "定金单" });
		contents.add(new String[] { "面值卡" });

		int choice = new MutiSelectForm().open("请选择查询类型", title, width, contents);

		if (choice == -1) return;

		if (choice == 1)
		{
			super.QueryMzkInfo();
		}
		else
		{
			StringBuffer cardno = new StringBuffer();
			// 创建面值卡付款对象
			PaymentMzk mzk = CreatePayment.getDefault().getPaymentMzk();

			mzk.paymode = DataService.getDefault().searchPayMode("0411");

			if (mzk.paymode == null)
			{
				new MessageBox("未定义 [定金单] 付款方式\n请定义 [定金单] 付款方式后查询");
			}

			// 刷面值卡
			TextBox txt = new TextBox();
			if (!txt.open("请输入定金单号", "定金单", "请输入定金单号", cardno, 0, 0, false, TextBox.IntegerInput)) { return; }

			String track1, track2, track3;

			ProgressBox progress = null;
			try
			{
				progress = new ProgressBox();

				progress.setText("正在查询面值卡信息，请等待.....");

				// 得到磁道信息
				track1 = txt.Track1;
				track2 = cardno.toString();
				track3 = txt.Track3;

				// 先发送冲正
				if (!mzk.sendAccountCz()) return;

				// 再查询
				if (!mzk.findMzk(track1, track2, track3)) { return; }

				// 在客显上显示面值卡号及余额
		        LineDisplay.getDefault().displayAt(0, 1, mzk.getDisplayCardno());
		        LineDisplay.getDefault().displayAt(1, 1, ManipulatePrecision.doubleToString(mzk.mzkret.ye));
				
				//
				progress.close();
				progress = null;

				// 显示卡信息
				StringBuffer info = new StringBuffer();
				info.append("定金单号: " + Convert.appendStringSize("", mzk.mzkret.cardno, 1, 20, 20, 0) + "\n");
				info.append("余    额: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(mzk.mzkret.ye), 1, 20, 20, 0) + "\n");

				String[] str1 = null;
				if (null != mzk.mzkret.str1 && mzk.mzkret.str1.trim().length() > 0 && mzk.mzkret.str1.indexOf(',') > -1)
				{
					str1 = mzk.mzkret.str1.split(",");
				}
				if (str1.length == 5)
				{
					if (str1[0] != null)
					{
						PayModeDef mode = DataService.getDefault().searchPayMode(str1[0]);
						info.append("付款方式: " + Convert.appendStringSize("", mode.name, 1, 20, 20, 0) + "\n");
					}
					if (str1[1] != null)
					{
						info.append("付款卡号: " + Convert.appendStringSize("", str1[1], 1, 20, 20, 0) + "\n");
					}

					if (str1[2] != null)
					{
						info.append("付款柜组: " + Convert.appendStringSize("", str1[2], 1, 20, 20, 0) + "\n");
					}

					if (str1[3] != null)
					{
						info.append("收银机号: " + Convert.appendStringSize("", str1[3], 1, 20, 20, 0) + "\n");
					}

					if (str1[4] != null)
					{
						info.append("下订时间: " + Convert.appendStringSize("", str1[4], 1, 20, 20, 0) + "\n");
					}
				}

				new MessageBox(info.toString());

			}
			catch (Exception er)
			{
				er.printStackTrace();
				new MessageBox(er.getMessage());
			}
			finally
			{
				if (progress != null) progress.close();
			}

		}
	}
}
