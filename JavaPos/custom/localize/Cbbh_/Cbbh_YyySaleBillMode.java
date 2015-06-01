package custom.localize.Cbbh;

import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import custom.localize.Bhcm.Bhcm_YyySaleBillMode;

public class Cbbh_YyySaleBillMode extends Bhcm_YyySaleBillMode {

	
	public Vector totalGoodsList = null;

	protected final static int YSB_total = 112;

	protected String extendCase(PrintTemplateItem item, int index) {
		String line = null;

		try {
			switch (Integer.parseInt(item.code)) {
			case YSB_total:
				line = "";
				for (int i = 0; i < totalGoodsList.size(); i++) {
					line += ((String[]) totalGoodsList.elementAt(i))[0] + "  "
							+ ((String[]) totalGoodsList.elementAt(i))[1]
							+ "\n";
				}
				break;

			}

			return line;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}


	public void printmx() {
		if (GlobalInfo.sysPara.isfp == 'Y') {
			// 设置打印区域
			setPrintArea("Detail");
			totalGoodsList = new Vector();
			// 循环打印商品明细
			for (int i = 0; i < originalsalegoods.size(); i++) {
				String[] list = new String[2];
				SaleGoodsDef sgd = (SaleGoodsDef) originalsalegoods
						.elementAt(i);
				list[0] = sgd.str1;
				list[1] = String.valueOf(sgd.hjje);
				if (totalGoodsList.size() == 0) {
					if (list[0].length() == 0) {
						list[0] = sgd.gz;// 如果汇总数据为空，以柜组汇总
					}
					totalGoodsList.add(list);
					continue;
				}

				boolean flag = true;
				for (int j = 0; j < totalGoodsList.size(); j++) {
					if (list[0].equals(((String[]) totalGoodsList.get(j))[0])) {
						double hjje = Double
								.parseDouble(((String[]) totalGoodsList.get(j))[1]);
						((String[]) totalGoodsList.get(j))[1] = String
								.valueOf(Double.parseDouble(list[1]) + hjje);
						flag = false;
					}
				}
				if (flag)
					totalGoodsList.add(list);

				printVector(getCollectDataString(Detail, i, Width));
			}

		} else {
			
			super.printDetail();
		}
	}

	public void printfp() {

		MessageBox me = new MessageBox(Language.apply("是否打印发票？"), null, true);

		if (me.verify() != GlobalVar.Key1) {
			return;
		}

		// 设置打印方式
		printSetPage();

		// 打印头部区域
		printHeader();

		// 打印明细区域
		printmx();

		// 打印汇总区域
		printTotal();

		// 打印付款区域
		printPay();

		// 打印尾部区域
		printBottom();

		// 切纸
		printCutPaper();
	}
	
	public void printSetPage()
	{
		int n = Integer.parseInt(String.valueOf(GlobalInfo.sysPara.fdprintyyytrack));
		switch(n)
		{
			case 1:
				// 向小票栈输出时应该按小票的输出栈执行
				super.printSetPage();
				break;
			case 2:
//				 设置是否分页打印
				if (PagePrint != 1)
				{
					Printer.getDefault().setPagePrint_Journal(false,1);
				}
				else
				{
					Printer.getDefault().setPagePrint_Journal(true, Area_PageFeet);
				}
				break;
			case 3:
				if (PagePrint != 1)
				{
					Printer.getDefault().setPagePrint_Slip(false,1);
				}
				else
				{
					Printer.getDefault().setPagePrint_Slip(true,Area_PageFeet);
				}
				break;				
			default:
				if (PagePrint != 1)
				{
					Printer.getDefault().setPagePrint_Slip(false,1);
				}
				else
				{
					Printer.getDefault().setPagePrint_Slip(true,Area_PageFeet);
				}
				break;
		}
	}
}
