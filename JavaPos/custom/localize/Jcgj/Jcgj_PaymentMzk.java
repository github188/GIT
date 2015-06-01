package custom.localize.Jcgj;

import java.util.Vector;

import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Payment.PaymentMzkEvent;
import com.efuture.javaPos.Struct.CustFilterDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Jcgj_PaymentMzk extends PaymentMzk
{
	public boolean findMzk(String track1, String track2, String track3)
	{
		Jcgj_Svc svc;
		svc = new Jcgj_Svc("svc_inq", null, "");
		Jcgj_YsCardDef card = new Jcgj_YsCardDef();
		if (svc.doYsCard(card))
		{
			mzkret.cardno = card.cardNo;
			mzkret.cardname = card.name;
			mzkret.ye = card.ye;
			return true;
		}
		else return false;
	}

	public boolean findMzkInfo(String track1, String track2, String track3)
	{
		choicFjkType();
		Jcgj_Svc svc;
		if (Jcgj_Svc.inputType == 3)
		{
			svc = new Jcgj_Svc("svc_inq_by_phone", null, "");
		}
		else
		{
			svc = new Jcgj_Svc("svc_inq", null, "");
		}

		Jcgj_YsCardDef card = new Jcgj_YsCardDef();
		if (svc.doYsCard(card))
		{
			mzkret.cardno = card.cardNo;
			mzkret.cardname = card.name;
			mzkret.ye = card.ye;
			return true;
		}
		else return false;
	}

	public boolean writeMzkCz()
	{
		return true;
	}

	public void specialDeal(PaymentMzkEvent event)
	{
		ProgressBox pb = null;
		try
		{
			pb = new ProgressBox();
			pb.setText("正在刷卡,请等待...");
			event.msrRead(null, "", "", "");
			if (pb != null)
			{
				pb.close();
				pb = null;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			event.shell.close();
			event.shell.dispose();
			event.shell = null;
		}
		finally
		{
			if (pb != null)
			{
				pb.close();
				pb = null;
			}
		}
	}

	public void choicFjkType()
	{
		HykInfoQueryBS bs = CustomLocalize.getDefault().createHykInfoQueryBS();
		// 获取自定义的解析规则
		Vector rulelist = bs.showRule();
		if (rulelist != null && rulelist.size() <= 0) rulelist = null;

		// 先选择规则后刷会员卡
		if (GlobalInfo.sysPara.unionVIPMode == 'A')
		{
			if (rulelist != null && rulelist.size() > 1)
			{
				Vector con = new Vector();
				for (int i = 0; i < rulelist.size(); i++)
				{
					CustFilterDef filterDef = (CustFilterDef) rulelist.elementAt(i);
					con.add(new String[] { filterDef.desc });
				}
				String[] title = { "会员卡类型" };
				int[] width = { 500 };

				int choice = new MutiSelectForm().open("请选择卡类型", title, width, con);

				if (choice != -1)
				{
					CustFilterDef rule = ((CustFilterDef) rulelist.elementAt(choice));
					Jcgj_Svc.inputType = rule.InputType;
				}
			}
		}
	}
}
