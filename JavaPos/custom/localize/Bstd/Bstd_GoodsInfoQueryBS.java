package custom.localize.Bstd;


import java.util.Vector;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.GoodsInfoQueryBS;
import com.efuture.javaPos.Struct.GoodsDef;


public class Bstd_GoodsInfoQueryBS extends GoodsInfoQueryBS 
{
	
	//	打开商品明细介面
	public void openGoodsDetailForm(String barcode,String code,String gz)
	{
		GoodsDef goods = null;
		
		try
		{
			if (listgoods == null || listgoods.size() < 1) 
			{
				new MessageBox("此商品没有明细...", null, false);
				return ;
			}
			
			for (int i = 0;i < listgoods.size();i++)
			{
				goods = (GoodsDef)listgoods.get(i);
				
				if (goods.barcode.trim().equals(barcode) && goods.code.trim().equals(code.trim()) && goods.gz.trim().equals(gz.trim()))
				{
					break;
				}
				else
				{
					goods = null;
				}
			}
			
			if (goods == null) return ;
			
			getGoodsDetail(goods);
			
			new Bstd_GoodsDetailQueryForm(goods,getYhList(ManipulateDateTime.getCurrentDateTime(),goods,"",""));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
	
	//	获得优惠信息列表
	public Vector getYhList(String rqsj,GoodsDef goods,String cardno,String cardtype)
	{
		try
		{
			
			if (GlobalInfo.isOnline)
	    	{
				return ((Bstd_NetService)NetService.getDefault()).getYhList(rqsj,goods,cardno,cardtype);
	    	}
			else
			{
				return ((Bstd_AccessBaseDB)AccessBaseDB.getDefault()).getYhList(rqsj,goods,cardno,cardtype);
			
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
	
	//	清空
	public void clear()
	{
		if (listgoods != null)
		{
			listgoods.clear();
			listgoods = null;
		}
	}
}
