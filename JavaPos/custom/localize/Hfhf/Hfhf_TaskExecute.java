package custom.localize.Hfhf;

import java.sql.ResultSet;
import java.util.Vector;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Global.TaskExecute;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

public class Hfhf_TaskExecute extends TaskExecute
{
	public boolean sendHykJf(String keytext)
	{
		// 将关键字分解为开始日期和小票号
		String[] rq = keytext.split(",");
		if (rq.length < 2)
			return true;

		ProgressBox pb = new ProgressBox();

		try
		{
			ManipulateDateTime dt = new ManipulateDateTime();
			pb.setText("正在发送 " + rq[0] + " 的小票积分销售数据.....");

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
				boolean bOK = true;
				SaleHeadDef salehead = new SaleHeadDef();
				Vector salegoods = new Vector();

				rs = sql.selectData("select * from SALEHEAD where fphm = " + rq[1] + " order by fphm");

				if (rs != null && rs.next())
				{
					if (!sql.getResultSetToObject(salehead)) { return false; }

					sql.resultSetClose();

					rs = sql.selectData("select * from SALEGOODS where syjh = '" + salehead.syjh + "' and fphm = " + rq[1] + " order by rowno");
					while (rs != null && rs.next())
					{
						SaleGoodsDef sg = new SaleGoodsDef();

						if (!sql.getResultSetToObject(sg))
						{
							bOK = false;
							break;
						}

						salegoods.add(sg);
					}

					sql.resultSetClose();

					if (!bOK)
					{
						new MessageBox("重发小票积分到会员服务器失败:商品明细读取失败");
						return false;
					}

					pb.setText("正在发送 " + rq[0] + " 的 " + String.valueOf(salehead.fphm) + " 号小票时时积分...");

					// 发送小票时时积分
					Hfhf_DataService dataService = (Hfhf_DataService) DataService.getDefault();
					boolean ret = dataService.sendHykJf(salehead, salegoods, null, true);

					if (!ret)
					{
						AccessDayDB.getDefault().writeWorkLog("重发小票积分到会员服务器失败:" + salehead.fphm + "," + salehead.rqsj, StatusType.WORK_SENDERROR);

						if (!NetService.getDefault().isStopService())
							new MessageBox("未上传实时积分小票号:" + salehead.fphm + "\n小票生成日期:" + salehead.rqsj + "\n记下信息请与信息部联系!", null, false);

						return false;
					}
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
}
