package custom.localize.Gbyw;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

import custom.localize.Bstd.Bstd_HykInfoQueryBS;
import custom.localize.Gbyw.Gbyw_MzkModule.RetInfoDef;

public class Gbyw_HykInfoQueryBS extends Bstd_HykInfoQueryBS
{
	public String readMemberCard(boolean ispay)
	{
		if (!GlobalInfo.isOnline)
		{
			new MessageBox("断网状态下无法使用此功能!");
			return null;
		}

		try
		{
			Vector content = new Vector();
			content.add(new String[] { "老卡" });
			content.add(new String[] { "联名卡" });

			String[] title = { "请选择会员卡" };
			int[] width = { 530 };

			int choice = new MutiSelectForm().open("请选择所要查询的会员卡类型", title, width, content);

			if (choice == -1)
				return null;

			if (choice == 0)
				return super.readMemberCard(ispay);

			if (choice == 1)
				return "bank";
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return null;
	}

	protected CustomerDef getNewMember(String track2)
	{
		// 查找会员卡
		CustomerDef cust = new CustomerDef();

		// 要发送的数据=“04，pos机号，商户号，卡号，密码，暗码”
		String line = "04," + GlobalInfo.syjStatus.syjh + "," + GlobalInfo.sysPara.commMerchantId + "," + track2 + ",,";
		String ret = Gbyw_MzkVipModule.getDefault().sendData(line);

		if (ret == null)
			return null;

		String[] item = ret.split(",");

		// new MessageBox("ret:" + ret);

		if (item == null)
			return null;

		// 返回响应码
		if (item.length > 0)
		{
			if (!item[0].equals("0"))
			{
				new MessageBox(Gbyw_MzkVipModule.getDefault().getError(item[0]));
				return null;
			}
		}

		// 外卡号
		if (item.length > 1)
		{
			cust.code = item[1].trim();

			cust.value2 = ManipulatePrecision.doubleConvert(GlobalInfo.sysPara.hflczcacctuplimit, 2, 1);
			cust.value4 = ManipulatePrecision.doubleConvert(GlobalInfo.sysPara.hflczcperje, 2, 1);
			cust.str1 = track2; // 保存内卡号
			cust.ishy = 'Y';
			cust.zkl = 1;
		}

		// 卡类别
		if (item.length > 2)
			cust.type = item[2].trim();

		// 累积消费
		if (item.length > 3)
			cust.num1 = ManipulatePrecision.doubleConvert(Convert.toDouble(item[3].trim()), 2, 1);

		// 总积分
		if (item.length > 4)
		{
			cust.valuememo = ManipulatePrecision.doubleConvert(Convert.toDouble(item[4].trim()), 2, 1);
		}

		// 零钱包总额
		if (item.length > 5)
			cust.value1 = ManipulatePrecision.doubleConvert(Convert.toDouble(item[5].trim()), 2, 1);

		cust.value6 = -1;

		if (cust.code == null || cust.code.trim().equals(""))
		{
			new MessageBox("查询的会员卡信息无效!");
			return null;
		}

		return cust;
	}

	protected CustomerDef getMember(String track2)
	{
		CustomerDef cust = new CustomerDef();

		String line = "04," + GlobalInfo.syjStatus.syjh + "," + GlobalInfo.sysPara.commMerchantId + "," + track2 + ",,";
		String ret = Gbyw_MzkVipModule.getDefault().sendData(line);

		if (ret == null)
			return null;

		String[] item = ret.split(",");

		if (item == null)
			return null;

		if (item.length > 0)
		{
			if (!item[0].equals("0"))
			{
				new MessageBox(Gbyw_MzkVipModule.getDefault().getError(item[0]));
				return null;
			}
		}

		if (item.length > 1)
		{
			cust.code = item[1].trim();

			cust.value2 = ManipulatePrecision.doubleConvert(GlobalInfo.sysPara.hflczcacctuplimit, 2, 1);
			cust.value4 = ManipulatePrecision.doubleConvert(GlobalInfo.sysPara.hflczcperje, 2, 1);
			cust.ishy = 'Y';
			cust.zkl = 1.0;
		}

		if (item.length > 2)
		{
			cust.type = item[2].trim();
		}

		if (item.length > 3)
		{
			cust.num1 = ManipulatePrecision.doubleConvert(Convert.toDouble(item[3].trim()), 2, 1);
		}

		if (item.length > 4)
		{
			cust.valuememo = ManipulatePrecision.doubleConvert(Convert.toDouble(item[4].trim()), 2, 1);
		}

		if (item.length > 5)
		{
			cust.value1 = ManipulatePrecision.doubleConvert(Convert.toDouble(item[5].trim()), 2, 1);
		}

		cust.value6 = -1;

		if ((cust.code == null) || (cust.code.trim().equals("")))
		{
			new MessageBox("查询的会员卡信息无效!");
			return null;
		}

		return cust;
	}

	public CustomerDef findMemberCard(String track2)
	{
		if (track2 == null)
			return null;

		if (track2.equals("bank"))
		{
			Gbyw_MzkModule.getDefault().initData();
			RetInfoDef retinfo = Gbyw_MzkModule.getDefault().cardQuery(false);

			if (retinfo == null)
				return null;

			CustomerDef cust = new CustomerDef();
			cust.code = retinfo.tradeCardno;
			cust.valuememo = retinfo.scoreYe;
			cust.ishy = 'Y';
			cust.isjf = 'Y';
			cust.type = "4";
			cust.zkl = 1;
			cust.valnum1 = retinfo.ye;
			cust.valnum2 = retinfo.elecbagYe;
			cust.valnum3 = retinfo.scoreRebateYe;

			cust.value6 = 1;

			return cust;
		}
		if (!Gbyw_MzkVipModule.getDefault().initConnection())
			return null;

		ProgressBox progress = null;
		CustomerDef cust = null;
		try
		{
			progress = new ProgressBox();
			progress.setText("正在查询会员卡信息，请等待.....");

			if (GlobalInfo.sysPara.isEnableLHCard == 'Y')
				cust = getNewMember(track2);
			else
				cust = getMember(track2);

		}
		finally
		{
			if (progress != null)
				progress.close();
		}

		return cust;
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

		// 在客显上显示卡号及余额
		// LineDisplay.getDefault().clearText();
		// LineDisplay.getDefault().displayAt(0, 1, cust.code);
		// LineDisplay.getDefault().displayAt(1, 1,
		// ManipulatePrecision.doubleToString(cust.valuememo));

		// 显示卡信息
		getHykDisplayInfo(cust);
	}

	protected void getHykDisplayInfo(CustomerDef cust)
	{
		if (cust.value6 == 1)
		{
			StringBuffer info = new StringBuffer();

			info.append(Language.apply("卡    号: ") + Convert.appendStringSize("", cust.code, 1, 20, 20, 0) + "\n");
			info.append(Language.apply("会员功能: ") + Convert.appendStringSize("", getFuncText(cust.ishy), 1, 20, 20, 0) + "\n");
			info.append(Language.apply("积分功能: ") + Convert.appendStringSize("", getFuncText(cust.isjf), 1, 20, 20, 0) + "\n");
			info.append(Language.apply("积分余额: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.valuememo), 1, 20, 20, 0) + "\n");
			info.append(Language.apply("储值余额: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.valnum1), 1, 20, 20, 0) + "\n");
			info.append(Language.apply("红包余额:") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.valnum2), 1, 20, 20, 0) + "\n");
			info.append(Language.apply("积返余额:") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(cust.valnum3), 1, 20, 20, 0) + "\n");
			// 弹出显示
			new MessageBox(info.toString());
		}
	}
}
