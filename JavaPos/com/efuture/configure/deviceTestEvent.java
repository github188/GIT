package com.efuture.configure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.JarFile;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Interface.Interface_CashBox;
import com.efuture.javaPos.Device.Interface.Interface_ICCard;
import com.efuture.javaPos.Device.Interface.Interface_KeyBoard;
import com.efuture.javaPos.Device.Interface.Interface_LineDisplay;
import com.efuture.javaPos.Device.Interface.Interface_MSR;
import com.efuture.javaPos.Device.Interface.Interface_Printer;
import com.efuture.javaPos.Device.Interface.Interface_Scanner;
import com.efuture.javaPos.Device.Interface.Interface_ElectronicScale;
import com.efuture.javaPos.Global.Language;
import com.swtdesigner.SWTResourceManager;

import device.CashBox.Printer_CashBox;

public class deviceTestEvent
{
	Text newEditor;
	Combo combo;
	private Vector configini;
	private Vector deviceini;
	BufferedReader br = null;
	BufferedReader br1 = null;
	PrintWriter pw = null;
	PrintWriter pw1 = null;
	public String configName = null;
	public String deviceName = null;

	// 所有类名分类
	public Vector printerClassName;
	public Vector msrClassName;
	public Vector keyBoardClassName;
	public Vector lineDisplayClassName;
	public Vector scannerClassName;
	public Vector cashBoxClassName;
	public Vector ICCardClassName;
	public Vector elecScaleClassName;

	// 所有描述分类
	public Vector printerDiscription;
	public Vector msrDiscription;
	public Vector keyBoardDiscription;
	public Vector lineDisplayDiscription;
	public Vector scannerDiscription;
	public Vector cashBoxDiscription;
	public Vector ICCardDiscription;
	public Vector elecScaleDescription;

	// 所有类实例分类
	public Vector printerInstances;
	public Vector msrInstances;
	public Vector keyBoardInstances;
	public Vector lineDisplayInstances;
	public Vector scannerInstances;
	public Vector cashBoxInstances;
	public Vector ICCardInstances;
	public Vector elecScaleInstances;

	// 类参数
	public Vector printerPara;
	public Vector msrPara;
	public Vector keyBoardPara;
	public Vector lineDisplayPara;
	public Vector ScannerPara;
	public Vector cashBoxPara;
	public Vector ICCardPara;
	public Vector elecScalePara;

	// 类接口
	public Interface_CashBox cashBox;
	public Interface_KeyBoard keyBoard;
	public Interface_Printer printer;
	public Interface_MSR MSR;
	public Interface_Scanner scanner;
	public Interface_LineDisplay linedisplay;
	public Interface_ICCard ICCard;
	public Interface_ElectronicScale elecScale;

	/**
	 * 1 : Printer 2 : MSR 3 : Keyboard 4 : CashBox 5 : LineDisplay 6 : Scanner
	 * 7 : ICCard
	 */
	String[][] config = new String[8][2];
	String[][] device = new String[8][2];
	int[] loadIndex = new int[8];

	final int PrinterIndex = 0;
	final int MSRIndex = 1;
	final int KeyBoardIndex = 2;
	final int CashBoxIndex = 3;
	final int LineDisplayIndex = 4;
	final int ScannerIndex = 5;
	final int ICCardIndex = 6;
	final int elecScaleIndex = 7;

	// printer
	// msr
	// keyboard
	// cashbox
	// linedisplay
	// Scanner
	// ICCard
	public deviceTestEvent(String jarName, String path)
	{
		for (int i = 0; i < 7; i++)
		{
			config[i][0] = "";
			config[i][1] = "";
			device[i][0] = "";
			device[i][1] = "";
		}

		configini = new Vector();
		deviceini = new Vector();

		printerClassName = new Vector();
		msrClassName = new Vector();
		keyBoardClassName = new Vector();
		lineDisplayClassName = new Vector();
		scannerClassName = new Vector();
		cashBoxClassName = new Vector();
		ICCardClassName = new Vector();
		elecScaleClassName = new Vector();

		printerDiscription = new Vector();
		msrDiscription = new Vector();
		keyBoardDiscription = new Vector();
		lineDisplayDiscription = new Vector();
		scannerDiscription = new Vector();
		cashBoxDiscription = new Vector();
		ICCardDiscription = new Vector();
		elecScaleDescription = new Vector();

		printerInstances = new Vector();
		msrInstances = new Vector();
		keyBoardInstances = new Vector();
		lineDisplayInstances = new Vector();
		scannerInstances = new Vector();
		cashBoxInstances = new Vector();
		ICCardInstances = new Vector();
		elecScaleInstances = new Vector();

		printerInstances.add(null);
		printerClassName.add("");
		printerDiscription.add(Language.apply("无打印机设备"));

		msrInstances.add(null);
		msrClassName.add("");
		msrDiscription.add(Language.apply("通用键盘口刷卡设备"));

		keyBoardInstances.add(null);
		keyBoardClassName.add("");
		keyBoardDiscription.add(Language.apply("标准PC键盘"));

		cashBoxInstances.add(null);
		cashBoxClassName.add("");
		cashBoxDiscription.add(Language.apply("无钱箱设备"));

		lineDisplayInstances.add(null);
		lineDisplayClassName.add("");
		lineDisplayDiscription.add(Language.apply("无客显设备"));

		scannerInstances.add(null);
		scannerClassName.add("");
		scannerDiscription.add(Language.apply("通用键盘口扫描设备"));

		ICCardInstances.add(null);
		ICCardClassName.add("");
		ICCardDiscription.add(Language.apply("无IC卡读写设备"));

		elecScaleInstances.add(null);
		elecScaleClassName.add("");
		elecScaleDescription.add(Language.apply("无电子秤设备"));

		readJarName(jarName, path);

		cashBox = null;
		keyBoard = null;
		printer = null;
		MSR = null;
		scanner = null;
		linedisplay = null;
		ICCard = null;
		elecScale = null;

		getCurrentDevice();
	}

	public void getCurrentDevice()
	{
		for (int i = 0; i < printerClassName.size(); i++)
		{
			if ((config[PrinterIndex][1] != null) && (config[PrinterIndex][1].length() > 1) && (config[PrinterIndex][1].equals(((String) printerClassName.elementAt(i)).trim())))
			{
				loadIndex[PrinterIndex] = i;

				break;
			}
		}

		for (int i = 0; i < msrClassName.size(); i++)
		{
			if ((config[MSRIndex][1] != null) && (config[MSRIndex][1].length() > 1) && (config[MSRIndex][1].equals((String) msrClassName.elementAt(i))))
			{
				loadIndex[MSRIndex] = i;

				break;
			}
		}

		for (int i = 0; i < keyBoardClassName.size(); i++)
		{
			if ((config[KeyBoardIndex][1] != null) && (config[KeyBoardIndex][1].length() > 1) && (config[KeyBoardIndex][1].equals((String) keyBoardClassName.elementAt(i))))
			{
				loadIndex[KeyBoardIndex] = i;

				break;
			}
		}

		for (int i = 0; i < cashBoxClassName.size(); i++)
		{
			if ((config[CashBoxIndex][1] != null) && (config[CashBoxIndex][1].length() > 1) && (config[CashBoxIndex][1].equals((String) cashBoxClassName.elementAt(i))))
			{
				loadIndex[CashBoxIndex] = i;

				break;
			}
		}

		for (int i = 0; i < lineDisplayClassName.size(); i++)
		{
			if ((config[LineDisplayIndex][1] != null) && (config[LineDisplayIndex][1].length() > 1) && (config[LineDisplayIndex][1].equals((String) lineDisplayClassName.elementAt(i))))
			{
				loadIndex[LineDisplayIndex] = i;

				break;
			}
		}

		for (int i = 0; i < scannerClassName.size(); i++)
		{
			if ((config[ScannerIndex][1] != null) && (config[ScannerIndex][1].length() > 1) && (config[ScannerIndex][1].equals((String) scannerClassName.elementAt(i))))
			{
				loadIndex[ScannerIndex] = i;

				break;
			}
		}

		for (int i = 0; i < ICCardClassName.size(); i++)
		{
			if ((config[ICCardIndex][1] != null) && (config[ICCardIndex][1].length() > 1) && (config[ICCardIndex][1].equals((String) ICCardClassName.elementAt(i))))
			{
				loadIndex[ICCardIndex] = i;

				break;
			}
		}
		for (int i = 0; i < elecScaleClassName.size(); i++)
		{
			if ((config[elecScaleIndex][1] != null) && (config[elecScaleIndex][1].length() > 1) && (config[elecScaleIndex][1].equals((String) elecScaleClassName.elementAt(i))))
			{
				loadIndex[elecScaleIndex] = i;

				break;
			}
		}
	}

