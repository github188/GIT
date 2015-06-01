package custom.localize.Cbcp;

import com.efuture.javaPos.Global.AccessDayDB;

public class Cbcp_AccessDayDB extends AccessDayDB
{

	/*public boolean getlasthhbackinfo(String syjh,StringBuffer buff)
	{
		try{
			Object obj = GlobalInfo.dayDB.selectOneData("select fphm from salehead where syjh = '"+syjh+"'  order by fphm desc");
			
	        if (obj == null)
	        {
	            return false;
	        }
	        else
	        {
	            long fphm = Long.parseLong(String.valueOf(obj));
	            
	            if (fphm > 0)
	            {
	            	buff.append(String.valueOf(fphm));
	            	return gethhbackinfo(syjh,String.valueOf(fphm));
	            }
	            else
	            {
	            	return false;
	            }
	        }
		}catch(Exception er)
		{
			er.printStackTrace();
			return false;
		}
	}
	
	public boolean gethhbackinfo(String syjh,String fphm)
	{
		boolean done = false;
		try{
			Object obj = GlobalInfo.dayDB.selectOneData("select count(*) from salehead where syjh = '"+syjh+"' and fphm = "+fphm+" and hhflag = 'Y' and djlb = '4'");
			
	        if (obj == null)
	        {
	            return done;
	        }
	        else
	        {
	            long seqno = Long.parseLong(String.valueOf(obj));
	            
	            if (seqno > 0)
	            {
	            	done = true;
	            	return done;
	            }
	            else
	            {
	            	return false;
	            }
	        }
		}
		catch(Exception er)
		{
			er.printStackTrace();
			return false;
		}
	}
	
	public double gethhbackYsje(String syjh,String fphm)
	{
		try{
			Object obj = GlobalInfo.dayDB.selectOneData("select ysje from salehead where syjh = '"+syjh+"' and fphm = "+fphm+" and hhflag = 'Y' and djlb = '4'");
			
	        if (obj == null)
	        {
	            return -1;
	        }
	        else
	        {
	            double ysje = Double.parseDouble(String.valueOf(obj));
	            if (ysje > 0)
	            {
	            	return ysje;
	            }
	            else
	            {
	            	return -1;
	            }
	        }
		}
		catch(Exception er)
		{
			er.printStackTrace();
			return -1;
		}
	}
	public boolean getlasthhbackHead(SaleHeadDef salehead,String syjh)
	{
		ResultSet rs = null;  
		 if ((rs = GlobalInfo.dayDB.selectData("select * from salehead where syjh = '"+syjh+"'  order by fphm desc")) != null)
	     {
	         try
			{
				if (rs.next())
				 {
				 	if (!GlobalInfo.dayDB.getResultSetToObject(salehead))
				     {
				 		return false;
				     }
				 }
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				return false;
			}
	         
	         return true;
	     }
		 return false;
	}
	public long getHcHHbackinfo(String syjh,String fphm)
	{
		long ret = -1;
		try{
			Object obj = GlobalInfo.dayDB.selectOneData("select fphm from salehead where syjh = '"+syjh+"'  order by fphm desc");
	        if (obj == null)
	        {
	            return ret;
	        }
	        else
	        {
	            fphm = String.valueOf(Long.parseLong(String.valueOf(obj)));
	        }
			
			obj = GlobalInfo.dayDB.selectOneData("select a.yfphm from salegoods a,salehead b where a.fphm = b.fphm and b.syjh = '"+syjh+"' and b.fphm = "+fphm+" and b.hhflag = 'Y' and b.djlb = '2'");
			
	        if (obj == null)
	        {
	            return ret;
	        }
	        else
	        {
	            long fphm1 = Long.parseLong(String.valueOf(obj));
	            
	            if (fphm1 > 0)
	            {
	            	ret = fphm1;
	            	return ret;
	            }
	            else
	            {
	            	return ret;
	            }
	        }
		}
		catch(Exception er)
		{
			er.printStackTrace();
			return ret;
		}
	}*/
}
