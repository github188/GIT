package custom.localize.Cctd;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;


public class Cctd_AccessDayDB extends AccessDayDB
{

	public boolean checkSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment)
	{
		if (super.checkSaleData(saleHead, saleGoods, salePayment))
		{
			// 如果这笔不是换消，检查上笔是否为换退
			if (!(SellType.ISSALE(saleHead.djlb) && saleHead.hhflag == 'Y'))
			{
				if (getlasthhbackinfo(saleHead.syjh, new StringBuffer()))
				{
					new MessageBox("上笔为【换货退货】，必须先进行【换货销售】才能进行其他操作");
					return false;
				}
			}

			// 如果上笔是红冲换消，检查这笔是否为"对应"红冲换退
			// 如果上笔不是红冲换消，本笔不能做红冲换退
			long fphm1 = 0;
			if ((fphm1 = ((Cctd_AccessDayDB) AccessDayDB.getDefault()).getHcHHbackinfo(ConfigClass.CashRegisterCode, String.valueOf(GlobalInfo.syjStatus.fphm - 1))) > 0)
			{
				if (!(saleHead.djlb.equals(SellType.RETAIL_BACK_HC) && saleHead.hhflag == 'Y'))
				{
					new MessageBox("上笔为【红冲换销】，必须先进行【红冲换退】才能进行其他操作");
					return false;
				}
				else
				// 本笔为红冲换退，检查是否为“对应”
				{
					if (Convert.toLong(saleHead.yfphm) != getYfphm(saleHead.syjh, String.valueOf(fphm1)))
					{
						new MessageBox("上笔红冲换消小票：" + fphm1 + "\n本笔必须红冲还退：" + (fphm1 - 1));
						return false;
					}
				}
			}
			else
			{
				if ((saleHead.djlb.equals(SellType.RETAIL_BACK_HC) && saleHead.hhflag == 'Y'))
				{
					new MessageBox("上笔不是【红冲换销】，不能进行【红冲换退】");
					return false;
				}
			}

			return true;
		}

		return false;
	}

	// false:查询过程中报错
	public boolean gethhbackpay(String syjh, StringBuffer fphm, Vector pay)
	{
		// 如果换退
		if (getlasthhbackinfo(syjh, fphm))
		{
			ResultSet rs = null;

			try
			{
				if ((rs = GlobalInfo.dayDB.selectData("select * from SALEPAY where fphm = " + fphm.toString() + " order by rowno")) != null)
				{
					boolean ret = false;

					while (rs.next())
					{
						SalePayDef sp = new SalePayDef();

						if (!GlobalInfo.dayDB.getResultSetToObject(sp)) { return false; }

						pay.add(sp);

						ret = true;
					}

					if (!ret)
						return ret;
				}

				return true;
			}
			catch (Exception er)
			{
				er.printStackTrace();
				return false;
			}
		}

		return true;
	}

	public double gethhbackYsje(String syjh, String fphm)
	{
		try
		{
			Object obj = GlobalInfo.dayDB.selectOneData("select ysje from salehead where syjh = '" + syjh + "' and fphm = " + fphm + " and hhflag = 'Y' and djlb = '4'");

			if (obj == null)
			{
				return -1;
			}
			else
			{
				double ysje = Double.parseDouble(String.valueOf(obj));
				if (ysje > 0)
				{
					return ysje;
				}
				else
				{
					return -1;
				}
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
			return -1;
		}
	}

	public long getYfphm(String syjh, String fphm)
	{
		long ret = -1;
		try
		{
			Object obj = GlobalInfo.dayDB.selectOneData("select a.yfphm from salegoods a,salehead b where a.fphm = b.fphm and b.syjh = '" + syjh + "' and b.fphm = " + fphm + " and b.hhflag = 'Y'");

			if (obj == null)
			{
				return ret;
			}
			else
			{
				long fphm1 = Long.parseLong(String.valueOf(obj));

				if (fphm1 > 0)
				{
					ret = fphm1;
					return ret;
				}
				else
				{
					return ret;
				}
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
			return -1;
		}
	}

	public long getHcHHbackinfo(String syjh, String fphm)
	{
		long ret = -1;
		try
		{
			Object obj = GlobalInfo.dayDB.selectOneData("select fphm from salehead where syjh = '" + syjh + "'  order by fphm desc");
			if (obj == null)
			{
				return ret;
			}
			else
			{
				fphm = String.valueOf(Long.parseLong(String.valueOf(obj)));
			}

			obj = GlobalInfo.dayDB.selectOneData("select a.yfphm from salegoods a,salehead b where a.fphm = b.fphm and b.syjh = '" + syjh + "' and b.fphm = " + fphm + " and b.hhflag = 'Y' and b.djlb = '2'");

			if (obj == null)
			{
				return ret;
			}
			else
			{
				long fphm1 = Long.parseLong(String.valueOf(obj));

				if (fphm1 > 0)
				{
					ret = fphm1;
					return ret;
				}
				else
				{
					return ret;
				}
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
			return ret;
		}
	}

	public boolean getlasthhbackHead(SaleHeadDef salehead, String syjh)
	{
		ResultSet rs = null;
		if ((rs = GlobalInfo.dayDB.selectData("select * from salehead where syjh = '" + syjh + "'  order by fphm desc")) != null)
		{
			try
			{
				if (rs.next())
				{
					if (!GlobalInfo.dayDB.getResultSetToObject(salehead)) { return false; }
				}
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}

			return true;
		}
		return false;
	}

	public boolean getlasthhbackinfo(String syjh, StringBuffer buff)
	{
		try
		{
			Object obj = GlobalInfo.dayDB.selectOneData("select fphm from salehead where syjh = '" + syjh + "'  order by fphm desc");

			if (obj == null)
			{
				return false;
			}
			else
			{
				long fphm = Long.parseLong(String.valueOf(obj));

				if (fphm > 0)
				{
					buff.append(String.valueOf(fphm));
					return gethhbackinfo(syjh, String.valueOf(fphm));
				}
				else
				{
					return false;
				}
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
			return false;
		}
	}

	public boolean gethhbackinfo(String syjh, String fphm)
	{
		boolean done = false;
		try
		{
			Object obj = GlobalInfo.dayDB.selectOneData("select count(*) from salehead where syjh = '" + syjh + "' and fphm = " + fphm + " and hhflag = 'Y' and djlb = '4'");

			if (obj == null)
			{
				return done;
			}
			else
			{
				long seqno = Long.parseLong(String.valueOf(obj));

				if (seqno > 0)
				{
					done = true;
					return done;
				}
				else
				{
					return false;
				}
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
			return false;
		}
	}
}
