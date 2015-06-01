package custom.localize.Bcsf;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import com.swtdesigner.SWTResourceManager;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Group;

import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Global.ConfigClass;

public class Bcsf_DailyFeeForm
{
	public Shell shell;
	public Composite cmpt_head;
	public Composite cmpt_detail;
	public Composite cmpt_buttom;

	public CLabel lbl_var_sky;
	public CLabel lbl_var_skdate;
	public CLabel lbl_var_billstatus;

	public Group group_head;
	public PosTable table_detail;

	public CLabel lbl_var_yshj;
	public CLabel lbl_var_sshj;

	public Group group_buttom;

	public CLabel lbl_czs;
	public Text txt_czs;
	public CLabel lbl_xmbm;
	public Text txt_xmbm;
	public CLabel lbl_mkt;
	public Text txt_month;
	private CLabel label_2;
	private TableColumn tblclmnNewColumn_0;

	/**
	 * @wbp.parser.entryPoint
	 */
	public void open()
	{
		final Display display = Display.getDefault();
		createContents();

		// 加载背景图片
		Image bkimg = ConfigClass.changeBackgroundImage(this, shell, null);

		new Bscf_DailyFeeEvent(this);

		if (!shell.isDisposed())
		{
			shell.open();
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
	}

	protected void createContents()
	{
		shell = new Shell(SWT.RESIZE | SWT.TITLE | SWT.APPLICATION_MODAL);
		shell.setSize(795, 510);
		shell.setLayout(new FormLayout());

		cmpt_head = new Composite(shell, SWT.NONE);
		FormData fd_cmpt_head = new FormData();
		fd_cmpt_head.right = new FormAttachment(100);
		fd_cmpt_head.top = new FormAttachment(0);
		fd_cmpt_head.left = new FormAttachment(0);
		cmpt_head.setLayoutData(fd_cmpt_head);
		cmpt_head.setLayout(new FillLayout(SWT.HORIZONTAL));

		cmpt_detail = new Composite(shell, SWT.NONE);
		FormData fd_cmpt_detail = new FormData();
		fd_cmpt_detail.bottom = new FormAttachment(100, -62);
		fd_cmpt_detail.right = new FormAttachment(100);
		fd_cmpt_detail.top = new FormAttachment(0, 44);
		fd_cmpt_detail.left = new FormAttachment(0);
		cmpt_detail.setLayoutData(fd_cmpt_detail);

		group_head = new Group(cmpt_head, SWT.NONE);

		CLabel lbl_sky = new CLabel(group_head, SWT.NONE);
		lbl_sky.setBounds(10, 10, 75, 22);
		lbl_sky.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		lbl_sky.setText("收款员");

		lbl_var_sky = new CLabel(group_head, SWT.NONE);
		lbl_var_sky.setForeground(SWTResourceManager.getColor(255, 0, 0));
		lbl_var_sky.setText("");
		lbl_var_sky.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		lbl_var_sky.setBounds(91, 10, 100, 22);

		CLabel lbl_skdate = new CLabel(group_head, SWT.NONE);
		lbl_skdate.setBounds(276, 10, 92, 22);
		lbl_skdate.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		lbl_skdate.setText("收款日期");

		lbl_var_skdate = new CLabel(group_head, SWT.NONE);
		lbl_var_skdate.setForeground(SWTResourceManager.getColor(255, 0, 0));
		lbl_var_skdate.setText("");
		lbl_var_skdate.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		lbl_var_skdate.setBounds(374, 10, 150, 22);

		CLabel lbl_billstatus = new CLabel(group_head, SWT.NONE);
		lbl_billstatus.setBounds(600, 10, 92, 22);
		lbl_billstatus.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		lbl_billstatus.setText("单据状态");

		lbl_var_billstatus = new CLabel(group_head, SWT.NONE);
		lbl_var_billstatus.setForeground(SWTResourceManager.getColor(255, 0, 0));
		lbl_var_billstatus.setText("");
		lbl_var_billstatus.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		lbl_var_billstatus.setBounds(698, 10, 75, 22);
		cmpt_detail.setLayout(new FormLayout());

		Composite cmpt_table = new Composite(cmpt_detail, SWT.NONE);
		FormData fd_cmpt_table = new FormData();
		fd_cmpt_table.right = new FormAttachment(100, -2);
		fd_cmpt_table.bottom = new FormAttachment(100, -44);
		fd_cmpt_table.top = new FormAttachment(0);
		fd_cmpt_table.left = new FormAttachment(0);
		cmpt_table.setLayoutData(fd_cmpt_table);
		cmpt_table.setLayout(new FillLayout(SWT.HORIZONTAL));

		table_detail = new PosTable(cmpt_table, SWT.BORDER | SWT.FULL_SELECTION);
		table_detail.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NORMAL));
		table_detail.setHeaderVisible(true);
		table_detail.setLinesVisible(true);

