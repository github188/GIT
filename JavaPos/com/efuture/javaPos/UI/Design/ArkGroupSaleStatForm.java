package com.efuture.javaPos.UI.Design;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.PosTimeDef;
import com.efuture.javaPos.UI.ArkGroupSaleStatEvent;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

public class ArkGroupSaleStatForm
{
	private Label lblMsg;
	private Text txtDate = null;
	private Combo cmbSyyh = null;
	private Combo cmbBc = null;
	private PosTable tabArkStatInfo = null;
	private Shell shell = null;
	private Label LblSaleJe = null;
	private Label LblSaleBS = null;
	private Label LblThJe = null;
	private Label LblThBS = null;
	private Label LblZke = null;

	public ArkGroupSaleStatForm()
	{
		this.open();
	}

	public void open()
	{
		final Display display = Display.getDefault();
		createContents();

		new ArkGroupSaleStatEvent(this);

		// 创建触屏操作按钮栏
		ControlBarForm.createMouseControlBar(this, shell);

		// 加载背景图片
		Image bkimg = ConfigClass.changeBackgroundImage(this, shell, null);

		if (!shell.isDisposed())
		{
			shell.open();
			shell.layout();
		}

		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}

		// 释放背景图片
		ConfigClass.disposeBackgroundImage(bkimg);
	}

	protected void createContents()
	{
		shell = new Shell(GlobalVar.style);
		// Rectangle area =
		// Display.getDefault().getPrimaryMonitor().getClientArea();

		shell.setSize(800, 510);

		shell.setBounds(GlobalVar.rec.x / 2 - shell.getSize().x / 2, GlobalVar.rec.y / 2 - shell.getSize().y / 2, shell.getSize().x, shell.getSize().y - GlobalVar.heightPL);
		shell.setText(Language.apply("柜组销售统计"));

		tabArkStatInfo = new PosTable(shell, SWT.BORDER | SWT.FULL_SELECTION);
		tabArkStatInfo.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		tabArkStatInfo.setLinesVisible(true);
		tabArkStatInfo.setHeaderVisible(true);
		tabArkStatInfo.setBounds(10, 66, 772, 379);

		final TableColumn newColumnTableColumn = new TableColumn(tabArkStatInfo, SWT.NONE);
		newColumnTableColumn.setWidth(257);
		newColumnTableColumn.setText(Language.apply("柜组号"));

		final TableColumn newColumnTableColumn_2 = new TableColumn(tabArkStatInfo, SWT.RIGHT);
		newColumnTableColumn_2.setAlignment(SWT.RIGHT);
		newColumnTableColumn_2.setWidth(139);
		newColumnTableColumn_2.setText(Language.apply("销售金额"));

		final TableColumn newColumnTableColumn_3 = new TableColumn(tabArkStatInfo, SWT.NONE);
		newColumnTableColumn_3.setAlignment(SWT.RIGHT);
		newColumnTableColumn_3.setWidth(56);
		newColumnTableColumn_3.setText(Language.apply("笔数"));

		final TableColumn newColumnTableColumn_4 = new TableColumn(tabArkStatInfo, SWT.RIGHT);
		newColumnTableColumn_4.setAlignment(SWT.RIGHT);
		newColumnTableColumn_4.setWidth(140);
		newColumnTableColumn_4.setText(Language.apply("退货金额"));

		final TableColumn newColumnTableColumn_5 = new TableColumn(tabArkStatInfo, SWT.NONE);
		newColumnTableColumn_5.setAlignment(SWT.RIGHT);
		newColumnTableColumn_5.setWidth(56);
		newColumnTableColumn_5.setText(Language.apply("笔数"));

		final TableColumn newColumnTableColumn_7 = new TableColumn(tabArkStatInfo, SWT.NONE);
		newColumnTableColumn_7.setAlignment(SWT.RIGHT);
		newColumnTableColumn_7.setWidth(95);
		newColumnTableColumn_7.setText(Language.apply("折扣金额"));

		final Group group = new Group(shell, SWT.NONE);
		group.setBounds(10, 5, 772, 55);
		/*
		 * final Label label = new Label(group, SWT.NONE);
		 * label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		 * label.setBounds(10, 20, 140, 20); label.setText("请输入查询日期");
		 * 
		 * txtDate = new Text(group, SWT.BORDER); txtDate.setTextLimit(8);
		 * txtDate.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		 * txtDate.setBounds(155, 15, 172, 30);
		 * 
		 * final Label label_1 = new Label(group, SWT.NONE);
		 * label_1.setForeground(SWTResourceManager.getColor(255, 0, 0));
		 * label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		 * label_1.setBounds(333, 20, 150, 20); label_1.setText("格式为:YYYYMMDD");
		 */
		final Label label = new Label(group, SWT.NONE);
		label.setBounds(10, 20, 140, 20);
		label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label.setText(Language.apply("日期(YYYYMMDD)"));

		txtDate = new Text(group, SWT.BORDER);
		txtDate.setBounds(155, 15, 99, 30);
		txtDate.setTextLimit(8);
		txtDate.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		final Label label_2 = new Label(group, SWT.NONE);
		label_2.setBounds(263, 20, 60, 20);
		label_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_2.setText(Language.apply("收银员"));

		cmbSyyh = new Combo(group, SWT.READ_ONLY);
		cmbSyyh.select(0);
		cmbSyyh.setItems(new String[] { Language.apply("当前款员"), Language.apply("所有款员") });
		cmbSyyh.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		cmbSyyh.setBounds(328, 15, 107, 28);

		final Label label_2_1 = new Label(group, SWT.NONE);
		label_2_1.setBounds(440, 20, 44, 20);
		label_2_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_2_1.setText(Language.apply("班次"));

		cmbBc = new Combo(group, SWT.READ_ONLY);
		cmbBc.select(0);
		String[] content = new String[GlobalInfo.posTime.size() + 1];
		content[0] = Language.apply("全部班次");
		for (int i = 0; i < GlobalInfo.posTime.size(); i++)
		{
			content[i + 1] = ((PosTimeDef) GlobalInfo.posTime.elementAt(i)).name;
		}
		cmbBc.setItems(content);
		cmbBc.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		cmbBc.setBounds(490, 15, 107, 28);

		final Label label_2_1_1 = new Label(group, SWT.NONE);
		label_2_1_1.setForeground(SWTResourceManager.getColor(255, 0, 0));
		label_2_1_1.setBounds(603, 20, 159, 20);
		label_2_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_2_1_1.setText(Language.apply("[付款键切换输入]"));

		LblSaleJe = new Label(shell, SWT.RIGHT);
		LblSaleJe.setFont(SWTResourceManager.getFont("", 15, SWT.NONE));
		LblSaleJe.setText("0.00");
		LblSaleJe.setBounds(270, 450, 134, 23);

		LblSaleBS = new Label(shell, SWT.RIGHT);
		LblSaleBS.setBounds(410, 450, 50, 23);
		LblSaleBS.setFont(SWTResourceManager.getFont("", 15, SWT.NONE));
		LblSaleBS.setText("0");

		LblThJe = new Label(shell, SWT.RIGHT);
		LblThJe.setBounds(465, 450, 134, 23);
		LblThJe.setFont(SWTResourceManager.getFont("", 15, SWT.NONE));
		LblThJe.setText("0.00");

		LblThBS = new Label(shell, SWT.RIGHT);
		LblThBS.setBounds(607, 451, 50, 23);
		LblThBS.setFont(SWTResourceManager.getFont("", 15, SWT.NONE));
		LblThBS.setText("0");

		LblZke = new Label(shell, SWT.RIGHT);
		LblZke.setBounds(665, 450, 84, 23);
		LblZke.setFont(SWTResourceManager.getFont("", 15, SWT.NONE));
		LblZke.setText("0.00");

		lblMsg = new Label(shell, SWT.RIGHT);
		lblMsg.setBounds(219, 451, 50, 23);
		lblMsg.setFont(SWTResourceManager.getFont("", 15, SWT.NONE));
		lblMsg.setText(Language.apply("合计:"));

	}

	public PosTable getTabArkStatInfo()
	{
		return tabArkStatInfo;
	}

	public Shell getShell()
	{
		return shell;
	}

	public Text getTxtDate()
	{
		return txtDate;
	}

	public Combo getCmbSyyh()
	{
		return cmbSyyh;
	}

	public Combo getCmbBc()
	{
		return cmbBc;
	}

	// 销售总金额
	public Label getLblSaleJe()
	{
		return LblSaleJe;
	}

	// 销售总笔数
	public Label getLblSaleBS()
	{
		return LblSaleBS;
	}

	// 退货总金额
	public Label getLblThJe()
	{
		return LblThJe;
	}

	// 退货总笔数
	public Label getLblThBS()
	{
		return LblThBS;
	}

	// 销售+退货的总折扣额
	public Label getLblZke()
	{
		return LblZke;
	}
}
