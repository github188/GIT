package custom.localize.Jcgj;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Cmls.Cmls_DataService;

public class Jcgj_DataService extends Cmls_DataService
{
	// 获取小票实时积分
	public void getCustomerSellJf(SaleHeadDef saleHead)
	{
		String[] row = new String[4];

		if ((saleHead.hykh != null) && (saleHead.hykh.length() > 0))
		{
			if (NetService.getDefault().getCustomerSellJf(row, saleHead.mkt, saleHead.syjh, String.valueOf(saleHead.fphm)))
			{
				saleHead.bcjf = Convert.toDouble(row[0]);
				//				saleHead.ljjf = Convert.toDouble(row[1]);
				saleHead.str5 = row[2];
				saleHead.num4 = Convert.toDouble(row[3]);

				if (GlobalInfo.sysPara.sendhyjf == 'Y')
				{
					if (!sendHykJf(saleHead))
					{
						new MessageBox("本笔积分同步失败无法获得累计积分\n请到会员中心查询累计积分!");
					}
				}

				if (saleHead.bcjf != 0)
				{
					StringBuffer sb = new StringBuffer();
					//					sb.append("累计积分: " + Convert.appendStringSize("", ManipulatePrecision.doubleToString(saleHead.ljjf), 0, 10, 10, 1) + "\n");
					if (saleHead.bcjf != 0) sb.append("本次积分: "
							+ Convert.appendStringSize("", ManipulatePrecision.doubleToString(saleHead.bcjf), 0, 10, 10, 1) + "\n");
					if (saleHead.num4 != 0) sb.append("倍享积分: "
							+ Convert.appendStringSize("", ManipulatePrecision.doubleToString(saleHead.num4), 0, 10, 10, 1) + "\n");
					new MessageBox(sb.toString());
				}

				AccessDayDB.getDefault().updateSaleJf(saleHead.fphm, 1, saleHead.bcjf, saleHead.ljjf, String.valueOf(saleHead.num4));

				// 调用银石接口上传积分
				if (saleHead.bcjf != 0)
				{
					Jcgj_Svc svc = new Jcgj_Svc("svc_score", saleHead, "");
					if (svc.doYsCard(null))
					{
						svc = new Jcgj_Svc("svc_commit", null, "");
						if (!svc.doYsCard(null))
						{
							new MessageBox(svc.getMethordName() + " 上传积分到YS系统失败，请到服务台补录积分");
						}
						else
						{
							saleHead.num5 = 1;
							saleHead.ljjf = saleHead.bcjf + saleHead.num2;
						}
					}
					else
					{
						new MessageBox(svc.getMethordName() + " 写入积分到YS系统失败，请到服务台补录积分");
					}
				}
			}
			else
			{
				saleHead.bcjf = 0;
				new MessageBox("计算本笔交易小票积分失败\n请到服务台补录积分!");
			}
		}
	}

	public boolean getCustomer(CustomerDef cust, String track)
	{
		Jcgj_Svc svc;
		// 选择输入手机号时
		if (Jcgj_Svc.inputType == 3)
		{
			svc = new Jcgj_Svc("svc_inq_by_phone", null, "");
		}
		// 选择刷卡时
		else
		{
			svc = new Jcgj_Svc("svc_inq_vip", null, "");
			/*
			if (((Jcgj_SaleBS)GlobalInfo.saleform.sale.saleBS).isHykQuery || GlobalInfo.saleform.sale.saleBS.saleGoods == null || GlobalInfo.saleform.sale.saleBS.saleGoods.size() < 1)
			{
				svc = new Jcgj_Svc("svc_inq_vip", null, "");
			}
			else svc = new Jcgj_Svc("svc_inq", null, "");
			*/
		}

		Jcgj_YsCardDef card = new Jcgj_YsCardDef();
		if (svc.doYsCard(card))
		{
			if (cust == null) cust = new CustomerDef();
			if (super.getCustomer(cust, card.cardNo))
			{
				cust.type = "0" + card.level; // 卡类别
				cust.track = card.cardNo; // 磁道
				cust.valuememo = card.jf; // 可用积分
				cust.name = card.name; // 持卡人姓名
				cust.str1 = card.birthday; // 持卡人生日
				return true;
			}
			else return false;
		}
		else
		{
			return false;
		}
	}
}
