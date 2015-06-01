package custom.localize.Gzbh;

import java.sql.ResultSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.LoadSysInfo;

public class Gzbh_QueryMzkSellBS
{

	protected Sqldb sql = null;

	public Gzbh_QueryMzkSellBS()
	{

	}

	//查询工作日志
	public boolean initQueryMzkSell(Table tabQueryMzkSell)
	{
		ResultSet rs = null;

		try
		{
			tabQueryMzkSell.removeAll();

			sql = LoadSysInfo.getDefault().loadDayDB(ManipulateDateTime.getConversionDate(new ManipulateDateTime().getDateByEmpty()));

			String sqlstr = "select sp.fphm,sp.payno,sp.je,sh.rqsj from salepay sp,salehead sh where sp.fphm = sh.fphm and (sp.paycode = '0010' or sp.paycode = '0021' or sp.paycode = '0022' or sp.paycode = '0031') and sh.syyh = '"
					+ GlobalInfo.posLogin.gh + "'";

			if ((rs = sql.selectData(sqlstr + "order by sp.fphm asc")) != null)
			{
				boolean ret = false;
				int i = 0;
				while (rs.next())
				{
					i++;
					String[] mzkSellInfo = { String.valueOf(i), rs.getString("fphm"), rs.getString("payno"), rs.getString("je"), rs.getString("rqsj") };
					TableItem item = new TableItem(tabQueryMzkSell, SWT.NONE);
					item.setText(mzkSellInfo);

					ret = true;
				}
				if (!ret)
				{
					new MessageBox("当前数据库无数据!", null, false);
					return false;
				}

				return true;
			}
			else
			{
				return false;
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			if (sql != GlobalInfo.dayDB)
			{
				if (sql != null)
				{
					sql.Close();
					sql = null;
				}
			}
			else
			{
				if (sql != null)
				{
					sql.resultSetClose();
					sql = null;
				}
			}
		}
	}

}
