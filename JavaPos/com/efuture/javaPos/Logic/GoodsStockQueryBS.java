package com.efuture.javaPos.Logic;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PosTable;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.GoodsStockDef;
import com.efuture.javaPos.Struct.ICallBack;

public class GoodsStockQueryBS
{
	public String strJoinSql = null;// 上一次查询的SQL
	public char chrJoinFlag = 'N';// 组合查询标志(1-并,2-或,N-无)
	public ArrayList listgoods = null;
	public ArrayList yhList = null;
	public ArrayList batchList = null;
	public StringBuffer sbBarcode;
	public ICallBack callBack;
	public boolean isDefineColumn;
	public char chrIsCloseForm;
	public Vector dynColumn = new Vector();
	public ArrayList arrItem = null;// 模板配置项集合

	public final String FIN_PATH_MOBAN = GlobalVar.ConfigPath + "\\GoodsInfoQueryMode.ini";

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
					if (tmp == null || tmp.equals("")) columnWidth[i] = 0;
					else columnWidth[i] = Integer.parseInt(tmp[1].trim());
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

	public int getTxtInputMode(String codeOrName)
	{
		try
		{
			for (int i = 0; i < arrItem.size(); i++)
			{
				FindGoodsItem item = null;
				item = (FindGoodsItem) arrItem.get(i);
				if (item.strCodeType.equals(codeOrName.trim()) || item.strCodeName.equals(codeOrName.trim())) return Integer
																															.parseInt(item.strInputMode
																																						.split("=")[1]
																																										.trim());

			}
			return -1;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return -1;
		}
	}

	//	 清空
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

	public void initData(Combo combo)
	{/*
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
				new MessageBox("出错:加载模板失败!\n模板文件地址为:" + FIN_PATH_MOBAN, null, false);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
*/
	}

	public int getInputTxtLimitLen(String codeOrName)
	{
		try
		{
			for (int i = 0; i < arrItem.size(); i++)
			{
				FindGoodsItem item = (FindGoodsItem) arrItem.get(i);
				if (item.strCodeType.equals(codeOrName.trim()) || item.strCodeName.equals(codeOrName.trim())) return Integer
																															.parseInt(item.strInputTxtDownLimitLen
																																									.split("=")[1]
																																													.trim());
			}
			return 1;
		}
		catch (Exception ex)
		{
			return 1;
		}
	}

	public void getGoodsStockList(Combo combo, Text txtCode, Text txtCode2, PosTable tabGoods)
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

			result = getGoodsStockList(listgoods, combo.getItem(combo.getSelectionIndex()), txtCode.getText().trim(), txtCode2.getText().trim());

			if (result && listgoods.size() > 0)
			{
				for (int i = 0; i < listgoods.size(); i++)
				{
					GoodsStockDef gsd = (GoodsStockDef) listgoods.get(i);

					TableItem item = new TableItem(tabGoods, SWT.NONE);

					if (isDefineColumn)
					{
						String[] columnValue = getDefineColClsName();
						String value[] = new String[columnValue.length];

						value[0] = String.valueOf((i + 1));

						for (int a = 1; a < columnValue.length; a++)
						{
							Field field = gsd.getClass().getField(columnValue[a]);
							value[a] = field.get(gsd).toString();
						}
						item.setText(value);
					}
					else
					{
						String value[] = {
											String.valueOf((i + 1)),
											gsd.goodsbigcode,
											gsd.goodsno,
											gsd.goodsbarcode,
											gsd.goodsyear,
											gsd.goodsquarter,
											gsd.goodscolor,
											gsd.goodscm,
											gsd.goodskc};

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

	/*
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
	 */
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

					if (tmpColumn.length > 1) dynColumn.add(tmpColumn);
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

					if (strArrRow.length < 5) continue;

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

			if (dynColumn != null && dynColumn.size() > 0) isDefineColumn = true;

			br.close();

			return true;
		}
		catch (Exception er)
		{
			er.printStackTrace();
			return false;
		}
	}

	public boolean loadComboValues(Combo combo)
	{
		try
		{
			combo.removeAll();

			if (arrItem.size() <= 0) { return false; }

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

	public boolean createTemplateFile()
	{
		try
		{
			StringBuffer sb = new StringBuffer();
			sb.append(";查询类别 = 查询条件说明 ; 脱网查询SQL语句 ; 联网查询SQL语句; 文本框接收字符类型;文本框接收查询字符串长度下限\r\n");
			sb.append(";0,商品条码(BARCODE;PGBARCODE)\r\n");
			sb.append(";1,商品代码(CODE;PGGDID)\r\n");
			sb.append(";2,商品分析码(FXM;PGANALCODE)\r\n");
			sb.append("\r\n");
			sb.append("0	= 商品条码 ; barcode = '#value#' ; pgbarcode = '#value#';inputmode=0;inpuTxtLimitLength=1 \r\n");
			sb.append("1	= 商品代码 ; code = '#value#';PGGDID = '#value#';inputmode=0;inpuTxtLimitLength=1 \r\n");
			sb.append("2	= 商品分析码 ; FXM = '#value#' ; PGANALCODE = '#value#';inputmode=0;inputTxtLimitLength=1 \r\n");
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

	class FindGoodsItem
	{
		String strCodeType = null; // 查询类型
		String strCodeName = null; // 查询类型名称
		String strLocaleSql = null; // 本地库SQL
		String strRemoteSql = null; // 远程库SQL
		String strInputMode = null; // txt控件接收的字符类型
		String strInputTxtDownLimitLen = null; // txt控件接收的查询字符串长度下限

	}

	private int getGoodsStockItem(String codeOrName)
	{
		try
		{
			if (codeOrName.trim().equals("商品流水码"))
			{
				return 0;
			}
			else if (codeOrName.trim().equals("商品编码"))
			{
				return 1;
			}
			else if (codeOrName.trim().equals("商品货号"))
			{
				return 2;
			}
			
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return -1;
	}

	public boolean getGoodsStockList(ArrayList listgoods, String type, String code1, String code2)
	{
		boolean result = false;
		if (GlobalInfo.isOnline)
		{
			// 如果找不到商品，按原编码重新查询
			result = NetService.getDefault().getGoodsStockList(listgoods, getGoodsStockItem(type), code1);
		}
		else
		{
			new MessageBox(Language.apply("商品查询功能须在联网状态下使用"));
			return result;

		}

		return result;
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
					if (tmp == null || tmp.length < 3) columnClsName[i] = "null";
					else columnClsName[i] = tmp[2].trim();
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
}
