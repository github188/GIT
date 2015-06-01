package custom.localize.Hfhf;

import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bstd.Bstd_SaleBS;

public class Hfhf_SaleBS0Customer extends Bstd_SaleBS
{

/*	public void activeLczc()
	{
		NewKeyListener.sendKey(GlobalVar.MemberGrant);
	}

	public boolean memberExtend(Label txt_zl, Group grp_zl_sy)
	{
		return doLcZc(txt_zl, grp_zl_sy);
	}
*/
	// 零钞转存功能
	public boolean doLcZc(Label txt_zl, Group grp_zl_sy)
	{
		// 销售交易才能转存
		if (!SellType.ISSALE(saletype))
			return false;

		if (curCustomer == null || !curCustomer.valstr3.equals("szd"))
			return false;

		if (GlobalInfo.sysPara.isAutoLczc != 'Y')
		{
			new MessageBox("零钞转存功能未启用");
			return false;
		}

		double zlmoney = 0;

		if (GlobalInfo.sysPara.lczcmaxmoney <= 0)
		{
			new MessageBox("系统参数定义最大零钞转存金额小于等于0\n\n无法进行零钞转存的功能!");
			return false;
		}

		// 计算实际可找零金额
		double zl = 0;
		for (int i = 0; i < salePayment.size(); i++)
		{
			SalePayDef sp = (SalePayDef) salePayment.elementAt(i);

			// 计算找零合计
			if (sp.flag == '2')
			{
				zl = ManipulatePrecision.add(zl, sp.je);
			}

			// 计算已有的转存金额，将转存金额补回到找零合计,得到未进行转存前真实的找零
			if (Hfhf_PaymentCoinPurse.isPaymentLczc(sp))
			{
				zlmoney = ManipulatePrecision.add(zlmoney, sp.je * -1);
			}
		}

		zl = ManipulatePrecision.doubleConvert(ManipulatePrecision.add(zl, zlmoney));
		if (zl <= 0)
			return false;

		double maxlczc = zl;
		curCustomer.value1 = 0;

		// value2表示会员卡零钞账户的余额上限,value1表示会员卡零钞账户的当前余额,value4表示会员卡零钞账户每次存入上限
		if (curCustomer.value2 != 0)
			maxlczc = Math.min(maxlczc, ManipulatePrecision.doubleConvert(curCustomer.value2 - curCustomer.value1));
		if (curCustomer.value4 > 0)
			maxlczc = Math.min(maxlczc, curCustomer.value4);

		// 输入转存金额
		StringBuffer buffer = new StringBuffer();
		buffer.append(ManipulatePrecision.doubleToString(Math.min(zl,0.00)));
		String line = "本笔应找零金额为 " + ManipulatePrecision.doubleToString(zl, 2, 1) + " 元\n" + "本次最多允许进行 " + ManipulatePrecision.doubleToString(maxlczc, 2, 1) + " 元的会员零钞转存";
		if (!new TextBox().open("请输入您要进行会员零钞转存的金额", "金额", line, buffer, 0.01, maxlczc, true))
			return false;

		zlmoney = Double.parseDouble(buffer.toString());
		
		if(zlmoney ==0.00)
			return false;
		
		if (zlmoney > GlobalInfo.sysPara.lczcmaxmoney)
		{
			new MessageBox("输入的转存充值金额大于系统定义的 " + ManipulatePrecision.doubleToString(GlobalInfo.sysPara.lczcmaxmoney) + " 元\n无法进行零钞转存的功能!");
			return false;
		}
		if (curCustomer.value2 != 0 && (zlmoney + curCustomer.value1) > curCustomer.value2)
		{
			new MessageBox("该会员账户的零钞余额已经到达最大的上限金额\n无法进行零钞转存的功能!");
			return false;
		}

		// 先删除已存在的零钞转存
		if (!deleteLcZc())
		{
			new MessageBox("取消之前的零钞转存失败!");
			return false;
		}

		// 再增加新的转存金额付款

		PayModeDef paymode = DataService.getDefault().searchPayMode("0404");
		if (paymode == null)
		{
			new MessageBox("未定义零钞转存付款方式\n无法进行零钞转存的功能!");
			return false;
		}
		Hfhf_PaymentCoinPurse pay = new Hfhf_PaymentCoinPurse(paymode, saleEvent.saleBS);

		if (pay == null || !pay.createLczcSalePay(zlmoney))
		{
			new MessageBox("没有零钞转存付款方式 或 零钞转存对象创建失败\n\n无法进行零钞转存的功能!");
			return false;
		}
		addSalePayObject(pay.salepay, pay);

		// 重新计算应收应付
		calcPayBalance();

		// 重新计算找零
		calcSaleChange();

		// 刷新找零窗口显示
		grp_zl_sy.setText(getChangeTitleLabel());
		grp_zl_sy.setText(grp_zl_sy.getText() + "/零钞转存(" + ManipulatePrecision.doubleToString(zlmoney) + ")");
		txt_zl.setText(ManipulatePrecision.doubleToString(saleHead.zl));

		return true;
	}

	public boolean deleteLcZc()
	{
		for (int i = 0; i < salePayment.size(); i++)
		{
			SalePayDef spd = (SalePayDef) salePayment.elementAt(i);

			if (spd.paycode.equals("0404") && spd.memo.trim().equals("3"))
			{
				if (!Hfhf_CrmModule.getDefault().cancelChangePocket(spd.batch))
					return false;

				delSalePayObject(i);
				return true;

			}
		}

		return true;
	}
}
