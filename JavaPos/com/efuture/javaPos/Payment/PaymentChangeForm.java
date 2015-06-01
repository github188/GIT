package com.efuture.javaPos.Payment;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

public class PaymentChangeForm
{
	public PosTable table1;
	public Text text;
	public PosTable table;
	public Shell shell;
	public Label payReqFee;
	public Label unpayfee;
	public Label lbl_ysje;
	public Label lbl_money;
	boolean done = false;

	/**
	 * Open the window
	 */
	public boolean open(PaymentChange paychg)
	{
		final Display display = Display.getDefault();
		createContents();

		// 创建触屏操作按钮栏
		ControlBarForm.createMouseControlBar(this, shell);

		new PaymentChangeEvent(paychg, this);

		// 加载背景图片
		Image bkimg = ConfigClass.changeBackgroundImage(this, shell, null);

		if (!shell.isDisposed())
		{
			shell.open();
			text.setFocus();
			shell.redraw();
		}

		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}

		// 释放背景图片
		ConfigClass.disposeBackgroundImage(bkimg);

		return done;
	}

	public void setDone(boolean done)
	{
		this.done = done;
	}

	/**
	 * Create contents of the window
	 */
	protected void createContents()
	{
		shell = new Shell(GlobalVar.style);
		shell.setLayout(new FormLayout());
		shell.setSize(800, 510);

		shell.setText(Language.apply("付款找零"));
		table = new PosTable(shell, SWT.BORDER | SWT.FULL_SELECTION, false);
		final FormData formData = new FormData();
		formData.bottom = new FormAttachment(0, 418);
		formData.top = new FormAttachment(0, 10);
		formData.right = new FormAttachment(0, 303);
		formData.left = new FormAttachment(0, 10);
		table.setLayoutData(formData);
		table.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		final TableColumn newColumnTableColumn = new TableColumn(table, SWT.NONE);
		newColumnTableColumn.setWidth(70);
		newColumnTableColumn.setText(Language.apply("代码"));

		final TableColumn newColumnTableColumn_1 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_1.setWidth(113);
		newColumnTableColumn_1.setText(Language.apply("找零名称"));

		final TableColumn newColumnTableColumn_5 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_5.setAlignment(SWT.RIGHT);
		newColumnTableColumn_5.setWidth(92);
		newColumnTableColumn_5.setText(Language.apply("汇率"));

		lbl_money = new Label(shell, SWT.NONE);
		final FormData formData_1 = new FormData();
		formData_1.top = new FormAttachment(0, 430);
		formData_1.bottom = new FormAttachment(0, 462);
		formData_1.right = new FormAttachment(0, 111);
		formData_1.left = new FormAttachment(0, 10);
		lbl_money.setLayoutData(formData_1);
		lbl_money.setFont(SWTResourceManager.getFont("宋体", 25, SWT.NONE));
		lbl_money.setText(Language.apply("付款名"));

		text = new Text(shell, SWT.BORDER);
		final FormData formData_2 = new FormData();
		formData_2.bottom = new FormAttachment(0, 462);
		formData_2.top = new FormAttachment(0, 430);
		formData_2.right = new FormAttachment(0, 303);
		formData_2.left = new FormAttachment(0, 117);
		text.setLayoutData(formData_2);
		text.setFont(SWTResourceManager.getFont("宋体", 25, SWT.NONE));

		table1 = new PosTable(shell, SWT.BORDER | SWT.FULL_SELECTION, false);
		final FormData formData_3 = new FormData();
		formData_3.bottom = new FormAttachment(0, 462);
		formData_3.top = new FormAttachment(0, 102);
		formData_3.right = new FormAttachment(0, 781);
		formData_3.left = new FormAttachment(0, 319);
		table1.setLayoutData(formData_3);
		table1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		table1.setLinesVisible(true);
		table1.setHeaderVisible(true);

		final TableColumn newColumnTableColumn_2 = new TableColumn(table1, SWT.NONE);
		newColumnTableColumn_2.setWidth(158);
		newColumnTableColumn_2.setText(Language.apply("找零名称"));

		final TableColumn newColumnTableColumn_4 = new TableColumn(table1, SWT.NONE);
		newColumnTableColumn_4.setAlignment(SWT.RIGHT);
		newColumnTableColumn_4.setWidth(133);
		newColumnTableColumn_4.setText(Language.apply("找零金额"));

		final TableColumn newColumnTableColumn_3 = new TableColumn(table1, SWT.RIGHT);
		newColumnTableColumn_3.setWidth(154);
		newColumnTableColumn_3.setText(Language.apply("找零汇率"));

		lbl_ysje = new Label(shell, SWT.NONE);
		final FormData formData_4 = new FormData();
		formData_4.bottom = new FormAttachment(0, 42);
		formData_4.top = new FormAttachment(0, 10);
		formData_4.right = new FormAttachment(0, 468);
		formData_4.left = new FormAttachment(0, 319);
		lbl_ysje.setLayoutData(formData_4);
		lbl_ysje.setFont(SWTResourceManager.getFont("宋体", 23, SWT.NONE));
		lbl_ysje.setText(Language.apply("找零金额")+":");

		final Label label_2 = new Label(shell, SWT.NONE);
		final FormData formData_5 = new FormData();
		formData_5.bottom = new FormAttachment(0, 82);
		formData_5.top = new FormAttachment(0, 50);
		formData_5.right = new FormAttachment(0, 468);
		formData_5.left = new FormAttachment(0, 319);
		label_2.setLayoutData(formData_5);
		label_2.setFont(SWTResourceManager.getFont("宋体", 23, SWT.NONE));
		label_2.setText(Language.apply("剩余金额:"));

		payReqFee = new Label(shell, SWT.NONE);
		final FormData formData_6 = new FormData();
		formData_6.bottom = new FormAttachment(0, 39);
		formData_6.top = new FormAttachment(0, 10);
		formData_6.right = new FormAttachment(0, 783);
		formData_6.left = new FormAttachment(0, 474);
		payReqFee.setLayoutData(formData_6);
		payReqFee.setAlignment(SWT.RIGHT);
		payReqFee.setFont(SWTResourceManager.getFont("宋体", 25, SWT.NONE));
		payReqFee.setText("Label");

		unpayfee = new Label(shell, SWT.NONE);
		final FormData formData_7 = new FormData();
		formData_7.bottom = new FormAttachment(0, 79);
		formData_7.top = new FormAttachment(0, 50);
		formData_7.right = new FormAttachment(0, 784);
		formData_7.left = new FormAttachment(0, 474);
		unpayfee.setLayoutData(formData_7);
		unpayfee.setAlignment(SWT.RIGHT);
		unpayfee.setFont(SWTResourceManager.getFont("宋体", 25, SWT.NONE));
		unpayfee.setText("Label");
	}
}
