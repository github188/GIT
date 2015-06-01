package com.efuture.configure;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableItem;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.swtdesigner.SWTResourceManager;

public class SetPrintBS
{
	public SetPrintEvent setPrintEvent = null;
	public String curLoc = null;
	public Vector oldHeader = new Vector();
	public Vector oldDetail = new Vector();
	public Vector oldTotal = new Vector();
	public Vector oldPay = new Vector();
	public Vector oldBottom = new Vector();
	public int Width = 0;
	public int PagePrint = 0;
	public int AreaPrint = 0;
	public int Area_PageHead = 0;
	public int Area_Header = 0;
	public int Area_Detail = 0;
	public int Area_Total = 0;
	public int Area_Pay = 0;
	public int Area_Bottom = 0;
	public int Area_PageFeet = 0;
	public HashMap itemMap = null;
	public Vector comboInfo = null;
	public String[] Title = { "General", "Header", "Detail", "Total", "Pay", "Bottom" };

	public String[] sbmData = {
	                            "",
	                            Language.apply("武汉广场"),
								"0001",
								"9999",
								Language.apply("周星驰"),
								"0026",
								"2001-09-11",
								"19:00:00",
								"1",
								"12345678",
								Language.apply("IBM笔记本"),
								"2",
								"8200",
								"16400",
								"150",
								"2",
								"300",
								"16100",
								"16100",
								"0",
								"10101010101010101",
								"1001",
								Language.apply("面值卡"),
								"16000",
								"2020202020202",
								Language.apply("零售销售"),
								"0",
								"1",
								"3000300030003",
								Language.apply("台"),
								"8150",
								"8150",
								"40400404040404",
								"58",
								"1003",
								"200",
								"201",
								"200",
								"421",
								"123456",
								"43000",
								"2010101",
								"890",
								"",
								"",
								"500",
								"",
								"",
								"",
								"",
								""};

	public void init()
	{
		Width = 0;
		PagePrint = 0;
		AreaPrint = 0;
		Area_PageHead = 0;
		Area_Header = 0;
		Area_Detail = 0;
		Area_Total = 0;
		Area_Pay = 0;
		Area_Bottom = 0;
		Area_PageFeet = 0;

		setPrintEvent.table.removeAll();
		setPrintEvent.labPreview.setText("");
		curLoc = null;
		oldHeader.removeAllElements();
		oldDetail.removeAllElements();
		oldTotal.removeAllElements();
		oldPay.removeAllElements();
		oldBottom.removeAllElements();
	}

	public void setTableItem()
	{
		setPrintEvent.table.removeAll();
		setTableItemByArea(null, Title[0]);
		if (oldHeader.size() > 0) setTableItemByArea(oldHeader, Title[1]);
		if (oldDetail.size() > 0) setTableItemByArea(oldDetail, Title[2]);
		if (oldTotal.size() > 0) setTableItemByArea(oldTotal, Title[3]);
		if (oldPay.size() > 0) setTableItemByArea(oldPay, Title[4]);
		if (oldBottom.size() > 0) setTableItemByArea(oldBottom, Title[5]);
	}

