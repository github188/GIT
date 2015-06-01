package custom.localize.Bcsf;

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
import com.efuture.javaPos.Struct.SaleAppendDef;
import com.efuture.javaPos.Struct.SaleHeadDef;


public class Bcsf_TaskExecute extends TaskExecute
{
	public boolean sendAllSaleAppendData(String keytext)
	{
		//	将关键字分解为开始日期和小票号
		String[] rq = keytext.split(",");
		if (rq.length < 2) return true;

		Vector saleappend = null;		
		
		ProgressBox pb = new ProgressBox();

		try
		{
			ManipulateDateTime dt = new ManipulateDateTime();
			pb.setText("正在发送 " + rq[0] + " 发票数据.....");

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
		
				try
				{
					// 读取小票头
					rs = sql.selectData("select * from SALEHEAD where fphm = " + rq[1] + " order by fphm");
					
					if (rs != null && rs.next())
					{
						if (!sql.getResultSetToObject(salehead))
						{
							AccessDayDB.getDefault().writeWorkLog("重发修改小票发票号转换对象失败:" + salehead.fphm + "," + salehead.rqsj,StatusType.WORK_SENDERROR);
							return false;
						}
						
						sql.resultSetClose();

						saleappend = new Vector();
						
						SaleAppendDef sad = new SaleAppendDef();
						
						sad.syjh = salehead.syjh; 
						sad.fphm = salehead.fphm;				
						sad.rowno = 0 ;
						sad.str1 = "A";
						sad.str2 = salehead.salefphm;
						
						saleappend.add(sad);
						
						if (saleappend.size() > 0 && !NetService.getDefault().sendSaleAppend(saleappend))
						{
							AccessDayDB.getDefault().writeWorkLog("重发修改小票发票号失败:" + salehead.fphm + "," + salehead.rqsj,StatusType.WORK_SENDERROR);
							
							return false;
						}
					}
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
					AccessDayDB.getDefault().writeWorkLog("重发修改小票发票号异常:" + salehead.fphm + "," + salehead.rqsj,StatusType.WORK_SENDERROR);
					return false;
				}
				finally
				{
					sql.resultSetClose();

					// 关闭数据库
					if (sql != null && dt.compareDate(rq[0], GlobalInfo.balanceDate) != 0)
					{
						sql.Close();
					}
					
					if (saleappend != null)
					{
						saleappend.clear();
						saleappend = null;
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
