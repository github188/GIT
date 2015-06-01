package custom.localize.Shhl;

import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;

import custom.localize.Bstd.Bstd_SaleBillMode;

public class Shhl_SaleBillMode extends Bstd_SaleBillMode
{
	protected final static int SBM_stje = 301;
	protected final static int SBM_fwtyj = 302;
	protected final static int SBM_gkxx = 303;
	protected final static int SBM_jbrfs = 304;
	protected final static int SBM_fwjl = 305;

//	protected String extendCase(PrintTemplateItem item, int index)
//	{
//		String line = null;
//
//		switch (Integer.parseInt(item.code))
//		{
//		case SBM_djlb: // 交易类型
//			if (salehead.djlb.equals("CANCEL"))
//				line = "取消交易";
//			else if (salehead.memo.equalsIgnoreCase("AllBack"))
//				line = String.valueOf(SellType.getDefault().typeExchange(salehead.djlb, salehead.hhflag, salehead)) + "(退货换开)";
//			else
//				line = String.valueOf(SellType.getDefault().typeExchange(salehead.djlb, salehead.hhflag, salehead));
//
//			break;
//
//		case SBM_stje:
//			if (salehead.memo.equalsIgnoreCase("AllBack"))
//				line = "退回金额:              " + salehead.num1;
//			break;
//		case SBM_fwtyj:
//			if (SellType.ISBACK(salehead.djlb))
//				line = "服务台处理意见:";
//			break;
//		case SBM_gkxx:
//			if (SellType.ISBACK(salehead.djlb))
//				line = "顾客签字:                  顾客电话:";
//			break;
//		case SBM_jbrfs:
//			if (SellType.ISBACK(salehead.djlb))
//				line = "经 办 人:                   防 损 人:";
//			break;
//		case SBM_fwjl:
//			if (SellType.ISBACK(salehead.djlb))
//				line = "客服经理(主管):";
//			break;
//
//		}
//
//		return line;
//	}

	protected void printSellBill()
	{
		if (salehead.djlb.equals("CANCEL"))
		{
			printSetPage();

			// 打印头部区域
			printHeader();

			// 打印明细区域
			printDetail();

			// 打印尾部区域
			printBottom();

			// 切纸
			printCutPaper();

			return;
		}

		super.printSellBill();
	}
}
