package custom.localize.Bgtx;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentCoupon;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Bgtx_PaymentCoupon extends PaymentCoupon
{	
	public Bgtx_PaymentCoupon()
	{
	}

	public Bgtx_PaymentCoupon(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public Bgtx_PaymentCoupon(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public void initPayment(PayModeDef mode, SaleBS sale)
	{
		super.initPayment(mode, sale);
	}
	

	protected void saveAccountMzkResultToSalePay()
	{
		// batch标记本付款方式已记账,这很重要
		salepay.batch = String.valueOf(mzkreq.seqno);

		// 标记记账返回的卡号
		if (!CommonMethod.isNull(mzkret.cardno))
			salepay.payno = mzkret.cardno;

		// new MessageBox("salepay.kye="+salepay.kye+" mzkret.ye="+mzkret.ye);
		// 后台退货时没有刷卡所以记录后台返回的卡余额,或者记账过程返回了最终余额
		if (salepay.kye <= 0 || mzkret.ye > 0 || (mzkret.status != null && mzkret.status.equals("RETURNYE")))
		{
			salepay.kye = mzkret.ye;
		}
		else
		{
			// 记账过程没有返回最终余额，以查询的余额做基准加减计算新余额
			if (mzkreq.type == "01")
				salepay.kye -= mzkreq.je;
			else
				salepay.kye += mzkreq.je;
		}
		salepay.kye = ManipulatePrecision.doubleConvert(salepay.kye);
		// new MessageBox("salepay.kye="+salepay.kye);
		// 更新付款断点数据，标记为已付款状态,否则在记账以后如果掉电,断点读入的还是未记账状态
		
		salepay.num2 = mzkret.num1;
		
		if (this.saleBS != null)
			this.saleBS.writeBrokenData();
	}

	
	public boolean CreateNewjPayment(int index, double money, StringBuffer bufferStr)
	{
		try
		{
			if (money <= 0)
			{
				new MessageBox(Language.apply("付款金额必须大于0"));

				return false;
			}

			Bgtx_PaymentCoupon cpf = new Bgtx_PaymentCoupon(paymode, saleBS);

			cpf.paymode = (PayModeDef) this.paymode.clone();
			cpf.salehead = this.salehead;
			cpf.saleBS = this.saleBS;
			cpf.couponList = this.couponList;

			cpf.mzkreq = (MzkRequestDef) mzkreq.clone();
			cpf.mzkret = (MzkResultDef) mzkret.clone();

			// ///////////////////// 创建新的付款明细对象
			// 设置券类型
			String[] rows = (String[]) couponList.elementAt(index);

			if (Convert.toInt(rows[5]) > 0)
			{
				cpf.CouponType = Convert.toInt(rows[5]);
			}

			cpf.mzkreq.memo = rows[0];
			cpf.mzkret.ye = Convert.toDouble(rows[2]);

			if ((GlobalInfo.sysPara.fjkkhhl != null) && (GlobalInfo.sysPara.fjkkhhl.length() > 0) && saleBS.isRefundStatus() && !SellType.ISCOUPON(saleBS.saletype))
			{
				String[] lines = null;
				if (GlobalInfo.sysPara.fjkkhhl.indexOf(";") >= 0)
					lines = GlobalInfo.sysPara.fjkkhhl.split(";");
				else if (GlobalInfo.sysPara.fjkkhhl.indexOf("|") >= 0)
					lines = GlobalInfo.sysPara.fjkkhhl.split("\\|");

				if (lines == null)
					lines = new String[] { GlobalInfo.sysPara.fjkkhhl };

				if (lines != null)
				{
					int i = 0;

					for (i = 0; i < lines.length; i++)
					{
						String l = lines[i];

						if (l.indexOf(",") > 0)
						{
							String cid = l.substring(0, l.indexOf(","));

							if (cid.equals(rows[0]))
							{
								cpf.paymode.hl = Convert.toDouble(l.substring(l.indexOf(",") + 1));

								break;
							}
						}
					}

					if (i >= lines.length)
					{
						cpf.paymode.hl = Convert.toDouble(rows[3]);
					}
				}
			}
			else
			{
				cpf.paymode.hl = Convert.toDouble(rows[3]);
			}
			cpf.allowpayje = this.allowpayje;

			// 查询并删除原付款
			// 如果是退货且非扣回时，不删除原付款方式
			if (!(SellType.ISBACK(salehead.djlb) && !saleBS.isRefundStatus()) || GlobalInfo.sysPara.isBackPaymentCover == 'Y')
			{
				if (!deletePayment(index, cpf))
				{
					new MessageBox(Language.apply("删除原付款方式失败！"));

					return false;
				}
			}
			/*
			 * if (!(SellType.ISBACK(salehead.djlb) && !saleBS.isRefundStatus())
			 * && !deletePayment(index, cpf))
			 * {
			 * (GlobalInfo.sysPara.isBackPaymentCover == 'N')
			 * new MessageBox("删除原付款方式失败！");
			 * 
			 * return false;
			 * }
			 */

			if (this.allowpayje >= 0 && money > this.allowpayje && paymode.isyy != 'Y')
			{
				// new MessageBox("该付款方式最多允许付款 " +
				// ManipulatePrecision.doubleToString(allowpayje) + " 元");
				new MessageBox(Language.apply("该付款方式最多允许付款 {0} 元", new Object[] { ManipulatePrecision.doubleToString(allowpayje) }));

				return false;
			}

			double yy = 0;
			if (yyje > 0 && sjje > 0)
			{
				double min = Math.min(ManipulatePrecision.doubleConvert(sjje / cpf.paymode.hl), cpf.allowpayje);
				if (sjje > 0 && money > min)
				{
					// new MessageBox("最大可退金额为: " + min);
					new MessageBox(Language.apply("最大可退金额为: {0}", new Object[] { min + "" }));
					return false;
				}

				if (GlobalInfo.sysPara.oldqpaydet == 'A')
				{
					StringBuffer buf = new StringBuffer();
					buf.append(ManipulatePrecision.doubleToString(money + (yyje / cpf.paymode.hl)));
					TextBox txt = new TextBox();
					// txt.open("请输入券面值", "券面值", "实际付款为:" +
					// ManipulatePrecision.doubleToString(money) + "\n最大券面值为:" +
					// ManipulatePrecision.doubleToString(money + (yyje /
					// cpf.paymode.hl)), buf, 0,
					// ManipulatePrecision.doubleConvert(money + (yyje /
					// cpf.paymode.hl)), true, TextBox.DoubleInput, -1);
					txt.open(Language.apply("请输入券面值"), Language.apply("券面值"), Language.apply("实际付款为:{0}\n最大券面值为:{1}", new Object[] { ManipulatePrecision.doubleToString(money), ManipulatePrecision.doubleToString(money + (yyje / cpf.paymode.hl)) }), buf, 0, ManipulatePrecision.doubleConvert(money + (yyje / cpf.paymode.hl)), true, TextBox.DoubleInput, -1);
					double yfk = money;
					money = Convert.toDouble(buf.toString());
					if (money > yfk)
						yy = ManipulatePrecision.doubleConvert(money - yfk);
				}
				else
				{
					StringBuffer buf = new StringBuffer();
					// buf.append(ManipulatePrecision.doubleToString((yyje/cpf.paymode.hl)));
					TextBox txt = new TextBox();
					// txt.open("请输入此券益余金额", "益余金额", "实际付款为:" +
					// ManipulatePrecision.doubleToString(money) + "\n最大益余金额为:"
					// + ManipulatePrecision.doubleToString((yyje /
					// cpf.paymode.hl)), buf, 0,
					// ManipulatePrecision.doubleConvert((yyje /
					// cpf.paymode.hl)), true, TextBox.DoubleInput, -1);
					txt.open(Language.apply("请输入此券益余金额"), Language.apply("益余金额"), Language.apply("实际付款为:{0}\n最大益余金额为:{1}", new Object[] { ManipulatePrecision.doubleToString(money), ManipulatePrecision.doubleToString((yyje / cpf.paymode.hl)) }), buf, 0, ManipulatePrecision.doubleConvert((yyje / cpf.paymode.hl)), true, TextBox.DoubleInput, -1);

					if (Convert.toDouble(buf.toString()) > 0)
						yy = Convert.toDouble(buf.toString());
				}
			}
			// 创建付款对象
			if (cpf.createSalePay(String.valueOf(money + yy)))
			{
				// 设置付款方式名称
				cpf.salepay.payname = rows[1];
				if (yy > 0)
					cpf.salepay.num1 = ManipulatePrecision.doubleConvert(yy * cpf.salepay.hl);

				// 增加已付款
				if (SellType.ISBACK(saleBS.saletype) && saleBS.isRefundStatus())
				{
					cpf.salepay.payname += Language.apply("扣回");
					saleBS.addSaleRefundObject(cpf.salepay, cpf);

				}
				else
				{
					saleBS.addSalePayObject(cpf.salepay, cpf);
				}

				alreadyAddSalePay = true;

				// 记录当前付款方式
				rows[4] = String.valueOf(cpf.salepay.num5);

				addMessage(cpf, bufferStr);

				// 开始分摊到各个商品
				paymentApportion(cpf.salepay, cpf, false);

				if (GlobalInfo.sysPara.oldqpaydet != 'N' && sjje > 0 && yyje > 0)
				{
					isCloseShell = true;
				}

				return true;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return false;
	}
}
