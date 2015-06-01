package custom.localize.Nmzd;

import com.efuture.javaPos.PrintTemplate.YyySaleBillMode;
import com.efuture.javaPos.Struct.SaleGoodsDef;

public class Nmzd_YyySaleBillMode extends YyySaleBillMode
{
//	 按营业员柜组分组
	public void groupByYyyGz()
	{
		//yyyh = group.key_set.get(0);gz = group.key_set.get(1); 
		
		groupset.clear();
		message = "";
		
		SaleGoodsDef sgd = null;
		GroupDef group = null;
		
		for (int i = 0; i < originalsalegoods.size(); i++)
		{
			sgd = (SaleGoodsDef) originalsalegoods.elementAt(i);
			
			// 查找是否相同商品,按营业员柜组分组
			int j = 0;
			for (j = 0; j < groupset.size(); j++)
			{
				group = (GroupDef) groupset.elementAt(j);

				if (group.key1.equals(sgd.yyyh) && group.key2.equals(sgd.gz))
				{
					group.row_set.add(String.valueOf(i));

					break;
				}
			}

			if (j >= groupset.size())
			{
				group = new GroupDef();
				group.key1 = sgd.yyyh;
				group.key2 = sgd.gz;
				group.yyyh = sgd.yyyh;
				group.gz = sgd.gz;
				group.row_set.add(String.valueOf(i));
				groupset.add(group);
			}
		}
		
		message = "请将营业员([key1])的现沽单放入打印机\n\n按‘回车’键后开始打印\n按‘退出’键则跳过打印";
	}
	
	// 按商品分组
	public void groupByGoods()
	{
		//goodscode = group.key_set.get(0)
		
		groupset.clear();
		message = "";
		
		SaleGoodsDef sgd = null;
		GroupDef group = null;
		
		for (int i = 0; i < originalsalegoods.size(); i++)
		{
			sgd = (SaleGoodsDef) originalsalegoods.elementAt(i);
			
			group = new GroupDef();
			group.key1 = sgd.code;
			group.yyyh = sgd.yyyh;
			group.gz = sgd.gz;
			group.row_set.add(String.valueOf(i));
			groupset.add(group);
		}
		
		message = "请将商品([key1])的现沽单放入打印机\n\n按‘回车’键后开始打印\n按‘退出’键则跳过打印";
	}
	
	// 按柜组
	public void groupByGz()
	{
		//yyyh = group.key_set.get(0);gz = group.key_set.get(1); 
		
		groupset.clear();
		message = "";
		
		SaleGoodsDef sgd = null;
		GroupDef group = null;
		
		for (int i = 0; i < originalsalegoods.size(); i++)
		{
			sgd = (SaleGoodsDef) originalsalegoods.elementAt(i);
			
			// 查找是否相同商品,按营业员柜组分组
			int j = 0;
			for (j = 0; j < groupset.size(); j++)
			{
				group = (GroupDef) groupset.elementAt(j);

				if (group.key1.equals(sgd.gz))
				{
					group.row_set.add(String.valueOf(i));

					break;
				}
			}

			if (j >= groupset.size())
			{
				group = new GroupDef();
				group.key1 = sgd.gz;
				group.yyyh = sgd.yyyh;
				group.gz = sgd.gz;
				group.row_set.add(String.valueOf(i));
				groupset.add(group);
			}
		}
		
		message = "请将柜组([key1])的现沽单放入打印机\n\n按‘回车’键后开始打印\n按‘退出’键则跳过打印";
	}
	
	//	 按营业员
	public void groupByYyy()
	{
		//yyyh = group.key_set.get(0);gz = group.key_set.get(1); 
		
		groupset.clear();
		message = "";
		
		SaleGoodsDef sgd = null;
		GroupDef group = null;
		
		for (int i = 0; i < originalsalegoods.size(); i++)
		{
			sgd = (SaleGoodsDef) originalsalegoods.elementAt(i);
			
			// 查找是否相同商品,按营业员柜组分组
			int j = 0;
			for (j = 0; j < groupset.size(); j++)
			{
				group = (GroupDef) groupset.elementAt(j);

				if (group.key1.equals(sgd.yyyh))
				{
					group.row_set.add(String.valueOf(i));

					break;
				}
			}

			if (j >= groupset.size())
			{
				group = new GroupDef();
				group.key1 = sgd.yyyh;
				group.yyyh = sgd.yyyh;
				group.gz = sgd.gz;
				group.row_set.add(String.valueOf(i));
				groupset.add(group);
			}
		}
		
		message = "请将营业员([key1])的现沽单放入打印机\n\n按‘回车’键后开始打印\n按‘退出’键则跳过打印";
	}
	
	public void groupByFph()
	{
		//yyyh = group.key_set.get(0);gz = group.key_set.get(1); 
		groupset.clear();
		message = "";
		
		SaleGoodsDef sgd = null;
		GroupDef group = null;
		
		for (int i = 0; i < originalsalegoods.size(); i++)
		{
			sgd = (SaleGoodsDef) originalsalegoods.elementAt(i);
			
			int j = 0;
			for (j = 0; j < groupset.size(); j++)
			{
				group = (GroupDef) groupset.elementAt(j);

				if (group.key1.equals(sgd.fph))
				{
					group.row_set.add(String.valueOf(i));

					break;
				}
			}

			if (j >= groupset.size())
			{
				group = new GroupDef();
				group.key1 = sgd.fph;
				group.yyyh = sgd.yyyh;
				group.gz = sgd.gz;
				group.row_set.add(String.valueOf(i));
				groupset.add(group);
			}
		}
		
		message = "请将现沽单号([key1])的现沽单放入打印机\n\n按‘回车’键后开始打印\n按‘退出’键则跳过打印";
	}
}
