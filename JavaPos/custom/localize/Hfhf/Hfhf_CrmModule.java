package custom.localize.Hfhf;

import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.RdPlugins;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Hfhf.Hfhf_CoinPurse.VipCoinPayResult;
import custom.localize.Hfhf.Hfhf_CoinPurse.VipCoinPurse;
import custom.localize.Hfhf.Hfhf_ElecMoney.LockPayResult;
import custom.localize.Hfhf.Hfhf_VipScore.ExchangebleScore;
import custom.localize.Hfhf.Hfhf_VipScore.ExchangebleScoreResult;

public class Hfhf_CrmModule
{
	private static Hfhf_CrmModule instance = new Hfhf_CrmModule();

	public static Hfhf_CrmModule getDefault()
	{
		return instance;
	}

	private Hfhf_Customer vip;
	private Hfhf_ElecMoney elecMoney;
	private Hfhf_CoinPurse coinPurse;
	private Hfhf_VipScore vipScore;

	public void init(boolean flag)
	{
		if (flag)
		{
			destory();

			vip = new Hfhf_Customer();
			elecMoney = new Hfhf_ElecMoney();
			coinPurse = new Hfhf_CoinPurse();
			vipScore = new Hfhf_VipScore();
		}
		else
		{
			destory();
		}
	}

	private void destory()
	{
		vip = null;
		elecMoney = null;
		coinPurse = null;
		vipScore = null;
	}

	// ======================查找会员=========================
	public String getCardNo(String track)
	{
		Hfhf_VipTrack viptrack = new Hfhf_VipTrack();
		if (!RdPlugins.getDefault().getPlugins2().exec(999, viptrack.getRequestXml(track)))
		{
			new MessageBox("解析卡号失败");
			return null;
		}

		if (RdPlugins.getDefault().getPlugins2().getObject() == null || ((String) RdPlugins.getDefault().getPlugins2().getObject()).equals(""))
		{
			new MessageBox("解析卡号失败");
			return null;
		}

		String retXml = (String) RdPlugins.getDefault().getPlugins2().getObject();

		if (!viptrack.parse(retXml))
		{
			new MessageBox("解析卡号失败");
			return null;
		}

		return viptrack.getCardFaceNo();

	}

	public boolean getCustomer(CustomerDef cust, String cardno)
	{
		init(true);

		if (!RdPlugins.getDefault().getPlugins2().exec(101, vip.getQueryRequestXml(cardno)))
		{
			new MessageBox("查找会员失败");
			return false;
		}

		if (RdPlugins.getDefault().getPlugins2().getObject() == null || ((String) RdPlugins.getDefault().getPlugins2().getObject()).equals(""))
		{
			new MessageBox("查找会员失败");
			return false;
		}

		String retXml = (String) RdPlugins.getDefault().getPlugins2().getObject();

		if (!vip.parseQuery(retXml))
		{
			new MessageBox("查找会员失败");
			return false;
		}

		if (vip.getAbacusCustomer().Status.equals("-1"))
		{
			new MessageBox("该会员卡已作废!");
			return false;
		}
		else if (vip.getAbacusCustomer().Status.equals("0"))
		{
			cust.status = "正常";
		}
		else if (vip.getAbacusCustomer().Status.equals("1"))
		{
			new MessageBox("该会员卡已冻结!");
			return false;
		}
		else
		{
			new MessageBox("该会员卡状态未知!");
			return false;
		}

		cust.code = vip.getAbacusCustomer().CardNo;
		if (vip.getAbacusCustomer().MemberClsId.trim().equals(""))
			cust.type = "blank";
		else
			cust.type = vip.getAbacusCustomer().MemberClsId;
		cust.deliveryinfo = vip.getAbacusCustomer().MemberClsName;
		cust.maxdate = vip.getAbacusCustomer().ExpiryDate;
		cust.valuememo = vip.getAbacusCustomer().Points;
		cust.name = vip.getAbacusCustomer().Name;
		cust.ishy = 'Y';
		cust.zkl = 1;

		// 标识商之都卡,防止挂单解挂时数据丢失
		cust.valstr3 = "szd";
		System.out.println("getCustomer valstr3:" + cust.valstr3);

		return true;
	}