	public void setTableItemByArea(Vector v, String area)
	{
		TableItem item = null;
		PrintTemplateItem row = null;
		item = new TableItem(setPrintEvent.table, SWT.NULL);
		item.setText(0, "[" + area + "]");
		item.setForeground(SWTResourceManager.getColor(255, 0, 0));

		// General的情况
		if (area.equalsIgnoreCase(Title[0]))
		{
			item = new TableItem(setPrintEvent.table, SWT.NULL);
			item.setText(0, "[Width]");
			item.setText(1, String.valueOf(Width));

			item = new TableItem(setPrintEvent.table, SWT.NULL);
			item.setText(0, "[PagePrint]");
			item.setText(1, convertCode("PagePrint", String.valueOf(PagePrint), 1));

			item = new TableItem(setPrintEvent.table, SWT.NULL);
			item.setText(0, "[AreaPrint]");
			item.setText(1, convertCode("AreaPrint", String.valueOf(AreaPrint), 1));

			item = new TableItem(setPrintEvent.table, SWT.NULL);
			item.setText(0, "[Area_PageHead]");
			item.setText(1, String.valueOf(Area_PageHead));

			item = new TableItem(setPrintEvent.table, SWT.NULL);
			item.setText(0, "[Area_Header]");
			item.setText(1, String.valueOf(Area_Header));

			item = new TableItem(setPrintEvent.table, SWT.NULL);
			item.setText(0, "[Area_Detail]");
			item.setText(1, String.valueOf(Area_Detail));

			item = new TableItem(setPrintEvent.table, SWT.NULL);
			item.setText(0, "[Area_Total]");
			item.setText(1, String.valueOf(Area_Total));

			item = new TableItem(setPrintEvent.table, SWT.NULL);
			item.setText(0, "[Area_Pay]");
			item.setText(1, String.valueOf(Area_Pay));

			item = new TableItem(setPrintEvent.table, SWT.NULL);
			item.setText(0, "[Area_Bottom]");
			item.setText(1, String.valueOf(Area_Bottom));

			item = new TableItem(setPrintEvent.table, SWT.NULL);
			item.setText(0, "[Area_PageFeet]");
			item.setText(1, String.valueOf(Area_PageFeet));
		}
		else
		{
			for (int i = 0; i < v.size(); i++)
			{
				item = new TableItem(setPrintEvent.table, SWT.NULL);
				row = (PrintTemplateItem) v.elementAt(i);
				String name = "";
				if (this.itemMap.get(row.code) != null) name = (String) this.itemMap.get(row.code); // [编号]名称
				item.setText(0, "[" + row.code + "] " + name);
				item.setText(1, convertCode(row.code, String.valueOf(row.rowno), 1));
				item.setText(2, String.valueOf(row.colno)); // 列
				item.setText(3, String.valueOf(row.length)); // 长度
				item.setText(4, convertCode("", String.valueOf(row.alignment), 4));// 对齐
				if (row.text != null) item.setText(5, row.text);// 文本内容
				if (!row.code.equalsIgnoreCase("00") && Integer.parseInt(row.code) < sbmData.length)
				{
					System.out.println(row.code + "  " + sbmData[Integer.parseInt(row.code)]);
					item.setText(6, sbmData[Integer.parseInt(row.code)]);
				}
			}
		}
	}

	// 新增一行
	public void addRow(int index)
	{
		TableItem item = null;

		item = new TableItem(setPrintEvent.table, SWT.NULL, index + 1);

		item.setText(0, "");
		
		setPrintEvent.currentPoint[1] = index + 1;
		setPrintEvent.currentPoint[0] = 0;
		setPrintEvent.findLocation();
		
	}

	// 删除一行
	public void deleteRow(int index)
	{
		if (index < setPrintEvent.table.getItemCount())
		{
			if (isTitle(setPrintEvent.table.getItem(index).getText(0))) { return; }
			setPrintEvent.editor.getEditor().dispose();
			setPrintEvent.table.remove(index);
		}
	}

	// 判断是否区域标记
	public boolean isTitle(String code)
	{
		code = formatTitle(code);
		if (code.equalsIgnoreCase(Title[0]) || code.equalsIgnoreCase(Title[1]) || code.equalsIgnoreCase(Title[2]) || code.equalsIgnoreCase(Title[3])
				|| code.equalsIgnoreCase(Title[4]) || code.equalsIgnoreCase(Title[5])) { return true; }
		return false;
	}

	public String formatTitle(String code)
	{
		if (code.indexOf('[') > -1 && code.indexOf(']') > -1) { return code = code.substring(code.indexOf('[') + 1, code.indexOf(']')); }
		return code;
	}

	// 判断能否预览
	public boolean checkValidDate()
	{
		TableItem item = null;
		// 记录当前是哪一部分
		String curPart = "";
		for (int i = 0; i < setPrintEvent.table.getItemCount(); i++)
		{
			item = setPrintEvent.table.getItem(i);
			String code = formatTitle(item.getText(0).trim());
			String rowno = item.getText(1).trim();
			String colno = item.getText(2).trim();
			String length = item.getText(3).trim();
			String alignment = item.getText(4).trim();

			if (isTitle(code))
			{
				curPart = code;
				continue;
			}

			if (!curPart.equalsIgnoreCase(Title[0])
					&& (code.equals("") || rowno.equals("") || colno.equals("") || length.equals("") || alignment.equals("")))
			{
				new MessageBox(Language.apply("{0} 部分填写不完整" ,new Object[]{curPart}));
				return false;
			}
		}

		return true;
	}

