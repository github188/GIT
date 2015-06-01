package custom.localize.Zsbh;

import java.sql.ResultSet;
import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Zsbh_AccessDayDB extends AccessDayDB
{
	// UPDATE:更新日志时,也更新金额字段
	public boolean updateBankLog(BankLogDef bcd, boolean allotflag, boolean firstupdate)
	{
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
				// 第一次撤销或退货日志,要减少原交易的可分配金额
				if (firstupdate && GlobalInfo.sysPara.allowbankselfsale == 'Y' && (bcd.type.equals(String.valueOf(PaymentBank.XYKCX)) || bcd.type.equals(String.valueOf(PaymentBank.XYKTH))) && bcd.retbz == 'Y')
				{
					// 找原交易的日志行
					int oldrowcode = -1;
					String oldrqsj = "";
					String oldsyjh = "";
					if (bcd.oldtrace > 0)
					{
						ResultSet rs = GlobalInfo.dayDB.selectData("select * from BANKLOG where trace = " + bcd.oldtrace + " and allotje > 0");
						if (rs != null && rs.next())
						{
							oldrowcode = rs.getInt("rowcode");
							oldrqsj = rs.getString("rqsj");
							oldsyjh = rs.getString("syjh");
						}
						GlobalInfo.dayDB.resultSetClose();
					}
					else
					{
						ResultSet rs = GlobalInfo.dayDB.selectData("select * from BANKLOG where cardno = '" + bcd.cardno + "' and je = " + bcd.je + " and allotje > 0");
						if (rs != null && rs.next())
						{
							oldrowcode = rs.getInt("rowcode");
							oldrqsj = rs.getString("rqsj");
							oldsyjh = rs.getString("syjh");
						}
						GlobalInfo.dayDB.resultSetClose();
					}

					//
					if (!GlobalInfo.dayDB.beginTrans())
						return false;

					// 更新当前交易返回信息
					sql = "update BANKLOG set je = " + bcd.je + ", oldtrace = " + bcd.oldtrace + ",net_bz = '" + bcd.net_bz + "' , cardno = '" + bcd.cardno + "' , trace = " + bcd.trace + " , " + "bankinfo = '" + bcd.bankinfo + "' , retcode ='" + bcd.retcode + "' ,retmsg = '" + bcd.retmsg + "' , retbz ='" + bcd.retbz + "' " + "where rowcode = " + bcd.rowcode + " and rqsj = '" + bcd.rqsj + "' and syjh = '" + bcd.syjh + "'";

					if (!GlobalInfo.dayDB.executeSql(sql)) { return false; }

					// 减少原交易可分配金额
					if (oldrowcode > 0)
					{
						sql = "update BANKLOG set allotje = allotje - " + bcd.je + " where rowcode = " + oldrowcode + " and rqsj = '" + oldrqsj + "' and syjh = '" + oldsyjh + "'";

						if (!GlobalInfo.dayDB.executeSql(sql)) { return false; }
					}

					if (!GlobalInfo.dayDB.commitTrans())
						return false;
				}
				else
				{
					sql = "update BANKLOG set net_bz = '" + bcd.net_bz + "' , cardno = '" + bcd.cardno + "' , trace = " + bcd.trace + " , " + "bankinfo = '" + bcd.bankinfo + "' , retcode ='" + bcd.retcode + "' ,retmsg = '" + bcd.retmsg + "' , retbz ='" + bcd.retbz + "' " + "where rowcode = " + bcd.rowcode + " and rqsj = '" + bcd.rqsj + "' and syjh = '" + bcd.syjh + "'";

					if (!GlobalInfo.dayDB.executeSql(sql)) { return false; }
				}
			}
			else
			{
				sql = "update BANKLOG set allotje = " + bcd.allotje + " where rowcode = " + bcd.rowcode + " and rqsj = '" + bcd.rqsj + "' and syjh = '" + bcd.syjh + "'";

				if (!GlobalInfo.dayDB.executeSql(sql)) { return false; }
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	// 获取面值卡付款的总张数
	public long getMzkAllCount(String syyh)
	{
		long count = 0;
		try
		{
			Object obj = GlobalInfo.dayDB.selectOneData("select count(distinct payno) from salepay where paycode = '04' and fphm in (SELECT FPHM FROM SALEHEAD WHERE SYYH = '" + syyh + "')");

			if (obj == null)
			{
				return count;
			}
			else
			{
				long seqno = Long.parseLong(String.valueOf(obj));

				if (seqno > 0)
				{
					count = seqno;
					return count;
				}
				else
				{
					return count;
				}
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
			return count;
		}
	}

	// 更新CRM标志
	public boolean updateSaleBz(long fphm, int flag, String bz)
	{
		String line = "";

		try
		{
			switch (flag)
			{
				case 1:
					line = "update SALEHEAD set NETBZ = '" + bz + "' where syjh = '" + ConfigClass.CashRegisterCode + "' and  fphm = " + fphm;
					break;
				case 2:
					line = "update SALEHEAD set SALEFPHM = '" + bz + "' where syjh = '" + ConfigClass.CashRegisterCode + "' and  fphm = " + fphm;
					break;
				case 3:
					line = "update SALEAPPEND set NETBZ = '" + bz + "' where syjh = '" + ConfigClass.CashRegisterCode + "' and  fphm = " + fphm;
					break;
				case 4:
					line = "update SALEHEAD set str1 = '" + bz + "' where syjh = '" + ConfigClass.CashRegisterCode + "' and  fphm = " + fphm;
					break;
				case 10:
					String[] refhead = GlobalInfo.dayDB.getTableColumns("PAYINHEAD");
					boolean done = false;
					for (int i = 0; i < refhead.length; i++)
					{
						if (refhead[i] != null && refhead[i].equals("hcbz"))
						{
							done = true;
							break;
						}
					}

					if (done)
					{
						line = "update  PAYINHEAD set hcbz = '" + bz + "' where seqno =" + fphm + " and  syyh='" + GlobalInfo.posLogin.gh + "'";
					}
					else
					{
						return true;
					}
					break;
				default:
					return false;
			}

			if (GlobalInfo.dayDB.executeSql(line))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();

			return false;
		}
	}

	public boolean getCrmResendFlag(long fphm)
	{
		ResultSet rs = null;
		try
		{
			rs = GlobalInfo.dayDB.selectData("select str1 from  SALEHEAD where syjh='" + ConfigClass.CashRegisterCode + "' and fphm=" + fphm);
			if (rs == null)
				return false;

			if (rs.next())
			{
				String flag = rs.getString("str1");
				if (flag != null && flag.equals("Y"))
					return true;
			}
			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			if (rs != null)
				try
				{
					rs.close();
				}
				catch (Exception ex)
				{
					rs = null;
				}
		}
	}

	// 获取空面值卡数量
	public long getMzkEmptyCount(String syyh)
	{
		long count = 0;
		try
		{
			Object obj = GlobalInfo.dayDB.selectOneData("select count(distinct payno) from salepay where paycode = '04' and kye <= 0 and fphm in (SELECT FPHM FROM SALEHEAD WHERE SYYH = '" + syyh + "')");

			if (obj == null)
			{
				return count;
			}
			else
			{
				long seqno = Long.parseLong(String.valueOf(obj));

				if (seqno > 0)
				{
					count = seqno;
					return count;
				}
				else
				{
					return count;
				}
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
			return count;
		}
	}
	
	public boolean checkSaleData(SaleHeadDef saleHead, Vector saleGoods,Vector salePayment)
    {
		for (int i = 0; i < salePayment.size(); i++) {
			SalePayDef sp = (SalePayDef) salePayment.elementAt(i);
			if(sp.paycode.equals(GlobalInfo.sysPara.hbPaymentCode))
			{
				//有红包付款不检查平衡
				return true;
			}
		}
		return super.checkSaleData(saleHead, saleGoods, salePayment);
    }
}