		tblclmnNewColumn_0 = new TableColumn(table_detail, SWT.CENTER);
		tblclmnNewColumn_0.setWidth(45);
		tblclmnNewColumn_0.setText("序");

		TableColumn tblclmnNewColumn_01 = new TableColumn(table_detail, SWT.CENTER);
		tblclmnNewColumn_01.setWidth(80);
		tblclmnNewColumn_01.setText("项目编码");

		TableColumn tblclmnNewColumn_1 = new TableColumn(table_detail, SWT.CENTER);
		tblclmnNewColumn_1.setWidth(88);
		tblclmnNewColumn_1.setText("项目名称");

		TableColumn tblclmnNewColumn_2 = new TableColumn(table_detail, SWT.CENTER);
		tblclmnNewColumn_2.setWidth(105);
		tblclmnNewColumn_2.setText("承租商编码");

		TableColumn tblclmnNewColumn_3 = new TableColumn(table_detail, SWT.CENTER);
		tblclmnNewColumn_3.setWidth(105);
		tblclmnNewColumn_3.setText("承租商名称");

		TableColumn tblclmnNewColumn_4 = new TableColumn(table_detail, SWT.CENTER);
		tblclmnNewColumn_4.setWidth(70);
		tblclmnNewColumn_4.setText("门店");

		TableColumn tblclmnNewColumn_5 = new TableColumn(table_detail, SWT.CENTER);
		tblclmnNewColumn_5.setWidth(83);
		tblclmnNewColumn_5.setText("应收月份");

		TableColumn tblclmnNewColumn_6 = new TableColumn(table_detail, SWT.RIGHT);
		tblclmnNewColumn_6.setWidth(100);
		tblclmnNewColumn_6.setText("应收金额");

		TableColumn tblclmnNewColumn_7 = new TableColumn(table_detail, SWT.RIGHT);
		tblclmnNewColumn_7.setWidth(100);
		tblclmnNewColumn_7.setText("实收金额");

		Composite cmpt_hj = new Composite(cmpt_detail, SWT.NONE);
		FormData fd_cmpt_hj = new FormData();
		fd_cmpt_hj.bottom = new FormAttachment(100, -2);
		fd_cmpt_hj.right = new FormAttachment(100, 2);
		fd_cmpt_hj.left = new FormAttachment(0, 2);
		cmpt_hj.setLayoutData(fd_cmpt_hj);
		cmpt_hj.setLayout(new FormLayout());