	// 打印预览
	public void preView()
	{

		if (!checkValidDate())
		{
			return;
		}

		setPrintEvent.labPreview.setText("");
		preViewHead();
		preViewDetil();
		preViewTotal();
		preViewPay();
		preViewBottom();
	}

	// 添加table信息至相应的区域vector里
	public Vector addItem(Vector v, int index)
	{
		v = new Vector();
		boolean k = false;
		TableItem item = null;
		for (int i = 0; i < setPrintEvent.table.getItemCount(); i++)
		{
			item = setPrintEvent.table.getItem(i);

			String text = item.getText().trim();

			if ((text.charAt(0) == '[') && (text.charAt(text.length() - 1) == ']'))
			{
				if (formatTitle(text).equalsIgnoreCase(Title[index]))
				{
					k = true;
					continue;
				}
				else
				{
					k = false;
				}
			}

			if (k)
			{
				v.add(item);
			}

		}
		return v;
	}

	// 打印预览头部
	public void preViewHead()
	{
		Vector newHead = null;
		newHead = addItem(newHead, 1);
		printVector(getCollectDataString(newHead, -1, Width));
	}

	// 打印预览商品明细
	public void preViewDetil()
	{
		Vector newDetil = null;
		newDetil = addItem(newDetil, 2);
		printVector(getCollectDataString(newDetil, -1, Width));
	}

	// 打印预览付款明细
	public void preViewPay()
	{
		Vector newPay = null;
		newPay = addItem(newPay, 4);
		printVector(getCollectDataString(newPay, -1, Width));
	}

	// 打印预览合计明细
	public void preViewTotal()
	{
		Vector newTotal = null;
		newTotal = addItem(newTotal, 3);
		printVector(getCollectDataString(newTotal, -1, Width));
	}

	// 打印预览尾部明细
	public void preViewBottom()
	{
		Vector newBottom = null;
		newBottom = addItem(newBottom, 5);
		printVector(getCollectDataString(newBottom, -1, Width));
	}

	public void printVector(Vector v)
	{
		if (v == null) return;

		for (int i = 0; i < v.size(); i++)
		{
			printLine((String) v.elementAt(i) + "\n");
		}
	}

	public Vector getCollectDataString(Vector mode, int index, int maxLength)
	{
		TableItem item = null;
		Vector v = new Vector();
		String code = "";
		int rowno = 1; // 行号
		int colno = 1; // 列号
		int length = 0; // 长度
		int alignment = 0; // 对齐
		String text = ""; // 文本内容
		String value = ""; // 值

		for (int i = 0; i < mode.size(); i++)
		{
			item = (TableItem) mode.get(i);
			if (!"".equals(item.getText(0).trim())) code = formatTitle(convertCode(code, item.getText(0).trim(), 1));
			if (!"".equals(item.getText(1).trim())) rowno = Integer.parseInt(item.getText(1).trim());
			if (!"".equals(item.getText(2).trim())) colno = Integer.parseInt(item.getText(2).trim());
			if (!"".equals(item.getText(3).trim())) length = Integer.parseInt(item.getText(3).trim());
			if (!"".equals(item.getText(4).trim())) alignment = Integer.parseInt(convertCode(code, item.getText(4).trim(), 4));

			text = item.getText(5);

			if (code.equals("00"))
			{
				value = text;
			}
			else
			{
				value = item.getText(6);
			}

			if (rowno > v.size())
			{
				String newLine = "";
				String addLine = value;

				if (addLine != null && addLine.equals("&!")) addLine = null;

				if (value != null && Integer.parseInt(code) != 0 && text != null && !text.trim().equals(""))
				{
					int maxline = length - Convert.countLength(text);
					addLine = text + Convert.appendStringSize("", addLine, 0, maxline, maxline, alignment);
				}

				v.add(Convert.appendStringSize(newLine, addLine, colno, length, maxLength, alignment));
			}
			else
			{
				String oldLine = (String) v.elementAt(rowno - 1);
				String addLine = value;

				if (addLine != null && addLine.equals("&!")) addLine = null;

				if (value != null && Integer.parseInt(code) != 0 && text != null && !text.trim().equals(""))
				{
					int maxline = length - Convert.countLength(text);
					addLine = text + Convert.appendStringSize("", addLine, 0, maxline, maxline, alignment);
				}
				v.add(rowno - 1, Convert.appendStringSize(oldLine, addLine, colno, length, maxLength, alignment));
				v.removeElementAt(rowno);
			}
		}
		return v;
	}

