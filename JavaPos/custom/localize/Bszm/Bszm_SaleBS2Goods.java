package custom.localize.Bszm;

import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;

public class Bszm_SaleBS2Goods extends Bszm_SaleBS1Cmpop
{

	public GoodsDef findGoodsInfo(String code, String yyyh, String gz, String dzcmscsj, boolean isdzc, StringBuffer slbuf)
	{
		//服装行业中，处理X,M,S,XL等英文大写
		if (GlobalInfo.sysPara.iscodeupper == 'Y')
			code = code.toUpperCase();
		
		return super.findGoodsInfo(code, yyyh, gz, dzcmscsj, isdzc, slbuf);
	}

	public SaleGoodsDef goodsDef2SaleGoods(GoodsDef goodsDef, String yyyh, double quantity, double price, double allprice, boolean dzcm)
	{
		SaleGoodsDef saleGoodsDef = super.goodsDef2SaleGoods(goodsDef, yyyh, quantity, price, allprice, dzcm);

		// 对不同的行业进行属性转换
		// 这个地方以后得规范字段赋值(目前先采用str6到str9)
		saleGoodsDef.str6 = goodsDef.str1; // 服装行业 颜色属性
		saleGoodsDef.str7 = goodsDef.str2; // 服装行业 规格属性

		return saleGoodsDef;
	}
	
	public boolean deleteGoods(int index)
	{
		if(super.deleteGoods(index))
		{
			if (index <0)
				return true;
			
			if(popinfo!=null && popinfo.size()>0 && popinfo.size()>index)
				popinfo.removeElementAt(index);
			return true;
		}
		return false;
	}
}