		CLabel label = new CLabel(cmpt_hj, SWT.NONE);
		FormData fd_label = new FormData();
		fd_label.right = new FormAttachment(0, 386);
		fd_label.bottom = new FormAttachment(0, 36);
		fd_label.top = new FormAttachment(0);
		fd_label.left = new FormAttachment(0);
		label.setLayoutData(fd_label);
		label.setText("按‘付款键’输入实收金额,按‘确认键’提交单据");
		label.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NORMAL));

		CLabel label_1 = new CLabel(cmpt_hj, SWT.NONE);
		FormData fd_label_1 = new FormData();
		fd_label_1.left = new FormAttachment(label, 3);
		fd_label_1.top = new FormAttachment(0);
		fd_label_1.bottom = new FormAttachment(100);
		label_1.setLayoutData(fd_label_1);
		label_1.setText("应收合计");
		label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));

		lbl_var_yshj = new CLabel(cmpt_hj, SWT.NONE);
		fd_label_1.right = new FormAttachment(lbl_var_yshj, -4);
		FormData fd_lbl_var_yshj = new FormData();
		fd_lbl_var_yshj.left = new FormAttachment(0, 478);
		fd_lbl_var_yshj.top = new FormAttachment(0);
		fd_lbl_var_yshj.bottom = new FormAttachment(0, 36);
		lbl_var_yshj.setLayoutData(fd_lbl_var_yshj);
		lbl_var_yshj.setText("");
		lbl_var_yshj.setForeground(SWTResourceManager.getColor(255, 0, 0));
		lbl_var_yshj.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		lbl_var_yshj.setAlignment(SWT.RIGHT);

		lbl_var_sshj = new CLabel(cmpt_hj, SWT.NONE);
		FormData fd_lbl_var_sshj = new FormData();
		fd_lbl_var_sshj.right = new FormAttachment(0, 778);
		fd_lbl_var_sshj.left = new FormAttachment(0, 678);
		fd_lbl_var_sshj.top = new FormAttachment(0);
		fd_lbl_var_sshj.bottom = new FormAttachment(0, 36);
		lbl_var_sshj.setLayoutData(fd_lbl_var_sshj);
		lbl_var_sshj.setText("");
		lbl_var_sshj.setForeground(SWTResourceManager.getColor(255, 0, 0));
		lbl_var_sshj.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		lbl_var_sshj.setAlignment(SWT.RIGHT);

		label_2 = new CLabel(cmpt_hj, SWT.NONE);
		fd_lbl_var_yshj.right = new FormAttachment(label_2, -10);
		label_2.setText("实收合计");
		label_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		FormData fd_label_2 = new FormData();
		fd_label_2.left = new FormAttachment(0, 588);
		fd_label_2.right = new FormAttachment(lbl_var_sshj, -5);
		fd_label_2.top = new FormAttachment(0);
		fd_label_2.bottom = new FormAttachment(100);
		label_2.setLayoutData(fd_label_2);

		cmpt_buttom = new Composite(shell, SWT.NONE);
		FormData fd_cmpt_buttom = new FormData();
		fd_cmpt_buttom.bottom = new FormAttachment(100, -3);
		fd_cmpt_buttom.left = new FormAttachment(0);
		fd_cmpt_buttom.top = new FormAttachment(100, -57);
		fd_cmpt_buttom.right = new FormAttachment(100);
		cmpt_buttom.setLayoutData(fd_cmpt_buttom);
		cmpt_buttom.setLayout(new FillLayout(SWT.HORIZONTAL));

		group_buttom = new Group(cmpt_buttom, SWT.NONE);
		group_buttom.setText("请输入查询条件");
		group_buttom.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NORMAL));

		lbl_czs = new CLabel(group_buttom, SWT.NONE);
		lbl_czs.setBounds(10, 21, 106, 26);
		lbl_czs.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		lbl_czs.setText("承租商编码");

		txt_czs = new Text(group_buttom, SWT.BORDER);
		txt_czs.setBounds(121, 20, 190, 26);
		txt_czs.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));

		/*
		 * txt_xmbm = new Text(group_buttom, SWT.BORDER);
		 * txt_xmbm.setBounds(373, 20, 150, 26);
		 * txt_xmbm.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		 * lbl_xmbm = new CLabel(group_buttom, SWT.NONE);
		 * lbl_xmbm.setBounds(281, 20, 86, 26);
		 * lbl_xmbm.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		 * lbl_xmbm.setText("项目编码");
		 */
		lbl_mkt = new CLabel(group_buttom, SWT.NONE);
		lbl_mkt.setBounds(349, 20, 86, 26);
		lbl_mkt.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
		lbl_mkt.setText("应收月份");

		txt_month = new Text(group_buttom, SWT.BORDER);
		txt_month.setBounds(441, 20, 170, 26);
		txt_month.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NORMAL));
	}

	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}
}
