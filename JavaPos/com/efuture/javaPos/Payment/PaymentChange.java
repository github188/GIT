package com.efuture.javaPos.Payment;

import java.util.Vector;

import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class PaymentChange
{
	public SaleBS saleBS = null;
	protected SaleHeadDef saleHead = null;
	protected Vector salePayment = null;
	protected double zlTotal = 0;

	public PaymentChange(SaleBS sale)
	{
		saleBS = sale;

		saleHead = saleBS.saleHead;
		salePayment = saleBS.salePayment;
	}

	public double getPayChange()
	{
		double zl = 0;
		SalePayDef sp = null;

		for (int i = 0; i < salePayment.size(); i++)
		{
			sp = (SalePayDef) salePayment.elementAt(i);

			if (sp.flag == '2')
			{
				zl = ManipulatePrecision.add(zl, sp.je);
			}
		}

		return zl;
	}

	public void clearChange()
	{
		SalePayDef sp = null;

		for (int i = 0; i < salePayment.size(); i++)
		{
			sp = (SalePayDef) salePayment.elementAt(i);

			if (sp.flag == '2')
			{
				saleBS.delSalePayObject(i);
				i--;
			}
		}
	}

	public double getChangeTotal()
	{
		return zlTotal;
	}

	public double getChangeBalance()
	{
		return getChangeTotal() - getPayChange();
	}

	public boolean calcPreChange(StringBuffer buff)
	{
		SalePayDef sp = null, spyy = null;

		// 先清除找零付款方式
		clearChange();

		// 反向查找最后一个可找零的付款方式进行找零

		double zl = 0;
		if (GlobalInfo.sysPara.cutzl == 'Y')
		{
			double tmpje = saleBS.saleyfje;
			tmpje = saleBS.getDetailOverFlow(tmpje, GlobalInfo.sysPara.zljd);
			zl = ManipulatePrecision.doubleConvert(saleHead.sjfk - tmpje - saleBS.salezlexception, 2, 1);
		}
		else
		{
			zl = ManipulatePrecision.doubleConvert(saleHead.sjfk - saleBS.saleyfje - saleBS.salezlexception, 2, 1);
		}

		double tzl = 0;
		for (int i = salePayment.size() - 1; zl > 0 && i >= 0; i--)
		{
			sp = (SalePayDef) salePayment.elementAt(i);
			if (sp.flag != '1')
				continue;

			if (checkpay(sp))
				continue;

			PayModeDef pm = DataService.getDefault().searchPayMode(sp.paycode);
			if (pm == null)
			{
				new MessageBox("[" + sp.paycode + "]" + sp.payname + Language.apply(" 付款方式找不到!"));
				return false;
			}

			if (pm.iszl == 'Y')
			{
				// 找零金额不能超过该付款方式的付款金额
				if (ManipulatePrecision.doubleConvert(sp.je, 2, 1) >= ManipulatePrecision.doubleConvert(zl - tzl, 2, 1))
				{
					tzl += ManipulatePrecision.doubleConvert(zl - tzl, 2, 1);
				}
				else
				{
					tzl += ManipulatePrecision.doubleConvert(sp.je, 2, 1);
				}

				tzl = ManipulatePrecision.doubleConvert(tzl, 2, 1);
				if (tzl >= zl)
					break;
			}
			else
			{
				// 确定最后一个溢余不找零的付款方式
				if (pm.isyy == 'Y' && spyy == null)
					spyy = sp;

				// 如果付款方式是后付款产生溢余不找零的付款方式代码,溢余部分不找零
				if (pm.isyy == 'Y' && GlobalInfo.sysPara.paychgyy != null && !GlobalInfo.sysPara.paychgyy.equals(""))
				{
					String[] s = GlobalInfo.sysPara.paychgyy.split(",");
					for (int j = 0; j < s.length; j++)
					{
						if (pm.code.equals(s[j].trim()))
						{
							double yy = 0;
							if (ManipulatePrecision.doubleConvert(sp.je, 2, 1) >= ManipulatePrecision.doubleConvert(zl - tzl, 2, 1))
							{
								yy = ManipulatePrecision.doubleConvert(zl - tzl, 2, 1);
							}
							else
							{
								yy = ManipulatePrecision.doubleConvert(sp.je, 2, 1);
							}
							// sp.num1 += yy;
							// saleBS.salezlexception += yy;
							zl = ManipulatePrecision.sub(zl, yy);
							break;
						}
					}
				}
			}
		}

		/*
		 * 付款方式的溢余直接在addSalePayObject中进行计算 // 不找零溢余部分算作最后一个可溢余付款方式产生 if
		 * (ManipulatePrecision.doubleConvert(zl - tzl, 2,1) > 0 && spyy !=
		 * null) { double yy = ManipulatePrecision.doubleConvert(zl - tzl, 2,1);
		 * spyy.num1 += yy; saleBS.salezlexception += yy; }
		 */
		buff.append(Math.min(zl, tzl));
		return true;
	}

	public boolean checkpay(SalePayDef sp)
	{

		return false;
	}

	public boolean calcChange()
	{
		StringBuffer buff = new StringBuffer();
		if (!calcPreChange(buff))
			return false;

		// 找零过大
		double tzl = Convert.toDouble(buff);
		if (tzl > GlobalInfo.sysPara.chglimit)
		{
			new MessageBox(Language.apply("找零金额过大,请减少付款金额"));
			return false;
		}

		// 生成找零付款
		zlTotal = tzl;
		if (zlTotal > 0)
		{
			if (GlobalInfo.sysPara.paychgmore != 'Y')
			{
				// 按收银截断方式计算找零金额
				if (String.valueOf(GlobalInfo.sysPara.zljd).trim().length() <= 0 || GlobalInfo.sysPara.zljd == 'Y')
					zlTotal = saleBS.getDetailOverFlow(zlTotal);
				else
					zlTotal = saleBS.getDetailOverFlow(zlTotal, GlobalInfo.sysPara.zljd);

				if (zlTotal > 0)
				{
					// 找到最后一个找零付款方式
					PayModeDef lastzlpaymode = null;

					// 使用人民币找零
					if (GlobalInfo.sysPara.paychgmore == 'B')
					{
						lastzlpaymode = DataService.getDefault().searchPayMode("01");
					}

					if (lastzlpaymode == null)
					{

						for (int i = salePayment.size() - 1; i >= 0; i--)
						{
							SalePayDef sp = (SalePayDef) salePayment.elementAt(i);
							if (sp.flag != '1')
								continue;

							PayModeDef pm = DataService.getDefault().searchPayMode(sp.paycode);
							if (pm.iszl == 'Y')
							{
								lastzlpaymode = pm;
								break;
							}
						}

						// 如果付款的找零方式不是本位币找零方式,则重新查找本位币付款方式,保证找零总是找本位币
						if (ManipulatePrecision.doubleCompare(lastzlpaymode.zlhl, 1.0000, 4) != 0)
						{
							PayModeDef pm = null;
							for (int i = 0; i < GlobalInfo.payMode.size(); i++)
							{
								pm = (PayModeDef) GlobalInfo.payMode.elementAt(i);
								if (pm.iszl == 'Y' && ManipulatePrecision.doubleCompare(pm.zlhl, 1.0000, 4) == 0)
								{
									lastzlpaymode = pm;
									break;
								}
							}
						}
					}
					// 记录找零明细
					if (!chgAccount(lastzlpaymode, String.valueOf(zlTotal)))
						return false;
				}
			}
			else
			{
				// 多币种找零模式
				if (!new PaymentChangeForm().open(this)) { return false; }
			}
		}

		return true;
	}

	private boolean checkMoneyValid(PayModeDef mode, String money)
	{
		try
		{
			if (money.equals(""))
			{
				new MessageBox(Language.apply("找零金额不能为空!"));

				return false;
			}

			// 检查本笔找零金额是否超过剩余可找零金额(应都转换为原币金额进行比较)
			double ybje = Double.parseDouble(getChgMoneyByPrecision(Double.parseDouble(money), mode));
			double syje = Double.parseDouble(getChgMoneyByPrecision(getChangeBalance() / mode.zlhl, mode));
			if (ManipulatePrecision.doubleCompare(ybje, syje, 2) > 0)
			{
				new MessageBox(Language.apply("找零金额过大!\n") + ybje + "  " + syje);

				return false;
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return false;
	}

	public boolean chgAccount(PayModeDef mode, String money)
	{
		if (checkMoneyValid(mode, money))
		{
			SalePayDef salepay;

			salepay = new SalePayDef();
			salepay.syjh = saleBS.saleHead.syjh;
			salepay.fphm = saleBS.saleHead.fphm;
			salepay.paycode = mode.code;
			salepay.payname = mode.name + Language.apply("找零");
			salepay.flag = '2'; // 找零标志
			salepay.ybje = Double.parseDouble(getChgMoneyByPrecision(Double.parseDouble(money), mode));
			salepay.hl = mode.zlhl;
			salepay.je = ManipulatePrecision.doubleConvert(salepay.ybje * salepay.hl, 2, 1);
			salepay.payno = "";
			salepay.batch = "";
			salepay.kye = 0;
			salepay.idno = "";
			salepay.memo = "";

			// 只增加付款明细,没有付款对象
			saleBS.addSalePayObject(salepay, null);

			return true;
		}
		else
		{
			return false;
		}
	}

	// 得到找零付款方式
	public Vector getChangePayMode()
	{
		Vector child = new Vector();
		String[] temp = null;
		PayModeDef mode = null;

		for (int i = 0; i < GlobalInfo.payMode.size(); i++)
		{
			mode = (PayModeDef) GlobalInfo.payMode.elementAt(i);

			if (mode.iszl == 'Y' && mode.ismj == 'Y')
			{
				temp = new String[3];
				temp[0] = mode.code.trim();
				temp[1] = mode.name;
				temp[2] = ManipulatePrecision.doubleToString(mode.zlhl, 4, 1, false);
				child.add(temp);
			}
		}

		return child;
	}

	public Vector getChangePaymentDisplay()
	{
		Vector v = new Vector();
		String[] detail = null;
		SalePayDef saledef = null;

		for (int i = 0; i < salePayment.size(); i++)
		{
			saledef = (SalePayDef) salePayment.elementAt(i);

			if (saledef.flag == '2')
			{
				detail = new String[3];
				detail[0] = "[" + saledef.paycode + "]" + saledef.payname;
				detail[1] = ManipulatePrecision.doubleToString(saledef.ybje);
				detail[2] = ManipulatePrecision.doubleToString(saledef.hl, 4, 1, false);
				v.add(detail);
			}
		}

		return v;
	}

	// 根据付款方式的付款精度,计算付款金额
	public String getChgMoneyByPrecision(double je, PayModeDef mode)
	{
		int jd;

		if (mode.sswrjd == 0)
			jd = 2;
		else
			jd = ManipulatePrecision.getDoubleScale(mode.sswrjd);

		double ye = ManipulatePrecision.doubleConvert(je, jd, 1);

		return ManipulatePrecision.doubleToString(ye, jd, 1);
	}

	// 设置付款金额输入框的缺省值
	public void setMoneyInputDefault(Text txt, String paycode)
	{
		PayModeDef pay = DataService.getDefault().searchPayMode(paycode);

		// 付款覆盖模式,找已有的找零金额
		if (GlobalInfo.sysPara.payover == 'Y')
		{
			int i = existChange(paycode, "");
			if (i >= 0)
			{
				SalePayDef salepay = (SalePayDef) salePayment.elementAt(i);
				txt.setText(ManipulatePrecision.doubleToString(salepay.ybje));
				txt.selectAll();
				return;
			}
		}

		// 计算剩余找零
		double needPay = getChangeBalance();
		if (pay.zlhl <= 0)
			pay.zlhl = 1;
		txt.setText(getChgMoneyByPrecision(needPay / pay.zlhl, pay));
		txt.selectAll();
	}

	public boolean deleteChange(int index)
	{
		SalePayDef saledef = null;
		int i;

		for (i = 0; i < salePayment.size(); i++)
		{
			saledef = (SalePayDef) salePayment.elementAt(i);

			if (saledef.flag == '2')
				break;
		}

		//
		saleBS.delSalePayObject(i + index);

		return true;
	}

	public int existChange(String code, String account)
	{
		SalePayDef saledef = null;

		for (int i = 0; i < salePayment.size(); i++)
		{
			saledef = (SalePayDef) salePayment.elementAt(i);

			if (saledef.flag == '2' && saledef.paycode.equals(code) && saledef.payno.equals(account)) { return i; }
		}

		return -1;
	}

}
