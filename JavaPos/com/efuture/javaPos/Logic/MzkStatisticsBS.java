package com.efuture.javaPos.Logic;

import java.sql.ResultSet;
import java.util.Vector;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.StoredCardStatisticsMode;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class MzkStatisticsBS
{
	/**
	 * 查询面值卡明细,并统计收款笔数和收款金额
	 * 
	 * @param gh
	 *            收银员工号
	 * @param bc
	 *            班次
	 * @param paycode
	 *            付款代码 0400,0050|01,002
	 */

	protected Sqldb sql = null;
	protected Vector detail = null;// 面值卡明细
	protected int bs = 0;// 收款笔数
	protected double ske = 0.00;// 收款金额

	// 以下两个字段用于统计零超转存
	protected double outje; // 零钱包支出的金额
	protected double inje; // 零钱包存入的金额

	public MzkStatisticsBS()
	{
		sql = GlobalInfo.dayDB;
		detail = new Vector();
	}

	public void SearchMZKDetails(String gh, String bc, String paycode)
	{
		int printDetailType =0;
		ResultSet rs = null;
		String strsql = null;
		String[] s = paycode.split(",");
		String wherePaycode = "";
		try
		{
			for (int i = 0; s != null && i < s.length; i++)
			{
				wherePaycode = wherePaycode + "'" + s[i] + "',";
			}

			strsql = "select b.fphm,b.rqsj, b.hykh, b.bc, b.syyh, b.djlb, a.* from salepay a,salehead b where a.syjh=b.syjh and a.fphm=b.fphm" + " and b.syyh='" + gh + "' and b.bc='" + bc + "' and a.paycode in (" + wherePaycode.substring(0, wherePaycode.length() - 1) + ")";
			rs = sql.selectData(strsql);
			if (rs == null)
				return;

			detail.clear();
			bs = 0;
			ske = 0.00;
			outje = 0.0;
			inje = 0.0;

			while (rs.next())
			{
				if (paycode.equals("0111"))
				{
					switch (printDetailType)
					{
						case -1:
							break;
						case 0:
							if (new MessageBox(Language.apply("是否打印收款明细?"), null, true).verify() != GlobalVar.Key1)
							{
								printDetailType = -1;
								detail.add(new String[] { ManipulateDateTime.getCurrentDate(), rs.getString("bc"), rs.getString("syyh"), rs.getString("syjh"), "-/-", "-/-", "-/-" });
							}
							else
							{
								printDetailType = 1;
								detail.add(new String[] { rs.getString("rqsj"), rs.getString("bc"), rs.getString("syyh"), rs.getString("syjh"), rs.getString("hykh"), ManipulatePrecision.doubleToString(SellType.SELLSIGN(rs.getString("djlb")) * rs.getDouble("je")), ManipulatePrecision.doubleToString(rs.getDouble("kye")) });
							}
							break;
						case 1:
							detail.add(new String[] { rs.getString("rqsj"), rs.getString("bc"), rs.getString("syyh"), rs.getString("syjh"), rs.getString("hykh"), ManipulatePrecision.doubleToString(SellType.SELLSIGN(rs.getString("djlb")) * rs.getDouble("je")), ManipulatePrecision.doubleToString(rs.getDouble("kye")) });
							break;
					}
				}
				else
				{
					detail.add(new String[] { rs.getString("rqsj"), rs.getString("bc"), rs.getString("syyh"), rs.getString("syjh"), rs.getString("payno"), ManipulatePrecision.doubleToString(SellType.SELLSIGN(rs.getString("djlb")) * rs.getDouble("je")), ManipulatePrecision.doubleToString(rs.getDouble("kye")) });
				}

				bs = bs + 1;

				// 零钞转存付款方式为0111
				if (paycode.equals("0111"))
				{
					// 存入的零钞
					if (rs.getString("memo").equals("3"))
						inje = ManipulatePrecision.doubleConvert(inje + rs.getDouble("je"), 2, 1);
					else
						// 用于付款的零钞
						outje = ManipulatePrecision.doubleConvert(outje + SellType.SELLSIGN(rs.getString("djlb")) * rs.getDouble("je"), 2, 1);
				}

				ske = ske + SellType.SELLSIGN(rs.getString("djlb")) * rs.getDouble("je");
			}

			if (paycode.equals("0111"))
				ske = ManipulatePrecision.doubleConvert(outje + inje, 2, 1);	
			
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (sql != null)
			{
				sql.resultSetClose();
			}
		}
	}

	/**
	 * 列当前收银员所有班次信息
	 * 
	 * @return
	 */
	public Vector ListSaleBC()
	{
		String sqlstr = "select bc,syyh from SALESUMMARY where syyh = '" + GlobalInfo.posLogin.gh + "' order by bc";// 查询当前班车
		ResultSet rs = null;
		try
		{
			if ((rs = sql.selectData(sqlstr)) != null)
			{
				Vector contents = new Vector();
				while (rs.next())
				{
					contents.add(new String[] { DataService.getDefault().getTimeNameByCode(rs.getString("bc").charAt(0)), rs.getString("syyh") });
				}

				return contents;
			}
			else
				return null;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
		finally
		{
			if (sql != null)
			{
				sql.resultSetClose();
			}
		}
	}

	/**
	 * 列出参数中的付款方式
	 * 
	 * @return
	 */
	public Vector ListPay()
	{
		String str = GlobalInfo.sysPara.mzkStatistics;
		String[] s = str.split("\\|");

		Vector contents = null;
		if (s != null)
			contents = new Vector();
		for (int i = 0; s != null && i < s.length; i++)
		{
			PayModeDef pmd = DataService.getDefault().searchPayMode(s[i].split(",") != null ? s[i].split(",")[0] : "");
			if (pmd != null)
				contents.add(new String[] { pmd.name.trim(), s[i] });
		}
		return contents;
	}

	/**
	 * 打印面值卡信息
	 * 
	 * @return
	 */
	public boolean PrintMZK(String[] pay)
	{
		if (StoredCardStatisticsMode.getDefault().checkTemplateFile())
		{
			StoredCardStatisticsMode.getDefault().setTemplateObject(pay, detail, bs, ske, outje, inje);
			StoredCardStatisticsMode.getDefault().PrintStoredCardStatistics();
			Printer.getDefault().cutPaper_Journal();
			new MessageBox(Language.apply("收款统计打印完毕"));
			return true;
		}
		else
		{
			new MessageBox(Language.apply("面值卡收款统计打印模板不存在，不能打印"));
			return false;
		}
	}

	/**
	 * 选择性打印储值卡汇总信息
	 * 
	 */
	public void PrintMZKStatistics()
	{
		String[] title = { Language.apply("班次"), Language.apply("收银员工号") };
		int[] width = { 100, 440 };
		Vector vector = ListSaleBC();
		int choice = new MutiSelectForm().open(Language.apply("请选择需打印的班次"), title, width, vector, false);
		if (choice >= 0)
		{
			String[] str = (String[]) vector.elementAt(choice);
			String str_bc = String.valueOf(DataService.getDefault().getTimeCodeByName(str[0]));
			String str_gh = GlobalInfo.posLogin.gh;
			String str_paycode = "";

			String[] title_pay = { Language.apply("付款方式"), Language.apply("付款代码") };

			vector.clear();
			vector = ListPay();

			if (vector.size() > 1)
			{
				choice = new MutiSelectForm().open(Language.apply("请选择需打印的付款方式"), title_pay, width, vector, false);
				if (choice >= 0)
				{
					str = (String[]) vector.elementAt(choice);
					str_paycode = str[1].trim();
					// 打印
					SearchMZKDetails(str_gh, str_bc, str_paycode);
					PrintMZK(str);
				}
			}
			else if (vector.size() == 1)
			{
				str = (String[]) vector.elementAt(choice);
				str_paycode = str[1];
				// 打印
				SearchMZKDetails(str_gh, str_bc, str_paycode);
				PrintMZK(str);
			}
		}
	}
}
