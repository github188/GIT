package com.efuture.configure;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.swtdesigner.SWTResourceManager;

public class deviceTest
{
	private Text elecScale_txt;
	private Text elecScaleClass_txt;
	private Table elecScale_table;
	private Combo elecScale_Class;
	private Text ICCardClass_txt;
	private Text ICCard_txt;
	private Table ICCard_table;
	private Combo ICCard_Class;
	private Text scannerClass_txt;
	private Text lineDisplayClass_txt;
	private Text cashBoxClass_txt;
	private Text keyClass_txt;
	private Text msrClass_txt;
	private Text printClass_txt;
	private Text Scanner_txt;
	private Table Scanner_table;
	private Combo Scanner_Class;
	private Text LineDisplay_txt;
	private Table LineDisplay_table;
	private Combo LineDisplay_Class;
	private Table cashBox_table;
	private Combo cashBox_Class;
	private Text displaytxt;
	private Table key_table;
	private Combo key_Class;
	private Text msr_track3;
	private Text msr_track2;
	private Text msr_track1;
	private Table msr_table;
	private Combo msr_Class;
	private Text text;
	private Combo Printer_Line;
	private Table printer_table;
	private Combo printer_Class;
	protected Shell shell;
	deviceTestEvent event = null;

	/**
	 * Launch the application
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
//			String JarName = ".//javaPos.ExtendJar//device.jar";
//			String path = ".//javaPos.ConfigFile";

			 String JarName = "C:/javapos/javaPos.ExtendJar/device.jar";
			 //javaPos.ExtendJar//device.jar"; //
			 String path = "C:/javapos/javaPos.ConfigFile";

			if (args.length > 0)
			{
				JarName = args[0];
			}

			if (args.length > 1)
			{
				path = args[1];
			}

			deviceTest window = new deviceTest();
			window.open(JarName, path);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		SWTResourceManager.dispose();
		System.exit(0);
	}

	/**
	 * Open the window
	 */
	public void open(String jarName, String configPath)
	{
		final Display display = Display.getDefault();
		event = new deviceTestEvent(jarName, configPath);
		createContents();
		shell.open();
		shell.layout();

		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window
	 */
	protected void createContents()
	{
		shell = new Shell(SWT.DIALOG_TRIM | GlobalVar.style);
		shell.setSize(678, 483);

		Rectangle rec = Display.getCurrent().getPrimaryMonitor().getClientArea();
		shell.setBounds((rec.width - shell.getSize().x) / 2, (rec.height - shell.getSize().y) / 2, shell.getSize().x, shell.getSize().y);
		shell.setText("SWT Application");

		final CTabFolder tabFolder = new CTabFolder(shell, SWT.NONE);
		tabFolder.setBorderVisible(true);
		tabFolder.setMRUVisible(true);
		tabFolder.setBounds(10, 10, 650, 429);

		final CTabItem printerTabItem = new CTabItem(tabFolder, SWT.NONE);
		printerTabItem.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		printerTabItem.setText(Language.apply("打印机"));

		final Composite composite_printer = new Composite(tabFolder, SWT.NONE);
		printerTabItem.setControl(composite_printer);

		final Label printer_lab_Class = new Label(composite_printer, SWT.NONE);
		printer_lab_Class.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NONE));
		printer_lab_Class.setText(Language.apply("设备型号"));
		printer_lab_Class.setBounds(20, 10, 112, 27);

		printer_Class = new Combo(composite_printer, SWT.READ_ONLY);
		printer_Class.setVisibleItemCount(10);
		printer_Class.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		printer_Class.setBounds(141, 10, 479, 27);

