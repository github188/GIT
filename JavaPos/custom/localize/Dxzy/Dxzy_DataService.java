package custom.localize.Dxzy;

import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bhcm.Bhcm_DataService;

public class Dxzy_DataService extends Bhcm_DataService
{
	public boolean sendSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, Sqldb sql)
	{
		GlobalInfo.sysPara.sendsaletocrm='Y';//强行上传小票到单品库
		return super.sendSaleData(saleHead, saleGoods, salePayment, sql);
	}
	
	public int doRefundExtendSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, Vector retValue)
	{
		return NetService.getDefault().sendSaleData(saleHead, saleGoods, salePayment, retValue);
	}
	
	// 发生小票到单品库
	public void sendSaleDataToMemberDB(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, boolean again)
	{
		for (int i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef goodsDef = (SaleGoodsDef) saleGoods.get(i);
			if(goodsDef==null) continue;
			PosLog.getLog(this.getClass()).info(String.valueOf("单品管理(小票上传前)：code=[" + goodsDef.code + "],大码信息=[" + goodsDef.code + "," + goodsDef.barcode + "," + goodsDef.name + "]."));
			PosLog.getLog(this.getClass()).info(String.valueOf("单品管理(小票上传前)：code=[" + goodsDef.code + "],小码str2=[" + goodsDef.str6 + "]."));
			
			goodsDef.str4 = goodsDef.code;
			if(goodsDef.str6!=null && goodsDef.str6.length()>0)
			{
				String[] arr = goodsDef.str6.split(",");
				if(arr.length>=3)
				{
					//单品库传小码商品信息，单店库传大码商品信息
					//品类
					goodsDef.code = arr[0];
					goodsDef.barcode = arr[1];
					goodsDef.name = arr[2];
					goodsDef.catid = arr[3];
					//goodsDef.str6="";//清空此值
				}
			}
		}
		super.sendSaleDataToMemberDB(saleHead, saleGoods, salePayment, again);
	}
}
