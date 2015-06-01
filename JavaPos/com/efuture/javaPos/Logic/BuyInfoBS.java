package com.efuture.javaPos.Logic;

import java.sql.ResultSet;
import java.util.Vector;

import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.BuyerInfoDef;

// 付款相关业务类
public class BuyInfoBS extends SaleBS4Refund
{
	public BuyInfoBS()
	{
		super();
		
		if (GlobalInfo.sysPara.noshowcustinfogroup.trim().length() >= 0)
		{
			noshowGroup = GlobalInfo.sysPara.noshowcustinfogroup.trim().split(";");
		}
	}
	
	// 有哪些组不显示
	private String[] noshowGroup = null;
	// 只显示哪些组
	private String[] onlyshowGroup = null;
	
	public void filterBuyInfo(Vector list)
	{
		if (list.size() > 0)
		{
			BuyerInfoDef bid = (BuyerInfoDef)list.get(0);
			
			if (bid.type == '1')
			{
				if (onlyshowGroup != null && onlyshowGroup.length > 0)
				{
					for (int i = 0;i < list.size();i ++)
					{
						BuyerInfoDef bid1 = (BuyerInfoDef)list.get(i);
						
						int j;
						for (j = 0;j< onlyshowGroup.length;j++)
						{
							if (bid1.code.equals(onlyshowGroup[j]))
							{
								break;
							}
						}
						
						if (j >= onlyshowGroup.length)
						{
							list.remove(i--);
						}
					}
				}
				else if (noshowGroup != null && noshowGroup.length > 0)
				{
					for (int i = 0;i < list.size();i ++)
					{
						BuyerInfoDef bid1 = (BuyerInfoDef)list.get(i);
						
						int j;
						for (j = 0;j< noshowGroup.length;j++)
						{
							if (bid1.code.equals(noshowGroup[j]))
							{
								break;
							}
						}
						
						if (j < noshowGroup.length)
						{
							list.remove(i--);
						}
					}
				}
			}	
		}
	}

	public void SetShowGroup(String[] mshowGroup)
	{
		onlyshowGroup = mshowGroup;
	}
	
	public Vector getBuyInfoBySjCode(String sjcode)
	{
		ResultSet rs = null;
		
		try
		{
			Vector list = new Vector();
			
			if ((rs = GlobalInfo.localDB.selectData("select code,type,sjcode,name from BuyerInfo where sjcode = '" + sjcode + "' and type <> '1'")) != null)
			{	
				while (rs.next())
				{
					BuyerInfoDef bid = new BuyerInfoDef();
	
					if (GlobalInfo.localDB.getResultSetToObject(bid, BuyerInfoDef.ref))
					{
						list.add(bid);
					}
				}
			}
			
			filterBuyInfo(list);
			
			return list;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
		finally
		{
			if (rs != null)
			{
				GlobalInfo.localDB.resultSetClose();
			}
		}
	}
	
	public Vector getBuyInfoByCode(String code)
	{
		ResultSet rs = null;
		
		try
		{
			Vector list = new Vector();
			
			if ((rs = GlobalInfo.localDB.selectData("select code,type,sjcode,name from BuyerInfo where code = '" + code + "'")) != null)
			{	
				while (rs.next())
				{
					BuyerInfoDef bid = new BuyerInfoDef();
	
					if (GlobalInfo.localDB.getResultSetToObject(bid, BuyerInfoDef.ref))
					{
						list.add(bid);
					}
				}
			}
			
			filterBuyInfo(list);
			
			return list;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
		finally
		{
			if (rs != null)
			{
				GlobalInfo.localDB.resultSetClose();
			}
		}
	}
	
	public Vector getBuyInfoByType(String type)
	{
		ResultSet rs = null;
		
		try
		{
			Vector list = new Vector();
			
			if ((rs = GlobalInfo.localDB.selectData("select code,type,sjcode,name from BuyerInfo where type = '" + type + "'")) != null)
			{	
				while (rs.next())
				{
					BuyerInfoDef bid = new BuyerInfoDef();
	
					if (GlobalInfo.localDB.getResultSetToObject(bid, BuyerInfoDef.ref))
					{
						list.add(bid);
					}
				}
			}
			
			filterBuyInfo(list);

			return list;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
		finally
		{
			if (rs != null)
			{
				GlobalInfo.localDB.resultSetClose();
			}
		}
	}
}
