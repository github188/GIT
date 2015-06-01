package custom.localize.Szxw;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.Sqldb;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Struct.MenuFuncDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.UI.MenuFuncEvent;

public class Szxw_MenuFuncBS extends MenuFuncBS
{
	public final static int MN_XSZJ = 204; //销售总结

	public boolean execExtendFuncMenu(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		switch (Integer.parseInt(mfd.code))
		{
			case MN_XSZJ:
				//openQtxStj(mfd, mffe);
				printSaleSummary();
				break;
			default:
				return false;
		}

		return true;
	}

	public void printSaleSummary()
	{
		ResultSet rs = null;
		String sqlstr = null;
		Sqldb sql = null;

		try
		{
			TextBox txt = new TextBox();
			StringBuffer buffer = new StringBuffer();
			buffer.append(new ManipulateDateTime().getDateByEmpty());
			if (txt.open("请输入销售总结的日期", "日期", "日期格式（YYYYMMDD）", buffer, -1, -1, false, TextBox.IntegerInput))
			{
				String date = buffer.toString();
				if (!PathFile.isPathExists(ConfigClass.LocalDBPath + "Invoice/" + date.trim() + "/" + LoadSysInfo.getDefault().getDayDBName()))
				{
					new MessageBox("您输入的本地数据库不存在,请重新输入!", null, false);
					return;
				}

				sqlstr = "select syyh from salesummary group by syyh";

				if (date.trim().equals(new ManipulateDateTime().getDateByEmpty()))
				{
					sql = GlobalInfo.dayDB;
				}
				else
				{
					sql = LoadSysInfo.getDefault().loadDayDB(ManipulateDateTime.getConversionDate(date.trim()));
				}

				Vector v = new Vector();
				if ((rs = sql.selectData(sqlstr)) != null)
				{
					while (rs.next())
					{
						v.add(rs.getString("syyh"));
					}
				}

				String paymentSql = "";
				Vector paymentList;
				HashMap syyInfoMap = new HashMap();
				PayModeDef pamodeDef;
				for (int i = 0; i < v.size(); i++)
				{
					paymentSql = "select sum(je) as je,paycode,syyh,payname,bs from salepaysummary where syyh='" + (String) v.elementAt(i)
							+ "' group by paycode";

					if ((rs = sql.selectData(paymentSql)) != null)
					{
						paymentList = new Vector();
						while (rs.next())
						{
							String paycode = rs.getString("paycode");
							if (paycode.trim().equals("DJQF"))
							{
								continue;
							}
							String localje = "";
							double je = rs.getDouble("je");

							if ((pamodeDef = DataService.getDefault().searchPayMode(paycode)) != null)
							{
								double hl = pamodeDef.hl;
								localje = ManipulatePrecision.doubleToString(ManipulatePrecision.mul(hl, je));
							}

							paymentList.add(new String[] { rs.getString("payname"), localje, ManipulatePrecision.doubleToString(je) });
							syyInfoMap.put(v.elementAt(i), paymentList);
						}
					}
				}

				String xsje = "";
				String thje = "";
				String hcje = "";
				String xsbs = "";
				String thbs = "";
				String hcbs = "";

				String saleSys = "select * from salesummary where syyh = '全天'";
				if ((rs = sql.selectData(saleSys)) != null)
				{
					if (rs.next())
					{
						xsje = ManipulatePrecision.doubleToString(rs.getDouble("xsje"));
						thje = ManipulatePrecision.doubleToString(rs.getDouble("thje"));
						hcje = ManipulatePrecision.doubleToString(rs.getDouble("hcje"));
						xsbs = String.valueOf(rs.getInt("xsbs"));
						thbs = String.valueOf(rs.getInt("thbs"));
						hcbs = String.valueOf(rs.getInt("hcbs"));
					}
				}

				if (v == null || v.size() < 1)
				{
					new MessageBox("您输入的日期没有交易", null, false);
					return;
				}
				final String blank = "        ";
				Printer.getDefault().startPrint_Normal();
				Printer.getDefault().printLine_Normal("                销售总结");
				Printer.getDefault().printLine_Normal("");
				Printer.getDefault().printLine_Normal("");

				Printer.getDefault().printLine_Normal("店铺" + blank + "    : " + GlobalInfo.sysPara.mktname);
				Printer.getDefault().printLine_Normal("                  (" + GlobalInfo.sysPara.mktcode + ")");
				Printer.getDefault().printLine_Normal("工作日期" + blank + ": " + ManipulateDateTime.getConversionDate(date));
				Printer.getDefault().printLine_Normal("完成日期" + blank + ": " + ManipulateDateTime.getCurrentDateTime());

				for (int i = 0; i < v.size(); i++)
				{
					Printer.getDefault().printLine_Normal("");
					String syyNo = (String) v.elementAt(i);

					Vector list = (Vector) syyInfoMap.get(v.elementAt(i));
					if (list == null) continue;

					if (!syyNo.equals("全天"))
					{
						Printer.getDefault().printLine_Normal("收银员:" + syyNo);
					}
					else
					{
						Printer.getDefault().printLine_Normal("合计");
					}
					String line = Convert.appendStringSize("", "付款方式", 0, 16, 38);
					line = Convert.appendStringSize(line, "本地金额", 16, 10, 38, 1);
					line = Convert.appendStringSize(line, "币种金额", 26, 12, 38, 1);
					Printer.getDefault().printLine_Normal(line);

					for (Iterator it = list.iterator(); it.hasNext();)
					{
						String[] arrPayment = (String[]) it.next();
						line = Convert.appendStringSize("", arrPayment[0], 2, 14, 38);
						line = Convert.appendStringSize(line, arrPayment[1], 16, 10, 38, 1);
						line = Convert.appendStringSize(line, arrPayment[2], 26, 12, 38, 1);

						Printer.getDefault().printLine_Normal(line);
					}

				}

				Printer.getDefault().printLine_Normal("");

				String strSaleSys = Convert.appendStringSize("", "销售系统", 0, 16, 38);
				strSaleSys = Convert.appendStringSize(strSaleSys, "签发", 16, 10, 38, 1);
				strSaleSys = Convert.appendStringSize(strSaleSys, "金额", 26, 12, 38, 1);
				Printer.getDefault().printLine_Normal(strSaleSys);

				String xsdjLine = Convert.appendStringSize("", "销售单据", 0, 16, 20);
				xsdjLine = Convert.appendStringSize(xsdjLine, xsbs, 16, 10, 38, 1);
				xsdjLine = Convert.appendStringSize(xsdjLine, xsje, 26, 12, 38, 1);
				Printer.getDefault().printLine_Normal(xsdjLine);

				String thdjLine = Convert.appendStringSize("", "退货单据", 0, 16, 20);
				thdjLine = Convert.appendStringSize(thdjLine, thbs, 16, 10, 38, 1);
				thdjLine = Convert.appendStringSize(thdjLine, thje, 26, 12, 38, 1);
				Printer.getDefault().printLine_Normal(thdjLine);

				String hcdjLine = Convert.appendStringSize("", "红冲单据", 0, 16, 20);
				hcdjLine = Convert.appendStringSize(hcdjLine, hcbs, 16, 10, 38, 1);
				hcdjLine = Convert.appendStringSize(hcdjLine, hcje, 26, 12, 38, 1);
				Printer.getDefault().printLine_Normal(hcdjLine);

				Printer.getDefault().printLine_Normal("");
				Printer.getDefault().cutPaper_Normal();
			}

		}
		catch (Exception er)
		{
			er.printStackTrace();
		}
		finally
		{
			if (sql != null && !sql.equals(GlobalInfo.dayDB)) sql.Close();
		}

	}
}
