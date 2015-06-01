package custom.localize.Zmjc;

import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Struct.SaleCustDef;

public class CustInfoBS
{

	public Vector getSaleCfg()
    {
        Vector cfgVec = new Vector();
        
		Zmjc_AccessLocalDB local = (Zmjc_AccessLocalDB)AccessLocalDB.getDefault();
		local.getSaleCfg(cfgVec);

        //
        Vector tableInfo = new Vector();
        String[] row = null;
        for (int j = 0; j < cfgVec.size(); j++)
        {
        	SaleCfgDef cfg = (SaleCfgDef) cfgVec.elementAt(j);
        	if (cfg == null) continue;
        	if (!cfg.scvalue.equalsIgnoreCase("Y")) continue;//是否启用

            row    = new String[5];
        	row[0] = String.valueOf(j+1);
        	row[1] = cfg.scname;
            row[2] = "";
            row[3] = cfg.scpara;//key:用于标识某一行
            row[4] = String.valueOf(cfg.scisbt);//表示是否必输
            tableInfo.add(row);
        }
        
        row    = new String[5];
    	row[0] = "";
    	row[1] = "";
        row[2] = "";
        row[3] = "";
        row[4] = "";
        tableInfo.add(row);//增加空行,为了兼容之前的回车确认
        
        return tableInfo;
    }

	/**
	 * 查询航班号
	 * @param fnumber 航班号
	 * @param isLike 是否模糊匹配航班号
	 * @return
	 */
	public Vector getFlight(String fnumber, boolean isLike)
    {
        Vector flightsVec = new Vector();
        
		Zmjc_AccessLocalDB local = (Zmjc_AccessLocalDB)AccessLocalDB.getDefault();
		local.getFlights(flightsVec, fnumber, isLike);
        

        //
        Vector tableInfo = new Vector();
        String[] row = null;
        for (int j = 0; j < flightsVec.size(); j++)
        {
        	FlightsDef f = (FlightsDef) flightsVec.elementAt(j);
        	if (f == null) continue;

            row    = new String[4];
        	row[0] = String.valueOf(j+1);	//序号
        	row[1] = f.fnumber;				//航班号
            row[2] = f.fairlines;			//航空公司
            row[3] = f.frealtime;				//起飞时间 ftime
            tableInfo.add(row);
        }
        return tableInfo;
    }
	
	public Vector getFlight(String fnumber)
    {
		return getFlight(fnumber, true);
    }
	
	/**
	 * 查询国籍号
	 * @param nNumber 国籍号
	 * @param isLike 是否模糊匹配国籍号
	 * @return
	 */
	public Vector getNationality(String nNumber, boolean isLike)
    {
        Vector nationalityVec = new Vector();
        
		Zmjc_AccessBaseDB base = (Zmjc_AccessBaseDB)AccessBaseDB.getDefault();
		base.getNationality(nationalityVec, nNumber, isLike);
        

        //
        Vector tableInfo = new Vector();
        String[] row = null;
        for (int j = 0; j < nationalityVec.size(); j++)
        {
        	NationalityDef f = (NationalityDef) nationalityVec.elementAt(j);
        	if (f == null) continue;

            row    = new String[4];
        	row[0] = String.valueOf(j+1);	//序号
        	row[1] =f.PCRENAME;				//国籍英
            row[2] = f.PCRCNAME;			//国籍名
            row[3] = "";				//
            tableInfo.add(row);
        }
        return tableInfo;
    }
	
	/**
	 * @param nNumber
	 * @return
	 */
	public Vector getNationality(String nNumber)
    {
		return getNationality(nNumber, true);
    }
	
	//检查护照号是否使用,及获取大类限额信息
	public int checkPassPort(String scpassportno, Vector retVec)
	{
		try
		{
			return ((Zmjc_NetService)NetService.getDefault()).checkPassPort(scpassportno, retVec);
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return -999;
		}
	}
    
	public String getLastFlightNo()
	{
		try
		{
			Zmjc_AccessDayDB day = (Zmjc_AccessDayDB)AccessDayDB.getDefault();
			return day.getLastFlightNo();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return "";
	}
	
	public String getLastNationality()
	{
		try
		{
			Zmjc_AccessDayDB day = (Zmjc_AccessDayDB)AccessDayDB.getDefault();
			return day.getLastNationality();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return "";
	}
	
	public boolean checkCustomer(SaleCustDef saleCust)
	{
		try
		{
			if(((Zmjc_NetService)NetService.getDefault()).checkCustomer(saleCust)==0) return true;			
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		return false;
	}
}
