package custom.localize.Jplm;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Jplm_NewPaymentCoupon extends PaymentMzk
{
	CouponRet ret = null;

	public Jplm_NewPaymentCoupon()
	{
	}

	public Jplm_NewPaymentCoupon(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public Jplm_NewPaymentCoupon(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	protected String getDisplayAccountInfo()
	{
		return "请 刷 卡";
	}

	public boolean createPayment(String cardno, String money)
	{
		if (ret == null)
			return false;

		if (checkExistSamePay(cardno))
			return false;

		//控制不允许同类型券多次付款
		if (checkExistSameType(ret.type))
			return false;

		this.allowpayje = ret.money;

		if (super.createSalePayObject(money))
		{
			// 记录券类型
			salepay.num4 = ret.type;
			return true;
		}

		return false;
	}

	public double getAccountYe()
	{
		if (ret != null)
			return ret.money;

		return 0;
	}

	private boolean checkExistSameType(int type)
	{
		SalePayDef saledef = null;
		for (int i = 0; i < saleBS.salePayment.size(); i++)
		{
			saledef = (SalePayDef) saleBS.salePayment.elementAt(i);

			if (saledef == null)
				continue;

			if (saledef.num4 == type)
			{
				new MessageBox("不允许同类型券多次付款!");
				return true;
			}
		}
		return false;

	}

	private boolean checkExistSamePay(String payno)
	{
		SalePayDef saledef = null;
		for (int i = 0; i < saleBS.salePayment.size(); i++)
		{
			saledef = (SalePayDef) saleBS.salePayment.elementAt(i);

			if (saledef == null)
				continue;

			if (saledef.payno.equals(payno))
			{
				new MessageBox("已存在" + payno + ",请删除后重新付款!");
				return true;
			}
		}
		return false;
	}

	public SalePayDef inputPay(String money)
	{

		try
		{
			// 退货小票不能使用,退货扣回按销售算
			if (checkMzkIsBackMoney())
			{
				new MessageBox("退货时不能使用" + paymode.name, null, false);

				return null;
			}

			// 先检查是否有冲正未发送
			if (!sendAccountCz())
				return null;

			// 打开明细输入窗口
			new Jplm_PaymentCouponForm().open(this, saleBS);

			// 如果付款成功,则salepay已在窗口中生成
			return salepay;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public boolean findFjk(String track1, String track2, String track3)
	{
		ProgressBox box = null;
		try
		{
			ret = new CouponRet();

			if ((track1.trim().length() <= 0) && (track2.trim().length() <= 0) && (track3.trim().length() <= 0))
				return false;

			box = new ProgressBox();
			box.setText("正在预上传小票...");
			if (!sendSaleBill())
			{
				new MessageBox("预上传小票,无法计算用券!");
				return false;
			}

			box.setText("正在计算用券...");
			if (((Jplm_NetService) NetService.getDefault()).getSaleCoupon(track2, paymode.code, ret))
				return true;

			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			if (box != null)
				box.close();
		}
	}

	public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret)
	{
		return DataService.getDefault().sendFjkSale(req, ret);
	}

	public boolean sendSaleBill()
	{
		Jplm_NetService netService = (Jplm_NetService) NetService.getDefault();

		if (netService.sendSaleBill(saleBS.saleHead, saleBS.saleGoods) == 0)
			return true;

		return false;
	}
}
