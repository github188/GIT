package custom.localize.Jdhx;

import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bstd.Bstd_DataService;

public class Jdhx_DataService extends Bstd_DataService
{

	protected String convertTradeType(String djlb)
	{
		if (djlb.equals(SellType.JF_FK))
			return "m";
		else if (djlb.equals(SellType.JF_FK_BACK))
			return "n";
		else if (djlb.equals("m"))
			return SellType.JF_FK;
		else if (djlb.equals("n"))
			return SellType.JF_FK_BACK;
		else if (djlb.equals(SellType.GROUPBUY_SALE))
			return "a";
		else if (djlb.equals(SellType.GROUPBUY_BACK))
			return "b";
		else if (djlb.equals("a"))
			return SellType.GROUPBUY_SALE;
		else if (djlb.equals("b"))
			return SellType.GROUPBUY_BACK;
		else
			return djlb;
	}

	public boolean sendSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, Sqldb sql)
	{
		if (!GlobalInfo.isOnline)
			return false;

		boolean again;

		// 送网小票返回数据
		Vector retValue = new Vector();

		// sql对象为空,非重发小票
		if (sql == null)
		{
			again = false;
		}
		else
		{
			again = true;
		}

		// 发送小票
		int result = 0;

		saleHead.djlb = convertTradeType(saleHead.djlb);
		result = NetService.getDefault().sendSaleData(saleHead, saleGoods, salePayment, retValue);

		// 非重发如果返回不为0，表示小票发送失败
		if (!again && result != 0)
			return false;

		// 重发小票，如果返回为2表示小票已存在，0表示成功，其他为送网失败
		if (again && result != 0 && result != 2)
			return false;

		// 得到返回数据,可对返回数据进行处理
		if (retValue.size() > 0)
		{
			String memo = retValue.elementAt(0).toString();
			double value = Double.parseDouble(CommonMethod.isNull(retValue.elementAt(1).toString(), "0"));

			updateSendSaleData(saleHead, memo, value, sql);
		}

		// 发送小票成功后更新小票送网标志
		if (sql == null)
		{
			// 更新小票送网标志
			AccessDayDB.getDefault().updateSaleBz(saleHead.fphm, 1, 'Y');
		}
		else
		{
			// 重发未送网小票时，不能用sql的execute(sqltext)方法
			// 和前面selectData换一个对象执行,否则冲突
			// 更新小票送网标志
			sql.setSql("update SALEHEAD set netbz = 'Y' where syjh = '" + saleHead.syjh + "' and fphm = " + String.valueOf(saleHead.fphm));
			sql.executeSql();
		}

		saleHead.djlb = convertTradeType(saleHead.djlb);
		// 续费，团购交易不用算积分
		//if (!SellType.isJF(saleHead.djlb) && !SellType.isGroupbuy(saleHead.djlb))
		if (!SellType.isGroupbuy(saleHead.djlb))//水费刷了会员卡，也算积分 for ybk for 客户
		{
			// 需要将小票发送到独立会员服务器
			if (GlobalInfo.sysPara.sendsaletocrm == 'Y')
			{
				sendSaleDataToMemberDB(saleHead, saleGoods, salePayment, again);
			}

			// 需要联网实时计算返券
			if (GlobalInfo.sysPara.calcfqbyreal == 'Y')
			{
				getSellRealFQ(saleHead);
			}

			if (GlobalInfo.sysPara.calcmystorecouponbyreal != 'N')
			{
				CreatePayment.getDefault().getPaymentMyStore().getMyStoreCoupon(saleHead, saleGoods, salePayment);
			}

			// 需要联网实时计算积分
			if (GlobalInfo.sysPara.calcjfbyconnect == 'Y' || GlobalInfo.sysPara.calcjfbyconnect == 'A')
			{
				getCustomerSellJf(saleHead, saleGoods, salePayment);
			}
		}

		return true;
	}
	
	//退货时 将缴水费(m)，退购类型(a) 转换成对应的JavaPos中的类型
	public boolean getBackSaleInfo(String syjh, String fphm, SaleHeadDef shd, Vector saleDetailList, Vector payDetail)
	{
		try
		{
			if (!GlobalInfo.isOnline)
			{
				if ((syjh == null) || syjh.trim().equals(""))
				{
					new MessageBox(Language.apply("脱网状态下不支持后台退货!"));
				}
				else
				{
					new MessageBox(Language.apply("脱网状态下不支持指定小票退货!"));
				}

				return false;
			}

			if (!NetService.getDefault().getBackSaleInfo(syjh, fphm, shd, saleDetailList, payDetail)) { return false; }
			
			//转换对应的类型 
			shd.djlb = convertTradeType(shd.djlb);

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
	}

	// 只有在联网时，才去获得促销信息 
	public boolean getU51Info(String posId, String mkt, SaleHeadDef saleHead)
	{
		if (!GlobalInfo.isOnline)
			return false;
		if ( ((Jdhx_NetService) NetService.getDefault()).getU51Info(posId, mkt, saleHead)) return true;
		
		return false;
	}
	
}
