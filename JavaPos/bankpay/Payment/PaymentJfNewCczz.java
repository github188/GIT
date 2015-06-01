package bankpay.Payment;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Payment.PaymentJfNew;

public class PaymentJfNewCczz extends PaymentJfNew
{
	public boolean initList()
	{
		try
		{
			couponList.clear();
			couponList.removeAllElements();

			if ((mzkret.memo != null) && (mzkret.memo.trim().length() > 0))
			{
				String[] row = mzkret.memo.split("\\|");

				if (row.length <= 0) { return false; }

				for (int i = 0; i < row.length; i++)
				{
					String[] line = row[i].split(",");

					if (line.length != 5)
					{
						continue;
					}

					//种类|名称|金额|汇率|原行数|纸券
					String[] lines = { line[0], line[1], line[2], line[3], "-1", line[4] };
					
					if (line[0].equals("AAA"))
					couponList.add(lines);
					// }

				}

				return true;
			}
			else
			{
				new MessageBox("当前没有积分余额\n或\n此积分已经消费或者过期");
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}

		return false;
	}
}
