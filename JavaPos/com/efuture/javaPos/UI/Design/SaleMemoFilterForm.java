package com.efuture.javaPos.UI.Design;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.SaleMemoBS;
import com.efuture.javaPos.Struct.SaleMemoInfo;
import com.efuture.javaPos.UI.SaleMemoFilterEvent;
import com.swtdesigner.SWTResourceManager;

public class SaleMemoFilterForm
{
	public Button btnok;
	public Button btnsearch;
	public Shell shell = null;
	public Text txtwhere;
	public Table table = null;
	
	public static void main(String[] args)
	{
		if (args.length > 0)
		{
			GlobalVar.RefushConfPath(args[0].trim());
		}
		
		//new SaleMemoFilterForm(saleHeadDef, null, null, null, 0);

		SWTResourceManager.dispose();
		System.exit(0);
	}

	public SaleMemoFilterForm(SaleMemoBS smb,SaleMemoInfo smi)
	{
		open(smb,smi);
	}

	public void open(SaleMemoBS smb,SaleMemoInfo smi)
	{
		final Display display = Display.getDefault();
		
		createContents();

		new SaleMemoFilterEvent(this, smb, smi);
		
		shell.layout();
		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch()) display.sleep();
		}
	}

	public void createContents()
	{
		shell = new Shell(SWT.DIALOG_TRIM | GlobalVar.style);
		shell.setSize(695, 550);

		// message
		final Label lbmessage = new Label(shell, SWT.NONE);
		lbmessage.setBounds(10, 20, 169, 20);
		lbmessage.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		lbmessage.setText(Language.apply("请输入查询关键字:"));

		txtwhere = new Text(shell, SWT.BORDER);
		txtwhere.setBounds(180, 15, 324, 30);
		txtwhere.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		// 内容
		table = new Table(shell, SWT.NONE | SWT.BORDER| SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		table.setBounds(10, 52, 669, 455);

		final TableColumn newColumnTableColumn1_1 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn1_1.setWidth(169);
		final TableColumn newColumnTableColumn1_2 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn1_2.setWidth(422);

		// 查询按钮
		btnsearch = new Button(shell, SWT.NONE);
		btnsearch.setBounds(510, 15, 82, 30);
		btnsearch.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		btnsearch.setText(Language.apply("查 询"));

		btnok = new Button(shell, SWT.NONE);
		btnok.setBounds(595, 15, 82, 30);
		btnok.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		btnok.setText(Language.apply("确定"));
	}
}
