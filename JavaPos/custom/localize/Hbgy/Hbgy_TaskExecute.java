package custom.localize.Hbgy;

import java.sql.ResultSet;
import java.util.Vector;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Global.TaskExecute;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.TasksDef;

public class Hbgy_TaskExecute extends TaskExecute
{
	public boolean sendTotalAmount(String keytext)
	{
		// 将关键字分解为开始日期和小票号
		String[] rq = keytext.split(",");
		if (rq.length < 2)
			return true;

		ProgressBox pb = new ProgressBox();

		try
		{
			ManipulateDateTime dt = new ManipulateDateTime();

			// 打开每日数据库
			Sqldb sql = null;
			if (dt.compareDate(rq[0], GlobalInfo.balanceDate) != 0)
			{
				sql = LoadSysInfo.getDefault().loadDayDB(rq[0]);
			}
			else
			{
				sql = GlobalInfo.dayDB;
			}

			if (sql != null)
			{
				ResultSet rs = null;
				SaleHeadDef salehead = new SaleHeadDef();
				Vector salegoods = new Vector();
				rs = sql.selectData("select * from SALEHEAD where fphm = " + rq[1] + " order by fphm");

				if (rs != null && rs.next())
				{
					if (!sql.getResultSetToObject(salehead))
						return false;

					sql.resultSetClose();

					rs = sql.selectData("select * from SALEGOODS where syjh = '" + salehead.syjh + "' and fphm = " + String.valueOf(salehead.fphm) + " order by rowno");
					while (rs != null && rs.next())
					{
						SaleGoodsDef sg = new SaleGoodsDef();

						if (!sql.getResultSetToObject(sg))
							return false;

						salegoods.add(sg);
					}
					sql.resultSetClose();
				}

				pb.setText("正在发送 " + rq[0] + " 的 " + String.valueOf(salehead.fphm) + " 号小票员工卡/惠发卡消费总金额...");
				boolean ret = ((Hbgy_NetService) NetService.getDefault()).sendTotalAmount(salehead, salegoods);

				if (!ret)
				{
					AccessDayDB.getDefault().writeWorkLog("重发小票员工卡/惠发卡消费总金额失败:" + salehead.fphm + "," + salehead.rqsj, StatusType.WORK_SENDERROR);
					return false;
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
		finally
		{
			pb.close();
		}
	}

	public boolean executeTask(TasksDef task)
	{
		if (task.type == StatusType.TASK_SENDTOTALAMOUNT)
			sendTotalAmount(task.keytext);
		
		return super.executeTask(task);
	}
}
