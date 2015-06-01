package com.efuture.javaPos.Logic;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Vector;
import java.lang.reflect.Field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ExpressionDeal;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PosTable;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.ICallBack;
import com.efuture.javaPos.UI.Design.GoodsDetailQueryForm;

public class GoodsInfoQueryBS
{
	public ArrayList listgoods = null;
	public ArrayList yhList = null;
	public ArrayList batchList = null;
	public ArrayList arrItem = null;// 模板配置项集合
	public String strJoinSql = null;// 上一次查询的SQL
	public char chrJoinFlag = 'N';// 组合查询标志(1-并,2-或,N-无)
	public char chrIsCloseForm = 'N';// 添加商品后是否关闭窗口
	public ICallBack callBack = null;
	public StringBuffer sbBarcode = null;
	public Vector dynColumn = new Vector();
	public boolean isDefineColumn = false;

	public final String FIN_PATH_MOBAN = GlobalVar.ConfigPath + "\\GoodsInfoQueryMode.ini";

	public GoodsInfoQueryBS()
	{

	}

	// 获得优惠信息列表
	public Vector getYhList(String rqsj, GoodsDef goods, String cardno, String cardtype)
	{
		return null;
	}

	// 打开商品明细介面
	public void openGoodsDetailForm(String barcode, String code, String gz)
	{
		GoodsDef goods = null;

		try
		{
			if (listgoods == null || listgoods.size() < 1)
			{
				new MessageBox(Language.apply("此商品没有明细..."), null, false);
				return;
			}

			for (int i = 0; i < listgoods.size(); i++)
			{
				goods = (GoodsDef) listgoods.get(i);

				if (goods.barcode.trim().equals(barcode) && goods.code.trim().equals(code.trim()) && goods.gz.trim().equals(gz.trim()))
				{
					break;
				}
				else
				{
					goods = null;
				}
			}

			if (goods == null)
				return;

			StringBuffer sbIsCloseForm = new StringBuffer();
			// new
			// GoodsDetailQueryForm(callBack,goods,getBatchList(goods.code,goods.gz,goods.uid),getYhList(goods.code,goods.gz,goods.catid,goods.ppcode,goods.specinfo),sbIsCloseForm);
			getGoodsDetail(goods);
			new GoodsDetailQueryForm(sbBarcode, callBack, goods, null, getYhList(goods.code, goods.gz, goods.catid, goods.ppcode, goods.specinfo), sbIsCloseForm);
			if (sbIsCloseForm != null && sbIsCloseForm.toString().equalsIgnoreCase("Y"))
			{
				chrIsCloseForm = 'Y';
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

	}

	// 获得批量列表
	public ArrayList getBatchList(String code, String gz, String uid)
	{
		try
		{
			batchList = new ArrayList();

			if (GlobalInfo.isOnline)
			{
				NetService.getDefault().getBatchList(batchList, code, gz, uid);
			}
			else
			{
				AccessBaseDB.getDefault().getBatchList(batchList, code, gz, uid);
			}

			if (batchList.size() < 1)
			{
				batchList = null;

				return null;
			}
			else
			{
				return batchList;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	// 查询商品明细
	public void getGoodsDetail(GoodsDef goodsdetail)
	{
		try
		{
			int result = DataService.getDefault().getGoodsDef(goodsdetail, 4, goodsdetail.barcode, goodsdetail.gz, "", "", SellType.RETAIL_SALE);

			if (result != 0)
			{
				new MessageBox(Language.apply("未找到商品明细信息!"));
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	// 获得优惠信息列表
	public ArrayList getYhList(String code, String gz, String catid, String ppcode, String specinfo)
	{
		try
		{
			yhList = new ArrayList();

			if (GlobalInfo.isOnline)
			{
				NetService.getDefault().getYhList(yhList, code, gz, catid, ppcode, specinfo);
			}
			else
			{
				AccessBaseDB.getDefault().getYhList(yhList, code, gz, catid, ppcode, specinfo);
			}

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

	public boolean getGoodsList(ArrayList listgoods, String type, String code1, String code2)
	{
		boolean result = false;
		if (GlobalInfo.isOnline)
		{
			// 如果找不到商品，按原编码重新查询
			result = NetService.getDefault().getGoodsList(listgoods, getFindGoodsItem(type).strCodeType.charAt(0), code1, getWheresql(true, type, code1, code2));
		}
		else
		{
			// 专卖模式下须联网才能使用，因为需要查询库存
			if (ConfigClass.DebugModeString.equals("Z"))
			{
				new MessageBox(Language.apply("商品查询功能须在联网状态下使用"));
				return result;
			}

			// 如果找不到商品，按原编码重新查询
			result = AccessBaseDB.getDefault().getGoodsList(listgoods, getFindGoodsItem(type).strCodeType.charAt(0), code1, getWheresql(false, type, code1, code2));
		}

		return result;
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

			result = getGoodsList(listgoods, combo.getItem(combo.getSelectionIndex()), txtCode.getText().trim(), txtCode2.getText().trim());

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

	public void initData(Combo combo)
	{
		try
		{
			isDefineColumn = false;

			if (readTemplateFile())
			{
				combo.removeAll();
				loadComboValues(combo);
			}
			else
			{
				new MessageBox(Language.apply("出错:加载模板失败!\n模板文件地址为:") + FIN_PATH_MOBAN, null, false);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public boolean readTemplateFile()
	{
		BufferedReader br = null;
		String line = null;
		arrItem = new ArrayList();
		;
		try
		{
			if (!CommonMethod.isFileExist(FIN_PATH_MOBAN))
			{
				createTemplateFile();
			}
			br = CommonMethod.readFileGB2312(FIN_PATH_MOBAN);

			while ((line = br.readLine()) != null)
			{
				if (line.trim().length() <= 0)
				{
					continue;
				}

				if (line.trim().charAt(0) == ';') // 判断是否为备注
				{
					continue;
				}

				if (line.indexOf(",") > 0)
				{
					String[] tmpColumn = new String[3];
					tmpColumn = line.split(",");

					if (tmpColumn.length > 1)
						dynColumn.add(tmpColumn);
				}
				else
				{
					int num = line.indexOf("=");

					if ((num < 0) || (num >= (line.length() - 1))) // 没有 '=' 或
					// 在最后一格有等号
					{
						continue;
					}

					String strQueryCodeType = line.substring(0, num).trim();
					String strSyntax = line.substring(num + 1);
					String[] strArrRow = strSyntax.split(";");

					if (strArrRow.length < 5)
						continue;

					// 添加模版项
					FindGoodsItem item = new FindGoodsItem();
					try
					{
						item.strCodeType = strQueryCodeType;
						item.strCodeName = strArrRow[0].trim();
						item.strLocaleSql = strArrRow[1].trim().toLowerCase();
						item.strRemoteSql = strArrRow[2].trim().toLowerCase();
						item.strInputMode = strArrRow[3].trim().toLowerCase();
						item.strInputTxtDownLimitLen = strArrRow[4].trim();

						arrItem.add(item);

					}
					catch (Exception er)
					{
						er.printStackTrace();

						continue;
					}
				}
			}

			if (dynColumn != null && dynColumn.size() > 0)
				isDefineColumn = true;

			br.close();

			return true;
		}
		catch (Exception er)
		{
			er.printStackTrace();
			return false;
		}
	}

	public boolean createTemplateFile()
	{
		try
		{
			StringBuffer sb = new StringBuffer();
			sb.append(Language.apply(";查询类别 = 查询条件说明 ; 脱网查询SQL语句 ; 联网查询SQL语句; 文本框接收字符类型;文本框接收查询字符串长度下限\r\n"));
			sb.append(Language.apply(";0,商品条码(BARCODE;PGBARCODE)\r\n"));
			sb.append(Language.apply(";1,商品代码(CODE;PGGDID)\r\n"));
			sb.append(Language.apply(";2,商品分析码(FXM;PGANALCODE)\r\n"));
			sb.append("\r\n");
			sb.append("0	= " + Language.apply("商品条码") + " ; barcode = '#value#' ; pgbarcode = '#value#';inputmode=0;inpuTxtLimitLength=1 \r\n");
			sb.append("1	= " + Language.apply("商品代码") + "; code = '#value#';PGGDID = '#value#';inputmode=0;inpuTxtLimitLength=1 \r\n");
			sb.append("2	= " + Language.apply("商品分析码") + " ; FXM = '#value#' ; PGANALCODE = '#value#';inputmode=0;inputTxtLimitLength=1 \r\n");
			PrintWriter pw = CommonMethod.writeFileAppendGBK(FIN_PATH_MOBAN);
			pw.print(sb.toString());
			pw.flush();
			pw.close();
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return false;
	}

	public boolean loadComboValues(Combo combo)
	{
		try
		{
			combo.removeAll();

			if (arrItem.size() <= 0)
			{
				return false;
			}

			for (int i = 0; i < arrItem.size(); i++)
			{
				combo.add(((FindGoodsItem) arrItem.get(i)).strCodeName);
			}

			if (combo.getItemCount() > 0)
			{
				combo.select(0);
			}
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}

	}

	public String getWheresql(boolean isRemoteQuery, String type, String code1, String code2)
	{
		String strSqlwhere = null; // 拼装的WHERE条件
		FindGoodsItem item = null;

		try
		{
			item = getFindGoodsItem(type);
			// 如果未找到查询项,则返回
			if (item == null)
			{
				return null;
			}

			// SQL赋值
			if (isRemoteQuery)
			{
				strSqlwhere = getFormatSql(item.strRemoteSql, code1, code2);
			}
			else
			{
				strSqlwhere = getFormatSql(item.strLocaleSql, code1, code2);
			}

			strSqlwhere = " ( " + strSqlwhere + " ) ";

			// 判断是否为组合查询
			if (String.valueOf(this.chrJoinFlag).equals("1")) // and
			{
				strSqlwhere = this.strJoinSql + " and " + strSqlwhere;
			}
			else if (String.valueOf(this.chrJoinFlag).equals("2")) // or
			{
				strSqlwhere = this.strJoinSql + " or " + strSqlwhere;
			}

			// 清空本次查询的SQL
			this.strJoinSql = "";// strSqlwhere;

			strSqlwhere = " Where " + strSqlwhere;

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			;
		}
		finally
		{
			// 还原组合查询标志
			this.chrJoinFlag = 'N';
		}

		return strSqlwhere;
	}

	// 获取Wheret条件SQL
	public String getWheresql(boolean isRemoteQuery, Combo combo, Text txtCode, Text txtCode2)
	{
		return getWheresql(isRemoteQuery, combo.getItem(combo.getSelectionIndex()), txtCode.getText().trim(), txtCode2.getText().trim());
	}

	private String getFormatSql(String strSql, String code1, String code2)
	{
		String strRetSql = null;
		try
		{
			strRetSql = strSql;

			/*
			 * if (getConditionType(strSql) == 3)//like查询类型,则对 % 与 _ 进行处理 { if
			 * ((txtCode.getText().indexOf("%")) > 0 ||
			 * (txtCode.getText().indexOf("_") > 0)) { strRetSql =
			 * strRetSql.replace("#value#", txtCode.getText()); } else {
			 * //如果是LIKE类型,且款员没有输入 % 或 _ 时,默认为 %?% 查询 strRetSql =
			 * strRetSql.replace("#value#", "%" + txtCode.getText() + "%"); }
			 * 
			 * if (txtCode2.getVisible()) { if
			 * ((txtCode2.getText().indexOf("%")) > 0 ||
			 * (txtCode2.getText().indexOf("_") > 0)) { strRetSql =
			 * strRetSql.replace("#value2#", txtCode2.getText()); } else {
			 * //如果是LIKE类型,且款员没有输入 % 或 _ 时,默认为 %?% 查询 strRetSql =
			 * strRetSql.replace("#value2#", "%" + txtCode2.getText() + "%"); } } }
			 * else { strRetSql = strRetSql.replace("#value#",
			 * txtCode.getText()); strRetSql = strRetSql.replace("#value2#",
			 * txtCode2.getText()); }
			 */

			strRetSql = ExpressionDeal.replace(strRetSql, "#value#", code1);
			strRetSql = ExpressionDeal.replace(strRetSql, "#value2#", code2);

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			// 介于XML传输,对于特殊字符进行处理:
			// #0# 表示 <>
			// #1# 表示 <
			// #2# 表示 >
			strRetSql = ExpressionDeal.replace(strRetSql, "<>", "#0#");
			strRetSql = ExpressionDeal.replace(strRetSql, "<", "#1#");
			strRetSql = ExpressionDeal.replace(strRetSql, ">", "#2#");
		}

		return strRetSql;
	}

	/*
	 * private int getConditionType(String strSql) { try { String[] arr =
	 * strSql.trim().split(" "); if (arr.length >= 2 &&
	 * arr[1].toString().trim().equalsIgnoreCase("like")) { return 3; } }
	 * catch(Exception ex) { ex.printStackTrace(); } return 1; }
	 */

	public int getInputBoxType(String strQueryTypeName)
	{
		try
		{
			FindGoodsItem item = getFindGoodsItem(strQueryTypeName);
			if (item == null)
				return 1;
			if (item.strLocaleSql.indexOf("#value#") > 0 && item.strLocaleSql.indexOf("#value2#") > 0)
			{
				// 两个输入框
				return 2;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		// 一个输入框
		return 1;
	}

	// 设置组合查询标志
	public void setCombQueryFlag(char flag, Combo combo, Text txtCode, Text txtCode2)
	{
		if (txtCode.getText().trim().length() <= 0 && txtCode2.getText().trim().trim().length() <= 0)
			return;
		FindGoodsItem item = null;
		item = getFindGoodsItem(combo.getItem(combo.getSelectionIndex()));
		if (GlobalInfo.isOnline)
		{
			this.strJoinSql = " (" + getFormatSql(item.strRemoteSql, txtCode.getText().trim(), txtCode2.getText().trim()) + ") ";
		}
		else
		{
			this.strJoinSql = " (" + getFormatSql(item.strLocaleSql, txtCode.getText().trim(), txtCode2.getText().trim()) + ") ";
		}

		this.chrJoinFlag = flag;
		new MessageBox(Language.apply("设置\"") + (flag == '1' ? Language.apply("且") : Language.apply("或")) + Language.apply("条件组合查询\"成功"), null, false);

	}

	// 清空
	public void clear()
	{
		try
		{
			if (listgoods != null)
			{
				listgoods.clear();
				listgoods = null;
			}

			if (batchList != null)
			{
				batchList.clear();
				batchList = null;
			}

			if (yhList != null)
			{
				yhList.clear();
				yhList = null;
			}

			this.chrJoinFlag = 'N';
			this.strJoinSql = null;

			if (chrIsCloseForm != 'Y')
			{
				new MessageBox(Language.apply("清除查询结果成功"), null, false);
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

	}

	private FindGoodsItem getFindGoodsItem(String codeOrName)
	{
		try
		{
			for (int i = 0; i < arrItem.size(); i++)
			{
				FindGoodsItem item = null;
				item = (FindGoodsItem) arrItem.get(i);
				if (item.strCodeType.equals(codeOrName.trim()) || item.strCodeName.equals(codeOrName.trim()))
				{
					return item;
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return new FindGoodsItem();
	}

	public int getTxtInputMode(String codeOrName)
	{
		try
		{
			for (int i = 0; i < arrItem.size(); i++)
			{
				FindGoodsItem item = null;
				item = (FindGoodsItem) arrItem.get(i);
				if (item.strCodeType.equals(codeOrName.trim()) || item.strCodeName.equals(codeOrName.trim()))
					return Integer.parseInt(item.strInputMode.split("=")[1].trim());

			}
			return -1;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return -1;
		}
	}

	public int getInputTxtLimitLen(String codeOrName)
	{
		try
		{
			for (int i = 0; i < arrItem.size(); i++)
			{
				FindGoodsItem item = (FindGoodsItem) arrItem.get(i);
				if (item.strCodeType.equals(codeOrName.trim()) || item.strCodeName.equals(codeOrName.trim()))
					return Integer.parseInt(item.strInputTxtDownLimitLen.split("=")[1].trim());
			}
			return 1;
		}
		catch (Exception ex)
		{
			return 1;
		}
	}

	public String[] getDefineColName()
	{
		String[] column = null;
		try
		{
			if (dynColumn != null && dynColumn.size() > 0)
			{
				column = new String[dynColumn.size()];
				for (int i = 0; i < dynColumn.size(); i++)
					column[i] = ((String[]) dynColumn.elementAt(i))[0];
			}
			return column;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public int[] getDefineColWidth()
	{
		int[] columnWidth = null;
		try
		{
			if (dynColumn != null && dynColumn.size() > 0)
			{
				columnWidth = new int[dynColumn.size()];
				for (int i = 0; i < dynColumn.size(); i++)
				{
					String[] tmp = (String[]) dynColumn.elementAt(i);
					if (tmp == null || tmp.equals(""))
						columnWidth[i] = 0;
					else
						columnWidth[i] = Integer.parseInt(tmp[1].trim());
				}
				// columnWidth[i] = Integer.parseInt(((String[])
				// dynColumn.elementAt(i))[1]);
			}
			return columnWidth;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public String[] getDefineColClsName()
	{
		String[] columnClsName = null;
		try
		{
			if (dynColumn != null && dynColumn.size() > 0)
			{
				columnClsName = new String[dynColumn.size()];
				for (int i = 0; i < dynColumn.size(); i++)
				{
					String tmp[] = (String[]) dynColumn.elementAt(i);
					if (tmp == null || tmp.length < 3)
						columnClsName[i] = "null";
					else
						columnClsName[i] = tmp[2].trim();
				}
			}
			return columnClsName;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	class FindGoodsItem
	{
		String strCodeType = null; // 查询类型
		String strCodeName = null; // 查询类型名称
		String strLocaleSql = null; // 本地库SQL
		String strRemoteSql = null; // 远程库SQL
		String strInputMode = null; // txt控件接收的字符类型
		String strInputTxtDownLimitLen=null; // txt控件接收的查询字符串长度下限

	}
}