	public void readJarName(String jarName, String path)
	{
		//jarName = "C:/javapos/javaPos.ExtendJar/device.jar";
		configName = path + "//Config.ini";
		deviceName = path + "//deviceName.ini";

		br = CommonMethod.readFile(configName);
		br1 = CommonMethod.readFile(deviceName);

		String line = null;

		try
		{
			while ((line = br.readLine()) != null)
			{
				if (line.trim().length() <= 0)
				{
					continue;
				}

				if (line.trim().indexOf("=") < 0)
				{
					configini.add(new String[] { line.trim() });

					continue;
				}
				else
				{
					String[] row = line.split("&&");
					String[] row1 = new String[3];

					if (row.length > 1)
					{
						row1[2] = row[1].trim();
					}

					String[] row2 = row[0].split("=");

					if (row2.length > 1)
					{
						row1[0] = row2[0].trim();
						row1[1] = row2[1].trim();

						if (row1[0].trim().compareToIgnoreCase("Printer1") == 0)
						{
							config[PrinterIndex] = row1;
						}
						else if (row1[0].trim().compareToIgnoreCase("MSR1") == 0)
						{
							config[MSRIndex] = row1;
						}
						else if (row1[0].trim().compareToIgnoreCase("KeyBoard1") == 0)
						{
							config[KeyBoardIndex] = row1;
						}
						else if (row1[0].trim().compareToIgnoreCase("CashBox1") == 0)
						{
							config[CashBoxIndex] = row1;
						}
						else if (row1[0].trim().compareToIgnoreCase("LineDisplay1") == 0)
						{
							config[LineDisplayIndex] = row1;
						}
						else if (row1[0].trim().compareToIgnoreCase("Scanner1") == 0)
						{
							config[ScannerIndex] = row1;
						}
						else if (row1[0].trim().compareToIgnoreCase("ICCard1") == 0)
						{
							config[ICCardIndex] = row1;
						}
						else if (row1[0].trim().compareToIgnoreCase("ElectronicScale1") == 0)
						{
							config[elecScaleIndex] = row1;
						}
					}
					else
					{
						row1[0] = row[0].trim();
						row1[1] = "";
					}

					if (row1[0] != null)
					{
						row1[0] = row1[0].trim();
					}

					if (row1[1] != null)
					{
						row1[1] = row1[1].trim();
					}

					configini.add(row1);
				}
			}

			while ((line = br1.readLine()) != null)
			{
				if (line.trim().length() <= 0)
				{
					continue;
				}

				if (line.trim().charAt(0) == '[')
				{
					deviceini.add(new String[] { line });

					continue;
				}
				else
				{
					String[] row1 = line.split("=");
					if (row1.length <= 1)
					{
						String[] row2 = new String[] { row1[0], "" };
						row1 = row2;
					}

					if (row1[0].trim().compareToIgnoreCase("Printer") == 0)
					{
						device[PrinterIndex] = row1;
					}
					else if (row1[0].trim().compareToIgnoreCase("MSR") == 0)
					{
						device[MSRIndex] = row1;
					}
					else if (row1[0].trim().compareToIgnoreCase("KeyBoard") == 0)
					{
						device[KeyBoardIndex] = row1;
					}
					else if (row1[0].trim().compareToIgnoreCase("CashBox") == 0)
					{
						device[CashBoxIndex] = row1;
					}
					else if (row1[0].trim().compareToIgnoreCase("LineDisplay") == 0)
					{
						device[LineDisplayIndex] = row1;
					}
					else if (row1[0].trim().compareToIgnoreCase("Scanner") == 0)
					{
						device[ScannerIndex] = row1;
					}
					else if (row1[0].trim().compareToIgnoreCase("ICCard") == 0)
					{
						device[ICCardIndex] = row1;
					}
					else if (row1[0].trim().compareToIgnoreCase("ElectronicScale") == 0)
					{
						device[elecScaleIndex] = row1;
					}

					if (row1[0] != null)
					{
						row1[0] = row1[0].trim();
					}

					if (row1.length > 1 && row1[1] != null)
					{
						row1[1] = row1[1].trim();
					}

					deviceini.add(row1);
				}
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}

			if (br1 != null)
			{
				try
				{
					br1.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}

		try
		{
			JarFile jarFile = new JarFile(jarName);
			Enumeration enum1 = jarFile.entries();

			while (enum1.hasMoreElements())
			{
				String name = String.valueOf(enum1.nextElement());
				System.out.println(name);

				if ((name.indexOf("$") >= 0) || (name.indexOf(".class") < 0))
				{
					continue;
				}
				else
				{
					name = name.substring(0, name.indexOf(".class"));
					name = name.replaceAll("/", ".");
				}

				if (name.indexOf("_Printer") >= 0)
				{
					try
					{
						Class cl = Class.forName(name);
						printer = (Interface_Printer) cl.newInstance();
					}
					catch (ClassNotFoundException e1)
					{
						e1.printStackTrace();

						continue;
					}
					catch (InstantiationException e1)
					{
						e1.printStackTrace();

						continue;
					}
					catch (IllegalAccessException e1)
					{
						e1.printStackTrace();

						continue;
					}
					catch (Exception e1)
					{
						e1.printStackTrace();
						new MessageBox(e1.getMessage());

						continue;
					}

					if (printer.getDiscription() != null)
					{
						printerInstances.add(printer);
						printerClassName.add(name);
						printerDiscription.add(printer.getDiscription());
					}
				}
				else if (name.indexOf("_MSR") >= 0)
				{
					try
					{
						Class cl = Class.forName(name);
						MSR = (Interface_MSR) cl.newInstance();
					}
					catch (ClassNotFoundException e1)
					{
						e1.printStackTrace();

						continue;
					}
					catch (InstantiationException e1)
					{
						e1.printStackTrace();

						continue;
					}
					catch (IllegalAccessException e1)
					{
						e1.printStackTrace();

						continue;
					}
					catch (Exception e1)
					{
						e1.printStackTrace();
						new MessageBox(e1.getMessage());

						continue;
					}

					if (MSR.getDiscription() != null)
					{
						msrInstances.add(MSR);
						msrClassName.add(name);
						msrDiscription.add(MSR.getDiscription());
					}
				}
				else if (name.indexOf("_KeyBoard") >= 0 && !name.endsWith("_KeyBoard_Thread"))
				{
					try
					{
						Class cl = Class.forName(name);
						keyBoard = (Interface_KeyBoard) cl.newInstance();
					}
					catch (ClassNotFoundException e1)
					{
						e1.printStackTrace();

						continue;
					}
					catch (InstantiationException e1)
					{
						e1.printStackTrace();

						continue;
					}
					catch (IllegalAccessException e1)
					{
						e1.printStackTrace();

						continue;
					}
					catch (Exception e1)
					{
						e1.printStackTrace();
						new MessageBox(e1.getMessage());

						continue;
					}

					if (keyBoard.getDiscription() != null)
					{
						keyBoardInstances.add(keyBoard);
						keyBoardClassName.add(name);
						keyBoardDiscription.add(keyBoard.getDiscription());
					}
				}
				else if (name.indexOf("_LineDisplay") >= 0)
				{
					try
					{
						Class cl = Class.forName(name);
						linedisplay = (Interface_LineDisplay) cl.newInstance();
					}
					catch (ClassNotFoundException e1)
					{
						e1.printStackTrace();

						continue;
					}
					catch (InstantiationException e1)
					{
						e1.printStackTrace();

						continue;
					}
					catch (IllegalAccessException e1)
					{
						e1.printStackTrace();

						continue;
					}
					catch (Exception e1)
					{
						e1.printStackTrace();
						new MessageBox(e1.getMessage());

						continue;
					}

					if (linedisplay.getDiscription() != null)
					{
						lineDisplayInstances.add(linedisplay);
						lineDisplayClassName.add(name);
						lineDisplayDiscription.add(linedisplay.getDiscription());
					}
				}
				else if (name.indexOf("_Scanner") >= 0)
				{
					try
					{
						Class cl = Class.forName(name);
						scanner = (Interface_Scanner) cl.newInstance();
					}
					catch (ClassNotFoundException e1)
					{
						e1.printStackTrace();

						continue;
					}
					catch (InstantiationException e1)
					{
						e1.printStackTrace();

						continue;
					}
					catch (IllegalAccessException e1)
					{
						e1.printStackTrace();

						continue;
					}
					catch (Exception e1)
					{
						e1.printStackTrace();
						new MessageBox(e1.getMessage());

						continue;
					}

					if (scanner.getDiscription() != null)
					{
						scannerInstances.add(scanner);
						scannerClassName.add(name);
						scannerDiscription.add(scanner.getDiscription());
					}
				}
				else if (name.indexOf("_CashBox") >= 0)
				{
					try
					{
						Class cl = Class.forName(name);
						cashBox = (Interface_CashBox) cl.newInstance();
					}
					catch (ClassNotFoundException e1)
					{
						e1.printStackTrace();
						new MessageBox(e1.getMessage());

						continue;
					}
					catch (InstantiationException e1)
					{
						e1.printStackTrace();
						new MessageBox(e1.getMessage());

						continue;
					}
					catch (IllegalAccessException e1)
					{
						e1.printStackTrace();
						new MessageBox(e1.getMessage());

						continue;
					}
					catch (Exception e1)
					{
						e1.printStackTrace();
						new MessageBox(e1.getMessage());

						continue;
					}

					if (cashBox.getDiscription() != null)
					{
						cashBoxInstances.add(cashBox);
						cashBoxClassName.add(name);
						cashBoxDiscription.add(cashBox.getDiscription());
					}
				}
				else if (name.indexOf("_ICCard") >= 0)
				{
					try
					{
						Class cl = Class.forName(name);
						ICCard = (Interface_ICCard) cl.newInstance();
					}
					catch (ClassNotFoundException e1)
					{
						e1.printStackTrace();
						new MessageBox(e1.getMessage());

						continue;
					}
					catch (InstantiationException e1)
					{
						e1.printStackTrace();
						new MessageBox(e1.getMessage());

						continue;
					}
					catch (IllegalAccessException e1)
					{
						e1.printStackTrace();
						new MessageBox(e1.getMessage());

						continue;
					}
					catch (Exception e1)
					{
						e1.printStackTrace();
						new MessageBox(e1.getMessage());

						continue;
					}

					if (ICCard.getDiscription() != null)
					{
						ICCardInstances.add(ICCard);
						ICCardClassName.add(name);
						ICCardDiscription.add(ICCard.getDiscription());
					}
				}
				else if (name.indexOf("_ElectronicScale") >= 0)
				{
					try
					{
						Class cl = Class.forName(name);
						elecScale = (Interface_ElectronicScale) cl.newInstance();
					}
					catch (ClassNotFoundException e1)
					{
						e1.printStackTrace();
						new MessageBox(e1.getMessage());

						continue;
					}
					catch (InstantiationException e1)
					{
						e1.printStackTrace();
						new MessageBox(e1.getMessage());

						continue;
					}
					catch (IllegalAccessException e1)
					{
						e1.printStackTrace();
						new MessageBox(e1.getMessage());

						continue;
					}
					catch (Exception e1)
					{
						e1.printStackTrace();
						new MessageBox(e1.getMessage());

						continue;
					}

					if (elecScale.getDiscription() != null)
					{
						elecScaleInstances.add(elecScale);
						elecScaleClassName.add(name);
						elecScaleDescription.add(elecScale.getDiscription());
					}
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void setScannerEvent(final Button open, final Button close, final Button save, final Table table, final Combo className, final Text txt, final Text classTxt)
	{
		final TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;

		for (int i = 0; i < scannerDiscription.size(); i++)
		{
			className.add(ManipulateStr.PadLeft(String.valueOf(i), 2, '0') + " - " + (String) scannerDiscription.elementAt(i));
		}

		className.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent arg0)
			{
				Control oldEditor = editor.getEditor();

				if (oldEditor != null)
				{
					oldEditor.dispose();
				}

				int index = className.getSelectionIndex();

				classTxt.setText((String) scannerClassName.get(index));

				table.removeAll();

				try
				{
					if (scanner != null)
					{
						scanner.close();
					}
				}
				catch (Exception er)
				{
					er.printStackTrace();
				}

				scanner = (Interface_Scanner) scannerInstances.get(index);

				if (scanner != null)
				{
					ScannerPara = scanner.getPara();

					if (ScannerPara == null) { return; }

					TableItem item = null;
					String[] row = null;

					for (int i = 0; i < ScannerPara.size(); i++)
					{
						item = new TableItem(table, SWT.NULL);
						row = (String[]) ScannerPara.elementAt(i);

						if ((config[ScannerIndex][1] != null) && !classTxt.getText().trim().equals(config[ScannerIndex][1]))
						{
							int min = Math.min(2, row.length);

							for (int j = 0; j < min; j++)
							{
								item.setText(j, row[j]);
							}
						}
						else
						{
							String deviceConfig = device[ScannerIndex][1];
							String[] dc = deviceConfig.split(",");
							item.setText(0, row[0]);

							if (dc.length > i)
							{
								item.setText(1, dc[i]);
							}
						}
					}
				}
			}
		});

		className.select(loadIndex[ScannerIndex]);

		SelectionListener selection = new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent arg0)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				if (e.widget.equals(open))
				{
					String line = "";

					for (int i = 0; i < table.getItemCount(); i++)
					{
						TableItem item = table.getItem(i);
						line += ("," + item.getText(1));
					}

					if (line.length() > 0)
					{
						DeviceName.deviceScanner = line.substring(1);
					}
					else
					{
						DeviceName.deviceScanner = "";
					}

					System.out.println(DeviceName.deviceScanner);

					if ((scanner != null) && scanner.open())
					{
						scanner.setEnable(true);
						new MessageBox(Language.apply("扫描仪设备打开成功"));
					}
				}
				else if (e.widget.equals(close))
				{
					if (scanner != null)
					{
						scanner.close();
						new MessageBox(Language.apply("扫描仪设备关闭成功"));
					}
				}
				else if (e.widget.equals(save))
				{
					String line = "";

					for (int i = 0; i < table.getItemCount(); i++)
					{
						TableItem item = table.getItem(i);
						line += ("," + item.getText(1));
					}

					if (line.length() > 0)
					{
						DeviceName.deviceScanner = line.substring(1);
					}
					else
					{
						DeviceName.deviceScanner = "";
					}

					if (saveConfigData("Scanner1", classTxt.getText()) && saveDeviceData("Scanner", DeviceName.deviceScanner))
					{
						new MessageBox(Language.apply("扫描仪配置保存成功"));
					}
					else
					{
						new MessageBox(Language.apply("扫描仪配置保存失败"));
					}
				}
			}
		};

		open.addSelectionListener(selection);
		close.addSelectionListener(selection);
		save.addSelectionListener(selection);

		MouseListener mouse = new MouseListener()
		{
			public void mouseDoubleClick(MouseEvent e)
			{
			}

			public void mouseDown(MouseEvent e)
			{
				Point pt = new Point(e.x, e.y);
				int index = table.getTopIndex();

				while (index < table.getItemCount())
				{
					TableItem item = table.getItem(index);
					Rectangle rect = item.getBounds(1);

					if (rect.contains(pt))
					{
						findLocation(ScannerPara, table, index, 1, editor);

						return;
					}

					index++;
				}
			}

			public void mouseUp(MouseEvent arg0)
			{

			}
		};

		table.addMouseListener(mouse);
	}

