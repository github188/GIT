package custom.localize.Bjkl;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.ExpressionDeal;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PosTable;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.GoodsInfoQueryBS;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;


public class Bjkl_GoodsInfoQueryBS extends GoodsInfoQueryBS 
{
	// 获得优惠信息列表
	public ArrayList getYhList(String code, String gz, String catid, String ppcode, String specinfo)
	{
		try
		{
			yhList = new ArrayList();
//			String value[] = {String.valueOf(changeType(gpd.type)),
//            String.valueOf(gpd.jsrq +" "+ gpd.jssj),
//            String.valueOf(gpd.jsrq +" "+ gpd.jssj),
//            ManipulatePrecision.doubleToString(gpd.poplsj),
//            ManipulatePrecision.doubleToString(gpd.pophyj),
//            ManipulatePrecision.doubleToString(gpd.poppfj)};

			GoodsPopDef gpd = new GoodsPopDef();
			gpd.type ='1';
			gpd.jsrq = "";

			GoodsDef goodsDef = new GoodsDef();
			//京客隆查询商品详细信息只显示单品优惠的，查询的商品信息时就会查询商品单品优惠的信息，所以将这些信息赋给GoodsPopDef就行    查找标志,1-超市销售/2-柜台销售检查营业员串柜/3-柜台销售不检查营业员串柜/4赠品
			                                                 // 商品对象,查找标志 ,编码，柜组,生鲜商品生产时间,当前时间,小票类型1-销售
			int result = DataService.getDefault().getGoodsDef(goodsDef, 1, code, gz, "", ManipulateDateTime.getCurrentDateTime(), "1");
			
			if (result != 0)
				return null;
						
			if (goodsDef.popdjbh.trim().length() < 1)
				return null;
			
			//将查询的优惠信息赋给 GoodsDef
			gpd.type = goodsDef.poptype;
			gpd.ksrq = goodsDef.str2.replace("/", "");
			gpd.jsrq = goodsDef.str3.replace("/", "");
			gpd.kssj = goodsDef.str4;
			gpd.jssj = goodsDef.str5;
			
			if (goodsDef.poplsj > 0)
			{
				gpd.poplsj = goodsDef.poplsj;
			}
			if (goodsDef.pophyj > 0)
			{
				gpd.pophyj = goodsDef.pophyj;
			}
			if (goodsDef.poppfj > 0)
			{
				gpd.poppfj = goodsDef.poppfj;
			}			

			yhList.add(gpd);
			
			if (yhList.size() < 1)
			{
				yhList = null;

				return null;
			}
			else
			{
				// GoodsPopDef gpd = (GoodsPopDef)yhList.get(0);

				return yhList;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}	
	
	public void getGoodsList(Combo combo, Text txtCode, Text txtCode2, PosTable tabGoods)
	{
		boolean result = false;
		ProgressBox pb = null;

		try
		{
			tabGoods.removeAll();

			if (txtCode.getText() == null || txtCode.getText().trim().length() <= 0)
			{
				txtCode.forceFocus();
				new MessageBox(Language.apply("输入框不能为空..."), null, false);
				return;
			}

			if (txtCode2.getVisible())
			{
				txtCode2.setText(txtCode2.getText().toUpperCase());
				if (txtCode2.getText() == null || txtCode2.getText().trim().length() <= 0)
				{
					txtCode2.forceFocus();
					new MessageBox(Language.apply("输入框2不能为空..."), null, false);
					return;
				}
			}

			listgoods = new ArrayList();

			pb = new ProgressBox();

			pb.setText(Language.apply("正在查询商品....."));

			int index = combo.getSelectionIndex();
			String typeName = combo.getItem(index);
			String code = txtCode.getText().trim();
			
			if (index < 2 )
			{
				if (code.length() < 7 )
				{
					typeName = "1";
					combo.select(1);
				}
				else
				{
					typeName = "0";
					combo.select(0);
				}
			}

			result = getGoodsList(listgoods, typeName, code, txtCode2.getText().trim());

			if (result && listgoods.size() > 0)
			{
				for (int i = 0; i < listgoods.size(); i++)
				{
					GoodsDef goods = (GoodsDef) listgoods.get(i);

					TableItem item = new TableItem(tabGoods, SWT.NONE);

					if (isDefineColumn)
					{
						String[] columnValue = getDefineColClsName();
						String value[] = new String[columnValue.length];

						value[0] = String.valueOf((i + 1));

						for (int a = 1; a < columnValue.length; a++)
						{
							Field field = goods.getClass().getField(columnValue[a]);
							value[a] = field.get(goods).toString();
						}
						item.setText(value);
					}
					else
					{
						String value[] = { String.valueOf((i + 1)), goods.barcode, goods.code, goods.gz, goods.name, goods.unit, ManipulatePrecision.doubleToString(goods.lsj) };

						item.setText(value);
					}
				}

				tabGoods.setSelection(0);

				if (pb != null)
				{
					pb.close();
					pb = null;
				}

				tabGoods.setFocus();
			}
			else
			{
				if (pb != null)
				{
					pb.close();
					pb = null;
				}

				new MessageBox(Language.apply("未找到此商品..."), null, false);
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (pb != null)
			{
				pb.close();
				pb = null;
			}
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
