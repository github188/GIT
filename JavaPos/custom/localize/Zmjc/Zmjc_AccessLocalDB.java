package custom.localize.Zmjc;

import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.PublicMethod;

import custom.localize.Bcrm.Bcrm_AccessLocalDB;

public class Zmjc_AccessLocalDB extends Bcrm_AccessLocalDB
{
	
	//保存顾客信息
	public boolean writeSaleCfg(Vector v)
	{
		//可参考 writeSyjGrange() 函数
		String[] row = null;

		try
		{
			//
			PublicMethod.timeStart(Language.apply("正在写入本地客户信息表,请等待......"));

			//
			if (!GlobalInfo.localDB.beginTrans()) { return false; }

			if (!GlobalInfo.localDB.executeSql("Delete From SALECFG")) { return false; }

			// 按表的字段确定对象的数据,表结构不存在的数据不保存
			String[] ref = GlobalInfo.localDB.getTableColumns("SALECFG");
			if (ref == null || ref.length <= 0)
				ref = SaleCfgDef.ref;

			String line = CommonMethod.getInsertSql("SALECFG", ref);

			if (!GlobalInfo.localDB.setSql(line)) { return false; }

			SaleCfgDef cusInfo = new SaleCfgDef();

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);

				if (!Transition.ConvertToObject(cusInfo, row)) { return false; }

				if (!GlobalInfo.localDB.setObjectToParam(cusInfo, ref)) { return false; }

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
			PublicMethod.timeEnd(Language.apply("写入本地客户信息表耗时: "));
		}
//		return true;
	}
	
	//保存航班信息
	public boolean writeFlights(Vector v)
	{
		//可参考 writeSyjGrange() 函数
		String[] row = null;

		try
		{
			//
			PublicMethod.timeStart(Language.apply("正在写入本地航班信息表,请等待......"));

			//
			if (!GlobalInfo.localDB.beginTrans()) { return false; }

			if (!GlobalInfo.localDB.executeSql("Delete From FLIGHTS ")) { return false; }

			// 按表的字段确定对象的数据,表结构不存在的数据不保存
			String[] ref = GlobalInfo.localDB.getTableColumns("FLIGHTS");
			if (ref == null || ref.length <= 0)
				ref = FlightsDef.ref;

			String line = CommonMethod.getInsertSql("FLIGHTS", ref);

			if (!GlobalInfo.localDB.setSql(line)) { return false; }

			FlightsDef gz = new FlightsDef();

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
			//
			PublicMethod.timeEnd(Language.apply("写入本地航班信息表耗时: "));
		}
//		return true;
	}
	
	public boolean writeRetFlights(Vector v)
	{
		String[] row = null;

		try
		{
			PublicMethod.timeStart(Language.apply("正在写入本地回程航班信息表,请等待......"));
			if (!GlobalInfo.localDB.beginTrans()) { return false; }
			if (!GlobalInfo.localDB.executeSql("Delete From RETFLIGHT ")) { return false; }
			// 按表的字段确定对象的数据,表结构不存在的数据不保存
			String[] ref = GlobalInfo.localDB.getTableColumns("RETFLIGHT");
			//if (ref == null || ref.length <= 0)
				ref = RetFlightDef.ref;

			String line = CommonMethod.getInsertSql("RETFLIGHT", ref);

			if (!GlobalInfo.localDB.setSql(line)) { return false; }

			RetFlightDef gz = new RetFlightDef();

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
			PublicMethod.timeEnd(Language.apply("写入本地回程航班信息表耗时:"));
		}
//		return true;
	}
	

	/**
	 * 获取顾客信息 
	 * @param saleCfg 获取的顾客信息(里面装载的是SaleCfgDef类)
	 * @return
	 */
	public boolean getSaleCfg(Vector saleCfg)
	{
		//可参考 readSyjGrange() 函数
		ResultSet rs = null;

		try
		{
			//
			PublicMethod.timeStart(Language.apply("正在读取本地顾客信息,请等待......"));

			rs = GlobalInfo.localDB.selectData("SELECT * FROM SALECFG where SCTYPE='2' order by scseq");

			if (rs == null) { return false; }

			while (rs.next())
			{
				SaleCfgDef cfgDef = new SaleCfgDef();
				
				if (!GlobalInfo.localDB.getResultSetToObject(cfgDef)) { return false; }
//				System.out.println(cfgDef.scpara+cfgDef.sctype+cfgDef.scseq+cfgDef.scvalue+cfgDef.scname+cfgDef.scisbt);
				//String[] s= new String[2];
				//s[0] = String.valueOf(cfgDef.scseq);
				//s[1] = cfgDef.scname;
//				s[2] = "";
				saleCfg.add(cfgDef);
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
		finally
		{
			GlobalInfo.localDB.resultSetClose();

			//
			PublicMethod.timeEnd(Language.apply("读取本地顾客信息耗时: "));
		}
//		return true;
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
			PublicMethod.timeStart(Language.apply("正在读取本地航班信息,请等待......"));

			flights.removeAllElements();
									
			sql = "SELECT * FROM FLIGHTS WHERE FREALTIME < '" + ManipulateDateTime.getHourMin(3)  + "' and FREALTIME > '" + ManipulateDateTime.getHourMin()  + "' AND " ;//FTIME(老系统)-->FREALTIME(新系统)
			if(fnumber != null && fnumber.length()>0)
			{				
				//sql += " and fnumber = '" + fnumber.toUpperCase() + "' ";
				if (isLike)
				{
					sql += " upper(fnumber) LIKE '" + fnumber.toUpperCase() + "%' ";
				}
				else
				{
					sql += " upper(fnumber) = '" + fnumber.toUpperCase() + "' ";//upper转为大写
				}
				

			}
			else
			{

				switch (Calendar.DAY_OF_WEEK)
				{
					case 1://周一
						sql += " DATA1 = 'Y'" ;
						break;
					case 2://周二
						sql += " DATA2 = 'Y'" ;
						break;
					case 3://周三
						sql += " DATA3 = 'Y'" ;
						break;
					case 4://周四
						sql += " DATA4 = 'Y'" ;
						break;
					case 5://周五
						sql += " DATA5 = 'Y'" ;
						break;
					case 6://周六
						sql += " DATA6 = 'Y'" ;
						break;
					case 7://周日
						sql += " DATA0 = 'Y'";
						break;

//					default:
//						break;

				}
			}
			
            sql += " ORDER BY FREALTIME";
			
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
			ex.printStackTrace();

			return false;
		}
		finally
		{
			GlobalInfo.localDB.resultSetClose();
			//
			PublicMethod.timeEnd(Language.apply("读取本地航班信息耗时: "));
		}
//		return true;
	}
	
	public void paraConvertByCode(String code, String value)
	{
		super.paraConvertByCode(code, value);
				
		GlobalInfo.sysPara.payprecision = 'N';//中免版本不检查此项(付款界面未启用精度) wangyong add by 2013.6.25
		GlobalInfo.sysPara.paychgmore = 'C';//中免版本强行使用多币+补找零方式 wangyong add by 2013.6.25
				
		try
		{
			if (code.equals("WS") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.bZlPayCode = value.trim();
				return;
			}
			if (code.equals("WT") && CommonMethod.noEmpty(value))
			{
				String[] s = value.trim().split(",");
            	
            	if (s.length > 0) GlobalInfo.sysPara.isEnableCustInput = s[0].trim().charAt(0);
            	if (s.length > 1) GlobalInfo.sysPara.isEnableCustInput_TH = s[1].trim().charAt(0);
				return;
			}
			if (code.equals("WC") && CommonMethod.noEmpty(value))
            {
                GlobalInfo.sysPara.isGroupJSLB = value.trim().charAt(0);
                return;
            }
			else if (code.equals("WU") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.mktZWB = value.trim();
				return;
			}
			else if (code.equals("W3") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isUseClk = value.trim();
				if (GlobalInfo.sysPara.isUseClk.length()<=0) GlobalInfo.sysPara.isUseClk = " ";
				return;
			}
			else if (code.equals("W4") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.enterNum = Convert.toInt(value);
				if (GlobalInfo.sysPara.enterNum < 0) GlobalInfo.sysPara.enterNum = 0;
				return;
			}
			else if (code.equals("W5") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isInputZCD = value.trim().charAt(0);
				return;
			}
			else if (code.equals("W6") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isUsePopLimit = value.trim().charAt(0);
				return;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void paraInitDefault()
	{
		super.paraInitDefault();
		GlobalInfo.sysPara.bZlPayCode = "01";
		GlobalInfo.sysPara.isEnableCustInput = 'Y';
		GlobalInfo.sysPara.isGroupJSLB = 'N';
		GlobalInfo.sysPara.mktZWB = "RMB";
		GlobalInfo.sysPara.isEnableCustInput_TH = 'Y';
        GlobalInfo.sysPara.isUseClk = " ";
        GlobalInfo.sysPara.enterNum = 1;
        GlobalInfo.sysPara.isInputZCD = 'N';
        GlobalInfo.sysPara.isUsePopLimit = 'N';
	}
	
	/**
	 * 获取返程航班信息
	 * 
	 * @param flights
	 *            获取的返程航班信息(里面装载的是RetFlightDef类)
	 * @param rfname
	 *            航班号查询条件
	 * @param isLike
	 *            是否模糊查询
	 * @return
	 */
	public boolean getRetFlights(Vector flights, String rfname, boolean isLike) {
		ResultSet rs = null;
		String sql = "";
		try {
			PublicMethod.timeStart("正在读取本地返程航班信息,请等待......");

			flights.removeAllElements();

			sql = "SELECT rfid, rfname,rfstatus,rfarrtime,rfmemo FROM retflight order by rfid";

			rs = GlobalInfo.localDB.selectData(sql);
			if (rs == null) {
				return false;
			}
			int i = 1;
			while (rs.next()) {
				RetFlightDef fDef = new RetFlightDef();
				i++;
				if (GlobalInfo.localDB.getResultSetToObject(fDef)) {
					flights.add(fDef);
				}
			}
			return true;
		} catch (Exception ex) {
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);

			return false;
		} finally {
			GlobalInfo.localDB.resultSetClose();
			PublicMethod.timeEnd("读取本地返程航班信息耗时: ");
		}
	}
	
}
