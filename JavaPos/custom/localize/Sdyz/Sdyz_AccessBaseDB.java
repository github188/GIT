package custom.localize.Sdyz;

import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;

public class Sdyz_AccessBaseDB extends com.efuture.javaPos.Global.AccessBaseDB
{
	public void transferPopInfoToGoodsInfo(GoodsDef finalGoods,GoodsPopDef popDef)
	{
		super.transferPopInfoToGoodsInfo(finalGoods,popDef);
		
		// 促销单的yhspace记录是否积分,赋值给str1
		if (popDef != null)
		{
			finalGoods.str1  = (((int)popDef.yhspace) >= 1)?"Y":"N";
		}
		else
		{
			finalGoods.str1 = "";
		}
	}
}
