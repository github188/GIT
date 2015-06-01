package custom.localize.Cmjb;

import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

public class Cmjb_AccessDayDB extends AccessDayDB
{
	public boolean updateBankLog(BankLogDef bcd)
	{
		if (!bcd.classname.equals("Hzjb_LHPaymentMzk"))
			return updateBankLog(bcd, false, false);
		else
		{
			try
			{
				Object obj = GlobalInfo.dayDB.selectOneData("select count(*) from BANKLOG where  rowcode = " + bcd.rowcode + " and rqsj = '" + bcd.rqsj + "' and syjh = '" + bcd.syjh + "'");

				if (obj == null || Long.parseLong(String.valueOf(obj)) < 1)
				{
					new MessageBox("找不到联华储值卡交易日志记录", null, false);
					return false;
				}

				String sql = "update BANKLOG set net_bz = '" + bcd.net_bz + "' , cardno = '" + bcd.cardno + "' , memo = '" + bcd.memo + "' , " + "kye = '" + bcd.kye + "' , retcode ='" + bcd.retcode + "' ,retmsg = '" + bcd.retmsg + "' , retbz ='" + bcd.retbz + "' " + "where rowcode = " + bcd.rowcode + " and rqsj = '" + bcd.rqsj + "' and syjh = '" + bcd.syjh + "'";

				if (!GlobalInfo.dayDB.executeSql(sql))
				{
					new MessageBox("写入联华储值卡交易日志失败!", null, false);
					return false;
				}
				return true;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				new MessageBox("更新SQL失败!", null, false);
				return false;
			}

		}
	}

	public boolean updateBankLog(BankLogDef bcd, boolean allotflag)
	{
		// 原小票号记录凭证号
		try
		{
			Object obj = GlobalInfo.dayDB.selectOneData("select count(*) from BANKLOG where  rowcode = " + bcd.rowcode + " and rqsj = '" + bcd.rqsj + "' and syjh = '" + bcd.syjh + "'");

			if (obj == null || Long.parseLong(String.valueOf(obj)) < 1)
			{
				new MessageBox("找不到信用卡交易记录", null, false);
				return false;
			}

			String sql;
			if (!allotflag)
			{
				sql = "update BANKLOG set net_bz = '" + bcd.net_bz + "' , cardno = '" + bcd.cardno + "' , trace = " + bcd.trace + " , " + "bankinfo = '" + bcd.bankinfo + "' , retcode ='" + bcd.retcode + "' ,retmsg = '" + bcd.retmsg + "' , retbz ='" + bcd.retbz + "' , oldtrace = '" + bcd.oldtrace + "' " + "where rowcode = " + bcd.rowcode + " and rqsj = '" + bcd.rqsj + "' and syjh = '" + bcd.syjh + "'";
			}
			else
			{
				sql = "update BANKLOG set allotje = " + bcd.allotje + " where rowcode = " + bcd.rowcode + " and rqsj = '" + bcd.rqsj + "' and syjh = '" + bcd.syjh + "'";
			}

			if (!GlobalInfo.dayDB.executeSql(sql))
			{
				new MessageBox("更新SQL失败!", null, false);
				return false;
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean checkSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment)
	{
		saleHead.str1 = GlobalInfo.posLogin.name;
		return super.checkSaleData(saleHead, saleGoods, salePayment);
	}

}
