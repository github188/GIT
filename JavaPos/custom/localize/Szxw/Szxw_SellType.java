package custom.localize.Szxw;

import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.SaleHeadDef;

public class Szxw_SellType extends SellType
{
	public String typeExchange(String type, char hhFlag,SaleHeadDef salehead)
	{
		
			if (type.equals (EARNEST_SALE))
                return "定金签发";

			else if (type.equals ( EARNEST_SALE_HC))
                return "定金撤销";

			else if (type.equals ( EARNEST_SALE_CLEAR))
                return "消收定金";
                
			else if (type.equals ( EARNEST_BACK))
                return "定金退定";

			else if (type.equals ( EARNEST_BACK_HC))
                return "红冲退订";
                
			else if (type.equals ( EARNEST_BACK_CLEAR))
                return "消退定金";
                
			else
				return super.typeExchange(type, hhFlag,salehead);
		
	}
}
