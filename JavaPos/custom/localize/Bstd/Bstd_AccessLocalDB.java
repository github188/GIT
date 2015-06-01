package custom.localize.Bstd;

import java.sql.ResultSet;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.PublicMethod;
import com.efuture.javaPos.Struct.GlobalParaDef;

public class Bstd_AccessLocalDB extends AccessLocalDB
{
	public boolean readSysPara(boolean dofinish)
	{
		ResultSet rs = null;

		// 赋缺省值
		GlobalInfo.sysPara = new GlobalParaDef();
		paraInitDefault();

		try
		{
			PublicMethod.timeStart(Language.apply("正在读取本地参数表,请等待......"));

			rs = GlobalInfo.localDB.selectData("select code,value,name from SysPara order by length(code),code desc");

			if (rs == null) { return false; }

			while (rs.next())
			{
				paraConvertByCode(rs.getString(1), rs.getString(2));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return false;
		}
		finally
		{
			GlobalInfo.localDB.resultSetClose();
			PublicMethod.timeEnd(Language.apply("读取本地参数表耗时: "));
		}

		// 参数转换完毕处理
		return (dofinish ? paraInitFinish() : true);
	}

	public void paraInitDefault()
	{
		super.paraInitDefault();

		GlobalInfo.sysPara.cardexptcmd = "";
		GlobalInfo.sysPara.isenablezklog = 'N';
		GlobalInfo.sysPara.hykinputmode = 2;
		GlobalInfo.sysPara.isusecoinbag = 'Y';
		GlobalInfo.sysPara.hykhandinputflag = "";
		GlobalInfo.sysPara.isprintpopflag = 'N';
		GlobalInfo.sysPara.isprintdzcsl = 'N';
		GlobalInfo.sysPara.enableiputzszrebate = 'N';
		GlobalInfo.sysPara.enabledzcCoefficient = 'N';
		GlobalInfo.sysPara.visiblepaycode = "0509";
		GlobalInfo.sysPara.backticketctrl = 'N';
		GlobalInfo.sysPara.iscardcode = 'Y';
		GlobalInfo.sysPara.issplitdzc = 'N';
		GlobalInfo.sysPara.isSuperMarketPop = 'N';
	}

	public void paraConvertByCode(String code, String value)
	{
		super.paraConvertByCode(code, value);

		try
		{
			if (code.equals("HB") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.cardexptcmd = value.trim();
				return;
			}

			if (code.equals("HD") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isbackoverpre = value.trim().charAt(0);
				return;
			}

			if (code.equals("HE") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isenablezklog = value.trim().charAt(0);
				return;
			}

			if (code.equals("HF") && CommonMethod.noEmpty(value))
			{
				String[] values = value.trim().split(",");

				if (values.length > 0)
				{
					if ("0123456789".indexOf(value.trim()) != -1)
						GlobalInfo.sysPara.hykinputmode = Convert.toInt(values[0].trim().charAt(0));
				}
				if (values.length > 1)
				{
					GlobalInfo.sysPara.isusecoinbag = values[1].charAt(0);
				}

				if (values.length > 2)
				{
					GlobalInfo.sysPara.hykhandinputflag = "|" + values[2].trim() + "|";
				}
				return;
			}

			if (code.equals("HG") && CommonMethod.noEmpty(value))
			{
				String[] values = value.trim().split(",");
				if (values.length > 0)
				{
					GlobalInfo.sysPara.isprintpopflag = values[0].charAt(0);
				}
				if (values.length > 1)
				{
					GlobalInfo.sysPara.isprintdzcsl = values[1].charAt(0);
				}
				return;
			}

			if (code.equals("HH") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.enableiputzszrebate = value.trim().charAt(0);
				return;
			}
			if (code.equals("HI") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.enabledzcCoefficient = value.trim().charAt(0);
				return;
			}
			if (code.equals("HJ") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.visiblepaycode = GlobalInfo.sysPara.visiblepaycode + "," + value.trim();
				return;
			}
			if (code.equals("HK") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.backticketctrl = value.trim().charAt(0);
				return;
			}
			if (code.equals("HL") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.issplitdzc = value.trim().charAt(0);
				return;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

}
