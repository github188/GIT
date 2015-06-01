package custom.localize.Hzjb;

import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

public class Hzjb_LHSaleBillMode extends Hzjb_SaleBillMode
{
	public static Hzjb_LHSaleBillMode billmode = null;

	public static Hzjb_LHSaleBillMode getInstance()
	{
		return getInstance("LHSalePrintMode.ini");
	}

	public static Hzjb_LHSaleBillMode getInstance(String fileName)
	{
		if (printConfig == null)
		{
			readPrintConfig();
		}

		if (billmode == null)
		{
			billmode = new Hzjb_LHSaleBillMode();// CustomLocalize.getDefault().createSaleBillMode();

			// 加载辅助模板
			String rows[] = PathFile.getAllDirName(GlobalVar.ConfigPath);
			for (int i = 0; i < rows.length; i++)
			{
				if (rows[i].indexOf("SalePrintMode") >= 0)
				{
					SaleBillMode billmode = CustomLocalize.getDefault().createSaleBillMode();
					if (rows[i].indexOf("SalePrintMode_") >= 0)
					{
						billmode.ReadTemplateFile(GlobalVar.ConfigPath + "//" + rows[i]);
						vbillmode.add(new Object[] { rows[i], billmode });
					}
				}
			}
		}

		for (int i = 0; vbillmode != null && i < vbillmode.size(); i++)
		{
			Object[] element = (Object[]) vbillmode.elementAt(i);
			if (element[0].toString().indexOf(fileName) >= 0)
				return (Hzjb_LHSaleBillMode) element[1];
		}

		return billmode;
	}

	public boolean ReadTemplateFile()
	{
		String line = GlobalVar.ConfigPath + "//LHSalePrintMode.ini";
		return ReadTemplateFile(line);
	}
}
