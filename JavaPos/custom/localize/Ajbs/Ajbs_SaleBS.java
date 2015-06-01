package custom.localize.Ajbs;

import java.util.Vector;

import bankpay.Bank.Ajmis_PaymentBankFunc;
import bankpay.Bank.BaoYuan_PaymentBankFunc;
import bankpay.Bank.RkysAj_PaymentBankFunc;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bstd.Bstd_SaleBS;

public class Ajbs_SaleBS extends Bstd_SaleBS
{
	public boolean memberGrant()
	{
		if (isPreTakeStatus())
		{
			new MessageBox("预售提货状态下不允许重新刷卡");
			return false;
		}

		// 会员卡必须在商品输入前,则输入了商品以后不能刷卡,指定小票除外
		if (GlobalInfo.sysPara.customvsgoods == 'A' && saleGoods.size() > 0 && !isNewUseSpecifyTicketBack(false))
		{
			new MessageBox("必须在输入商品前进行刷会员卡\n\n请把商品清除后再重刷卡");
			return false;
		}

		// 读取会员卡
		HykInfoQueryBS bs = CustomLocalize.getDefault().createHykInfoQueryBS();
		/*
		 * String track2 = bs.readMemberCard(); if (track2 == null ||
		 * track2.equals("")) return false;
		 */

		// 查找会员卡
		CustomerDef cust = bs.findMemberCard("");

		if (cust == null)
			return false;

		// 调出原交易的指定小票退货模式允许重新刷卡改变当前会员卡(原卡可能失效、换卡等情况)
		if (isNewUseSpecifyTicketBack(false))
		{
			// 指定小票退仅记录卡号,不执行商品重算等处理
			curCustomer = cust;
			saleHead.hykh = cust.code;
			saleHead.hytype = cust.type;
			saleHead.str4 = cust.valstr2;

			return true;
		}
		else
		{
			// 记录会员卡
			return memberGrantFinish(cust);
		}
	}

/*	public CustomerVipZklDef getGoodsVIPZKL(int index)
	{
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);

		// 未刷卡
		if (!checkMemberSale() || (curCustomer == null)) { return null; }

		// 查询商品VIP折上折折扣率定义
		CustomerVipZklDef zklDef = new CustomerVipZklDef();

		if (DataService.getDefault().findVIPZKL(zklDef, curCustomer.code, curCustomer.type, goodsDef))
		{
			// 促销允许折上折且会员类允许折上折，VIP折扣才允许折上折
			if (!(popvipzsz == 'Y' && (zklDef.iszsz == 'Y' || zklDef.iszsz == 'A')))
				zklDef.iszsz = 'N';

			// ljj test trace
			PublicMethod.traceDebugLog("findVIPZKL OK:" + saleHead.fphm + "," + goodsDef.code + "," + zklDef.zkl + "," + zklDef.iszsz);

			// 有柜组和商品的VIP折扣定义
			return zklDef;
		}
		else
		{
			// 无限量序号
			zklDef.seqno = 0;

			// 促销允许折上折且会员类允许折上折，VIP折扣才允许折上折
			zklDef.iszsz = popvipzsz;
			curCustomer.zkl = 1;

			// 无柜组和商品的VIP折扣定义,以卡类别的折扣率为VIP打折标准
			zklDef.zkmode = '2'; // 比例模式
			zklDef.zkl = curCustomer.zkl;
			zklDef.zklareadn = 0;
			zklDef.zklareaup = 1;
			zklDef.inareazkl = curCustomer.zkl;
			zklDef.dnareazkl = curCustomer.zkl;
			zklDef.upareazkl = curCustomer.zkl;

			// ljj test trace
			PublicMethod.traceDebugLog("findVIPZKL NO:" + saleHead.fphm + "," + goodsDef.code + "," + zklDef.zkl + "," + zklDef.iszsz);

			return zklDef;
		}
	}*/

	public void doSaleFinshed(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment)
	{
		if (saleHead.hykh == null || saleHead.hykh.equals(""))
			return;

		if (salePayment == null || salePayment.size() == 0)
			return;

		SalePayDef pay = null;

		for (int i = 0; i < salePayment.size(); i++)
		{
			SalePayDef paytmp = (SalePayDef) salePayment.get(i);
			if (paytmp.flag == '2')
			{
				pay = paytmp;
				break;
			}
		}

		if (pay == null)
			return;

		int ret = new MessageBox("当笔交易存在找零,是否进行零钞转存?", null, true).verify();

		if (ret == GlobalVar.Key1)
		{
			StringBuffer buffer = new StringBuffer();
			buffer.append(String.valueOf(pay.je));

			if (new TextBox().open("请输入零钞转存金额", "金额", "", buffer, 0.00, pay.je, true, TextBox.DoubleInput, 4))
			{
				double money = Double.parseDouble(buffer.toString());

				if (Ajbs_ICCard.getDefault().saveChangeMoney(pay.je, GlobalInfo.posLogin.gh, money))
				{
					pay.num2 = ManipulatePrecision.doubleConvert(money, 2, 2);

				}
			}
		}

	}

	//初始化银联及IC卡计数变量
	public void initOneSale(String type)
	{
		
		Ajbs_ICCard.getDefault().clear();
		
		Ajmis_PaymentBankFunc.billcount = 0;
		RkysAj_PaymentBankFunc.printtimes = 0;
		BaoYuan_PaymentBankFunc.isOneBill = false;
		BaoYuan_PaymentBankFunc.printcount = 0;
		
		super.initOneSale(type);
	}
}
