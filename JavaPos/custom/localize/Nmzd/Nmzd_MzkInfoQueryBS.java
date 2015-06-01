package custom.localize.Nmzd;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.LineDisplay;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Nmzd_MzkInfoQueryBS extends MzkInfoQueryBS
{
	
	public void QueryMzkInfo(PaymentMzk pay)
	{
		//StringBuffer cardno = new StringBuffer();
		//String track1, track2, track3;

		// 创建面值卡付款对象
		
		PaymentMzk mzk = null;
		if (pay == null)
		{
			mzk = CreatePayment.getDefault().getPaymentMzk();
			text = "面值卡";
		}
		else
		{
			mzk = pay;
			if (text == null || text.equals("")) text = mzk.paymode.name;
		}
		
		if (!mzk.allowMzkOffline() && !GlobalInfo.isOnline)
		{
			new MessageBox("此功能必须联网使用");
			return;
		}
		
		/**
		Vector contents = new Vector();
		
		while(true)
		{
			// 刷面值卡
			TextBox txt = new TextBox();
			if (!txt.open("请刷" + text, text, "请将" + text + "从刷卡槽刷入", cardno, 0, 0, false, mzk.getAccountInputMode())) { break; }
	
			ProgressBox progress = null;
	
			try
			{
				progress = new ProgressBox();
				progress.setText("正在查询" + text + "信息，请等待.....");
	
				// 得到磁道信息
				track1 = txt.Track1;
				track2 = txt.Track2;
				track3 = txt.Track3;
	
				// 先发送冲正
				if (!mzk.sendAccountCz()) break;
	
				// 再查询
				if (!mzk.findMzkInfo(track1, track2, track3)) { break;}
	
				// 在客显上显示面值卡号及余额
				LineDisplay.getDefault().display(Convert.increaseChar(ManipulatePrecision.doubleToString(mzk.mzkret.ye),20));
				// LineDisplay.getDefault().displayAt(1, 1,
				// ManipulatePrecision.doubleToString(mzk.mzkret.ye));
	
				//
				progress.close();
				progress = null;
				//检查卡是否已经刷过
				boolean add = false;
				for (int i = 0 ; i < contents.size(); i ++)
				{
					String row[] = (String[]) contents.elementAt(i);
					if (mzk.mzkret.cardno.equals(row[0]))
					{
						contents.removeElementAt(i);
						String row1[] = new String[]{mzk.mzkret.cardno, ManipulatePrecision.doubleToString(mzk.mzkret.ye)};
						contents.add(row1);
						add = true;
						break;
					}
				}
				
				if (!add)
				{
					String row1[] = new String[]{mzk.mzkret.cardno, ManipulatePrecision.doubleToString(mzk.mzkret.ye)};
					contents.add(row1);
				}
				
				//System.out.println("aaa");
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
		
		if (contents.size() > 0)
		{
			//计算总金额
			double je = 0;
			for (int i = 0 ; i < contents.size(); i ++)
			{
				String row[] = (String[]) contents.elementAt(i);
				je = ManipulatePrecision.doubleConvert(je + Convert.toDouble(row[1]));
			}
			
			LineDisplay.getDefault().display(Convert.increaseChar(ManipulatePrecision.doubleToString(je),20));
			
			
		*/
			String[] title = { "卡号", "余额" };
			int[] width = { 260, 100 };
			new MutiSelectForm().open("卡数量:0"+"   总余额:"+ManipulatePrecision.doubleToString(0), title, width, null,false,589,319,560,192,false,false,-1,false, 0, 0, null, null, null, 301);
			//退出时清空客显
			LineDisplay.getDefault().displayAt(0,0,Convert.increaseChar("",20));
			LineDisplay.getDefault().displayAt(1,0,Convert.increaseChar(" ",20));
			//}
	}
}
