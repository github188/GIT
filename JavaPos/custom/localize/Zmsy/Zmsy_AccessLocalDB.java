package custom.localize.Zmsy;

import java.sql.ResultSet;
import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.PublicMethod;

import custom.localize.Zmjc.FlightsDef;
import custom.localize.Zmjc.Zmjc_AccessLocalDB;


/**
 * 中免三亚
 * @author sf
 *
 */
public class Zmsy_AccessLocalDB extends Zmjc_AccessLocalDB
{
	/*
	*//**
	 * 提货地点
	 * @param v THPlace.ref 
	 * @return boolean 返回是否成功写入本地DB
	 *//*
	public boolean writeTourist_THPlace(Vector v)
	{
		// TODO Auto-generated method stub
		String[] row = null;

		try
		{
			//
			PublicMethod.timeStart("正在写入本地提货地点信息表,请等待......");

			//
			if (!GlobalInfo.localDB.beginTrans()) { return false; }

			if (!GlobalInfo.localDB.executeSql("DELETE FROM TOURIST_THPLACE")) { return false; }

			// 按表的字段确定对象的数据,表结构不存在的数据不保存
			String[] ref = GlobalInfo.localDB.getTableColumns("TOURIST_THPLACE");
			if (ref == null || ref.length <= 0)
				ref = THPlaceDef.ref;

			String line = CommonMethod.getInsertSql("TOURIST_THPLACE", ref);

			if (!GlobalInfo.localDB.setSql(line)) { return false; }

			THPlaceDef th = new THPlaceDef();

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);

				if (!Transition.ConvertToObject(th, row)) { return false; }

				if (!GlobalInfo.localDB.setObjectToParam(th, ref)) { return false; }

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
			//
			PublicMethod.timeEnd("写入本地提货地点信息表耗时: ");
		}
	}
	
	
	*//**
	 * 证件类型
	 * @param v ZJTypeDef.ref 
	 * @return boolean 返回是否成功写入本地DB
	 *//*
	public boolean writeZJType(Vector v)
	{
		// TODO Auto-generated method stub
		String[] row = null;

		try
		{
			//
			PublicMethod.timeStart("正在写入本地证件类型信息表,请等待......");

			//
			if (!GlobalInfo.localDB.beginTrans()) { return false; }

			if (!GlobalInfo.localDB.executeSql("DELETE FROM ZJTYPE")) { return false; }

			// 按表的字段确定对象的数据,表结构不存在的数据不保存
			String[] ref = GlobalInfo.localDB.getTableColumns("ZJTYPE");
			if (ref == null || ref.length <= 0)
				ref = ZJTypeDef.ref;

			String line = CommonMethod.getInsertSql("ZJTYPE", ref);

			if (!GlobalInfo.localDB.setSql(line)) { return false; }

			ZJTypeDef zj = new ZJTypeDef();

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);

				if (!Transition.ConvertToObject(zj, row)) { return false; }

				if (!GlobalInfo.localDB.setObjectToParam(zj, ref)) { return false; }

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
			//
			PublicMethod.timeEnd("写入本地证件类型信息表耗时: ");
		}
	}*/
	
	public void paraInitDefault()
	{
		super.paraInitDefault();
		
        GlobalInfo.sysPara.gwkHGUrl = "N";
        GlobalInfo.sysPara.gwkSvrUrl = null;
        GlobalInfo.sysPara.gwkSvrCmdlist = "";
        GlobalInfo.sysPara.gwkQuan_iszl = "N";
        GlobalInfo.sysPara.sgQuan_paycode = "";
        GlobalInfo.sysPara.enterNum = 0;
        GlobalInfo.sysPara.isInvokeZHX = 'N';
        GlobalInfo.sysPara.ZHXTimeout = 15;
	}
	public void paraConvertByCode(String code, String value)
	{
		super.paraConvertByCode(code, value);
		
		try
		{
			if (code.equals("WW") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.gwkHGUrl = value.trim();
				return;
			}
			else if (code.equals("WX") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.gwkSvrUrl = value.trim();
				return;
			}
			else if (code.equals("WY") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.gwkSvrCmdlist = value.trim();
				return;
			}
			else if (code.equals("WZ") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.gwkQuan_iszl = value.trim();
				return;
			}
			else if (code.equals("W2") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.sgQuan_paycode = value.trim();
				return;
			}
			else if (code.equals("W8") && CommonMethod.noEmpty(value))
			{

				String[] tmpArr = value.split("\\|");

				if (tmpArr.length > 0)
				{
					GlobalInfo.sysPara.isInvokeZHX = tmpArr[0].charAt(0);
				}

				if (tmpArr.length > 1)
				{
					GlobalInfo.sysPara.ZHXTimeout = Convert.toInt(tmpArr[1]);
					if(GlobalInfo.sysPara.ZHXTimeout<=0) GlobalInfo.sysPara.ZHXTimeout=15;
				}

				return;
			}
		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
	}
	
	/**
	 * 获取航班信息
	 * @param flights 获取的航班信息(里面装载的是FlightsDef类)
	 * @param fnumber 航班号查询条件
	 * @param isLike 是否模糊查询
	 * @return
	 */
	public boolean getFlights(Vector flights, String fnumber, boolean isLike)
	{
		ResultSet rs = null;
		String sql = "";
		try
		{
			PublicMethod.timeStart("正在读取本地航班信息,请等待......");

			flights.removeAllElements();
									
			//sql = "SELECT * FROM FLIGHTS WHERE FREALTIME < '" + ManipulateDateTime.getHourMin(3)  + "' and FREALTIME > '" + ManipulateDateTime.getHourMin()  + "' AND " ;//FTIME(老系统)-->FREALTIME(新系统)
			sql = "SELECT * FROM FLIGHTS order by FNUMBER";
			
            //sql += " ORDER BY FREALTIME";
			
			rs = GlobalInfo.localDB.selectData(sql);
			if (rs == null) { return false; }
			int i=1;
			while (rs.next())
			{
				FlightsDef fDef = new FlightsDef();
				i++;
				if (GlobalInfo.localDB.getResultSetToObject(fDef)) 
				{
					flights.add(fDef);					
				}
			} 
			return true;
		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);

			return false;
		}
		finally
		{
			GlobalInfo.localDB.resultSetClose();
			//
			PublicMethod.timeEnd("读取本地航班信息耗时: ");
		}
//		return true;
	}
	
}
