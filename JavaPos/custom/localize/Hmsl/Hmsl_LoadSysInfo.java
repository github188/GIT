package custom.localize.Hmsl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.LoadSysInfo;

public class Hmsl_LoadSysInfo extends LoadSysInfo
{
	public void getLocalNewData()
	{
		if (!(new File(GlobalVar.ConfigPath + "//LocalSysPara.ini").exists()))
			return;

		BufferedReader br;
		br = CommonMethod.readFile(GlobalVar.ConfigPath + "/LocalSysPara.ini");
		if (br == null)
			return;
		String line;
		String[] sp;
		try
		{
			while ((line = br.readLine()) != null)
			{
				if ((line == null) || (line.length() <= 0))
				{
					continue;
				}
				String[] lines = line.split("&&");
				sp = lines[0].split("=");
				if (sp.length < 2)
					continue;
				AccessLocalDB.getDefault().paraConvertByCode(sp[0].trim(), sp[1].trim());
			}
			
			// 参数转换完毕处理
			AccessLocalDB.getDefault().paraInitFinish();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
