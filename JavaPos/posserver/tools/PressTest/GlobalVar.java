package posserver.tools.PressTest;

import java.util.Timer;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.Sqldb;

public class GlobalVar {
	


	public Timer timer2 = new Timer();
	
	// 数据库访问控件
	private static Sqldb sqldb1 = null;
	private static Sqldb sqldb2 = null;
	
	public static void setSqldb1(Sqldb sqldb1) {
		GlobalVar.sqldb1 = sqldb1;
	}

	public static Sqldb getSqldb1() {
		if (sqldb1 == null)
		{
			sqldb1 = new Sqldb("org.sqlite.JDBC",GlobalConfig.dataBaseUrl);
		}
		return sqldb1;
	}

	public static void setSqldb2(Sqldb sqldb2) {
		GlobalVar.sqldb2 = sqldb2;
	}

	public static Sqldb getSqldb2() {
		if (sqldb2 == null)
		{
			sqldb2 = new Sqldb("org.sqlite.JDBC",GlobalConfig.dataBaseUrl);
		}
		return sqldb2;
	}
}
