package custom.localize.Tygc;

import java.util.Vector;

import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.PublicMethod;

import custom.localize.Bcrm.Bcrm_AccessLocalDB;

public class Tygc_AccessLocalDB extends Bcrm_AccessLocalDB
{

//	 插入收银机参数
	public boolean writeReprint(Vector v, boolean done)
	{
		String[] row = null;

		try
		{
			//
			PublicMethod.timeStart(Language.apply("正在写入本地数据表,请等待......"));

			if (!GlobalInfo.localDB.beginTrans()) { return false; }

			if (done)
			{
				if (!GlobalInfo.localDB.executeSql("Delete From REPRINT")) { return false; }
			}

			if (!GlobalInfo.localDB.setSql("Insert into REPRINT(IWID,IWMEMO,IWSTATUS,IWSORT) values(?,?,?,?)")) { return false; }

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);

				GlobalInfo.localDB.paramSetString(1, row[0]);
				GlobalInfo.localDB.paramSetString(2, row[1]);
				GlobalInfo.localDB.paramSetString(3, row[2]);
				GlobalInfo.localDB.paramSetString(4, row[3]);

				if (!GlobalInfo.localDB.executeSql()) { return false; }
			}

			if (GlobalInfo.localDB.commitTrans())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception ex)
		{
			return false;
		}
		finally
		{
			//
			PublicMethod.timeEnd(Language.apply("写入本地参数表耗时: "));
		}
	}

}