	public boolean saveVipPoint(SaleHeadDef salehead, Vector salegoods, boolean again)
	{
		// 重发
		if (again)
			vip = new Hfhf_Customer();

		if (vip == null)
			vip = new Hfhf_Customer();

		if (!RdPlugins.getDefault().getPlugins2().exec(102, vip.getSavePointXml(salehead, salegoods)))
		{
			if (!again)
				new MessageBox("保存会员积分失败");
			return false;
		}

		if (!RdPlugins.getDefault().getPlugins2().getErrorCode().equals("0"))
		{
			if (!again)
				new MessageBox(RdPlugins.getDefault().getPlugins2().getErrorMsg());
			return false;
		}

		return true;
	}

	// ========================电子币==========================
	public boolean regElecMoney()
	{
		if (!RdPlugins.getDefault().getPlugins2().exec(100, elecMoney.getRegTerminalRequestXml()))
		{
			new MessageBox("注册消费终端失败");
			return false;
		}

		if (!RdPlugins.getDefault().getPlugins2().getErrorCode().equals("0"))
		{
			new MessageBox(RdPlugins.getDefault().getPlugins2().getErrorMsg());
			return false;
		}

		return true;
	}

	public Vector getElecMoneys(String cardno)
	{
		if (!regElecMoney())
			return null;

		elecMoney.clearElecMoneyList();

		if (!RdPlugins.getDefault().getPlugins2().exec(103, elecMoney.getQueryRequestXml(cardno)))
		{
			new MessageBox("会员电子币查询失败");
			return null;
		}

		if (RdPlugins.getDefault().getPlugins2().getObject() == null || ((String) RdPlugins.getDefault().getPlugins2().getObject()).equals(""))
		{
			new MessageBox("会员电子币查询失败");
			return null;
		}

		String retXml = (String) RdPlugins.getDefault().getPlugins2().getObject();

		if (!elecMoney.parseQuery(retXml))
		{
			new MessageBox("会员电子币查询失败");
			return null;
		}

		return elecMoney.getElecMoneyList();
	}

	public LockPayResult lockElecMoney(String djlb, String cardno, String acctID, double money)
	{
		elecMoney.clearResult();

		if (!RdPlugins.getDefault().getPlugins2().exec(104, elecMoney.getLockPayRequestXml(djlb, cardno, acctID, money)))
		{
			new MessageBox("会员电子币支付锁定失败");
			return null;
		}

		if (RdPlugins.getDefault().getPlugins2().getObject() == null || ((String) RdPlugins.getDefault().getPlugins2().getObject()).equals(""))
		{
			new MessageBox("会员电子币支付锁定失败");
			return null;
		}

		String retXml = (String) RdPlugins.getDefault().getPlugins2().getObject();

		if (!elecMoney.parseLockPay(retXml))
		{
			new MessageBox("会员电子币支付锁定失败");
			return null;
		}

		return elecMoney.getLockPayResult();
	}

	public boolean confirmElecMoney(String referno)
	{
		if (!RdPlugins.getDefault().getPlugins2().exec(105, elecMoney.getConfirmPayRequestXml(referno)))
		{
			new MessageBox("会员电子币支付确认失败");
			return false;
		}

		if (!RdPlugins.getDefault().getPlugins2().getErrorCode().equals("0"))
		{
			new MessageBox(RdPlugins.getDefault().getPlugins2().getErrorMsg());
			return false;
		}

		return true;
	}

