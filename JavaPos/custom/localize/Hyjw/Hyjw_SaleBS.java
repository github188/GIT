package custom.localize.Hyjw;

import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bstd.Bstd_SaleBS;

public class Hyjw_SaleBS extends Bstd_SaleBS
{
	public void initNewSale()
	{
		Hyjw_MzkModule.getDefault().initData();
		super.initNewSale();
	}

	public void execCustomKey0(boolean keydownonsale)
	{
		Hyjw_MzkModule.getDefault().exeOtherFunc();
	}

	public boolean doLcZc(Label txt_zl, Group grp_zl_sy)
	{
		double zlmoney = 0;
		boolean showtips = !(GlobalInfo.sysPara.isAutoLczc == 'Y'); // 是否强制存入零钞

		// 销售交易才能转存
		if (!SellType.ISSALE(saletype))
		{
			if (showtips)
				new MessageBox(Language.apply("必须是销售模式才能进行零钞转存的功能!"));
			return false;
		}

		if (GlobalInfo.sysPara.lczcmaxmoney <= 0)
		{
			if (showtips)
				new MessageBox(Language.apply("系统参数定义最大零钞转存金额小于等于0\n\n无法进行零钞转存的功能!"));
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
			if (sp.paycode.equals("0111") && sp.memo.trim().equals("3"))
			{
				zlmoney = ManipulatePrecision.add(zlmoney, sp.je * -1);
			}
		}
		zl = ManipulatePrecision.doubleConvert(ManipulatePrecision.add(zl, zlmoney));
		if (zl <= 0)
		{
			if (showtips)
				new MessageBox(Language.apply("当前无找零金额\n\n无法进行零钞转存的功能!"));

			return false;
		}

		// 强制零钞转存则自动存入参数定义的最大金额,否则提示输入找零金额
		if (GlobalInfo.sysPara.isAutoLczc == 'Y')
		{
			// 存入低于参数的找零金额的零头部分,参数表示的意义是最小的可找零面额
			double maxlczc = ManipulatePrecision.doubleConvert(zl % GlobalInfo.sysPara.lczcmaxmoney);

			zlmoney = maxlczc;
		}
		else
		{
			double maxlczc = zl;

			// 输入转存金额
			StringBuffer buffer = new StringBuffer();
			buffer.append(ManipulatePrecision.doubleToString(Math.min(zl, 0.00)));
			String line = "本笔应找零金额为 " + ManipulatePrecision.doubleToString(zl, 2, 1) + " 元\n" + "本次最多允许进行 " + ManipulatePrecision.doubleToString(maxlczc, 2, 1) + " 元的零钞转存";
			if (!new TextBox().open("请输入您要进行零钞转存的金额", "金额", line, buffer, 0.01, maxlczc, true))
				return false;

			zlmoney = Double.parseDouble(buffer.toString());

			if (zlmoney == 0.00)
				return false;

			if (zlmoney > GlobalInfo.sysPara.lczcmaxmoney)
			{
				new MessageBox(Language.apply("输入的转存充值金额大于系统定义的{0}元\n无法进行零钞转存的功能!", new Object[] { ManipulatePrecision.doubleToString(GlobalInfo.sysPara.lczcmaxmoney) }));
				return false;
			}
		}

		// 先删除已存在的零钞转存
		deleteLcZc();

		// 再增加新的转存金额付款

		PayModeDef pmd = DataService.getDefault().searchPayMode("0111");
		if (pmd == null)
		{
			new MessageBox("未定义编码为0111的付款方式!");
			return false;
		}

		Hyjw_PaymentLczc pay = new Hyjw_PaymentLczc(pmd, this);
		if (pay == null || !pay.createLczcSalePay(zlmoney))
		{
			new MessageBox("零钞转存对象创建失败!");
			return false;
		}

		addSalePayObject(pay.salepay, pay);

		// 重新计算应收应付
		calcPayBalance();

		// 重新计算找零
		calcSaleChange();

		// 刷新找零窗口显示
		grp_zl_sy.setText(getChangeTitleLabel());
		grp_zl_sy.setText(grp_zl_sy.getText() + "/" + Language.apply("零钞转存") + "(" + ManipulatePrecision.doubleToString(zlmoney) + ")");
		txt_zl.setText(ManipulatePrecision.doubleToString(saleHead.zl));

		return true;
	}

	// 删除零钞转存功能
	public boolean deleteLcZc()
	{
		for (int i = 0; i < salePayment.size(); i++)
		{
			SalePayDef spd = (SalePayDef) salePayment.elementAt(i);

			if (spd != null)
			{
				if (spd.paycode.equals("0111") && spd.memo.trim().equals("3"))
				{
					Payment p = (Payment) payAssistant.elementAt(i);

					if (p.cancelPay())
					{
						delSalePayObject(i);
						return true;
					}
				}
			}
		}

		return false;
	}
}