	public void printLine(String s)
	{
		setPrintEvent.labPreview.append(s);
	}

	public boolean addTemplateeItem(PrintTemplateItem item, String curLoc)
	{
		if (curLoc.equalsIgnoreCase("General"))
		{
			if (item.code.equalsIgnoreCase("Width")) Width = item.rowno;
			else if (item.code.equalsIgnoreCase("PagePrint")) PagePrint = item.rowno;
			else if (item.code.equalsIgnoreCase("AreaPrint")) AreaPrint = item.rowno;
			else if (item.code.equalsIgnoreCase("Area_PageHead")) Area_PageHead = item.rowno;
			else if (item.code.equalsIgnoreCase("Area_Header")) Area_Header = item.rowno;
			else if (item.code.equalsIgnoreCase("Area_Detail")) Area_Detail = item.rowno;
			else if (item.code.equalsIgnoreCase("Area_Total")) Area_Total = item.rowno;
			else if (item.code.equalsIgnoreCase("Area_Pay")) Area_Pay = item.rowno;
			else if (item.code.equalsIgnoreCase("Area_Bottom")) Area_Bottom = item.rowno;
			else if (item.code.equalsIgnoreCase("Area_PageFeet")) Area_PageFeet = item.rowno;

			return true;
		}
		else if (curLoc.equalsIgnoreCase("Header"))
		{
			oldHeader.add(item);

			return true;
		}
		else if (curLoc.equalsIgnoreCase("Detail"))
		{
			oldDetail.add(item);

			return true;
		}
		else if (curLoc.equalsIgnoreCase("Total"))
		{
			oldTotal.add(item);

			return true;
		}
		else if (curLoc.equalsIgnoreCase("Pay"))
		{
			oldPay.add(item);

			return true;
		}
		else if (curLoc.equalsIgnoreCase("Bottom"))
		{
			oldBottom.add(item);

			return true;
		}

		return false;
	}

	public void ReadTemplateFile(String pathName)
	{
		BufferedReader br = null;
		String line = null;
		comboInfo = new Vector();
		itemMap = new HashMap();
		try
		{
			br = CommonMethod.readFile(pathName);

			if (br == null)
			{
				new MessageBox(Language.apply("文件 ") + pathName + Language.apply(" 不存在"));
			}

			while ((line = br.readLine()) != null)
			{
				if (line.trim().length() <= 0)
				{
					continue;
				}

				if (line.trim().charAt(0) == ';') // 判断是否为备注
				{
					// 读取打印项列表
					if (Character.isDigit(line.trim().charAt(1)))
					{
						String line1 = line.trim().substring(1, line.trim().length());
						String[] items = line1.split(",");

						if (items.length == 2)
						{
							this.itemMap.put(items[0], items[1]);
							this.comboInfo.add("[" + items[0] + "] " + items[1]);
						}
					}
					continue;
				}

				// 判断标记
				if ((line.trim().charAt(0) == '[') && (line.trim().charAt(line.trim().length() - 1) == ']'))
				{
					String line1 = line.trim().substring(1, line.trim().length() - 1);

					for (int i = 0; i < Title.length; i++)
					{
						if (line1.equalsIgnoreCase(Title[i]))
						{
							curLoc = Title[i];
							i = Title.length;
						}
					}
					PrintTemplateItem item = new PrintTemplateItem();
					item.code = line1;
				}
				else
				// 添加 ITEM
				{
					if (curLoc == null) // 未开启 '添加' 标志 或当前要添加的类型不明
					{
						continue;
					}

					int num = line.indexOf("=");

					if ((num < 0) || (num >= (line.length() - 1))) // 没有
					// '=' 或
					// 在最后一格有等号
					{
						continue;
					}

					String code = line.substring(0, num);
					String syntax = line.substring(num + 1);
					String[] row = syntax.split(",");

					// 添加模版项
					PrintTemplateItem item = new PrintTemplateItem();

					try
					{
						item.code = code.trim();
						item.rowno = Integer.parseInt(row[0].trim());
						if (row.length >= 2) item.colno = Integer.parseInt(row[1].trim());
						if (row.length >= 3) item.length = Integer.parseInt(row[2].trim());
						if (row.length >= 4) item.alignment = Integer.parseInt(row[3].trim());
						if (row.length >= 5) item.text = row[4];
					}
					catch (Exception er)
					{
						er.printStackTrace();
						continue;
					}
					// 将已构建的ITEM添加到对应的集合
					addTemplateeItem(item, curLoc);
				}
			}

			br.close();
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}
	}

