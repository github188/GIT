package custom.localize.Gzbh;

import java.sql.ResultSet;
import java.util.Vector;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
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
import com.efuture.javaPos.Struct.SalePayDef;

public class Gzbh_TaskExecute extends TaskExecute
{
	//发送小票到会员服务器里面
	public boolean sendInvoiceToExtend(String keytext)
	{
		// 将关键字分解为开始日期和小票号
		String[] rq = keytext.split(",");
		if (rq.length < 2) return true;

		boolean allsendok = true;

		//
		ProgressBox pb = new ProgressBox();

		try
		{
			ManipulateDateTime dt = new ManipulateDateTime();
			pb.setText("正在发送 " + rq[0] + " 的销售数据.....");

			// 打开每日数据库
			Sqldb sql = null;
			if (dt.compareDate(rq[0], GlobalInfo.balanceDate) != 0) sql = LoadSysInfo.getDefault().loadDayDB(rq[0]);
			else sql = GlobalInfo.dayDB;
			if (sql != null)
			{
				ResultSet rs = null;
				SaleHeadDef salehead = new SaleHeadDef();
				Vector salegoods = new Vector();
				Vector salepay = new Vector();

				try
				{
					boolean bOK;
					bOK = true;
					rs = sql.selectData("select * from SALEHEAD where fphm = " + rq[1] + " order by fphm");
					if (rs != null && rs.next())
					{
						salegoods.removeAllElements();
						salepay.removeAllElements();

						if (!sql.getResultSetToObject(salehead))
						{
							allsendok = false;
							bOK = false;
							return bOK;
						}
						sql.resultSetClose();

						//
						pb.setText("正在发送 " + rq[0] + " 的 " + String.valueOf(salehead.fphm) + " 号小票到CRM...");

						// 读取商品明细
						rs = sql.selectData("select * from SALEGOODS where syjh = '" + salehead.syjh + "' and fphm = "
								+ String.valueOf(salehead.fphm) + " order by rowno");
						while (rs != null && rs.next())
						{
							SaleGoodsDef sg = new SaleGoodsDef();

							if (!sql.getResultSetToObject(sg))
							{
								allsendok = false;
								return bOK;
							}

							salegoods.add(sg);
						}
						sql.resultSetClose();

						// 读取付款明细
						rs = sql.selectData("select * from SALEPAY where syjh = '" + salehead.syjh + "' and fphm = " + String.valueOf(salehead.fphm)
								+ " order by rowno");
						while (rs != null && rs.next())
						{
							SalePayDef sp = new SalePayDef();

							if (!sql.getResultSetToObject(sp))
							{
								allsendok = false;
								bOK = false;
								return bOK;
							}

							salepay.add(sp);
						}
						sql.resultSetClose();

						//发送小票到CRM服务器
						salehead.str1 = "N";
						int ret = NetService.getDefault().sendExtendSaleData(salehead, salegoods, salepay, null);
						if (ret != 0 && ret != 2)
						{
							AccessDayDB.getDefault()
										.writeWorkLog("重发未送网小票到会员服务器失败:" + salehead.fphm + "," + salehead.rqsj, StatusType.WORK_SENDERROR);

							new MessageBox("未上传到会员服务器小票号:" + salehead.fphm + "\n小票生成日期:" + salehead.rqsj + "\n记下信息请与信息部联系!", null, false);

							return false;
						}
						else
						{
							return true;
						}
					}
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
				finally
				{
					sql.resultSetClose();

					// 关闭数据库
					if (sql != null && dt.compareDate(rq[0], GlobalInfo.balanceDate) != 0)
					{
						sql.Close();
					}
				}
			}
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			pb.close();
		}

		return allsendok;
	}

	public boolean openDrawGrant()
	{
		return true;
	}
}
