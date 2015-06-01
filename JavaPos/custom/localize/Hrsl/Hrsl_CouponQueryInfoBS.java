package custom.localize.Hrsl;

import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.swt.custom.StyledText;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Logic.CouponQueryInfoBS;
import com.efuture.javaPos.Payment.CreatePayment;

public class Hrsl_CouponQueryInfoBS extends CouponQueryInfoBS
{
	public void displayBaseInfo(StyledText text)
	{
		StringBuffer info = new StringBuffer();
		info.append("卡    号: " + Convert.appendStringSize("", cust.code, 1, 16, 16, 0) + "\n");
		info.append("持 卡 人: " + Convert.appendStringSize("", cust.name, 1, 16, 16, 0) + "\n");
		info.append("卡 状 态: " + Convert.appendStringSize("", cust.status, 1, 16, 16, 0) + "\n");
		info.append("卡 类 型: " + Convert.appendStringSize("", cust.str1, 1, 16, 16, 0) + "\n");
		info.append("有 效 期: " + Convert.appendStringSize("", cust.maxdate, 1, 16, 16, 0) + "\n");
		
		//info.append("会员类型: " + Convert.appendStringSize("", cust.str1, 1, 16, 16, 0) + "\n");
		
		info.append("积分功能: " + Convert.appendStringSize("", getFuncText(cust.isjf), 1, 16, 16, 0) + "\n");
		info.append("储值功能: " + Convert.appendStringSize("", getFuncText(cust.str2.charAt(0)), 1, 16, 16, 0) + "\n");
		info.append("券 功 能: " + Convert.appendStringSize("", getFuncText(cust.str3.charAt(0)), 1, 16, 16, 0) + "\n");
		
		info.append("可用积分: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.num1), 1, 16, 16, 0) + "\n");
		info.append("储值余额: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.num2), 1, 16, 16, 0) + "\n");
		info.append("券 余 额: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.num3), 1, 16, 16, 0) + "\n");
		if (isLczcFunc(cust))
		{
			info.append("零钞转存: " + Convert.appendStringSize("", getFuncText('Y'), 1, 16, 16, 0) + "\n");
			info.append("零钞余额: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.value1), 1, 16, 16, 0) + "\n");
			info.append("零钞上限: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.value2), 1, 16, 16, 0) + "\n");
		}

		text.setText(info.toString());
	}
	
	public boolean findHYK(String track1, String track2, String track3)
	{
		hyk = CustomLocalize.getDefault().createHykInfoQueryBS();
		String[] s = parseFjkTrack(track1, track2, track3);
		cust = hyk.findMemberCard(s[1]);

		if (cust == null) { return false; }

		coupon = CreatePayment.getDefault().getPaymentCoupon();

		if (coupon.findFjk(track1, s[1], track3) && coupon.initList())
		{
			couponlist = coupon.couponList;
		}
		ArrayList list = new ArrayList();
		double zje = 0;
		if (couponlist != null)
		{
			for (int i = 0; i < couponlist.size(); i++)
			{
				String[] row = (String[]) couponlist.elementAt(i);
				zje += Convert.toDouble(row[2]);
				String[] row1 = {row[1]," ",row[5],row[2]};
				list.add(row1);
			}
		}
        //"券名称", "券开始日期", "券结束日期", "券余额"
		if (list.size() > 0)
		{
			coupondetail = list;
		}
		
		return true;
	}
	
	public void displayCouponDetail(PosTable table)
	{
		if (coupondetail != null)
		{
			String[] title = { "券名称",  "券结束日期", "券余额" };
			int[] width = { 200,  250, 200 };
			table.setTitle(title);
			table.setWidth(width);

			table.initialize();

			Vector v = new Vector();

			for (int i = 0; i < coupondetail.size(); i++)
			{
				String[] fjd =  (String[]) coupondetail.get(i);
				v.add(new String[] { fjd[0],  fjd[2], fjd[3] });
			}

			table.exchangeContent(v);
		}
	}
}
