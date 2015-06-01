package custom.localize.Zmsy;

import java.sql.ResultSet;
import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.PublicMethod;

import custom.localize.Zmjc.NationalityDef;
import custom.localize.Zmjc.Zmjc_AccessBaseDB;

public class Zmsy_AccessBaseDB extends Zmjc_AccessBaseDB
{


	/**
	 * 获取国籍信息
	 * @param flights 获取的国籍信息(里面装载的是NationalityDef类)
	 * @param fnumber 国籍查询条件
	 * @param isLike 是否模糊查询
	 * @return
	 */
	public boolean getNationality(Vector nationality)
	{		
		ResultSet rs = null;
		String sql = "";
		try
		{
			PublicMethod.timeStart("正在读取本地国籍信息,请等待......");
			
			nationality.removeAllElements();
			
			sql = "SELECT PCRCODE, PCRCNAME, PCRENAME FROM poscoderegion" ;//PCRPCODE为NID，表示常用国籍   order by PCRPCODE sqlite排序有问题，所以默认以过程排序为准
						
			rs = GlobalInfo.baseDB.selectData(sql);
			if (rs == null) { return false; }
			//int i=1;
			while (rs.next())
			{
				NationalityDef nDef = new NationalityDef();
				//i++;
				if (GlobalInfo.baseDB.getResultSetToObject(nDef)) 
				{
					nationality.add(nDef);					
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
			GlobalInfo.baseDB.resultSetClose();
			//
			PublicMethod.timeEnd("读取本地国籍信息耗时: ");
		}
	}
	
	/**
	 * 读取提货地点信息
	 * @param v THPlace.ref  用来接收读取到的提货地点
	 * @return boolean 返回是否成功读取
	 */
	public boolean getTourist_THPlace(Vector v)
	{
		ResultSet rs = null;
		if(v == null)
		v = new Vector();
		try
		{
			PublicMethod.timeStart("正在读取本地提货地点信息,请等待......");
			 
			rs = GlobalInfo.baseDB.selectData("SELECT THBILLNO, THSP, THTIME, THJC, THGKLB FROM TOURIST_THPLACE ");

			if (rs == null) { return false; }

			while (rs.next())
			{
				THPlaceDef th = new THPlaceDef();
				
				if (!GlobalInfo.baseDB.getResultSetToObject(th)) { return false; }
				v.add(th);
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
			GlobalInfo.baseDB.resultSetClose();
			PublicMethod.timeEnd("读取本地取提货地点信息耗时: ");
		}
	}
	
	/**
	 * 读取证件类型
	 * @param v ZJTypeDef.ref 用来接收读取到的证件类型
	 * @return boolean 返回是否成功读取
	 */
	public boolean getZJType(Vector v)
	{
		ResultSet rs = null;
		if(v == null)
		v = new Vector();
		try
		{
			PublicMethod.timeStart("正在读取本地证件类型信息,请等待......");
			 
			rs = GlobalInfo.baseDB.selectData("SELECT ZJID, ZJNAME FROM ZJTYPE ");

			if (rs == null) { return false; }

			while (rs.next())
			{
				ZJTypeDef zj = new ZJTypeDef();
				
				if (!GlobalInfo.baseDB.getResultSetToObject(zj)) { return false; }
				v.add(zj);
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
			GlobalInfo.baseDB.resultSetClose();
			PublicMethod.timeEnd("读取本地取证件类型信息耗时: ");
		}
	}
	
}
