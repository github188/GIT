package custom.localize.Jnyz;

import java.util.ArrayList;

import org.eclipse.swt.custom.StyledText;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.CouponQueryInfoBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Struct.CustomerDef;

public class Jnyz_CouponQueryInfoBS extends CouponQueryInfoBS {
	
	public void displayBaseInfo(StyledText text)
	{
		StringBuffer info = new StringBuffer();
		info.append("卡    号: " + Convert.appendStringSize("", cust.code, 1, 16, 16, 0) + "\n");
		info.append("持 卡 人: " + Convert.appendStringSize("", cust.name, 1, 16, 16, 0) + "\n");
		info.append("卡 状 态: " + Convert.appendStringSize("", cust.status, 1, 16, 16, 0) + "\n");
		info.append("卡 积 分: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.valuememo), 1, 16, 16, 0) + "\n");
		info.append("会员功能: " + Convert.appendStringSize("", getFuncText(cust.ishy), 1, 16, 16, 0) + "\n");
		info.append("积分功能: " + Convert.appendStringSize("", getFuncText(cust.isjf), 1, 16, 16, 0) + "\n");
		info.append("折扣功能: " + Convert.appendStringSize("", getFuncText(cust.iszk), 1, 16, 16, 0) + "\n");
		/*info.append("卡有效期：" + Convert.appendStringSize("", cust.maxdate, 1, 16, 16, 0) + "\n");
		if (isLczcFunc(cust))
		{
			info.append("零钞转存: " + Convert.appendStringSize("", getFuncText('Y'), 1, 16, 16, 0) + "\n");
			info.append("零钞余额: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.value1), 1, 16, 16, 0) + "\n");
			info.append("零钞上限: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.value2), 1, 16, 16, 0) + "\n");
		}*/

		text.setText(info.toString());
	}
	
	public boolean findHYK(String track1, String track2, String track3)
	{
		String[] s = parseFjkTrack(track1, track2, track3);
		cust = findMemberCard(s[1],s[2]);

		if (cust == null) { return false; }

		coupon = CreatePayment.getDefault().getPaymentCoupon();

		if (coupon.findFjk(track1, s[1], track3) && coupon.initList())
		{
			couponlist = coupon.couponList;
		}

		double zje = 0;
		if (couponlist != null)
		{
			for (int i = 0; i < couponlist.size(); i++)
			{
				String[] row = (String[]) couponlist.elementAt(i);
				zje += Convert.toDouble(row[2]);
			}
		}

		if (zje > 0)
		{
			ArrayList list = new ArrayList();

			if (coupon.findFjkInfo(track1, coupon.mzkret.cardno, track3, list))
			{
				if (list.size() > 0)
				{
					coupondetail = list;
				}
			}
		}

		return true;
	}
	
	public CustomerDef findMemberCard(String track2,String track3)
	{
		ProgressBox progress = null;
		CustomerDef cust = null;
		try
		{
			progress = new ProgressBox();
			progress.setText("正在查询会员卡信息，请等待.....");

			// 查找会员卡
			cust = new CustomerDef();
			cust.str2 = "2";//磁道查询
			cust.str3 = track3;
			if (!DataService.getDefault().getCustomer(cust, track2)) { return null; }
			if (cust.code == null || cust.code.trim().equals(""))
			{
				new MessageBox("查询的会员卡信息无效!\n请找后台人员");
				return null;
			}
		}
		finally
		{
			if (progress != null)
				progress.close();
		}

		return cust;
	}
	
}
