package com.efuture.javaPos.UI.Design;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.UI.SaleMemoEvent;
import com.swtdesigner.SWTResourceManager;

public class SaleMemoForm
{
	public Shell shell = null;
	public Text txt_syjh;
	public Text txt_fphm;
	public Label lbl_Netbz;
	public Table table = null;
	public Button btn_printandsend = null;
	public Button btn_resend = null;
	public Button btn_exit = null;
	public SaleMemoEvent sme = null;

	public static void main(String[] args)
	{
		if (args.length > 0)
		{
			GlobalVar.RefushConfPath(args[0].trim());
		}
		
		SaleHeadDef saleHeadDef = new SaleHeadDef();
		saleHeadDef.syjh = "7777";
		saleHeadDef.fphm = 222;
		
		new SaleMemoForm(saleHeadDef, null, null, null, 0);

		SWTResourceManager.dispose();
		System.exit(0);
	}

	public SaleMemoForm(SaleHeadDef saleHead, Vector salegoods, Vector salepay, Vector vcdefault,int flag)
	{
		open(saleHead, salegoods, salepay, vcdefault ,flag);
	}

	public void open(SaleHeadDef saleHead, Vector salegoods, Vector salepay, Vector vcdefault, int flag)
	{
		final Display display = Display.getDefault();
		createContents();

		sme = new SaleMemoEvent(this, saleHead, salegoods, salepay, vcdefault, flag);
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
		shell.setSize(695, 633);

		// 收银机号
		final Label label_syjh = new Label(shell, SWT.NONE);
		label_syjh.setBounds(10, 20, 80, 20);
		label_syjh.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_syjh.setText(Language.apply("收银机号"));

		txt_syjh = new Text(shell, SWT.BORDER);
		txt_syjh.setBounds(100, 15, 118, 30);
		txt_syjh.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		// 小票号
		final Label label_fphm = new Label(shell, SWT.NONE);
		label_fphm.setBounds(230, 20, 60, 20);
		label_fphm.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_fphm.setText(Language.apply("小票号"));

		txt_fphm = new Text(shell, SWT.BORDER);
		txt_fphm.setBounds(300, 15, 118, 30);
		txt_fphm.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		// 送网标志
		lbl_Netbz = new Label(shell, SWT.NONE);
		lbl_Netbz.setBounds(615, 20, 60, 20);
		lbl_Netbz.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		lbl_Netbz.setText(Language.apply("未送网"));

		// 内容
		table = new Table(shell, SWT.NONE | SWT.BORDER);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		table.setBounds(10, 52, 669, 455);

		final TableColumn newColumnTableColumn1_1 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn1_1.setWidth(169);
		final TableColumn newColumnTableColumn1_2 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn1_2.setWidth(422);

		// 打印并发送按钮
		btn_printandsend = new Button(shell, SWT.NONE);
		btn_printandsend.setBounds(10, 516, 110, 30);
		btn_printandsend.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		btn_printandsend.setText(Language.apply("保存并打印"));

		// 重送网按钮
		btn_resend = new Button(shell, SWT.NONE);
		btn_resend.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		btn_resend.setText(Language.apply("重送网"));
		btn_resend.setBounds(130, 515, 94, 30);

		// 退出按钮
		btn_exit = new Button(shell, SWT.NONE);
		btn_exit.setBounds(585, 516, 94, 30);
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

		final Label label = new Label(shell, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("", 16, SWT.NONE));
		label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		label.setText(Language.apply("F11-保存   F12-退出"));
		label.setBounds(12, 563, 205, 24);
	}
}
