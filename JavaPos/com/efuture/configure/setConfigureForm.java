package com.efuture.configure;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.swtdesigner.SWTResourceManager;

public class setConfigureForm {

	public Combo combo;
	public Table table;
	public Text text;
	protected Shell shell;
	public Button button =null;
	public Button button_1 =null;
	public Button button_2 =null;

	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			setConfigureForm window = new setConfigureForm();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window
	 */
	public void open() {
		final Display display = Display.getDefault();
		createContents();
		new setConfigureEvent(this);
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	/**
	 * Create contents of the window
	 */
	protected void createContents() {
		shell = new Shell(SWT.DIALOG_TRIM|GlobalVar.style);
		shell.setSize(750, 463);
		shell.setText(Language.apply("配置工具"));

		combo = new Combo(shell, SWT.NONE);
		combo.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		combo.setBounds(10, 12, 150, 31);

		text = new Text(shell, SWT.BORDER);
		text.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		text.setBounds(166, 13, 342, 27);

		button = new Button(shell, SWT.NONE);
		button.setFont(SWTResourceManager.getFont("宋体", 10, SWT.NONE));
		button.setText("..");
		button.setBounds(514, 13, 31, 27);

		button_2 = new Button(shell, SWT.NONE);
		button_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		button_2.setText(Language.apply("打 开"));
		button_2.setBounds(555, 13, 82, 27);

		button_1 = new Button(shell, SWT.NONE);
		button_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		button_1.setText(Language.apply("保 存"));
		button_1.setBounds(651, 12, 82, 27);

		table = new Table(shell, SWT.BORDER|SWT.FULL_SELECTION);
		table.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setBounds(10, 52, 723, 299);

		final TableColumn newColumnTableColumn = new TableColumn(table, SWT.NONE);
		newColumnTableColumn.setWidth(156);
		newColumnTableColumn.setText(Language.apply("参数名称"));

		final TableColumn newColumnTableColumn_1 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_1.setWidth(300);
		newColumnTableColumn_1.setText(Language.apply("数值"));

		final TableColumn newColumnTableColumn_2 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_2.setWidth(226);
		newColumnTableColumn_2.setText(Language.apply("注释"));

		final Label label_1 = new Label(shell, SWT.NONE);
		label_1.setText(Language.apply("ESC键      ：退出当前程序    TAB键   : 改变光标所在的位置\n上/下光标键: 选择表的行数    右光标键：修改此行 "));
		label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_1.setBounds(10, 366, 723, 55);
		
	}

}