	// 保存至文件
	public void saveData(String pathName)
	{
		if (!checkValidDate())
		{
			return;
		}

		// 备份原文件
		try
		{
			FileInputStream fi = new FileInputStream(pathName);
			FileOutputStream fo = new FileOutputStream(pathName + ".bak");
			FileChannel sfc = fi.getChannel();
			FileChannel tfc = fo.getChannel();
			sfc.transferTo(0, sfc.size(), tfc);
			sfc.close();
			tfc.close();
			fi.close();
			fo.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		// 修改文件
		BufferedReader br = null;

		String line = null;

		try
		{
			br = CommonMethod.readFile(pathName);

			if (br == null)
			{
				new MessageBox(Language.apply("文件 ") + pathName + Language.apply(" 不存在"));
				return;
			}

			StringBuffer line1 = new StringBuffer();

			while ((line = br.readLine()) != null)
			{
				if (line.trim().length() <= 0)
				{
					line1.append("\r\n");
					continue;
				}

				else if (line.trim().charAt(0) == '[')
				{
					break;
				}
				else
				{
					line1.append(line + "\r\n");
					continue;
				}
			}
			TableItem item = null;
			for (int i = 0; i < setPrintEvent.table.getItemCount(); i++)
			{
				item = setPrintEvent.table.getItem(i);
				if (isTitle(item.getText(0).trim()))
				{
					line1.append("\r\n" + item.getText(0).trim() + "\r\n");
				}
				else
				{
					String code = formatTitle(item.getText(0).trim());
					String rowno = item.getText(1).trim().equals("") ? "" : convertCode(code, item.getText(1).trim(), 1);
					String colno = item.getText(2).trim().equals("") ? "" : "," + item.getText(2).trim();
					String length = item.getText(3).trim().equals("") ? "" : "," + item.getText(3).trim();
					String alignment = item.getText(4).trim().equals("") ? "" : "," + convertCode(code, item.getText(4).trim(), 4);
					String text = item.getText(5).trim().equals("") ? "" : "," + item.getText(5).trim();

					line1.append(code + " = " + rowno + colno + length + alignment + text + "\r\n");
				}
			}
			br.close();
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(pathName), "UTF-8"), true);
			pw.write(line1.toString());
			pw.close();

			new MessageBox(Language.apply("打印模板保存成功"));

		}
		catch (Exception er)
		{
			er.printStackTrace();
		}

	}

	public boolean isGeneral(String str)
	{
		str = formatTitle(str);
		if (str.equalsIgnoreCase("PagePrint") || str.equalsIgnoreCase("AreaPrint") || str.equalsIgnoreCase("Area_PageHead")
				|| str.equalsIgnoreCase("Area_Header") || str.equalsIgnoreCase("Area_Detail") || str.equalsIgnoreCase("Area_Total")
				|| str.equalsIgnoreCase("Area_Pay") || str.equalsIgnoreCase("Area_Bottom") || str.equalsIgnoreCase("Area_PageFeet")
				|| str.equalsIgnoreCase("Width")) { return true; }
		return false;
	}

	public void setSetPrintEvent(SetPrintEvent setPrintEvent)
	{
		this.setPrintEvent = setPrintEvent;
	}

	public String convertCode(String item, String code, int col)
	{
		String str = formatTitle(item.trim());

		if ((str.equalsIgnoreCase("PagePrint") || str.equalsIgnoreCase("AreaPrint")) && col == 1)
		{
			if (code.endsWith("1")) { return Language.apply("是"); }

			if (code.endsWith("0")) { return Language.apply("否"); }

			if (code.endsWith(Language.apply("是"))) { return "1"; }

			if (code.endsWith(Language.apply("否"))) { return "0"; }
		}
		else if (col == 4)
		{
			if (code.endsWith("0")) { return Language.apply("0-左对齐"); }

			if (code.endsWith("1")) { return Language.apply("1-右对齐"); }

			if (code.endsWith("2")) { return Language.apply("2-居中"); }

			if (code.endsWith(Language.apply("0-左对齐"))) { return "0"; }

			if (code.endsWith(Language.apply("1-右对齐"))) { return "1"; }

			if (code.endsWith(Language.apply("2-居中"))) { return "2"; }
		}
		return code;
	}

}
