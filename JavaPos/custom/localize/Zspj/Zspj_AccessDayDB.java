package custom.localize.Zspj;

import java.sql.ResultSet;

import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;

public class Zspj_AccessDayDB extends AccessDayDB
{
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
					line = "update SALEHEAD set str3 = '" + bz + "' where syjh = '" + ConfigClass.CashRegisterCode + "' and  fphm = " + fphm;
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
			rs = GlobalInfo.dayDB.selectData("select str3 from  SALEHEAD where syjh='" + ConfigClass.CashRegisterCode + "' and fphm=" + fphm);
			if (rs == null)
				return false;

			if (rs.next())
			{
				String flag = rs.getString("str3");
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

	public boolean updateSaleJf(long fphm, int flag, double bcjf, double ljjf, double bxjf, String memo)
	{
		try
		{
			if (flag == 5)
			{
				String line = "update SALEHEAD set bcjf = " + bcjf + ",ljjf = " + ljjf + ", num4=" +bxjf + " where syjh = '" + ConfigClass.CashRegisterCode + "' and  fphm = " + fphm;
				if (GlobalInfo.dayDB.executeSql(line))
					return true;
				return false;
			}

			return super.updateSaleJf(fphm, flag, bcjf, ljjf);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
}
