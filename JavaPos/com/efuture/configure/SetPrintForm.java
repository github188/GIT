package com.efuture.configure;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.swtdesigner.SWTResourceManager;

public class SetPrintForm
{
	public Button btn_select;
	public Button btn_open;
	public Text txt_path;
	public Combo combo_select;
	public Shell shell = null;
	public Button btn_preview = null;
	public Button btn_save = null;
	public Button btn_add = null;
	public Button btn_delete = null;
	public Button btn_back = null;
	public Button btn_exit = null;
	public Table table = null;
	public StyledText labPreview = null;

	public static void main(String[] args)
	{
		if (args.length > 0)
		{
			GlobalVar.RefushConfPath(args[0].trim());
		}

		SetPrintForm setPrintForm = new SetPrintForm();
		setPrintForm.open();
		
		SWTResourceManager.dispose();
		System.exit(0);
	}

	public void open()
	{
		final Display display = Display.getDefault();
		createContents();
		SetPrintEvent printEvent = new SetPrintEvent(this);
		shell.layout();
		shell.open();
		printEvent.findLocation();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch()) display.sleep();
		}
	}

	public void createContents()
	{
		shell = new Shell(SWT.DIALOG_TRIM|GlobalVar.style);
		shell.setSize(974, 591);
		shell.setText(Language.apply("配置打印模板"));

		table = new Table(shell, SWT.NONE | SWT.BORDER);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		table.setBounds(10, 52, 669, 455);

		final TableColumn newColumnTableColumn1_1 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn1_1.setWidth(228);
		newColumnTableColumn1_1.setText(Language.apply("名称"));
		final TableColumn newColumnTableColumn1_2 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn1_2.setWidth(55);
		newColumnTableColumn1_2.setText(Language.apply("行"));
		final TableColumn newColumnTableColumn1_3 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn1_3.setWidth(55);
		newColumnTableColumn1_3.setText(Language.apply("列"));
		final TableColumn newColumnTableColumn1_4 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn1_4.setWidth(55);
		newColumnTableColumn1_4.setText(Language.apply("长"));
		final TableColumn newColumnTableColumn1_5 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn1_5.setWidth(100);
		newColumnTableColumn1_5.setText(Language.apply("对齐"));
		final TableColumn newColumnTableColumn1_6 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn1_6.setWidth(420);
		newColumnTableColumn1_6.setText(Language.apply("文本内容"));
		final TableColumn newColumnTableColumn1_7 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn1_7.setWidth(420);
		newColumnTableColumn1_7.setText(Language.apply("模拟数据"));

		// 预览区域
		labPreview = new StyledText(shell, SWT.NONE);
		labPreview.setFont(SWTResourceManager.getFont("宋体", 9, SWT.NONE));
		labPreview.setBounds(688, 52, 270, 455);
		labPreview.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		labPreview.setEnabled(false);

		btn_back = new Button(shell, SWT.NONE);
		btn_back.setBounds(10, 516, 94, 30);
		btn_back.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		btn_back.setText(Language.apply("还 原"));

		// 增加行按钮
		btn_add = new Button(shell, SWT.NONE);
		btn_add.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		btn_add.setText(Language.apply("增加行"));
		btn_add.setBounds(114, 516, 94, 30);

		// 删除行按钮
		btn_delete = new Button(shell, SWT.NONE);
		btn_delete.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		btn_delete.setBounds(218, 516, 94, 30);
		btn_delete.setText(Language.apply("删除行"));

		// 预览按钮
		btn_preview = new Button(shell, SWT.NONE);
		btn_preview.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		btn_preview.setText(Language.apply("预 览"));
		btn_preview.setBounds(690, 516, 82, 30);

		// 保存按钮
		btn_save = new Button(shell, SWT.NONE);
		btn_save.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		btn_save.setText(Language.apply("保 存"));
		btn_save.setBounds(786, 516, 82, 30);

		// 退出按钮
		btn_exit = new Button(shell, SWT.NONE);
		btn_exit.setBounds(878, 516, 82, 30);
		btn_exit.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		btn_exit.setText(Language.apply("退 出"));

		btn_exit.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				shell.close();
				shell.dispose();
			}
		});

		combo_select = new Combo(shell, SWT.NONE);
		combo_select.setBounds(10, 14, 150, 30);
		combo_select.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		txt_path = new Text(shell, SWT.BORDER);
		txt_path.setBounds(176, 14, 463, 30);
		txt_path.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		btn_open = new Button(shell, SWT.NONE);
		btn_open.setBounds(689, 14, 94, 30);
		btn_open.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		btn_open.setText(Language.apply("打 开"));

		btn_select = new Button(shell, SWT.NONE);
		btn_select.setFont(SWTResourceManager.getFont("宋体", 10, SWT.NONE));
		btn_select.setBounds(648, 14, 31, 30);
		btn_select.setText("..");
	}
}
