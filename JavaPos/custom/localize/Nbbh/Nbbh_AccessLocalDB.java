package custom.localize.Nbbh;

import java.sql.ResultSet;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.PublicMethod;

public class Nbbh_AccessLocalDB extends AccessLocalDB {
	
	public boolean paraInitFinish()
	{
		if (GlobalInfo.sysPara.BankPaycode!=null && GlobalInfo.sysPara.BankPaycode.trim().length() > 0)
		{
			if (GlobalInfo.sysPara.custompayobj != null && GlobalInfo.sysPara.custompayobj.trim().length() > 0)
			{
				String s[] = null;
				if (GlobalInfo.sysPara.custompayobj.indexOf(';') >= 0)
					s = GlobalInfo.sysPara.custompayobj.split(";");
				else
					s = GlobalInfo.sysPara.custompayobj.split("\\|");
				
				String custompayobj = "";
				for (int i = 0; i < s.length; i++)
				{
					if (s[i].trim().length() <= 0)
					{
						continue;
					}
					if(i<=0)
						custompayobj = s[i];
					else
						custompayobj = custompayobj + ";" + s[i];
					
					if(s[i].indexOf("Nbbh_PaymentBank")>=0)//
					{
						custompayobj = s[i].trim() + "," + GlobalInfo.sysPara.BankPaycode.replace("|",",");
					}
					//custompayobj = custompayobj + "," + s[i];
				}
				if(custompayobj.indexOf("Nbbh_PaymentBank")<0)
					custompayobj = custompayobj + ";" + "Nbbh_PaymentBank" + "," + GlobalInfo.sysPara.BankPaycode.replace("|",",");;
				GlobalInfo.sysPara.custompayobj = custompayobj;
			}
			else
			{
				GlobalInfo.sysPara.custompayobj = "Nbbh_PaymentBank" + "," + GlobalInfo.sysPara.BankPaycode.replace("|",",");
			}
		}
		return super.paraInitFinish();
	}
	public boolean writeRules(Vector v)
	{
		String[] row = null;
		try
		{
			PublicMethod.timeStart(Language.apply("正在写入本地打印信息表,请等待......"));
			if (!GlobalInfo.localDB.beginTrans()) { return false; }
			if (!GlobalInfo.localDB.executeSql("Delete From TKTRULE_GS ")) { return false; }
			// 按表的字段确定对象的数据,表结构不存在的数据不保存
			String[] ref = GlobalInfo.localDB.getTableColumns("TKTRULE_GS");
			//if (ref == null || ref.length <= 0)
				ref = RulesDef.ref;

			String line = CommonMethod.getInsertSql("TKTRULE_GS", ref);
			System.out.println("sql====>"+line);

			if (!GlobalInfo.localDB.setSql(line)) { return false; }

			RulesDef gz = new RulesDef();

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);

				if (!Transition.ConvertToObject(gz, row)) { return false; }

				if (!GlobalInfo.localDB.setObjectToParam(gz, ref)) { return false; }

				if (!GlobalInfo.localDB.executeSql()) { return false; }
			}

			if (!GlobalInfo.localDB.commitTrans()) { return false; }

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
		finally
		{
			PublicMethod.timeEnd(Language.apply("写入本地打印规则信息表耗时: "));
		}
		
		/*CREATE TABLE TKTRULE_GS
		(
		  TRGCODE   VARCHAR2(4) not null,
		  TRGNAME   VARCHAR2(255) not null,
		  TRGMEMO   VARCHAR2(2000) default '',
		  TRGDETAIL VARCHAR2(2000) default '',
		  TRGMKT    VARCHAR2(20) not null,
		  TRGQZ     VARCHAR2(1) not null,
		  TRGFONT1  VARCHAR2(30),
		  TRGBOLD1  VARCHAR2(1),
		  TRGFONT2  VARCHAR2(30),
		  TRGBOLD2  VARCHAR2(1),
		  TRGFONT3  VARCHAR2(30),
		  TRGBOLD3  VARCHAR2(1),
		  TRGBD     VARCHAR2(1),
		  TRGSIZE1  VARCHAR2(10),
		  TRGSIZE2  VARCHAR2(10),
		  TRGSIZE3  VARCHAR2(10),
		  TRGSTART  DATE,
		  TRGEND    DATE,
		  TRGMONEY  NUMBER
		);

*/
	}
	
	public void paraInitDefault()
	{
		super.paraInitDefault();

		GlobalInfo.sysPara.isUseNewBankZS = 'N';//Y
		GlobalInfo.sysPara.BankPaycode ="";//0303|0302
		GlobalInfo.sysPara.BankZSPaycode="";//0304
	}
	
	public void paraConvertByCode(String code, String value)
	{
		super.paraConvertByCode(code, value);
		try
		{

			if (code.equals("DL"))
			{
				GlobalInfo.sysPara.printNo = Convert.toInt(value.trim().charAt(0));
				return;
			}
			if (code.equals("W9") && CommonMethod.noEmpty(value))
			{
				String[] s = value.trim().split(",");

				if (s.length > 0) GlobalInfo.sysPara.isUseNewBankZS = s[0].charAt(0);
				if (s.length > 1) GlobalInfo.sysPara.BankPaycode = s[1].trim();
				if (s.length > 2) GlobalInfo.sysPara.BankZSPaycode = s[2].trim();
				return;
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public boolean getRule(Vector PrintRules, String Ysje, String Rqsj) {
		ResultSet rs = null;
		try {
			PublicMethod.timeStart(Language.apply("正在查询本地规则库,请等待......"));
			rs = GlobalInfo.localDB
					.selectData("select * from TKTRULE_GS WHERE strftime('%Y/%m/%d %H:%M:%S',TRGSTART)<= '"+Rqsj+"' AND strftime('%Y/%m/%d %H:%M:%S',TRGEND)>= '"+Rqsj+"' AND TRGMONEY<="
							+ Ysje);
			if (rs == null) {
				return false;
			}
			while (rs.next()) {
				RulesDef fDef = new RulesDef();
				if (!GlobalInfo.localDB.getResultSetToObject(fDef)) {
					return false;
				}
				PrintRules.add(fDef);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			GlobalInfo.localDB.resultSetClose();
			PublicMethod.timeEnd(Language.apply("查询本地规则库耗时: "));
		}
	}
	
	
	
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
