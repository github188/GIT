package custom.localize.Zspj;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Global.TaskExecute;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.TasksDef;

import custom.localize.Bstd.Bstd_DataService;

public class Zspj_DataService extends Bstd_DataService
{
	public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret)
	{
		// 中商面值卡不判断是否联网
		return NetService.getDefault().sendMzkSale(req, ret);
	}

	public void sendSaleDataToMemberDB(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, boolean again)
	{
		// 会员卡号为空且issendcrmnohyk=N时，则不传至CRM，否则其它情况下则传
		if (saleHead.hykh.equals("") && GlobalInfo.sysPara.issendcrmnohyk == 'N')
			return;

		boolean sendok = true;

		int result = NetService.getDefault().sendExtendSaleData(saleHead, saleGoods, salePayment, null);

		// 非重发如果返回不为0，表示小票发送失败
		if (!again && result != 0)
			sendok = false;

		// 重发小票，如果返回为2表示小票已存在，0表示成功，其他为送网失败
		if (again && result != 0 && result != 2)
			sendok = false;

		// 送网失败，记录小票未发送到CRM数据源的命令任务
		if (!sendok)
		{
			// 如果是重传则检查一下CRM标志是否为Y
			if (again)
			{
				if (((Zspj_AccessDayDB) AccessDayDB.getDefault()).getCrmResendFlag(saleHead.fphm))
					return;
			}

			AccessDayDB.getDefault().updateSaleBz(saleHead.fphm, 4, 'N');
			AccessLocalDB.getDefault().writeTask(StatusType.TASK_SENDINVTOEXTEND, GlobalInfo.balanceDate + "," + saleHead.fphm);

			// 提示
			if (!NetService.getDefault().isStopService())
				new MessageBox("上传小票到会员服务器失败\n请到会员中心查询!");

		}
		else
		{
			AccessDayDB.getDefault().updateSaleBz(saleHead.fphm, 4, 'Y');
		}
		// super.sendSaleDataToMemberDB(saleHead, saleGoods, salePayment,
		// again);
	}
	
	public void getCustomerSellJf(SaleHeadDef saleHead)
	{
		// 会员卡号为空，则不算积分
		if (saleHead.hykh.equals(""))
			return;

		// super.getCustomerSellJf(saleHead);

		String[] row = new String[4];

		if ((saleHead.hykh != null) && (saleHead.hykh.length() > 0))
		{
			if (NetService.getDefault().getCustomerSellJf(row, GlobalInfo.sysPara.mktcode, saleHead.syjh, String.valueOf(saleHead.fphm), saleHead.hykh, saleHead.hytype))
			{
				saleHead.bcjf = Double.parseDouble(row[0]);
				saleHead.ljjf = Double.parseDouble(row[1]);
				saleHead.num4 = Convert.toDouble(row[3]);
				saleHead.str5 = row[2];

				if (GlobalInfo.sysPara.sendhyjf == 'Y')
				{
					if (!sendHykJf(saleHead))
					{
						new MessageBox("本笔积分同步失败无法获得累计积分\n请到会员中心查询累计积分!");
					}
				}

				if ((Math.abs(saleHead.bcjf) > 0 || Math.abs(saleHead.ljjf) > 0) && GlobalInfo.sysPara.calcjfbyconnect == 'Y')
				{
					StringBuffer sb = new StringBuffer();
					sb.append("本笔交易存在积分\n");
					sb.append("本次积分: " + Convert.appendStringSize("", String.valueOf(saleHead.bcjf), 0, 10, 10, 1) + "\n");
					sb.append("倍享积分: " + Convert.appendStringSize("", String.valueOf(saleHead.num4), 0, 10, 10, 1) + "\n");
					sb.append("累计积分: " + Convert.appendStringSize("", String.valueOf(saleHead.ljjf), 0, 10, 10, 1));

					new MessageBox(sb.toString());
				}

				((Zspj_AccessDayDB)AccessDayDB.getDefault()).updateSaleJf(saleHead.fphm, 5, saleHead.bcjf, saleHead.ljjf,saleHead.num4,"");
				
				if (!((Zspj_NetService) NetService.getDefault()).sendJfToPos(saleHead.fphm, saleHead.ljjf, saleHead.bcjf,saleHead.num4,saleHead.hykh))
					new MessageBox("同步积分到POS库失败");
			}
			else
			{
				saleHead.bcjf = 0;
				new MessageBox("计算本笔交易小票积分失败\n请到会员中心查询积分!");
			}
		}
	}

	// 查找满减满增促销
	public boolean findPopRuleCRM(GoodsPopDef popDef, String code, String gz, String uid, String rulecode, String catid, String ppcode, String time, String cardno, String cardtype, String djlb)
	{
		if (GlobalInfo.isOnline)
		{
			Zspj_NetService netservice = (Zspj_NetService) NetService.getDefault();
			return netservice.findPopRuleCRM(popDef, code, gz, uid, rulecode, catid, ppcode, time, cardno, cardtype, NetService.getDefault().getMemCardHttp(CmdDef.FINDCRMPOP));
		}

		return false;
	}

	public void execHistoryTask()
	{
		TasksDef task = null;
		int sendCrmdOK = 0, sendCrmFailed = 0;
		int sendPosOK = 0, sendPosFailed = 0;

		long seqno = 0;
		boolean ret = false;
		// 强制写入小票及缴款发送任务
		AccessLocalDB.getDefault().writeTask(StatusType.TASK_SENDINVOICE, TaskExecute.getKeyTextByBalanceDate());
		AccessLocalDB.getDefault().writeTask(StatusType.TASK_SENDPAYJK, TaskExecute.getKeyTextByBalanceDate());

		// 执行任务表未完成任务
		while ((task = AccessLocalDB.getDefault().readTask(seqno)) != null)
		{
			// 读取下一个seqno任务
			seqno = task.seqno;

			if (task.type == StatusType.TASK_SENDINVTOEXTEND)
			{
				ret = TaskExecute.getDefault().executeTask(task);

				if (!ret)
					sendCrmFailed++;
				else
					sendCrmdOK++;
			}
			else if (task.type == StatusType.TASK_SENDINVOICE)
			{
				ret = TaskExecute.getDefault().executeTask(task);
				if (!ret)
					sendPosFailed++;
				else
					sendPosOK++;
			}
			else
				ret = TaskExecute.getDefault().executeTask(task);

			// 任务执行成功则删除任务 或者 不是一个必要任务先删除,避免任务重复执行
			if (ret || !StatusType.isMustTask(task.type))
			{
				AccessLocalDB.getDefault().deleteTask(task.seqno);
			}
		}

		if (sendPosFailed > 0)
			new MessageBox("上传小票到POS服务器成功笔数: " + String.valueOf(sendPosOK) + "\n上传小票到POS服务器失败笔数: " + String.valueOf(sendPosFailed));

		if (sendCrmFailed > 0)
			new MessageBox("上传小票到会员服务器成功笔数: " + String.valueOf(sendCrmdOK) + "\n上传小票到会员服务器失败笔数: " + String.valueOf(sendCrmFailed));

		// 发送付款冲正
		CreatePayment.getDefault().sendAllPaymentCz();
	}
}
