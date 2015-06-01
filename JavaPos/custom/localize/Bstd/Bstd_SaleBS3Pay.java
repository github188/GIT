package custom.localize.Bstd;

import java.util.Vector;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;

public class Bstd_SaleBS3Pay extends Bstd_SaleBS2Goods
{
	public Vector getPayModeBySuper(String sjcode, StringBuffer index, String code)
	{
		Vector child = new Vector();
		String[] temp = null;
		PayModeDef mode = null;
		int k = -1;
		for (int i = 0; i < GlobalInfo.payMode.size(); i++)
		{
			mode = (PayModeDef) GlobalInfo.payMode.elementAt(i);

			if (("," + GlobalInfo.sysPara.visiblepaycode + ",").indexOf("," + mode.code + ",") > -1)
				continue;

			if ((mode.sjcode.trim().equals(sjcode.trim()) || (sjcode.equals("0") && mode.sjcode.trim().equals(mode.code))) && getPayModeByNeed(mode))
			{
				k++;

				// 标记code付款方式在vector中的位置
				if (index != null && code != null && mode.code.compareTo(code) == 0)
				{
					index.append(String.valueOf(k));
				}

				//
				if (GlobalInfo.sysPara.salepayDisplayRate == 'Y')
				{
					temp = new String[3];
					temp[0] = mode.code.trim();
					temp[1] = mode.name;
					temp[2] = ManipulatePrecision.doubleToString(mode.hl, 4, 1, false);
				}
				else
				{
					temp = new String[2];
					temp[0] = mode.code.trim();
					temp[1] = mode.name;
					if (mode.hl != 1)
						temp[1] = temp[1] + "<" + ManipulatePrecision.doubleToString(mode.hl, 4, 1, false) + ">";
				}
				child.add(temp);
			}

		}

		return child;

	}

	public String getPayMoneyByPrecision(double je, PayModeDef mode)
	{
		int jd;

		if (mode.sswrjd == 0)
		{
			jd = 2;
		}
		else
		{
			jd = ManipulatePrecision.getDoubleScale(mode.sswrjd);
		}

		double ye = 0;

		// 超市V8
		if (mode.sswrfs == 'Y')
		{
			ye = ManipulatePrecision.doubleConvert(je, jd, 0);
		}
		else if (mode.sswrfs == 'N')
		{
			ye = ManipulatePrecision.doubleConvert(je, jd, 1);
		}
		else
		{ // 超市R5
			switch (mode.sswrfs)
			{
			case '0':
				ye = ManipulatePrecision.doubleConvert(je, 2, 1);

				break;

			case '1':
				ye = ManipulatePrecision.doubleConvert(je, 1, 1);

				break;

			case '2':
				ye = ManipulatePrecision.doubleConvert(je, 1, 0);

				break;

			case '3':
				ye = ManipulatePrecision.doubleConvert(je, 0, 1);

				break;

			case '4':
				ye = ManipulatePrecision.doubleConvert(je, 0, 0);

				break;
			case '5':
				ye = ManipulatePrecision.doubleConvert(je + 0.09, 1, 0);

				break;
			case '6':
				ye = ManipulatePrecision.doubleConvert(je + 0.9, 0, 0);

				break;

			case '7':
				ye = ManipulatePrecision.doubleConvert(je - 0.01, 1, 1);

				break;
			}
		}

		return ManipulatePrecision.doubleToString(ye, jd, 1);
	}

	public boolean SaleCollectAccountPayJf()
	{
		int totaljf = 0;
		String jfinfo = "";
		for (int i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(i);

			if (sgd.yhdjbh != null && sgd.yhdjbh.equals("w") && sgd.mjzke > 0)
			{
				sgd.yhzke += sgd.mjzke;
				totaljf += sgd.num4;
				jfinfo = jfinfo + (sgd.num4 + "|" + sgd.code + "|") + ",";
			}
			saleHead.num8 = totaljf;
		}

		if (totaljf <= 0)
			return true;

		PayModeDef paymode = DataService.getDefault().searchPayMode("0509");
		if (paymode == null)
		{
			new MessageBox("发送积分换购失败\n没有[0509]积分换购付款方式");
			return false;
		}

		Bstd_PaymentCustJfSale pay = new Bstd_PaymentCustJfSale(paymode, this);
		pay.createJfExchangeSalePay(0, totaljf, jfinfo, -1);

		if (!pay.collectAccountPay())
			return false;

		return true;

	}

	public boolean saleCollectAccountPay()
	{
		if (super.saleCollectAccountPay())
			return SaleCollectAccountPayJf();

		return false;
	}
}