	public boolean cancelElecMoney(String djlb, String cardno, String acctID, double money, String referno)
	{

		if (!RdPlugins.getDefault().getPlugins2().exec(106, elecMoney.getCancelPayRequestXml(djlb, cardno, acctID, money, referno)))
		{
			new MessageBox("会员电子币支付取消失败");
			return false;
		}

		if (!RdPlugins.getDefault().getPlugins2().getErrorCode().equals("0"))
		{
			new MessageBox(RdPlugins.getDefault().getPlugins2().getErrorMsg());
			return false;
		}

		return true;

	}

	// =========================零钱包============================
	public VipCoinPurse queryChangePocket(String cardno, boolean isshowmsg)
	{
		if (!RdPlugins.getDefault().getPlugins2().exec(108, coinPurse.getQueryRequestXml(cardno)))
		{
			if (isshowmsg)
				new MessageBox("零钱包账户查询失败");
			return null;
		}

		if (RdPlugins.getDefault().getPlugins2().getObject() == null || ((String) RdPlugins.getDefault().getPlugins2().getObject()).equals(""))
		{
			if (isshowmsg)
				new MessageBox("零钱包账户查询失败");
			return null;
		}

		String retXml = (String) RdPlugins.getDefault().getPlugins2().getObject();

		if (!coinPurse.parseQuery(retXml))
		{
			if (isshowmsg)
				new MessageBox("零钱包账户查询失败");
			return null;
		}
		return coinPurse.getVipCoin();
	}

	public VipCoinPayResult lockChangePocket(String djlb, String cardno, double money)
	{
		if (!RdPlugins.getDefault().getPlugins2().exec(109, coinPurse.getPayOrRechangeRequestXml(djlb, cardno, money)))
		{
			new MessageBox("零钱包支付锁定失败");
			return null;
		}

		if (RdPlugins.getDefault().getPlugins2().getObject() == null || ((String) RdPlugins.getDefault().getPlugins2().getObject()).equals(""))
		{
			new MessageBox("零钱包支付锁定失败");
			return null;
		}

		String retXml = (String) RdPlugins.getDefault().getPlugins2().getObject();

		if (!coinPurse.parsePayOrRechange(retXml))
		{
			new MessageBox("零钱包支付锁定失败");
			return null;
		}
		return coinPurse.getVipCoinPayRet();
	}

	public boolean confirmChangePocket(String referno)
	{
		if (!RdPlugins.getDefault().getPlugins2().exec(120, coinPurse.getConfirmPayRequestXml(referno)))
		{
			new MessageBox("零钱包支付确认失败");
			return false;
		}

		if (!RdPlugins.getDefault().getPlugins2().getErrorCode().equals("0"))
		{
			new MessageBox(RdPlugins.getDefault().getPlugins2().getErrorMsg());
			return false;
		}

		return true;
	}

	public boolean cancelChangePocket(String referNo)
	{
		if (!RdPlugins.getDefault().getPlugins2().exec(111, coinPurse.cancelPayRequestXml(referNo)))
		{
			new MessageBox("取消零钱包交易失败");
			return false;
		}

		if (!RdPlugins.getDefault().getPlugins2().getErrorCode().equals("0"))
		{
			new MessageBox(RdPlugins.getDefault().getPlugins2().getErrorMsg());
			return false;
		}

		return true;
	}

	public VipCoinPayResult rechangeChangePocket(String djlb, String cardno, double money)
	{
		if (!RdPlugins.getDefault().getPlugins2().exec(110, coinPurse.getPayOrRechangeRequestXml(djlb, cardno, money)))
		{
			new MessageBox("零钱包账户充值失败");
			return null;
		}

		if (RdPlugins.getDefault().getPlugins2().getObject() == null || ((String) RdPlugins.getDefault().getPlugins2().getObject()).equals(""))
		{
			new MessageBox("零钱包账户充值失败");
			return null;
		}

		String retXml = (String) RdPlugins.getDefault().getPlugins2().getObject();

		if (!coinPurse.parsePayOrRechange(retXml))
		{
			new MessageBox("零钱包账户充值失败");
			return null;
		}
		return coinPurse.getVipCoinPayRet();
	}

