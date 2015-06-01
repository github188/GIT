package custom.localize.Zspj;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.AxisWebService;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Zsbh.Zsbh_NetService;
import custom.localize.Zsbh.Zsbh_WebServicePaymentMzk;

public class Zspj_WebServicePaymentMzk extends Zsbh_WebServicePaymentMzk
{
	public Zspj_WebServicePaymentMzk()
	{
		super();
	}

	public Zspj_WebServicePaymentMzk(PayModeDef mode, SaleBS sale)
	{
		super(mode, sale);
	}

	public Zspj_WebServicePaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		super(pay, head);
	}

	public boolean findMzkInfo(String track1, String track2, String track3)
	{
		mzkreq.str1 = ManipulateDateTime.getCurrentDateTime();

		setRequestDataByFind(track1, track2, track3);

		if (ConfigClass.DebugMode)
			System.out.println("正在查询卡余额: 发票号码[" + mzkreq.fphm + "] 消费序号[" + mzkreq.seqno + "] 交易类型[" + mzkreq.type + "] 卡号[" + mzkreq.track2 + "]");

		Object ret = ((AxisWebService) GlobalInfo.axis).executeFunction(CmdDef.FINDMZKINFO, mzkreq);

		if (ret == null)
			return false;

		double money = chkMzkQryRetInfo(ret);

		if (money == -999)
			return false;
		else if (money == -666)
			money = Double.parseDouble(ret.toString());

		if (ConfigClass.DebugMode)
			System.out.println("查询卡余额结束: 发票号码[" + mzkreq.fphm + "] 消费序号[" + mzkreq.seqno + "] 交易类型[" + mzkreq.type + "] 卡号[" + mzkreq.track2 + "] 余额[" + money + "]");

		if (money < 0)
		{
			new MessageBox("查询余额失败,无法进行交易");
			return false;
		}

		mzkreq.str2 = "Y";
		mzkreq.memo = "查余成功";

		mzkret.cardno = mzkreq.track2;
		mzkret.cardname = ((paymode != null) ? paymode.name : "");
		mzkret.status = "Y";
		mzkret.money = money;
		mzkret.ye = money;

		// 记录查余日志
		((Zspj_NetService) NetService.getDefault()).sendGongMaoMzkLog(mzkreq, mzkret);
		return true;
	}

	public boolean mzkAccount(boolean isAccount)
	{
		do
		{
			// 退货交易卡号为空时提示刷卡
			paynoMsrflag = false;
			if (!paynoMSR())
				return false;

			// 设置交易类型,isAccount=true是记账,false是撤销
			if (isAccount)
			{
				if (SellType.SELLSIGN(salehead.djlb) > 0)
					mzkreq.type = "01"; // 消费,减
				else
					mzkreq.type = "03"; // 退货,加
			}
			else
			{
				if (SellType.SELLSIGN(salehead.djlb) > 0)
					mzkreq.type = "03"; // 退货,加
				else
					mzkreq.type = "01"; // 消费,减
			}

			// 保存交易数据进行交易
			if (!setRequestDataByAccount())
			{
				if (paynoMsrflag)
				{
					salepay.payno = "";
					continue;
				}
				return false;
			}

			// 先写冲正文件
			if (!writeMzkCz())
			{
				if (paynoMsrflag)
				{
					salepay.payno = "";
					continue;
				}
				return false;
			}

			// 记录面值卡交易日志
			bld = mzkAccountLog(false, null, mzkreq, null);

			// 记录交易时间
			mzkreq.str1 = ManipulateDateTime.getCurrentDateTime();

			if (ConfigClass.DebugMode)
				System.out.println("正在发送交易请求: 发票号码[" + mzkreq.fphm + "] 消费序号[" + mzkreq.seqno + "] 交易类型[" + mzkreq.type + "] 卡号[" + mzkreq.track2 + "] 消费金额[" + mzkreq.je + "]");

			// 发送交易请求
			Object ret = ((AxisWebService) GlobalInfo.axis).executeFunction(CmdDef.SENDMZK, mzkreq);

			if (!((AxisWebService) GlobalInfo.axis).isCheckSuccess(CmdDef.SENDMZK, ret))
			{
				if (paynoMsrflag)
				{
					salepay.payno = "";
					continue;
				}

				mzkreq.str2 = "N";
				if (mzkreq.type == "03")
					mzkreq.memo = "退货失败";
				else if (mzkreq.type == "01")
					mzkreq.memo = "消费失败";

				if (ConfigClass.DebugMode)
					System.out.println("交易结果: 发票号码[" + mzkreq.fphm + "] 消费序号[" + mzkreq.seqno + "] 交易类型[" + mzkreq.type + "] 卡号[" + mzkreq.track2 + "] 消费金额[" + mzkreq.je + "] 交易失败");

				((Zspj_NetService) NetService.getDefault()).sendGongMaoMzkLog(mzkreq, mzkret);
				return false;
			}

			mzkreq.str2 = "Y";
			if (mzkreq.type == "03")
				mzkreq.memo = "退货成功";
			else if (mzkreq.type == "01")
				mzkreq.memo = "消费成功";

			if (ConfigClass.DebugMode)
				System.out.println("交易结果: 发票号码[" + mzkreq.fphm + "] 消费序号[" + mzkreq.seqno + "] 交易类型[" + mzkreq.type + "] 卡号[" + mzkreq.track2 + "] 消费金额[" + mzkreq.je + "] 交易成功");

			((Zspj_NetService) NetService.getDefault()).sendGongMaoMzkLog(mzkreq, mzkret);

			// 记录应答信息, batch标记本付款方式已记账,这很重要
			saveAccountMzkResultToSalePay();

			// 查询卡余额
			queryCardMoney();

			// 记账完成操作,可用于记录记账日志或其他操作
			return mzkAccountFinish(isAccount, bld);

		} while (true);
	}

	protected void queryCardMoney()
	{
		try
		{
			double YPayJe = mzkreq.je; // 原消费金额
			double YYe = mzkret.ye; // 原余额
			long seqno = mzkreq.seqno; // 原消费序号

			setRequestDataByFind("", mzkreq.track2, "");

			if (ConfigClass.DebugMode)
				System.out.println("交易结束余额查询: 发票号码[" + mzkreq.fphm + "] 消费序号[" + mzkreq.seqno + "] 交易类型[" + mzkreq.type + "] 卡号[" + mzkreq.track2 + "] 消费前余额[" + mzkret.ye + "]");

			Object ret = ((AxisWebService) GlobalInfo.axis).executeFunction(CmdDef.FINDMZKINFO, mzkreq);

			mzkreq.seqno = seqno;

			// 将算出来的卡余额为负数,代表不是从接口中获取,打印时可以进行判断
			salepay.kye = 1000 * -1;

			if (ret != null)
			{
				double money = chkMzkQryRetInfo(ret);

				if (money == -666)
					Double.parseDouble(ret.toString());

				if (money >= 0)
				{
					if (ConfigClass.DebugMode)
						System.out.println("交易结束余额查询: 发票号码[" + mzkreq.fphm + "] 消费序号[" + mzkreq.seqno + "] 交易类型[" + mzkreq.type + "] 卡号[" + mzkreq.track2 + "] 消费后余额[" + money + "]");

					// salepay.kye =
					// ManipulatePrecision.sub(YYe,YPayJe);//(mzkret.ye,mzkreq.je);
					if (SellType.ISBACK(saleBS.saleHead.djlb))
					{
						salepay.kye = ManipulatePrecision.add(YYe, YPayJe);// 退货时，新余额=原余额+退货金额
					}
					else
					{
						salepay.kye = ManipulatePrecision.sub(YYe, YPayJe);// 非退货时，新余额=原余额-消费金额
					}

					mzkret.ye = salepay.kye;

					if ((ManipulatePrecision.doubleCompare(money, salepay.kye, 4) != 0))
					{
						if (bld != null)
							bld.retmsg = "查询余额:" + money + "计算余额为:" + salepay.kye + "余额不相等";

						mzkreq.str2 = "N";
						mzkreq.memo = "消费查余不平-工贸余额(" + String.valueOf(money) + ")";

						new MessageBox("查询余额为: " + money + "\n计算余额为: " + salepay.kye + "\n以查询的余额为准");
					}
					else
					{
						mzkreq.str2 = "Y";
						mzkreq.memo = "消费查余成功";
					}
					salepay.kye = money;

					// 记录查余日志
					((Zspj_NetService) NetService.getDefault()).sendGongMaoMzkLog(mzkreq, mzkret);
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public boolean sendAccountCzData(MzkRequestDef req, String czfile, String czname)
	{
		// 根据冲正文件的原交易类型转换冲正数据包
		if (req.type.equals("01"))
		{
			req.type = "02"; // 消费冲正,加
		}
		else if (req.type.equals("03"))
		{
			req.type = "04"; // 退货冲正,减
		}
		else
		{
			new MessageBox("冲正文件的交易类型无效，请检查冲正文件");
			return false;
		}

		// 记录面值卡交易日志
		BankLogDef bld = mzkAccountLog(false, null, req, null);

		// 冲正记录
		String czmsg = "WebService发起[" + czname + "]冲正:" + req.type + "," + req.fphm + "," + req.track2 + "," + ManipulatePrecision.doubleToString(req.je) + ",返回:";

		if (ConfigClass.DebugMode)
			System.out.println("正在发起冲正: 类型[" + req.type + "] 小票号[" + req.fphm + "] 卡号[" + req.track2 + "] 金额["+req.je+"]");

		mzkreq.str1 = ManipulateDateTime.getCurrentDateTime();

		// 发送冲正交易
		Object ret = ((AxisWebService) GlobalInfo.axis).executeFunction(CmdDef.SENDMZK, req);
		
		if (ConfigClass.DebugMode)
			System.out.println("冲正结束: 类型[" + req.type + "] 小票号[" + req.fphm + "] 卡号[" + req.track2 + "] 金额["+req.je+"]");
		
		if (!((AxisWebService) GlobalInfo.axis).isCheckSuccess(CmdDef.SENDMZK, ret))
		{
			// 记录日志表明发送过冲正数据
			AccessDayDB.getDefault().writeWorkLog(czmsg + "失败", StatusType.WORK_SENDERROR);

			mzkreq.str2 = "N";
			mzkreq.memo ="冲正失败";
			((Zspj_NetService) NetService.getDefault()).sendGongMaoMzkLog(mzkreq, mzkret);
			return false;
		}
		else
		{
			// 记录应答日志
			mzkAccountLog(true, bld, req, null);

			// 记录日志表明发送过冲正数据
			AccessDayDB.getDefault().writeWorkLog(czmsg + "成功", StatusType.WORK_SENDERROR);

			mzkreq.str2 = "Y";
			mzkreq.memo ="冲正成功";
			((Zspj_NetService) NetService.getDefault()).sendGongMaoMzkLog(mzkreq, mzkret);

			// 冲正发送成功,删除冲正文件
			deleteMzkCz(czfile);

			return true;
		}
	}

}