		printer_table = new Table(composite_printer, SWT.BORDER | SWT.FULL_SELECTION);
		printer_table.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NONE));
		printer_table.setLinesVisible(true);
		printer_table.setHeaderVisible(true);
		printer_table.setBounds(23, 83, 597, 184);

		final TableColumn newColumnTableColumn = new TableColumn(printer_table, SWT.NONE);
		newColumnTableColumn.setWidth(260);
		newColumnTableColumn.setText(Language.apply("参 数 描 述"));

		final TableColumn newColumnTableColumn_2 = new TableColumn(printer_table, SWT.NONE);
		newColumnTableColumn_2.setWidth(284);
		newColumnTableColumn_2.setText(Language.apply("参 数 值"));

		final Button printer_open = new Button(composite_printer, SWT.NONE);
		printer_open.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		printer_open.setText(Language.apply("打开设备"));
		printer_open.setBounds(23, 273, 109, 36);

		final Button Printer_close = new Button(composite_printer, SWT.NONE);
		Printer_close.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		Printer_close.setText(Language.apply("关闭设备"));
		Printer_close.setBounds(23, 315, 109, 36);

		final Button printNormalButton = new Button(composite_printer, SWT.NONE);
		printNormalButton.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		printNormalButton.setText(Language.apply("打 印"));
		printNormalButton.setBounds(141, 273, 109, 36);

		Printer_Line = new Combo(composite_printer, SWT.READ_ONLY);
		Printer_Line.setText(Language.apply("打印栈"));
		Printer_Line.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		Printer_Line.setBounds(141, 361, 109, 27);

		text = new Text(composite_printer, SWT.MULTI);
		text.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		text.setBounds(285, 273, 335, 115);

		final Button printer_saveButton = new Button(composite_printer, SWT.NONE);
		printer_saveButton.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		printer_saveButton.setText(Language.apply("保存配置"));
		printer_saveButton.setBounds(23, 357, 109, 33);

		final Button cutpaper = new Button(composite_printer, SWT.NONE);
		cutpaper.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		cutpaper.setText(Language.apply("切 纸"));
		cutpaper.setBounds(141, 315, 109, 36);

		printClass_txt = new Text(composite_printer, SWT.BORDER);
		printClass_txt.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		printClass_txt.setBounds(141, 45, 479, 25);

		event.setPrinterEvent(cutpaper, printer_open, Printer_close, printNormalButton, printer_Class, Printer_Line, printer_table, text, printClass_txt, printer_saveButton);

		final Label printer_lab_Class_txt = new Label(composite_printer, SWT.NONE);
		printer_lab_Class_txt.setBounds(20, 44, 112, 27);
		printer_lab_Class_txt.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NONE));
		printer_lab_Class_txt.setText(Language.apply("设备类名"));

		final CTabItem msrTabItem = new CTabItem(tabFolder, SWT.NONE);
		msrTabItem.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		msrTabItem.setText(Language.apply("刷卡槽"));

		final Composite composite_msr = new Composite(tabFolder, SWT.NONE);
		msrTabItem.setControl(composite_msr);

		final Label msr_lab_Class = new Label(composite_msr, SWT.NONE);
		msr_lab_Class.setBounds(20, 10, 112, 27);
		msr_lab_Class.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NONE));
		msr_lab_Class.setText(Language.apply("设备型号"));

		msr_Class = new Combo(composite_msr, SWT.READ_ONLY);
		msr_Class.setVisibleItemCount(10);
		msr_Class.setBounds(141, 10, 479, 27);
		msr_Class.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));

		msr_table = new Table(composite_msr, SWT.FULL_SELECTION | SWT.BORDER);
		msr_table.setBounds(23, 83, 597, 184);
		msr_table.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NONE));
		msr_table.setLinesVisible(true);
		msr_table.setHeaderVisible(true);

		final TableColumn newColumnTableColumn_1 = new TableColumn(msr_table, SWT.NONE);
		newColumnTableColumn_1.setWidth(260);
		newColumnTableColumn_1.setText(Language.apply("参 数 描 述"));

		final TableColumn newColumnTableColumn_2_1 = new TableColumn(msr_table, SWT.NONE);
		newColumnTableColumn_2_1.setWidth(284);
		newColumnTableColumn_2_1.setText(Language.apply("参 数 值"));

		final Button msr_open = new Button(composite_msr, SWT.NONE);
		msr_open.setBounds(23, 273, 109, 36);
		msr_open.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		msr_open.setText(Language.apply("打开设备"));

		final Button msr_close = new Button(composite_msr, SWT.NONE);
		msr_close.setBounds(23, 315, 109, 36);
		msr_close.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		msr_close.setText(Language.apply("关闭设备"));

		final Button msr_saveButton = new Button(composite_msr, SWT.NONE);
		msr_saveButton.setBounds(23, 357, 109, 33);
		msr_saveButton.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		msr_saveButton.setText(Language.apply("保存配置"));

		final Label label = new Label(composite_msr, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("宋体", 18, SWT.NONE));
		label.setText(Language.apply("磁道一"));
		label.setBounds(141, 278, 70, 27);

		final Label label_1 = new Label(composite_msr, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("宋体", 18, SWT.NONE));
		label_1.setText(Language.apply("磁道二"));
		label_1.setBounds(141, 320, 70, 27);

		final Label label_2 = new Label(composite_msr, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("宋体", 18, SWT.NONE));
		label_2.setText(Language.apply("磁道三"));
		label_2.setBounds(141, 360, 70, 27);

		msr_track1 = new Text(composite_msr, SWT.BORDER);
		msr_track1.setFont(SWTResourceManager.getFont("宋体", 18, SWT.NONE));
		msr_track1.setBounds(283, 273, 337, 36);
		msr_track1.setData("MSRINPUT");

		msr_track2 = new Text(composite_msr, SWT.BORDER);
		msr_track2.setFont(SWTResourceManager.getFont("宋体", 18, SWT.NONE));
		msr_track2.setBounds(283, 315, 337, 36);
		msr_track2.setData("MSRINPUT");

		msr_track3 = new Text(composite_msr, SWT.BORDER);
		msr_track3.setFont(SWTResourceManager.getFont("宋体", 18, SWT.NONE));
		msr_track3.setBounds(283, 357, 337, 33);
		msr_track3.setData("MSRINPUT");

		msrClass_txt = new Text(composite_msr, SWT.BORDER);
		msrClass_txt.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		msrClass_txt.setBounds(141, 45, 479, 25);

		event.setMsrEvent(msr_open, msr_close, msr_saveButton, msr_table, msr_Class, msr_track1, msr_track2, msr_track3, msrClass_txt);

		final Label msr_lab_Class_txt = new Label(composite_msr, SWT.NONE);
		msr_lab_Class_txt.setBounds(20, 44, 112, 27);
		msr_lab_Class_txt.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NONE));
		msr_lab_Class_txt.setText(Language.apply("设备类名"));

		final CTabItem keyboardTabItem = new CTabItem(tabFolder, SWT.NONE);
		keyboardTabItem.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		keyboardTabItem.setText(Language.apply("键盘"));

		final Composite composite_keyboard = new Composite(tabFolder, SWT.NONE);
		keyboardTabItem.setControl(composite_keyboard);

		final Label keyboard_lab_Class = new Label(composite_keyboard, SWT.NONE);
		keyboard_lab_Class.setBounds(20, 10, 112, 27);
		keyboard_lab_Class.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NONE));
		keyboard_lab_Class.setText(Language.apply("设备型号"));

		key_Class = new Combo(composite_keyboard, SWT.READ_ONLY);
		key_Class.setVisibleItemCount(10);
		key_Class.setBounds(141, 10, 479, 27);
		key_Class.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));

		key_table = new Table(composite_keyboard, SWT.FULL_SELECTION | SWT.BORDER);
		key_table.setBounds(23, 83, 597, 184);
		key_table.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NONE));
		key_table.setLinesVisible(true);
		key_table.setHeaderVisible(true);

		final TableColumn newColumnTableColumn_3 = new TableColumn(key_table, SWT.NONE);
		newColumnTableColumn_3.setWidth(260);
		newColumnTableColumn_3.setText(Language.apply("参 数 描 述"));

		final TableColumn newColumnTableColumn_2_2 = new TableColumn(key_table, SWT.NONE);
		newColumnTableColumn_2_2.setWidth(284);
		newColumnTableColumn_2_2.setText(Language.apply("参 数 值"));

		final Button key_open = new Button(composite_keyboard, SWT.NONE);
		key_open.setBounds(23, 273, 109, 36);
		key_open.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		key_open.setText(Language.apply("打开设备"));

		final Button key_close = new Button(composite_keyboard, SWT.NONE);
		key_close.setBounds(23, 315, 109, 36);
		key_close.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		key_close.setText(Language.apply("关闭设备"));

		final Button key_saveButton = new Button(composite_keyboard, SWT.NONE);
		key_saveButton.setBounds(23, 357, 109, 33);
		key_saveButton.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		key_saveButton.setText(Language.apply("保存配置"));

		displaytxt = new Text(composite_keyboard, SWT.BORDER | SWT.MULTI);
		displaytxt.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		displaytxt.setBounds(141, 273, 479, 117);

		keyClass_txt = new Text(composite_keyboard, SWT.BORDER);
		keyClass_txt.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		keyClass_txt.setBounds(141, 45, 479, 25);

		event.setKeyEvent(key_open, key_close, key_saveButton, key_table, key_Class, displaytxt, keyClass_txt);

		final Label keyboard_lab_Class_txt = new Label(composite_keyboard, SWT.NONE);
		keyboard_lab_Class_txt.setBounds(20, 44, 112, 27);
		keyboard_lab_Class_txt.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NONE));
		keyboard_lab_Class_txt.setText(Language.apply("设备类名"));

		final CTabItem cashboxTabItem = new CTabItem(tabFolder, SWT.NONE);
		cashboxTabItem.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		cashboxTabItem.setText(Language.apply("钱箱"));

		final Composite composite_cashbox = new Composite(tabFolder, SWT.NONE);
		cashboxTabItem.setControl(composite_cashbox);

		final Label cashbox_lab_Class = new Label(composite_cashbox, SWT.NONE);
		cashbox_lab_Class.setBounds(20, 10, 112, 27);
		cashbox_lab_Class.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NONE));
		cashbox_lab_Class.setText(Language.apply("设备型号"));

		cashBox_Class = new Combo(composite_cashbox, SWT.READ_ONLY);
		cashBox_Class.setVisibleItemCount(10);
		cashBox_Class.setBounds(141, 10, 479, 27);
		cashBox_Class.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));

		cashBox_table = new Table(composite_cashbox, SWT.FULL_SELECTION | SWT.BORDER);
		cashBox_table.setBounds(23, 83, 597, 184);
		cashBox_table.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NONE));
		cashBox_table.setLinesVisible(true);
		cashBox_table.setHeaderVisible(true);

		final TableColumn newColumnTableColumn_3_1 = new TableColumn(cashBox_table, SWT.NONE);
		newColumnTableColumn_3_1.setWidth(260);
		newColumnTableColumn_3_1.setText(Language.apply("参 数 描 述"));

		final TableColumn newColumnTableColumn_2_2_1 = new TableColumn(cashBox_table, SWT.NONE);
		newColumnTableColumn_2_2_1.setWidth(284);
		newColumnTableColumn_2_2_1.setText(Language.apply("参 数 值"));

		final Button cashBox_open = new Button(composite_cashbox, SWT.NONE);
		cashBox_open.setBounds(23, 273, 109, 36);
		cashBox_open.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		cashBox_open.setText(Language.apply("打开设备"));

		final Button cashBox_close = new Button(composite_cashbox, SWT.NONE);
		cashBox_close.setBounds(23, 315, 109, 36);
		cashBox_close.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		cashBox_close.setText(Language.apply("关闭设备"));

		final Button cashBox_saveButton = new Button(composite_cashbox, SWT.NONE);
		cashBox_saveButton.setBounds(23, 357, 109, 33);
		cashBox_saveButton.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		cashBox_saveButton.setText(Language.apply("保存配置") );

		final Button openCashBox = new Button(composite_cashbox, SWT.NONE);
		openCashBox.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		openCashBox.setText(Language.apply("开钱箱"));
		openCashBox.setBounds(141, 273, 109, 36);

		cashBoxClass_txt = new Text(composite_cashbox, SWT.BORDER);
		cashBoxClass_txt.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		cashBoxClass_txt.setBounds(141, 45, 479, 25);

		event.setCashBoxEvent(cashBox_open, cashBox_close, cashBox_saveButton, cashBox_table, cashBox_Class, openCashBox, cashBoxClass_txt);

		final Label cashbox_lab_Class_txt = new Label(composite_cashbox, SWT.NONE);
		cashbox_lab_Class_txt.setBounds(20, 44, 112, 27);
		cashbox_lab_Class_txt.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NONE));
		cashbox_lab_Class_txt.setText(Language.apply("设备类名"));

		final CTabItem linedisplayTabItem = new CTabItem(tabFolder, SWT.NONE);
		linedisplayTabItem.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		linedisplayTabItem.setText(Language.apply("顾客显示牌"));

		final Composite composite_linedisplay = new Composite(tabFolder, SWT.NONE);
		linedisplayTabItem.setControl(composite_linedisplay);

		final Label linedisplay_lab_Class = new Label(composite_linedisplay, SWT.NONE);
		linedisplay_lab_Class.setBounds(20, 10, 112, 27);
		linedisplay_lab_Class.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NONE));
		linedisplay_lab_Class.setText(Language.apply("设备型号"));

		LineDisplay_Class = new Combo(composite_linedisplay, SWT.READ_ONLY);
		LineDisplay_Class.setVisibleItemCount(10);
		LineDisplay_Class.setBounds(141, 10, 479, 27);
		LineDisplay_Class.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));

		LineDisplay_table = new Table(composite_linedisplay, SWT.FULL_SELECTION | SWT.BORDER);
		LineDisplay_table.setBounds(23, 83, 597, 184);
		LineDisplay_table.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NONE));
		LineDisplay_table.setLinesVisible(true);
		LineDisplay_table.setHeaderVisible(true);

		final TableColumn newColumnTableColumn_4 = new TableColumn(LineDisplay_table, SWT.NONE);
		newColumnTableColumn_4.setWidth(260);
		newColumnTableColumn_4.setText(Language.apply("参 数 描 述"));

		final TableColumn newColumnTableColumn_2_3 = new TableColumn(LineDisplay_table, SWT.NONE);
		newColumnTableColumn_2_3.setWidth(284);
		newColumnTableColumn_2_3.setText(Language.apply("参 数 值"));

		final Button LineDisplay_open = new Button(composite_linedisplay, SWT.NONE);
		LineDisplay_open.setBounds(23, 273, 109, 36);
		LineDisplay_open.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		LineDisplay_open.setText(Language.apply("打开设备"));

		final Button LineDisplay_show = new Button(composite_linedisplay, SWT.NONE);
		LineDisplay_show.setBounds(141, 273, 109, 36);
		LineDisplay_show.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		LineDisplay_show.setText(Language.apply("显 示"));

		final Button LineDisplay_close = new Button(composite_linedisplay, SWT.NONE);
		LineDisplay_close.setBounds(23, 315, 109, 36);
		LineDisplay_close.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		LineDisplay_close.setText(Language.apply("关闭设备"));

		final Button LineDisplay_saveButton = new Button(composite_linedisplay, SWT.NONE);
		LineDisplay_saveButton.setBounds(23, 357, 109, 33);
		LineDisplay_saveButton.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		LineDisplay_saveButton.setText(Language.apply("保存配置"));

		LineDisplay_txt = new Text(composite_linedisplay, SWT.MULTI);
		LineDisplay_txt.setBounds(285, 273, 335, 115);
		LineDisplay_txt.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));

		lineDisplayClass_txt = new Text(composite_linedisplay, SWT.BORDER);
		lineDisplayClass_txt.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		lineDisplayClass_txt.setBounds(141, 45, 479, 25);

		event.setLineDisplayEvent(LineDisplay_open, LineDisplay_close, LineDisplay_saveButton, LineDisplay_table, LineDisplay_Class, LineDisplay_show, LineDisplay_txt, lineDisplayClass_txt);

		final Label linedisplay_lab_Class_txt = new Label(composite_linedisplay, SWT.NONE);
		linedisplay_lab_Class_txt.setBounds(20, 44, 112, 27);
		linedisplay_lab_Class_txt.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NONE));
		linedisplay_lab_Class_txt.setText(Language.apply("设备类名"));

		final CTabItem ScannerTabItem = new CTabItem(tabFolder, SWT.NONE);
		ScannerTabItem.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		ScannerTabItem.setText(Language.apply("扫描仪"));

		final CTabItem ICCardTabItem = new CTabItem(tabFolder, SWT.NONE);
		ICCardTabItem.setText(Language.apply("IC读卡器"));
		ICCardTabItem.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));

		final Composite composite_ICCard = new Composite(tabFolder, SWT.NONE);
		ICCardTabItem.setControl(composite_ICCard);

		final Label ICCard_lab_Class = new Label(composite_ICCard, SWT.NONE);
		ICCard_lab_Class.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NONE));
		ICCard_lab_Class.setBounds(20, 10, 112, 27);
		ICCard_lab_Class.setText(Language.apply("设备型号"));

		ICCard_Class = new Combo(composite_ICCard, SWT.READ_ONLY);
		ICCard_Class.setVisibleItemCount(10);
		ICCard_Class.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		ICCard_Class.setBounds(141, 10, 479, 27);

		ICCard_table = new Table(composite_ICCard, SWT.FULL_SELECTION | SWT.BORDER);
		ICCard_table.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NONE));
		ICCard_table.setBounds(23, 83, 597, 184);
		ICCard_table.setLinesVisible(true);
		ICCard_table.setHeaderVisible(true);

		final TableColumn newColumnTableColumn_4_1 = new TableColumn(ICCard_table, SWT.NONE);
		newColumnTableColumn_4_1.setWidth(260);
		newColumnTableColumn_4_1.setText(Language.apply("参 数 描 述"));

		final TableColumn newColumnTableColumn_2_3_1 = new TableColumn(ICCard_table, SWT.NONE);
		newColumnTableColumn_2_3_1.setWidth(284);
		newColumnTableColumn_2_3_1.setText(Language.apply("参 数 值"));

		final Button ICCard_open = new Button(composite_ICCard, SWT.NONE);
		ICCard_open.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		ICCard_open.setBounds(23, 273, 109, 36);
		ICCard_open.setText(Language.apply("打开设备"));

		final Button ICCard_read = new Button(composite_ICCard, SWT.NONE);
		ICCard_read.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		ICCard_read.setBounds(141, 273, 109, 36);
		ICCard_read.setText(Language.apply("读 卡"));

		final Button ICCard_close = new Button(composite_ICCard, SWT.NONE);
		ICCard_close.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		ICCard_close.setBounds(23, 315, 109, 36);
		ICCard_close.setText(Language.apply("关闭设备"));

		final Button ICCard_saveButton = new Button(composite_ICCard, SWT.NONE);
		ICCard_saveButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(final SelectionEvent arg0)
			{
			}
		});
		ICCard_saveButton.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		ICCard_saveButton.setBounds(23, 357, 109, 33);
		ICCard_saveButton.setText(Language.apply("保存配置"));

		ICCard_txt = new Text(composite_ICCard, SWT.MULTI);
		ICCard_txt.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		ICCard_txt.setBounds(285, 273, 335, 115);

		ICCardClass_txt = new Text(composite_ICCard, SWT.BORDER);
		ICCardClass_txt.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		ICCardClass_txt.setBounds(141, 45, 479, 25);

		event.setICCardEvent(ICCard_open, ICCard_close, ICCard_saveButton, ICCard_table, ICCard_Class, ICCard_read, ICCard_txt, ICCardClass_txt);

		final Label ICCard_lab_Class_txt = new Label(composite_ICCard, SWT.NONE);
		ICCard_lab_Class_txt.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NONE));
		ICCard_lab_Class_txt.setBounds(20, 44, 112, 27);
		ICCard_lab_Class_txt.setText(Language.apply("设备类名"));

		final Composite composite_scanner = new Composite(tabFolder, SWT.NONE);
		ScannerTabItem.setControl(composite_scanner);

		final Label Scanner_lab_Class = new Label(composite_scanner, SWT.NONE);
		Scanner_lab_Class.setBounds(20, 10, 112, 27);
		Scanner_lab_Class.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NONE));
		Scanner_lab_Class.setText(Language.apply("设备型号"));

		Scanner_Class = new Combo(composite_scanner, SWT.READ_ONLY);
		Scanner_Class.setVisibleItemCount(10);
		Scanner_Class.setBounds(141, 10, 479, 27);
		Scanner_Class.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));

		Scanner_table = new Table(composite_scanner, SWT.FULL_SELECTION | SWT.BORDER);
		Scanner_table.setBounds(23, 83, 597, 184);
		Scanner_table.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NONE));
		Scanner_table.setLinesVisible(true);
		Scanner_table.setHeaderVisible(true);

		final TableColumn newColumnTableColumn_5 = new TableColumn(Scanner_table, SWT.NONE);
		newColumnTableColumn_5.setWidth(260);
		newColumnTableColumn_5.setText(Language.apply("参 数 描 述"));

		final TableColumn newColumnTableColumn_2_4 = new TableColumn(Scanner_table, SWT.NONE);
		newColumnTableColumn_2_4.setWidth(284);
		newColumnTableColumn_2_4.setText(Language.apply("参 数 值"));

		final Button Scanner_open = new Button(composite_scanner, SWT.NONE);
		Scanner_open.setBounds(23, 273, 109, 36);
		Scanner_open.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		Scanner_open.setText(Language.apply("打开设备"));

		final Button Scanner_close = new Button(composite_scanner, SWT.NONE);
		Scanner_close.setBounds(23, 315, 109, 36);
		Scanner_close.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		Scanner_close.setText(Language.apply("关闭设备"));

		final Button Scanner_saveButton = new Button(composite_scanner, SWT.NONE);
		Scanner_saveButton.setBounds(23, 357, 109, 33);
		Scanner_saveButton.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		Scanner_saveButton.setText(Language.apply("保存配置"));

		Scanner_txt = new Text(composite_scanner, SWT.MULTI);
		Scanner_txt.setBounds(141, 273, 479, 115);
		Scanner_txt.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));

		scannerClass_txt = new Text(composite_scanner, SWT.BORDER);
		scannerClass_txt.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		scannerClass_txt.setBounds(141, 45, 479, 25);

		event.setScannerEvent(Scanner_open, Scanner_close, Scanner_saveButton, Scanner_table, Scanner_Class, Scanner_txt, scannerClass_txt);

		final Label Scanner_lab_Class_txt = new Label(composite_scanner, SWT.NONE);
		Scanner_lab_Class_txt.setBounds(20, 43, 112, 27);
		Scanner_lab_Class_txt.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NONE));
		Scanner_lab_Class_txt.setText(Language.apply("设备类名"));

		final CTabItem elecScaleTabItem = new CTabItem(tabFolder, SWT.NONE);
		elecScaleTabItem.setText(Language.apply("电子秤"));
		elecScaleTabItem.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));

		final Composite composite_elecScale = new Composite(tabFolder, SWT.NONE);
		elecScaleTabItem.setControl(composite_elecScale);

		final Label elecScale_lab_Class = new Label(composite_elecScale, SWT.NONE);
		elecScale_lab_Class.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NONE));
		elecScale_lab_Class.setBounds(20, 10, 112, 27);
		elecScale_lab_Class.setText(Language.apply("设备型号"));

		elecScale_Class = new Combo(composite_elecScale, SWT.READ_ONLY);
		elecScale_Class.setVisibleItemCount(10);
		elecScale_Class.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		elecScale_Class.setBounds(141, 10, 479, 27);

		elecScale_table = new Table(composite_elecScale, SWT.FULL_SELECTION | SWT.BORDER);
		elecScale_table.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NONE));
		elecScale_table.setBounds(23, 83, 597, 184);
		elecScale_table.setLinesVisible(true);
		elecScale_table.setHeaderVisible(true);

		final TableColumn newColumnTableColumn_5_1 = new TableColumn(elecScale_table, SWT.NONE);
		newColumnTableColumn_5_1.setWidth(260);
		newColumnTableColumn_5_1.setText(Language.apply("参 数 描 述"));

		final TableColumn newColumnTableColumn_2_3_2 = new TableColumn(elecScale_table, SWT.NONE);
		newColumnTableColumn_2_3_2.setWidth(284);
		newColumnTableColumn_2_3_2.setText(Language.apply("参 数 值"));

		final Button elecScale_open = new Button(composite_elecScale, SWT.NONE);
		elecScale_open.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		elecScale_open.setBounds(23, 273, 109, 36);
		elecScale_open.setText(Language.apply("打开设备"));

		final Button elecScale_read = new Button(composite_elecScale, SWT.NONE);
		elecScale_read.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		elecScale_read.setBounds(141, 315, 109, 36);
		elecScale_read.setText(Language.apply("读取数据"));

		final Button elecScale_close = new Button(composite_elecScale, SWT.NONE);
		elecScale_close.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		elecScale_close.setBounds(23, 315, 109, 36);
		elecScale_close.setText(Language.apply("关闭设备"));

		final Button elecScale_saveButton = new Button(composite_elecScale, SWT.NONE);
		elecScale_saveButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(final SelectionEvent arg0)
			{
			}
		});
		elecScale_saveButton.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		elecScale_saveButton.setBounds(23, 357, 109, 33);
		elecScale_saveButton.setText(Language.apply("保存配置"));

		elecScale_txt = new Text(composite_elecScale, SWT.MULTI);
		elecScale_txt.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		elecScale_txt.setBounds(285, 273, 335, 115);

		elecScaleClass_txt = new Text(composite_elecScale, SWT.BORDER);
		elecScaleClass_txt.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		elecScaleClass_txt.setBounds(141, 45, 479, 25);

		final Label elecScaleClass_lab_Class_txt = new Label(composite_elecScale, SWT.NONE);
		elecScaleClass_lab_Class_txt.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NONE));
		elecScaleClass_lab_Class_txt.setBounds(20, 44, 112, 27);
		elecScaleClass_lab_Class_txt.setText(Language.apply("设备类名"));

		final Button elecScale_write = new Button(composite_elecScale, SWT.NONE);
		elecScale_write.setBounds(141, 273, 109, 36);
		elecScale_write.setFont(SWTResourceManager.getFont("宋体", 14, SWT.NONE));
		elecScale_write.setText(Language.apply("发送数据"));

		event.setElecScaleEvent(elecScale_open, elecScale_close, elecScale_saveButton, elecScale_table, elecScale_Class, elecScale_read, elecScale_write, elecScale_txt, elecScaleClass_txt);
	}
}