	// ===========================积分换购=========================
	public ExchangebleScore queryExchangebleScoreBalance(String cardno)
	{
		if (!RdPlugins.getDefault().getPlugins2().exec(113, vipScore.getExchangebleScoreBalanceRequestXml(cardno)))
		{
			new MessageBox("可兑换积分余额查询失败");
			return null;
		}

		if (RdPlugins.getDefault().getPlugins2().getObject() == null || ((String) RdPlugins.getDefault().getPlugins2().getObject()).equals(""))
		{
			new MessageBox("可兑换积分余额查询失败");
			return null;
		}

		String retXml = (String) RdPlugins.getDefault().getPlugins2().getObject();

		if (!vipScore.parseExchangebleScoreBalance(retXml))
		{
			new MessageBox("可兑换积分余额查询失败");
			return null;
		}

		return vipScore.getExchangebleScore();

	}

	public ExchangebleScoreResult lockExchangebleScore(String djlb, String cardno, double money, double point)
	{

		if (!RdPlugins.getDefault().getPlugins2().exec(114, vipScore.getLockExchangeScoreRequestXml(djlb, cardno, money, point)))
		{
			new MessageBox("积分立兑锁定失败");
			return null;
		}

		if (RdPlugins.getDefault().getPlugins2().getObject() == null || ((String) RdPlugins.getDefault().getPlugins2().getObject()).equals(""))
		{
			new MessageBox("积分立兑锁定失败");
			return null;
		}

		String retXml = (String) RdPlugins.getDefault().getPlugins2().getObject();

		if (!vipScore.parseLockExchangeScore(retXml))
		{
			new MessageBox("积分立兑锁定失败");
			return null;
		}

		return vipScore.getExchangebleScoreResult();

	}

	public boolean cancelExchangebleScore(String djlb, String cardno, double money, double point, String referno)
	{

		if (!RdPlugins.getDefault().getPlugins2().exec(115, vipScore.getConfirmOrCancelExchangeScoreRequestXml(djlb, cardno, money, point, referno)))
		{
			new MessageBox("积分立兑锁定失败");
			return false;
		}

		if (!RdPlugins.getDefault().getPlugins2().getErrorCode().equals("0"))
		{
			new MessageBox(RdPlugins.getDefault().getPlugins2().getErrorMsg());
			return false;
		}

		return true;

	}

	public boolean confirmExchangebleScore(String djlb, String cardno, double money, double point, String referno)
	{

		if (!RdPlugins.getDefault().getPlugins2().exec(116, vipScore.getConfirmOrCancelExchangeScoreRequestXml(djlb, cardno, money, point, referno)))
		{
			new MessageBox("积分立兑锁定失败");
			return false;
		}

		if (!RdPlugins.getDefault().getPlugins2().getErrorCode().equals("0"))
		{
			new MessageBox(RdPlugins.getDefault().getPlugins2().getErrorMsg());
			return false;
		}

		return true;

	}

	public Vector queryPointDetail(String card, String fromDate, String toDate)
	{
		if (vipScore == null)
			vipScore = new Hfhf_VipScore();

		if (!RdPlugins.getDefault().getPlugins2().exec(112, vipScore.getScoreDetailRequestXml(card, fromDate, toDate)))
		{
			new MessageBox("查询积分明细失败");
			return null;
		}

		if (RdPlugins.getDefault().getPlugins2().getObject() == null || ((String) RdPlugins.getDefault().getPlugins2().getObject()).equals(""))
		{
			new MessageBox("查询积分明细失败");
			return null;
		}

		String retXml = (String) RdPlugins.getDefault().getPlugins2().getObject();

		if (!vipScore.parseScoreDetail(retXml))
		{
			new MessageBox("查询积分明细失败");
			return null;
		}

		return vipScore.getScoreDetail();
	}

}