	public void setLineDisplayEvent(final Button open, final Button close, final Button save, final Table table, final Combo className, final Button show, final Text txt, final Text classTxt)
	{
		final TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;

		for (int i = 0; i < lineDisplayDiscription.size(); i++)
		{
			className.add(ManipulateStr.PadLeft(String.valueOf(i), 2, '0') + " - " + (String) lineDisplayDiscription.elementAt(i));
		}

		className.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent arg0)
			{
				Control oldEditor = editor.getEditor();

				if (oldEditor != null)
				{
					oldEditor.dispose();
				}

				int index = className.getSelectionIndex();

				classTxt.setText((String) lineDisplayClassName.get(index));

				table.removeAll();

				try
				{
					if (linedisplay != null)
					{
						linedisplay.close();
					}
				}
				catch (Exception er)
				{
					er.printStackTrace();
				}

				linedisplay = (Interface_LineDisplay) lineDisplayInstances.get(index);

				if (linedisplay != null)
				{
					lineDisplayPara = linedisplay.getPara();

					if (lineDisplayPara == null) { return; }

					TableItem item = null;
					String[] row = null;

					for (int i = 0; i < lineDisplayPara.size(); i++)
					{
						item = new TableItem(table, SWT.NULL);
						row = (String[]) lineDisplayPara.elementAt(i);

						if ((config[LineDisplayIndex][1] != null) && !classTxt.getText().trim().equals(config[LineDisplayIndex][1]))
						{
							int min = Math.min(2, row.length);

							for (int j = 0; j < min; j++)
							{
								item.setText(j, row[j]);
							}
						}
						else
						{
							String deviceConfig = device[LineDisplayIndex][1];
							String[] dc = deviceConfig.split(",");
							item.setText(0, row[0]);

							if (dc.length > i)
							{
								item.setText(1, dc[i]);
							}
						}
					}
				}
			}
		});

		className.select(loadIndex[LineDisplayIndex]);

		SelectionListener selection = new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent arg0)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				if (e.widget.equals(open))
				{
					String line = "";

					for (int i = 0; i < table.getItemCount(); i++)
					{
						TableItem item = table.getItem(i);
						line += ("," + item.getText(1));
					}

					if (line.length() > 0)
					{
						DeviceName.deviceLineDisplay = line.substring(1);
					}
					else
					{
						DeviceName.deviceLineDisplay = "";
					}

					System.out.println(DeviceName.deviceLineDisplay);

					if ((linedisplay != null) && linedisplay.open())
					{
						linedisplay.setEnable(true);
						new MessageBox(Language.apply("客显设备打开成功"));
					}
				}
				else if (e.widget.equals(close))
				{
					if (linedisplay != null)
					{
						linedisplay.close();
						new MessageBox(Language.apply("客显设备关闭成功"));
					}
				}
				else if (e.widget.equals(show))
				{
					if ((linedisplay != null) && (txt.getText().length() > 0))
					{
						linedisplay.display(txt.getText());
					}
				}
				else if (e.widget.equals(save))
				{
					String line = "";

					for (int i = 0; i < table.getItemCount(); i++)
					{
						TableItem item = table.getItem(i);
						line += ("," + item.getText(1));
					}

					if (line.length() > 0)
					{
						DeviceName.deviceLineDisplay = line.substring(1);
					}
					else
					{
						DeviceName.deviceLineDisplay = "";
					}

					if (saveConfigData("LineDisplay1", classTxt.getText()) && saveDeviceData("LineDisplay", DeviceName.deviceLineDisplay))
					{
						new MessageBox(Language.apply("客显配置保存成功"));
					}
					else
					{
						new MessageBox(Language.apply("客显配置保存失败"));
					}
				}
			}
		};

		open.addSelectionListener(selection);
		close.addSelectionListener(selection);
		show.addSelectionListener(selection);
		save.addSelectionListener(selection);

		MouseListener mouse = new MouseListener()
		{
			public void mouseDoubleClick(MouseEvent e)
			{

			}

			public void mouseDown(MouseEvent e)
			{

				Point pt = new Point(e.x, e.y);
				int index = table.getTopIndex();

				while (index < table.getItemCount())
				{
					TableItem item = table.getItem(index);
					Rectangle rect = item.getBounds(1);

					if (rect.contains(pt))
					{
						findLocation(lineDisplayPara, table, index, 1, editor);

						return;
					}

					index++;
				}
			}

			public void mouseUp(MouseEvent arg0)
			{

			}
		};

		table.addMouseListener(mouse);
	}

	public void setElecScaleEvent(final Button open, final Button close, final Button save, final Table table, final Combo className, final Button read, final Button write, final Text txt, final Text classTxt)
	{
		final TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;

		for (int i = 0; i < elecScaleDescription.size(); i++)
		{
			className.add(ManipulateStr.PadLeft(String.valueOf(i), 2, '0') + " - " + (String) elecScaleDescription.elementAt(i));
		}

		className.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent arg0)
			{
				Control oldEditor = editor.getEditor();

				if (oldEditor != null)
				{
					oldEditor.dispose();
				}

				int index = className.getSelectionIndex();

				classTxt.setText((String) elecScaleClassName.get(index));

				table.removeAll();

				try
				{
					if (elecScale != null)
					{
						elecScale.close();
					}
				}
				catch (Exception er)
				{
					er.printStackTrace();
				}

				elecScale = (Interface_ElectronicScale) elecScaleInstances.get(index);

				if (elecScale != null)
				{
					elecScalePara = elecScale.getPara();

					if (elecScalePara == null) { return; }

					TableItem item = null;
					String[] row = null;

					for (int i = 0; i < elecScalePara.size(); i++)
					{
						item = new TableItem(table, SWT.NULL);
						row = (String[]) elecScalePara.elementAt(i);

						if ((config[elecScaleIndex][1] != null) && !classTxt.getText().trim().equals(config[elecScaleIndex][1]))
						{
							int min = Math.min(2, row.length);

							for (int j = 0; j < min; j++)
							{
								item.setText(j, row[j]);
							}
						}
						else
						{
							String deviceConfig = device[elecScaleIndex][1];
							String[] dc = deviceConfig.split(",");
							item.setText(0, row[0]);

							if (dc.length > i)
							{
								item.setText(1, dc[i]);
							}
						}
					}
				}
			}
		});

		className.select(loadIndex[elecScaleIndex]);

		SelectionListener selection = new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent arg0)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				if (e.widget.equals(open))
				{
					String line = "";

					for (int i = 0; i < table.getItemCount(); i++)
					{
						TableItem item = table.getItem(i);
						line += ("," + item.getText(1));
					}

					if (line.length() > 0)
					{
						DeviceName.deviceElectronicScale = line.substring(1);
					}
					else
					{
						DeviceName.deviceElectronicScale = "";
					}

					System.out.println(DeviceName.deviceElectronicScale);

					if ((elecScale != null) && elecScale.open())
					{
						new MessageBox(Language.apply("电子秤打开成功"));
					}
				}
				else if (e.widget.equals(close))
				{
					if (elecScale != null)
					{
						elecScale.close();
						new MessageBox(Language.apply("电子秤关闭成功"));
					}
				}
				else if (e.widget.equals(read))
				{
					if ((elecScale != null))
					{
						new MessageBox(Language.apply("请将测试物放置于秤台"));
						if (elecScale.recvData3())
							txt.setText(String.valueOf(elecScale.getWeight()));
						else
							txt.setText(Language.apply("获取重量失败"));
					}
				}
				else if (e.widget.equals(write))
				{
					if ((elecScale != null))
					{
						if (elecScale.sendData4(String.valueOf(88.88)))
							txt.setText(Language.apply("发送价格成功"));
						else
							txt.setText(Language.apply("发送价格失败"));
					}
				}
				else if (e.widget.equals(save))
				{
					String line = "";

					for (int i = 0; i < table.getItemCount(); i++)
					{
						TableItem item = table.getItem(i);
						line += ("," + item.getText(1));
					}

					if (line.length() > 0)
					{
						DeviceName.deviceElectronicScale = line.substring(1);
					}
					else
					{
						DeviceName.deviceElectronicScale = "";
					}

					if (saveConfigData("ElectronicScale1", classTxt.getText()) && saveDeviceData("ElectronicScale", DeviceName.deviceElectronicScale))
					{
						new MessageBox(Language.apply("电子秤配置保存成功"));
					}
					else
					{
						new MessageBox(Language.apply("配置保存失败"));
					}
				}
			}
		};

		open.addSelectionListener(selection);
		close.addSelectionListener(selection);
		read.addSelectionListener(selection);
		save.addSelectionListener(selection);

		MouseListener mouse = new MouseListener()
		{
			public void mouseDoubleClick(MouseEvent e)
			{

			}

			public void mouseDown(MouseEvent e)
			{

				Point pt = new Point(e.x, e.y);
				int index = table.getTopIndex();

				while (index < table.getItemCount())
				{
					TableItem item = table.getItem(index);
					Rectangle rect = item.getBounds(1);

					if (rect.contains(pt))
					{
						findLocation(elecScalePara, table, index, 1, editor);

						return;
					}

					index++;
				}
			}

			public void mouseUp(MouseEvent arg0)
			{

			}
		};

		table.addMouseListener(mouse);

	}

	public void setICCardEvent(final Button open, final Button close, final Button save, final Table table, final Combo className, final Button read, final Text txt, final Text classTxt)
	{
		final TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;

		for (int i = 0; i < ICCardDiscription.size(); i++)
		{
			className.add(ManipulateStr.PadLeft(String.valueOf(i), 2, '0') + " - " + (String) ICCardDiscription.elementAt(i));
		}

		className.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent arg0)
			{
				Control oldEditor = editor.getEditor();

				if (oldEditor != null)
				{
					oldEditor.dispose();
				}

				int index = className.getSelectionIndex();

				classTxt.setText((String) ICCardClassName.get(index));

				table.removeAll();

				try
				{
					if (ICCard != null)
					{
						ICCard.close();
					}
				}
				catch (Exception er)
				{
					er.printStackTrace();
				}

				ICCard = (Interface_ICCard) ICCardInstances.get(index);

				if (ICCard != null)
				{
					ICCardPara = ICCard.getPara();

					if (ICCardPara == null) { return; }

					TableItem item = null;
					String[] row = null;

					for (int i = 0; i < ICCardPara.size(); i++)
					{
						item = new TableItem(table, SWT.NULL);
						row = (String[]) ICCardPara.elementAt(i);

						if ((config[ICCardIndex][1] != null) && !classTxt.getText().trim().equals(config[ICCardIndex][1]))
						{
							int min = Math.min(2, row.length);

							for (int j = 0; j < min; j++)
							{
								item.setText(j, row[j]);
							}
						}
						else
						{
							String deviceConfig = device[ICCardIndex][1];
							String[] dc = deviceConfig.split(",");
							item.setText(0, row[0]);

							if (dc.length > i)
							{
								item.setText(1, dc[i]);
							}
						}
					}
				}
			}
		});

		className.select(loadIndex[ICCardIndex]);

		SelectionListener selection = new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent arg0)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				if (e.widget.equals(open))
				{
					String line = "";

					for (int i = 0; i < table.getItemCount(); i++)
					{
						TableItem item = table.getItem(i);
						line += ("," + item.getText(1));
					}

					if (line.length() > 0)
					{
						DeviceName.deviceICCard = line.substring(1);
					}
					else
					{
						DeviceName.deviceICCard = "";
					}

					System.out.println(DeviceName.deviceICCard);

					if ((ICCard != null) && ICCard.open())
					{
						new MessageBox(Language.apply("IC读卡设备打开成功"));
					}
				}
				else if (e.widget.equals(close))
				{
					if (ICCard != null)
					{
						ICCard.close();
						new MessageBox(Language.apply("IC读卡设备关闭成功"));
					}
				}
				else if (e.widget.equals(read))
				{
					if ((ICCard != null))
					{
						String s = ICCard.findCard();
						if (s != null && s.length() > 0)
							txt.setText(s);
						else
							txt.setText(Language.apply("读不到IC卡号"));
					}
				}
				else if (e.widget.equals(save))
				{
					String line = "";

					for (int i = 0; i < table.getItemCount(); i++)
					{
						TableItem item = table.getItem(i);
						line += ("," + item.getText(1));
					}

					if (line.length() > 0)
					{
						DeviceName.deviceICCard = line.substring(1);
					}
					else
					{
						DeviceName.deviceICCard = "";
					}

					if (saveConfigData("ICCard1", classTxt.getText()) && saveDeviceData("ICCard", DeviceName.deviceICCard))
					{
						new MessageBox(Language.apply("IC读卡配置保存成功"));
					}
					else
					{
						new MessageBox(Language.apply("IC读卡配置保存失败"));
					}
				}
			}
		};

		open.addSelectionListener(selection);
		close.addSelectionListener(selection);
		read.addSelectionListener(selection);
		save.addSelectionListener(selection);

		MouseListener mouse = new MouseListener()
		{
			public void mouseDoubleClick(MouseEvent e)
			{

			}

			public void mouseDown(MouseEvent e)
			{

				Point pt = new Point(e.x, e.y);
				int index = table.getTopIndex();

				while (index < table.getItemCount())
				{
					TableItem item = table.getItem(index);
					Rectangle rect = item.getBounds(1);

					if (rect.contains(pt))
					{
						findLocation(ICCardPara, table, index, 1, editor);

						return;
					}

					index++;
				}
			}

			public void mouseUp(MouseEvent arg0)
			{

			}
		};

		table.addMouseListener(mouse);
	}

	public void setCashBoxEvent(final Button open, final Button close, final Button save, final Table table, final Combo className, final Button openCashBox, final Text classTxt)
	{
		final TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;

		for (int i = 0; i < cashBoxDiscription.size(); i++)
		{
			className.add(ManipulateStr.PadLeft(String.valueOf(i), 2, '0') + " - " + (String) cashBoxDiscription.elementAt(i));
		}

		className.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent arg0)
			{
				Control oldEditor = editor.getEditor();

				if (oldEditor != null)
				{
					oldEditor.dispose();
				}

				int index = className.getSelectionIndex();

				classTxt.setText((String) cashBoxClassName.get(index));

				table.removeAll();

				try
				{
					if (cashBox != null)
					{
						cashBox.close();
					}
				}
				catch (Exception er)
				{
					er.printStackTrace();
				}

				cashBox = (Interface_CashBox) cashBoxInstances.get(index);

				if ((classTxt.getText().indexOf("Printer") >= 0) && (printer != null))
				{
					Printer_CashBox cash = (Printer_CashBox) cashBox;
					cash.setTempPrinter(printer);
				}

				if (cashBox != null)
				{
					cashBoxPara = cashBox.getPara();

					if (cashBoxPara == null) { return; }

					TableItem item = null;
					String[] row = null;

					for (int i = 0; i < cashBoxPara.size(); i++)
					{
						item = new TableItem(table, SWT.NULL);
						row = (String[]) cashBoxPara.elementAt(i);

						if ((config[CashBoxIndex][1] != null) && !classTxt.getText().trim().equals(config[CashBoxIndex][1]))
						{
							int min = Math.min(2, row.length);

							for (int j = 0; j < min; j++)
							{
								item.setText(j, row[j]);
							}
						}
						else
						{
							String deviceConfig = device[CashBoxIndex][1];
							String[] dc = deviceConfig.split(",");
							item.setText(0, row[0]);

							if (dc.length > i)
							{
								item.setText(1, dc[i]);
							}
						}
					}
				}
			}
		});

		className.select(loadIndex[CashBoxIndex]);

		SelectionListener selection = new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent arg0)
			{

			}

			public void widgetSelected(SelectionEvent e)
			{
				if (e.widget.equals(open))
				{
					String line = "";

					for (int i = 0; i < table.getItemCount(); i++)
					{
						TableItem item = table.getItem(i);
						line += ("," + item.getText(1));
					}

					if (line.length() > 0)
					{
						DeviceName.deviceCashBox = line.substring(1);
					}
					else
					{
						DeviceName.deviceCashBox = "";
					}

					System.out.println(DeviceName.deviceCashBox);

					if ((cashBox != null) && cashBox.open())
					{
						cashBox.setEnable(true);
						new MessageBox(Language.apply("钱箱设备打开成功"));
					}
				}
				else if (e.widget.equals(close))
				{
					if (cashBox != null)
					{
						cashBox.close();
						new MessageBox(Language.apply("钱箱设备关闭成功"));
					}
				}
				else if (e.widget.equals(openCashBox))
				{
					if (cashBox != null)
					{
						cashBox.openCashBox();
					}
				}
				else if (e.widget.equals(save))
				{
					String line = "";

					for (int i = 0; i < table.getItemCount(); i++)
					{
						TableItem item = table.getItem(i);
						line += ("," + item.getText(1));
					}

					if (line.length() > 0)
					{
						DeviceName.deviceCashBox = line.substring(1);
					}
					else
					{
						DeviceName.deviceCashBox = "";
					}

					if (saveConfigData("CashBox1", classTxt.getText()) && saveDeviceData("CashBox", DeviceName.deviceCashBox))
					{
						new MessageBox(Language.apply("钱箱配置保存成功"));
					}
					else
					{
						new MessageBox(Language.apply("钱箱配置保存失败"));
					}
				}
			}
		};

		open.addSelectionListener(selection);
		close.addSelectionListener(selection);
		openCashBox.addSelectionListener(selection);
		save.addSelectionListener(selection);

		MouseListener mouse = new MouseListener()
		{
			public void mouseDoubleClick(MouseEvent e)
			{

			}

			public void mouseDown(MouseEvent e)
			{

				Point pt = new Point(e.x, e.y);
				int index = table.getTopIndex();

				while (index < table.getItemCount())
				{
					TableItem item = table.getItem(index);
					Rectangle rect = item.getBounds(1);

					if (rect.contains(pt))
					{
						findLocation(cashBoxPara, table, index, 1, editor);

						return;
					}

					index++;
				}
			}

			public void mouseUp(MouseEvent arg0)
			{

			}
		};

		table.addMouseListener(mouse);
	}

	public void setKeyEvent(final Button open, final Button close, final Button save, final Table table, final Combo className, final Text txt, final Text classTxt)
	{
		final TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;

		for (int i = 0; i < keyBoardDiscription.size(); i++)
		{
			className.add(ManipulateStr.PadLeft(String.valueOf(i), 2, '0') + " - " + (String) keyBoardDiscription.elementAt(i));
		}

		className.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent arg0)
			{
				Control oldEditor = editor.getEditor();

				if (oldEditor != null)
				{
					oldEditor.dispose();
				}

				int index = className.getSelectionIndex();

				classTxt.setText((String) keyBoardClassName.get(index));

				table.removeAll();

				try
				{
					if (keyBoard != null)
					{
						keyBoard.close();
					}
				}
				catch (Exception er)
				{
					er.printStackTrace();
				}

				keyBoard = (Interface_KeyBoard) keyBoardInstances.get(index);

				if (keyBoard != null)
				{
					keyBoardPara = keyBoard.getPara();

					if (keyBoardPara == null) { return; }

					TableItem item = null;
					String[] row = null;

					for (int i = 0; i < keyBoardPara.size(); i++)
					{
						item = new TableItem(table, SWT.NULL);
						row = (String[]) keyBoardPara.elementAt(i);

						if ((config[KeyBoardIndex][1] != null) && !classTxt.getText().trim().equals(config[KeyBoardIndex][1]))
						{
							int min = Math.min(2, row.length);

							for (int j = 0; j < min; j++)
							{
								item.setText(j, row[j]);
							}
						}
						else
						{
							String deviceConfig = device[KeyBoardIndex][1];
							String[] dc = deviceConfig.split(",");
							item.setText(0, row[0]);

							if (dc.length > i)
							{
								item.setText(1, dc[i]);
							}
						}
					}
				}
			}
		});

		className.select(loadIndex[KeyBoardIndex]);

		SelectionListener selection = new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent arg0)
			{

			}

			public void widgetSelected(SelectionEvent e)
			{
				if (e.widget.equals(open))
				{
					String line = "";

					for (int i = 0; i < table.getItemCount(); i++)
					{
						TableItem item = table.getItem(i);
						line += ("," + item.getText(1));
					}

					if (line.length() > 0)
					{
						DeviceName.deviceKeyBoard = line.substring(1);
					}
					else
					{
						DeviceName.deviceKeyBoard = "";
					}

					System.out.println(DeviceName.deviceKeyBoard);

					if ((keyBoard != null) && keyBoard.open())
					{
						keyBoard.setEnable(true);
						new MessageBox(Language.apply("键盘设备打开成功"));
					}
				}
				else if (e.widget.equals(close))
				{
					if (keyBoard != null)
					{
						keyBoard.close();
						new MessageBox(Language.apply("键盘设备关闭成功"));
					}
				}
				else if (e.widget.equals(save))
				{
					String line = "";

					for (int i = 0; i < table.getItemCount(); i++)
					{
						TableItem item = table.getItem(i);
						line += ("," + item.getText(1));
					}

					if (line.length() > 0)
					{
						DeviceName.deviceKeyBoard = line.substring(1);
					}
					else
					{
						DeviceName.deviceKeyBoard = "";
					}

					if (saveConfigData("KeyBoard1", classTxt.getText()) && saveDeviceData("KeyBoard", DeviceName.deviceKeyBoard))
					{
						new MessageBox(Language.apply("键盘配置保存成功"));
					}
					else
					{
						new MessageBox(Language.apply("键盘配置保存失败"));
					}
				}
			}
		};

		open.addSelectionListener(selection);
		close.addSelectionListener(selection);
		save.addSelectionListener(selection);

		MouseListener mouse = new MouseListener()
		{
			public void mouseDoubleClick(MouseEvent e)
			{

			}

			public void mouseDown(MouseEvent e)
			{

				Point pt = new Point(e.x, e.y);
				int index = table.getTopIndex();

				while (index < table.getItemCount())
				{
					TableItem item = table.getItem(index);
					Rectangle rect = item.getBounds(1);

					if (rect.contains(pt))
					{
						findLocation(keyBoardPara, table, index, 1, editor);

						return;
					}

					index++;
				}
			}

			public void mouseUp(MouseEvent arg0)
			{

			}
		};

		table.addMouseListener(mouse);
	}

	public void setMsrEvent(final Button open, final Button close, final Button save, final Table table, final Combo className, final Text track1, final Text track2, final Text track3, final Text classTxt)
	{
		final TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;

		for (int i = 0; i < msrDiscription.size(); i++)
		{
			className.add(ManipulateStr.PadLeft(String.valueOf(i), 2, '0') + " - " + (String) msrDiscription.elementAt(i));
		}

		className.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent arg0)
			{
				Control oldEditor = editor.getEditor();

				if (oldEditor != null)
				{
					oldEditor.dispose();
				}

				int index = className.getSelectionIndex();

				classTxt.setText((String) msrClassName.get(index));

				table.removeAll();

				try
				{
					if (MSR != null)
					{
						MSR.close();
					}
				}
				catch (Exception er)
				{
					er.printStackTrace();
				}

				MSR = (Interface_MSR) msrInstances.get(index);

				if (MSR != null)
				{
					msrPara = MSR.getPara();

					if (msrPara == null) { return; }

					TableItem item = null;
					String[] row = null;

					for (int i = 0; i < msrPara.size(); i++)
					{
						item = new TableItem(table, SWT.NULL);
						row = (String[]) msrPara.elementAt(i);

						if ((config[MSRIndex][1] != null) && !classTxt.getText().trim().equals(config[MSRIndex][1]))
						{
							int min = Math.min(2, row.length);

							for (int j = 0; j < min; j++)
							{
								item.setText(j, row[j]);
							}
						}
						else
						{
							String deviceConfig = device[MSRIndex][1];
							String[] dc = deviceConfig.split(",");
							item.setText(0, row[0]);

							if (dc.length > i)
							{
								item.setText(1, dc[i]);
							}
						}
					}
				}
			}
		});

		className.select(loadIndex[MSRIndex]);

		SelectionListener selection = new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent arg0)
			{

			}

			public void widgetSelected(SelectionEvent e)
			{
				if (com.efuture.javaPos.Device.MSR.havaMSR)
				{
					track1.setText(com.efuture.javaPos.Device.MSR.MSRTrack1);
					track2.setText(com.efuture.javaPos.Device.MSR.MSRTrack2);
					track3.setText(com.efuture.javaPos.Device.MSR.MSRTrack3);
					com.efuture.javaPos.Device.MSR.havaMSR = false;

					return;
				}

				if (e.widget.equals(open))
				{
					String line = "";

					for (int i = 0; i < table.getItemCount(); i++)
					{
						TableItem item = table.getItem(i);
						line += ("," + item.getText(1));
					}

					if (line.length() > 0)
					{
						DeviceName.deviceMSR = line.substring(1);
					}
					else
					{
						DeviceName.deviceMSR = "";
					}

					System.out.println(DeviceName.deviceMSR);

					if ((MSR != null) && MSR.open())
					{
						MSR.setEnable(true);
						new MessageBox(Language.apply("刷卡设备打开成功"));
					}
				}
				else if (e.widget.equals(close))
				{
					if (printer != null)
					{
						printer.close();
						new MessageBox(Language.apply("刷卡设备关闭成功"));
					}
				}
				else if (e.widget.equals(save))
				{
					String line = "";

					for (int i = 0; i < table.getItemCount(); i++)
					{
						TableItem item = table.getItem(i);
						line += ("," + item.getText(1));
					}

					if (line.length() > 0)
					{
						DeviceName.deviceMSR = line.substring(1);
					}
					else
					{
						DeviceName.deviceMSR = "";
					}

					if (saveConfigData("MSR1", classTxt.getText()) && saveDeviceData("MSR", DeviceName.deviceMSR))
					{
						new MessageBox(Language.apply("刷卡配置保存成功"));
					}
					else
					{
						new MessageBox(Language.apply("刷卡配置保存失败"));
					}
				}
			}
		};

		open.addSelectionListener(selection);
		close.addSelectionListener(selection);
		save.addSelectionListener(selection);

		KeyListener key = new KeyListener()
		{
			StringBuffer trackkeycode = new StringBuffer();
			StringBuffer trackkeychar = new StringBuffer();

			public void keyPressed(KeyEvent arg0)
			{
				if (MSR == null)
				{
					trackkeycode.append(arg0.keyCode);
					trackkeycode.append(",");

					if (arg0.keyCode >= 32 && arg0.keyCode <= 127)
					{
						trackkeychar.append(arg0.character);
					}
					else
					{
						trackkeychar.append((char) 8);
					}

					if ((arg0.character == '\r') || (arg0.character == '\n'))
					{
						track1.selectAll();

						track2.setText("");
						track3.setText("");

						track2.setText(trackkeycode.toString().substring(0, trackkeycode.toString().length() - 1));
						track3.setText(trackkeychar.toString().substring(0, trackkeychar.toString().length() - 1));

						trackkeycode.delete(0, trackkeycode.length());
						trackkeychar.delete(0, trackkeychar.length());
					}
				}
			}

			public void keyReleased(KeyEvent arg0)
			{
				if (MSR == null) { return; }

				if (arg0.keyCode == 13)
				{
					StringBuffer trackbuffer = new StringBuffer();
					trackbuffer.append(((Text) arg0.widget).getText());

					StringBuffer msrtrack1 = new StringBuffer();
					StringBuffer msrtrack2 = new StringBuffer();
					StringBuffer msrtrack3 = new StringBuffer();

					if (MSR.parseTrack(trackbuffer, msrtrack1, msrtrack2, msrtrack3))
					{
						track1.setText(msrtrack1.toString());
						track2.setText(msrtrack2.toString());
						track3.setText(msrtrack3.toString());
					}
					else
					{
						if (com.efuture.javaPos.Device.MSR.havaMSR)
						{
							track1.setText(com.efuture.javaPos.Device.MSR.MSRTrack1);
							track2.setText(com.efuture.javaPos.Device.MSR.MSRTrack2);
							track3.setText(com.efuture.javaPos.Device.MSR.MSRTrack3);
							com.efuture.javaPos.Device.MSR.havaMSR = false;

							return;
						}
					}
				}
			}
		};

		track1.addKeyListener(key);
		track2.addKeyListener(key);
		track3.addKeyListener(key);

		MouseListener mouse = new MouseListener()
		{
			public void mouseDoubleClick(MouseEvent e)
			{

			}

			public void mouseDown(MouseEvent e)
			{

				Point pt = new Point(e.x, e.y);
				int index = table.getTopIndex();

				while (index < table.getItemCount())
				{
					TableItem item = table.getItem(index);
					Rectangle rect = item.getBounds(1);

					if (rect.contains(pt))
					{
						findLocation(msrPara, table, index, 1, editor);

						return;
					}

					index++;
				}
			}

			public void mouseUp(MouseEvent arg0)
			{

			}
		};

		table.addMouseListener(mouse);
	}

	public void setPrinterEvent(final Button cutpaper, final Button open, final Button close, final Button print, final Combo className, final Combo printLine, final Table table, final Text txt, final Text classTxt, final Button save)
	{
		final TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;

		for (int i = 0; i < printerDiscription.size(); i++)
		{
			className.add(ManipulateStr.PadLeft(String.valueOf(i), 2, '0') + " - " + (String) printerDiscription.elementAt(i));
		}

		final String[] s = new String[] { Language.apply("小票栈"), Language.apply("备注栈"), Language.apply("平推栈") };
		printLine.setItems(s);
		printLine.setText(s[0]);

		className.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				Control oldEditor = editor.getEditor();

				if (oldEditor != null)
				{
					oldEditor.dispose();
				}

				int index = className.getSelectionIndex();

				classTxt.setText((String) printerClassName.get(index));

				table.removeAll();

				try
				{
					if (printer != null)
					{
						printer.close();
					}
				}
				catch (Exception er)
				{
					er.printStackTrace();
				}

				printer = (Interface_Printer) printerInstances.get(index);

				if (printer != null)
				{
					printerPara = printer.getPara();

					if (printerPara == null) { return; }

					TableItem item = null;
					String[] row = null;

					for (int i = 0; i < printerPara.size(); i++)
					{
						item = new TableItem(table, SWT.NULL);
						row = (String[]) printerPara.elementAt(i);

						if ((config[PrinterIndex][1] != null) && !classTxt.getText().trim().equals(config[PrinterIndex][1]))
						{
							int min = Math.min(2, row.length);

							for (int j = 0; j < min; j++)
							{
								item.setText(j, row[j]);
							}
						}
						else
						{
							String deviceConfig = device[PrinterIndex][1];
							String[] dc = deviceConfig.split(",");
							item.setText(0, row[0]);

							if (dc.length > i)
							{
								item.setText(1, dc[i]);
							}
						}
					}
				}
			}
		});

		className.select(loadIndex[PrinterIndex]);

		SelectionListener selection = new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent arg0)
			{

			}

			public void widgetSelected(SelectionEvent e)
			{
				if (e.widget.equals(open))
				{
					String line = "";

					for (int i = 0; i < table.getItemCount(); i++)
					{
						TableItem item = table.getItem(i);
						line += ("," + item.getText(1));
					}

					if (line.length() > 0)
					{
						DeviceName.devicePrinter = line.substring(1);
						System.out.println(DeviceName.devicePrinter);
					}

					if ((printer != null) && printer.open())
					{
						printer.setEnable(true);
						new MessageBox(Language.apply("打印机设备打开成功"));
					}
				}
				else if (e.widget.equals(close))
				{
					if (printer != null)
					{
						printer.close();
						new MessageBox(Language.apply("打印机设备关闭成功"));
					}
				}
				else if (e.widget.equals(cutpaper))
				{
					if (printer != null)
					{
						if (printLine.getText().equals(s[0]))
						{
							printer.cutPaper_Normal();
						}
						else if (printLine.getText().equals(s[1]))
						{
							printer.cutPaper_Journal();
						}
						else if (printLine.getText().equals(s[2]))
						{
							printer.cutPaper_Slip();
						}
					}
				}
				else if (e.widget.equals(print))
				{
					// 添加尾部换行
					String printStr = txt.getText();

					if (printStr.length() <= 0)
					{
						printStr += "\n";
					}
					else if (printStr.charAt(printStr.length() - 1) != '\n')
					{
						printStr += "\n";
					}

					if (printer == null) { return; }

					if (printLine.getText().equals(s[0]))
					{
						printer.printLine_Normal(printStr);
					}
					else if (printLine.getText().equals(s[1]))
					{
						printer.printLine_Journal(printStr);
					}
					else if (printLine.getText().equals(s[2]))
					{
						printer.printLine_Slip(printStr);
					}
				}
				else if (e.widget.equals(save))
				{
					String line = "";

					for (int i = 0; i < table.getItemCount(); i++)
					{
						TableItem item = table.getItem(i);
						line += ("," + item.getText(1));
					}

					if (line.length() > 0)
					{
						DeviceName.devicePrinter = line.substring(1);
					}
					else
					{
						DeviceName.devicePrinter = "";
					}

					if (saveConfigData("Printer1", classTxt.getText()) && saveDeviceData("Printer", DeviceName.devicePrinter))
					{
						new MessageBox(Language.apply("打印机配置保存成功"));
					}
					else
					{
						new MessageBox(Language.apply("打印机配置保存失败"));
					}
				}
			}
		};

		open.addSelectionListener(selection);
		close.addSelectionListener(selection);
		print.addSelectionListener(selection);
		cutpaper.addSelectionListener(selection);
		save.addSelectionListener(selection);

		MouseListener mouse = new MouseListener()
		{
			public void mouseDoubleClick(MouseEvent e)
			{

			}

			public void mouseDown(MouseEvent e)
			{

				Point pt = new Point(e.x, e.y);
				int index = table.getTopIndex();

				while (index < table.getItemCount())
				{
					TableItem item = table.getItem(index);
					Rectangle rect = item.getBounds(1);

					if (rect.contains(pt))
					{
						findLocation(printerPara, table, index, 1, editor);

						return;
					}

					index++;
				}
			}

			public void mouseUp(MouseEvent arg0)
			{

			}
		};

		table.addMouseListener(mouse);
	}

	public void findLocation(final Vector para, final Table table, final int row, final int col, final TableEditor editor)
	{
		Control oldEditor = editor.getEditor();

		if (oldEditor != null)
		{
			oldEditor.dispose();
		}

		if (table.getItemCount() <= 0) { return; }

		TableItem item = table.getItem(row);

		if (item == null) { return; }

		String[] paras = (String[]) para.elementAt(row);

		if (paras.length > 2)
		{
			combo = new Combo(table, SWT.READ_ONLY);
			combo.setVisibleItemCount(10);

			for (int i = 1; i < paras.length; i++)
			{
				combo.add(paras[i]);
			}

			combo.setText(item.getText(col));
			editor.setEditor(combo, item, col);
			combo.addModifyListener(new ModifyListener()
			{
				public void modifyText(ModifyEvent e)
				{
					Combo text = (Combo) editor.getEditor();
					editor.getItem().setText(col, text.getText());
				}
			});
		}
		else
		{
			newEditor = new Text(table, SWT.LEFT | SWT.BORDER);
			newEditor.setTextLimit(100);
			newEditor.setText(item.getText(col));
			editor.setEditor(newEditor, item, col);
			newEditor.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
			newEditor.addModifyListener(new ModifyListener()
			{
				public void modifyText(ModifyEvent e)
				{
					Text text = (Text) editor.getEditor();
					editor.getItem().setText(col, text.getText());
				}
			});
			newEditor.selectAll();
			newEditor.setFocus();
		}
	}

	public boolean saveConfigData(String label, String value)
	{
		pw = CommonMethod.writeFileUTF(configName);

		if ((configini == null) || (configini.size() <= 0) || (pw == null))
		{
			new MessageBox(Language.apply("打开配置文件异常,请手工保存"));

			return false;
		}

		String line = null;

		for (int i = 0; i < configini.size(); i++)
		{
			String[] row = (String[]) configini.elementAt(i);

			if (row[0].trim().compareToIgnoreCase(label) == 0)
			{
				// 格式化Value
				String strvalue = "";
				int ilenth = Convert.countLength(value);
				if (ilenth <= 60)
				{
					strvalue = Convert.appendStringSize("", value, 0, 60, 60);
				}
				else
				{
					strvalue = Convert.appendStringSize("", value, 0, ilenth + 10, ilenth + 10);
				}

				if ((row.length > 2) && (row[2] != null))
				{
					line = Convert.appendStringSize("", row[0], 0, 20, 20) + " = " + strvalue + " && " + row[2];
				}
				else
				{
					line = Convert.appendStringSize("", row[0], 0, 20, 20) + " = " + strvalue + " && ";
				}

				row[1] = value;

				if (label.trim().compareToIgnoreCase("Printer1") == 0)
				{
					config[PrinterIndex][1] = value;
				}
				else if (label.trim().compareToIgnoreCase("MSR1") == 0)
				{
					config[MSRIndex][1] = value;
				}
				else if (label.trim().compareToIgnoreCase("KeyBoard1") == 0)
				{
					config[KeyBoardIndex][1] = value;
				}
				else if (label.trim().compareToIgnoreCase("CashBox1") == 0)
				{
					config[CashBoxIndex][1] = value;
				}
				else if (label.trim().compareToIgnoreCase("LineDisplay1") == 0)
				{
					config[LineDisplayIndex][1] = value;
				}
				else if (label.trim().compareToIgnoreCase("Scanner1") == 0)
				{
					config[ScannerIndex][1] = value;
				}
				else if (label.trim().compareToIgnoreCase("ICCard1") == 0)
				{
					config[ICCardIndex][1] = value;
				}
				else if (label.trim().compareToIgnoreCase("ElectronicScale1") == 0)
				{
					config[elecScaleIndex][1] = value;
				}
			}
			else if (row.length <= 1)
			{
				line = row[0];
			}
			else
			{
				// 格式化Value
				String strvalue = "";
				int ilenth = Convert.countLength(row[1]);
				if (ilenth <= 60)
				{
					strvalue = Convert.appendStringSize("", row[1], 0, 60, 60);
				}
				else
				{
					strvalue = Convert.appendStringSize("", row[1], 0, ilenth + 10, ilenth + 10);
				}

				if ((row.length > 2) && (row[2] != null))
				{
					line = Convert.appendStringSize("", row[0], 0, 20, 20) + " = " + strvalue + " && " + row[2];
				}
				else
				{
					line = Convert.appendStringSize("", row[0], 0, 20, 20) + " = " + strvalue + " && ";
				}
			}

			// 当此行是[]标注时
			if (line.trim().charAt(0) == '[')
			{
				if (i > 0)
				{
					String[] s = (String[]) configini.elementAt(i - 1);
					if ((s[0] != null) && (s[0].trim().length() > 0))
					{
						pw.println("");
					}
				}
			}

			pw.println(line);
		}

		pw.flush();
		pw.close();

		return true;
	}

	public boolean saveDeviceData(String label, String value)
	{
		pw1 = CommonMethod.writeFileUTF(deviceName);

		if ((deviceini == null) || (deviceini.size() <= 0) || (pw1 == null))
		{
			new MessageBox(Language.apply("打开配置文件异常,请手工保存"));

			return false;
		}

		String line = null;

		for (int i = 0; i < deviceini.size(); i++)
		{
			String[] row = (String[]) deviceini.elementAt(i);

			if (row.length <= 1)
			{
				line = row[0];
			}
			else if (row[0].trim().equals(label))
			{
				row[1] = value;

				// 格式化Value
				String strvalue = "";
				int ilenth = Convert.countLength(value);
				if (ilenth <= 60)
				{
					strvalue = Convert.appendStringSize("", value, 0, 60, 60);
				}
				else
				{
					strvalue = Convert.appendStringSize("", value, 0, ilenth + 10, ilenth + 10);
				}

				line = Convert.appendStringSize("", row[0], 0, 20, 20) + " = " + strvalue;
			}
			else
			{
				// 格式化Value
				String strvalue = "";
				int ilenth = Convert.countLength(row[1]);
				if (ilenth <= 60)
				{
					strvalue = Convert.appendStringSize("", row[1], 0, 60, 60);
				}
				else
				{
					strvalue = Convert.appendStringSize("", row[1], 0, ilenth + 10, ilenth + 10);
				}

				line = Convert.appendStringSize("", row[0], 0, 20, 20) + " = " + strvalue;
			}

			pw1.println(line);
		}

		pw1.flush();
		pw1.close();

		if (label.trim().compareToIgnoreCase("Printer") == 0)
		{
			device[PrinterIndex][1] = value;
		}
		else if (label.trim().compareToIgnoreCase("MSR") == 0)
		{
			device[MSRIndex][1] = value;
		}
		else if (label.trim().compareToIgnoreCase("KeyBoard") == 0)
		{
			device[KeyBoardIndex][1] = value;
		}
		else if (label.trim().compareToIgnoreCase("CashBox") == 0)
		{
			device[CashBoxIndex][1] = value;
		}
		else if (label.trim().compareToIgnoreCase("LineDisplay") == 0)
		{
			device[LineDisplayIndex][1] = value;
		}
		else if (label.trim().compareToIgnoreCase("Scanner") == 0)
		{
			device[ScannerIndex][1] = value;
		}
		else if (label.trim().compareToIgnoreCase("ICCard") == 0)
		{
			device[ICCardIndex][1] = value;
		}
		else if (label.trim().compareToIgnoreCase("ElectronicScale") == 0)
		{
			device[elecScaleIndex][1] = value;
		}

		return true;
	}
}
