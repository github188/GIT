package custom.localize.Cjmx;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Struct.CustomerDef;

public class Cjmx_HykInfoQueryBS extends HykInfoQueryBS
{
	protected void getHykDisplayInfo(CustomerDef cust, StringBuffer info)
	{
		info.append("卡    号: " + Convert.appendStringSize("", cust.code, 1, 16, 16, 0) + "\n");
		info.append("持 卡 人: " + Convert.appendStringSize("", cust.name, 1, 16, 16, 0) + "\n");
		info.append("手 机 号: " + Convert.appendStringSize("", cust.valstr1, 1, 16, 16, 0) + "\n");
		info.append("卡 状 态: " + Convert.appendStringSize("", cust.status, 1, 16, 16, 0) + "\n");
		info.append("卡 积 分: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.valuememo), 1, 16, 16, 0) + "\n");
		info.append("会员功能: " + Convert.appendStringSize("", getFuncText(cust.ishy), 1, 16, 16, 0) + "\n");
		info.append("积分功能: " + Convert.appendStringSize("", getFuncText(cust.isjf), 1, 16, 16, 0) + "\n");
		info.append("折扣功能: " + Convert.appendStringSize("", getFuncText(cust.iszk), 1, 16, 16, 0) + "\n");
		if (isLczcFunc(cust))
		{
			info.append("零钞转存: " + Convert.appendStringSize("", getFuncText('Y'), 1, 16, 16, 0) + "\n");
			info.append("零钞余额: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.value1), 1, 16, 16, 0) + "\n");
			info.append("零钞上限: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.value2), 1, 16, 16, 0) + "\n");
		}
	}
	
	protected void hykDisplayInfo(CustomerDef cust,Vector saleinfo)
	{
		//getHykDisplayInfo(cust, info);

		//new MessageBox(info.toString());

		Cjmx_HykSaleInfoForm window = new Cjmx_HykSaleInfoForm();
		window.open(cust,saleinfo);
		
		// 查询卡上的返券资料
//		if (GlobalInfo.sysPara.findcustfjk == 'Y')
//		{
//			if (new MessageBox(Language.apply("是否立即查询卡中电子券的余额?"), null, true).verify() == GlobalVar.Key1)
//			{
//				CustomLocalize.getDefault().createFjkInfoQueryBS().QueryFjkInfo(custtrack);
//			}
//		}
	}
	
	public void QueryHykInfo()
	{
		// 读会员卡

		String track2 = readMemberCard();
		if (track2 == null || track2.equals(""))
			return;

		// 查找会员卡
		CustomerDef cust = findMemberCard(track2);

		if (cust == null)
			return;

		Vector saleinfo = new Vector();
		Cjmx_NetService cns = new Cjmx_NetService();
		cns.getCustSaleList(saleinfo,cust.code);
		
		
		// 在客显上显示卡号及余额
		// LineDisplay.getDefault().clearText();
		// LineDisplay.getDefault().displayAt(0, 1, cust.code);
		// LineDisplay.getDefault().displayAt(1, 1,
		// ManipulatePrecision.doubleToString(cust.valuememo));

		// 显示卡信息
		hykDisplayInfo(cust,saleinfo);
	}
}
