package custom.localize.Bhcm;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ExpressionDeal;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.PrintTemplate.YyySaleBillMode;
import com.efuture.javaPos.Struct.SaleGoodsDef;

public class Bhcm_YyySaleBillMode extends YyySaleBillMode {

	public void printBill()
	{
		// GlobalInfo.sysPara.fdprintyyy = (N-不打营业员联但打印小票联/Y-打印营业员联也打印小票/A-打印营业员联但不打印小票)
    	// 超市小票 或者 系统参数定义不打印分单,则不打印营业员小票
		if (
			(GlobalInfo.syjDef.issryyy == 'N') || 
		    (GlobalInfo.syjDef.issryyy == 'A' && ((SaleGoodsDef)originalsalegoods.elementAt(0)).yyyh.equals("超市")) ||
		    (GlobalInfo.sysPara.fdprintyyy == 'N')
			)
    	{
    		return;
    	}
		
		// 按分组进行分单打印
		for (int i = 0;i < groupset.size();i ++)
		{
			// 设置当前分组信息
			curgroup = (GroupDef)groupset.elementAt(i);
			
			// 设置当前分组的salegoods
			salegoods.clear();
			for (int j = 0;j < curgroup.row_set.size();j++)
			{
				int index = Convert.toInt(curgroup.row_set.get(j));
				SaleGoodsDef sgd = (SaleGoodsDef)originalsalegoods.get(index);
				salegoods.add(sgd);
			}
			
			// 从第3栈打印才进行提示
			if (!message.equals("") && GlobalInfo.sysPara.fdprintyyytrack == '3')
			{
				String str = "";
				/*str = message.replace("[key1]", curgroup.key1);
				str = str.replace("[key2]", curgroup.key2);
				str = str.replace("[key3]", curgroup.key3);
				str = str.replace("[yyyh]", curgroup.yyyh);
				str = str.replace("[gz]", curgroup.gz);*/
				
				str = ExpressionDeal.replace(message,"[key1]",curgroup.key1);
				str = ExpressionDeal.replace(str,"[key2]", curgroup.key2);
				str = ExpressionDeal.replace(str,"[key3]", curgroup.key3);
				str = ExpressionDeal.replace(str,"[yyyh]", curgroup.yyyh);
				str = ExpressionDeal.replace(str,"[gz]", curgroup.gz);
				
				if (new MessageBox(str).verify() == GlobalVar.Exit)
				{
					continue;
				}
			}
			
			// 设置打印方式
			printSetPage();

			// 打印头部区域
			printHeader();

			// 打印明细区域
			printDetail();

			// 打印汇总区域
			printTotal();

			// 打印付款区域
			printPay();

			// 打印尾部区域
			printBottom();

			// 切纸
			printCutPaper();
		}
	}
	
}
