package custom.localize.Jwyt;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.GoodsInfoQueryBS;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PosTable;

public class Jwyt_GoodsInfoQueryBS extends GoodsInfoQueryBS
{
	public void getGoodsList(Combo combo,Text txtCode,Text txtCode2,PosTable tabGoods)
	{
		if (!GlobalInfo.isOnline)
		{
			new MessageBox("商品查询功能必须联网使用");
			return ;
		}
		super.getGoodsList(combo, txtCode, txtCode2, tabGoods);
	}
}
