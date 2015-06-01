package custom.localize.Cmjb;

import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

public class Cmjb_LHSaleBillMode extends Cmjb_SaleBillMode
{
	public static Cmjb_LHSaleBillMode billmode = null;

	public static Cmjb_LHSaleBillMode getInstance()
	{
		return getInstance("LHSalePrintMode.ini");
	}

	public static Cmjb_LHSaleBillMode getInstance(String fileName)
	{
		if (printConfig == null)
		{
			readPrintConfig();
		}

		if (billmode == null)
		{
			billmode = new Cmjb_LHSaleBillMode();// CustomLocalize.getDefault().createSaleBillMode();

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
				return (Cmjb_LHSaleBillMode) element[1];
		}

		return billmode;
	}

	public boolean ReadTemplateFile()
	{
		String line = GlobalVar.ConfigPath + "//LHSalePrintMode.ini";
		return ReadTemplateFile(line);
	}
}
